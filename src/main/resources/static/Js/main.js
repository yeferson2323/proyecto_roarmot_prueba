// main.js # Inicialización principal y Lógica de Control de Motos Control/Lógica: Inicializa, gestiona la lista (motosDelUsuario), 
// genera botones, maneja la selección de moto, maneja el envío/cancelación/eliminación.

// =========================================================
// DATOS GLOBALES Y CONFIGURACIÓN DE LISTA
// =========================================================
// Usamos APP_CONFIG, pero creamos una variable local para la lista de motos

let motosDelUsuario = []; 
let currentMotoIndex = -1; // Índice (posición en el array) de la moto actualmente seleccionada.

// =========================================================
// FUNCIÓN PRINCIPAL: INICIALIZACIÓN DE LA PÁGINA
// =========================================================
document.addEventListener('DOMContentLoaded', function() {
    console.log('Página cargada. Inicializando sistemas...');
    
    // 1. Inicializar gestión de motos: Ahora usamos nuestra nueva lógica
    inicializarMotosManager();
    
    // 2. Inicializar manejador de imágenes
    ImageHandler.configurarDragAndDrop();
    ImageHandler.configurarUploadImagen();
    
    // 3. Posicionar botones
    ButtonsPosition.init();
    
    // 4. Inicializar carrusel
    setTimeout(() => CarouselManager.inicializarCarruselTips(), 500);
    
    // 5. Cargar alertas
    AlertsManager.cargarAlertas();
    
    // 6. Inicializar estilos globales
    inicializarEstilosGlobales();
});

// =========================================================
// LÓGICA DE GESTIÓN DE MÚLTIPLES MOTOS
// =========================================================

async function inicializarMotosManager() {
    console.log('=== Inicializando Motos Manager ===');
    
    try {
        // 1. LLAMADA A LA API PARA OBTENER MOTOS (usa el endpoint /api/motos)
        const response = await fetch(APP_CONFIG.MOTOS_ENDPOINT, { 
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('jwtToken') 
            }
        });
        
        if (!response.ok) {
            // Si el token JWT expira o es inválido, puede dar 401/403
            throw new Error(`Error ${response.status}: No se pudo cargar la lista de motos.`);
        }
        
        motosDelUsuario = await response.json(); 
        
        console.log(`Motos cargadas desde la API: ${motosDelUsuario.length}`);
        
        // 2. Generar los botones (USO DE FUNCIÓN LOCAL)
        generarBotonesDeMoto(); // <--- CORRECCIÓN 1
        
        if (motosDelUsuario.length > 0) { 
            // 3. Si hay motos, seleccionar la primera por defecto (USO DE FUNCIÓN LOCAL)
            seleccionarMoto(0); // <--- CORRECCIÓN 1
        } else {
            // 4. Si no hay, mostrar el formulario vacío para agregar
            MotoUI.mostrarVistaRegistro(); // (MotoUI.mostrarVistaRegistro() asumo que viene de ui-moto.js global)
        }
        
    } catch (error) {
        console.error('Error al inicializar el gestor de motos:', error);
        // Opcional: Redirigir al login si el error es 401
    } finally {
        // 5. Configurar eventos de edición, cancelación y envío (USO DE FUNCIÓN LOCAL)
        configurarEventosMoto(); // <--- CORRECCIÓN 1
    }
}

/**
 * Genera dinámicamente los botones de selección de moto (1, 2, 3...) y el botón (+)
 * Utiliza las clases CSS personalizadas para la animación de expansión.
 */
function generarBotonesDeMoto() {
    const selectorContainer = document.getElementById('moto-selector-container');
    if (!selectorContainer) {
        console.warn('Contenedor de botones (ID: moto-selector-container) no encontrado.');
        return;
    }
    
    selectorContainer.innerHTML = ''; // Limpiar botones existentes
    
    // Generar botones para cada moto
    motosDelUsuario.forEach((moto, index) => {
        const button = document.createElement('button');
        // Usa la clase CSS de tu animación
        button.className = 'moto-selector-btn'; 
        button.dataset.index = index;
        
        // 1. Estado Normal (el número)
        const normalSpan = document.createElement('span');
        normalSpan.className = 'btn-state-normal';
        normalSpan.textContent = (index + 1).toString();

        // 2. Estado Hover (el texto completo, usaremos la placa para el ejemplo)
        const hoverSpan = document.createElement('span');
        hoverSpan.className = 'btn-state-hover';
        // Usamos la placa o la marca/modelo como texto en hover
        hoverSpan.textContent = moto.placaMoto || `Moto ${index + 1}`; 

        button.appendChild(normalSpan);
        button.appendChild(hoverSpan);
        
        // Marcar la moto activa si coincide
        if (index === currentMotoIndex) {
            button.classList.add('active'); // Usa la clase CSS 'active'
        }
        
        button.addEventListener('click', () => seleccionarMoto(index));
        selectorContainer.appendChild(button);
    });
    
    // Generar el botón de Agregar (+)
    const addButton = document.createElement('button');
    addButton.id = 'btn-agregar-moto';
    
    // Usa la clase CSS de la animación y la clase especial para agregar
    addButton.className = 'moto-selector-btn add-moto-btn'; 
    addButton.title = 'Agregar nueva moto';
    
    // 1. Estado Normal (el signo +)
    const normalSpan = document.createElement('span');
    normalSpan.className = 'btn-state-normal';
    normalSpan.innerHTML = '+';

    // 2. Estado Hover (el texto "Agregar Moto")
    const hoverSpan = document.createElement('span');
    hoverSpan.className = 'btn-state-hover';
    hoverSpan.textContent = 'Agregar Moto'; 

    addButton.appendChild(normalSpan);
    addButton.appendChild(hoverSpan);

    addButton.addEventListener('click', () => {
        currentMotoIndex = -1; // Indica que es una nueva moto (no edición)
        APP_CONFIG.currentMotoData = null; // Limpiar data actual
        MotoUI.mostrarVistaRegistro();
        MotoUI.limpiarFormulario();
        
        // Deseleccionar todos los botones para que el botón "+" quede visualmente activo (aunque no tenga la clase 'active')
        document.querySelectorAll('.moto-selector-btn').forEach(btn => {
            btn.classList.remove('active');
        });
    });
    selectorContainer.appendChild(addButton);
}

/**
 * Selecciona una moto del array, actualiza el estado global y la UI.
 * @param {number} index - El índice de la moto a seleccionar.
 */
function seleccionarMoto(index) {
    if (index < 0 || index >= motosDelUsuario.length) {
        console.error('Índice de moto inválido:', index);
        return;
    }
    
    const motoSeleccionada = motosDelUsuario[index];
    currentMotoIndex = index;
    APP_CONFIG.currentMotoData = motoSeleccionada; // ¡Actualizar la data de edición/detalle!
    StorageManager.saveMotoData(motoSeleccionada); // Guardar en storage si lo usas para persistencia
    
    console.log(`Seleccionando moto ${index + 1}: ${motoSeleccionada.placaMoto}`);
    
    // Mostrar el detalle y actualizar los campos
    MotoUI.mostrarVistaDetalle(motoSeleccionada);
    
    // Actualizar el estado visual de los botones
    const botones = document.querySelectorAll('#moto-selector-container .moto-btn');
    botones.forEach(btn => {
        const btnIndex = parseInt(btn.dataset.index);
        if (btnIndex === index) {
            btn.classList.add('bg-roarmot-primary', 'text-white', 'scale-110');
            btn.classList.remove('bg-gray-200', 'text-black');
        } else {
            btn.classList.remove('bg-roarmot-primary', 'text-white', 'scale-110');
            btn.classList.add('bg-gray-200', 'text-black');
        }
    });
}

/**
 * Llama al backend para obtener la lista actualizada de motos y refresca la UI.
 */
async function recargarListaDeMotos(motoIdFocus = null) {
    console.log('Recargando lista de motos desde el servidor...');
    
    // ELIMINAMOS la dependencia de window.usuarioIdBackend
    
    try {
        // Llamamos al endpoint GET /api/motos, que ya sabe qué usuario es (JWT)
        const response = await fetch(APP_CONFIG.MOTOS_ENDPOINT, { 
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('jwtToken')
            }
        });
        
        if (!response.ok) {
            throw new Error(`Error ${response.status}: Falló la recarga de motos.`);
        }
        
        const listaActualizada = await response.json(); 
        
        // ¡Actualizar la variable GLOBAL de motos!
        motosDelUsuario = listaActualizada; 
        
        // Regenerar los botones
        generarBotonesDeMoto(); // <--- Usa función local
        
        // Decidir qué moto seleccionar
        if (motosDelUsuario.length > 0) {
            let motoIndex = 0; // Por defecto la primera
            
            // Si hay un ID específico para enfocar, buscarlo
            if (motoIdFocus !== null) {
                const foundIndex = motosDelUsuario.findIndex(m => m.idMoto === motoIdFocus);
                if (foundIndex !== -1) {
                    motoIndex = foundIndex;
                }
            }
            
            seleccionarMoto(motoIndex); // <--- Usa función local
        } else {
            // Si no hay motos, limpiar y mostrar formulario de registro
            currentMotoIndex = -1;
            APP_CONFIG.currentMotoData = null;
            MotoUI.mostrarVistaRegistro();
            MotoUI.limpiarFormulario();
        }
        
        console.log(`Lista de motos actualizada. Total: ${motosDelUsuario.length}`);
    } catch (error) {
        console.error('Error al recargar la lista de motos:', error);
        alert('Error al recargar la lista de motos desde el servidor.');
    }
}

// =========================================================
// EVENTOS Y MANEJO DE FORMULARIO
// =========================================================

function configurarEventosMoto() {
    // Botón Editar
    document.getElementById('btn-editar-moto')?.addEventListener('click', function() {
        if (APP_CONFIG.currentMotoData) {
            MotoUI.mostrarVistaRegistro();
            MotoUI.precargarFormularioEdicion();
        }
    });
    
    // Botón Cancelar (Ajustado)
    document.getElementById('btn-cancelar')?.addEventListener('click', function() {
        console.log('Cancelar clickeado');
        if (motosDelUsuario.length > 0) {
            // Si había motos, volvemos a la moto que estaba seleccionada o la primera
            seleccionarMoto(currentMotoIndex !== -1 ? currentMotoIndex : 0);
        } else {
            // Si no hay motos registradas, mantenemos el formulario vacío
            MotoUI.mostrarVistaRegistro();
            MotoUI.limpiarFormulario();
        }
    });
    
    // Botón Eliminar (NUEVO EVENTO)
    document.getElementById('btn-eliminar-moto')?.addEventListener('click', async function() {
        if (!APP_CONFIG.currentMotoData) return;
        
        if (confirm(`¿Estás seguro de eliminar la moto con placa ${APP_CONFIG.currentMotoData.placaMoto}? Esta acción es irreversible.`)) {
            const motoIdAEliminar = APP_CONFIG.currentMotoData.idMoto;
            
            try {
                // Asume una función en ApiHandler.js: await ApiHandler.eliminarMoto(motoId)
                await ApiHandler.eliminarMoto(motoIdAEliminar);
                
                alert('Registro de moto eliminado correctamente');
                
                // Recargar lista después de la eliminación (llama a recargarListaDeMotos)
                await recargarListaDeMotos();
                
            } catch(error) {
                console.error('Error al eliminar:', error);
                alert('Error al eliminar la moto: ' + error.message);
            }
        }
    });
    
    // Envío del formulario (Ajustado)
    document.getElementById('motoForm')?.addEventListener('submit', async function(e) {
        e.preventDefault();
        await handleFormSubmit();
    });
}

// Manejar envío de formulario (Ajustado para la recarga de lista)
async function handleFormSubmit() {
    console.log('Enviando formulario...');
    
    const formData = obtenerDatosFormulario();
    
    try {
        // La respuesta del backend debe ser el MotoDTO guardado, incluyendo el idMoto
        const resultado = await ApiHandler.guardarMoto(formData); 
        
        console.log('Moto guardada exitosamente');
        
        // Recargar la lista y enfocar la moto recién guardada/actualizada
        await recargarListaDeMotos(resultado.idMoto); 
        
        // Limpiar almacenamiento temporal
        StorageManager.clearTempImage();
        
        alert('Moto registrada/actualizada correctamente');
        
        // Nota: La actualización de APP_CONFIG.currentMotoData y la UI se hacen dentro de recargarListaDeMotos/seleccionarMoto
        
    } catch (error) {
        console.error('Error:', error);
        alert('Error: ' + error.message);
    }
}

// Obtener datos del formulario (Se mantiene igual, usa APP_CONFIG.currentMotoData)
function obtenerDatosFormulario() {
    const imagenSubida = StorageManager.getTempImage();
    
    // Función auxiliar para normalizar campos de texto/fecha. 
    // Si el campo está vacío (""), devuelve null. Si no, devuelve el valor.
    const valorOmitible = (id) => {
        const value = document.getElementById(id).value.trim();
        return value === "" ? null : value;
    };
    
    return {
        marcaMoto: valorOmitible('marca'),
        modeloMoto: valorOmitible('modelo'),
        colorMoto: valorOmitible('color'),
        placaMoto: valorOmitible('placa'),
        
        // Kilometraje: Mantenemos el 0 si está vacío para evitar problemas con Integer
        kilometraje: parseInt(document.getElementById('kilometraje').value) || 0, 
        
        // Fechas: Se normalizan a null si están vacías. (REQUERIDO para Date de Java)
        soatMoto: valorOmitible('soat'), 
        tecnomecanica: valorOmitible('tecnomecanica'),
        
        // ID CORREGIDO: Debe coincidir con el campo de la Entidad Java
        idDatosMoto: APP_CONFIG.currentMotoData ? APP_CONFIG.currentMotoData.idDatosMoto : null, 
        
        // Imagen: También puede ser null si no hay imagen
        imagenMoto: imagenSubida || (APP_CONFIG.currentMotoData ? APP_CONFIG.currentMotoData.imagenMoto : null)
    };
}

// Inicializar estilos globales (Se mantiene igual)
function inicializarEstilosGlobales() {
    const style = document.createElement('style');
    style.textContent = `
        .alerta-item { transition: all 0.3s ease; }
        .alerta-item:hover { transform: translateY(-2px); box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.3); }
        .badge-alerta { font-size: 0.7rem; letter-spacing: 0.05em; }
        @keyframes pulse-alerta { 0%, 100% { opacity: 1; } 50% { opacity: 0.5; } }
        .alerta-no-leida { animation: pulse-alerta 2s infinite; }
    `;
    document.head.appendChild(style);
}

// Exportar funciones globales (Ajustado para reflejar la nueva lógica)
window.MotoManager = {
    obtenerDatosMoto: function() { return APP_CONFIG.currentMotoData; },
    tieneMotoRegistrada: function() { return motosDelUsuario.length > 0; },
    recargarDatos: function() { return recargarListaDeMotos(); } // Ahora recarga toda la lista
};

// =========================================================
// Nota: La función MotoUI.verificarYMostrarMotoRegistrada() de ui-moto.js ha sido reemplazada por inicializarMotosManager()
// =========================================================