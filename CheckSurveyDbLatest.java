import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckSurveyDbLatest {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/survey_db", "root", "root");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, submitted_at FROM survey_responses ORDER BY submitted_at DESC LIMIT 5");
            System.out.println("Latest 5 survey responses:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + " - Submitted At: " + rs.getString("submitted_at"));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
