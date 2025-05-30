package ui.Interfaces_Client.Gestion_Commandes;

import dao.CommandeDAO;
import dao.DetailsCommandeDAO;
import database.DatabaseConnection;
import model.Commande;
import model.DetailsCommande;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class SuivreCommande extends JFrame {
    // Modern color scheme - même palette que AccueilClient et ModifierCommande
    private static final Color PRIMARY_GREEN = new Color(21, 111, 98);
    private static final Color LIGHT_GREEN = new Color(129, 199, 132);
    private static final Color ACCENT_GREEN = new Color(76, 175, 80);
    private static final Color WHITE = Color.WHITE;
    private static final Color LIGHT_GRAY = new Color(248, 249, 250);
    private static final Color CARD_BACKGROUND = new Color(255, 255, 255);
    private static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    private static final Color TEXT_SECONDARY = new Color(108, 117, 125);
    private static final Color SUCCESS_GREEN = new Color(40, 167, 69);
    private static final Color DANGER_RED = new Color(220, 53, 69);
    private static final Color WARNING_ORANGE = new Color(255, 193, 7);
    private static final Color INFO_BLUE = new Color(23, 162, 184);

    private final int clientId;
    private JTable commandesTable;
    private DefaultTableModel tableModel;
    private JPanel detailsPanel;
    private JPanel mainContentPanel;
    private JPanel headerPanel;
    private final Map<String, Color> statusColors = new HashMap<>();

    public SuivreCommande(int clientId) {
        this.clientId = clientId;
        initializeStatusColors();
        setupLookAndFeel();
        initializeModernUI();
        loadCommandes();
    }

    private void initializeStatusColors() {
        statusColors.put("non traitée", WARNING_ORANGE);
        statusColors.put("en préparation", INFO_BLUE);
        statusColors.put("prête", SUCCESS_GREEN);
        statusColors.put("livrée", ACCENT_GREEN);
        statusColors.put("annulée", DANGER_RED);
    }

    private void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 12);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeModernUI() {
        setTitle("Suivre mes Commandes - Café Shop");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LIGHT_GRAY);
        setContentPane(mainPanel);

        // Create modern header
        createModernHeader();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Create main content
        createMainContent();
        mainPanel.add(mainContentPanel, BorderLayout.CENTER);
    }

    private void createModernHeader() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                new EmptyBorder(20, 30, 20, 30)
        ));

        // Left side - Title and subtitle
        JPanel leftHeader = new JPanel();
        leftHeader.setLayout(new BoxLayout(leftHeader, BoxLayout.Y_AXIS));
        leftHeader.setOpaque(false);

        JLabel titleLabel = new JLabel("📍 Suivre mes Commandes");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_GREEN);

        JLabel subtitleLabel = new JLabel("Visualisez l'état de vos commandes et gérez-les facilement");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setBorder(new EmptyBorder(5, 0, 0, 0));

        leftHeader.add(titleLabel);
        leftHeader.add(subtitleLabel);

        // Right side - Actions
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightHeader.setOpaque(false);

        JButton refreshButton = createModernButton("🔄 Actualiser", ACCENT_GREEN, WHITE);
        refreshButton.addActionListener(e -> {
            loadCommandes();
            showSuccessMessage("Liste des commandes actualisée !");
        });

        JButton closeButton = createModernButton("✕ Fermer", DANGER_RED, WHITE);
        closeButton.addActionListener(e -> dispose());

        rightHeader.add(refreshButton);
        rightHeader.add(closeButton);

        headerPanel.add(leftHeader, BorderLayout.WEST);
        headerPanel.add(rightHeader, BorderLayout.EAST);
    }

    private void createMainContent() {
        mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(LIGHT_GRAY);
        mainContentPanel.setBorder(new EmptyBorder(25, 30, 30, 30));

        // Create split pane for table and details
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(600);
        splitPane.setResizeWeight(0.6);
        splitPane.setBorder(null);

        // Left panel - Commands table
        JPanel tablePanel = createTablePanel();
        splitPane.setLeftComponent(tablePanel);

        // Right panel - Command details
        JPanel detailsContainer = createDetailsPanel();
        splitPane.setRightComponent(detailsContainer);

        mainContentPanel.add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(25, 25, 25, 25)
        ));

        JLabel tableTitle = new JLabel("📋 Mes Commandes");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(PRIMARY_GREEN);
        tableTitle.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Create table
        String[] columnNames = {"#", "Date", "Mode", "État", "Total", "Actions"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only actions column is editable
            }
        };

        commandesTable = new JTable(tableModel);
        commandesTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        commandesTable.setRowHeight(50);
        commandesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        commandesTable.setShowGrid(false);
        commandesTable.setIntercellSpacing(new Dimension(0, 0));

        // Custom cell renderer for status
        commandesTable.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());

        // Custom cell renderer for actions
        commandesTable.getColumnModel().getColumn(5).setCellRenderer(new ActionsCellRenderer());
        commandesTable.getColumnModel().getColumn(5).setCellEditor(new ActionsCellEditor());

        // Column widths
        commandesTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        commandesTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        commandesTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        commandesTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        commandesTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        commandesTable.getColumnModel().getColumn(5).setPreferredWidth(130);

        // Selection listener
        commandesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = commandesTable.getSelectedRow();
                if (selectedRow != -1) {
                    int commandeId = (Integer) tableModel.getValueAt(selectedRow, 0);
                    loadCommandeDetails(commandeId);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(commandesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        scrollPane.getViewport().setBackground(WHITE);

        panel.add(tableTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDetailsPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(CARD_BACKGROUND);
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(25, 25, 25, 25)
        ));

        JLabel detailsTitle = new JLabel("📋 Détails de la Commande");
        detailsTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        detailsTitle.setForeground(PRIMARY_GREEN);
        detailsTitle.setBorder(new EmptyBorder(0, 0, 20, 0));

        detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);

        // Initial message
        JLabel selectLabel = new JLabel("Sélectionnez une commande pour voir les détails");
        selectLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        selectLabel.setForeground(TEXT_SECONDARY);
        selectLabel.setHorizontalAlignment(SwingConstants.CENTER);
        detailsPanel.add(selectLabel);

        JScrollPane detailsScroll = new JScrollPane(detailsPanel);
        detailsScroll.setBorder(null);
        detailsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        detailsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        container.add(detailsTitle, BorderLayout.NORTH);
        container.add(detailsScroll, BorderLayout.CENTER);

        return container;
    }

    private void loadCommandes() {
        try {
            tableModel.setRowCount(0);
            detailsPanel.removeAll();

            JLabel selectLabel = new JLabel("Sélectionnez une commande pour voir les détails");
            selectLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            selectLabel.setForeground(TEXT_SECONDARY);
            selectLabel.setHorizontalAlignment(SwingConstants.CENTER);
            detailsPanel.add(selectLabel);
            detailsPanel.revalidate();
            detailsPanel.repaint();

            CommandeDAO commandeDAO = new CommandeDAO();
            List<Commande> commandes = commandeDAO.getCommandesClient(clientId);

            if (commandes.isEmpty()) {
                Object[] emptyRow = {"", "", "", "Aucune commande trouvée", "", ""};
                tableModel.addRow(emptyRow);
                return;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (Commande commande : commandes) {
                Object[] row = {
                        commande.getId(),
                        commande.getDateCommande().toLocalDateTime().format(formatter),
                        commande.getModeRecuperation(),
                        commande.getEtat(),
                        "Actions" // Placeholder for actions column
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            handleDatabaseError("Erreur lors du chargement des commandes", e);
        }
    }

    private void loadCommandeDetails(int commandeId) {
        detailsPanel.removeAll();

        try {
            CommandeDAO commandeDAO = new CommandeDAO();
            Commande commande = commandeDAO.getCommandeById(commandeId);

            if (commande == null) {
                showErrorMessage("Commande introuvable");
                return;
            }

            // Command info card
            JPanel infoCard = createCommandeInfoCard(commande);
            detailsPanel.add(infoCard);
            detailsPanel.add(Box.createRigidArea(new Dimension(0, 15)));

            // Articles list
            DetailsCommandeDAO detailsDAO = new DetailsCommandeDAO();
            List<DetailsCommande> details = detailsDAO.getDetailsCommande(commandeId);

            if (!details.isEmpty()) {
                JPanel articlesCard = createArticlesCard(details);
                detailsPanel.add(articlesCard);
            }

        } catch (SQLException e) {
            handleDatabaseError("Erreur lors du chargement des détails", e);
        }

        detailsPanel.revalidate();
        detailsPanel.repaint();
    }

    private JPanel createCommandeInfoCard(Commande commande) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // Status badge
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusPanel.setOpaque(false);

        JLabel statusBadge = new JLabel("● " + commande.getEtat().toUpperCase());
        statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusBadge.setForeground(statusColors.getOrDefault(commande.getEtat(), TEXT_SECONDARY));
        statusPanel.add(statusBadge);

        card.add(statusPanel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));

        // Command details
        addInfoRow(card, "📅 Date", commande.getDateCommande().toLocalDateTime().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")));
        addInfoRow(card, "🚚 Mode", commande.getModeRecuperation());

        if (commande.getAdresseLivraison() != null && !commande.getAdresseLivraison().isEmpty()) {
            addInfoRow(card, "📍 Adresse", commande.getAdresseLivraison());
        }

        return card;
    }

    private void addInfoRow(JPanel parent, String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(5, 0, 5, 0));

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelComp.setForeground(TEXT_SECONDARY);

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        valueComp.setForeground(TEXT_PRIMARY);

        row.add(labelComp, BorderLayout.WEST);
        row.add(valueComp, BorderLayout.EAST);

        parent.add(row);
    }

    private JPanel createArticlesCard(List<DetailsCommande> details) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel articlesTitle = new JLabel("🛒 Articles commandés");
        articlesTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        articlesTitle.setForeground(PRIMARY_GREEN);
        card.add(articlesTitle);
        card.add(Box.createRigidArea(new Dimension(0, 10)));

        for (DetailsCommande detail : details) {
            JPanel articleRow = new JPanel(new BorderLayout());
            articleRow.setOpaque(false);
            articleRow.setBorder(new EmptyBorder(8, 0, 8, 0));

            JLabel nameLabel = new JLabel(detail.getNomArticle());
            nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            nameLabel.setForeground(TEXT_PRIMARY);

            JLabel qtyPriceLabel = new JLabel(String.format("%d × %.2f €",
                    detail.getQuantite(), detail.getPrixUnitaire()));
            qtyPriceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            qtyPriceLabel.setForeground(TEXT_SECONDARY);

            articleRow.add(nameLabel, BorderLayout.WEST);
            articleRow.add(qtyPriceLabel, BorderLayout.EAST);

            card.add(articleRow);
        }

        return card;
    }

    // Custom cell renderer for status column
    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value != null) {
                String status = value.toString();
                setText("● " + status);
                setForeground(statusColors.getOrDefault(status, TEXT_SECONDARY));
                setFont(new Font("Segoe UI", Font.BOLD, 12));
            }

            return this;
        }
    }

    // Custom cell renderer for actions column
    private class ActionsCellRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            panel.setOpaque(false);

            // Get command status
            String status = (String) table.getValueAt(row, 3);
            boolean canModify = "non traitée".equals(status);

            if (canModify) {
                JButton modifyBtn = new JButton("✏️");
                modifyBtn.setToolTipText("Modifier");
                modifyBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                modifyBtn.setPreferredSize(new Dimension(30, 25));
                modifyBtn.setBackground(ACCENT_GREEN);
                modifyBtn.setForeground(WHITE);
                modifyBtn.setBorder(null);
                modifyBtn.setFocusPainted(false);

                JButton deleteBtn = new JButton("🗑️");
                deleteBtn.setToolTipText("Supprimer");
                deleteBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                deleteBtn.setPreferredSize(new Dimension(30, 25));
                deleteBtn.setBackground(DANGER_RED);
                deleteBtn.setForeground(WHITE);
                deleteBtn.setBorder(null);
                deleteBtn.setFocusPainted(false);

                panel.add(modifyBtn);
                panel.add(deleteBtn);
            } else {
                JLabel noActionsLabel = new JLabel("-");
                noActionsLabel.setForeground(TEXT_SECONDARY);
                panel.add(noActionsLabel);
            }

            return panel;
        }
    }

    // Custom cell editor for actions column
    private class ActionsCellEditor extends AbstractCellEditor implements javax.swing.table.TableCellEditor {
        private JPanel panel;
        private int currentRow;

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            panel.setOpaque(false);

            String status = (String) table.getValueAt(row, 3);
            boolean canModify = "non traitée".equals(status);

            if (canModify) {
                JButton modifyBtn = new JButton("✏️");
                modifyBtn.setToolTipText("Modifier");
                modifyBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                modifyBtn.setPreferredSize(new Dimension(30, 25));
                modifyBtn.setBackground(ACCENT_GREEN);
                modifyBtn.setForeground(WHITE);
                modifyBtn.setBorder(null);
                modifyBtn.setFocusPainted(false);
                modifyBtn.addActionListener(e -> {
                    stopCellEditing();
                    modifierCommande(row);
                });

                JButton deleteBtn = new JButton("🗑️");
                deleteBtn.setToolTipText("Supprimer");
                deleteBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                deleteBtn.setPreferredSize(new Dimension(30, 25));
                deleteBtn.setBackground(DANGER_RED);
                deleteBtn.setForeground(WHITE);
                deleteBtn.setBorder(null);
                deleteBtn.setFocusPainted(false);
                deleteBtn.addActionListener(e -> {
                    stopCellEditing();
                    supprimerCommande(row);
                });

                panel.add(modifyBtn);
                panel.add(deleteBtn);
            }

            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "Actions";
        }
    }

    private void modifierCommande(int row) {
        int commandeId = (Integer) tableModel.getValueAt(row, 0);
        try {
            new ModifierCommande(clientId).setVisible(true);
        } catch (Exception e) {
            showErrorMessage("Erreur lors de l'ouverture de l'interface de modification");
        }
    }

    private void supprimerCommande(int row) {
        int commandeId = (Integer) tableModel.getValueAt(row, 0);

        int result = JOptionPane.showConfirmDialog(
                this,
                "Êtes-vous sûr de vouloir supprimer cette commande ?\nCette action est irréversible.",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            try {
                DetailsCommandeDAO detailsDAO = new DetailsCommandeDAO();
                CommandeDAO commandeDAO = new CommandeDAO();

                boolean detailsDeleted = detailsDAO.supprimerDetailsCommande(commandeId);
                boolean commandeDeleted = commandeDAO.supprimerCommande(commandeId);

                if (commandeDeleted) {
                    showSuccessMessage("La commande a été supprimée avec succès.");
                    loadCommandes();
                } else {
                    showErrorMessage("Erreur lors de la suppression de la commande");
                }
            } catch (SQLException e) {
                handleDatabaseError("Erreur lors de la suppression", e);
            }
        }
    }

    private JButton createModernButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    private void handleDatabaseError(String message, SQLException e) {
        e.printStackTrace();
        showErrorMessage(message + ": " + e.getMessage());
    }
}