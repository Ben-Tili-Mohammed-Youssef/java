package dao;

import database.DatabaseConnection;
import model.Article;
import model.TypeArticle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArticleDAO {

    public List<Article> listerArticles() {
        List<Article> articles = new ArrayList<>();
        String sql = "SELECT a.id, a.nom, a.description, a.prix, a.image_path, t.id as type_id, t.nom as type_nom " +
                "FROM articles a JOIN type_articles t ON a.type_id = t.id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TypeArticle type = new TypeArticle(rs.getInt("type_id"), rs.getString("type_nom"));
                Article article = new Article(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getDouble("prix"),
                        type,
                        rs.getString("image_path")
                );
                articles.add(article);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return articles;
    }

    public Article trouverParId(int id) {
        String sql = "SELECT a.id, a.nom, a.description, a.prix, a.image_path, t.id as type_id, t.nom as type_nom " +
                "FROM articles a JOIN type_articles t ON a.type_id = t.id WHERE a.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                TypeArticle type = new TypeArticle(rs.getInt("type_id"), rs.getString("type_nom"));
                return new Article(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getDouble("prix"),
                        type,
                        rs.getString("image_path")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void ajouterArticle(Article article) {
        String sql = "INSERT INTO articles (nom, description, prix, type_id, image_path) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, article.getNom());
            ps.setString(2, article.getDescription());
            ps.setDouble(3, article.getPrix());
            ps.setInt(4, article.getType().getId());
            ps.setString(5, article.getImagePath());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void modifierArticle(Article article) {
        String sql = "UPDATE articles SET nom = ?, description = ?, prix = ?, type_id = ?, image_path = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, article.getNom());
            ps.setString(2, article.getDescription());
            ps.setDouble(3, article.getPrix());
            ps.setInt(4, article.getType().getId());
            ps.setString(5, article.getImagePath());
            ps.setInt(6, article.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void supprimerArticle(int id) {
        String sql = "DELETE FROM articles WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
