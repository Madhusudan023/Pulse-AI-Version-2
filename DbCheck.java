import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbCheck {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/employee_db", "root", "root");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, email, role, region FROM employees");
            while (rs.next()) {
                System.out.println(rs.getString("email") + " - " + rs.getString("role") + " - " + rs.getString("region"));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
