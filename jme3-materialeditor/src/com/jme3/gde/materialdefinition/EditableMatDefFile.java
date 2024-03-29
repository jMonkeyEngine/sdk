/*
 *  Copyright (c) 2009-2018 jMonkeyEngine
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
 */package com.jme3.gde.materialdefinition;

import com.jme3.asset.AssetKey;
import com.jme3.gde.core.assets.ProjectAssetManager;
import com.jme3.gde.core.errorreport.ExceptionUtils;
import com.jme3.gde.core.scene.SceneApplication;
import com.jme3.gde.materialdefinition.fileStructure.MatDefBlock;
import com.jme3.gde.materialdefinition.fileStructure.TechniqueBlock;
import com.jme3.gde.materialdefinition.fileStructure.UberStatement;
import com.jme3.gde.materialdefinition.fileStructure.leaves.LeafStatement;
import com.jme3.gde.materialdefinition.navigator.node.MatDefNode;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.material.TechniqueDef;
import com.jme3.material.plugins.J3MLoader;
import com.jme3.material.plugins.MatParseException;
import com.jme3.shader.Glsl100ShaderGenerator;
import com.jme3.shader.Glsl150ShaderGenerator;
import com.jme3.shader.Shader;
import com.jme3.shader.ShaderGenerator;
import com.jme3.util.blockparser.BlockLanguageParser;
import com.jme3.util.blockparser.Statement;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import org.openide.cookies.EditorCookie;
import org.openide.explorer.ExplorerManager;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Node;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.WeakListeners;

/**
 * This is the MatDef representation in the editor. It will update the file with
 * any changes.
 * 
 * @author Nehon
 */
public class EditableMatDefFile {

    private FileObject matDefFile;
    private final MatDefDataObject obj;
    private Material material;
    private MatDefBlock matDefStructure;
    private TechniqueBlock currentTechnique;
    private MaterialDef materialDef;
    private static ProjectAssetManager assetManager;
    private ShaderGenerator glsl100;
    private ShaderGenerator glsl150;    
    private final static String GLSL100 = "GLSL100";    
    private Lookup lookup;
    private boolean loaded = false;
    private boolean dirty = false;
    private final MatStructChangeListener changeListener = new MatStructChangeListener(this);

    public EditableMatDefFile(Lookup lookup) {
        obj = lookup.lookup(MatDefDataObject.class);
        load(lookup);

    }

    public final void load(Lookup lookup) {
        loaded = false;
        this.matDefFile = obj.getPrimaryFile();
        this.assetManager = lookup.lookup(ProjectAssetManager.class);
        this.glsl100 = new Glsl100ShaderGenerator(assetManager);
        this.glsl150 = new Glsl150ShaderGenerator(assetManager);
        this.lookup = lookup;

        if (matDefStructure != null) {
            obj.getLookupContents().remove(matDefStructure);
            matDefStructure = null;
        }
        if (materialDef != null) {
            obj.getLookupContents().remove(materialDef);
            materialDef = null;
        }
        if (material != null) {
            obj.getLookupContents().remove(material);
            matToRemove = material;
            material = null;
        }
        FileLock lock = null;
        InputStream in = null;
        boolean matParseError = false;        
        try {
            lock = matDefFile.lock();
            in = obj.getPrimaryFile().getInputStream();
            List<Statement> sta = BlockLanguageParser.parse(in);
            matDefStructure = new MatDefBlock(sta.get(0));
            if (assetManager != null) {
                AssetKey<MaterialDef> matDefKey = new AssetKey<MaterialDef>(assetManager.getRelativeAssetPath(assetManager.getRelativeAssetPath(matDefFile.getPath())));
                assetManager.deleteFromCache(matDefKey);
                materialDef = (MaterialDef) assetManager.loadAsset(assetManager.getRelativeAssetPath(matDefFile.getPath()));
            }
        } catch (Exception ex) {
            Throwable t = ex.getCause();

            while (t != null) {
                if (t instanceof MatParseException) {
                    Logger.getLogger(EditableMatDefFile.class.getName()).log(Level.SEVERE, t.getMessage());
                    matParseError = true;
                }
                t = t.getCause();
            }
            if (matParseError) {
                // Show an Exception if it's already in the console?
                ExceptionUtils.caughtException(ex, "This means the related "
                        + "j3md file contained a syntax error of some sort. "
                        + "It has also been logged into the Console.", false);
            } else {
                ExceptionUtils.caughtException(ex, "This means that there was "
                        + "an unexpected exception when parsing the j3md. "
                        + "If this was due to you opening a j3md from a jar "
                        + "(org.openide.filesystems.FSException) then don't "
                        + "report it to us, if it differs, do so!", true);
            }
        } finally {
            if (lock != null) {
                lock.releaseLock();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        if (materialDef != null && !matParseError) {
            if(currentTechnique == null){
                currentTechnique = matDefStructure.getTechniques().get(0);
            }
            registerListener(matDefStructure);

            obj.getLookupContents().add(matDefStructure);
            updateLookupWithMaterialData(obj);
            loaded = true;
        }
    }

    void registerListener(Statement sta) {
        if (sta instanceof UberStatement) {
            ((UberStatement) sta).addPropertyChangeListener(WeakListeners.propertyChange(changeListener, ((UberStatement) sta)));
        } else if (sta instanceof LeafStatement) {
            ((LeafStatement) sta).addPropertyChangeListener(WeakListeners.propertyChange(changeListener, ((LeafStatement) sta)));
        }
        if (sta.getContents() != null) {
            for (Statement statement : sta.getContents()) {
                registerListener(statement);
            }
        }
    }

    public void buildOverview(ExplorerManager mgr) {
        if (materialDef != null) {
            mgr.setRootContext(new MatDefNode(lookup));

        } else {
            mgr.setRootContext(Node.EMPTY);
        }
    }

    public String getShaderCode(String version, Shader.ShaderType type) {
        try {
            material.selectTechnique(currentTechnique.getName(), SceneApplication.getApplication().getRenderManager());
            Shader s;
            StringBuilder sb = new StringBuilder();
            TechniqueDef def = material.getActiveTechnique().getDef();
            sb.append(def.getShaderPrologue());
            material.getActiveTechnique().getDynamicDefines().generateSource(sb, Arrays.asList(def.getDefineNames()), Arrays.asList(def.getDefineTypes()));
            
            if (version.equals(GLSL100)) {
                glsl100.initialize(material.getActiveTechnique().getDef());
                s = glsl100.generateShader(sb.toString());
            } else {
                glsl150.initialize(material.getActiveTechnique().getDef());
                s = glsl150.generateShader(sb.toString());
            }
            for (Shader.ShaderSource source : s.getSources()) {
                if (source.getType() == type) {
                    return source.getSource();
                }
            }
            return "";
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
            return "Error generating shader: " + e.getMessage();
        }
    }

    public TechniqueBlock getCurrentTechnique() {
        return currentTechnique;
    }
    
    public void setCurrentTechnique(TechniqueBlock tech){
        this.currentTechnique = tech;
    }

    public MatDefBlock getMatDefStructure() {
        return matDefStructure;
    }
    
    J3MLoader loader = new J3MLoader();

    private void updateLookupWithMaterialData(MatDefDataObject obj) {
        obj.getLookupContents().add(materialDef);
        material = new Material(materialDef);

        try {
            //material.selectTechnique("Default", SceneApplication.getApplication().getRenderManager());
            if (matToRemove != null) {
                obj.getLookupContents().remove(matToRemove);
                matToRemove = null;
            }
            obj.getLookupContents().add(material);
        } catch (Exception e) {
            Logger.getLogger(EditableMatDefFile.class.getName()).log(Level.WARNING, "Error making material {0}", e.getMessage());
            material = matToRemove;
        }
    }

    public boolean isLoaded() {
        return loaded;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    Material matToRemove;

    protected void applyChange() {

        try {
            EditorCookie ec = lookup.lookup(EditorCookie.class);
            final StyledDocument doc = ec.getDocument();
            final BadLocationException[] exc = new BadLocationException[]{null};
            NbDocument.runAtomicAsUser(ec.getDocument(), new Runnable() {
                public void run() {
                    try {
                    doc.remove(0, doc.getLength());
                    doc.insertString(doc.getLength(),
                    matDefStructure.toString(),
                    SimpleAttributeSet.EMPTY);
                    } catch (BadLocationException e) {
                        exc[0] = e;
                    }
                }
            });
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
        AssetKey<MaterialDef> key = new AssetKey<MaterialDef>(assetManager.getRelativeAssetPath(matDefFile.getPath()));
        obj.getLookupContents().remove(materialDef);
        matToRemove = material;

        List<Statement> l = new ArrayList<Statement>();
        l.add(matDefStructure);
        try {
            materialDef = loader.loadMaterialDef(l, assetManager, key);
        } catch (IOException ex) {
            Logger.getLogger(EditableMatDefFile.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        updateLookupWithMaterialData(obj);
    }
    
    public void cleanup(){
        if (matDefStructure != null) {
            obj.getLookupContents().remove(matDefStructure);
            matDefStructure = null;
        }
        if (materialDef != null) {
            obj.getLookupContents().remove(materialDef);
            materialDef = null;
        }
        if (material != null) {
            obj.getLookupContents().remove(material);
            matToRemove = material;
            material = null;
        }
        
        setCurrentTechnique(null);
        setLoaded(false);
    }
    
    public static ProjectAssetManager getAssetManager(){
        return assetManager;
    }
}
