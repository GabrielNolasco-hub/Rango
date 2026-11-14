document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById("restaurant-filters");
    const categoria = document.getElementById("categoria");
    const preco = document.getElementById("preco");
    const cards = Array.from(document.querySelectorAll(".restaurant-card"));
    const contador = document.getElementById("results-count");
    const mensagemVazia = document.getElementById("empty-results");
    const avisoPreco = document.getElementById("price-warning");
    const atalhos = document.querySelectorAll(".category-choice");
    const parametros = new URLSearchParams(window.location.search);

    categoria.value = parametros.get("categoria") || "";
    preco.value = parametros.get("preco") || "";

    function normalizar (texto) {
        return (texto || "")
            .toLowerCase()
            .normalize("NFD")
            .replace(/[\u0300-\u036f]/g, "")
            .trim();
    }

    function correspondeCategoria(texto, filtro) {
        if (!filtro) return true;

        const valor = normalizar(texto);
        const palavras = {
            massas: ["massa", "macarrão", "espaguete", "lasanha"],
            hamburguer: ["hambúrguer", "hamburguer", "burger", "x-burguer"],
            pizza: ["pizza", "pizzaria"],
            sushi: ["sushi", "japonês", "japonesa", "temaki"],
            chinesa: ["chinesa", "china", "chop suey"],
            mexicana: ["mexicana", "taco", "burrito", "nachos"],
            brasileira: ["brasileira", "feijoada", "churrasco", "picanha"],
            vegetariana: ["vegetariana", "vegana", "tofu", "salada"],
            frutosdomar: ["frutos do mar", "peixe", "camarão", "sushi"],
            sobremesas: ["sobremesa", "doce", "bolo", "sorvete"],
            regional : ["regional", "comida típica", "prato típico", "nordeste"],
        };

        return (palavras[filtro] || [filtro]).some(palavra => valor.includes(palavra));
    }

    function atualizarResultados() {
        let visiveis = 0;

        cards.forEach(card => {
            const mostrar = correspondeCategoria(
                card.dataset.categoria, 
                categoria.value
            );
        
            card.hidden = !mostrar;
            if (mostrar) visiveis++;
        });
        contador.textContent = visiveis +
           (visiveis === 1 ? " resultado encontrado" : " resultados encontrados");
        mensagemVazia.hidden = visiveis !== 0;
        avisoPreco.hidden = !preco.value;

        atalhos.forEach(botao => {
            botao.classList.toggle("active", botao.dataset.categoria === categoria.value);
        });
    }

        atalhos.forEach(function (botao) {
        botao.addEventListener("click", function () {
            categoria.value = botao.dataset.category;
            form.requestSubmit();
        });

        });


        atualizarResultados();
    });