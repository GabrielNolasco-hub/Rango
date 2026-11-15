package com.rango.rango.controller;

import java.time.LocalTime;
import java.time.ZoneId;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import com.rango.rango.model.Restaurante;
import com.rango.rango.repository.PratoRepository;
import com.rango.rango.repository.RestauranteRepository;

@Controller
public class RestaurantePaginaController {

    private static final ZoneId FUSO = ZoneId.of("America/Maceio");

    private final RestauranteRepository restauranteRepository;
    private final PratoRepository pratoRepository;

    public RestaurantePaginaController(RestauranteRepository restauranteRepository,
                                       PratoRepository pratoRepository) {
        this.restauranteRepository = restauranteRepository;
        this.pratoRepository = pratoRepository;
    }

    @GetMapping("/restaurantes/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        Restaurante r = restauranteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Restaurante não encontrado"));

        boolean aberto = r.estaAberto(LocalTime.now(FUSO));
        String status;
        if (r.getHorarioAbertura() == null || r.getHorarioFechamento() == null) {
            status = "Horário não informado";
        } else if (aberto) {
            status = "Aberto agora · fecha às " + r.getHorarioFechamento();
        } else {
            status = "Fechado · abre às " + r.getHorarioAbertura();
        }

        model.addAttribute("restaurante", r);
        model.addAttribute("pratos", pratoRepository.findByRestauranteId(id));
        model.addAttribute("aberto", aberto);
        model.addAttribute("status", status);
        return "restaurantes/detalhe";
    }
}