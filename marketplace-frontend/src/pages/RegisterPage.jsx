import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { registerUser } from '../services/api';
import { maskForField, onlyDigits } from '../utils/inputMasks';

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
    setForm((current) => ({ ...current, [name]: maskForField(name, value) }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setError('');
    setLoading(true);

    try {
      await registerUser({
        cpf: onlyDigits(form.cpf, 11),
        nome: form.nome,
        email: form.email,
        telefone: onlyDigits(form.telefone, 11),
        senha: form.senha,
        endereco: {
          logradouro: form.logradouro,
          numero: Number(form.numero),
          caixaPostal: onlyDigits(form.caixaPostal, 15),
          cidade: form.cidade,
          cep: onlyDigits(form.cep, 8)
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
        <label>CPF<input name="cpf" value={form.cpf} onChange={updateField} inputMode="numeric" autoComplete="off" minLength="14" maxLength="14" pattern="[0-9]{3}[.][0-9]{3}[.][0-9]{3}-[0-9]{2}" title="Informe os 11 números do CPF" placeholder="000.000.000-00" required /></label>
        <label>Nome completo<input name="nome" value={form.nome} onChange={updateField} required /></label>
        <label>E-mail<input name="email" type="email" value={form.email} onChange={updateField} required /></label>
        <label>Telefone<input name="telefone" value={form.telefone} onChange={updateField} inputMode="tel" autoComplete="tel" minLength="14" maxLength="15" pattern="[(][0-9]{2}[)] [0-9]{4,5}-[0-9]{4}" title="Informe o DDD e o número do telefone" placeholder="(00) 00000-0000" required /></label>
        <label>Senha<input name="senha" type="password" minLength="8" value={form.senha} onChange={updateField} autoComplete="new-password" required /></label>
        <label>CEP<input name="cep" value={form.cep} onChange={updateField} inputMode="numeric" autoComplete="postal-code" minLength="9" maxLength="9" pattern="[0-9]{5}-[0-9]{3}" title="Informe os 8 números do CEP" placeholder="00000-000" required /></label>
        <label className="span-2">Logradouro<input name="logradouro" value={form.logradouro} onChange={updateField} required /></label>
        <label>Número<input name="numero" type="number" min="1" value={form.numero} onChange={updateField} required /></label>
        <label>Caixa postal<input name="caixaPostal" value={form.caixaPostal} onChange={updateField} inputMode="numeric" maxLength="15" pattern="[0-9]+" title="Informe somente números" placeholder="Somente números" required /></label>
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
