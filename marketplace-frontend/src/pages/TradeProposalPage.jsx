import { useEffect, useMemo, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { createTradeProposal, getOffer, getPossessions, getProfile } from '../services/api';
import './TradeProposalPage.css';

const CONDITIONS = ['EXCELENTE', 'RAZOAVEL', 'DESGASTADA'];
const RARITIES = { COMUM: 'Comum', LEGEND_COMUM: 'Legend comum', LEGEND_BRONZE: 'Legend bronze', LEGEND_PRATA: 'Legend prata', LEGEND_OURO: 'Legend ouro' };

function normalizeList(value) {
  if (Array.isArray(value)) return value;
  if (Array.isArray(value?.content)) return value.content;
  if (Array.isArray(value?.items)) return value.items;
  return [];
}

function possessionView(possession) {
  const sticker = possession.figurinha ?? possession.sticker ?? {};
  return {
    idPosse: possession.idPosse ?? possession.id_posse ?? possession.id,
    quantidade: Number(possession.quantidade ?? possession.quantity ?? 0),
    nome: possession.nomeFigurinha ?? possession.nome ?? sticker.nome ?? 'Figurinha',
    codigo: possession.codigoFigurinha ?? possession.codigo ?? sticker.codigo ?? '—',
    tipo: possession.tipoFigurinha ?? possession.tipo ?? sticker.tipo ?? '—',
    valorDeMercado: Number(possession.valorDeMercado ?? sticker.valorDeMercado ?? 0)
  };
}

export default function TradeProposalPage() {
  const { idOferta } = useParams();
  const navigate = useNavigate();
  const [offer, setOffer] = useState(null);
  const [possessions, setPossessions] = useState([]);
  const [selected, setSelected] = useState([]);
  const [loading, setLoading] = useState(true);
  const [sending, setSending] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    let active = true;
    async function load() {
      try {
        const [currentOffer, profile] = await Promise.all([getOffer(idOferta), getProfile()]);
        if (currentOffer.tipo !== 'TROCA') throw new Error('Esta oferta não é uma troca.');
        if (currentOffer.cpfProponente === profile.cpf) throw new Error('Você não pode propor uma troca para a própria oferta.');
        const inventory = await getPossessions(profile.cpf);
        if (active) { setOffer(currentOffer); setPossessions(normalizeList(inventory).map(possessionView)); }
      } catch (err) { if (active) setError(err.message); }
      finally { if (active) setLoading(false); }
    }
    load();
    return () => { active = false; };
  }, [idOferta]);

  const selectedIds = useMemo(() => new Set(selected.map((item) => item.idPosse)), [selected]);
  const proposalValue = useMemo(() => selected.reduce((total, item) => total + item.valorDeMercado * item.quantidadeOfertada, 0), [selected]);

  function toggle(possession) {
    setError('');
    setSelected((current) => current.some((item) => item.idPosse === possession.idPosse)
      ? current.filter((item) => item.idPosse !== possession.idPosse)
      : [...current, { ...possession, quantidadeOfertada: 1, condicao: 'EXCELENTE', foto: '' }]);
  }

  function updateItem(idPosse, field, value) {
    setSelected((current) => current.map((item) => item.idPosse === idPosse
      ? { ...item, [field]: field === 'quantidadeOfertada' ? Number(value) : value }
      : item));
  }

  async function submit(event) {
    event.preventDefault(); setError('');
    if (selected.length === 0) { setError('Selecione pelo menos uma figurinha do seu inventário.'); return; }
    const invalid = selected.find((item) => item.quantidadeOfertada < 1 || item.quantidadeOfertada > item.quantidade);
    if (invalid) { setError(`Quantidade inválida para ${invalid.nome}.`); return; }
    setSending(true);
    try {
      await createTradeProposal(idOferta, selected.map((item) => ({
        idPosse: item.idPosse, quantidadeOfertada: item.quantidadeOfertada,
        condicao: item.condicao, foto: item.foto.trim() || null
      })));
      navigate('/negociacoes', { state: { message: 'Proposta enviada. Agora o proponente poderá aceitar ou recusar.' } });
    } catch (err) { setError(err.message); setSending(false); }
  }

  if (loading) return <div className="empty-state">Carregando oferta e inventário...</div>;
  if (!offer) return <section><Link className="back-link" to={`/ofertas/${idOferta}`}>← Voltar</Link><div className="alert error">{error}</div></section>;

  return <section className="trade-proposal-page">
    <Link className="back-link" to={`/ofertas/${idOferta}`}>← Voltar para a oferta</Link>
    <div className="page-heading"><span className="eyebrow">PROPOSTA DE TROCA</span><h1>Escolha o que você vai oferecer</h1><p>O proponente analisará seus itens antes de confirmar a troca.</p></div>

    <aside className="trade-target-card"><div><span className="eyebrow">OFERTA #{offer.idOferta}</span><h2>{offer.descricao || 'Troca de figurinhas'}</h2><p>Proponente: <strong>{offer.nomeProponente}</strong></p><div className="trade-market-value"><small>Valor de mercado da oferta</small><strong>{formatMoney(offer.valorDeMercado)}</strong></div></div><div><strong>O que ele procura</strong><div className="wanted-chips">{(offer.itensSolicitados ?? []).map((item) => <span key={`${item.codigo}-${item.raridade}`}>{item.codigo} · {RARITIES[item.raridade] || item.raridade} · {item.quantidade} un.</span>)}{(offer.itensSolicitados ?? []).length === 0 && <span>O proponente não detalhou itens específicos.</span>}</div></div></aside>

    <form className="form-card create-offer-form" onSubmit={submit}>
      <section className="offer-items-section"><div className="section-heading"><div><span className="eyebrow">SEU INVENTÁRIO</span><h2>Selecione as figurinhas da proposta</h2></div><span className="selection-count">{selected.length} selecionada(s)</span></div>
        {possessions.length === 0 && <div className="notice">Você ainda não possui figurinhas disponíveis no inventário.</div>}
        <div className="inventory-grid">{possessions.map((possession) => <button key={possession.idPosse} type="button" className={selectedIds.has(possession.idPosse) ? 'inventory-card selected' : 'inventory-card'} onClick={() => toggle(possession)}><strong>{possession.nome}</strong><span>{possession.codigo} · {RARITIES[possession.tipo] || possession.tipo}</span><span>Disponível: {possession.quantidade}</span><b>{selectedIds.has(possession.idPosse) ? 'Selecionada' : 'Selecionar'}</b></button>)}</div>
      </section>

      {selected.length > 0 && <section className="selected-items-section"><div className="proposal-value-heading"><h2>Configure os itens oferecidos</h2><div><small>Valor da sua proposta</small><strong>{formatMoney(proposalValue)}</strong></div></div>{selected.map((item) => <div className="selected-item-card" key={item.idPosse}><div className="item-summary"><strong>{item.nome}</strong><span>{item.codigo} · disponível: {item.quantidade}</span><span>Mercado: {formatMoney(item.valorDeMercado)} por unidade</span></div><label>Quantidade<input type="number" min="1" max={item.quantidade} value={item.quantidadeOfertada} onChange={(e) => updateItem(item.idPosse, 'quantidadeOfertada', e.target.value)} required/></label><label>Condição<select value={item.condicao} onChange={(e) => updateItem(item.idPosse, 'condicao', e.target.value)}>{CONDITIONS.map((condition) => <option key={condition}>{condition}</option>)}</select></label><label className="photo-field">URL da foto (opcional)<input value={item.foto} onChange={(e) => updateItem(item.idPosse, 'foto', e.target.value)}/></label><button type="button" className="danger-button" onClick={() => toggle(item)}>Remover</button></div>)}</section>}

      {error && <div className="alert error">{error}</div>}
      <div className="form-actions"><button type="button" className="secondary-button" onClick={() => navigate(`/ofertas/${idOferta}`)}>Cancelar</button><button className="primary-button" disabled={sending || possessions.length === 0}>{sending ? 'Enviando...' : 'Enviar proposta'}</button></div>
    </form>
  </section>;
}

function formatMoney(value) { return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(Number(value ?? 0)); }
