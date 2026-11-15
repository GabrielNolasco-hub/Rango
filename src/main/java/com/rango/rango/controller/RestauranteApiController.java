package com.rango.rango.controller;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rango.rango.model.Prato;
import com.rango.rango.model.Restaurante;
import com.rango.rango.repository.PratoRepository;
import com.rango.rango.repository.RestauranteRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/restaurantes")
public class RestauranteApiController {

    private static final ZoneId FUSO = ZoneId.of("America/Maceio");

    record PratoDTO(Long id, String nome, String descricao, Double preco) {}

    record RestauranteResumo(Long id, String nome, String bairro, String categoria,
                             String horarioAbertura, String horarioFechamento,
                             boolean aberto, String status) {}

    record RestauranteDetalhe(Long id, String nome, String endereco, String bairro,
                              String categoria, String descricao,
                              String horarioAbertura, String horarioFechamento,
                              boolean aberto, String status, List<PratoDTO> pratos) {}

    private final RestauranteRepository restauranteRepository;
    private final PratoRepository pratoRepository;

    public RestauranteApiController(RestauranteRepository restauranteRepository,
                                    PratoRepository pratoRepository) {
        this.restauranteRepository = restauranteRepository;
        this.pratoRepository = pratoRepository;
    }

    // Lista de restaurantes, já dizendo se cada um está aberto agora
    @GetMapping
    public List<RestauranteResumo> listar(@RequestParam(defaultValue = "") String nome,
                                          @RequestParam(defaultValue = "") String bairro,
                                          @RequestParam(defaultValue = "") String categoria) {
        LocalTime agora = LocalTime.now(FUSO);
        return restauranteRepository
                .findByNomeContainingIgnoreCaseAndBairroContainingIgnoreCaseAndCategoriaContainingIgnoreCase(
                        nome, bairro, categoria)
                .stream()
                .map(r -> resumo(r, agora))
                .toList();
    }

    // Página do restaurante: dados, horário, aberto/fechado e cardápio
    @GetMapping("/{id}")
    public RestauranteDetalhe detalhar(@PathVariable Long id) {
        Restaurante r = restauranteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Restaurante não encontrado"));

        boolean aberto = r.estaAberto(LocalTime.now(FUSO));
        List<PratoDTO> pratos = pratoRepository.findByRestauranteId(id).stream()
                .map(p -> new PratoDTO(p.getId(), p.getNome(), p.getDescricao(), p.getPreco()))
                .toList();

        return new RestauranteDetalhe(r.getId(), r.getNome(), r.getEndereco(), r.getBairro(),
                r.getCategoria(), r.getDescricao(),
                texto(r.getHorarioAbertura()), texto(r.getHorarioFechamento()),
                aberto, status(r, aberto), pratos);
    }

    // Cadastrar restaurante (JSON)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RestauranteResumo cadastrar(@RequestBody Restaurante restaurante) {
        restaurante.setId(null);
        Restaurante salvo = restauranteRepository.save(restaurante);
        return resumo(salvo, LocalTime.now(FUSO));
    }

    // Cadastrar prato em um restaurante (JSON)
    @PostMapping("/{id}/pratos")
    @ResponseStatus(HttpStatus.CREATED)
    public PratoDTO cadastrarPrato(@PathVariable Long id, @RequestBody Prato prato) {
        Restaurante r = restauranteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Restaurante não encontrado"));
        prato.setId(null);
        prato.setRestaurante(r);
        Prato salvo = pratoRepository.save(prato);
        return new PratoDTO(salvo.getId(), salvo.getNome(), salvo.getDescricao(), salvo.getPreco());
    }

    private RestauranteResumo resumo(Restaurante r, LocalTime agora) {
        boolean aberto = r.estaAberto(agora);
        return new RestauranteResumo(r.getId(), r.getNome(), r.getBairro(), r.getCategoria(),
                texto(r.getHorarioAbertura()), texto(r.getHorarioFechamento()),
                aberto, status(r, aberto));
    }

    private String status(Restaurante r, boolean aberto) {
        if (r.getHorarioAbertura() == null || r.getHorarioFechamento() == null) {
            return "Horário não informado";
        }
        return aberto
                ? "Aberto agora · fecha às " + r.getHorarioFechamento()
                : "Fechado · abre às " + r.getHorarioAbertura();
    }

    private String texto(LocalTime hora) {
        return hora == null ? null : hora.toString();
    }
}