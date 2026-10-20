import { useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { answerTradeProposal, createReview, getGivenReviews, getMyNegotiations, getProfile, getReceivedTradeProposals, getSentTradeProposals } from '../services/api';
import './NegotiationsPage.css';

const STATUS = { PENDENTE: 'Aguardando resposta', ACEITA: 'Aceita', RECUSADA: 'Recusada', CANCELADA: 'Cancelada' };

export default function NegotiationsPage() {
  const location = useLocation();
  const [received, setReceived] = useState([]); const [sent, setSent] = useState([]); const [history, setHistory] = useState([]);
  const [profile, setProfile] = useState(null); const [reviewed, setReviewed] = useState(new Set());
  const [error, setError] = useState(''); const [loading, setLoading] = useState(true); const [answering, setAnswering] = useState(null);
  const [message, setMessage] = useState(location.state?.message || '');

  useEffect(() => {
    let active = true;
    getProfile().then(async (currentProfile) => {
      if (active) setProfile(currentProfile);
      return Promise.all([getReceivedTradeProposals(), getSentTradeProposals(), getMyNegotiations(), getGivenReviews(currentProfile.cpf)]);
    }).then(([receivedData, sentData, historyData, reviewsData]) => {
      if (!active) return;
      setReceived(Array.isArray(receivedData) ? receivedData : []);
      setSent(Array.isArray(sentData) ? sentData : []);
      setHistory(Array.isArray(historyData) ? historyData : []);
      setReviewed(new Set((Array.isArray(reviewsData) ? reviewsData : []).map((review) => review.idConcretizacao)));
    }).catch((err) => { if (active) setError(err.message); }).finally(() => { if (active) setLoading(false); });
    return () => { active = false; };
  }, []);

  async function answer(idProposta, action) {
    setAnswering(idProposta); setError(''); setMessage('');
    try {
      const updated = await answerTradeProposal(idProposta, action);
      setReceived((current) => action === 'aceitar'
        ? current.filter((proposal) => proposal.idOferta !== updated.idOferta)
        : current.filter((proposal) => proposal.idProposta !== idProposta));
      if (action === 'aceitar') {
        const confirmed = await getMyNegotiations();
        setHistory(Array.isArray(confirmed) ? confirmed : []);
      }
      setMessage(action === 'aceitar' ? 'Proposta aceita e troca confirmada.' : 'Proposta recusada.');
    } catch (err) { setError(err.message); }
    finally { setAnswering(null); }
  }

  async function submitReview(item, nota, comentario) {
    setError(''); setMessage('');
    try {
      await createReview(item.idConcretizacao, { cpfAvaliador: profile.cpf, nota, comentario });
      setReviewed((current) => new Set([...current, item.idConcretizacao]));
      setMessage('Avaliação enviada com sucesso.');
    } catch (err) { setError(err.message); throw err; }
  }

  if (loading) return <div className="empty-state">Carregando negociações...</div>;
  return <section className="negotiations-page">
    <div className="page-heading"><span className="eyebrow">ACOMPANHAMENTO</span><h1>Minhas negociações</h1><p>Analise propostas recebidas e acompanhe as trocas que você enviou.</p></div>
    {message && <div className="alert success">{message}</div>}{error && <div className="alert error">{error}</div>}

    <NegotiationSection title="Propostas recebidas" subtitle="Pessoas interessadas nas suas ofertas de troca." empty="Você ainda não recebeu propostas.">
      {received.map((proposal) => <ProposalCard key={proposal.idProposta} proposal={proposal} received answering={answering} onAnswer={answer}/>) }
    </NegotiationSection>

    <NegotiationSection title="Propostas enviadas" subtitle="Acompanhe a resposta dos proponentes." empty="Você ainda não enviou propostas de troca.">
      {sent.map((proposal) => <ProposalCard key={proposal.idProposta} proposal={proposal}/>) }
    </NegotiationSection>

    <NegotiationSection title="Negociações confirmadas" subtitle="Histórico de ofertas aceitas por você." empty="Nenhuma negociação confirmada.">
      {history.map((item) => <ConfirmedNegotiationCard key={item.idConcretizacao} item={item} profile={profile} reviewed={reviewed.has(item.idConcretizacao)} onReview={submitReview}/>)}
    </NegotiationSection>
  </section>;
}

function NegotiationSection({ title, subtitle, empty, children }) {
  const entries = Array.isArray(children) ? children : [children];
  const visible = entries.filter(Boolean);
  return <section className="negotiation-section"><div className="negotiation-section-heading"><div><h2>{title}</h2><p>{subtitle}</p></div><span>{visible.length}</span></div>{visible.length ? <div className="proposal-grid">{visible}</div> : <div className="empty-state">{empty}</div>}</section>;
}

function ProposalCard({ proposal, received = false, answering, onAnswer }) {
  return <article className="trade-proposal-card"><div className="proposal-card-header"><span className={`proposal-status is-${proposal.status?.toLowerCase()}`}>{STATUS[proposal.status] || proposal.status}</span><small>{formatDate(proposal.dataCriacao)}</small></div><h3>Oferta #{proposal.idOferta}</h3><p>{received ? <><strong>{proposal.nomeSolicitante}</strong>{proposal.cidadeSolicitante ? ` · ${proposal.cidadeSolicitante}` : ''}</> : <>Proposta para <strong>{proposal.nomeProponente}</strong></>}</p><div className="proposal-values"><span>Oferta: <strong>{formatMoney(proposal.valorMercadoOferta)}</strong></span><span>Proposta: <strong>{formatMoney(proposal.valorMercadoProposta)}</strong></span></div><div className="proposal-items">{proposal.itens?.map((item) => <div key={item.idItem}><span>{item.codigo}</span><p><strong>{item.nome}</strong><small>{item.quantidade} un. · {label(item.condicao)} · {formatMoney(item.valorTotal)}</small></p></div>)}</div><div className="proposal-card-footer"><Link className="secondary-button" to={`/negociacoes/propostas/${proposal.idProposta}`}>Ver proposta completa</Link>{received && proposal.status === 'PENDENTE' && <div><button type="button" className="danger-button" disabled={answering === proposal.idProposta} onClick={() => onAnswer(proposal.idProposta, 'recusar')}>Recusar</button><button type="button" className="primary-button" disabled={answering === proposal.idProposta} onClick={() => onAnswer(proposal.idProposta, 'aceitar')}>{answering === proposal.idProposta ? 'Processando...' : 'Aceitar troca'}</button></div>}</div></article>;
}

function ConfirmedNegotiationCard({ item, profile, reviewed, onReview }) {
  const [open, setOpen] = useState(false); const [nota, setNota] = useState(5); const [comentario, setComentario] = useState(''); const [sending, setSending] = useState(false);
  const isOwner = profile?.cpf === item.cpfProponente;
  const otherName = isOwner ? item.nomeAceitante : item.nomeProponente;
  const completed = item.statusPagamento === 'PAGO';
  async function submit(event) { event.preventDefault(); setSending(true); try { await onReview(item, nota, comentario); setOpen(false); } catch { /* mensagem exibida pela página */ } finally { setSending(false); } }
  return <article className="negotiation-card confirmed-negotiation-card"><div className="proposal-card-header"><span className={`status-pill ${completed ? 'is-completed' : ''}`}>{completed ? 'CONFIRMADA' : item.statusPagamento}</span><small>{formatDate(item.dataAceite)}</small></div><h3>Oferta #{item.idOferta}</h3><p>{item.tipoOferta === 'TROCA' ? 'Troca' : 'Venda'} com <strong>{otherName}</strong></p><div className="confirmed-value"><small>Valor de mercado</small><strong>{formatMoney(item.valorDeMercado)}</strong></div><div className="confirmed-actions"><Link className="secondary-button" to={`/ofertas/${item.idOferta}`}>Ver oferta</Link>{reviewed ? <span className="review-done">✓ Avaliação enviada</span> : completed ? <button type="button" className="primary-button" onClick={() => setOpen((current) => !current)}>Avaliar {otherName?.split(' ')[0]}</button> : <span className="review-waiting">Avaliação disponível após a conclusão</span>}</div>{open && !reviewed && <form className="review-form" onSubmit={submit}><strong>Sua avaliação</strong><div className="star-selector" aria-label="Nota">{[1, 2, 3, 4, 5].map((star) => <button key={star} type="button" className={star <= nota ? 'selected' : ''} onClick={() => setNota(star)} aria-label={`${star} estrela${star > 1 ? 's' : ''}`}>★</button>)}</div><label>Comentário<textarea value={comentario} onChange={(event) => setComentario(event.target.value)} maxLength="150" rows="3" placeholder="Conte como foi negociar com esta pessoa" required/></label><div><button type="button" className="secondary-button" onClick={() => setOpen(false)}>Cancelar</button><button className="primary-button" disabled={sending}>{sending ? 'Enviando...' : 'Enviar avaliação'}</button></div></form>}</article>;
}

function formatDate(value) { return value ? new Intl.DateTimeFormat('pt-BR', { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(value)) : '—'; }
function label(value) { return value ? value.replaceAll('_', ' ').toLowerCase().replace(/^./, (letter) => letter.toUpperCase()) : '—'; }
function formatMoney(value) { return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(Number(value ?? 0)); }
