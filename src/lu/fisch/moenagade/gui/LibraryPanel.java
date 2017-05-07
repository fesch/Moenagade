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

package lu.fisch.moenagade.gui;

import lu.fisch.moenagade.model.Library;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JPanel;
import lu.fisch.graphics.ColorUtils;
import lu.fisch.moenagade.bloxs.Element;
import lu.fisch.moenagade.bloxs.Element.Type;
import lu.fisch.moenagade.bloxs.Item;
import lu.fisch.moenagade.model.BloxsColors;

/**
 *
 * @author robert.fisch
 */
public class LibraryPanel extends JPanel {
    private final String label;

    public LibraryPanel(String label) {
        this.label = label;
        
        // add mouse listener
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent me) {
                //
            }

            @Override
            public void mousePressed(MouseEvent me) {
                // loop through all elements contained inside the editor
                //for (int i = 0; i < elements.size() && Library.getInstance().setSelected()==null; i++) {
                for (int i = elements.size()-1; i >=0 && Library.getInstance().getSelected()==null; i--) {
                    Element element = elements.get(i);
                    // try to select it or one of it's sub-elements
                    
                    Element sel = element.getSelected(me.getPoint());
                    // if there is a Library.getInstance().getSelected() element
                    
                    if(sel!=null)
                    {
                        // clone the element
                        //Library.getInstance().setSelected((Element) Element.cloneObject(Library.getInstance().getSelected()));
                        Library.getInstance().setSelected(sel.clone());
                        
                        
                        /*
                        System.out.println("Selected item is: "+Library.getInstance().getSelected());
                        if(Library.getInstance().getSelected()!=null)
                        {
                            System.out.println("Selected item is: "+Library.getInstance().getSelected().getClass().getSimpleName());
                            System.out.println("Selected item is: "+Library.getInstance().getSelected().getType());
                        }
                        if(Library.getInstance().getSelected().getParent()!=null)
                        {
                            System.out.println("Selected parent is: "+Library.getInstance().getSelected().getParent());
                            System.out.println("Selected parent is: "+Library.getInstance().getSelected().getParent().getClass().getSimpleName());
                            System.out.println("Selected parent2 is: "+Library.getInstance().getSelected().getParent().getParent());
                            if(Library.getInstance().getSelected().getParent().getParent()!=null)
                                System.out.println("Selected parent2 is: "+Library.getInstance().getSelected().getParent().getParent().getClass().getSimpleName());
                        }/**/
                        
                        if(Library.getInstance().getSelected().getType()==Type.EXPRESSION)
                        {
                            System.out.println("TOO BE CHECKED --> LibraryPanel");
                            // get the parent (should be Value)
                            if(Library.getInstance().getSelected().getParent()!=null &&
                               Library.getInstance().getSelected().getParent().getParent()!=null &&
                               Library.getInstance().getSelected().getParent().getParent().getParent()!=null)
                            {
                                Library.getInstance().setSelected(Library.getInstance().getSelected().getParent().getParent().getParent().clone());
                            }
                        }

                        else if(Library.getInstance().getSelected().getType()==Type.VALUE || 
                           Library.getInstance().getSelected().getType()==Type.LIST || 
                           Library.getInstance().getSelected().getType()==Type.PARAMETERS)
                        {
                            // get the parent (should be Value)
                            if(Library.getInstance().getSelected().getParent()!=null)
                                Library.getInstance().setSelected(Library.getInstance().getSelected().getParent().clone());
                            // get the parent (should be contained element)
                            //if(Library.getInstance().getSelected().getParent()!=null)
                            //    Library.getInstance().setSelected(Library.getInstance().getSelected().getParent().clone());
                        }

                        // calculate & save the delta-click position
                        Library.getInstance().setSelectedDelta(new Dimension(me.getX()-Library.getInstance().getSelected().getOffset().x, 
                                                    me.getY()-Library.getInstance().getSelected().getOffset().y));
                        // reposition it
                        Library.getInstance().getSelected().setOffset(new Point(me.getX()-Library.getInstance().getSelectedDelta().width,
                                             me.getY()-Library.getInstance().getSelectedDelta().height));
                        // add the element to the list of editor elements
                        // and make shure it is last to be drawn!!
                        if(elements.contains(Library.getInstance().getSelected()))
                            elements.remove(Library.getInstance().getSelected());
                        elements.add(Library.getInstance().getSelected());
                        // break the loop
                        break;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                if(Library.getInstance().getSelected()!=null)
                {
                    // repostion element
                    Library.getInstance().getSelected().setOffset(new Point(me.getX()-Library.getInstance().getSelectedDelta().width,
                                                 me.getY()-Library.getInstance().getSelectedDelta().height));
                    elements.remove(Library.getInstance().getSelected());
                    
                }   
                Library.getInstance().setSelected(null);
                repaint();
            }

            @Override
            public void mouseEntered(MouseEvent me) {
                //
            }

            @Override
            public void mouseExited(MouseEvent me) {
                //
                //System.out.println("Exit");
        
            }
        });
        
        // add mouse motion listener
        this.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent me) {
                
                //System.out.println("Point: "+me.getPoint()+" DElta: "+Library.getInstance().getSelectedDelta());
                if(((me.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) && Library.getInstance().getSelected()!=null)
                {
                    // move element
                    Library.getInstance().getSelected().setOffset(new Point(me.getX()-Library.getInstance().getSelectedDelta().width,
                                                 me.getY()-Library.getInstance().getSelectedDelta().height));
                    repaint();
                }
                
            }

            @Override
            public void mouseMoved(MouseEvent me) {
               //
            }
        });
    }

    public String getLabel() {
        return label;
    }


    
    
     // list of elements
    private ArrayList<Element> elements = new ArrayList<>();
   
    
    public void addElement(Element element, Point position)
    {
        element.setOffset(position);
        elements.add(element);
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;
        RenderingHints rh = new RenderingHints(
                 RenderingHints.KEY_TEXT_ANTIALIASING,
                 RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHints(rh);
    
        g.setColor(BloxsColors.$BACKGROUND);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        for (int i = 0; i < elements.size(); i++) {
            Element get = elements.get(i);
            get.draw((Graphics2D) g);
        }
    }
    
    
}
