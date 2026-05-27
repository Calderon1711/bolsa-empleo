import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { api } from "../api.js";

export default function Inicio() {
    const [puestos, setPuestos] = useState([]);
    const [puestoSeleccionado, setPuestoSeleccionado] = useState(null);
    const [error, setError] = useState("");
    const [cargando, setCargando] = useState(true);

    useEffect(() => {
        cargarPuestosRecientes();
    }, []);

    async function cargarPuestosRecientes() {
        setCargando(true);
        setError("");

        try {
            const data = await api.get("/public/puestos-recientes");
            setPuestos(Array.isArray(data) ? data : []);
        } catch (e) {
            console.error(e);
            setError(e.message || "No se pudieron cargar los puestos recientes");
        } finally {
            setCargando(false);
        }
    }

    function abrirDetalle(puesto) {
        setPuestoSeleccionado(puesto);
    }

    function cerrarDetalle() {
        setPuestoSeleccionado(null);
    }

    return (
        <div className="container mt-4">
            <nav className="d-flex justify-content-between align-items-center border-bottom pb-3 mb-4">
                <h4 className="mb-0">Bolsa de Empleo</h4>

                <div className="d-flex gap-2">
                    <Link className="btn btn-outline-primary btn-sm" to="/">
                        Inicio
                    </Link>

                    <Link className="btn btn-outline-primary btn-sm" to="/registro-empresa">
                        Empresa
                    </Link>

                    <Link className="btn btn-outline-primary btn-sm" to="/registro-oferente">
                        Oferente
                    </Link>

                    <Link className="btn btn-primary btn-sm" to="/login">
                        Login
                    </Link>
                </div>
            </nav>

            <h2 className="mb-4">Puestos públicos recientes</h2>

            {error && (
                <div className="alert alert-danger">
                    {error}
                </div>
            )}

            {cargando ? (
                <div className="alert alert-secondary">
                    Cargando puestos recientes...
                </div>
            ) : puestos.length === 0 ? (
                <div className="alert alert-info">
                    No hay puestos públicos recientes.
                </div>
            ) : (
                <div className="row g-3">
                    {puestos.map((puesto) => (
                        <div className="col-md-4" key={puesto.id}>
                            <div className="card h-100 shadow-sm">
                                <div className="card-body">
                                    <h5 className="card-title">
                                        {puesto.titulo}
                                    </h5>

                                    <p className="mb-1">
                                        <strong>Empresa:</strong> {puesto.empresaNombre}
                                    </p>

                                    <p className="mb-1">
                                        <strong>Salario:</strong> ₡{puesto.salario}
                                    </p>

                                    <p className="mb-3">
                                        {puesto.descripcion}
                                    </p>

                                    <button
                                        className="btn btn-outline-primary btn-sm w-100"
                                        onClick={() => abrirDetalle(puesto)}
                                    >
                                        Ver detalle
                                    </button>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}

            {puestoSeleccionado && (
                <div
                    className="modal d-block"
                    tabIndex="-1"
                    style={{ backgroundColor: "rgba(0,0,0,0.55)" }}
                >
                    <div className="modal-dialog modal-dialog-centered">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title">
                                    {puestoSeleccionado.titulo}
                                </h5>

                                <button
                                    type="button"
                                    className="btn-close"
                                    onClick={cerrarDetalle}
                                />
                            </div>

                            <div className="modal-body">
                                <p>
                                    <strong>Empresa:</strong>{" "}
                                    {puestoSeleccionado.empresaNombre}
                                </p>

                                <p>
                                    <strong>Descripción:</strong>{" "}
                                    {puestoSeleccionado.descripcion}
                                </p>

                                <p>
                                    <strong>Salario:</strong> ₡
                                    {puestoSeleccionado.salario}
                                </p>

                                <p>
                                    <strong>Tipo:</strong>{" "}
                                    {puestoSeleccionado.tipoPublicacion}
                                </p>

                                <h6>Requisitos</h6>

                                {puestoSeleccionado.requisitos &&
                                puestoSeleccionado.requisitos.length > 0 ? (
                                    <ul>
                                        {puestoSeleccionado.requisitos.map((req) => (
                                            <li key={req.id}>
                                                {req.caracteristicaNombre} - Nivel{" "}
                                                {req.nivelRequerido}
                                            </li>
                                        ))}
                                    </ul>
                                ) : (
                                    <p className="text-muted">
                                        Este puesto no tiene requisitos registrados.
                                    </p>
                                )}
                            </div>

                            <div className="modal-footer">
                                <button
                                    className="btn btn-secondary"
                                    onClick={cerrarDetalle}
                                >
                                    Cerrar
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}

            <footer className="border-top mt-5 pt-3 text-muted">
                <div className="d-flex justify-content-between">
                    <span>Bolsa de Empleo</span>
                    <span>Contacto: info@bolsaempleo.local</span>
                </div>
            </footer>
        </div>
    );
}