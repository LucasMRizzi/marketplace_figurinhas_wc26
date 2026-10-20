export function onlyDigits(value, maxLength = Infinity) {
  return String(value ?? '').replace(/\D/g, '').slice(0, maxLength);
}

export function formatCpf(value) {
  const digits = onlyDigits(value, 11);
  return digits
    .replace(/^(\d{3})(\d)/, '$1.$2')
    .replace(/^(\d{3})\.(\d{3})(\d)/, '$1.$2.$3')
    .replace(/\.(\d{3})(\d)/, '.$1-$2');
}

export function formatPhone(value) {
  const digits = onlyDigits(value, 11);
  if (digits.length <= 10) {
    return digits.replace(/^(\d{2})(\d)/, '($1) $2').replace(/(\d{4})(\d)/, '$1-$2');
  }
  return digits.replace(/^(\d{2})(\d)/, '($1) $2').replace(/(\d{5})(\d)/, '$1-$2');
}

export function formatCep(value) {
  return onlyDigits(value, 8).replace(/^(\d{5})(\d)/, '$1-$2');
}

export function formatPostalBox(value) {
  return onlyDigits(value, 15);
}

export function maskForField(name, value) {
  if (name === 'cpf') return formatCpf(value);
  if (name === 'telefone') return formatPhone(value);
  if (name === 'cep') return formatCep(value);
  if (name === 'caixaPostal') return formatPostalBox(value);
  return value;
}
