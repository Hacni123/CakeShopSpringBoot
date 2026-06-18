package com.grokonez.jwtauthentication.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract Base Service implementing common CRUD operations
 * Demonstrates encapsulation and inheritance (OOP concepts)
 *
 * @param <T> Entity type
 * @param <ID> Primary key type
 * @param <R> Repository type
 */
public abstract class AbstractCrudService<T, ID, R extends JpaRepository<T, ID>> implements CrudService<T, ID> {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final R repository;

    public AbstractCrudService(R repository) {
        this.repository = repository;
    }

    @Override
    public List<T> getAll() {
        logger.info("Fetching all entities from {}", this.getClass().getSimpleName());
        return repository.findAll();
    }

    @Override
    public Optional<T> getById(ID id) {
        logger.info("Fetching entity with id: {} from {}", id, this.getClass().getSimpleName());
        return repository.findById(id);
    }

    @Override
    public T create(T entity) {
        logger.info("Creating new entity in {}", this.getClass().getSimpleName());
        return repository.save(entity);
    }

    @Override
    public T update(ID id, T entity) {
        logger.info("Updating entity with id: {} in {}", id, this.getClass().getSimpleName());
        return repository.save(entity);
    }

    @Override
    public void delete(ID id) {
        logger.info("Deleting entity with id: {} from {}", id, this.getClass().getSimpleName());
        repository.deleteById(id);
    }

    @Override
    public boolean exists(ID id) {
        return repository.existsById(id);
    }

    @Override
    public long count() {
        return repository.count();
    }

    /**
     * Template method for validation - can be overridden by subclasses
     */
    protected void validate(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
    }
}

