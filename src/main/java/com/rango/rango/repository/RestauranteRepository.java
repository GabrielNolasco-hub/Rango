package com.rango.rango.repository;

import com.rango.rango.model.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RestauranteRepository
        extends JpaRepository<Restaurante, Long> {

      
        List<Restaurante> 
        findByNomeContainingIgnoreCaseAndBairroContainingIgnoreCase(
                String nome, String bairro
        );

}
