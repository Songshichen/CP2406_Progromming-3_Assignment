package citySimulator;

import java.io.Serializable;

public class Counting implements Serializable {
    public static Counting instance = null;

    public Counting() {
        instance = this;
    }

    public int nowVehicleCount = 0;

    public int totalMotorbikeCount = 1;
    public int totalCarCount = 1;
    public int totalBusCount = 1;
    public int totalLightCount = 0;

    public int totalMotorbikeDistance = 0;
    public int totalCarsDistance = 0;
    public int totalBusesDistance = 0;

    public int totalLightWait = 0;

    public int totalWait = 0;

    public int avgSpeed() {
        return (totalMotorbikeDistance + totalCarsDistance + totalBusesDistance) / (totalMotorbikeCount + totalCarCount + totalBusCount + 1);
    }
}
