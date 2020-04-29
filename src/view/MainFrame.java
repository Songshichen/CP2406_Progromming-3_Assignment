package citySimulator.view;

import citySimulator.Counting;
import citySimulator.Environment;
import citySimulator.Util;
import citySimulator.model.City;
import citySimulator.model.Orientation;
import citySimulator.model.impl.Crossroads;
import citySimulator.model.impl.Road;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame {
    private JPanel rootPanel;
    private JButton newButton;
    private JButton openButton;
    private JButton saveButton;
    private JButton editButton;
    private JPanel statusPanel;
    private JSpinner madeRateSpinner;
    private JButton roadButton;
    private JButton crossRoadsButton;
    private JButton runSwitchButton;
    private JPanel cityRenderPanel;
    private JRadioButton verticalRadioButton;
    private JRadioButton horizontalRadioButton;
    private JCheckBox autoSaveWhenExitCheckBox;
    private JSpinner refreshRateSpinner;
    private JSpinner roadLengthSpinner;
    private JTextField roadNameTextField;
    private JTextField totalVehicleField;
    private JTextField totalLightField;
    private JTextField motorbikeAvgSpeedField;
    private JTextField carAvgSpeedField;
    private JTextField busAvgSpeedField;
    private JTextField totalWaitTimeField;
    private JTextField totalWaitLightField;
    private JTextField avgSpeedField;

    private CityEditor cityEditor;

    public MainFrame() {
        cityEditor = new CityEditor();
        cityRenderPanel.add(cityEditor, BorderLayout.CENTER);

        roadButton.addActionListener(e -> {
            var road = new Road(verticalRadioButton.isSelected() ? Orientation.vertical : Orientation.horizontal);
            road.setLength(((int) roadLengthSpinner.getValue()) * Environment.standardCellLength);
            road.setRoadName(roadNameTextField.getText());
            cityEditor.newCityFragment(road);
        });
        crossRoadsButton.addActionListener(e -> {
            var crossroad = new Crossroads();
            cityEditor.newCityFragment(crossroad);
        });
        newButton.addActionListener(actionEvent -> {
            cityEditor.city = new City();
        });
        openButton.addActionListener(actionEvent -> {
            JFileChooser jfc = new JFileChooser();
            jfc.showOpenDialog(this.rootPanel.getParent());
            var file = jfc.getSelectedFile();
            if (file != null) {
                saveFilePath = file.getPath();
                cityEditor.city = Util.loadCity(saveFilePath);
            }
        });
        saveButton.addActionListener(actionEvent -> {
            if (saveFilePath == null) {
                JFileChooser jfc = new JFileChooser();
                jfc.showSaveDialog(this.rootPanel.getParent());
                var file = jfc.getSelectedFile();
                if (file == null) {
                    return;
                }
                saveFilePath = file.getPath();
            }

            Util.saveCity(cityEditor.city, saveFilePath);

        });
        editButton.addActionListener(actionEvent -> {
            cityEditor.setEditEnable(true);
        });
        runSwitchButton.addActionListener(actionEvent -> {
            cityEditor.city.setTickingRun(!cityEditor.city.isTickingRun());
            if (cityEditor.city.isTickingRun()) {
            }
            runSwitchButton.setText(cityEditor.city.isTickingRun() ? "Run" : "Stop");
        });
        refreshRateSpinner.addChangeListener(changeEvent -> Environment.maxFps = (int) refreshRateSpinner.getValue());
        madeRateSpinner.addChangeListener(changeEvent -> Environment.madeRate = (int) madeRateSpinner.getValue());
        new Timer(100, a -> {
            if (Counting.instance == null) return;
            totalVehicleField.setText(Counting.instance.nowVehicleCount + "");

            avgSpeedField.setText(Counting.instance.avgSpeed() + "");
            motorbikeAvgSpeedField.setText(Counting.instance.totalMotorbikeDistance / Counting.instance.totalMotorbikeCount + "");
            carAvgSpeedField.setText(Counting.instance.totalCarsDistance / Counting.instance.totalCarCount + "");
            busAvgSpeedField.setText(Counting.instance.totalBusesDistance / Counting.instance.totalBusCount + "");

            totalWaitTimeField.setText(Counting.instance.totalWait / 1000 + "");
            totalWaitLightField.setText(Counting.instance.totalLightWait / 1000 + "");

        }).start();
    }

    private String saveFilePath = null;

    private void autoSave() {
        if (saveFilePath != null) {
            Util.saveCity(cityEditor.city, saveFilePath);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("City Simulator");
        var mf = new MainFrame();
        frame.setContentPane(mf.rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (mf.autoSaveWhenExitCheckBox.isSelected())
                    mf.autoSave();
                super.windowClosing(e);
            }
        });
        frame.setSize(800, 600);
//        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        refreshRateSpinner = new JSpinner(new SpinnerNumberModel(200, 10, 1000, 1));
        roadLengthSpinner = new JSpinner(new SpinnerNumberModel(10, 2, 10, 1));
        madeRateSpinner = new JSpinner(new SpinnerNumberModel(5, 0, 10, 1));
    }
}
