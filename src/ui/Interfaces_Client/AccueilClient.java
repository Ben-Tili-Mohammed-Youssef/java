package ui.Interfaces_Client;

import ui.Interfaces_Client.Gestion_Commandes.ModifierCommande;
import ui.Interfaces_Client.Gestion_Commandes.PasserCommande;
import ui.Interfaces_Client.Gestion_Commandes.SupprimerCommande;
import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccueilClient extends JFrame {
    private int clientId;

    public AccueilClient(int clientId) {
        this.clientId = clientId;

        setTitle("Espace model.Interfaces_Client - RestaurantApp");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();

        // Menu Compte
        JMenu menuCompte = new JMenu("Compte");
        JMenuItem modifierMotDePasse = new JMenuItem("Modifier Mot de Passe");

        menuCompte.add(modifierMotDePasse);

        // Menu Carte
        JMenu menuCarte = new JMenu("Carte du Menu");
        JMenuItem consulterCarte = new JMenuItem("Consulter");

        menuCarte.add(consulterCarte);

        // Menu Commandes
        JMenu menuCommandes = new JMenu("Commandes");
        JMenuItem passerCommande = new JMenuItem("Passer une commande");
        JMenuItem modifierCommande = new JMenuItem("Modifier une commande");
        JMenuItem supprimerCommande = new JMenuItem("Supprimer une commande");
        JMenuItem suivreCommande = new JMenuItem("Suivre l'état de la commande");

        menuCommandes.add(passerCommande);
        menuCommandes.add(modifierCommande);
        menuCommandes.add(supprimerCommande);
        menuCommandes.add(suivreCommande);

        menuBar.add(menuCompte);
        menuBar.add(menuCarte);
        menuBar.add(menuCommandes);

        setJMenuBar(menuBar);

        // ✅ Message de bienvenue
        String nomComplet = getNomPrenomClient(clientId);
        JLabel bienvenue = new JLabel("Bienvenue " + nomComplet + " !");
        bienvenue.setFont(new Font("Arial", Font.BOLD, 18));
        bienvenue.setHorizontalAlignment(SwingConstants.CENTER);

        add(bienvenue, BorderLayout.CENTER);

        // ActionListeners
        modifierMotDePasse.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Fonctionnalité: Modifier Mot de Passe");
        });

        consulterCarte.addActionListener(e -> {
            new ConsulterCarte().setVisible(true);
        });

        passerCommande.addActionListener(e -> {
            new PasserCommande(clientId).setVisible(true);
        });

        modifierCommande.addActionListener(e -> {
            new ModifierCommande(clientId).setVisible(true);
        });

        supprimerCommande.addActionListener(e -> {
            new SupprimerCommande(clientId).setVisible(true);
        });

        suivreCommande.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Fonctionnalité: Suivre Commande");
        });
    }

    private String getNomPrenomClient(int id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT nom, prenom FROM clients WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                return (prenom != null ? prenom : "") + " " + (nom != null ? nom : "");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Cher client";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AccueilClient(2).setVisible(true));
    }
}
