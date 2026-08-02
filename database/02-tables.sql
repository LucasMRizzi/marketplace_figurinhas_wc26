CREATE TABLE Usuario
(
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(14) NOT NULL,
    email VARCHAR(100) NOT NULL,
    telefone VARCHAR(15) NOT NULL,
    saldo NUMERIC(10, 2) NOT NULL,
    avaliacao_media NUMERIC(3, 2) NOT NULL DEFAULT 0,
    logradouro VARCHAR(100) NOT NULL,
    numero INT NOT NULL,
    caixa_postal VARCHAR(15) NOT NULL,
    cidade VARCHAR(50) NOT NULL,
    cep VARCHAR(13) NOT NULL,
    senha VARCHAR(255) NOT NULL,

    CONSTRAINT pk_usuario
        PRIMARY KEY (cpf),

    CONSTRAINT ck_usuario_avaliacao_media
        CHECK (avaliacao_media BETWEEN 0 AND 5),

    CONSTRAINT ck_usuario_saldo
        CHECK (saldo >= 0),

    CONSTRAINT uq_usuario_email
        UNIQUE (email)
);

CREATE TABLE Figurinha
(
    nome VARCHAR(100) NOT NULL,
    codigo VARCHAR(6) NOT NULL,
    valor_de_mercado NUMERIC(10,2) NOT NULL,
    tipo tipo_figurinha NOT NULL,

    CONSTRAINT pk_figurinha
        PRIMARY KEY (codigo, tipo),

    CONSTRAINT ck_figurinha_valor
        CHECK (valor_de_mercado >= 0)
);

CREATE TABLE Album
(
    nome VARCHAR(100) NOT NULL,
    completude NUMERIC(5,2) NOT NULL,
    usuario VARCHAR(14) NOT NULL,

    CONSTRAINT pk_album
        PRIMARY KEY (nome, usuario),

    CONSTRAINT fk_album_usuario
        FOREIGN KEY (usuario)
        REFERENCES usuario(cpf),

    CONSTRAINT ck_album_completude
        CHECK(completude BETWEEN 0 AND 100)
);

CREATE TABLE Oferta
(
    status status_oferta NOT NULL,
    tipo tipo_oferta NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    id_oferta SERIAL NOT NULL,
    usuario_proponente VARCHAR(14) NOT NULL,
    prazo_limite TIMESTAMP NOT NULL,
    descricao VARCHAR(140) NOT NULL,
    valor_de_mercado NUMERIC(10, 2) NOT NULL,

    CONSTRAINT pk_oferta
        PRIMARY KEY (id_oferta),

    CONSTRAINT fk_oferta_usuario
        FOREIGN KEY (usuario_proponente)
        REFERENCES  Usuario(cpf),

    CONSTRAINT ck_oferta_valor_de_mercado
        CHECK(valor_de_mercado >= 0)
);

CREATE TABLE Troca
(
    id_oferta INT NOT NULL,

    CONSTRAINT pk_troca
        PRIMARY KEY (id_oferta),

    CONSTRAINT fk_troca_oferta
        FOREIGN KEY (id_oferta)
        REFERENCES Oferta(id_oferta)
);

CREATE TABLE Venda
(
    valor_da_proposta NUMERIC(10,2) NOT NULL,
    id_oferta INT NOT NULL,

    CONSTRAINT pk_venda
      PRIMARY KEY (id_oferta),

    CONSTRAINT fk_venda_oferta
      FOREIGN KEY (id_oferta)
          REFERENCES Oferta(id_oferta),

    CONSTRAINT ck_venda_valor_proposta
      CHECK ( valor_da_proposta >= 0 )
);

CREATE TABLE Possui_Figurinha
(
    quantidade INT NOT NULL,
    id_posse SERIAL NOT NULL,
    usuario VARCHAR(14) NOT NULL,
    codigo_da_figurinha VARCHAR(6) NOT NULL,
    tipo_da_figurinha tipo_figurinha NOT NULL,

    CONSTRAINT pk_possui_figurinha
        PRIMARY KEY (id_posse),

    CONSTRAINT fk_possui_figurinha_usuario
        FOREIGN KEY (usuario)
        REFERENCES  Usuario(cpf),

    CONSTRAINT fk_possui_figurinha_figurinha
        FOREIGN KEY (codigo_da_figurinha, tipo_da_figurinha)
        REFERENCES Figurinha(codigo, tipo),

    CONSTRAINT uq_possui_figurinha_usuario_figurinha
        UNIQUE (usuario, codigo_da_figurinha, tipo_da_figurinha),

    CONSTRAINT ck_possui_figurinha_quantidade
        CHECK (quantidade > 0)
);

CREATE TABLE Deseja_Figurinha
(
    usuario VARCHAR(14) NOT NULL,
    codigo_da_figurinha VARCHAR(6) NOT NULL,
    tipo_da_figurinha tipo_figurinha NOT NULL,

    CONSTRAINT pk_deseja_figurinha
        PRIMARY KEY (usuario, codigo_da_figurinha, tipo_da_figurinha),

    CONSTRAINT fk_deseja_figurinha_usuario
        FOREIGN KEY (usuario)
        REFERENCES Usuario(cpf),

    CONSTRAINT fk_deseja_figurinha_figurinha
        FOREIGN KEY (codigo_da_figurinha, tipo_da_figurinha)
        REFERENCES Figurinha(codigo, tipo)
);

CREATE TABLE Figurinhas_Coladas
(
    codigo_da_figurinha VARCHAR(6) NOT NULL,
    tipo_da_figurinha tipo_figurinha NOT NULL,
    nome_do_album VARCHAR(100) NOT NULL,
    usuario VARCHAR(14) NOT NULL,

    CONSTRAINT pk_figurinhas_coladas
        PRIMARY KEY (codigo_da_figurinha, tipo_da_figurinha, nome_do_album, usuario),

    CONSTRAINT fk_figurinhas_coladas_figurinha
        FOREIGN KEY (codigo_da_figurinha, tipo_da_figurinha)
        REFERENCES Figurinha(codigo, tipo),

    CONSTRAINT fk_figurinhas_coladas_album
        FOREIGN KEY (nome_do_album, usuario)
        REFERENCES Album(nome, usuario)
);

CREATE TABLE Concretizacao
(
    status_do_pagamento status_pagamento NOT NULL,
    data_do_aceite TIMESTAMP NOT NULL,
    id_concretizacao SERIAL NOT NULL,
    id_oferta INT NOT NULL,
    aceitante VARCHAR(14) NOT NULL,
    codigo_transacao VARCHAR(100),
    motivo_recusa VARCHAR(255),

    CONSTRAINT pk_concretizacao
        PRIMARY KEY (id_concretizacao),

    CONSTRAINT fk_concretizacao_oferta
        FOREIGN KEY (id_oferta)
        REFERENCES Oferta(id_oferta),

    CONSTRAINT fk_concretizacao_usuario
        FOREIGN KEY (aceitante)
        REFERENCES Usuario(cpf),

    CONSTRAINT uq_concretizacao_oferta
        UNIQUE (id_oferta)
);

CREATE TABLE Avaliacao
(
    nota NUMERIC(3, 2) NOT NULL,
    comentario VARCHAR(150) NOT NULL,
    data TIMESTAMP NOT NULL,
    usuario_avaliador VARCHAR(14) NOT NULL,
    usuario_avaliado VARCHAR(14) NOT NULL,
    id_concretizacao INT NOT NULL,

    CONSTRAINT pk_avaliacao
        PRIMARY KEY (usuario_avaliador, usuario_avaliado, id_concretizacao),

    CONSTRAINT fk_avaliacao_usuario_avaliador
        FOREIGN KEY (usuario_avaliador)
        REFERENCES Usuario(cpf),

    CONSTRAINT fk_avaliacao_usuario_avaliado
        FOREIGN KEY (usuario_avaliado)
        REFERENCES Usuario(cpf),

    CONSTRAINT fk_avaliacao_concretizacao
        FOREIGN KEY (id_concretizacao)
        REFERENCES Concretizacao(id_concretizacao),

    CONSTRAINT ck_avaliacao_nota
        CHECK (nota BETWEEN 0 AND 5)
);


CREATE TABLE Item_Ofertado
(
    foto VARCHAR(255),
    condicao condicao NOT NULL,
    id_item SERIAL NOT NULL,
    quantidade_ofertada INT NOT NULL,
    id_oferta INT NOT NULL,
    id_posse INT NOT NULL,

    CONSTRAINT pk_item_ofertado
        PRIMARY KEY (id_item, id_oferta),

    CONSTRAINT fk_item_ofertado_oferta
        FOREIGN KEY (id_oferta)
        REFERENCES Oferta(id_oferta),

    CONSTRAINT fk_item_ofertado_possui_figurinha
        FOREIGN KEY (id_posse)
        REFERENCES Possui_Figurinha(id_posse),

    CONSTRAINT ck_item_ofertado_quantidade
        CHECK (quantidade_ofertada > 0)
);

CREATE TABLE Item_Solicitado
(
    id_item_solicitado SERIAL NOT NULL,
    quantidade INT NOT NULL,
    codigo_da_figurinha VARCHAR(6) NOT NULL,
    tipo_da_figurinha tipo_figurinha NOT NULL,
    id_oferta INT NOT NULL,

    CONSTRAINT pk_item_solicitado
        PRIMARY KEY (id_item_solicitado),

    CONSTRAINT fk_item_solicitado_figurinha
        FOREIGN KEY (codigo_da_figurinha, tipo_da_figurinha)
        REFERENCES Figurinha(codigo, tipo),

    CONSTRAINT fk_item_solicitado_troca
        FOREIGN KEY (id_oferta)
        REFERENCES Troca(id_oferta),

    CONSTRAINT uq_item_solicitado_figurinha_oferta
        UNIQUE (codigo_da_figurinha, tipo_da_figurinha, id_oferta),

    CONSTRAINT ck_item_solicitado_quantidade
        CHECK (quantidade > 0)
);
