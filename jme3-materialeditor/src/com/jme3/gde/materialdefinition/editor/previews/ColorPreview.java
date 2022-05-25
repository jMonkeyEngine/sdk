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

import com.jme3.gde.materials.multiview.widgets.ColorRGBADialog;
import com.jme3.shader.ShaderNodeVariable;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JFrame;
import javax.swing.border.LineBorder;

/**
 * Component for previewing and changing a default Color value for a MatParam.
 *
 * @author rickard
 */
public class ColorPreview extends BasePreview implements MouseListener {

    private final float[] rgba = new float[]{0f, 0f, 0f, 1f};

    public ColorPreview(ShaderNodeVariable output) {
        super(output);
        String color = output.getDefaultValue();
        populateRGBA(color);
        setBackground(new Color(rgba[0], rgba[1], rgba[2], rgba[3]));
        addMouseListener(this);
        setBorder(new LineBorder(Color.DARK_GRAY));
        setSize(20, 20);
    }

    private void populateRGBA(String colorString) {
        if(colorString != null && !colorString.isEmpty()){
            String[] split = colorString.split(" ");
            rgba[0] = Float.parseFloat(split[0]);
            rgba[1] = Float.parseFloat(split[1]);
            rgba[2] = Float.parseFloat(split[2]);
            rgba[3] = Float.parseFloat(split[3]);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        ColorRGBADialog dialog = new ColorRGBADialog(new JFrame(), true);
        dialog.setLocationRelativeTo(null);
        dialog.setColor(new Color(rgba[0], rgba[1], rgba[2], rgba[3]));
        dialog.setVisible(true);
        if (dialog.getColorAsText() != null) {
            setBackground(dialog.getColor());
            populateRGBA(dialog.getColorAsText());
            onDefaultValueChanged(dialog.getColorAsText());
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

}
