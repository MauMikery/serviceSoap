package com.equipo3.service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class ProductDB {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @NotEmpty
  String name;

  @NotEmpty
  String description;

  @NotNull
  Double price;

  @ManyToOne
  @JoinColumn(name = "category_id")
  Category category;
}
