/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jme3.netbeans.plaf.darkmonkey;

import javax.swing.UIManager;
import org.netbeans.modules.editor.settings.storage.api.EditorSettings;
import org.openide.modules.ModuleInstall;
import org.openide.util.NbPreferences;

public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        UIManager.installLookAndFeel(new UIManager.LookAndFeelInfo(
                new DarkMonkeyLookAndFeel().getName(),
                DarkMonkeyLookAndFeel.class.getName()));
        // TODO

        String[] fontsToLoad = {
            "fonts/DejaVuSans.ttf",
            "fonts/DejaVuSans-Bold.ttf",
            "fonts/DejaVuSans-Oblique.ttf",
            "fonts/DejaVuSans-BoldOblique.ttf",
            "fonts/DejaVuSansCondensed.ttf",
            "fonts/DejaVuSansCondensed-Bold.ttf",
            "fonts/DejaVuSansCondensed-Oblique.ttf",
            "fonts/DejaVuSansCondensed-BoldOblique.ttf",
            "fonts/DejaVuSansMono.ttf",
            "fonts/DejaVuSansMono-Bold.ttf",
            "fonts/DejaVuSansMono-Oblique.ttf",
            "fonts/DejaVuSansMono-BoldOblique.ttf"
        };
        DMUtils.loadFontsFromJar(this, fontsToLoad);
    }

    static {
        // Set DarkMonkey as the default LaF (Note: This code could also be placed inside the DarkMonkey LaF, so it'll be executed )
        try {
            if (NbPreferences.root().nodeExists("laf")) {
                String LaF = NbPreferences.root().node("laf").get("laf", null);
                if (LaF == null) { /* Did the user already set a LaF? */
                   NbPreferences.root().node("laf").put("laf", "org.jme3.netbeans.plaf.darkmonkey.DarkMonkeyLookAndFeel"); // Set DarkMonkey as default LaF
                    // TODO: Make this more generic and code independant. Read some other properties file

                    /* The laf file is parsed before this code is executed so we set the LaF once programatically */
                   UIManager.setLookAndFeel("org.jme3.netbeans.plaf.darkmonkey.DarkMonkeyLookAndFeel");

                   /* Calling Netbeans Internal API, unfortunately there is no way around that, apart from manually fiddling with the config/.nbattrs file,
                   which would need some parsing and a restart to take effect: <fileobject name=""> <attr name="Editors\currentFontColorProfile" stringvalue="Dark Monkey"/></fileobject>
                   Also see http://www.netbeans.org/dtds/attributes-1_0.dtd
                   */
                    EditorSettings setting = org.netbeans.modules.editor.settings.storage.api.EditorSettings.getDefault();
                    setting.setCurrentFontColorProfile("Dark Monkey");
                }
            }

            UIManager.put("Nb.DarkMonkeyLFCustoms", new DarkMonkeyLFCustoms((DarkMonkeyLookAndFeel)UIManager.getLookAndFeel()));
        } catch (Exception e) {}

    }

}
