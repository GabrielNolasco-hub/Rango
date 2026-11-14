package com.rango.rango.controller;

import com.rango.rango.model.Restaurante;
import com.rango.rango.repository.RestauranteRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RestauranteController {

    private final RestauranteRepository repository;

    public RestauranteController(
            RestauranteRepository repository) {
        this.repository = repository;
    }


    @GetMapping("/restaurantes")
    public String listarRestaurantes(
            @RequestParam (defaultValue = "") String nome,
            @RequestParam (defaultValue = "") String bairro,
            Model model) {
        
                nome = nome.trim();
                bairro = bairro.trim();

                model.addAttribute(
                        "restaurantes",
                        repository.findByNomeContainingIgnoreCaseAndBairroContainingIgnoreCase(
                                nome, bairro
                        )
                );

                model.addAttribute("nome", nome);
                model.addAttribute("bairro", bairro);

                return "restaurantes/lista";
            }
    


    @GetMapping("/restaurantes/novo")
    public String novoRestaurante(Model model) {
        model.addAttribute(
            "restaurante",
            new Restaurante()
        );
        return "restaurantes/form";
    }


    @PostMapping("/restaurantes")
    public String salvarRestaurante(
            @ModelAttribute Restaurante restaurante) {

        repository.save(restaurante);

        return "redirect:/restaurantes";
    }
}
