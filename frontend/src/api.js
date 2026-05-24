const API_URL = "http://localhost:8080/api";

async function request(path, options = {}) {
    const response = await fetch(`${API_URL}${path}`, {
        credentials: "include",
        headers: {
            ...(options.body instanceof FormData
                ? {}
                : {"Content-Type": "application/json"}),
            ...(options.headers || {})
        },
        ...options
    });

    const contentType = response.headers.get("content-type") || "";
    const isJson = contentType.includes("application/json");

    const body = isJson ? await response.json() : await response.text();

    if (!response.ok) {
        const message = isJson ? body.mensaje || "Ocurrio un error" : body;
        throw new Error(message);
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

    form: (path, formData) =>
        request(path, {
            method: "POST",
            body: formData
        })
};