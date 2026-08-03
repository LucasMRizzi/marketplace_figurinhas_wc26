import { Navigate, Route, Routes } from 'react-router-dom';
import Layout from './components/Layout';
import ProtectedRoute from './components/ProtectedRoute';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import OffersPage from './pages/OffersPage';
import CreateOfferPage from './pages/CreateOfferPage';
import InventoryPage from './pages/InventoryPage';

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/registro" element={<RegisterPage />} />

      <Route element={<ProtectedRoute />}>
        <Route element={<Layout />}>
          <Route path="/ofertas" element={<OffersPage />} />
          <Route path="/ofertas/nova" element={<CreateOfferPage />} />
          <Route path="/inventario" element={<InventoryPage />} />
        </Route>
      </Route>

      <Route path="/" element={<Navigate to="/ofertas" replace />} />
      <Route path="*" element={<Navigate to="/ofertas" replace />} />
    </Routes>
  );
}
