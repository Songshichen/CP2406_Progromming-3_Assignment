package citySimulator.model;

import java.awt.*;

public interface ICityFragment extends IShowable {
    Orientation getOrientation();

    void setOrientation(Orientation orientation);

    /**
     * get the vehicle's length.Unit: decimeter
     */
    int getLength();

    /**
     * set the vehicle's length.Unit: decimeter
     */
    void setLength(int length);

    /**
     * get the vehicle's width.Unit: decimeter
     */
    int getWidth();

    /**
     * set the vehicle's width.Unit: decimeter
     */
    void setWidth(int width);

    /**
     * get the vehicle's Location.Unit: decimeter
     */
    Point getLocation();

    /**
     * set the vehicle's Location.Unit: decimeter
     */
    void setLocation(Point location);
    /**
     * set the vehicle's Location.Unit: decimeter
     */
    void setLocation(int x, int y);
}
