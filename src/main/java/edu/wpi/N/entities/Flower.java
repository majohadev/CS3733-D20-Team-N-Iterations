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

  @Override
  public String toString() {
    return flowerName + ", " + price;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Flower)) {
      return false;
    }

    Flower other = (Flower) obj;

    return flowerName.equals(other.flowerName);
  }
}
