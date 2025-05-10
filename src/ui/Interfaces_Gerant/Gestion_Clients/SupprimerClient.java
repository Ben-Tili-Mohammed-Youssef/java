package ui.Interfaces_Gerant.Gestion_Clients;

import dao.ClientDAO;

import javax.swing.*;
import java.awt.*;

public class SupprimerClient extends JFrame {
    public SupprimerClient() {
        setTitle("Supprimer Client");
        setSize(300, 150);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout());

        JTextField emailField = new JTextField(20);
        JButton supprimerButton = new JButton("Supprimer");
        add(new JLabel("Email du client à supprimer:"));
        add(emailField);
        add(supprimerButton);

        ClientDAO clientDAO = new ClientDAO();

        supprimerButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            boolean success = clientDAO.supprimerClientByEmail(email);
            if (success) {
                JOptionPane.showMessageDialog(this, "Client supprimé avec succès.");
            } else {
                JOptionPane.showMessageDialog(this, "Aucun client trouvé avec cet email.");
            }
        });
    }
}
