import { useEffect, useMemo, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { answerTradeProposal, getOffer, getProfile, getTradeProposal, getUserReviews } from '../services/api';
import './OfferDetailPage.css';
import './ProposalDetailPage.css';

const RARITIES = { COMUM: 'Comum', LEGEND_COMUM: 'Legend comum', LEGEND_BRONZE: 'Legend bronze', LEGEND_PRATA: 'Legend prata', LEGEND_OURO: 'Legend ouro' };
const CONDITIONS = { EXCELENTE: 'Excelente', RAZOAVEL: 'Razoável', DESGASTADA: 'Desgastada' };
const STATUS = { PENDENTE: 'Aguardando resposta', ACEITA: 'Aceita', RECUSADA: 'Recusada', CANCELADA: 'Cancelada' };

function StickerImage({ item, large = false }) {
  const [failed, setFailed] = useState(false);
  const valid = /^https?:\/\//i.test(item?.foto ?? '');
  if (valid && !failed) return <img className={large ? 'offer-detail-main-image' : ''} src={item.foto} alt={`Figurinha ${item.nome || item.codigo}`} onError={() => setFailed(true)} referrerPolicy="no-referrer"/>;
  return <div className={`offer-detail-placeholder${large ? ' is-large' : ''}`}><svg width="58" height="68" viewBox="0 0 58 68" fill="none" aria-hidden="true"><rect x="10" y="5" width="38" height="54" rx="5" stroke="currentColor" strokeWidth="2" transform="rotate(9 29 32)"/><rect x="6" y="9" width="38" height="54" rx="5" fill="#e5f0f4" stroke="currentColor" strokeWidth="2"/><circle cx="25" cy="28" r="7" stroke="currentColor" strokeWidth="2"/><path d="M14 49c0-13 22-13 22 0M17 55h16" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/></svg><span>Foto não disponível</span></div>;
}

export default function ProposalDetailPage() {
  const { idProposta } = useParams(); const navigate = useNavigate();
  const [proposal, setProposal] = useState(null); const [offer, setOffer] = useState(null); const [profile, setProfile] = useState(null);
  const [reviews, setReviews] = useState([]); const [activeItem, setActiveItem] = useState(0);
  const [loading, setLoading] = useState(true); const [answering, setAnswering] = useState(false); const [error, setError] = useState('');

  useEffect(() => {
    let active = true;
    Promise.all([getTradeProposal(idProposta), getProfile()]).then(async ([data, user]) => {
      const [offerData, reviewData] = await Promise.all([getOffer(data.idOferta), getUserReviews(data.cpfSolicitante).catch(() => [])]);
      if (active) { setProposal(data); setProfile(user); setOffer(offerData); setReviews(Array.isArray(reviewData) ? reviewData : []); }
    }).catch((err) => { if (active) setError(err.message); }).finally(() => { if (active) setLoading(false); });
    return () => { active = false; };
  }, [idProposta]);

  const items = proposal?.itens ?? [];
  const selectedItem = items[activeItem] ?? items[0];
  const ratingDistribution = useMemo(() => [5, 4, 3, 2, 1].map((score) => ({ score, count: reviews.filter((review) => Math.round(Number(review.nota)) === score).length })), [reviews]);
  const canAnswer = profile?.cpf === proposal?.cpfProponente && proposal?.status === 'PENDENTE';
  const initials = proposal?.nomeSolicitante?.trim().split(/\s+/).slice(0, 2).map((word) => word[0]).join('') || '?';

  async function answer(action) {
    setAnswering(true); setError('');
    try {
      const updated = await answerTradeProposal(proposal.idProposta, action);
      setProposal(updated);
      if (action === 'aceitar') navigate('/negociacoes', { state: { message: 'Proposta aceita e troca confirmada.' } });
    } catch (err) { setError(err.message); }
    finally { setAnswering(false); }
  }

  if (loading) return <div className="empty-state">Carregando proposta...</div>;
  if (!proposal || !offer) return <section><Link className="back-link" to="/negociacoes">← Voltar</Link><div className="alert error">{error}</div></section>;

  return <section className="offer-detail-page proposal-detail-page">
    <Link className="back-link" to="/negociacoes">← Voltar para negociações</Link>
    <div className="offer-detail-layout">
      <div className="offer-detail-gallery"><div className="offer-detail-featured"><StickerImage key={selectedItem?.idItem || 'empty'} item={selectedItem} large/><span className="offer-detail-type is-trade">Proposta de troca</span></div>{items.length > 1 && <div className="offer-detail-thumbs">{items.map((item, index) => <button key={item.idItem} className={index === activeItem ? 'is-active' : ''} type="button" onClick={() => setActiveItem(index)}><StickerImage item={item}/></button>)}</div>}</div>
      <article className="offer-detail-summary"><div className="proposal-title-line"><span className="eyebrow">PROPOSTA #{proposal.idProposta}</span><span className={`proposal-status is-${proposal.status.toLowerCase()}`}>{STATUS[proposal.status] || proposal.status}</span></div><h1>{items.length === 1 ? items[0].nome : `${items.length} figurinhas oferecidas`}</h1><p className="offer-detail-description">Proposta enviada por {proposal.nomeSolicitante} para a oferta #{proposal.idOferta}.</p>
        <div className="trade-value-comparison"><div><small>Valor da oferta original</small><strong>{formatMoney(proposal.valorMercadoOferta)}</strong></div><span aria-hidden="true">⇄</span><div><small>Valor desta proposta</small><strong>{formatMoney(proposal.valorMercadoProposta)}</strong></div></div>
        <p className="market-value-note">Valores de mercado são uma referência. Avalie também raridade, condição e interesse pelas figurinhas.</p>
        <dl className="offer-detail-meta"><div><dt>Enviada em</dt><dd>{formatDate(proposal.dataCriacao)}</dd></div><div><dt>Situação</dt><dd>{STATUS[proposal.status] || proposal.status}</dd></div></dl>
        {canAnswer && <div className="proposal-detail-actions"><button type="button" className="danger-button" disabled={answering} onClick={() => answer('recusar')}>Recusar</button><button type="button" className="primary-button" disabled={answering} onClick={() => answer('aceitar')}>{answering ? 'Processando...' : 'Aceitar proposta'}</button></div>}
        {error && <div className="alert error">{error}</div>}
      </article>
    </div>

    <section className="offer-detail-section"><div className="offer-detail-section-heading"><span className="eyebrow">COMPARAÇÃO</span><h2>O que cada pessoa oferece</h2><p>Revise todos os itens e seus valores antes de confirmar.</p></div><div className="exchange-columns"><ItemSide title={`${proposal.nomeSolicitante} oferece`} total={proposal.valorMercadoProposta} items={proposal.itens}/><ItemSide title={`${proposal.nomeProponente} oferece`} total={proposal.valorMercadoOferta} items={offer.itensOfertados}/></div></section>

    <section className="offer-detail-section seller-section"><div className="seller-profile-card"><span className="seller-profile-avatar" aria-hidden="true">{initials}</span><div className="seller-profile-copy"><span className="eyebrow">INTERESSADO NA TROCA</span><h2>{proposal.nomeSolicitante}</h2><p>{proposal.cidadeSolicitante || 'Cidade não informada'}</p></div><div className="seller-profile-score"><strong><span aria-hidden="true">★</span> {Number(proposal.avaliacaoSolicitante) > 0 ? Number(proposal.avaliacaoSolicitante).toLocaleString('pt-BR', { minimumFractionDigits: 1, maximumFractionDigits: 1 }) : '—'}</strong><small>{reviews.length === 1 ? '1 avaliação' : `${reviews.length} avaliações`}</small></div></div>
      <div className="reviews-layout"><aside className="reviews-overview"><h3>Reputação</h3>{ratingDistribution.map(({ score, count }) => <div className="rating-line" key={score}><span>{score} ★</span><i><b style={{ width: reviews.length ? `${count / reviews.length * 100}%` : '0%' }}/></i><small>{count}</small></div>)}</aside><div className="reviews-list"><div className="reviews-heading"><h3>Comentários sobre {proposal.nomeSolicitante.split(' ')[0]}</h3><span>{reviews.length}</span></div>{reviews.length === 0 ? <div className="reviews-empty">Este usuário ainda não recebeu comentários.</div> : reviews.map((review) => <article className="review-card" key={`${review.idConcretizacao}-${review.cpfAvaliador}`}><div className="review-author"><span>{review.nomeAvaliador?.slice(0, 1) || '?'}</span><div><strong>{review.nomeAvaliador || 'Colecionador'}</strong><small>{formatReviewDate(review.data)}</small></div><b aria-label={`Nota ${review.nota} de 5`}>{'★'.repeat(Math.max(0, Math.min(5, Math.round(Number(review.nota)))))}</b></div><p>{review.comentario || 'O avaliador não deixou um comentário.'}</p></article>)}</div></div>
    </section>
  </section>;
}

function ItemSide({ title, total, items = [] }) {
  return <div className="exchange-side"><div className="exchange-side-heading"><h3>{title}</h3><strong>{formatMoney(total)}</strong></div><div>{items.map((item) => <article className="exchange-item" key={item.idItem}><div className="exchange-item-photo"><StickerImage item={item}/></div><div><span className="offer-detail-code">{item.codigo}</span><h4>{item.nome}</h4><p>{RARITIES[item.raridade] || item.raridade} · {item.quantidade} un.</p>{item.condicao && <p>{CONDITIONS[item.condicao] || item.condicao}</p>}<strong>{formatMoney(item.valorTotal)}</strong></div></article>)}</div></div>;
}

function formatMoney(value) { return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(Number(value ?? 0)); }
function formatDate(value) { return value ? new Intl.DateTimeFormat('pt-BR', { dateStyle: 'long', timeStyle: 'short' }).format(new Date(value)) : '—'; }
function formatReviewDate(value) { return value ? new Intl.DateTimeFormat('pt-BR', { dateStyle: 'medium' }).format(new Date(value)) : 'Data não informada'; }
