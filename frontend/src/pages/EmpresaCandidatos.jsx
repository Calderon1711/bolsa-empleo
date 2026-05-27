import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { api, API_URL, obtenerToken } from "../api.js";

export default function EmpresaCandidatos() {
    const { idPuesto } = useParams();

    const [candidatos, setCandidatos] = useState([]);
    const [error, setError] = useState("");
    const [cargando, setCargando] = useState(true);

    useEffect(() => {
        cargarCandidatos();
    }, [idPuesto]);

    async function cargarCandidatos() {
        setCargando(true);
        setError("");

        try {
            const data = await api.get(`/empresa/puestos/${idPuesto}/candidatos`);
            setCandidatos(Array.isArray(data) ? data : []);
        } catch (e) {
            console.error(e);
            setError(e.message || "No se pudieron cargar los candidatos");
        } finally {
            setCargando(false);
        }
    }

    async function verCv(cedulaOferente) {
        setError("");

        try {
            const token = obtenerToken();

            const response = await fetch(`${API_URL}/empresa/oferentes/${cedulaOferente}/cv`, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });

            if (!response.ok) {
                throw new Error("No se pudo abrir el cv del oferente");
            }

            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);

            window.open(url, "_blank");

        } catch (e) {
            console.error(e);
            setError(e.message || "No se pudo abrir el cv");
        }
    }

    return (
        <div className="container mt-5">
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h2>Candidatos compatibles</h2>

                <Link className="btn btn-secondary" to="/empresa/puestos">
                    Volver a mis puestos
                </Link>
            </div>

            {error && (
                <div className="alert alert-danger">
                    {error}
                </div>
            )}

            {cargando ? (
                <div className="alert alert-secondary">
                    Cargando candidatos...
                </div>
            ) : candidatos.length === 0 ? (
                <div className="alert alert-info">
                    No se encontraron candidatos compatibles para este puesto.
                </div>
            ) : (
                <div className="row g-3">
                    {candidatos.map((candidato) => (
                        <div className="col-md-6" key={candidato.cedulaOferente}>
                            <div className="card h-100 shadow-sm">
                                <div className="card-body">
                                    <div className="d-flex justify-content-between align-items-start mb-2">
                                        <h5 className="card-title mb-0">
                                            {candidato.nombreOferente} {candidato.primerApellido}
                                        </h5>

                                        <span className="badge bg-primary">
                                            {candidato.porcentajeCoincidencia}% match
                                        </span>
                                    </div>

                                    <p className="mb-1">
                                        <strong>Cédula:</strong> {candidato.cedulaOferente}
                                    </p>

                                    <p className="mb-1">
                                        <strong>Correo:</strong> {candidato.correoOferente}
                                    </p>

                                    <p className="mb-1">
                                        <strong>Teléfono:</strong> {candidato.telefonoOferente}
                                    </p>

                                    <p className="mb-3">
                                        <strong>Residencia:</strong> {candidato.lugarResidencia}
                                    </p>

                                    <p>
                                        Coincidencias:{" "}
                                        <strong>
                                            {candidato.coincidencias} de {candidato.totalRequisitos}
                                        </strong>
                                    </p>

                                    <h6>Detalle de coincidencias</h6>

                                    <table className="table table-sm table-bordered">
                                        <thead>
                                        <tr>
                                            <th>Característica</th>
                                            <th>Req.</th>
                                            <th>Oferente</th>
                                            <th>%</th>
                                            <th>Estado</th>
                                        </tr>
                                        </thead>

                                        <tbody>
                                        {candidato.detalleCoincidencias.map((detalle) => (
                                            <tr key={detalle.caracteristicaId}>
                                                <td>{detalle.caracteristicaNombre}</td>
                                                <td>{detalle.nivelRequerido}</td>
                                                <td>{detalle.nivelOferente ?? "-"}</td>
                                                <td>{detalle.porcentajeDetalle ?? 0}%</td>
                                                <td>
                                                    {detalle.cumple ? (
                                                        <span className="badge bg-success">Cumple</span>
                                                    ) : (
                                                        <span className="badge bg-secondary">No cumple</span>
                                                    )}
                                                </td>
                                            </tr>
                                        ))}
                                        </tbody>
                                    </table>

                                    {candidato.tieneCv ? (
                                        <button
                                            className="btn btn-outline-success btn-sm"
                                            onClick={() => verCv(candidato.cedulaOferente)}
                                        >
                                            Ver currículo PDF
                                        </button>
                                    ) : (
                                        <div className="alert alert-warning mb-0">
                                            Este oferente no tiene cv cargado.
                                        </div>
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