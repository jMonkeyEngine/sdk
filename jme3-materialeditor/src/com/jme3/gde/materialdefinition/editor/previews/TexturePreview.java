/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jme3.gde.materialdefinition.editor.previews;

import com.jme3.gde.materialdefinition.EditableMatDefFile;
import com.jme3.shader.ShaderNodeVariable;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import org.openide.util.Exceptions;

/**
 * Component for previewing and changing a default Texture2D value for a
 * MatParam.
 *
 * @author rickard
 */
public class TexturePreview extends BasePreview implements MouseListener {

    public TexturePreview(ShaderNodeVariable output) {
        super(output);
        String texture = output.getDefaultValue();
        if (texture != null) {
            try {
                texture = texture.replace("\"", "");
                String path = EditableMatDefFile.getAssetManager().getAbsoluteAssetPath(texture);
                if (path != null) {
                    BufferedImage image = ImageIO.read(new File(path));
                    image = resize(image, 20, 20);
                    Icon icon = new ImageIcon(image);
                    JLabel label = new JLabel(icon);
//                    setBorder(BorderFactory.createEmptyBorder( -5 /*top*/, 0, 0, 0 ));
                    label.setVerticalAlignment(JLabel.TOP);
                    setAlignmentY(TOP_ALIGNMENT);
                    add(label);
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            setBackground(Color.BLACK);
        }
        setSize(20, 20);
    }

    public static BufferedImage resize(BufferedImage image, int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
        Graphics2D g2d = (Graphics2D) bi.createGraphics();
        g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();
        return bi;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
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
