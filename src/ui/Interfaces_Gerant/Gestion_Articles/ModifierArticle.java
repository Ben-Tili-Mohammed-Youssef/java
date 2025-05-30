package ui.Interfaces_Gerant.Gestion_Articles;

import dao.ArticleDAO;
import dao.ImageService;
import dao.TypeArticleDAO;
import model.Article;
import model.TypeArticle;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.IOException;

public class ModifierArticle extends JFrame {
    // Couleurs cohérentes avec ModifierClient
    private static final Color PRIMARY_BLUE = new Color(13, 71, 161);
    private static final Color SECONDARY_BLUE = new Color(25, 118, 210);
    private static final Color ACCENT_BLUE = new Color(33, 150, 243);
    private static final Color SUCCESS_GREEN = new Color(76, 175, 80);
    private static final Color DANGER_RED = new Color(244, 67, 54);
    private static final Color WARNING_ORANGE = new Color(255, 152, 0);
    private static final Color WHITE = Color.WHITE;
    private static final Color LIGHT_GRAY = new Color(248, 249, 250);
    private static final Color CARD_BACKGROUND = new Color(255, 255, 255);
    private static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    private static final Color TEXT_SECONDARY = new Color(108, 117, 125);

    private JComboBox<Article> articleCombo;
    private JTextField nomField;
    private JTextField prixField;
    private JTextArea descriptionArea;
    private JComboBox<TypeArticle> typeCombo;
    private JTextField imagePathField;
    private JButton chercherButton;
    private JButton browseButton;
    private JButton modifierButton;
    private JButton annulerButton;
    private JPanel formPanel;
    private ArticleDAO articleDAO;
    private TypeArticleDAO typeArticleDAO;
    private Article articleActuel;
    private boolean articlePreselectionne = false;

    /**
     * Constructeur utilisé quand un article spécifique est déjà sélectionné
     */
    public ModifierArticle(Article article) {
        this();
        articlePreselectionne = true;
        articleActuel = article;

        // Sélectionne l'article dans la combobox
        for (int i = 0; i < articleCombo.getItemCount(); i++) {
            if (((Article)articleCombo.getItemAt(i)).getId() == article.getId()) {
                articleCombo.setSelectedIndex(i);
                break;
            }
        }

        // Désactiver la sélection d'article puisqu'on modifie un article spécifique
        articleCombo.setEnabled(false);
        chercherButton.setEnabled(false);

        // Préremplit les champs avec les données de l'article
        chargerArticleSelectionne();
        setFormEnabled(true);
    }

    /**
     * Constructeur par défaut
     */
    public ModifierArticle() {
        setupLookAndFeel();
        articleDAO = new ArticleDAO();
        typeArticleDAO = new TypeArticleDAO();
        initializeModernUI();
        setupValidation();
        setFormEnabled(false);
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
        setTitle("Modifier Article - Café Shop");
        setSize(600, 850);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // Panel principal avec fond gris clair
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LIGHT_GRAY);
        setContentPane(mainPanel);

        // Header avec titre et icône
        createHeader(mainPanel);

        // Contenu principal avec scroll
        createMainContent(mainPanel);

        // Footer avec boutons
        createFooter(mainPanel);
    }

    private void createHeader(JPanel mainPanel) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                new EmptyBorder(20, 25, 20, 25)
        ));

        // Icône et titre
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);

        JLabel iconLabel = new JLabel("📝");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.setBorder(new EmptyBorder(0, 15, 0, 0));

        JLabel titleLabel = new JLabel("Modifier un article");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(PRIMARY_BLUE);

        JLabel subtitleLabel = new JLabel("Sélectionnez et modifiez les informations de l'article");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);

        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);

        titlePanel.add(iconLabel);
        titlePanel.add(textPanel);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
    }

    private void createMainContent(JPanel mainPanel) {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(LIGHT_GRAY);
        contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel allCardsPanel = new JPanel();
        allCardsPanel.setLayout(new BoxLayout(allCardsPanel, BoxLayout.Y_AXIS));
        allCardsPanel.setBackground(LIGHT_GRAY);

        // Carte de sélection d'article
        allCardsPanel.add(createSelectionCard());
        allCardsPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Carte du formulaire
        formPanel = createFormCard();
        allCardsPanel.add(formPanel);

        // Création du scroll pane
        JScrollPane scrollPane = new JScrollPane(allCardsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createSelectionCard() {
        JPanel selectionCard = new JPanel();
        selectionCard.setLayout(new BoxLayout(selectionCard, BoxLayout.Y_AXIS));
        selectionCard.setBackground(CARD_BACKGROUND);
        selectionCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(25, 25, 25, 25)
        ));

        // Titre de la section
        JLabel selectionTitle = new JLabel("🔍 Sélectionner un article");
        selectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        selectionTitle.setForeground(PRIMARY_BLUE);
        selectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel selectionSubtitle = new JLabel("Choisissez l'article à modifier dans la liste");
        selectionSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        selectionSubtitle.setForeground(TEXT_SECONDARY);
        selectionSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        selectionSubtitle.setBorder(new EmptyBorder(5, 0, 20, 0));

        // ComboBox d'articles
        articleCombo = createStyledComboBox();
        remplirArticles();
        JPanel articleFieldPanel = createFieldGroup("Article à modifier *", articleCombo, "");

        // Bouton chercher
        chercherButton = createModernButton("Charger l'article", ACCENT_BLUE, WHITE);
        chercherButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        chercherButton.addActionListener(e -> chargerArticleSelectionne());

        selectionCard.add(selectionTitle);
        selectionCard.add(selectionSubtitle);
        selectionCard.add(articleFieldPanel);
        selectionCard.add(Box.createRigidArea(new Dimension(0, 20)));
        selectionCard.add(chercherButton);

        return selectionCard;
    }

    private JPanel createFormCard() {
        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(CARD_BACKGROUND);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(25, 25, 25, 25)
        ));

        // Titre de la section
        JLabel formTitle = new JLabel("📦 Informations de l'article");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formTitle.setForeground(PRIMARY_BLUE);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel formSubtitle = new JLabel("Modifiez les informations de l'article sélectionné");
        formSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formSubtitle.setForeground(TEXT_SECONDARY);
        formSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formSubtitle.setBorder(new EmptyBorder(5, 0, 20, 0));

        // Champs du formulaire
        formCard.add(formTitle);
        formCard.add(formSubtitle);

        // Nom et Prix sur la même ligne
        nomField = createStyledTextField();
        prixField = createStyledTextField();
        formCard.add(createDoubleFieldGroup(
                "Nom de l'article *", nomField,
                "Prix (DT) *", prixField
        ));
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));

        // Description
        descriptionArea = createStyledTextArea();
        formCard.add(createFieldGroup("Description", new JScrollPane(descriptionArea), ""));
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));

        // Type d'article
        typeCombo = createStyledComboBox();
        remplirTypes();
        formCard.add(createFieldGroup("Type d'article *", typeCombo, ""));
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));

        // Image avec bouton parcourir
        imagePathField = createStyledTextField();
        browseButton = createModernButton("Parcourir", new Color(108, 117, 125), WHITE);
        browseButton.addActionListener(e -> browseImage());
        formCard.add(createImageFieldGroup("Image de l'article", imagePathField, browseButton));

        // Note sur les champs obligatoires
        JLabel noteLabel = new JLabel("* Champs obligatoires");
        noteLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        noteLabel.setForeground(TEXT_SECONDARY);
        noteLabel.setBorder(new EmptyBorder(20, 0, 0, 0));
        noteLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(noteLabel);

        return formCard;
    }

    private JPanel createFieldGroup(String labelText, JComponent field, String placeholder) {
        JPanel group = new JPanel();
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setOpaque(false);
        group.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (field instanceof JTextField && !placeholder.isEmpty()) {
            JTextField textField = (JTextField) field;
            textField.putClientProperty("JTextField.placeholderText", placeholder);
        }

        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        group.add(label);
        group.add(Box.createRigidArea(new Dimension(0, 8)));
        group.add(field);

        return group;
    }

    private JPanel createDoubleFieldGroup(String label1, JComponent field1, String label2, JComponent field2) {
        JPanel group = new JPanel();
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setOpaque(false);
        group.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel labelsPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        labelsPanel.setOpaque(false);

        JLabel labelLeft = new JLabel(label1);
        labelLeft.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelLeft.setForeground(TEXT_PRIMARY);

        JLabel labelRight = new JLabel(label2);
        labelRight.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelRight.setForeground(TEXT_PRIMARY);

        labelsPanel.add(labelLeft);
        labelsPanel.add(labelRight);

        JPanel fieldsPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        fieldsPanel.setOpaque(false);

        fieldsPanel.add(field1);
        fieldsPanel.add(field2);

        group.add(labelsPanel);
        group.add(Box.createRigidArea(new Dimension(0, 8)));
        group.add(fieldsPanel);

        return group;
    }

    private JPanel createImageFieldGroup(String labelText, JTextField field, JButton button) {
        JPanel group = new JPanel();
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setOpaque(false);
        group.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel fieldPanel = new JPanel(new BorderLayout(10, 0));
        fieldPanel.setOpaque(false);
        fieldPanel.add(field, BorderLayout.CENTER);
        fieldPanel.add(button, BorderLayout.EAST);

        group.add(label);
        group.add(Box.createRigidArea(new Dimension(0, 8)));
        group.add(fieldPanel);

        return group;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(200, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    private JTextArea createStyledTextArea() {
        JTextArea area = new JTextArea(4, 20);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        return area;
    }

    private JComboBox createStyledComboBox() {
        JComboBox combo = new JComboBox();
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setPreferredSize(new Dimension(200, 40));
        return combo;
    }

    private void createFooter(JPanel mainPanel) {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(WHITE);
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
                new EmptyBorder(20, 25, 20, 25)
        ));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonsPanel.setOpaque(false);

        annulerButton = createModernButton("Annuler", new Color(108, 117, 125), WHITE);
        annulerButton.addActionListener(e -> dispose());

        modifierButton = createModernButton("Enregistrer les modifications", SUCCESS_GREEN, WHITE);
        modifierButton.addActionListener(e -> modifierArticle());

        buttonsPanel.add(annulerButton);
        buttonsPanel.add(modifierButton);

        footerPanel.add(buttonsPanel, BorderLayout.EAST);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
    }

    private JButton createModernButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(12, 24, 12, 24));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 44));
        return button;
    }

    private void setupValidation() {
        // Validation pour le prix
        prixField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validatePrice();
            }
        });
    }

    private void validatePrice() {
        String price = prixField.getText().trim();
        if (!price.isEmpty()) {
            try {
                double p = Double.parseDouble(price);
                if (p > 0) {
                    prixField.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(SUCCESS_GREEN, 2),
                            new EmptyBorder(8, 12, 8, 12)
                    ));
                } else {
                    prixField.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(DANGER_RED, 2),
                            new EmptyBorder(8, 12, 8, 12)
                    ));
                }
            } catch (NumberFormatException e) {
                prixField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(DANGER_RED, 2),
                        new EmptyBorder(8, 12, 8, 12)
                ));
            }
        }
    }

    private void remplirArticles() {
        for (Article a : articleDAO.listerArticles()) {
            articleCombo.addItem(a);
        }
    }

    private void remplirTypes() {
        for (TypeArticle t : typeArticleDAO.listerTousLesTypes()) {
            typeCombo.addItem(t);
        }
    }

    private void browseImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Sélectionner une image");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // Filtrer les fichiers par extension
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Images (*.jpg, *.png, *.gif)", "jpg", "png", "gif");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            imagePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void chargerArticleSelectionne() {
        Article a = (Article) articleCombo.getSelectedItem();
        if (a != null) {
            articleActuel = a;
            nomField.setText(a.getNom());
            prixField.setText(String.valueOf(a.getPrix()));
            descriptionArea.setText(a.getDescription());
            imagePathField.setText(a.getImagePath());

            // Sélectionner le type d'article dans la combobox
            for (int i = 0; i < typeCombo.getItemCount(); i++) {
                TypeArticle type = typeCombo.getItemAt(i);
                if (type.getId() == a.getType().getId()) {
                    typeCombo.setSelectedIndex(i);
                    break;
                }
            }

            setFormEnabled(true);
            showSuccessDialog("Article chargé !", "L'article a été chargé avec succès. Vous pouvez maintenant modifier ses informations.");
        } else {
            showErrorDialog("Aucun article sélectionné", "Veuillez sélectionner un article dans la liste.");
            setFormEnabled(false);
        }
    }

    private void modifierArticle() {
        if (articleActuel == null) {
            showErrorDialog("Aucun article sélectionné", "Veuillez d'abord sélectionner un article à modifier.");
            return;
        }

        String nom = nomField.getText().trim();
        String prixStr = prixField.getText().trim();
        String description = descriptionArea.getText().trim();
        String imagePath = imagePathField.getText().trim();
        TypeArticle type = (TypeArticle) typeCombo.getSelectedItem();

        if (nom.isEmpty() || prixStr.isEmpty() || type == null) {
            showErrorDialog("Champs obligatoires", "Veuillez remplir tous les champs obligatoires (nom, prix, type).");
            return;
        }

        try {
            double prix = Double.parseDouble(prixStr);
            if (prix <= 0) {
                showErrorDialog("Prix invalide", "Le prix doit être supérieur à 0.");
                return;
            }

            // Gestion de l'image
            String imageName = articleActuel.getImagePath(); // Conserve l'ancienne image par défaut
            if (!imagePathField.getText().isEmpty() && !imagePathField.getText().equals(articleActuel.getImagePath())) {
                File imageFile = new File(imagePathField.getText());
                if (imageFile.exists()) {
                    imageName = ImageService.saveImage(imageFile);
                }
            }

            Article updated = new Article(articleActuel.getId(), nom, description, prix, type, imageName);
            articleDAO.modifierArticle(updated);

            showSuccessDialog("Modifications enregistrées", "L'article a été modifié avec succès !");
            dispose();

        } catch (NumberFormatException e) {
            showErrorDialog("Prix invalide", "Prix invalide. Veuillez entrer un nombre valide (ex: 12.50).");
        } catch (IOException e) {
            showErrorDialog("Erreur image", "Erreur lors de l'enregistrement de l'image.");
        }
    }

    private void setFormEnabled(boolean enabled) {
        nomField.setEnabled(enabled);
        prixField.setEnabled(enabled);
        descriptionArea.setEnabled(enabled);
        typeCombo.setEnabled(enabled);
        imagePathField.setEnabled(enabled);
        browseButton.setEnabled(enabled);
        modifierButton.setEnabled(enabled);

        Color bgColor = enabled ? WHITE : LIGHT_GRAY;
        nomField.setBackground(bgColor);
        prixField.setBackground(bgColor);
        descriptionArea.setBackground(bgColor);
        typeCombo.setBackground(bgColor);
        imagePathField.setBackground(bgColor);
    }

    private void clearFields() {
        nomField.setText("");
        prixField.setText("");
        descriptionArea.setText("");
        imagePathField.setText("");
        typeCombo.setSelectedIndex(-1);
    }

    private void showErrorDialog(String titre, String message) {
        JOptionPane.showMessageDialog(this, message, titre, JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccessDialog(String titre, String message) {
        JOptionPane.showMessageDialog(this, message, titre, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new ModifierArticle().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}