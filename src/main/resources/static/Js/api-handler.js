// api-handler.js # Comunicación con APIs Comunicación: Enviar/Obtener/Eliminar data del servidor.
const ApiHandler = {
    // Obtener token CSRF
    getCsrfToken: function() {
        const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
        return { csrfToken, csrfHeader };
    },
    
    // =========================================================
    // 1. Guardar/Actualizar moto (Se mantiene igual)
    // =========================================================
        guardarMoto: async function(formData) {
        try {
            const { csrfToken, csrfHeader } = this.getCsrfToken();
            const token = localStorage.getItem('jwtToken'); // <-- Obtener JWT

            // Determinar método y endpoint (POST para nuevo registro)
            const isEditing = formData.idMoto != null;
            const endpoint = isEditing ? `${APP_CONFIG.MOTOS_ENDPOINT}/${formData.idMoto}` : APP_CONFIG.MOTOS_ENDPOINT;
            const method = isEditing ? 'PUT' : 'POST';

            // CONSTRUIR HEADERS
            const headers = {  
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken,  // Header CSRF
                'Authorization': `Bearer ${token}` // <-- ¡HEADER JWT AÑADIDO!
            };

            const response = await fetch(endpoint, { // Usar endpoint y method dinámicos
                method: method,
                headers: headers, 
                body: JSON.stringify(formData)
            });
            
            // ... (El resto del manejo de respuesta se mantiene igual)
            if (response.ok) {
                return await response.json();
            } else {
                let errorText = await response.text();
                throw new Error(`Error al guardar la moto: ${response.status} - ${errorText.substring(0, 100)}...`);
            }
        } catch (error) {
            console.error('Error API:', error);
            throw error;
        }
    },

    // =========================================================
    // 2. Obtener lista de motos por usuario (NUEVA FUNCIÓN)
    // =========================================================
    obtenerMotosPorUsuario: async function(usuarioId) {
        try {
            // Endpoint: GET /api/motos/usuario/{usuarioId}
            const endpoint = `${APP_CONFIG.MOTOS_ENDPOINT}/usuario/${usuarioId}`; 
            
            const response = await fetch(endpoint, {
                method: 'GET',
                // No necesita CSRF para GET
            });

            if (response.ok) {
                // Debe devolver un Array de MotoDTOs
                return await response.json(); 
            } else {
                 let errorText = await response.text();
                 throw new Error(`Error al obtener motos: ${response.status} - ${errorText.substring(0, 100)}...`);
            }
        } catch (error) {
            console.error('Error API (obtenerMotosPorUsuario):', error);
            throw error;
        }
    },
    
    // =========================================================
    // 3. Eliminar moto (NUEVA FUNCIÓN)
    // =========================================================
    eliminarMoto: async function(motoId) {
        try {
            const { csrfToken, csrfHeader } = this.getCsrfToken();
            
            // Endpoint: DELETE /api/motos/{motoId}
            const endpoint = `${APP_CONFIG.MOTOS_ENDPOINT}/${motoId}`;
            
            const response = await fetch(endpoint, {
                method: 'DELETE',
                headers: { 
                    [csrfHeader]: csrfToken // Necesita CSRF para DELETE
                }
            });

            if (response.ok) {
                // No devuelve cuerpo, solo confirmación de éxito (200/204)
                return true; 
            } else {
                 let errorText = await response.text();
                 throw new Error(`Error al eliminar la moto: ${response.status} - ${errorText.substring(0, 100)}...`);
            }
        } catch (error) {
            console.error('Error API (eliminarMoto):', error);
            throw error;
        }
    }
};

window.ApiHandler = ApiHandler;