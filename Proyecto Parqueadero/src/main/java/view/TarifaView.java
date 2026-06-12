package view;

import model.Tarifa;
import persistencia.TarifaDAO;

import javax.swing.*;
import javax.swing.border.TitledBorder;
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
  setTitle("Gesti\u00f3n de Tarifas");
  setSize(700, 500);
  setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
  setLocationRelativeTo(null);
  getContentPane().setBackground(new Color(0xA9CCE3));

  JPanel panelForm = new JPanel(new GridBagLayout());
  panelForm.setBackground(Color.WHITE);
  panelForm.setBorder(BorderFactory.createTitledBorder(
   BorderFactory.createLineBorder(new Color(0x85C1E9), 2),
   "Datos de la Tarifa",
   TitledBorder.LEFT, TitledBorder.TOP,
   new Font("Segoe UI", Font.BOLD, 13),
   new Color(0x2E86C1)
  ));
  GridBagConstraints gbc = new GridBagConstraints();
  gbc.insets = new Insets(6, 8, 6, 8);
  gbc.anchor = GridBagConstraints.WEST;

  JLabel lblId = new JLabel("ID:");
  lblId.setFont(new Font("Segoe UI", Font.BOLD, 13));
  gbc.gridx = 0; gbc.gridy = 0;
  panelForm.add(lblId, gbc);

  gbc.gridx = 1;
  txtId = new JTextField(10);
  txtId.setEditable(false);
  txtId.setFont(new Font("Segoe UI", Font.PLAIN, 13));
  txtId.setBackground(new Color(0xF8F9F9));
  panelForm.add(txtId, gbc);

  JLabel lblTipo = new JLabel("Tipo Veh\u00edculo:");
  lblTipo.setFont(new Font("Segoe UI", Font.BOLD, 13));
  gbc.gridx = 0; gbc.gridy = 1;
  panelForm.add(lblTipo, gbc);

  gbc.gridx = 1;
  txtTipo = new JTextField(20);
  txtTipo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
  txtTipo.setEditable(false);
  txtTipo.setBackground(new Color(0xF8F9F9));
  txtTipo.setBorder(BorderFactory.createCompoundBorder(
   BorderFactory.createLineBorder(new Color(0x85C1E9), 2),
   BorderFactory.createEmptyBorder(4, 8, 4, 8)
  ));
  panelForm.add(txtTipo, gbc);

  JLabel lblHora = new JLabel("Tarifa por Hora:");
  lblHora.setFont(new Font("Segoe UI", Font.BOLD, 13));
  gbc.gridx = 0; gbc.gridy = 2;
  panelForm.add(lblHora, gbc);

  gbc.gridx = 1;
  txtHora = new JTextField(20);
  txtHora.setFont(new Font("Segoe UI", Font.PLAIN, 14));
  txtHora.setBorder(BorderFactory.createCompoundBorder(
   BorderFactory.createLineBorder(new Color(0x85C1E9), 2),
   BorderFactory.createEmptyBorder(4, 8, 4, 8)
  ));
  panelForm.add(txtHora, gbc);

  JLabel lblDia = new JLabel("Tarifa por D\u00eda:");
  lblDia.setFont(new Font("Segoe UI", Font.BOLD, 13));
  gbc.gridx = 0; gbc.gridy = 3;
  panelForm.add(lblDia, gbc);

  gbc.gridx = 1;
  txtDia = new JTextField(20);
  txtDia.setFont(new Font("Segoe UI", Font.PLAIN, 14));
  txtDia.setBorder(BorderFactory.createCompoundBorder(
   BorderFactory.createLineBorder(new Color(0x85C1E9), 2),
   BorderFactory.createEmptyBorder(4, 8, 4, 8)
  ));
  panelForm.add(txtDia, gbc);

  JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 6));
  panelBotones.setBackground(Color.WHITE);
  panelBotones.setBorder(BorderFactory.createEmptyBorder(4, 10, 10, 10));

  JButton btnActualizar = new JButton("Actualizar Tarifa");
  btnActualizar.setFont(new Font("Segoe UI", Font.BOLD, 13));
  btnActualizar.setForeground(Color.WHITE);
  btnActualizar.setBackground(new Color(0x2E86C1));
  btnActualizar.setFocusPainted(false);
  btnActualizar.setBorderPainted(false);
  btnActualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
  btnActualizar.setPreferredSize(new Dimension(150, 34));
  btnActualizar.addActionListener(e -> actualizar());

  JButton btnLimpiar = new JButton("Limpiar Campos");
  btnLimpiar.setFont(new Font("Segoe UI", Font.BOLD, 13));
  btnLimpiar.setForeground(Color.WHITE);
  btnLimpiar.setBackground(new Color(0x808B96));
  btnLimpiar.setFocusPainted(false);
  btnLimpiar.setBorderPainted(false);
  btnLimpiar.setCursor(new Cursor(Cursor.HAND_CURSOR));
  btnLimpiar.setPreferredSize(new Dimension(150, 34));
  btnLimpiar.addActionListener(e -> limpiarCampos());

  panelBotones.add(btnActualizar);
  panelBotones.add(btnLimpiar);

  modeloTabla = new DefaultTableModel(new String[]{"ID", "Tipo", "Tarifa Hora", "Tarifa D\u00eda"}, 0) {
   @Override
   public boolean isCellEditable(int row, int col) { return false; }
  };
  tabla = new JTable(modeloTabla);
  tabla.setFillsViewportHeight(true);
  tabla.setRowHeight(26);
  tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
  tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
  tabla.getTableHeader().setBackground(new Color(0xEBF5FB));
  tabla.getTableHeader().setForeground(new Color(0x1A5276));
  tabla.setSelectionBackground(new Color(0xA9CCE3));
  tabla.getSelectionModel().addListSelectionListener(e -> {
   if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
    cargarDatosFila();
   }
  });
  JScrollPane scroll = new JScrollPane(tabla);
  scroll.getViewport().setBackground(Color.WHITE);
  scroll.setBorder(BorderFactory.createCompoundBorder(
   BorderFactory.createEmptyBorder(0, 8, 0, 8),
   scroll.getBorder()
  ));

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
   if (modeloTabla.getRowCount() > 0) {
    tabla.setRowSelectionInterval(0, 0);
   }
  } catch (SQLException e) {
   JOptionPane.showMessageDialog(this, "Error al cargar: " + e.getMessage());
  }
 }

 private void cargarDatosFila() {
  int fila = tabla.getSelectedRow();
  if (fila < 0) return;
  try {
   txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
   txtTipo.setText(modeloTabla.getValueAt(fila, 1).toString());
   Object horaObj = modeloTabla.getValueAt(fila, 2);
   txtHora.setText(horaObj instanceof BigDecimal ? ((BigDecimal) horaObj).toPlainString() : horaObj.toString());
   Object diaObj = modeloTabla.getValueAt(fila, 3);
   txtDia.setText(diaObj instanceof BigDecimal ? ((BigDecimal) diaObj).toPlainString() : diaObj.toString());
  } catch (Exception ex) {
   JOptionPane.showMessageDialog(this,
    "Error al cargar datos de la fila: " + ex.getMessage(),
    "Error", JOptionPane.ERROR_MESSAGE);
  }
 }

 private void actualizar() {
  if (txtId.getText().isEmpty()) {
   JOptionPane.showMessageDialog(this,
    "Seleccione una tarifa de la tabla para actualizar.",
    "Advertencia", JOptionPane.WARNING_MESSAGE);
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
   JOptionPane.showMessageDialog(this,
    "Tarifa actualizada correctamente.",
    "Actualizado", JOptionPane.INFORMATION_MESSAGE);
  } catch (NumberFormatException e) {
   JOptionPane.showMessageDialog(this,
    "Ingrese valores num\u00e9ricos v\u00e1lidos.",
    "Error", JOptionPane.ERROR_MESSAGE);
  } catch (SQLException e) {
   JOptionPane.showMessageDialog(this,
    "Error: " + e.getMessage(),
    "Error", JOptionPane.ERROR_MESSAGE);
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
