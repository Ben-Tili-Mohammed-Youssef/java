package ui.Interfaces_Gerant.Gestion_typeArticles;

import dao.TypeArticleDAO;
import model.TypeArticle;

import javax.swing.*;
import java.awt.*;

public class AjouterTypeArticle extends JFrame {
    public AjouterTypeArticle() {
        setTitle("Ajouter un Type d'Article");
        setSize(300, 150);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        JTextField champNom = new JTextField();
        JButton btnAjouter = new JButton("Ajouter");

        panel.add(new JLabel("Nom du type :"), BorderLayout.NORTH);
        panel.add(champNom, BorderLayout.CENTER);
        panel.add(btnAjouter, BorderLayout.SOUTH);
        add(panel);

        btnAjouter.addActionListener(e -> {
            String nom = champNom.getText().trim();
            if (!nom.isEmpty()) {
                TypeArticle type = new TypeArticle(0, nom);
                new TypeArticleDAO().ajouterType(type);
                JOptionPane.showMessageDialog(this, "Type ajouté !");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Nom requis.");
            }
        });
    }
}