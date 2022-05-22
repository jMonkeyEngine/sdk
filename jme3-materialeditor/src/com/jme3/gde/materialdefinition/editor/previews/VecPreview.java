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
