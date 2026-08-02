console.log("Marketplace iniciado.");

let ofertaSelecionada = null;

// =========================
// FORMULÁRIO
// =========================

const formSolicitado = document.getElementById("formItemSolicitado");

if (formSolicitado) {

    formSolicitado.addEventListener("submit", async function (e) {

        e.preventDefault();

        if (!ofertaSelecionada) {
            alert("Selecione uma oferta.");
            return;
        }

        const body = {

            codigoFigurinha: document.getElementById("codigoSolicitado").value,

            tipoFigurinha: document.getElementById("tipoSolicitado").value,

            quantidade: Number(document.getElementById("quantidadeSolicitada").value)

        };

        const response = await fetch(
            `/api/ofertas/${ofertaSelecionada.idOferta}/itens-solicitados`,
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(body)
            }
        );

        if (response.ok) {

            alert("Item solicitado adicionado!");

            formSolicitado.reset();

            carregarItensSolicitados();

        } else {

            alert("Erro ao adicionar item.");

        }

    });

}

// =========================
// CARREGAR ITENS
// =========================

async function carregarItensSolicitados() {

    if (!ofertaSelecionada)
        return;

    const response = await fetch(
        `/api/ofertas/${ofertaSelecionada.idOferta}/itens-solicitados`
    );

    const lista = await response.json();

    const tbody = document.getElementById("listaSolicitados");

    tbody.innerHTML = "";

    lista.forEach(item => {

        tbody.innerHTML += `
            <tr>
                <td>${item.codigoFigurinha}</td>
                <td>${item.tipoFigurinha}</td>
                <td>${item.quantidade}</td>
            </tr>
        `;

    });

}