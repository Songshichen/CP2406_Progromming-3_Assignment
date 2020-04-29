package citySimulator.view;


import citySimulator.Environment;
import citySimulator.model.*;
import citySimulator.model.City;

import javax.swing.*;
import java.awt.*;

public class CityRender extends JComponent {
    protected City city;
    protected Image bufferImg;
    protected Graphics2D bufferGraphics;
    private Timer timer;
    private boolean showGrid = true;
    protected Stroke gridStroke;
    protected Stroke stroke;


    public CityRender() {
        animate();
        gridStroke = new BasicStroke(1.0f);
        stroke = new BasicStroke(3.0f);
    }

    private void animate() {
        timer = new Timer(1000 / Environment.maxFps, e -> {
            if (city == null) return;
            city.ticking(Environment.ticksPerFrame);
            repaint();
            timer.setDelay(1000 / Environment.maxFps);
        });
        timer.start();
    }


    @Override
    protected void paintComponent(Graphics g) {
        if (bufferGraphics == null || bufferImg.getWidth(null) != getWidth() || bufferImg.getHeight(null) != getHeight()) {
            if (bufferGraphics != null) {
                bufferGraphics.dispose();
            }
            bufferImg = createImage(getSize().width, getSize().height);
            bufferGraphics = (Graphics2D) bufferImg.getGraphics();
        }
//        super.paintComponent(bufferGraphics);
        bufferGraphics.clearRect(0, 0, getWidth(), getHeight());
        paintGrid(bufferGraphics);
        paintCity(bufferGraphics);
        g.drawImage(bufferImg, 0, 0, getWidth(), getHeight(), this);
        //use graphics buffer to avoid ui flash
    }

    protected void paintCity(Graphics2D g) {
        g.setColor(Color.black);
        g.setStroke(stroke);

        if (city == null) return;
        if (city.getFragments() != null) {
            for (ICityFragment s : city.getFragments()) {
                s.paint(g);
            }
            for (ICityFragment s : city.getFragments()) {
                s.paintChildren(g);
            }
        }
    }

    private void paintGrid(Graphics2D g) {
        g.setColor(Color.GRAY);
        g.setStroke(gridStroke);
        for (int i = 0; i * Environment.standardCellLength < getWidth(); i++) {
            var x = i * Environment.standardCellLength;
            g.drawLine(x, 0, x, getHeight());
        }
        for (int i = 0; i * Environment.standardCellLength < getHeight(); i++) {
            var y = i * Environment.standardCellLength;
            g.drawLine(0, y, getWidth(), y);
        }
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public boolean isShowGrid() {
        return showGrid;
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }
}
