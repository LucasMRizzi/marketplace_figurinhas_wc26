import { Link, useParams } from 'react-router-dom';
import { useEffect, useMemo, useState } from 'react';
import { acceptOffer, getOffer, getProfile, getUserReviews } from '../services/api';
import './OfferDetailPage.css';

const RARITIES = { COMUM: 'Comum', LEGEND_COMUM: 'Legend comum', LEGEND_BRONZE: 'Legend bronze', LEGEND_PRATA: 'Legend prata', LEGEND_OURO: 'Legend ouro' };
const CONDITIONS = { EXCELENTE: 'Excelente', RAZOAVEL: 'Razoável', DESGASTADA: 'Desgastada' };

function StickerImage({ item, large = false }) {
  const validUrl = /^https?:\/\//i.test(item?.foto ?? '');
  const [failed, setFailed] = useState(false);
  if (validUrl && !failed) return <img className={large ? 'offer-detail-main-image' : ''} src={item.foto} alt={`Figurinha ${item.nome || item.codigo}`} onError={() => setFailed(true)} referrerPolicy="no-referrer" />;
  return <div className={`offer-detail-placeholder${large ? ' is-large' : ''}`}><svg width="58" height="68" viewBox="0 0 58 68" fill="none" aria-hidden="true"><rect x="10" y="5" width="38" height="54" rx="5" stroke="currentColor" strokeWidth="2" transform="rotate(9 29 32)"/><rect x="6" y="9" width="38" height="54" rx="5" fill="#e5f0f4" stroke="currentColor" strokeWidth="2"/><circle cx="25" cy="28" r="7" stroke="currentColor" strokeWidth="2"/><path d="M14 49c0-13 22-13 22 0M17 55h16" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/></svg><span>Foto não disponível</span></div>;
}

export default function OfferDetailPage() {
  const { idOferta } = useParams();
  const [offer, setOffer] = useState(null); const [profile, setProfile] = useState(null);
  const [reviews, setReviews] = useState([]); const [activeItem, setActiveItem] = useState(0);
  const [error, setError] = useState(''); const [message, setMessage] = useState(''); const [loading, setLoading] = useState(true);

  useEffect(() => {
    let active = true;
    Promise.all([getOffer(idOferta), getProfile()]).then(async ([data, user]) => {
      if (!active) return;
      setOffer(data); setProfile(user);
      try { const received = await getUserReviews(data.cpfProponente); if (active) setReviews(Array.isArray(received) ? received : []); } catch { if (active) setReviews([]); }
    }).catch((err) => { if (active) setError(err.message); }).finally(() => { if (active) setLoading(false); });
    return () => { active = false; };
  }, [idOferta]);

  const items = offer?.itensOfertados ?? [];
  const selectedItem = items[activeItem] ?? items[0];
  const average = Number(offer?.avaliacaoProponente ?? 0);
  const sellerName = offer?.nomeProponente || 'Colecionador';
  const initials = sellerName.trim().split(/\s+/).slice(0, 2).map((word) => word[0]).join('');
  const ratingDistribution = useMemo(() => [5, 4, 3, 2, 1].map((score) => ({ score, count: reviews.filter((review) => Math.round(Number(review.nota)) === score).length })), [reviews]);

  async function handleAccept() { try { setError(''); await acceptOffer(offer.idOferta, profile.cpf); setMessage('Oferta aceita. A negociação foi enviada para processamento.'); setOffer((current) => ({ ...current, status: 'EM_PROCESSAMENTO' })); } catch (err) { setError(err.message); } }
  if (loading) return <div className="empty-state">Carregando oferta...</div>;
  if (error && !offer) return <div className="alert error">{error}</div>;
  const ownOffer = profile?.cpf === offer.cpfProponente;
  const sale = offer.tipo === 'VENDA';

  return <section className="offer-detail-page">
    <Link className="back-link" to="/ofertas">← Voltar para ofertas</Link>
    <div className="offer-detail-layout">
      <div className="offer-detail-gallery">
        <div className="offer-detail-featured"><StickerImage key={selectedItem?.idItem ?? 'empty'} item={selectedItem} large/><span className={`offer-detail-type ${sale ? 'is-sale' : 'is-trade'}`}>{sale ? 'Venda' : 'Troca'}</span></div>
        {items.length > 1 && <div className="offer-detail-thumbs">{items.map((item, index) => <button key={item.idItem} className={index === activeItem ? 'is-active' : ''} type="button" onClick={() => setActiveItem(index)} aria-label={`Ver ${item.nome || item.codigo}`}><StickerImage item={item}/></button>)}</div>}
      </div>
      <article className="offer-detail-summary">
        <span className="eyebrow">OFERTA #{offer.idOferta}</span>
        <h1>{items.length === 1 ? items[0].nome || items[0].codigo : `${items.length} figurinhas nesta oferta`}</h1>
        <p className="offer-detail-description">{offer.descricao || 'Uma nova oportunidade para completar sua coleção.'}</p>
        <div className="offer-detail-price"><small>{sale ? 'Valor da oferta' : 'Valor de mercado das figurinhas'}</small><strong>{sale ? formatMoney(offer.valorDaProposta) : formatMoney(offer.valorDeMercado)}</strong>{sale && offer.valorDeMercado != null && <span>Valor de mercado: {formatMoney(offer.valorDeMercado)}</span>}{!sale && <span>Use este valor como referência ao montar sua proposta.</span>}</div>
        <dl className="offer-detail-meta"><div><dt>Prazo limite</dt><dd>{formatDate(offer.prazoLimite)}</dd></div><div><dt>Situação</dt><dd>{statusLabel(offer.status)}</dd></div></dl>
        <div className="offer-detail-action"><div><strong>Negociação segura</strong><span>{sale ? 'O aceite registra a negociação no sistema.' : 'Escolha suas figurinhas e aguarde a confirmação do proponente.'}</span></div>{!ownOffer && offer.status === 'PENDENTE' && (sale ? <button className="primary-button" onClick={handleAccept}>Comprar</button> : <Link className="primary-button" to={`/ofertas/${offer.idOferta}/propor-troca`}>Propor troca</Link>)}{ownOffer && <Link className="secondary-button" to="/ofertas/minhas">Gerenciar oferta</Link>}</div>
        {message && <div className="alert success">{message}</div>}{error && <div className="alert error">{error}</div>}
      </article>
    </div>

    <section className="offer-detail-section"><div className="offer-detail-section-heading"><span className="eyebrow">CONTEÚDO</span><h2>Figurinhas da oferta</h2><p>Confira os códigos, raridades, quantidades, condições e valores.</p></div><div className="offer-detail-items">{items.map((item) => <article key={item.idItem} className="offer-detail-item"><div className="offer-detail-item-photo"><StickerImage item={item}/></div><div><span className="offer-detail-code">{item.codigo}</span><h3>{item.nome || 'Figurinha'}</h3><dl><div><dt>Raridade</dt><dd>{RARITIES[item.raridade] || item.raridade || '—'}</dd></div><div><dt>Condição</dt><dd>{CONDITIONS[item.condicao] || item.condicao || '—'}</dd></div><div><dt>Quantidade</dt><dd>{item.quantidade} un.</dd></div><div><dt>Valor total</dt><dd>{formatMoney(item.valorTotal)}</dd></div></dl></div></article>)}</div></section>

    <section className="offer-detail-section seller-section"><div className="seller-profile-card"><span className="seller-profile-avatar" aria-hidden="true">{initials}</span><div className="seller-profile-copy"><span className="eyebrow">PROPONENTE</span><h2>{sellerName}</h2><p>{offer.cidadeProponente || 'Cidade não informada'}</p></div><div className="seller-profile-score"><strong><span aria-hidden="true">★</span> {average > 0 ? average.toLocaleString('pt-BR', { minimumFractionDigits: 1, maximumFractionDigits: 1 }) : '—'}</strong><small>{reviews.length === 1 ? '1 avaliação' : `${reviews.length} avaliações`}</small></div></div>
      <div className="reviews-layout"><aside className="reviews-overview"><h3>Reputação</h3>{ratingDistribution.map(({ score, count }) => <div className="rating-line" key={score}><span>{score} ★</span><i><b style={{ width: reviews.length ? `${(count / reviews.length) * 100}%` : '0%' }}/></i><small>{count}</small></div>)}</aside><div className="reviews-list"><div className="reviews-heading"><h3>Comentários sobre {sellerName.split(' ')[0]}</h3><span>{reviews.length}</span></div>{reviews.length === 0 ? <div className="reviews-empty">Este usuário ainda não recebeu comentários.</div> : reviews.map((review) => <article className="review-card" key={`${review.idConcretizacao}-${review.cpfAvaliador}`}><div className="review-author"><span>{review.nomeAvaliador?.slice(0, 1) || '?'}</span><div><strong>{review.nomeAvaliador || 'Colecionador'}</strong><small>{formatReviewDate(review.data)}</small></div><b aria-label={`Nota ${review.nota} de 5`}>{'★'.repeat(Math.max(0, Math.min(5, Math.round(Number(review.nota)))))}</b></div><p>{review.comentario || 'O avaliador não deixou um comentário.'}</p></article>)}</div></div>
    </section>
  </section>;
}

function formatMoney(value) { return value == null ? 'Valor não informado' : new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value); }
function formatDate(value) { return value ? new Intl.DateTimeFormat('pt-BR', { dateStyle: 'long', timeStyle: 'short' }).format(new Date(value)) : '—'; }
function formatReviewDate(value) { return value ? new Intl.DateTimeFormat('pt-BR', { dateStyle: 'medium' }).format(new Date(value)) : 'Data não informada'; }
function statusLabel(status) { return ({ PENDENTE: 'Disponível', EM_PROCESSAMENTO: 'Em processamento', CONCRETIZADA: 'Concretizada', EXPIRADA: 'Expirada' })[status] || status; }
