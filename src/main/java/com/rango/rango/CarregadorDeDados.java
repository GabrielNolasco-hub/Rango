package com.rango.rango;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.rango.rango.model.Prato;
import com.rango.rango.model.Restaurante;
import com.rango.rango.repository.PratoRepository;
import com.rango.rango.repository.RestauranteRepository;

@Component
public class CarregadorDeDados implements CommandLineRunner {

    private final RestauranteRepository restauranteRepository;
    private final PratoRepository pratoRepository;

    public CarregadorDeDados(RestauranteRepository restauranteRepository,
                             PratoRepository pratoRepository) {
        this.restauranteRepository = restauranteRepository;
        this.pratoRepository = pratoRepository;
    }

    @Override
    public void run(String... args) {
        if (restauranteRepository.count() > 0) {
            return;
        }
        LocalTime agora = LocalTime.now(ZoneId.of("America/Maceio"));

        Restaurante aberto = restauranteRepository.save(criar("Aberto de Teste",
                "Rua Exemplo, 100", "Ponta Verde", "Brasileira",
                agora.minusHours(1), agora.plusHours(3)));
        Restaurante fechado = restauranteRepository.save(criar("Fechado de Teste",
                "Rua Exemplo, 200", "Jatiúca", "Pizza",
                agora.plusHours(3), agora.plusHours(5)));
        Restaurante noite = restauranteRepository.save(criar("Noturno de Teste",
                "Rua Exemplo, 300", "Pajuçara", "Hambúrguer",
                LocalTime.of(18, 0), LocalTime.of(2, 0)));

        pratoRepository.saveAll(List.of(
                prato("Sururu", "Sururu ao molho", 35.0, aberto),
                prato("Tapioca", "Tapioca com queijo coalho", 12.5, aberto),
                prato("Pizza Margherita", "Molho, mussarela e manjericão", 48.0, fechado),
                prato("X-Burger", "Pão, carne e queijo", 28.0, noite)));
    }

    private Restaurante criar(String nome, String endereco, String bairro, String categoria,
                              LocalTime abre, LocalTime fecha) {
        Restaurante r = new Restaurante();
        r.setNome(nome);
        r.setEndereco(endereco);
        r.setBairro(bairro);
        r.setCategoria(categoria);
        r.setDescricao("Dado de teste");
        r.setHorarioAbertura(abre);
        r.setHorarioFechamento(fecha);
        return r;
    }

    private Prato prato(String nome, String descricao, Double preco, Restaurante restaurante) {
        Prato p = new Prato();
        p.setNome(nome);
        p.setDescricao(descricao);
        p.setPreco(preco);
        p.setRestaurante(restaurante);
        return p;
    }
}