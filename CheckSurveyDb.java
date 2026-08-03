import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckSurveyDb {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/survey_db", "root", "root");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM survey_responses");
            if (rs.next()) {
                System.out.println("Total survey responses: " + rs.getInt("total"));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
