package com.rango.rango.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.rango.rango.model.Prato;
import com.rango.rango.repository.PratoRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/pratos")
public class PratoApiController {

    private static final double SEM_LIMITE = 1_000_000.0;

    record PratoBusca(Long id, String nome, String descricao, Double preco,
                      Long restauranteId, String restauranteNome, String bairro) {}

    private final PratoRepository pratoRepository;

    public PratoApiController(PratoRepository pratoRepository) {
        this.pratoRepository = pratoRepository;
    }

    @GetMapping
    public List<PratoBusca> buscar(@RequestParam(defaultValue = "") String nome,
                                   @RequestParam(required = false) Double precoMax) {
        double max = precoMax != null ? precoMax : SEM_LIMITE;
        if (max < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "precoMax não pode ser negativo");
        }
        return pratoRepository.buscar(nome.trim(), max).stream()
                .map(this::converter)
                .toList();
    }

    private PratoBusca converter(Prato p) {
        return new PratoBusca(p.getId(), p.getNome(), p.getDescricao(), p.getPreco(),
                p.getRestaurante() != null ? p.getRestaurante().getId() : null,
                p.getRestaurante() != null ? p.getRestaurante().getNome() : null,
                p.getRestaurante() != null ? p.getRestaurante().getBairro() : null);
    }
}