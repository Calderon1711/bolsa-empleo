import { useNavigate } from "react-router-dom";
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

            <button className="btn btn-danger" onClick={cerrarSesion}>
                Cerrar sesión
            </button>
        </div>
    );
}