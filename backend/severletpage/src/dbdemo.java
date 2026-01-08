
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
//
public class dbdemo {

    public static void main(String[] args) throws Exception{
//as the jdbc connected to postgres
        String sql="select * from captials";
        String url = "jdbc:postgresql://localhost:5432/world";
        String username = "postgres";
        String password = "hari";
//as this throws error when connection try to established that time a error can occur to handle  it we use try

            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Database connected");



    }
}
