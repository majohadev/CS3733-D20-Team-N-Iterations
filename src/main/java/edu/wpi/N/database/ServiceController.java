package edu.wpi.N.database;



import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Doctor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ServiceController {
    private static Connection con = DbController.getCon();

    public static void initService() throws DBException {
        try {
            String query =
                "Create Table doctors ("
                + "doctorID INT NOT NULL PRIMARY KEY, "
                + "name VARCHAR(255) NOT NULL, "
                + "location CHAR(10) NOT NULL, "
                + "field VARCHAR(255) NOT NULL, "
                + "FOREIGN KEY (location) REFERENCES nodes(nodeID) ON DELETE CASCADE)";

            PreparedStatement state = con.prepareStatement(query);
            state.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("initService causing error", e);
        }
    }
    // Nick
    public static Doctor getDoctor(int id) {
        return new Doctor(0,"a", new DbNode(), "a");
    }
    // Nick
    public static boolean addDoctor(Doctor d){
        return addDoctor(d.getId(), d.getName(), d.getLoc(), d.getField());
    }
    // Nick
    public static boolean addDoctor(int id, String name, DbNode location, String field){
        return false;
    }

    // Chris
    public static boolean deleteDoctor(int docID) throws DBException {
        try {
            String query = "DELETE FROM doctors WHERE doctorID = ?";
            PreparedStatement state = con.prepareStatement(query);
            state.setInt(1, docID);
            return state.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("delete function causing error", e);
        }
    }
    // Chris
    public static boolean modifyDoctor(int docID, String n, DbNode loc, String f) throws DBException {
        String key = loc.getNodeID();
        try {
            if (key.contains("DEPT")) {
                String query = "UPDATE doctors SET name = ?, location = ?, field = ? WHERE doctorID = ?";
                PreparedStatement state = con.prepareStatement(query);
                state.setString(1, n);
                state.setString(2, key);
                state.setString(3, f);
                state.setInt(4, docID);
                return state.executeUpdate() > 0;
            }
            System.out.println("nodeID for the location is incorrect");
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("modify function causing error", e);
        }
    }
}
