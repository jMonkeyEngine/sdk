/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jme3.gde.materialdefinition.editor.previews;

import com.jme3.gde.materialdefinition.EditableMatDefFile;
import com.jme3.gde.materials.MaterialProperty;
import com.jme3.gde.materials.multiview.widgets.TexturePanelSquare;
import com.jme3.shader.ShaderNodeVariable;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Component for previewing and changing a default Texture2D value for a
 * MatParam.
 *
 * @author rickard
 */
public class TexturePreviewSquare extends BasePreview {

    private TexturePanelSquare texturePanel;

    public TexturePreviewSquare(ShaderNodeVariable output) {
        super(output);
        String texture = output.getDefaultValue();
        texturePanel = new TexturePanelSquare(EditableMatDefFile.getAssetManager());
        MaterialProperty property = new MaterialProperty(output.getType(), output.getName(), texture != null ? texture : "");
        texturePanel.setProperty(property);
        texturePanel.registerChangeListener(this);
        add(texturePanel);
        setSize(texturePanel.getPreferredSize());
    }

    private static BufferedImage resize(BufferedImage image, int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
        Graphics2D g2d = (Graphics2D) bi.createGraphics();
        g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();
        return bi;
    }

}
