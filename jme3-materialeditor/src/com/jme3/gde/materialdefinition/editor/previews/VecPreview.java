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
package com.jme3.gde.materialdefinition.editor.previews;

import com.jme3.shader.ShaderNodeVariable;
import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * Component for previewing and changing a default Vector4 value for a MatParam.
 *
 * @author rickard
 */
public class VecPreview extends BasePreview {

    private final int components;

    public VecPreview(ShaderNodeVariable output, int components) {
        super(output);
        this.components = components;
        JTextField textField = new JTextField(output.getDefaultValue());
        textField.addActionListener((ActionEvent e) -> {
            String value = ((JTextField) e.getSource()).getText();
            if (verifyString(value)) {
                onDefaultValueChanged(value);
            }
        });
        add(textField);
        JLabel content = new JLabel(output.getDefaultValue());
        add(content);
        setBackground(new Color(55, 55, 55));
        setSize(90, 20);
    }

    private boolean verifyString(String value) {
        String[] split = value.split(" ");
        if (split.length != components) {
            logger.warning(String.format("Value should contain {0} components", components));
            return false;
        }
        for (String s : split) {
            if (!verifyFloatString(s)) {
                return false;
            }
        }
        return true;
    }

    private boolean verifyFloatString(String value) {
        try {
            Float.parseFloat(value);
        } catch (NumberFormatException ex) {
            logger.warning("Value is not valid float");
            return false;
        } catch (NullPointerException ex) {
            logger.warning("Value is null");
            return false;
        }
        return true;
    }

}
