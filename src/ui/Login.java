package ui;

import dao.ClientDAO;
import model.Client;
import ui.Interfaces_Client.AccueilClient;
import ui.Interfaces_Gerant.AccueilGerant;

import javax.swing.*;
import java.awt.*;

public class Login extends JFrame {
    private JPanel panel1;
    private JTextField email;
    private JPasswordField password;
    private JButton submit;
    private JLabel errorLabel;

    public Login() {
        setTitle("Connexion");
        setContentPane(panel1);
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        submit.addActionListener(e -> {
            String mail = email.getText().trim();
            String mdp = new String(password.getPassword()).trim();
            errorLabel.setText("");

            if (mail.isEmpty() || mdp.isEmpty()) {
                errorLabel.setForeground(Color.RED);
                errorLabel.setText("Veuillez remplir tous les champs.");
                return;
            }

            if (mail.equals("admin") && mdp.equals("admin")) {
                new AccueilGerant().setVisible(true);
                dispose();
            } else {
                ClientDAO clientDAO = new ClientDAO();
                Client client = clientDAO.verifierConnexion(mail, mdp);

                if (client != null) {
                    errorLabel.setForeground(new Color(0, 128, 0));
                    errorLabel.setText("Connexion client réussie !");
                    new AccueilClient(client.getId()).setVisible(true);
                    dispose();
                } else {
                    errorLabel.setForeground(Color.RED);
                    errorLabel.setText("Email ou mot de passe incorrect.");
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}
