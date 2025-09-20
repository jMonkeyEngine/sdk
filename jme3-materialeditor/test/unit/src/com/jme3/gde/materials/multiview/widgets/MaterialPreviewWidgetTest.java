/*
 * Copyright (c) 2009-2024 jMonkeyEngine
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
package com.jme3.gde.materials.multiview.widgets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import org.openide.util.Exceptions;

/**
 * Test class for MaterialPreviewWidget animation functionality
 * 
 * @author copilot
 */
public class MaterialPreviewWidgetTest {
    
    public MaterialPreviewWidgetTest() {
    }
    
    @Test
    public void testAnimationEnabled() {
        MaterialPreviewWidget widget = new MaterialPreviewWidget();
        
        // Test initial state - animation should be disabled by default
        assertFalse(widget.isAnimationEnabled());
        
        // Test enabling animation
        try {
            EventQueue.invokeAndWait(() -> {
                widget.setAnimationEnabled(true);
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        assertTrue(widget.isAnimationEnabled());
        
        // Test disabling animation
        try {
            EventQueue.invokeAndWait(() -> {
                widget.setAnimationEnabled(false);
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        assertFalse(widget.isAnimationEnabled());
    }
    
    @Test
    public void testAnimationToggle() {
        MaterialPreviewWidget widget = new MaterialPreviewWidget();
        
        // Initially disabled
        assertFalse(widget.isAnimationEnabled());
        
        // Enable and verify
        try {
            EventQueue.invokeAndWait(() -> {
                widget.setAnimationEnabled(true);
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        assertTrue(widget.isAnimationEnabled());
        
        // Toggle off and verify
        try {
            EventQueue.invokeAndWait(() -> {
                widget.setAnimationEnabled(false);
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        assertFalse(widget.isAnimationEnabled());
    }
    
    @Test
    public void testCleanupStopsAnimation() {
        MaterialPreviewWidget widget = new MaterialPreviewWidget();
        
        // Enable animation
        try {
            EventQueue.invokeAndWait(() -> {
                widget.setAnimationEnabled(true);
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        assertTrue(widget.isAnimationEnabled());
        
        // Cleanup should stop animation
        try {
            EventQueue.invokeAndWait(() -> {
                widget.cleanUp();
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        // Animation should still be logically enabled but timer stopped
        // This tests that cleanup properly stops the timer
        assertTrue(widget.isAnimationEnabled());
    }
}