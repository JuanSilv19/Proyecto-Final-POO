package view;

import model.Usuario;
import persistencia.UsuarioDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class UsuarioView extends JFrame {

    private JTextField txtId, txtNombre, txtApellido, txtTelefono, txtEmail;
    private JCheckBox chkFrecuente;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private UsuarioDAO dao = new UsuarioDAO();

    public UsuarioView() {
        setTitle("Gestión de Usuarios");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("Datos del Usuario"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        txtId = new JTextField(10);
        txtId.setEditable(false);
        panelForm.add(txtId, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        txtNombre = new JTextField(20);
        panelForm.add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelForm.add(new JLabel("Apellido:"), gbc);
        gbc.gridx = 1;
        txtApellido = new JTextField(20);
        panelForm.add(txtApellido, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panelForm.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1;
        txtTelefono = new JTextField(20);
        panelForm.add(txtTelefono, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panelForm.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtEmail = new JTextField(20);
        panelForm.add(txtEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panelForm.add(new JLabel("Cliente Frecuente:"), gbc);
        gbc.gridx = 1;
        chkFrecuente = new JCheckBox();
        panelForm.add(chkFrecuente, gbc);

        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton btnAgregar = new JButton("Agregar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");

        btnAgregar.addActionListener(e -> agregar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiarCampos());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre", "Apellido", "Teléfono", "Email", "Frecuente"}, 0);
        tabla = new JTable(modeloTabla);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                cargarDatosFila();
            }
        });
        JScrollPane scroll = new JScrollPane(tabla);

        add(panelForm, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        cargarTabla();
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        try {
            List<Usuario> lista = dao.leerTodos();
            for (Usuario u : lista) {
                modeloTabla.addRow(new Object[]{
                    u.getId(), u.getNombre(), u.getApellido(), 
                    u.getTelefono(), u.getEmail(), u.isClienteFrecuente() ? "Sí" : "No"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar: " + e.getMessage());
        }
    }

    private void cargarDatosFila() {
        int fila = tabla.getSelectedRow();
        txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
        txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
        txtApellido.setText(modeloTabla.getValueAt(fila, 2).toString());
        txtTelefono.setText(modeloTabla.getValueAt(fila, 3).toString());
        txtEmail.setText(modeloTabla.getValueAt(fila, 4).toString());
        chkFrecuente.setSelected(modeloTabla.getValueAt(fila, 5).toString().equals("Sí"));
    }

    private void agregar() {
        if (txtNombre.getText().isEmpty() || txtApellido.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre y apellido son obligatorios");
            return;
        }
        try {
            Usuario u = new Usuario(
                txtNombre.getText(), txtApellido.getText(), 
                txtTelefono.getText(), txtEmail.getText(), 
                chkFrecuente.isSelected()
            );
            dao.crear(u);
            cargarTabla();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Usuario agregado");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void actualizar() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para actualizar");
            return;
        }
        try {
            Usuario u = new Usuario(
                Integer.parseInt(txtId.getText()),
                txtNombre.getText(), txtApellido.getText(),
                txtTelefono.getText(), txtEmail.getText(),
                chkFrecuente.isSelected()
            );
            dao.actualizar(u);
            cargarTabla();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Usuario actualizado");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void eliminar() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para eliminar");
            return;
        }
        int opcion = JOptionPane.showConfirmDialog(this, "¿Está seguro?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            try {
                dao.eliminar(Integer.parseInt(txtId.getText()));
                cargarTabla();
                limpiarCampos();
                JOptionPane.showMessageDialog(this, "Usuario eliminado");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
        chkFrecuente.setSelected(false);
        tabla.clearSelection();
    }
}