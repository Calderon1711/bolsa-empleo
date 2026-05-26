export const API_URL = "http://localhost:8080/api";
const TOKEN_KEY = "bolsaEmpleoToken";

export function obtenerToken() {
    return localStorage.getItem(TOKEN_KEY);
}

export function guardarToken(token) {
    if (token) {
        localStorage.setItem(TOKEN_KEY, token);
    }
}

export function limpiarToken() {
    localStorage.removeItem(TOKEN_KEY);
}

async function request(path, options = {}) {
    const token = obtenerToken();

    const response = await fetch(`${API_URL}${path}`, {
        ...options,
        headers: {
            ...(options.body instanceof FormData
                ? {}
                : { "Content-Type": "application/json" }),

            ...(token ? { Authorization: `Bearer ${token}` } : {}),

            ...(options.headers || {})
        }
    });

    const contentType = response.headers.get("content-type") || "";
    const isJson = contentType.includes("application/json");

    const body = isJson ? await response.json() : await response.text();

    if (!response.ok) {
        console.error("Error API:", {
            status: response.status,
            path,
            body
        });

        const mensaje = isJson
            ? body.mensaje || body.error || `Error HTTP ${response.status}`
            : body || `Error HTTP ${response.status}`;

        throw new Error(mensaje);
    }

    return body.data ?? body;
}

export const api = {
    get: (path) => request(path),

    post: (path, data = {}) =>
        request(path, {
            method: "POST",
            body: JSON.stringify(data)
        }),

    put: (path, data = {}) =>
        request(path, {
            method: "PUT",
            body: JSON.stringify(data)
        }),

    delete: (path) =>
        request(path, {
            method: "DELETE"
        }),

    form: (path, formData) =>
        request(path, {
            method: "POST",
            body: formData
        })
};