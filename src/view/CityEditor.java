package citySimulator.view;

import citySimulator.Environment;
import citySimulator.model.*;
import citySimulator.model.impl.Crossroads;
import citySimulator.model.impl.Road;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * TODO:any problem ,email me: wackwang@outlook.com,if not need ,pleas delete this comment
 */
public class CityEditor extends CityRender {

    private ICityFragment selectedElement;
    private EditorStatus status;

    private boolean editEnable = true;

    public CityEditor() {
        city = new City();
        status = EditorStatus.none;
        setBackground(Color.CYAN);
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (editEnable == false) return;
                if (selectedElement == null) return;
                Point location = e.getPoint();
                normalizeLocation(location);
                if (checkSpaceAvailability(location, selectedElement)) {
                    selectedElement.getBoundary().setLocation(location);
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (editEnable == false) return;
                //mouse click:select element or place new element,
                switch (status) {
                    //mouse left key is ok,right key is delete
                    case adding:
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (addElement(selectedElement.getLocation(), selectedElement)) {
                                selectedElement = null;
                                status = EditorStatus.none;
                            }
                        } else {
                            selectedElement = null;
                            status = EditorStatus.none;
                        }
                        break;
                    case moving:
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            removeElement(selectedElement);
                            addElement(selectedElement.getLocation(), selectedElement);
                            selectedElement = null;
                            status = EditorStatus.none;
                        } else if (e.getButton() == MouseEvent.BUTTON3) {
                            removeElement(selectedElement);
                            selectedElement = null;
                            status = EditorStatus.none;
                        }

                        break;
                    case none:
                        selectedElement = pickElement(e.getPoint());
                        status = selectedElement == null ? EditorStatus.none : EditorStatus.moving;
                        break;
                }
            }
        });
    }

    private boolean addElement(Point location, ICityFragment f) {
        if (checkSpaceAvailability(location, selectedElement)) {
            city.add(selectedElement);
            if (f instanceof Crossroads) {
                var crossroad = (Crossroads) f;
                var accessRoads = findAccessibleRoads(crossroad, f.getLocation());
                crossroad.accessRoads(accessRoads);
            } else if (f instanceof Road) {
                var road = (Road) f;

                //access with new crossroads;
                var accessRoads = findAccessibleCrossroads(road, f.getLocation());
                road.accessCrossroads(accessRoads);
            }
            return true;
        }
        return false;
    }

    private void removeElement(ICityFragment f) {
        city.remove(f);
        //disconnect
        if (f instanceof Crossroads) {
            var crossroad = (Crossroads) f;
            if (crossroad.getLeft() != null) {
                crossroad.getLeft().setEndCrossroad(null);
                crossroad.setLeft(null);
            }
            if (crossroad.getTop() != null) {
                crossroad.getTop().setEndCrossroad(null);
                crossroad.setTop(null);
            }
            if (crossroad.getRight() != null) {
                crossroad.getRight().setStartCrossroad(null);
                crossroad.setRight(null);
            }
            if (crossroad.getBottom() != null) {
                crossroad.getBottom().setStartCrossroad(null);
                crossroad.setBottom(null);
            }
        }
        if (f instanceof Road) {
            var road = (Road) f;
            if (road.getStartCrossroad() != null) {
                if (road.getStartCrossroad().getRight() == road) {
                    road.getStartCrossroad().setRight(null);
                } else {
                    road.getStartCrossroad().setBottom(null);
                }
                if (road.getStartCrossroad().getAllRoads() == null || road.getStartCrossroad().getAllRoads().size() <= 1){
                    removeElement(road.getStartCrossroad());
                }
            }
            if (road.getEndCrossroad() != null) {
                if (road.getEndCrossroad().getTop() == road) {
                    road.getEndCrossroad().setTop(null);
                } else {
                    road.getEndCrossroad().setRight(null);
                }
                if (road.getEndCrossroad().getAllRoads() == null || road.getEndCrossroad().getAllRoads().size() <= 1){
                    removeElement(road.getEndCrossroad());
                }
            }
            road.setStartCrossroad(null);
            road.setEndCrossroad(null);
        }
    }


    public void newCityFragment(ICityFragment f) {
        status = EditorStatus.adding;
        selectedElement = f;
    }

    /**
     * think the city is build up with cells,so  normalize location to cell left top
     */
    private void normalizeLocation(Point location) {
        location.x = location.x / Environment.standardCellLength * Environment.standardCellLength;
        location.y = location.y / Environment.standardCellLength * Environment.standardCellLength;
    }

    /**
     * Check if the target position is available
     */
    private boolean checkSpaceAvailability(Point targetLocation, ICityFragment element) {
        //basic constraint:no other things here

        if (city.isAnythingHere(targetLocation, element.getBoundary().width, element.getBoundary().height, element)) {
            return false;
        }

        //crossroads:at least one road can connected
        if (element instanceof Crossroads) {
            var crossroad = (Crossroads) element;

            var accessibleRoad = findAccessibleRoads(crossroad, targetLocation);

            if (accessibleRoad == null || accessibleRoad.size() <= 1) {
                return false;
            }
        }
        return true;
    }

    private ArrayList<Road> findAccessibleRoads(Crossroads crossroad, Point targetLocation) {
        var accessibleRoad = new ArrayList<Road>();
        for (var f : city.getFragments()) {
            if (f instanceof Road && crossroad.canAccess((Road) f, targetLocation)) {
                accessibleRoad.add((Road) f);
            }
        }
        return accessibleRoad;
    }

    private ArrayList<Crossroads> findAccessibleCrossroads(Road road, Point location) {
        var accessibleCrossroads = new ArrayList<Crossroads>();
        for (var f : city.getFragments()) {
            if (f instanceof Crossroads) {
                var crossroad = (Crossroads) f;
                if (road.canAccess(crossroad, location)) {
                    accessibleCrossroads.add(crossroad);
                }
            }
        }
        return accessibleCrossroads;
    }

    private ICityFragment pickElement(Point location) {
        return city.pickOne(location);
    }

    @Override
    protected void paintCity(Graphics2D g) {
        super.paintCity(g);
        //if in adding status ,paint this element which is not in city right now,
        if (status == EditorStatus.adding)
            selectedElement.paint(g);
    }

    public boolean isEditEnable() {
        return editEnable;
    }

    public void setEditEnable(boolean editEnable) {
        this.editEnable = editEnable;
    }

    public enum EditorStatus {
        none,
        moving,
        adding,
    }

}
