import { Navigate, Route, Routes } from 'react-router-dom';
import Layout from './components/Layout';
import ProtectedRoute from './components/ProtectedRoute';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import OffersPage from './pages/OffersPage';
import CreateOfferPage from './pages/CreateOfferPage';
import InventoryPage from './pages/InventoryPage';
import OfferDetailPage from './pages/OfferDetailPage';
import CollectionPage from './pages/CollectionPage';
import NegotiationsPage from './pages/NegotiationsPage';
import ProfilePage from './pages/ProfilePage';
import MyOffersPage from './pages/MyOffersPage';
import TradeProposalPage from './pages/TradeProposalPage';
import ProposalDetailPage from './pages/ProposalDetailPage';

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/registro" element={<RegisterPage />} />

      <Route element={<ProtectedRoute />}>
        <Route element={<Layout />}>
          <Route path="/ofertas" element={<OffersPage />} />
          <Route path="/ofertas/minhas" element={<MyOffersPage />} />
          <Route path="/ofertas/nova" element={<CreateOfferPage />} />
          <Route path="/ofertas/:idOferta" element={<OfferDetailPage />} />
          <Route path="/ofertas/:idOferta/propor-troca" element={<TradeProposalPage />} />
          <Route path="/inventario" element={<InventoryPage />} />
          <Route path="/colecao" element={<CollectionPage />} />
          <Route path="/negociacoes" element={<NegotiationsPage />} />
          <Route path="/negociacoes/propostas/:idProposta" element={<ProposalDetailPage />} />
          <Route path="/perfil" element={<ProfilePage />} />
        </Route>
      </Route>

      <Route path="/" element={<Navigate to="/ofertas" replace />} />
      <Route path="*" element={<Navigate to="/ofertas" replace />} />
    </Routes>
  );
}
