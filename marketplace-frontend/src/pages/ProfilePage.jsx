import { useEffect, useState } from 'react';
import { getProfile, updateProfile } from '../services/api';
import { formatCep, formatCpf, formatPhone, formatPostalBox, maskForField, onlyDigits } from '../utils/inputMasks';
import './ProfilePage.css';

const emptyForm = { nome: '', email: '', telefone: '', logradouro: '', numero: '', caixaPostal: '', cidade: '', cep: '' };

function formFromProfile(profile) {
  return {
    nome: profile?.nome || '', email: profile?.email || '', telefone: formatPhone(profile?.telefone),
    logradouro: profile?.endereco?.logradouro || '', numero: profile?.endereco?.numero ?? '',
    caixaPostal: formatPostalBox(profile?.endereco?.caixaPostal), cidade: profile?.endereco?.cidade || '', cep: formatCep(profile?.endereco?.cep)
  };
}

export default function ProfilePage() {
  const [profile, setProfile] = useState(null); const [form, setForm] = useState(emptyForm);
  const [editing, setEditing] = useState(false); const [saving, setSaving] = useState(false);
  const [error, setError] = useState(''); const [message, setMessage] = useState('');

  useEffect(() => { getProfile().then((data) => { setProfile(data); setForm(formFromProfile(data)); }).catch((requestError) => setError(requestError.message)); }, []);

  function updateField(event) { const { name, value } = event.target; setForm((current) => ({ ...current, [name]: maskForField(name, value) })); }
  function cancelEditing() { setForm(formFromProfile(profile)); setEditing(false); setError(''); }

  async function handleSubmit(event) {
    event.preventDefault(); setSaving(true); setError(''); setMessage('');
    try {
      const updated = await updateProfile({
        nome: form.nome.trim(), email: form.email.trim(), telefone: onlyDigits(form.telefone, 11),
        endereco: { logradouro: form.logradouro.trim(), numero: Number(form.numero), caixaPostal: onlyDigits(form.caixaPostal, 15), cidade: form.cidade.trim(), cep: onlyDigits(form.cep, 8) }
      });
      setProfile(updated); setForm(formFromProfile(updated)); setEditing(false); setMessage('Perfil atualizado com sucesso.');
    } catch (requestError) { setError(requestError.message); } finally { setSaving(false); }
  }

  if (!profile && !error) return <div className="empty-state">Carregando perfil...</div>;
  if (!profile) return <div className="alert error">{error}</div>;

  return <section className="profile-page">
    <div className="page-heading profile-heading"><div><span className="eyebrow">CONTA</span><h1>Meu perfil</h1><p>Gerencie seus dados pessoais e endereço de negociação.</p></div>{!editing && <button type="button" className="primary-button" onClick={() => { setEditing(true); setMessage(''); }}>Editar perfil</button>}</div>
    {message && <div className="alert success">{message}</div>}{error && <div className="alert error">{error}</div>}
    <div className="profile-summary-card"><div className="profile-identity"><div className="profile-avatar" aria-hidden="true">{profile.nome?.slice(0, 1)?.toUpperCase()}</div><div><h2>{profile.nome}</h2><p>{profile.email}</p><small>CPF {formatCpf(profile.cpf)}</small></div></div><dl className="profile-stats"><div><dt>Saldo</dt><dd>{formatMoney(profile.saldo)}</dd></div><div><dt>Avaliação</dt><dd><span className="profile-star">★</span> {Number(profile.avaliacaoMedia ?? 0).toFixed(1)} / 5</dd></div><div><dt>Localização</dt><dd>{profile.endereco?.cidade ?? '—'}</dd></div></dl></div>
    {editing ? <form className="profile-edit-card" onSubmit={handleSubmit}>
      <div className="profile-form-title"><div><span className="eyebrow">EDIÇÃO</span><h2>Dados do perfil</h2></div><p>CPF, saldo e avaliação não podem ser alterados aqui.</p></div>
      <div className="profile-form-grid">
        <Field label="Nome completo" name="nome" value={form.nome} onChange={updateField} maxLength="100" />
        <Field label="E-mail" name="email" type="email" value={form.email} onChange={updateField} maxLength="100" />
        <Field label="Telefone" name="telefone" value={form.telefone} onChange={updateField} inputMode="tel" minLength="14" maxLength="15" pattern="[(][0-9]{2}[)] [0-9]{4,5}-[0-9]{4}" title="Informe o DDD e o número do telefone" placeholder="(19) 99999-9999" />
        <Field label="CEP" name="cep" value={form.cep} onChange={updateField} inputMode="numeric" minLength="9" maxLength="9" pattern="[0-9]{5}-[0-9]{3}" title="Informe os 8 números do CEP" placeholder="00000-000" />
        <label className="profile-field profile-field-wide">Logradouro<input name="logradouro" value={form.logradouro} onChange={updateField} maxLength="100" required /></label>
        <Field label="Número" name="numero" type="number" min="1" value={form.numero} onChange={updateField} />
        <Field label="Caixa postal" name="caixaPostal" value={form.caixaPostal} onChange={updateField} inputMode="numeric" maxLength="15" pattern="[0-9]+" title="Informe somente números" placeholder="Somente números" />
        <Field label="Cidade" name="cidade" value={form.cidade} onChange={updateField} maxLength="50" />
      </div>
      <div className="profile-form-actions"><button type="button" className="secondary-button" onClick={cancelEditing} disabled={saving}>Cancelar</button><button className="primary-button" disabled={saving}>{saving ? 'Salvando...' : 'Salvar alterações'}</button></div>
    </form> : <section className="profile-details-card"><div><span className="eyebrow">CONTATO</span><h2>Dados cadastrados</h2></div><dl className="profile-details-list"><div><dt>Telefone</dt><dd>{formatPhone(profile.telefone) || 'Não informado'}</dd></div><div><dt>Endereço</dt><dd>{formatAddress(profile.endereco)}</dd></div><div><dt>CEP</dt><dd>{formatCep(profile.endereco?.cep) || 'Não informado'}</dd></div><div><dt>Caixa postal</dt><dd>{formatPostalBox(profile.endereco?.caixaPostal) || 'Não informada'}</dd></div></dl></section>}
  </section>;
}

function Field({ label, ...props }) { return <label className="profile-field">{label}<input {...props} required /></label>; }
function formatAddress(address) { if (!address) return 'Não informado'; return `${address.logradouro || 'Endereço'}${address.numero ? `, ${address.numero}` : ''} · ${address.cidade || 'Cidade não informada'}`; }
function formatMoney(value) { return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(Number(value ?? 0)); }
