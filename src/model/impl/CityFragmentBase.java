package citySimulator.model.impl;

import citySimulator.Environment;
import citySimulator.model.ICityFragment;
import citySimulator.model.Orientation;

import java.awt.*;
import java.io.Serializable;

public class CityFragmentBase implements ICityFragment, Serializable {
    protected Orientation orientation = Orientation.vertical;
    protected Rectangle boundary = new Rectangle(0, 0, Environment.standardCellLength, 0);

    @Override
    public Orientation getOrientation() {
        return orientation;
    }

    @Override
    public void setOrientation(Orientation orientation) {
        if (orientation != this.orientation) {
            int swap = boundary.height;
            boundary.height = boundary.width;
            boundary.width = swap;
        }
        this.orientation = orientation;
    }

    /**
     * get the vehicle's length.Unit: decimeter
     */
    @Override
    public int getLength() {
        return orientation == Orientation.vertical ? boundary.height : boundary.width;
    }

    /**
     * set the vehicle's length.Unit: decimeter
     *
     * @param length
     */
    @Override
    public void setLength(int length) {
        if (orientation == Orientation.vertical) {
            boundary.height = length;
        } else {
            boundary.width = length;
        }
    }

    /**
     * get the vehicle's width.Unit: decimeter
     */
    @Override
    public int getWidth() {
        return orientation == Orientation.vertical ? boundary.width : boundary.height;
    }

    /**
     * set the vehicle's width.Unit: decimeter
     *
     * @param width
     */
    @Override
    public void setWidth(int width) {
        if (orientation == Orientation.horizontal) {
            boundary.height = width;
        } else {
            boundary.width = width;
        }
    }

    /**
     * get the vehicle's Location.Unit: decimeter
     */
    @Override
    public Point getLocation() {
        return boundary.getLocation();
    }

    /**
     * set the vehicle's Location.Unit: decimeter
     *
     * @param location
     */
    @Override
    public void setLocation(Point location) {
        boundary.setLocation(location);
    }

    /**
     * set the vehicle's Location.Unit: decimeter
     *
     * @param x
     * @param y
     */
    @Override
    public void setLocation(int x, int y) {
        boundary.setLocation(x, y);
    }

    @Override
    public Rectangle getBoundary() {
        return boundary;
    }

    @Override
    public void paint(Graphics2D g) {
        var rect = getBoundary();
        g.setColor(Color.BLACK);
        g.drawRect(rect.x, rect.y, rect.width, rect.height);
    }

    @Override
    public void paintChildren(Graphics2D g) {

    }
}
