package ui.Interfaces_Client.Gestion_Commandes;

import dao.CommandeDAO;
import dao.DetailsCommandeDAO;
import database.DatabaseConnection;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PasserCommande extends JFrame {
    // Modern color scheme - cohérent avec les autres interfaces
    private static final Color PRIMARY_GREEN = new Color(21, 111, 98);
    private static final Color LIGHT_GREEN = new Color(129, 199, 132);
    private static final Color ACCENT_GREEN = new Color(76, 175, 80);
    private static final Color WHITE = Color.WHITE;
    private static final Color LIGHT_GRAY = new Color(248, 249, 250);
    private static final Color CARD_BACKGROUND = new Color(255, 255, 255);
    private static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    private static final Color TEXT_SECONDARY = new Color(108, 117, 125);
    private static final Color BORDER_COLOR = new Color(222, 226, 230);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color WARNING_COLOR = new Color(255, 193, 7);

    private int clientId;
    private JComboBox<String> modeRecupCombo;
    private JTextField adresseLivraisonField;
    private JPanel articlesPanel;
    private JPanel mainPanel;
    private JScrollPane scrollPane;
    private JLabel totalLabel;
    private ArrayList<JSpinner> quantites = new ArrayList<>();
    private ArrayList<Integer> articleIds = new ArrayList<>();
    private ArrayList<Double> articlesPrix = new ArrayList<>();

    public PasserCommande(int clientId) {
        this.clientId = clientId;
        setupLookAndFeel();
        initializeModernUI();
        loadArticles();
        updateTotal();
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
        setTitle("Café Shop - Passer une Commande");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Panneau principal
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LIGHT_GRAY);
        setContentPane(mainPanel);

        createModernHeader();
        createModernContent();
        createModernFooter();
    }

    private void createModernHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                new EmptyBorder(25, 30, 25, 30)
        ));

        // Titre et sous-titre
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(WHITE);

        JLabel titleLabel = new JLabel("🛒 Passer une Commande");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_GREEN);

        JLabel subtitleLabel = new JLabel("Sélectionnez vos articles et configurez votre commande");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setBorder(new EmptyBorder(5, 0, 0, 0));

        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        // Panel de configuration de commande
        JPanel configPanel = createConfigPanel();

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(configPanel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
    }

    private JPanel createConfigPanel() {
        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBackground(CARD_BACKGROUND);
        configPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(20, 25, 20, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 15);
        gbc.anchor = GridBagConstraints.WEST;

        // Mode de récupération
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel modeLabel = new JLabel("Mode de récupération :");
        modeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        modeLabel.setForeground(TEXT_PRIMARY);
        configPanel.add(modeLabel, gbc);

        gbc.gridx = 1;
        modeRecupCombo = new JComboBox<>(new String[]{"livraison", "sur place", "à emporter"});
        modeRecupCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        modeRecupCombo.setPreferredSize(new Dimension(200, 35));
        modeRecupCombo.addActionListener(e -> {
            boolean isLivraison = "livraison".equals(modeRecupCombo.getSelectedItem());
            adresseLivraisonField.setEnabled(isLivraison);
            if (!isLivraison) {
                adresseLivraisonField.setText("");
            }
        });
        configPanel.add(modeRecupCombo, gbc);

        // Adresse de livraison
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel adresseLabel = new JLabel("Adresse de livraison :");
        adresseLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        adresseLabel.setForeground(TEXT_PRIMARY);
        configPanel.add(adresseLabel, gbc);

        gbc.gridx = 1;
        adresseLivraisonField = new JTextField();
        adresseLivraisonField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        adresseLivraisonField.setPreferredSize(new Dimension(200, 35));
        adresseLivraisonField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        adresseLivraisonField.setEnabled(false);
        configPanel.add(adresseLivraisonField, gbc);

        return configPanel;
    }

    private void createModernContent() {
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(LIGHT_GRAY);
        contentWrapper.setBorder(new EmptyBorder(25, 30, 25, 30));

        // Titre de la section articles
        JPanel sectionHeader = new JPanel(new BorderLayout());
        sectionHeader.setBackground(LIGHT_GRAY);
        sectionHeader.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel sectionTitle = new JLabel("Sélectionnez vos articles");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        sectionTitle.setForeground(TEXT_PRIMARY);

        JLabel instructionLabel = new JLabel("Ajustez les quantités avec les contrôles à droite de chaque article");
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        instructionLabel.setForeground(TEXT_SECONDARY);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(LIGHT_GRAY);
        titlePanel.add(sectionTitle);
        titlePanel.add(instructionLabel);

        sectionHeader.add(titlePanel, BorderLayout.WEST);

        // Panel des articles
        articlesPanel = new JPanel();
        articlesPanel.setLayout(new BoxLayout(articlesPanel, BoxLayout.Y_AXIS));
        articlesPanel.setBackground(LIGHT_GRAY);

        scrollPane = new JScrollPane(articlesPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(LIGHT_GRAY);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        contentWrapper.add(sectionHeader, BorderLayout.NORTH);
        contentWrapper.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(contentWrapper, BorderLayout.CENTER);
    }

    private void createModernFooter() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(WHITE);
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
                new EmptyBorder(20, 30, 20, 30)
        ));

        // Panel total
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        totalPanel.setBackground(WHITE);

        totalLabel = new JLabel("Total: 0.00 DT");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        totalLabel.setForeground(PRIMARY_GREEN);

        totalPanel.add(totalLabel);

        // Panel boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(WHITE);

        JButton annulerBtn = createModernButton("Annuler", TEXT_SECONDARY, WHITE);
        annulerBtn.addActionListener(e -> dispose());

        JButton validerBtn = createModernButton("🛒 Valider la commande", SUCCESS_COLOR, WHITE);
        validerBtn.addActionListener(e -> passerCommande());

        buttonPanel.add(annulerBtn);
        buttonPanel.add(validerBtn);

        footerPanel.add(totalPanel, BorderLayout.WEST);
        footerPanel.add(buttonPanel, BorderLayout.EAST);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);
    }

    private JButton createModernButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(new EmptyBorder(12, 24, 12, 24));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(bgColor.brighter());
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(bgColor);
                }
            }
        });

        return button;
    }

    private void loadArticles() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT id, nom, prix FROM articles ORDER BY nom";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int articleId = rs.getInt("id");
                String nom = rs.getString("nom");
                double prix = rs.getDouble("prix");

                JPanel articleCard = createModernArticleCard(articleId, nom, prix);
                articlesPanel.add(articleCard);
                articlesPanel.add(Box.createVerticalStrut(10));

                articleIds.add(articleId);
                articlesPrix.add(prix);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showModernMessage("Erreur lors du chargement des articles.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createModernArticleCard(int articleId, String nom, double prix) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(20, 25, 20, 25)
        ));

        // Panel gauche - Info produit
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(CARD_BACKGROUND);

        JLabel nameLabel = new JLabel(nom);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(TEXT_PRIMARY);

        JLabel priceLabel = new JLabel(String.format("%.2f DT", prix));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        priceLabel.setForeground(PRIMARY_GREEN);
        priceLabel.setBorder(new EmptyBorder(5, 0, 0, 0));

        infoPanel.add(nameLabel);
        infoPanel.add(priceLabel);

        // Panel droit - Contrôles quantité
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setBackground(CARD_BACKGROUND);

        JLabel qtyLabel = new JLabel("Quantité :");
        qtyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        qtyLabel.setForeground(TEXT_PRIMARY);

        JSpinner quantiteSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
        quantiteSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        quantiteSpinner.setPreferredSize(new Dimension(80, 35));

        // Listener pour mettre à jour le total
        quantiteSpinner.addChangeListener(e -> updateTotal());

        quantites.add(quantiteSpinner);

        controlPanel.add(qtyLabel);
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(quantiteSpinner);

        card.add(infoPanel, BorderLayout.WEST);
        card.add(controlPanel, BorderLayout.EAST);

        return card;
    }

    private void updateTotal() {
        double total = 0.0;
        for (int i = 0; i < quantites.size(); i++) {
            int qty = (int) quantites.get(i).getValue();
            double prix = articlesPrix.get(i);
            total += qty * prix;
        }
        totalLabel.setText(String.format("Total: %.2f DT", total));
    }

    private void passerCommande() {
        String mode = (String) modeRecupCombo.getSelectedItem();
        String adresse = adresseLivraisonField.getText().trim();

        // Validation
        if ("livraison".equals(mode) && adresse.isEmpty()) {
            showModernMessage("Adresse requise pour la livraison.", "Erreur de validation", JOptionPane.WARNING_MESSAGE);
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
            showModernMessage("Veuillez sélectionner au moins un article.", "Erreur de validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirmation
        double total = 0.0;
        for (int i = 0; i < quantites.size(); i++) {
            int qty = (int) quantites.get(i).getValue();
            double prix = articlesPrix.get(i);
            total += qty * prix;
        }

        String confirmMessage = String.format(
                "Confirmer la commande ?\n\nMode: %s\n%sTotal: %.2f DT",
                mode,
                "livraison".equals(mode) ? "Adresse: " + adresse + "\n" : "",
                total
        );

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                confirmMessage,
                "Confirmation de commande",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        // Traitement de la commande
        try {
            CommandeDAO commandeDAO = new CommandeDAO();
            DetailsCommandeDAO detailsDAO = new DetailsCommandeDAO();

            Connection conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try {
                int commandeId = commandeDAO.creerCommande(clientId, mode, adresse);

                if (commandeId == -1) {
                    throw new SQLException("Erreur lors de la création de la commande");
                }

                List<Integer> articlesACommander = new ArrayList<>();
                List<Integer> quantitesArticles = new ArrayList<>();

                for (int i = 0; i < quantites.size(); i++) {
                    int qte = (int) quantites.get(i).getValue();
                    if (qte > 0) {
                        articlesACommander.add(articleIds.get(i));
                        quantitesArticles.add(qte);
                    }
                }

                detailsDAO.ajouterDetailsCommandeBatch(commandeId, articlesACommander, quantitesArticles);

                conn.commit();

                showModernMessage(
                        String.format("Commande #%d enregistrée avec succès !\n\nTotal: %.2f DT\nMode: %s",
                                commandeId, total, mode),
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE
                );

                dispose();

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showModernMessage("Erreur lors de l'enregistrement de la commande : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showModernMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PasserCommande(2).setVisible(true));
    }
}