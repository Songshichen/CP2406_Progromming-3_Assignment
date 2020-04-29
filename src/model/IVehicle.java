package citySimulator.model;

import citySimulator.model.impl.Road;

import java.awt.*;

public interface IVehicle extends ICityFragment {
    /**
     * get the vehicle's max speed
     */
    int getMaxSpeed();

    /**
     * set the vehicle's max speed
     */
    void setMaxSpeed(int maxSpeed);

    /**
     * get the vehicle's Acceleration Capability science it is run the speed lower than its max speed
     */
    int getAccelerationCapability();

    /**
     * set the vehicle's Acceleration Capability science it is run the speed lower than its max speed
     */

    void setAccelerationCapability(int accelerationCapability);

    /**
     * get the vehicle's speed,why not vector? because it will just run on a lane,the lane has a direction.
     */
    int getSpeed();

    /**
     * set the vehicle's speed,why not vector? because it will just run on a lane,the lane has a direction.
     */
    void setSpeed(int speed);

    /**
     * set lane offset,shows where this vehicle is on its lane.
     */
    int getLaneOffset();

    Road.Lane getLane();

    void setLane(Road.Lane lane);

    /**
     * set lane offset,shows where this vehicle is on its lane.
     */
    void setLaneOffset(int laneOffset);

    /**
     * append lane offset,shows where this vehicle is on its lane.
     */
    void appendLaneOffset(int laneOffset);

    String getName();

    void setName(String name);

    void dispose();
}