package com.rango.rango.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rango.rango.model.Prato;

public interface PratoRepository extends JpaRepository<Prato, Long> {

    List<Prato> findByRestauranteId(Long restauranteId);
}