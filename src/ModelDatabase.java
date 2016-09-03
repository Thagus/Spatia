import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Created by Thagus on 02/09/16.
 */
public class ModelDatabase {
    private static ModelDatabase uniqueInstance;
    private Connection con;
    private Statement st;

    private ModelDatabase() throws Exception {
        if(uniqueInstance!=null)
            throw new Exception("There can only be one instance of the database");
        try {
            Class.forName("org.h2.Driver");
            //String addr = System.getProperty("user.home") + "\\.spatia\\spatia";
            con = DriverManager.getConnection("jdbc:h2:./database/spatia", "spatia", "hi");

            st = con.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error accessing the database. Please, restart the program and ensure that there's no other instance running", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static synchronized ModelDatabase instance(){
        if (uniqueInstance==null) {
            try {
                uniqueInstance = new ModelDatabase();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            return uniqueInstance;
        }  else {
            return uniqueInstance;
        }
    }
}
