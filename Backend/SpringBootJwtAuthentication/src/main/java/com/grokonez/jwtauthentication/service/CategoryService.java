package com.grokonez.jwtauthentication.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grokonez.jwtauthentication.exception.ResourceNotFoundException;
import com.grokonez.jwtauthentication.exception.InvalidOperationException;
import com.grokonez.jwtauthentication.model.Category;
import com.grokonez.jwtauthentication.repository.CategoryRepository;

/**
 * Category Service - Business Logic Layer
 * Implements caching for frequently accessed categories
 * Demonstrates service layer abstraction
 */
@Service
public class CategoryService {

	private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

	@Autowired
    private CategoryRepository categoryRepository;
	
	/**
	 * Get all categories with caching
	 * Categories don't change frequently, so longer cache TTL
	 */
	@Cacheable(value = "categories", key = "'all'")
	public List<Category> getCategory(){
		logger.info("Fetching all categories");
        return categoryRepository.findAll();
    }

	/**
	 * Get category by ID with caching
	 */
	@Cacheable(value = "categories", key = "#id")
    public Category getCategory(long id){
		logger.debug("Fetching category with ID: {}", id);
        Optional<Category> category = categoryRepository.findById(id);
        if(!category.isPresent()) {
            logger.warn("Category not found with ID: {}", id);
            throw new ResourceNotFoundException("Category not found with ID: " + id);
        }
        return category.get();
    }

	/**
	 * Add new category
	 * Evicts all categories cache
	 */
	@CacheEvict(value = "categories", allEntries = true)
    public Category addCategory(Category category){
		logger.info("Adding new category");
		if (category == null) {
            throw new InvalidOperationException("Category cannot be null");
        }
        return categoryRepository.save(category);
    }

	/**
	 * Update category with cache update
	 */
	@CachePut(value = "categories", key = "#id")
    public Category updateCategory(long id, Category category){
		logger.info("Updating category with ID: {}", id);
		if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with ID: " + id);
        }
    	category.setId(id);
        return categoryRepository.save(category);
    }

	/**
	 * Delete category
	 * Evicts all categories cache
	 */
	@CacheEvict(value = "categories", allEntries = true)
    public void deleteCategory(long id){
		logger.info("Deleting category with ID: {}", id);
		if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with ID: " + id);
        }
    	categoryRepository.deleteById(id);
    }
}
