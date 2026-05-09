package view;

import javax.swing.*;
import java.awt.*;

public class PrincipalView extends JFrame {

    public PrincipalView() {
        setTitle("Sistema de Parqueadero");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("Sistema de Parqueadero");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.ipadx = 150;

        JButton btnUsuarios = new JButton("Gestión de Usuarios");
        btnUsuarios.addActionListener(e -> {
            UsuarioView view = new UsuarioView();
            view.setVisible(true);
        });
        panel.add(btnUsuarios, gbc);

        gbc.gridy = 2;
        JButton btnVehiculos = new JButton("Gestión de Vehículos");
        btnVehiculos.addActionListener(e -> {
            VehiculoView view = new VehiculoView();
            view.setVisible(true);
        });
        panel.add(btnVehiculos, gbc);

        gbc.gridy = 3;
        JButton btnTarifas = new JButton("Gestión de Tarifas");
        btnTarifas.addActionListener(e -> {
            TarifaView view = new TarifaView();
            view.setVisible(true);
        });
        panel.add(btnTarifas, gbc);

        gbc.gridy = 4;
        JButton btnSalir = new JButton("Salir");
        btnSalir.addActionListener(e -> {
            int opcion = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro que desea salir?", "Confirmar salida", 
                JOptionPane.YES_NO_OPTION);
            if (opcion == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        panel.add(btnSalir, gbc);

        JLabel lblPie = new JLabel("© 2026 - Parqueadero PRO");
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(lblPie, gbc);

        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PrincipalView vista = new PrincipalView();
            vista.setVisible(true);
        });
    }
}