package ui.Interfaces_Gerant.Gestion_Clients;

import dao.ClientDAO;
import model.Client;

import javax.swing.*;
import java.awt.*;

public class ModifierClient extends JFrame {
    private JTextField emailField, nomField, prenomField, telephoneField;
    private JButton chercherButton, enregistrerButton;
    private ClientDAO clientDAO;

    public ModifierClient() {
        setTitle("Modifier Client");
        setSize(400, 300);
        setLayout(new GridLayout(6, 2));
        setLocationRelativeTo(null);

        clientDAO = new ClientDAO();

        add(new JLabel("Email (à rechercher):"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("Nom:"));
        nomField = new JTextField();
        add(nomField);

        add(new JLabel("Prénom:"));
        prenomField = new JTextField();
        add(prenomField);

        add(new JLabel("Téléphone:"));
        telephoneField = new JTextField();
        add(telephoneField);

        chercherButton = new JButton("Chercher");
        enregistrerButton = new JButton("Enregistrer");

        add(chercherButton);
        add(enregistrerButton);

        chercherButton.addActionListener(e -> chercherClient());
        enregistrerButton.addActionListener(e -> enregistrerModifications());
    }

    private void chercherClient() {
        String email = emailField.getText().trim();
        Client client = clientDAO.getClientByEmail(email);
        if (client != null) {
            nomField.setText(client.getNom());
            prenomField.setText(client.getPrenom());
            telephoneField.setText(client.getTelephone());
        } else {
            JOptionPane.showMessageDialog(this, "Client non trouvé.");
        }
    }

    private void enregistrerModifications() {
        String email = emailField.getText().trim();
        Client client = clientDAO.getClientByEmail(email);
        if (client == null) {
            JOptionPane.showMessageDialog(this, "Client non trouvé pour modification.");
            return;
        }
        client.setNom(nomField.getText());
        client.setPrenom(prenomField.getText());
        client.setTelephone(telephoneField.getText());
        boolean success = clientDAO.modifierClient(client);
        if (success) {
            JOptionPane.showMessageDialog(this, "Client modifié avec succès.");
        } else {
            JOptionPane.showMessageDialog(this, "Erreur lors de la modification.");
        }
    }
}
