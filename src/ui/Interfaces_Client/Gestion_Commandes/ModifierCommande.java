package ui.Interfaces_Client.Gestion_Commandes;

import dao.CommandeDAO;
import dao.DetailsCommandeDAO;
import database.DatabaseConnection;
import model.Commande;
import model.DetailsCommande;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModifierCommande extends JFrame {
    // Modern color scheme - même palette que AccueilClient
    private static final Color PRIMARY_GREEN = new Color(21, 111, 98);
    private static final Color LIGHT_GREEN = new Color(129, 199, 132);
    private static final Color ACCENT_GREEN = new Color(76, 175, 80);
    private static final Color WHITE = Color.WHITE;
    private static final Color LIGHT_GRAY = new Color(248, 249, 250);
    private static final Color CARD_BACKGROUND = new Color(255, 255, 255);
    private static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    private static final Color TEXT_SECONDARY = new Color(108, 117, 125);
    private static final Color SUCCESS_GREEN = new Color(40, 167, 69);
    private static final Color DANGER_RED = new Color(220, 53, 69);
    private static final Color WARNING_ORANGE = new Color(255, 193, 7);

    private final int clientId;
    private JComboBox<String> commandesBox;
    private JPanel articlesPanel;
    private JTextField adresseField;
    private JComboBox<String> modeBox;
    private JPanel mainContentPanel;
    private JPanel headerPanel;
    private final Map<Integer, JTextField> quantiteFields = new HashMap<>();
    private final Map<String, Integer> commandesMap = new HashMap<>();

    public ModifierCommande(int clientId) {
        this.clientId = clientId;
        setupLookAndFeel();
        initializeModernUI();
        loadCommandes();
    }

    private void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 12);
            UIManager.put("TextComponent.arc", 8);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeModernUI() {
        setTitle("Modifier ou Supprimer Commande - Café Shop");
        setSize(800, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LIGHT_GRAY);
        setContentPane(mainPanel);

        // Create modern header
        createModernHeader();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Create main content
        createMainContent();
        mainPanel.add(mainContentPanel, BorderLayout.CENTER);
    }

    private void createModernHeader() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                new EmptyBorder(20, 30, 20, 30)
        ));

        // Left side - Title and subtitle
        JPanel leftHeader = new JPanel();
        leftHeader.setLayout(new BoxLayout(leftHeader, BoxLayout.Y_AXIS));
        leftHeader.setOpaque(false);

        JLabel titleLabel = new JLabel("✏️ Modifier mes Commandes");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_GREEN);

        JLabel subtitleLabel = new JLabel("Modifiez ou supprimez vos commandes non traitées");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setBorder(new EmptyBorder(5, 0, 0, 0));

        leftHeader.add(titleLabel);
        leftHeader.add(subtitleLabel);

        // Right side - Close button
        JButton closeButton = createModernButton("✕ Fermer", DANGER_RED, WHITE);
        closeButton.addActionListener(e -> dispose());

        headerPanel.add(leftHeader, BorderLayout.WEST);
        headerPanel.add(closeButton, BorderLayout.EAST);
    }

    private void createMainContent() {
        mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(LIGHT_GRAY);
        mainContentPanel.setBorder(new EmptyBorder(25, 30, 30, 30));

        // Command selection card
        JPanel selectionCard = createSelectionCard();
        mainContentPanel.add(selectionCard, BorderLayout.NORTH);

        // Articles panel with scroll
        JPanel articlesCard = createArticlesCard();
        mainContentPanel.add(articlesCard, BorderLayout.CENTER);

        // Action buttons
        JPanel actionPanel = createActionPanel();
        mainContentPanel.add(actionPanel, BorderLayout.SOUTH);
    }

    private JPanel createSelectionCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(25, 30, 25, 30)
        ));

        JLabel cardTitle = new JLabel("🛒 Sélection de la Commande");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        cardTitle.setForeground(PRIMARY_GREEN);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        GridBagConstraints gbc = new GridBagConstraints();

        // Command selection
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 15, 15);
        JLabel commandeLabel = new JLabel("Commande à modifier :");
        commandeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        commandeLabel.setForeground(TEXT_PRIMARY);
        formPanel.add(commandeLabel, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        commandesBox = new JComboBox<>();
        commandesBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        commandesBox.addActionListener(e -> loadDetailsCommande());
        formPanel.add(commandesBox, gbc);

        // Mode selection
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel modeLabel = new JLabel("Mode de récupération :");
        modeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        modeLabel.setForeground(TEXT_PRIMARY);
        formPanel.add(modeLabel, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        modeBox = new JComboBox<>(new String[]{"livraison", "sur place", "à importer"});
        modeBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        modeBox.addActionListener(e -> updateAddressFieldState());
        formPanel.add(modeBox, gbc);

        // Address field
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel adresseLabel = new JLabel("Adresse de livraison :");
        adresseLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        adresseLabel.setForeground(TEXT_PRIMARY);
        formPanel.add(adresseLabel, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        adresseField = new JTextField();
        adresseField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        adresseField.setEnabled(false);
        formPanel.add(adresseField, gbc);

        card.add(cardTitle, BorderLayout.NORTH);
        card.add(formPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createArticlesCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(25, 30, 25, 30)
        ));

        JLabel cardTitle = new JLabel("📋 Articles de la Commande");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        cardTitle.setForeground(PRIMARY_GREEN);

        // Articles panel with modern styling
        articlesPanel = new JPanel();
        articlesPanel.setLayout(new BoxLayout(articlesPanel, BoxLayout.Y_AXIS));
        articlesPanel.setOpaque(false);
        articlesPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JScrollPane scrollPane = new JScrollPane(articlesPanel);
        scrollPane.setBackground(CARD_BACKGROUND);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        card.add(cardTitle, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        panel.setOpaque(false);

        // Save button
        JButton saveBtn = createModernButton("💾 Enregistrer les modifications", SUCCESS_GREEN, WHITE);
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveBtn.addActionListener(e -> enregistrerModifications());

        // Delete button
        JButton deleteBtn = createModernButton("🗑️ Supprimer la commande", DANGER_RED, WHITE);
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        deleteBtn.addActionListener(e -> supprimerCommande());

        panel.add(saveBtn);
        panel.add(deleteBtn);

        return panel;
    }

    private void updateAddressFieldState() {
        boolean isLivraison = "livraison".equals(modeBox.getSelectedItem());
        adresseField.setEnabled(isLivraison);
        if (!isLivraison) {
            adresseField.setText("");
        }
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
                showInfoMessage("Information",
                        "Vous n'avez aucune commande non traitée à modifier.");
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
            setComponentsEnabled(true);
        } catch (SQLException e) {
            handleDatabaseError("Erreur lors du chargement des commandes", e);
        }
    }

    private void setComponentsEnabled(boolean enabled) {
        commandesBox.setEnabled(enabled);
        modeBox.setEnabled(enabled);
        updateAddressFieldState();
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
                showErrorMessage("Commande introuvable");
                return;
            }

            // Vérifier que la commande est encore modifiable
            if (!"non traitée".equals(commande.getEtat())) {
                showWarningMessage("Cette commande ne peut plus être modifiée car elle est déjà " + commande.getEtat());
                setComponentsEnabled(false);
                return;
            }

            modeBox.setSelectedItem(commande.getModeRecuperation());
            adresseField.setText(commande.getAdresseLivraison() != null ? commande.getAdresseLivraison() : "");
            updateAddressFieldState();

            DetailsCommandeDAO detailsDAO = new DetailsCommandeDAO();
            List<DetailsCommande> details = detailsDAO.getDetailsCommande(commandeId);

            if (details.isEmpty()) {
                JLabel noItemsLabel = new JLabel("Aucun article trouvé pour cette commande");
                noItemsLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                noItemsLabel.setForeground(TEXT_SECONDARY);
                noItemsLabel.setHorizontalAlignment(SwingConstants.CENTER);
                articlesPanel.add(noItemsLabel);
            } else {
                for (DetailsCommande detail : details) {
                    addModernArticleRow(detail);
                }
            }

            articlesPanel.revalidate();
            articlesPanel.repaint();
        } catch (SQLException e) {
            handleDatabaseError("Erreur lors du chargement des détails", e);
        }
    }

    private void addModernArticleRow(DetailsCommande detail) {
        JPanel articleRow = new JPanel(new BorderLayout());
        articleRow.setBackground(WHITE);
        articleRow.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(15, 20, 15, 20)
        ));
        articleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        // Left panel - Article info
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        JLabel nomLabel = new JLabel(detail.getNomArticle());
        nomLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nomLabel.setForeground(TEXT_PRIMARY);

        JLabel prixLabel = new JLabel(String.format("%.2f € l'unité", detail.getPrixUnitaire()));
        prixLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        prixLabel.setForeground(TEXT_SECONDARY);

        leftPanel.add(nomLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(prixLabel);

        // Right panel - Quantity
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setOpaque(false);

        JLabel qtyLabel = new JLabel("Quantité :");
        qtyLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        qtyLabel.setForeground(TEXT_PRIMARY);

        JTextField quantiteField = new JTextField(String.valueOf(detail.getQuantite()), 5);
        quantiteField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        quantiteField.setHorizontalAlignment(JTextField.CENTER);
        quantiteFields.put(detail.getId(), quantiteField);

        rightPanel.add(qtyLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        rightPanel.add(quantiteField);

        articleRow.add(leftPanel, BorderLayout.WEST);
        articleRow.add(rightPanel, BorderLayout.EAST);

        articlesPanel.add(articleRow);
        articlesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
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
                showSuccessMessage("La commande a été modifiée avec succès.");
                loadCommandes(); // Refresh the list
            } else {
                showErrorMessage("Erreur lors de la modification de la commande");
            }
        } catch (SQLException e) {
            handleDatabaseError("Erreur lors de la modification", e);
        }
    }

    private void supprimerCommande() {
        String selectedItem = (String) commandesBox.getSelectedItem();
        if (selectedItem == null || !commandesMap.containsKey(selectedItem)) {
            showErrorMessage("Veuillez sélectionner une commande valide");
            return;
        }

        int result = JOptionPane.showConfirmDialog(
                this,
                "Êtes-vous sûr de vouloir supprimer cette commande ?\nCette action est irréversible.",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            int commandeId = commandesMap.get(selectedItem);

            try {
                // Supprimer d'abord les détails puis la commande
                DetailsCommandeDAO detailsDAO = new DetailsCommandeDAO();
                CommandeDAO commandeDAO = new CommandeDAO();

                boolean detailsDeleted = detailsDAO.supprimerDetailsCommande(commandeId);
                boolean commandeDeleted = commandeDAO.supprimerCommande(commandeId);

                if (commandeDeleted) {
                    showSuccessMessage("La commande a été supprimée avec succès.");
                    loadCommandes(); // Refresh the list
                } else {
                    showErrorMessage("Erreur lors de la suppression de la commande");
                }
            } catch (SQLException e) {
                handleDatabaseError("Erreur lors de la suppression", e);
            }
        }
    }

    private JButton createModernButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(12, 24, 12, 24));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarningMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Attention", JOptionPane.WARNING_MESSAGE);
    }

    private void showInfoMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleDatabaseError(String message, SQLException e) {
        e.printStackTrace();
        showErrorMessage(message + ": " + e.getMessage());
    }
}