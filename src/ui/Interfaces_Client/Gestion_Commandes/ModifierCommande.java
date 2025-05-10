package ui.Interfaces_Client.Gestion_Commandes;

import dao.CommandeDAO;
import dao.DetailsCommandeDAO;
import database.DatabaseConnection;
import model.Commande;
import model.DetailsCommande;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModifierCommande extends JFrame {
    private final int clientId;
    private JComboBox<String> commandesBox;
    private JPanel articlesPanel;
    private JTextField adresseField;
    private JComboBox<String> modeBox;
    private final Map<Integer, JTextField> quantiteFields = new HashMap<>();
    private final Map<String, Integer> commandesMap = new HashMap<>();

    public ModifierCommande(int clientId) {
        this.clientId = clientId;
        initializeUI();
        loadCommandes();
    }

    private void initializeUI() {
        setTitle("Modifier Commande");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top panel with command selection and delivery info
        JPanel topPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        commandesBox = new JComboBox<>();
        commandesBox.addActionListener(e -> loadDetailsCommande());
        topPanel.add(new JLabel("Commande à modifier :"));
        topPanel.add(commandesBox);

        topPanel.add(new JLabel("Mode de récupération :"));
        modeBox = new JComboBox<>(new String[]{"livraison", "sur place", "à importer"});
        modeBox.addActionListener(e -> updateAddressFieldState());
        topPanel.add(modeBox);

        topPanel.add(new JLabel("Adresse de livraison :"));
        adresseField = new JTextField();
        adresseField.setEnabled(false);
        topPanel.add(adresseField);

        add(topPanel, BorderLayout.NORTH);

        // Articles panel with scroll
        articlesPanel = new JPanel();
        articlesPanel.setLayout(new BoxLayout(articlesPanel, BoxLayout.Y_AXIS));
        articlesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(articlesPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Save button
        JButton saveBtn = new JButton("Enregistrer les modifications");
        saveBtn.addActionListener(e -> enregistrerModifications());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(saveBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateAddressFieldState() {
        adresseField.setEnabled("livraison".equals(modeBox.getSelectedItem()));
    }

    private void loadCommandes() {
        try {
            commandesBox.removeAllItems();
            commandesMap.clear();
            articlesPanel.removeAll();
            quantiteFields.clear();

            CommandeDAO commandeDAO = new CommandeDAO();
            List<Commande> commandes = commandeDAO.getCommandesNonTraiteesClient(clientId);

            if (commandes.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Vous n'avez aucune commande non traitée à modifier.",
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE);
                setComponentsEnabled(false);
                return;
            }

            for (Commande commande : commandes) {
                String label = String.format("Commande #%d - %s",
                        commande.getId(),
                        commande.getDateCommande().toLocalDateTime().toLocalDate());
                commandesBox.addItem(label);
                commandesMap.put(label, commande.getId());
            }

            commandesBox.setSelectedIndex(0);
            loadDetailsCommande();
        } catch (SQLException e) {
            handleDatabaseError("Erreur lors du chargement des commandes", e);
        }
    }

    private void setComponentsEnabled(boolean enabled) {
        modeBox.setEnabled(enabled);
        adresseField.setEnabled(enabled && "livraison".equals(modeBox.getSelectedItem()));
    }

    private void loadDetailsCommande() {
        articlesPanel.removeAll();
        quantiteFields.clear();

        String selectedItem = (String) commandesBox.getSelectedItem();
        if (selectedItem == null || !commandesMap.containsKey(selectedItem)) {
            return;
        }

        int commandeId = commandesMap.get(selectedItem);

        try {
            CommandeDAO commandeDAO = new CommandeDAO();
            Commande commande = commandeDAO.getCommandeById(commandeId);

            if (commande == null) {
                JOptionPane.showMessageDialog(this,
                        "Commande introuvable",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            modeBox.setSelectedItem(commande.getModeRecuperation());
            adresseField.setText(commande.getAdresseLivraison() != null ? commande.getAdresseLivraison() : "");
            updateAddressFieldState();

            DetailsCommandeDAO detailsDAO = new DetailsCommandeDAO();
            List<DetailsCommande> details = detailsDAO.getDetailsCommande(commandeId);

            if (details.isEmpty()) {
                articlesPanel.add(new JLabel("Aucun article trouvé pour cette commande"));
            } else {
                for (DetailsCommande detail : details) {
                    addArticleRow(detail);
                }
            }

            articlesPanel.revalidate();
            articlesPanel.repaint();
        } catch (SQLException e) {
            handleDatabaseError("Erreur lors du chargement des détails", e);
        }
    }

    private void addArticleRow(DetailsCommande detail) {
        JPanel articleRow = new JPanel(new GridLayout(1, 3, 5, 5));
        articleRow.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JLabel nomArticle = new JLabel(detail.getNomArticle());
        JLabel prixArticle = new JLabel(String.format("%.2f €", detail.getPrixUnitaire()));

        JTextField quantiteField = new JTextField(String.valueOf(detail.getQuantite()));
        quantiteField.setHorizontalAlignment(JTextField.CENTER);
        quantiteFields.put(detail.getId(), quantiteField);

        articleRow.add(nomArticle);
        articleRow.add(prixArticle);
        articleRow.add(quantiteField);

        articlesPanel.add(articleRow);
    }

    private void enregistrerModifications() {
        String selectedItem = (String) commandesBox.getSelectedItem();
        if (selectedItem == null || !commandesMap.containsKey(selectedItem)) {
            showErrorMessage("Veuillez sélectionner une commande valide");
            return;
        }

        int commandeId = commandesMap.get(selectedItem);
        String modeRecuperation = (String) modeBox.getSelectedItem();
        String adresseLivraison = adresseField.getText().trim();

        if ("livraison".equals(modeRecuperation) && adresseLivraison.isEmpty()) {
            showErrorMessage("Veuillez fournir une adresse de livraison");
            return;
        }

        try {
            // Validate quantities first
            for (Map.Entry<Integer, JTextField> entry : quantiteFields.entrySet()) {
                try {
                    int quantite = Integer.parseInt(entry.getValue().getText().trim());
                    if (quantite <= 0) {
                        showErrorMessage("La quantité doit être un nombre positif");
                        return;
                    }
                } catch (NumberFormatException e) {
                    showErrorMessage("Veuillez entrer des quantités valides");
                    return;
                }
            }

            // Update order
            CommandeDAO commandeDAO = new CommandeDAO();
            boolean commandeUpdated = commandeDAO.modifierCommande(commandeId, modeRecuperation, adresseLivraison);

            // Update quantities
            DetailsCommandeDAO detailsDAO = new DetailsCommandeDAO();
            boolean allDetailsUpdated = true;

            for (Map.Entry<Integer, JTextField> entry : quantiteFields.entrySet()) {
                int quantite = Integer.parseInt(entry.getValue().getText().trim());
                allDetailsUpdated &= detailsDAO.updateQuantiteDetail(entry.getKey(), quantite);
            }

            if (commandeUpdated && allDetailsUpdated) {
                JOptionPane.showMessageDialog(this,
                        "La commande a été modifiée avec succès.",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                loadCommandes(); // Refresh the list
            } else {
                showErrorMessage("Erreur lors de la modification de la commande");
            }
        } catch (SQLException e) {
            handleDatabaseError("Erreur lors de la modification", e);
        }
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    private void handleDatabaseError(String message, SQLException e) {
        e.printStackTrace();
        showErrorMessage(message + ": " + e.getMessage());
    }
}