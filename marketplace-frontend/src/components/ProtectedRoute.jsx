import { useEffect, useState } from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { getProfile } from '../services/api';

export default function ProtectedRoute() {
  const [state, setState] = useState({ loading: true, authenticated: false });

  useEffect(() => {
    let active = true;

    getProfile()
      .then(() => active && setState({ loading: false, authenticated: true }))
      .catch(() => active && setState({ loading: false, authenticated: false }));

    return () => {
      active = false;
    };
  }, []);

  if (state.loading) {
    return <div className="screen-center">Verificando sessão...</div>;
  }

  return state.authenticated ? <Outlet /> : <Navigate to="/login" replace />;
}
