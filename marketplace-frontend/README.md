# Frontend — Marketplace de Figurinhas WC26

Frontend React + Vite integrado à API Spring Boot.

## Funcionalidades

- Login por sessão (`POST /login`)
- Cadastro com endereço aninhado em `endereco`
- Listagem de ofertas
- Criação de venda e troca
- Carregamento do inventário em `GET /api/usuarios/{cpf}/posses`
- Seleção de itens do inventário para `itensOfertados`
- Definição de quantidade, condição e foto por item
- Inclusão de `itensSolicitados` durante a criação de trocas

## Executar

```bash
npm install
npm run dev
```

Abra `http://localhost:5173`.

O backend deve estar em `http://localhost:8081`. O proxy do Vite encaminha `/api`, `/login` e `/logout`.

## Contratos usados

### Venda

```json
{
  "valorDaProposta": 200.00,
  "prazoLimite": "2026-08-20T23:59:59",
  "descricao": "Vendo lote de figurinhas raras.",
  "itensOfertados": [
    {
      "idPosse": 210,
      "quantidadeOfertada": 3,
      "condicao": "EXCELENTE",
      "foto": "lote-raras.jpg"
    }
  ]
}
```

### Troca

```json
{
  "prazoLimite": "2026-08-10T23:59:59",
  "descricao": "Troco repetidas.",
  "itensOfertados": [
    {
      "idPosse": 150,
      "quantidadeOfertada": 1,
      "condicao": "EXCELENTE",
      "foto": null
    }
  ],
  "itensSolicitados": [
    {
      "codigoFigurinha": "FRA07",
      "tipoFigurinha": "COMUM",
      "quantidade": 1
    }
  ]
}
```

## Ajuste dos enums

O arquivo `src/pages/CreateOfferPage.jsx` possui as constantes `CONDITIONS` e `STICKER_TYPES`. Caso os nomes dos seus enums Java sejam diferentes, altere apenas essas listas.

## Spring Security

Para um frontend React, o `POST /login` deve responder com status `200` no sucesso e `401` na falha, sem redirecionar para template Thymeleaf. Em desenvolvimento, as rotas usadas pelo frontend precisam aceitar o cookie `JSESSIONID`.

## Inventário

A rota `/inventario` permite:

- consultar `GET /api/usuarios/{cpf}/posses`;
- carregar o catálogo em `GET /api/figurinhas`;
- adicionar uma posse com `POST /api/usuarios/{cpf}/posses`;
- reduzir quantidade com `PATCH /api/usuarios/{cpf}/posses/{idPosse}/quantidade/remover`;
- excluir uma posse com `DELETE /api/usuarios/{cpf}/posses/{idPosse}`.

O frontend envia a criação da posse neste formato:

```json
{
  "codigoFigurinha": "BRA01",
  "tipoFigurinha": "COMUM",
  "quantidade": 2
}
```

O PATCH de redução envia:

```json
{
  "quantidade": 1
}
```

Caso seus DTOs usem nomes diferentes, ajuste somente as funções `addPossession` e `removePossessionQuantity` em `src/services/api.js`.
