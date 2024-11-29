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
package com.jme3.gde.core.assets.actions;

import com.jme3.anim.AnimClip;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.AnimTrack;
import com.jme3.anim.Armature;
import com.jme3.anim.Joint;
import com.jme3.anim.SkinningControl;
import com.jme3.anim.TransformTrack;
import com.jme3.anim.util.HasLocalTransform;
import com.jme3.gde.core.assets.SpatialAssetDataObject;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.util.SafeArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor.Confirmation;
import org.openide.util.Exceptions;

/**
 * Action for merging one or more spatials' animation to another. Same rig
 * required.
 *
 * @author rickard
 */
public class MergeAnimationsAction implements ActionListener {

    private static final String CANCEL = "Cancel";
    private final List<SpatialAssetDataObject> spatials;
    private final Logger logger;

    public MergeAnimationsAction(List<SpatialAssetDataObject> context) {
        this.spatials = context;
        logger = Logger.getLogger(MergeAnimationsAction.class.getName());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (spatials.size() == 1) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                    "Must select more than one spatial",
                    NotifyDescriptor.ERROR_MESSAGE));
            return;
        }

        final Object selectedSpatial = createSelector().getValue();

        if (selectedSpatial == null || selectedSpatial.equals(CANCEL)) {
            logger.log(Level.INFO, "Operation cancelled by user.");
            return;
        }

        final SpatialAssetDataObject targetAsset = findSpatial(selectedSpatial.toString());

        if (targetAsset == null) {
            logger.log(Level.INFO, "Operation failed. No spatial.");
            return;
        }

        final Spatial targetSpatial = targetAsset.loadAsset();
        final AnimComposer targetAnimComposer = findAnimComposer(targetSpatial, null);

        if (targetAnimComposer == null) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                    String.format("%s has no AnimComposer.", targetSpatial),
                    NotifyDescriptor.ERROR_MESSAGE));
            return;
        }
        final Spatial targetAnimComposerSpatial = targetAnimComposer.getSpatial();
        for (final Iterator<SpatialAssetDataObject> it = spatials.iterator(); it.hasNext();) {
            final SpatialAssetDataObject spatialAssetDataObject = it.next();
            if (spatialAssetDataObject.getName().equals(selectedSpatial)) {
                continue;
            }
            Spatial sourceSpatial = spatialAssetDataObject.loadAsset();
            final AnimComposer sourceAnimComposer = findAnimComposer(sourceSpatial, null);
            if (sourceAnimComposer == null) {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                        String.format("%s has no AnimComposer.", sourceSpatial),
                        NotifyDescriptor.ERROR_MESSAGE));
                return;
            }
            copyClips(sourceAnimComposer, targetAnimComposer, targetAnimComposerSpatial.getControl(SkinningControl.class).getArmature());
        }
        logger.log(Level.INFO, "Merging animations done. Saving.");
        try {
            targetAsset.saveAsset();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void copyClips(final AnimComposer from, final AnimComposer to, Armature toArmature) {
        final Collection<AnimClip> animClips = from.getAnimClips();
        for (AnimClip animClip : animClips) {
            to.addAnimClip(retargetClip(animClip, toArmature, animClip.getName()));
            logger.log(Level.FINE, String.format("Added anim clip %s", animClip.getName()));
        }
    }

    private Confirmation createSelector() {
        final Object[] spatials = new Object[this.spatials.size() + 1];
        int index = 0;
        for (Iterator<SpatialAssetDataObject> it = this.spatials.iterator(); it.hasNext();) {
            final SpatialAssetDataObject spatialAssetDataObject = it.next();
            spatials[index++] = spatialAssetDataObject.getName();
        }
        spatials[index++] = CANCEL;
        final NotifyDescriptor.Confirmation message
                = new NotifyDescriptor.Confirmation(
                        "Select spatial to copy animations to.");
        message.setOptions(spatials);

        DialogDisplayer.getDefault().notify(message);

        return message;
    }

    private AnimComposer findAnimComposer(Spatial spatial, AnimComposer animComposer) {
        if (spatial.getControl(AnimComposer.class) != null) {
            return spatial.getControl(AnimComposer.class);
        }
        if (animComposer == null && spatial instanceof Node node) {
            for (Spatial child : node.getChildren()) {
                animComposer = findAnimComposer(child, animComposer);
                if (animComposer != null) {
                    return animComposer;
                }
            }
        }
        return animComposer;
    }

    private SpatialAssetDataObject findSpatial(String selected) {
        for (Iterator<SpatialAssetDataObject> it = spatials.iterator(); it.hasNext();) {
            final SpatialAssetDataObject spatialAssetDataObject = it.next();
            if (spatialAssetDataObject.getName().equals(selected)) {
                return spatialAssetDataObject;
            }
        }
        NotifyDescriptor.Message msg = new NotifyDescriptor.Message(
                "Main asset to copy to not found. This is likely an issue with the tool itself.",
                NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(msg);
        return null;
    }

    private AnimClip retargetClip(AnimClip sourceClip, Armature targetArmature, String clipName) {

        // Create a list to hold the new tracks
        SafeArrayList<AnimTrack> tracks = new SafeArrayList<>(AnimTrack.class);

        // Iterate through each track in the source clip
        for (AnimTrack animTrack : sourceClip.getTracks()) {

            TransformTrack sourceTrack = (TransformTrack) animTrack;
            String targetName = getTargetName(sourceTrack.getTarget());
            if (targetName == null) {
                logger.log(Level.SEVERE, String.format("Unsupported target for: %s. Skipping.", animTrack));
                continue;
            }
            Joint target = targetArmature.getJoint(targetName);

            if (target != null) {
                // Clone the source track and set the new target joint
                TransformTrack newTrack = sourceTrack.jmeClone();
                newTrack.setTarget(target);
                tracks.add(newTrack);

            } else {
                logger.log(Level.WARNING, "Joint not found in the target Armature: {0}", targetName);
            }
        }

        // Create a new animation clip with the specified name and set its tracks
        AnimClip newClip = new AnimClip(clipName);
        newClip.setTracks(tracks.getArray());

        logger.log(Level.INFO, "Created new AnimClip {0} with {1} tracks out of {2} from the source clip",
                new Object[]{clipName, tracks.size(), sourceClip.getTracks().length});
        return newClip;
    }

    private String getTargetName(final HasLocalTransform target) {
        if (target instanceof Node node) {
            return node.getName();
        }
        if (target instanceof Joint joint) {
            return joint.getName();
        }
        return null;
    }

}
