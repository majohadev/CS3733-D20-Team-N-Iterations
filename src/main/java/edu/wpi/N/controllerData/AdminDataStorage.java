package edu.wpi.N.controllerData;

import java.util.ArrayList;

public class AdminDataStorage {

  public ArrayList<Integer> newData = new ArrayList<Integer>();

  public ArrayList<Integer> getNewData() {
    return newData;
  }

  public void setNewData(ArrayList<Integer> newData) {
    this.newData = newData;
  }

  public void addToList(Integer integer) {
    System.out.println(integer);
    newData.add(integer);
  }
}
