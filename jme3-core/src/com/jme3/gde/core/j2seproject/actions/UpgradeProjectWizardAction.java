package com.jme3.gde.core.j2seproject.actions;

import com.jme3.gde.core.icons.IconList;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.java.j2seproject.J2SEProject;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

// An example action demonstrating how the wizard could be called from within
// your code. You can move the code below wherever you need, or register an action:
// @ActionID(category="...", id="com.jme3.gde.core.j2seproject.actions.UpgradeProjectWizardAction")
// @ActionRegistration(displayName="Open UpgradeProject Wizard")
// @ActionReference(path="Menu/Tools", position=...)
@ActionID(
        category = "Project",
        id = "com.jme3.gde.core.j2seproject.actions.UpgradeProjectWizardAction"
)
@ActionRegistration(
        iconBase = IconList.CONFIRMED,
        displayName = "#CTL_UpgradeProjectWizardAction"
)
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 1415),
    @ActionReference(path = "Projects/org-netbeans-modules-java-j2seproject/Actions", position = 200)
})
@NbBundle.Messages("CTL_UpgradeProjectWizardAction=Upgrade Project")

public final class UpgradeProjectWizardAction implements ActionListener {
    private final Project context;
    public final static Logger logger = Logger.getLogger(UpgradeProjectWizardAction.class.getName());
    private boolean isValidProject = false;
    
    /**
     * This is used for the actual conversion jme30<->jme31.
     * It is also used to see whether a project is 3.0 or 3.1
     */
    public final static HashMap<String, String[]> libraries = new HashMap<String, String[]>() {{
        put("${libs.jme3.classpath}",           new String[] {"${libs.jme3-core.classpath}"});
        put("${libs.jme3-libraries.classpath}", new String[] {"${libs.jme3-desktop.classpath}",
                                                "${libs.jme3-plugins.classpath}",
                                                "${libs.jme3-effects.classpath}",
                                                "${libs.jme3-networking.classpath}",
                                                "${libs.jme3-jogg.classpath}",
                                                "${libs.jme3-terrain.classpath}",
                                                "${libs.jme3-lwjgl.classpath}", 
                                                "${libs.jme3-bullet.classpath}",
                                                "${libs.jme3-bullet-native.classpath}",
                                                "${libs.jme3-niftygui.classpath}"});
    }};
    
    public UpgradeProjectWizardAction(Project context) {
        this.context = context;
        
        if (context instanceof J2SEProject) {
             String assetsFolderName = getProperties(context).getProperty("assets.folder.name");
             if (assetsFolderName == null)
                 assetsFolderName = "assets";
             
                if (context.getProjectDirectory().getFileObject(assetsFolderName) != null) {
                    isValidProject = true;// Valid JMP Project
                }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (context == null) {
            DialogDisplayer.getDefault().notifyLater(new NotifyDescriptor.Message("There is no project selected. Can't upgrade", NotifyDescriptor.Message.ERROR_MESSAGE));
            return;
        }
        
        if (!isValidProject) {
            DialogDisplayer.getDefault().notifyLater(new NotifyDescriptor.Message("The Project you selected isn't valid.\nMost likely it's no real JME Project but rather a usual Java Project.", NotifyDescriptor.Message.ERROR_MESSAGE));
            return;
        }
        
        if (isJME31(context)) {
            DialogDisplayer.getDefault().notifyLater(new NotifyDescriptor.Message("Your Project is already 3.1 compliant. There is no need to upgrade it :)", NotifyDescriptor.Message.INFORMATION_MESSAGE));
            return;
        }
        
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        panels.add(new UpgradeProjectWizardPanel1(context));
        panels.add(new UpgradeProjectWizardPanel2(context));
        String[] steps = new String[panels.size()];
        for (int i = 0; i < panels.size(); i++) {
            Component c = panels.get(i).getComponent();
            // Default step name to component name of panel.
            steps[i] = c.getName();
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
            }
        }
        WizardDescriptor wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator<WizardDescriptor>(panels));
        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle("Upgrade jMonkeyPlatform Project");
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
            // do something
        }
    }
    
    /**
     * Return the EditableProperties-Object of the <code>project.properties</code> file, but don't expand variables.
     * @param project The Project who's Properties you want to change
     * @return The Properties Object
     */
    public static EditableProperties getProperties(Project project) {
        FileObject propFO = project.getProjectDirectory().getFileObject("nbproject/project.properties");
        if (propFO != null && propFO.isValid()) {
            FileLock lock = null;
            try {
                lock = propFO.lock();
                InputStream in = propFO.getInputStream();
                EditableProperties properties = new EditableProperties(true);
                properties.load(in);
                in.close();
                return properties;
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error when trying to open project.properties",e);
            } finally {
                if (lock != null) {
                    lock.releaseLock();
                }
            }
        }
        return null;
    }
    
    /**
     * Determine if this Project is a JME 3.1 project or rather not.
     * This is done by first looking for the <code>jme.project.version</code>, introduced in 3.1
     * If the project doesn't contain that information, we compare the libraries the project depends on.
     * @param project The Project to Test
     * @param editableProperties The Properties which contain
     * @return 
     */
    public static boolean isJME31(Project project)
    {
        EditableProperties properties = getProperties(project);
        
        String jmeVersion = properties.getProperty("jme.project.version");
        if ("3.1".equals(jmeVersion))
            return true;
        
        String javacClasspath = properties.getProperty("javac.classpath");
        
        if (javacClasspath != null) {
            for (String[] s: libraries.values()) { /* 3.1 library names */
                for (String t: s) { /* For each name */
                    if (javacClasspath.contains(t))
                            return true;
                }
            }
            for (String s: libraries.keySet()) {
                if (javacClasspath.contains(s)) /* 3.0 library name */
                    return false;
            }
        }
        
        logger.log(Level.WARNING, "Could not determine whether the Project {0} is a JME 3.1 project or not. Assuming: No.", ProjectUtils.getInformation(project).getDisplayName());
        return false;
    }
    
    public static boolean isJME30(Project project)
    {
        EditableProperties properties = getProperties(project);
        String jmeVersion = properties.getProperty("jme.project.version");
        String javacClasspath = properties.getProperty("javac.classpath");
        
        if ("3.0".equals(jmeVersion))
            return true;
        
        if (javacClasspath != null) {
            for (String s: libraries.keySet()) {
                if (javacClasspath.contains(s)) /* 3.0 library name */
                    return true;
            }
            for (String[] s: libraries.values()) { /* 3.1 library names */
                for (String t: s) { /* For each name */
                    if (javacClasspath.contains(t))
                            return false;
                }
            }
        }
        
        logger.log(Level.WARNING, "Could not determine whether the Project {0} is a JME 3.0 project or not. Assuming: No.", ProjectUtils.getInformation(project).getDisplayName());
        return false;
    }
}
