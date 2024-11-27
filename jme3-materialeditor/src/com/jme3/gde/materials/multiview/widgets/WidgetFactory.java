/*
 *  Copyright (c) 2009-2024 jMonkeyEngine
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

package com.jme3.gde.materials.multiview.widgets;

import com.jme3.gde.core.assets.ProjectAssetManager;
import com.jme3.gde.materials.MaterialProperty;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;

/**
 * Creates editor widgets for values in the Material Editor
 * 
 * @author normenhansen
 */
public class WidgetFactory {

    public static MaterialPropertyWidget getWidget(MaterialProperty prop, ProjectAssetManager manager){      
        MaterialPropertyWidget widget;
        if(prop.getType().contains("Texture")){
            widget=new TexturePanel(manager);
            widget.setProperty(prop);
            return widget;
        }
        else if("Boolean".equals(prop.getType())){
            widget=new BooleanPanel();
            widget.setProperty(prop);
            return widget;
        }
        else if("OnOff".equals(prop.getType())){
            widget=new OnOffPanel();
            widget.setProperty(prop);
            return widget;
        }
        else if("Float".equals(prop.getType())){
            widget=new FloatPanel();
            widget.setProperty(prop);
            return widget;
        }
        else if("Int".equals(prop.getType())){
            widget=new IntPanel();
            widget.setProperty(prop);
            return widget;
        }
        else if("Color".equals(prop.getType())){
            widget=new ColorPanel();
            widget.setProperty(prop);
            return widget;
        }
        else if("FaceCullMode".equals(prop.getType())){
            widget=new SelectionPanel();
            String[] strings=new String[FaceCullMode.values().length];
            for (int i = 0; i < strings.length; i++) {
                strings[i]=FaceCullMode.values()[i].name();
            }
            ((SelectionPanel)widget).setSelectionList(strings);
            widget.setProperty(prop);
            return widget;
        }
        else if("BlendMode".equals(prop.getType())){
            widget=new SelectionPanel();
            String[] strings=new String[BlendMode.values().length];
            for (int i = 0; i < strings.length; i++) {
                strings[i]=BlendMode.values()[i].name();
            }
            ((SelectionPanel)widget).setSelectionList(strings);
            widget.setProperty(prop);
            return widget;
        }
        widget = new TextPanel();      
        widget.setProperty(prop);
        return widget;
    }

}
