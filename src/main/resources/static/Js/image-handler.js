// image-handler.js # Subida y manejo de imágenes
const ImageHandler = {
    // Configurar drag and drop
    configurarDragAndDrop: function() {
        const dropArea = document.getElementById('moto-image-display');
        if (!dropArea) return;
        
        // Prevenir comportamientos por defecto
        ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
            dropArea.addEventListener(eventName, this.preventDefaults, false);
        });
        
        // Resaltar área
        ['dragenter', 'dragover'].forEach(eventName => {
            dropArea.addEventListener(eventName, this.highlightArea, false);
        });
        
        ['dragleave', 'drop'].forEach(eventName => {
            dropArea.addEventListener(eventName, this.unhighlightArea, false);
        });
        
        // Manejar archivo soltado
        dropArea.addEventListener('drop', this.handleDrop, false);
    },
    
    preventDefaults: function(e) {
        e.preventDefault();
        e.stopPropagation();
    },
    
    highlightArea: function() {
        this.classList.add('border-blue-500', 'border-2');
    },
    
    unhighlightArea: function() {
        this.classList.remove('border-blue-500', 'border-2');
    },
    
    handleDrop: function(e) {
        const dt = e.dataTransfer;
        const files = dt.files;
        
        if (files.length > 0) {
            const input = document.getElementById('upload-moto-image');
            if (input) {
                const dataTransfer = new DataTransfer();
                dataTransfer.items.add(files[0]);
                input.files = dataTransfer.files;
                
                // Disparar evento change
                const event = new Event('change', { bubbles: true });
                input.dispatchEvent(event);
            }
        }
    },
    
    // Configurar evento de subida de imagen
    configurarUploadImagen: function() {
        const uploadInput = document.getElementById('upload-moto-image');
        if (!uploadInput) return;
        
        uploadInput.addEventListener('change', async function(event) {
            const file = event.target.files[0];
            if (!file) return;
            
            console.log('📸 Imagen seleccionada:', file.name);
            
            // Previsualización
            ImageHandler.mostrarPrevisualizacion(file);
            
            // Intentar subir al backend
            await ImageHandler.subirImagenServidor(file);
        });
    },
    
    // Mostrar previsualización
    mostrarPrevisualizacion: function(file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            const displayArea = document.getElementById('moto-image-display');
            if (displayArea) {
                displayArea.innerHTML = `<img src="${e.target.result}" alt="Foto de la moto" class="w-full h-full object-cover" />`;
                displayArea.dataset.tempImage = e.target.result;
            }
        };
        reader.readAsDataURL(file);
    },
    
    // Subir imagen al servidor
    subirImagenServidor: async function(file) {
        try {
            const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
            
            if (csrfToken && csrfHeader) {
                const formData = new FormData();
                formData.append('imagenMoto', file);
                
                const response = await fetch(APP_CONFIG.IMAGEN_ENDPOINT, {
                    method: 'POST',
                    headers: { [csrfHeader]: csrfToken },
                    body: formData
                });
                
                if (response.ok) {
                    const result = await response.json();
                    StorageManager.saveTempImage(result.nombreArchivo || file.name);
                    console.log('Imagen subida al servidor');
                }
            }
        } catch (error) {
            console.log('Imagen guardada solo localmente:', error);
            sessionStorage.setItem('imagenTemp', file.name);
        }
    }
};

window.ImageHandler = ImageHandler;