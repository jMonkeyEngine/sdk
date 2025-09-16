/*
 * Copyright (c) 2009-2024 jMonkeyEngine
 * All rights reserved.
 */
package com.jme3.gde.materials.multiview.widgets;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Simple manual test to verify animation toggle functionality
 * 
 * @author copilot
 */
public class MaterialPreviewWidgetManualTest {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Material Preview Widget Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            MaterialPreviewWidget widget = new MaterialPreviewWidget();
            frame.add(widget);
            
            frame.setSize(300, 250);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            // Test animation toggle after 2 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    System.out.println("Animation enabled: " + widget.isAnimationEnabled());
                    
                    Thread.sleep(1000);
                    SwingUtilities.invokeLater(() -> {
                        widget.setAnimationEnabled(true);
                        System.out.println("Animation enabled: " + widget.isAnimationEnabled());
                    });
                    
                    Thread.sleep(3000);
                    SwingUtilities.invokeLater(() -> {
                        widget.setAnimationEnabled(false);
                        System.out.println("Animation enabled: " + widget.isAnimationEnabled());
                    });
                    
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }
}