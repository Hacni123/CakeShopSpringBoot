# Developer Checklist - Using Advanced Concepts

This checklist helps developers use the advanced concepts implemented in the CakeShop project correctly.

---

## Pre-Development Checklist

- [ ] Read `IMPLEMENTATION_GUIDE.md` (10 minutes)
- [ ] Read `QUICK_REFERENCE.md` (5 minutes)
- [ ] Run `mvn clean install` to download all dependencies
- [ ] Verify database connection in `application.properties`
- [ ] Start the application and check logs for startup success
- [ ] Verify caches are initialized in logs
- [ ] Verify async thread pools are created

---

## When Creating a New Service

### Step 1: Extend Base Class
```java
@Service
public class YourService extends AbstractCrudService<Entity, Long, EntityRepository> {
    
    public YourService(EntityRepository repository) {
        super(repository);
    }
    
    // Your custom methods here
}
```

- [ ] Extends `AbstractCrudService<T, ID, R>`
- [ ] Constructor passes repository to super
- [ ] Inherits getAll(), getById(), create(), update(), delete(), exists(), count()

### Step 2: Add Caching to Read Methods
```java
@Cacheable(value = "yourEntities", key = "#id")
public YourEntity getById(Long id) {
    logger.debug("Getting entity with ID: {}", id);
    return repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
}
```

- [ ] Use `@Cacheable` for read-only methods
- [ ] Use meaningful cache name (matching cache-names in properties)
- [ ] Use appropriate cache key (#id, #name, etc.)
- [ ] Add logging for debugging

### Step 3: Add Exception Handling
```java
public YourEntity create(YourEntity entity) {
    logger.info("Creating new entity");
    
    if (entity == null || entity.getName() == null) {
        throw new InvalidOperationException("Entity name cannot be empty");
    }
    
    return repository.save(entity);
}
```

- [ ] Validate inputs
- [ ] Throw appropriate custom exceptions
- [ ] Add meaningful error messages
- [ ] Include logging

### Step 4: Invalidate Cache on Write
```java
@CacheEvict(value = "yourEntities", allEntries = true)
public YourEntity create(YourEntity entity) {
    // ... creation logic
}

@CachePut(value = "yourEntities", key = "#id")
public YourEntity update(Long id, YourEntity entity) {
    // ... update logic
}

@CacheEvict(value = "yourEntities", allEntries = true)
public void delete(Long id) {
    // ... deletion logic
}
```

- [ ] Use `@CacheEvict` on create/delete (clears entire cache)
- [ ] Use `@CachePut` on update (updates cache)
- [ ] Or use `@CacheEvict(allEntries=true)` on update if modifying multiple fields
- [ ] Include logging

---

## When Creating a New Controller

### Step 1: Extend Base Controller
```java
@RestController
@RequestMapping("/api/auth/yourresource")
public class YourController extends BaseController {
    
    @Autowired
    private YourService yourService;
    
    // Your methods here
}
```

- [ ] Extends `BaseController`
- [ ] Add `@RestController` and `@RequestMapping`
- [ ] Inject services via `@Autowired`

### Step 2: Use Base Controller Methods
```java
@GetMapping("/{id}")
public ResponseEntity<YourDTO> getById(@PathVariable Long id) {
    logMethodEntry("getById", id);
    try {
        YourEntity entity = yourService.getById(id);
        return success(convertToDTO(entity));
    } catch (ResourceNotFoundException ex) {
        logError("getById", ex);
        throw ex;  // Caught by GlobalExceptionHandler
    }
}

@PostMapping("/create")
public ResponseEntity<YourDTO> create(@RequestBody YourDTO dto) {
    logMethodEntry("create");
    try {
        YourEntity entity = yourService.create(convertToEntity(dto));
        return created(convertToDTO(entity));
    } catch (Exception ex) {
        logError("create", ex);
        throw ex;
    }
}
```

- [ ] Use `logMethodEntry()` for entry logging
- [ ] Use `logMethodExit()` for exit logging
- [ ] Use `success()` for successful responses
- [ ] Use `created()` for creation responses
- [ ] Use `error()` for error responses
- [ ] Exceptions are caught by `GlobalExceptionHandler`
- [ ] Don't catch exceptions unless you need to log extra info

---

## When Adding Async Operations

### Step 1: Create Async Service Method
```java
@Service
public class YourAsyncService {
    
    private static final Logger logger = LoggerFactory.getLogger(YourAsyncService.class);
    
    @Async("asyncExecutor")
    public CompletableFuture<String> doAsyncWork(Long id) {
        try {
            logger.info("Starting async work for ID: {}", id);
            Thread.sleep(2000);  // Simulate work
            logger.info("Async work completed for ID: {}", id);
            return CompletableFuture.completedFuture("Success");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return CompletableFuture.failedFuture(e);
        }
    }
    
    @Async("notificationExecutor")
    public void fireAndForget(Long id) {
        logger.info("Fire and forget operation for ID: {}", id);
        // No return value - fire and forget
    }
}
```

- [ ] Use `@Async` with appropriate executor name
- [ ] Use `CompletableFuture<T>` for return value
- [ ] Use `void` for fire-and-forget operations
- [ ] Add try-catch for InterruptedException
- [ ] Call `Thread.currentThread().interrupt()` on interrupt
- [ ] Add logging for tracking

### Step 2: Call Async Method
```java
@Autowired
private YourAsyncService asyncService;

// Non-blocking call
CompletableFuture<String> result = asyncService.doAsyncWork(id);

result.thenAccept(message -> {
    logger.info("Async result: {}", message);
}).exceptionally(ex -> {
    logger.error("Async error", ex);
    return null;
});

// Fire and forget
asyncService.fireAndForget(id);
```

- [ ] Don't wait on async result (defeats purpose)
- [ ] Use `thenAccept()` for success handling
- [ ] Use `exceptionally()` for error handling
- [ ] Fire-and-forget methods are called but not awaited
- [ ] Check thread pool size if too many async tasks

---

## When Adding Pricing/Strategy Logic

### Step 1: Use Strategy Pattern
```java
@Autowired
private PricingService pricingService;

// Calculate with specific strategy
BigDecimal price = pricingService.calculatePrice(
    basePrice, quantity, "BULK"
);

// Auto-select strategy
BigDecimal autoPrice = pricingService.calculatePriceAuto(
    basePrice, quantity
);

// VIP pricing
BigDecimal vipPrice = pricingService.calculateVIPPrice(
    basePrice, quantity
);
```

- [ ] Use `calculatePrice()` for specific strategy
- [ ] Use `calculatePriceAuto()` for automatic selection
- [ ] Use `calculateVIPPrice()` for premium pricing
- [ ] Strategy types: "REGULAR", "BULK", "PREMIUM"
- [ ] Add new strategies by extending `PricingStrategy`

### Step 2: Add New Pricing Strategy
```java
public class YourPricingStrategy implements PricingStrategy {
    
    @Override
    public BigDecimal calculatePrice(BigDecimal basePrice, int quantity) {
        // Your pricing logic
        return basePrice.multiply(BigDecimal.valueOf(quantity));
    }
    
    @Override
    public String getStrategyName() {
        return "YOUR_STRATEGY";
    }
}
```

Then update `PricingService.selectStrategy()`:
```java
case "YOUR_STRATEGY":
    return new YourPricingStrategy();
```

- [ ] Implement `PricingStrategy` interface
- [ ] Implement `calculatePrice()` method
- [ ] Implement `getStrategyName()` method
- [ ] Register in `PricingService.selectStrategy()`

---

## When Building Complex DTOs

### Step 1: Use Builder Pattern
```java
OrderDTO order = new OrderDTOBuilder()
    .withOrderId(1L)
    .withUserId(100L)
    .withCartId(50L)
    .withStatus("PENDING")
    .withTotalPrice(250.00)
    .withDeliveryAddress("123 Main St")
    .withPaymentMethod("CREDIT_CARD")
    .build();
```

- [ ] Use fluent API for readable code
- [ ] Chain method calls for clarity
- [ ] Call `build()` at end to construct object
- [ ] Use `reset()` to reuse builder

### Step 2: Create New Builder
```java
public class YourDTOBuilder {
    private Long id;
    private String name;
    private String description;
    
    public YourDTOBuilder withId(Long id) {
        this.id = id;
        return this;
    }
    
    public YourDTOBuilder withName(String name) {
        this.name = name;
        return this;
    }
    
    public YourDTO build() {
        YourDTO dto = new YourDTO();
        dto.setId(this.id);
        dto.setName(this.name);
        return dto;
    }
}
```

- [ ] Create builder class for complex DTO
- [ ] Return `this` from setter methods
- [ ] Implement `build()` method
- [ ] Optional: add `reset()` method

---

## When Handling Errors

### Exception Usage
```java
// Not found error
throw new ResourceNotFoundException("User not found with ID: " + id);

// Invalid operation
throw new InvalidOperationException("Cannot process order");

// Insufficient stock
throw new InsufficientStockException(
    "Insufficient stock for cake ID " + id,
    id, requestedQty, availableQty
);

// Payment failure
throw new PaymentProcessingException(
    "Payment processing failed",
    transactionId, paymentMethod
);
```

- [ ] Use specific exception type
- [ ] Include ID/context in message
- [ ] Don't expose sensitive information
- [ ] Exception is caught by `GlobalExceptionHandler`

### Custom Exception Creation
```java
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class YourException extends RuntimeException {
    
    public YourException(String message) {
        super(message);
    }
}
```

- [ ] Extend `RuntimeException`
- [ ] Add `@ResponseStatus` annotation
- [ ] Include descriptive message

---

## When Configuring Caching

### Caching Configuration
```properties
# In application.properties
spring.cache.cache-names=yourEntity,yourOtherEntity
```

In `CacheConfig.java`:
```java
@Bean
public CacheManager yourCacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager("yourEntity");
    cacheManager.setCaffeine(Caffeine.newBuilder()
        .expireAfterWrite(15, TimeUnit.MINUTES)
        .maximumSize(1000)
        .recordStats());
    return cacheManager;
}
```

- [ ] Define cache name in properties
- [ ] Create CacheManager bean for custom config
- [ ] Set appropriate TTL
- [ ] Set maximumSize to prevent memory issues
- [ ] Enable recordStats() for monitoring

### Cache Invalidation Strategy
- [ ] Use `@Cacheable` on READ operations only
- [ ] Use `@CacheEvict(allEntries=true)` on CREATE/DELETE to clear entire cache
- [ ] Use `@CachePut` on UPDATE to update single entry
- [ ] Consider cache consistency when designing cache keys
- [ ] Avoid caching mutable data without proper invalidation

---

## When Monitoring Performance

### Check Cache Stats
```java
// Enable in config:
cacheManager.setCaffeine(Caffeine.newBuilder().recordStats());

// Access stats (if needed in code):
CacheStats stats = cache.stats();
logger.info("Cache hits: {}, misses: {}", 
    stats.hitCount(), stats.missCount());
```

- [ ] Monitor hit/miss ratio
- [ ] Track evictions
- [ ] Watch for memory leaks
- [ ] Adjust TTL based on access patterns

### Check Async Performance
```properties
# In logs, look for thread names:
async-task-1
order-processing-2
notification-1
scheduled-task-1

# Check queue capacity
spring.task.execution.pool.queue-capacity=100
```

- [ ] Monitor thread pool usage
- [ ] Check queue capacity utilization
- [ ] Look for thread name in logs
- [ ] Adjust pool size if needed

### Check Logging Output
```
2024-01-15 10:30:45 - INFO  - Creating order for user ID: 100
2024-01-15 10:30:45 - DEBUG - Fetching cake with ID: 50
2024-01-15 10:30:46 - INFO  - Order created successfully
```

- [ ] DEBUG logs show method entry/exit
- [ ] INFO logs show business operations
- [ ] WARN logs show potential issues
- [ ] ERROR logs show failures

---

## Common Pitfalls to Avoid

### ❌ Cache-Related
- [ ] **DON'T**: Use `@Cacheable` on write methods
- [ ] **DON'T**: Forget to invalidate cache on updates
- [ ] **DON'T**: Use same cache for unrelated entities
- [ ] **DON'T**: Set cache TTL too high for volatile data
- [ ] **DON'T**: Set cache TTL too low for static data

### ❌ Async-Related
- [ ] **DON'T**: Call `get()` on CompletableFuture (blocks thread)
- [ ] **DON'T**: Forget thread pool can get exhausted
- [ ] **DON'T**: Use async for quick operations
- [ ] **DON'T**: Ignore InterruptedException
- [ ] **DON'T**: Return null instead of CompletableFuture

### ❌ Exception-Related
- [ ] **DON'T**: Throw checked exceptions from services
- [ ] **DON'T**: Expose internal exception messages to clients
- [ ] **DON'T**: Create exception without meaningful message
- [ ] **DON'T**: Catch and re-throw without adding value
- [ ] **DON'T**: Forget to add logging in exception handlers

### ❌ Design Pattern-Related
- [ ] **DON'T**: Create builder for simple DTOs
- [ ] **DON'T**: Add strategies without need
- [ ] **DON'T**: Violate single responsibility
- [ ] **DON'T**: Ignore existing patterns
- [ ] **DON'T**: Over-engineer simple solutions

---

## Testing Checklist

- [ ] Unit test caching behavior
- [ ] Unit test pricing strategies
- [ ] Unit test exception handling
- [ ] Integration test async operations
- [ ] Integration test cache invalidation
- [ ] Load test async thread pools
- [ ] Verify error response format
- [ ] Test cache hit/miss ratio
- [ ] Verify logging output

---

## Code Review Checklist

When reviewing code, ensure:

- [ ] Service extends `AbstractCrudService` or implements `CrudService`
- [ ] Read methods have `@Cacheable`
- [ ] Write methods have `@CacheEvict` or `@CachePut`
- [ ] All services have logging
- [ ] Exceptions are custom and meaningful
- [ ] Controller extends `BaseController`
- [ ] Async methods use `@Async` with appropriate executor
- [ ] Builder methods return `this` (except `build()`)
- [ ] Strategy pattern used for multiple algorithms
- [ ] No synchronous blocking calls in controllers

---

## Deployment Checklist

- [ ] All dependencies installed: `mvn clean install`
- [ ] No compilation errors
- [ ] Database connection verified
- [ ] Cache configuration correct in properties
- [ ] Async thread pools configured
- [ ] Logging level set appropriately
- [ ] Exception handling tested
- [ ] Async operations tested
- [ ] Performance baseline established
- [ ] Monitoring in place

---

## Documentation Checklist

When adding new features:

- [ ] Code comments explain "why", not "what"
- [ ] Class-level javadoc includes purpose
- [ ] Method-level javadoc includes parameters and return
- [ ] Update QUICK_REFERENCE.md if adding new pattern
- [ ] Update IMPLEMENTATION_GUIDE.md with significant changes
- [ ] Add example usage in code

---

**Remember:**
- When in doubt, refer to IMPLEMENTATION_GUIDE.md
- Follow existing patterns in the codebase
- Think about caching implications early
- Consider async for I/O operations
- Always log important operations
- Design for testability from the start

**Happy Coding! ✅**

