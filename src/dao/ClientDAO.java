package dao;

import model.Client;
import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO {

    // Vérifie l'email et le mot de passe pour la connexion
    public Client verifierConnexion(String email, String motDePasse) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM clients WHERE email = ? AND mot_de_passe = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, motDePasse);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return creerClientDepuisResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Vérifie si un email existe déjà (utile avant ajout)
    public boolean emailExiste(String email) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM clients WHERE email = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Ajoute un nouveau client
    public boolean ajouterClient(Client client) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO clients (email, mot_de_passe, nom, prenom, date_naissance, adresse, telephone) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, client.getEmail());
            ps.setString(2, client.getMotDePasse());
            ps.setString(3, client.getNom());
            ps.setString(4, client.getPrenom());
            ps.setString(5, client.getDateNaissance());
            ps.setString(6, client.getAdresse());
            ps.setString(7, client.getTelephone());

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Méthode utilitaire : transforme un ResultSet en objet Client
    private Client creerClientDepuisResultSet(ResultSet rs) throws SQLException {
        return new Client(
                rs.getInt("id"),
                rs.getString("email"),
                rs.getString("mot_de_passe"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("date_naissance"),
                rs.getString("adresse"),
                rs.getString("telephone")
        );
    }

    public Client getClientByEmail(String email) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM clients WHERE email = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return creerClientDepuisResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Client> listerTousLesClients() {
        List<Client> clients = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM clients";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                clients.add(creerClientDepuisResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }


    public boolean modifierClient(Client client) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE clients SET nom = ?, prenom = ?, telephone = ? WHERE email = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, client.getNom());
            ps.setString(2, client.getPrenom());
            ps.setString(3, client.getTelephone());
            ps.setString(4, client.getEmail());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean supprimerClientByEmail(String email) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM clients WHERE email = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
