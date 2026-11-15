package com.rango.rango.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rango.rango.model.Restaurante;

public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {

    List<Restaurante> findByNomeContainingIgnoreCaseAndBairroContainingIgnoreCase(
            String nome, String bairro);

    List<Restaurante> findByNomeContainingIgnoreCaseAndBairroContainingIgnoreCaseAndCategoriaContainingIgnoreCase(
            String nome, String bairro, String categoria);
}