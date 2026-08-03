import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { login } from '../services/api';

export default function LoginPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: '', senha: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  function updateField(event) {
    setForm((current) => ({ ...current, [event.target.name]: event.target.value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setError('');
    setLoading(true);

    try {
      await login(form.email, form.senha);
      navigate('/ofertas', { replace: true });
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="auth-page">
      <section className="auth-hero">
        <span className="eyebrow">COPA DO MUNDO 2026</span>
        <h1>Complete seu álbum negociando com colecionadores.</h1>
        <p>Encontre vendas, proponha trocas e acompanhe suas negociações em um só lugar.</p>
      </section>

      <section className="auth-card">
        <div>
          <span className="eyebrow">BEM-VINDO</span>
          <h2>Entrar na sua conta</h2>
        </div>

        <form onSubmit={handleSubmit} className="form-stack">
          <label>
            E-mail
            <input
              name="email"
              type="email"
              value={form.email}
              onChange={updateField}
              placeholder="voce@email.com"
              required
            />
          </label>

          <label>
            Senha
            <input
              name="senha"
              type="password"
              value={form.senha}
              onChange={updateField}
              placeholder="Sua senha"
              required
            />
          </label>

          {error && <div className="alert error">{error}</div>}

          <button className="primary-button" disabled={loading}>
            {loading ? 'Entrando...' : 'Entrar'}
          </button>
        </form>

        <p className="auth-footer">
          Ainda não possui conta? <Link to="/registro">Cadastre-se</Link>
        </p>
      </section>
    </div>
  );
}
