package view;

import javax.swing.*;
import java.awt.*;

public class PrincipalView extends JFrame {

 public PrincipalView() {
  setTitle("Sistema de Parqueadero - Administraci\u00f3n");
  setSize(500, 380);
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  setLocationRelativeTo(null);
  getContentPane().setBackground(new Color(0xA9CCE3));
  setLayout(new BorderLayout(10, 10));

  // T\u00edtulo
  JPanel panelTitulo = new JPanel();
  panelTitulo.setBackground(new Color(0xA9CCE3));
  JLabel lblTitulo = new JLabel("Panel de Administraci\u00f3n", SwingConstants.CENTER);
  lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
  lblTitulo.setForeground(new Color(0x1A5276));
  panelTitulo.add(lblTitulo);
  add(panelTitulo, BorderLayout.NORTH);

  // Botones principales
  JPanel panelBotones = new JPanel(new GridLayout(4, 1, 10, 10));
  panelBotones.setBackground(Color.WHITE);
  panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

  JButton btnUsuarios = new JButton("Gesti\u00f3n de Usuarios");
  estilizarBotonMenu(btnUsuarios, new Color(0x2E86C1));
  btnUsuarios.addActionListener(e -> {
   UsuarioView view = new UsuarioView();
   view.setVisible(true);
  });

  JButton btnVehiculos = new JButton("Gesti\u00f3n de Veh\u00edculos");
  estilizarBotonMenu(btnVehiculos, new Color(0x27AE60));
  btnVehiculos.addActionListener(e -> {
   VehiculoView view = new VehiculoView();
   view.setVisible(true);
  });

  JButton btnTarifas = new JButton("Gesti\u00f3n de Tarifas");
  estilizarBotonMenu(btnTarifas, new Color(0xE67E22));
  btnTarifas.addActionListener(e -> {
   TarifaView view = new TarifaView();
   view.setVisible(true);
  });

  JButton btnSalir = new JButton("Cerrar Sesi\u00f3n y Salir");
  estilizarBotonMenu(btnSalir, new Color(0xE74C3C));
  btnSalir.addActionListener(e -> {
   int opcion = JOptionPane.showConfirmDialog(this,
    "\u00BFEst\u00e1 seguro que desea salir?",
    "Confirmar salida", JOptionPane.YES_NO_OPTION);
   if (opcion == JOptionPane.YES_OPTION) {
    System.exit(0);
   }
  });

  panelBotones.add(btnUsuarios);
  panelBotones.add(btnVehiculos);
  panelBotones.add(btnTarifas);
  panelBotones.add(btnSalir);

  add(panelBotones, BorderLayout.CENTER);

  // Pie
  JLabel lblPie = new JLabel("\u00a9 2026 - Parqueadero PRO", SwingConstants.CENTER);
  lblPie.setFont(new Font("Segoe UI", Font.PLAIN, 11));
  lblPie.setForeground(new Color(0x5D6D7E));
  add(lblPie, BorderLayout.SOUTH);
 }

 private void estilizarBotonMenu(JButton btn, Color fondo) {
  btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
  btn.setForeground(Color.WHITE);
  btn.setBackground(fondo);
  btn.setFocusPainted(false);
  btn.setBorderPainted(false);
  btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
  btn.setPreferredSize(new Dimension(300, 45));
 }

 // Punto de entrada: SIEMPRE inicia por LoginView
 public static void main(String[] args) {
  java.awt.EventQueue.invokeLater(() -> {
   new LoginView().setVisible(true);
  });
 }
}
