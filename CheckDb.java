import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckDb {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pulse_reporting", "root", "root");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT count(*) FROM reports");
            if (rs.next()) {
                System.out.println("Reports count: " + rs.getInt(1));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
