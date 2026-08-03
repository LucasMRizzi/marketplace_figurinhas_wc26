import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { createSale, createTrade, getPossessions, getProfile } from '../services/api';

const CONDITIONS = ['PERFEITA', 'EXCELENTE', 'RAZOAVEL', 'DESGASTADA'];
const STICKER_TYPES = ['NORMAL', 'COMUM', 'LEGEND_COMUM', 'LEGEND_BRONZE', 'LEGEND_PRATA', 'LEGEND_OURO'];

function normalizeList(value) {
  if (Array.isArray(value)) return value;
  if (Array.isArray(value?.content)) return value.content;
  if (Array.isArray(value?.items)) return value.items;
  return [];
}

function possessionView(possession) {
  const sticker = possession.figurinha ?? possession.sticker ?? {};
  return {
    raw: possession,
    idPosse: possession.idPosse ?? possession.id_posse ?? possession.id,
    quantidade: Number(possession.quantidade ?? possession.quantity ?? 0),
    nome: possession.nomeFigurinha ?? possession.nome ?? sticker.nome ?? 'Figurinha',
    codigo: possession.codigoFigurinha ?? possession.codigo ?? sticker.codigo ?? '—',
    tipo: possession.tipoFigurinha ?? possession.tipo ?? sticker.tipo ?? '—'
  };
}

export default function CreateOfferPage() {
  const navigate = useNavigate();
  const [profile, setProfile] = useState(null);
  const [possessions, setPossessions] = useState([]);
  const [loadingPossessions, setLoadingPossessions] = useState(true);
  const [type, setType] = useState('VENDA');
  const [form, setForm] = useState({
    descricao: '', prazoLimite: '', valorDaProposta: '',
    itensOfertados: [], itensSolicitados: []
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    let active = true;
    async function load() {
      try {
        const currentProfile = await getProfile();
        const inventory = await getPossessions(currentProfile.cpf);
        if (!active) return;
        setProfile(currentProfile);
        setPossessions(normalizeList(inventory).map(possessionView));
      } catch (err) {
        if (active) setError(err.message);
      } finally {
        if (active) setLoadingPossessions(false);
      }
    }
    load();
    return () => { active = false; };
  }, []);

  const selectedIds = useMemo(
    () => new Set(form.itensOfertados.map((item) => item.idPosse)),
    [form.itensOfertados]
  );

  function updateField(event) {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  }

  function togglePossession(possession) {
    setForm((current) => {
      const exists = current.itensOfertados.some((item) => item.idPosse === possession.idPosse);
      return {
        ...current,
        itensOfertados: exists
          ? current.itensOfertados.filter((item) => item.idPosse !== possession.idPosse)
          : [...current.itensOfertados, {
              idPosse: possession.idPosse,
              nome: possession.nome,
              codigo: possession.codigo,
              tipo: possession.tipo,
              quantidadeDisponivel: possession.quantidade,
              quantidadeOfertada: 1,
              condicao: 'PERFEITA',
              foto: ''
            }]
      };
    });
  }

  function updateOfferedItem(idPosse, field, value) {
    setForm((current) => ({
      ...current,
      itensOfertados: current.itensOfertados.map((item) => item.idPosse === idPosse
        ? { ...item, [field]: field === 'quantidadeOfertada' ? Number(value) : value }
        : item)
    }));
  }

  function addRequestedItem() {
    setForm((current) => ({
      ...current,
      itensSolicitados: [...current.itensSolicitados, {
        key: crypto.randomUUID(), codigoFigurinha: '', tipoFigurinha: 'NORMAL', quantidade: 1
      }]
    }));
  }

  function updateRequestedItem(key, field, value) {
    setForm((current) => ({
      ...current,
      itensSolicitados: current.itensSolicitados.map((item) => item.key === key
        ? { ...item, [field]: field === 'quantidade' ? Number(value) : value }
        : item)
    }));
  }

  function removeRequestedItem(key) {
    setForm((current) => ({
      ...current,
      itensSolicitados: current.itensSolicitados.filter((item) => item.key !== key)
    }));
  }

  function validate() {
    if (!profile?.cpf) throw new Error('Não foi possível identificar o usuário autenticado.');
    if (form.itensOfertados.length === 0) throw new Error('Selecione pelo menos uma figurinha do seu inventário.');
    for (const item of form.itensOfertados) {
      if (item.quantidadeOfertada < 1 || item.quantidadeOfertada > item.quantidadeDisponivel) {
        throw new Error(`Quantidade inválida para ${item.nome}.`);
      }
    }
    if (type === 'TROCA' && form.itensSolicitados.length === 0) {
      throw new Error('Adicione pelo menos uma figurinha solicitada para a troca.');
    }
  }

  function offeredPayload() {
    return form.itensOfertados.map((item) => ({
      idPosse: item.idPosse,
      quantidadeOfertada: item.quantidadeOfertada,
      condicao: item.condicao,
      foto: item.foto.trim() || null
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setLoading(true);
    setError('');
    try {
      validate();
      const commonPayload = {
        descricao: form.descricao,
        prazoLimite: form.prazoLimite,
        itensOfertados: offeredPayload()
      };

      if (type === 'VENDA') {
        await createSale(profile.cpf, {
          ...commonPayload,
          valorDaProposta: Number(form.valorDaProposta)
        });
      } else {
        await createTrade(profile.cpf, {
          ...commonPayload,
          itensSolicitados: form.itensSolicitados.map(({ codigoFigurinha, tipoFigurinha, quantidade }) => ({
            codigoFigurinha: codigoFigurinha.trim(), tipoFigurinha, quantidade
          }))
        });
      }
      navigate('/ofertas');
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <section>
      <div className="page-heading">
        <span className="eyebrow">NOVA PUBLICAÇÃO</span>
        <h1>Criar oferta</h1>
        <p>Escolha o tipo, selecione as figurinhas do seu inventário e publique tudo de uma vez.</p>
      </div>

      <form className="form-card create-offer-form" onSubmit={handleSubmit}>
        <div className="type-selector">
          <button type="button" className={type === 'VENDA' ? 'type-card selected' : 'type-card'} onClick={() => setType('VENDA')}>
            <strong>Venda</strong><span>Receba dinheiro pelas figurinhas ofertadas.</span>
          </button>
          <button type="button" className={type === 'TROCA' ? 'type-card selected' : 'type-card'} onClick={() => setType('TROCA')}>
            <strong>Troca</strong><span>Negocie figurinhas por outras figurinhas.</span>
          </button>
        </div>

        <label>Descrição
          <textarea name="descricao" value={form.descricao} onChange={updateField} maxLength="140" rows="4" placeholder="Descreva sua proposta" />
        </label>
        <label>Prazo limite
          <input name="prazoLimite" type="datetime-local" value={form.prazoLimite} onChange={updateField} required />
        </label>
        {type === 'VENDA' && <label>Valor da proposta
          <input name="valorDaProposta" type="number" min="0.01" step="0.01" value={form.valorDaProposta} onChange={updateField} placeholder="0,00" required />
        </label>}

        <section className="offer-items-section">
          <div className="section-heading">
            <div><span className="eyebrow">SEU INVENTÁRIO</span><h2>Escolha os itens ofertados</h2></div>
            <span className="selection-count">{form.itensOfertados.length} selecionado(s)</span>
          </div>

          {loadingPossessions && <p>Carregando inventário...</p>}
          {!loadingPossessions && possessions.length === 0 && <div className="notice">Você ainda não possui figurinhas no inventário.</div>}
          <div className="inventory-grid">
            {possessions.map((possession) => (
              <button key={possession.idPosse} type="button"
                className={selectedIds.has(possession.idPosse) ? 'inventory-card selected' : 'inventory-card'}
                onClick={() => togglePossession(possession)}>
                <strong>{possession.nome}</strong>
                <span>{possession.codigo} · {possession.tipo}</span>
                <span>Disponível: {possession.quantidade}</span>
                <b>{selectedIds.has(possession.idPosse) ? 'Selecionada' : 'Selecionar'}</b>
              </button>
            ))}
          </div>
        </section>

        {form.itensOfertados.length > 0 && <section className="selected-items-section">
          <h2>Configurar itens ofertados</h2>
          {form.itensOfertados.map((item) => (
            <div className="selected-item-card" key={item.idPosse}>
              <div className="item-summary"><strong>{item.nome}</strong><span>{item.codigo} · {item.tipo} · disponível: {item.quantidadeDisponivel}</span></div>
              <label>Quantidade<input type="number" min="1" max={item.quantidadeDisponivel} value={item.quantidadeOfertada}
                onChange={(e) => updateOfferedItem(item.idPosse, 'quantidadeOfertada', e.target.value)} required /></label>
              <label>Condição<select value={item.condicao} onChange={(e) => updateOfferedItem(item.idPosse, 'condicao', e.target.value)}>
                {CONDITIONS.map((condition) => <option key={condition} value={condition}>{condition.replace('_', ' ')}</option>)}
              </select></label>
              <label className="photo-field">URL da foto (opcional)<input value={item.foto} onChange={(e) => updateOfferedItem(item.idPosse, 'foto', e.target.value)} /></label>
              <button type="button" className="danger-button" onClick={() => togglePossession(item)}>Remover</button>
            </div>
          ))}
        </section>}

        {type === 'TROCA' && <section className="requested-items-section">
          <div className="section-heading">
            <div><span className="eyebrow">O QUE VOCÊ PROCURA</span><h2>Itens solicitados</h2></div>
            <button type="button" className="secondary-button" onClick={addRequestedItem}>Adicionar figurinha</button>
          </div>
          {form.itensSolicitados.length === 0 && <div className="notice">Adicione as figurinhas que deseja receber na troca.</div>}
          {form.itensSolicitados.map((item) => <div className="requested-item-card" key={item.key}>
            <label>Código<input value={item.codigoFigurinha} onChange={(e) => updateRequestedItem(item.key, 'codigoFigurinha', e.target.value)} placeholder="FRA07" required /></label>
            <label>Tipo<select value={item.tipoFigurinha} onChange={(e) => updateRequestedItem(item.key, 'tipoFigurinha', e.target.value)}>
              {STICKER_TYPES.map((stickerType) => <option key={stickerType} value={stickerType}>{stickerType.replaceAll('_', ' ')}</option>)}
            </select></label>
            <label>Quantidade<input type="number" min="1" value={item.quantidade} onChange={(e) => updateRequestedItem(item.key, 'quantidade', e.target.value)} required /></label>
            <button type="button" className="danger-button" onClick={() => removeRequestedItem(item.key)}>Remover</button>
          </div>)}
        </section>}

        {error && <div className="alert error">{error}</div>}
        <div className="form-actions">
          <button type="button" className="secondary-button" onClick={() => navigate('/ofertas')}>Cancelar</button>
          <button className="primary-button" disabled={loading || loadingPossessions}>{loading ? 'Criando...' : 'Criar oferta'}</button>
        </div>
      </form>
    </section>
  );
}
