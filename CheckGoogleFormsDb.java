import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckGoogleFormsDb {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/google_form_db", "root", "root");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM google_forms");
            System.out.println("Google Forms tracked:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + " - Form ID: " + rs.getString("google_form_id") + " - Survey ID: " + rs.getInt("survey_id") + " - Expires(Sync): " + rs.getString("expires_at"));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
