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

package com.jme3.gde.materials.multiview.widgets;

import com.jme3.asset.AssetNotFoundException;
import com.jme3.gde.core.assets.ProjectAssetManager;
import com.jme3.gde.core.properties.TexturePropertyEditor;
import com.jme3.gde.core.properties.preview.TexturePreview;
import com.jme3.gde.materials.MaterialProperty;
import com.jme3.gde.materials.multiview.MaterialEditorTopComponent;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A more compact texture panel designed for the shader node editor.
 * @author rickard
 */
public class TexturePanelSquare extends MaterialPropertyWidget {

    private final TexturePropertyEditor editor;
    private final ProjectAssetManager manager;
    private boolean flip = false;
    private boolean repeat = false;
    protected String textureName = null; // always enclosed with ""
    private TexturePreview texPreview;
    private final ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);

    /** Creates new form SelectionPanel */
    public TexturePanelSquare(ProjectAssetManager manager) {
        this.manager = manager;
        editor = new TexturePropertyEditor(manager);
        initComponents();
        texturePreview.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Component view = editor.getCustomEditor();
                view.setVisible(true);
                if (editor.getValue() != null) {
                    textureName = "\"" + editor.getAsText() + "\"";
                    displayPreview();
                    updateFlipRepeat();
                    fireChanged();
                } else { // "No Texture" has been clicked
                    textureName = "\"\"";
                    texturePreview.setIcon(null);
                    texturePreview.setToolTipText("");
                    property.setValue("");
                    fireChanged();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }

    private void displayPreview() {
        if (!"".equals(textureName)) {
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    try{
                        if (texPreview == null) {
                            texPreview = new TexturePreview(manager);
                        }
                        final String[] textureNameComponents = textureName.split(" ");
                        texPreview.requestPreview(stripQuotes(textureNameComponents[textureNameComponents.length - 1]), "", 80, 25, texturePreview, null);
                    } catch (AssetNotFoundException a) {
                        Logger.getLogger(MaterialEditorTopComponent.class.getName()).log(Level.WARNING, "Could not load texture {0}", textureName);
                    }
                }
            });
        }
    }
    
    private String stripQuotes(String s) {
        return s.substring(1, s.length() - 1);
    }

    private void updateFlipRepeat() {
        String propertyValue = property.getValue();
        propertyValue = propertyValue.replaceFirst(textureName, "");
        if (flip && !propertyValue.contains("Flip ")) {
            propertyValue += "Flip ";
        } else if (!flip) {
            propertyValue = propertyValue.replaceFirst("Flip ", "");
        }
        if (repeat && !propertyValue.contains("Repeat ")) {
            propertyValue += "Repeat ";
        } else if (!repeat) {
            propertyValue = propertyValue.replaceFirst("Repeat ", "");
        }
        propertyValue += textureName;
        property.setValue(propertyValue);
        texturePreview.setToolTipText(propertyValue);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        texturePreview = new javax.swing.JLabel();

        setBackground(new java.awt.Color(170, 170, 170));
        setMaximumSize(new java.awt.Dimension(95, 65));
        setMinimumSize(new java.awt.Dimension(40, 40));
        setPreferredSize(new java.awt.Dimension(95, 45));

        jPanel1.setPreferredSize(new java.awt.Dimension(10, 0));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jCheckBox1.setBackground(new java.awt.Color(170, 170, 170));
        jCheckBox1.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jCheckBox1.setForeground(new java.awt.Color(0, 0, 0));
        jCheckBox1.setText(org.openide.util.NbBundle.getMessage(TexturePanelSquare.class, "TexturePanelSquare.jCheckBox1.text")); // NOI18N
        jCheckBox1.setFocusable(false);
        jCheckBox1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBox1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jCheckBox2.setBackground(new java.awt.Color(170, 170, 170));
        jCheckBox2.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jCheckBox2.setForeground(new java.awt.Color(0, 0, 0));
        jCheckBox2.setText(org.openide.util.NbBundle.getMessage(TexturePanelSquare.class, "TexturePanelSquare.jCheckBox2.text")); // NOI18N
        jCheckBox2.setFocusable(false);
        jCheckBox2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBox2.setMaximumSize(new java.awt.Dimension(53, 15));
        jCheckBox2.setMinimumSize(new java.awt.Dimension(53, 15));
        jCheckBox2.setPreferredSize(new java.awt.Dimension(53, 15));
        jCheckBox2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jCheckBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox2ActionPerformed(evt);
            }
        });

        texturePreview.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        texturePreview.setText(org.openide.util.NbBundle.getMessage(TexturePanelSquare.class, "TexturePanelSquare.text")); // NOI18N
        texturePreview.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        texturePreview.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        texturePreview.setFocusable(false);
        texturePreview.setIconTextGap(0);
        texturePreview.setMaximumSize(new java.awt.Dimension(60, 60));
        texturePreview.setMinimumSize(new java.awt.Dimension(40, 40));
        texturePreview.setName(""); // NOI18N
        texturePreview.setPreferredSize(new java.awt.Dimension(40, 40));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(texturePreview, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 94, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 1, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(texturePreview, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jCheckBox1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 52, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(TexturePanelSquare.class, "TexturePanelSquare.AccessibleContext.accessibleName")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        if (property == null) {
            return;
        }
        flip = jCheckBox1.isSelected();
        updateFlipRepeat();
        fireChanged();
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jCheckBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox2ActionPerformed
        if (property == null) {
            return;
        }
        repeat = jCheckBox2.isSelected();
        updateFlipRepeat();
        fireChanged();
    }//GEN-LAST:event_jCheckBox2ActionPerformed

    @Override
    protected void readProperty() {
        java.awt.EventQueue.invokeLater(() -> {
            textureName = property.getValue();
            if (textureName.contains("Flip ")) {
                flip = true;
                textureName = textureName.replaceFirst("Flip ", "").trim();
            }
            if (textureName.contains("Repeat ")) {
                repeat = true;
                textureName = textureName.replaceFirst("Repeat ", "").trim();
            }
            property.setValue(textureName);
            displayPreview();
            texturePreview.setToolTipText(property.getValue());
            MaterialProperty prop = property;
            property = null;
            jCheckBox1.setSelected(flip);
            jCheckBox2.setSelected(repeat);
            property = prop;
        });
    }

    @Override
    public void cleanUp() {
        if (texPreview != null) {
            texPreview.cleanUp();
        }
        exec.shutdownNow();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel texturePreview;
    // End of variables declaration//GEN-END:variables
}
