package presentacion;

import entidades.Acceso;
import entidades.Usuario;
import logicanegocio.AccesoService;
import logicanegocio.UsuarioService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VentanaPrincipal extends JFrame {
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final Color COLOR_FONDO = new Color(245, 247, 250);
    private static final Color COLOR_PRIMARIO = new Color(11, 92, 171);
    private static final Color COLOR_SECUNDARIO = new Color(12, 141, 129);
    private static final Color COLOR_ACCION = new Color(234, 88, 12);
    private static final Color COLOR_PELIGRO = new Color(220, 38, 38);
    private static final Color COLOR_SUAVE = new Color(233, 239, 247);
    private static final Color COLOR_TEXTO = new Color(32, 41, 62);

    private final UsuarioService usuarioService;
    private final AccesoService accesoService;

    private final JTextField txtIdUsuario = new JTextField(15);
    private final JTextField txtNombreUsuario = new JTextField(15);
    private final JComboBox<String> cmbRolUsuario = new JComboBox<>(new String[]{"Estudiante", "Docente"});
    private final JTextField txtBuscarUsuario = new JTextField(18);
    private final DefaultTableModel modeloUsuarios = crearModeloNoEditable("ID", "Nombre", "Rol");
    private final JTable tablaUsuarios = new JTable(modeloUsuarios);

    private final JTextField txtIdAcceso = new JTextField(15);
    private final JComboBox<UsuarioItem> cmbUsuariosAcceso = new JComboBox<>();
    private final JLabel lblEstadoSeleccionado = new JLabel("Selecciona un usuario para registrar entradas o salidas.");
    private final DefaultTableModel modeloAccesos = crearModeloNoEditable("ID Usuario", "Nombre", "Entrada", "Salida", "Estado");
    private final JTable tablaAccesos = new JTable(modeloAccesos);
    private final DefaultTableModel modeloActivos = crearModeloNoEditable("ID Usuario", "Nombre", "Entrada");
    private final JTable tablaActivos = new JTable(modeloActivos);

    private final JComboBox<UsuarioItem> cmbUsuariosReporte = new JComboBox<>();
    private final JLabel lblTiempoTotal = crearTarjetaResumen("Tiempo total", "Seleccione un usuario");
    private final JLabel lblCantidadAccesos = crearTarjetaResumen("Cantidad de accesos", "0");
    private final JLabel lblEstadoActual = crearTarjetaResumen("Estado actual", "Sin consultar");
    private final DefaultTableModel modeloReporte = crearModeloNoEditable("Entrada", "Salida", "Duracion", "Estado");
    private final JTable tablaReporte = new JTable(modeloReporte);

    private final JLabel lblResumenUsuarios = crearTarjetaResumen("Usuarios registrados", "0");
    private final JLabel lblResumenHistorial = crearTarjetaResumen("Accesos registrados", "0");
    private final JLabel lblResumenActivos = crearTarjetaResumen("Accesos activos", "0");
    private final JLabel lblResumenDocentes = crearTarjetaResumen("Docentes", "0");

    private List<Usuario> cacheUsuarios = new ArrayList<>();
    private List<Acceso> cacheAccesos = new ArrayList<>();

    public VentanaPrincipal() {
        this.usuarioService = new UsuarioService();
        this.accesoService = new AccesoService();

        setTitle("Control de Acceso a Laboratorio");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(crearContenidoPrincipal());
        setSize(1240, 780);
        setMinimumSize(new Dimension(1120, 720));
        setLocationRelativeTo(null);

        configurarTablas();
        registrarEventos();
        cargarDatos();
    }

    private JPanel crearContenidoPrincipal() {
        JPanel contenedor = new JPanel(new BorderLayout(16, 16));
        contenedor.setBorder(new EmptyBorder(16, 16, 16, 16));
        contenedor.setBackground(COLOR_FONDO);

        contenedor.add(crearEncabezado(), BorderLayout.NORTH);
        contenedor.add(crearAreaCentral(), BorderLayout.CENTER);
        return contenedor;
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout(16, 12));
        panel.setBorder(new EmptyBorder(16, 18, 16, 18));
        panel.setBackground(new Color(15, 51, 98));

        JPanel textos = new JPanel(new GridLayout(0, 1, 0, 6));
        textos.setOpaque(false);

        JLabel titulo = new JLabel("Sistema de Control de Acceso");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));

        JLabel subtitulo = new JLabel("Gestion visual de usuarios, entradas, salidas e historial del laboratorio");
        subtitulo.setForeground(new Color(214, 228, 239));
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        textos.add(titulo);
        textos.add(subtitulo);
        panel.add(textos, BorderLayout.CENTER);

        JPanel resumen = new JPanel(new GridLayout(1, 4, 10, 10));
        resumen.setOpaque(false);
        resumen.add(crearPanelTarjeta(lblResumenUsuarios));
        resumen.add(crearPanelTarjeta(lblResumenHistorial));
        resumen.add(crearPanelTarjeta(lblResumenActivos));
        resumen.add(crearPanelTarjeta(lblResumenDocentes));
        panel.add(resumen, BorderLayout.SOUTH);

        return panel;
    }

    private JTabbedPane crearAreaCentral() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabs.addTab("Usuarios", crearPanelUsuarios());
        tabs.addTab("Accesos", crearPanelAccesos());
        tabs.addTab("Reportes", crearPanelReportes());
        return tabs;
    }

    private JPanel crearPanelUsuarios() {
        JPanel panel = crearPanelBlanco(new BorderLayout(16, 16));

        JPanel formulario = crearPanelBlanco(new GridBagLayout());
        formulario.setBorder(BorderFactory.createTitledBorder("Gestion de usuario"));
        GridBagConstraints gbc = crearRestriccionesBase();

        agregarCampo(formulario, gbc, 0, "ID:", txtIdUsuario);
        agregarCampo(formulario, gbc, 1, "Nombre:", txtNombreUsuario);
        agregarCampo(formulario, gbc, 2, "Rol:", cmbRolUsuario);

        JPanel acciones = new JPanel(new GridLayout(1, 4, 10, 10));
        acciones.setOpaque(false);

        JButton btnRegistrar = crearBoton("Agregar usuario", COLOR_PRIMARIO);
        JButton btnActualizar = crearBoton("Actualizar usuario", COLOR_SECUNDARIO);
        JButton btnEliminar = crearBoton("Eliminar usuario", COLOR_PELIGRO);
        JButton btnLimpiar = crearBoton("Limpiar", new Color(104, 116, 135));

        btnRegistrar.addActionListener(e -> registrarUsuario());
        btnActualizar.addActionListener(e -> actualizarUsuario());
        btnEliminar.addActionListener(e -> eliminarUsuario());
        btnLimpiar.addActionListener(e -> limpiarFormularioUsuario());

        acciones.add(btnRegistrar);
        acciones.add(btnActualizar);
        acciones.add(btnEliminar);
        acciones.add(btnLimpiar);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formulario.add(acciones, gbc);

        JPanel tablaPanel = crearPanelBlanco(new BorderLayout());
        tablaPanel.setBorder(BorderFactory.createTitledBorder("Usuarios registrados"));
        tablaPanel.add(crearBarraBusquedaUsuarios(), BorderLayout.NORTH);
        tablaPanel.add(new JScrollPane(tablaUsuarios), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formulario, tablaPanel);
        split.setResizeWeight(0.42);
        split.setBorder(null);

        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelAccesos() {
        JPanel panel = crearPanelBlanco(new BorderLayout(16, 16));

        JPanel barra = crearPanelBlanco(new GridBagLayout());
        barra.setBorder(BorderFactory.createTitledBorder("Registro de entradas y salidas"));
        GridBagConstraints gbc = crearRestriccionesBase();

        agregarCampo(barra, gbc, 0, "Usuario:", cmbUsuariosAcceso);
        agregarCampo(barra, gbc, 1, "ID del usuario:", txtIdAcceso);

        lblEstadoSeleccionado.setOpaque(true);
        lblEstadoSeleccionado.setBackground(new Color(239, 246, 255));
        lblEstadoSeleccionado.setBorder(new EmptyBorder(10, 12, 10, 12));
        lblEstadoSeleccionado.setForeground(COLOR_TEXTO);
        lblEstadoSeleccionado.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        barra.add(lblEstadoSeleccionado, gbc);

        JPanel acciones = new JPanel(new GridLayout(1, 3, 10, 10));
        acciones.setOpaque(false);

        JButton btnEntrada = crearBoton("Registrar entrada", COLOR_SECUNDARIO);
        JButton btnSalida = crearBoton("Registrar salida", COLOR_ACCION);
        JButton btnRefrescar = crearBoton("Actualizar historial", COLOR_PRIMARIO);

        btnEntrada.addActionListener(e -> registrarEntrada());
        btnSalida.addActionListener(e -> registrarSalida());
        btnRefrescar.addActionListener(e -> cargarDatosManteniendoReporte());

        acciones.add(btnEntrada);
        acciones.add(btnSalida);
        acciones.add(btnRefrescar);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        barra.add(acciones, gbc);

        JPanel tablaPanel = crearPanelBlanco(new BorderLayout());
        tablaPanel.setBorder(BorderFactory.createTitledBorder("Historial completo"));
        tablaPanel.add(new JScrollPane(tablaAccesos), BorderLayout.CENTER);

        JPanel activosPanel = crearPanelBlanco(new BorderLayout());
        activosPanel.setBorder(BorderFactory.createTitledBorder("Usuarios dentro del laboratorio"));
        activosPanel.add(new JScrollPane(tablaActivos), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tablaPanel, activosPanel);
        split.setResizeWeight(0.72);
        split.setBorder(null);

        panel.add(barra, BorderLayout.NORTH);
        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelReportes() {
        JPanel panel = crearPanelBlanco(new BorderLayout(16, 16));

        JPanel filtros = crearPanelBlanco(new GridBagLayout());
        filtros.setBorder(BorderFactory.createTitledBorder("Consulta por usuario"));
        GridBagConstraints gbc = crearRestriccionesBase();

        agregarCampo(filtros, gbc, 0, "Usuario:", cmbUsuariosReporte);

        JButton btnConsultar = crearBoton("Consultar reporte", COLOR_PRIMARIO);
        btnConsultar.addActionListener(e -> consultarReporteUsuario());

        JButton btnRefrescar = crearBoton("Recargar datos", new Color(76, 110, 158));
        btnRefrescar.addActionListener(e -> cargarDatosManteniendoReporte());

        JPanel acciones = new JPanel(new GridLayout(1, 2, 10, 10));
        acciones.setOpaque(false);
        acciones.add(btnConsultar);
        acciones.add(btnRefrescar);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        filtros.add(acciones, gbc);

        JPanel resumen = new JPanel(new GridLayout(1, 3, 12, 12));
        resumen.setOpaque(false);
        resumen.add(crearPanelTarjeta(lblTiempoTotal));
        resumen.add(crearPanelTarjeta(lblCantidadAccesos));
        resumen.add(crearPanelTarjeta(lblEstadoActual));

        JPanel tablaPanel = crearPanelBlanco(new BorderLayout());
        tablaPanel.setBorder(BorderFactory.createTitledBorder("Detalle del usuario"));
        tablaPanel.add(new JScrollPane(tablaReporte), BorderLayout.CENTER);

        JPanel superior = new JPanel(new BorderLayout(16, 16));
        superior.setOpaque(false);
        superior.add(filtros, BorderLayout.WEST);
        superior.add(resumen, BorderLayout.CENTER);

        panel.add(superior, BorderLayout.NORTH);
        panel.add(tablaPanel, BorderLayout.CENTER);
        return panel;
    }

    private void configurarTablas() {
        configurarTabla(tablaUsuarios);
        configurarTabla(tablaAccesos);
        configurarTabla(tablaActivos);
        configurarTabla(tablaReporte);
    }

    private void configurarTabla(JTable tabla) {
        tabla.setRowHeight(24);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setGridColor(new Color(222, 228, 236));
        tabla.setSelectionBackground(new Color(215, 231, 252));
        tabla.setSelectionForeground(COLOR_TEXTO);
        tabla.getTableHeader().setBackground(new Color(229, 236, 245));
        tabla.getTableHeader().setForeground(COLOR_TEXTO);
        tabla.setFillsViewportHeight(true);
    }

    private void registrarEventos() {
        tablaUsuarios.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || tablaUsuarios.getSelectedRow() < 0) {
                return;
            }

            int fila = tablaUsuarios.getSelectedRow();
            txtIdUsuario.setText(modeloUsuarios.getValueAt(fila, 0).toString());
            txtNombreUsuario.setText(modeloUsuarios.getValueAt(fila, 1).toString());
            cmbRolUsuario.setSelectedItem(modeloUsuarios.getValueAt(fila, 2).toString());
            txtIdAcceso.setText(modeloUsuarios.getValueAt(fila, 0).toString());
            seleccionarUsuarioEnCombo(cmbUsuariosAcceso, modeloUsuarios.getValueAt(fila, 0).toString());
            seleccionarUsuarioEnCombo(cmbUsuariosReporte, modeloUsuarios.getValueAt(fila, 0).toString());
            actualizarEtiquetaEstadoUsuario();
        });

        cmbUsuariosAcceso.addActionListener(e -> {
            UsuarioItem usuarioItem = (UsuarioItem) cmbUsuariosAcceso.getSelectedItem();
            if (usuarioItem != null) {
                txtIdAcceso.setText(usuarioItem.getId());
                actualizarEtiquetaEstadoUsuario();
            }
        });

        cmbUsuariosReporte.addActionListener(e -> {
            if (cmbUsuariosReporte.getSelectedItem() != null) {
                consultarReporteUsuario();
            }
        });

        txtBuscarUsuario.addActionListener(e -> filtrarUsuarios());
        txtIdAcceso.addActionListener(e -> actualizarEtiquetaEstadoUsuario());
    }

    private void cargarDatos() {
        cacheUsuarios = ordenarUsuarios(new ArrayList<>(usuarioService.listarUsuarios()));
        cacheAccesos = ordenarAccesos(new ArrayList<>(accesoService.listarAccesos()));

        actualizarTablaUsuarios(cacheUsuarios);
        actualizarTablaAccesos(cacheUsuarios, cacheAccesos);
        actualizarTablaActivos(cacheUsuarios, cacheAccesos);
        actualizarCombosUsuarios(cacheUsuarios);
        actualizarResumenGeneral(cacheAccesos, cacheUsuarios);
        actualizarEtiquetaEstadoUsuario();
    }

    private void cargarDatosManteniendoReporte() {
        UsuarioItem seleccionado = (UsuarioItem) cmbUsuariosReporte.getSelectedItem();
        String idSeleccionado = seleccionado == null ? null : seleccionado.getId();

        cargarDatos();

        if (idSeleccionado != null) {
            seleccionarUsuarioEnCombo(cmbUsuariosReporte, idSeleccionado);
            consultarReporteUsuario();
        } else {
            limpiarReporte();
        }
    }

    private List<Usuario> ordenarUsuarios(List<Usuario> usuarios) {
        usuarios.sort(Comparator.comparing(Usuario::getId));
        return usuarios;
    }

    private List<Acceso> ordenarAccesos(List<Acceso> accesos) {
        accesos.sort(Comparator.comparing(Acceso::getFechaHoraEntrada).reversed());
        return accesos;
    }

    private void actualizarTablaUsuarios(List<Usuario> usuarios) {
        modeloUsuarios.setRowCount(0);
        for (Usuario usuario : usuarios) {
            modeloUsuarios.addRow(new Object[]{usuario.getId(), usuario.getNombre(), usuario.getRol()});
        }
    }

    private void actualizarTablaAccesos(List<Usuario> usuarios, List<Acceso> accesos) {
        modeloAccesos.setRowCount(0);
        Map<String, String> nombres = construirMapaNombres(usuarios);

        for (Acceso acceso : accesos) {
            modeloAccesos.addRow(new Object[]{
                    acceso.getIdUsuario(),
                    nombres.getOrDefault(acceso.getIdUsuario(), "Usuario no encontrado"),
                    formatearFecha(acceso.getFechaHoraEntrada()),
                    formatearFecha(acceso.getFechaHoraSalida()),
                    acceso.getFechaHoraSalida() == null ? "Dentro del laboratorio" : "Salida registrada"
            });
        }
    }

    private void actualizarTablaActivos(List<Usuario> usuarios, List<Acceso> accesos) {
        modeloActivos.setRowCount(0);
        Map<String, String> nombres = construirMapaNombres(usuarios);

        for (Acceso acceso : accesos) {
            if (acceso.getFechaHoraSalida() == null) {
                modeloActivos.addRow(new Object[]{
                        acceso.getIdUsuario(),
                        nombres.getOrDefault(acceso.getIdUsuario(), "Usuario no encontrado"),
                        formatearFecha(acceso.getFechaHoraEntrada())
                });
            }
        }
    }

    private void actualizarCombosUsuarios(List<Usuario> usuarios) {
        actualizarComboUsuarios(cmbUsuariosAcceso, usuarios);
        actualizarComboUsuarios(cmbUsuariosReporte, usuarios);
    }

    private void actualizarComboUsuarios(JComboBox<UsuarioItem> combo, List<Usuario> usuarios) {
        UsuarioItem seleccionado = (UsuarioItem) combo.getSelectedItem();
        String idSeleccionado = seleccionado == null ? null : seleccionado.getId();

        combo.removeAllItems();
        for (Usuario usuario : usuarios) {
            combo.addItem(new UsuarioItem(usuario.getId(), usuario.getNombre()));
        }

        if (idSeleccionado != null) {
            seleccionarUsuarioEnCombo(combo, idSeleccionado);
        }
    }

    private void actualizarResumenGeneral(List<Acceso> accesos, List<Usuario> usuarios) {
        long accesosActivos = accesos.stream()
                .filter(acceso -> acceso.getFechaHoraSalida() == null)
                .count();
        long docentes = usuarios.stream()
                .filter(usuario -> "Docente".equalsIgnoreCase(usuario.getRol()))
                .count();

        actualizarTarjeta(lblResumenUsuarios, "Usuarios registrados", String.valueOf(usuarios.size()));
        actualizarTarjeta(lblResumenHistorial, "Accesos registrados", String.valueOf(accesos.size()));
        actualizarTarjeta(lblResumenActivos, "Accesos activos", String.valueOf(accesosActivos));
        actualizarTarjeta(lblResumenDocentes, "Docentes", String.valueOf(docentes));
    }

    private void registrarUsuario() {
        try {
            boolean guardado = usuarioService.guardarUsuario(
                    txtIdUsuario.getText(),
                    txtNombreUsuario.getText(),
                    String.valueOf(cmbRolUsuario.getSelectedItem())
            );

            if (guardado) {
                mostrarMensaje("Usuario agregado correctamente.");
                limpiarFormularioUsuario();
                cargarDatosManteniendoReporte();
            } else {
                mostrarError("No fue posible registrar el usuario.");
            }
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    private void actualizarUsuario() {
        try {
            boolean actualizado = usuarioService.actualizarUsuario(
                    txtIdUsuario.getText(),
                    txtNombreUsuario.getText(),
                    String.valueOf(cmbRolUsuario.getSelectedItem())
            );

            if (actualizado) {
                mostrarMensaje("Usuario actualizado correctamente.");
                limpiarFormularioUsuario();
                cargarDatosManteniendoReporte();
            } else {
                mostrarError("No se encontro un usuario con ese ID.");
            }
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    private void eliminarUsuario() {
        String id = txtIdUsuario.getText().trim();
        if (id.isEmpty()) {
            mostrarError("Ingrese o seleccione un usuario para eliminar.");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "Se eliminara el usuario con ID " + id + ".\n¿Deseas continuar?",
                "Confirmar eliminacion",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean eliminado = usuarioService.eliminarUsuario(id);

            if (eliminado) {
                mostrarMensaje("Usuario eliminado correctamente.");
                limpiarFormularioUsuario();
                cargarDatosManteniendoReporte();
            } else {
                mostrarError("No se encontro un usuario con el id ingresado.");
            }
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    private void registrarEntrada() {
        try {
            boolean registrado = accesoService.registrarEntrada(obtenerIdAccesoSeleccionado());

            if (registrado) {
                mostrarMensaje("Entrada registrada correctamente.");
                txtIdAcceso.setText("");
                cargarDatosManteniendoReporte();
            } else {
                mostrarError("No fue posible registrar la entrada.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrarError(e.getMessage());
        }
    }

    private void registrarSalida() {
        try {
            boolean registrado = accesoService.registrarSalida(obtenerIdAccesoSeleccionado());

            if (registrado) {
                mostrarMensaje("Salida registrada correctamente.");
                txtIdAcceso.setText("");
                cargarDatosManteniendoReporte();
            } else {
                mostrarError("No fue posible registrar la salida.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrarError(e.getMessage());
        }
    }

    private String obtenerIdAccesoSeleccionado() {
        String idManual = txtIdAcceso.getText().trim();
        if (!idManual.isEmpty()) {
            return idManual;
        }

        UsuarioItem usuarioItem = (UsuarioItem) cmbUsuariosAcceso.getSelectedItem();
        return usuarioItem == null ? "" : usuarioItem.getId();
    }

    private void consultarReporteUsuario() {
        UsuarioItem usuarioSeleccionado = (UsuarioItem) cmbUsuariosReporte.getSelectedItem();
        if (usuarioSeleccionado == null) {
            mostrarError("No hay usuarios disponibles para consultar.");
            return;
        }

        try {
            String idUsuario = usuarioSeleccionado.getId();
            List<Acceso> accesosUsuario = new ArrayList<>(accesoService.listarAccesosPorUsuario(idUsuario));
            accesosUsuario.sort(Comparator.comparing(Acceso::getFechaHoraEntrada).reversed());

            modeloReporte.setRowCount(0);
            for (Acceso acceso : accesosUsuario) {
                modeloReporte.addRow(new Object[]{
                        formatearFecha(acceso.getFechaHoraEntrada()),
                        formatearFecha(acceso.getFechaHoraSalida()),
                        formatearDuracion(acceso),
                        acceso.getFechaHoraSalida() == null ? "Activo" : "Cerrado"
                });
            }

            actualizarTarjeta(
                    lblTiempoTotal,
                    "Tiempo total",
                    formatearDuracion(accesoService.calcularTiempoTotalEnLaboratorio(idUsuario))
            );
            actualizarTarjeta(lblCantidadAccesos, "Cantidad de accesos", String.valueOf(accesosUsuario.size()));
            actualizarTarjeta(
                    lblEstadoActual,
                    "Estado actual",
                    tieneAccesoActivo(accesosUsuario) ? "Dentro del laboratorio" : "Fuera del laboratorio"
            );
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    private boolean tieneAccesoActivo(List<Acceso> accesosUsuario) {
        for (Acceso acceso : accesosUsuario) {
            if (acceso.getFechaHoraSalida() == null) {
                return true;
            }
        }
        return false;
    }

    private void limpiarFormularioUsuario() {
        txtIdUsuario.setText("");
        txtNombreUsuario.setText("");
        cmbRolUsuario.setSelectedIndex(0);
        tablaUsuarios.clearSelection();
    }

    private void limpiarReporte() {
        modeloReporte.setRowCount(0);
        actualizarTarjeta(lblTiempoTotal, "Tiempo total", "Seleccione un usuario");
        actualizarTarjeta(lblCantidadAccesos, "Cantidad de accesos", "0");
        actualizarTarjeta(lblEstadoActual, "Estado actual", "Sin consultar");
    }

    private JPanel crearBarraBusquedaUsuarios() {
        JPanel barra = new JPanel(new BorderLayout(10, 10));
        barra.setOpaque(false);
        barra.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel etiqueta = new JLabel("Buscar por ID, nombre o rol:");
        etiqueta.setForeground(COLOR_TEXTO);
        etiqueta.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);

        JButton btnBuscar = crearBoton("Filtrar", COLOR_PRIMARIO);
        btnBuscar.setPreferredSize(new Dimension(110, 34));
        btnBuscar.addActionListener(e -> filtrarUsuarios());

        JButton btnTodos = crearBoton("Ver todos", new Color(90, 107, 130));
        btnTodos.setPreferredSize(new Dimension(110, 34));
        btnTodos.addActionListener(e -> {
            txtBuscarUsuario.setText("");
            actualizarTablaUsuarios(cacheUsuarios);
        });

        acciones.add(btnBuscar);
        acciones.add(btnTodos);

        barra.add(etiqueta, BorderLayout.WEST);
        barra.add(txtBuscarUsuario, BorderLayout.CENTER);
        barra.add(acciones, BorderLayout.EAST);
        return barra;
    }

    private void filtrarUsuarios() {
        String filtro = txtBuscarUsuario.getText().trim().toLowerCase();
        if (filtro.isEmpty()) {
            actualizarTablaUsuarios(cacheUsuarios);
            return;
        }

        List<Usuario> filtrados = cacheUsuarios.stream()
                .filter(usuario ->
                        usuario.getId().toLowerCase().contains(filtro)
                                || usuario.getNombre().toLowerCase().contains(filtro)
                                || usuario.getRol().toLowerCase().contains(filtro))
                .collect(Collectors.toList());

        actualizarTablaUsuarios(filtrados);
    }

    private void seleccionarUsuarioEnCombo(JComboBox<UsuarioItem> combo, String id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            UsuarioItem item = combo.getItemAt(i);
            if (item.getId().equals(id)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void actualizarEtiquetaEstadoUsuario() {
        String id = obtenerIdAccesoSeleccionado().trim();
        if (id.isEmpty()) {
            lblEstadoSeleccionado.setText("Selecciona un usuario para registrar entradas o salidas.");
            return;
        }

        Usuario usuario = usuarioService.buscarUsuarioPorId(id);
        if (usuario == null) {
            lblEstadoSeleccionado.setText("No existe un usuario con el ID " + id + ".");
            return;
        }

        Acceso ultimoAcceso = null;
        for (Acceso acceso : cacheAccesos) {
            if (acceso.getIdUsuario().equals(id)) {
                ultimoAcceso = acceso;
                break;
            }
        }

        if (ultimoAcceso == null) {
            lblEstadoSeleccionado.setText(usuario.getNombre() + " | Sin movimientos registrados.");
            return;
        }

        String estado = ultimoAcceso.getFechaHoraSalida() == null ? "Dentro del laboratorio" : "Fuera del laboratorio";
        lblEstadoSeleccionado.setText(
                usuario.getNombre() + " | " + estado + " | Ultima entrada: " + formatearFecha(ultimoAcceso.getFechaHoraEntrada())
        );
    }

    private GridBagConstraints crearRestriccionesBase() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets.set(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int fila, String texto, Component componente) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0;
        panel.add(new JLabel(texto), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(componente, gbc);
    }

    private JPanel crearPanelBlanco(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        return panel;
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFocusPainted(false);
        boton.setOpaque(true);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(true);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setPreferredSize(new Dimension(160, 36));
        return boton;
    }

    private JLabel crearTarjetaResumen(String titulo, String valorInicial) {
        JLabel label = new JLabel("", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        actualizarTarjeta(label, titulo, valorInicial);
        return label;
    }

    private void actualizarTarjeta(JLabel label, String titulo, String valor) {
        label.putClientProperty("tituloTarjeta", titulo);
        label.setText(
                "<html><div style='text-align:center;'><span style='font-size:11px; color:#d8e4f7;'>"
                        + titulo
                        + "</span><br><span style='font-size:20px; font-weight:bold; color:#ffffff;'>"
                        + valor
                        + "</span></div></html>"
        );
    }

    private JPanel crearPanelTarjeta(JLabel contenido) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(29, 77, 134));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(73, 112, 159)),
                new EmptyBorder(14, 10, 14, 10)
        ));
        panel.add(contenido, BorderLayout.CENTER);
        return panel;
    }

    private DefaultTableModel crearModeloNoEditable(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private Map<String, String> construirMapaNombres(List<Usuario> usuarios) {
        Map<String, String> nombres = new HashMap<>();
        for (Usuario usuario : usuarios) {
            nombres.put(usuario.getId(), usuario.getNombre());
        }
        return nombres;
    }

    private String formatearFecha(LocalDateTime fecha) {
        return fecha == null ? "Pendiente" : fecha.format(FORMATO_FECHA);
    }

    private String formatearDuracion(Acceso acceso) {
        if (acceso.getFechaHoraSalida() == null) {
            return "En curso";
        }

        return formatearDuracion(Duration.between(acceso.getFechaHoraEntrada(), acceso.getFechaHoraSalida()));
    }

    private String formatearDuracion(Duration duracion) {
        long horas = duracion.toHours();
        long minutos = duracion.toMinutes() % 60;
        long segundos = duracion.getSeconds() % 60;
        return horas + " h, " + minutos + " min, " + segundos + " s";
    }

    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Informacion", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Atencion", JOptionPane.ERROR_MESSAGE);
    }

    private static class UsuarioItem {
        private final String id;
        private final String nombre;

        private UsuarioItem(String id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        public String getId() {
            return id;
        }

        @Override
        public String toString() {
            return id + " - " + nombre;
        }
    }
}
