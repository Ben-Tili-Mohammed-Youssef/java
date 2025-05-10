package ui.Interfaces_Client;

import dao.ArticleDAO;
import model.Article;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ConsulterCarte extends JFrame {

    public ConsulterCarte() {
        setTitle("Carte du Menu");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panelContenu = new JPanel();
        panelContenu.setLayout(new BoxLayout(panelContenu, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(panelContenu);
        add(scrollPane, BorderLayout.CENTER);

        try {
            ArticleDAO articleDAO = new ArticleDAO();
            List<Article> articles = articleDAO.listerArticles();

            // ✅ Regrouper les articles par type
            Map<String, List<Article>> articlesParType = new HashMap<>();
            for (Article article : articles) {
                String typeNom = article.getType().getNom();
                articlesParType.computeIfAbsent(typeNom, k -> new ArrayList<>()).add(article);
            }

            // ✅ Afficher les types et leurs articles
            for (String typeNom : articlesParType.keySet()) {
                JLabel lblType = new JLabel("➤ " + typeNom);
                lblType.setFont(new Font("Arial", Font.BOLD, 18));
                lblType.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
                panelContenu.add(lblType);

                for (Article article : articlesParType.get(typeNom)) {
                    JPanel articlePanel = new JPanel(new BorderLayout());
                    articlePanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                    articlePanel.setBackground(new Color(245, 245, 245));
                    articlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

                    JLabel lblNom = new JLabel(article.getNom() + " - " + article.getPrix() + " DT");
                    lblNom.setFont(new Font("Arial", Font.BOLD, 14));

                    JTextArea txtDescription = new JTextArea(
                            article.getDescription() != null ? article.getDescription() : ""
                    );
                    txtDescription.setWrapStyleWord(true);
                    txtDescription.setLineWrap(true);
                    txtDescription.setEditable(false);
                    txtDescription.setBackground(new Color(245, 245, 245));
                    txtDescription.setFont(new Font("Arial", Font.PLAIN, 12));

                    articlePanel.add(lblNom, BorderLayout.NORTH);
                    articlePanel.add(txtDescription, BorderLayout.CENTER);

                    panelContenu.add(articlePanel);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement de la carte du menu.");
        }
    }
}
