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

public class AjouterArticle extends JFrame {
    // Couleurs cohérentes avec AjouterClient
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

    private JTextField nomField;
    private JTextField prixField;
    private JTextArea descriptionArea;
    private JTextField imagePathField;
    private JComboBox<TypeArticle> typeCombo;
    private JButton ajouterBtn;
    private JButton browseButton;
    private JButton annulerButton;

    public AjouterArticle() {
        setupLookAndFeel();
        initializeModernUI();
        setupValidation();
        remplirTypes();
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
        setTitle("Nouvel Article - Café Shop");
        setSize(520, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // Panel principal avec fond gris clair
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LIGHT_GRAY);
        setContentPane(mainPanel);

        // Header avec titre et icône
        createHeader(mainPanel);

        // Formulaire dans une carte blanche
        createFormCard(mainPanel);

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

        JLabel iconLabel = new JLabel("📦");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.setBorder(new EmptyBorder(0, 15, 0, 0));

        JLabel titleLabel = new JLabel("Ajouter un nouvel article");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(PRIMARY_BLUE);

        JLabel subtitleLabel = new JLabel("Remplissez les informations de l'article");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);

        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);

        titlePanel.add(iconLabel);
        titlePanel.add(textPanel);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
    }

    private void createFormCard(JPanel mainPanel) {
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(LIGHT_GRAY);
        cardPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(CARD_BACKGROUND);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(30, 30, 30, 30)
        ));

        // Champs du formulaire
        formCard.add(createFieldGroup("Nom de l'article *", createStyledTextField(), "Nom du produit"));
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));

        formCard.add(createFieldGroup("Prix (TND) *", createStyledTextField(), "0.00"));
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));

        formCard.add(createFieldGroup("Type d'article *", createStyledComboBox(), null));
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));

        formCard.add(createDescriptionGroup());
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));

        formCard.add(createImageGroup());

        // Note sur les champs obligatoires
        JLabel noteLabel = new JLabel("* Champs obligatoires");
        noteLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        noteLabel.setForeground(TEXT_SECONDARY);
        noteLabel.setBorder(new EmptyBorder(20, 0, 0, 0));
        formCard.add(noteLabel);

        // Création du scroll pane
        JScrollPane scrollPane = new JScrollPane(formCard);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        cardPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(cardPanel, BorderLayout.CENTER);
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

        if (field instanceof JTextField) {
            JTextField textField = (JTextField) field;
            if (placeholder != null) {
                textField.putClientProperty("JTextField.placeholderText", placeholder);
            }
            assignFieldReference(labelText, textField);
        } else if (field instanceof JComboBox) {
            typeCombo = (JComboBox<TypeArticle>) field;
        }

        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        group.add(label);
        group.add(Box.createRigidArea(new Dimension(0, 8)));
        group.add(field);

        return group;
    }

    private JPanel createDescriptionGroup() {
        JPanel group = new JPanel();
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setOpaque(false);
        group.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel("Description");
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(new EmptyBorder(8, 12, 8, 12));

        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        group.add(label);
        group.add(Box.createRigidArea(new Dimension(0, 8)));
        group.add(scrollPane);

        return group;
    }

    private JPanel createImageGroup() {
        JPanel group = new JPanel();
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setOpaque(false);
        group.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel("Image du produit");
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel imagePanel = new JPanel(new BorderLayout(10, 0));
        imagePanel.setOpaque(false);
        imagePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        imagePathField = createStyledTextField();
        imagePathField.putClientProperty("JTextField.placeholderText", "Aucun fichier sélectionné");
        imagePathField.setEditable(false);

        browseButton = createModernButton("Parcourir", ACCENT_BLUE, WHITE);
        browseButton.setPreferredSize(new Dimension(120, 40));
        browseButton.addActionListener(e -> browseImage());

        imagePanel.add(imagePathField, BorderLayout.CENTER);
        imagePanel.add(browseButton, BorderLayout.EAST);

        group.add(label);
        group.add(Box.createRigidArea(new Dimension(0, 8)));
        group.add(imagePanel);

        return group;
    }

    private void assignFieldReference(String labelText, JTextField field) {
        if (labelText.startsWith("Nom")) {
            nomField = field;
        } else if (labelText.startsWith("Prix")) {
            prixField = field;
        }
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

    private JComboBox<TypeArticle> createStyledComboBox() {
        JComboBox<TypeArticle> combo = new JComboBox<>();
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setPreferredSize(new Dimension(200, 40));
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(4, 8, 4, 8)
        ));
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

        ajouterBtn = createModernButton("Ajouter l'article", SUCCESS_GREEN, WHITE);
        ajouterBtn.addActionListener(e -> ajouterArticle());

        buttonsPanel.add(annulerButton);
        buttonsPanel.add(ajouterBtn);

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
        button.setPreferredSize(new Dimension(160, 44));
        return button;
    }

    private void setupValidation() {
        // Validation en temps réel pour le prix
        if (prixField != null) {
            prixField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    validatePrice();
                }
            });
        }
    }

    private void validatePrice() {
        String prix = prixField.getText().trim();
        if (!prix.isEmpty()) {
            try {
                double value = Double.parseDouble(prix);
                if (value <= 0) {
                    prixField.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(DANGER_RED, 2),
                            new EmptyBorder(8, 12, 8, 12)
                    ));
                } else {
                    prixField.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(SUCCESS_GREEN, 2),
                            new EmptyBorder(8, 12, 8, 12)
                    ));
                }
            } catch (NumberFormatException e) {
                prixField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(DANGER_RED, 2),
                        new EmptyBorder(8, 12, 8, 12)
                ));
            }
        } else {
            prixField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                    new EmptyBorder(8, 12, 8, 12)
            ));
        }
    }

    private void remplirTypes() {
        TypeArticleDAO typeDao = new TypeArticleDAO();
        for (TypeArticle type : typeDao.listerTousLesTypes()) {
            typeCombo.addItem(type);
        }
    }

    private void browseImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Sélectionner une image");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Images (*.jpg, *.png, *.gif)", "jpg", "png", "gif");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            imagePathField.setText(selectedFile.getAbsolutePath());

            // Changer le style du champ pour indiquer qu'un fichier est sélectionné
            imagePathField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(SUCCESS_GREEN, 2),
                    new EmptyBorder(8, 12, 8, 12)
            ));
        }
    }

    private void ajouterArticle() {
        String nom = nomField.getText().trim();
        String prixStr = prixField.getText().trim();
        String description = descriptionArea.getText().trim();
        String imagePath = imagePathField.getText().trim();
        TypeArticle type = (TypeArticle) typeCombo.getSelectedItem();

        // Validation des champs obligatoires
        if (nom.isEmpty() || prixStr.isEmpty() || type == null) {
            showErrorDialog("Champs obligatoires manquants",
                    "Veuillez remplir tous les champs obligatoires (Nom, Prix, Type).");
            return;
        }

        // Validation du prix
        try {
            double prix = Double.parseDouble(prixStr);
            if (prix <= 0) {
                showErrorDialog("Prix invalide",
                        "Le prix doit être supérieur à 0.");
                prixField.requestFocus();
                return;
            }

            // Gestion de l'image
            String imageName = null;
            if (!imagePath.isEmpty()) {
                try {
                    File imageFile = new File(imagePath);
                    imageName = ImageService.saveImage(imageFile);
                } catch (IOException e) {
                    showErrorDialog("Erreur image",
                            "Erreur lors de l'enregistrement de l'image : " + e.getMessage());
                    return;
                }
            }
            // In your calling code
            Article article = new Article(0, nom, description, prix, type, imageName);
            new ArticleDAO().ajouterArticle(article); // just call the method without expecting a return

            // You'll need to handle success/failure differently, perhaps with exceptions
            showSuccessDialog();
            dispose();

        } catch (NumberFormatException e) {
            showErrorDialog("Prix invalide",
                    "Veuillez entrer un prix valide (ex: 12.50).");
            prixField.requestFocus();
        } catch (Exception e) {
            showErrorDialog("Erreur inattendue",
                    "Une erreur inattendue s'est produite : " + e.getMessage());
        }
    }

    private void showErrorDialog(String titre, String message) {
        JOptionPane.showMessageDialog(this,
                message,
                titre,
                JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccessDialog() {
        JOptionPane.showMessageDialog(this,
                "L'article a été ajouté avec succès !",
                "Succès",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new AjouterArticle().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}