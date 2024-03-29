/*
 *  Copyright (c) 2009-2022 jMonkeyEngine
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are
 *  met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *  TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *  PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.gde.shadernodedefinition.wizard;

import java.awt.EventQueue;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings({"unchecked", "rawtypes"})
public final class SNDefVisualPanel2 extends JPanel {
    
    private final Object[] emptyObj = {"float", "", ""};
    private final String type;
    private final String[] varTypes = new String[]{"bool", "int", "float", "vec2", 
        "vec3", "vec4", "sampler", "sampler2D", "sampler3D", "mat3", "mat4",
        "samplerCube", "sampler2DArray"};

    /**
     * Creates new form SNDefVisualPanel2
     */
    public SNDefVisualPanel2(String type) {
        initComponents();
        this.type = type;
        titleLabel.setText(type);
        varTable.getColumnModel().getSelectionModel().addListSelectionListener(
                new ExploreSelectionListener());
        varTable.getColumn("Type").setCellEditor(new DefaultCellEditor(
                new JComboBox(varTypes)));
    }

    @Override
    public String getName() {
        return type;
    }

    public String[][] getData() {
       
        varTable.editCellAt(-1, -1);
        varTable.getSelectionModel().clearSelection();
       
        DefaultTableModel model = (DefaultTableModel) varTable.getModel();
        String[][] data = new String[model.getRowCount()][3];
        for (int i = 0; i < model.getRowCount(); i++) {
            data[i][0] = (String) model.getValueAt(i, 0);
            data[i][1] = (String) model.getValueAt(i, 1);
            data[i][2] = (String) model.getValueAt(i, 2);
        }

        return data;
    }

    // Add this class to the body of MyTable class.
    private class ExploreSelectionListener implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {              
                int row = varTable.getSelectedRow();
                int col = varTable.getSelectedColumn();
                // Make sure we start with legal values.
                if (col < 0) {
                    col = 0;
                }
                if (row < 0) {
                    row = 0;
                }
                // Find the next editable cell.
                while (!varTable.isCellEditable(row, col)) {
                    col++;
                    if (col > varTable.getColumnCount() - 1) {
                        col = 1;
                        row = (row == varTable.getRowCount() - 1) ? 1 : row + 1;
                    }
                }
                // Select the cell in the table.
                final int r = row, c = col;
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        varTable.changeSelection(r, c, false, false);
                    }
                });
                // Edit.
                if (varTable.isCellEditable(row, col)) {
                    varTable.editCellAt(row, col);
                    if(col != 0) {
                        ((JTextField) varTable.getEditorComponent()).selectAll();
                    }
                    varTable.getEditorComponent().requestFocusInWindow();
                }
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        varTable = new javax.swing.JTable();
        jToolBar1 = new javax.swing.JToolBar();
        titleLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        delButton = new javax.swing.JButton();

        varTable.setBackground(javax.swing.UIManager.getDefaults().getColor("Label.background"));
        varTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Type", "Name", "Description"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(varTable);

        jToolBar1.setRollover(true);

        org.openide.awt.Mnemonics.setLocalizedText(titleLabel, org.openide.util.NbBundle.getMessage(SNDefVisualPanel2.class, "SNDefVisualPanel2.titleLabel.text")); // NOI18N
        jToolBar1.add(titleLabel);

        jPanel1.setOpaque(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 372, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );

        jToolBar1.add(jPanel1);

        addButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jme3/gde/core/editor/icons/add.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(addButton, org.openide.util.NbBundle.getMessage(SNDefVisualPanel2.class, "SNDefVisualPanel2.addButton.text")); // NOI18N
        addButton.setToolTipText(org.openide.util.NbBundle.getMessage(SNDefVisualPanel2.class, "SNDefVisualPanel2.addButton.toolTipText")); // NOI18N
        addButton.setAlignmentX(0.5F);
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(addButton);

        delButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jme3/gde/core/editor/icons/remove.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(delButton, org.openide.util.NbBundle.getMessage(SNDefVisualPanel2.class, "SNDefVisualPanel2.delButton.text")); // NOI18N
        delButton.setToolTipText(org.openide.util.NbBundle.getMessage(SNDefVisualPanel2.class, "SNDefVisualPanel2.delButton.toolTipText")); // NOI18N
        delButton.setAlignmentX(0.5F);
        delButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(delButton);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        DefaultTableModel model = (DefaultTableModel) varTable.getModel();
        model.addRow(emptyObj);
    }//GEN-LAST:event_addButtonActionPerformed

    private void delButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delButtonActionPerformed
        DefaultTableModel model = (DefaultTableModel) varTable.getModel();
        int selRow = varTable.getSelectedRow();
        if (selRow >= 0) {
            model.removeRow(selRow);
        }
    }//GEN-LAST:event_delButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton delButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JTable varTable;
    // End of variables declaration//GEN-END:variables
}
