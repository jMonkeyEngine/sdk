
package com.jme3.gde.core.codeless;

import com.jme3.gde.core.icons.IconList;
import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectFactory2;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = ProjectFactory.class)
public class CodelessProjectFactory implements ProjectFactory2 {

    public static final String CONFIG_NAME = "assets.jmp";

    //Specifies when a project is a project, i.e. properties file exists
    @Override
    public boolean isProject(FileObject projectDirectory) {
        return projectDirectory.getFileObject(CONFIG_NAME) != null;
    }

    //Specifies when the project will be opened, i.e.,
    //if the project exists:
    @Override
    public Project loadProject(FileObject dir, ProjectState state) throws IOException {
        return isProject(dir) ? new CodelessProject(dir, state) : null;
    }

    @Override
    public void saveProject(final Project project) throws IOException, ClassCastException {
        FileObject projectRoot = project.getProjectDirectory();
        if (projectRoot.getFileObject(CONFIG_NAME) == null) {
            throw new IOException("Project Settings " + projectRoot.getPath() +
                    " deleted," +
                    " cannot save project");
        }
    }

    @Override
    public ProjectManager.Result isProject2(FileObject fo) {
        if (!isProject(fo)) {
            return null;
        }

        return new ProjectManager.Result(IconList.jmeLogo);
    }

}
