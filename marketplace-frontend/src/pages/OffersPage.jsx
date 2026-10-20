import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { searchOffers } from '../services/api';
import OfferCard from '../components/OfferCard';

const INITIAL_FILTERS = {
  tipos: [], tiposFigurinha: [], valorMinimo: '', valorMaximo: '', cidade: '', termo: ''
};

const FILTER_GROUPS = [
  { key: 'tipos', label: 'Tipo de negociação', options: [['VENDA', 'Venda'], ['TROCA', 'Troca']] },
  { key: 'tiposFigurinha', label: 'Raridade da figurinha', options: [['COMUM', 'Comum'], ['LEGEND_COMUM', 'Legend comum'], ['LEGEND_BRONZE', 'Legend bronze'], ['LEGEND_PRATA', 'Legend prata'], ['LEGEND_OURO', 'Legend ouro']] }
];

export default function OffersPage() {
  const [draft, setDraft] = useState(INITIAL_FILTERS);
  const [filters, setFilters] = useState(INITIAL_FILTERS);
  const [offers, setOffers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    setLoading(true);
    setError('');
    searchOffers(filters)
      .then((data) => setOffers(Array.isArray(data) ? data : data.content ?? []))
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, [filters]);

  function toggle(group, value) {
    setDraft((current) => ({
      ...current,
      [group]: current[group].includes(value)
        ? current[group].filter((item) => item !== value)
        : [...current[group], value]
    }));
  }

  function applyFilters(event) {
    event.preventDefault();
    setFilters({ ...draft });
  }

  function clearFilters() {
    setDraft(INITIAL_FILTERS);
    setFilters(INITIAL_FILTERS);
  }

  return (
    <section>
      <div className="page-heading split-heading">
        <div>
          <span className="eyebrow">MARKETPLACE</span>
          <h1>Ofertas disponíveis</h1>
          <p>Use os filtros para encontrar uma negociação que combine com sua coleção.</p>
        </div>
        <Link className="primary-button" to="/ofertas/nova">Criar oferta</Link>
      </div>

      <div className="marketplace-layout">
        <aside className="commerce-filters">
          <div className="filter-header"><div><span className="eyebrow">FILTROS</span><h2>Refine sua busca</h2></div><button type="button" className="clear-button" onClick={clearFilters}>Limpar</button></div>
          <form onSubmit={applyFilters}>
            <label className="filter-search">Buscar
              <input value={draft.termo} onChange={(event) => setDraft((current) => ({ ...current, termo: event.target.value }))} placeholder="Descrição, nome ou CPF" />
            </label>
            <label className="filter-search">Cidade do proponente
              <input value={draft.cidade} onChange={(event) => setDraft((current) => ({ ...current, cidade: event.target.value }))} placeholder="Ex.: Rio Claro" />
            </label>
            <div className="price-fields"><label>Valor mínimo<input type="number" min="0" step="0.01" value={draft.valorMinimo} onChange={(event) => setDraft((current) => ({ ...current, valorMinimo: event.target.value }))} /></label><label>Valor máximo<input type="number" min="0" step="0.01" value={draft.valorMaximo} onChange={(event) => setDraft((current) => ({ ...current, valorMaximo: event.target.value }))} /></label></div>
            {FILTER_GROUPS.map((group) => <fieldset className="filter-group" key={group.key}><legend>{group.label}</legend>{group.options.map(([value, label]) => <label className="check-option" key={value}><input type="checkbox" checked={draft[group.key].includes(value)} onChange={() => toggle(group.key, value)} /><span>{label}</span></label>)}</fieldset>)}
            <button className="primary-button filter-submit">Aplicar filtros</button>
          </form>
        </aside>

        <div className="offers-results">
          <div className="results-header"><strong>{loading ? 'Buscando...' : `${offers.length} oferta(s) encontrada(s)`}</strong><span>Resultados calculados no servidor</span></div>
          {error && <div className="alert error">{error}</div>}
          {!loading && !error && offers.length === 0 && <div className="empty-state">Nenhuma oferta corresponde aos filtros selecionados.</div>}
          <div className="offers-grid">
            {offers.map((offer) => <OfferCard key={offer.idOferta} offer={offer} />)}
          </div>
        </div>
      </div>
    </section>
  );
}

