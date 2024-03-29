
package com.jme3.gde.core.codeless;

import com.jme3.gde.core.assets.ProjectAssetManager;
import com.jme3.gde.core.icons.IconList;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import javax.swing.Icon;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.CopyOperationImplementation;
import org.netbeans.spi.project.DeleteOperationImplementation;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.DefaultProjectOperations;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

@SuppressWarnings("unchecked")
class CodelessProject implements Project {

    private final FileObject projectDir;
    LogicalViewProvider logicalView = new CodelessProjectLogicalView(this);
    private final ProjectState state;
    private Lookup lkp;
    private final ProjectAssetManager projectAssetManager;

    public CodelessProject(FileObject projectDir, ProjectState state) {
        this.projectDir = projectDir;
        this.state = state;
        Properties properties=getProperties();
        String assetsFolder=properties.getProperty("assets.folder.name","assets");
        projectAssetManager = new ProjectAssetManager(this, assetsFolder);
    }

    @Override
    public FileObject getProjectDirectory() {
        return projectDir;
    }

//    public FileObject getConfigFolder(boolean create) {
//        FileObject result =
//                projectDir.getFileObject(CodelessProjectFactory.PROJECT_DIR);
//        if (result == null && create) {
//            try {
//                result = projectDir.createFolder(CodelessProjectFactory.PROJECT_DIR);
//            } catch (IOException ioe) {
//                Exceptions.printStackTrace(ioe);
//            }
//        }
//        return result;
//    }

    public FileObject getConfigFile(boolean create){
        FileObject folder=projectDir;//getConfigFolder(create);
        FileObject file=folder.getFileObject(CodelessProjectFactory.CONFIG_NAME);
        if(file==null){
            try {
                return folder.createData(CodelessProjectFactory.CONFIG_NAME);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return file;
    }

    public FileObject getAssetsFolder() {
        Properties properties=getProperties();
        String assetsFolder=properties.getProperty("assets.folder.name","assets");
        FileObject result =
                projectDir.getFileObject(assetsFolder);
        return result;
    }

    private Properties getProperties(){
        Properties properties=new Properties();
        try {
            properties.load(getConfigFile(true).getInputStream());
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        return properties;
    }

    private void saveProperties(Properties prop){
        try {
            prop.store(getConfigFile(true).getOutputStream(), "jMonkeyPlatform Properties");
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public Lookup getLookup() {
        if (lkp == null) {
            lkp = Lookups.fixed(new Object[]{
                        this, //project spec requires a project be in its own lookup
                        state, //allow outside code to mark the project as needing saving
                        projectAssetManager,
                        new ActionProviderImpl(), //Provides standard actions like Build and Clean
                        new DemoDeleteOperation(),
                        new DemoCopyOperation(this),
                        new Info(), //Project information implementation
                        logicalView, //Logical view of project implementation
                    });
        }
        return lkp;
    }

    public ProjectAssetManager getProjectAssetManager() {
        return projectAssetManager;
    }

    private final class ActionProviderImpl implements ActionProvider {

        private final String[] supported = new String[]{
            ActionProvider.COMMAND_DELETE,
            ActionProvider.COMMAND_COPY,
        };

        @Override
        public String[] getSupportedActions() {
            return supported;
        }

        @Override
        public void invokeAction(String string, Lookup lookup) throws IllegalArgumentException {
            if (string.equalsIgnoreCase(ActionProvider.COMMAND_DELETE)) {
                DefaultProjectOperations.performDefaultDeleteOperation(CodelessProject.this);
            }
            if (string.equalsIgnoreCase(ActionProvider.COMMAND_COPY)) {
                DefaultProjectOperations.performDefaultCopyOperation(CodelessProject.this);
            }
        }

        @Override
        public boolean isActionEnabled(String command, Lookup lookup) throws IllegalArgumentException {
            switch (command) {
                case ActionProvider.COMMAND_DELETE -> {
                    return true;
                }
                case ActionProvider.COMMAND_COPY -> {
                    return true;
                }
                default -> throw new IllegalArgumentException(command);
            }
        }
    }

    private final class DemoDeleteOperation implements DeleteOperationImplementation {

        @Override
        public void notifyDeleting() throws IOException {
        }

        @Override
        public void notifyDeleted() throws IOException {
        }

        @Override
        public List<FileObject> getMetadataFiles() {
            List<FileObject> dataFiles = new ArrayList<>();
            return dataFiles;
        }

        @Override
        public List<FileObject> getDataFiles() {
            List<FileObject> dataFiles = new ArrayList<>();
            return dataFiles;
        }
    }

    private final class DemoCopyOperation implements CopyOperationImplementation {

        private final CodelessProject project;
        private final FileObject projectDir;

        public DemoCopyOperation(CodelessProject project) {
            this.project = project;
            this.projectDir = project.getProjectDirectory();
        }

        @Override
        public List<FileObject> getMetadataFiles() {
            return Collections.emptyList();
        }

        @Override
        public List<FileObject> getDataFiles() {
            return Collections.emptyList();
        }

        @Override
        public void notifyCopying() throws IOException {
        }

        @Override
        public void notifyCopied(Project arg0, File arg1, String arg2) throws IOException {
        }
    }

    /** Implementation of project system's ProjectInformation class */
    private final class Info implements ProjectInformation {

        @Override
        public Icon getIcon() {
            return IconList.jmeLogo;
        }

        @Override
        public String getName() {
            return getProjectDirectory().getName();
        }

        @Override
        public String getDisplayName() {
            return getName();
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener pcl) {
            //do nothing, won't change
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener pcl) {
            //do nothing, won't change
        }

        @Override
        public Project getProject() {
            return CodelessProject.this;
        }
    }
}
