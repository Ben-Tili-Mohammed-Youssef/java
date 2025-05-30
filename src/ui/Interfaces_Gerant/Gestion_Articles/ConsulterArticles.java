package ui.Interfaces_Gerant.Gestion_Articles;

import dao.ArticleDAO;
import dao.ImageService;
import dao.TypeArticleDAO;
import model.Article;
import model.TypeArticle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsulterArticles extends JFrame {
    // Modern color scheme - cohérent avec AccueilGerant
    private static final Color PRIMARY_BLUE = new Color(13, 71, 161);
    private static final Color SECONDARY_BLUE = new Color(25, 118, 210);
    private static final Color ACCENT_BLUE = new Color(33, 150, 243);
    private static final Color SUCCESS_GREEN = new Color(76, 175, 80);
    private static final Color WARNING_ORANGE = new Color(255, 152, 0);
    private static final Color DANGER_RED = new Color(244, 67, 54);
    private static final Color WHITE = Color.WHITE;
    private static final Color LIGHT_GRAY = new Color(248, 249, 250);
    private static final Color CARD_BACKGROUND = new Color(255, 255, 255);
    private static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    private static final Color TEXT_SECONDARY = new Color(108, 117, 125);
    private static final Color BORDER_COLOR = new Color(222, 226, 230);
    private static final Color HOVER_COLOR = new Color(240, 245, 255);

    // Composants
    private JPanel mainPanel;
    private JPanel menuPanel;
    private JScrollPane scrollPane;
    private JTextField searchField;
    private JComboBox<String> filterCombo;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;

    // Données
    private List<Article> allArticles;
    private Article selectedArticle = null;
    private Map<Integer, JPanel> categoryPanels = new HashMap<>();
    private List<TypeArticle> orderedTypes = new ArrayList<>();

    public ConsulterArticles() {
        setupUI();
        chargerArticles();
    }

    private void setupUI() {
        setTitle("Gestion des Articles - Carte du Menu");
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Panneau principal avec style moderne
        mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(LIGHT_GRAY);
        add(mainPanel);

        createModernHeader();
        createModernContent();
    }

    private void createModernHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(224, 224, 224)),
                new EmptyBorder(25, 30, 25, 30)
        ));

        // Titre principal
        JLabel titleLabel = new JLabel("Gestion des Articles");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_PRIMARY);

        // Panneau de recherche et filtres
        JPanel searchPanel = createSearchPanel();

        // Panneau des boutons d'action
        JPanel actionPanel = createActionPanel();

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.CENTER);
        headerPanel.add(actionPanel, BorderLayout.EAST);

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

        // Bouton refresh moderne
        refreshButton = createModernButton("Actualiser", SECONDARY_BLUE, WHITE);
        refreshButton.addActionListener(e -> chargerArticles());

        searchPanel.add(searchField);
        searchPanel.add(filterCombo);
        searchPanel.add(refreshButton);

        return searchPanel;
    }

    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionPanel.setBackground(WHITE);

        // Boutons d'action modernes
        addButton = createModernButton("Ajouter", SUCCESS_GREEN, WHITE);
        editButton = createModernButton("Modifier", WARNING_ORANGE, WHITE);
        deleteButton = createModernButton("Supprimer", DANGER_RED, WHITE);

        // Désactiver les boutons qui nécessitent une sélection
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);

        setupButtonActions();

        actionPanel.add(addButton);
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);

        return actionPanel;
    }

    private JButton createModernButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(new EmptyBorder(12, 24, 12, 24));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Effet hover moderne
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

    private void setupButtonActions() {
        addButton.addActionListener(e -> {
            new AjouterArticle().setVisible(true);
            SwingUtilities.invokeLater(() -> chargerArticles());
        });

        editButton.addActionListener(e -> {
            if (selectedArticle != null) {
                ModifierArticle modifFrame = new ModifierArticle(selectedArticle);
                modifFrame.setVisible(true);
                SwingUtilities.invokeLater(() -> chargerArticles());
            }
        });

        deleteButton.addActionListener(e -> {
            if (selectedArticle != null) {
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Êtes-vous sûr de vouloir supprimer l'article '" + selectedArticle.getNom() + "' ?",
                        "Confirmation de suppression",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    new ArticleDAO().supprimerArticle(selectedArticle.getId());
                    showModernMessage("Article supprimé avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    selectedArticle = null;
                    updateButtonStates();
                    chargerArticles();
                }
            }
        });
    }

    private void createModernContent() {
        // Panneau principal pour le contenu
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(LIGHT_GRAY);
        contentWrapper.setBorder(new EmptyBorder(25, 30, 30, 30));

        // Panneau pour les catégories avec gestion de l'ordre
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

    private void chargerArticles() {
        menuPanel.removeAll();
        categoryPanels.clear();

        // Récupérer tous les types d'articles
        TypeArticleDAO typeDAO = new TypeArticleDAO();
        List<TypeArticle> types = typeDAO.listerTousLesTypes();

        // Initialiser l'ordre des catégories si nécessaire
        if (orderedTypes.isEmpty()) {
            orderedTypes.addAll(types);
        } else {
            // Synchroniser avec les nouvelles catégories
            for (TypeArticle type : types) {
                if (!orderedTypes.contains(type)) {
                    orderedTypes.add(type);
                }
            }
        }

        // Remplir la combobox de filtre
        filterCombo.removeAllItems();
        filterCombo.addItem("Toutes les catégories");
        for (TypeArticle type : orderedTypes) {
            filterCombo.addItem(type.getNom());
        }

        // Récupérer tous les articles
        ArticleDAO articleDAO = new ArticleDAO();
        allArticles = articleDAO.listerArticles();

        // Créer les panneaux de catégories dans l'ordre défini
        for (int i = 0; i < orderedTypes.size(); i++) {
            TypeArticle type = orderedTypes.get(i);
            JPanel categoryPanel = createModernCategoryPanel(type, i);
            categoryPanels.put(type.getId(), categoryPanel);
            menuPanel.add(categoryPanel);

            // Ajouter un espacement entre les catégories
            if (i < orderedTypes.size() - 1) {
                menuPanel.add(Box.createVerticalStrut(30));
            }
        }

        // Ajouter les articles à leurs catégories respectives
        for (Article article : allArticles) {
            JPanel articleCard = createModernArticleCard(article);
            JPanel categoryPanel = categoryPanels.get(article.getType().getId());
            if (categoryPanel != null) {
                JPanel gridPanel = (JPanel) ((JPanel) categoryPanel.getComponent(1)).getComponent(0);
                gridPanel.add(articleCard);
            }
        }

        menuPanel.revalidate();
        menuPanel.repaint();
    }

    private JPanel createModernCategoryPanel(TypeArticle type, int index) {
        JPanel categoryPanel = new JPanel(new BorderLayout(0, 20));
        categoryPanel.setBackground(CARD_BACKGROUND);
        categoryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(25, 25, 25, 25)
        ));

        // Header de la catégorie avec titre et boutons de réordonnement
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_BACKGROUND);

        // Titre de la catégorie
        JLabel titleLabel = new JLabel(type.getNom());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(PRIMARY_BLUE);

        // Panneau des boutons de réordonnement
        JPanel orderPanel = createOrderPanel(type, index);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(orderPanel, BorderLayout.EAST);

        // GRILLE POUR LES ARTICLES - 5 COLONNES
        JPanel gridPanel = new JPanel(new GridLayout(0, 5, 15, 15));
        gridPanel.setBackground(CARD_BACKGROUND);

        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(CARD_BACKGROUND);
        contentWrapper.add(gridPanel, BorderLayout.NORTH);

        categoryPanel.add(headerPanel, BorderLayout.NORTH);
        categoryPanel.add(contentWrapper, BorderLayout.CENTER);

        return categoryPanel;
    }

    private JPanel createOrderPanel(TypeArticle type, int index) {
        JPanel orderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        orderPanel.setBackground(CARD_BACKGROUND);

        // Bouton monter
        JButton upButton = createOrderButton("▲", "Monter la catégorie");
        upButton.setEnabled(index > 0);
        upButton.addActionListener(e -> moveCategoryUp(type));

        // Bouton descendre
        JButton downButton = createOrderButton("▼", "Descendre la catégorie");
        downButton.setEnabled(index < orderedTypes.size() - 1);
        downButton.addActionListener(e -> moveCategoryDown(type));

        orderPanel.add(upButton);
        orderPanel.add(downButton);

        return orderPanel;
    }

    private JButton createOrderButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(SECONDARY_BLUE);
        button.setForeground(WHITE);
        button.setBorder(new EmptyBorder(6, 10, 6, 10));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(32, 32));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(ACCENT_BLUE);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(SECONDARY_BLUE);
                }
            }
        });

        return button;
    }

    private void moveCategoryUp(TypeArticle type) {
        int currentIndex = orderedTypes.indexOf(type);
        if (currentIndex > 0) {
            orderedTypes.remove(currentIndex);
            orderedTypes.add(currentIndex - 1, type);
            chargerArticles();
        }
    }

    private void moveCategoryDown(TypeArticle type) {
        int currentIndex = orderedTypes.indexOf(type);
        if (currentIndex < orderedTypes.size() - 1) {
            orderedTypes.remove(currentIndex);
            orderedTypes.add(currentIndex + 1, type);
            chargerArticles();
        }
    }

    private JPanel createModernArticleCard(Article article) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(200, 280));
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

        // Effet de survol moderne
        setupCardHoverEffect(card, article);

        return card;
    }

    private JPanel createImagePanel(Article article) {
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setPreferredSize(new Dimension(200, 130));
        imagePanel.setBackground(new Color(248, 249, 250));

        try {
            ImageIcon icon;
            if (article.getImagePath() != null && !article.getImagePath().isEmpty()) {
                icon = new ImageIcon(ImageService.getImagePath(article.getImagePath()));
            } else {
                icon = new ImageIcon(getClass().getResource("/ressources/no_image.png"));
            }

            Image img = icon.getImage().getScaledInstance(200, 130, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(img));
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imagePanel.add(imageLabel, BorderLayout.CENTER);
        } catch (Exception e) {
            JLabel placeholder = new JLabel("Image non disponible");
            placeholder.setHorizontalAlignment(SwingConstants.CENTER);
            placeholder.setForeground(TEXT_SECONDARY);
            placeholder.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            imagePanel.add(placeholder, BorderLayout.CENTER);
        }

        return imagePanel;
    }

    private JPanel createInfoPanel(Article article) {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(WHITE);
        infoPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Nom de l'article
        JLabel nameLabel = new JLabel(article.getNom());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(TEXT_PRIMARY);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Prix avec style moderne
        JLabel priceLabel = new JLabel(String.format("%.2f €", article.getPrix()));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        priceLabel.setForeground(SUCCESS_GREEN);
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Description
        String desc = article.getDescription();
        if (desc != null && desc.length() > 35) {
            desc = desc.substring(0, 32) + "...";
        }
        JLabel descLabel = new JLabel(desc == null ? "Aucune description" : desc);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(priceLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(descLabel);

        return infoPanel;
    }

    private void setupCardHoverEffect(JPanel card, Article article) {
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (selectedArticle == null || selectedArticle.getId() != article.getId()) {
                    card.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(ACCENT_BLUE, 2),
                            new EmptyBorder(0, 0, 0, 0)
                    ));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (selectedArticle == null || selectedArticle.getId() != article.getId()) {
                    card.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(233, 236, 239), 1),
                            new EmptyBorder(0, 0, 0, 0)
                    ));
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                selectArticle(article, card);
            }
        });
    }

    private void selectArticle(Article article, JPanel card) {
        // Désélectionner l'article précédent
        if (selectedArticle != null) {
            resetAllCardBorders();
        }

        // Sélectionner le nouvel article
        selectedArticle = article;
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_BLUE, 3),
                new EmptyBorder(0, 0, 0, 0)
        ));

        // Mettre à jour l'état des boutons
        updateButtonStates();

        // Afficher les détails
        showModernArticleDetails(article);
    }

    private void updateButtonStates() {
        boolean hasSelection = selectedArticle != null;
        editButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);
    }

    private void resetAllCardBorders() {
        for (JPanel categoryPanel : categoryPanels.values()) {
            JPanel gridPanel = (JPanel) ((JPanel) categoryPanel.getComponent(1)).getComponent(0);
            Component[] articleCards = gridPanel.getComponents();
            for (Component comp : articleCards) {
                if (comp instanceof JPanel) {
                    JPanel cardPanel = (JPanel) comp;
                    cardPanel.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(233, 236, 239), 1),
                            new EmptyBorder(0, 0, 0, 0)
                    ));
                }
            }
        }
    }

    private void showModernArticleDetails(Article article) {
        String message = String.format(
                "<html><div style='width: 300px; font-family: Segoe UI;'>" +
                        "<h2 style='color: #0D47A1; margin-bottom: 10px;'>%s</h2>" +
                        "<p style='font-size: 14px; color: #4CAF50; font-weight: bold; margin-bottom: 5px;'>Prix: %.2f €</p>" +
                        "<p style='font-size: 12px; color: #6C757D; margin-bottom: 10px;'>Catégorie: %s</p>" +
                        "<p style='font-size: 12px; color: #212529; line-height: 1.4;'>%s</p>" +
                        "</div></html>",
                article.getNom(),
                article.getPrix(),
                article.getType().getNom(),
                article.getDescription() != null ? article.getDescription() : "Aucune description disponible"
        );

        showModernMessage(message, "Détails de l'article", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showModernMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    private void filterArticles() {
        String searchText = searchField.getText().toLowerCase();
        String selectedCategory = (String) filterCombo.getSelectedItem();

        for (Map.Entry<Integer, JPanel> entry : categoryPanels.entrySet()) {
            JPanel categoryPanel = entry.getValue();
            JPanel headerPanel = (JPanel) categoryPanel.getComponent(0);
            JLabel titleLabel = (JLabel) headerPanel.getComponent(0);
            String categoryName = titleLabel.getText();

            boolean showCategory = "Toutes les catégories".equals(selectedCategory) ||
                    categoryName.equals(selectedCategory);
            categoryPanel.setVisible(showCategory);

            if (showCategory) {
                JPanel gridPanel = (JPanel) ((JPanel) categoryPanel.getComponent(1)).getComponent(0);
                Component[] articleCards = gridPanel.getComponents();
                for (Component comp : articleCards) {
                    if (comp instanceof JPanel) {
                        JPanel card = (JPanel) comp;
                        Article article = findArticleForCard(card);
                        if (article != null) {
                            boolean matchesSearch = searchText.isEmpty() ||
                                    (article.getNom() != null &&
                                            article.getNom().toLowerCase().contains(searchText)) ||
                                    (article.getDescription() != null &&
                                            article.getDescription().toLowerCase().contains(searchText));

                            card.setVisible(matchesSearch);
                        }
                    }
                }
            }
        }

        menuPanel.revalidate();
        menuPanel.repaint();
    }

    private Article findArticleForCard(JPanel card) {
        for (Article article : allArticles) {
            Component[] components = card.getComponents();
            for (Component comp : components) {
                if (comp instanceof JPanel) {
                    JPanel panel = (JPanel) comp;
                    Component[] subComps = panel.getComponents();
                    for (Component subComp : subComps) {
                        if (subComp instanceof JLabel) {
                            JLabel label = (JLabel) subComp;
                            String labelText = label.getText();
                            if (labelText != null && labelText.equals(article.getNom())) {
                                return article;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ConsulterArticles().setVisible(true);
        });
    }
}