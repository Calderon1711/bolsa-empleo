import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { api } from "../api.js";

export default function OferenteHabilidades() {
    const [caracteristicas, setCaracteristicas] = useState([]);
    const [habilidades, setHabilidades] = useState([]);

    const [caracteristicaId, setCaracteristicaId] = useState("");
    const [nivel, setNivel] = useState("1");

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
            const catalogo = await api.get("/catalogo/caracteristicas");
            const misHabilidades = await api.get("/oferente/habilidades");

            setCaracteristicas(Array.isArray(catalogo) ? catalogo : []);
            setHabilidades(Array.isArray(misHabilidades) ? misHabilidades : []);
        } catch (e) {
            console.error(e);
            setError(e.message || "No se pudieron cargar las habilidades");
        } finally {
            setCargando(false);
        }
    }

    async function guardarHabilidad(event) {
        event.preventDefault();

        setMensaje("");
        setError("");

        if (!caracteristicaId) {
            setError("Debe seleccionar una característica.");
            return;
        }

        try {
            await api.post("/oferente/habilidades", {
                caracteristicaId: Number(caracteristicaId),
                nivel: Number(nivel)
            });

            setMensaje("Habilidad guardada correctamente.");
            setCaracteristicaId("");
            setNivel("1");

            await cargarDatos();
        } catch (e) {
            console.error(e);
            setError(e.message || "No se pudo guardar la habilidad");
        }
    }

    async function eliminarHabilidad(idCaracteristica) {
        setMensaje("");
        setError("");

        try {
            const respuesta = await api.delete(`/oferente/habilidades/${idCaracteristica}`);

            setMensaje(respuesta.mensaje || "Habilidad eliminada correctamente.");
            await cargarDatos();
        } catch (e) {
            console.error(e);
            setError(e.message || "No se pudo eliminar la habilidad");
        }
    }

    return (
        <div className="container mt-5">
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h2>Mis habilidades</h2>

                <Link className="btn btn-secondary" to="/oferente">
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
                    <h5 className="card-title">Agregar o actualizar habilidad</h5>

                    <form onSubmit={guardarHabilidad}>
                        <div className="row g-2 align-items-end">
                            <div className="col-md-7">
                                <label className="form-label">Característica</label>

                                <select
                                    className="form-select"
                                    value={caracteristicaId}
                                    onChange={(e) => setCaracteristicaId(e.target.value)}
                                    required
                                >
                                    <option value="">Seleccione una característica</option>

                                    {caracteristicas.map((caracteristica) => (
                                        <option key={caracteristica.id} value={caracteristica.id}>
                                            {caracteristica.padreNombre
                                                ? `${caracteristica.padreNombre} / ${caracteristica.nombre}`
                                                : caracteristica.nombre}
                                        </option>
                                    ))}
                                </select>
                            </div>

                            <div className="col-md-3">
                                <label className="form-label">Nivel</label>

                                <select
                                    className="form-select"
                                    value={nivel}
                                    onChange={(e) => setNivel(e.target.value)}
                                >
                                    <option value="1">1 - Básico</option>
                                    <option value="2">2</option>
                                    <option value="3">3 - Intermedio</option>
                                    <option value="4">4</option>
                                    <option value="5">5 - Avanzado</option>
                                </select>
                            </div>

                            <div className="col-md-2">
                                <button className="btn btn-primary w-100">
                                    Guardar
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>

            <div className="card">
                <div className="card-body">
                    <h5 className="card-title">Habilidades registradas</h5>

                    {cargando ? (
                        <div className="alert alert-secondary">
                            Cargando habilidades...
                        </div>
                    ) : habilidades.length === 0 ? (
                        <div className="alert alert-info">
                            Aún no ha registrado habilidades.
                        </div>
                    ) : (
                        <table className="table table-bordered align-middle">
                            <thead>
                            <tr>
                                <th>Categoría</th>
                                <th>Característica</th>
                                <th>Nivel</th>
                                <th>Acción</th>
                            </tr>
                            </thead>

                            <tbody>
                            {habilidades.map((habilidad) => (
                                <tr key={habilidad.id}>
                                    <td>{habilidad.padreNombre || "Principal"}</td>
                                    <td>{habilidad.caracteristicaNombre}</td>
                                    <td>{habilidad.nivel}</td>
                                    <td>
                                        <button
                                            className="btn btn-outline-danger btn-sm"
                                            onClick={() => eliminarHabilidad(habilidad.caracteristicaId)}
                                        >
                                            Eliminar
                                        </button>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    )}
                </div>
            </div>
        </div>
    );
}