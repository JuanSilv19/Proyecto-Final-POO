package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import model.Usuario;
import persistencia.UsuarioDAO;

public class LoginView extends JFrame {

 private JTextField txtUsuario;
 private JPasswordField txtClave;
 private final UsuarioDAO usuarioDAO = new UsuarioDAO();

 public LoginView() {
  setTitle("Parqueadero PRO - Acceso");
  setSize(420, 260);
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  setLocationRelativeTo(null);
  setResizable(false);

  getContentPane().setBackground(new Color(0xA9CCE3));
  setLayout(new BorderLayout());

  JPanel panelCentral = new JPanel(new GridBagLayout());
  panelCentral.setBackground(Color.WHITE);
  panelCentral.setBorder(BorderFactory.createCompoundBorder(
   BorderFactory.createLineBorder(new Color(0x85C1E9), 2),
   new EmptyBorder(25, 30, 25, 30)
  ));

  GridBagConstraints gbc = new GridBagConstraints();
  gbc.insets = new Insets(8, 8, 8, 8);
  gbc.fill = GridBagConstraints.HORIZONTAL;
  gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;

  JLabel lblTitulo = new JLabel("INICIAR SESI\u00d3N", SwingConstants.CENTER);
  lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
  lblTitulo.setForeground(new Color(0x1A5276));
  panelCentral.add(lblTitulo, gbc);

  gbc.gridwidth = 1;
  gbc.gridy = 1;
  gbc.gridx = 0;
  JLabel lblUsuario = new JLabel("Usuario (email):");
  lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 13));
  panelCentral.add(lblUsuario, gbc);

  gbc.gridx = 1;
  txtUsuario = new JTextField(18);
  txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
  panelCentral.add(txtUsuario, gbc);

  gbc.gridy = 2;
  gbc.gridx = 0;
  JLabel lblClave = new JLabel("Contrase\u00f1a:");
  lblClave.setFont(new Font("Segoe UI", Font.BOLD, 13));
  panelCentral.add(lblClave, gbc);

  gbc.gridx = 1;
  txtClave = new JPasswordField(18);
  txtClave.setFont(new Font("Segoe UI", Font.PLAIN, 14));
  txtClave.setEchoChar('\u25cf');
  panelCentral.add(txtClave, gbc);

  gbc.gridy = 3;
  gbc.gridx = 0;
  gbc.gridwidth = 2;
  gbc.insets = new Insets(16, 8, 8, 8);

  JPanel panelBotones = new JPanel(new GridLayout(1, 2, 12, 0));
  panelBotones.setBackground(Color.WHITE);

  JButton btnIngresar = new JButton("INGRESAR");
  estilizarBoton(btnIngresar, new Color(0x2E86C1));
  btnIngresar.addActionListener(e -> autenticar());

  JButton btnSalir = new JButton("SALIR");
  estilizarBoton(btnSalir, new Color(0xE74C3C));
  btnSalir.addActionListener(e -> System.exit(0));

  panelBotones.add(btnIngresar);
  panelBotones.add(btnSalir);
  panelCentral.add(panelBotones, gbc);

  add(panelCentral, BorderLayout.CENTER);

  txtClave.addActionListener(e -> autenticar());

  addWindowListener(new WindowAdapter() {
   @Override
   public void windowClosing(WindowEvent e) {
    System.exit(0);
   }
  });
 }

 private void estilizarBoton(JButton btn, Color fondo) {
  btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
  btn.setForeground(Color.WHITE);
  btn.setBackground(fondo);
  btn.setFocusPainted(false);
  btn.setBorderPainted(false);
  btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
  btn.setPreferredSize(new Dimension(140, 35));
 }

 private void autenticar() {
  String email = txtUsuario.getText().trim().toLowerCase();
  String clave = new String(txtClave.getPassword()).trim();

  if (email.isEmpty() || clave.isEmpty()) {
   JOptionPane.showMessageDialog(this,
    "Por favor ingrese usuario y contrase\u00f1a.",
    "Campos Vac\u00edos", JOptionPane.WARNING_MESSAGE);
   return;
  }

  try {
   Usuario usuario = usuarioDAO.validarLogin(email, clave);
   if (usuario != null) {
    abrirVenta(usuario.isClienteFrecuente(), usuario);
   } else {
    // Fallback credenciales de prueba sin BD
    if ("admin".equals(email) && "1234".equals(clave)) {
     usuario = new Usuario(0, "Administrador", "Sistema", "0000000", email, true);
     abrirVenta(true, usuario);
    } else if ("cliente".equals(email) && "5678".equals(clave)) {
     usuario = new Usuario(0, "Mar\u00eda", "L\u00f3pez", "1111111", email, false);
     abrirVenta(false, usuario);
    } else {
     JOptionPane.showMessageDialog(this,
      "Credenciales inv\u00e1lidas. Verifique su usuario y contrase\u00f1a.",
      "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
     txtClave.setText("");
     txtClave.requestFocus();
    }
   }
  } catch (Exception ex) {
   // Sin conexion a BD: probar credenciales hardcodeadas
   if ("admin".equals(email) && "1234".equals(clave)) {
    Usuario u = new Usuario(0, "Administrador", "Sistema", "0000000", email, true);
    abrirVenta(true, u);
   } else if ("cliente".equals(email) && "5678".equals(clave)) {
    Usuario u = new Usuario(0, "Mar\u00eda", "L\u00f3pez", "1111111", email, false);
    abrirVenta(false, u);
   } else {
    JOptionPane.showMessageDialog(this,
     "Error de conexi\u00f3n con la base de datos.\n" + ex.getMessage(),
     "Error", JOptionPane.ERROR_MESSAGE);
   }
  }
 }

 private void abrirVenta(boolean esAdmin, Usuario usuario) {
  this.dispose();

  SwingUtilities.invokeLater(() -> {
   // Ambos roles abren OperationalView: admin con privilegios, cliente restringido
   OperationalView operativa = new OperationalView(esAdmin, usuario.getNombre() + " " + usuario.getApellido());
   operativa.setVisible(true);
  });
 }
}
