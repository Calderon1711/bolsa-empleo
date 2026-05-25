import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../AuthContext.jsx";

export default function AdminDashboard() {
    const { session, logout } = useAuth();
    const navigate = useNavigate();

    async function cerrarSesion() {
        await logout();
        navigate("/login");
    }

    return (
        <div className="container mt-5">
            <h1>Panel administrador</h1>

            <div className="alert alert-info">
                Bienvenido, <strong>{session?.nombre}</strong>
            </div>

            <div className="d-grid gap-2 col-md-6">
                <Link className="btn btn-primary" to="/admin/empresas-pendientes">
                    Ver empresas pendientes
                </Link>

                <Link className="btn btn-primary" to="/admin/oferentes-pendientes">
                    Ver oferentes pendientes
                </Link>

                <button className="btn btn-danger" onClick={cerrarSesion}>
                    Cerrar sesión
                </button>
            </div>
        </div>
    );
}