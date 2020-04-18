package edu.wpi.N.database;

import edu.wpi.N.entities.DbNode;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ServiceController {
    public static void initService() throws DBException {
        try{
            String query = "Create Table doctors (" +
                    "doctorID INT NOT NULL PRIMARY KEY," +
                    "name VARCHAR(255) NOT NULL," +
                    "location CHAR(10) NOT NULL references nodes(nodeID)," +
                    "field VARCHAR(255) NOT NULL)";
            PreparedStatement state = DbController.getCon().prepareStatement(query);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("initService causing error",e);
        }
    }

    public static boolean deleteDoc(int docID) throws DBException {
        try{
            String query = "DELETE FROM doctors WHERE doctorID = '"+docID+"'";
            PreparedStatement state = DbController.getCon().prepareStatement(query);
            return true;
        }catch(SQLException e){
            e.printStackTrace();
            throw new DBException("delete function causing error", e);
        }
    }

    public static boolean modifyDoc(int docID, String n, String loc, String f) throws DBException {
        try{
            if(loc.contains("DEPT")){
                String query = "UPDATE doctors SET name = '"+n+"'," +
                        "location = '"+loc+"', field = '"+f+"' WHERE doctorID = '"+docID+"'";
                PreparedStatement state = DbController.getCon().prepareStatement(query);
                return true;
            }
            System.out.println("nodeID for the location is incorrect");
            return false;
        }catch(SQLException e){
            e.printStackTrace();
            throw new DBException("modify function causing error", e);
        }
    }
}
