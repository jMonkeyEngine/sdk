package com.jme3.gde.materialdefinition.editor.previews;

import com.jme3.shader.ShaderNodeVariable;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;

/**
 * A component for displaying and editing a Bool MatParam
 *
 * @author rickard
 */
public class BoolPreview extends BasePreview {

    private final JCheckBox checkbox;

    public BoolPreview(ShaderNodeVariable output) {
        super(output);
        boolean checked = false;
        String value = output.getDefaultValue();
        if (value != null && value.isEmpty()) {
            checked = Boolean.parseBoolean(value);
        }
        checkbox = new JCheckBox("", checked);

        checkbox.setSize(20, 20);
        checkbox.addActionListener((ActionEvent e) -> {
            onDefaultValueChanged(((JCheckBox) e.getSource()).isSelected() ? "true" : "false");
        });

        add(checkbox);
    }

}
