import { createContext, useContext, useEffect, useState } from "react";
import { api, guardarToken, limpiarToken, obtenerToken } from "./api.js";

const AuthContext = createContext(null);
const SESSION_KEY = "bolsaEmpleoSession";

export function AuthProvider({ children }) {
    const [session, setSession] = useState(() => {
        const guardada = localStorage.getItem("bolsaEmpleoSession");
        return guardada ? JSON.parse(guardada) : null;
    });

    const [loading, setLoading] = useState(true);

    function guardarSesion(data) {
        setSession(data);
        localStorage.setItem(SESSION_KEY, JSON.stringify(data));
    }

    function limpiarSesion() {
        setSession(null);
        localStorage.removeItem(SESSION_KEY);
        limpiarToken();
    }

    async function cargarSesion() {
        if (!obtenerToken()) {
            limpiarSesion();
            setLoading(false);
            return;
        }

        try {
            const data = await api.get("/auth/me");
            guardarSesion(data);
        } catch (error) {
            limpiarSesion();
        } finally {
            setLoading(false);
        }
    }

    async function login(usuario, password) {
        const data = await api.post("/auth/login", {
            usuario,
            password
        });

        guardarToken(data.token);
        guardarSesion(data);

        return data;
    }

    async function logout() {
        try {
            await api.post("/auth/logout");
        } finally {
            limpiarSesion();
        }
    }

    useEffect(() => {
        cargarSesion();
    }, []);

    return (
        <AuthContext.Provider value={{ session, loading, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    return useContext(AuthContext);
}