/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citySimulator.model.impl;

import citySimulator.Counting;
import citySimulator.Environment;
import citySimulator.Util;
import citySimulator.model.IShowable;
import citySimulator.model.IVehicle;
import citySimulator.model.Orientation;

import java.awt.*;

/**
 * @author wackw
 */
public class Vehicle implements IVehicle, IShowable, java.io.Serializable {
    protected Orientation orientation = Orientation.vertical;
    private Point location = new Point();
    private int width;
    private int length;

    private String hImagePath;
    private String vImagePath;
    /**
     * the vehicle's max speed
     */
    int maxSpeed;

    /**
     * the vehicle's Acceleration Capability science it is run the speed lower than its max speed
     */
    int accelerationCapability;

    /**
     * the vehicle's speed,why not vector? because it will just run on a lane,the lane has a direction.
     */
    int speed;

    /**
     * lane offset,shows where this vehicle is on its lane.
     */
    int laneOffset;

    private Road.Lane lane;
    private String name;

    public static Vehicle motorbike(Road.Lane lane) {
        Vehicle v = new Vehicle(lane);
        v.maxSpeed = 80;
        v.name = "motorbike";
        v.speed = Environment.motorbikeSpeed;
        v.accelerationCapability = 20;
        v.setLength(Environment.standardVehicleLength);
        v.width = Environment.standardCellLength / 4;
        v.hImagePath = "imgs/motorbike-h.png";
        v.vImagePath = "imgs/motorbike-v.png";
        Counting.instance.totalMotorbikeCount++;
        return v;
    }

    public static Vehicle car(Road.Lane lane) {
        Vehicle v = new Vehicle(lane);
        v.name = "car";
        v.maxSpeed = 80;
        v.speed = Environment.carSpeed;
        v.accelerationCapability = 15;
        v.setLength(Environment.standardVehicleLength * 2);
        v.width = Environment.standardCellLength / 2 - 10;
        v.hImagePath = "imgs/car-h.png";
        v.vImagePath = "imgs/car-v.png";
        Counting.instance.totalCarCount++;
        return v;

    }

    public static Vehicle bus(Road.Lane lane) {
        Vehicle v = new Vehicle(lane);
        v.maxSpeed = 60;
        v.speed = Environment.busSpeed;
        v.name = "bus";
        v.accelerationCapability = 5;
        v.setLength(Environment.standardVehicleLength * 6);
        v.width = Environment.standardCellLength / 2 - 5;
        v.hImagePath = "imgs/bus-h.png";
        v.vImagePath = "imgs/bus-v.png";
        Counting.instance.totalBusCount++;
        return v;
    }

    public static Vehicle random(Road.Lane lane) {
        switch (Environment.random.nextInt(3)) {
            case 0:
                return motorbike(lane);
            case 1:
                return car(lane);
            case 2:
                return bus(lane);
            default:
                return car(lane);
        }
    }

    private Vehicle() {
        throw new RuntimeException();
    }

    public Vehicle(Road.Lane lane) {
        this.lane = lane;
        speed = 1;
        Counting.instance.nowVehicleCount++;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Road.Lane getLane() {
        return lane;
    }

    public void setLane(Road.Lane lane) {
        this.lane = lane;
    }

    /**
     * get the vehicle's max speed
     */
    @Override
    public int getMaxSpeed() {
        return maxSpeed;
    }

    /**
     * set the vehicle's max speed
     */
    @Override
    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    /**
     * get the vehicle's Acceleration Capability science it is run the speed lower than its max speed
     */
    @Override
    public int getAccelerationCapability() {
        return accelerationCapability;
    }

    /**
     * set the vehicle's Acceleration Capability science it is run the speed lower than its max speed
     */

    @Override
    public void setAccelerationCapability(int accelerationCapability) {
        this.accelerationCapability = accelerationCapability;
    }

    /**
     * get the vehicle's speed,why not vector? because it will just run on a lane,the lane has a direction.
     */
    @Override
    public int getSpeed() {
        return speed;
    }

    /**
     * set the vehicle's speed,why not vector? because it will just run on a lane,the lane has a direction.
     */
    @Override
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    /**
     * set lane offset,shows where this vehicle is on its lane.
     */
    @Override
    public int getLaneOffset() {
        return laneOffset;
    }

    /**
     * set lane offset,shows where this vehicle is on its lane.
     */
    @Override
    public void setLaneOffset(int laneOffset) {
//        System.out.println(String.format("old:%d,new:%d,o:%s", this.laneOffset, laneOffset, getOrientation().toString()));
        this.laneOffset = laneOffset;
        var location = lane.getLocation();
        if (getOrientation() == Orientation.vertical) {
            if (lane.isleftOrTop()) {
                location.y = location.y + lane.getParent().getLength() - laneOffset;
            } else {
                location.y += laneOffset;
            }
        } else {
            if (lane.isleftOrTop()) {
                location.x += laneOffset;
            } else {
                location.x = location.x + lane.getParent().getLength() - laneOffset;
            }
        }
        this.location.x = (location.x);
        this.location.y = (location.y);
    }

    /**
     * append lane offset,shows where this vehicle is on its lane.
     *
     * @param laneOffset
     */
    @Override
    public void appendLaneOffset(int laneOffset) {
        setLaneOffset(this.laneOffset + laneOffset);
        CountingDistance(laneOffset);
    }

    /**
     * set the vehicle's Location.Unit: decimeter
     *
     * @param location
     */
    @Override
    public void setLocation(Point location) {
        throw new RuntimeException("can not set location for vehicle,please use setLaneOffset instead！");
    }

    /**
     * set the vehicle's Location.Unit: decimeter
     *
     * @param x
     * @param y
     */
    @Override
    public void setLocation(int x, int y) {
        throw new RuntimeException("can not set location for vehicle,please use setLaneOffset instead！");
    }

    @Override
    public Orientation getOrientation() {
        return lane.getParent().orientation;
    }

    @Override
    public void setOrientation(Orientation orientation) {
        throw new RuntimeException("do not need set orientation for vehicle,please use setLaneOffset instead！");
    }

    @Override
    public Rectangle getBoundary() {
        var boundary = new Rectangle(location);
        var ori = getOrientation();
        var isLeftOrTop = lane.isleftOrTop();

        if (isLeftOrTop) {
            boundary.x -= ori == Orientation.vertical ? 0 : length;
            boundary.y -= ori == Orientation.vertical ? width : 0;
        } else {
//            boundary.y -= ori == Orientation.vertical ? 0 : width;
            boundary.y -= ori == Orientation.vertical ? length : 0;
        }

        boundary.width = ori == Orientation.vertical ? width : length;
        boundary.height = ori == Orientation.horizontal ? width : length;

        return boundary;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * get the vehicle's Location.Unit: decimeter
     */
    @Override
    public Point getLocation() {
        return location;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public void paint(Graphics2D g) {
        var boundary = getBoundary();
        if (getOrientation() == Orientation.vertical)
            g.drawImage(Util.loadImage(vImagePath), boundary.x, boundary.y, boundary.width, boundary.height, null);
        else
            g.drawImage(Util.loadImage(hImagePath), boundary.x, boundary.y, boundary.width, boundary.height, null);
        //g.drawImage(hImage, boundary.x - boundary.width, boundary.y, boundary.width, boundary.height, null);
    }

    @Override
    public void paintChildren(Graphics2D g) {
    }

    private void CountingDistance(int laneOffset) {
        switch (name) {
            case "motorbike":
                Counting.instance.totalMotorbikeDistance += laneOffset;
                break;
            case "car":
                Counting.instance.totalCarsDistance += laneOffset;
                break;
            case "bus":
                Counting.instance.totalBusesDistance += laneOffset;
                break;
        }
    }

    @Override
    public void dispose() {
        Counting.instance.nowVehicleCount--;
    }
}
