package ui.Interfaces_Client;

import dao.ArticleDAO;
import dao.CommandeDAO;
import dao.DetailsCommandeDAO;
import dao.ImageService;
import database.DatabaseConnection;
import model.Article;
import ui.Interfaces_Client.Gestion_Commandes.PasserCommande;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class ConsulterCarteAvecCommande extends JFrame {
    // Modern color scheme - cohérent avec AccueilClient
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
    private static final Color HOVER_COLOR = new Color(240, 255, 248);

    // Composants
    private JPanel mainPanel;
    private JPanel menuPanel;
    private JScrollPane scrollPane;
    private JTextField searchField;
    private JComboBox<String> filterCombo;
    private JButton commanderButton;
    private JButton refreshButton;
    private JLabel totalLabel;
    private JComboBox<String> modeRecupCombo;
    private JTextField adresseLivraisonField;
    private JButton viderPanierButton;

    // Données
    private List<Article> allArticles;
    private Map<String, List<Article>> articlesParType;
    private Map<Integer, JSpinner> articleQuantites = new HashMap<>(); // Map articleId -> spinner
    private int clientId = -1;

    public ConsulterCarteAvecCommande(int clientId) {
        this.clientId = clientId;
        setupUI();
        chargerCarte();
        updateTotal();
    }

    private void setupUI() {
        String titre = clientId > 0 ? "Café Shop - Menu & Commande" : "Café Shop - Carte du Menu";
        setTitle(titre);
        setSize(1500, 950);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Panneau principal avec style moderne
        mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(LIGHT_GRAY);
        add(mainPanel);

        createModernHeader();
        createModernContent();
        if (clientId > 0) {
            createCommandeFooter();
        }
    }

    private void createModernHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(224, 224, 224)),
                new EmptyBorder(25, 30, 25, 30)
        ));

        // Panneau gauche - Titre et sous-titre
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(WHITE);

        String titre = clientId > 0 ? "☕ Menu & Commande" : "☕ Notre Carte";
        JLabel titleLabel = new JLabel(titre);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_GREEN);

        String sousTitre = clientId > 0 ?
                "Sélectionnez vos articles et ajustez les quantités" :
                "Découvrez notre sélection de délicieux cafés et accompagnements";
        JLabel subtitleLabel = new JLabel(sousTitre);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setBorder(new EmptyBorder(5, 0, 0, 0));

        leftPanel.add(titleLabel);
        leftPanel.add(subtitleLabel);

        // Panneau central - Recherche et filtres
        JPanel searchPanel = createSearchPanel();

        // Panneau droit - Configuration commande (si client connecté)
        JPanel rightPanel;
        if (clientId > 0) {
            rightPanel = createCommandeConfigPanel();
        } else {
            rightPanel = createActionPanel();
        }

        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.CENTER);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        searchPanel.setBackground(WHITE);

        // Champ de recherche moderne
        searchField = new JTextField(25);
        searchField.putClientProperty("JTextField.placeholderText", "Rechercher un article...");
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
                new EmptyBorder(10, 12, 10, 12)
        ));
        searchField.addActionListener(e -> filterArticles());

        // ComboBox moderne pour les catégories
        filterCombo = new JComboBox<>(new String[]{"Toutes les catégories"});
        filterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filterCombo.setPreferredSize(new Dimension(200, 40));
        filterCombo.addActionListener(e -> filterArticles());

        searchPanel.add(searchField);
        searchPanel.add(filterCombo);

        return searchPanel;
    }

    private JPanel createCommandeConfigPanel() {
        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBackground(CARD_BACKGROUND);
        configPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(15, 20, 15, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Mode de récupération
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel modeLabel = new JLabel("Mode :");
        modeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        modeLabel.setForeground(TEXT_PRIMARY);
        configPanel.add(modeLabel, gbc);

        gbc.gridx = 1;
        modeRecupCombo = new JComboBox<>(new String[]{"livraison", "sur place", "à emporter"});
        modeRecupCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        modeRecupCombo.setPreferredSize(new Dimension(140, 30));
        modeRecupCombo.addActionListener(e -> {
            boolean isLivraison = "livraison".equals(modeRecupCombo.getSelectedItem());
            adresseLivraisonField.setEnabled(isLivraison);
            if (!isLivraison) {
                adresseLivraisonField.setText("");
            }
        });
        configPanel.add(modeRecupCombo, gbc);

        // Adresse de livraison
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel adresseLabel = new JLabel("Adresse :");
        adresseLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        adresseLabel.setForeground(TEXT_PRIMARY);
        configPanel.add(adresseLabel, gbc);

        gbc.gridx = 1;
        adresseLivraisonField = new JTextField();
        adresseLivraisonField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        adresseLivraisonField.setPreferredSize(new Dimension(140, 30));
        adresseLivraisonField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(5, 8, 5, 8)
        ));
        adresseLivraisonField.setEnabled(false);
        configPanel.add(adresseLivraisonField, gbc);

        return configPanel;
    }

    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionPanel.setBackground(WHITE);

        // Bouton refresh moderne
        refreshButton = createModernButton("🔄 Actualiser", TEXT_SECONDARY, WHITE);
        refreshButton.addActionListener(e -> {
            chargerCarte();
            updateTotal();
        });

        actionPanel.add(refreshButton);
        return actionPanel;
    }

    private void createCommandeFooter() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(WHITE);
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
                new EmptyBorder(20, 30, 20, 30)
        ));

        // Panel total et actions panier
        JPanel leftFooterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        leftFooterPanel.setBackground(WHITE);

        totalLabel = new JLabel("Total: 0.00 DT");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        totalLabel.setForeground(PRIMARY_GREEN);

        viderPanierButton = createModernButton("🗑️ Vider panier", WARNING_COLOR, WHITE);
        viderPanierButton.addActionListener(e -> viderPanier());

        leftFooterPanel.add(totalLabel);
        leftFooterPanel.add(viderPanierButton);

        // Panel boutons principaux
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(WHITE);

        refreshButton = createModernButton("🔄 Actualiser", TEXT_SECONDARY, WHITE);
        refreshButton.addActionListener(e -> {
            chargerCarte();
            updateTotal();
        });

        commanderButton = createModernButton("🛒 Valider la commande", SUCCESS_COLOR, WHITE);
        commanderButton.addActionListener(e -> validerCommande());

        buttonPanel.add(refreshButton);
        buttonPanel.add(commanderButton);

        footerPanel.add(leftFooterPanel, BorderLayout.WEST);
        footerPanel.add(buttonPanel, BorderLayout.EAST);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);
    }

    private JButton createModernButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1),
                new EmptyBorder(12, 24, 12, 24)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Ajout d'ombre et effet moderne
        button.setBorderPainted(true);
        button.setContentAreaFilled(true);
        button.setOpaque(true);

        // Effet hover amélioré avec transition douce
        button.addMouseListener(new MouseAdapter() {
            private Timer hoverTimer;

            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    // Animation douce
                    if (hoverTimer != null) hoverTimer.stop();
                    hoverTimer = new Timer(10, evt -> {
                        Color currentBg = button.getBackground();
                        Color targetBg = new Color(
                                Math.min(255, currentBg.getRed() + 15),
                                Math.min(255, currentBg.getGreen() + 15),
                                Math.min(255, currentBg.getBlue() + 15)
                        );
                        button.setBackground(targetBg);
                        button.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(targetBg.darker(), 2),
                                new EmptyBorder(11, 23, 11, 23)
                        ));
                    });
                    hoverTimer.start();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    if (hoverTimer != null) hoverTimer.stop();
                    button.setBackground(bgColor);
                    button.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(bgColor.darker(), 1),
                            new EmptyBorder(12, 24, 12, 24)
                    ));
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(bgColor.darker().darker(), 2),
                            new EmptyBorder(13, 25, 11, 23)
                    ));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(bgColor.darker(), 1),
                            new EmptyBorder(12, 24, 12, 24)
                    ));
                }
            }
        });

        return button;
    }

    private void createModernContent() {
        // Panneau principal pour le contenu
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(LIGHT_GRAY);
        contentWrapper.setBorder(new EmptyBorder(25, 30, 30, 30));

        // Panneau pour les catégories
        menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(LIGHT_GRAY);

        // ScrollPane moderne
        scrollPane = new JScrollPane(menuPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(LIGHT_GRAY);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(20);

        contentWrapper.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(contentWrapper, BorderLayout.CENTER);
    }

    private void chargerCarte() {
        menuPanel.removeAll();
        articlesParType = new HashMap<>();
        articleQuantites.clear();

        try {
            ArticleDAO articleDAO = new ArticleDAO();
            allArticles = articleDAO.listerArticles();

            // Regrouper les articles par type
            for (Article article : allArticles) {
                String typeNom = article.getType().getNom();
                articlesParType.computeIfAbsent(typeNom, k -> new ArrayList<>()).add(article);

                // Initialiser les spinners de quantité pour le mode commande
                if (clientId > 0) {
                    JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
                    spinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    spinner.setPreferredSize(new Dimension(70, 30));
                    spinner.addChangeListener(e -> updateTotal());
                    articleQuantites.put(article.getId(), spinner);
                }
            }

            // Remplir la combobox de filtre
            filterCombo.removeAllItems();
            filterCombo.addItem("Toutes les catégories");
            for (String typeNom : articlesParType.keySet()) {
                filterCombo.addItem(typeNom);
            }

            // Créer les panneaux de catégories
            boolean first = true;
            for (Map.Entry<String, List<Article>> entry : articlesParType.entrySet()) {
                if (!first) {
                    menuPanel.add(Box.createVerticalStrut(30));
                }
                JPanel categoryPanel = createModernCategoryPanel(entry.getKey(), entry.getValue());
                menuPanel.add(categoryPanel);
                first = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            showModernMessage("Erreur lors du chargement de la carte du menu.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        menuPanel.revalidate();
        menuPanel.repaint();
    }

    private JPanel createModernCategoryPanel(String categoryName, List<Article> articles) {
        JPanel categoryPanel = new JPanel(new BorderLayout(0, 20));
        categoryPanel.setBackground(CARD_BACKGROUND);
        categoryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(25, 25, 25, 25)
        ));

        // Header de la catégorie
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_BACKGROUND);

        JLabel titleLabel = new JLabel(categoryName);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(PRIMARY_GREEN);

        // Compteur d'articles
        JLabel countLabel = new JLabel(articles.size() + " article" + (articles.size() > 1 ? "s" : ""));
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        countLabel.setForeground(TEXT_SECONDARY);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(countLabel, BorderLayout.EAST);

        int columns = 5; ;
        JPanel gridPanel = new JPanel(new GridLayout(0, columns, 15, 20)); // Moins d'espacement horizontal
        gridPanel.setBackground(CARD_BACKGROUND);

        for (Article article : articles) {
            JPanel articleCard = createModernArticleCard(article);
            gridPanel.add(articleCard);
        }

        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(CARD_BACKGROUND);
        contentWrapper.add(gridPanel, BorderLayout.NORTH);

        categoryPanel.add(headerPanel, BorderLayout.NORTH);
        categoryPanel.add(contentWrapper, BorderLayout.CENTER);

        return categoryPanel;
    }

    private JPanel createModernArticleCard(Article article) {
        JPanel card = new JPanel(new BorderLayout());
        int cardWidth = 240; // Plus petit pour 5 colonnes
        int cardHeight = clientId > 0 ? 350 : 300;
        card.setPreferredSize(new Dimension(cardWidth, cardHeight));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(233, 236, 239), 1),
                new EmptyBorder(0, 0, 0, 0)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Panneau image moderne
        JPanel imagePanel = createImagePanel(article);

        // Panneau informations moderne
        JPanel infoPanel = createInfoPanel(article);

        card.add(imagePanel, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);

        // Ajouter contrôles de quantité si en mode commande
        if (clientId > 0) {
            JPanel quantityPanel = createQuantityPanel(article);
            card.add(quantityPanel, BorderLayout.SOUTH);
        }

        // Effet de survol moderne
        setupCardHoverEffect(card, article);

        return card;
    }

    private JPanel createQuantityPanel(Article article) {
        JPanel quantityPanel = new JPanel(new BorderLayout());
        quantityPanel.setBackground(new Color(248, 250, 252));
        quantityPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
                new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel qtyLabel = new JLabel("Quantité :");
        qtyLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        qtyLabel.setForeground(TEXT_PRIMARY);

        JSpinner quantiteSpinner = articleQuantites.get(article.getId());

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlPanel.setBackground(new Color(248, 250, 252));
        controlPanel.add(qtyLabel);
        controlPanel.add(quantiteSpinner);

        quantityPanel.add(controlPanel, BorderLayout.EAST);

        return quantityPanel;
    }

    private JPanel createImagePanel(Article article) {
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setPreferredSize(new Dimension(280, 160));
        imagePanel.setBackground(new Color(248, 249, 250));

        try {
            ImageIcon icon;
            if (article.getImagePath() != null && !article.getImagePath().isEmpty()) {
                icon = new ImageIcon(ImageService.getImagePath(article.getImagePath()));
            } else {
                icon = createPlaceholderIcon();
            }

            Image img = icon.getImage().getScaledInstance(280, 160, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(img));
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imagePanel.add(imageLabel, BorderLayout.CENTER);
        } catch (Exception e) {
            JLabel placeholder = createImagePlaceholder(article.getType().getNom());
            imagePanel.add(placeholder, BorderLayout.CENTER);
        }

        return imagePanel;
    }

    private ImageIcon createPlaceholderIcon() {
        return new ImageIcon(new byte[0]);
    }

    private JLabel createImagePlaceholder(String categoryName) {
        JLabel placeholder = new JLabel();
        placeholder.setHorizontalAlignment(SwingConstants.CENTER);
        placeholder.setVerticalAlignment(SwingConstants.CENTER);
        placeholder.setOpaque(true);
        placeholder.setBackground(new Color(240, 240, 240));

        String icon = "☕";
        if (categoryName.toLowerCase().contains("pâtisserie")) {
            icon = "🧁";
        } else if (categoryName.toLowerCase().contains("sandwich")) {
            icon = "🥪";
        } else if (categoryName.toLowerCase().contains("boisson")) {
            icon = "🥤";
        }

        placeholder.setText("<html><div style='text-align: center; font-size: 28px;'>" +
                icon + "<br><span style='font-size: 10px; color: #6c757d;'>Image non disponible</span></div></html>");

        return placeholder;
    }

    private JPanel createInfoPanel(Article article) {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(WHITE);
        infoPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Nom de l'article
        JLabel nameLabel = new JLabel(article.getNom());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        nameLabel.setForeground(TEXT_PRIMARY);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Prix avec style moderne
        JLabel priceLabel = new JLabel(String.format("%.2f DT", article.getPrix()));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        priceLabel.setForeground(PRIMARY_GREEN);
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Description avec limitation de texte
        String desc = article.getDescription();
        if (desc != null && desc.length() > 70) {
            desc = desc.substring(0, 67) + "...";
        }
        JTextArea descArea = new JTextArea(desc == null ? "Délicieux produit de notre sélection" : desc);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descArea.setForeground(TEXT_SECONDARY);
        descArea.setBackground(WHITE);
        descArea.setEditable(false);
        descArea.setWrapStyleWord(true);
        descArea.setLineWrap(true);
        descArea.setRows(2);
        descArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(priceLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(descArea);

        return infoPanel;
    }

    private void setupCardHoverEffect(JPanel card, Article article) {
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ACCENT_GREEN, 2),
                        new EmptyBorder(0, 0, 0, 0)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(233, 236, 239), 1),
                        new EmptyBorder(0, 0, 0, 0)
                ));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (clientId <= 0) {
                    showArticleDetails(article);
                }
            }
        });
    }

    private void showArticleDetails(Article article) {
        String message = String.format(
                "<html><div style='width: 350px; font-family: Segoe UI;'>" +
                        "<h2 style='color: #156F62; margin-bottom: 15px;'>%s</h2>" +
                        "<p style='font-size: 18px; color: #4CAF50; font-weight: bold; margin-bottom: 10px;'>Prix: %.2f DT</p>" +
                        "<p style='font-size: 14px; color: #6C757D; margin-bottom: 15px;'>Catégorie: %s</p>" +
                        "<div style='padding: 15px; background-color: #f8f9fa; border-radius: 8px;'>" +
                        "<p style='font-size: 13px; color: #212529; line-height: 1.5; margin: 0;'>%s</p>" +
                        "</div>" +
                        "</div></html>",
                article.getNom(),
                article.getPrix(),
                article.getType().getNom(),
                article.getDescription() != null ? article.getDescription() : "Un délicieux produit de notre sélection, préparé avec soin et des ingrédients de qualité."
        );

        JDialog dialog = new JDialog(this, "Détails de l'article", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel dialogPanel = new JPanel(new BorderLayout(0, 20));
        dialogPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        dialogPanel.setBackground(WHITE);

        JLabel messageLabel = new JLabel(message);
        dialogPanel.add(messageLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(WHITE);

        JButton fermerBtn = createModernButton("Fermer", TEXT_SECONDARY, WHITE);
        fermerBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(fermerBtn);

        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(dialogPanel);
        dialog.setVisible(true);
    }

    private void updateTotal() {
        if (clientId <= 0) return;

        double total = 0.0;
        for (Article article : allArticles) {
            JSpinner spinner = articleQuantites.get(article.getId());
            if (spinner != null) {
                int qty = (int) spinner.getValue();
                total += qty * article.getPrix();
            }
        }

        if (totalLabel != null) {
            totalLabel.setText(String.format("Total: %.2f DT", total));
        }
    }

    private void viderPanier() {
        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Êtes-vous sûr de vouloir vider votre panier ?",
                "Vider le panier",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmation == JOptionPane.YES_OPTION) {
            for (JSpinner spinner : articleQuantites.values()) {
                spinner.setValue(0);
            }
            updateTotal();
        }
    }

    private void validerCommande() {
        String mode = (String) modeRecupCombo.getSelectedItem();
        String adresse = adresseLivraisonField.getText().trim();

        // Validation
        if ("livraison".equals(mode) && adresse.isEmpty()) {
            showModernMessage("Adresse requise pour la livraison.", "Erreur de validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Vérifier qu'au moins un article est sélectionné
        boolean auMoinsUnArticle = false;
        List<Integer> articlesACommander = new ArrayList<>();
        List<Integer> quantitesArticles = new ArrayList<>();
        double total = 0.0;

        for (Article article : allArticles) {
            JSpinner spinner = articleQuantites.get(article.getId());
            if (spinner != null) {
                int qty = (int) spinner.getValue();
                if (qty > 0) {
                    auMoinsUnArticle = true;
                    articlesACommander.add(article.getId());
                    quantitesArticles.add(qty);
                    total += qty * article.getPrix();
                }
            }
        }

        if (!auMoinsUnArticle) {
            showModernMessage("Veuillez sélectionner au moins un article.", "Erreur de validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirmation
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
                // Vérifier que le client existe avant de créer la commande
                if (!clientExists(clientId)) {
                    throw new SQLException("Le client spécifié n'existe pas dans le système");
                }

                int commandeId = commandeDAO.creerCommande(clientId, mode, adresse);

                if (commandeId == -1) {
                    throw new SQLException("Erreur lors de la création de la commande");
                }

                detailsDAO.ajouterDetailsCommandeBatch(commandeId, articlesACommander, quantitesArticles);

                conn.commit();

                showModernMessage(
                        String.format("Commande #%d enregistrée avec succès !\n\nTotal: %.2f DT\nMode: %s",
                                commandeId, total, mode),
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // Réinitialiser le panier après commande réussie
                viderPanier();

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            String errorMessage;
            if (e.getMessage().contains("foreign key constraint fails")) {
                errorMessage = "Erreur: Le client spécifié n'existe pas dans le système.";
            } else {
                errorMessage = "Erreur lors de l'enregistrement de la commande: " + e.getMessage();
            }
            showModernMessage(errorMessage, "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean clientExists(int clientId) throws SQLException {
        String sql = "SELECT id FROM clients WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Returns true if client exists
            }
        }
    }

    private void showModernMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
    private void filterArticles() {
        String searchText = searchField.getText().toLowerCase();
        String selectedCategory = (String) filterCombo.getSelectedItem();

        menuPanel.removeAll();

        boolean first = true;
        for (Map.Entry<String, List<Article>> entry : articlesParType.entrySet()) {
            String categoryName = entry.getKey();

            // Filtrer par catégorie
            boolean showCategory = "Toutes les catégories".equals(selectedCategory) ||
                    categoryName.equals(selectedCategory);

            if (!showCategory) continue;

            // Filtrer les articles par recherche
            List<Article> filteredArticles = new ArrayList<>();
            for (Article article : entry.getValue()) {
                boolean matchesSearch = searchText.isEmpty() ||
                        (article.getNom() != null && article.getNom().toLowerCase().contains(searchText)) ||
                        (article.getDescription() != null && article.getDescription().toLowerCase().contains(searchText));

                if (matchesSearch) {
                    filteredArticles.add(article);
                }
            }

            // Afficher la catégorie seulement si elle contient des articles
            if (!filteredArticles.isEmpty()) {
                if (!first) {
                    menuPanel.add(Box.createVerticalStrut(30));
                }
                JPanel categoryPanel = createModernCategoryPanel(categoryName, filteredArticles);
                menuPanel.add(categoryPanel);
                first = false;
            }
        }

        menuPanel.revalidate();
        menuPanel.repaint();
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ConsulterCarteAvecCommande(2).setVisible(true));
    }
}