import { useState } from "react";
import { Link } from "react-router-dom";
import { api } from "../api.js";

export default function RegistroEmpresa() {
    const [form, setForm] = useState({
        nombreEmpresa: "",
        correoEmpresa: "",
        passwordEmpresa: "",
        telefono: "",
        localizacion: "",
        descripcionEmpresa: ""
    });

    const [mensaje, setMensaje] = useState("");
    const [error, setError] = useState("");

    function cambiar(event) {
        setForm({
            ...form,
            [event.target.name]: event.target.value
        });
    }

    async function guardar(event) {
        event.preventDefault();
        setMensaje("");
        setError("");

        try {
            const respuesta = await api.post("/public/registro-empresa", form);

            setMensaje(respuesta.mensaje || "Registro enviado correctamente");

            setForm({
                nombreEmpresa: "",
                correoEmpresa: "",
                passwordEmpresa: "",
                telefono: "",
                localizacion: "",
                descripcionEmpresa: ""
            });
        } catch (e) {
            setError(e.message);
        }
    }

    return (
        <div className="container mt-5">
            <div className="card shadow">
                <div className="card-body">
                    <h2 className="mb-4">Registro de empresa</h2>

                    {mensaje && <div className="alert alert-success">{mensaje}</div>}
                    {error && <div className="alert alert-danger">{error}</div>}

                    <form onSubmit={guardar}>
                        <div className="mb-3">
                            <label className="form-label">Nombre de la empresa</label>
                            <input
                                className="form-control"
                                name="nombreEmpresa"
                                value={form.nombreEmpresa}
                                onChange={cambiar}
                                required
                            />
                        </div>

                        <div className="mb-3">
                            <label className="form-label">Correo electrónico</label>
                            <input
                                type="email"
                                className="form-control"
                                name="correoEmpresa"
                                value={form.correoEmpresa}
                                onChange={cambiar}
                                required
                            />
                        </div>

                        <div className="mb-3">
                            <label className="form-label">Contraseña</label>
                            <input
                                type="password"
                                className="form-control"
                                name="passwordEmpresa"
                                value={form.passwordEmpresa}
                                onChange={cambiar}
                                required
                            />
                        </div>

                        <div className="mb-3">
                            <label className="form-label">Teléfono</label>
                            <input
                                className="form-control"
                                name="telefono"
                                value={form.telefono}
                                onChange={cambiar}
                                required
                            />
                        </div>

                        <div className="mb-3">
                            <label className="form-label">Localización</label>
                            <input
                                className="form-control"
                                name="localizacion"
                                value={form.localizacion}
                                onChange={cambiar}
                                placeholder="Ejemplo: San José, Costa Rica"
                                required
                            />
                        </div>

                        <div className="mb-3">
                            <label className="form-label">Descripción de la empresa</label>
                            <textarea
                                className="form-control"
                                name="descripcionEmpresa"
                                value={form.descripcionEmpresa}
                                onChange={cambiar}
                                rows="4"
                                required
                            />
                        </div>

                        <button className="btn btn-primary me-2" type="submit">
                            Registrar empresa
                        </button>

                        <Link className="btn btn-secondary" to="/">
                            Volver
                        </Link>
                    </form>
                </div>
            </div>
        </div>
    );
}