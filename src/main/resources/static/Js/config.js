// config.js # Configuraciones globales
const APP_CONFIG = {
    // Rutas de imágenes
    DEFAULT_IMAGE_PATH: '/imagenes/moto-default.jpg',
    IMG_BASE_PATH: '/motos/',
    
    // API Endpoints
    API_BASE: '/api',
    ALERTAS_ENDPOINT: '/api/alertas/usuario/',
    MOTOS_ENDPOINT: '/api/motos',
    IMAGEN_ENDPOINT: '/api/motos/subir-imagen',
    
    // Tiempos
    CARRUSEL_INTERVAL: 5000, // 5 segundos
    RESIZE_DEBOUNCE: 250,
    
    // Colores principales
    COLOR_PRINCIPAL: '#1E2333',
    
    // Estado global
    currentMotoData: null,
    usuarioId: window.usuarioIdBackend || null
};

// Exportar para otros scripts
window.APP_CONFIG = APP_CONFIG;