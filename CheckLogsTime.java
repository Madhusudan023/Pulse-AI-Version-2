import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckLogsTime {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sentiment_db", "root", "root");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, created_at, status FROM ai_processing_log ORDER BY created_at DESC LIMIT 5");
            while (rs.next()) {
                System.out.println("ID: " + rs.getLong(1) + " | Time: " + rs.getString(2) + " | Status: " + rs.getString(3));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
