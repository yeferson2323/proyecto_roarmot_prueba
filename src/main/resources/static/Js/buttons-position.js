// buttons-position.js # Posicionamiento de botones
const ButtonsPosition = {
    // Posicionar botones de motos
    positionMotoButtons: function() {
        const buttonsContainer = document.getElementById('moto-selector-buttons');
        const imagenContainer = document.querySelector('.clip-moto');
        
        if (!buttonsContainer || !imagenContainer) return;
        
        const imagenRect = imagenContainer.getBoundingClientRect();
        const topPosition = imagenRect.top + (imagenRect.height / 2);
        const leftPosition = imagenRect.left - 40 - 15;
        
        buttonsContainer.style.position = 'absolute';
        buttonsContainer.style.top = `${topPosition + window.scrollY}px`;
        buttonsContainer.style.left = `${leftPosition + window.scrollX}px`;
        buttonsContainer.style.transform = 'translateY(-20%)';
    },
    
    // Inicializar eventos
    init: function() {
        // Usar requestAnimationFrame para la primera ejecución
        requestAnimationFrame(this.positionMotoButtons.bind(this));
        
        // Ejecutar en eventos
        window.addEventListener('load', this.positionMotoButtons.bind(this));
        window.addEventListener('resize', this.positionMotoButtons.bind(this));
        window.addEventListener('scroll', this.positionMotoButtons.bind(this));
    }
};

window.ButtonsPosition = ButtonsPosition;