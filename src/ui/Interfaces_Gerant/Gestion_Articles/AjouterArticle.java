package ui.Interfaces_Gerant.Gestion_Articles;

import dao.ArticleDAO;
import dao.TypeArticleDAO;
import model.Article;
import model.TypeArticle;

import javax.swing.*;
import java.awt.*;

public class AjouterArticle extends JFrame {
    private JTextField nomField;
    private JTextField prixField;
    private JTextArea descriptionArea;
    private JTextField imagePathField;
    private JComboBox<TypeArticle> typeCombo;
    private JButton ajouterBtn;

    public AjouterArticle() {
        setTitle("Ajouter un article");
        setSize(400, 500);
        setLayout(new GridLayout(7, 2));
        setLocationRelativeTo(null);

        nomField = new JTextField();
        prixField = new JTextField();
        descriptionArea = new JTextArea();
        imagePathField = new JTextField();
        typeCombo = new JComboBox<>();
        ajouterBtn = new JButton("Ajouter");

        add(new JLabel("Nom :"));
        add(nomField);
        add(new JLabel("Prix :"));
        add(prixField);
        add(new JLabel("Description :"));
        add(new JScrollPane(descriptionArea));
        add(new JLabel("Image Path :"));
        add(imagePathField);
        add(new JLabel("Type :"));
        add(typeCombo);
        add(new JLabel(""));
        add(ajouterBtn);

        remplirTypes();

        ajouterBtn.addActionListener(e -> ajouterArticle());
    }

    private void remplirTypes() {
        TypeArticleDAO typeDao = new TypeArticleDAO();
        for (TypeArticle type : typeDao.listerTousLesTypes()) {
            typeCombo.addItem(type);
        }
    }

    private void ajouterArticle() {
        String nom = nomField.getText();
        String prixStr = prixField.getText();
        String description = descriptionArea.getText();
        String imagePath = imagePathField.getText();
        TypeArticle type = (TypeArticle) typeCombo.getSelectedItem();

        if (nom.isEmpty() || prixStr.isEmpty() || type == null) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs obligatoires");
            return;
        }

        try {
            double prix = Double.parseDouble(prixStr);
            Article article = new Article(0, nom, description, prix, type, imagePath);
            new ArticleDAO().ajouterArticle(article);
            JOptionPane.showMessageDialog(this, "Article ajouté avec succès");
            dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Prix invalide");
        }
    }
}
