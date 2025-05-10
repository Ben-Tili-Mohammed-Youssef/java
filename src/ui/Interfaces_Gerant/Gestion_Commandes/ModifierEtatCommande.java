package ui.Interfaces_Gerant.Gestion_Commandes;

import dao.CommandeDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class ModifierEtatCommande extends JFrame {
    public ModifierEtatCommande() {
        setTitle("Modifier l'État d'une Commande");
        setSize(350, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 1));
        JTextField champId = new JTextField();
        JComboBox<String> comboEtat = new JComboBox<>(new String[]{
                "non traitée", "préparation", "prête", "en route"
        });
        JButton btnModifier = new JButton("Modifier");

        panel.add(new JLabel("ID de la commande :"));
        panel.add(champId);
        panel.add(new JLabel("Nouvel état :"));
        panel.add(comboEtat);
        panel.add(btnModifier);
        add(panel);

        btnModifier.addActionListener(e -> {
            String idText = champId.getText().trim();
            String nouvelEtat = (String) comboEtat.getSelectedItem();

            if (!idText.isEmpty() && nouvelEtat != null) {
                int idCommande = Integer.parseInt(idText);
                CommandeDAO commandeDAO = new CommandeDAO();
                try {
                    commandeDAO.updateEtatCommande(idCommande, nouvelEtat);
                    JOptionPane.showMessageDialog(this, "État modifié !");
                    dispose();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erreur lors de la modification !");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Champs requis.");
            }
        });
    }
}
