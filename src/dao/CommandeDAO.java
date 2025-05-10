package dao;

import database.DatabaseConnection;
import model.Commande;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeDAO {
    // ===== MÉTHODES UTILISÉES PAR LE CLIENT =====

    /**
     * Crée une nouvelle commande pour un client
     * @param clientId ID du client passant la commande
     * @param modeRecuperation Mode de récupération (livraison, sur place, à importer)
     * @param adresseLivraison Adresse de livraison (peut être null si pas en livraison)
     * @return L'ID de la commande créée ou -1 en cas d'erreur
     */
    public int creerCommande(int clientId, String modeRecuperation, String adresseLivraison) throws SQLException {
        String sql = "INSERT INTO commandes (client_id, mode_recuperation, adresse_livraison, etat) VALUES (?, ?, ?, 'non traitée')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, clientId);
            ps.setString(2, modeRecuperation);
            ps.setString(3, modeRecuperation.equals("livraison") ? adresseLivraison : null);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    /**
     * Modifie les informations d'une commande existante
     * @param commandeId ID de la commande à modifier
     * @param modeRecuperation Nouveau mode de récupération
     * @param adresseLivraison Nouvelle adresse de livraison
     * @return true si la modification a réussi, false sinon
     */
    public boolean modifierCommande(int commandeId, String modeRecuperation, String adresseLivraison) throws SQLException {
        String sql = "UPDATE commandes SET mode_recuperation = ?, adresse_livraison = ? WHERE id = ? AND etat = 'non traitée'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, modeRecuperation);
            ps.setString(2, modeRecuperation.equals("livraison") ? adresseLivraison : null);
            ps.setInt(3, commandeId);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Supprime une commande entière (sans supprimer les détails au préalable)
     * @param commandeId ID de la commande à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public boolean supprimerCommande(int commandeId) throws SQLException {
        String sql = "DELETE FROM commandes WHERE id = ? AND etat = 'non traitée'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, commandeId);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Récupère la liste des commandes d'un client spécifique
     * @param clientId ID du client
     * @return Liste des commandes du client
     */
    public List<Commande> getCommandesClient(int clientId) throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM commandes WHERE client_id = ? ORDER BY date_commande DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Commande commande = new Commande(
                        rs.getInt("id"),
                        rs.getInt("client_id"),
                        rs.getString("etat"),
                        rs.getString("mode_recuperation"),
                        rs.getString("adresse_livraison"),
                        rs.getTimestamp("date_commande")
                );
                commandes.add(commande);
            }
        }
        return commandes;
    }

    /**
     * Récupère les commandes non traitées d'un client
     * @param clientId ID du client
     * @return Liste des commandes non traitées
     */
    public List<Commande> getCommandesNonTraiteesClient(int clientId) throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM commandes WHERE client_id = ? AND etat = 'non traitée' ORDER BY date_commande DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Commande commande = new Commande(
                        rs.getInt("id"),
                        rs.getInt("client_id"),
                        rs.getString("etat"),
                        rs.getString("mode_recuperation"),
                        rs.getString("adresse_livraison"),
                        rs.getTimestamp("date_commande")
                );
                commandes.add(commande);
            }
        }
        return commandes;
    }

    /**
     * Récupère les détails d'une commande spécifique
     * @param commandeId ID de la commande
     * @return La commande avec tous ses détails
     */
    public Commande getCommandeById(int commandeId) throws SQLException {
        String sql = "SELECT * FROM commandes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, commandeId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Commande(
                        rs.getInt("id"),
                        rs.getInt("client_id"),
                        rs.getString("etat"),
                        rs.getString("mode_recuperation"),
                        rs.getString("adresse_livraison"),
                        rs.getTimestamp("date_commande")
                );
            }
        }
        return null;
    }

    // ===== MÉTHODES UTILISÉES PAR LE GÉRANT =====

    /**
     * Récupère toutes les commandes pour le gérant
     * @return Liste de toutes les commandes
     */
    public List<Commande> getAllCommandes() throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM commandes ORDER BY date_commande DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Commande c = new Commande(
                        rs.getInt("id"),
                        rs.getInt("client_id"),
                        rs.getString("etat"),
                        rs.getString("mode_recuperation"),
                        rs.getString("adresse_livraison"),
                        rs.getTimestamp("date_commande")
                );
                commandes.add(c);
            }
        }
        return commandes;
    }

    /**
     * Récupère les commandes filtrées par état
     * @param etat État des commandes à récupérer (non traitée, en cours de préparation, prête, en route)
     * @return Liste des commandes filtrées par état
     */
    public List<Commande> getCommandesByEtat(String etat) throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM commandes WHERE etat = ? ORDER BY date_commande DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, etat);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Commande commande = new Commande(
                        rs.getInt("id"),
                        rs.getInt("client_id"),
                        rs.getString("etat"),
                        rs.getString("mode_recuperation"),
                        rs.getString("adresse_livraison"),
                        rs.getTimestamp("date_commande")
                );
                commandes.add(commande);
            }
        }
        return commandes;
    }

    /**
     * Met à jour l'état d'une commande
     * @param id ID de la commande
     * @param nouvelEtat Nouvel état (non traitée, en cours de préparation, prête, en route)
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean updateEtatCommande(int id, String nouvelEtat) throws SQLException {
        String sql = "UPDATE commandes SET etat = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nouvelEtat);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Supprime une commande par son ID (méthode pour le gérant)
     * @param id ID de la commande à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public boolean deleteCommande(int id) throws SQLException {
        String sql = "DELETE FROM commandes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}