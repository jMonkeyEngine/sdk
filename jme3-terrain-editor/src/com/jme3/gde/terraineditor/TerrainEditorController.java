/*
 * Copyright (c) 2009-2010 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jme3.gde.terraineditor;

import com.jme3.asset.AssetManager;
import com.jme3.gde.core.assets.AssetDataObject;
import com.jme3.gde.core.assets.ProjectAssetManager;
import com.jme3.gde.core.scene.SceneApplication;
import com.jme3.gde.core.sceneexplorer.nodes.JmeNode;
import com.jme3.gde.core.sceneexplorer.nodes.JmeSpatial;
import com.jme3.gde.core.util.TerrainUtils;
import com.jme3.gde.terraineditor.tools.PaintTerrainToolAction;
import com.jme3.material.MatParam;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.ProgressMonitor;
import com.jme3.terrain.Terrain;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.SkyFactory;
import com.jme3.util.SkyFactory.EnvMapType;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import jme3tools.converters.ImageToAwt;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.Exceptions;

/**
 * Modifies the actual terrain in the scene.
 *
 * @author normenhansen, bowens
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class TerrainEditorController implements NodeListener {

    private final JmeSpatial jmeRootNode;
    private JmeSpatial selectedSpat;
    private Node terrainNode;
    private Node rootNode;
    private final AssetDataObject currentFileObject;
    private final TerrainEditorTopComponent topComponent;

    // texture settings
    public static final String DEFAULT_TERRAIN_TEXTURE = "com/jme3/gde/terraineditor/dirt.jpg";
    public static final float DEFAULT_TEXTURE_SCALE = 16.0625f;
    public static final int NUM_ALPHA_TEXTURES = 3;
    protected final int MAX_DIFFUSE = 12;
    protected final int MAX_TEXTURES = 16-NUM_ALPHA_TEXTURES; // 16 max (diffuse and normal), minus the ones we are reserving

    private boolean alphaLayersChanged = false;
    //private InstanceContent content;

    class TerrainSaveCookie implements SaveCookie {
        @Override
        public void save() throws IOException {
            if (alphaLayersChanged) {
                SceneApplication.getApplication().enqueue(() -> {
                    doSaveAlphaImages();
                    return null;
                });
                alphaLayersChanged = false;
            }
            SceneApplication.getApplication().enqueue(() -> {
                    currentFileObject.saveAsset();
                    return null;
                });
                        
        }
    }
    private final TerrainSaveCookie terrainSaveCookie = new TerrainSaveCookie();


    public TerrainEditorController(JmeSpatial jmeRootNode,
                                    AssetDataObject currentFileObject,
                                    TerrainEditorTopComponent topComponent)
    {
        this.jmeRootNode = jmeRootNode;
        rootNode = this.jmeRootNode.getLookup().lookup(Node.class);
        this.currentFileObject = currentFileObject;
        //this.content = content;
        terrainSaveCookie.rootNode = jmeRootNode;
        this.currentFileObject.setSaveCookie(terrainSaveCookie);
        this.topComponent = topComponent;
        this.jmeRootNode.addNodeListener(this);
    }

    public FileObject getCurrentFileObject() {
        return currentFileObject.getPrimaryFile();
    }

    public DataObject getCurrentDataObject() {
        return currentFileObject;
    }

    public void setNeedsSave(boolean state) {
        currentFileObject.setModified(state);
    }
    
    protected void setSelectedSpat(JmeSpatial selectedSpat) {

        if (this.selectedSpat == selectedSpat) {
            return;
        }
        if (this.selectedSpat != null) {
            this.selectedSpat.removePropertyChangeListener(this);
            this.selectedSpat.removeNodeListener(this);
        }
        this.selectedSpat = selectedSpat;
        if (selectedSpat != null) {
            selectedSpat.addPropertyChangeListener(this);
            selectedSpat.addNodeListener(this);
        }
    }

    public Node getTerrain(Spatial root) {
        if (terrainNode != null)
            return terrainNode;

        if (root == null)
            root = rootNode;

        // is this the terrain?
        if (root instanceof Terrain && root instanceof Node) {
            terrainNode = (Node)root;
            return terrainNode;
        }

        if (root instanceof Node n) {
            for (Spatial c : n.getChildren()) {
                if (c instanceof Node){
                    Node res = getTerrain(c);
                    if (res != null)
                        return res;
                }
            }
        }

        return terrainNode;
    }

    public JmeNode findJmeTerrain(JmeNode root) {
        if (root == null)
            root = (JmeNode) jmeRootNode;

        Node node = root.getLookup().lookup(Node.class);
        if (node != null && node instanceof Terrain && node instanceof Node) {
            return root;
        }

        if (node != null) {
            if (root.getChildren() != null) {
                for (org.openide.nodes.Node child : root.getChildren().getNodes() ) {
                    if (child instanceof JmeNode jmeNode) {
                        JmeNode res = findJmeTerrain(jmeNode);
                        if (res != null)
                            return res;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Painting happened and the alpha maps need saving.
     */
    public void alphaLayersChanged() {
        //if (!alphaLayersChanged)
        //    content.add(terrainSaveCookie);
        alphaLayersChanged = true;
    }

    /**
     * Perform the actual height modification on the terrain.
     * @param worldLoc the location in the world where the tool was activated
     * @param radius of the tool, terrain in this radius will be affected
     * @param heightFactor the amount to adjust the height by
     */
    public void doModifyTerrainHeight(Vector3f worldLoc, float radius, float heightFactor) {

        Terrain terrain = (Terrain) getTerrain(null);
        if (terrain == null)
            return;

        setNeedsSave(true);

        int radiusStepsX = (int) (radius / ((Node)terrain).getLocalScale().x);
        int radiusStepsZ = (int) (radius / ((Node)terrain).getLocalScale().z);

        float xStepAmount = ((Node)terrain).getLocalScale().x;
        float zStepAmount = ((Node)terrain).getLocalScale().z;

        List<Vector2f> locs = new ArrayList<>();
        List<Float> heights = new ArrayList<>();

        for (int z=-radiusStepsZ; z<radiusStepsZ; z++) {
            for (int x=-radiusStepsZ; x<radiusStepsX; x++) {

                float locX = worldLoc.x + (x*xStepAmount);
                float locZ = worldLoc.z + (z*zStepAmount);

                // see if it is in the radius of the tool
                if (isInRadius(locX-worldLoc.x,locZ-worldLoc.z,radius)) {
                    // adjust height based on radius of the tool
                    float h = calculateHeight(radius, heightFactor, locX-worldLoc.x, locZ-worldLoc.z);
                    // increase the height
                    locs.add(new Vector2f(locX, locZ));
                    heights.add(h);
                }
            }
        }

        // do the actual height adjustment
        terrain.adjustHeight(locs, heights);

        ((Node)terrain).updateModelBound(); // or else we won't collide with it where we just edited

    }

    /**
     * See if the X,Y coordinate is in the radius of the circle. It is assumed
     * that the "grid" being tested is located at 0,0 and its dimensions are 2*radius.
     * @param x
     * @param z
     * @param radius
     * @return
     */
    private boolean isInRadius(float x, float y, float radius) {
        Vector2f point = new Vector2f(x,y);
        // return true if the distance is less than equal to the radius
        return Math.abs(point.length()) <= radius;
    }

    /**
     * Interpolate the height value based on its distance from the center (how far along
     * the radius it is).
     * The farther from the center, the less the height will be.
     * This produces a linear height falloff.
     * @param radius of the tool
     * @param heightFactor potential height value to be adjusted
     * @param x location
     * @param z location
     * @return the adjusted height value
     */
    private float calculateHeight(float radius, float heightFactor, float x, float z) {
        float val = calculateRadiusPercent(radius, x, z);
        return heightFactor * val;
    }

    private float calculateRadiusPercent(float radius, float x, float z) {
         // find percentage for each 'unit' in radius
        Vector2f point = new Vector2f(x,z);
        float val = Math.abs(point.length()) / radius;
        val = 1f - val;
        return val;
    }

    public void cleanup() {
        terrainNode = null;
        rootNode = null;
    }

    /**
     * pre-calculate the terrain's entropy values
     */
    public void generateEntropies(final ProgressMonitor progressMonitor) {
        if (SceneApplication.getApplication().isOgl()) {
            Terrain terrain = (Terrain) getTerrain(null);
            if (terrain == null)
                return;

            terrain.generateEntropy(progressMonitor);
        } else {
            SceneApplication.getApplication().enqueue(() -> {
                generateEntropies(progressMonitor);
                return null;
            });
        }
    }

    /**
     * Get the scale of the texture at the specified layer.
     * Blocks on the OGL thread
     */
    public Float getTextureScale(final int layer) {
        if (SceneApplication.getApplication().isOgl()) {
            Terrain terrain = (Terrain) getTerrain(null);
            if (terrain == null)
                return 1f;
            MatParam matParam = terrain.getMaterial().getParam("DiffuseMap_"+layer+"_scale");
            if (matParam == null) {
                return -1f;
            }
            return (Float) matParam.getValue();
        } else {
            try {
                Float scale =
                    SceneApplication.getApplication().enqueue(() -> getTextureScale(layer)).get();
                    return scale;
            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return null;
    }


    /**
     * Set the scale of a texture at the specified layer
     * Blocks on the OGL thread
     */
    public void setTextureScale(final int layer, final float scale) {
        if (SceneApplication.getApplication().isOgl()) {
            Terrain terrain = (Terrain) getTerrain(null);
            if (terrain == null)
                return;
            terrain.getMaterial().setFloat("DiffuseMap_"+layer+"_scale", scale);
            setNeedsSave(true);
        } else {
            try {
                SceneApplication.getApplication().enqueue(() -> {
                    setTextureScale(layer, scale);
                    return null;
                }).get();
            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }


    /**
     * Get the diffuse texture at the specified layer.
     * Blocks on the GL thread!
     */
    public Texture getDiffuseTexture(final int layer) {
        if (SceneApplication.getApplication().isOgl()) {
            Terrain terrain = (Terrain) getTerrain(null);
            if (terrain == null)
                return null;
            MatParam matParam;
            if (layer == 0) {
                matParam = terrain.getMaterial().getParam("DiffuseMap");
            } else {
                matParam = terrain.getMaterial().getParam("DiffuseMap_"+layer);
            }

            if (matParam == null || matParam.getValue() == null) {
                return null;
            }
            Texture tex = (Texture) matParam.getValue();

            return tex;
        } else {
            try {
                Texture tex =
                    SceneApplication.getApplication().enqueue(() -> getDiffuseTexture(layer)).get();
                    return tex;
            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
            return null;
        }
    }

    private Texture doGetAlphaTexture(Terrain terrain, int alphaLayer) {
        if (terrain == null) {
            return null;
        }

        MatParam matParam = null;

        switch (alphaLayer) {
            case 0 -> matParam = terrain.getMaterial().getParam("AlphaMap");
            case 1 -> matParam = terrain.getMaterial().getParam("AlphaMap_1");
            case 2 -> matParam = terrain.getMaterial().getParam("AlphaMap_2");
            default -> throw new IllegalArgumentException("Invalid AlphaLayer");
        }

        if (matParam == null || matParam.getValue() == null) {
            return null;
        }
        Texture tex = (Texture) matParam.getValue();
        return tex;
    }


    /**
     * Set the diffuse texture at the specified layer.
     * Blocks on the GL thread
     * @param layer number to set the texture
     * @param texturePath if null, the default texture will be used
     */
    public void setDiffuseTexture(final int layer, final String texturePath) {
        String path = texturePath;
        if (texturePath == null || texturePath.equals(""))
            path = DEFAULT_TERRAIN_TEXTURE;

        Texture tex = SceneApplication.getApplication().getAssetManager().loadTexture(path);
        setDiffuseTexture(layer, tex);
    }

    /**
     * Set the diffuse texture at the specified layer.
     * Blocks on the GL thread
     * @param layer number to set the texture
     */
    public void setDiffuseTexture(final int layer, final Texture texture) {
        if (SceneApplication.getApplication().isOgl()) {
            texture.setWrap(WrapMode.Repeat);
            Terrain terrain = (Terrain) getTerrain(null);
            if (terrain == null)
                return;
            if (layer == 0)
                terrain.getMaterial().setTexture("DiffuseMap", texture);
            else
                terrain.getMaterial().setTexture("DiffuseMap_"+layer, texture);

            setNeedsSave(true);
        } else {
            try {
                SceneApplication.getApplication().enqueue(() -> {
                    setDiffuseTexture(layer, texture);
                    return null;
                }).get();
            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    /**
     * Remove a whole texture layer: diffuse and normal map
     * @param layer
     * @param texturePath
     */
    public void removeTextureLayer(final int layer) {
        if (SceneApplication.getApplication().isOgl()) {
            doRemoveDiffuseTexture(layer);
            doRemoveNormalMap(layer);
            doClearAlphaMap(layer);
        } else {
            try {
                SceneApplication.getApplication().enqueue(() -> {
                    removeTextureLayer(layer);
                    return null;
                }).get();
            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private void doRemoveDiffuseTexture(int layer) {
        Terrain terrain = (Terrain) getTerrain(null);
        if (terrain == null)
            return;
        if (layer == 0)
            terrain.getMaterial().clearParam("DiffuseMap");
        else
            terrain.getMaterial().clearParam("DiffuseMap_"+layer);

        setNeedsSave(true);
    }


    private void doRemoveNormalMap(int layer) {
        Terrain terrain = (Terrain) getTerrain(null);
        if (terrain == null)
            return;
        if (layer == 0)
            terrain.getMaterial().clearParam("NormalMap");
        else
            terrain.getMaterial().clearParam("NormalMap_"+layer);

        setNeedsSave(true);
    }

    private void doClearAlphaMap(int selectedTextureIndex) {
        Terrain terrain = (Terrain) getTerrain(null);
        if (terrain == null)
            return;

        int alphaIdx = selectedTextureIndex/4; // 4 = rgba = 4 textures
        int texIndex = selectedTextureIndex - ((selectedTextureIndex/4)*4); // selectedTextureIndex/4 is an int floor
        //selectedTextureIndex - (alphaIdx * 4)
        Texture tex = doGetAlphaTexture(terrain, alphaIdx);
        Image image = tex.getImage();

        PaintTerrainToolAction paint = new PaintTerrainToolAction();

        ColorRGBA color = ColorRGBA.Black;
        for (int y=0; y<image.getHeight(); y++) {
            for (int x=0; x<image.getWidth(); x++) {

                paint.manipulatePixel(image, x, y, color, false); // gets the color at that location (false means don't write to the buffer)
                switch (texIndex) {
                    case 0:
                        color.r = 0; break;
                    case 1:
                        color.g = 0; break;
                    case 2:
                        color.b = 0; break;
                    case 3:
                        color.a = 0; break;
                    default:
                        throw new IllegalArgumentException("Invalid texIndex " + texIndex);
                }
                color.clamp();
                paint.manipulatePixel(image, x, y, color, true); // set the new color
            }
        }
        image.getData(0).rewind();
        tex.getImage().setUpdateNeeded();
        setNeedsSave(true);
        alphaLayersChanged();
    }

    /**
     * Get the normal map texture at the specified layer.
     * Run this on the GL thread!
     */
    public Texture getNormalMap(final int layer) {
        if (SceneApplication.getApplication().isOgl()) {
            Terrain terrain = (Terrain) getTerrain(null);
            if (terrain == null)
                return null;
            MatParam matParam;
            if (layer == 0) {
                matParam = terrain.getMaterial().getParam("NormalMap");
            } else {
                matParam = terrain.getMaterial().getParam("NormalMap_"+layer);
            }

            if (matParam == null || matParam.getValue() == null) {
                return null;
            }
            Texture tex = (Texture) matParam.getValue();
            return tex;
        } else {
            try {
                Texture tex =
                    SceneApplication.getApplication().enqueue(() -> getNormalMap(layer)).get();
                    return tex;
            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return null;
    }

    /**
     * Set the normal map at the specified layer.
     * Blocks on the GL thread
     */
    public void setNormalMap(final int layer, final String texturePath) {
        if (texturePath != null) {
            Texture tex = SceneApplication.getApplication().getAssetManager().loadTexture(texturePath);
            setNormalMap(layer, tex);
        } else {
            setNormalMap(layer, (Texture)null);
        }
        /*try {
            SceneApplication.getApplication().enqueue(new Callable() {
                public Object call() throws Exception {
                    doSetNormalMap(layer, texturePath);
                    return null;
                }
            }).get();
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        } catch (ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }*/
    }

    /**
     * Set the normal map texture at the specified layer
     */
    public void setNormalMap(final int layer, final Texture texture) {
        if (SceneApplication.getApplication().isOgl()) {
            Terrain terrain = (Terrain) getTerrain(null);
            if (terrain == null)
                return;
            if (texture == null) {
                // remove the texture if it is null
                if (layer == 0)
                    terrain.getMaterial().clearParam("NormalMap");
                else
                    terrain.getMaterial().clearParam("NormalMap_"+layer);
                return;
            }

            texture.setWrap(WrapMode.Repeat);

            if (layer == 0)
                terrain.getMaterial().setTexture("NormalMap", texture);
            else
                terrain.getMaterial().setTexture("NormalMap_"+layer, texture);

            setNeedsSave(true);
        } else {
            try {
                SceneApplication.getApplication().enqueue(() -> {
                    setNormalMap(layer, texture);
                    return null;
                }).get();
            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    // blocks on GL thread until terrain is created
    public Terrain createTerrain(final Node parent,
                                final int totalSize,
                                final int patchSize,
                                final int alphaTextureSize,
                                final float[] heightmapData,
                                final String sceneName,
                                final JmeSpatial jmeNodeParent) throws IOException
    {
        try {
            Terrain terrain =
            SceneApplication.getApplication().enqueue(() -> {
                //return doCreateTerrain(parent, totalSize, patchSize, alphaTextureSize, heightmapData, sceneName, jmeNodeParent);
                AddTerrainAction a = new AddTerrainAction();
                return (Terrain) a.doCreateTerrain(parent, totalSize, patchSize, alphaTextureSize, heightmapData, sceneName, jmeRootNode);
            }).get();
            return terrain;
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }

        return null; // if failed
    }

    /**
     * Save the terrain's alpha maps to disk, in the Textures/terrain-alpha/ directory
     * @throws IOException
     */
    private synchronized void doSaveAlphaImages() {

        terrainNode = null;
        // re-look it up
        Terrain terrain = (Terrain)getTerrain(rootNode);


        AssetManager manager = SceneApplication.getApplication().getAssetManager();
        String assetFolder = null;
        if (manager != null && manager instanceof ProjectAssetManager)
            assetFolder = ((ProjectAssetManager)manager).getAssetFolderName();
        if (assetFolder == null)
            throw new IllegalStateException("AssetManager was not a ProjectAssetManager. Could not locate image save directories.");


        Texture alpha1 = doGetAlphaTexture(terrain, 0);
        BufferedImage bi1 = ImageToAwt.convert(alpha1.getImage(), false, true, 0);
        File imageFile1 = new File(assetFolder+"/"+alpha1.getKey().getName());
        Texture alpha2 = doGetAlphaTexture(terrain, 1);
        BufferedImage bi2 = ImageToAwt.convert(alpha2.getImage(), false, true, 0);
        File imageFile2 = new File(assetFolder+"/"+alpha2.getKey().getName());
        Texture alpha3 = doGetAlphaTexture(terrain, 2);
        BufferedImage bi3 = ImageToAwt.convert(alpha3.getImage(), false, true, 0);
        File imageFile3 = new File(assetFolder+"/"+alpha3.getKey().getName());

        ImageOutputStream ios1 = null;
        ImageOutputStream ios2 = null;
        ImageOutputStream ios3 = null;
        try {
            ios1 = new FileImageOutputStream(imageFile1);
            ios2 = new FileImageOutputStream(imageFile2);
            ios3 = new FileImageOutputStream(imageFile3);
            ImageIO.write(bi1, "png", ios1);
            ImageIO.write(bi2, "png", imageFile2);
            ImageIO.write(bi3, "png", imageFile3);
        } catch (IOException ex) {
            System.out.println("Failed saving alphamaps");
            System.out.println("    "+imageFile1);
            System.out.println("    "+imageFile2);
            System.out.println("    "+imageFile3);
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                if (ios1 != null)
                    ios1.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            try {
                if (ios2 != null)
                    ios2.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            try {
                if (ios3 != null)
                    ios3.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

    }

    /**
     * Create a skybox with 6 textures.
     * Blocking call.
     */
    protected Spatial createSky(final Node parent,
                                final Texture west,
                                final Texture east,
                                final Texture north,
                                final Texture south,
                                final Texture top,
                                final Texture bottom,
                                final Vector3f normalScale)
    {
        try {
            Spatial sky =
            SceneApplication.getApplication().enqueue(() -> doCreateSky(parent, west, east, north, south, top, bottom, normalScale)).get();
            return sky;
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null; // if failed
    }

    private Spatial doCreateSky(Node parent,
                                Texture west,
                                Texture east,
                                Texture north,
                                Texture south,
                                Texture top,
                                Texture bottom,
                                Vector3f normalScale)
    {
        AssetManager manager = SceneApplication.getApplication().getAssetManager();
        Spatial sky = SkyFactory.createSky(manager, west, east, north, south, top, bottom, normalScale);
        parent.attachChild(sky);
        return sky;
    }

    /**
     * Create a skybox with a single texture.
     * Blocking call.
     */
    protected Spatial createSky(final Node parent,
                                final Texture texture,
                                final boolean useSpheremap,
                                final Vector3f normalScale)
    {
        try {
            Spatial sky =
            SceneApplication.getApplication().enqueue(() -> doCreateSky(parent, texture, useSpheremap, normalScale)).get();
            return sky;
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null; // if failed
    }

    private Spatial doCreateSky(Node parent,
                                Texture texture,
                                boolean useSpheremap,
                                Vector3f normalScale)
    {
        AssetManager manager = SceneApplication.getApplication().getAssetManager();
        Spatial sky = SkyFactory.createSky(manager, texture, normalScale, useSpheremap ? EnvMapType.SphereMap : EnvMapType.CubeMap);
        parent.attachChild(sky);
        return sky;
    }

    /**
     * Is there a texture at the specified layer?
     * Blocks on ogl thread
     */
    public boolean hasTextureAt(final int i) {
        if (SceneApplication.getApplication().isOgl()) {
            Texture tex = getDiffuseTexture(i);
            return tex != null;
        } else {
            try {
                Boolean result =
                    SceneApplication.getApplication().enqueue(() -> hasTextureAt(i)).get();
                    return result;
            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
            return false;
        }
    }

    /**
     * Enable/disable the add and remove texture buttons based
     * on how many textures are currently being used.
     */
    protected void enableTextureButtons() {
        //SceneApplication.getApplication().enqueue(new Callable<Object>() {
        //    public Object call() throws Exception {
                final int numAvailable = MAX_TEXTURES-getNumUsedTextures();
                final boolean add = getNumDiffuseTextures() < MAX_DIFFUSE && numAvailable > 0;
                final boolean remove = getNumDiffuseTextures() > 1;

                java.awt.EventQueue.invokeLater(() -> {
                    topComponent.enableAddTextureButton(add);
                    topComponent.enableRemoveTextureButton(remove);
                    topComponent.updateTextureCountLabel(numAvailable);
                    topComponent.setAddNormalTextureEnabled(numAvailable>0);
                });
        //        return null;
        //    }
        //});

    }

    /**
     * How many diffuse textures are being used.
     * Blocking call on GL thread
     */
    protected int getNumDiffuseTextures() {
        if (SceneApplication.getApplication().isOgl()) {
            Terrain terrain = (Terrain) getTerrain(null);
            if (terrain == null)
                return 0;

            int count = 0;

            for (int i=0; i<MAX_TEXTURES; i++) {
                Texture tex = getDiffuseTexture(i);
                if (tex != null)
                    count++;
            }
            return count;
        } else {
            try {
                Integer count =
                  SceneApplication.getApplication().enqueue(() -> getNumDiffuseTextures()).get();
                return count;
            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
            return -1;
        }
    }

    /**
     * How many textures are currently being used.
     * Blocking call on GL thread
     */
    protected int getNumUsedTextures() {
        if (SceneApplication.getApplication().isOgl()) {
            Terrain terrain = (Terrain) getTerrain(null);
            if (terrain == null)
                return 0;

            int count = 0;

            for (int i=0; i<MAX_TEXTURES; i++) {
                Texture tex = getDiffuseTexture(i);
                if (tex != null)
                    count++;
                tex = getNormalMap(i);
                if (tex != null)
                    count++;
            }
            return count;
        } else {
            try {
                Integer count =
                  SceneApplication.getApplication().enqueue(() -> getNumUsedTextures()).get();
                return count;
            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
            return -1;
        }
    }

    public boolean isTriPlanarEnabled() {
        if (SceneApplication.getApplication().isOgl()) {
            Terrain terrain = (Terrain) getTerrain(null);
            if (terrain == null)
                return false;
            MatParam param = terrain.getMaterial().getParam("useTriPlanarMapping");
            if (param != null)
                return (Boolean)param.getValue();

            return false;
        } else {
            try {
                Boolean isEnabled =
                SceneApplication.getApplication().enqueue(() -> isTriPlanarEnabled()).get();
                return isEnabled;
            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
            return false;
        }
    }

    /**
     * Also adjusts the scale. Normal texture scale uses texture coordinates,
     * which are each 1/(total size of the terrain). But for tri planar it doesn't
     * use texture coordinates, so we need to re-calculate it to be the same scale.
     * @param enabled
     * @param terrainTotalSize
     */
    public void setTriPlanarEnabled(final boolean enabled) {
        if (SceneApplication.getApplication().isOgl()) {
            Terrain terrain = (Terrain) getTerrain(null);
            if (terrain == null)
                return;
            terrain.getMaterial().setBoolean("useTriPlanarMapping", enabled);

            float size = terrain.getTerrainSize();

            if (enabled) {
                for (int i=0; i<getNumUsedTextures(); i++) {
                    float scale = 1f/(size/getTextureScale(i));
                    setTextureScale(i, scale);
                }
            } else {
                for (int i=0; i<getNumUsedTextures(); i++) {
                    float scale = (size*getTextureScale(i));
                    setTextureScale(i, scale);
                }
            }

            setNeedsSave(true);
        } else {
            try {
                SceneApplication.getApplication().enqueue(() -> {
                    setTriPlanarEnabled(enabled);
                    return null;
                }).get();
            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

     protected void setShininess(final float shininess) {
        if (SceneApplication.getApplication().isOgl()) {
            Terrain terrain = (Terrain) getTerrain(null);
            if (terrain == null)
                return;

            terrain.getMaterial().setFloat("Shininess", shininess);

            setNeedsSave(true);
        } else {
            SceneApplication.getApplication().enqueue(() -> {
                setShininess(shininess);
                return null;
            });
        }
    }

      protected float getShininess() {
        if (SceneApplication.getApplication().isOgl()) {
            Terrain terrain = (Terrain) getTerrain(null);
            if (terrain == null)
                return 0;

            MatParam param = terrain.getMaterial().getParam("Shininess");
            if (param != null)
                return (Float)param.getValue();

            return 0;
        } else {
            try {
                Float shininess = SceneApplication.getApplication().enqueue(() -> getShininess()).get();
                return shininess;
            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
            return 0;
        }
    }

    protected void setWardIsoEnabled(final boolean enabled) {
        if (SceneApplication.getApplication().isOgl()) {
            Terrain terrain = (Terrain) getTerrain(null);
            if (terrain == null)
                return;
            terrain.getMaterial().setBoolean("WardIso", enabled);

            setNeedsSave(true);
        } else {
            try {
                SceneApplication.getApplication().enqueue(() -> {
                    setWardIsoEnabled(enabled);
                    return null;
                }).get();
            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    protected boolean isWardIsoEnabled() {
        if (SceneApplication.getApplication().isOgl()) {
            Terrain terrain = (Terrain) getTerrain(null);
            if (terrain == null)
                return false;
            MatParam param = terrain.getMaterial().getParam("WardIso");
            if (param != null)
                return (Boolean)param.getValue();

            return false;
        } else {
            try {
                Boolean isEnabled =
                SceneApplication.getApplication().enqueue(() -> isWardIsoEnabled()).get();
                return isEnabled;
            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
            return false;
        }
    }


    @Override
    public void propertyChange(PropertyChangeEvent ev) {
        if (ev.getNewValue() == null && ev.getOldValue() != null) {
            topComponent.clearTextureTable(); // terrain deleted
            terrainNode = null;
        }
    }

    @Override
    public void childrenAdded(NodeMemberEvent ev) {
        boolean isTerrain = false;
        for(org.openide.nodes.Node n : ev.getSnapshot()) {
            Node node = n.getLookup().lookup(Node.class);
            if (node instanceof Terrain) {
                isTerrain = true;
                break;
            }
        }
        if (isTerrain)
            topComponent.reinitTextureTable();
    }

    @Override
    public void childrenRemoved(NodeMemberEvent ev) {

    }

    @Override
    public void childrenReordered(NodeReorderEvent ev) {
    }

    @Override
    public void nodeDestroyed(NodeEvent ev) {

    }

    /**
     * Re-attach the camera to the LOD control.
     * Called when the scene is opened and will only
     * update the control if there is already a terrain present in
     * the scene.
     */
    protected void setTerrainLodCamera() {
        Camera camera = SceneApplication.getApplication().getCamera();
        Node root = jmeRootNode.getLookup().lookup(Node.class);
        TerrainUtils.enableLodControl(camera, root);
    }



}
