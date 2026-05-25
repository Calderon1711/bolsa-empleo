import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { api } from "../api.js";

export default function EmpresaPublicarPuesto() {
    const [caracteristicas, setCaracteristicas] = useState([]);

    const [titulo, setTitulo] = useState("");
    const [descripcion, setDescripcion] = useState("");
    const [salario, setSalario] = useState("");
    const [tipoPublicacion, setTipoPublicacion] = useState("PUBLICA");

    const [caracteristicaId, setCaracteristicaId] = useState("");
    const [nivelRequerido, setNivelRequerido] = useState("1");
    const [requisitos, setRequisitos] = useState([]);

    const [error, setError] = useState("");
    const [mensaje, setMensaje] = useState("");
    const [cargando, setCargando] = useState(false);

    const navigate = useNavigate();

    useEffect(() => {
        cargarCaracteristicas();
    }, []);

    async function cargarCaracteristicas() {
        try {
            const data = await api.get("/catalogo/caracteristicas");
            setCaracteristicas(Array.isArray(data) ? data : []);
        } catch (e) {
            console.error(e);
            setError(e.message || "No se pudieron cargar las características");
        }
    }

    function agregarRequisito() {
        setError("");

        if (!caracteristicaId) {
            setError("Debe seleccionar una característica.");
            return;
        }

        const yaExiste = requisitos.some(
            (req) => String(req.caracteristicaId) === String(caracteristicaId)
        );

        if (yaExiste) {
            setError("Esa característica ya fue agregada.");
            return;
        }

        const caracteristica = caracteristicas.find(
            (c) => String(c.id) === String(caracteristicaId)
        );

        if (!caracteristica) {
            setError("Característica no encontrada.");
            return;
        }

        const nuevoRequisito = {
            caracteristicaId: Number(caracteristicaId),
            caracteristicaNombre: caracteristica.nombre,
            nivelRequerido: Number(nivelRequerido)
        };

        setRequisitos([...requisitos, nuevoRequisito]);
        setCaracteristicaId("");
        setNivelRequerido("1");
    }

    function eliminarRequisito(idCaracteristica) {
        setRequisitos(
            requisitos.filter((req) => req.caracteristicaId !== idCaracteristica)
        );
    }

    async function publicarPuesto(event) {
        event.preventDefault();

        setError("");
        setMensaje("");

        if (requisitos.length === 0) {
            setError("Debe agregar al menos una característica requerida.");
            return;
        }

        try {
            setCargando(true);

            await api.post("/empresa/puestos", {
                titulo,
                descripcion,
                salario: Number(salario),
                tipoPublicacion,
                requisitos: requisitos.map((req) => ({
                    caracteristicaId: req.caracteristicaId,
                    nivelRequerido: req.nivelRequerido
                }))
            });

            setMensaje("Puesto publicado correctamente.");

            setTimeout(() => {
                navigate("/empresa/puestos");
            }, 700);

        } catch (e) {
            console.error(e);
            setError(e.message || "No se pudo publicar el puesto");
        } finally {
            setCargando(false);
        }
    }

    return (
        <div className="container mt-5">
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h2>Publicar nuevo puesto</h2>

                <Link className="btn btn-secondary" to="/empresa/puestos">
                    Volver a mis puestos
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

            <form onSubmit={publicarPuesto}>
                <div className="card mb-4">
                    <div className="card-body">
                        <h5 className="card-title">Datos del puesto</h5>

                        <div className="mb-3">
                            <label className="form-label">Título del puesto</label>
                            <input
                                type="text"
                                className="form-control"
                                value={titulo}
                                onChange={(e) => setTitulo(e.target.value)}
                                required
                            />
                        </div>

                        <div className="mb-3">
                            <label className="form-label">Descripción</label>
                            <textarea
                                className="form-control"
                                rows="4"
                                value={descripcion}
                                onChange={(e) => setDescripcion(e.target.value)}
                                required
                            />
                        </div>

                        <div className="row">
                            <div className="col-md-6 mb-3">
                                <label className="form-label">Salario ofrecido</label>
                                <input
                                    type="number"
                                    className="form-control"
                                    value={salario}
                                    onChange={(e) => setSalario(e.target.value)}
                                    min="1"
                                    required
                                />
                            </div>

                            <div className="col-md-6 mb-3">
                                <label className="form-label">Tipo de publicación</label>
                                <select
                                    className="form-select"
                                    value={tipoPublicacion}
                                    onChange={(e) => setTipoPublicacion(e.target.value)}
                                >
                                    <option value="PUBLICA">Pública</option>
                                    <option value="PRIVADA">Privada</option>
                                </select>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="card mb-4">
                    <div className="card-body">
                        <h5 className="card-title">Características requeridas</h5>

                        <div className="row g-2 align-items-end">
                            <div className="col-md-7">
                                <label className="form-label">Característica</label>
                                <select
                                    className="form-select"
                                    value={caracteristicaId}
                                    onChange={(e) => setCaracteristicaId(e.target.value)}
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
                                <label className="form-label">Nivel requerido</label>
                                <select
                                    className="form-select"
                                    value={nivelRequerido}
                                    onChange={(e) => setNivelRequerido(e.target.value)}
                                >
                                    <option value="1">1 - Básico</option>
                                    <option value="2">2</option>
                                    <option value="3">3 - Intermedio</option>
                                    <option value="4">4</option>
                                    <option value="5">5 - Avanzado</option>
                                </select>
                            </div>

                            <div className="col-md-2">
                                <button
                                    type="button"
                                    className="btn btn-outline-primary w-100"
                                    onClick={agregarRequisito}
                                >
                                    Agregar
                                </button>
                            </div>
                        </div>

                        <hr />

                        {requisitos.length === 0 ? (
                            <div className="alert alert-info">
                                Aún no ha agregado características requeridas.
                            </div>
                        ) : (
                            <table className="table table-bordered align-middle">
                                <thead>
                                <tr>
                                    <th>Característica</th>
                                    <th>Nivel requerido</th>
                                    <th>Acción</th>
                                </tr>
                                </thead>

                                <tbody>
                                {requisitos.map((req) => (
                                    <tr key={req.caracteristicaId}>
                                        <td>{req.caracteristicaNombre}</td>
                                        <td>{req.nivelRequerido}</td>
                                        <td>
                                            <button
                                                type="button"
                                                className="btn btn-outline-danger btn-sm"
                                                onClick={() => eliminarRequisito(req.caracteristicaId)}
                                            >
                                                Quitar
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        )}
                    </div>
                </div>

                <button
                    className="btn btn-success"
                    type="submit"
                    disabled={cargando}
                >
                    {cargando ? "Publicando..." : "Publicar puesto"}
                </button>
            </form>
        </div>
    );
}