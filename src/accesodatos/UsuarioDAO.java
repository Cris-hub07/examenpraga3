package accesodatos;

import entidades.Usuario;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    private static final String CARPETA_DATOS = "data";
    private static final String ARCHIVO_USUARIOS = CARPETA_DATOS + File.separator + "usuarios.txt";

    public boolean guardar(Usuario usuario) {
        List<Usuario> usuarios = listar();

        for (Usuario existente : usuarios) {
            if (existente.getId().equals(usuario.getId())) {
                return false;
            }
        }

        File archivo = obtenerArchivoUsuarios();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo, true))) {
            writer.write(usuario.getId() + "," + usuario.getNombre() + "," + usuario.getRol());
            writer.newLine();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public List<Usuario> listar() {
        List<Usuario> usuarios = new ArrayList<>();
        File archivo = obtenerArchivoUsuarios();

        if (!archivo.exists()) {
            return usuarios;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;

            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(",", -1);

                if (datos.length == 3) {
                    usuarios.add(new Usuario(datos[0], datos[1], datos[2]));
                }
            }
        } catch (IOException e) {
            return new ArrayList<>();
        }

        return usuarios;
    }

    public boolean eliminar(String id) {
        List<Usuario> usuarios = listar();
        List<Usuario> actualizados = new ArrayList<>();
        boolean eliminado = false;

        for (Usuario usuario : usuarios) {
            if (usuario.getId().equals(id)) {
                eliminado = true;
            } else {
                actualizados.add(usuario);
            }
        }

        if (!eliminado) {
            return false;
        }

        File archivo = obtenerArchivoUsuarios();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            for (Usuario usuario : actualizados) {
                writer.write(usuario.getId() + "," + usuario.getNombre() + "," + usuario.getRol());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private File obtenerArchivoUsuarios() {
        File carpeta = new File(CARPETA_DATOS);

        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        return new File(ARCHIVO_USUARIOS);
    }
}
