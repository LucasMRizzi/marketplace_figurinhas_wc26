import { NavLink, Outlet, useNavigate } from 'react-router-dom';

export default function Layout() {
  const navigate = useNavigate();

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
          <NavLink to="/ofertas">Ofertas</NavLink>
          <NavLink to="/ofertas/nova">Criar oferta</NavLink>
          <NavLink to="/inventario">Inventário</NavLink>
          <button className="link-button" onClick={handleLogout}>Sair</button>
        </nav>
      </header>

      <main className="page-container">
        <Outlet />
      </main>
    </div>
  );
}
