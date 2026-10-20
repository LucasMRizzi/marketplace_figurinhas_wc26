CREATE TABLE IF NOT EXISTS Proposta_Troca
(
    id_proposta SERIAL PRIMARY KEY,
    id_oferta INT NOT NULL REFERENCES Oferta(id_oferta),
    solicitante VARCHAR(14) NOT NULL REFERENCES Usuario(cpf),
    status VARCHAR(20) NOT NULL CHECK
        (status IN ('PENDENTE', 'ACEITA', 'RECUSADA', 'CANCELADA')),
    data_criacao TIMESTAMP NOT NULL,
    data_resposta TIMESTAMP
);@@

CREATE TABLE IF NOT EXISTS Item_Proposta_Troca
(
    id_item_proposta SERIAL PRIMARY KEY,
    id_proposta INT NOT NULL REFERENCES Proposta_Troca(id_proposta) ON DELETE CASCADE,
    id_posse INT NOT NULL REFERENCES Possui_Figurinha(id_posse),
    quantidade INT NOT NULL CHECK (quantidade > 0),
    condicao condicao NOT NULL,
    foto VARCHAR(255)
);@@

CREATE INDEX IF NOT EXISTS idx_proposta_troca_oferta
    ON Proposta_Troca(id_oferta);@@
CREATE INDEX IF NOT EXISTS idx_proposta_troca_solicitante
    ON Proposta_Troca(solicitante);@@

CREATE OR REPLACE FUNCTION validar_usuarios_avaliacao()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS $$
DECLARE
    usuario_aceitante   VARCHAR(14);
    usuario_proponente VARCHAR(14);
BEGIN
    SELECT c.aceitante, o.usuario_proponente
    INTO usuario_aceitante, usuario_proponente
    FROM concretizacao AS c
    JOIN oferta AS o ON o.id_oferta = c.id_oferta
    WHERE c.id_concretizacao = NEW.id_concretizacao;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'A concretização de ID % não existe ou não possui uma oferta válida.',
            NEW.id_concretizacao;
    END IF;

    IF NOT (
        (NEW.usuario_avaliador = usuario_aceitante
            AND NEW.usuario_avaliado = usuario_proponente)
        OR
        (NEW.usuario_avaliador = usuario_proponente
            AND NEW.usuario_avaliado = usuario_aceitante)
    ) THEN
        RAISE EXCEPTION
            'A avaliação deve ocorrer entre o proponente % e o aceitante % da concretização %.',
            usuario_proponente,
            usuario_aceitante,
            NEW.id_concretizacao;
    END IF;

    RETURN NEW;
END;
$$;@@
