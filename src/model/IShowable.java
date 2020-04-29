package citySimulator.model;

import java.awt.*;

public interface IShowable {

    /**
     * get render boundary
     */
    Rectangle getBoundary();

    /**
     * todo:this may cause problem：this graphics can edit the hole ui
     */
    void paint(Graphics2D g);

    void paintChildren(Graphics2D g);
}