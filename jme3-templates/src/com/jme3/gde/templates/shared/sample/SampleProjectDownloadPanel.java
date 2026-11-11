/*
 * Copyright (c) 2026 jMonkeyEngine
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
package com.jme3.gde.templates.shared.sample;

import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * Wizard panel that downloads a sample project zip from GitHub. The download
 * runs on a background thread with timeouts, so the wizard UI stays
 * responsive. The zip is cached in the temporary directory under a name that
 * includes the project name, so different sample projects never share a
 * cached file.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class SampleProjectDownloadPanel implements WizardDescriptor.Panel,
        WizardDescriptor.ValidatingPanel, WizardDescriptor.FinishablePanel {

    private static final Logger logger = Logger.getLogger(SampleProjectDownloadPanel.class.getName());
    private static final ExecutorService DOWNLOAD_EXECUTOR = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "sample-project-download");
        t.setDaemon(true);
        return t;
    });

    private final SampleProjectConfig config;
    private WizardDescriptor wizardDescriptor;
    private SampleProjectDownloadPanelVisual component;
    private final AtomicBoolean downloaded = new AtomicBoolean(false);
    private final AtomicBoolean downloadInProgress = new AtomicBoolean(false);

    public SampleProjectDownloadPanel(SampleProjectConfig config) {
        this.config = config;
        File cached = getCachedFile();
        downloaded.set(cached.isFile() && cached.length() > 0);
    }

    public SampleProjectConfig getConfig() {
        return config;
    }

    public File getCachedFile() {
        return new File(System.getProperty("java.io.tmpdir"), config.getCacheFileName());
    }

    public boolean isDownloaded() {
        return downloaded.get();
    }

    /**
     * Downloads the project zip on a background thread. The consumer is
     * called with the result once the download has finished or failed.
     */
    public void downloadAsync(Consumer<Boolean> resultConsumer) {
        if (downloadInProgress.get() || downloaded.get()) {
            resultConsumer.accept(downloaded.get());
            return;
        }
        downloadInProgress.set(true);
        DOWNLOAD_EXECUTOR.submit(() -> {
            boolean success = downloadFile();
            downloaded.set(success);
            downloadInProgress.set(false);
            resultConsumer.accept(success);
        });
    }

    private boolean downloadFile() {
        final File outputFile = getCachedFile();
        if (outputFile.isFile() && outputFile.length() > 0) {
            return true;
        }

        File tempFile = new File(outputFile.getAbsolutePath() + ".part");
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(30))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();
            HttpRequest request = HttpRequest.newBuilder(URI.create(config.getDownloadUrl()))
                    .timeout(Duration.ofMinutes(10))
                    .GET()
                    .build();
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            if (response.statusCode() != 200) {
                logger.log(Level.WARNING, "Download of {0} failed with HTTP status {1}",
                        new Object[]{config.getDownloadUrl(), response.statusCode()});
                return false;
            }
            try (InputStream in = new BufferedInputStream(response.body());
                 OutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            if (!tempFile.renameTo(outputFile)) {
                // rename can fail across file systems; fall back to delete+rename
                if (outputFile.exists() && !outputFile.delete()) {
                    return false;
                }
                if (!tempFile.renameTo(outputFile)) {
                    return false;
                }
            }
            return outputFile.isFile() && outputFile.length() > 0;
        } catch (IOException | InterruptedException ex) {
            logger.log(Level.WARNING, "Download of " + config.getDownloadUrl() + " failed", ex);
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            tempFile.delete();
            return false;
        }
    }

    @Override
    public Component getComponent() {
        if (component == null) {
            component = new SampleProjectDownloadPanelVisual(this);
            component.setName(NbBundle.getMessage(SampleProjectDownloadPanel.class, "LBL_DownloadProjectStep"));
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        return new HelpCtx("sdk.download_project");
    }

    @Override
    public boolean isValid() {
        getComponent();
        return component.valid(wizardDescriptor);
    }

    private final Set<ChangeListener> listeners = new HashSet<>(1); // or can use ChangeSupport in NB 6.0

    @Override
    public final void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }

    @Override
    public final void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    public final void fireChangeEvent() {
        Set<ChangeListener> ls;
        synchronized (listeners) {
            ls = new HashSet<>(listeners);
        }
        ChangeEvent ev = new ChangeEvent(this);
        for (ChangeListener l : ls) {
            l.stateChanged(ev);
        }
    }

    @Override
    public void readSettings(Object settings) {
        wizardDescriptor = (WizardDescriptor) settings;
        component.read(wizardDescriptor);
    }

    @Override
    public void storeSettings(Object settings) {
        WizardDescriptor d = (WizardDescriptor) settings;
        component.store(d);
    }

    @Override
    public boolean isFinishPanel() {
        return false;
    }

    @Override
    public void validate() throws WizardValidationException {
        getComponent();
        component.validate(wizardDescriptor);
    }
}
