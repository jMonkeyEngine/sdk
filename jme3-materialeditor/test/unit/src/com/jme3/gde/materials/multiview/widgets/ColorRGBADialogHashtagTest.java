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

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import javax.swing.JTextField;
import java.lang.reflect.Method;

/**
 * Test class for hashtag color code support in ColorRGBADialog.
 * This tests the ability to paste HTML color codes starting with '#'.
 * 
 * @author JMonkeyEngine Copilot
 */
public class ColorRGBADialogHashtagTest {
    
    private ColorRGBADialog dialog;
    
    @Before
    public void setUp() {
        // Create a dialog instance for testing
        dialog = new ColorRGBADialog(null, true);
    }
    
    @Test
    public void testIsValidHexString() throws Exception {
        // Use reflection to access the private method for testing
        Method isValidHexStringMethod = ColorRGBADialog.class.getDeclaredMethod("isValidHexString", String.class);
        isValidHexStringMethod.setAccessible(true);
        
        // Test valid 6-character hex strings
        assertTrue((Boolean) isValidHexStringMethod.invoke(dialog, "a6f5fe"));
        assertTrue((Boolean) isValidHexStringMethod.invoke(dialog, "A6F5FE"));
        assertTrue((Boolean) isValidHexStringMethod.invoke(dialog, "123456"));
        assertTrue((Boolean) isValidHexStringMethod.invoke(dialog, "ABCDEF"));
        assertTrue((Boolean) isValidHexStringMethod.invoke(dialog, "000000"));
        assertTrue((Boolean) isValidHexStringMethod.invoke(dialog, "FFFFFF"));
        
        // Test valid 3-character hex strings
        assertTrue((Boolean) isValidHexStringMethod.invoke(dialog, "abc"));
        assertTrue((Boolean) isValidHexStringMethod.invoke(dialog, "ABC"));
        assertTrue((Boolean) isValidHexStringMethod.invoke(dialog, "123"));
        assertTrue((Boolean) isValidHexStringMethod.invoke(dialog, "fff"));
        
        // Test invalid hex strings
        assertFalse((Boolean) isValidHexStringMethod.invoke(dialog, null));
        assertFalse((Boolean) isValidHexStringMethod.invoke(dialog, ""));
        assertFalse((Boolean) isValidHexStringMethod.invoke(dialog, "ab"));    // Too short
        assertFalse((Boolean) isValidHexStringMethod.invoke(dialog, "abcd"));  // Wrong length
        assertFalse((Boolean) isValidHexStringMethod.invoke(dialog, "abcde")); // Wrong length
        assertFalse((Boolean) isValidHexStringMethod.invoke(dialog, "abcdefg")); // Too long
        assertFalse((Boolean) isValidHexStringMethod.invoke(dialog, "ghijkl")); // Invalid hex characters
        assertFalse((Boolean) isValidHexStringMethod.invoke(dialog, "a6f5f!")); // Invalid character
        assertFalse((Boolean) isValidHexStringMethod.invoke(dialog, "a6f5 e")); // Space character
    }
    
    @Test
    public void testProcessHashtagInput() throws Exception {
        // Use reflection to access the private method for testing
        Method processHashtagInputMethod = ColorRGBADialog.class.getDeclaredMethod("processHashtagInput", JTextField.class);
        processHashtagInputMethod.setAccessible(true);
        
        // Test with valid hashtag input
        JTextField textField = new JTextField();
        textField.setText("#a6f5fe");
        processHashtagInputMethod.invoke(dialog, textField);
        assertEquals("a6f5fe", textField.getText());
        
        // Test with valid 3-character hashtag input
        textField.setText("#abc");
        processHashtagInputMethod.invoke(dialog, textField);
        assertEquals("abc", textField.getText());
        
        // Test with uppercase hashtag input
        textField.setText("#A6F5FE");
        processHashtagInputMethod.invoke(dialog, textField);
        assertEquals("A6F5FE", textField.getText());
        
        // Test with hashtag and spaces
        textField.setText(" #a6f5fe ");
        processHashtagInputMethod.invoke(dialog, textField);
        assertEquals("a6f5fe", textField.getText());
        
        // Test with invalid hex after hashtag (should not change)
        textField.setText("#ghijkl");
        processHashtagInputMethod.invoke(dialog, textField);
        assertEquals("#ghijkl", textField.getText()); // Should remain unchanged
        
        // Test with just hashtag (should not change)
        textField.setText("#");
        processHashtagInputMethod.invoke(dialog, textField);
        assertEquals("#", textField.getText()); // Should remain unchanged
        
        // Test without hashtag (should not change)
        textField.setText("a6f5fe");
        processHashtagInputMethod.invoke(dialog, textField);
        assertEquals("a6f5fe", textField.getText()); // Should remain unchanged
        
        // Test with invalid length (should not change)
        textField.setText("#abcd");
        processHashtagInputMethod.invoke(dialog, textField);
        assertEquals("#abcd", textField.getText()); // Should remain unchanged
        
        // Test empty string (should not change)
        textField.setText("");
        processHashtagInputMethod.invoke(dialog, textField);
        assertEquals("", textField.getText()); // Should remain unchanged
    }
}