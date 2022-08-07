/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.gde.materialdefinition.fileStructure;

import com.jme3.util.blockparser.Statement;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.openide.util.WeakListeners;

/**
 * A ShaderNodesBlock is a set of shader nodes in the MatDef file, for example all frag shader nodes.
 * 
 * @author Nehon
 */
public class ShaderNodesBlock extends UberStatement implements PropertyChangeListener {
        
    protected ShaderNodesBlock(int lineNumber, String line) {
        super(lineNumber, line);
        
    }

    public ShaderNodesBlock(Statement sta) {
        this(sta.getLineNumber(), sta.getLine());
        for (Statement statement : sta.getContents()) {
            ShaderNodeBlock b = new ShaderNodeBlock(statement);
            b.addPropertyChangeListener(WeakListeners.propertyChange(this, b));
            addStatement(b);
        }
    }

    public List<ShaderNodeBlock> getShaderNodes() {
        return getBlocks(ShaderNodeBlock.class);
    }

    public void addShaderNode(ShaderNodeBlock shaderNodeBlock) {
        addStatement(shaderNodeBlock);
        shaderNodeBlock.addPropertyChangeListener(WeakListeners.propertyChange(this, shaderNodeBlock));
    }

    public boolean removeShaderNode(ShaderNodeBlock shaderNodeBlock) {
        return contents.remove(shaderNodeBlock);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ShaderNodeBlock.ADD_MAPPING) || evt.getPropertyName().equals("order")) {
            sort();
        }
    }

    public void sort() {
        List<ShaderNodeBlock> list = getShaderNodes();
        int passes = 0;
        NodeComparator nodeComparator = new NodeComparator(list);
        while (nodeComparator.changes != 0 && passes < 10) {
            nodeComparator.changes = 0;
            Collections.sort(list, nodeComparator);
            passes++;
        }
        contents.clear();
        contents.addAll(list);
        fire("reorder", null, null);
    }
    
    /**
     * Sorts nodes so that they are initialized after their input dependencies.
     * Will look higher up in hierarchy and move node incrementally. This may require several passes
     */
    private final class NodeComparator implements Comparator<ShaderNodeBlock> {

        private final List<ShaderNodeBlock> list;
        private int changes = -1;

        public NodeComparator(List<ShaderNodeBlock> list) {
            this.list = list;
        }

        @Override
        public int compare(ShaderNodeBlock o1, ShaderNodeBlock o2) {
            if (o1.name.equals(o2.name)) {
                return 0;
            }
            if (hasInputConnection(o2.name, o1)) {
                changes++;
                return 1;
            }
            if (hasInputConnection(o1.name, o2)) {
                changes++;
                return -1;
            }

            if ((o1.globalInput && o2.globalOutput) || (o2.globalInput && o1.globalOutput)) {
                changes++;
                return (int) Math.signum(o1.spatialOrder - o2.spatialOrder);
            }
            return 0;
        }

        private ShaderNodeBlock findAncestorNode(String name) {
            for (ShaderNodeBlock node : list) {
                if (node.name.equals(name)) {
                    return node;
                }
            }
            return null;
        }

        private boolean hasInputConnection(String name, ShaderNodeBlock node) {
            for (String s : node.inputNodes) {
                if (s.equals(name)) {
                    return true;
                }
                ShaderNodeBlock ancestor = findAncestorNode(s);
                if (ancestor != null && hasInputConnection(name, ancestor)) {
                    return true;
                }
            }
            return false;
        }
    }
}
