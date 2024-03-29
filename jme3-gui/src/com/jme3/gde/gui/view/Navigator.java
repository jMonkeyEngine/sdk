/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.gde.gui.view;

import com.jme3.gde.gui.nodes.GElementNode;
import com.jme3.gde.gui.nodes.GUINode;
import jada.ngeditor.controller.CommandProcessor;
import jada.ngeditor.controller.commands.SelectCommand;
import jada.ngeditor.listeners.events.SelectionChanged;
import jada.ngeditor.model.GUI;
import jada.ngeditor.model.GuiEditorModel;
import jada.ngeditor.model.elements.GElement;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JComponent;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author cris
 */
@NavigatorPanel.Registration(mimeType = "text/x-niftygui+xml", displayName="Gui View")
public class Navigator extends javax.swing.JPanel implements NavigatorPanel,ExplorerManager.Provider , Observer, PropertyChangeListener{
    private Lookup lookup;
    private  ExplorerManager mgr = new ExplorerManager();
    private final BeanTreeView beanTreeView;
    /**
     * Creates new form Navigator
     */
    public Navigator() {
        initComponents();
        setLayout(new BorderLayout());
        beanTreeView = new BeanTreeView();
        add(beanTreeView, BorderLayout.CENTER);
        final GuiEditorModel model = (GuiEditorModel) CommandProcessor.getInstance().getObservable();
        model.addObserver(this);
        if(model.getCurrent() != null){
            try {
                this.intNavigator(model.getCurrent());
            } catch (PropertyVetoException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 244, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 274, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    @Override
    public String getDisplayName() {
       return "GuiView";
    }

    @Override
    public String getDisplayHint() {
       return "Easy view for your gui";
    }

    @Override
    public JComponent getComponent() {
       return this;
    }

    @Override
    public void panelActivated(Lookup context) {
        
    }

    @Override
    public void panelDeactivated() {
        ExplorerUtils.activateActions(mgr, false);
    }

    @Override
    public Lookup getLookup() {
       return lookup;
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return mgr;
    }

    @Override
    public void update(Observable o, Object arg) {
       if(o instanceof GuiEditorModel){
           try {
               GuiEditorModel model = (GuiEditorModel) o;
               model.getCurrent().addObserver(this);
               model.getCurrent().getSelection().addObserver(this);
               this.intNavigator(model.getCurrent());
           } catch (PropertyVetoException ex) {
               Exceptions.printStackTrace(ex);
           }
       }
       
       if(arg instanceof SelectionChanged){
           SelectionChanged event = (SelectionChanged) arg;
           if(event.getNewSelection().isEmpty()){
               return;
           }
           ArrayList<String> path = new ArrayList<String>();
           GElement parent = ((SelectionChanged)arg).getElement();
           while(parent!=null){
               path.add(parent.getID());
               parent = parent.getParent();
           }
           Node result = mgr.getRootContext();
           for(int i=path.size()-1;i>=0;i--){
               if(result!=null){
                    result = result.getChildren().findChild(path.get(i));
               }
           }
            try {
                if(result!=null){
                    mgr.setSelectedNodes(new Node[]{result});
                }
                
            } catch (PropertyVetoException ex) {
                Exceptions.printStackTrace(ex);
            }
       }
        beanTreeView.updateUI();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ExplorerManager.PROP_SELECTED_NODES)) {
            Node[] newValue = (Node[]) evt.getNewValue();
            if (newValue.length > 0) {
                AbstractNode firstSelected = (AbstractNode) newValue[0];
                if (firstSelected instanceof GElementNode) {
                    GElement element = ((GElementNode) firstSelected).getGelement();
                    GUI gui = ((GUINode)mgr.getRootContext()).getGui();
                    gui.getSelection().deleteObserver(this); // I don't wont to get notified about this selection change
                    SelectCommand command = CommandProcessor.getInstance().getCommand(SelectCommand.class);
                    command.setElement(element);
                    try {
                        CommandProcessor.getInstance().excuteCommand(command);
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }
                     gui.getSelection().addObserver(this);
                    
                }
            }
        }
    }


    private void intNavigator(GUI gui) throws PropertyVetoException {
        
       
        ExplorerUtils.activateActions(mgr, true);
       
        
        AbstractNode guiRoot = new GUINode(gui);
        guiRoot.setName("Gui");
        this.mgr.setRootContext(guiRoot);
        this.beanTreeView.updateUI();
        mgr.addPropertyChangeListener(this);
        this.mgr.setSelectedNodes(new Node[]{guiRoot});
        Lookup lookup1 = ExplorerUtils.createLookup(mgr, getActionMap());
        lookup = new ProxyLookup(lookup1);
        
    }
}
