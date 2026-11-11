/*
 * Copyright (c) 2024-2026 jMonkeyEngine
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
package com.jme3.gde.templates.jaimesascent;

import com.jme3.gde.templates.gradledesktop.options.CachedOptionsContainer;
import com.jme3.gde.templates.shared.sample.SampleProjectConfig;
import com.jme3.gde.templates.shared.sample.SampleProjectWizardIterator;

/**
 * Sample project template for Jaime's Ascent. The actual wizard logic lives in
 * {@link SampleProjectWizardIterator}; this class only supplies the template
 * specific configuration.
 */
public class JaimesAscentWizardIterator extends SampleProjectWizardIterator {

    private static final SampleProjectConfig CONFIG = new SampleProjectConfig(
            "https://github.com/neph1/JaimesAscent/archive/refs/tags/v1.1.3.zip",
            "JaimesAscent.zip",
            "JaimesAscent",
            "/com/jme3/gde/templates/jaimesascent/jaimesascent.png");

    public JaimesAscentWizardIterator() {
        super(CONFIG);

        // Initiate the options getting...
        CachedOptionsContainer.getInstance();
    }

    public static JaimesAscentWizardIterator createIterator() {
        return new JaimesAscentWizardIterator();
    }
}
