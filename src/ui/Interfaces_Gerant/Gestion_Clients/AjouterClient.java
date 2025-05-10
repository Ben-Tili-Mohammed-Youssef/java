package ui.Interfaces_Gerant.Gestion_Clients;

import dao.ClientDAO;
import model.Client;

import javax.swing.*;
import java.awt.*;

public class AjouterClient extends JFrame {
    public AjouterClient() {
        setTitle("Créer un client");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(8, 2));

        JTextField emailField = new JTextField();
        JPasswordField mdpField = new JPasswordField();
        JTextField nomField = new JTextField();
        JTextField prenomField = new JTextField();
        JTextField dateNaissanceField = new JTextField();
        JTextField adresseField = new JTextField();
        JTextField telField = new JTextField();
        JButton saveBtn = new JButton("Créer");

        add(new JLabel("Email:"));
        add(emailField);
        add(new JLabel("Mot de passe:"));
        add(mdpField);
        add(new JLabel("Nom:"));
        add(nomField);
        add(new JLabel("Prénom:"));
        add(prenomField);
        add(new JLabel("Date naissance (YYYY-MM-DD):"));
        add(dateNaissanceField);
        add(new JLabel("Adresse:"));
        add(adresseField);
        add(new JLabel("Téléphone:"));
        add(telField);
        add(new JLabel(""));
        add(saveBtn);

        saveBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String mdp = new String(mdpField.getPassword()).trim();
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();
            String dateNaissance = dateNaissanceField.getText().trim();
            String adresse = adresseField.getText().trim();
            String telephone = telField.getText().trim();

            if (email.isEmpty() || mdp.isEmpty() || nom.isEmpty() || prenom.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez remplir les champs obligatoires !");
                return;
            }

            ClientDAO clientDAO = new ClientDAO();
            if (clientDAO.emailExiste(email)) {
                JOptionPane.showMessageDialog(this, "Email déjà utilisé !");
                return;
            }

            Client client = new Client(email, mdp, nom, prenom, dateNaissance, adresse, telephone);
            boolean success = clientDAO.ajouterClient(client);

            if (success) {
                JOptionPane.showMessageDialog(this, "Client ajouté avec succès !");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de l’ajout du client.");
            }
        });
    }
}
