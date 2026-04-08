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

        if (existeUsuario(id)) {
            throw new IllegalArgumentException("Ya existe un usuario con el id ingresado.");
        }

        Usuario usuario = new Usuario(id.trim(), nombre.trim(), rol.trim());
        return usuarioDAO.guardar(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioDAO.listar();
    }

    public boolean eliminarUsuario(String id) {
        validarTexto(id, "El id del usuario es obligatorio.");
        return usuarioDAO.eliminar(id.trim());
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

    private void validarTexto(String valor, String mensaje) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(mensaje);
        }
    }
}
