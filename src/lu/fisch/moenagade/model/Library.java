/*
    Moenagade
    °°°°°°°°°

    Moenagade is a graphical programming tool for beginners. For this
    reason it is rather limited in its functionality. It aims to help
    students to gain knowledge about the programming structures. Due
    to the explicit conversion to real and executable Java code, it 
    should help to make an easy from block programming to coding.

    Copyright (C) 2009  Bob Fisch

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or any
    later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package lu.fisch.moenagade.model;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Hashtable;
import lu.fisch.moenagade.bloxs.Element;
import lu.fisch.moenagade.gui.LibraryPanel;

/**
 *
 * @author robert.fisch
 */
public class Library implements MouseMotionListener, MouseListener{
    
    private static final Library instance = new Library();
    
    private Hashtable<String,ArrayList<Element>> elements = new Hashtable<>();
    private ArrayList<LibraryPanel> panels = new ArrayList<>();
    
    private Library() 
    {
    }
    
    public static Library getInstance()
    {
        return instance;
    }
    
    private Element selected = null;
    private Dimension selectedDelta = null;
    private BloxsEditor bloxsEditor = null;
    private Project project = null;

    public Element getSelected() {
        return selected;
    }

    public void setSelected(Element selected) {
        this.selected = selected;
    }

    public Dimension getSelectedDelta() {
        return selectedDelta;
    }

    public void setSelectedDelta(Dimension selectedDelta) {
        this.selectedDelta = selectedDelta;
    }

    public BloxsEditor getBloxsEditor() {
        return bloxsEditor;
    }

    public void setBloxsEditor(BloxsEditor bloxsEditor) {
        this.bloxsEditor = bloxsEditor;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
    
    
    
    public void register(Element element, String tab)
    {
        register(element,tab,-1);
    }
    
    public void register(Element element, String tab, int position)
    {
        // check if tab exists
        if(!elements.containsKey(tab))
            elements.put(tab,new ArrayList<>());
        // get tab
        ArrayList<Element> list = elements.get(tab);
        // can we insert a desired position?
        if(position <= list.size() && position>0)
            list.add(position,element);
        else
            list.add(element);
    }
    
    public void load(String destination)
    {
        BloxsDefinitions bds = new BloxsDefinitions();
        panels.clear();
        
        for (int i = 0; i < bds.getCategories().size(); i++) {
            String label = bds.getCategories().get(i);
            // create the panel
            LibraryPanel panel = new LibraryPanel(label);
            // add it to the tabbed pane
            panels.add(panel);
            
            // add elements
            ArrayList<BloxsDefinition> bd = bds.getDefinitions(label);
            int y = Element.PADDING_BT;
            for (int j = 0; j < bd.size(); j++) {
                BloxsDefinition bed = bd.get(j);
                
                if(
                        bed.getDestinations().trim().isEmpty() || 
                        bed.getDestinations().contains(destination) || 
                        destination.trim().isEmpty()
                  )
                {
                    // create the element
                    Element element = new Element(bed);
                    // draw it on some offline canvas
                    element.draw((Graphics2D) (new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)).getGraphics());
                    // add it to the panel
                    panel.addElement(element, new Point(Element.PADDING_LR,y));
                    // add some space
                    y+=Element.PADDING_BT+element.getTotalHeight();
                }
            }
            panel.setPreferredSize(new Dimension(100, y+Element.PADDING_BT));
            panel.revalidate();
            
        }
    }
    
    public ArrayList<LibraryPanel> getTabs()
    {
        return panels;
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        if(bloxsEditor!=null) bloxsEditor.mouseDragged(me);
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        if(bloxsEditor!=null) bloxsEditor.mouseMoved(me);
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        if(bloxsEditor!=null) bloxsEditor.mouseClicked(me);
    }

    @Override
    public void mousePressed(MouseEvent me) {
        if(bloxsEditor!=null) bloxsEditor.mousePressed(me);
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        if(bloxsEditor!=null) bloxsEditor.mouseReleased(me);
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        if(bloxsEditor!=null) bloxsEditor.mouseEntered(me);
    }

    @Override
    public void mouseExited(MouseEvent me) {
        if(bloxsEditor!=null) bloxsEditor.mouseExited(me);
    }
    
}
