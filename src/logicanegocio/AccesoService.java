package logicanegocio;

import accesodatos.AccesoDAO;
import accesodatos.UsuarioDAO;
import entidades.Acceso;
import entidades.Usuario;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AccesoService {
    private final AccesoDAO accesoDAO;
    private final UsuarioDAO usuarioDAO;

    public AccesoService() {
        this.accesoDAO = new AccesoDAO();
        this.usuarioDAO = new UsuarioDAO();
    }

    public boolean registrarEntrada(String idUsuario) {
        validarTexto(idUsuario, "El id del usuario es obligatorio.");

        String idNormalizado = idUsuario.trim();

        if (!existeUsuario(idNormalizado)) {
            throw new IllegalArgumentException("No existe un usuario registrado con el id ingresado.");
        }

        if (tieneAccesoActivo(idNormalizado)) {
            throw new IllegalStateException("El usuario ya tiene una entrada activa.");
        }

        Acceso acceso = new Acceso(idNormalizado, LocalDateTime.now(), null);
        return accesoDAO.guardar(acceso);
    }

    public boolean registrarSalida(String idUsuario) {
        validarTexto(idUsuario, "El id del usuario es obligatorio.");

        String idNormalizado = idUsuario.trim();
        List<Acceso> accesos = accesoDAO.listar();
        Acceso accesoActivo = obtenerAccesoActivo(idNormalizado, accesos);

        if (accesoActivo == null) {
            throw new IllegalStateException("El usuario no tiene una entrada activa para registrar salida.");
        }

        accesoActivo.setFechaHoraSalida(LocalDateTime.now());
        return accesoDAO.guardarTodos(accesos);
    }

    public List<Acceso> listarAccesos() {
        return accesoDAO.listar();
    }

    public List<Acceso> listarAccesosPorUsuario(String idUsuario) {
        validarTexto(idUsuario, "El id del usuario es obligatorio.");

        String idNormalizado = idUsuario.trim();

        if (!existeUsuario(idNormalizado)) {
            throw new IllegalArgumentException("No existe un usuario registrado con el id ingresado.");
        }

        List<Acceso> accesosUsuario = new ArrayList<>();
        List<Acceso> accesos = accesoDAO.listar();

        for (Acceso acceso : accesos) {
            if (acceso.getIdUsuario().equals(idNormalizado)) {
                accesosUsuario.add(acceso);
            }
        }

        return accesosUsuario;
    }

    public Duration calcularTiempoTotalEnLaboratorio(String idUsuario) {
        validarTexto(idUsuario, "El id del usuario es obligatorio.");

        String idNormalizado = idUsuario.trim();

        if (!existeUsuario(idNormalizado)) {
            throw new IllegalArgumentException("No existe un usuario registrado con el id ingresado.");
        }

        Duration tiempoTotal = Duration.ZERO;
        List<Acceso> accesos = listarAccesosPorUsuario(idNormalizado);

        for (Acceso acceso : accesos) {
            if (acceso.getFechaHoraSalida() == null) {
                continue;
            }

            Duration duracionAcceso = Duration.between(
                    acceso.getFechaHoraEntrada(),
                    acceso.getFechaHoraSalida()
            );
            tiempoTotal = tiempoTotal.plus(duracionAcceso);
        }

        return tiempoTotal;
    }

    public String obtenerTiempoTotalFormateado(String idUsuario) {
        Duration tiempoTotal = calcularTiempoTotalEnLaboratorio(idUsuario);
        long horas = tiempoTotal.toHours();
        long minutos = tiempoTotal.toMinutes();

        if (horas > 0) {
            return horas + " horas y " + (minutos % 60) + " minutos";
        }

        return minutos + " minutos";
    }

    private boolean existeUsuario(String idUsuario) {
        List<Usuario> usuarios = usuarioDAO.listar();

        for (Usuario usuario : usuarios) {
            if (usuario.getId().equals(idUsuario)) {
                return true;
            }
        }

        return false;
    }

    private boolean tieneAccesoActivo(String idUsuario) {
        return obtenerAccesoActivo(idUsuario, accesoDAO.listar()) != null;
    }

    private Acceso obtenerAccesoActivo(String idUsuario, List<Acceso> accesos) {
        for (int i = accesos.size() - 1; i >= 0; i--) {
            Acceso acceso = accesos.get(i);

            if (acceso.getIdUsuario().equals(idUsuario) && acceso.getFechaHoraSalida() == null) {
                return acceso;
            }
        }

        return null;
    }

    private void validarTexto(String valor, String mensaje) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(mensaje);
        }
    }
}
