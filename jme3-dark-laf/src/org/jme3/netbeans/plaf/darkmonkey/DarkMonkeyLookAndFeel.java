/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jme3.netbeans.plaf.darkmonkey;

import com.nilo.plaf.nimrod.NimRODTheme;
import org.openide.util.NbPreferences;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.util.HashMap;
import java.util.Properties;
import java.util.prefs.Preferences;

/**
 * The DarkMonkey look and feel class Extends the Nimrod LAF, which in turn,
 * extends Metal.  The version of Nimrod used is 1.2b obtained from: <br/>
 * <a src="http://nilogonzalez.es/nimrodlf/download-en.html">
 * http://nilogonzalez.es/nimrodlf/download-en.html</a>
 * <p> A copy of the jar and source used for this project is in the ext/ folder.
 * </p>
 * 
 * @author Charles Anderson
 */
public class DarkMonkeyLookAndFeel extends com.nilo.plaf.nimrod.NimRODLookAndFeel{
    
    public static final String dmLAFDefault = "DarkMonkey.theme";
    public static final String DEJA_VU_SANS_CONDENSED_PLAIN_12 = "DejaVu Sans Condensed-PLAIN-12";

    protected static NimRODTheme nrTheme = new NimRODTheme();
    private final HashMap<String, Properties> propertiesMap = new HashMap<String, Properties>();
    private final Properties fontProperties = new Properties();

    public DarkMonkeyLookAndFeel(){
        super();

        try {
            fontProperties.load(this.getClass()
                    .getResourceAsStream("/src/org/jme3/netbeans/plaf/darkmonkey/fonts/lang.propeties"));
        } catch (Exception e) {
            // NOP
        }

        // Todo: replace following code with proper loading
        //  From DarkMonkey.theme
        // This replaces getXYZTheme(), they will be seperate Files
        
        StyleSheet styleSheet = new HTMLEditorKit().getStyleSheet();
        
        try {
                      
            // Since the Blue Theme is the Default one (and the fallback for missing values), it'll always be defined.
            Properties prop = new Properties();
            prop.put("linkColor", "#4673DB");
            prop.put("nb.versioning.added.color", "#49D249");
            prop.put("nb.versioning.modified.color", "#1AB8FF");
            prop.put("nb.versioning.deleted.color", "#FFAFAF");
            prop.put("nb.versioning.conflicted.color", "#FF6464");
            prop.put("nb.versioning.ignored.color", "#8E8E8E");
            prop.put("nb.diff.added.color", "#2B552B");
            prop.put("nb.diff.changed.color", "#285570");
            prop.put("nb.diff.deleted.color", "#552B2B");
            prop.put("nb.diff.applied.color", "#447152");
            prop.put("nb.diff.notapplied.color", "#43698D");
            prop.put("nb.diff.unresolved.color", "#821E1E");
            prop.put("nb.diff.sidebar.deleted.color", "#552B2B");
            prop.put("nb.diff.sidebar.changed.color", "#1E4B70");
            propertiesMap.put("blue", prop);
                
            if (NbPreferences.root().nodeExists("laf")) {
                String color = NbPreferences.root().node("laf").get("darkmonkey.color", null);
                if (color == null) { /* Create key with default value */
                    NbPreferences.root().node("laf").put("darkmonkey.color", "blue");
                    color = "blue";
                }
      
                switch (color.toLowerCase()) {
                    case "blue":
                        setCurrentTheme(getBlueTheme());
                        
                        styleSheet.addRule("a {color: " + propertiesMap.get("blue").getProperty("linkColor") + ";}"); // We use prop.get so you only have to change the color once.
                        break;
                        
                    case "yellow":
                        setCurrentTheme(getYellowTheme());
                        prop = new Properties();
                        prop.put("linkColor", "#FFD200");
                        
                        propertiesMap.put("yellow", prop);
                        styleSheet.addRule("a {color: " + prop.getProperty("linkColor") + ";}");
                        break;
                    
                    case "yellow-two":
                        setCurrentTheme(getYellowTwoTheme());
                        prop = new Properties();
                        prop.put("linkColor", "#BAA027");
                        
                        propertiesMap.put("yellow-two", prop);
                        styleSheet.addRule("a {color: " + prop.getProperty("linkColor") + ";}");
                        break;
                        
                    case "manual":
                        NimRODTheme nt = new NimRODTheme();
                        Preferences pref = NbPreferences.root().node("laf");
                        nt.setBlack(Color.decode(pref.get("darkmonkey.color.black", "#E8EAE0")));
                        nt.setWhite(Color.decode(pref.get("darkmonkey.color.white", "#373737")));
                        nt.setPrimary1(Color.decode(pref.get("darkmonkey.color.primary1", "#1A28BD")));
                        nt.setPrimary2(Color.decode(pref.get("darkmonkey.color.primary2", "#233FB0")));
                        nt.setPrimary3(Color.decode(pref.get("darkmonkey.color.primary3", "#3AA5F2")));
                        nt.setSecondary1(Color.decode(pref.get("darkmonkey.color.secondary1", "#303030")));
                        nt.setSecondary2(Color.decode(pref.get("darkmonkey.color.secondary2", "#3A3A3A")));
                        nt.setSecondary3(Color.decode(pref.get("darkmonkey.color.secondary3", "#515151")));
                        nt.setFrameOpacity(180);
                        nt.setMenuOpacity(219);
                        // apply DejaVu font if supported
                        applyDejaVuFontIfSupported(nt);
                        setCurrentTheme(nt);
                        
                        prop = new Properties();
                        prop.put("linkColor", pref.get("darkmonkey.color.linkcolor", "#233fb0"));
                        
                        for (String key : pref.keys()) {
                            if (key.startsWith("darkmonkey.color.nb.")) {
                                prop.put(key.substring("darkmonkey.color.".length()), pref.get(key, "#FF0000"));
                            }
                        }
                        
                        propertiesMap.put("manual", prop);
                        styleSheet.addRule("a {color:" + prop.getProperty("linkColor") + ");}");
                        break;
                        
                    case "legacy":
                        setCurrentTheme(getLegacyTheme());
                        prop = new Properties();
                        prop.put("linkColor", "#CC8D56");
                        
                        propertiesMap.put("legacy", prop);
                        styleSheet.addRule("a {color: " + prop.getProperty("linkColor") + ";}");
                        break;
                        
                    case "debug":
                        setCurrentTheme(getDebugTheme());
                        prop = new Properties();
                        prop.put("linkColor", "#00ff00");
                        
                        propertiesMap.put("debug", prop);
                        styleSheet.addRule("a {color: " + prop.getProperty("linkColor") + ";}");
                        break;
                        
                    case "pony":
                        setCurrentTheme(getPonyTheme());
                        prop = new Properties();
                        prop.put("linkColor", "#CF3E9C");
                        
                        propertiesMap.put("pony", prop);
                        styleSheet.addRule("a {color: " + prop.getProperty("linkColor") + ";}");
                        break;
                }
            } else {
                setCurrentTheme(getBlueTheme());
                styleSheet.addRule("a {color:" + propertiesMap.get("blue").getProperty("linkColor") + ";}");
            }
        } catch (Exception e) {
            setCurrentTheme(getBlueTheme());
            Properties prop = new Properties();
            prop.put("linkColor", "#4673DB");
            prop.put("nb.versioning.added.color", "#49D249");
            prop.put("nb.versioning.modified.color", "#1AB8FF");
            prop.put("nb.versioning.deleted.color", "#FFAFAF");
            prop.put("nb.versioning.conflicted.color", "#FF6464");
            prop.put("nb.versioning.ignored.color", "#8E8E8E");
            prop.put("nb.diff.added.color", "#2B552B");
            prop.put("nb.diff.changed.color", "#285570");
            prop.put("nb.diff.deleted.color", "#552B2B");
            prop.put("nb.diff.applied.color", "#447152");
            prop.put("nb.diff.notapplied.color", "#43698D");
            prop.put("nb.diff.unresolved.color", "#821E1E");
            prop.put("nb.diff.sidebar.deleted.color", "#552B2B");
            prop.put("nb.diff.sidebar.changed.color", "#1E4B70");
            propertiesMap.put("blue", prop);
            styleSheet.addRule("a {color:" + prop.getProperty("linkColor") + ";}");
        }
    }
    
    /**
     * This method override, getID() returns the String "DarkMonkey" for 
     * registering this Look And Feel with the UImanager.
     * @return String "DarkMonkey"
     */
    @Override
    public String getID() {
        return "DarkMonkey";
    }

    /**
     * This method override, getName() returns the String "DarkMonkey" for 
     * its Look and Feel Name. I don't know that this is important, but is
     * overridden anyway, for completion.
     * @return String "DarkMonkey"
     */
    @Override
    public String getName() {
        return "DarkMonkey";
    }

    /**
     * This method override, getDescription() returns the String 
     * "Look and Feel DarkMonkey - 2016, based on NimROD 2007" for 
     * instances of future programming that might use it as a tool tip or 
     * small descriptor in their Look and Feel modules.
     * @return String "Look and Feel DarkMonkey - 2016, based on NimROD 2007"
     */
    @Override
    public String getDescription() {
        return "Look and Feel DarkMonkey - 2016, based on NimROD 2007";
    }
    
       
    @Override
    protected void initClassDefaults( UIDefaults table) {
        super.initClassDefaults( table);
        // MenuBarUI currently doesn't compile table.put("MenuBarUI", "org.jme3.netbeans.plaf.darkmonkey.components.MenuBarUI");
        /*
        for( Enumeration en = table.keys(); en.hasMoreElements(); ) {
            System.out.println( "[" + en.nextElement() + "]");
        }
        */
    }
    
    @Override
    protected void initComponentDefaults( UIDefaults table) {
        super.initComponentDefaults( table);
        
        table.put("Tree.collapsedIcon", DarkMonkeyIconFactory.getTreeCollapsedIcon());
        table.put("Tree.expandedIcon", DarkMonkeyIconFactory.getTreeExpandedIcon());
        // 
        /*
        for( Enumeration en = table.keys(); en.hasMoreElements(); ) {
            System.out.println( "[" + en.nextElement() + "]");
        }
        */
             
    }
    
    private NimRODTheme getBlueTheme() {
        NimRODTheme nt = new NimRODTheme(); //nbres:/org.jme3.netbeans.plaf.darkmonkey
        
        nt.setBlack(Color.decode("#E8EAE0"));
        nt.setWhite(Color.decode("#373737"));
        nt.setPrimary1(Color.decode("#1A28BD"));
        nt.setPrimary2(Color.decode("#233FB0"));
        nt.setPrimary3(Color.decode("#3AA5F2"));
        nt.setSecondary1(Color.decode("#303030"));
        nt.setSecondary2(Color.decode("#3A3A3A"));
        nt.setSecondary3(Color.decode("#515151"));
        nt.setFrameOpacity(180);
        nt.setMenuOpacity(219);
        // apply DejaVu font if supported
        applyDejaVuFontIfSupported(nt);
        return nt;
    }
        
    /**
     * This is the Yellow Theme. It's the new default one, since Bananas = Yellow
     * @return 
     */
    private NimRODTheme getYellowTheme() {
        NimRODTheme nt = new NimRODTheme(); //nbres:/org.jme3.netbeans.plaf.darkmonkey
        
        nt.setBlack(Color.decode("#E8EAE0"));
        nt.setWhite(Color.decode("#262626"));
        nt.setPrimary1(Color.decode("#F3EF02"));
        nt.setPrimary2(Color.decode("#F3C802"));
        nt.setPrimary3(Color.decode("#FFB54D"));
        nt.setSecondary1(Color.decode("#303030"));
        nt.setSecondary2(Color.decode("#3A3A3A"));
        nt.setSecondary3(Color.decode("#515151"));
        nt.setFrameOpacity(180);
        nt.setMenuOpacity(219);
        // apply DejaVu font if supported
        applyDejaVuFontIfSupported(nt);
        return nt;
    }
    
    private NimRODTheme getYellowTwoTheme() {
        NimRODTheme nt = new NimRODTheme();
        
        nt.setBlack(Color.decode("#F3C802")); // Font Foreground
        nt.setWhite(Color.decode("#262626")); // Font Background
        nt.setPrimary1(Color.decode("#2040D0")); // Surrounds Bars and Shortcuts
        nt.setPrimary2(Color.decode("#1D3DBF")); // mainly used for bars 
        nt.setPrimary3(Color.decode("#1D3DBF")); // e.g. for open tabs (Application)
        nt.setSecondary1(Color.decode("#000000")); // Outlines, some decoration and tooltips for disabled buttons
        nt.setSecondary2(Color.decode("#A6A086")); // Selected inactive things (and disabled text)
        nt.setSecondary3(Color.decode("#323232")); // Unselected Frames (and general background color)
        nt.setFrameOpacity(180);
        nt.setMenuOpacity(219);
        // apply DejaVu font if supported
        applyDejaVuFontIfSupported(nt);

        return nt;
    }
    
    
    /**
     * This is the Legacy Theme. It's the one that DarkMonkey had before alpha-4
     * @return 
     */
    private NimRODTheme getLegacyTheme() {
        NimRODTheme nt = new NimRODTheme(); //nbres:/org.jme3.netbeans.plaf.darkmonkey
        
        nt.setBlack(Color.decode("#E8EAE0"));
        nt.setWhite(Color.decode("#262626"));
        nt.setPrimary1(Color.decode("#77411D"));
        nt.setPrimary2(Color.decode("#9E5F28"));
        nt.setPrimary3(Color.decode("#948519"));
        nt.setSecondary1(Color.decode("#303030"));
        nt.setSecondary2(Color.decode("#3A3A3A"));
        nt.setSecondary3(Color.decode("#515151"));
        nt.setFrameOpacity(180);
        nt.setMenuOpacity(219);
        // apply DejaVu font if supported
        applyDejaVuFontIfSupported(nt);

        return nt;
    }
        
    /**
     * Return the Debug Theme (It will have R, G and B so you know what Primary1 means etc)
     * @return 
     */
    private NimRODTheme getDebugTheme() {
        NimRODTheme nt = new NimRODTheme(); //nbres:/org.jme3.netbeans.plaf.darkmonkey
        
        nt.setBlack(Color.decode("#000000")); // Font Foreground
        nt.setWhite(Color.decode("#FFFFFF")); // Font Background
        nt.setPrimary1(Color.decode("#FF0000")); // Surrounds Bars and Shortcuts
        nt.setPrimary2(Color.decode("#00FF00")); // mainly used for bars 
        nt.setPrimary3(Color.decode("#0000FF")); // e.g. for open tabs (Application)
        nt.setSecondary1(Color.decode("#FFFF00")); // Outlines, some decoration and tooltips for disabled buttons
        nt.setSecondary2(Color.decode("#FF00FF")); // Selected inactive things (and disabled text)
        nt.setSecondary3(Color.decode("#00FFFF")); // Unselected Frames
        nt.setFrameOpacity(180);
        nt.setMenuOpacity(219);
        // apply DejaVu font if supported
        applyDejaVuFontIfSupported(nt);
        
        return nt;
    }
    
        
    private NimRODTheme getPonyTheme() {
        NimRODTheme nt = new NimRODTheme(); //nbres:/org.jme3.netbeans.plaf.darkmonkey
        
        nt.setBlack(Color.decode("#ED8EBB"));
        nt.setWhite(Color.decode("#222222"));
        nt.setPrimary1(Color.decode("#42002B"));
        nt.setPrimary2(Color.decode("#AF0072"));
        nt.setPrimary3(Color.decode("#0088CC"));
        nt.setSecondary1(Color.decode("#0088CC"));
        nt.setSecondary2(Color.decode("#003047"));
        nt.setSecondary3(Color.decode("#333333"));
        nt.setFrameOpacity(180);
        nt.setMenuOpacity(219);
        // apply DejaVu font if supported
        applyDejaVuFontIfSupported(nt);
        
        return nt;
    }
    
    
    /**
     * This Method will simplify the lookup of any theme-related value.
     * It will see if it's a built in theme or a manual theme and therefore look at the right place.
     * 
     * @param name The property to look for
     * @param defaultValue The Value to return when the property isn't defined.
     * @return The String Value of said property.
     */
    public String getThemeProperty(String name, String defaultValue) {
        try {
            if (NbPreferences.root().nodeExists("laf")) {
                String color = NbPreferences.root().node("laf").get("darkmonkey.color", "blue");

                return propertiesMap.get(color.toLowerCase()).getProperty(name, defaultValue);
            } else {
                return propertiesMap.get("blue").getProperty(name, defaultValue);
            }
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    /**
     * This Method will simplify the lookup of any theme-related value.
     * It will return null if the value wasn't found.
     * 
     * @see #getThemeProperty(java.lang.String, java.lang.String) 
     * @param name The Parameter to look for
     * @return The String Value of said property (or null)
     */
    public String getThemeProperty(String name) {
        return getThemeProperty(name, null);
    }
    
    public String getDefaultThemeProperty(String name, String defaultValue) {
        return propertiesMap.get("blue").getProperty(name, defaultValue);
    }

    private void applyDejaVuFontIfSupported(NimRODTheme theme) {
        if (dejaVuFontIsSupported()) {
            theme.setFont(Font.decode(DEJA_VU_SANS_CONDENSED_PLAIN_12));
        }
    }

    /**
     * @return true if DejaVu fonts support glyphs for user's locale
     */
    private Boolean dejaVuFontIsSupported() {
        String lang = System.getProperty("user.language").toLowerCase();
        String country = System.getProperty("user.country").toLowerCase();
        Boolean langCdMatch = fontProperties.containsKey(lang);
        Boolean langCountryCdMatch = fontProperties.containsKey(String.format("%s-%s", lang, country));
        return langCdMatch || langCountryCdMatch;
    }
}
