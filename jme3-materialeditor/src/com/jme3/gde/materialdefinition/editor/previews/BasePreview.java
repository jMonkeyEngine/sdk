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
