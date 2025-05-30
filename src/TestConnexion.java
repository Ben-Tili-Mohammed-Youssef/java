import java.sql.Connection;
import java.sql.DriverManager;

public class TestConnexion {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Chargement du driver
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/restaurantdb", "root", ""
            );
            System.out.println("Connexion réussie !");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
