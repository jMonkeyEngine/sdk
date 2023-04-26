/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jme3.gde.assetbrowser;

import com.jme3.asset.MaterialKey;
import com.jme3.gde.assetbrowser.widgets.AssetPreviewWidget;
import com.jme3.gde.core.assets.ProjectAssetManager;
import com.jme3.gde.core.editor.icons.Icons;
import com.jme3.gde.core.icons.IconList;
import com.jme3.gde.core.scene.PreviewRequest;
import com.jme3.gde.core.scene.SceneApplication;
import com.jme3.gde.core.scene.SceneListener;
import com.jme3.gde.core.scene.SceneRequest;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.util.mikktspace.MikktspaceTangentGenerator;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import jme3tools.converters.ImageToAwt;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author rickard
 */
public class PreviewUtil {
    
    private ProjectAssetManager assetManager;
    
    public PreviewUtil(ProjectAssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public Icon getOrCreateTexturePreview(String asset) {
        final var icon = tryGetPreview(asset);
        if(icon != null) {
            return icon;
        }
        System.out.println("creating preview ");
        Texture texture = assetManager.loadTexture(asset);
        Image image = texture.getImage();

        
        BufferedImage buff = ImageToAwt.convert(image, false, false, 0);
        
        BufferedImage scaled = scaleDown(buff, 150, 150);
        BufferedImage noAlpha = convertImage(scaled);
        savePreview(assetManager, asset.split("\\.")[0], noAlpha);
        return new ImageIcon(noAlpha);
    }

    public Icon getOrCreateMaterialPreview(String asset, AssetPreviewWidget widget) {
        final var icon = tryGetPreview(asset);
        if(icon != null) {
            return icon;
        }
        
        Material mat = assetManager.loadMaterial(asset);

        Box boxMesh = new Box(1.75f, 1.75f, 1.75f);
        Geometry box = new Geometry("previewBox", boxMesh);
        box.setMaterial(mat);
        PreviewListener listener = new PreviewListener(assetManager, mat.getAssetName().split("\\.")[0], widget);
        SceneApplication.getApplication().addSceneListener(listener);
        SceneApplication.getApplication().enqueue(() -> {
            SceneApplication.getApplication().getRenderManager().preloadScene(box);
            java.awt.EventQueue.invokeLater(() -> {
                
                
                box.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.DEG_TO_RAD * 30, Vector3f.UNIT_X).multLocal(new Quaternion().fromAngleAxis(FastMath.QUARTER_PI, Vector3f.UNIT_Y)));
                MikktspaceTangentGenerator.generate(box);
                System.out.println("preview requested " + asset);
                PreviewRequest request = new PreviewRequest(listener, box, 150, 150);
                request.getCameraRequest().setLocation(new Vector3f(0, 0, 7));
                request.getCameraRequest().setLookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
                SceneApplication.getApplication().createPreview(request);
            });
        });
        return IconList.asset;
    }
    
    private Icon tryGetPreview(String asset) {
        final var assetPath = assetManager.getAbsoluteAssetPath(asset);
        
        FileTime assetModificationTime = getAssetModificationTime(assetPath);
        
        File previewFile = loadPreviewFile(assetManager, asset.split("\\.")[0]);
        
        if(previewFile != null && assetModificationTime != null) {
            Path previewPath = previewFile.toPath();
            try {
                BasicFileAttributes previewAttributes = Files.readAttributes(
                        previewPath, BasicFileAttributes.class);
                FileTime previewCreationTime = previewAttributes.creationTime();
                
                if(previewCreationTime.compareTo(assetModificationTime) > 0) {
                    System.out.println("existing preview OK " + previewFile);
                    BufferedImage image = ImageIO.read(previewFile);
                    if(image != null) {
                        return new ImageIcon(image);
                    }
                    System.out.println("previewFile is null " + previewFile);
                }
                
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return null;
    }
    
    public Icon getOrCreateModelPreview(String asset, AssetPreviewWidget widget) {
        final var icon = tryGetPreview(asset);
        if(icon != null) {
            return icon;
        }
        
        Material unshaded = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        unshaded.setColor("Color", ColorRGBA.Red);
        
        Spatial spatial = assetManager.loadModel(asset);

        recurseApplyDefaultMaterial(spatial, unshaded);
        
        PreviewListener listener = new PreviewListener(assetManager, asset.split("\\.")[0], widget);
        SceneApplication.getApplication().addSceneListener(listener);
        SceneApplication.getApplication().enqueue(() -> {
            SceneApplication.getApplication().getRenderManager().preloadScene(spatial);
            java.awt.EventQueue.invokeLater(() -> {
                System.out.println("preview requested " + asset);
                PreviewRequest request = new PreviewRequest(listener, spatial, 150, 150);
                request.getCameraRequest().setLocation(new Vector3f(0, 0, 7));
                request.getCameraRequest().setLookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
                SceneApplication.getApplication().createPreview(request);
            });
        });
        return IconList.asset;
    }
    
    private void recurseApplyDefaultMaterial(Spatial spatial, Material material) {
        if(spatial instanceof Node) {
            ((Node) spatial).getChildren().forEach(child -> recurseApplyDefaultMaterial(child, material));
        } else if (spatial instanceof Geometry) {
            if(((Geometry) spatial).getMaterial() == null) {
                spatial.setMaterial(material);
            }
        }
    }
    
    private FileTime getAssetModificationTime(String assetPath) {
        if(assetPath == null) {
            return null;
        }
        Path path = new File(assetPath).toPath();

        try {
            // creating BasicFileAttributes class object using
            // readAttributes method
            BasicFileAttributes file_att = Files.readAttributes(
                    path, BasicFileAttributes.class);
            return file_att.lastModifiedTime();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }
    
    private File loadPreviewFile(ProjectAssetManager assetManager, String id) {
        FileObject fileObject = assetManager.getProject().getProjectDirectory();
        return new File(fileObject.getPath() + "/.assetBrowser/", id + ".jpg");
    }
    
    private BufferedImage loadPreview(ProjectAssetManager assetManager, String id) {
        try {
            return ImageIO.read(loadPreviewFile(assetManager, id));
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }
    
    private void savePreview(ProjectAssetManager assetManager, String id, BufferedImage preview) {
        FileObject fileObject = assetManager.getProject().getProjectDirectory();
        String[] fileSections = id.split("/");
        String fileName = fileSections[fileSections.length-1];
        System.out.println("saving " + id.substring(0, id.length() - fileName.length()) + "    " + fileName + ".jpg");
        File path = new File(fileObject.getPath() + "/.assetBrowser/" + id.substring(0, id.length() - fileName.length()));
        File file = new File(path, fileName + ".jpg");
        try {
            path.mkdirs();
            file.createNewFile();
            ImageIO.write(preview, "jpg", file);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private BufferedImage scaleDown(BufferedImage sourceImage, int targetWidth, int targetHeight) {
        int sourceWidth = sourceImage.getWidth();
        int sourceHeight = sourceImage.getHeight();

        BufferedImage targetImage = new BufferedImage(targetWidth, targetHeight, sourceImage.getType());

        Graphics2D g = targetImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(sourceImage, 0, 0, targetWidth, targetHeight, 0, 0, sourceWidth, sourceHeight, null);
        g.dispose();

        return targetImage;
    }
    
    private class PreviewListener implements SceneListener {

        final AssetPreviewWidget widget;
        final ProjectAssetManager assetManager;
        private String assetName;

        public PreviewListener(ProjectAssetManager assetManager, String assetName, AssetPreviewWidget widget) {
            this.widget = widget;
            this.assetManager = assetManager;
            this.assetName = assetName;
        }

        @Override
        public void sceneOpened(SceneRequest request) {
        }

        @Override
        public void sceneClosed(SceneRequest request) {
        }

        @Override
        public void previewCreated(PreviewRequest request) {
            if (request.getRequester() == this) {
                final BufferedImage image = request.getImage();
                final BufferedImage noAlpha = convertImage(image);
                System.out.println("preview generated " + image);
                java.awt.EventQueue.invokeLater(() -> {
                    widget.setPreviewImage(Icons.error);
                    savePreview(assetManager, assetName, noAlpha);
                    widget.invalidate();
                    widget.revalidate();
                    widget.updateUI();
//                    ((DefaultTreeModel)tree.getModel()).reload();
//                    tree.revalidate();
//                    tree.repaint();
                            });
            }
        }
    };

    private static BufferedImage convertImage(BufferedImage file) {
        Color bgColor = Color.WHITE;
        int width = file.getWidth();
        int height = file.getHeight();
        BufferedImage background = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = background.createGraphics();
        g.setColor(bgColor);
        g.fillRect(0, 0, width, height);
        g.drawImage(file, 0, 0, null);
        g.dispose();
        return background;
    }
}