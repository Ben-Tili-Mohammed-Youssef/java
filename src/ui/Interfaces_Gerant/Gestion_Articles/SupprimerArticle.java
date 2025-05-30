package ui.Interfaces_Gerant.Gestion_Articles;

import dao.ArticleDAO;
import dao.ImageService;
import model.Article;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class SupprimerArticle extends JFrame {
    private JComboBox<Article> articleCombo;
    private JButton supprimerBtn;
    private JButton annulerBtn;
    private JPanel previewPanel;
    private JLabel imageLabel;
    private JLabel nomLabel;
    private JLabel prixLabel;
    private JLabel descriptionLabel;
    private JLabel categorieLabel;

    // Couleurs modernes cohérentes avec ConsulterClients
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

    public SupprimerArticle() {
        setupLookAndFeel();
        initializeComponents();
        setupUI();
        remplirArticles();
    }

    // Constructeur pour pré-remplir avec un article spécifique
    public SupprimerArticle(Article article) {
        this();
        if (article != null) {
            articleCombo.setSelectedItem(article);
            updatePreview();
        }
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

    private void initializeComponents() {
        setTitle("Supprimer un Article - Café Shop");
        setSize(700, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Essayer de charger l'icône
        try {
            setIconImage(new ImageIcon(getClass().getResource("/ressources/cafe_logo.png")).getImage());
        } catch (Exception e) {
            // Ignorer si l'icône n'existe pas
        }
    }

    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LIGHT_GRAY);
        setContentPane(mainPanel);

        // Header moderne
        JPanel headerPanel = createModernHeader();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Panel central avec carte
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(LIGHT_GRAY);
        centerPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Carte principale
        JPanel mainCard = createMainCard();
        centerPanel.add(mainCard, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Panel des boutons
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createModernHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                new EmptyBorder(20, 30, 20, 30)
        ));
        headerPanel.setPreferredSize(new Dimension(0, 90));

        // Titre avec icône
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);

        JLabel iconLabel = new JLabel("🗑️");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.setBorder(new EmptyBorder(0, 15, 0, 0));

        JLabel titleLabel = new JLabel("Supprimer un Article");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(DANGER_RED);

        JLabel subtitleLabel = new JLabel("Sélectionnez l'article à supprimer définitivement");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);

        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);

        titlePanel.add(iconLabel);
        titlePanel.add(textPanel);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        return headerPanel;
    }

    private JPanel createMainCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(25, 25, 25, 25)
        ));

        // Section de sélection
        JPanel selectionPanel = createSelectionPanel();
        card.add(selectionPanel, BorderLayout.NORTH);

        // Section de prévisualisation
        previewPanel = createPreviewPanel();
        card.add(previewPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Titre de section
        JLabel sectionTitle = new JLabel("📋 Sélection de l'article");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sectionTitle.setForeground(TEXT_PRIMARY);

        // ComboBox moderne
        articleCombo = new JComboBox<>();
        articleCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        articleCombo.setPreferredSize(new Dimension(0, 40));
        articleCombo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        articleCombo.addActionListener(e -> updatePreview());

        // Renderer personnalisé pour la ComboBox
        articleCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Article) {
                    Article article = (Article) value;
                    setText(article.getNom() + " - " + String.format("%.2f €", article.getPrix()));
                }
                return this;
            }
        });

        JPanel comboPanel = new JPanel(new BorderLayout());
        comboPanel.setOpaque(false);
        comboPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        comboPanel.add(articleCombo, BorderLayout.CENTER);

        panel.add(sectionTitle, BorderLayout.NORTH);
        panel.add(comboPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPreviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                        "👁️ Aperçu de l'article sélectionné",
                        0, 0,
                        new Font("Segoe UI", Font.BOLD, 16),
                        TEXT_PRIMARY
                ),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Panel pour l'image
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setPreferredSize(new Dimension(200, 150));
        imagePanel.setBackground(new Color(248, 249, 250));
        imagePanel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));

        imageLabel = new JLabel("Aucun article sélectionné");
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        imageLabel.setForeground(TEXT_SECONDARY);
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        // Panel pour les informations
        JPanel infoPanel = createInfoPanel();

        panel.add(imagePanel, BorderLayout.WEST);
        panel.add(infoPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 20, 0, 0));

        // Labels d'information
        nomLabel = createInfoLabel("Nom: ", "Aucun article sélectionné");
        prixLabel = createInfoLabel("Prix: ", "-");
        categorieLabel = createInfoLabel("Catégorie: ", "-");
        descriptionLabel = createInfoLabel("Description: ", "-");

        panel.add(nomLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(prixLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(categorieLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(descriptionLabel);

        return panel;
    }

    private JLabel createInfoLabel(String prefix, String value) {
        JLabel label = new JLabel(String.format("<html><b style='color: #212529;'>%s</b><span style='color: #6C757D;'>%s</span></html>", prefix, value));
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        panel.setBackground(LIGHT_GRAY);
        panel.setBorder(new EmptyBorder(0, 25, 10, 25));

        annulerBtn = createModernButton("❌ Annuler", TEXT_SECONDARY);
        supprimerBtn = createModernButton("🗑️ Supprimer", DANGER_RED);
        supprimerBtn.setEnabled(false);

        // Actions des boutons
        annulerBtn.addActionListener(e -> dispose());
        supprimerBtn.addActionListener(e -> supprimerArticle());

        panel.add(annulerBtn);
        panel.add(supprimerBtn);

        return panel;
    }

    private JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(130, 40));
        button.setBackground(bgColor);
        button.setForeground(WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(8, 16, 8, 16));

        // Effet hover moderne
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(bgColor.brighter());
                    button.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(bgColor.darker(), 1),
                            new EmptyBorder(7, 15, 7, 15)
                    ));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(bgColor);
                    button.setBorder(new EmptyBorder(8, 16, 8, 16));
                }
            }
        });

        return button;
    }

    private void remplirArticles() {
        articleCombo.removeAllItems();
        ArticleDAO articleDAO = new ArticleDAO();
        List<Article> articles = articleDAO.listerArticles();

        for (Article article : articles) {
            articleCombo.addItem(article);
        }

        if (articles.isEmpty()) {
            showModernMessage("ℹ️ Information",
                    "Aucun article disponible à supprimer.",
                    JOptionPane.INFORMATION_MESSAGE);
            supprimerBtn.setEnabled(false);
        } else {
            updatePreview();
        }
    }

    private void updatePreview() {
        Article selectedArticle = (Article) articleCombo.getSelectedItem();

        if (selectedArticle != null) {
            // Mettre à jour l'image
            try {
                ImageIcon icon;
                if (selectedArticle.getImagePath() != null && !selectedArticle.getImagePath().isEmpty()) {
                    icon = new ImageIcon(ImageService.getImagePath(selectedArticle.getImagePath()));
                } else {
                    icon = new ImageIcon(getClass().getResource("/ressources/no_image.png"));
                }

                Image img = icon.getImage().getScaledInstance(180, 120, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(img));
                imageLabel.setText("");
            } catch (Exception e) {
                imageLabel.setIcon(null);
                imageLabel.setText("Image non disponible");
            }

            // Mettre à jour les informations
            nomLabel.setText(String.format("<html><b style='color: #212529;'>Nom: </b><span style='color: #6C757D;'>%s</span></html>",
                    selectedArticle.getNom()));
            prixLabel.setText(String.format("<html><b style='color: #212529;'>Prix: </b><span style='color: #4CAF50; font-weight: bold;'>%.2f €</span></html>",
                    selectedArticle.getPrix()));
            categorieLabel.setText(String.format("<html><b style='color: #212529;'>Catégorie: </b><span style='color: #6C757D;'>%s</span></html>",
                    selectedArticle.getType().getNom()));

            String description = selectedArticle.getDescription();
            if (description == null || description.trim().isEmpty()) {
                description = "Aucune description disponible";
            }
            descriptionLabel.setText(String.format("<html><b style='color: #212529;'>Description: </b><span style='color: #6C757D;'>%s</span></html>",
                    description));

            supprimerBtn.setEnabled(true);
        } else {
            // Réinitialiser l'aperçu
            imageLabel.setIcon(null);
            imageLabel.setText("Aucun article sélectionné");
            nomLabel.setText("<html><b style='color: #212529;'>Nom: </b><span style='color: #6C757D;'>Aucun article sélectionné</span></html>");
            prixLabel.setText("<html><b style='color: #212529;'>Prix: </b><span style='color: #6C757D;'>-</span></html>");
            categorieLabel.setText("<html><b style='color: #212529;'>Catégorie: </b><span style='color: #6C757D;'>-</span></html>");
            descriptionLabel.setText("<html><b style='color: #212529;'>Description: </b><span style='color: #6C757D;'>-</span></html>");
            supprimerBtn.setEnabled(false);
        }

        previewPanel.revalidate();
        previewPanel.repaint();
    }

    private void supprimerArticle() {
        Article selectedArticle = (Article) articleCombo.getSelectedItem();
        if (selectedArticle == null) {
            showModernMessage("⚠️ Aucune sélection",
                    "Veuillez sélectionner un article à supprimer.",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Dialogue de confirmation moderne
        String message = String.format(
                "<html><div style='width: 300px; font-family: Segoe UI;'>" +
                        "<h3 style='color: #D32F2F; margin-bottom: 10px;'>⚠️ Confirmation de suppression</h3>" +
                        "<p style='margin-bottom: 10px;'>Êtes-vous sûr de vouloir supprimer cet article ?</p>" +
                        "<div style='background-color: #FFEBEE; padding: 10px; border-radius: 5px; margin: 10px 0;'>" +
                        "<p style='margin: 0; font-weight: bold; color: #C62828;'>%s</p>" +
                        "<p style='margin: 5px 0 0 0; color: #D32F2F;'>Prix: %.2f €</p>" +
                        "</div>" +
                        "<p style='color: #D32F2F; font-weight: bold;'>⚠️ Cette action est irréversible !</p>" +
                        "</div></html>",
                selectedArticle.getNom(),
                selectedArticle.getPrix()
        );

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                message,
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                ArticleDAO articleDAO = new ArticleDAO();
                articleDAO.supprimerArticle(selectedArticle.getId());

                showModernMessage("✅ Succès",
                        "L'article '" + selectedArticle.getNom() + "' a été supprimé avec succès !",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (Exception e) {
                showModernMessage("❌ Erreur",
                        "Une erreur s'est produite lors de la suppression : " + e.getMessage(),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showModernMessage(String title, String message, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    // Méthode pour pré-remplir avec un article spécifique (pour l'appel depuis ConsulterArticles)
    public void preremplirArticle(Article article) {
        if (article != null) {
            articleCombo.setSelectedItem(article);
            updatePreview();
        }
    }

    // Pour les tests
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SupprimerArticle().setVisible(true);
        });
    }
}