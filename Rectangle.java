/**
 * This work is marked with CC0 1.0 Universal
 */
package shapes;

public class Rectangle extends Shape2D {

    private double width;
    private double length;

    /**
     * Constructor for Rectangle shape object
     * @param centre The centre of the Rectangle represented as a Point object
     * @param width The width of rectangle
     * @param length The length of rectangle
     */
    public Rectangle(Point centre, double width, double length) {
        super(centre);
        this.width = width;
        this.length = length;
    }

    public double getWidth() {
        return this.width;
    }

    private void setWidth(double widthVal) {
        this.width = widthVal;
    }

    public double getLength() {
        return this.length;
    }

    private void setLength(double lengthVal) {
        this.length = lengthVal;
    }

    @Override
    public boolean containsPoint(Point aPoint) {
        return aPoint.getXCord() >= centre.getXCord() - (this.width / 2) &&
                aPoint.getXCord() <= centre.getXCord() + (this.width / 2) &&
                aPoint.getYCord() >= centre.getYCord() - (this.length / 2) &&
                aPoint.getYCord() <= centre.getYCord() + (this.length / 2);
    }

    @Override
    public Point[] getVertices() {
        return new Point[] {
                new Point(centre.getXCord() - (this.width / 2), centre.getYCord() + (this.length / 2)),
                new Point(centre.getXCord() + (this.width / 2), centre.getYCord() + (this.length / 2)),
                new Point(centre.getXCord() - (this.width / 2), centre.getYCord() - (this.length / 2)),
                new Point(centre.getXCord() + (this.width / 2), centre.getYCord() - (this.length / 2))

        };
    }

    @Override
    public double getArea() {
        return this.length * this.width;
    }

    @Override
    public double getPerimeter() {
        return 2 * (this.length + this.width);
    }
}
