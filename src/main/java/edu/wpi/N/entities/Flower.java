package edu.wpi.N.entities;

public class Flower {
  String flowerName;
  String price;

  public Flower(String name, int price) {
    this.flowerName = name;
    this.price = "$" + price / 100 + String.format(".%02d", price % 100);
  }

  public String getFlowerName() {
    return flowerName;
  }

  public String getPrice() {
    return price;
  }
}
