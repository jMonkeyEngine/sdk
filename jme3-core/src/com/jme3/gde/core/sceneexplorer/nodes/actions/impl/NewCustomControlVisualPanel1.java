/*
 *  Copyright (c) 2009-2010 jMonkeyEngine
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
package com.jme3.gde.core.sceneexplorer.nodes.actions.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.swing.JPanel;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.source.ClassIndex;
import org.netbeans.api.java.source.ClassIndex.NameKind;
import org.netbeans.api.java.source.ClassIndex.SearchScope;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

@SuppressWarnings({"unchecked", "rawtypes"})
public final class NewCustomControlVisualPanel1 extends JPanel {

    Project proj;

    /**
     * Creates new form NewCustomControlVisualPanel1
     */
    public NewCustomControlVisualPanel1() {
        initComponents();
    }

    @Override
    public String getName() {
        return "Select Control";
    }

    public String getClassName() {
        return jTextField1.getText();
    }

    private void scanControls() {
        List<String> sources = getSources();
        jList1.setListData(sources.toArray());
    }

    private List<String> getSources() {
        Project root = ProjectUtils.rootOf(proj);
        Set<Project> containedProjects = ProjectUtils.getContainedProjects(root, true);
        List<Project> projects = new ArrayList<>();
        projects.add(root);
        if (containedProjects != null) {
            projects.addAll(containedProjects);
        }
        if (projects.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> list = new HashSet<>();
        for (Project project : projects) {
            Sources sources = project.getLookup().lookup(Sources.class);
            if (sources == null) {
                continue;
            }

            SourceGroup[] groups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
            if (groups == null) {
                continue;
            }

            for (SourceGroup sourceGroup : groups) {
                FileObject rootFolder = sourceGroup.getRootFolder();
                final ClasspathInfo cpInfo = ClasspathInfo.create(ClassPath.getClassPath(rootFolder, ClassPath.BOOT),
                        ClassPath.getClassPath(rootFolder, ClassPath.COMPILE),
                        ClassPath.getClassPath(rootFolder, ClassPath.SOURCE));

                Set<SearchScope> set = EnumSet.of(ClassIndex.SearchScope.SOURCE);
                Set<ElementHandle<TypeElement>> types = cpInfo.getClassIndex().getDeclaredTypes("", NameKind.PREFIX, set);
                for (Iterator<ElementHandle<TypeElement>> it = types.iterator(); it.hasNext();) {
                    final ElementHandle<TypeElement> elementHandle = it.next();
                    JavaSource js = JavaSource.create(cpInfo);
                    try {
                        js.runUserActionTask(new Task<CompilationController>() {
                            @Override
                            public void run(CompilationController control)
                                    throws Exception {
                                control.toPhase(Phase.RESOLVED);
                                //TODO: check with proper casting check.. gotta get TypeMirror of Control interface..
                                //                                    TypeUtilities util = control.getTypeUtilities();//.isCastable(Types., null)
                                //                                    util.isCastable(null, null);
                                TypeElement elem = elementHandle.resolve(control);
                                if (elem == null) {
                                    return;
                                }

                                String elementName = elem.getQualifiedName().toString();

                                if (list.contains(elementName)) /* No duplicates */ {
                                    return;
                                }

                                do {
                                    //Check if it implements control interface
                                    for (TypeMirror typeMirror : elem.getInterfaces()) {
                                        String interfaceName = typeMirror.toString();
                                        if ("com.jme3.scene.control.Control".equals(interfaceName) && !list.contains(elementName)) {
                                            list.add(elementName);
                                            break;
                                        }
                                    }
                                    //Check if it is an AbstractControl
                                    String className = elem.toString();
                                    if ("com.jme3.scene.control.AbstractControl".equals(className) && !list.contains(elementName)) {
                                        list.add(elementName);
                                    }

                                    TypeMirror superClass = elem.getSuperclass();
                                    if (superClass == null || superClass.getKind() == TypeKind.NONE) {
                                        break;
                                    }

                                    elem = (TypeElement) ((DeclaredType) superClass).asElement(); // Iterate deeper
                                } while (elem != null);
                            }
                        }, false);
                    } catch (Exception ioe) {
                        Exceptions.printStackTrace(ioe);
                    }
                }
            }
        }

        return new ArrayList<>(list);
    }

    public void load(Project proj) {
        this.proj = proj;
        scanControls();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();

        jTextField1.setText(org.openide.util.NbBundle.getMessage(NewCustomControlVisualPanel1.class, "NewCustomControlVisualPanel1.jTextField1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(NewCustomControlVisualPanel1.class, "NewCustomControlVisualPanel1.jLabel1.text")); // NOI18N

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                updateClassName(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void updateClassName(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_updateClassName
        Object obj = jList1.getSelectedValue();
        if (obj != null) {
            jTextField1.setText(obj + "");
        }
    }//GEN-LAST:event_updateClassName
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList jList1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
