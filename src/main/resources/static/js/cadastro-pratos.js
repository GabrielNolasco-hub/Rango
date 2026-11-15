document.addEventListener("DOMContentLoaded", function () {

    const form = document.getElementById("form-cadastro-prato");

    if (!form) {
        return;
    }

    const botaoSalvar = document.getElementById("salvar-prato");
    const mensagem = document.getElementById("mensagem-cadastro-prato");

    form.addEventListener("submit", async function (event) {

        event.preventDefault();

        const restauranteId = form.dataset.restauranteId;

        const nome = document.getElementById("nomePrato").value.trim();

        const descricao = document.getElementById("descricaoPrato").value.trim();

        const preco = Number(
            document.getElementById("precoPrato").value
        );

        if (!restauranteId || !nome || !Number.isFinite(preco) || preco <= 0) {

            mensagem.textContent =
                "Preencha o nome e informe um preço válido.";

            mensagem.className = "rd-dish-feedback error";

            return;
        }

        const novoPrato = {
            nome: nome,
            descricao: descricao,
            preco: preco
        };

        botaoSalvar.disabled = true;
        botaoSalvar.textContent = "Salvando...";

        mensagem.textContent = "";
        mensagem.className = "rd-dish-feedback";

        try {

            const resposta = await fetch(
                `/api/restaurantes/${encodeURIComponent(restauranteId)}/pratos`,
                {
                    method: "POST",

                    headers: {
                        "Content-Type": "application/json"
                    },

                    body: JSON.stringify(novoPrato)
                }
            );

            if (!resposta.ok) {
                throw new Error("Erro HTTP " + resposta.status);
            }

            mensagem.textContent = "Prato cadastrado com sucesso!";
            mensagem.className = "rd-dish-feedback success";

            window.location.reload();

        } catch (erro) {

            console.error("Erro ao cadastrar prato:", erro);

            mensagem.textContent =
                "Não foi possível cadastrar o prato. Tente novamente.";

            mensagem.className = "rd-dish-feedback error";

        } finally {

            botaoSalvar.disabled = false;
            botaoSalvar.textContent = "Salvar prato";

        }

    });

});
