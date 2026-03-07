package com.roarmot.roarmot.seeders;

import com.roarmot.roarmot.models.Alerta;
import com.roarmot.roarmot.models.Moto;
import com.roarmot.roarmot.models.Usuario;
import com.roarmot.roarmot.repositories.AlertaRepository;
import com.roarmot.roarmot.repositories.MotoRepository;
import com.roarmot.roarmot.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

import java.util.Date;
import java.util.Calendar;

@Component
public class AlertaDataSeeder implements CommandLineRunner {

    @Autowired
    private AlertaRepository alertaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private MotoRepository motoRepository;

    @Override
    public void run(String... args) throws Exception {
        // Verificar si ya existen alertas para no duplicar
        if (alertaRepository.count() == 0) {
            System.out.println("Insertando datos de prueba para alertas...");
            
            // Obtener usuario de prueba (asumiendo que existe el usuario con ID 1)
            Usuario usuario = usuarioRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Usuario con ID 1 no encontrado"));
            
            // Obtener o crear moto de prueba
            Moto moto = motoRepository.findById(1L)
                .orElseGet(() -> crearMotoEjemplo(usuario));
            
            // Crear alertas de ejemplo
            crearAlertasEjemplo(usuario, moto);
            
            System.out.println("Datos de alertas insertados correctamente");
        }
    }
    
    // En el método crearMotoEjemplo:
    private Moto crearMotoEjemplo(Usuario usuario) {
        Moto moto = new Moto();
        moto.setMarcaMoto("Yamaha");
        moto.setModeloMoto("MT-09");
        moto.setColorMoto("Negro");
        moto.setPlacaMoto("ABC-123");
        
        // Usar Calendar para crear Date
        Calendar cal = Calendar.getInstance();
        
        // SOAT: 15 Diciembre 2025
        cal.set(2025, Calendar.DECEMBER, 15);
        moto.setSoatMoto(cal.getTime());
        
        // Tecnomecánica: 20 Diciembre 2025  
        cal.set(2025, Calendar.DECEMBER, 20);
        moto.setTecnomecanica(cal.getTime());
        
        moto.setUsuario(usuario);
        return motoRepository.save(moto);
    }
    
        private void crearAlertasEjemplo(Usuario usuario, Moto moto) {
        // Crear Calendar para las fechas
        Calendar cal = Calendar.getInstance();
        
        // Alerta SOAT próxima a vencer
        Alerta alertaSoat = new Alerta();
        alertaSoat.setTipo("SOAT");
        alertaSoat.setTitulo("SOAT próximo a vencer");
        alertaSoat.setMensaje("Tu SOAT vence el 15 de Diciembre 2025. Renueva para evitar multas.");
        alertaSoat.setIcono("warning");
        alertaSoat.setNivel("warning");
        
        // Fecha SOAT 
        alertaSoat.setVencimientoSoat(LocalDate.of(2025, 12, 15));
        
        alertaSoat.setAccionUrl("/renovar-soat");
        alertaSoat.setAccionTexto("Renovar SOAT");
        alertaSoat.setMoto(moto);
        alertaSoat.setUsuario(usuario);
        alertaRepository.save(alertaSoat);
        
        // Alerta Tecnomecánica próxima a vencer
        Alerta alertaTecno = new Alerta();
        alertaTecno.setTipo("TECNOMECANICA");
        alertaTecno.setTitulo("Tecnomecánica próxima a vencer");
        alertaTecno.setMensaje("Tu revisión tecnomecánica vence el 20 de Diciembre 2025.");
        alertaTecno.setIcono("warning");
        alertaTecno.setNivel("warning");
        
        // Fecha Tecnomecánica 
        alertaTecno.setVencimientoTecnomecanica(LocalDate.of(2025, 12, 20));
        
        alertaTecno.setAccionUrl("/agendar-tecnomecanica");
        alertaTecno.setAccionTexto("Agendar cita");
        alertaTecno.setMoto(moto);
        alertaTecno.setUsuario(usuario);
        alertaRepository.save(alertaTecno);
        
        // Alerta promocional (ejemplo multipropósito)
        Alerta alertaPromo = new Alerta();
        alertaPromo.setTipo("PROMOCION");
        alertaPromo.setTitulo("Oferta especial en cascos");
        alertaPromo.setMensaje("20% de descuento en cascos integrales esta semana.");
        alertaPromo.setIcono("gift");
        alertaPromo.setNivel("promocion");
        alertaPromo.setAccionUrl("/panel/productos");
        alertaPromo.setAccionTexto("Ver ofertas");
        alertaPromo.setUsuario(usuario);
        // Esta alerta no está asociada a una moto específica
        alertaRepository.save(alertaPromo);
    }
}