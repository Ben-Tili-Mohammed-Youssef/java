package ui.Interfaces_Gerant;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import ui.Interfaces_Gerant.Gestion_Articles.*;
import ui.Interfaces_Gerant.Gestion_Clients.*;
import ui.Interfaces_Gerant.Gestion_typeArticles.*;
import ui.Interfaces_Gerant.Gestion_Commandes.*;
import ui.Login;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AccueilGerant extends JFrame {
    // ======================
    // CONSTANTES ET VARIABLES
    // ======================
    private JPanel mainPanel;
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private JLabel welcomeLabel;
    private JLabel activeSectionLabel;
    private JLabel dateTimeLabel;
    private Timer timer;

    // Boutons de menu
    private JButton dashboardBtn;
    private JButton clientsBtn;
    private JButton articlesBtn;
    private JButton typesArticlesBtn;
    private JButton commandesBtn;
    private JButton logoutBtn;
    private JToggleButton toggleSidebarBtn;

    // Couleurs
    private final Color primaryColor = new Color(0, 128, 128);
    private final Color sidebarColor = new Color(0, 77, 64);
    private final Color buttonHoverColor = new Color(0, 150, 136);
    private final Color textColor = Color.WHITE;

    private CardLayout cardLayout;
    private boolean sidebarExpanded = true;
    private final int expandedWidth = 220;
    private final int collapsedWidth = 60;

    // ==============
    // CONSTRUCTEUR
    // ==============
    public AccueilGerant() {
        setupLookAndFeel();
        initializeUI();
    }

    // ===================
    // INITIALISATION UI
    // ===================
    private void initializeUI() {
        setTitle("Espace Gérant - RestaurantApp");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(new ImageIcon(getClass().getResource("/ressources/cafe_logo.png")).getImage());

        mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);

        // Création des composants
        JPanel topPanel = createTopPanel();
        createSidebar();
        setupContentPanel();
        setupTimeUpdater();

        // Organisation de la fenêtre
        mainPanel.add(topPanel, BorderLayout.NORTH);
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(sidebarPanel, BorderLayout.WEST);
        centerPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        showDashboard();
    }

    private void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("Panel.arc", 10);
            UIManager.put("TextComponent.arc", 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =====================
    // TOP PANEL (nouveau)
    // =====================
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(primaryColor);
        topPanel.setPreferredSize(new Dimension(getWidth(), 100));

        // Panel supérieur contenant hamburger et titre app
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(primaryColor);
        titleBar.setBorder(new EmptyBorder(10, 20, 5, 20));

        // Bouton hamburger ≡
        toggleSidebarBtn = new JToggleButton("≡");
        toggleSidebarBtn.setFont(new Font("Arial", Font.BOLD, 18));
        toggleSidebarBtn.setForeground(textColor);
        toggleSidebarBtn.setBackground(primaryColor);
        toggleSidebarBtn.setBorderPainted(false);
        toggleSidebarBtn.setFocusPainted(false);
        toggleSidebarBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleSidebarBtn.addActionListener(e -> toggleSidebar());

        // Titre application
        JLabel titleLabel = new JLabel("Restaurant Management");
        titleLabel.setForeground(textColor);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(new EmptyBorder(0, 15, 0, 0));

        // Message de bienvenue (optionnel, à droite)
        welcomeLabel = new JLabel("Bienvenue, Gérant");
        welcomeLabel.setForeground(textColor);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        // Assemblage barre de titre
        titleBar.add(toggleSidebarBtn, BorderLayout.WEST);
        titleBar.add(titleLabel, BorderLayout.CENTER);
        titleBar.add(welcomeLabel, BorderLayout.EAST);

        // Panel pour le titre de la section active (qui changera dynamiquement)
        JPanel sectionTitlePanel = new JPanel(new BorderLayout());
        sectionTitlePanel.setBackground(primaryColor);
        sectionTitlePanel.setBorder(new EmptyBorder(5, 20, 10, 20));

        // Le label sera mis à jour en fonction de la section sélectionnée
        activeSectionLabel = new JLabel("Tableau de Bord");
        activeSectionLabel.setForeground(textColor);
        activeSectionLabel.setFont(new Font("Arial", Font.BOLD, 24));
        activeSectionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        sectionTitlePanel.add(activeSectionLabel, BorderLayout.CENTER);

        // Assemblage final
        topPanel.add(titleBar, BorderLayout.NORTH);
        topPanel.add(sectionTitlePanel, BorderLayout.CENTER);

        return topPanel;
    }

    // =================
    // SIDEBAR (simplifié)
    // =================
    private void createSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(sidebarColor);
        sidebarPanel.setPreferredSize(new Dimension(expandedWidth, getHeight()));

        // Création des boutons de menu
        dashboardBtn = createMenuButton("Dashboard", "/ressources/cafe_logo.png", this::showDashboard);
        clientsBtn = createMenuButton("Clients", "/images/clients_icon.png", this::showClientsPanel);
        articlesBtn = createMenuButton("Menu", "/images/menu_icon.png", this::showArticlesPanel);
        typesArticlesBtn = createMenuButton("Catégories", "/images/types_icon.png", this::showTypesArticlesPanel);
        commandesBtn = createMenuButton("Commandes", "/images/orders_icon.png", this::showCommandesPanel);

        // Bouton de déconnexion
        logoutBtn = createMenuButton("Déconnexion", "/images/logout_icon.png", this::logout);
        logoutBtn.setBackground(new Color(192, 57, 43));
        logoutBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutBtn.setBackground(new Color(231, 76, 60));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutBtn.setBackground(new Color(192, 57, 43));
            }
        });

        // Panel date/heure
        JPanel datePanel = new JPanel();
        datePanel.setBackground(sidebarColor);
        datePanel.setMaximumSize(new Dimension(expandedWidth, 40));
        dateTimeLabel = new JLabel();
        dateTimeLabel.setForeground(textColor);
        dateTimeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        datePanel.add(dateTimeLabel);

        // Ajout des composants au sidebar
        sidebarPanel.add(Box.createVerticalStrut(20));
        sidebarPanel.add(dashboardBtn);
        sidebarPanel.add(clientsBtn);
        sidebarPanel.add(articlesBtn);
        sidebarPanel.add(typesArticlesBtn);
        sidebarPanel.add(commandesBtn);
        sidebarPanel.add(Box.createVerticalGlue());
        sidebarPanel.add(logoutBtn);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(datePanel);
    }

    private void toggleSidebar() {
        sidebarExpanded = !sidebarExpanded;
        int newWidth = sidebarExpanded ? expandedWidth : collapsedWidth;
        sidebarPanel.setPreferredSize(new Dimension(newWidth, getHeight()));

        // Gestion du texte des boutons
        Component[] components = sidebarPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                if (!sidebarExpanded) {
                    btn.setText("");
                    btn.setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    // Restauration du texte
                    if (btn == dashboardBtn) btn.setText("Dashboard");
                    else if (btn == clientsBtn) btn.setText("Clients");
                    else if (btn == articlesBtn) btn.setText("Menu");
                    else if (btn == typesArticlesBtn) btn.setText("Catégories");
                    else if (btn == commandesBtn) btn.setText("Commandes");
                    else if (btn == logoutBtn) btn.setText("Déconnexion");
                    btn.setHorizontalAlignment(SwingConstants.LEFT);
                }
            }
        }

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    // ======================
    // METHODES UTILITAIRES
    // ======================
    private JButton createMenuButton(String text, String iconPath, Runnable action) {
        JButton button = new JButton(text);
        button.setForeground(textColor);
        button.setBackground(sidebarColor);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(expandedWidth, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            Image img = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
            button.setIconTextGap(10);
        } catch (Exception e) {
            System.out.println("Icon not found: " + iconPath);
        }

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(buttonHoverColor);
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(sidebarColor);
            }
        });

        button.addActionListener(e -> {
            resetButtonColors();
            button.setBackground(buttonHoverColor);
            action.run();
        });

        return button;
    }

    private void resetButtonColors() {
        dashboardBtn.setBackground(sidebarColor);
        clientsBtn.setBackground(sidebarColor);
        articlesBtn.setBackground(sidebarColor);
        typesArticlesBtn.setBackground(sidebarColor);
        commandesBtn.setBackground(sidebarColor);
    }

    // =====================
    // CONTENT PANEL
    // =====================
    private void setupContentPanel() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        createDashboardPanel();
        createClientsPanel();
        createArticlesPanel();
        createTypesArticlesPanel();
        createCommandesPanel();
    }

    private void showDashboard() {
        // Reset button colors and mark dashboard as active
        resetButtonColors();
        dashboardBtn.setBackground(buttonHoverColor);

        // Mise à jour du titre actif
        activeSectionLabel.setText("Tableau de Bord");

        // Remove existing dashboard if present
        Component[] components = contentPanel.getComponents();
        for (Component comp : components) {
            if (comp.getName() != null && comp.getName().equals("Dashboard")) {
                contentPanel.remove(comp);
                break;
            }
        }

        // Create and show fresh dashboard
        createDashboardPanel();
        cardLayout.show(contentPanel, "Dashboard");
    }
    // =============================
    // PANELS SPECIFIQUES (exemple)
    // =============================

    private void createDashboardPanel() {
        JPanel dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.setName("Dashboard");
        dashboardPanel.setBackground(Color.WHITE);
        // Stats Panel
        JPanel statsPanel = createStatsPanel();
        dashboardPanel.add(statsPanel, BorderLayout.CENTER);
        contentPanel.add(dashboardPanel, "Dashboard");
    }

    private JPanel createStatsPanel() {
        JPanel mainStatsPanel = new JPanel(new BorderLayout());
        mainStatsPanel.setBackground(Color.WHITE);

        // Top row - 2 columns grid
        JPanel topStatsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        topStatsPanel.setBackground(Color.WHITE);
        topStatsPanel.setBorder(new EmptyBorder(30, 30, 15, 30));

        // Bottom row - 3 columns grid
        JPanel bottomStatsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        bottomStatsPanel.setBackground(Color.WHITE);
        bottomStatsPanel.setBorder(new EmptyBorder(15, 30, 30, 30));

        // Get fresh data (replace with actual database calls)
        int activeClients = 42; // getActiveClientsCount();
        int menuItems = 68; // getMenuItemsCount();
        int pendingOrders = 12; // getPendingOrdersCount();
        double dailyRevenue = 1240.50; // getDailyRevenue();
        double totalRevenue = 24680.75; // getTotalRevenue();

        // Top row stats (larger cards)
        topStatsPanel.add(createStatCard("Revenus du Jour", String.format("%.2f €", dailyRevenue), new Color(155, 89, 182), "/images/daily_revenue_icon.png"));
        topStatsPanel.add(createStatCard("Revenus Totaux", String.format("%.2f €", totalRevenue), new Color(142, 68, 173), "/images/total_revenue_icon.png"));

        // Bottom row stats
        bottomStatsPanel.add(createStatCard("Clients Actifs", String.valueOf(activeClients), new Color(41, 128, 185), "/images/clients_icon.png"));
        bottomStatsPanel.add(createStatCard("Articles au Menu", String.valueOf(menuItems), new Color(46, 204, 113), "/images/menu_icon.png"));
        bottomStatsPanel.add(createStatCard("Commandes en Attente", String.valueOf(pendingOrders), new Color(230, 126, 34), "/images/pending_orders_icon.png"));

        mainStatsPanel.add(topStatsPanel, BorderLayout.NORTH);
        mainStatsPanel.add(bottomStatsPanel, BorderLayout.CENTER);

        return mainStatsPanel;
    }

    private JPanel createStatCard(String title, String value, Color color, String iconPath) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Icon panel (left side)
        JPanel iconPanel = new JPanel(new BorderLayout());
        iconPanel.setBackground(color);
        iconPanel.setPreferredSize(new Dimension(60, 60));
        iconPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            Image img = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            JLabel iconLabel = new JLabel(new ImageIcon(img));
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            iconPanel.add(iconLabel);
        } catch (Exception e) {
            // If icon not found, use text instead
            JLabel fallbackLabel = new JLabel(title.substring(0, 1));
            fallbackLabel.setForeground(Color.WHITE);
            fallbackLabel.setFont(new Font("Arial", Font.BOLD, 24));
            fallbackLabel.setHorizontalAlignment(SwingConstants.CENTER);
            iconPanel.add(fallbackLabel);
        }

        // Data panel (right side)
        JPanel dataPanel = new JPanel(new BorderLayout(5, 5));
        dataPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.DARK_GRAY);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(color);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));

        dataPanel.add(titleLabel, BorderLayout.NORTH);
        dataPanel.add(valueLabel, BorderLayout.CENTER);

        card.add(iconPanel, BorderLayout.WEST);
        card.add(dataPanel, BorderLayout.CENTER);

        return card;
    }

    private void createClientsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Action buttons in a 2x2 grid
        JPanel buttonsGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        buttonsGrid.setBackground(Color.WHITE);
        buttonsGrid.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Create action cards instead of plain buttons
        buttonsGrid.add(createActionCard("Ajouter un Client",
                "Créer un nouveau compte client dans le système",
                "/images/add_client_icon.png",
                new Color(41, 128, 185),
                e -> new AjouterClient().setVisible(true)));

        buttonsGrid.add(createActionCard("Consulter les Clients",
                "Visualiser tous les comptes clients enregistrés",
                "/images/view_clients_icon.png",
                new Color(46, 204, 113),
                e -> new ConsulterClients().setVisible(true)));

        buttonsGrid.add(createActionCard("Modifier un Client",
                "Mettre à jour les informations d'un client existant",
                "/images/edit_client_icon.png",
                new Color(230, 126, 34),
                e -> new ModifierClient().setVisible(true)));

        buttonsGrid.add(createActionCard("Supprimer un Client",
                "Retirer un compte client du système",
                "/images/delete_client_icon.png",
                new Color(231, 76, 60),
                e -> new SupprimerClient().setVisible(true)));

        panel.add(buttonsGrid, BorderLayout.CENTER);
        contentPanel.add(panel, "Clients");
    }

    private void createArticlesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        // Action buttons in a 2x2 grid
        JPanel buttonsGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        buttonsGrid.setBackground(Color.WHITE);
        buttonsGrid.setBorder(new EmptyBorder(30, 30, 30, 30));

        buttonsGrid.add(createActionCard("Ajouter un Article",
                "Créer un nouveau plat ou produit au menu",
                "/images/add_item_icon.png",
                new Color(41, 128, 185),
                e -> new AjouterArticle().setVisible(true)));

        buttonsGrid.add(createActionCard("Consulter les Articles",
                "Visualiser tous les articles disponibles au menu",
                "/images/view_items_icon.png",
                new Color(46, 204, 113),
                e -> new ConsulterArticles().setVisible(true)));

        buttonsGrid.add(createActionCard("Modifier un Article",
                "Mettre à jour les informations d'un article existant",
                "/images/edit_item_icon.png",
                new Color(230, 126, 34),
                e -> new ModifierArticle().setVisible(true)));

        buttonsGrid.add(createActionCard("Supprimer un Article",
                "Retirer un article du menu",
                "/images/delete_item_icon.png",
                new Color(231, 76, 60),
                e -> new SupprimerArticle().setVisible(true)));

        panel.add(buttonsGrid, BorderLayout.CENTER);
        contentPanel.add(panel, "Articles");
    }

    // Remplacer la méthode createTypesArticlesPanel() existante par celle-ci :
    private void createTypesArticlesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Panel principal avec BoxLayout vertical
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Top row - 2 colonnes
        JPanel topRow = new JPanel(new GridLayout(1, 2, 20, 0));
        topRow.setBackground(Color.WHITE);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        topRow.add(createActionCard("Ajouter un Type",
                "Créer une nouvelle catégorie de produits",
                "/images/add_category_icon.png",
                new Color(41, 128, 185),
                e -> new AjouterTypeArticle().setVisible(true)));

        topRow.add(createActionCard("Consulter/Modifier les Types",
                "Visualiser et modifier les catégories existantes",
                "/images/edit_category_icon.png",
                new Color(46, 204, 113),
                e -> new ConsulterModifierTypeArticle().setVisible(true)));

        // Bottom row - 1 colonne centrée avec même hauteur
        JPanel bottomRow = new JPanel(new GridLayout(1, 3, 20, 0));
        bottomRow.setBackground(Color.WHITE);
        bottomRow.setBorder(new EmptyBorder(20, 0, 0, 0));
        bottomRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        // Panels vides pour centrer
        JPanel emptyLeft = new JPanel();
        emptyLeft.setBackground(Color.WHITE);
        JPanel emptyRight = new JPanel();
        emptyRight.setBackground(Color.WHITE);

        bottomRow.add(emptyLeft);
        bottomRow.add(createActionCard("Supprimer un Type",
                "Retirer une catégorie de produits",
                "/images/delete_category_icon.png",
                new Color(231, 76, 60),
                e -> new SupprimerTypeArticle().setVisible(true)));
        bottomRow.add(emptyRight);

        // Assemblage
        mainPanel.add(topRow);
        mainPanel.add(bottomRow);

        panel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.add(panel, "TypesArticles");
    }

    // Remplacer la méthode createCommandesPanel() existante par celle-ci :
    private void createCommandesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Panel principal avec BoxLayout vertical
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Top row - 2 colonnes
        JPanel topRow = new JPanel(new GridLayout(1, 2, 20, 0));
        topRow.setBackground(Color.WHITE);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        topRow.add(createActionCard("Afficher les Commandes",
                "Visualiser toutes les commandes clients",
                "/images/view_orders_icon.png",
                new Color(41, 128, 185),
                e -> new AfficherCommandes().setVisible(true)));

        topRow.add(createActionCard("Modifier l'État d'une Commande",
                "Changer le statut des commandes en cours",
                "/images/update_status_icon.png",
                new Color(46, 204, 113),
                e -> new ModifierEtatCommande().setVisible(true)));

        // Bottom row - 1 colonne centrée avec même hauteur
        JPanel bottomRow = new JPanel(new GridLayout(1, 3, 20, 0));
        bottomRow.setBackground(Color.WHITE);
        bottomRow.setBorder(new EmptyBorder(20, 0, 0, 0));
        bottomRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        // Panels vides pour centrer
        JPanel emptyLeft = new JPanel();
        emptyLeft.setBackground(Color.WHITE);
        JPanel emptyRight = new JPanel();
        emptyRight.setBackground(Color.WHITE);

        bottomRow.add(emptyLeft);
        bottomRow.add(createActionCard("Supprimer une Commande",
                "Annuler et retirer une commande du système",
                "/images/delete_order_icon.png",
                new Color(231, 76, 60),
                e -> new SupprimerCommande().setVisible(true)));
        bottomRow.add(emptyRight);

        // Assemblage
        mainPanel.add(topRow);
        mainPanel.add(bottomRow);

        panel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.add(panel, "Commandes");
    }

    private JPanel createActionCard(String title, String description, String iconPath, Color color, ActionListener listener) {
        JPanel card = new JPanel(new BorderLayout(0, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout(10, 0));
        titlePanel.setBackground(card.getBackground());

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            Image img = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            JLabel iconLabel = new JLabel(new ImageIcon(img));
            titlePanel.add(iconLabel, BorderLayout.WEST);
        } catch (Exception e) {
            // Icon not found, continue without icon
        }

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(color);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Description panel
        JLabel descLabel = new JLabel(description);
        descLabel.setForeground(Color.DARK_GRAY);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        // Button panel
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(card.getBackground());

        JButton actionButton = new JButton("Accéder");
        actionButton.setForeground(Color.WHITE);
        actionButton.setBackground(color);
        actionButton.setFocusPainted(false);
        actionButton.setBorderPainted(false);
        actionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        actionButton.addActionListener(listener);

        actionButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                actionButton.setBackground(color.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                actionButton.setBackground(color);
            }
        });

        buttonPanel.add(actionButton, BorderLayout.EAST);

        card.add(titlePanel, BorderLayout.NORTH);
        card.add(descLabel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

// Add hover effect to the entire card
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(color.brighter(), 2),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(color, 2),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
            }
        });

        return card;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Êtes-vous sûr de vouloir vous déconnecter?",
                "Confirmation de déconnexion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            // Code pour retourner à l'écran de connexion
            new Login().setVisible(true);
        }
    }

    private void setupTimeUpdater() {
        updateDateTime();
        timer = new Timer(60000, e -> updateDateTime());
        timer.start();
    }

    private void updateDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMMM yyyy - HH:mm", java.util.Locale.FRENCH);
        dateTimeLabel.setText(sdf.format(new Date()));
    }
    private void showClientsPanel() {
        resetButtonColors();
        clientsBtn.setBackground(buttonHoverColor);
        activeSectionLabel.setText("Gestion des Clients");
        cardLayout.show(contentPanel, "Clients");
    }

    private void showArticlesPanel() {
        resetButtonColors();
        articlesBtn.setBackground(buttonHoverColor);
        activeSectionLabel.setText("Gestion des Articles");
        cardLayout.show(contentPanel, "Articles");
    }

    private void showTypesArticlesPanel() {
        resetButtonColors();
        typesArticlesBtn.setBackground(buttonHoverColor);
        activeSectionLabel.setText("Gestion des Types d'Articles");
        cardLayout.show(contentPanel, "TypesArticles");
    }

    private void showCommandesPanel() {
        resetButtonColors();
        commandesBtn.setBackground(buttonHoverColor);
        activeSectionLabel.setText("Gestion des Commandes");
        cardLayout.show(contentPanel, "Commandes");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AccueilGerant frame = new AccueilGerant();
            frame.setVisible(true);
        });
    }

// Méthodes pour obtenir les données réelles (à implémenter avec votre DAO)
/*
private int getActiveClientsCount() {
    // Implémentation DAO
}

private int getMenuItemsCount() {
    // Implémentation DAO
}

private int getPendingOrdersCount() {
    // Implémentation DAO
}

private double getDailyRevenue() {
    // Implémentation DAO
}

private double getTotalRevenue() {
    // Implémentation DAO
}
*/
}
