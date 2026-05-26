import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../AuthContext.jsx";

export default function OferenteDashboard() {
    const auth = useAuth();
    const navigate = useNavigate();

    const session = auth?.session;
    const logout = auth?.logout;

    async function cerrarSesion() {
        if (logout) {
            await logout();
        }

        navigate("/login");
    }

    return (
        <div className="container mt-5">
            <h1>Panel oferente</h1>

            <div className="alert alert-info">
                Bienvenido, <strong>{session?.nombre || "Oferente"}</strong>
            </div>

            <div className="d-grid gap-2 col-md-6">
                <Link className="btn btn-primary" to="/oferente/habilidades">
                    Administrar habilidades
                </Link>

                <Link className="btn btn-success" to="/oferente/cv">
                    Subir currículo PDF
                </Link>

                <button className="btn btn-danger" onClick={cerrarSesion}>
                    Cerrar sesión
                </button>
            </div>
        </div>
    );
}