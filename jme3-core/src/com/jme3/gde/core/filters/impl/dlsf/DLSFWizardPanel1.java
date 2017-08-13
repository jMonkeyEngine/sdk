package com.jme3.gde.core.filters.impl.dlsf;

import com.jme3.math.FastMath;
import java.awt.Component;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;

@SuppressWarnings({"unchecked", "rawtypes"})
public class DLSFWizardPanel1 implements WizardDescriptor.ValidatingPanel<WizardDescriptor> {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private Component component;
    private static final Logger LOG = Logger.getLogger(DLSFWizardPanel1.class.getName());    
    private int shadowMapSize;
    private int nbSplits;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public Component getComponent() {
        if (component == null) {
            component = new DLSFVisualPanel1();
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx(SampleWizardPanel1.class);
    }

    @Override
    public boolean isValid() {
        // If it is always OK to press Next or Finish, then:
        return true;
        // If it depends on some condition (form filled out...), then:
        // return someCondition();
        // and when this condition changes (last form field filled in...) then:
        // fireChangeEvent();
        // and uncomment the complicated stuff below.
    }

    @Override
    public final void addChangeListener(ChangeListener l) {
    }

    @Override
    public final void removeChangeListener(ChangeListener l) {
    }
    
    void tryParseShadowMapSize(DLSFVisualPanel1 comp) throws WizardValidationException {
        try {
            shadowMapSize = Integer.parseInt(comp.getjShadowMapField().getText());
        } catch (NumberFormatException nfe) {
            throw new WizardValidationException(comp, "NumberFormatException when trying to parse the ShadowMapSize!", null);
        }
        
        double log2 = Math.log(shadowMapSize) / Math.log(2);

        if (!FastMath.approximateEquals((float)(Math.floor(log2) - log2), 0f)) {
            LOG.warning("The Shadow Map Size is not power of two!");
        }
    }
    
    void tryParseNbSplits(DLSFVisualPanel1 comp) throws WizardValidationException {
        try {
            nbSplits = Integer.parseInt(comp.getjNbSplitsField().getText());
        } catch (NumberFormatException nfe) {
            throw new WizardValidationException(comp, "NumberFormatException when trying to parse the Number of Splits!", null);
        }
    }

    @Override
    public void validate() throws WizardValidationException {
        DLSFVisualPanel1 comp = (DLSFVisualPanel1) getComponent();
        tryParseNbSplits(comp);
        tryParseShadowMapSize(comp);
    }  
    
    // You can use a settings object to keep track of state. Normally the
    // settings object will be the WizardDescriptor, so you can use
    // WizardDescriptor.getProperty & putProperty to store information entered
    // by the user.
    @Override
    public void readSettings(WizardDescriptor settings) {
    }

    @Override
    public void storeSettings(WizardDescriptor settings) {
        WizardDescriptor wiz = settings;
        
        wiz.putProperty("shadowMapSize", shadowMapSize);
        wiz.putProperty("nbSplits", nbSplits);
    }
}
