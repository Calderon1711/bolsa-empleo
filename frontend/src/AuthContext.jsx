import { createContext, useContext, useEffect, useState } from "react";
import { api } from "./api.js";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [session, setSession] = useState(null);
    const [loading, setLoading] = useState(true);

    async function cargarSesion() {
        try {
            const data = await api.get("/auth/me");
            setSession(data);
        } catch {
            setSession(null);
        } finally {
            setLoading(false);
        }
    }

    async function login(correo, password) {
        const data = await api.post("/auth/login", {
            correo,
            password
        });

        setSession(data);
        return data;
    }

    async function logout() {
        await api.post("/auth/logout");
        setSession(null);
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