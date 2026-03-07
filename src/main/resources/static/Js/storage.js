// storage.js # Manejo de localStorage/sessionStorage
const StorageManager = {
    // LocalStorage
    saveMotoData: function(datosMoto) {
        try {
            localStorage.setItem('motoUsuario', JSON.stringify(datosMoto));
            console.log('Datos guardados en localStorage');
            return true;
        } catch (e) {
            console.error('Error guardando en localStorage:', e);
            return false;
        }
    },
    
    getMotoData: function() {
        try {
            const data = localStorage.getItem('motoUsuario');
            return data ? JSON.parse(data) : null;
        } catch (e) {
            console.error('Error leyendo localStorage:', e);
            return null;
        }
    },
    
    clearMotoData: function() {
        localStorage.removeItem('motoUsuario');
        sessionStorage.removeItem('imagenMotoSubida');
        sessionStorage.removeItem('imagenTemp');
        console.log('Almacenamiento limpiado');
    },
    
    // SessionStorage para imágenes temporales
    saveTempImage: function(nombreArchivo) {
        sessionStorage.setItem('imagenMotoSubida', nombreArchivo);
    },
    
    getTempImage: function() {
        return sessionStorage.getItem('imagenMotoSubida');
    },
    
    clearTempImage: function() {
        sessionStorage.removeItem('imagenMotoSubida');
        sessionStorage.removeItem('imagenTemp');
    }
};

window.StorageManager = StorageManager;