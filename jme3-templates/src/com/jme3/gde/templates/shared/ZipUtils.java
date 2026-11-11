/*
 * Copyright (c) 2009-2026 jMonkeyEngine
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
package com.jme3.gde.templates.shared;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public final class ZipUtils {

    private ZipUtils() {
    }

    /**
     * Extracts a zip archive into the given project root.
     *
     * If the archive contains a single top-level directory (as GitHub source
     * archives do), its contents are stripped so the project lands directly in
     * the project root regardless of the folder name inside the zip.
     *
     * If the archive contains {@code nbproject/project.xml} with an Ant-based
     * project, the {@code <name>} element is replaced with the project root's
     * folder name so NetBeans recognises the project.
     */
    public static void unZipFile(InputStream source, FileObject projectRoot) throws IOException {
        byte[] content = source.readAllBytes();
        List<ZipEntry> entries = new ArrayList<>();
        try (ZipInputStream str = new ZipInputStream(new ByteArrayInputStream(content))) {
            ZipEntry entry;
            while ((entry = str.getNextEntry()) != null) {
                entries.add(entry);
            }
        }

        String rootPrefix = detectSingleRootPrefix(entries);
        try (ZipInputStream str = new ZipInputStream(new ByteArrayInputStream(content))) {
            ZipEntry entry;
            while ((entry = str.getNextEntry()) != null) {
                String entryName = entry.getName();
                if (rootPrefix != null) {
                    if (entryName.equals(rootPrefix)) {
                        continue;
                    }
                    if (!entryName.startsWith(rootPrefix)) {
                        // Archive does not have a single root after all; extract as-is
                        rootPrefix = null;
                    } else {
                        entryName = entryName.substring(rootPrefix.length());
                        if (entryName.isEmpty()) {
                            continue;
                        }
                    }
                }
                if (entry.isDirectory()) {
                    FileUtil.createFolder(projectRoot, entryName);
                } else {
                    FileObject fo = FileUtil.createData(projectRoot, entryName);
                    if ("nbproject/project.xml".equals(entryName)) {
                        filterProjectXML(str, fo, projectRoot.getName());
                    } else {
                        try (OutputStream out = fo.getOutputStream()) {
                            str.transferTo(out);
                        }
                    }
                }
            }
        }
    }

    /**
     * Replaces the {@code <name>} element inside an Ant-based
     * {@code nbproject/project.xml} with the actual project folder name, so
     * NetBeans recognises the extracted project.
     */
    private static void filterProjectXML(ZipInputStream str, FileObject fo, String name) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        str.transferTo(baos);
        byte[] content = baos.toByteArray();
        try {
            Document doc = XMLUtil.parse(new InputSource(new ByteArrayInputStream(content)), false, false, null, null);
            NodeList nl = doc.getDocumentElement().getElementsByTagName("name");
            if (nl != null) {
                for (int i = 0; i < nl.getLength(); i++) {
                    Element el = (Element) nl.item(i);
                    if (el.getParentNode() != null && "data".equals(el.getParentNode().getNodeName())) {
                        NodeList nl2 = el.getChildNodes();
                        if (nl2.getLength() > 0) {
                            nl2.item(0).setNodeValue(name);
                        }
                        break;
                    }
                }
            }
            try (OutputStream out = fo.getOutputStream()) {
                XMLUtil.write(doc, out, "UTF-8");
            }
        } catch (Exception ex) {
            try (OutputStream out = fo.getOutputStream()) {
                out.write(content);
            }
        }
    }

    /**
     * Returns the top-level directory shared by all entries of the archive
     * (e.g. "repo-master/" for a GitHub source archive), or null if the
     * entries do not share a single root directory.
     */
    private static String detectSingleRootPrefix(List<ZipEntry> entries) {
        if (entries.isEmpty()) {
            return null;
        }
        String root = null;
        for (ZipEntry entry : entries) {
            String name = entry.getName();
            int slash = name.indexOf('/');
            if (slash <= 0) {
                return null; // file at archive root -> no single root dir
            }
            String top = name.substring(0, slash + 1);
            if (root == null) {
                root = top;
            } else if (!root.equals(top)) {
                return null; // multiple top-level entries -> no single root dir
            }
        }
        return root;
    }
}
