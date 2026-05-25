import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { api } from "../api.js";

export default function AdminEmpresasPendientes() {
    const [empresas, setEmpresas] = useState([]);
    const [mensaje, setMensaje] = useState("");
    const [error, setError] = useState("");

    useEffect(() => {
        cargarEmpresas();
    }, []);

    async function cargarEmpresas() {
        try {
            const data = await api.get("/admin/empresas-pendientes");
            setEmpresas(data);
        } catch (e) {
            setError(e.message);
        }
    }

    async function aprobarEmpresa(idEmpresa) {
        setMensaje("");
        setError("");

        try {
            const respuesta = await api.post(`/admin/empresas/${idEmpresa}/aprobar`);
            setMensaje(respuesta.mensaje || "Empresa aprobada correctamente");
            await cargarEmpresas();
        } catch (e) {
            setError(e.message);
        }
    }

    return (
        <div className="container mt-5">
            <h2>Empresas pendientes</h2>

            <Link className="btn btn-secondary mb-3" to="/admin">
                Volver al panel
            </Link>

            {mensaje && <div className="alert alert-success">{mensaje}</div>}
            {error && <div className="alert alert-danger">{error}</div>}

            {empresas.length === 0 ? (
                <div className="alert alert-info">
                    No hay empresas pendientes de aprobación.
                </div>
            ) : (
                <div className="table-responsive">
                    <table className="table table-bordered table-striped align-middle">
                        <thead>
                        <tr>
                            <th>Empresa</th>
                            <th>Correo</th>
                            <th>Teléfono</th>
                            <th>Localización</th>
                            <th>Fecha registro</th>
                            <th>Acción</th>
                        </tr>
                        </thead>

                        <tbody>
                        {empresas.map((empresa) => (
                            <tr key={empresa.idEmpresa}>
                                <td>{empresa.nombreEmpresa}</td>
                                <td>{empresa.correoEmpresa}</td>
                                <td>{empresa.telefono}</td>
                                <td>{empresa.localizacion}</td>
                                <td>{empresa.fechaRegistroEmpresa}</td>
                                <td>
                                    <button
                                        className="btn btn-success btn-sm"
                                        onClick={() => aprobarEmpresa(empresa.idEmpresa)}
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