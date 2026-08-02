CREATE OR REPLACE FUNCTION validar_usuario_item_ofertado()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS $$
DECLARE
    usuario_posse VARCHAR(14);
    usuario_oferta VARCHAR(14);
BEGIN
    SELECT pf.usuario
    INTO usuario_posse
    FROM possui_figurinha AS pf
    WHERE pf.id_posse = NEW.id_posse;

    IF usuario_posse IS NULL THEN
        RAISE EXCEPTION
            'A posse de ID % não existe.',
            NEW.id_posse;
    END IF;

    SELECT o.usuario_proponente
    INTO usuario_oferta
    FROM oferta AS o
    WHERE o.id_oferta = NEW.id_oferta;

    IF usuario_oferta IS NULL THEN
        RAISE EXCEPTION
            'A oferta de ID % não existe.',
            NEW.id_oferta;
    END IF;

    IF usuario_posse <> usuario_oferta THEN
        RAISE EXCEPTION
            'A posse % pertence ao usuário %, mas a oferta % pertence ao usuário %.',
            NEW.id_posse,
            usuario_posse,
            NEW.id_oferta,
            usuario_oferta;
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_validar_usuario_item_ofertado
    BEFORE INSERT OR UPDATE OF id_posse, id_oferta
    ON item_ofertado
    FOR EACH ROW
EXECUTE FUNCTION validar_usuario_item_ofertado();

CREATE OR REPLACE FUNCTION validar_usuarios_avaliacao()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS $$
DECLARE
    usuario_aceitante   VARCHAR(14);
    usuario_proponente VARCHAR(14);
BEGIN
    SELECT
        c.aceitante,
        o.usuario_proponente
    INTO
        usuario_aceitante,
        usuario_proponente
    FROM concretizacao AS c
             JOIN oferta AS o
                  ON o.id_oferta = c.id_oferta
    WHERE c.id_concretizacao = NEW.id_concretizacao;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'A concretização de ID % não existe ou não possui uma oferta válida.',
            NEW.id_concretizacao;
    END IF;

    IF NEW.usuario_avaliador <> usuario_aceitante THEN
        RAISE EXCEPTION
            'O usuário avaliador % deve ser o aceitante % da concretização %.',
            NEW.usuario_avaliador,
            usuario_aceitante,
            NEW.id_concretizacao;
    END IF;

    IF NEW.usuario_avaliado <> usuario_proponente THEN
        RAISE EXCEPTION
            'O usuário avaliado % deve ser o proponente % da oferta concretizada.',
            NEW.usuario_avaliado,
            usuario_proponente;
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_validar_usuarios_avaliacao
    BEFORE INSERT OR UPDATE OF
        usuario_avaliador,
        usuario_avaliado,
        id_concretizacao
    ON Avaliacao
    FOR EACH ROW
EXECUTE FUNCTION validar_usuarios_avaliacao();

CREATE OR REPLACE FUNCTION validar_quantidade_item_ofertado()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS $$
DECLARE
    quantidade_possuida INT;
BEGIN
    SELECT quantidade
    INTO quantidade_possuida
    FROM possui_figurinha
    WHERE id_posse = NEW.id_posse;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'A posse de ID % não existe.',
            NEW.id_posse;
    END IF;

    IF NEW.quantidade_ofertada > quantidade_possuida THEN
        RAISE EXCEPTION
            'Quantidade ofertada (%) maior que a quantidade possuída (%).',
            NEW.quantidade_ofertada,
            quantidade_possuida;
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_validar_quantidade_item_ofertado
    BEFORE INSERT OR UPDATE OF quantidade_ofertada, id_posse
    ON item_ofertado
    FOR EACH ROW
EXECUTE FUNCTION validar_quantidade_item_ofertado();

CREATE OR REPLACE FUNCTION validar_aceitante_concretizacao()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS $$
DECLARE
    v_usuario_proponente VARCHAR(14);
BEGIN
    SELECT o.usuario_proponente
    INTO v_usuario_proponente
    FROM oferta AS o
    WHERE o.id_oferta = NEW.id_oferta;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'A oferta de ID % não existe.',
            NEW.id_oferta;
    END IF;

    IF NEW.aceitante = v_usuario_proponente THEN
        RAISE EXCEPTION
            'O usuário % não pode aceitar a própria oferta %.',
            NEW.aceitante,
            NEW.id_oferta;
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_validar_aceitante_concretizacao
    BEFORE INSERT OR UPDATE OF aceitante, id_oferta
    ON concretizacao
    FOR EACH ROW
EXECUTE FUNCTION validar_aceitante_concretizacao();

CREATE OR REPLACE FUNCTION validar_tipo_oferta_troca()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS $$
DECLARE
    tipo_oferta_encontrado tipo_oferta;
BEGIN
    SELECT tipo
    INTO tipo_oferta_encontrado
    FROM oferta
    WHERE id_oferta = NEW.id_oferta;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'A oferta de ID % não existe.',
            NEW.id_oferta;
    END IF;

    IF tipo_oferta_encontrado <> 'TROCA' THEN
        RAISE EXCEPTION
            'A oferta % não é do tipo Troca.',
            NEW.id_oferta;
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_validar_tipo_oferta_troca
    BEFORE INSERT OR UPDATE OF id_oferta
    ON troca
    FOR EACH ROW
EXECUTE FUNCTION validar_tipo_oferta_troca();

CREATE OR REPLACE FUNCTION validar_tipo_oferta_venda()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS $$
DECLARE
    tipo_oferta_encontrado tipo_oferta;
BEGIN
    SELECT tipo
    INTO tipo_oferta_encontrado
    FROM oferta
    WHERE id_oferta = NEW.id_oferta;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'A oferta de ID % não existe.',
            NEW.id_oferta;
    END IF;

    IF tipo_oferta_encontrado <> 'VENDA' THEN
        RAISE EXCEPTION
            'A oferta % não é do tipo Venda.',
            NEW.id_oferta;
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_validar_tipo_oferta_venda
    BEFORE INSERT OR UPDATE OF id_oferta
    ON venda
    FOR EACH ROW
EXECUTE FUNCTION validar_tipo_oferta_venda();

CREATE OR REPLACE FUNCTION validar_status_oferta_concretizacao()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS $$
DECLARE
    status_atual status_oferta;
BEGIN
    SELECT status
    INTO status_atual
    FROM oferta
    WHERE id_oferta = NEW.id_oferta;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'A oferta de ID % não existe.',
            NEW.id_oferta;
    END IF;

    IF status_atual <> 'PENDENTE' THEN
        RAISE EXCEPTION
            'A oferta % não está pendente. Status atual: %.',
            NEW.id_oferta,
            status_atual;
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_validar_status_oferta_concretizacao
    BEFORE INSERT
    ON concretizacao
    FOR EACH ROW
EXECUTE FUNCTION validar_status_oferta_concretizacao();

CREATE OR REPLACE FUNCTION atualizar_media_avaliacao_usuario()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS $$
DECLARE
    usuario_alvo VARCHAR(14);
BEGIN
    usuario_alvo := COALESCE(NEW.usuario_avaliado, OLD.usuario_avaliado);

    UPDATE usuario
    SET avaliacao_media = COALESCE(
            (
                SELECT AVG(nota)
                FROM avaliacao
                WHERE usuario_avaliado = usuario_alvo
            ),
            0
                          )
    WHERE cpf = usuario_alvo;

    RETURN COALESCE(NEW, OLD);
END;
$$;

CREATE TRIGGER trg_atualizar_media_avaliacao_usuario
    AFTER INSERT OR UPDATE OR DELETE
    ON avaliacao
    FOR EACH ROW
EXECUTE FUNCTION atualizar_media_avaliacao_usuario();

CREATE OR REPLACE FUNCTION impedir_autoavaliacao()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS $$
BEGIN
    IF NEW.usuario_avaliador = NEW.usuario_avaliado THEN
        RAISE EXCEPTION
            'Um usuário não pode avaliar a si mesmo.';
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_impedir_autoavaliacao
    BEFORE INSERT OR UPDATE OF usuario_avaliador, usuario_avaliado
    ON avaliacao
    FOR EACH ROW
EXECUTE FUNCTION impedir_autoavaliacao();
