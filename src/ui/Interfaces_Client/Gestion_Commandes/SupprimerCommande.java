package ui.Interfaces_Client.Gestion_Commandes;

import dao.CommandeDAO;
import dao.DetailsCommandeDAO;
import model.Commande;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupprimerCommande extends JFrame {
    private int clientId;
    private JComboBox<String> commandeCombo;
    private List<Integer> commandeIds = new ArrayList<>();

    public SupprimerCommande(int clientId) {
        this.clientId = clientId;

        setTitle("Supprimer une commande");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());

        JLabel label = new JLabel("Sélectionnez une commande à supprimer :");
        panel.add(label, BorderLayout.NORTH);

        commandeCombo = new JComboBox<>();
        chargerCommandes();

        panel.add(commandeCombo, BorderLayout.CENTER);

        JButton supprimerBtn = new JButton("Supprimer");
        panel.add(supprimerBtn, BorderLayout.SOUTH);

        supprimerBtn.addActionListener(e -> supprimerCommande());

        add(panel);
    }

    private void chargerCommandes() {
        try {
            CommandeDAO commandeDAO = new CommandeDAO();
            List<Commande> commandes = commandeDAO.getCommandesNonTraiteesClient(clientId);

            for (Commande commande : commandes) {
                commandeCombo.addItem("Commande n°" + commande.getId() + " - " + commande.getDateCommande().toLocalDateTime().toLocalDate());
                commandeIds.add(commande.getId());
            }

            if (commandeIds.isEmpty()) {
                commandeCombo.addItem("Aucune commande à supprimer");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des commandes.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void supprimerCommande() {
        int index = commandeCombo.getSelectedIndex();
        if (commandeIds.isEmpty() || index == -1 || commandeCombo.getSelectedItem().equals("Aucune commande à supprimer")) {
            JOptionPane.showMessageDialog(this, "Aucune commande sélectionnée.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int commandeId = commandeIds.get(index);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer cette commande ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            // Utiliser les DAOs pour la transaction
            CommandeDAO commandeDAO = new CommandeDAO();
            DetailsCommandeDAO detailsDAO = new DetailsCommandeDAO();

            // Supprimer les détails de la commande d'abord
            detailsDAO.supprimerDetailsCommande(commandeId);

            // Puis supprimer la commande
            boolean resultat = commandeDAO.supprimerCommande(commandeId);

            if (resultat) {
                JOptionPane.showMessageDialog(this, "Commande supprimée avec succès !");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "La commande n'a pas pu être supprimée. Elle a peut-être déjà été traitée.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la suppression : " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}