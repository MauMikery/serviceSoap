package com.equipo3.service.model;

import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Long> {
  public Category findByName(String name);

  public Boolean existsByName(String name);
}
