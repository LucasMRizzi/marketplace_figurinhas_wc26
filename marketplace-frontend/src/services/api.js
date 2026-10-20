const JSON_HEADERS = {
  Accept: 'application/json',
  'Content-Type': 'application/json'
};

function extractErrorMessage(body, status) {
  if (typeof body === 'string' && body.trim()) return body;
  if (!body || typeof body !== 'object') return `Erro HTTP ${status}`;

  if (body.message || body.detail || body.error) {
    return body.message || body.detail || body.error;
  }

  const entries = Object.entries(body);
  if (entries.length > 0) {
    return entries
      .map(([field, message]) => `${field}: ${Array.isArray(message) ? message.join(', ') : message}`)
      .join(' | ');
  }

  return `Erro HTTP ${status}`;
}

async function parseResponse(response) {
  const contentType = response.headers.get('content-type') ?? '';
  const isJson = contentType.includes('application/json');
  const body = response.status === 204
    ? null
    : isJson
      ? await response.json()
      : await response.text();

  if (!response.ok) {
    throw new Error(extractErrorMessage(body, response.status));
  }

  return body;
}

export async function login(email, senha) {
  const form = new URLSearchParams();
  form.set('email', email);
  form.set('senha', senha);

  const response = await fetch('/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    credentials: 'include',
    body: form
  });

  if (!response.ok) throw new Error('E-mail ou senha inválidos.');
  return true;
}

export async function registerUser(payload) {
  return parseResponse(await fetch('/api/usuarios', {
    method: 'POST',
    headers: JSON_HEADERS,
    credentials: 'include',
    body: JSON.stringify(payload)
  }));
}

export async function getProfile() {
  const response = await fetch('/api/perfil', {
    credentials: 'include',
    headers: { Accept: 'application/json' },
    redirect: 'manual'
  });

  if (response.status === 401 || response.status === 403 || response.type === 'opaqueredirect') {
    throw new Error('UNAUTHENTICATED');
  }

  return parseResponse(response);
}

export async function updateProfile(payload) {
  return parseResponse(await fetch('/api/perfil', {
    method: 'PUT',
    headers: JSON_HEADERS,
    credentials: 'include',
    body: JSON.stringify(payload)
  }));
}

export async function getOffers(filters = {}) {
  const params = new URLSearchParams();
  if (filters.tipo && filters.tipo !== 'TODAS') params.set('tipo', filters.tipo);
  if (filters.status && filters.status !== 'TODOS') params.set('status', filters.status);

  const query = params.size ? `?${params.toString()}` : '';
  return parseResponse(await fetch(`/api/ofertas${query}`, {
    credentials: 'include',
    headers: { Accept: 'application/json' }
  }));
}

export async function searchOffers(filters) {
  const params = new URLSearchParams();
  for (const tipo of filters.tipos) params.append('tipos', tipo);
  for (const tipoFigurinha of filters.tiposFigurinha) params.append('tiposFigurinha', tipoFigurinha);
  if (filters.valorMinimo) params.set('valorMinimo', filters.valorMinimo);
  if (filters.valorMaximo) params.set('valorMaximo', filters.valorMaximo);
  if (filters.cidade.trim()) params.set('cidade', filters.cidade.trim());
  if (filters.termo.trim()) params.set('termo', filters.termo.trim());

  return parseResponse(await fetch(`/api/ofertas/busca?${params.toString()}`, {
    credentials: 'include', headers: { Accept: 'application/json' }
  }));
}

export async function getOffer(idOferta) {
  return parseResponse(await fetch(`/api/ofertas/detalhes/${idOferta}`, {
    credentials: 'include', headers: { Accept: 'application/json' }
  }));
}

export async function getUserReviews(cpf) {
  return parseResponse(await fetch(`/api/avaliacoes/avaliados/${encodeURIComponent(cpf)}`, {
    credentials: 'include', headers: { Accept: 'application/json' }
  }));
}

export async function getMyOffers(cpf) {
  return parseResponse(await fetch(`/api/ofertas/usuario/${encodeURIComponent(cpf)}`, {
    credentials: 'include', headers: { Accept: 'application/json' }
  }));
}

export async function acceptOffer(idOferta, cpfAceitante) {
  return parseResponse(await fetch(`/api/concretizacoes/ofertas/${idOferta}`, {
    method: 'POST', headers: JSON_HEADERS, credentials: 'include',
    body: JSON.stringify({ cpfAceitante })
  }));
}

export async function createTradeProposal(idOferta, itens) {
  return parseResponse(await fetch(`/api/ofertas/${idOferta}/propostas-troca`, {
    method: 'POST', headers: JSON_HEADERS, credentials: 'include',
    body: JSON.stringify({ itens })
  }));
}

export async function getReceivedTradeProposals() {
  return parseResponse(await fetch('/api/propostas-troca/recebidas', {
    credentials: 'include', headers: { Accept: 'application/json' }
  }));
}

export async function getSentTradeProposals() {
  return parseResponse(await fetch('/api/propostas-troca/enviadas', {
    credentials: 'include', headers: { Accept: 'application/json' }
  }));
}

export async function getTradeProposal(idProposta) {
  return parseResponse(await fetch(`/api/propostas-troca/${idProposta}`, {
    credentials: 'include', headers: { Accept: 'application/json' }
  }));
}

export async function answerTradeProposal(idProposta, action) {
  return parseResponse(await fetch(`/api/propostas-troca/${idProposta}/${action}`, {
    method: 'PATCH', credentials: 'include', headers: { Accept: 'application/json' }
  }));
}

export async function getAcceptedNegotiations(cpf) {
  return parseResponse(await fetch(`/api/concretizacoes/aceitante/${encodeURIComponent(cpf)}`, {
    credentials: 'include', headers: { Accept: 'application/json' }
  }));
}

export async function getMyNegotiations() {
  return parseResponse(await fetch('/api/concretizacoes/minhas', {
    credentials: 'include', headers: { Accept: 'application/json' }
  }));
}

export async function getGivenReviews(cpf) {
  return parseResponse(await fetch(`/api/avaliacoes/avaliadores/${encodeURIComponent(cpf)}`, {
    credentials: 'include', headers: { Accept: 'application/json' }
  }));
}

export async function createReview(idConcretizacao, payload) {
  return parseResponse(await fetch(`/api/avaliacoes/concretizacoes/${idConcretizacao}`, {
    method: 'POST', headers: JSON_HEADERS, credentials: 'include',
    body: JSON.stringify(payload)
  }));
}

export async function getAlbums(cpf) {
  return parseResponse(await fetch(`/api/usuarios/${encodeURIComponent(cpf)}/albuns`, {
    credentials: 'include', headers: { Accept: 'application/json' }
  }));
}

export async function createAlbum(cpf, nome) {
  return parseResponse(await fetch(`/api/usuarios/${encodeURIComponent(cpf)}/albuns`, {
    method: 'POST', headers: JSON_HEADERS, credentials: 'include',
    body: JSON.stringify({ nome, cpfUsuario: cpf })
  }));
}

export async function getWishes(cpf) {
  return parseResponse(await fetch(`/api/usuarios/${encodeURIComponent(cpf)}/desejos`, {
    credentials: 'include', headers: { Accept: 'application/json' }
  }));
}

export async function addWish(cpf, payload) {
  return parseResponse(await fetch(`/api/usuarios/${encodeURIComponent(cpf)}/desejos`, {
    method: 'POST', headers: JSON_HEADERS, credentials: 'include', body: JSON.stringify(payload)
  }));
}

export async function getAlbumStickers(cpf, nomeAlbum) {
  return parseResponse(await fetch(`/api/usuarios/${encodeURIComponent(cpf)}/albuns/${encodeURIComponent(nomeAlbum)}/figurinhas`, {
    credentials: 'include', headers: { Accept: 'application/json' }
  }));
}

export async function addStickerToAlbum(cpf, nomeAlbum, payload) {
  return parseResponse(await fetch(`/api/usuarios/${encodeURIComponent(cpf)}/albuns/${encodeURIComponent(nomeAlbum)}/figurinhas`, {
    method: 'POST', headers: JSON_HEADERS, credentials: 'include', body: JSON.stringify(payload)
  }));
}

export async function removeStickerFromAlbum(cpf, nomeAlbum, codigo, tipo) {
  return parseResponse(await fetch(
    `/api/usuarios/${encodeURIComponent(cpf)}/albuns/${encodeURIComponent(nomeAlbum)}/figurinhas/${encodeURIComponent(codigo)}/${encodeURIComponent(tipo)}`,
    { method: 'DELETE', credentials: 'include' }
  ));
}

export async function getPossessions(cpf) {
  return parseResponse(await fetch(`/api/usuarios/${encodeURIComponent(cpf)}/posses`, {
    credentials: 'include',
    headers: { Accept: 'application/json' }
  }));
}

export async function createSale(cpf, payload) {
  return parseResponse(await fetch(`/api/usuarios/${encodeURIComponent(cpf)}/vendas`, {
    method: 'POST',
    headers: JSON_HEADERS,
    credentials: 'include',
    body: JSON.stringify(payload)
  }));
}

export async function createTrade(cpf, payload) {
  return parseResponse(await fetch(`/api/usuarios/${encodeURIComponent(cpf)}/trocas`, {
    method: 'POST',
    headers: JSON_HEADERS,
    credentials: 'include',
    body: JSON.stringify(payload)
  }));
}

export async function getCatalog() {
  return parseResponse(await fetch('/api/figurinhas', {
    credentials: 'include',
    headers: { Accept: 'application/json' }
  }));
}

export async function addPossession(cpf, payload) {
  return parseResponse(await fetch(`/api/usuarios/${encodeURIComponent(cpf)}/posses`, {
    method: 'POST',
    headers: JSON_HEADERS,
    credentials: 'include',
    body: JSON.stringify(payload)
  }));
}

export async function removePossessionQuantity(cpf, idPosse, quantidade) {
  return parseResponse(await fetch(
    `/api/usuarios/${encodeURIComponent(cpf)}/posses/${idPosse}/quantidade/remover`,
    {
      method: 'PATCH',
      headers: JSON_HEADERS,
      credentials: 'include',
      body: JSON.stringify({ quantidade })
    }
  ));
}

export async function deletePossession(cpf, idPosse) {
  return parseResponse(await fetch(
    `/api/usuarios/${encodeURIComponent(cpf)}/posses/${idPosse}`,
    { method: 'DELETE', credentials: 'include' }
  ));
}
