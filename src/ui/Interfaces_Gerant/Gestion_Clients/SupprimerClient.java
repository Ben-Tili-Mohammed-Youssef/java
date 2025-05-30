package ui.Interfaces_Gerant.Gestion_Clients;

import dao.ClientDAO;
import model.Client;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class SupprimerClient extends JFrame {
    // Couleurs cohérentes
    private static final Color PRIMARY_RED = new Color(183, 28, 28);
    private static final Color SECONDARY_RED = new Color(211, 47, 47);
    private static final Color ACCENT_RED = new Color(244, 67, 54);
    private static final Color DANGER_RED = new Color(244, 67, 54);
    private static final Color WARNING_ORANGE = new Color(255, 152, 0);
    private static final Color SUCCESS_GREEN = new Color(76, 175, 80);
    private static final Color WHITE = Color.WHITE;
    private static final Color LIGHT_GRAY = new Color(248, 249, 250);
    private static final Color CARD_BACKGROUND = new Color(255, 255, 255);
    private static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    private static final Color TEXT_SECONDARY = new Color(108, 117, 125);
    private static final Color BORDER_COLOR = new Color(230, 230, 230);

    private JTextField emailSearchField;
    private JLabel nomLabel, prenomLabel, emailLabel, dateNaissanceLabel, adresseLabel, telephoneLabel;
    private JButton chercherButton;
    private JButton supprimerButton;
    private JButton annulerButton;
    private JPanel clientInfoPanel;
    private ClientDAO clientDAO;
    private Client clientActuel;

    public SupprimerClient() {
        setupLookAndFeel();
        clientDAO = new ClientDAO();
        initializeUI();
        setupValidation();
        setDeleteEnabled(false);
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

    private void initializeUI() {
        setTitle("Supprimer Client - Café Shop");
        setSize(600, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // Panel principal avec GridBagLayout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(LIGHT_GRAY);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Header
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        mainPanel.add(createHeaderPanel(), gbc);

        // Search Panel
        gbc.gridy = 1;
        mainPanel.add(createSearchPanel(), gbc);

        // Client Info Panel
        gbc.gridy = 2;
        clientInfoPanel = createClientInfoPanel();
        mainPanel.add(clientInfoPanel, gbc);
        setClientInfoVisible(false);

        // Warning Panel
        gbc.gridy = 3;
        mainPanel.add(createWarningPanel(), gbc);

        // Buttons Panel
        gbc.gridy = 4;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(createButtonsPanel(), gbc);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBackground(WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(25, 25, 25, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();

        // Icône
        JLabel iconLabel = new JLabel("🗑️");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.insets = new Insets(0, 0, 0, 15);
        headerPanel.add(iconLabel, gbc);

        // Titre principal
        JLabel titleLabel = new JLabel("Supprimer un client");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_RED);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        headerPanel.add(titleLabel, gbc);

        // Sous-titre
        JLabel subtitleLabel = new JLabel("Recherchez et supprimez un client du système");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 0, 0, 0);
        headerPanel.add(subtitleLabel, gbc);

        return headerPanel;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(CARD_BACKGROUND);

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                "🔍 Rechercher un client"
        );
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 16));
        border.setTitleColor(PRIMARY_RED);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                border,
                new EmptyBorder(15, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();

        // Label d'instruction
        JLabel instructionLabel = new JLabel("Entrez l'adresse email du client à supprimer");
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        instructionLabel.setForeground(TEXT_SECONDARY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.anchor = GridBagConstraints.WEST;
        searchPanel.add(instructionLabel, gbc);

        // Label pour le champ email
        JLabel emailLabel = new JLabel("Email du client *");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        emailLabel.setForeground(TEXT_PRIMARY);
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 8, 0);
        searchPanel.add(emailLabel, gbc);

        // Champ de recherche
        emailSearchField = createStyledTextField();
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 15, 10);
        searchPanel.add(emailSearchField, gbc);

        // Bouton rechercher
        chercherButton = createModernButton("Rechercher", ACCENT_RED, WHITE);
        chercherButton.addActionListener(e -> chercherClient());
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 0, 15, 0);
        searchPanel.add(chercherButton, gbc);

        return searchPanel;
    }

    private JPanel createClientInfoPanel() {
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(CARD_BACKGROUND);

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                "👤 Informations du client"
        );
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 16));
        border.setTitleColor(PRIMARY_RED);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                border,
                new EmptyBorder(15, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 5, 20);

        // Instruction
        JLabel instructionLabel = new JLabel("Vérifiez les informations avant la suppression");
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        instructionLabel.setForeground(TEXT_SECONDARY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(0, 0, 20, 0);
        infoPanel.add(instructionLabel, gbc);

        // Reset gridwidth and insets for the form fields
        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 0, 8, 20);

        // Création des labels avec style
        Font labelFont = new Font("Segoe UI", Font.BOLD, 13);
        Font valueFont = new Font("Segoe UI", Font.PLAIN, 13);

        // Nom
        JLabel nomTitleLabel = new JLabel("Nom:");
        nomTitleLabel.setFont(labelFont);
        nomTitleLabel.setForeground(TEXT_PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 1;
        infoPanel.add(nomTitleLabel, gbc);

        nomLabel = new JLabel("Non renseigné");
        nomLabel.setFont(valueFont);
        nomLabel.setForeground(TEXT_SECONDARY);
        gbc.gridx = 1;
        infoPanel.add(nomLabel, gbc);

        // Prénom
        JLabel prenomTitleLabel = new JLabel("Prénom:");
        prenomTitleLabel.setFont(labelFont);
        prenomTitleLabel.setForeground(TEXT_PRIMARY);
        gbc.gridx = 2;
        infoPanel.add(prenomTitleLabel, gbc);

        prenomLabel = new JLabel("Non renseigné");
        prenomLabel.setFont(valueFont);
        prenomLabel.setForeground(TEXT_SECONDARY);
        gbc.gridx = 3;
        infoPanel.add(prenomLabel, gbc);

        // Email
        JLabel emailTitleLabel = new JLabel("Email:");
        emailTitleLabel.setFont(labelFont);
        emailTitleLabel.setForeground(TEXT_PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 2;
        infoPanel.add(emailTitleLabel, gbc);

        emailLabel = new JLabel("Non renseigné");
        emailLabel.setFont(valueFont);
        emailLabel.setForeground(TEXT_SECONDARY);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        infoPanel.add(emailLabel, gbc);

        // Date de naissance
        JLabel dateTitleLabel = new JLabel("Date de naissance:");
        dateTitleLabel.setFont(labelFont);
        dateTitleLabel.setForeground(TEXT_PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        infoPanel.add(dateTitleLabel, gbc);

        dateNaissanceLabel = new JLabel("Non renseignée");
        dateNaissanceLabel.setFont(valueFont);
        dateNaissanceLabel.setForeground(TEXT_SECONDARY);
        gbc.gridx = 1;
        infoPanel.add(dateNaissanceLabel, gbc);

        // Téléphone
        JLabel telephoneTitleLabel = new JLabel("Téléphone:");
        telephoneTitleLabel.setFont(labelFont);
        telephoneTitleLabel.setForeground(TEXT_PRIMARY);
        gbc.gridx = 2;
        infoPanel.add(telephoneTitleLabel, gbc);

        telephoneLabel = new JLabel("Non renseigné");
        telephoneLabel.setFont(valueFont);
        telephoneLabel.setForeground(TEXT_SECONDARY);
        gbc.gridx = 3;
        infoPanel.add(telephoneLabel, gbc);

        // Adresse
        JLabel adresseTitleLabel = new JLabel("Adresse:");
        adresseTitleLabel.setFont(labelFont);
        adresseTitleLabel.setForeground(TEXT_PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 4;
        infoPanel.add(adresseTitleLabel, gbc);

        adresseLabel = new JLabel("Non renseignée");
        adresseLabel.setFont(valueFont);
        adresseLabel.setForeground(TEXT_SECONDARY);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        infoPanel.add(adresseLabel, gbc);

        return infoPanel;
    }

    private JPanel createWarningPanel() {
        JPanel warningPanel = new JPanel(new GridBagLayout());
        warningPanel.setBackground(new Color(255, 248, 225));
        warningPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(WARNING_ORANGE, 2),
                new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();

        // Header avec icône et titre
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerPanel.setOpaque(false);

        JLabel warningIcon = new JLabel("⚠️");
        warningIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));

        JLabel warningTitle = new JLabel("Attention !");
        warningTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        warningTitle.setForeground(new Color(183, 110, 0));
        warningTitle.setBorder(new EmptyBorder(0, 10, 0, 0));

        headerPanel.add(warningIcon);
        headerPanel.add(warningTitle);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        warningPanel.add(headerPanel, gbc);

        // Texte d'avertissement
        JTextArea warningText = new JTextArea(
                "La suppression d'un client est une action IRRÉVERSIBLE. " +
                        "Toutes les données associées à ce client seront définitivement perdues. " +
                        "Assurez-vous de vouloir procéder à cette suppression."
        );
        warningText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        warningText.setForeground(new Color(102, 77, 3));
        warningText.setBackground(new Color(255, 248, 225));
        warningText.setEditable(false);
        warningText.setWrapStyleWord(true);
        warningText.setLineWrap(true);
        warningText.setBorder(new EmptyBorder(10, 0, 0, 0));

        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 0, 0, 0);
        warningPanel.add(warningText, gbc);

        return warningPanel;
    }

    private JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonsPanel.setOpaque(false);

        annulerButton = createModernButton("Annuler", TEXT_SECONDARY, WHITE);
        annulerButton.addActionListener(e -> dispose());

        supprimerButton = createModernButton("Supprimer définitivement", DANGER_RED, WHITE);
        supprimerButton.addActionListener(e -> confirmerSuppression());

        buttonsPanel.add(annulerButton);
        buttonsPanel.add(supprimerButton);

        return buttonsPanel;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(250, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    private JButton createModernButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(12, 20, 12, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(180, 40));
        return button;
    }

    private void setupValidation() {
        emailSearchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validateEmail();
            }
        });
    }

    private void validateEmail() {
        String email = emailSearchField.getText().trim();
        if (!email.isEmpty()) {
            if (email.contains("@") && email.contains(".")) {
                emailSearchField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(SUCCESS_GREEN, 2),
                        new EmptyBorder(8, 12, 8, 12)
                ));
            } else {
                emailSearchField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(DANGER_RED, 2),
                        new EmptyBorder(8, 12, 8, 12)
                ));
            }
        } else {
            emailSearchField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                    new EmptyBorder(8, 12, 8, 12)
            ));
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
            // Afficher les informations du client trouvé
            nomLabel.setText(clientActuel.getNom() != null ? clientActuel.getNom() : "Non renseigné");
            prenomLabel.setText(clientActuel.getPrenom() != null ? clientActuel.getPrenom() : "Non renseigné");
            emailLabel.setText(clientActuel.getEmail() != null ? clientActuel.getEmail() : "Non renseigné");
            dateNaissanceLabel.setText(clientActuel.getDateNaissance() != null ?
                    clientActuel.getDateNaissance().toString() : "Non renseignée");
            adresseLabel.setText(clientActuel.getAdresse() != null ? clientActuel.getAdresse() : "Non renseignée");
            telephoneLabel.setText(clientActuel.getTelephone() != null ? clientActuel.getTelephone() : "Non renseigné");

            setClientInfoVisible(true);
            setDeleteEnabled(true);

            showSuccessDialog("Client trouvé !", "Le client a été trouvé avec succès. Vérifiez ses informations avant de procéder à la suppression.");
        } else {
            showErrorDialog("Client introuvable", "Aucun client trouvé avec cette adresse email.");
            clearClientInfo();
            setDeleteEnabled(false);
        }
    }

    private void confirmerSuppression() {
        if (clientActuel == null) {
            showErrorDialog("Aucun client sélectionné", "Veuillez d'abord rechercher un client à supprimer.");
            return;
        }

        String message = String.format(
                "Êtes-vous absolument certain(e) de vouloir supprimer le client :\n\n" +
                        "• Nom : %s %s\n" +
                        "• Email : %s\n\n" +
                        "⚠️ Cette action est IRRÉVERSIBLE !\n" +
                        "Toutes les données de ce client seront définitivement perdues.",
                clientActuel.getPrenom(),
                clientActuel.getNom(),
                clientActuel.getEmail()
        );

        int choix = JOptionPane.showConfirmDialog(
                this,
                message,
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choix == JOptionPane.YES_OPTION) {
            int doubleConfirmation = JOptionPane.showConfirmDialog(
                    this,
                    "Dernière confirmation :\n\nConfirmez-vous la suppression définitive ?",
                    "Confirmation finale",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE
            );

            if (doubleConfirmation == JOptionPane.YES_OPTION) {
                supprimerClient();
            }
        }
    }

    private void supprimerClient() {
        boolean success = clientDAO.supprimerClientByEmail(clientActuel.getEmail());

        if (success) {
            showSuccessDialog("Client supprimé",
                    "Le client a été supprimé avec succès du système.\n" +
                            "Toutes ses données ont été définitivement effacées.");

            // Réinitialiser l'interface
            emailSearchField.setText("");
            clearClientInfo();
            setDeleteEnabled(false);
            clientActuel = null;
        } else {
            showErrorDialog("Erreur système",
                    "Une erreur est survenue lors de la suppression du client.\n" +
                            "Veuillez réessayer ou contacter l'administrateur système.");
        }
    }

    private void setDeleteEnabled(boolean enabled) {
        supprimerButton.setEnabled(enabled);
        supprimerButton.setBackground(enabled ? DANGER_RED : new Color(200, 200, 200));
    }

    private void setClientInfoVisible(boolean visible) {
        Component[] components = clientInfoPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                if (label == nomLabel || label == prenomLabel || label == emailLabel ||
                        label == dateNaissanceLabel || label == adresseLabel || label == telephoneLabel) {
                    label.setForeground(visible ? TEXT_PRIMARY : TEXT_SECONDARY);
                }
            }
        }
        clientInfoPanel.setBackground(visible ? WHITE : new Color(248, 249, 250));
        clientInfoPanel.repaint();
    }

    private void clearClientInfo() {
        nomLabel.setText("Non renseigné");
        prenomLabel.setText("Non renseigné");
        emailLabel.setText("Non renseigné");
        dateNaissanceLabel.setText("Non renseignée");
        adresseLabel.setText("Non renseignée");
        telephoneLabel.setText("Non renseigné");
        setClientInfoVisible(false);
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
                new SupprimerClient().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}