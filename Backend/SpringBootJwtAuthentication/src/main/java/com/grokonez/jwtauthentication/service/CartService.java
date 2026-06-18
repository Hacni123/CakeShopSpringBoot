package com.grokonez.jwtauthentication.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grokonez.jwtauthentication.exception.ResourceNotFoundException;
import com.grokonez.jwtauthentication.exception.InvalidOperationException;
import com.grokonez.jwtauthentication.model.Cake;
import com.grokonez.jwtauthentication.model.Cart;
import com.grokonez.jwtauthentication.model.User;
import com.grokonez.jwtauthentication.repository.CartRepository;
import com.grokonez.jwtauthentication.security.services.UserDetailsServiceImpl;

/**
 * Cart Service - Business Logic Layer
 * Handles shopping cart operations with caching and exception handling
 * Demonstrates service layer abstraction and error management
 */
@Service
public class CartService {

	private static final Logger logger = LoggerFactory.getLogger(CartService.class);

	@Autowired
    private CartRepository cartRepository;
	
	@Autowired
	private UserDetailsServiceImpl userService;
	
	/**
	 * Find carts by user ID with pagination
	 */
	@Cacheable(value = "carts", key = "#id + '_user'")
	public Page<Cart> findByUserId(Long id, Pageable pageable) {
		logger.debug("Finding carts for user ID: {}", id);
        return cartRepository.findByUserId(id, pageable);
    }

	/**
	 * Create cart for user
	 * Evicts carts cache after creation
	 */
	@CacheEvict(value = "carts", allEntries = true)
	public Cart createCartByUser(long id, Cart cart) throws Exception {
		logger.info("Creating cart for user ID: {}", id);
		try {
            User user = this.userService.getUserById(id);
            cart.setUser(user);
            return cartRepository.save(cart);
        } catch (Exception e) {
            logger.error("Error creating cart for user {}: {}", id, e.getMessage());
            throw new InvalidOperationException("Failed to create cart: " + e.getMessage());
        }
    }

	/**
	 * Get cart by ID with caching
	 */
	@Cacheable(value = "carts", key = "#cartid")
	public Cart getCartById(long cartid) throws Exception {
		logger.debug("Getting cart with ID: {}", cartid);
        return this.cartRepository.findById(cartid)
                .orElseThrow(() -> {
                    logger.warn("Cart not found with ID: {}", cartid);
                    return new ResourceNotFoundException("Cart not found with ID: " + cartid);
                });
    }

	/**
	 * Get all carts with caching
	 */
	@Cacheable(value = "carts", key = "'all'")
	public List<Cart> getCart(){
		logger.info("Fetching all carts");
        return cartRepository.findAll();
    }

	/**
	 * Get cart by ID with exception handling
	 */
    public Cart getCart(long cartid){
		logger.debug("Getting cart with ID: {}", cartid);
        Optional<Cart> cart = cartRepository.findById(cartid);
        if(!cart.isPresent()) {
            logger.warn("Cart not found with ID: {}", cartid);
            throw new ResourceNotFoundException("Cart not found with ID: " + cartid);
        }
        return cart.get();
    }

	/**
	 * Get cart by cart ID with exception handling
	 */
	@Cacheable(value = "carts", key = "#cartid")
    public Cart getCartByCartId(long cartid) throws Exception {
		logger.debug("Getting cart by cartid: {}", cartid);
        return this.cartRepository.findById(cartid)
                .orElseThrow(() -> {
                    logger.warn("Cart not found with ID: {}", cartid);
                    return new ResourceNotFoundException("Cart not found with ID: " + cartid);
                });
    }

	/**
	 * Add new cart
	 * Evicts carts cache after adding
	 */
	@CacheEvict(value = "carts", allEntries = true)
    public Cart addCart(Cart cart){
		logger.info("Adding new cart");
		if (cart == null) {
            throw new InvalidOperationException("Cart cannot be null");
        }
        return cartRepository.save(cart);
    }

	/**
	 * Update cart with cache update
	 */
	@CachePut(value = "carts", key = "#cartid")
    public Cart updateCart(long cartid, Cart cart){
		logger.info("Updating cart with ID: {}", cartid);
		if (!cartRepository.existsById(cartid)) {
            throw new ResourceNotFoundException("Cart not found with ID: " + cartid);
        }
    	cart.setCartid(cartid);
        return cartRepository.save(cart);
    }

	/**
	 * Delete cart
	 * Evicts carts cache after deletion
	 */
	@CacheEvict(value = "carts", allEntries = true)
    public void deleteCart(long cartid){
		logger.info("Deleting cart with ID: {}", cartid);
		if (!cartRepository.existsById(cartid)) {
            throw new ResourceNotFoundException("Cart not found with ID: " + cartid);
        }
    	cartRepository.deleteById(cartid);
    }
}
