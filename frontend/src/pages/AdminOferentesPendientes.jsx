import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { api } from "../api.js";

export default function AdminOferentesPendientes() {
    const [oferentes, setOferentes] = useState([]);
    const [mensaje, setMensaje] = useState("");
    const [error, setError] = useState("");

    useEffect(() => {
        cargarOferentes();
    }, []);

    async function cargarOferentes() {
        try {
            const data = await api.get("/admin/oferentes-pendientes");
            setOferentes(data);
        } catch (e) {
            setError(e.message);
        }
    }

    async function aprobarOferente(cedula) {
        setMensaje("");
        setError("");

        try {
            const respuesta = await api.post(`/admin/oferentes/${cedula}/aprobar`);
            setMensaje(respuesta.mensaje || "Oferente aprobado correctamente");
            await cargarOferentes();
        } catch (e) {
            setError(e.message);
        }
    }

    return (
        <div className="container mt-5">
            <h2>Oferentes pendientes</h2>

            <Link className="btn btn-secondary mb-3" to="/admin">
                Volver al panel
            </Link>

            {mensaje && <div className="alert alert-success">{mensaje}</div>}
            {error && <div className="alert alert-danger">{error}</div>}

            {oferentes.length === 0 ? (
                <div className="alert alert-info">
                    No hay oferentes pendientes de aprobación.
                </div>
            ) : (
                <div className="table-responsive">
                    <table className="table table-bordered table-striped align-middle">
                        <thead>
                        <tr>
                            <th>Cédula</th>
                            <th>Nombre</th>
                            <th>Apellido</th>
                            <th>Correo</th>
                            <th>Teléfono</th>
                            <th>Residencia</th>
                            <th>Nacionalidad</th>
                            <th>Acción</th>
                        </tr>
                        </thead>

                        <tbody>
                        {oferentes.map((oferente) => (
                            <tr key={oferente.cedulaOferente}>
                                <td>{oferente.cedulaOferente}</td>
                                <td>{oferente.nombreOferente}</td>
                                <td>{oferente.primerApellido}</td>
                                <td>{oferente.correoOferente}</td>
                                <td>{oferente.telefonoOferente}</td>
                                <td>{oferente.lugarResidencia}</td>
                                <td>{oferente.nacionalidad}</td>
                                <td>
                                    <button
                                        className="btn btn-success btn-sm"
                                        onClick={() => aprobarOferente(oferente.cedulaOferente)}
                                    >
                                        Aprobar
                                    </button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
}