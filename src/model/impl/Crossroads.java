package citySimulator.model.impl;

import citySimulator.Environment;
import citySimulator.model.Orientation;

import java.awt.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

import static citySimulator.Environment.trafficLightInterval;

/**
 * Crossroads connect roads,
 */
public class Crossroads extends CityFragmentBase implements Serializable {
    private Road left;
    private Road top;
    private Road right;
    private Road bottom;


    private TrafficLight trafficLight;
    private HashSet<Road> allRoads;

    public Crossroads() {
        allRoads = new HashSet<>();
        trafficLight = new TrafficLight();
        setLength(Environment.standardCellLength);
    }

    @Override
    public void paint(Graphics2D g) {
        //todo:paint crossroads
        var light = getTrafficLight();
        g.setColor(Color.GRAY);
        g.fillRect(boundary.x, boundary.y, boundary.width, boundary.height);

        g.setColor(Color.GREEN);

        if (light != null) {
            g.drawString(light.getTicks() / 1000 + "", boundary.x + boundary.width / 2 - 5, boundary.y + boundary.height / 2 - 5);
        }
        if (light != null && light.orientation == Orientation.horizontal) {
            g.setColor(Color.RED);
        } else {
            g.setColor(Color.GREEN);
            g.drawLine(boundary.x + boundary.width / 2, boundary.y,
                    boundary.x + boundary.width / 2, boundary.y + boundary.height);
        }
        if (top != null) {
            g.fillRect(boundary.x, boundary.y, boundary.width, 5);
        }
        if (bottom != null) {
            g.fillRect(boundary.x, boundary.y + boundary.height - 5, boundary.width, 5);
        }

        if (light != null && light.orientation == Orientation.vertical) {
            g.setColor(Color.RED);
        } else {
            g.setColor(Color.GREEN);
            g.drawLine(boundary.x, boundary.y + boundary.height / 2,
                    boundary.x + boundary.width, boundary.y + boundary.height / 2);
        }
        if (left != null) {
            g.fillRect(boundary.x, boundary.y, 5, boundary.height);
        }
        if (right != null) {
            g.fillRect(boundary.x + boundary.width - 5, boundary.y, 5, boundary.height);
        }
    }

    public Collection<Road> getAllRoads() {
        return allRoads;
    }

    public void accessRoad(Road road) {
        var roadLocation = road.getLocation();

        //when accessRoad locate at the left or bottom of the road,it is the road's startCrossroad
        //when accessRoad locate at the right or top of the road,it is the road's EndCrossroad

        if (roadLocation.x < boundary.x) {
            left = road;
            road.setEndCrossroad(this);
        } else if (roadLocation.y > boundary.y) {
            bottom = road;
            road.setEndCrossroad(this);
        } else if (roadLocation.x > boundary.x) {
            right = road;
            road.setStartCrossroad(this);
        } else if (roadLocation.y < boundary.y) {
            top = road;
            road.setStartCrossroad(this);
        }
        allRoads.add(road);
    }

    public void accessRoads(Collection<Road> roads) {
        left = null;
        top = null;
        right = null;
        bottom = null;
        if (roads == null || roads.isEmpty())
            return;
        for (var r : roads) {
            accessRoad(r);
        }
    }

    /**
     * Check if the road is accessible
     */
    public boolean canAccess(Road road, Point targetLocation) {

        //If the road and intersection are neither on the same row nor in the same column, they cannot be accessed
        var roadBoundary = road.getBoundary();
        var newBoundary = new Rectangle(targetLocation.x, targetLocation.y, this.boundary.width, this.boundary.height);

        //if can access,they must be on the same line
        if (roadBoundary.x != targetLocation.x && roadBoundary.y != targetLocation.y)
            return false;
        //also their bottom right point also be on the same line
        if (roadBoundary.x + roadBoundary.width != newBoundary.x + newBoundary.width && roadBoundary.y + roadBoundary.height != newBoundary.y + newBoundary.height)
            return false;

        //Rectangle.contains not include the points which on it's right and bottom line
        newBoundary.x -= 1;
        newBoundary.y -= 1;
        newBoundary.width += 2;
        newBoundary.height += 2;
        return newBoundary.contains(roadBoundary.x, roadBoundary.y) || newBoundary.contains(roadBoundary.x + roadBoundary.width, roadBoundary.y + roadBoundary.height);
    }

    public Road getLeft() {
        return left;
    }

    public Road getTop() {
        return top;
    }

    public Road getRight() {
        return right;
    }

    public Road getBottom() {
        return bottom;
    }

    public void setLeft(Road left) {
        if (this.left != null)
            allRoads.remove(this.left);
        this.left = left;
        if (left != null)
            allRoads.add(left);
    }

    public void setTop(Road top) {
        if (this.top != null)
            allRoads.remove(this.top);
        this.top = top;
        if (top != null)
            allRoads.add(top);
    }

    public void setRight(Road right) {
        if (this.right != null)
            allRoads.remove(this.right);
        this.right = right;
        if (right != null)
            allRoads.add(right);
    }

    public void setBottom(Road bottom) {
        if (this.bottom != null)
            allRoads.remove(this.bottom);
        this.bottom = bottom;
        if (bottom != null)
            allRoads.add(bottom);
    }

    public TrafficLight getTrafficLight() {
        if (allRoads.size() < 3)
            return null;
        return trafficLight;
    }

    /**
     * just have two status:horizontal and vertical。when ticks is 0，toggle status and ticks jump to interval
     */
    public static class TrafficLight implements Serializable {
        private int ticks;
        private Orientation orientation;

        public TrafficLight() {
            var random = new Random();
            orientation = random.nextBoolean() ? Orientation.vertical : Orientation.horizontal;
            ticks = random.nextInt(trafficLightInterval);
        }


        public Orientation getOrientation() {
            return orientation;
        }

        public int getTicks() {
            return trafficLightInterval - ticks;
        }

        /**
         * get the status of this light
         */
        public void setOrientation(Orientation orientation) {
            this.orientation = orientation;
        }

        public void Ticking(int ticks) {
            this.ticks += ticks;
            if (this.ticks > trafficLightInterval) {
                this.ticks %= trafficLightInterval;
                orientation = orientation == Orientation.horizontal ? Orientation.vertical : Orientation.horizontal;
            }
        }
    }
}

