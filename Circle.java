/**
 * This work is marked with CC0 1.0 Universal
 */
package shapes;

/**
 * Class to represent a circle shape - as this is a circle it does not
 * contain any vertices
 */

public class Circle extends Shape2D {

    private double radius;

    /**
     * Constructor for Circle shape object
     * @param centre The centre of the circle represented as a Point object
     * @param radius The radius of the circle created
     */
    public Circle(Point centre, double radius) {
        super(centre);
        this.radius = radius;
    }


    @Override
    public boolean containsPoint(Point aPoint) {
        return this.centre.distanceBetween(aPoint) <= this.radius;
    }

    @Override
    public Point[] getVertices() {
        return new Point[0];
    }

    @Override
    public double getArea() {
        return Math.PI * this.radius * this.radius;
    }

    @Override
    public double getPerimeter() {
        return 2 * Math.PI * this.radius;
    }
}
