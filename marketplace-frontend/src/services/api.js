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

export async function getOffers() {
  return parseResponse(await fetch('/api/ofertas', {
    credentials: 'include',
    headers: { Accept: 'application/json' }
  }));
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
