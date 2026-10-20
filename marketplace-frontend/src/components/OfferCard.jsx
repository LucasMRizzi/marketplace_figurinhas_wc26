import { useState } from 'react';
import { Link } from 'react-router-dom';
import './OfferCard.css';

const RARITIES = { COMUM: 'Comum', LEGEND_COMUM: 'Legend comum', LEGEND_BRONZE: 'Legend bronze', LEGEND_PRATA: 'Legend prata', LEGEND_OURO: 'Legend ouro' };
const money = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

function OfferPhoto({ photo, name }) {
  const [failed, setFailed] = useState(false);
  return photo && !failed ? (
    <img src={photo} alt={`Figurinha ${name}`} loading="lazy" onError={() => setFailed(true)} referrerPolicy="no-referrer" />
  ) : (
    <div className="market-offer-placeholder">
      <svg width="58" height="68" viewBox="0 0 58 68" fill="none" aria-hidden="true"><rect x="10" y="5" width="38" height="54" rx="5" stroke="currentColor" strokeWidth="2" transform="rotate(9 29 32)" /><rect x="6" y="9" width="38" height="54" rx="5" fill="#e5f0f4" stroke="currentColor" strokeWidth="2" /><circle cx="25" cy="28" r="7" stroke="currentColor" strokeWidth="2" /><path d="M14 49c0-13 22-13 22 0M17 55h16" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /></svg>
      <span>Foto não disponível</span>
    </div>
  );
}

export default function OfferCard({ offer }) {
  const items = offer.itensOfertados ?? [];
  const featured = items.find((item) => /^https?:\/\//i.test(item.foto ?? ''));
  const seller = offer.nomeProponente || 'Colecionador';
  const initials = seller.trim().split(/\s+/).slice(0, 2).map((word) => word[0]).join('');
  const rating = Number(offer.avaliacaoProponente);
  const sale = offer.tipo === 'VENDA';
  const deadline = offer.prazoLimite ? new Date(offer.prazoLimite) : null;

  return (
    <Link className="market-offer-card" to={`/ofertas/${offer.idOferta}`}>
      <div className="market-offer-photo">
        <OfferPhoto key={featured?.foto || 'placeholder'} photo={featured?.foto} name={featured?.nome || featured?.codigo} />
        <span className={`market-offer-type ${sale ? 'is-sale' : 'is-trade'}`}>{sale ? 'Venda' : 'Troca'}</span>
        {deadline && !Number.isNaN(deadline.getTime()) && <span className="market-offer-deadline">Até {deadline.toLocaleDateString('pt-BR')}</span>}
      </div>
      <div className="market-offer-body">
        <h3>{items.length === 1 ? items[0].nome || items[0].codigo : `Oferta #${offer.idOferta}`}</h3>
        <p className="market-offer-description">{offer.descricao || 'Uma nova oportunidade para completar sua coleção.'}</p>
        <div className="market-offer-items">
          {items.map((item) => <div className="market-offer-item" key={item.idItem}>
            <span className="market-offer-code">{item.codigo}</span>
            <div><strong>{item.nome || 'Figurinha'}</strong><small>{RARITIES[item.raridade] || item.raridade} · {item.quantidade} un.</small></div>
          </div>)}
          {items.length === 0 && <span className="market-offer-no-items">Figurinhas não informadas</span>}
        </div>
        <div className="market-offer-seller">
          <span className="market-offer-avatar" aria-hidden="true">{initials}</span>
          <div className="market-offer-seller-name"><strong>{seller}</strong><small>{offer.cidadeProponente || 'Cidade não informada'}</small></div>
          <span className="market-offer-rating" aria-label={rating > 0 ? `Avaliação: ${rating.toLocaleString('pt-BR')} de 5` : 'Sem avaliações'}>{rating > 0 ? <><span aria-hidden="true">★</span> {rating.toLocaleString('pt-BR', { minimumFractionDigits: 1, maximumFractionDigits: 1 })}<small>/5</small></> : <small>Sem avaliações</small>}</span>
        </div>
        <div className="market-offer-footer"><div><small>{sale ? 'Valor da oferta' : 'Tipo de negociação'}</small><strong>{sale ? offer.valorDaProposta == null ? 'Valor não informado' : money.format(offer.valorDaProposta) : 'Troca de figurinhas'}</strong></div><span className="market-offer-action">Ver oferta <span aria-hidden="true">→</span></span></div>
      </div>
    </Link>
  );
}
