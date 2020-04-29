package citySimulator.model.impl;


import citySimulator.Counting;
import citySimulator.Environment;
import citySimulator.model.ICityFragment;
import citySimulator.model.IVehicle;
import citySimulator.model.Orientation;

import java.awt.*;
import java.io.Serializable;
import java.util.*;

/**
 * all the roads are described with 3 factor:start ,orientation and length.all cars are include in road.
 * so we can manage cars more easily,roads are connected by Crossroadss .
 * TODO::when cars cross from one road to another,it while jump to it insteadof move over the intersection.it is a bug
 */
public class Road extends CityFragmentBase implements ICityFragment, Serializable {
    private String roadName = "";
    /**
     * start crossroad is always the most left and bottom crossroad。origin is (0,0)，Use UI coordinate system
     */
    private Crossroads startCrossroad;
    /**
     * start crossroad is always the most right and top crossroad。
     */
    private Crossroads endCrossroad;
    //    private Queue<IVehicle> leftLaneVehicles;
//    private Queue<IVehicle> rightLaneVehicles;
    //whice lane is the left lane?it is the lane in the left or top of a road

    private Lane leftOrTopLane;
    private Lane rightOrBottomLane;

    /**
     * all vehicles on this road,each list represent a lane，not limit
     */
    // private List<Lane> lanes;
    public void access(Crossroads crossroad) {

    }

    public Road(Orientation orientation) {
        super();
        setOrientation(orientation);
        leftOrTopLane = new Lane(true, this);
        rightOrBottomLane = new Lane(false, this);
    }

    public Crossroads getStartCrossroad() {
        return startCrossroad;
    }

    public void setStartCrossroad(Crossroads startCrossroad) {
        this.startCrossroad = startCrossroad;
    }

    public Crossroads getEndCrossroad() {
        return endCrossroad;
    }

    public void setEndCrossroad(Crossroads endCrossroad) {
        this.endCrossroad = endCrossroad;
    }

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    private Random random = new Random();

    public void timeTicking(int ticks) {
        //step1:move and remove cars
        //todo:Assuming acceleration doesn't take time
        leftOrTopLane.ticking(ticks);
        rightOrBottomLane.ticking(ticks);
        //step2:add new cars

        /*just the first car need use traffic light,the others just see the previous car*/
    }

    @Override
    public Rectangle getBoundary() {
        return boundary;
    }

    private static Stroke laneSepratorStroke = new BasicStroke(2);

    @Override
    public void paint(Graphics2D g) {
        g.setColor(Color.BLACK);
        var stroke = g.getStroke();
        g.drawRect(boundary.x, boundary.y, boundary.width, boundary.height);

        g.setStroke(laneSepratorStroke);
        if (this.orientation == Orientation.vertical) {
            g.drawLine(boundary.x + boundary.width / 2, boundary.y,
                    boundary.x + boundary.width / 2, boundary.y + boundary.height);
        } else if (orientation == Orientation.horizontal) {
            g.drawLine(boundary.x, boundary.y + boundary.height / 2,
                    boundary.x + boundary.width, boundary.y + boundary.height / 2);
        }
//        if (startCrossroad != null) {
//            g.setColor(Color.RED);
//            if (orientation == Orientation.horizontal)
//                g.fillRect(boundary.x + 10, boundary.y, 10, boundary.height);
//            else
//                g.fillRect(boundary.x, boundary.y + boundary.height, boundary.width, 10);
//        }
//        if (endCrossroad != null) {
//            g.setColor(Color.YELLOW);
//            if (orientation == Orientation.horizontal)
//                g.fillRect(boundary.x + boundary.width - 10, boundary.y, 10, boundary.height);
//            else
//                g.fillRect(boundary.x, boundary.y, boundary.width, 10);
//        }
        leftOrTopLane.paint(g);
        rightOrBottomLane.paint(g);
        g.setStroke(stroke);
        g.setColor(Color.RED);
        g.drawString(roadName, (int) (boundary.x + 0.5 * boundary.width), (int) (boundary.y + 0.5 * boundary.height));


    }

    @Override
    public void paintChildren(Graphics2D g) {
        super.paintChildren(g);
        leftOrTopLane.paintChildren(g);
        rightOrBottomLane.paintChildren(g);
    }

    public void accessCrossroads(ArrayList<Crossroads> accessRoads) {
        for (var a : accessRoads) {
            a.accessRoad(this);
        }
    }

    public boolean canAccess(Crossroads crossroad, Point targetLocation) {
        //If the road and intersection are neither on the same row nor in the same column, they cannot be accessed
        var roadBoundary = crossroad.getBoundary();
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

    protected Point getLaneLeftTop(Lane lane) {
        //todo:if have more than 2 lane,change here to adapt it.

        if (lane == leftOrTopLane) {
            return new Point(boundary.x, boundary.y);
        }
        if (orientation == Orientation.horizontal)
            return new Point(boundary.x, boundary.y + boundary.height / 2);
        else
            return new Point(boundary.x + boundary.width / 2, boundary.y);
    }

    /**
     * find the lan can accessed by the other road's land
     *
     * @param otherRoadLand the lane at other road
     */
    protected Lane getAccessibleLane(Lane otherRoadLand) {
        //todo:if have more than 2 lane,change here to adapt it.
        if (otherRoadLand.getParent().orientation == this.orientation)
            return otherRoadLand.leftOrTop ? leftOrTopLane : rightOrBottomLane;

        if (getCrossroad(leftOrTopLane) == otherRoadLand.parent.getCrossroad(otherRoadLand))
            return rightOrBottomLane;
        return leftOrTopLane;
    }

    protected Crossroads.TrafficLight getTrafficLight(Lane lane) {
        Crossroads cs = getCrossroad(lane);
        return cs == null ? null : cs.getTrafficLight();
    }

    /**
     * find the Crossroads which can be accessed by  the giving lane in this road
     */
    protected Crossroads getCrossroad(Lane lane) {
        if (endCrossroad == startCrossroad && endCrossroad == null)
            return null;
        return lane == leftOrTopLane ? endCrossroad : startCrossroad;
//        if (orientation == Orientation.horizontal) {
//            if (lane == leftOrTopLane) {
//                if (startCrossroad != null && lane.getLocation().x < startCrossroad.boundary.x) {
//                    return startCrossroad;
//                }
//                if (endCrossroad != null && lane.getLocation().x < endCrossroad.boundary.x) {
//                    return endCrossroad;
//                }
//                return null;
//            } else {
//                if (startCrossroad != null && lane.getLocation().x > startCrossroad.boundary.x) {
//                    return startCrossroad;
//                }
//                if (endCrossroad != null && lane.getLocation().x > endCrossroad.boundary.x) {
//                    return endCrossroad;
//                }
//                return null;
//            }
//        } else {
//            if (lane == leftOrTopLane) {
//                if (startCrossroad != null && lane.getLocation().y < startCrossroad.boundary.y) {
//                    return startCrossroad;
//                }
//                if (endCrossroad != null && lane.getLocation().y < endCrossroad.boundary.y) {
//                    return endCrossroad;
//                }
//                return null;
//            } else {
//                if (startCrossroad != null && lane.getLocation().y > startCrossroad.boundary.y) {
//                    return startCrossroad;
//                }
//                if (endCrossroad != null && lane.getLocation().y > endCrossroad.boundary.y) {
//                    return endCrossroad;
//                }
//                return null;
//            }
//        }
//        if (orientation == Orientation.horizontal)
//            return lane == leftOrTopLane ? endCrossroad : startCrossroad;
//        return lane == leftOrTopLane ? startCrossroad : endCrossroad;

    }

    public static class Lane implements Serializable {

        private Deque<IVehicle> vehicles = new ArrayDeque<>();
        private boolean leftOrTop;
        private Road parent;

        //todo:Assuming lanes are unrestricted, you can go straight and turn
        public Lane(boolean leftOrTop, Road parent) {
            this.leftOrTop = leftOrTop;
            this.parent = parent;
        }

        /**
         * add a vehicle to this lane ,it is on this lane's tail,when move ,it will be the last one to move
         */
        public void addVehicle(IVehicle vehicle) {
            vehicles.add(vehicle);
        }

        public void removeVehicle(IVehicle vehicle) {
            vehicles.remove(vehicle);
        }

        public Queue<IVehicle> getVehicles() {
            return vehicles;
        }

        public void setVehicles(Deque<IVehicle> vehicles) {
            this.vehicles = vehicles;
        }

        public boolean isleftOrTop() {
            return leftOrTop;
        }

        public void setIsleftOrTop(boolean leftOrTop) {
            this.leftOrTop = leftOrTop;
        }

        public Road getParent() {
            return parent;
        }

        public void setParent(Road parent) {
            this.parent = parent;
        }

        public void paintChildren(Graphics2D g) {
            for (var v : vehicles) {
                v.paint(g);
            }
        }

        public void paint(Graphics2D g) {
            var loc = parent.getLaneLeftTop(this);
            if (parent.orientation == Orientation.vertical) {
                g.setColor(leftOrTop ? Color.BLUE : Color.GREEN);
                g.fillRect(loc.x + 3, loc.y + 3, parent.boundary.width / 2 - 5, parent.boundary.height - 5);
            } else {
                g.setColor(leftOrTop ? Color.BLUE : Color.GREEN);
                g.fillRect(loc.x + 3, loc.y + 3, parent.boundary.width - 5, parent.boundary.height / 2 - 5);
            }
            //todo:draw lane image
        }

        public void ticking(int ticks) {
            IVehicle previousVehicle = null;

            int removeCount = 0;//Cannot delete while traversing the collection, so record the number of deletions first
            for (var v : vehicles) {

                var moveDistance = getMoveDistance(v, previousVehicle, ticks);
                //Please do not modify the order of any code
                previousVehicle = v;
                if (moveDistance <= 0) continue;

                int remainLength;
                remainLength = this.getAvailableLengthOnThisLaneHead(v);
                if (moveDistance <= remainLength) {
                    v.appendLaneOffset(moveDistance);
                    continue;
                }

                //4.If the end of the road is reached, the vehicle disappears
                if (isRoadEnd()) {
                    removeCount++;
                    v.dispose();
                    previousVehicle = null;
                    System.out.println(String.format("[debug] disappear :ori:%s,leftOrTop:%s,name:", parent.orientation.toString(), leftOrTop, v.getName()));
                    continue;
                }

                var crossRoad = parent.getCrossroad(this);
                //5.go to next road,if next road is too busy,this
                moveDistance = moveDistance - remainLength;

                HashSet<Road> roadAvailable = new HashSet<>(crossRoad.getAllRoads());
                roadAvailable.remove(parent);

                System.out.println(String.format("roadAvailable.size():%d", roadAvailable.size()));
                Road nextRoad;
                if (roadAvailable.size() == 1) {
                    nextRoad = roadAvailable.toArray(new Road[]{})[0];
                } else {
                    nextRoad = roadAvailable.toArray(new Road[]{})[Environment.random.nextInt(roadAvailable.size())];
                }
                var nextLane = nextRoad.getAccessibleLane(this);
                var nextLaneAvailableLengthOnTail = nextLane.getAvailableLengthOnThisLaneTail();

                //Traffic jam maker
                if (nextLaneAvailableLengthOnTail <= 0 || nextLaneAvailableLengthOnTail < v.getLength()) {
                    v.appendLaneOffset(parent.getLength() - v.getLaneOffset());
                    System.out.println(String.format("[debug] wait space :%d,ori:%s,leftOrTop:%s", moveDistance, parent.orientation.toString(), leftOrTop));
                    continue;
                }
                removeCount++;
                previousVehicle = null;
                v.setLane(nextLane);
                v.appendLaneOffset(Math.min(moveDistance, nextLane.getAvailableLengthOnThisLaneTail()) - v.getLaneOffset());
                nextLane.addVehicle(v);
                System.out.println(String.format("[debug] change road :%d,o:%s,leftOrTop:%s,from:%s,to:%s", moveDistance, nextLane.parent.orientation.toString(), nextLane.leftOrTop, parent.roadName, nextRoad.roadName));
            }

            //do remove operate
            while (removeCount > 0) {
                var v = vehicles.remove();
                removeCount--;
            }

            //add new cars

            if (isRoadStart() && getAvailableLengthOnThisLaneTail() > 0 && Environment.madeRate > 0 && Environment.random.nextInt(100) < Environment.madeRate) {
                var car = Vehicle.random(this);
                vehicles.add(car);
                car.setLaneOffset(0);
                System.out.println(String.format("[debug] new come :ori:%s,leftOrTop:%s", parent.orientation.toString(), leftOrTop));
            }
        }

        private boolean isRoadStart() {
            var land = leftOrTop ? parent.rightOrBottomLane : parent.leftOrTopLane;
            return parent.getCrossroad(land) == null;
        }

        private boolean isRoadEnd() {
            return parent.getCrossroad(this) == null;
        }

        public Point getLocation() {
            return parent.getLaneLeftTop(this);
        }

        private int getMoveDistance(IVehicle v, IVehicle previousVehicle, int ticks) {
            //record the max distance during the ticks
            int maxDistance = (int) (v.getSpeed() * (ticks));

            //1. may wait lane space used by previous car,so stop before previous car
            if (previousVehicle != null) {
                var distanceWithPrevious = previousVehicle.getLaneOffset() - v.getLaneOffset() - previousVehicle.getLength();
                if (maxDistance < distanceWithPrevious) {
                    return maxDistance;
                }

                //TODO:counting have some problem,Not precise enough
                Counting.instance.totalWait += ticks;
                return distanceWithPrevious;
            }

            var light = parent.getTrafficLight(this);

            //2.if have light here,may wait light,so stop before light
            if (light != null && light.getOrientation() != parent.orientation) {
                var availableLengthOnThisLaneHead = this.getAvailableLengthOnThisLaneHead(v);
                if (maxDistance < availableLengthOnThisLaneHead) {
                    return maxDistance;
                }
                //TODO:counting have some problem,Not precise enough
                Counting.instance.totalLightWait += ticks;
                return availableLengthOnThisLaneHead;
            }

            //3.if no light or is OK, and the car can go next road
            return maxDistance;
        }

        private int getAvailableLengthOnThisLaneHead(IVehicle v2) {
            return Math.abs(parent.getLength() - v2.getLaneOffset());
        }

        private int getAvailableLengthOnThisLaneTail() {

            var p1 = vehicles.peekLast();

            if (p1 == null) return parent.getLength();
            return p1.getLaneOffset() - p1.getLength();

            // two vehicle on the lane must have the same x or y with the lane
        }

    }
}

