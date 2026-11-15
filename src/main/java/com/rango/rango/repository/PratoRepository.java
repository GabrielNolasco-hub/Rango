package com.rango.rango.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rango.rango.model.Prato;

public interface PratoRepository extends JpaRepository<Prato, Long> {

    List<Prato> findByRestauranteId(Long restauranteId);

    @Query("""
            SELECT p FROM Prato p
            WHERE (:nome = '' OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
              AND p.preco <= :precoMax
            ORDER BY p.preco
            """)
    List<Prato> buscar(@Param("nome") String nome,
                       @Param("precoMax") Double precoMax);
}