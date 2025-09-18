/*
 *  Copyright (c) 2009-2025 jMonkeyEngine
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
package com.jme3.gde.materials;

import com.jme3.asset.AssetNotFoundException;
import com.jme3.asset.MaterialKey;
import com.jme3.gde.core.assets.ProjectAssetManager;
import com.jme3.gde.core.scene.PreviewRequest;
import com.jme3.gde.core.scene.SceneApplication;
import com.jme3.gde.core.scene.SceneListener;
import com.jme3.gde.core.scene.SceneRequest;
import com.jme3.gde.core.editor.icons.Icons;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RendererException;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.mikktspace.MikktspaceTangentGenerator;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Handles rendering of materials in preview widgets of Material and Shader Node
 * editor.
 *
 * @author Nehon
 */
public class MaterialPreviewRenderer implements SceneListener {

    private Geometry sphere;
    private Geometry box;
    private Geometry quad;
    private Geometry teapot;
    private Geometry currentGeom;
    private Material currentMaterial;
    private boolean init = false;
    private final JLabel label;
    private final ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(5);
    private boolean previewRequested;

    public enum DisplayType {

        Sphere,
        Box,
        Quad,
        Teapot
    }

    public MaterialPreviewRenderer(JLabel label) {
        this.label = label;
    }

    private void init() {
        SceneApplication.getApplication().addSceneListener(this);
        Sphere sphMesh = new Sphere(32, 32, 2.5f);
        sphMesh.setTextureMode(Sphere.TextureMode.Projected);
        sphMesh.updateGeometry(32, 32, 2.5f, false, false);
        Logger log = Logger.getLogger(MikktspaceTangentGenerator.class.getName());
        log.setLevel(Level.SEVERE);
        sphere = new Geometry("previewSphere", sphMesh);
        sphere.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.QUARTER_PI, Vector3f.UNIT_X));
        MikktspaceTangentGenerator.generate(sphere);

        Box boxMesh = new Box(1.75f, 1.75f, 1.75f);
        box = new Geometry("previewBox", boxMesh);
        box.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.DEG_TO_RAD * 30, Vector3f.UNIT_X).multLocal(new Quaternion().fromAngleAxis(FastMath.QUARTER_PI, Vector3f.UNIT_Y)));
        MikktspaceTangentGenerator.generate(box);

        Quad quadMesh = new Quad(4.5f, 4.5f);
        quad = new Geometry("previewQuad", quadMesh);
        quad.setLocalTranslation(new Vector3f(-2.25f, -2.25f, 0));
        MikktspaceTangentGenerator.generate(quad);

        teapot = (Geometry) SceneApplication.getApplication().getAssetManager()
                .loadModel("Models/Teapot/Teapot.obj");
        teapot.scale(3.5f);
        teapot.rotate(FastMath.PI, -FastMath.QUARTER_PI * 0.5f, -0.0f);
        teapot.setLocalTranslation(new Vector3f(-0.5f, 1.75f, 0));
        MikktspaceTangentGenerator.generate(teapot);

        currentGeom = sphere;
        init = true;
    }

    @SuppressWarnings("unchecked")
    public void showMaterial(final ProjectAssetManager assetManager, final String materialFileName) {
        if (!init) {
            init();
        }
        exec.execute(() -> {
            MaterialKey key = new MaterialKey(assetManager.getRelativeAssetPath(materialFileName));
            assetManager.deleteFromCache(key);
            Material mat = assetManager.loadAsset(key);
            if (mat != null) {
                showMaterial(mat);
            }
        });

    }

    public void showMaterial(final Material m) {
        showMaterial(m, null);
    }

    public void showMaterial(final Material m, final String techniqueName) {
        if (!init) {
            init();
        }
        SceneApplication.getApplication().enqueue(() -> {
            if (techniqueName != null) {
                try {
                    m.selectTechnique(techniqueName, SceneApplication.getApplication().getRenderManager());
                } catch (Exception e) {
                    //
                }
            }
            final Material mat = reloadMaterial(m);
            if (mat != null) {
                java.awt.EventQueue.invokeLater(() -> {
                    currentMaterial = mat;
                    currentGeom.setMaterial(mat);
                    try {
                        if (currentGeom.getMaterial() != null) {
                            PreviewRequest request = new PreviewRequest(MaterialPreviewRenderer.this, currentGeom, label.getWidth(), label.getHeight());
                            request.getCameraRequest().setLocation(new Vector3f(0, 0, 7));
                            request.getCameraRequest().setLookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
                            SceneApplication.getApplication().createPreview(request);
                        }
                    } catch (Exception e) {
                        java.awt.EventQueue.invokeLater(() -> {
                            label.setIcon(Icons.error);
                        });
                        smartLog("Error rendering material{0}", e.getMessage());
                    }
                });

            }
            return mat;
        });
    }

    private static int lastErrorHash = 0;

    private void smartLog(String expText, String message) {
        int hash = message.hashCode();
        if (hash != lastErrorHash) {
            Logger.getLogger(MaterialPreviewRenderer.class.getName()).log(Level.SEVERE, expText, message);
            lastErrorHash = hash;
        }
    }

    public Material reloadMaterial(Material mat) {
        Material dummy;
        try {
            ((ProjectAssetManager) mat.getMaterialDef().getAssetManager()).clearCache();

            //creating a dummy mat with the mat def of the mat to reload
            dummy = new Material(mat.getMaterialDef());

            for (MatParam matParam : mat.getParams()) {
                dummy.setParam(matParam.getName(), matParam.getVarType(), matParam.getValue());
            }
            if (mat.getActiveTechnique() != null) {
                dummy.selectTechnique(mat.getActiveTechnique().getDef().getName(), SceneApplication.getApplication().getRenderManager());
            }
            dummy.getAdditionalRenderState().set(mat.getAdditionalRenderState());

            //creating a dummy geom and assigning the dummy material to it
            Geometry dummyGeom = new Geometry("dummyGeom", new Box(1f, 1f, 1f));
            dummyGeom.setMaterial(dummy);

            //preloading the dummyGeom, this call will compile the shader again
            SceneApplication.getApplication().getRenderManager().preloadScene(dummyGeom);
        } catch (RendererException e) {
            //compilation error, the shader code will be output to the console
            //the following code will output the error
            //System.err.println(e.getMessage());
            //Logger.getLogger(MaterialDebugAppState.class.getName()).log(Level.SEVERE, e.getMessage());
            smartLog("{0}", e.getMessage());

            java.awt.EventQueue.invokeLater(() -> label.setIcon(Icons.error));
            return null;
        } catch (NullPointerException npe) {
            //utterly bad, but for some reason I get random NPE here and can't figure out why so to avoid bigger issues, I just catch it.
            //the printStackTrace is intended, it will show up in debug mode, but won't be displayed in standzrd mode
            npe.printStackTrace();
            return null;
        } catch (AssetNotFoundException a) {
            smartLog("Could not fully load Shader: Missing File: {0}", a.getMessage());
            return null;
        }

        //Logger.getLogger(MaterialDebugAppState.class.getName()).log(Level.INFO, "Material succesfully reloaded");
        //System.out.println("Material succesfully reloaded");
        return dummy;
    }

    public void switchDisplay(DisplayType type) {
        switch (type) {
            case Box -> currentGeom = box;
            case Sphere -> currentGeom = sphere;
            case Quad -> currentGeom = quad;
            case Teapot -> currentGeom = teapot;
        }
        showMaterial(currentMaterial);
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
            final ImageIcon icon = new ImageIcon(request.getImage());
            java.awt.EventQueue.invokeLater(() -> {
                label.setIcon(icon);
            });
            previewRequested = false;
        }
    }

    public void cleanUp() {
        SceneApplication.getApplication().removeSceneListener(this);
        exec.shutdownNow();
    }

    public boolean isPreviewRequested() {
        return previewRequested;
    }

    /**
     * A more lightweight refresh than showMaterials that doesn't rebuild the
     * material
     */
    public void refreshOnly() {
        if(previewRequested) {
            return;
        }
        SceneApplication.getApplication().enqueue((Callable<Object>) () -> {
            if (currentGeom.getMaterial() != null) {
                PreviewRequest request = new PreviewRequest(MaterialPreviewRenderer.this, currentGeom, label.getWidth(), label.getHeight());
                request.getCameraRequest().setLocation(new Vector3f(0, 0, 7));
                request.getCameraRequest().setLookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
                SceneApplication.getApplication().createPreview(request);
            }
            return null;
        });
        previewRequested = true;
    }

}
