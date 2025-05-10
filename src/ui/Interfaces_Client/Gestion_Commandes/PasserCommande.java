package ui.Interfaces_Client.Gestion_Commandes;

import dao.CommandeDAO;
import dao.DetailsCommandeDAO;
import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PasserCommande extends JFrame {
    private int clientId;
    private JComboBox<String> modeRecupCombo;
    private JTextField adresseLivraisonField;
    private JPanel articlesPanel;
    private ArrayList<JSpinner> quantites = new ArrayList<>();
    private ArrayList<Integer> articleIds = new ArrayList<>();

    public PasserCommande(int clientId) {
        this.clientId = clientId;

        setTitle("Passer une Commande");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Haut : Choix du mode de récupération
        JPanel topPanel = new JPanel(new GridLayout(3, 2));
        topPanel.add(new JLabel("Mode de récupération :"));
        modeRecupCombo = new JComboBox<>(new String[]{"livraison", "sur place", "à importer"});
        modeRecupCombo.addActionListener(e -> {
            adresseLivraisonField.setEnabled(modeRecupCombo.getSelectedItem().equals("livraison"));
        });
        topPanel.add(modeRecupCombo);

        topPanel.add(new JLabel("Adresse (si livraison) :"));
        adresseLivraisonField = new JTextField();
        adresseLivraisonField.setEnabled(false); // Désactivé par défaut
        topPanel.add(adresseLivraisonField);

        add(topPanel, BorderLayout.NORTH);

        // Centre : Liste des articles
        articlesPanel = new JPanel();
        articlesPanel.setLayout(new BoxLayout(articlesPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(articlesPanel);
        add(scrollPane, BorderLayout.CENTER);

        loadArticles();

        // Bas : Bouton valider
        JButton validerBtn = new JButton("Valider la commande");
        validerBtn.addActionListener(e -> passerCommande());
        add(validerBtn, BorderLayout.SOUTH);

        // Activer/désactiver le champ d'adresse selon mode initial
        adresseLivraisonField.setEnabled(modeRecupCombo.getSelectedItem().equals("livraison"));
    }

    private void loadArticles() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT id, nom, prix FROM articles";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int articleId = rs.getInt("id");
                String nom = rs.getString("nom");
                double prix = rs.getDouble("prix");

                JPanel articleLine = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JLabel label = new JLabel(nom + " - " + prix + " €");
                JSpinner quantite = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));

                articleLine.add(label);
                articleLine.add(quantite);

                articlesPanel.add(articleLine);

                articleIds.add(articleId);
                quantites.add(quantite);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void passerCommande() {
        String mode = (String) modeRecupCombo.getSelectedItem();
        String adresse = adresseLivraisonField.getText().trim();

        if (mode.equals("livraison") && adresse.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Adresse requise pour la livraison.");
            return;
        }

        boolean auMoinsUnArticle = false;
        for (JSpinner spinner : quantites) {
            if ((int)spinner.getValue() > 0) {
                auMoinsUnArticle = true;
                break;
            }
        }

        if (!auMoinsUnArticle) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner au moins un article.");
            return;
        }

        try {
            // Utiliser les DAOs pour la transaction
            CommandeDAO commandeDAO = new CommandeDAO();
            DetailsCommandeDAO detailsDAO = new DetailsCommandeDAO();

            Connection conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try {
                // Créer la commande et récupérer son ID
                int commandeId = commandeDAO.creerCommande(clientId, mode, adresse);

                if (commandeId == -1) {
                    throw new SQLException("Erreur lors de la création de la commande");
                }

                // Préparer les listes pour l'ajout en batch
                List<Integer> articlesACommander = new ArrayList<>();
                List<Integer> quantitesArticles = new ArrayList<>();

                for (int i = 0; i < quantites.size(); i++) {
                    int qte = (int) quantites.get(i).getValue();
                    if (qte > 0) {
                        articlesACommander.add(articleIds.get(i));
                        quantitesArticles.add(qte);
                    }
                }

                // Ajouter les détails
                detailsDAO.ajouterDetailsCommandeBatch(commandeId, articlesACommander, quantitesArticles);

                conn.commit();
                JOptionPane.showMessageDialog(this, "Commande enregistrée avec succès !");
                this.dispose();

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement de la commande : " + e.getMessage());
        }
    }
}