package view;

import model.Vehiculo;
import persistencia.VehiculoDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class VehiculoView extends JFrame {

    private JTextField txtId, txtPlaca;
    private JComboBox<String> cmbTipo;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private VehiculoDAO dao = new VehiculoDAO();

    public VehiculoView() {
        setTitle("Gestión de Vehículos");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("Datos del Vehículo"));
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
        panelForm.add(new JLabel("Placa:"), gbc);
        gbc.gridx = 1;
        txtPlaca = new JTextField(20);
        panelForm.add(txtPlaca, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelForm.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1;
        cmbTipo = new JComboBox<>(new String[]{"CARRO", "MOTO", "MCNC"});
        panelForm.add(cmbTipo, gbc);

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

        modeloTabla = new DefaultTableModel(new String[]{"ID", "Placa", "Tipo"}, 0);
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
            List<Vehiculo> lista = dao.leerTodos();
            for (Vehiculo v : lista) {
                modeloTabla.addRow(new Object[]{v.getId(), v.getPlaca(), v.getTipo()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar: " + e.getMessage());
        }
    }

    private void cargarDatosFila() {
        int fila = tabla.getSelectedRow();
        txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
        txtPlaca.setText(modeloTabla.getValueAt(fila, 1).toString());
        cmbTipo.setSelectedItem(modeloTabla.getValueAt(fila, 2).toString());
    }

    private void agregar() {
        if (txtPlaca.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La placa es obligatoria");
            return;
        }
        try {
            Vehiculo v = new Vehiculo();
            v.setPlaca(txtPlaca.getText());
            v.setTipo((String) cmbTipo.getSelectedItem());
            dao.crear(v);
            cargarTabla();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Vehículo agregado");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void actualizar() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un vehículo para actualizar");
            return;
        }
        try {
            Vehiculo v = new Vehiculo();
            v.setId(Integer.parseInt(txtId.getText()));
            v.setPlaca(txtPlaca.getText());
            v.setTipo((String) cmbTipo.getSelectedItem());
            dao.actualizar(v);
            cargarTabla();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Vehículo actualizado");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void eliminar() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un vehículo para eliminar");
            return;
        }
        int opcion = JOptionPane.showConfirmDialog(this, "¿Está seguro?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            try {
                dao.eliminar(Integer.parseInt(txtId.getText()));
                cargarTabla();
                limpiarCampos();
                JOptionPane.showMessageDialog(this, "Vehículo eliminado");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtPlaca.setText("");
        cmbTipo.setSelectedIndex(0);
        tabla.clearSelection();
    }
}