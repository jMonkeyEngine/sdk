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
package com.jme3.gde.materials.multiview.widgets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;
import com.jme3.gde.materials.MaterialProperty;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import org.openide.util.Exceptions;



/**
 *
 * @author rickard
 */
public class TexturePanelTest {
    
    public TexturePanelTest() {
    }

    String textureNameWithModifier = "Flip Repeat \"simple_name.jpg\"";
    String textureNameWithSpaces = "\"texture name with spaces.jpg\"";
    String textureNameWithSpaceAndModifier = "Flip Repeat \"texture name with spaces.jpg\"";
    String textureName = "\"simple_name.jpg\"";
    String textureNameWithExtraInfo = "Flip \"simple_name.jpg\" LINEAR";
    
    @Test
    public void testExtractTextureName() {
        TexturePanel texturePanel = new TexturePanel();
        
        String extractedName = texturePanel.extractTextureName(textureName);
        assertEquals("simple_name.jpg", extractedName);
        
        
        extractedName = texturePanel.extractTextureName(textureNameWithModifier);
        assertEquals("simple_name.jpg", extractedName);
        
        
        extractedName = texturePanel.extractTextureName(textureNameWithSpaces);
        assertEquals("texture name with spaces.jpg", extractedName);
        
        
        extractedName = texturePanel.extractTextureName(textureNameWithSpaceAndModifier);
        assertEquals("texture name with spaces.jpg", extractedName);
        
        extractedName = texturePanel.extractTextureName(textureNameWithExtraInfo);
        assertEquals("simple_name.jpg", extractedName);
    }
    
    @Test
    public void testReadPropertyFlipRepeat() {
        TexturePanel texturePanel = new TexturePanel();
        texturePanel.setProperty(new MaterialProperty());
        texturePanel.property.setValue(textureNameWithModifier);
        try {
            EventQueue.invokeAndWait(() -> {
                texturePanel.readProperty();
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        
        assertEquals(textureName, texturePanel.textureName);
        assertTrue(texturePanel.isFlip());
        assertTrue(texturePanel.isRepeat());
        assertEquals(texturePanel.extraProperties, "");
    }
    
    @Test
    public void testReadProperty() {
        TexturePanel texturePanel = new TexturePanel();
        texturePanel.setProperty(new MaterialProperty());
        texturePanel.property.setValue(textureName);
        
        try {
            EventQueue.invokeAndWait(() -> {
                texturePanel.readProperty();
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        assertEquals(texturePanel.textureName, textureName);
        assertFalse(texturePanel.isFlip());
        assertFalse(texturePanel.isRepeat());
    }
    
    @Test
    public void testReadPropertyExtraProperty() {
        TexturePanel texturePanel = new TexturePanel();
        texturePanel.setProperty(new MaterialProperty());
        texturePanel.property.setValue(textureNameWithExtraInfo);
        try {
            EventQueue.invokeAndWait(() -> {
                texturePanel.readProperty();
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        assertEquals(texturePanel.textureName, textureName);
        assertEquals(texturePanel.extraProperties, "LINEAR");
    }
    
    @Test
    public void testUpdateFlipRepeat() {
        TexturePanel texturePanel = new TexturePanel();
        texturePanel.setProperty(new MaterialProperty());
        
        texturePanel.property.setValue(textureNameWithModifier);
        try {
            EventQueue.invokeAndWait(() -> {
                texturePanel.readProperty();
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        texturePanel.updateFlipRepeat();
        assertEquals(texturePanel.property.getValue(), textureNameWithModifier);
    }
    
    @Test
    public void testUpdateFlipRepeatExtraProperty() {
        TexturePanel texturePanel = new TexturePanel();
        texturePanel.setProperty(new MaterialProperty());
        texturePanel.property.setValue(textureNameWithExtraInfo);
        try {
            EventQueue.invokeAndWait(() -> {
                texturePanel.readProperty();
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        texturePanel.updateFlipRepeat();
        assertTrue(texturePanel.property.getValue().contains("LINEAR"));
        
    }

}
