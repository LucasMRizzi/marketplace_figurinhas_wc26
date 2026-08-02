CREATE TYPE tipo_figurinha AS ENUM
    (
    'COMUM',
    'LEGEND_COMUM',
    'LEGEND_BRONZE',
    'LEGEND_PRATA',
    'LEGEND_OURO'
);

CREATE TYPE status_oferta AS ENUM
    (
    'PENDENTE',
    'CONCRETIZADA',
    'EXPIRADA'
);

CREATE TYPE tipo_oferta AS ENUM
    (
    'VENDA',
    'TROCA'
);

CREATE TYPE status_pagamento AS ENUM
    (
    'PENDENTE',
    'PROCESSAMENTO',
    'PAGO',
    'RECUSADO'
);

CREATE TYPE condicao AS ENUM
    (
    'DESGASTADA',
    'RAZOAVEL',
    'EXCELENTE'
);