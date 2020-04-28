package edu.wpi.N.entities;

public class Flower {


  public void setFlowerName(String flowerName) {
    this.flowerName = flowerName;
  }

  public void setPrice(int price) {
    this.price = price;
  }

  String flowerName;
  String price;

  public Flower(String name, int price) {
    this.flowerName = name;
    this.price = "$" + price/100 + String.format(".%02d", price % 100);
  }

  public String getFlowerName() {
    return flowerName;
  }

  public double getPrice() {
    return price;
  }
}
