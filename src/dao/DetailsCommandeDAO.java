package dao;

import database.DatabaseConnection;
import model.DetailsCommande;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetailsCommandeDAO {

    /**
     * Ajoute un détail à une commande ET met à jour le total
     * @param commandeId ID de la commande
     * @param articleId ID de l'article
     * @param quantite Quantité commandée
     * @param prixUnitaire Prix unitaire de l'article au moment de la commande
     * @return true si l'ajout a réussi, false sinon
     */
    public boolean ajouterDetailCommande(int commandeId, int articleId, int quantite, double prixUnitaire) throws SQLException {
        String sql = "INSERT INTO details_commande (commande_id, article_id, quantite, prix_unitaire) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, commandeId);
                ps.setInt(2, articleId);
                ps.setInt(3, quantite);
                ps.setDouble(4, prixUnitaire);

                boolean detailAdded = ps.executeUpdate() > 0;

                if (detailAdded) {
                    // Mettre à jour le total de la commande
                    CommandeDAO commandeDAO = new CommandeDAO();
                    boolean totalUpdated = commandeDAO.updateTotalCommande(commandeId);

                    if (totalUpdated) {
                        conn.commit();
                        return true;
                    }
                }

                conn.rollback();
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
     * Ajoute plusieurs détails à une commande d'un coup (méthode batch) ET met à jour le total
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
                boolean hasValidItems = false;

                for (int i = 0; i < articlesIds.size(); i++) {
                    if (quantites.get(i) > 0) {
                        ps.setInt(1, commandeId);
                        ps.setInt(2, articlesIds.get(i));
                        ps.setInt(3, quantites.get(i));
                        ps.setInt(4, articlesIds.get(i));
                        ps.addBatch();
                        hasValidItems = true;
                    }
                }

                if (hasValidItems) {
                    int[] results = ps.executeBatch();

                    // Vérifier si au moins un enregistrement a été ajouté
                    boolean anySuccess = false;
                    for (int result : results) {
                        if (result > 0) {
                            anySuccess = true;
                            break;
                        }
                    }

                    if (anySuccess) {
                        // Mettre à jour le total de la commande
                        CommandeDAO commandeDAO = new CommandeDAO();
                        boolean totalUpdated = commandeDAO.updateTotalCommande(commandeId);

                        if (totalUpdated) {
                            conn.commit();
                            return true;
                        }
                    }
                }

                conn.rollback();
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
     * Supprime tous les détails d'une commande ET remet le total à zéro
     * @param commandeId ID de la commande
     * @return true si la suppression a réussi, false sinon
     */
    public boolean supprimerDetailsCommande(int commandeId) throws SQLException {
        String sql = "DELETE FROM details_commande WHERE commande_id = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, commandeId);
                ps.executeUpdate(); // Peu importe le nombre de lignes supprimées

                // Remettre le total à zéro
                CommandeDAO commandeDAO = new CommandeDAO();
                commandeDAO.updateTotalCommande(commandeId, 0.0);

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
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
     * Met à jour la quantité d'un détail de commande ET synchronise le total
     * @param detailId ID du détail à modifier
     * @param nouvelleQuantite Nouvelle quantité
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean updateQuantiteDetail(int detailId, int nouvelleQuantite) throws SQLException {
        // D'abord récupérer l'ID de la commande
        String getCommandeIdSql = "SELECT commande_id FROM details_commande WHERE id = ?";
        String updateSql = "UPDATE details_commande SET quantite = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                int commandeId = -1;

                // Récupérer l'ID de la commande
                try (PreparedStatement ps = conn.prepareStatement(getCommandeIdSql)) {
                    ps.setInt(1, detailId);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        commandeId = rs.getInt("commande_id");
                    }
                }

                if (commandeId == -1) {
                    conn.rollback();
                    return false;
                }

                // Mettre à jour la quantité
                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setInt(1, nouvelleQuantite);
                    ps.setInt(2, detailId);

                    boolean quantityUpdated = ps.executeUpdate() > 0;

                    if (quantityUpdated) {
                        // Mettre à jour le total de la commande
                        CommandeDAO commandeDAO = new CommandeDAO();
                        boolean totalUpdated = commandeDAO.updateTotalCommande(commandeId);

                        if (totalUpdated) {
                            conn.commit();
                            return true;
                        }
                    }
                }

                conn.rollback();
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
     * Supprime un détail spécifique ET met à jour le total
     * @param detailId ID du détail à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public boolean supprimerDetail(int detailId) throws SQLException {
        String getCommandeIdSql = "SELECT commande_id FROM details_commande WHERE id = ?";
        String deleteSql = "DELETE FROM details_commande WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                int commandeId = -1;

                // Récupérer l'ID de la commande
                try (PreparedStatement ps = conn.prepareStatement(getCommandeIdSql)) {
                    ps.setInt(1, detailId);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        commandeId = rs.getInt("commande_id");
                    }
                }

                if (commandeId == -1) {
                    conn.rollback();
                    return false;
                }

                // Supprimer le détail
                try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                    ps.setInt(1, detailId);

                    boolean detailDeleted = ps.executeUpdate() > 0;

                    if (detailDeleted) {
                        // Mettre à jour le total de la commande
                        CommandeDAO commandeDAO = new CommandeDAO();
                        boolean totalUpdated = commandeDAO.updateTotalCommande(commandeId);

                        if (totalUpdated) {
                            conn.commit();
                            return true;
                        }
                    }
                }

                conn.rollback();
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
     * Calcule le montant total d'une commande (méthode de vérification)
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