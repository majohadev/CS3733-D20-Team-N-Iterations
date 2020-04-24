package edu.wpi.N.entities;

public class EmotionalSupporter extends Employee {

    public EmotionalSupporter(int id, String name, String serviceType) {
        super(id, name);
    }

    @Override
    public String getServiceType() {
        return "Emotional Support";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Translator)) {
            return false;
        }

        Translator other = (Translator) o;

        return getID() == other.getID()
                && getName().equals(other.getName());
    }
}
