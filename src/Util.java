package citySimulator;

import citySimulator.model.City;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.util.Dictionary;
import java.util.HashMap;

public class Util {

    public static City loadCity() {
        return loadCity("city.city");
    }

    public static City loadCity(String path) {
        City city = null;
        try {
            FileInputStream fileIn = new FileInputStream(path);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            city = (City) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
            return new City();
        } catch (ClassNotFoundException c) {
            System.out.println("Employee class not found");
            c.printStackTrace();
            return new City();
        }
        return city;
    }

    public static void saveCity(City city) {
        saveCity(city, "city.city");
    }

    public static void saveCity(City city, String path) {
        try {
            FileOutputStream fileOut = new FileOutputStream(path, false);
            ObjectOutputStream out = null;
            out = new ObjectOutputStream(fileOut);
            out.writeObject(city);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static HashMap<String, Image> cachedImage = new HashMap<>();

    public static Image loadImage(String path) {
        try {
            if (cachedImage.containsKey(path))
                return cachedImage.get(path);

            var img = ImageIO.read(new File(path));
            cachedImage.put(path, img);
            return img;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
