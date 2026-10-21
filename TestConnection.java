import java.sql.Connection;

public class TestConnection {
    public static void main(String[] args) {

        Connection con = VConnection.getConnection();

        if (con != null) {
            System.out.println("SUCCESS: Database Connected!");
        } else {
            System.out.println("FAILED: Database Connection Failed!");
        }
    }
}