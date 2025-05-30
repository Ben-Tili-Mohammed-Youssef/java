package ui.Interfaces_Client;

import ui.Interfaces_Client.Gestion_Commandes.ModifierCommande;
import ui.Interfaces_Client.Gestion_Commandes.PasserCommande;
import ui.Interfaces_Client.Gestion_Commandes.SuivreCommande;
import ui.Interfaces_Client.Gestion_Commandes.SupprimerCommande;
import database.DatabaseConnection;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccueilClient extends JFrame {
    // Modern color scheme
    private static final Color PRIMARY_GREEN = new Color(21, 111, 98);
    private static final Color LIGHT_GREEN = new Color(129, 199, 132);
    private static final Color ACCENT_GREEN = new Color(76, 175, 80);
    private static final Color WHITE = Color.WHITE;
    private static final Color LIGHT_GRAY = new Color(248, 249, 250);
    private static final Color CARD_BACKGROUND = new Color(255, 255, 255);
    private static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    private static final Color TEXT_SECONDARY = new Color(108, 117, 125);

    private int clientId;
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel contentPanel;
    private JPanel sidebarPanel;

    public AccueilClient(int clientId) {
        this.clientId = clientId;

        // Setup modern look and feel
        setupLookAndFeel();

        // Initialize the modern UI
        initializeModernUI();

        // Setup frame properties
        setTitle("Café Shop - Espace Client");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
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
        // Main container
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LIGHT_GRAY);
        setContentPane(mainPanel);

        // Create header
        createModernHeader();

        // Create sidebar
        createModernSidebar();

        // Create main content area
        createModernContent();
    }

    private void createModernHeader() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                new EmptyBorder(15, 25, 15, 25)
        ));
        headerPanel.setPreferredSize(new Dimension(0, 80));

        // Left side - Logo and title
        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftHeader.setOpaque(false);

        JLabel logoLabel = new JLabel("☕");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.setBorder(new EmptyBorder(0, 15, 0, 0));

        JLabel titleLabel = new JLabel("Café Shop");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_GREEN);

        JLabel subtitleLabel = new JLabel("Espace Client");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);

        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        leftHeader.add(logoLabel);
        leftHeader.add(titlePanel);

        // Right side - User info and logout
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightHeader.setOpaque(false);

        // Welcome message
        String nomComplet = getNomPrenomClient(clientId);
        JLabel welcomeLabel = new JLabel("Bienvenue, " + nomComplet);
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        welcomeLabel.setForeground(TEXT_PRIMARY);

        // Logout button
        JButton logoutButton = createModernButton("Déconnexion", new Color(220, 53, 69), WHITE);
        logoutButton.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(
                    this,
                    "Êtes-vous sûr de vouloir vous déconnecter?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION
            );
            if (option == JOptionPane.YES_OPTION) {
                dispose();
                // Retour à l'écran de connexion
                SwingUtilities.invokeLater(() -> new ui.Login().setVisible(true));
            }
        });

        rightHeader.add(welcomeLabel);
        rightHeader.add(logoutButton);

        headerPanel.add(leftHeader, BorderLayout.WEST);
        headerPanel.add(rightHeader, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
    }

    private void createModernSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(WHITE);
        sidebarPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 230, 230)),
                new EmptyBorder(25, 0, 25, 0)
        ));
        sidebarPanel.setPreferredSize(new Dimension(280, 0));

        // Menu sections
        addMenuSection("Mon Compte", new String[][]{
                {"👤", "Modifier Mot de Passe", "modifierMotDePasse"}
        });

        addMenuSection("Menu & Carte", new String[][]{
                {"📋", "Consulter la Carte", "consulterCarte"}
        });

        addMenuSection("Mes Commandes", new String[][]{
                {"🛒", "Passer une Commande", "passerCommande"},
                {"✏️", "Modifier une Commande", "modifierCommande"},
                {"🗑️", "Supprimer une Commande", "supprimerCommande"},
                {"📍", "Suivre ma Commande", "suivreCommande"}
        });

        mainPanel.add(sidebarPanel, BorderLayout.WEST);
    }

    private void addMenuSection(String sectionTitle, String[][] menuItems) {
        // Section title
        JLabel sectionLabel = new JLabel(sectionTitle);
        sectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sectionLabel.setForeground(TEXT_SECONDARY);
        sectionLabel.setBorder(new EmptyBorder(15, 25, 10, 25));
        sectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarPanel.add(sectionLabel);

        // Menu items
        for (String[] item : menuItems) {
            JButton menuButton = createSidebarButton(item[0], item[1], item[2]);
            sidebarPanel.add(menuButton);
        }
    }

    private JButton createSidebarButton(String icon, String text, String action) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setBorder(new EmptyBorder(12, 25, 12, 25));
        button.setBackground(WHITE);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textLabel.setForeground(TEXT_PRIMARY);
        textLabel.setBorder(new EmptyBorder(0, 15, 0, 0));

        button.add(iconLabel, BorderLayout.WEST);
        button.add(textLabel, BorderLayout.CENTER);

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(LIGHT_GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(WHITE);
            }
        });

        // Action handlers
        button.addActionListener(e -> handleMenuAction(action));

        return button;
    }

    private void handleMenuAction(String action) {
        switch (action) {
            case "modifierMotDePasse":
                showFeatureDialog("Modifier Mot de Passe",
                        "Cette fonctionnalité vous permet de changer votre mot de passe.");
                break;
            case "consulterCarte":
                try {
                    new ConsulterCarteAvecCommande(clientId).setVisible(true);
                } catch (Exception e) {
                    showErrorDialog("Erreur", "Impossible d'ouvrir la carte du menu.");
                }
                break;
            case "passerCommande":
                try {
                    new PasserCommande(clientId).setVisible(true);
                } catch (Exception e) {
                    showErrorDialog("Erreur", "Impossible d'ouvrir l'interface de commande.");
                }
                break;
            case "modifierCommande":
                try {
                    new ModifierCommande(clientId).setVisible(true);
                } catch (Exception e) {
                    showErrorDialog("Erreur", "Impossible d'ouvrir l'interface de modification.");
                }
                break;
            case "supprimerCommande":
                try {
                    new SupprimerCommande(clientId).setVisible(true);
                } catch (Exception e) {
                    showErrorDialog("Erreur", "Impossible d'ouvrir l'interface de suppression.");
                }
                break;
            case "suivreCommande":
                try {
                    new SuivreCommande(clientId).setVisible(true);
                } catch (Exception e) {
                    showErrorDialog("Erreur", "Impossible d'ouvrir l'interface de suivre commande.");
                }
                break;
        }
    }

    private void createModernContent() {
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(LIGHT_GRAY);
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Welcome card
        JPanel welcomeCard = createWelcomeCard();

        // Quick actions
        JPanel quickActionsPanel = createQuickActionsPanel();

        // Main content area
        JPanel centerPanel = new JPanel(new BorderLayout(0, 25));
        centerPanel.setOpaque(false);
        centerPanel.add(welcomeCard, BorderLayout.NORTH);
        centerPanel.add(quickActionsPanel, BorderLayout.CENTER);

        contentPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createWelcomeCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(30, 35, 30, 35)
        ));

        String nomComplet = getNomPrenomClient(clientId);

        JLabel welcomeTitle = new JLabel("Bienvenue " + nomComplet + " !");
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeTitle.setForeground(PRIMARY_GREEN);

        JLabel welcomeSubtitle = new JLabel("Découvrez notre délicieuse sélection de cafés et gérez vos commandes facilement.");
        welcomeSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        welcomeSubtitle.setForeground(TEXT_SECONDARY);
        welcomeSubtitle.setBorder(new EmptyBorder(10, 0, 0, 0));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(welcomeTitle);
        textPanel.add(welcomeSubtitle);

        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setOpaque(false);

        // Quick action cards
        panel.add(createActionCard("🛒", "Commander", "Passez une nouvelle commande", PRIMARY_GREEN, () ->
                handleMenuAction("passerCommande")));
        panel.add(createActionCard("📋", "Menu", "Consultez notre carte", ACCENT_GREEN, () ->
                handleMenuAction("consulterCarte")));
        panel.add(createActionCard("✏️", "Modifier", "Modifiez vos commandes", new Color(255, 193, 7), () ->
                handleMenuAction("modifierCommande")));
        panel.add(createActionCard("📍", "Suivre", "Suivez vos commandes", new Color(108, 117, 125), () ->
                handleMenuAction("suivreCommande")));

        return panel;
    }

    private JPanel createActionCard(String icon, String title, String description, Color color, Runnable action) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(25, 25, 25, 25)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(color);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(TEXT_SECONDARY);
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(descLabel);

        card.add(iconLabel, BorderLayout.NORTH);
        card.add(textPanel, BorderLayout.CENTER);

        // Hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(LIGHT_GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(CARD_BACKGROUND);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                action.run();
            }
        });

        return card;
    }

    private JButton createModernButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(8, 16, 8, 16));
        return button;
    }

    private void showFeatureDialog(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private String getNomPrenomClient(int id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT nom, prenom FROM clients WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                return (prenom != null ? prenom : "") + " " + (nom != null ? nom : "");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Cher client";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AccueilClient(2).setVisible(true));
    }
}