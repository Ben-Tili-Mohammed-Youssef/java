package ui.Interfaces_Gerant.Gestion_Commandes;

import dao.CommandeDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class SupprimerCommande extends JFrame {
    public SupprimerCommande() {
        setTitle("Supprimer une Commande");
        setSize(300, 150);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        JTextField champId = new JTextField();
        JButton btnSupprimer = new JButton("Supprimer");

        panel.add(new JLabel("ID de la commande à supprimer :"), BorderLayout.NORTH);
        panel.add(champId, BorderLayout.CENTER);
        panel.add(btnSupprimer, BorderLayout.SOUTH);
        add(panel);

        btnSupprimer.addActionListener(e -> {
            String idText = champId.getText().trim();
            if (!idText.isEmpty()) {
                int idCommande = Integer.parseInt(idText);
                CommandeDAO commandeDAO = new CommandeDAO();
                try {
                    commandeDAO.deleteCommande(idCommande);
                    JOptionPane.showMessageDialog(this, "Commande supprimée !");
                    dispose();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erreur SQL !");
                }
            } else {
                JOptionPane.showMessageDialog(this, "ID requis.");
            }
        });
    }
}
