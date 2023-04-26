/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jme3.gde.core.dnd;

import java.awt.datatransfer.DataFlavor;
import javax.swing.JPanel;

/**
 * Based on: https://stackoverflow.com/questions/23225958/dragging-between-two-components-in-swing
 * @author rickard
 */
public class StringDataFlavor extends DataFlavor {

    // This saves me having to make lots of copies of the same thing
//    public static final StringDataFlavor SHARED_INSTANCE = new StringDataFlavor();

    public StringDataFlavor() {

        super("text/plain", null);

    }

}