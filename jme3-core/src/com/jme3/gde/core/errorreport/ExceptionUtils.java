/*
 * Copyright (c) 2003-2018 jMonkeyEngine
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
package com.jme3.gde.core.errorreport;

import com.google.common.base.Throwables;
import com.jme3.system.JmeVersion;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;

/**
 * Provides a standard interface to throw Exceptions. This makes them
 * more accessible to the user than when they are just logged into the console.<br>
 * This could also be a starting point for crash statistics etc
 * 
 * @author MeFisto94
 */

public class ExceptionUtils {
    protected static String newLine = System.lineSeparator();//getProperty("line.separator");
    public static final String ISSUE_TRACKER_URL = "https://github.com/jMonkeyEngine/sdk/issues";
    public static final String FORUMS_URL = "https://hub.jmonkeyengine.org";
    
    /**
     * Notify the user about an Exception and tell him to visit either the SDK
     * Issue Github Page or the Forums.
     * 
     * @param t The Exception that happened
     * @param comment A Comment explaining the Situation
     * @param wantIssue Whether we want to be informed using an issue or rather 
     * only via a forum post.
     */
    public static void caughtException(Throwable t, String comment, boolean wantIssue) {
        StringBuilder sB = new StringBuilder();
        sB.append("jMonkeyEngine SDK Exception Report");
        sB.append(newLine);
        if (wantIssue) {
            sB.append("Please submit me to the Issue Tracker");
        } else {
            sB.append("If you need troubleshooting help, visit our forums, but "
            + "don't report this as a github issue");
        }
        sB.append(newLine);
        sB.append("Comment: ");
        sB.append(comment);
        sB.append(newLine);
        sB.append(t != null ? Throwables.getStackTraceAsString(t) : fakeCallstack());
        sB.append(newLine);
        //sB.append("Versions: ");
        //sB.append(newLine);
        sB.append("Operating System: ");
        sB.append(System.getProperty("os.name"));
        sB.append(newLine);
        // @TODO: If Linux, try uname -a and lsb_release -a
        // @TODO: Maybe generally try os.version (or how it's called)
        sB.append("Engine Version as used by the SDK: ");
        //sB.append(newLine);
        sB.append(JmeVersion.VERSION_FULL);
        //sB.append(newLine);
        
        ExceptionPanel ep = new ExceptionPanel(sB.toString(), wantIssue);
        DialogDescriptor d = new DialogDescriptor(ep, "Oops! An Exception has occured.", true, new Object[] { DialogDescriptor.OK_OPTION }, DialogDescriptor.DEFAULT_OPTION, DialogDescriptor.DEFAULT_ALIGN, null, null);
        DialogDisplayer.getDefault().notifyLater(d);
   }
    
    public static void caughtException(Throwable t, String comment) {
        caughtException(t, comment, true);
    }
    
    public static void caughtException(Throwable t) {
        caughtException(t, "", true);
    }
    
    private static String fakeCallstack() {
        return Throwables.getStackTraceAsString(new IllegalStateException("Fake-Callstack!"));
    }
}

