/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NumberPanel.java
 *
 * Created on 14.06.2010, 16:42:25
 */
package com.jme3.gde.materials.multiview.widgets;

import com.jme3.gde.materials.MaterialProperty;

/**
 *
 * @author normenhansen
 */
public class FloatPanelSmall extends MaterialPropertyWidget {

    /** Creates new form NumberPanel */
    public FloatPanelSmall() {
        initComponents();
           jSpinner1.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(0.1f)));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        jPanel1 = new javax.swing.JPanel();
        jSpinner1 = new javax.swing.JSpinner();

        setBackground(new java.awt.Color(170, 170, 170));

        jToolBar1.setBackground(new java.awt.Color(170, 170, 170));
        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        jPanel1.setBackground(new java.awt.Color(170, 170, 170));
        jPanel1.setOpaque(false);

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(0.0f, null, null, 1.0f));
        jSpinner1.setPreferredSize(new java.awt.Dimension(70, 20));
        jSpinner1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                valueChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jToolBar1.add(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void valueChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_valueChanged
        if (property == null) {
            return;
        }
        property.setValue(jSpinner1.getValue() + "");
        fireChanged();
    }//GEN-LAST:event_valueChanged

    @Override
    protected void readProperty() {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                setToolTipText(property.getName());
                MaterialProperty prop = property;
                property = null;
                try {
                    jSpinner1.setValue(Float.parseFloat(prop.getValue()));
                } catch (Exception e) {
                    jSpinner1.setValue(0);
                }
                property = prop;
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables
}