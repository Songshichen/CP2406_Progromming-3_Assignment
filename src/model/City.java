package citySimulator.model;

import citySimulator.Counting;
import citySimulator.model.ICityFragment;
import citySimulator.model.impl.Crossroads;
import citySimulator.model.impl.Road;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * there 2 part :roads and intersections,roads are alone roads,not connected with any road.
 */
public class City implements Serializable {
    private ArrayList<ICityFragment> fragments;
    private boolean tickingRun = false;
    private Counting counting;

//    private ArrayList<Road> aloneRoads;
//    private ArrayList<Crossroads> intersections;

    public City() {
        fragments = new ArrayList<>();
        counting = new Counting();
    }

    public void add(ICityFragment element) {
        if (element == null)
            return;
        //warning:here not Check if the target position is available,so may cause some bug
        fragments.add(element);
    }

    public void remove(ICityFragment element) {
        if (element == null)
            return;
        //warning:here not Check if the target position is available,so may cause some bug
        fragments.remove(element);
    }

    public ICityFragment pickOne(Point location) {
        for (ICityFragment f : fragments) {
            if (f.getBoundary().contains(location)) {
                return f;
            }
        }
        return null;
    }

    public boolean isAnythingHere(Point location, int width, int height, ICityFragment except) {
        for (ICityFragment f : fragments) {
            if (except != f && f.getBoundary().intersects(location.x, location.y, width, height)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<ICityFragment> getFragments() {
        return fragments;
    }

    public void setFragments(ArrayList<ICityFragment> fragments) {
        this.fragments = fragments;
    }

    public boolean isTickingRun() {
        return tickingRun;
    }

    public void setTickingRun(boolean tickingRun) {
        this.tickingRun = tickingRun;
    }

    public Counting getCounting() {
        return counting;
    }

    /**
     * only when tickingRun is true,this method will work
     */
    public void ticking(int ticks) {
        if (tickingRun) {
            //TODO:ticking this city
            //first:change traffic lights
            tickingTrafficLights(ticks);
            tickingCars(ticks);
        }
    }

    private void tickingTrafficLights(int ticks) {
        for (var f : fragments) {
            if (f instanceof Crossroads) {
                var c = (Crossroads) f;
                if (c.getTrafficLight() != null) {
                    c.getTrafficLight().Ticking(ticks);
                }
            }
        }
    }

    private void tickingCars(int ticks) {
        for (var f : fragments) {
            if (f instanceof Road) {
                var r = (Road) f;
                r.timeTicking(ticks);
            }
        }
    }
}
