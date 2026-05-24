import { Route, Routes } from "react-router-dom";
import Inicio from "./pages/Inicio.jsx";
import Login from "./pages/Login.jsx";
import AdminDashboard from "./pages/AdminDashboard.jsx";
import EmpresaDashboard from "./pages/EmpresaDashboard.jsx";
import OferenteDashboard from "./pages/OferenteDashboard.jsx";
import RegistroEmpresa from "./pages/RegistroEmpresa.jsx";
import RegistroOferente from "./pages/RegistroOferente.jsx";
import RequireAuth from "./components/RequireAuth.jsx";

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
      </Routes>
  );
}