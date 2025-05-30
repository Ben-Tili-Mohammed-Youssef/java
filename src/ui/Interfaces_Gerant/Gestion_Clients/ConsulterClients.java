package ui.Interfaces_Gerant.Gestion_Clients;

import dao.ClientDAO;
import model.Client;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ConsulterClients extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private List<Client> clients;
    private ClientDAO clientDAO;
    private JTextField searchField;

    // Couleurs modernes cohérentes avec AccueilGerant
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

    public ConsulterClients() {
        setupLookAndFeel();
        initializeComponents();
        setupUI();
        chargerClients();
        resizeColumnWidth(table);
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
        setTitle("Gestion des Clients - Café Shop");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Essayer de charger l'icône
        try {
            setIconImage(new ImageIcon(getClass().getResource("/ressources/cafe_logo.png")).getImage());
        } catch (Exception e) {
            // Ignorer si l'icône n'existe pas
        }

        clientDAO = new ClientDAO();
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

        // Carte contenant la table
        JPanel tableCard = createTableCard();
        centerPanel.add(tableCard, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
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
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);

        JLabel iconLabel = new JLabel("👥");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.setBorder(new EmptyBorder(0, 15, 0, 0));

        JLabel titleLabel = new JLabel("Liste des Clients");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_BLUE);

        JLabel subtitleLabel = new JLabel("Gérer et consulter tous les clients");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);

        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        leftPanel.add(iconLabel);
        leftPanel.add(titlePanel);

        // Panel de recherche moderne
        JPanel searchPanel = createModernSearchPanel();

        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createModernSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        searchPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("🔍");
        searchLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(250, 35));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                String searchTerm = searchField.getText().trim().toLowerCase();
                if (searchTerm.isEmpty()) {
                    chargerClients();
                } else {
                    filterClients(searchTerm);
                }
            }
        });

        JButton clearButton = createIconButton("❌", "Effacer", TEXT_SECONDARY);
        clearButton.addActionListener(e -> {
            searchField.setText("");
            chargerClients();
        });

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(clearButton);

        return searchPanel;
    }

    private JPanel createTableCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(25, 25, 25, 25)
        ));

        // Titre de la carte
        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setOpaque(false);
        cardHeader.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel cardTitle = new JLabel("📋 Données des Clients");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        cardTitle.setForeground(TEXT_PRIMARY);

        JLabel clientCount = new JLabel("0 clients");
        clientCount.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        clientCount.setForeground(TEXT_SECONDARY);

        cardHeader.add(cardTitle, BorderLayout.WEST);
        cardHeader.add(clientCount, BorderLayout.EAST);

        // Table moderne
        createModernTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        scrollPane.getViewport().setBackground(WHITE);

        // Panel des boutons d'action
        JPanel buttonPanel = createModernButtonPanel();

        card.add(cardHeader, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

        return card;
    }

    private void createModernTable() {
        // Modèle de table non-éditable
        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Colonnes avec en-têtes modernes
        model.setColumnIdentifiers(new String[]{
                "👤 Nom", "👤 Prénom", "📧 Email", "🎂 Date de naissance", "🏠 Adresse", "📞 Téléphone"
        });

        table = new JTable(model);
        table.setRowHeight(45);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(240, 240, 240));
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(new Color(230, 247, 255));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Header de table moderne
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(PRIMARY_BLUE);
        table.getTableHeader().setForeground(WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 50));
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder());

        // Renderer personnalisé pour les cellules
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(WHITE);
                    } else {
                        c.setBackground(new Color(250, 250, 250));
                    }
                }

                setBorder(new EmptyBorder(8, 12, 8, 12));
                return c;
            }
        };

        // Appliquer le renderer à toutes les colonnes
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
    }

    private JPanel createModernButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        buttonPanel.setOpaque(false);

        JButton refreshButton = createModernButton("🔄 Actualiser", PRIMARY_BLUE);
        JButton addButton = createModernButton("➕ Ajouter", SUCCESS_GREEN);
        JButton editButton = createModernButton("✏️ Modifier", WARNING_ORANGE);
        JButton deleteButton = createModernButton("🗑️ Supprimer", DANGER_RED);

        // Actions des boutons
        refreshButton.addActionListener(e -> {
            chargerClients();
            searchField.setText("");
        });

        addButton.addActionListener(e -> {
            new AjouterClient().setVisible(true);
            // Actualiser après ajout (simulation)
            Timer timer = new Timer(1000, evt -> chargerClients());
            timer.setRepeats(false);
            timer.start();
        });

        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String email = (String) table.getValueAt(selectedRow, 2);
                // Enlever l'icône de l'email pour l'utilisation
                email = email.replace("📧 ", "");

                ModifierClient modifierClient = new ModifierClient();
                modifierClient.preremplirEmail(email);
                modifierClient.setVisible(true);

                modifierClient.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent e) {
                        chargerClients();
                    }
                });
            } else {
                showModernMessage("⚠️ Sélection requise",
                        "Veuillez sélectionner un client à modifier.",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String email = (String) table.getValueAt(selectedRow, 2);
                email = email.replace("📧 ", ""); // Nettoyer l'email

                SupprimerClient supprimerClient = new SupprimerClient();
                supprimerClient.preremplirEmail(email); // Pré-remplir l'email
                supprimerClient.setVisible(true);

                // Rafraîchir après fermeture
                supprimerClient.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent e) {
                        chargerClients();
                    }
                });
            } else {
                showModernMessage("⚠️ Sélection requise",
                        "Veuillez sélectionner un client à supprimer.",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        return buttonPanel;
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
                button.setBackground(bgColor.brighter());
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(bgColor.darker(), 1),
                        new EmptyBorder(7, 15, 7, 15)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
                button.setBorder(new EmptyBorder(8, 16, 8, 16));
            }
        });

        return button;
    }

    private JButton createIconButton(String icon, String tooltip, Color color) {
        JButton button = new JButton(icon);
        button.setPreferredSize(new Dimension(35, 35));
        button.setBackground(WHITE);
        button.setForeground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText(tooltip);
        button.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

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

        return button;
    }

    private void filterClients(String searchTerm) {
        model.setRowCount(0);
        int count = 0;

        for (Client client : clients) {
            if (client.getNom().toLowerCase().contains(searchTerm) ||
                    client.getPrenom().toLowerCase().contains(searchTerm) ||
                    client.getEmail().toLowerCase().contains(searchTerm) ||
                    client.getTelephone().toLowerCase().contains(searchTerm)) {

                model.addRow(new Object[]{
                        client.getNom(),
                        client.getPrenom(),
                        client.getEmail(),
                        client.getDateNaissance(),
                        client.getAdresse(),
                        client.getTelephone()
                });
                count++;
            }
        }

        updateClientCount(count);
    }

    private void chargerClients() {
        model.setRowCount(0);
        clients = clientDAO.listerTousLesClients();

        for (Client client : clients) {
            model.addRow(new Object[]{
                    client.getNom(),
                    client.getPrenom(),
                    client.getEmail(),
                    client.getDateNaissance(),
                    client.getAdresse(),
                    client.getTelephone()
            });
        }

        updateClientCount(clients.size());
    }

    private void updateClientCount(int count) {
        // Trouver le label de comptage et le mettre à jour
        SwingUtilities.invokeLater(() -> {
            Component[] components = ((JPanel) getContentPane()).getComponents();
            for (Component comp : components) {
                if (comp instanceof JPanel) {
                    updateCountRecursively((JPanel) comp, count);
                }
            }
        });
    }

    private void updateCountRecursively(JPanel panel, int count) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                if (label.getText().contains("clients")) {
                    label.setText(count + " client" + (count > 1 ? "s" : ""));
                    return;
                }
            } else if (comp instanceof JPanel) {
                updateCountRecursively((JPanel) comp, count);
            }
        }
    }

    private void resizeColumnWidth(JTable table) {
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 50; // Largeur minimum

            // Calculer la largeur basée sur le header
            TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
            Component headerComp = headerRenderer.getTableCellRendererComponent(
                    table, table.getColumnModel().getColumn(column).getHeaderValue(),
                    false, false, 0, column);
            width = Math.max(width, headerComp.getPreferredSize().width + 20);

            // Calculer la largeur basée sur le contenu
            for (int row = 0; row < Math.min(table.getRowCount(), 10); row++) { // Limiter à 10 rows pour la performance
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width + 20, width);
            }

            // Largeur maximum pour éviter des colonnes trop larges
            width = Math.min(width, 250);
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }

    private void showModernMessage(String title, String message, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    private int showModernConfirmDialog(String title, String message, String yesText, String noText) {
        return JOptionPane.showConfirmDialog(
                this, message, title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
    }

    // Pour les tests
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ConsulterClients().setVisible(true);
        });
    }
}