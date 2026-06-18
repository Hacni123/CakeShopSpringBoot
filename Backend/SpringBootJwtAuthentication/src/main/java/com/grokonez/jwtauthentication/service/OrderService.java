package com.grokonez.jwtauthentication.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grokonez.jwtauthentication.exception.ResourceNotFoundException;
import com.grokonez.jwtauthentication.exception.InvalidOperationException;
import com.grokonez.jwtauthentication.model.Cake;
import com.grokonez.jwtauthentication.model.Cart;
import com.grokonez.jwtauthentication.model.Order;
import com.grokonez.jwtauthentication.model.User;
import com.grokonez.jwtauthentication.repository.OrderRepository;
import com.grokonez.jwtauthentication.security.services.UserDetailsServiceImpl;

/**
 * Order Service - Business Logic Layer
 * Handles order operations with caching, async processing, and exception handling
 * Demonstrates service layer abstraction, multi-threading, and system design
 */
@Service
public class OrderService {

	private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

	@Autowired
    private OrderRepository orderRepository;
	
	@Autowired
	private CakeService cakeService;
	
	@Autowired
	private UserDetailsServiceImpl userService;
	
	@Autowired
	private CartService cartService;

	@Autowired
	private AsyncOrderService asyncOrderService;

	/**
	 * Create order for user and process it asynchronously
	 * Demonstrates async/multi-threading pattern
	 */
	@CacheEvict(value = "orders", allEntries = true)
    public Order createOrderByUser(long id, long cartid, Order order) throws Exception {
		logger.info("Creating order for user ID: {} with cart ID: {}", id, cartid);
		try {
            User user = this.userService.getUserById(id);
            order.setUser(user);
            Cart cart = this.cartService.getCartByCartId(cartid);
            order.setCart(cart);

            Order savedOrder = orderRepository.save(order);
            logger.info("Order created successfully with ID: {}", savedOrder.getOrderid());

            // Trigger async operations
            asyncOrderService.sendOrderConfirmationEmail(savedOrder.getOrderid(), user.getEmail());
            asyncOrderService.updateInventoryAsync(savedOrder.getOrderid());

            return savedOrder;
        } catch (Exception e) {
            logger.error("Error creating order: {}", e.getMessage());
            throw new InvalidOperationException("Failed to create order: " + e.getMessage());
        }
    }

	/**
	 * Get order by user ID with caching
	 */
	@Cacheable(value = "orders", key = "#userid")
    public Order getbyUserId(long userid){
		logger.debug("Getting order for user ID: {}", userid);
        Optional<Order> order = orderRepository.findByUserId(userid);
        if(!order.isPresent()) {
            logger.warn("Order not found for user ID: {}", userid);
            throw new ResourceNotFoundException("Order not found for user ID: " + userid);
        }
        return order.get();
    }

	/**
	 * Get all orders with caching
	 */
	@Cacheable(value = "orders", key = "'all'")
	public List<Order> getOrder(){
		logger.info("Fetching all orders");
        return orderRepository.findAll();
    }

	/**
	 * Get order by ID with caching
	 */
	@Cacheable(value = "orders", key = "#orderid")
    public Order getOrder(long orderid){
		logger.debug("Getting order with ID: {}", orderid);
        Optional<Order> order = orderRepository.findById(orderid);
        if(!order.isPresent()) {
            logger.warn("Order not found with ID: {}", orderid);
            throw new ResourceNotFoundException("Order not found with ID: " + orderid);
        }
        return order.get();
    }

	/**
	 * Add new order
	 * Evicts orders cache after adding
	 */
	@CacheEvict(value = "orders", allEntries = true)
    public Order addOrder(Order order){
		logger.info("Adding new order");
		if (order == null) {
            throw new InvalidOperationException("Order cannot be null");
        }
        return orderRepository.save(order);
    }

	/**
	 * Update order with cache update
	 */
	@CachePut(value = "orders", key = "#id")
    public Order updateOrder(long id, Order order) throws Exception{
		logger.info("Updating order with ID: {}", id);
		try {
            User user = this.userService.getUserById(id);
            order.setUser(user);
            return orderRepository.save(order);
        } catch (Exception e) {
            logger.error("Error updating order: {}", e.getMessage());
            throw new InvalidOperationException("Failed to update order: " + e.getMessage());
        }
    }

	/**
	 * Delete order
	 * Evicts orders cache after deletion
	 */
	@CacheEvict(value = "orders", allEntries = true)
    public void deleteOrder(long orderid){
		logger.info("Deleting order with ID: {}", orderid);
		if (!orderRepository.existsById(orderid)) {
            throw new ResourceNotFoundException("Order not found with ID: " + orderid);
        }
    	orderRepository.deleteById(orderid);
    }

	/**
	 * Generate and retrieve invoice asynchronously
	 */
	@Async("asyncExecutor")
	public CompletableFuture<byte[]> generateInvoice(long orderid) {
		logger.info("Generating invoice for order ID: {}", orderid);
		return asyncOrderService.generateInvoiceAsync(orderid);
	}
}
