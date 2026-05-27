import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { api } from "../api.js";

export default function OferentePuestos() {
    const [puestos, setPuestos] = useState([]);
    const [postulaciones, setPostulaciones] = useState([]);
    const [mensaje, setMensaje] = useState("");
    const [error, setError] = useState("");
    const [cargando, setCargando] = useState(true);

    useEffect(() => {
        cargarDatos();
    }, []);

    async function cargarDatos() {
        setCargando(true);
        setError("");

        try {
            const puestosData = await api.get("/oferente/puestos-disponibles");
            const postulacionesData = await api.get("/oferente/postulaciones");

            setPuestos(Array.isArray(puestosData) ? puestosData : []);
            setPostulaciones(Array.isArray(postulacionesData) ? postulacionesData : []);
        } catch (e) {
            console.error(e);
            setError(e.message || "No se pudieron cargar los puestos");
        } finally {
            setCargando(false);
        }
    }

    async function postular(idPuesto) {
        setMensaje("");
        setError("");

        try {
            const respuesta = await api.post(`/oferente/puestos/${idPuesto}/postular`);

            setMensaje(respuesta.mensaje || "Postulación enviada correctamente.");
            await cargarDatos();
        } catch (e) {
            console.error(e);
            setError(e.message || "No se pudo enviar la postulación");
        }
    }

    function yaPostulado(idPuesto) {
        return postulaciones.some((p) => Number(p.puestoId) === Number(idPuesto));
    }

    return (
        <div className="container mt-5">
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h2>Puestos disponibles</h2>

                <Link className="btn btn-secondary" to="/oferente">
                    Volver al panel
                </Link>
            </div>

            {mensaje && <div className="alert alert-success">{mensaje}</div>}
            {error && <div className="alert alert-danger">{error}</div>}

            {cargando ? (
                <div className="alert alert-secondary">Cargando puestos...</div>
            ) : puestos.length === 0 ? (
                <div className="alert alert-info">No hay puestos disponibles.</div>
            ) : (
                <div className="row g-3">
                    {puestos.map((puesto) => (
                        <div className="col-md-6" key={puesto.id}>
                            <div className="card h-100 shadow-sm">
                                <div className="card-body">
                                    <div className="d-flex justify-content-between align-items-start">
                                        <h5 className="card-title">{puesto.titulo}</h5>

                                        <span className="badge bg-primary">
                                            {puesto.tipoPublicacion}
                                        </span>
                                    </div>

                                    <p className="card-text">{puesto.descripcion}</p>

                                    <p className="mb-1">
                                        <strong>Empresa:</strong> {puesto.empresaNombre}
                                    </p>

                                    <p className="mb-1">
                                        <strong>Salario:</strong> ₡{puesto.salario}
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
                                        <p className="text-muted">Sin requisitos registrados.</p>
                                    )}

                                    {yaPostulado(puesto.id) || puesto.postulado ? (
                                        <button className="btn btn-secondary btn-sm" disabled>
                                            Ya postulado
                                        </button>
                                    ) : (
                                        <button
                                            className="btn btn-success btn-sm"
                                            onClick={() => postular(puesto.id)}
                                        >
                                            Postularme
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