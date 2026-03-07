// ui-moto.js  # Interfaz de gestión de motos (Funciones que solo manipulan el DOM) UI/DOM: Muestra detalles, precarga/limpia formularios, cambia vistas.
const MotoUI = {
    // Mostrar vista de detalle
    mostrarVistaDetalle: function(datosMoto) {
        console.log('Mostrando vista de detalle');
        
        // Ocultar formulario, mostrar detalle
        this.toggleView('moto-manager__registro', 'hidden');
        this.toggleView('moto-manager__detalle', 'active');
        
        // Llenar campos
        this.actualizarCamposDetalle(datosMoto);
    },
    
    // Mostrar vista de registro/edición
    mostrarVistaRegistro: function() {
        console.log('Mostrando vista de registro');
        
        this.toggleView('moto-manager__registro', 'active');
        this.toggleView('moto-manager__detalle', 'hidden');
        
        if (!APP_CONFIG.currentMotoData) {
            this.limpiarFormulario();
        }
    },
    
    // Helper para cambiar vistas
    toggleView: function(elementId, action) {
        const element = document.getElementById(elementId);
        if (!element) return;
        
        if (action === 'active') {
            element.classList.remove('hidden');
            element.classList.add('active');
        } else if (action === 'hidden') {
            element.classList.remove('active');
            element.classList.add('hidden');
        }
    },
    
    // Actualizar campos de detalle
    actualizarCamposDetalle: function(datosMoto) {
        const campos = {
            'detalle-placa': datosMoto.placaMoto,
            'detalle-marca': datosMoto.marcaMoto,
            'detalle-modelo': datosMoto.modeloMoto,
            'detalle-color': datosMoto.colorMoto,
            'detalle-kilometraje': datosMoto.kilometraje ? 
                datosMoto.kilometraje.toLocaleString() + ' km' : '0 km',
            'detalle-tecnomecanica': datosMoto.tecnomecanica,
            'detalle-soat': datosMoto.soatMoto
        };
        
        // Actualizar campos de texto
        for (const [id, valor] of Object.entries(campos)) {
            const elemento = document.getElementById(id);
            if (elemento && valor) elemento.textContent = valor;
        }
        
        // Actualizar imagen
        this.actualizarImagenDetalle(datosMoto.imagenMoto);
    },
    
    // Actualizar imagen en vista detalle
    actualizarImagenDetalle: function(imagenUrl) {
        const detalleImagen = document.getElementById('detalle-imagen');
        if (!detalleImagen) return;
        
        let urlFinal = imagenUrl;
        
        // Si no hay imagen, usar placeholder
        if (!urlFinal || urlFinal === '' || urlFinal === 'moto-default.jpg') {
            urlFinal = APP_CONFIG.DEFAULT_IMAGE_PATH;
        }
        // Si es una ruta relativa sin /, agregar /img/
        else if (!urlFinal.startsWith('http') && !urlFinal.startsWith('/') && !urlFinal.startsWith('data:')) {
            urlFinal = APP_CONFIG.IMG_BASE_PATH + urlFinal;
        }
        
        // Configurar imagen
        detalleImagen.src = urlFinal;
        
        // Fallback si no carga
        detalleImagen.onerror = function() {
            console.warn('Imagen no encontrada, usando placeholder:', urlFinal);
            this.src = APP_CONFIG.DEFAULT_IMAGE_PATH;
            this.onerror = null;
        };
    },
    
    // Limpiar formulario
    limpiarFormulario: function() {
        const form = document.getElementById('motoForm');
        if (form) {
            form.reset();
            this.resetImageDisplay();
            console.log('Formulario limpiado');
        }
    },
    
    // Resetear área de imagen
    resetImageDisplay: function() {
        const imageDisplay = document.getElementById('moto-image-display');
        if (imageDisplay) {
            imageDisplay.innerHTML = `
                <i class="fas fa-motorcycle text-4xl mb-2 text-gray-400"></i>
                <p class="text-sm font-bold">Subir foto de tu moto</p>
                <p class="text-xs text-gray-400">Clic para seleccionar</p>
            `;
            delete imageDisplay.dataset.tempImage;
        }
    },
    
    // Precargar formulario para edición
    precargarFormularioEdicion: function() {
        if (!APP_CONFIG.currentMotoData) {
            console.log('No hay datos para precargar');
            return;
        }
        
        console.log('Precargando datos para edición');
        const data = APP_CONFIG.currentMotoData;
        
        // Llenar campos
        document.getElementById('marca').value = data.marcaMoto || '';
        document.getElementById('modelo').value = data.modeloMoto || '';
        document.getElementById('color').value = data.colorMoto || '';
        document.getElementById('placa').value = data.placaMoto || '';
        document.getElementById('kilometraje').value = data.kilometraje || 0;
        document.getElementById('soat').value = data.soatMoto || '';
        document.getElementById('tecnomecanica').value = data.tecnomecanica || '';
        
        // Precargar imagen
        this.precargarImagenEdicion(data.imagenMoto);
    },
    
    // Precargar imagen en formulario
    precargarImagenEdicion: function(imagenUrl) {
        const imageDisplay = document.getElementById('moto-image-display');
        if (!imageDisplay || !imagenUrl || imagenUrl === 'moto-default.jpg') return;
        
        let imgUrl = imagenUrl;
        if (imgUrl && !imgUrl.startsWith('http') && !imgUrl.startsWith('/') && !imgUrl.startsWith('data:')) {
            imgUrl = APP_CONFIG.IMG_BASE_PATH + imgUrl;
        }
        
        imageDisplay.innerHTML = `
            <img src="${imgUrl}" 
                 alt="Foto de la moto" 
                 class="w-full h-full object-cover"
                 onerror="this.onerror=null; MotoUI.resetImageDisplay();">
        `;
    }
};

window.MotoUI = MotoUI;