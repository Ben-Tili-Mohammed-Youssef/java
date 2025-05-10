package ui.Interfaces_Gerant.Gestion_Articles;

import dao.ArticleDAO;
import dao.TypeArticleDAO;
import model.Article;
import model.TypeArticle;

import javax.swing.*;
import java.awt.*;

public class ModifierArticle extends JFrame {
    private JComboBox<Article> articleCombo;
    private JTextField nomField, prixField;
    private JTextArea descriptionArea;
    private JComboBox<TypeArticle> typeCombo;
    private JTextField imagePathField;
    private JButton modifierBtn;

    public ModifierArticle() {
        setTitle("Modifier un article");
        setSize(450, 450);
        setLayout(new GridLayout(7, 2));
        setLocationRelativeTo(null);

        articleCombo = new JComboBox<>();
        nomField = new JTextField();
        prixField = new JTextField();
        descriptionArea = new JTextArea();
        typeCombo = new JComboBox<>();
        imagePathField = new JTextField();
        modifierBtn = new JButton("Modifier");

        add(new JLabel("Sélectionnez l'article :"));
        add(articleCombo);
        add(new JLabel("Nom :"));
        add(nomField);
        add(new JLabel("Prix :"));
        add(prixField);
        add(new JLabel("Description :"));
        add(new JScrollPane(descriptionArea));
        add(new JLabel("Type :"));
        add(typeCombo);
        add(new JLabel("Image (chemin) :"));
        add(imagePathField);
        add(new JLabel(""));
        add(modifierBtn);

        remplirArticles();
        remplirTypes();

        articleCombo.addActionListener(e -> chargerArticleSelectionne());
        modifierBtn.addActionListener(e -> modifierArticle());
    }

    private void remplirArticles() {
        for (Article a : new ArticleDAO().listerArticles()) {
            articleCombo.addItem(a);
        }
    }

    private void remplirTypes() {
        for (TypeArticle t : new TypeArticleDAO().listerTousLesTypes()) {
            typeCombo.addItem(t);
        }
    }

    private void chargerArticleSelectionne() {
        Article a = (Article) articleCombo.getSelectedItem();
        if (a != null) {
            nomField.setText(a.getNom());
            prixField.setText(String.valueOf(a.getPrix()));
            descriptionArea.setText(a.getDescription());
            imagePathField.setText(a.getImagePath());
            typeCombo.setSelectedItem(a.getType());
        }
    }

    private void modifierArticle() {
        Article a = (Article) articleCombo.getSelectedItem();
        if (a == null) return;

        String nom = nomField.getText();
        String prixStr = prixField.getText();
        String description = descriptionArea.getText();
        String imagePath = imagePathField.getText();
        TypeArticle type = (TypeArticle) typeCombo.getSelectedItem();

        try {
            double prix = Double.parseDouble(prixStr);
            Article updated = new Article(a.getId(), nom, description, prix, type, imagePath);
            new ArticleDAO().modifierArticle(updated);
            JOptionPane.showMessageDialog(this, "Article modifié avec succès !");
            dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Prix invalide !");
        }
    }
}
