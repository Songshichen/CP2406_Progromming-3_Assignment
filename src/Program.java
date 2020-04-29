package citySimulator;

import citySimulator.view.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

public class Program {
    public static void main(String[] args) throws InterruptedException {
        Queue<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < 10; i++) {
            q.add(i);
        }
        for (var i : q) {
            System.out.println(i);
        }
//        Rectangle rectangle = new Rectangle(0, 0, 5, 5);
//        System.out.println(rectangle.contains(0,0));
//        MainFrame mainFrame = new MainFrame();
//        mainFrame.setSize(800, 600);
//        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
////        mainFrame.pack();
//        mainFrame.setLocationRelativeTo(null);
//        mainFrame.setVisible(true);
    }
}

