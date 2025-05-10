package ui.Interfaces_Gerant.Gestion_Articles;

import dao.ArticleDAO;
import model.Article;

import javax.swing.*;
import java.awt.*;

public class SupprimerArticle extends JFrame {
    private JComboBox<Article> articleCombo;
    private JButton supprimerBtn;

    public SupprimerArticle() {
        setTitle("Supprimer un article");
        setSize(400, 150);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(2, 2));

        articleCombo = new JComboBox<>();
        supprimerBtn = new JButton("Supprimer");

        add(new JLabel("Sélectionnez un article :"));
        add(articleCombo);
        add(new JLabel(""));
        add(supprimerBtn);

        remplirArticles();

        supprimerBtn.addActionListener(e -> supprimerArticle());
    }

    private void remplirArticles() {
        for (Article a : new ArticleDAO().listerArticles()) {
            articleCombo.addItem(a);
        }
    }

    private void supprimerArticle() {
        Article a = (Article) articleCombo.getSelectedItem();
        if (a == null) {
            JOptionPane.showMessageDialog(this, "Aucun article sélectionné !");
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(this, "Confirmer la suppression ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            new ArticleDAO().supprimerArticle(a.getId());
            JOptionPane.showMessageDialog(this, "Article supprimé avec succès !");
            dispose();
        }
    }
}
