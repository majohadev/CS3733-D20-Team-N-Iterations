package edu.wpi.N.entities;

public class Flower {

  String flowerName;
  int price;

  public Flower(String name, int price) {
    this.flowerName = name;
    this.price =
        price; // can convert it using "$" + price/100 + String.format(".%02d", price % 100)
  }

  public String getFlowerName() {
    return flowerName;
  }

  public double getPrice() {
    return price;
  }
}
