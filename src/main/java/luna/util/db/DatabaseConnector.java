package luna.util.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    // The DB connection
    private Connection connection;

    public DatabaseConnector(){
        try{
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/SimDev", "root", "j3llyf1sh42");
            if(connection != null) System.out.println("Connected to SimDev Database");
            else System.out.println("Error connecting to Database");
        }catch (SQLException ex){
            System.out.println("SQL Exception occurred");
            System.out.println(ex.getMessage());
            System.exit(1);
        }catch (Exception ex){
            System.out.println("Exception occurred");
            System.out.println(ex.getMessage());
            System.exit(1);
        }//

    }// end of constructor

    // currently all we need
    public Connection getConnection(){return this.connection;}

}
