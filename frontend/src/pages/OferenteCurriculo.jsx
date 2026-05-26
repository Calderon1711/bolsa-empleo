import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { api, API_URL, obtenerToken } from "../api.js";

export default function OferenteCurriculo() {
    const [cv, setCv] = useState(null);
    const [descripcion, setDescripcion] = useState("");
    const [archivo, setArchivo] = useState(null);

    const [mensaje, setMensaje] = useState("");
    const [error, setError] = useState("");
    const [cargando, setCargando] = useState(true);
    const [subiendo, setSubiendo] = useState(false);

    useEffect(() => {
        cargarCv();
    }, []);

    async function cargarCv() {
        setCargando(true);
        setError("");

        try {
            const data = await api.get("/oferente/cv");

            setCv(data);

            if (data && data.descripcionCurriculum) {
                setDescripcion(data.descripcionCurriculum);
            }
        } catch (e) {
            console.error(e);
            setError(e.message || "No se pudo cargar el currículo");
        } finally {
            setCargando(false);
        }
    }

    async function subirCurriculo(event) {
        event.preventDefault();

        setMensaje("");
        setError("");

        if (!archivo) {
            setError("Debe seleccionar un archivo PDF.");
            return;
        }

        if (archivo.type !== "application/pdf") {
            setError("Solo se permite subir archivos PDF.");
            return;
        }

        const formData = new FormData();
        formData.append("descripcion", descripcion);
        formData.append("archivo", archivo);

        try {
            setSubiendo(true);

            const data = await api.form("/oferente/cv", formData);

            setCv(data);
            setArchivo(null);
            setMensaje("Currículo subido correctamente.");

            const inputArchivo = document.getElementById("archivoCv");
            if (inputArchivo) {
                inputArchivo.value = "";
            }

        } catch (e) {
            console.error(e);
            setError(e.message || "No se pudo subir el currículo");
        } finally {
            setSubiendo(false);
        }
    }

    async function verCurriculo() {
        setError("");

        try {
            const token = obtenerToken();

            const response = await fetch(`${API_URL}/oferente/cv/archivo`, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });

            if (!response.ok) {
                throw new Error("No se pudo abrir el currículo");
            }

            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);

            window.open(url, "_blank");

        } catch (e) {
            console.error(e);
            setError(e.message || "No se pudo abrir el currículo");
        }
    }

    return (
        <div className="container mt-5">
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h2>Mi currículo</h2>

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
                    <h5 className="card-title">Currículo actual</h5>

                    {cargando ? (
                        <div className="alert alert-secondary">
                            Cargando currículo...
                        </div>
                    ) : cv ? (
                        <>
                            <p>
                                <strong>Descripción:</strong>{" "}
                                {cv.descripcionCurriculum || "Sin descripción"}
                            </p>

                            <p>
                                <strong>Fecha de carga:</strong>{" "}
                                {cv.fechaCreacionCurriculum || "Sin fecha"}
                            </p>

                            {cv.tieneArchivo && (
                                <button
                                    type="button"
                                    className="btn btn-outline-primary"
                                    onClick={verCurriculo}
                                >
                                    Ver currículo PDF
                                </button>
                            )}
                        </>
                    ) : (
                        <div className="alert alert-info">
                            Aún no ha subido un currículo.
                        </div>
                    )}
                </div>
            </div>

            <div className="card">
                <div className="card-body">
                    <h5 className="card-title">
                        {cv ? "Actualizar currículo" : "Subir currículo"}
                    </h5>

                    <form onSubmit={subirCurriculo}>
                        <div className="mb-3">
                            <label className="form-label">Descripción</label>

                            <textarea
                                className="form-control"
                                rows="3"
                                value={descripcion}
                                onChange={(e) => setDescripcion(e.target.value)}
                                placeholder="Ejemplo: Currículo actualizado con experiencia en Java, React y MySQL."
                            />
                        </div>

                        <div className="mb-3">
                            <label className="form-label">Archivo PDF</label>

                            <input
                                id="archivoCv"
                                type="file"
                                className="form-control"
                                accept="application/pdf"
                                onChange={(e) => setArchivo(e.target.files[0])}
                                required
                            />

                            <small className="text-muted">
                                Solo se permite formato PDF.
                            </small>
                        </div>

                        <button
                            type="submit"
                            className="btn btn-success"
                            disabled={subiendo}
                        >
                            {subiendo ? "Subiendo..." : "Guardar currículo"}
                        </button>
                    </form>
                </div>
            </div>
        </div>
    );
}