package model;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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

    public void createSchema(){
        try {
            st.execute("CREATE SCHEMA SPATIA");
        } catch (SQLException e) {
            System.out.println("Error creating schema:");
            e.printStackTrace();
        }
    }

    public void createTables(){
        //Create Terms table
        try {
            st.execute("CREATE TABLE SPATIA.TERMS(" +
                    "idDoc INTEGER NOT NULL," +
                    "term VARCHAR NOT NULL," +
                    "occurrence INTEGER NOT NULL," +
                    "PRIMARY KEY (id,term)" +
                    ")");
        } catch (SQLException e) {
            System.out.println("Error creating terms table:");
            e.printStackTrace();
        }

        //Create Documents table
        try {
            st.execute("CREATE TABLE SPATIA.DOCUMENT(" +
                    "idDoc INTEGER NOT NULL," +
                    "term VARCHAR NOT NULL," +
                    "occurrence INTEGER NOT NULL," +
                    "PRIMARY KEY (id,term)" +
                    ")");
        } catch (SQLException e) {
            System.out.println("Error creating terms table:");
            e.printStackTrace();
        }
    }

    public void clearDB(){
        try {
            st.execute("DROP TABLE JNOMINAS.EMPLEADO");
            st.execute("DROP TABLE JNOMINAS.HORARIO");
            st.execute("DROP TABLE JNOMINAS.ASISTENCIA");
            st.execute("DROP TABLE JNOMINAS.PERMISO");
            st.execute("DROP TABLE JNOMINAS.FERIADO");
            st.execute("DROP TABLE JNOMINAS.VACACIONES");
            st.execute("DROP TABLE JNOMINAS.PERIODOECONOMICO");
            //st.execute("DROP TABLE JNOMINAS.PASSWORD");
        } catch (SQLException e) {
            System.out.println("Error cleaning DB:");
            e.printStackTrace();
        }
    }

    public void close(){

    }
}
