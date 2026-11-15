package com.rango.rango.controller;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

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
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

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

        String horarioAberturaFormatado = r.getHorarioAbertura() != null
                ? r.getHorarioAbertura().format(FORMATO_HORA)
                : "Não informado";

        String horarioFechamentoFormatado = r.getHorarioFechamento() != null
                ? r.getHorarioFechamento().format(FORMATO_HORA)
                : "Não informado";

        String status;
        if (r.getHorarioAbertura() == null || r.getHorarioFechamento() == null) {
            status = "Horário não informado";
        } else if (aberto) {
            status = "Aberto agora · fecha às " + horarioFechamentoFormatado;
        } else {
            status = "Fechado · abre às " + horarioAberturaFormatado;
        }

        model.addAttribute("restaurante", r);
        model.addAttribute("pratos", pratoRepository.findByRestauranteId(id));
        model.addAttribute("aberto", aberto);
        model.addAttribute("status", status);
        model.addAttribute("horarioAberturaFormatado", horarioAberturaFormatado);
        model.addAttribute("horarioFechamentoFormatado", horarioFechamentoFormatado);
         return "restaurantes/detalhe";
    }
}