import { useEffect, useMemo, useState } from 'react';
import {
  addPossession,
  deletePossession,
  getCatalog,
  getPossessions,
  getProfile,
  removePossessionQuantity
} from '../services/api';

function normalizeList(value) {
  if (Array.isArray(value)) return value;
  if (Array.isArray(value?.content)) return value.content;
  if (Array.isArray(value?.items)) return value.items;
  return [];
}

function normalizeSticker(sticker) {
  return {
    codigo: sticker.codigo ?? sticker.codigoFigurinha ?? sticker.code,
    tipo: sticker.tipo ?? sticker.tipoFigurinha ?? sticker.type,
    nome: sticker.nome ?? sticker.name ?? 'Figurinha',
    valorDeMercado: sticker.valorDeMercado ?? sticker.valor_de_mercado ?? sticker.marketValue
  };
}

function normalizePossession(possession) {
  const sticker = possession.figurinha ?? {};
  return {
    idPosse: possession.idPosse ?? possession.id_posse ?? possession.id,
    quantidade: Number(possession.quantidade ?? possession.quantity ?? 0),
    codigo: possession.codigoFigurinha ?? possession.codigo ?? sticker.codigo ?? '—',
    tipo: possession.tipoFigurinha ?? possession.tipo ?? sticker.tipo ?? '—',
    nome: possession.nomeFigurinha ?? possession.nome ?? sticker.nome ?? 'Figurinha'
  };
}

export default function InventoryPage() {
  const [profile, setProfile] = useState(null);
  const [catalog, setCatalog] = useState([]);
  const [possessions, setPossessions] = useState([]);
  const [form, setForm] = useState({ codigoFigurinha: '', tipoFigurinha: '', quantidade: 1 });
  const [removeAmounts, setRemoveAmounts] = useState({});
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  async function loadData() {
    setLoading(true);
    setError('');
    try {
      const currentProfile = await getProfile();
      const [inventoryResponse, catalogResponse] = await Promise.all([
        getPossessions(currentProfile.cpf),
        getCatalog()
      ]);

      setProfile(currentProfile);
      setPossessions(normalizeList(inventoryResponse).map(normalizePossession));
      setCatalog(normalizeList(catalogResponse).map(normalizeSticker));
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadData();
  }, []);

  const selectedSticker = useMemo(
    () => catalog.find(
      (sticker) => sticker.codigo === form.codigoFigurinha && sticker.tipo === form.tipoFigurinha
    ),
    [catalog, form.codigoFigurinha, form.tipoFigurinha]
  );

  function handleStickerChange(event) {
    const [codigoFigurinha, tipoFigurinha] = event.target.value.split('::');
    setForm((current) => ({ ...current, codigoFigurinha, tipoFigurinha }));
  }

  async function handleAdd(event) {
    event.preventDefault();
    setError('');
    setMessage('');

    if (!profile) {
      setError('Não foi possível identificar o usuário autenticado.');
      return;
    }

    if (!form.codigoFigurinha || !form.tipoFigurinha) {
      setError('Selecione uma figurinha do catálogo.');
      return;
    }

    if (Number(form.quantidade) <= 0) {
      setError('A quantidade deve ser maior que zero.');
      return;
    }

    setSubmitting(true);
    try {
      const payload = {
        cpfUsuario: profile.cpf,
        codigoFigurinha: form.codigoFigurinha,
        tipoFigurinha: form.tipoFigurinha,
        quantidade: Number(form.quantidade)
      };

      await addPossession(profile.cpf, payload);
      setMessage('Figurinha adicionada ao inventário.');
      setForm({ codigoFigurinha: '', tipoFigurinha: '', quantidade: 1 });
      await loadData();
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  }

  async function handleRemoveQuantity(possession) {
    const quantidade = Number(removeAmounts[possession.idPosse] ?? 1);
    if (quantidade <= 0 || quantidade > possession.quantidade) {
      setError(`Informe uma quantidade entre 1 e ${possession.quantidade}.`);
      return;
    }

    setError('');
    setMessage('');
    try {
      await removePossessionQuantity(profile.cpf, possession.idPosse, quantidade);
      setMessage('Quantidade atualizada.');
      await loadData();
    } catch (err) {
      setError(err.message);
    }
  }

  async function handleDelete(possession) {
    const confirmed = window.confirm(`Remover ${possession.nome} do inventário?`);
    if (!confirmed) return;

    setError('');
    setMessage('');
    try {
      await deletePossession(profile.cpf, possession.idPosse);
      setMessage('Posse removida do inventário.');
      await loadData();
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <div className="inventory-page">
      <section className="page-heading">
        <div>
          <span className="eyebrow">MINHA COLEÇÃO</span>
          <h1>Inventário</h1>
          <p>Adicione figurinhas do catálogo e gerencie as quantidades disponíveis.</p>
        </div>
      </section>

      <section className="inventory-add-panel">
        <div>
          <span className="eyebrow">NOVA POSSE</span>
          <h2>Adicionar figurinha</h2>
        </div>

        <form className="inventory-add-form" onSubmit={handleAdd}>
          <label className="inventory-sticker-field">
            Figurinha do catálogo
            <select
              value={form.codigoFigurinha ? `${form.codigoFigurinha}::${form.tipoFigurinha}` : ''}
              onChange={handleStickerChange}
              required
            >
              <option value="">Selecione...</option>
              {catalog.map((sticker) => (
                <option key={`${sticker.codigo}-${sticker.tipo}`} value={`${sticker.codigo}::${sticker.tipo}`}>
                  {sticker.nome} — {sticker.codigo} ({sticker.tipo})
                </option>
              ))}
            </select>
          </label>

          <label>
            Quantidade
            <input
              type="number"
              min="1"
              value={form.quantidade}
              onChange={(event) => setForm((current) => ({ ...current, quantidade: event.target.value }))}
              required
            />
          </label>

          <button className="primary-button" type="submit" disabled={submitting || loading}>
            {submitting ? 'Adicionando...' : 'Adicionar ao inventário'}
          </button>
        </form>

        {selectedSticker?.valorDeMercado != null && (
          <small className="catalog-hint">
            Valor de mercado: R$ {Number(selectedSticker.valorDeMercado).toFixed(2)}
          </small>
        )}
      </section>

      {error && <div className="alert error">{error}</div>}
      {message && <div className="alert success">{message}</div>}

      {loading ? (
        <div className="empty-state">Carregando inventário...</div>
      ) : possessions.length === 0 ? (
        <div className="empty-state">
          <strong>Seu inventário está vazio.</strong>
          <span>Use o formulário acima para adicionar sua primeira figurinha.</span>
        </div>
      ) : (
        <section className="inventory-list-grid">
          {possessions.map((possession) => (
            <article className="inventory-possession-card" key={possession.idPosse}>
              <div className="inventory-possession-header">
                <div>
                  <span className="eyebrow">{possession.tipo}</span>
                  <h3>{possession.nome}</h3>
                </div>
                <span className="quantity-badge">{possession.quantidade} un.</span>
              </div>

              <dl className="inventory-meta">
                <div><dt>Código</dt><dd>{possession.codigo}</dd></div>
                <div><dt>ID da posse</dt><dd>{possession.idPosse}</dd></div>
              </dl>

              <div className="inventory-actions">
                <label>
                  Reduzir quantidade
                  <input
                    type="number"
                    min="1"
                    max={possession.quantidade}
                    value={removeAmounts[possession.idPosse] ?? 1}
                    onChange={(event) => setRemoveAmounts((current) => ({
                      ...current,
                      [possession.idPosse]: event.target.value
                    }))}
                  />
                </label>
                <button type="button" className="secondary-button" onClick={() => handleRemoveQuantity(possession)}>
                  Reduzir
                </button>
                <button type="button" className="danger-button" onClick={() => handleDelete(possession)}>
                  Excluir posse
                </button>
              </div>
            </article>
          ))}
        </section>
      )}
    </div>
  );
}
