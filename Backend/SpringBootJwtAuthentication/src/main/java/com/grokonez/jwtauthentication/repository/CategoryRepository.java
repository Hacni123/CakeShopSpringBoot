package com.grokonez.jwtauthentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.grokonez.jwtauthentication.model.Category;


public interface CategoryRepository extends JpaRepository<Category, Long>{

}
