import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { api } from "../api.js";

export default function EmpresaPuestos() {
    const [puestos, setPuestos] = useState([]);
    const [mensaje, setMensaje] = useState("");
    const [error, setError] = useState("");
    const [cargando, setCargando] = useState(true);

    useEffect(() => {
        cargarPuestos();
    }, []);

    async function cargarPuestos() {
        setCargando(true);
        setError("");

        try {
            const data = await api.get("/empresa/puestos");
            setPuestos(Array.isArray(data) ? data : []);
        } catch (e) {
            console.error(e);
            setError(e.message || "No se pudieron cargar los puestos");
        } finally {
            setCargando(false);
        }
    }

    async function desactivarPuesto(idPuesto) {
        setMensaje("");
        setError("");

        try {
            const respuesta = await api.post(`/empresa/puestos/${idPuesto}/desactivar`);

            setMensaje(respuesta.mensaje || "Puesto desactivado correctamente.");
            await cargarPuestos();

        } catch (e) {
            console.error(e);
            setError(e.message || "No se pudo desactivar el puesto");
        }
    }

    async function activarPuesto(idPuesto) {
        setMensaje("");
        setError("");

        try {
            const respuesta = await api.post(`/empresa/puestos/${idPuesto}/activar`);

            setMensaje(respuesta.mensaje || "Puesto activado correctamente.");
            await cargarPuestos();

        } catch (e) {
            console.error(e);
            setError(e.message || "No se pudo activar el puesto");
        }
    }

    return (
        <div className="container mt-5">
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h2>Mis puestos publicados</h2>

                <div className="d-flex gap-2">
                    <Link className="btn btn-success" to="/empresa/puestos/nuevo">
                        Publicar puesto
                    </Link>

                    <Link className="btn btn-secondary" to="/empresa">
                        Volver al panel
                    </Link>
                </div>
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

            {cargando ? (
                <div className="alert alert-secondary">
                    Cargando puestos...
                </div>
            ) : puestos.length === 0 ? (
                <div className="alert alert-info">
                    No hay puestos publicados todavía.
                </div>
            ) : (
                <div className="row g-3">
                    {puestos.map((puesto) => (
                        <div className="col-md-6" key={puesto.id}>
                            <div className="card h-100 shadow-sm">
                                <div className="card-body">
                                    <div className="d-flex justify-content-between align-items-start">
                                        <h5 className="card-title">{puesto.titulo}</h5>

                                        <span className={`badge ${puesto.estado ? "bg-success" : "bg-secondary"}`}>
                                            {puesto.estado ? "Activo" : "Inactivo"}
                                        </span>
                                    </div>

                                    <p className="card-text">
                                        {puesto.descripcion}
                                    </p>

                                    <p className="mb-1">
                                        <strong>Salario:</strong> ₡{puesto.salario}
                                    </p>

                                    <p className="mb-1">
                                        <strong>Tipo:</strong> {puesto.tipoPublicacion}
                                    </p>

                                    <p className="mb-3">
                                        <strong>Fecha:</strong> {puesto.fechaRegistro}
                                    </p>

                                    <h6>Requisitos</h6>

                                    {puesto.requisitos && puesto.requisitos.length > 0 ? (
                                        <ul>
                                            {puesto.requisitos.map((req) => (
                                                <li key={req.id}>
                                                    {req.caracteristicaNombre} - Nivel {req.nivelRequerido}
                                                </li>
                                            ))}
                                        </ul>
                                    ) : (
                                        <p className="text-muted">
                                            Sin requisitos registrados.
                                        </p>
                                    )}

                                    <Link
                                        className="btn btn-outline-primary btn-sm me-2"
                                        to={`/empresa/puestos/${puesto.id}/candidatos`}
                                    >
                                        Buscar candidatos
                                    </Link>

                                    {puesto.estado ? (
                                        <button
                                            className="btn btn-outline-danger btn-sm"
                                            onClick={() => desactivarPuesto(puesto.id)}
                                        >
                                            Desactivar puesto
                                        </button>
                                    ) : (
                                        <button
                                            className="btn btn-outline-success btn-sm"
                                            onClick={() => activarPuesto(puesto.id)}
                                        >
                                            Activar puesto
                                        </button>
                                    )}
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}