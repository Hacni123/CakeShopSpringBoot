package com.grokonez.jwtauthentication.service;

import java.util.ArrayList;
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
import com.grokonez.jwtauthentication.model.Category;
import com.grokonez.jwtauthentication.repository.CakeRepository;
import com.grokonez.jwtauthentication.repository.CategoryRepository;

/**
 * Cake Service - Business Logic Layer
 * Implements caching, exception handling, and logging
 * Demonstrates OOP principles through service abstraction
 */
@Service
public class CakeService {

	private static final Logger logger = LoggerFactory.getLogger(CakeService.class);

	@Autowired
    private CakeRepository cakeRepository;

	@Autowired
    private CategoryService categoryService;

	/**
	 * Find cake by ID with caching
	 * Cache key: cakeid
	 */
	@Cacheable(value = "cakes", key = "#cakeid")
    public Cake findCakeById(Long cakeid) {
		logger.debug("Fetching cake with ID: {}", cakeid);
        Cake cake = cakeRepository.findByCakeid(cakeid);
        if (cake == null) {
            throw new ResourceNotFoundException("Cake not found with ID: " + cakeid);
        }
        return cake;
    }

	/**
	 * Find cakes by category with caching
	 */
	@Cacheable(value = "cakes", key = "#id + '_category'")
    public Page<Cake> findByCategoryId(Long id, Pageable pageable) {
		logger.debug("Fetching cakes for category ID: {}", id);
        return cakeRepository.findByCategoryId(id, pageable);
    }

	/**
	 * Search cakes by keyword
	 */
	@Cacheable(value = "cakes", key = "#keyword")
    public Page<Cake> searchbykey(String keyword, Pageable pageable) {
		logger.debug("Searching cakes with keyword: {}", keyword);
        return cakeRepository.findByNameContaining(keyword, pageable);
    }

	/**
	 * Find cake by ID - Optional version
	 */
	@Cacheable(value = "cakes", key = "#cakeid")
    public Optional<Cake> findById(Long cakeid) {
		logger.debug("Finding cake with ID: {}", cakeid);
        return cakeRepository.findById(cakeid);
    }

	/**
	 * Get all cakes with caching
	 */
	@Cacheable(value = "cakes", key = "'all'")
	public List<Cake> getCake(){
		logger.info("Fetching all cakes");
        return (List<Cake>) cakeRepository.findAll();
    }

	/**
	 * Get cake by ID with exception handling
	 */
    public Cake getCake(long cakeid){
		logger.debug("Getting cake with ID: {}", cakeid);
        Optional<Cake> cake = cakeRepository.findById(cakeid);
        
        if(!cake.isPresent()) {
            logger.warn("Cake not found with ID: {}", cakeid);
            throw new ResourceNotFoundException("Cake not found with ID: " + cakeid);
        }
        return cake.get();
    }

	/**
	 * Add new cake
	 * Evicts all cakes cache after adding
	 */
	@CacheEvict(value = "cakes", allEntries = true)
    public Cake addCake(Cake cake){
		logger.info("Adding new cake: {}", cake.getName());
		if (cake == null || cake.getName() == null || cake.getName().isEmpty()) {
            throw new InvalidOperationException("Cake name cannot be empty");
        }
        return cakeRepository.save(cake);
    }
    
	/**
	 * Create cake with category
	 * Evicts cakes cache after creation
	 */
	@CacheEvict(value = "cakes", allEntries = true)
    public Cake createCakeById(long id, Cake cake) throws Exception {
		logger.info("Creating cake for category ID: {}", id);
		try {
            Category category = this.categoryService.getCategory(id);
            cake.setCategory(category);
            return cakeRepository.save(cake);
        } catch (Exception e) {
            logger.error("Error creating cake for category {}: {}", id, e.getMessage());
            throw new InvalidOperationException("Failed to create cake: " + e.getMessage());
        }
    }

	/**
	 * Update cake with caching update
	 * Updates cache after modification
	 */
	@CachePut(value = "cakes", key = "#cakeid")
    public Cake updateCake(long cakeid, long id, Cake cake){
		logger.info("Updating cake ID: {} for category ID: {}", cakeid, id);
		if (!cakeRepository.existsById(cakeid)) {
            throw new ResourceNotFoundException("Cake not found with ID: " + cakeid);
        }
    	cake.setCakeid(cakeid);
    	Category category = this.categoryService.getCategory(id);
        cake.setCategory(category);
        return cakeRepository.save(cake);
    }

	/**
	 * Delete cake
	 * Evicts cakes cache after deletion
	 */
	@CacheEvict(value = "cakes", allEntries = true)
    public void deleteCake(long cakeid){
		logger.info("Deleting cake with ID: {}", cakeid);
		if (!cakeRepository.existsById(cakeid)) {
            throw new ResourceNotFoundException("Cake not found with ID: " + cakeid);
        }
    	cakeRepository.deleteById(cakeid);
    }
}
