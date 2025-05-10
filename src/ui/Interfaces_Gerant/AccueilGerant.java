package ui.Interfaces_Gerant;

import ui.Interfaces_Gerant.Gestion_Articles.AjouterArticle;
import ui.Interfaces_Gerant.Gestion_Articles.ConsulterArticles;
import ui.Interfaces_Gerant.Gestion_Articles.ModifierArticle;
import ui.Interfaces_Gerant.Gestion_Articles.SupprimerArticle;
import ui.Interfaces_Gerant.Gestion_Clients.AjouterClient;
import ui.Interfaces_Gerant.Gestion_Clients.ConsulterClients;
import ui.Interfaces_Gerant.Gestion_Clients.ModifierClient;
import ui.Interfaces_Gerant.Gestion_Clients.SupprimerClient;
import ui.Interfaces_Gerant.Gestion_typeArticles.AjouterTypeArticle;
import ui.Interfaces_Gerant.Gestion_typeArticles.ConsulterModifierTypeArticle;
import ui.Interfaces_Gerant.Gestion_typeArticles.SupprimerTypeArticle;
import ui.Interfaces_Gerant.Gestion_Commandes.AfficherCommandes;
import ui.Interfaces_Gerant.Gestion_Commandes.SupprimerCommande;
import ui.Interfaces_Gerant.Gestion_Commandes.ModifierEtatCommande;

import javax.swing.*;

public class AccueilGerant extends JFrame {
    private JPanel panel1;
    public AccueilGerant() {
        setTitle("Espace Gérant - RestaurantApp");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Création de la barre de menu
        JMenuBar menuBar = new JMenuBar();

        // Menu Clients
        JMenu menuClients = new JMenu("Clients");
        JMenuItem creerClient = new JMenuItem("Créer");
        JMenuItem consulterClient = new JMenuItem("Consulter");
        JMenuItem modifierClient = new JMenuItem("Modifier");
        JMenuItem supprimerClient = new JMenuItem("Supprimer");

        menuClients.add(creerClient);
        menuClients.add(consulterClient);
        menuClients.add(modifierClient);
        menuClients.add(supprimerClient);

        // Menu Articles
        JMenu menuArticles = new JMenu("Menu Articles");
        JMenuItem ajouterArticle = new JMenuItem("Ajouter");
        JMenuItem consulterArticle = new JMenuItem("Consulter");
        JMenuItem modifierArticle = new JMenuItem("Modifier");
        JMenuItem supprimerArticle = new JMenuItem("Supprimer");

        menuArticles.add(ajouterArticle);
        menuArticles.add(consulterArticle);
        menuArticles.add(modifierArticle);
        menuArticles.add(supprimerArticle);

        // Menu Commandes
        JMenu menuCommandes = new JMenu("Commandes");
        JMenuItem afficherCommandes = new JMenuItem("Afficher");
        JMenuItem supprimerCommande = new JMenuItem("Supprimer");
        JMenuItem modifierEtat = new JMenuItem("Modifier état");

        menuCommandes.add(afficherCommandes);
        menuCommandes.add(supprimerCommande);
        menuCommandes.add(modifierEtat);

        // Menu Types d'Articles
        JMenu menuTypesArticles = new JMenu("Types d'Articles");
        JMenuItem ajouterType = new JMenuItem("Ajouter");
        JMenuItem consulterModifierType = new JMenuItem("Consulter / Modifier");
        JMenuItem supprimerType = new JMenuItem("Supprimer");

        menuTypesArticles.add(ajouterType);
        menuTypesArticles.add(consulterModifierType);
        menuTypesArticles.add(supprimerType);

        // Ajout des menus à la barre
        menuBar.add(menuClients);
        menuBar.add(menuArticles);
        menuBar.add(menuTypesArticles);
        menuBar.add(menuCommandes);
        setJMenuBar(menuBar);

        // Action "Créer client"
        creerClient.addActionListener(e -> {
            new AjouterClient().setVisible(true);
        });

        consulterClient.addActionListener(e -> new ConsulterClients().setVisible(true));
        modifierClient.addActionListener(e -> new ModifierClient().setVisible(true));
        supprimerClient.addActionListener(e -> new SupprimerClient().setVisible(true));
        ajouterArticle.addActionListener(e -> new AjouterArticle().setVisible(true));
        consulterArticle.addActionListener(e -> new ConsulterArticles().setVisible(true));
        modifierArticle.addActionListener(e -> new ModifierArticle().setVisible(true));
        supprimerArticle.addActionListener(e -> new SupprimerArticle().setVisible(true));
        ajouterType.addActionListener(e -> new AjouterTypeArticle().setVisible(true));
        consulterModifierType.addActionListener(e -> new ConsulterModifierTypeArticle().setVisible(true));
        supprimerType.addActionListener(e -> new SupprimerTypeArticle().setVisible(true));
        afficherCommandes.addActionListener(e -> new AfficherCommandes().setVisible(true));
        supprimerCommande.addActionListener(e -> new SupprimerCommande().setVisible(true));
        modifierEtat.addActionListener(e -> new ModifierEtatCommande().setVisible(true));



    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AccueilGerant().setVisible(true);
        });
    }
}
