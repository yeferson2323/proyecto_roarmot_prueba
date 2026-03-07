package com.roarmot.roarmot.seeders;

import com.roarmot.roarmot.models.Alerta;
import com.roarmot.roarmot.models.EstadoUsuario;
import com.roarmot.roarmot.models.Moto;
import com.roarmot.roarmot.models.Usuario;
import com.roarmot.roarmot.repositories.AlertaRepository;
import com.roarmot.roarmot.repositories.MotoRepository;
import com.roarmot.roarmot.repositories.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
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
    public void run(String... args) {

        if (alertaRepository.count() > 0) {
            return;
        }

        System.out.println("Insertando datos de prueba para alertas...");

        // Buscar cualquier usuario existente o crear uno
        Usuario usuario = usuarioRepository.findAll()
                .stream()
                .findFirst()
                .orElseGet(this::crearUsuarioEjemplo);

        // Buscar cualquier moto existente o crear una
        Moto moto = motoRepository.findAll()
                .stream()
                .findFirst()
                .orElseGet(() -> crearMotoEjemplo(usuario));

        crearAlertasEjemplo(usuario, moto);

        System.out.println("Datos de alertas insertados correctamente");
    }

    private Usuario crearUsuarioEjemplo() {

        Usuario usuario = new Usuario();

        usuario.setNombre("Admin");
        usuario.setApellido("RoarMot");
        usuario.setEmail("admin@roarmot.com");
        usuario.setPassword("123456");
        usuario.setNumeroUsuario("0001");
        usuario.setTelefono("3000000000");
        usuario.setTipoDocumento("CC");

        usuario.setRolId(1);
        usuario.setEstadoUsuario(EstadoUsuario.Activo);

        return usuarioRepository.save(usuario);
    }

    private Moto crearMotoEjemplo(Usuario usuario) {

        Moto moto = new Moto();
        moto.setMarcaMoto("Yamaha");
        moto.setModeloMoto("MT-09");
        moto.setColorMoto("Negro");
        moto.setPlacaMoto("ABC123");

        Calendar cal = Calendar.getInstance();

        cal.set(2025, Calendar.DECEMBER, 15);
        moto.setSoatMoto(cal.getTime());

        cal.set(2025, Calendar.DECEMBER, 20);
        moto.setTecnomecanica(cal.getTime());

        moto.setUsuario(usuario);

        return motoRepository.save(moto);
    }

    private void crearAlertasEjemplo(Usuario usuario, Moto moto) {

        Alerta alertaSoat = new Alerta();
        alertaSoat.setTipo("SOAT");
        alertaSoat.setTitulo("SOAT próximo a vencer");
        alertaSoat.setMensaje("Tu SOAT vence el 15 de diciembre de 2025.");
        alertaSoat.setIcono("warning");
        alertaSoat.setNivel("warning");
        alertaSoat.setVencimientoSoat(LocalDate.of(2025, 12, 15));
        alertaSoat.setAccionUrl("/renovar-soat");
        alertaSoat.setAccionTexto("Renovar SOAT");
        alertaSoat.setMoto(moto);
        alertaSoat.setUsuario(usuario);

        alertaRepository.save(alertaSoat);

        Alerta alertaTecno = new Alerta();
        alertaTecno.setTipo("TECNOMECANICA");
        alertaTecno.setTitulo("Tecnomecánica próxima a vencer");
        alertaTecno.setMensaje("Tu tecnomecánica vence el 20 de diciembre de 2025.");
        alertaTecno.setIcono("warning");
        alertaTecno.setNivel("warning");
        alertaTecno.setVencimientoTecnomecanica(LocalDate.of(2025, 12, 20));
        alertaTecno.setAccionUrl("/agendar-tecnomecanica");
        alertaTecno.setAccionTexto("Agendar cita");
        alertaTecno.setMoto(moto);
        alertaTecno.setUsuario(usuario);

        alertaRepository.save(alertaTecno);

        Alerta alertaPromo = new Alerta();
        alertaPromo.setTipo("PROMOCION");
        alertaPromo.setTitulo("Oferta especial en cascos");
        alertaPromo.setMensaje("20% de descuento en cascos integrales esta semana.");
        alertaPromo.setIcono("gift");
        alertaPromo.setNivel("promocion");
        alertaPromo.setAccionUrl("/panel/productos");
        alertaPromo.setAccionTexto("Ver ofertas");
        alertaPromo.setUsuario(usuario);

        alertaRepository.save(alertaPromo);
    }
}