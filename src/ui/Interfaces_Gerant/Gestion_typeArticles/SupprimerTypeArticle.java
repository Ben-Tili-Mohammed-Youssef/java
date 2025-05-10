package ui.Interfaces_Gerant.Gestion_typeArticles;

import dao.TypeArticleDAO;
import model.TypeArticle;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SupprimerTypeArticle extends JFrame {
    private JComboBox<TypeArticle> comboBoxTypes;
    private JButton btnSupprimer;

    public SupprimerTypeArticle() {
        setTitle("Supprimer un Type d'Article");
        setSize(400, 150);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        comboBoxTypes = new JComboBox<>();
        btnSupprimer = new JButton("Supprimer");

        panel.add(comboBoxTypes, BorderLayout.CENTER);
        panel.add(btnSupprimer, BorderLayout.SOUTH);
        add(panel);

        remplirComboBox();

        btnSupprimer.addActionListener(e -> {
            TypeArticle selected = (TypeArticle) comboBoxTypes.getSelectedItem();
            if (selected != null) {
                new TypeArticleDAO().supprimerType(selected.getId());
                JOptionPane.showMessageDialog(this, "Type supprimé !");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un type à supprimer.");
            }
        });
    }

    private void remplirComboBox() {
        List<TypeArticle> types = new TypeArticleDAO().listerTousLesTypes();
        for (TypeArticle type : types) {
            comboBoxTypes.addItem(type);
        }
    }
}
