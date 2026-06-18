# Quick Reference Guide - Advanced Concepts in CakeShop

## File Structure Overview

```
src/main/java/com/grokonez/jwtauthentication/
├── config/
│   ├── AsyncConfig.java              # Multi-threading configuration
│   ├── CacheConfig.java              # Caching configuration
│   └── RepositoryConfig.java         # (existing)
├── controller/
│   ├── BaseController.java           # Base controller with OOP principles
│   ├── CakeController.java           # (existing - can extend BaseController)
│   └── ... (other controllers)
├── dto/
│   ├── OrderDTO.java                 # Data transfer object
│   └── ... (other DTOs)
├── exception/
│   ├── GlobalExceptionHandler.java   # Centralized exception handling
│   ├── ErrorResponse.java            # Standard error response
│   ├── ErrorCode.java                # Error codes enum
│   ├── InvalidOperationException.java# Custom exception
│   ├── InsufficientStockException.java# Custom exception
│   ├── PaymentProcessingException.java# Custom exception
│   └── ResourceNotFoundException.java# (existing)
├── model/
│   └── ... (entity models)
├── pattern/
│   ├── builder/
│   │   └── OrderDTOBuilder.java      # Builder pattern
│   ├── factory/
│   │   └── ServiceFactory.java       # Factory pattern
│   └── strategy/
│       ├── PricingStrategy.java      # Strategy interface
│       ├── RegularPricingStrategy.java
│       ├── BulkPricingStrategy.java
│       └── PremiumPricingStrategy.java
├── repository/
│   └── ... (data access layer)
├── security/
│   └── ... (existing security)
└── service/
    ├── AbstractCrudService.java      # Abstract base service
    ├── CrudService.java              # CRUD interface
    ├── AsyncOrderService.java        # Async operations
    ├── PricingService.java           # Strategy pattern usage
    ├── CakeService.java              # (refactored with caching)
    ├── CartService.java              # (refactored with caching)
    ├── CategoryService.java          # (refactored with caching)
    ├── OrderService.java             # (refactored with caching & async)
    └── ... (other services)
```

---

## Key Classes and Their Purpose

### Configuration Classes

| Class | Purpose | Key Configuration |
|-------|---------|-------------------|
| AsyncConfig | Thread pool configuration | 4 thread pools for different async tasks |
| CacheConfig | Cache setup | Caffeine cache with different TTLs per cache type |

### Exception Classes

| Class | HTTP Status | Use Case |
|-------|------------|----------|
| ResourceNotFoundException | 404 | When resource not found |
| InvalidOperationException | 400 | Invalid business logic |
| InsufficientStockException | 400 | Insufficient inventory |
| PaymentProcessingException | 402 | Payment failures |

### Pattern Classes

| Pattern | Location | Implementation |
|---------|----------|-----------------|
| Factory | `pattern/factory/ServiceFactory.java` | Service creation |
| Strategy | `pattern/strategy/` | Pricing strategies |
| Builder | `pattern/builder/OrderDTOBuilder.java` | Object construction |
| Service Layer | `service/` | Business logic abstraction |

---

## Quick API Usage

### Cache Management (Automatic)
```java
// Cache hits automatically via annotations
CakeService.findCakeById(1L);              // @Cacheable - hits cache
CakeService.updateCake(1L, 2L, cake);      // @CachePut - updates cache
CakeService.deleteCake(1L);                // @CacheEvict - clears cache
```

### Async Operations (Non-blocking)
```java
AsyncOrderService.processOrderAsync(orderId);         // Returns CompletableFuture
AsyncOrderService.sendOrderConfirmationEmail(...);    // Fire and forget
AsyncOrderService.generateInvoiceAsync(orderId);      // Async processing
```

### Pricing Strategies
```java
// Regular pricing (no discount)
pricingService.calculatePrice(price, qty, "REGULAR");

// Bulk pricing (5-10% discount)
pricingService.calculatePrice(price, qty, "BULK");

// Premium pricing (15% markup)
pricingService.calculatePrice(price, qty, "PREMIUM");

// Auto-select based on quantity
pricingService.calculatePriceAuto(price, qty);
```

### Exception Handling
```java
// Automatic global handling via @ControllerAdvice
// All exceptions mapped to standardized ErrorResponse with:
// - HTTP status code
// - Error code
// - Timestamp
// - Path
// - Message
// - Details (context-specific)
```

---

## Logging Levels

```properties
# Application Properties
logging.level.root=INFO                              # Global level
logging.level.com.grokonez.jwtauthentication=DEBUG  # Application debug

# Log Pattern
[2024-01-15 10:30:45] - DEBUG - Method entry with parameters
```

---

## Caching Strategy

| Cache Name | TTL | Max Size | Purpose |
|-----------|-----|----------|---------|
| cakes | 15 min | 1000 | Frequently accessed cake details |
| categories | 30 min | 500 | Static category data |
| users | 20 min | 500 | User session data |
| orders | 10 min | 1000 | Recent orders |
| carts | 10 min | 1000 | Shopping carts |
| productDetails | 15 min | 1000 | Detailed product info |
| categoryDetails | 30 min | 500 | Detailed category info |

---

## Threading Model

| Thread Pool | Size | Use Case | Queue |
|------------|------|----------|-------|
| asyncExecutor | 5-10 | General async tasks | 100 |
| orderProcessingExecutor | 3-5 | Order processing | 50 |
| notificationExecutor | 2-4 | Email/notifications | 50 |
| taskScheduler | 3 | Scheduled tasks | N/A |

---

## Validation & Exception Flow

```
Request
  ↓
@Valid annotation check
  ↓
Service method execution
  ↓
Exception occurs?
  ↓ (YES)
GlobalExceptionHandler catches
  ↓
Convert to ErrorResponse
  ↓
Return with appropriate HTTP status
  ↓ (NO)
@Cacheable/@CachePut annotation
  ↓
Return to client
```

---

## Refactored Service Methods (Enhanced)

### Before (Original)
```java
public Cake findCakeById(Long cakeid) {
    return cakeRepository.findByCakeid(cakeid);
}
```

### After (Enhanced)
```java
@Cacheable(value = "cakes", key = "#cakeid")
public Cake findCakeById(Long cakeid) {
    logger.debug("Fetching cake with ID: {}", cakeid);
    Cake cake = cakeRepository.findByCakeid(cakeid);
    if (cake == null) {
        throw new ResourceNotFoundException("Cake not found with ID: " + cakeid);
    }
    return cake;
}
```

**Improvements:**
- ✅ Caching reduces database queries
- ✅ Logging for debugging and audit
- ✅ Proper exception handling
- ✅ Meaningful error messages

---

## Performance Impact Estimates

| Feature | Before | After | Improvement |
|---------|--------|-------|-------------|
| Repeated cake queries | 100ms (DB) | 1ms (cache) | **99x faster** |
| Order creation | 1.2s (sync) | 50ms (async) | **24x faster API response** |
| Email notifications | Blocking | Non-blocking | **Immediate response** |
| Pricing calculations | Direct | Strategy | **Flexible & maintainable** |

---

## Integration Examples

### In Controller
```java
@RestController
public class OrderController extends BaseController {
    
    @PostMapping("/create")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO dto) {
        try {
            Order order = orderService.createOrderByUser(userId, cartId, order);
            // Async operations already triggered in service
            return created(convertToDTO(order));  // From BaseController
        } catch (ResourceNotFoundException ex) {
            // Caught by GlobalExceptionHandler
            throw ex;
        }
    }
}
```

### In Service
```java
@Service
public class OrderService {
    
    @CacheEvict(value = "orders", allEntries = true)
    public Order createOrderByUser(long id, long cartid, Order order) throws Exception {
        logger.info("Creating order for user: {}", id);
        
        Order savedOrder = orderRepository.save(order);
        
        // Trigger async operations
        asyncOrderService.sendOrderConfirmationEmail(...);
        asyncOrderService.updateInventoryAsync(...);
        
        return savedOrder;
    }
}
```

---

## Testing Considerations

### Unit Testing
```java
// Mock services, test business logic
@Test
void testCakePricing() {
    BigDecimal price = pricingService.calculatePrice(
        BigDecimal.valueOf(100), 10, "BULK"
    );
    assertEquals(BigDecimal.valueOf(900), price);
}
```

### Integration Testing
```java
// Test with actual Spring context, databases, caches
@SpringBootTest
@Transactional
class OrderServiceIntegrationTest {
    
    @Test
    void testCreateOrder() {
        Order order = orderService.createOrderByUser(1L, 1L, order);
        
        // Verify order created
        Order fetched = orderService.getOrder(order.getOrderid());
        assertNotNull(fetched);
    }
}
```

---

## Monitoring & Observability

### Via Logs
```
INFO  - Creating order for user ID: 100
DEBUG - Fetching cake with ID: 50
INFO  - Order created successfully with ID: 205
INFO  - Sending order confirmation email to: user@example.com
```

### Via Cache Stats (Caffeine)
```
Cache Statistics:
- cakes cache hits: 1250
- cakes cache misses: 150
- Hit rate: 89.3%
- Evictions: 10
```

---

## Troubleshooting

| Issue | Possible Cause | Solution |
|-------|---|---|
| No caching | Cache not enabled | Verify @EnableCaching in main class |
| Async methods hang | Thread pool exhausted | Increase pool size in AsyncConfig |
| Stale data in cache | Cache not evicted | Verify @CacheEvict on write methods |
| High memory usage | Cache size too large | Reduce maximumSize in CacheConfig |

---

## Next Steps for Production

1. **Add API Documentation**: Use Swagger/Springfox
2. **Add Unit Tests**: 80%+ code coverage
3. **Add Security**: OAuth2, JWT validation
4. **Add Monitoring**: Spring Boot Actuator, Prometheus
5. **Add Tracing**: Spring Cloud Sleuth
6. **Add Message Queue**: RabbitMQ for reliable async
7. **Add Distributed Cache**: Redis for multi-instance setup
8. **Add Circuit Breaker**: Resilience4j for fault tolerance


