package dao;

import database.DatabaseConnection;
import model.DetailsCommande;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetailsCommandeDAO {

    /**
     * Ajoute un détail à une commande
     * @param commandeId ID de la commande
     * @param articleId ID de l'article
     * @param quantite Quantité commandée
     * @param prixUnitaire Prix unitaire de l'article au moment de la commande
     * @return true si l'ajout a réussi, false sinon
     */
    public boolean ajouterDetailCommande(int commandeId, int articleId, int quantite, double prixUnitaire) throws SQLException {
        String sql = "INSERT INTO details_commande (commande_id, article_id, quantite, prix_unitaire) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, commandeId);
            ps.setInt(2, articleId);
            ps.setInt(3, quantite);
            ps.setDouble(4, prixUnitaire);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Ajoute plusieurs détails à une commande d'un coup (méthode batch)
     * @param commandeId ID de la commande
     * @param articlesIds Liste des IDs des articles
     * @param quantites Liste des quantités correspondantes aux articles
     * @return true si l'ajout batch a réussi, false sinon
     */
    public boolean ajouterDetailsCommandeBatch(int commandeId, List<Integer> articlesIds, List<Integer> quantites) throws SQLException {
        if (articlesIds.size() != quantites.size()) {
            throw new IllegalArgumentException("Les listes d'IDs d'articles et de quantités doivent avoir la même taille");
        }

        String sql = "INSERT INTO details_commande (commande_id, article_id, quantite, prix_unitaire) " +
                "VALUES (?, ?, ?, (SELECT prix FROM articles WHERE id = ?))";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < articlesIds.size(); i++) {
                    if (quantites.get(i) > 0) {
                        ps.setInt(1, commandeId);
                        ps.setInt(2, articlesIds.get(i));
                        ps.setInt(3, quantites.get(i));
                        ps.setInt(4, articlesIds.get(i));
                        ps.addBatch();
                    }
                }

                int[] results = ps.executeBatch();
                conn.commit();

                // Vérifie si au moins un enregistrement a été ajouté
                for (int result : results) {
                    if (result > 0) return true;
                }
                return false;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Supprime tous les détails d'une commande
     * @param commandeId ID de la commande
     * @return true si la suppression a réussi, false sinon
     */
    public boolean supprimerDetailsCommande(int commandeId) throws SQLException {
        String sql = "DELETE FROM details_commande WHERE commande_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, commandeId);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Récupère tous les détails d'une commande
     * @param commandeId ID de la commande
     * @return Liste des détails de la commande
     */
    public List<DetailsCommande> getDetailsCommande(int commandeId) throws SQLException {
        List<DetailsCommande> details = new ArrayList<>();
        String sql = "SELECT dc.*, a.nom as nom_article FROM details_commande dc " +
                "JOIN articles a ON dc.article_id = a.id " +
                "WHERE dc.commande_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, commandeId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                DetailsCommande detail = new DetailsCommande(
                        rs.getInt("id"),
                        rs.getInt("commande_id"),
                        rs.getInt("article_id"),
                        rs.getString("nom_article"),
                        rs.getInt("quantite"),
                        rs.getDouble("prix_unitaire")
                );
                details.add(detail);
            }
        }
        return details;
    }

    /**
     * Met à jour la quantité d'un détail de commande
     * @param detailId ID du détail à modifier
     * @param nouvelleQuantite Nouvelle quantité
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean updateQuantiteDetail(int detailId, int nouvelleQuantite) throws SQLException {
        String sql = "UPDATE details_commande SET quantite = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nouvelleQuantite);
            ps.setInt(2, detailId);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Calcule le montant total d'une commande
     * @param commandeId ID de la commande
     * @return Montant total de la commande
     */
    public double calculerMontantCommande(int commandeId) throws SQLException {
        String sql = "SELECT SUM(quantite * prix_unitaire) as total FROM details_commande WHERE commande_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, commandeId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0.0;
    }
}