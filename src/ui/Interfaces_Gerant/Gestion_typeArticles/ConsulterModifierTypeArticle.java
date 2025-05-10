package ui.Interfaces_Gerant.Gestion_typeArticles;

import dao.TypeArticleDAO;
import model.TypeArticle;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ConsulterModifierTypeArticle extends JFrame {
    private JComboBox<TypeArticle> comboBoxTypes;
    private JTextField champNom;
    private JButton btnModifier;

    public ConsulterModifierTypeArticle() {
        setTitle("Consulter / Modifier Type d'Article");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 1));
        comboBoxTypes = new JComboBox<>();
        champNom = new JTextField();
        btnModifier = new JButton("Modifier");

        panel.add(comboBoxTypes);
        panel.add(champNom);
        panel.add(btnModifier);
        add(panel);

        remplirComboBox();

        comboBoxTypes.addActionListener(e -> {
            TypeArticle selected = (TypeArticle) comboBoxTypes.getSelectedItem();
            if (selected != null) {
                champNom.setText(selected.getNom());
            }
        });

        btnModifier.addActionListener(e -> {
            TypeArticle selected = (TypeArticle) comboBoxTypes.getSelectedItem();
            String nouveauNom = champNom.getText().trim();
            if (selected != null && !nouveauNom.isEmpty()) {
                new TypeArticleDAO().modifierType(selected.getId(), nouveauNom);
                JOptionPane.showMessageDialog(this, "Type modifié !");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un type et entrer un nom valide.");
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