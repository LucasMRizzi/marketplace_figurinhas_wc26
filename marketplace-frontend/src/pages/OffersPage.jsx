import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { getOffers } from '../services/api';

export default function OffersPage() {
  const [offers, setOffers] = useState([]);
  const [filter, setFilter] = useState('TODAS');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    getOffers()
      .then((data) => setOffers(Array.isArray(data) ? data : data.content ?? []))
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  const filtered = useMemo(() => {
    if (filter === 'TODAS') return offers;
    return offers.filter((offer) => offer.tipo === filter);
  }, [offers, filter]);

  return (
    <section>
      <div className="page-heading split-heading">
        <div>
          <span className="eyebrow">MARKETPLACE</span>
          <h1>Ofertas disponíveis</h1>
          <p>Encontre oportunidades de venda e troca publicadas pela comunidade.</p>
        </div>
        <Link className="primary-button" to="/ofertas/nova">Criar oferta</Link>
      </div>

      <div className="filter-row">
        {['TODAS', 'VENDA', 'TROCA'].map((value) => (
          <button
            key={value}
            className={filter === value ? 'filter active' : 'filter'}
            onClick={() => setFilter(value)}
          >
            {value === 'TODAS' ? 'Todas' : value === 'VENDA' ? 'Vendas' : 'Trocas'}
          </button>
        ))}
      </div>

      {loading && <div className="empty-state">Carregando ofertas...</div>}
      {error && <div className="alert error">{error}</div>}

      {!loading && !error && filtered.length === 0 && (
        <div className="empty-state">Nenhuma oferta encontrada.</div>
      )}

      <div className="offers-grid">
        {filtered.map((offer) => (
          <article className="offer-card" key={offer.idOferta}>
            <div className="offer-card-top">
              <span className={`badge ${offer.tipo?.toLowerCase()}`}>{offer.tipo}</span>
              <span className="status">{offer.status}</span>
            </div>

            <h3>Oferta #{offer.idOferta}</h3>
            <p>{offer.descricao || 'Oferta publicada no marketplace.'}</p>

            <dl>
              <div>
                <dt>Valor de mercado</dt>
                <dd>{formatMoney(offer.valorDeMercado)}</dd>
              </div>
              <div>
                <dt>Prazo</dt>
                <dd>{formatDate(offer.prazoLimite)}</dd>
              </div>
              <div>
                <dt>Proponente</dt>
                <dd>{offer.cpfProponente || offer.usuarioProponente || '—'}</dd>
              </div>
            </dl>
          </article>
        ))}
      </div>
    </section>
  );
}

function formatMoney(value) {
  if (value === null || value === undefined) return '—';
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL'
  }).format(value);
}

function formatDate(value) {
  if (!value) return '—';
  return new Intl.DateTimeFormat('pt-BR', {
    dateStyle: 'short',
    timeStyle: 'short'
  }).format(new Date(value));
}
