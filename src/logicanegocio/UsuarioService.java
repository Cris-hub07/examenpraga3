package logicanegocio;

import accesodatos.UsuarioDAO;
import entidades.Usuario;

import java.util.List;

public class UsuarioService {
    private final UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public boolean guardarUsuario(String id, String nombre, String rol) {
        validarTexto(id, "El id del usuario es obligatorio.");
        validarTexto(nombre, "El nombre del usuario es obligatorio.");
        validarTexto(rol, "El rol del usuario es obligatorio.");

        String idNormalizado = normalizarTexto(id, "El id del usuario es obligatorio.");
        String nombreNormalizado = normalizarTexto(nombre, "El nombre del usuario es obligatorio.");
        String rolNormalizado = normalizarRol(rol);

        if (existeUsuario(idNormalizado)) {
            throw new IllegalArgumentException("Ya existe un usuario con el id ingresado.");
        }

        Usuario usuario = new Usuario(idNormalizado, nombreNormalizado, rolNormalizado);
        return usuarioDAO.guardar(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioDAO.listar();
    }

    public boolean actualizarUsuario(String id, String nombre, String rol) {
        String idNormalizado = normalizarTexto(id, "El id del usuario es obligatorio.");
        String nombreNormalizado = normalizarTexto(nombre, "El nombre del usuario es obligatorio.");
        String rolNormalizado = normalizarRol(rol);

        Usuario existente = usuarioDAO.buscarPorId(idNormalizado);
        if (existente == null) {
            return false;
        }

        Usuario usuarioActualizado = new Usuario(idNormalizado, nombreNormalizado, rolNormalizado);
        return usuarioDAO.actualizar(usuarioActualizado);
    }

    public boolean eliminarUsuario(String id) {
        validarTexto(id, "El id del usuario es obligatorio.");
        return usuarioDAO.eliminar(id.trim());
    }

    public Usuario buscarUsuarioPorId(String id) {
        validarTexto(id, "El id del usuario es obligatorio.");
        return usuarioDAO.buscarPorId(id.trim());
    }

    private boolean existeUsuario(String id) {
        List<Usuario> usuarios = usuarioDAO.listar();

        for (Usuario usuario : usuarios) {
            if (usuario.getId().equals(id.trim())) {
                return true;
            }
        }

        return false;
    }

    private String normalizarRol(String rol) {
        String rolNormalizado = normalizarTexto(rol, "El rol del usuario es obligatorio.");

        if (rolNormalizado.equalsIgnoreCase("estudiante")) {
            return "Estudiante";
        }

        if (rolNormalizado.equalsIgnoreCase("docente")) {
            return "Docente";
        }

        throw new IllegalArgumentException("El rol debe ser Estudiante o Docente.");
    }

    private String normalizarTexto(String valor, String mensaje) {
        validarTexto(valor, mensaje);

        String textoNormalizado = valor.trim();
        if (textoNormalizado.contains(",")) {
            throw new IllegalArgumentException("No se permite usar comas en los datos.");
        }

        return textoNormalizado;
    }

    private void validarTexto(String valor, String mensaje) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(mensaje);
        }
    }
}
