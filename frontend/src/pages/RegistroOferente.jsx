import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { api } from "../api.js";

export default function RegistroOferente() {
    const [form, setForm] = useState({
        cedulaOferente: "",
        nombreOferente: "",
        primerApellido: "",
        correoOferente: "",
        passwordOferente: "",
        telefonoOferente: "",
        lugarResidencia: "",
        idNacionalidad: ""
    });

    const [nacionalidades, setNacionalidades] = useState([]);
    const [mensaje, setMensaje] = useState("");
    const [error, setError] = useState("");

    useEffect(() => {
        cargarNacionalidades();
    }, []);

    async function cargarNacionalidades() {
        try {
            const data = await api.get("/public/nacionalidades");
            setNacionalidades(data);
        } catch (e) {
            setError("No se pudieron cargar las nacionalidades");
        }
    }

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
            const respuesta = await api.post("/public/registro-oferente", form);

            setMensaje(respuesta.mensaje || "Registro enviado correctamente");

            setForm({
                cedulaOferente: "",
                nombreOferente: "",
                primerApellido: "",
                correoOferente: "",
                passwordOferente: "",
                telefonoOferente: "",
                lugarResidencia: "",
                idNacionalidad: ""
            });
        } catch (e) {
            setError(e.message);
        }
    }

    return (
        <div className="container mt-5">
            <div className="card shadow">
                <div className="card-body">
                    <h2 className="mb-4">Registro de oferente</h2>

                    {mensaje && <div className="alert alert-success">{mensaje}</div>}
                    {error && <div className="alert alert-danger">{error}</div>}

                    <form onSubmit={guardar}>
                        <div className="mb-3">
                            <label className="form-label">Cédula</label>
                            <input
                                className="form-control"
                                name="cedulaOferente"
                                value={form.cedulaOferente}
                                onChange={cambiar}
                                required
                            />
                        </div>

                        <div className="mb-3">
                            <label className="form-label">Nombre</label>
                            <input
                                className="form-control"
                                name="nombreOferente"
                                value={form.nombreOferente}
                                onChange={cambiar}
                                required
                            />
                        </div>

                        <div className="mb-3">
                            <label className="form-label">Primer apellido</label>
                            <input
                                className="form-control"
                                name="primerApellido"
                                value={form.primerApellido}
                                onChange={cambiar}
                                required
                            />
                        </div>

                        <div className="mb-3">
                            <label className="form-label">Correo electrónico</label>
                            <input
                                type="email"
                                className="form-control"
                                name="correoOferente"
                                value={form.correoOferente}
                                onChange={cambiar}
                                required
                            />
                        </div>

                        <div className="mb-3">
                            <label className="form-label">Contraseña</label>
                            <input
                                type="password"
                                className="form-control"
                                name="passwordOferente"
                                value={form.passwordOferente}
                                onChange={cambiar}
                                required
                            />
                        </div>

                        <div className="mb-3">
                            <label className="form-label">Teléfono</label>
                            <input
                                className="form-control"
                                name="telefonoOferente"
                                value={form.telefonoOferente}
                                onChange={cambiar}
                                required
                            />
                        </div>

                        <div className="mb-3">
                            <label className="form-label">Lugar de residencia</label>
                            <input
                                className="form-control"
                                name="lugarResidencia"
                                value={form.lugarResidencia}
                                onChange={cambiar}
                                required
                            />
                        </div>

                        <div className="mb-3">
                            <label className="form-label">Nacionalidad</label>
                            <select
                                className="form-select"
                                name="idNacionalidad"
                                value={form.idNacionalidad}
                                onChange={cambiar}
                                required
                            >
                                <option value="">Seleccione una nacionalidad</option>

                                {nacionalidades.map((nacionalidad) => (
                                    <option
                                        key={nacionalidad.idNacionalidad}
                                        value={nacionalidad.idNacionalidad}
                                    >
                                        {nacionalidad.nombreNacionalidad}
                                    </option>
                                ))}
                            </select>
                        </div>

                        <button className="btn btn-primary me-2" type="submit">
                            Registrar oferente
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