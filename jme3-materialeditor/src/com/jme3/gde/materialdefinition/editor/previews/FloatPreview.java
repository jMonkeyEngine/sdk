package com.jme3.gde.materialdefinition.editor.previews;

import com.jme3.gde.materials.MaterialProperty;
import com.jme3.gde.materials.multiview.widgets.FloatPanelSmall;
import com.jme3.shader.ShaderNodeVariable;

/**
 * Component for previewing and changing a default Float value for a MatParam.
 *
 * @author rickard
 */
public class FloatPreview extends BasePreview {

    public FloatPreview(ShaderNodeVariable output) {
        super(output);
        MaterialProperty p = new MaterialProperty();
        p.setName(output.getName());
        p.setType("float");
        p.setValue(output.getDefaultValue());
        FloatPanelSmall floatPanel = new FloatPanelSmall();
        floatPanel.setProperty(p);
        floatPanel.registerChangeListener(this);
        add(floatPanel);
        setSize(floatPanel.getPreferredSize());
    }

}
