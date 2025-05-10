package ui.Interfaces_Gerant.Gestion_Commandes;

import dao.CommandeDAO;
import model.Commande;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AfficherCommandes extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> etatComboBox;
    private JButton modifierEtatBtn;
    private JButton supprimerBtn;
    private CommandeDAO commandeDAO;

    public AfficherCommandes() {
        setTitle("Commandes des Clients");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        commandeDAO = new CommandeDAO();

        model = new DefaultTableModel(new String[]{"ID", "Client ID", "Date", "État", "Mode", "Adresse"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        etatComboBox = new JComboBox<>(new String[]{"non traitée", "préparation", "prête", "en route"});
        modifierEtatBtn = new JButton("Modifier État");
        supprimerBtn = new JButton("Supprimer");

        JPanel controlsPanel = new JPanel();
        controlsPanel.add(new JLabel("Nouvel état :"));
        controlsPanel.add(etatComboBox);
        controlsPanel.add(modifierEtatBtn);
        controlsPanel.add(supprimerBtn);

        add(scrollPane, BorderLayout.CENTER);
        add(controlsPanel, BorderLayout.SOUTH);

        chargerCommandes();

        supprimerBtn.addActionListener(e -> supprimerCommande());
        modifierEtatBtn.addActionListener(e -> modifierEtatCommande());
    }

    private void chargerCommandes() {
        try {
            List<Commande> commandes = commandeDAO.getAllCommandes();
            model.setRowCount(0);
            for (Commande c : commandes) {
                model.addRow(new Object[]{
                        c.getId(),
                        c.getClientId(),
                        c.getDateCommande(),
                        c.getEtat(),
                        c.getModeRecuperation(),
                        c.getAdresseLivraison()
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur de chargement des commandes.");
        }
    }

    private void supprimerCommande() {
        int row = table.getSelectedRow();
        if (row != -1) {
            int idCommande = (int) model.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Supprimer la commande ?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    commandeDAO.deleteCommande(idCommande);
                    model.removeRow(row);
                    JOptionPane.showMessageDialog(this, "Commande supprimée.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression.");
                }
            }
        }
    }

    private void modifierEtatCommande() {
        int row = table.getSelectedRow();
        if (row != -1) {
            int idCommande = (int) model.getValueAt(row, 0);
            String nouvelEtat = etatComboBox.getSelectedItem().toString();
            try {
                commandeDAO.updateEtatCommande(idCommande, nouvelEtat);
                model.setValueAt(nouvelEtat, row, 3);
                JOptionPane.showMessageDialog(this, "État mis à jour !");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AfficherCommandes().setVisible(true));
    }
}
