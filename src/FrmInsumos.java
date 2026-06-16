import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;

public class FrmInsumos extends JFrame {
    private final JTextField txtId = new JTextField();
    private final JTextField txtCodigo = new JTextField();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtDescripcion = new JTextField();
    private final JTextField txtPrecio = new JTextField();
    private final JTextField txtStock = new JTextField();
    private final JTextField txtBuscar = new JTextField();

    private final JButton btnNuevo = new JButton("Nuevo");
    private final JButton btnGuardar = new JButton("Guardar");
    private final JButton btnEditar = new JButton("Modificar");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnBuscar = new JButton("Buscar");
    private final JButton btnListar = new JButton("Listar");
    private final JButton btnReporte = new JButton("Hacer Reporte");
    private final java.util.List<String> historialCambios = new java.util.ArrayList<>();

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[] { "ID", "Codigo", "Nombre", "Descripcion", "Precio", "Stock" }, 0) {
        @Override
        public boolean isCellEditable(int row , int column) { //pregunta Si  se puede editar la celda que está en esta fila (row) y esta columna (column)?
            return false;
        }
    };

    private final JTable tablaInsumos = new JTable(modeloTabla);
    private final InsumosDAO insumosDAO = new InsumosDAO();

    public FrmInsumos() {
        configurarVentana();
        armarInterfaz();
        configurarEventos();
        cargarTabla();
    }

    private void configurarVentana() {
        setTitle("Gestion de Insumos Electronicos");
        setSize(900, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private void armarInterfaz() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        panelPrincipal.setBackground(new Color(246, 247, 249));
        add(panelPrincipal, BorderLayout.CENTER);

        JLabel titulo = new JLabel("ABM de Insumos Electronicos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(new Color(35, 45, 65));
        panelPrincipal.add(titulo, BorderLayout.NORTH);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del insumo"));
        panelFormulario.setBackground(new Color(255, 245, 199));
        panelPrincipal.add(panelFormulario, BorderLayout.WEST);

        txtId.setEditable(false);
        agregarCampo(panelFormulario, "ID:", txtId, 0);
        agregarCampo(panelFormulario, "Codigo:", txtCodigo, 1);
        agregarCampo(panelFormulario, "Nombre:", txtNombre, 2);
        agregarCampo(panelFormulario, "Descripcion:", txtDescripcion, 3);
        agregarCampo(panelFormulario, "Precio:", txtPrecio, 4);
        agregarCampo(panelFormulario, "Stock:", txtStock, 5);

        JPanel panelBotones = new JPanel(new GridBagLayout());
        panelBotones.setOpaque(false);
        GridBagConstraints gbcBotones = new GridBagConstraints();
        gbcBotones.insets = new Insets(8, 4, 4, 4);
        gbcBotones.fill = GridBagConstraints.HORIZONTAL;
        gbcBotones.weightx = 1;

        JButton[] botones = { btnNuevo, btnGuardar, btnEditar, btnEliminar };
        for (int i = 0; i < botones.length; i++) {
            gbcBotones.gridx = i % 2;
            gbcBotones.gridy = i / 2;
            panelBotones.add(botones[i], gbcBotones);
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelFormulario.add(panelBotones, gbc);

        JPanel panelCentro = new JPanel(new BorderLayout(8, 8));
        panelCentro.setOpaque(false);
        panelPrincipal.add(panelCentro, BorderLayout.CENTER);

        JPanel panelBusqueda = new JPanel(new BorderLayout(8, 8));
        panelBusqueda.setOpaque(false);
        panelBusqueda.add(new JLabel("Buscar:"), BorderLayout.WEST);
        panelBusqueda.add(txtBuscar, BorderLayout.CENTER);

        JPanel panelAccionesBusqueda = new JPanel();
        panelAccionesBusqueda.setOpaque(false);
        panelAccionesBusqueda.add(btnBuscar);
        panelAccionesBusqueda.add(btnListar);
        panelAccionesBusqueda.add(btnReporte);
        panelBusqueda.add(panelAccionesBusqueda, BorderLayout.EAST);
        panelCentro.add(panelBusqueda, BorderLayout.NORTH);

        tablaInsumos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaInsumos.setRowHeight(24);
        tablaInsumos.getTableHeader().setReorderingAllowed(false);
        panelCentro.add(new JScrollPane(tablaInsumos), BorderLayout.CENTER);

        JLabel pie = new JLabel("Java Swing | Modelo/Database: MySQL + JDBC | Patron MVC");
        pie.setHorizontalAlignment(JLabel.CENTER);
        pie.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        panelPrincipal.add(pie, BorderLayout.SOUTH);
    }

    private void agregarCampo(JPanel panel, String etiqueta, JTextField campo, int fila) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = fila;
        panel.add(new JLabel(etiqueta), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        campo.setColumns(18);
        panel.add(campo, gbc);
    }

    private void configurarEventos() {
        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnGuardar.addActionListener(e -> guardarInsumo());
        btnEditar.addActionListener(e -> modificarInsumo());
        btnEliminar.addActionListener(e -> eliminarInsumo());
        btnBuscar.addActionListener(e -> buscarInsumos());
        btnListar.addActionListener(e -> cargarTabla());
        btnReporte.addActionListener(e -> generarReporte());

        tablaInsumos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccionEnFormulario();
            }
        });
    }

    private void guardarInsumo() {
        try {
            Insumos insumo = leerFormulario(false);
            insumosDAO.registrarInsumo(insumo);
            historialCambios.add("[AÑADIDO] - Código: " + insumo.getCodigo() + " | Nombre: " + insumo.getNombre());
            mostrarMensaje("Insumo registrado correctamente.");
            limpiarFormulario();
            cargarTabla();
        } catch (IllegalArgumentException | SQLException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void modificarInsumo() {
    try {
        // 1. Obtenemos la fila que el usuario seleccionó antes de modificar
        int fila = tablaInsumos.getSelectedRow(); 
        
        // 2. Leemos los datos nuevos que escribió en las cajitas de texto
        Insumos insumo = leerFormulario(true); 

        // 3. Preparamos el texto base para el historial
        String detalleCambios = "[MODIFICADO] - ID: " + insumo.getId() + " -> Cambios: ";
        boolean huboCambios = false;

        // 4. Comparamos lo viejo (de la tabla) con lo nuevo (del formulario)
        if (fila != -1) {
            if (!valorTabla(fila, 2).equals(insumo.getNombre())) {
                detalleCambios += "Nombre (" + valorTabla(fila, 2) + " a " + insumo.getNombre() + ") | ";
                huboCambios = true;
            }
            if (!valorTabla(fila, 3).equals(insumo.getDescripcion())) {
                detalleCambios += "Desc (" + valorTabla(fila, 3) + " a " + insumo.getDescripcion() + ") | ";
                huboCambios = true;
            }
            // Comparamos convirtiendo los números a texto para evitar errores
            if (!valorTabla(fila, 4).equals(String.valueOf(insumo.getPrecio()))) {
                detalleCambios += "Precio (" + valorTabla(fila, 4) + " a " + insumo.getPrecio() + ") | ";
                huboCambios = true;
            }
            if (!valorTabla(fila, 5).equals(String.valueOf(insumo.getStock()))) {
                detalleCambios += "Stock (" + valorTabla(fila, 5) + " a " + insumo.getStock() + ") | ";
                huboCambios = true;
            }
        }

        // Si el usuario apretó "Modificar" pero no cambió ninguna letra
        if (!huboCambios) {
            detalleCambios += "Se guardó sin alterar los datos.";
        }

        // 5. Guardamos en la base de datos
        insumosDAO.modificarInsumo(insumo); 
        
        // 6. Anotamos en la bitácora el texto exacto de lo que cambió
        historialCambios.add(detalleCambios);
        
        mostrarMensaje("Insumo modificado correctamente."); 
        limpiarFormulario(); 
        cargarTabla(); 
    } catch (IllegalArgumentException | SQLException ex) { 
        mostrarError(ex.getMessage()); 
    }
}

    private void eliminarInsumo() {
        if (txtId.getText().trim().isEmpty()) {
            mostrarError("Selecciona un insumo de la tabla para eliminar.");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(this, "Deseas eliminar el insumo seleccionado?",
                "Confirmar baja", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            int idEliminado = Integer.parseInt(txtId.getText());
            String nombreEliminado = txtNombre.getText().trim();
            String descripcionEliminada = txtDescripcion.getText().trim();
            
            insumosDAO.eliminarInsumo(idEliminado);
            historialCambios.add("[ELIMINADO] - ID: " + idEliminado + " | Nombre: " + nombreEliminado + " | Descripcion: " + descripcionEliminada);
            mostrarMensaje("Insumo eliminado correctamente.");
            limpiarFormulario();
            cargarTabla();
        } catch (SQLException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void buscarInsumos() {
        try {
            llenarTabla(insumosDAO.buscarInsumos(txtBuscar.getText().trim()));
        } catch (SQLException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void cargarTabla() {
        try {
            llenarTabla(insumosDAO.listarInsumos());
        } catch (SQLException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void llenarTabla(List<Insumos> insumos) {
        modeloTabla.setRowCount(0);
        for (Insumos insumo : insumos) {
            modeloTabla.addRow(new Object[] {
                    insumo.getId(),
                    insumo.getCodigo(),
                    insumo.getNombre(),
                    insumo.getDescripcion(),
                    insumo.getPrecio(),
                    insumo.getStock()
            });
        }
    }

    private void cargarSeleccionEnFormulario() {
        int fila = tablaInsumos.getSelectedRow();
        if (fila == -1) {
            return;
        }

        txtId.setText(valorTabla(fila, 0));
        txtCodigo.setText(valorTabla(fila, 1));
        txtNombre.setText(valorTabla(fila, 2));
        txtDescripcion.setText(valorTabla(fila, 3));
        txtPrecio.setText(valorTabla(fila, 4));
        txtStock.setText(valorTabla(fila, 5));
    }

    private String valorTabla(int fila, int columna) {
        Object valor = modeloTabla.getValueAt(fila, columna);
        return valor == null ? "" : valor.toString();
    }

    private Insumos leerFormulario(boolean requiereId) {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String precioTexto = txtPrecio.getText().trim().replace(",", ".");
        String stockTexto = txtStock.getText().trim();

        if (requiereId && txtId.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Selecciona un insumo de la tabla para modificar.");
        }
        if (codigo.isEmpty() || nombre.isEmpty() || precioTexto.isEmpty() || stockTexto.isEmpty()) {
            throw new IllegalArgumentException("Completa codigo, nombre, precio y stock.");
        }

        try {
            int id = requiereId ? Integer.parseInt(txtId.getText()) : 0;
            double precio = Double.parseDouble(precioTexto);
            int stock = Integer.parseInt(stockTexto);

            if (precio < 0 || stock < 0) {
                throw new IllegalArgumentException("Precio y stock no pueden ser negativos.");
            }

            return new Insumos(id, codigo, nombre, descripcion, precio, stock);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Precio y stock deben ser numeros validos.");
        }
    }

    private void limpiarFormulario() {
        txtId.setText("");
        txtCodigo.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        tablaInsumos.clearSelection();
        txtCodigo.requestFocus();
    }

    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Operacion correcta", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmInsumos().setVisible(true));
    }
    
    private void generarReporte() {
    // 1. Validamos que haya algo para reportar
    if (historialCambios.isEmpty()) {
        JOptionPane.showMessageDialog(this, 
            "No se han realizado cambios (altas, modificaciones o bajas) para exportar.", 
            "Reporte Vacío", 
            JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    // 2. Creamos el documento PDF
    Document documento = new Document();

    try {
        // 3. Le decimos a Java dónde guardar el archivo y cómo se va a llamar
        String rutaDestino = "Reporte_Auditoria_Insumos.pdf";
        PdfWriter.getInstance(documento, new FileOutputStream(rutaDestino));

        // 4. Abrimos el documento para empezar a escribirle
        documento.open();

        // 5. Agregamos el título
        documento.add(new Paragraph("=== REPORTE DE AUDITORÍA Y MOVIMIENTOS ===\n\n"));

        // 6. Recorremos nuestra bitácora y agregamos renglón por renglón
        for (String cambio : historialCambios) {
            documento.add(new Paragraph(cambio));
        }

        // 7. Cerramos el documento (¡Súper importante!)
        documento.close();

        // 8. Le avisamos al usuario que salió todo bien
        JOptionPane.showMessageDialog(this, 
            "¡El reporte PDF se generó correctamente en la carpeta del proyecto!", 
            "Éxito", 
            JOptionPane.INFORMATION_MESSAGE);

    } catch (Exception e) {
        // Si no se puede crear el PDF (ej. si el archivo ya estaba abierto y bloqueado)
        JOptionPane.showMessageDialog(this, 
            "Error al generar el PDF: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}
}
