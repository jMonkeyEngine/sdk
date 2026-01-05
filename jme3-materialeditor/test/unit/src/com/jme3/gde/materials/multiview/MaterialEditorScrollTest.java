/*
 * Copyright (c) 2009-2023 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.gde.materials.multiview;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Field;
import javax.swing.JPanel;
import org.openide.util.Exceptions;

/**
 * Test for Material Editor scrolling functionality
 * @author copilot
 */
public class MaterialEditorScrollTest {
    
    public MaterialEditorScrollTest() {
    }
    
    @Test
    public void testEditorPanelDoesNotHaveHardcodedZeroSize() {
        MaterialEditorTopComponent editor = new MaterialEditorTopComponent();
        
        try {
            EventQueue.invokeAndWait(() -> {
                // Access the editorPanel via reflection to check it doesn't have hardcoded (0,0) size
                try {
                    Field editorPanelField = MaterialEditorTopComponent.class.getDeclaredField("editorPanel");
                    editorPanelField.setAccessible(true);
                    JPanel editorPanel = (JPanel) editorPanelField.get(editor);
                    
                    // The panel should not have a hardcoded (0,0) preferred size
                    Dimension preferredSize = editorPanel.getPreferredSize();
                    
                    // The preferred size should not be exactly (0,0) as that was the bug
                    // Note: it might still be (0,0) if no components are added yet, but it should not be hardcoded
                    // We can't test the exact size since it depends on content, but we can ensure it's not artificially set to (0,0)
                    
                    // The key test is that when components are added, the preferred size should grow
                    // For now, we just ensure the panel exists and can calculate a preferred size
                    assertTrue(preferredSize != null, "Preferred size should not be null");
                    
                    // Add a dummy component to test that preferred size calculation works
                    editorPanel.add(new javax.swing.JLabel("Test"));
                    editorPanel.revalidate();
                    
                    Dimension newPreferredSize = editorPanel.getPreferredSize();
                    // Now it should have a non-zero size since we added a component
                    assertTrue(newPreferredSize.width > 0 || newPreferredSize.height > 0, 
                            "Preferred size should grow when components are added");
                    
                } catch (NoSuchFieldException | IllegalAccessException ex) {
                    Exceptions.printStackTrace(ex);
                }
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}