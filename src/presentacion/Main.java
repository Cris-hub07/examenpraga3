package presentacion;

import entidades.Acceso;
import entidades.Usuario;
import logicanegocio.AccesoService;
import logicanegocio.UsuarioService;

import java.time.Duration;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UsuarioService usuarioService = new UsuarioService();
        AccesoService accesoService = new AccesoService();
        boolean continuar = true;

        while (continuar) {
            mostrarMenu();
            String opcion = scanner.nextLine().trim();

            try {
                switch (opcion) {
                    case "1":
                        registrarUsuario(scanner, usuarioService);
                        break;
                    case "2":
                        listarUsuarios(usuarioService);
                        break;
                    case "3":
                        eliminarUsuario(scanner, usuarioService);
                        break;
                    case "4":
                        registrarEntrada(scanner, accesoService);
                        break;
                    case "5":
                        registrarSalida(scanner, accesoService);
                        break;
                    case "6":
                        verHistorialAccesos(accesoService);
                        break;
                    case "7":
                        verTiempoTotal(scanner, accesoService);
                        break;
                    case "0":
                        continuar = false;
                        System.out.println("Saliendo del sistema...");
                        break;
                    default:
                        System.out.println("Opcion invalida. Intente nuevamente.");
                        break;
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Ocurrio un error inesperado. Intente nuevamente.");
            }

            System.out.println();
        }

        scanner.close();
    }

    private static void mostrarMenu() {
        System.out.println("=== Sistema de Control de Acceso a Laboratorio ===");
        System.out.println("1. Registrar usuario");
        System.out.println("2. Listar usuarios");
        System.out.println("3. Eliminar usuario");
        System.out.println("4. Registrar entrada");
        System.out.println("5. Registrar salida");
        System.out.println("6. Ver historial de accesos");
        System.out.println("7. Ver tiempo total en laboratorio");
        System.out.println("0. Salir");
        System.out.print("Seleccione una opcion: ");
    }

    private static void registrarUsuario(Scanner scanner, UsuarioService usuarioService) {
        System.out.print("Ingrese el id del usuario: ");
        String id = scanner.nextLine();
        System.out.print("Ingrese el nombre del usuario: ");
        String nombre = scanner.nextLine();
        System.out.print("Ingrese el rol del usuario (Estudiante o Docente): ");
        String rol = scanner.nextLine();

        boolean guardado = usuarioService.guardarUsuario(id, nombre, rol);

        if (guardado) {
            System.out.println("Usuario registrado correctamente.");
        } else {
            System.out.println("No fue posible registrar el usuario.");
        }
    }

    private static void listarUsuarios(UsuarioService usuarioService) {
        List<Usuario> usuarios = usuarioService.listarUsuarios();

        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
            return;
        }

        System.out.println("=== Lista de usuarios ===");

        for (Usuario usuario : usuarios) {
            System.out.println(usuario);
        }
    }

    private static void eliminarUsuario(Scanner scanner, UsuarioService usuarioService) {
        System.out.print("Ingrese el id del usuario a eliminar: ");
        String id = scanner.nextLine();

        boolean eliminado = usuarioService.eliminarUsuario(id);

        if (eliminado) {
            System.out.println("Usuario eliminado correctamente.");
        } else {
            System.out.println("No se encontro un usuario con el id ingresado.");
        }
    }

    private static void registrarEntrada(Scanner scanner, AccesoService accesoService) {
        System.out.print("Ingrese el id del usuario: ");
        String idUsuario = scanner.nextLine();

        boolean registrado = accesoService.registrarEntrada(idUsuario);

        if (registrado) {
            System.out.println("Entrada registrada correctamente.");
        } else {
            System.out.println("No fue posible registrar la entrada.");
        }
    }

    private static void registrarSalida(Scanner scanner, AccesoService accesoService) {
        System.out.print("Ingrese el id del usuario: ");
        String idUsuario = scanner.nextLine();

        boolean registrado = accesoService.registrarSalida(idUsuario);

        if (registrado) {
            System.out.println("Salida registrada correctamente.");
        } else {
            System.out.println("No fue posible registrar la salida.");
        }
    }

    private static void verHistorialAccesos(AccesoService accesoService) {
        List<Acceso> accesos = accesoService.listarAccesos();

        if (accesos.isEmpty()) {
            System.out.println("No hay accesos registrados.");
            return;
        }

        System.out.println("=== Historial de accesos ===");

        for (Acceso acceso : accesos) {
            System.out.println(acceso);
        }
    }

    private static void verTiempoTotal(Scanner scanner, AccesoService accesoService) {
        System.out.print("Ingrese el id del usuario: ");
        String idUsuario = scanner.nextLine();

        Duration tiempoTotal = accesoService.calcularTiempoTotalEnLaboratorio(idUsuario);
        long horas = tiempoTotal.toHours();
        long minutos = tiempoTotal.toMinutes() % 60;
        long segundos = tiempoTotal.getSeconds() % 60;

        System.out.println(
                "Tiempo total en laboratorio: "
                        + horas + " horas, "
                        + minutos + " minutos, "
                        + segundos + " segundos."
        );
    }
}
