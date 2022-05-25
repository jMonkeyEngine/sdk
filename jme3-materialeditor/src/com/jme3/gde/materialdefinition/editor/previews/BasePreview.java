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

import com.jme3.gde.materials.MaterialProperty;
import com.jme3.gde.materials.multiview.widgets.MaterialWidgetListener;
import com.jme3.shader.ShaderNodeVariable;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Base component for all previews.
 *
 * @author rickard
 */
public abstract class BasePreview extends JPanel implements MaterialWidgetListener {

    private ShaderNodeVariable output;
    protected Logger logger = Logger.getLogger(BasePreview.class.getName());

    public interface OnDefaultValueChangedListener {

        void onDefaultValueChanged(String value);
    }

    public BasePreview(ShaderNodeVariable output) {
        this.output = output;
        setBorder(new EmptyBorder(0, 0, 0, 0));
        setBackground(new Color(170, 170, 170));
        ((FlowLayout) getLayout()).setVgap(0);
    }

    protected void onDefaultValueChanged(String value) {
        output.setDefaultValue(value);
        firePropertyChange(output.getName(), "", value);
    }

    @Override
    public void propertyChanged(MaterialProperty property) {
        onDefaultValueChanged(property.getValue());
    }

}
