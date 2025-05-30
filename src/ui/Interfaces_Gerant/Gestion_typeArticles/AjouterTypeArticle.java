package ui.Interfaces_Gerant.Gestion_typeArticles;

import dao.TypeArticleDAO;
import model.TypeArticle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AjouterTypeArticle extends JFrame {
    // Couleurs cohérentes avec le design global
    private final Color primaryColor = new Color(0, 128, 128);
    private final Color accentColor = new Color(41, 128, 185);
    private final Color successColor = new Color(46, 204, 113);
    private final Color dangerColor = new Color(231, 76, 60);
    private final Color cardBackground = Color.WHITE;
    private final Color borderColor = new Color(230, 230, 230);

    private JTextField champNom;
    private JButton btnAjouter;
    private JButton btnAnnuler;

    public AjouterTypeArticle() {
        setTitle("Ajouter un Type d'Article - RestaurantApp");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setIconImage(new ImageIcon(getClass().getResource("/ressources/cafe_logo.png")).getImage());

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        setContentPane(mainPanel);

        // Panel d'en-tête
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Panel central avec formulaire
        JPanel centerPanel = createFormPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 70));
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel titleLabel = new JLabel("📝 Ajouter un Type d'Article");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Créer une nouvelle catégorie de produits");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(200, 255, 255));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(primaryColor);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.setBackground(new Color(245, 245, 245));
        formContainer.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Carte du formulaire
        JPanel formCard = new JPanel();
        formCard.setLayout(new BorderLayout(0, 20));
        formCard.setBackground(cardBackground);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                new EmptyBorder(40, 30, 40, 30)
        ));

        // Section du champ de saisie
        JPanel inputSection = new JPanel(new BorderLayout(0, 10));
        inputSection.setBackground(cardBackground);

        JLabel labelNom = new JLabel("Nom du type d'article :");
        labelNom.setFont(new Font("Arial", Font.BOLD, 16));
        labelNom.setForeground(new Color(60, 60, 60));

        champNom = new JTextField();
        champNom.setPreferredSize(new Dimension(0, 40));
        champNom.setFont(new Font("Arial", Font.PLAIN, 14));
        champNom.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));

        // Effet focus sur le champ de texte
        champNom.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                champNom.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(accentColor, 2),
                        new EmptyBorder(7, 11, 7, 11)
                ));
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                champNom.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(borderColor, 1),
                        new EmptyBorder(8, 12, 8, 12)
                ));
            }
        });

        inputSection.add(labelNom, BorderLayout.NORTH);
        inputSection.add(champNom, BorderLayout.CENTER);

        // Section des boutons
        JPanel buttonSection = createButtonPanel();

        // Assemblage du formulaire
        formCard.add(inputSection, BorderLayout.CENTER);
        formCard.add(buttonSection, BorderLayout.SOUTH);

        formContainer.add(formCard, BorderLayout.CENTER);

        return formContainer;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(cardBackground);

        btnAnnuler = createStyledButton("Annuler", dangerColor);
        btnAjouter = createStyledButton("Ajouter", successColor);

        // Actions des boutons
        btnAnnuler.addActionListener(e -> dispose());

        btnAjouter.addActionListener(e -> {
            String nom = champNom.getText().trim();
            if (!nom.isEmpty()) {
                try {
                    TypeArticle type = new TypeArticle(0, nom);
                    new TypeArticleDAO().ajouterType(type);

                    // Message de succès
                    JOptionPane.showMessageDialog(this,
                            "Type d'article '" + nom + "' ajouté avec succès !",
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE);

                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Erreur lors de l'ajout du type d'article.",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Veuillez saisir un nom pour le type d'article.",
                        "Champ requis",
                        JOptionPane.WARNING_MESSAGE);
                champNom.requestFocus();
            }
        });

        buttonPanel.add(btnAnnuler);
        buttonPanel.add(btnAjouter);

        return buttonPanel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(120, 40));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Effet hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new AjouterTypeArticle().setVisible(true);
        });
    }
}