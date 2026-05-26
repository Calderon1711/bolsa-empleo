import { Navigate } from "react-router-dom";
import { useAuth } from "../AuthContext.jsx";

export default function RequireAuth({ tipo, children }) {
    const { session, loading } = useAuth();

    if (loading) {
        return (
            <div className="container mt-5">
                <p>Cargando sesión...</p>
            </div>
        );
    }

    if (!session) {
        return <Navigate to="/login" replace />;
    }

    if (tipo && session.tipo !== tipo) {
        return (
            <div className="container mt-5">
                <div className="alert alert-danger">
                    No tiene permisos para entrar a esta sección.
                    <br />
                    Rol actual: {session.tipo}
                    <br />
                    Rol requerido: {tipo}
                </div>
            </div>
        );
    }

    return children;
}