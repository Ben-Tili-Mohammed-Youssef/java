package ui.Interfaces_Gerant;

import com.formdev.flatlaf.FlatLightLaf;
import ui.Interfaces_Gerant.Gestion_Articles.*;
import ui.Interfaces_Gerant.Gestion_Clients.*;
import ui.Interfaces_Gerant.Gestion_typeArticles.*;
import ui.Interfaces_Gerant.Gestion_Commandes.*;
import ui.Login;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AccueilGerant extends JFrame {
    // Modern color scheme
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

    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel contentPanel;
    private JPanel sidebarPanel;
    private JLabel timeLabel;
    private Timer clockTimer;
    private JLabel activeSectionLabel;
    private CardLayout cardLayout;

    public AccueilGerant() {
        setupLookAndFeel();
        initializeModernUI();

        setTitle("Café Shop - Panneau de Gestion");
        setSize(1400, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        startClock();
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
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LIGHT_GRAY);
        setContentPane(mainPanel);

        createModernHeader();
        createModernSidebar();
        createModernContent();
    }

    private void createModernHeader() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                new EmptyBorder(15, 25, 15, 25)
        ));
        headerPanel.setPreferredSize(new Dimension(0, 85));

        // Left side - Logo and title
        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftHeader.setOpaque(false);

        JLabel logoLabel = new JLabel("☕");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.setBorder(new EmptyBorder(0, 15, 0, 0));

        JLabel titleLabel = new JLabel("Café Shop Gérant");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(PRIMARY_BLUE);

        activeSectionLabel = new JLabel("Menu Principal");
        activeSectionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        activeSectionLabel.setForeground(TEXT_SECONDARY);

        titlePanel.add(titleLabel);
        titlePanel.add(activeSectionLabel);

        leftHeader.add(logoLabel);
        leftHeader.add(titlePanel);

        // Center - Quick stats
        JPanel centerHeader = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        centerHeader.setOpaque(false);

        centerHeader.add(createQuickStat("👥", "142", "Clients"));
        centerHeader.add(createQuickStat("📦", "87", "Commandes"));
        centerHeader.add(createQuickStat("☕", "23", "Produits"));

        // Right side - Time and logout
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightHeader.setOpaque(false);

        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        timeLabel.setForeground(TEXT_PRIMARY);
        updateTime();

        JButton logoutButton = createModernButton("Déconnexion", DANGER_RED, WHITE);
        logoutButton.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(
                    this,
                    "Êtes-vous sûr de vouloir vous déconnecter?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION
            );
            if (option == JOptionPane.YES_OPTION) {
                if (clockTimer != null) {
                    clockTimer.stop();
                }
                dispose();
                SwingUtilities.invokeLater(() -> new Login().setVisible(true));
            }
        });

        rightHeader.add(timeLabel);
        rightHeader.add(logoutButton);

        headerPanel.add(leftHeader, BorderLayout.WEST);
        headerPanel.add(centerHeader, BorderLayout.CENTER);
        headerPanel.add(rightHeader, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
    }

    private JPanel createQuickStat(String icon, String value, String label) {
        JPanel statPanel = new JPanel();
        statPanel.setLayout(new BoxLayout(statPanel, BoxLayout.Y_AXIS));
        statPanel.setOpaque(false);
        statPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(PRIMARY_BLUE);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelText.setForeground(TEXT_SECONDARY);
        labelText.setAlignmentX(Component.CENTER_ALIGNMENT);

        statPanel.add(iconLabel);
        statPanel.add(valueLabel);
        statPanel.add(labelText);

        return statPanel;
    }

    private void createModernSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(WHITE);
        sidebarPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 230, 230)),
                new EmptyBorder(25, 0, 25, 0)
        ));
        sidebarPanel.setPreferredSize(new Dimension(300, 0));

        // Menu sections simplifiés
        addMenuSection("Navigation", new String[][]{
                {"🏠", "Menu Principal", "accueil"},
                {"📊", "Tableau de Bord", "dashboard"} // Nouveau
        });

        addMenuSection("Gestion", new String[][]{
                {"👥", "Gérer les Clients", "gererClients"},
                {"☕", "Gérer les Articles", "gererArticles"},
                {"🏷️", "Types d'Articles", "gererCategories"},
                {"📦", "Gérer les Commandes", "gererCommandes"},
                {"👔", "Gérer les Employés", "gererEmployes"} // Nouveau
        });

        mainPanel.add(sidebarPanel, BorderLayout.WEST);
    }
    private void addMenuSection(String sectionTitle, String[][] menuItems) {
        JLabel sectionLabel = new JLabel(sectionTitle);
        sectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sectionLabel.setForeground(TEXT_SECONDARY);
        sectionLabel.setBorder(new EmptyBorder(15, 25, 10, 25));
        sectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarPanel.add(sectionLabel);

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

        button.addActionListener(e -> handleMenuAction(action));
        return button;
    }

    private void handleMenuAction(String action) {
        switch (action) {
            case "accueil":
                activeSectionLabel.setText("Menu Principal");
                showAccueil();
                break;
            case "gererClients":
                activeSectionLabel.setText("Gestion des Clients");
                showClientsPanel();
                break;
            case "gererArticles":
                activeSectionLabel.setText("Gestion des Articles");
                showArticlesPanel();
                break;
            case "gererCategories":
                activeSectionLabel.setText("Types d'Articles");
                showTypesArticlesPanel();
                break;
            case "gererCommandes":
                activeSectionLabel.setText("Gestion des Commandes");
                showCommandesPanel();
            case "dashboard":
                activeSectionLabel.setText("Tableau de Bord");
                showDashboardPanel();
                break;
            case "gererEmployes":
                activeSectionLabel.setText("Gestion des Employés");
                showEmployesPanel();
                break;
        }
    }

    private void createModernContent() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(LIGHT_GRAY);
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Créer TOUS les panels
        createAccueilPanel();
        createClientsPanel();
        createArticlesPanel();
        createTypesArticlesPanel();
        createCommandesPanel();
        createDashboardPanel(); // Nouveau
        createEmployesPanel(); // Nouveau

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        showAccueil();
    }

    // CHANGEMENT PRINCIPAL: Cette fonction remplace showDashboard()
    private void showAccueil() {
        cardLayout.show(contentPanel, "Accueil");
    }

    private void showClientsPanel() {
        cardLayout.show(contentPanel, "Clients");
    }

    private void showArticlesPanel() {
        cardLayout.show(contentPanel, "Articles");
    }

    private void showTypesArticlesPanel() {
        cardLayout.show(contentPanel, "TypesArticles");
    }

    private void showCommandesPanel() {
        cardLayout.show(contentPanel, "Commandes");
    }
    private void showDashboardPanel() {
        cardLayout.show(contentPanel, "Dashboard");
    }

    private void showEmployesPanel() {
        cardLayout.show(contentPanel, "Employes");
    }

    private void showConfigPanel() {
        cardLayout.show(contentPanel, "Config");
    }

    // CHANGEMENT PRINCIPAL: Cette fonction remplace createDashboardPanel()
    private void createAccueilPanel() {
        JPanel accueilPanel = new JPanel(new BorderLayout());
        accueilPanel.setBackground(LIGHT_GRAY);

        // Welcome card (inchangé)
        JPanel welcomeCard = createWelcomeCard();

        // Main menu grid - version 3x2
        JPanel menuGrid = createMainMenuGrid();

        // Conteneur avec scroll si nécessaire
        JScrollPane scrollPane = new JScrollPane(menuGrid);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 25));
        centerPanel.setOpaque(false);
        centerPanel.add(welcomeCard, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        accueilPanel.add(centerPanel, BorderLayout.CENTER);
        contentPanel.add(accueilPanel, "Accueil");
    }

    private JPanel createWelcomeCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(30, 35, 30, 35)
        ));

        JLabel welcomeTitle = new JLabel("Tableau de Bord Gérant");
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        welcomeTitle.setForeground(PRIMARY_BLUE);

        JLabel welcomeSubtitle = new JLabel("Gérez votre café shop - Clients, Produits, Catégories et Commandes");
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

    // CHANGEMENT PRINCIPAL: Grid simplifié 2x2 au lieu de 1x4
    private JPanel createMainMenuGrid() {
        // Changement clé : GridLayout(2, 3) pour 2 lignes x 3 colonnes
        JPanel panel = new JPanel(new GridLayout(2, 3, 20, 20));
        panel.setOpaque(false);

        // Ligne 1
        panel.add(createActionCard("👥", "Clients", "Gérer les clients", PRIMARY_BLUE, () ->
                showClientsPanel()));
        panel.add(createActionCard("☕", "Articles", "Gérer les produits", SUCCESS_GREEN, () ->
                showArticlesPanel()));
        panel.add(createActionCard("🏷️", "Catégories", "Types d'articles", WARNING_ORANGE, () ->
                showTypesArticlesPanel()));

        // Ligne 2
        panel.add(createActionCard("📦", "Commandes", "Suivi des commandes", ACCENT_BLUE, () ->
                showCommandesPanel()));
        panel.add(createActionCard("👔", "Employés", "Gérer le personnel", DANGER_RED, () ->
                showEmployesPanel()));
        panel.add(createActionCard("📊", "Dashboard", "Statistiques", SECONDARY_BLUE, () ->
                showDashboardPanel()));

        return panel;
    }
    private void createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_GRAY);

        // Titre
        JLabel titleLabel = new JLabel("📊 Tableau de Bord - Statistiques");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Contenu statique (à remplacer plus tard par des vrais données)
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        statsPanel.add(createStatCard("💰", "Revenus", "1,240 €", PRIMARY_BLUE));
        statsPanel.add(createStatCard("📦", "Commandes", "87", SUCCESS_GREEN));
        statsPanel.add(createStatCard("👥", "Clients", "142", WARNING_ORANGE));
        statsPanel.add(createStatCard("☕", "Produits", "23", ACCENT_BLUE));
        statsPanel.add(createStatCard("👔", "Employés", "5", DANGER_RED));

        panel.add(statsPanel, BorderLayout.CENTER);
        contentPanel.add(panel, "Dashboard");
    }

    private JPanel createStatCard(String icon, String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(iconLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(valueLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(titleLabel);

        return card;
    }

    private void createClientsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_GRAY);

        JPanel buttonsGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        buttonsGrid.setOpaque(false);

        buttonsGrid.add(createFunctionalActionCard("Ajouter un Client",
                "Créer un nouveau compte client dans le système",
                "➕", PRIMARY_BLUE,
                e -> new AjouterClient().setVisible(true)));

        buttonsGrid.add(createFunctionalActionCard("Consulter les Clients",
                "Visualiser tous les comptes clients enregistrés",
                "👀", SUCCESS_GREEN,
                e -> new ConsulterClients().setVisible(true)));

        buttonsGrid.add(createFunctionalActionCard("Modifier un Client",
                "Mettre à jour les informations d'un client existant",
                "✏️", WARNING_ORANGE,
                e -> new ModifierClient().setVisible(true)));

        buttonsGrid.add(createFunctionalActionCard("Supprimer un Client",
                "Retirer un compte client du système",
                "🗑️", DANGER_RED,
                e -> new SupprimerClient().setVisible(true)));

        panel.add(buttonsGrid, BorderLayout.CENTER);
        contentPanel.add(panel, "Clients");
    }

    private void createArticlesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_GRAY);

        JPanel buttonsGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        buttonsGrid.setOpaque(false);

        buttonsGrid.add(createFunctionalActionCard("Ajouter un Article",
                "Créer un nouveau plat ou produit au menu",
                "➕", PRIMARY_BLUE,
                e -> new AjouterArticle().setVisible(true)));

        buttonsGrid.add(createFunctionalActionCard("Consulter les Articles",
                "Visualiser tous les articles disponibles au menu",
                "👀", SUCCESS_GREEN,
                e -> new ConsulterArticles().setVisible(true)));

        buttonsGrid.add(createFunctionalActionCard("Modifier un Article",
                "Mettre à jour les informations d'un article existant",
                "✏️", WARNING_ORANGE,
                e -> new ModifierArticle().setVisible(true)));

        buttonsGrid.add(createFunctionalActionCard("Supprimer un Article",
                "Retirer un article du menu",
                "🗑️", DANGER_RED,
                e -> new SupprimerArticle().setVisible(true)));

        panel.add(buttonsGrid, BorderLayout.CENTER);
        contentPanel.add(panel, "Articles");
    }

    private void createTypesArticlesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_GRAY);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);

        JPanel topRow = new JPanel(new GridLayout(1, 2, 20, 0));
        topRow.setOpaque(false);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        topRow.add(createFunctionalActionCard("Ajouter un Type",
                "Créer une nouvelle catégorie de produits",
                "➕", PRIMARY_BLUE,
                e -> new AjouterTypeArticle().setVisible(true)));

        topRow.add(createFunctionalActionCard("Consulter/Modifier les Types",
                "Visualiser et modifier les catégories existantes",
                "✏️", SUCCESS_GREEN,
                e -> new ConsulterModifierTypeArticle().setVisible(true)));

        JPanel bottomRow = new JPanel(new GridLayout(1, 3, 20, 0));
        bottomRow.setOpaque(false);
        bottomRow.setBorder(new EmptyBorder(20, 0, 0, 0));
        bottomRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JPanel emptyLeft = new JPanel();
        emptyLeft.setOpaque(false);
        JPanel emptyRight = new JPanel();
        emptyRight.setOpaque(false);

        bottomRow.add(emptyLeft);
        bottomRow.add(createFunctionalActionCard("Supprimer un Type",
                "Retirer une catégorie de produits",
                "🗑️", DANGER_RED,
                e -> new SupprimerTypeArticle().setVisible(true)));
        bottomRow.add(emptyRight);

        mainPanel.add(topRow);
        mainPanel.add(bottomRow);

        panel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.add(panel, "TypesArticles");
    }

    private void createCommandesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_GRAY);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);

        JPanel topRow = new JPanel(new GridLayout(1, 2, 20, 0));
        topRow.setOpaque(false);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        topRow.add(createFunctionalActionCard("Afficher les Commandes",
                "Visualiser toutes les commandes clients",
                "👀", PRIMARY_BLUE,
                e -> new AfficherCommandes().setVisible(true)));

        topRow.add(createFunctionalActionCard("Modifier l'État d'une Commande",
                "Changer le statut des commandes en cours",
                "✏️", SUCCESS_GREEN,
                e -> new ModifierEtatCommande().setVisible(true)));

        JPanel bottomRow = new JPanel(new GridLayout(1, 3, 20, 0));
        bottomRow.setOpaque(false);
        bottomRow.setBorder(new EmptyBorder(20, 0, 0, 0));
        bottomRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JPanel emptyLeft = new JPanel();
        emptyLeft.setOpaque(false);
        JPanel emptyRight = new JPanel();
        emptyRight.setOpaque(false);

        bottomRow.add(emptyLeft);
        bottomRow.add(createFunctionalActionCard("Supprimer une Commande",
                "Annuler et retirer une commande du système",
                "🗑️", DANGER_RED,
                e -> new SupprimerCommande().setVisible(true)));
        bottomRow.add(emptyRight);

        mainPanel.add(topRow);
        mainPanel.add(bottomRow);

        panel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.add(panel, "Commandes");
    }

    private void createEmployesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_GRAY);

        JPanel buttonsGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        buttonsGrid.setOpaque(false);

        buttonsGrid.add(createFunctionalActionCard("Ajouter un Employé",
                "Créer un nouveau compte employé",
                "➕", PRIMARY_BLUE,
                e -> JOptionPane.showMessageDialog(this, "Fonctionnalité à implémenter")));

        buttonsGrid.add(createFunctionalActionCard("Liste des Employés",
                "Visualiser tous les employés",
                "👀", SUCCESS_GREEN,
                e -> JOptionPane.showMessageDialog(this, "Fonctionnalité à implémenter")));

        buttonsGrid.add(createFunctionalActionCard("Modifier un Employé",
                "Mettre à jour les informations",
                "✏️", WARNING_ORANGE,
                e -> JOptionPane.showMessageDialog(this, "Fonctionnalité à implémenter")));

        buttonsGrid.add(createFunctionalActionCard("Supprimer un Employé",
                "Retirer un employé du système",
                "🗑️", DANGER_RED,
                e -> JOptionPane.showMessageDialog(this, "Fonctionnalité à implémenter")));

        panel.add(buttonsGrid, BorderLayout.CENTER);
        contentPanel.add(panel, "Employes");
    }

    private JPanel createActionCard(String icon, String title, String description, Color color, Runnable action) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(25, 25, 25, 25)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
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
        textPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        textPanel.add(descLabel);

        card.add(iconLabel, BorderLayout.NORTH);
        card.add(textPanel, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(248, 249, 250));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(color, 2),
                        new EmptyBorder(23, 23, 23, 23)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(CARD_BACKGROUND);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                        new EmptyBorder(25, 25, 25, 25)
                ));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                action.run();
            }
        });

        return card;
    }

    private JPanel createFunctionalActionCard(String title, String description, String icon, Color color, java.awt.event.ActionListener listener) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(color);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel descLabel = new JLabel("<html><div style='text-align: center;'>" + description + "</div></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(TEXT_SECONDARY);
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton actionButton = createModernButton("Accéder", color, WHITE);
        actionButton.addActionListener(listener);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(actionButton);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.add(iconLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        contentPanel.add(descLabel);

        card.add(contentPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(248, 249, 250));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(color, 2),
                        new EmptyBorder(18, 18, 18, 18)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(CARD_BACKGROUND);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                        new EmptyBorder(20, 20, 20, 20)
                ));
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
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void startClock() {
        clockTimer = new Timer(1000, e -> updateTime());
        clockTimer.start();
    }

    private void updateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
        timeLabel.setText(now.format(formatter));
    }

    @Override
    public void dispose() {
        if (clockTimer != null) {
            clockTimer.stop();
        }
        super.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new AccueilGerant().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Erreur lors du lancement de l'application : " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);

            }
        });
    }
}