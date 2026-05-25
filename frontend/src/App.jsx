import { Route, Routes } from "react-router-dom";
import Inicio from "./pages/inicio.jsx";
import Login from "./pages/login.jsx";
import AdminDashboard from "./pages/AdminDashboard.jsx";
import EmpresaDashboard from "./pages/EmpresaDashboard.jsx";
import OferenteDashboard from "./pages/OferenteDashboard.jsx";
import RegistroEmpresa from "./pages/RegistroEmpresa.jsx";
import RegistroOferente from "./pages/RegistroOferente.jsx";
import RequireAuth from "./components/RequireAuth.jsx";
import AdminEmpresasPendientes from "./pages/AdminEmpresasPendientes.jsx";
import AdminOferentesPendientes from "./pages/AdminOferentesPendientes.jsx";
import AdminCaracteristicas from "./pages/AdminCaracteristicas.jsx";
import EmpresaPuestos from "./pages/EmpresaPuestos";
import EmpresaPublicarPuesto from "./pages/EmpresaPublicarPuesto.jsx";

export default function App() {
    return (
        <Routes>
            <Route path="/" element={<Inicio />} />

            <Route path="/login" element={<Login />} />

            <Route path="/registro-empresa" element={<RegistroEmpresa />} />

            <Route path="/registro-oferente" element={<RegistroOferente />} />

            <Route
                path="/admin"
                element={
                    <RequireAuth tipo="ADMIN">
                        <AdminDashboard />
                    </RequireAuth>
                }
            />

            <Route
                path="/admin/empresas-pendientes"
                element={
                    <RequireAuth tipo="ADMIN">
                        <AdminEmpresasPendientes />
                    </RequireAuth>
                }
            />

            <Route
                path="/admin/oferentes-pendientes"
                element={
                    <RequireAuth tipo="ADMIN">
                        <AdminOferentesPendientes />
                    </RequireAuth>
                }
            />

            <Route
                path="/admin/caracteristicas"
                element={
                    <RequireAuth tipo="ADMIN">
                        <AdminCaracteristicas />
                    </RequireAuth>
                }
            />

            <Route
                path="/empresa"
                element={
                    <RequireAuth tipo="EMPRESA">
                        <EmpresaDashboard />
                    </RequireAuth>
                }
            />

            <Route
                path="/oferente"
                element={
                    <RequireAuth tipo="OFERENTE">
                        <OferenteDashboard />
                    </RequireAuth>
                }
            />

            <Route
                path="/empresa/puestos"
                element={
                    <RequireAuth tipo="EMPRESA">
                        <EmpresaPuestos />
                    </RequireAuth>
                }
            />

            <Route
                path="/empresa/puestos/nuevo"
                element={
                    <RequireAuth tipo="EMPRESA">
                        <EmpresaPublicarPuesto />
                    </RequireAuth>
                }
            />
            
        </Routes>
    );
}