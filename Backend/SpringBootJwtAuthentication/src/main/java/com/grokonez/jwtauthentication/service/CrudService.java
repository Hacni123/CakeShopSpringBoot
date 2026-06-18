package com.grokonez.jwtauthentication.service;

import java.util.List;
import java.util.Optional;

/**
 * Generic CRUD Service Interface
 * Implements the Service abstraction layer following OOP principles
 *
 * @param <T> Entity type
 * @param <ID> Primary key type
 */
public interface CrudService<T, ID> {

    /**
     * Get all entities
     */
    List<T> getAll();

    /**
     * Get entity by ID
     */
    Optional<T> getById(ID id);

    /**
     * Create new entity
     */
    T create(T entity);

    /**
     * Update existing entity
     */
    T update(ID id, T entity);

    /**
     * Delete entity by ID
     */
    void delete(ID id);

    /**
     * Check if entity exists
     */
    boolean exists(ID id);

    /**
     * Count all entities
     */
    long count();
}

