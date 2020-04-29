package citySimulator;

import java.util.Random;

public class Environment {
    public static int standardVehicleLength = 12;
    public static int standardCellLength = 50;
    public static int maxFps = 200;
    public static int ticksPerFrame = 10;
    public static int madeRate = 5;
    public static int trafficLightInterval = 20 * 1000;
    public static int motorbikeSpeed = 1;
    public static int carSpeed = 4;
    public static int busSpeed = 2;

    public static Random random = new Random();
}

