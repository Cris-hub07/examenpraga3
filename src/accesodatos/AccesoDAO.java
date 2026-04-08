package accesodatos;

import entidades.Acceso;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AccesoDAO {
    private static final String CARPETA_DATOS = "data";
    private static final String ARCHIVO_ACCESOS = CARPETA_DATOS + File.separator + "accesos.txt";

    public boolean guardar(Acceso acceso) {
        List<Acceso> accesos = listar();

        for (Acceso existente : accesos) {
            if (existente.getIdUsuario().equals(acceso.getIdUsuario())
                    && existente.getFechaHoraEntrada().equals(acceso.getFechaHoraEntrada())
                    && Objects.equals(existente.getFechaHoraSalida(), acceso.getFechaHoraSalida())) {
                return false;
            }
        }

        File archivo = obtenerArchivoAccesos();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo, true))) {
            writer.write(
                    acceso.getIdUsuario() + ","
                            + acceso.getFechaHoraEntrada() + ","
                            + convertirFechaSalidaATexto(acceso.getFechaHoraSalida())
            );
            writer.newLine();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public List<Acceso> listar() {
        List<Acceso> accesos = new ArrayList<>();
        File archivo = obtenerArchivoAccesos();

        if (!archivo.exists()) {
            return accesos;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;

            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(",", -1);

                if (datos.length == 3) {
                    accesos.add(new Acceso(
                            datos[0],
                            LocalDateTime.parse(datos[1]),
                            convertirTextoAFechaSalida(datos[2])
                    ));
                }
            }
        } catch (IOException e) {
            return new ArrayList<>();
        }

        return accesos;
    }

    public boolean guardarTodos(List<Acceso> accesos) {
        File archivo = obtenerArchivoAccesos();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            for (Acceso acceso : accesos) {
                writer.write(
                        acceso.getIdUsuario() + ","
                                + acceso.getFechaHoraEntrada() + ","
                                + convertirFechaSalidaATexto(acceso.getFechaHoraSalida())
                );
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private File obtenerArchivoAccesos() {
        File carpeta = new File(CARPETA_DATOS);

        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        return new File(ARCHIVO_ACCESOS);
    }

    private String convertirFechaSalidaATexto(LocalDateTime fechaHoraSalida) {
        if (fechaHoraSalida == null) {
            return "";
        }

        return fechaHoraSalida.toString();
    }

    private LocalDateTime convertirTextoAFechaSalida(String textoFechaSalida) {
        if (textoFechaSalida == null || textoFechaSalida.isEmpty()) {
            return null;
        }

        return LocalDateTime.parse(textoFechaSalida);
    }
}
