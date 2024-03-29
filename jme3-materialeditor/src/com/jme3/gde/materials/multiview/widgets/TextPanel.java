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

/**
 *
 * @author normenhansen
 */
public class TextPanel extends MaterialPropertyWidget {

    /** Creates new form NumberPanel */
    public TextPanel() {
        initComponents();
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
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();

        jToolBar1.setRollover(true);
        jToolBar1.setAlignmentY(0.5F);

        jLabel1.setText(org.openide.util.NbBundle.getMessage(TextPanel.class, "TextPanel.jLabel1.text")); // NOI18N
        jLabel1.setPreferredSize(new java.awt.Dimension(100, 16));
        jToolBar1.add(jLabel1);

        jPanel1.setOpaque(false);
        jPanel1.setPreferredSize(new java.awt.Dimension(10, 0));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 4, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 28, Short.MAX_VALUE)
        );

        jToolBar1.add(jPanel1);

        jTextField1.setText(org.openide.util.NbBundle.getMessage(TextPanel.class, "TextPanel.jTextField1.text")); // NOI18N
        jTextField1.setMaximumSize(new java.awt.Dimension(200, 2147483647));
        jTextField1.setMinimumSize(new java.awt.Dimension(34, 23));
        jTextField1.setPreferredSize(new java.awt.Dimension(200, 28));
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textChanged(evt);
            }
        });
        jTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField1FocusLost(evt);
            }
        });
        jToolBar1.add(jTextField1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void textChanged(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textChanged
        property.setValue(jTextField1.getText());
        fireChanged();
    }//GEN-LAST:event_textChanged

private void jTextField1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusLost
        property.setValue(jTextField1.getText());
        fireChanged();
}//GEN-LAST:event_jTextField1FocusLost

    @Override
    protected void readProperty() {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                jLabel1.setToolTipText(property.getName() + " (" + property.getType() + ")");
                jLabel1.setText(property.getName() + " (" + property.getType() + ")");
                jTextField1.setText(property.getValue());
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables

}
