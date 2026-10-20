import { NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom';
import { useState } from 'react';

export default function Layout() {
  const navigate = useNavigate();
  const location = useLocation();
  const [offersMenuOpen, setOffersMenuOpen] = useState(false);

  async function handleLogout() {
    await fetch('/logout', {
      method: 'POST',
      credentials: 'include'
    });
    navigate('/login', { replace: true });
  }

  return (
    <div className="app-shell">
      <header className="topbar">
        <NavLink to="/ofertas" className="brand">
          <span className="brand-mark">26</span>
          <span>
            <strong>Figurinhas WC</strong>
            <small>Marketplace</small>
          </span>
        </NavLink>

        <nav className="nav-links">
          <div className="nav-dropdown">
            <button
              type="button"
              className={location.pathname.startsWith('/ofertas') ? 'nav-dropdown-trigger active' : 'nav-dropdown-trigger'}
              onClick={() => setOffersMenuOpen((current) => !current)}
              aria-expanded={offersMenuOpen}
              aria-controls="offers-menu"
            >
              Ofertas <span className="nav-caret" aria-hidden="true" />
            </button>
            {offersMenuOpen && (
              <div className="offers-menu" id="offers-menu">
                <NavLink to="/ofertas" onClick={() => setOffersMenuOpen(false)}>Ver ofertas</NavLink>
                <NavLink to="/ofertas/nova" onClick={() => setOffersMenuOpen(false)}>Criar oferta</NavLink>
                <NavLink to="/ofertas/minhas" onClick={() => setOffersMenuOpen(false)}>Minhas ofertas</NavLink>
              </div>
            )}
          </div>
          <NavLink to="/inventario">Inventário</NavLink>
          <NavLink to="/colecao">Coleção</NavLink>
          <NavLink to="/negociacoes">Negociações</NavLink>
          <NavLink to="/perfil">Perfil</NavLink>
          <button className="link-button" onClick={handleLogout}>Sair</button>
        </nav>
      </header>

      <main className="page-container">
        <Outlet />
      </main>
    </div>
  );
}
