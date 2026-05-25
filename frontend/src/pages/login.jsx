import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../AuthContext.jsx";

export default function Login() {
    const [usuario, setUsuario] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");

    const { login } = useAuth();
    const navigate = useNavigate();

    async function ingresar(event) {
        event.preventDefault();
        setError("");

        try {
            const session = await login(usuario, password);

            if (session.tipo === "ADMIN") {
                navigate("/admin");
            } else if (session.tipo === "EMPRESA") {
                navigate("/empresa");
            } else if (session.tipo === "OFERENTE") {
                navigate("/oferente");
            } else {
                navigate("/");
            }

        } catch (e) {
            setError(e.message || "Ocurrió un error");
        }
    }

    return (
        <div className="container mt-5">
            <div className="card mx-auto shadow" style={{ maxWidth: "430px" }}>
                <div className="card-body">
                    <h3 className="text-center mb-4">Iniciar sesión</h3>

                    {error && (
                        <div className="alert alert-danger">
                            {error}
                        </div>
                    )}

                    <form onSubmit={ingresar}>
                        <div className="mb-3">
                            <label className="form-label">
                                Correo electrónico o identificación
                            </label>

                            <input
                                type="text"
                                className="form-control"
                                value={usuario}
                                onChange={(e) => setUsuario(e.target.value)}
                                required
                            />
                        </div>

                        <div className="mb-3">
                            <label className="form-label">
                                Contraseña
                            </label>

                            <input
                                type="password"
                                className="form-control"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                            />
                        </div>

                        <button type="submit" className="btn btn-primary w-100">
                            Ingresar
                        </button>
                    </form>
                </div>
            </div>
        </div>
    );
}