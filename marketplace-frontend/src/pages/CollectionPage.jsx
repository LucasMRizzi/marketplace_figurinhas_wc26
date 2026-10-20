import { useEffect, useMemo, useState } from 'react';
import {
  addStickerToAlbum, addWish, createAlbum, getAlbumStickers, getAlbums, getCatalog,
  getPossessions, getProfile, getWishes, removeStickerFromAlbum
} from '../services/api';

function asList(value) {
  if (Array.isArray(value)) return value;
  return value?.content ?? value?.items ?? [];
}

function normalizePossession(possession) {
  const figurinha = possession.figurinha ?? {};
  return {
    id: possession.idPosse ?? possession.id,
    codigo: possession.codigoFigurinha ?? possession.codigo ?? figurinha.codigo,
    tipo: possession.tipoFigurinha ?? possession.tipo ?? figurinha.tipo,
    nome: possession.nomeFigurinha ?? possession.nome ?? figurinha.nome ?? 'Figurinha',
    quantidade: possession.quantidade ?? 0
  };
}

export default function CollectionPage() {
  const [albums, setAlbums] = useState([]);
  const [wishes, setWishes] = useState([]);
  const [catalog, setCatalog] = useState([]);
  const [possessions, setPossessions] = useState([]);
  const [albumStickers, setAlbumStickers] = useState([]);
  const [profile, setProfile] = useState(null);
  const [selectedAlbumName, setSelectedAlbumName] = useState('');
  const [name, setName] = useState('');
  const [wishKey, setWishKey] = useState('');
  const [stickerKey, setStickerKey] = useState('');
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  const selectedAlbum = useMemo(
    () => albums.find((album) => album.nome === selectedAlbumName),
    [albums, selectedAlbumName]
  );

  async function loadCollection() {
    try {
      const currentProfile = await getProfile();
      const [albumList, wishList, stickerList, possessionList] = await Promise.all([
        getAlbums(currentProfile.cpf), getWishes(currentProfile.cpf), getCatalog(), getPossessions(currentProfile.cpf)
      ]);
      const normalizedAlbums = asList(albumList);
      setProfile(currentProfile);
      setAlbums(normalizedAlbums);
      setWishes(asList(wishList));
      setCatalog(asList(stickerList));
      setPossessions(asList(possessionList).map(normalizePossession));
      setSelectedAlbumName((current) => current || normalizedAlbums[0]?.nome || '');
    } catch (err) {
      setError(err.message);
    }
  }

  async function loadAlbumStickers() {
    if (!profile || !selectedAlbumName) {
      setAlbumStickers([]);
      return;
    }
    try {
      setAlbumStickers(asList(await getAlbumStickers(profile.cpf, selectedAlbumName)));
    } catch (err) {
      setError(err.message);
    }
  }

  useEffect(() => { loadCollection(); }, []);
  useEffect(() => { loadAlbumStickers(); }, [profile, selectedAlbumName]);

  async function createNewAlbum(event) {
    event.preventDefault();
    try {
      await createAlbum(profile.cpf, name);
      setName('');
      setMessage('Álbum criado. Agora escolha as figurinhas para colar.');
      await loadCollection();
    } catch (err) {
      setError(err.message);
    }
  }

  async function addToWishes(event) {
    event.preventDefault();
    const [codigoFigurinha, tipoFigurinha] = wishKey.split('::');
    if (!codigoFigurinha) return;
    try {
      await addWish(profile.cpf, { cpfUsuario: profile.cpf, codigoFigurinha, tipoFigurinha });
      setWishKey('');
      await loadCollection();
    } catch (err) {
      setError(err.message);
    }
  }

  async function addSticker(event) {
    event.preventDefault();
    const [codigoFigurinha, tipoFigurinha] = stickerKey.split('::');
    if (!codigoFigurinha || !selectedAlbumName) return;
    try {
      setError('');
      await addStickerToAlbum(profile.cpf, selectedAlbumName, { codigoFigurinha, tipoFigurinha });
      setStickerKey('');
      setMessage('Figurinha colada e completude atualizada.');
      await Promise.all([loadCollection(), loadAlbumStickers()]);
    } catch (err) {
      setError(err.message);
    }
  }

  async function removeSticker(sticker) {
    try {
      setError('');
      await removeStickerFromAlbum(profile.cpf, selectedAlbumName, sticker.codigoFigurinha, sticker.tipoFigurinha);
      setMessage('Figurinha removida e completude atualizada.');
      await Promise.all([loadCollection(), loadAlbumStickers()]);
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <section className="collection-page">
      <div className="page-heading">
        <span className="eyebrow">MINHA COLEÇÃO</span>
        <h1>Álbuns e desejos</h1>
        <p>Crie álbuns, cole figurinhas do seu inventário e acompanhe a completude.</p>
      </div>
      {error && <div className="alert error">{error}</div>}
      {message && <div className="alert success">{message}</div>}

      <div className="collection-grid">
        <div className="form-card">
          <h2>Meus álbuns</h2>
          <form className="inline-form" onSubmit={createNewAlbum}>
            <input value={name} onChange={(event) => setName(event.target.value)} placeholder="Nome do álbum" required />
            <button className="primary-button">Criar álbum</button>
          </form>
          {albums.length ? albums.map((album) => (
            <button type="button" className={album.nome === selectedAlbumName ? 'collection-row album-row selected' : 'collection-row album-row'} key={album.nome} onClick={() => setSelectedAlbumName(album.nome)}>
              <strong>{album.nome}</strong><span>{Number(album.completude ?? 0).toFixed(1)}% completo</span>
            </button>
          )) : <p className="muted-copy">Nenhum álbum criado ainda.</p>}
        </div>

        <div className="form-card">
          <h2>Lista de desejos</h2>
          <form className="inline-form" onSubmit={addToWishes}>
            <select value={wishKey} onChange={(event) => setWishKey(event.target.value)} required>
              <option value="">Selecione uma figurinha</option>
              {catalog.map((sticker) => <option key={`${sticker.codigo}-${sticker.tipo}`} value={`${sticker.codigo}::${sticker.tipo}`}>{sticker.nome} — {sticker.codigo}</option>)}
            </select>
            <button className="primary-button">Adicionar</button>
          </form>
          {wishes.length ? wishes.map((wish) => (
            <div className="collection-row" key={`${wish.codigoFigurinha}-${wish.tipoFigurinha}`}><strong>{wish.nomeFigurinha}</strong><span>{wish.codigoFigurinha} · {wish.tipoFigurinha}</span></div>
          )) : <p className="muted-copy">Sua lista está vazia.</p>}
        </div>
      </div>

      <section className="album-workspace form-card">
        <div className="section-heading">
          <div><span className="eyebrow">ÁLBUM SELECIONADO</span><h2>{selectedAlbum?.nome ?? 'Escolha ou crie um álbum'}</h2></div>
          {selectedAlbum && <span className="completion-badge">{Number(selectedAlbum.completude ?? 0).toFixed(1)}% completo</span>}
        </div>
        {selectedAlbum ? <>
          <form className="inline-form" onSubmit={addSticker}>
            <select value={stickerKey} onChange={(event) => setStickerKey(event.target.value)} required>
              <option value="">Selecione uma figurinha do seu inventário</option>
              {possessions.map((possession) => <option key={possession.id} value={`${possession.codigo}::${possession.tipo}`}>{possession.nome} — {possession.codigo} ({possession.quantidade} un.)</option>)}
            </select>
            <button className="primary-button">Colar figurinha</button>
          </form>
          {possessions.length === 0 && <p className="muted-copy">Adicione figurinhas ao inventário antes de colá-las no álbum.</p>}
          <div className="sticker-list">
            {albumStickers.map((sticker) => <div className="collection-row" key={`${sticker.codigoFigurinha}-${sticker.tipoFigurinha}`}><div><strong>{sticker.nomeFigurinha}</strong><span>{sticker.codigoFigurinha} · {sticker.tipoFigurinha}</span></div><button type="button" className="danger-button" onClick={() => removeSticker(sticker)}>Remover</button></div>)}
            {albumStickers.length === 0 && <p className="muted-copy">Nenhuma figurinha colada neste álbum.</p>}
          </div>
        </> : <div className="notice">Crie um álbum para começar a completar sua coleção.</div>}
      </section>
    </section>
  );
}
