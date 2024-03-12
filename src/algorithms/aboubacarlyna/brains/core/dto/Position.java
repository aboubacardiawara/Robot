package algorithms.aboubacarlyna.brains.core.dto;

/**
 * Position
 */
public class Position {

    private double x;
    private double y;

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static Position of(double x, double y) {
        return new Position(x, y);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double distanceTo(Position position) {
        return Math.sqrt(Math.pow(position.getX() - x, 2) + Math.pow(position.getY() - y, 2));
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

}