
document.addEventListener("DOMContentLoaded", function () {

    const form = document.getElementById("restaurant-filters");
    const categoria = document.getElementById("categoria");
    const preco = document.getElementById("preco");


    const campoPersonalizado = document.getElementById(
        "campo-preco-personalizado"
    );

    const precoPersonalizado = document.getElementById(
        "precoPersonalizado"
    );


    const cards = Array.from(
        document.querySelectorAll(".restaurant-card")
    );

    const contador = document.getElementById("results-count");
    const mensagemVazia = document.getElementById("empty-results");
    const avisoPreco = document.getElementById("price-warning");
    const atalhos = document.querySelectorAll(".category-choice");

    if (!form || !categoria || !preco) {
        console.error("Os campos de filtro não foram encontrados.");
        return;
    }

    const parametros = new URLSearchParams(window.location.search);

    categoria.value = parametros.get("categoria") || "";
    preco.value = parametros.get("preco") || "";

    precoPersonalizado.value = parametros.get("precoMaximo") || "";

    function atualizarCampoPersonalizado() {
        const personalizado = preco.value === "personalizado";

        campoPersonalizado.hidden = !personalizado;
        precoPersonalizado.disabled = !personalizado;
        precoPersonalizado.required = personalizado;
    }

    preco.addEventListener("change", atualizarCampoPersonalizado);

    atualizarCampoPersonalizado();

    let numeroBusca = 0;

    function normalizar(texto) {
        return String(texto || "")
            .toLowerCase()
            .normalize("NFD")
            .replace(/[\u0300-\u036f]/g, "");
    }


    function correspondeCategoria(texto, filtro) {
        const busca = normalizar(filtro).trim();

        if (!busca) {
            return true;
        }

        const categoriaRestaurante = normalizar(texto);

        const categorias = {
            massas: ["massa", "italian"],
            hamburguer: ["hamburg", "burger", "burguer"],
            japones: ["japon", "sushi"],
            sushi: ["sushi", "japon"],
            vegano: ["vegan"],
            vegetariana: ["vegetarian"],
            regional: ["regional", "brasileir", "nordestin"],
            brasileira: ["brasileir", "regional", "nordestin"],
            pizza: ["pizza", "pizzaria"],
            doces: ["doce", "sobremesa", "confeitaria"],
            sobremesas: ["sobremesa", "doce", "confeitaria"],
            churrasco: ["churrasc", "grelhad"]
        };

        const palavras = categorias[busca] || [busca];

        return palavras.some(function (palavra) {
            return categoriaRestaurante.includes(palavra);
        });
    }


    function correspondePreco(valor, faixa) {
        const numero = Number(valor);

        if (!Number.isFinite(numero)) {
            return false;
        }

        if (faixa === "ate40") {
            return numero <= 40;
        }

        if (faixa === "40a70") {
            return numero > 40 && numero <= 70;
        }

        if (faixa === "70a100") {
            return numero > 70 && numero <= 100;
        }

        if (faixa === "acima100") {
            return numero > 100;
        }

        if (faixa === "personalizado") {
            const limite = Number(precoPersonalizado.value);

            return Number.isFinite(limite) &&
                limite > 0 &&
                numero <= limite;
        }
        return true;
    }

    async function aplicarFiltros() {
        const buscaAtual = ++numeroBusca;

        if (avisoPreco) {
            avisoPreco.hidden = true;
        }

        let restaurantesPorPreco = null;

        if (preco.value) {
            if (contador) {
                contador.textContent = "Buscando preços...";
            }

            try {
                const resposta = await fetch("/api/pratos", {
                    cache: "no-store"
                });

                if (!resposta.ok) {
                    throw new Error("Erro HTTP " + resposta.status);
                }

                const pratos = await resposta.json();

                if (!Array.isArray(pratos)) {
                    throw new Error("Resposta inválida da API");
                }

                restaurantesPorPreco = new Set();

                pratos.forEach(function (prato) {
                    if (
                        prato.restauranteId != null &&
                        correspondePreco(prato.preco, preco.value)
                    ) {
                        restaurantesPorPreco.add(
                            String(prato.restauranteId)
                        );
                    }
                });

            } catch (erro) {
                if (buscaAtual !== numeroBusca) {
                    return;
                }

                console.error("Falha no filtro de preço:", erro);

                cards.forEach(function (card) {
                    card.hidden = true;
                });

                if (contador) {
                    contador.textContent = "Filtro indisponível";
                }

                if (mensagemVazia) {
                    mensagemVazia.hidden = true;
                }

                if (avisoPreco) {
                    avisoPreco.textContent =
                        "Não foi possível consultar os preços. " +
                        "Verifique a API e tente novamente.";

                    avisoPreco.hidden = false;
                }

                return;
            }
        }

        if (buscaAtual !== numeroBusca) {
            return;
        }

        let total = 0;

        cards.forEach(function (card) {

            const categoriaValida = correspondeCategoria(
                card.dataset.categoria,
                categoria.value
            );

            const precoValido =
                restaurantesPorPreco === null ||
                restaurantesPorPreco.has(
                    card.dataset.restauranteId
                );

            const mostrar = categoriaValida && precoValido;

            card.hidden = !mostrar;

            if (mostrar) {
                total++;
            }
        });

        if (contador) {
            contador.textContent =
                total + " resultado(s) encontrado(s)";
        }

        if (mensagemVazia) {
            mensagemVazia.hidden = total !== 0;
        }

        atalhos.forEach(function (botao) {
            botao.classList.toggle(
                "active",
                botao.dataset.category === categoria.value
            );
        });
    }

    atalhos.forEach(function (botao) {
        botao.addEventListener("click", function () {
            categoria.value = botao.dataset.category;
            form.requestSubmit();
        });
    });

    aplicarFiltros();
});
