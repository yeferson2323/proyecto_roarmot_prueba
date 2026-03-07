// carousel.js # Carrusel de tips
const CarouselManager = {
    currentIndex: 0,
    totalItems: 0,
    intervalo: null,
    intervaloTime: APP_CONFIG.CARRUSEL_INTERVAL,
    
    // Inicializar carrusel
    inicializarCarruselTips: function() {
        console.log('Inicializando carrusel de tips...');
        
        const carruselContainer = document.getElementById('tips-carousel-container');
        const innerWrapper = document.getElementById('tips-inner-wrapper');
        const items = document.querySelectorAll('#tips-inner-wrapper .carousel-item');
        
        if (!carruselContainer || !innerWrapper || items.length === 0) {
            console.error('Elementos del carrusel no encontrados');
            return;
        }
        
        console.log(`Carrusel encontrado con ${items.length} items`);
        
        this.totalItems = items.length;
        this.configurarEstilos(innerWrapper);
        this.configurarEventos(carruselContainer, innerWrapper, items);
        this.configurarAnchos(carruselContainer, innerWrapper, items);
    },
    
    // Configurar estilos básicos
    configurarEstilos: function(innerWrapper) {
        innerWrapper.style.display = 'flex';
        innerWrapper.style.transition = 'transform 0.5s ease-in-out';
    },
    
    // Configurar eventos
    configurarEventos: function(container, wrapper, items) {
        const indicators = document.querySelectorAll('.carousel-indicator');
        
        // Eventos para indicadores
        indicators.forEach((indicator, index) => {
            indicator.addEventListener('click', () => {
                this.irASlide(index);
                this.reiniciarCarruselAutomatico();
            });
        });
        
        // Pausar al hacer hover
        container.addEventListener('mouseenter', this.detenerCarruselAutomatico.bind(this));
        container.addEventListener('mouseleave', this.iniciarCarruselAutomatico.bind(this));
        
        // Redimensionamiento
        let resizeTimeout;
        window.addEventListener('resize', () => {
            this.detenerCarruselAutomatico();
            clearTimeout(resizeTimeout);
            resizeTimeout = setTimeout(() => {
                this.configurarAnchos(container, wrapper, items);
                this.reiniciarCarruselAutomatico();
            }, APP_CONFIG.RESIZE_DEBOUNCE);
        });
    },
    
    // Configurar anchos de items
    configurarAnchos: function(container, wrapper, items) {
        const anchoFinal = container.clientWidth;
        
        if (anchoFinal === 0) {
            setTimeout(() => this.configurarAnchos(container, wrapper, items), 300);
            return;
        }
        
        items.forEach(item => {
            item.style.minWidth = `${anchoFinal}px`;
            item.style.maxWidth = `${anchoFinal}px`;
            item.style.width = `${anchoFinal}px`;
            item.style.flexShrink = '0';
        });
        
        wrapper.style.width = `${this.totalItems * anchoFinal}px`;
        this.irASlide(this.currentIndex, false);
        this.iniciarCarruselAutomatico();
    },
    
    // Navegar a slide
    irASlide: function(index, animate = true) {
        if (index < 0) index = this.totalItems - 1;
        if (index >= this.totalItems) index = 0;
        
        this.currentIndex = index;
        
        const container = document.getElementById('tips-carousel-container');
        const wrapper = document.getElementById('tips-inner-wrapper');
        
        if (!container || !wrapper) return;
        
        const anchoContenedor = container.clientWidth;
        
        if (!animate) wrapper.style.transition = 'none';
        
        const translateX = -(this.currentIndex * anchoContenedor);
        wrapper.style.transform = `translateX(${translateX}px)`;
        
        if (!animate) {
            requestAnimationFrame(() => {
                wrapper.style.transition = 'transform 0.5s ease-in-out';
            });
        }
        
        this.actualizarIndicadores();
    },
    
    // Actualizar indicadores
    actualizarIndicadores: function() {
        const indicators = document.querySelectorAll('.carousel-indicator');
        indicators.forEach((indicator, index) => {
            if (index === this.currentIndex) {
                indicator.classList.add('active', 'bg-white');
                indicator.classList.remove('bg-gray-500');
            } else {
                indicator.classList.remove('active', 'bg-white');
                indicator.classList.add('bg-gray-500');
            }
        });
    },
    
    // Siguiente slide
    siguienteSlide: function() {
        this.irASlide(this.currentIndex + 1);
    },
    
    // Control del carrusel automático
    iniciarCarruselAutomatico: function() {
        this.detenerCarruselAutomatico();
        this.intervalo = setInterval(this.siguienteSlide.bind(this), this.intervaloTime);
    },
    
    detenerCarruselAutomatico: function() {
        if (this.intervalo) {
            clearInterval(this.intervalo);
            this.intervalo = null;
        }
    },
    
    reiniciarCarruselAutomatico: function() {
        this.detenerCarruselAutomatico();
        this.iniciarCarruselAutomatico();
    }
};

window.CarouselManager = CarouselManager;