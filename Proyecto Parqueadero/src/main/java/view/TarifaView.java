package view;

import model.Tarifa;
import persistencia.TarifaDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class TarifaView extends JFrame {

    private JTextField txtId, txtTipo, txtHora, txtDia;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private TarifaDAO dao = new TarifaDAO();

    public TarifaView() {
        setTitle("Gestión de Tarifas");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("Datos de la Tarifa"));
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
        panelForm.add(new JLabel("Tipo Vehículo:"), gbc);
        gbc.gridx = 1;
        txtTipo = new JTextField(20);
        panelForm.add(txtTipo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelForm.add(new JLabel("Tarifa por Hora:"), gbc);
        gbc.gridx = 1;
        txtHora = new JTextField(20);
        panelForm.add(txtHora, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panelForm.add(new JLabel("Tarifa por Día:"), gbc);
        gbc.gridx = 1;
        txtDia = new JTextField(20);
        panelForm.add(txtDia, gbc);

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

        modeloTabla = new DefaultTableModel(new String[]{"ID", "Tipo", "Tarifa Hora", "Tarifa Día"}, 0);
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
            List<Tarifa> lista = dao.leerTodos();
            for (Tarifa t : lista) {
                modeloTabla.addRow(new Object[]{
                    t.getId(), t.getTipoVehiculo(), 
                    t.getTarifaHora(), t.getTarifaDia()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar: " + e.getMessage());
        }
    }

    private void cargarDatosFila() {
        int fila = tabla.getSelectedRow();
        txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
        txtTipo.setText(modeloTabla.getValueAt(fila, 1).toString());
        txtHora.setText(modeloTabla.getValueAt(fila, 2).toString());
        txtDia.setText(modeloTabla.getValueAt(fila, 3).toString());
    }

    private void agregar() {
        if (txtTipo.getText().isEmpty() || txtHora.getText().isEmpty() || txtDia.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios");
            return;
        }
        try {
            Tarifa t = new Tarifa();
            t.setTipoVehiculo(txtTipo.getText());
            t.setTarifaHora(new BigDecimal(txtHora.getText()));
            t.setTarifaDia(new BigDecimal(txtDia.getText()));
            dao.crear(t);
            cargarTabla();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Tarifa agregada");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese valores numéricos válidos");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void actualizar() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione una tarifa para actualizar");
            return;
        }
        try {
            Tarifa t = new Tarifa();
            t.setId(Integer.parseInt(txtId.getText()));
            t.setTipoVehiculo(txtTipo.getText());
            t.setTarifaHora(new BigDecimal(txtHora.getText()));
            t.setTarifaDia(new BigDecimal(txtDia.getText()));
            dao.actualizar(t);
            cargarTabla();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Tarifa actualizada");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese valores numéricos válidos");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void eliminar() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione una tarifa para eliminar");
            return;
        }
        int opcion = JOptionPane.showConfirmDialog(this, "¿Está seguro?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            try {
                dao.eliminar(Integer.parseInt(txtId.getText()));
                cargarTabla();
                limpiarCampos();
                JOptionPane.showMessageDialog(this, "Tarifa eliminada");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtTipo.setText("");
        txtHora.setText("");
        txtDia.setText("");
        tabla.clearSelection();
    }
}