package ui.Interfaces_Gerant.Gestion_Clients;

import dao.ClientDAO;
import model.Client;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ModifierClient extends JFrame {
    // Couleurs cohérentes avec AjouterClient
    private static final Color PRIMARY_BLUE = new Color(13, 71, 161);
    private static final Color SECONDARY_BLUE = new Color(25, 118, 210);
    private static final Color ACCENT_BLUE = new Color(33, 150, 243);
    private static final Color SUCCESS_GREEN = new Color(76, 175, 80);
    private static final Color DANGER_RED = new Color(244, 67, 54);
    private static final Color WHITE = Color.WHITE;
    private static final Color LIGHT_GRAY = new Color(248, 249, 250);
    private static final Color CARD_BACKGROUND = new Color(255, 255, 255);
    private static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    private static final Color TEXT_SECONDARY = new Color(108, 117, 125);

    private JTextField emailSearchField;
    private JTextField nomField;
    private JTextField prenomField;
    private JTextField dateNaissanceField;
    private JTextField adresseField;
    private JTextField telField;
    private JButton chercherButton;
    private JButton modifierButton;
    private JButton annulerButton;
    private JPanel formPanel;
    private ClientDAO clientDAO;
    private Client clientActuel;

    public ModifierClient() {
        setupLookAndFeel();
        clientDAO = new ClientDAO();
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
        setTitle("Modifier Client - Café Shop");
        setSize(550, 750);
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

        JLabel iconLabel = new JLabel("✏️");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.setBorder(new EmptyBorder(0, 15, 0, 0));

        JLabel titleLabel = new JLabel("Modifier un client");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(PRIMARY_BLUE);

        JLabel subtitleLabel = new JLabel("Recherchez et modifiez les informations du client");
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

        // Carte de recherche
        allCardsPanel.add(createSearchCard());
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

    private JPanel createSearchCard() {
        JPanel searchCard = new JPanel();
        searchCard.setLayout(new BoxLayout(searchCard, BoxLayout.Y_AXIS));
        searchCard.setBackground(CARD_BACKGROUND);
        searchCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(25, 25, 25, 25)
        ));

        // Titre de la section
        JLabel searchTitle = new JLabel("🔍 Rechercher un client");
        searchTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        searchTitle.setForeground(PRIMARY_BLUE);
        searchTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel searchSubtitle = new JLabel("Entrez l'adresse email du client à modifier");
        searchSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchSubtitle.setForeground(TEXT_SECONDARY);
        searchSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchSubtitle.setBorder(new EmptyBorder(5, 0, 20, 0));

        // Champ de recherche
        emailSearchField = createStyledTextField();
        JPanel searchFieldPanel = createFieldGroup("Email du client *", emailSearchField, "email@exemple.com");


        // Bouton rechercher
        chercherButton = createModernButton("Rechercher", ACCENT_BLUE, WHITE);
        chercherButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        chercherButton.addActionListener(e -> chercherClient());

        searchCard.add(searchTitle);
        searchCard.add(searchSubtitle);
        searchCard.add(searchFieldPanel);
        searchCard.add(Box.createRigidArea(new Dimension(0, 20)));
        searchCard.add(chercherButton);

        return searchCard;
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
        JLabel formTitle = new JLabel("📝 Informations du client");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formTitle.setForeground(PRIMARY_BLUE);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel formSubtitle = new JLabel("Modifiez les informations du client trouvé");
        formSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formSubtitle.setForeground(TEXT_SECONDARY);
        formSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formSubtitle.setBorder(new EmptyBorder(5, 0, 20, 0));

        // Champs du formulaire
        formCard.add(formTitle);
        formCard.add(formSubtitle);

        formCard.add(createDoubleFieldGroup(
                "Nom *", createStyledTextField(),
                "Prénom *", createStyledTextField()
        ));
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));

        formCard.add(createFieldGroup("Date de naissance", createStyledTextField(), "YYYY-MM-DD (ex: 1990-12-25)"));
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));

        formCard.add(createFieldGroup("Adresse", createStyledTextField(), "Adresse complète"));
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));

        formCard.add(createFieldGroup("Téléphone", createStyledTextField(), "+216 XX XXX XXX"));

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

        if (field instanceof JTextField) {
            JTextField textField = (JTextField) field;
            textField.putClientProperty("JTextField.placeholderText", placeholder);
            // Stocker la référence selon le label
            assignFieldReference(labelText, textField);
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

        // Assigner les références
        if (field1 instanceof JTextField) {
            nomField = (JTextField) field1;
        }
        if (field2 instanceof JTextField) {
            prenomField = (JTextField) field2;
        }

        fieldsPanel.add(field1);
        fieldsPanel.add(field2);

        group.add(labelsPanel);
        group.add(Box.createRigidArea(new Dimension(0, 8)));
        group.add(fieldsPanel);

        return group;
    }

    private void assignFieldReference(String labelText, JTextField field) {
        if (labelText.startsWith("Date")) {
            dateNaissanceField = field;
        } else if (labelText.startsWith("Adresse")) {
            adresseField = field;
        } else if (labelText.startsWith("Téléphone")) {
            telField = field;
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
        modifierButton.addActionListener(e -> modifierClient());

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
        button.setPreferredSize(new Dimension(160, 44));
        return button;
    }

    private void setupValidation() {
        // Validation pour la date
        if (dateNaissanceField != null) {
            dateNaissanceField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    validateDate();
                }
            });
        }
    }

    private void validateDate() {
        String date = dateNaissanceField.getText().trim();
        if (!date.isEmpty()) {
            try {
                LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                dateNaissanceField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(SUCCESS_GREEN, 2),
                        new EmptyBorder(8, 12, 8, 12)
                ));
            } catch (DateTimeParseException e) {
                dateNaissanceField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(DANGER_RED, 2),
                        new EmptyBorder(8, 12, 8, 12)
                ));
            }
        }
    }

    private void chercherClient() {
        String email = emailSearchField.getText().trim();

        if (email.isEmpty()) {
            showErrorDialog("Email requis", "Veuillez saisir l'adresse email du client à rechercher.");
            return;
        }

        clientActuel = clientDAO.getClientByEmail(email);

        if (clientActuel != null) {
            // Remplir les champs avec les données du client
            nomField.setText(clientActuel.getNom());
            prenomField.setText(clientActuel.getPrenom());
            dateNaissanceField.setText(clientActuel.getDateNaissance() != null ? clientActuel.getDateNaissance() : "");
            adresseField.setText(clientActuel.getAdresse() != null ? clientActuel.getAdresse() : "");
            telField.setText(clientActuel.getTelephone() != null ? clientActuel.getTelephone() : "");

            setFormEnabled(true);
            showSuccessDialog("Client trouvé !", "Le client a été trouvé avec succès. Vous pouvez maintenant modifier ses informations.");
        } else {
            showErrorDialog("Client introuvable", "Aucun client trouvé avec cette adresse email.");
            clearFields();
            setFormEnabled(false);
        }
    }

    private void modifierClient() {
        if (clientActuel == null) {
            showErrorDialog("Aucun client sélectionné", "Veuillez d'abord rechercher un client à modifier.");
            return;
        }

        // Récupération des données
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String dateNaissance = dateNaissanceField.getText().trim();
        String adresse = adresseField.getText().trim();
        String telephone = telField.getText().trim();

        // Validation des champs obligatoires
        if (nom.isEmpty() || prenom.isEmpty()) {
            showErrorDialog("Champs obligatoires", "Veuillez remplir les champs Nom et Prénom.");
            return;
        }

        // Validation date si renseignée
        if (!dateNaissance.isEmpty()) {
            try {
                LocalDate.parse(dateNaissance, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException e) {
                showErrorDialog("Date invalide", "Format de date invalide. Utilisez le format YYYY-MM-DD (ex: 1990-12-25).");
                dateNaissanceField.requestFocus();
                return;
            }
        }

        // Mettre à jour le client
        clientActuel.setNom(nom);
        clientActuel.setPrenom(prenom);
        clientActuel.setDateNaissance(dateNaissance.isEmpty() ? null : dateNaissance);
        clientActuel.setAdresse(adresse.isEmpty() ? null : adresse);
        clientActuel.setTelephone(telephone.isEmpty() ? null : telephone);

        boolean success = clientDAO.modifierClient(clientActuel);

        if (success) {
            showSuccessDialog("Modifications enregistrées", "Les informations du client ont été mises à jour avec succès !");
            dispose();
        } else {
            showErrorDialog("Erreur système", "Une erreur est survenue lors de la modification du client. Veuillez réessayer.");
        }
    }

    private void setFormEnabled(boolean enabled) {
        nomField.setEnabled(enabled);
        prenomField.setEnabled(enabled);
        dateNaissanceField.setEnabled(enabled);
        adresseField.setEnabled(enabled);
        telField.setEnabled(enabled);
        modifierButton.setEnabled(enabled);

        Color bgColor = enabled ? WHITE : LIGHT_GRAY;
        nomField.setBackground(bgColor);
        prenomField.setBackground(bgColor);
        dateNaissanceField.setBackground(bgColor);
        adresseField.setBackground(bgColor);
        telField.setBackground(bgColor);
    }

    private void clearFields() {
        nomField.setText("");
        prenomField.setText("");
        dateNaissanceField.setText("");
        adresseField.setText("");
        telField.setText("");
    }

    public void preremplirEmail(String email) {
        emailSearchField.setText(email);
        chercherClient();
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
                new ModifierClient().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}