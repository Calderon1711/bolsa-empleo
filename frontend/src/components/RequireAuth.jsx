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
        return <Navigate to="/" replace />;
    }

    return children;
}