package edu.wpi.N.database;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class ServiceControllerTest {
    @Test
    public void testGetDoctor(){}

    @Test
    public void testAddDoctor(){}

    @Test
    public void testRemoveDoctor(){
        ServiceController.addDoc(1, "Wilson Wong", "NDEPT00104", "Neurology");
        ServiceController.deleteDoc(1);
        assertNull(ServiceController.getDoc(1));
    }

    @Test
    public void testModifyDoctorFalse(){
        ServiceController.addDoc(1, "Wilson Wong", "NDEPT00104", "Software Engineering");
        assertFalse(ServiceController.modifyDoc(1, "Wilson Wang", "NHALL00104", "Algorithms"));
    }

    @Test
    public void testModifyDoctorTrue(){
        ServiceController.addDoc(1, "Wilson Wong", "NDEPT00104", "Software Engineering");
        assertFalse(ServiceController.modifyDoc(1, "Wilson Wang", "NDEPT00204", "Algorithms"));
    }
}
