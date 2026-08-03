import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { registerUser } from '../services/api';

const initialForm = {
  cpf: '', nome: '', email: '', telefone: '', senha: '',
  logradouro: '', numero: '', caixaPostal: '', cidade: '', cep: ''
};

export default function RegisterPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState(initialForm);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  function updateField(event) {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setError('');
    setLoading(true);

    try {
      await registerUser({
        cpf: form.cpf,
        nome: form.nome,
        email: form.email,
        telefone: form.telefone,
        senha: form.senha,
        endereco: {
          logradouro: form.logradouro,
          numero: Number(form.numero),
          caixaPostal: form.caixaPostal,
          cidade: form.cidade,
          cep: form.cep
        }
      });
      navigate('/login', { replace: true });
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="register-page">
      <div className="register-header">
        <span className="eyebrow">NOVA CONTA</span>
        <h1>Comece sua coleção</h1>
        <p>Crie seu cadastro para comprar, vender e trocar figurinhas.</p>
      </div>

      <form onSubmit={handleSubmit} className="form-card form-grid">
        <label>CPF<input name="cpf" value={form.cpf} onChange={updateField} required /></label>
        <label>Nome completo<input name="nome" value={form.nome} onChange={updateField} required /></label>
        <label>E-mail<input name="email" type="email" value={form.email} onChange={updateField} required /></label>
        <label>Telefone<input name="telefone" value={form.telefone} onChange={updateField} required /></label>
        <label>Senha<input name="senha" type="password" minLength="8" value={form.senha} onChange={updateField} autoComplete="new-password" required /></label>
        <label>CEP<input name="cep" value={form.cep} onChange={updateField} required /></label>
        <label className="span-2">Logradouro<input name="logradouro" value={form.logradouro} onChange={updateField} required /></label>
        <label>Número<input name="numero" type="number" min="1" value={form.numero} onChange={updateField} required /></label>
        <label>Caixa postal<input name="caixaPostal" value={form.caixaPostal} onChange={updateField} required /></label>
        <label className="span-2">Cidade<input name="cidade" value={form.cidade} onChange={updateField} required /></label>

        {error && <div className="alert error span-2">{error}</div>}

        <div className="form-actions span-2">
          <Link className="secondary-button" to="/login">Voltar</Link>
          <button className="primary-button" disabled={loading}>{loading ? 'Cadastrando...' : 'Criar conta'}</button>
        </div>
      </form>
    </div>
  );
}
