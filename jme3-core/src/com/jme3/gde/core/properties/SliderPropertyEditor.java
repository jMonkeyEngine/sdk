package com.jme3.gde.core.properties;

import com.jme3.gde.core.util.SliderInplaceEditor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditorSupport;
import java.util.LinkedList;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;

/**
 *
 * @author Nehon
 */
public class SliderPropertyEditor extends PropertyEditorSupport implements ExPropertyEditor, InplaceEditor.Factory {

    private final LinkedList<PropertyChangeListener> listeners = new LinkedList<>();
    PropertyEnv env;

    public SliderPropertyEditor() {
        ed = new SliderInplaceEditor(0f, 100f);
    }

    @Override
    public void attachEnv(PropertyEnv env) {
        this.env = env;
        env.registerInplaceEditorFactory(this);
    }

    public SliderPropertyEditor(int min, int max) {
        ed = new SliderInplaceEditor(min, max);
    }

    public SliderPropertyEditor(float min, float max) {
        ed = new SliderInplaceEditor(min, max);
    }

    public void setRange(float min, float max) {
        ed.setRangeFloat(min, max);
    }

    public void setRange(int min, int max) {
        ed.setRangeInt(min, max);
    }

    @Override
    public String getAsText() {
        return ed.getValue().toString();
    }

    @Override
    public void setAsText(String s) {
        Object o = ed.getValue();
        ed.setAsText(s);
        notifyListeners(o, ed.getValue());
    }
    private SliderInplaceEditor ed = null;

    @Override
    public InplaceEditor getInplaceEditor() {
        return ed;
    }

    @Override
    public void setValue(Object value) {
        ed.setValue(value);

    }

    @Override
    public Object getValue() {
        return ed.getValue();
    }

    public PropertyEnv getEnv() {
        return env;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(Object before, Object after) {
        for (PropertyChangeListener propertyChangeListener : listeners) {
            //TODO: check what the "programmatic name" is supposed to be here.. for now its Quaternion
            propertyChangeListener.propertyChange(new PropertyChangeEvent(this, null, before, after));
        }
    }
}
