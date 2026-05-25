import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../AuthContext.jsx";


export default function EmpresaDashboard() {
    const { session, logout } = useAuth();
    const navigate = useNavigate();

    async function cerrarSesion() {
        await logout();
        navigate("/login");
    }

    return (
        <div className="container mt-5">
            <h1>Panel empresa</h1>

            <div className="alert alert-info">
                Bienvenido, <strong>{session?.nombre}</strong>
            </div>

            <Link className="btn btn-primary" to="/empresa/puestos">
                Mis puestos publicados
            </Link>

            <Link className="btn btn-success" to="/empresa/puestos/nuevo">
                Publicar nuevo puesto
            </Link>

            <button className="btn btn-danger" onClick={cerrarSesion}>
                Cerrar sesión
            </button>
        </div>
    );
}