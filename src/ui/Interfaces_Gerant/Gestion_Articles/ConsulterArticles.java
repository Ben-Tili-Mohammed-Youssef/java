package ui.Interfaces_Gerant.Gestion_Articles;

import dao.ArticleDAO;
import model.Article;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ConsulterArticles extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public ConsulterArticles() {
        setTitle("Carte des Articles");
        setSize(800, 400);
        setLocationRelativeTo(null);

        model = new DefaultTableModel(new String[]{"ID", "Nom", "Description", "Prix", "Type", "Image"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        chargerArticles();
    }

    private void chargerArticles() {
        for (Article a : new ArticleDAO().listerArticles()) {
            model.addRow(new Object[]{
                    a.getId(),
                    a.getNom(),
                    a.getDescription(),
                    a.getPrix(),
                    a.getType().getNom(),
                    a.getImagePath()
            });
        }
    }
}
