import { Link } from "react-router-dom";

export default function Inicio() {
    return (
        <div className="container mt-5">
            <div className="text-center">
                <h1>Bolsa de Empleo</h1>

                <p className="lead">
                    Plataforma para empresas, oferentes y administración.
                </p>

                <div className="mt-4">
                    <Link className="btn btn-primary me-2" to="/login">
                        Iniciar sesión
                    </Link>

                    <Link className="btn btn-outline-primary me-2" to="/registro-empresa">
                        Registrar empresa
                    </Link>

                    <Link className="btn btn-outline-secondary" to="/registro-oferente">
                        Registrar oferente
                    </Link>
                </div>
            </div>
        </div>
    );
}