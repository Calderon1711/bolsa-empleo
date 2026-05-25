import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { api } from "../api.js";

export default function AdminCaracteristicas() {
    const [padreId, setPadreId] = useState(null);
    const [actual, setActual] = useState(null);
    const [ruta, setRuta] = useState([]);
    const [raices, setRaices] = useState([]);
    const [subcategorias, setSubcategorias] = useState([]);

    const [nombre, setNombre] = useState("");
    const [mensaje, setMensaje] = useState("");
    const [error, setError] = useState("");
    const [cargando, setCargando] = useState(true);

    useEffect(() => {
        cargarCaracteristicas(padreId);
    }, [padreId]);

    async function cargarCaracteristicas(idPadre) {
        setCargando(true);
        setError("");

        try {
            const path = idPadre
                ? `/admin/caracteristicas?padreId=${idPadre}`
                : "/admin/caracteristicas";

            const data = await api.get(path);

            setActual(data.actual);
            setRuta(data.ruta || []);
            setRaices(data.raices || []);
            setSubcategorias(data.subcategorias || []);
        } catch (e) {
            console.error(e);
            setError(e.message || "No se pudieron cargar las características");
        } finally {
            setCargando(false);
        }
    }

    async function registrarCaracteristica(event) {
        event.preventDefault();

        setMensaje("");
        setError("");

        try {
            await api.post("/admin/caracteristicas", {
                nombre,
                padreId: padreId ? String(padreId) : null
            });

            setNombre("");
            setMensaje("Característica registrada correctamente.");

            await cargarCaracteristicas(padreId);
        } catch (e) {
            console.error(e);
            setError(e.message || "No se pudo registrar la característica");
        }
    }

    function abrirCaracteristica(id) {
        setPadreId(id);
        setMensaje("");
        setError("");
    }

    function volverRaiz() {
        setPadreId(null);
        setMensaje("");
        setError("");
    }

    function volverAnterior() {
        if (ruta.length <= 1) {
            volverRaiz();
            return;
        }

        const anterior = ruta[ruta.length - 2];
        setPadreId(anterior.id);
    }

    return (
        <div className="container mt-5">
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h2>Administrar características</h2>

                <Link className="btn btn-secondary" to="/admin">
                    Volver al panel
                </Link>
            </div>

            {mensaje && (
                <div className="alert alert-success">
                    {mensaje}
                </div>
            )}

            {error && (
                <div className="alert alert-danger">
                    {error}
                </div>
            )}

            <div className="card mb-4">
                <div className="card-body">
                    <h5 className="card-title">
                        Ubicación actual
                    </h5>

                    {ruta.length === 0 ? (
                        <p className="text-muted mb-0">
                            Nivel principal
                        </p>
                    ) : (
                        <div>
                            <button
                                className="btn btn-link p-0"
                                onClick={volverRaiz}
                            >
                                Principal
                            </button>

                            {ruta.map((item) => (
                                <span key={item.id}>
                                    {" / "}
                                    <button
                                        className="btn btn-link p-0"
                                        onClick={() => abrirCaracteristica(item.id)}
                                    >
                                        {item.nombre}
                                    </button>
                                </span>
                            ))}
                        </div>
                    )}
                </div>
            </div>

            <div className="card mb-4">
                <div className="card-body">
                    <h5 className="card-title">
                        {actual
                            ? `Agregar subcaracterística a: ${actual.nombre}`
                            : "Agregar característica principal"}
                    </h5>

                    <form onSubmit={registrarCaracteristica}>
                        <div className="row g-2">
                            <div className="col-md-9">
                                <input
                                    type="text"
                                    className="form-control"
                                    placeholder="Nombre de la característica"
                                    value={nombre}
                                    onChange={(e) => setNombre(e.target.value)}
                                    required
                                />
                            </div>

                            <div className="col-md-3">
                                <button className="btn btn-primary w-100">
                                    Registrar
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>

            <div className="card">
                <div className="card-body">
                    <div className="d-flex justify-content-between align-items-center mb-3">
                        <h5 className="card-title mb-0">
                            {actual
                                ? `Subcaracterísticas de ${actual.nombre}`
                                : "Características principales"}
                        </h5>

                        {actual && (
                            <button
                                className="btn btn-outline-secondary btn-sm"
                                onClick={volverAnterior}
                            >
                                Subir nivel
                            </button>
                        )}
                    </div>

                    {cargando ? (
                        <div className="alert alert-secondary">
                            Cargando características...
                        </div>
                    ) : subcategorias.length === 0 ? (
                        <div className="alert alert-info">
                            No hay características registradas en este nivel.
                        </div>
                    ) : (
                        <div className="list-group">
                            {subcategorias.map((caracteristica) => (
                                <button
                                    type="button"
                                    key={caracteristica.id}
                                    className="list-group-item list-group-item-action d-flex justify-content-between align-items-center"
                                    onClick={() => abrirCaracteristica(caracteristica.id)}
                                >
                                    <span>{caracteristica.nombre}</span>
                                    <span className="badge bg-primary rounded-pill">
                                        Ver subniveles
                                    </span>
                                </button>
                            ))}
                        </div>
                    )}
                </div>
            </div>

            {raices.length > 0 && (
                <div className="card mt-4">
                    <div className="card-body">
                        <h5 className="card-title">Acceso rápido a principales</h5>

                        <div className="d-flex flex-wrap gap-2">
                            {raices.map((raiz) => (
                                <button
                                    key={raiz.id}
                                    className="btn btn-outline-primary btn-sm"
                                    onClick={() => abrirCaracteristica(raiz.id)}
                                >
                                    {raiz.nombre}
                                </button>
                            ))}
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}