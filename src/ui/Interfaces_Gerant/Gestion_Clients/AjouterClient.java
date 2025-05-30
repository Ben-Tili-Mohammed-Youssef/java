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

public class AjouterClient extends JFrame {
    // Couleurs cohérentes avec AccueilGerant
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

    private JTextField emailField;
    private JPasswordField mdpField;
    private JTextField nomField;
    private JTextField prenomField;
    private JTextField dateNaissanceField;
    private JTextField adresseField;
    private JTextField telField;
    private JButton creerButton;
    private JButton annulerButton;

    public AjouterClient() {
        setupLookAndFeel();
        initializeModernUI();
        setupValidation();
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
        setTitle("Nouveau Client - Café Shop");
        setSize(520, 680);
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

        JLabel iconLabel = new JLabel("👤");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.setBorder(new EmptyBorder(0, 15, 0, 0));

        JLabel titleLabel = new JLabel("Créer un nouveau client");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(PRIMARY_BLUE);

        JLabel subtitleLabel = new JLabel("Remplissez les informations du client");
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
        formCard.add(createFieldGroup("Email *", createStyledTextField(), "email@exemple.com"));
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));

        formCard.add(createFieldGroup("Mot de passe *", createStyledPasswordField(), "Mot de passe sécurisé"));
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));

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
        formCard.add(noteLabel);

        // Création du scroll pane
        JScrollPane scrollPane = new JScrollPane(formCard);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Vitesse du scroll

        // Ajout au panel principal
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
            textField.putClientProperty("JTextField.placeholderText", placeholder);
            // Stocker la référence selon le label
            assignFieldReference(labelText, textField);
        } else if (field instanceof JPasswordField) {
            JPasswordField passwordField = (JPasswordField) field;
            passwordField.putClientProperty("JTextField.placeholderText", placeholder);
            mdpField = passwordField;
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
        if (labelText.startsWith("Email")) {
            emailField = field;
        } else if (labelText.startsWith("Date")) {
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

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
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

        creerButton = createModernButton("Créer le client", SUCCESS_GREEN, WHITE);
        creerButton.addActionListener(e -> creerClient());

        buttonsPanel.add(annulerButton);
        buttonsPanel.add(creerButton);

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
        button.setPreferredSize(new Dimension(140, 44));
        return button;
    }

    private void setupValidation() {
        // Validation en temps réel pour l'email
        if (emailField != null) {
            emailField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    validateEmail();
                }
            });
        }

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

    private void validateEmail() {
        String email = emailField.getText().trim();
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            emailField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(DANGER_RED, 2),
                    new EmptyBorder(8, 12, 8, 12)
            ));
        } else {
            emailField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                    new EmptyBorder(8, 12, 8, 12)
            ));
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

    private void creerClient() {
        // Récupération des données
        String email = emailField.getText().trim();
        String mdp = new String(mdpField.getPassword()).trim();
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String dateNaissance = dateNaissanceField.getText().trim();
        String adresse = adresseField.getText().trim();
        String telephone = telField.getText().trim();

        // Validation des champs obligatoires
        if (email.isEmpty() || mdp.isEmpty() || nom.isEmpty() || prenom.isEmpty()) {
            showErrorDialog("Erreur de validation",
                    "Veuillez remplir tous les champs obligatoires (Email, Mot de passe, Nom, Prénom).");
            return;
        }

        // Validation format email
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showErrorDialog("Email invalide",
                    "Veuillez saisir une adresse email valide.");
            emailField.requestFocus();
            return;
        }

        // Validation mot de passe
        if (mdp.length() < 6) {
            showErrorDialog("Mot de passe trop court",
                    "Le mot de passe doit contenir au moins 6 caractères.");
            mdpField.requestFocus();
            return;
        }

        // Validation date si renseignée
        if (!dateNaissance.isEmpty()) {
            try {
                LocalDate.parse(dateNaissance, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException e) {
                showErrorDialog("Date invalide",
                        "Format de date invalide. Utilisez le format YYYY-MM-DD (ex: 1990-12-25).");
                dateNaissanceField.requestFocus();
                return;
            }
        }

        // Vérification email unique
        ClientDAO clientDAO = new ClientDAO();
        if (clientDAO.emailExiste(email)) {
            showErrorDialog("Email déjà utilisé",
                    "Cette adresse email est déjà associée à un compte client.");
            emailField.requestFocus();
            return;
        }

        // Création du client
        try {
            Client client = new Client(email, mdp, nom, prenom, dateNaissance, adresse, telephone);
            boolean success = clientDAO.ajouterClient(client);

            if (success) {
                showSuccessDialog();
                dispose();
            } else {
                showErrorDialog("Erreur système",
                        "Une erreur est survenue lors de la création du client. Veuillez réessayer.");
            }
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
                "Le client a été créé avec succès !",
                "Succès",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new AjouterClient().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}