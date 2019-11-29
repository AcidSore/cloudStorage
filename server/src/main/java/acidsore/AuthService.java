package acidsore;
import java.sql.*;

public class AuthService {
    private static Connection connection;
    private static Statement stmt=null;

    static String url = "jdbc:mysql://localhost:8889/users?serverTimezone=Europe/Moscow&useSSL=false";
    static String username = "root";
    static String password = "root";

    public static boolean connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
            if (connection != null) {
                stmt = connection.createStatement();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void disconnect() {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean checkAuth(String login, String password) {
        String sql = String.format("SELECT * FROM users WHERE login='%s'", login);
        try {
            ResultSet rs = stmt.executeQuery(sql);

            if ((rs.next() && rs.getString(1).equals(password)) && (rs.getInt(2)==0))
            {
                String upd = String.format("UPDATE users SET auth = 1 WHERE login='%s'", login );
                stmt.executeQuery(upd);
            }
                return true;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
}







