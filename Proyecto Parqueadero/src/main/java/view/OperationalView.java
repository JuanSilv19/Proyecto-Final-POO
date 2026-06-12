package view;

import java.awt.*;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import model.Registro;
import model.Tarifa;
import model.Vehiculo;
import persistencia.RegistroDAO;
import persistencia.TarifaDAO;
import persistencia.VehiculoDAO;

public class OperationalView extends JFrame {

 private final RegistroDAO registroDAO = new RegistroDAO();
 private final TarifaDAO tarifaDAO = new TarifaDAO();
 private final VehiculoDAO vehiculoDAO = new VehiculoDAO();

 // Marcador superior (solo cliente)
 private JLabel lblPlaca;
 private JLabel lblValorAPagar;

 // Tabla (solo admin)
 private JTable tblVehiculos;
 private DefaultTableModel tableModel;
 private JScrollPane scrollTabla;

 // Panel CRUD Admin
 private JTextField txtAdminPlaca;
 private JComboBox<String> cmbAdminTipo;
 private JButton btnActualizarVehiculo;
 private JButton btnEliminarVehiculo;
 private JButton btnLimpiarForm;

 // Panel liquidacion (admin)
 private JLabel lblMinutosBanner;
 private JLabel lblEntrada;
 private JLabel lblSalida;
 private JLabel lblValorPagarLiq;
 private JButton btnRegistrarSalida;

 // Campo busqueda cliente
 private JTextField txtBuscarPlaca;

 // Campo ingreso cliente
 private JTextField txtPlacaIngreso;
 private JComboBox<String> cmbTipoVehiculoIngreso;
 private JButton btnIngresarVehiculo;

 // Barra inferior
 private JLabel lblCarrosCount;
 private JLabel lblMotosCount;
 private JLabel lblFechaHoraSistema;

 // Barra admin
 private JButton btnCierreCaja;

 // Rol
 private final boolean esAdmin;
 private final String nombreUsuario;

 private Timer timer;
 private Timer refreshTimer;
 private final DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm:ss");

 // ==================== TARIFAS COLOMBIA ====================
 private static final BigDecimal TARIFA_FRACCION = new BigDecimal("3250.00");
 private static final BigDecimal TARIFA_HORA = new BigDecimal("5450.00");
 private static final int MINUTOS_FRACCION = 15;

 // ==================== CONSTRUCTORES ====================
 public OperationalView() {
  this(true, null);
 }

 public OperationalView(boolean esAdmin, String nombreUsuario) {
  this.esAdmin = esAdmin;
  this.nombreUsuario = (nombreUsuario != null && !nombreUsuario.isBlank())
      ? nombreUsuario.trim()
      : (esAdmin ? "Administrador" : "Cliente");

  setTitle("Parqueadero PRO - Vista Operativa" + (esAdmin ? " [ADMIN]" : " [CLIENTE]"));
  setSize(1300, 750);
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  setLocationRelativeTo(null);
  getContentPane().setBackground(new Color(0xA9CCE3));

  initComponents();

  if (esAdmin) {
   loadCurrentVehicles();
  }
  startTimer();
 }

 // ==================== INICIALIZACION ====================
 private void initComponents() {
  setLayout(new BorderLayout(10, 10));

  if (!esAdmin) {
   add(createTopMarkerPanel(), BorderLayout.NORTH);
  }

  JPanel centralPanel = new JPanel(new GridLayout(1, 2, 12, 10));
  centralPanel.setOpaque(false);
  centralPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

  if (esAdmin) {
   centralPanel.add(createVehiclesTablePanel());
   centralPanel.add(createAdminCrudPanel());
  } else {
   centralPanel.add(createLeftPanelCliente());
   centralPanel.add(createRightPanelCliente());
  }
  add(centralPanel, BorderLayout.CENTER);

  JPanel southWrapper = new JPanel(new BorderLayout(0, 4));
  southWrapper.setOpaque(false);
  southWrapper.add(createBottomBar(), BorderLayout.CENTER);
  if (esAdmin) {
   southWrapper.add(createAdminActionBar(), BorderLayout.SOUTH);
  }
  add(southWrapper, BorderLayout.SOUTH);

  if (esAdmin && tblVehiculos != null) {
   tblVehiculos.getSelectionModel().addListSelectionListener(e -> {
    if (!e.getValueIsAdjusting()) {
     int row = tblVehiculos.getSelectedRow();
     if (row >= 0) {
      cargarDatosFilaVehiculo(row);
     }
    }
   });
  }
 }

 // ==================== PANEL SUPERIOR (SOLO CLIENTE) ====================
 private JPanel createTopMarkerPanel() {
  JPanel panel = new JPanel(new GridLayout(1, 2, 12, 0));
  panel.setBackground(new Color(0xA9CCE3));
  panel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

  JPanel placaBlock = new JPanel(new BorderLayout());
  placaBlock.setBackground(new Color(0xFCF3CF));
  placaBlock.setBorder(BorderFactory.createCompoundBorder(
   BorderFactory.createLineBorder(new Color(0xD4AC0D), 2),
   BorderFactory.createEmptyBorder(12, 20, 12, 20)
  ));

  JPanel placaInner = new JPanel(new BorderLayout());
  placaInner.setBackground(new Color(0xFCF3CF));
  JLabel lblTituloPlaca = new JLabel("PLACA", SwingConstants.CENTER);
  lblTituloPlaca.setFont(new Font("Segoe UI", Font.BOLD, 18));
  lblTituloPlaca.setForeground(new Color(0x7D6608));
  lblTituloPlaca.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

  lblPlaca = new JLabel("--", SwingConstants.CENTER);
  lblPlaca.setFont(new Font("Segoe UI", Font.BOLD, 64));
  lblPlaca.setForeground(new Color(0x1A1A1A));

  placaInner.add(lblTituloPlaca, BorderLayout.NORTH);
  placaInner.add(lblPlaca, BorderLayout.CENTER);
  placaBlock.add(placaInner, BorderLayout.CENTER);

  JPanel valorBlock = new JPanel(new BorderLayout());
  valorBlock.setBackground(new Color(0xD5F5E3));
  valorBlock.setBorder(BorderFactory.createCompoundBorder(
   BorderFactory.createLineBorder(new Color(0x27AE60), 2),
   BorderFactory.createEmptyBorder(12, 20, 12, 20)
  ));

  JPanel valorInner = new JPanel(new BorderLayout());
  valorInner.setBackground(new Color(0xD5F5E3));
  JLabel lblTituloValor = new JLabel("VALOR A PAGAR", SwingConstants.CENTER);
  lblTituloValor.setFont(new Font("Segoe UI", Font.BOLD, 18));
  lblTituloValor.setForeground(new Color(0x1E8449));
  lblTituloValor.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

  lblValorAPagar = new JLabel("$0", SwingConstants.CENTER);
  lblValorAPagar.setFont(new Font("Segoe UI", Font.BOLD, 64));
  lblValorAPagar.setForeground(new Color(0x145A32));

  valorInner.add(lblTituloValor, BorderLayout.NORTH);
  valorInner.add(lblValorAPagar, BorderLayout.CENTER);
  valorBlock.add(valorInner, BorderLayout.CENTER);

  panel.add(placaBlock);
  panel.add(valorBlock);
  return panel;
 }

 // ==================== PANEL IZQUIERDO CLIENTE ====================
 private JPanel createLeftPanelCliente() {
  JPanel panel = new JPanel();
  panel.setBackground(new Color(0xA9CCE3));
  panel.setBorder(BorderFactory.createLineBorder(new Color(0xD5D8DC), 1));
  return panel;
 }

 // ==================== TABLA VEHICULOS (ADMIN) ====================
 private JPanel createVehiclesTablePanel() {
  JPanel panel = new JPanel(new BorderLayout());
  panel.setBackground(Color.WHITE);
  panel.setBorder(BorderFactory.createTitledBorder(
   BorderFactory.createLineBorder(new Color(0x85C1E9), 2),
   "Veh\u00edculos en Parqueadero",
   TitledBorder.LEFT, TitledBorder.TOP,
   new Font("Segoe UI", Font.BOLD, 14),
   new Color(0x2E86C1)
  ));

  String[] columnas = {"PLACA", "ENTRADA", "HORA", "TARIFA", "RECIBO"};
  tableModel = new DefaultTableModel(columnas, 0) {
   @Override
   public boolean isCellEditable(int row, int col) {
    return false;
   }
  };

  tblVehiculos = new JTable(tableModel);
  tblVehiculos.setFillsViewportHeight(true);
  tblVehiculos.setRowHeight(28);
  tblVehiculos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
  tblVehiculos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
  tblVehiculos.getTableHeader().setBackground(new Color(0xEBF5FB));
  tblVehiculos.getTableHeader().setForeground(new Color(0x1A5276));
  tblVehiculos.setSelectionBackground(new Color(0xA9CCE3));
  tblVehiculos.setSelectionForeground(Color.BLACK);
  tblVehiculos.setGridColor(new Color(0xD6EAF8));
  tblVehiculos.getColumnModel().getColumn(0).setPreferredWidth(90);
  tblVehiculos.getColumnModel().getColumn(1).setPreferredWidth(100);
  tblVehiculos.getColumnModel().getColumn(2).setPreferredWidth(80);
  tblVehiculos.getColumnModel().getColumn(3).setPreferredWidth(90);
  tblVehiculos.getColumnModel().getColumn(4).setPreferredWidth(80);

  scrollTabla = new JScrollPane(tblVehiculos);
  scrollTabla.getViewport().setBackground(Color.WHITE);
  scrollTabla.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

  panel.add(scrollTabla, BorderLayout.CENTER);
  return panel;
 }

 // ==================== PANEL CRUD ADMIN ====================
 private JPanel createAdminCrudPanel() {
  JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
  mainPanel.setBackground(Color.WHITE);
  mainPanel.setBorder(BorderFactory.createTitledBorder(
   BorderFactory.createLineBorder(new Color(0x85C1E9), 2),
   "Control de Veh\u00edculos",
   TitledBorder.LEFT, TitledBorder.TOP,
   new Font("Segoe UI", Font.BOLD, 14),
   new Color(0x2E86C1)
  ));

  // Formulario
  JPanel formPanel = new JPanel(new GridBagLayout());
  formPanel.setBackground(Color.WHITE);
  GridBagConstraints gbc = new GridBagConstraints();
  gbc.insets = new Insets(6, 8, 6, 8);
  gbc.fill = GridBagConstraints.HORIZONTAL;
  gbc.anchor = GridBagConstraints.WEST;

  gbc.gridx = 0; gbc.gridy = 0;
  JLabel lblPlacaForm = new JLabel("Placa:");
  lblPlacaForm.setFont(new Font("Segoe UI", Font.BOLD, 13));
  formPanel.add(lblPlacaForm, gbc);

  gbc.gridx = 1; gbc.weightx = 1.0;
  txtAdminPlaca = new JTextField(12);
  txtAdminPlaca.setFont(new Font("Segoe UI", Font.PLAIN, 14));
  txtAdminPlaca.setBorder(BorderFactory.createCompoundBorder(
   BorderFactory.createLineBorder(new Color(0x85C1E9), 2),
   BorderFactory.createEmptyBorder(4, 8, 4, 8)
  ));
  formPanel.add(txtAdminPlaca, gbc);

  gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
  JLabel lblTipoForm = new JLabel("Tipo:");
  lblTipoForm.setFont(new Font("Segoe UI", Font.BOLD, 13));
  formPanel.add(lblTipoForm, gbc);

  gbc.gridx = 1; gbc.weightx = 1.0;
  cmbAdminTipo = new JComboBox<>(new String[]{"Autom\u00f3vil", "Motocicleta"});
  cmbAdminTipo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
  cmbAdminTipo.setBackground(Color.WHITE);
  formPanel.add(cmbAdminTipo, gbc);

   // Botones CRUD
   JPanel btnPanel = new JPanel(new GridLayout(1, 3, 8, 8));
   btnPanel.setBackground(Color.WHITE);
   btnPanel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

   btnActualizarVehiculo = createStyledButton("Actualizar", new Color(0x2E86C1));
   btnEliminarVehiculo = createStyledButton("Eliminar", new Color(0xE74C3C));
   btnLimpiarForm = createStyledButton("Limpiar", new Color(0x808B96));

   btnActualizarVehiculo.addActionListener(e -> actualizarVehiculoAdmin());
   btnEliminarVehiculo.addActionListener(e -> eliminarRegistroAdmin());
   btnLimpiarForm.addActionListener(e -> limpiarFormularioAdmin());

   btnPanel.add(btnActualizarVehiculo);
   btnPanel.add(btnEliminarVehiculo);
   btnPanel.add(btnLimpiarForm);

  // Seccion liquidacion
  JPanel liqPanel = new JPanel(new BorderLayout(0, 6));
  liqPanel.setBackground(new Color(0xEBF5FB));
  liqPanel.setBorder(BorderFactory.createCompoundBorder(
   BorderFactory.createLineBorder(new Color(0x85C1E9), 1),
   BorderFactory.createEmptyBorder(10, 12, 10, 12)
  ));

  JLabel lblLiqTitle = new JLabel("Liquidaci\u00f3n de Salida", SwingConstants.CENTER);
  lblLiqTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
  lblLiqTitle.setForeground(new Color(0x1A5276));

  JPanel liqCampos = new JPanel(new GridLayout(2, 2, 6, 4));
  liqCampos.setOpaque(false);
  JLabel lblEntradaTitle = new JLabel("Entrada:");
  lblEntradaTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
  liqCampos.add(lblEntradaTitle);
  lblEntrada = new JLabel("-- : -- : --");
  lblEntrada.setFont(new Font("Segoe UI", Font.PLAIN, 13));
  liqCampos.add(lblEntrada);
  JLabel lblSalidaTitle = new JLabel("Salida:");
  lblSalidaTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
  liqCampos.add(lblSalidaTitle);
  lblSalida = new JLabel("-- : -- : --");
  lblSalida.setFont(new Font("Segoe UI", Font.PLAIN, 13));
  liqCampos.add(lblSalida);

   lblMinutosBanner = new JLabel("-- Minutos", SwingConstants.CENTER);
   lblMinutosBanner.setFont(new Font("Segoe UI", Font.BOLD, 18));
   lblMinutosBanner.setForeground(new Color(0x922B21));

   lblValorPagarLiq = new JLabel("Valor a Pagar: $0", SwingConstants.CENTER);
   lblValorPagarLiq.setFont(new Font("Segoe UI", Font.BOLD, 14));
   lblValorPagarLiq.setForeground(new Color(0x145A32));
   lblValorPagarLiq.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

   btnRegistrarSalida = createStyledButton("Registrar Salida", new Color(0xE67E22));
  btnRegistrarSalida.addActionListener(e -> calcularImprimirSalida());

  liqPanel.add(lblLiqTitle, BorderLayout.NORTH);
  liqPanel.add(liqCampos, BorderLayout.CENTER);
  liqPanel.add(lblMinutosBanner, BorderLayout.SOUTH);

  // Ensamblar
  JPanel topSection = new JPanel(new BorderLayout(0, 12));
  topSection.setBackground(Color.WHITE);
  topSection.add(formPanel, BorderLayout.NORTH);
  topSection.add(btnPanel, BorderLayout.CENTER);

  JPanel liqWrapper = new JPanel(new BorderLayout(0, 4));
  liqWrapper.setBackground(new Color(0xEBF5FB));
  liqWrapper.add(liqPanel, BorderLayout.CENTER);
  liqWrapper.add(lblValorPagarLiq, BorderLayout.SOUTH);

  mainPanel.add(topSection, BorderLayout.NORTH);
  mainPanel.add(liqWrapper, BorderLayout.CENTER);
  mainPanel.add(btnRegistrarSalida, BorderLayout.SOUTH);

  return mainPanel;
 }

 // ==================== BARRA ACCIONES ADMIN ====================
 private JPanel createAdminActionBar() {
  JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
  panel.setBackground(new Color(0xEBF5FB));
  panel.setBorder(BorderFactory.createCompoundBorder(
   BorderFactory.createLineBorder(new Color(0x85C1E9), 1),
   BorderFactory.createEmptyBorder(4, 10, 4, 10)
  ));
  panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

  JLabel lblAdmin = new JLabel("PANEL DE CONTROL:", SwingConstants.CENTER);
  lblAdmin.setFont(new Font("Segoe UI", Font.BOLD, 11));
  lblAdmin.setForeground(new Color(0x1A5276));
  panel.add(lblAdmin);

   btnCierreCaja = new JButton("Cierre de Caja / Reportes");
   btnCierreCaja.setFont(new Font("Segoe UI", Font.BOLD, 11));
   btnCierreCaja.setForeground(Color.WHITE);
   btnCierreCaja.setBackground(new Color(0x8E44AD));
   btnCierreCaja.setFocusPainted(false);
   btnCierreCaja.setBorderPainted(false);
   btnCierreCaja.setCursor(new Cursor(Cursor.HAND_CURSOR));
   btnCierreCaja.setPreferredSize(new Dimension(160, 28));
   btnCierreCaja.addActionListener(e -> mostrarCierreCaja());
   panel.add(btnCierreCaja);

   return panel;
 }

 // ==================== PANEL DERECHO (CLIENTE) ====================
 private JPanel createRightPanelCliente() {
  JTabbedPane tabbedPane = new JTabbedPane();
  tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
  tabbedPane.setBackground(Color.WHITE);

  tabbedPane.addTab("1. Registrar Ingreso (Simular Ticket)", null, createTabIngreso(), null);
  tabbedPane.addTab("2. Consultar Estad\u00eda y Tarifa de Salida", null, createTabConsulta(), null);

  JPanel wrapper = new JPanel(new BorderLayout());
  wrapper.setBackground(Color.WHITE);
  wrapper.setBorder(BorderFactory.createTitledBorder(
   BorderFactory.createLineBorder(new Color(0x85C1E9), 2),
   "Vista del Conductor",
   TitledBorder.LEFT, TitledBorder.TOP,
   new Font("Segoe UI", Font.BOLD, 14),
   new Color(0x2E86C1)
  ));
  wrapper.add(tabbedPane, BorderLayout.CENTER);
  return wrapper;
 }

 // ==================== PESTAÑA 1: REGISTRAR INGRESO ====================
 private JPanel createTabIngreso() {
  JPanel panel = new JPanel();
  panel.setBackground(Color.WHITE);
  panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
  panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

  JLabel lblBienvenida = new JLabel("Bienvenido a Parqueadero PRO", SwingConstants.CENTER);
  lblBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 16));
  lblBienvenida.setForeground(new Color(0x1A5276));
  lblBienvenida.setAlignmentX(Component.CENTER_ALIGNMENT);
  lblBienvenida.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
  panel.add(lblBienvenida);
  panel.add(Box.createVerticalStrut(20));

  JLabel lblInfo = new JLabel("Registre el ingreso de su veh\u00edculo al parqueadero", SwingConstants.CENTER);
  lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
  lblInfo.setForeground(new Color(0x5D6D7E));
  lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
  lblInfo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
  panel.add(lblInfo);
  panel.add(Box.createVerticalStrut(30));

  JPanel panelPlaca = new JPanel(new BorderLayout(8, 0));
  panelPlaca.setBackground(Color.WHITE);
  panelPlaca.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
  panelPlaca.setAlignmentX(Component.CENTER_ALIGNMENT);
  JLabel lblPlacaIng = createFieldLabel("Mi Placa:");
  lblPlacaIng.setPreferredSize(new Dimension(80, 30));
  txtPlacaIngreso = new JTextField();
  txtPlacaIngreso.setFont(new Font("Segoe UI", Font.PLAIN, 16));
  txtPlacaIngreso.setBackground(new Color(0xF8F9F9));
  txtPlacaIngreso.setBorder(BorderFactory.createCompoundBorder(
   BorderFactory.createLineBorder(new Color(0x85C1E9), 2),
   BorderFactory.createEmptyBorder(6, 12, 6, 12)
  ));
  txtPlacaIngreso.setPreferredSize(new Dimension(200, 34));
  panelPlaca.add(lblPlacaIng, BorderLayout.WEST);
  panelPlaca.add(txtPlacaIngreso, BorderLayout.CENTER);
  panel.add(panelPlaca);
  panel.add(Box.createVerticalStrut(18));

  JPanel panelTipo = new JPanel(new BorderLayout(8, 0));
  panelTipo.setBackground(Color.WHITE);
  panelTipo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
  panelTipo.setAlignmentX(Component.CENTER_ALIGNMENT);
  JLabel lblTipo = createFieldLabel("Tipo:");
  lblTipo.setPreferredSize(new Dimension(80, 30));
  cmbTipoVehiculoIngreso = new JComboBox<>(new String[]{"Autom\u00f3vil", "Motocicleta"});
  cmbTipoVehiculoIngreso.setFont(new Font("Segoe UI", Font.PLAIN, 15));
  cmbTipoVehiculoIngreso.setBackground(Color.WHITE);
  cmbTipoVehiculoIngreso.setPreferredSize(new Dimension(200, 34));
  cmbTipoVehiculoIngreso.setMaximumSize(new Dimension(300, 34));
  panelTipo.add(lblTipo, BorderLayout.WEST);
  panelTipo.add(cmbTipoVehiculoIngreso, BorderLayout.CENTER);
  panel.add(panelTipo);
  panel.add(Box.createVerticalStrut(30));

  btnIngresarVehiculo = new JButton("Ingresar Veh\u00edculo / Generar Ticket");
  btnIngresarVehiculo.setFont(new Font("Segoe UI", Font.BOLD, 15));
  btnIngresarVehiculo.setForeground(Color.WHITE);
  btnIngresarVehiculo.setBackground(new Color(0x1A5276));
  btnIngresarVehiculo.setFocusPainted(false);
  btnIngresarVehiculo.setBorderPainted(false);
  btnIngresarVehiculo.setCursor(new Cursor(Cursor.HAND_CURSOR));
  btnIngresarVehiculo.setPreferredSize(new Dimension(300, 50));
  btnIngresarVehiculo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
  btnIngresarVehiculo.setAlignmentX(Component.CENTER_ALIGNMENT);
  btnIngresarVehiculo.addActionListener(e -> ingresarVehiculo());
  panel.add(btnIngresarVehiculo);

  panel.add(Box.createVerticalGlue());

  JPanel panelTarifaInfo = new JPanel(new GridLayout(2, 1, 0, 4));
  panelTarifaInfo.setBackground(new Color(0xEBF5FB));
  panelTarifaInfo.setBorder(BorderFactory.createCompoundBorder(
   BorderFactory.createLineBorder(new Color(0x85C1E9), 1),
   BorderFactory.createEmptyBorder(10, 15, 10, 15)
  ));
  panelTarifaInfo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
  panelTarifaInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
  JLabel lblTarifaInfo1 = new JLabel("Tarifa: $3,250 (hasta 15 min) | $5,450 por hora", SwingConstants.CENTER);
  lblTarifaInfo1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
  lblTarifaInfo1.setForeground(new Color(0x2E86C1));
  JLabel lblTarifaInfo2 = new JLabel("Tarifa oficial de parqueadero - Colombia", SwingConstants.CENTER);
  lblTarifaInfo2.setFont(new Font("Segoe UI", Font.ITALIC, 11));
  lblTarifaInfo2.setForeground(new Color(0x5D6D7E));
  panelTarifaInfo.add(lblTarifaInfo1);
  panelTarifaInfo.add(lblTarifaInfo2);
  panel.add(Box.createVerticalStrut(10));
  panel.add(panelTarifaInfo);

  return panel;
 }

 // ==================== PESTAÑA 2: CONSULTAR ESTADÍA ====================
 private JPanel createTabConsulta() {
  JPanel panel = new JPanel();
  panel.setBackground(Color.WHITE);
  panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
  panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

  JLabel lblBienvenida = new JLabel("Bienvenido a Parqueadero PRO", SwingConstants.CENTER);
  lblBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 16));
  lblBienvenida.setForeground(new Color(0x1A5276));
  lblBienvenida.setAlignmentX(Component.CENTER_ALIGNMENT);
  lblBienvenida.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
  panel.add(lblBienvenida);
  panel.add(Box.createVerticalStrut(12));

  JLabel lblInfo = new JLabel("Consulte el tiempo y valor a pagar de su veh\u00edculo", SwingConstants.CENTER);
  lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
  lblInfo.setForeground(new Color(0x5D6D7E));
  lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
  lblInfo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
  panel.add(lblInfo);
  panel.add(Box.createVerticalStrut(20));

  JPanel panelBusqueda = new JPanel(new BorderLayout(8, 0));
  panelBusqueda.setBackground(Color.WHITE);
  panelBusqueda.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
  panelBusqueda.setAlignmentX(Component.CENTER_ALIGNMENT);
  JLabel lblBuscar = createFieldLabel("Mi Placa:");
  lblBuscar.setPreferredSize(new Dimension(80, 30));
  txtBuscarPlaca = new JTextField();
  txtBuscarPlaca.setFont(new Font("Segoe UI", Font.PLAIN, 16));
  txtBuscarPlaca.setBackground(new Color(0xF8F9F9));
  txtBuscarPlaca.setBorder(BorderFactory.createCompoundBorder(
   BorderFactory.createLineBorder(new Color(0x85C1E9), 2),
   BorderFactory.createEmptyBorder(6, 12, 6, 12)
  ));
  txtBuscarPlaca.setPreferredSize(new Dimension(200, 34));
  txtBuscarPlaca.addActionListener(e -> buscarMiVehiculo());

  JButton btnBuscar = new JButton("Calcular Total a Pagar");
  btnBuscar.setFont(new Font("Segoe UI", Font.BOLD, 14));
  btnBuscar.setForeground(Color.WHITE);
  btnBuscar.setBackground(new Color(0x2E86C1));
  btnBuscar.setFocusPainted(false);
  btnBuscar.setBorderPainted(false);
  btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));
  btnBuscar.setPreferredSize(new Dimension(200, 40));
  btnBuscar.addActionListener(e -> buscarMiVehiculo());

  panelBusqueda.add(lblBuscar, BorderLayout.WEST);
  panelBusqueda.add(txtBuscarPlaca, BorderLayout.CENTER);
  panelBusqueda.add(btnBuscar, BorderLayout.EAST);
  panel.add(panelBusqueda);
  panel.add(Box.createVerticalStrut(20));

  JPanel panelCampos = new JPanel(new GridLayout(2, 2, 10, 10));
  panelCampos.setBackground(Color.WHITE);
  panelCampos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
  panelCampos.setAlignmentX(Component.CENTER_ALIGNMENT);
  panelCampos.add(createFieldLabel("Entrada:"));
  lblEntrada = createFieldValue("-- : -- : --");
  panelCampos.add(lblEntrada);
  panelCampos.add(createFieldLabel("Salida:"));
  lblSalida = createFieldValue("-- : -- : --");
  panelCampos.add(lblSalida);
  panel.add(panelCampos);
  panel.add(Box.createVerticalStrut(16));

  JPanel bannerMinutos = new JPanel(new BorderLayout());
  bannerMinutos.setBackground(new Color(0xFADBD8));
  bannerMinutos.setBorder(BorderFactory.createCompoundBorder(
   BorderFactory.createLineBorder(new Color(0xE74C3C), 2),
   BorderFactory.createEmptyBorder(14, 10, 14, 10)
  ));
  JLabel lblMinutosTitulo = new JLabel("Minutos Transcurridos", SwingConstants.CENTER);
  lblMinutosTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
  lblMinutosTitulo.setForeground(new Color(0xC0392B));
  lblMinutosBanner = new JLabel("-- Minutos", SwingConstants.CENTER);
  lblMinutosBanner.setFont(new Font("Segoe UI", Font.BOLD, 30));
  lblMinutosBanner.setForeground(new Color(0x922B21));
  bannerMinutos.add(lblMinutosTitulo, BorderLayout.NORTH);
  bannerMinutos.add(lblMinutosBanner, BorderLayout.CENTER);
  bannerMinutos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
  bannerMinutos.setAlignmentX(Component.CENTER_ALIGNMENT);
  panel.add(bannerMinutos);

  panel.add(Box.createVerticalGlue());

  JLabel lblNota = new JLabel("* El valor se calcula en tiempo real al buscar su placa", SwingConstants.CENTER);
  lblNota.setFont(new Font("Segoe UI", Font.ITALIC, 11));
  lblNota.setForeground(new Color(0x808B96));
  lblNota.setAlignmentX(Component.CENTER_ALIGNMENT);
  lblNota.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
  panel.add(lblNota);

  return panel;
 }

 // ==================== BARRA INFERIOR ====================
 private JPanel createBottomBar() {
  JPanel panel = new JPanel(new GridLayout(1, 3, 12, 0));
  panel.setBackground(new Color(0xA9CCE3));
  panel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
  panel.setPreferredSize(new Dimension(0, 55));

  JPanel carrosPanel = new JPanel(new BorderLayout(6, 0));
  carrosPanel.setBackground(new Color(0xEBF5FB));
  carrosPanel.setBorder(BorderFactory.createLineBorder(new Color(0x85C1E9), 1));
  JLabel lblCarrosIcon = new JLabel("\uD83D\uDE97", SwingConstants.CENTER);
  lblCarrosIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20));
  JPanel carrosText = new JPanel(new GridLayout(2, 1));
  carrosText.setOpaque(false);
  JLabel lblCarrosTitle = new JLabel("CARROS", SwingConstants.CENTER);
  lblCarrosTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
  lblCarrosTitle.setForeground(new Color(0x1A5276));
  lblCarrosCount = new JLabel("0", SwingConstants.CENTER);
  lblCarrosCount.setFont(new Font("Segoe UI", Font.BOLD, 18));
  lblCarrosCount.setForeground(new Color(0x1A5276));
  carrosText.add(lblCarrosTitle);
  carrosText.add(lblCarrosCount);
  carrosPanel.add(lblCarrosIcon, BorderLayout.WEST);
  carrosPanel.add(carrosText, BorderLayout.CENTER);
  panel.add(carrosPanel);

  JPanel motosPanel = new JPanel(new BorderLayout(6, 0));
  motosPanel.setBackground(new Color(0xEAFAF1));
  motosPanel.setBorder(BorderFactory.createLineBorder(new Color(0x82E0AA), 1));
  JLabel lblMotosIcon = new JLabel("\uD83C\uDF1F\uFE0F", SwingConstants.CENTER);
  lblMotosIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20));
  JPanel motosText = new JPanel(new GridLayout(2, 1));
  motosText.setOpaque(false);
  JLabel lblMotosTitle = new JLabel("MOTOS", SwingConstants.CENTER);
  lblMotosTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
  lblMotosTitle.setForeground(new Color(0x145A32));
  lblMotosCount = new JLabel("0", SwingConstants.CENTER);
  lblMotosCount.setFont(new Font("Segoe UI", Font.BOLD, 18));
  lblMotosCount.setForeground(new Color(0x145A32));
  motosText.add(lblMotosTitle);
  motosText.add(lblMotosCount);
  motosPanel.add(lblMotosIcon, BorderLayout.WEST);
  motosPanel.add(motosText, BorderLayout.CENTER);
  panel.add(motosPanel);

  JPanel relojPanel = new JPanel(new BorderLayout(6, 0));
  relojPanel.setBackground(Color.WHITE);
  relojPanel.setBorder(BorderFactory.createLineBorder(new Color(0xBDC3C7), 1));
  JLabel lblRelojIcon = new JLabel("\uD83D\uDD50", SwingConstants.CENTER);
  lblRelojIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20));
  lblFechaHoraSistema = new JLabel("", SwingConstants.CENTER);
  lblFechaHoraSistema.setFont(new Font("Segoe UI", Font.BOLD, 13));
  lblFechaHoraSistema.setForeground(new Color(0x2C3E50));
  relojPanel.add(lblRelojIcon, BorderLayout.WEST);
  relojPanel.add(lblFechaHoraSistema, BorderLayout.CENTER);
  panel.add(relojPanel);

  return panel;
 }

 // ==================== HELPERS UI ====================
 private JLabel createFieldLabel(String texto) {
  JLabel label = new JLabel(texto);
  label.setFont(new Font("Segoe UI", Font.BOLD, 13));
  label.setForeground(new Color(0x2E4053));
  label.setPreferredSize(new Dimension(75, 26));
  return label;
 }

 private JLabel createFieldValue(String texto) {
  JLabel label = new JLabel(texto);
  label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
  label.setForeground(new Color(0x1A1A1A));
  label.setOpaque(true);
  label.setBackground(new Color(0xF8F9F9));
  label.setBorder(BorderFactory.createLineBorder(new Color(0xD5D8DC), 1));
  label.setPreferredSize(new Dimension(140, 26));
  return label;
 }

 private JButton createStyledButton(String texto, Color color) {
  JButton btn = new JButton(texto);
  btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
  btn.setForeground(Color.WHITE);
  btn.setBackground(color);
  btn.setFocusPainted(false);
  btn.setBorderPainted(false);
  btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
  return btn;
 }

 // ==================== LOGICA: REGISTRAR INGRESO (CLIENTE) ====================
 private void ingresarVehiculo() {
  String placa = txtPlacaIngreso.getText().trim().toUpperCase();
  if (placa.isEmpty()) {
   JOptionPane.showMessageDialog(this,
    "Por favor ingrese la placa de su veh\u00edculo.",
    "Campo Vac\u00edo", JOptionPane.WARNING_MESSAGE);
   return;
  }
  if (placa.length() < 5) {
   JOptionPane.showMessageDialog(this,
    "La placa debe tener al menos 5 caracteres.",
    "Placa Inv\u00e1lida", JOptionPane.WARNING_MESSAGE);
   return;
  }

  String tipoSeleccionado = (String) cmbTipoVehiculoIngreso.getSelectedItem();
  String tipoBD = "Autom\u00f3vil".equals(tipoSeleccionado) ? "CARRO" : "MOTO";

  try {
   List<Vehiculo> vehiculos = vehiculoDAO.leerTodos();
   Vehiculo vehiculo = null;
   for (Vehiculo v : vehiculos) {
    if (v.getPlaca().equalsIgnoreCase(placa)) {
     vehiculo = v;
     break;
    }
   }

   int idVehiculo;
   if (vehiculo == null) {
    Vehiculo nuevo = new Vehiculo(0, null, placa, tipoBD);
    vehiculoDAO.crear(nuevo);
    idVehiculo = nuevo.getId();
   } else {
    idVehiculo = vehiculo.getId();
   }

   Tarifa tarifa = tarifaDAO.leerPorTipo(tipoSeleccionado);
   if (tarifa == null) {
    tarifa = tarifaDAO.leerPorTipo(tipoBD);
   }
   if (tarifa == null) {
    java.math.BigDecimal hora = new java.math.BigDecimal("5450.00");
    java.math.BigDecimal dia = new java.math.BigDecimal("32500.00");
    tarifa = new Tarifa(0, tipoBD, hora, dia);
    tarifaDAO.crear(tarifa);
   }
   int idTarifa = tarifa.getId();

   LocalDateTime ahora = LocalDateTime.now();
   Registro registro = new Registro(idVehiculo, 0, ahora, idTarifa, false);
   boolean exito = registroDAO.insertarIngreso(registro);

   if (exito) {
    String mensaje = String.format(
     "TICKET DE INGRESO\n\n" +
     "Placa: %s\n" +
     "Tipo: %s\n" +
     "Fecha/Hora Entrada: %s\n" +
     "Espacio asignado: %d\n\n" +
     "\u00a1Bienvenido! Recuerde conservar su placa.",
     placa, tipoSeleccionado,
     ahora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
     registro.getIdEspacio()
    );
    JOptionPane.showMessageDialog(this, mensaje, "Ticket de Ingreso", JOptionPane.INFORMATION_MESSAGE);
    txtPlacaIngreso.setText("");

    lblPlaca.setText(placa);
    lblValorAPagar.setText("$--");
    lblEntrada.setText(ahora.format(formatoHora));
    lblSalida.setText("-- : -- : --");
    lblMinutosBanner.setText("0 Minutos");
    updateCounters();
   } else {
    JOptionPane.showMessageDialog(this,
     "Error al registrar el ingreso. Intente nuevamente.",
     "Error", JOptionPane.ERROR_MESSAGE);
   }
  } catch (Exception ex) {
   JOptionPane.showMessageDialog(this,
    "Error al registrar ingreso: " + ex.getMessage(),
    "Error", JOptionPane.ERROR_MESSAGE);
   ex.printStackTrace();
  }
 }

 // ==================== BUSQUEDA POR PLACA (CLIENTE) ====================
 private void buscarMiVehiculo() {
  String placa = txtBuscarPlaca.getText().trim().toUpperCase();
  if (placa.isEmpty()) {
   JOptionPane.showMessageDialog(this,
    "Por favor ingrese su placa.",
    "Campo Vac\u00edo", JOptionPane.WARNING_MESSAGE);
   return;
  }

  try {
   Registro registro = registroDAO.getRegistroByPlaca(placa);
   if (registro != null) {
    actualizarMarkerCliente(registro);
   } else {
    JOptionPane.showMessageDialog(this,
     "No se encontr\u00f3 un veh\u00edculo activo con placa: " + placa,
     "Veh\u00edculo No Encontrado", JOptionPane.INFORMATION_MESSAGE);
    limpiarMarcador();
   }
  } catch (Exception ex) {
   JOptionPane.showMessageDialog(this,
    "Error al buscar veh\u00edculo: " + ex.getMessage(),
    "Error", JOptionPane.ERROR_MESSAGE);
  }
 }

 private void actualizarMarkerCliente(Registro registro) {
  lblPlaca.setText(registro.getPlaca());
  long segundos = ChronoUnit.SECONDS.between(
   registro.getHoraEntrada(), LocalDateTime.now());
  double valor = calcularValorAPagarCliente(registro);
  lblValorAPagar.setText(String.format("$%,.0f", valor));
  lblEntrada.setText(registro.getHoraEntrada().format(formatoHora));
  lblSalida.setText(LocalTime.now().format(formatoHora));

  if (segundos < 60) {
   lblMinutosBanner.setText("Tarifa m\u00ednima ($3.250) aplicada");
  } else {
   long minutos = ChronoUnit.MINUTES.between(
    registro.getHoraEntrada(), LocalDateTime.now());
   lblMinutosBanner.setText(minutos + " Minutos");
  }
 }

 // ==================== CALCULO TARIFA (CLIENTE) ====================
 private double calcularValorAPagarCliente(Registro registro) {
  try {
   LocalDateTime entrada = registro.getHoraEntrada();
   LocalDateTime ahora = LocalDateTime.now();
   long segundosTotales = ChronoUnit.SECONDS.between(entrada, ahora);

   if (segundosTotales <= 0) {
    return TARIFA_FRACCION.doubleValue();
   }

   if (segundosTotales < 60) {
    return TARIFA_FRACCION.doubleValue();
   }

   long minutosTotales = ChronoUnit.MINUTES.between(entrada, ahora);
   if (minutosTotales <= MINUTOS_FRACCION) {
    return TARIFA_FRACCION.doubleValue();
   }

   long minutosAdicionales = minutosTotales - MINUTOS_FRACCION;
   double horasAdicionales = Math.ceil(minutosAdicionales / 60.0);
   double total = TARIFA_FRACCION.doubleValue() + (horasAdicionales * TARIFA_HORA.doubleValue());
   return Math.max(TARIFA_FRACCION.doubleValue(), total);
  } catch (Exception ex) {
   ex.printStackTrace();
   return TARIFA_FRACCION.doubleValue();
  }
 }

 // ==================== CRUD ADMIN: ACTUALIZAR VEHICULO ====================
 private void actualizarVehiculoAdmin() {
  int row = tblVehiculos.getSelectedRow();
  if (row < 0) {
   JOptionPane.showMessageDialog(this,
    "Seleccione un veh\u00edculo de la tabla para actualizar.",
    "Advertencia", JOptionPane.WARNING_MESSAGE);
   return;
  }

  String placaVieja = (String) tableModel.getValueAt(row, 0);
  String placaNueva = txtAdminPlaca.getText().trim().toUpperCase();
  if (placaNueva.isEmpty()) {
   JOptionPane.showMessageDialog(this,
    "Ingrese la nueva placa.", "Campo Vac\u00edo", JOptionPane.WARNING_MESSAGE);
   return;
  }

  String tipoBD = "Autom\u00f3vil".equals(cmbAdminTipo.getSelectedItem()) ? "CARRO" : "MOTO";
  try {
   Vehiculo vehiculo = null;
   List<Vehiculo> vehiculos = vehiculoDAO.leerTodos();
   for (Vehiculo v : vehiculos) {
    if (v.getPlaca().equalsIgnoreCase(placaVieja)) {
     vehiculo = v;
     break;
    }
   }
   if (vehiculo == null) {
    JOptionPane.showMessageDialog(this,
     "No se encontr\u00f3 el veh\u00edculo en la base de datos.",
     "Error", JOptionPane.ERROR_MESSAGE);
    return;
   }
   vehiculo.setPlaca(placaNueva);
   vehiculo.setTipo(tipoBD);
   vehiculoDAO.actualizar(vehiculo);
   JOptionPane.showMessageDialog(this,
    "Veh\u00edculo actualizado correctamente.",
    "Actualizado", JOptionPane.INFORMATION_MESSAGE);
   limpiarFormularioAdmin();
   loadCurrentVehicles();
  } catch (Exception ex) {
   JOptionPane.showMessageDialog(this,
    "Error al actualizar: " + ex.getMessage(),
    "Error", JOptionPane.ERROR_MESSAGE);
   ex.printStackTrace();
  }
 }

 // ==================== CRUD ADMIN: ELIMINAR REGISTRO ====================
 private void eliminarRegistroAdmin() {
  int row = tblVehiculos.getSelectedRow();
  if (row < 0) {
   JOptionPane.showMessageDialog(this,
    "Seleccione un registro de la tabla para eliminar.",
    "Advertencia", JOptionPane.WARNING_MESSAGE);
   return;
  }

  int idRegistro = (int) tableModel.getValueAt(row, 4);
  String placa = (String) tableModel.getValueAt(row, 0);

  int confirm = JOptionPane.showConfirmDialog(this,
   "\u00BFEst\u00e1 seguro de eliminar el registro de " + placa + "?",
   "Confirmar Eliminaci\u00f3n", JOptionPane.YES_NO_OPTION);
  if (confirm != JOptionPane.YES_OPTION) return;

  try {
   boolean exito = registroDAO.eliminar(idRegistro);
   if (exito) {
    JOptionPane.showMessageDialog(this,
     "Registro eliminado correctamente.",
     "Eliminado", JOptionPane.INFORMATION_MESSAGE);
    limpiarFormularioAdmin();
    loadCurrentVehicles();
    updateCounters();
   } else {
    JOptionPane.showMessageDialog(this,
     "No se pudo eliminar el registro.", "Error", JOptionPane.ERROR_MESSAGE);
   }
  } catch (Exception ex) {
   JOptionPane.showMessageDialog(this,
    "Error al eliminar: " + ex.getMessage(),
    "Error", JOptionPane.ERROR_MESSAGE);
   ex.printStackTrace();
  }
 }

 // ==================== CRUD ADMIN: LIMPIAR FORMULARIO ====================
 private void limpiarFormularioAdmin() {
  txtAdminPlaca.setText("");
  cmbAdminTipo.setSelectedIndex(0);
  tblVehiculos.clearSelection();
  lblEntrada.setText("-- : -- : --");
  lblSalida.setText("-- : -- : --");
  lblMinutosBanner.setText("-- Minutos");
  lblValorPagarLiq.setText("Valor a Pagar: $0");
 }

 // ==================== CARGAR DATOS FILA SELECCIONADA ====================
 private void cargarDatosFilaVehiculo(int row) {
  try {
   String placa = (String) tableModel.getValueAt(row, 0);
   txtAdminPlaca.setText(placa);

   String tipoViejo = (String) tableModel.getValueAt(row, 3);
   if ("CARRO".equals(tipoViejo)) {
    cmbAdminTipo.setSelectedItem("Autom\u00f3vil");
   } else {
    cmbAdminTipo.setSelectedItem("Motocicleta");
   }

   int idIngresoSalida = (int) tableModel.getValueAt(row, 4);
   Registro registro = registroDAO.getRegistroById(idIngresoSalida);
   if (registro != null) {
    lblEntrada.setText(registro.getHoraEntrada().format(formatoHora));
    lblSalida.setText(LocalTime.now().format(formatoHora));
    long totalMinutos = ChronoUnit.MINUTES.between(
     registro.getHoraEntrada(), LocalDateTime.now());
    if (totalMinutos >= 60) {
     long horas = totalMinutos / 60;
     long mins = totalMinutos % 60;
     lblMinutosBanner.setText(horas + " Horas y " + mins + " Minutos");
    } else {
     lblMinutosBanner.setText(totalMinutos + " Minutos");
    }
    double valor = calcularValorAPagar(registro);
    lblValorPagarLiq.setText(String.format("Valor a Pagar: $%,.0f", valor));
   }
  } catch (Exception ex) {
   JOptionPane.showMessageDialog(this,
    "Error al cargar datos: " + ex.getMessage(),
    "Error", JOptionPane.ERROR_MESSAGE);
  }
 }

 // ==================== LOGICA COMPARTIDA ====================
 private void loadCurrentVehicles() {
  if (!esAdmin || tableModel == null) return;
  try {
   tableModel.setRowCount(0);
   List<Object[]> activos = registroDAO.getRegistrosActivosConDetalles();
   int carros = 0, motos = 0;
   for (Object[] fila : activos) {
    Object[] rowDisplay = new Object[5];
    rowDisplay[0] = fila[0];
    rowDisplay[1] = fila[1];
    rowDisplay[2] = fila[2];
    rowDisplay[3] = fila[3];
    rowDisplay[4] = fila[4];
    tableModel.addRow(rowDisplay);
    if ("CARRO".equals(fila[3])) carros++;
    else if ("MOTO".equals(fila[3])) motos++;
   }
   lblCarrosCount.setText(String.valueOf(carros));
   lblMotosCount.setText(String.valueOf(motos));
  } catch (Exception ex) {
   JOptionPane.showMessageDialog(this,
    "Error al cargar veh\u00edculos: " + ex.getMessage(),
    "Error", JOptionPane.ERROR_MESSAGE);
  }
 }

 private double calcularValorAPagar(Registro registro) {
  return calcularValorAPagarCliente(registro);
 }

 private void calcularImprimirSalida() {
  if (!esAdmin) return;
  int selectedRow = tblVehiculos.getSelectedRow();
  if (selectedRow < 0) {
   JOptionPane.showMessageDialog(this,
    "Por favor seleccione un veh\u00edculo de la tabla",
    "Advertencia", JOptionPane.WARNING_MESSAGE);
   return;
  }

  try {
   int idIngresoSalida = (int) tableModel.getValueAt(selectedRow, 4);
   int confirm = JOptionPane.showConfirmDialog(this,
    "\u00BFEst\u00e1 seguro de registrar la salida de este veh\u00edculo?",
    "Confirmar Salida", JOptionPane.YES_NO_OPTION);
   if (confirm != JOptionPane.YES_OPTION) return;

   Registro registro = registroDAO.getRegistroById(idIngresoSalida);
   if (registro == null) {
    JOptionPane.showMessageDialog(this,
     "No se encontr\u00f3 el registro del veh\u00edculo",
     "Error", JOptionPane.ERROR_MESSAGE);
    return;
   }

   double valorAPagar = calcularValorAPagar(registro);
   boolean actualizado = registroDAO.actualizarSalida(
    idIngresoSalida, LocalDateTime.now(), valorAPagar
   );

   if (!actualizado) {
    JOptionPane.showMessageDialog(this,
     "No se pudo actualizar la salida del veh\u00edculo",
     "Error", JOptionPane.ERROR_MESSAGE);
    return;
   }

   String mensaje = String.format(
    "RECIBO DE PAGO\n\n" +
    "Placa: %s\n" +
    "Entrada: %s\n" +
    "Salida: %s\n" +
    "Tiempo: %s\n" +
    "Valor a Pagar: $%,.0f\n\n" +
    "\u00a1Gracias por usar Parqueadero PRO!",
    registro.getPlaca(),
    registro.getHoraEntrada().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
    registro.getHoraSalida().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
    lblMinutosBanner.getText(),
    valorAPagar
   );

   JOptionPane.showMessageDialog(this, mensaje, "Recibo de Pago", JOptionPane.INFORMATION_MESSAGE);
   loadCurrentVehicles();
   limpiarFormularioAdmin();
  } catch (Exception ex) {
   JOptionPane.showMessageDialog(this,
    "Error al registrar salida: " + ex.getMessage(),
    "Error", JOptionPane.ERROR_MESSAGE);
   ex.printStackTrace();
  }
 }

 private void limpiarMarcador() {
  lblPlaca.setText("--");
  lblValorAPagar.setText("$0");
  lblEntrada.setText("-- : -- : --");
  lblSalida.setText("-- : -- : --");
  lblMinutosBanner.setText("-- Minutos");
  if (txtBuscarPlaca != null) {
   txtBuscarPlaca.setText("");
  }
 }

 private void updateCounters() {
  try {
   int carros = registroDAO.contarVehiculosPorTipo("CARRO");
   int motos = registroDAO.contarVehiculosPorTipo("MOTO");
   lblCarrosCount.setText(String.valueOf(carros));
   lblMotosCount.setText(String.valueOf(motos));
  } catch (Exception ex) {
   ex.printStackTrace();
  }
 }

 // ==================== CIERRE DE CAJA ====================
 private void mostrarCierreCaja() {
  if (!esAdmin) return;
  try {
   Object[] cierre = registroDAO.getCierreCajaDiario();
   int total = (int) cierre[0];
   double recaudado = ((java.math.BigDecimal) cierre[1]).doubleValue();
   int carros = (int) cierre[2];
   int motos = (int) cierre[3];

   String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
   String mensaje = String.format(
    "--- CIERRE DE CAJA DIARIO ---\n\n" +
    "Fecha: %s\n" +
    "Total veh\u00edculos procesados: %d\n" +
    "Carros procesados: %d\n" +
    "Motos procesadas: %d\n\n" +
    "Total Recaudado: $%,.0f",
    fecha, total, carros, motos, recaudado
   );

   JOptionPane.showMessageDialog(this, mensaje,
    "Cierre de Caja / Reportes", JOptionPane.INFORMATION_MESSAGE);
  } catch (Exception ex) {
   JOptionPane.showMessageDialog(this,
    "Error al consultar cierre de caja: " + ex.getMessage(),
    "Error", JOptionPane.ERROR_MESSAGE);
   ex.printStackTrace();
  }
 }

 // ==================== TIMER ====================
 private void startTimer() {
  timer = new Timer(1000, e -> {
   String diaSemana = LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE"));
   String fechaHora = LocalDateTime.now().format(
    DateTimeFormatter.ofPattern("dd/MMM/yyyy hh:mm:ss a"));
   lblFechaHoraSistema.setText(diaSemana.toUpperCase() + " " + fechaHora);
  });
  timer.start();

  if (esAdmin) {
   refreshTimer = new Timer(5000, e -> loadCurrentVehicles());
   refreshTimer.start();
  }
 }

 @Override
 public void dispose() {
  if (timer != null) {
   timer.stop();
  }
  if (refreshTimer != null) {
   refreshTimer.stop();
  }
  super.dispose();
 }
}
