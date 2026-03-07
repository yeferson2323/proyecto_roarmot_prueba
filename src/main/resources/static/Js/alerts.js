// alerts.js # Sistema de alertas
const AlertsManager = {
    // Cargar alertas
    cargarAlertas: async function() {
        const container = document.getElementById('alertas-container');
        
        try {
            const response = await fetch(APP_CONFIG.ALERTAS_ENDPOINT + APP_CONFIG.usuarioId);
            
            if (!response.ok) throw new Error(`Error: ${response.status}`);
            
            const alertas = await response.json();
            this.mostrarAlertasHTML(alertas);
            
        } catch (error) {
            console.error('Error al cargar alertas:', error);
            this.mostrarError(error);
        }
    },
    
    // Mostrar alertas en HTML
    mostrarAlertasHTML: function(alertas) {
        const container = document.getElementById('alertas-container');
        if (!container) return;
        
        if (!alertas || alertas.length === 0) {
            container.innerHTML = this.getEmptyStateHTML();
            return;
        }
        
        let html = '';
        alertas.forEach(alerta => {
            html += this.getAlertaHTML(alerta);
        });
        
        container.innerHTML = html;
    },
    
    // HTML para estado vacío
    getEmptyStateHTML: function() {
        return `
            <div class="text-center py-6">
                <div class="text-gray-400 text-3xl mb-2">🔔</div>
                <p class="text-gray-500">No tienes alertas programadas</p>
                <p class="text-sm text-gray-400 mt-1">Todo está al día</p>
            </div>
        `;
    },
    
    // HTML para cada alerta
    getAlertaHTML: function(alerta) {
        const colores = this.getColoresPorTipo(alerta.tipo);
        
        return `
            <div class="mb-4 p-5 rounded-xl shadow-sm transition-all duration-300 hover:shadow-md" 
                style="background: ${APP_CONFIG.COLOR_PRINCIPAL}; border-left: 4px solid ${colores.borde};">
                ${this.getAlertaHeaderHTML(alerta, colores)}
                ${this.getAlertaBodyHTML(alerta, colores)}
                ${this.getAlertaFooterHTML(alerta, colores)}
            </div>
        `;
    },
    
    // Obtener colores por tipo
    getColoresPorTipo: function(tipo) {
        const colores = {
            'SOAT': {
                borde: '#EF4444', texto: '#FCA5A5', badge: '#DC2626',
                badgeText: '#FFFFFF', badgeBg: '#EF4444', badgeBorder: '#B91C1C', boton: '#DC2626'
            },
            'TECNOMECANICA': {
                borde: '#F59E0B', texto: '#FCD34D', badge: '#D97706',
                badgeText: '#FFFFFF', badgeBg: '#F59E0B', badgeBorder: '#B45309', boton: '#D97706'
            },
            'PROMOCION': {
                borde: '#10B981', texto: '#A7F3D0', badge: '#059669',
                badgeText: '#FFFFFF', badgeBg: '#10B981', badgeBorder: '#047857', boton: '#059669'
            },
            'LICENCIA': {
                borde: '#3B82F6', texto: '#93C5FD', badge: '#1D4ED8',
                badgeText: '#FFFFFF', badgeBg: '#3B82F6', badgeBorder: '#1E40AF', boton: '#1D4ed8'
            },
            'MANTENIMIENTO': {
                borde: '#8B5CF6', texto: '#C4B5FD', badge: '#7C3AED',
                badgeText: '#FFFFFF', badgeBg: '#8B5CF6', badgeBorder: '#6D28D9', boton: '#7C3AED'
            }
        };
        
        return colores[tipo] || {
            borde: '#6B7280', texto: '#D1D5DB', badge: '#4B5563',
            badgeText: '#FFFFFF', badgeBg: '#6B7280', badgeBorder: '#374151', boton: '#4B5563'
        };
    },
    
    // Obtener emoji por tipo
    getEmojiIcono: function(tipo) {
        const emojis = {
            'SOAT': '🚨', 'TECNOMECANICA': '🔧', 'PROMOCION': '🎁',
            'MANTENIMIENTO': '⚙️', 'LICENCIA': '📄'
        };
        return emojis[tipo] || '🔔';
    },
    
    // Más métodos helper... (getAlertaHeaderHTML, getAlertaBodyHTML, etc.)
    // Se mantienen similares a tu código original
};

window.AlertsManager = AlertsManager;