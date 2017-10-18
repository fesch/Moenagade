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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Stack;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import lu.fisch.moenagade.MainFrame;
import lu.fisch.moenagade.Moenagade;
import lu.fisch.moenagade.bloxs.Element;
import lu.fisch.moenagade.bloxs.Element.Type;
import lu.fisch.moenagade.bloxs.List;
import lu.fisch.moenagade.bloxs.Parser;
import lu.fisch.moenagade.bloxs.Item;
import lu.fisch.moenagade.bloxs.Parameters;
import lu.fisch.moenagade.bloxs.VariableDefinition;
import lu.fisch.moenagade.gui.Change;
import lu.fisch.moenagade.gui.LibraryPanel;
import lu.fisch.moenagade.gui.dialogs.ConfigureParameters;
import oracle.jrockit.jfr.tools.ConCatRepository;
import org.xml.sax.SAXException;

/**
 *
 * @author robert.fisch
 */
public class BloxsEditor extends javax.swing.JPanel implements MouseMotionListener, MouseListener {

    // list of elements
    private ArrayList<Element> elements = new ArrayList<>();
    
    private Stack<ArrayList<Element>> undoStack = new Stack<>();
    private Stack<ArrayList<Element>> redoStack = new Stack<>();
    
    private BloxsClass bloxsClass = null;
    
    // mouse drag'n'ndrop
    private Element selected = null;
    private Dimension selectedDelta = new Dimension();
    
    private ArrayList<Element> drawLast = new ArrayList<>();
    
    private MainFrame mainFrame = null;
    
    private Element list = null;
    
    /**
     * Creates new form BloxsEditor
     */
    public BloxsEditor() {
        this(null);
    }
    
    public BloxsEditor(BloxsClass bloxsClass) {
        initComponents();     
        
        // add mouse listener
        this.addMouseListener(this);
        
        // add mouse motion listener
        this.addMouseMotionListener(this);
        
        this.bloxsClass=bloxsClass;
        if(bloxsClass!=null)
            elements=bloxsClass.getElements();
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON );
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                            RenderingHints.VALUE_RENDER_QUALITY );
    
        g.setColor(BloxsColors.$BACKGROUND);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        drawLast.clear();
        
        Rectangle result = new Rectangle();
        for (int i = 0; i < elements.size(); i++) {
            Element get = elements.get(i);
            result=get.draw((Graphics2D) g).union(result);
        }
        
        //System.out.println("Size: "+drawLast.size());
        for (int i = 0; i < drawLast.size(); i++) {
            drawLast.get(i).draw((Graphics2D) g);                
        }
        
        setPreferredSize(new Dimension(result.x+result.width, result.y+result.height));
        revalidate();
    }
    
    public void somethingChanged()
    {
        check();
        
        if(bloxsClass!=null)
        {
            bloxsClass.modified();
        }
        
        if(mainFrame!=null)
        {
            mainFrame.showCode();
            mainFrame.setTitleNew();
        }
        
        //System.out.println(getJavaCode());
        //mainFrame.getCode().setCode(getXml());
        //mainFrame.getCode().setCode(getJavaCode(0));
    }
    
    public ArrayList<Element> cloneElements()
    {
        ArrayList<Element> clone = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            clone.add(elements.get(i).clone().setEditor(this));
        }
        return clone;
    }
    
    public void pushUndo()
    {
        undoStack.push(cloneElements());
        redoStack.clear();
        //System.out.println("Undo: "+undoStack.size());
        //System.out.println("Redo: "+redoStack.size());
    }
    
    public void undo()
    {
        if(undoStack.size()>0)
        {
            redoStack.push(elements);
            elements = undoStack.pop();
            repaint();
        }
        
        //System.out.println("Undo: "+undoStack.size());
        //System.out.println("Redo: "+redoStack.size());
    }
    
    public void redo()
    {
        if(redoStack.size()>0)
        {
            undoStack.push(elements);
            elements = redoStack.pop();
            repaint();
        }
        
        //System.out.println("Undo: "+undoStack.size());
        //System.out.println("Redo: "+redoStack.size());
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
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void mouseDragged(MouseEvent me) {
        // convert point in case we got triggered by a libraryPanel
        Point clickPoint = me.getPoint();
        //System.out.println("me.source: "+me.getSource());
        
        if(me.getSource() instanceof LibraryPanel)
        {
            clickPoint = new java.awt.Point(me.getLocationOnScreen());
            SwingUtilities.convertPointFromScreen(clickPoint, this);
            /*
            // get the selected item if there is any
            if(selected==null)
            {
                if(Library.getInstance().getSelected()!=null)
                {
                    selected = Library.getInstance().getSelected().clone();
                    selectedDelta = (Dimension) Library.getInstance().getSelectedDelta().clone();
                    elements.add(selected);
                }
            }/**/
        }
        
        if(((me.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0))
        {
            if(selected!=null)
            {
                // disconnect it!
                selected.setParent(null);
                
                // move element
                selected.setOffset(new Point(clickPoint.x-selectedDelta.width,
                                             clickPoint.y-selectedDelta.height));
                if(selected.isElementary())
                {
                    selected.setOffset(new Point((clickPoint.x-selectedDelta.width) -((clickPoint.x-selectedDelta.width) % 10),
                                                 (clickPoint.y-selectedDelta.height)-((clickPoint.y-selectedDelta.height)% 10)));
                }
                for (int i = 0; i < elements.size(); i++) {
                    Element get = elements.get(i);
                    get.cleanDockStatus();
                    // check if docking is possible
                    Element dock = get.canDock(selected);                      
                }
                //System.out.println("Dock: "+dock);
            }

            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        //
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        //
    }

    private boolean hasElementOfType(String classname)
    {
        for (int i = 0; i < elements.size(); i++) {
            Element get = elements.get(i);
            if(get.getClassname().equals(classname)) return true;
        }
        return false;
    }
    
    @Override
    public void mousePressed(MouseEvent me) {
        
        // convert point in case we got triggered by a libraryPanel
        Point clickPoint = me.getPoint();
        //System.out.println("me.source: "+me.getSource());
        if(me.getSource() instanceof LibraryPanel)
        {
            clickPoint = new java.awt.Point(me.getLocationOnScreen());
            SwingUtilities.convertPointFromScreen(clickPoint, this);
            // get the selected item if there is any
            if(selected==null)
            {
                if(Library.getInstance().getSelected()!=null &&
                   ((Library.getInstance().getSelected().isElementary() &&
                   !hasElementOfType(Library.getInstance().getSelected().getClassname())) ||
                   (!Library.getInstance().getSelected().isElementary() 
                        || (Library.getInstance().getSelected().getClassname().equals("AttributeDefinition"))
                        || (Library.getInstance().getSelected().getClassname().equals("MethodDefinition"))
                    )))
                {
                    pushUndo();
                    selected = Library.getInstance().getSelected().clone();
                    selectedDelta = (Dimension) Library.getInstance().getSelectedDelta().clone();
                    addElement(selected);
                    selected.setParent(null);
                    return;
                }
            }
        }
        
        // first let's see if there is not an opened list
        if(list!=null)
        {
            // execute the event on the opened list
            mouseOnElement(list,me);
        }
        // if not, loop through all elements contained inside the editor
        else for (int i = elements.size()-1; i >=0 && selected==null; i--) {
            Element element = elements.get(i);
            // execute the event on the actual element,
            // break the loop if requested
            if(mouseOnElement(element,me)) break;
        }
        
        // NOT NEEDED HERE
        //somethingChanged();
    }
    
    public static boolean isValidJavaIdentifier(String s) {
        if (s.isEmpty()) {
            return false;
        }
        if (!Character.isJavaIdentifierStart(s.charAt(0))) {
            return false;
        }
        for (int i = 1; i < s.length(); i++) {
            if (!Character.isJavaIdentifierPart(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    private boolean mouseOnElement(Element element,MouseEvent me)
    {
        
            // try to select it or one of it's sub-elements
            //System.out.println("------------------------------");
            selected = element.getSelected(me.getPoint());
            // if there is a selected element
            
            //System.out.println("Selected item is: "+selected);
            if(selected!=null)
            {
                /*
                System.out.println("Selected item is: "+selected);
                System.out.println("ParamTypes: "+selected.getParamTypes());
                if(selected!=null)
                    System.out.println("Selected item type is: "+selected.getClass().getSimpleName());
                if(selected.getParent()!=null)
                {
                    System.out.println("Selected parent is: "+selected.getParent());
                    System.out.println("Selected parent type is: "+selected.getParent().getClassname());
                    System.out.println("Selected parent2 is: "+selected.getParent().getParent());
                    if(selected.getParent().getParent()!=null)
                        System.out.println("Selected parent2 type is: "+selected.getParent().getParent().getClassname());
                }/**/

                //System.out.println("Selected is: "+selected.getClassname());
                if(selected.getType()==Type.PARAMETERS)// && me.getClickCount()==2)
                {
                    Parameters parameters = ((Parameters)selected);
                    
                    if(parameters.allowConfig())
                    {
                        ConfigureParameters cp = ConfigureParameters.showModal(mainFrame, "Configure parameters",parameters.getDefinitions());
                        if(cp.isOK())
                        {
                            pushUndo();
                            parameters.setDefinitions(cp.getDefinitions());
                            refresh(new Change(selected.getParent(), selected.getPosition(), "parameters.configured", null, parameters.getDefinitions()));
                            somethingChanged();
                            selected=null;
                            repaint();
                        }
                    }
                    
                    selected=null;
                }
                else if(selected.getType()==Type.VALUE)
                {
                    if(me.getClickCount()==1)
                    {
                        boolean OK = false;
                            
                        String old = selected.getTitle();
                        String value = "";
                        
                        while(OK==false && value!=null)
                        {
                            value = (String) JOptionPane.showInputDialog(mainFrame, "Value", "Edit", JOptionPane.PLAIN_MESSAGE,null,null,old);
                           
                            if(value!=null)
                            {
                                OK = true;
                                 
                                // in case this is a variable definition
                                if(selected.getParent()!=null && selected.getParent().getClassname().equals("VariableDefinition"))
                                {
                                    // try to see if there is a variable with the same name in this block
                                    if(selected.hasVariableWithName(value) && !selected.getTitle().equals(value))
                                    {
                                        JOptionPane.showMessageDialog(mainFrame, "Sorry, but the actual block already contains\na variable with the given name!", "Error", JOptionPane.ERROR_MESSAGE, Moenagade.IMG_ERROR);
                                        value="";
                                        OK=false;
                                    }
                                    else if (!isValidJavaIdentifier(value) || !value.substring(0,1).toLowerCase().equals(value.substring(0,1)))
                                    {
                                        JOptionPane.showMessageDialog(mainFrame, "Sorry, but variable names must conform the naming conventions!", "Error", JOptionPane.ERROR_MESSAGE, Moenagade.IMG_ERROR);
                                        value="";
                                        OK=false;
                                    }
                                }
                                else if(selected.getParent().getClassname().equals("AttributeDefinition") && selected.getTopMostElement()!=null)
                                {
                                    if(selected.getTopMostElement().getEditor().hasAttributeWithName(value) && !selected.getTitle().equals(value))
                                    {
                                        JOptionPane.showMessageDialog(mainFrame, "Sorry, but the actual class already contains\nan attribute with the given name!", "Error", JOptionPane.ERROR_MESSAGE, Moenagade.IMG_ERROR);
                                        value="";
                                        OK=false;
                                    }
                                    else if (!isValidJavaIdentifier(value) || !value.substring(0,1).toLowerCase().equals(value.substring(0,1)))
                                    {
                                        JOptionPane.showMessageDialog(mainFrame, "Sorry, but attribute names must conform the naming conventions!", "Error", JOptionPane.ERROR_MESSAGE, Moenagade.IMG_ERROR);
                                        value="";
                                        OK=false;
                                    }
                                }
                                else if(selected.getParent().getClassname().equals("UserInteger"))
                                {
                                    try 
                                    {
                                        int i = Integer.valueOf(value);
                                    }
                                    catch(Exception e)
                                    {
                                        JOptionPane.showMessageDialog(mainFrame, "Sorry, but '"+value+"' is not a valid integer value!", "Error", JOptionPane.ERROR_MESSAGE, Moenagade.IMG_ERROR);
                                        value="";
                                        OK=false;
                                    }
                                }
                                else if(selected.getParent().getClassname().equals("UserFloat"))
                                {
                                    try 
                                    {
                                        float i = Float.valueOf(value);
                                    }
                                    catch(Exception e)
                                    {
                                        JOptionPane.showMessageDialog(mainFrame, "Sorry, but '"+value+"' is not a valid decimal value!", "Error", JOptionPane.ERROR_MESSAGE, Moenagade.IMG_ERROR);
                                        value="";
                                        OK=false;
                                    }
                                }

                                if(OK)
                                {
                                    pushUndo();
                                    selected.setTitle(value);

                                    // notify blockmost element
                                    if(selected.getParent().getClassname().equals("AttributeDefinition"))
                                        refresh(new Change(selected.getParent(), selected.getPosition(),"rename.attribute", old, value)); 
                                    // Variable & Co
                                    else if((selected.getParent().getClassname().equals("VariableDefinition") ||
                                             selected.getParent().getClassname().equals("For")) && 
                                            selected.getBlockMostElement()!=null)
                                        selected.getBlockMostElement().refresh(new Change(selected.getParent(), selected.getPosition(),"rename.variable", old, value));      
                                    // method definition
                                    else if(selected.getParent().getClassname().equals("MethodDefinition"))
                                        refresh(new Change(selected.getParent(), selected.getPosition(),"rename.method", old, value));      
                                }
                            }
                        }
                        selected=null;
                        repaint();
                        somethingChanged();
                    }
                    else
                    {
                        //System.out.println("No double click on value, selecting parent");
                        if(selected.getParent()!=null )
                            selected=selected.getParent();
                    }
                    
                }
                
                if(selected!=null && 
                        selected.getParent()!=null && 
                        selected.getParent().getParent()!=null &&
                        selected.getParent().getParent().getType()==Type.PARAMETERS)
                {
                    // copy reference
                    Element oldSelected = selected;
                    // clone
                    selected = selected.clone();
                    // disconnect from parent immediately
                    selected.setParent(null);
                    // dock back old reference
                    putBack(oldSelected);
                }
                
                if(selected==null)
                {
                    // filter out these cases
                }
                else if(selected instanceof List)
                {
                    // toggle the list
                    ((List) selected).toggle();
                    repaint();
                    selected=null;
                }
                else if(selected instanceof Item)
                {
                    pushUndo();
                    
                    // select item
                    String old = selected.getParent().getTitle();
                    selected.getParent().setTitle(selected.getTitle());
                    
                    // notify topmost element
                    /*
                    System.out.println("S: "+selected);
                    System.out.println("SP: "+selected.getParent());
                    System.out.println("SPP: "+selected.getParent().getParent());
                    System.out.println("SBM: "+selected.getBlockMostElement());
                    
                    /**/
                    if(selected.getBlockMostElement()!=null)
                    {
                        if(
                                selected.getBlockMostElement().getClassname().equals("AttributeDefinition") ||
                                selected.getBlockMostElement().getClassname().equals("MethodDefinition")
                          )
                        {
                            //System.out.println("Yes");
                            refresh(
                                    new Change(selected.getParent().getParent(), 
                                            selected.getParent().getPosition(),"list", old, 
                                            selected.getTitle()));
                        }
                        else // variable & Co
                            selected.getBlockMostElement().refresh(
                                    new Change(selected.getParent().getParent(), 
                                            selected.getParent().getPosition(),"list", old, 
                                            selected.getTitle()));
                    }
                    
                    // close the list
                    ((List) selected.getParent()).toggle();
                    // repaint
                    repaint();
                    somethingChanged();
                    selected=null;
                }
                else
                {
                    pushUndo();
                    
                    // clone if CTRL is pressed
                    //System.out.println("pre: "+selected);
                    if ((me.getModifiers() & ActionEvent.CTRL_MASK) ==ActionEvent.CTRL_MASK) {
                        // put back actual selected element
                        putBack(selected);   
                        // clone the selected element
                        selected=selected.clone();
                        // kill eventually next elements
                        selected.setNext(null);
                    }
                    //System.out.println("post: "+selected);

                    // calculate &  save the delta-click position
                    selectedDelta=new Dimension(me.getX()-selected.getOffset().x, 
                                                me.getY()-selected.getOffset().y);
                    // add the element to the list of editor elements
                    // and make shure it is last to be drawn!!
                    if(elements.contains(selected))
                        elements.remove(selected);
                    if(selected.getType()!=Type.PARAMETERS &&
                       selected.getType()!=Type.LIST &&
                       selected.getType()!=Type.ITEM)
                    {
                        addElement(selected);
                        refresh(new Change(selected, -1, "undock", null, selected));
                    }
                    // break the loop
                    //break;
                    return true;
                }
            }
            return false;
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        // convert point in case we got triggered by a libraryPanel
        Point clickPoint = me.getPoint();
        if(me.getSource() instanceof LibraryPanel)
        {
            clickPoint = new java.awt.Point(me.getLocationOnScreen());
            SwingUtilities.convertPointFromScreen(clickPoint, this);
        }
        
        // only act if we had an element 
        if(selected!=null)
        {
            // repostion element
            selected.setOffset(new Point(clickPoint.x-selectedDelta.width,
                                         clickPoint.y-selectedDelta.height));
            if(selected.isElementary())
            {
                selected.setOffset(new Point((clickPoint.x-selectedDelta.width) -((clickPoint.x-selectedDelta.width) % 10),
                                             (clickPoint.y-selectedDelta.height)-((clickPoint.y-selectedDelta.height)% 10)));
            }
            
            if(selected.getType()!=Type.PARAMETERS &&
                       selected.getType()!=Type.LIST &&
                       selected.getType()!=Type.ITEM)
            {
                putBack(selected); 
            }
            
            // something changed
            somethingChanged();
        }   
        selected=null;
        
        repaint();
    }
    
    private void putBack(Element selected)
    {
        for (int i = 0; i < elements.size(); i++) {
            Element get = elements.get(i);
            // try to dock
            Element dock = get.canDock(selected);
            //System.out.println("Dock: "+dock);
            if(dock!=null)
            {
                dock.dock(selected);
                elements.remove(selected);
                get.cleanDockStatus();
                break;
            }
            else
            {
                // if element is outside
                if(!(new Rectangle(this.getSize())).contains(selected.getOffset()))
                {
                    // delete it!
                    elements.remove(selected);
                    
                    refresh(new Change(selected, -1,"delete."+selected.getClassname(), null, null));      
                }
            }
            get.cleanDockStatus();
        }
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        //
    }

    @Override
    public void mouseExited(MouseEvent me) {
        //
    }

    public Element check() {
        Element result = null;
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            //System.out.println("Element: "+element.getClass().getSimpleName());
            element.cleanErrors();
            Element elementCheck = element.check();
            if(elementCheck!=null) result=elementCheck;
            //result = element.check() && result;
        }
        repaint();
        return result;
    }

    public String getJavaCode(int indent) {
        String result = "";
       
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            if(element.isElementary())
            {
                //result+=element.getIndent(indent)+"/*\n"+
                //        element.getIndent(indent)+" * Code for "+element.getTitle()+"\n"+
                //        element.getIndent(indent)+" */\n";
                result+=element.getIndent(indent)+element.getJavaCode(indent)+"\n";
            }
        }
        
        return result;
    }
    
    public String getXml() {
        String code = "";

        code+= "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        code+= "<bloxs xmlns:nsd=\"http://moenagade.fisch.lu/\" version=\"alpha\">\n";
                
        for (int i = 0; i < elements.size(); i++) {
            code+=elements.get(i).getXml(1);
        }
        
        code+= "</bloxs>\n";
        
        return code;
    }
    
    
    public void saveAsBloxs(String filename) throws FileNotFoundException, UnsupportedEncodingException, IOException
    {
        FileOutputStream fos = new FileOutputStream(filename);
        Writer out = new OutputStreamWriter(fos, "UTF-8");
        out.write(getXml());
        out.close();       
    }
    
    
    public void loadFromBloxs(String filename) throws SAXException, IOException
    {
        Parser parser = new Parser();
        elements=parser.parse(filename);
        for (int i = 0; i < elements.size(); i++) {
            Element get = elements.get(i);
            get.setEditor(this);
        }
        repaint();
    }


    public MainFrame getMainFrame() {
        return mainFrame;
    }

    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        //somethingChanged();
    }

    private void addElement(Element e)
    {
        elements.add(e);
        e.setEditor(this);
    }

    public ArrayList<VariableDefinition> getAttributes() 
    {
        ArrayList<VariableDefinition> attributes = new ArrayList<>();
        
        for (int i = 0; i < elements.size(); i++) 
        {
            Element e = elements.get(i);
            //System.out.println("Checking: "+e.getClassname());
            if(e.getClassname().equals("AttributeDefinition"))
            {
                attributes.add(e.getVariableDefinition());
            }
        }
        return attributes;
    }

    public ArrayList<String> getAttributeNames() 
    {
        ArrayList<String> attributes = new ArrayList<>();
        
        for (int i = 0; i < elements.size(); i++) 
        {
            Element e = elements.get(i);
            //System.out.println("I: "+this+" Checking: "+e.getClassname());
            if(e.getClassname().equals("AttributeDefinition"))
            {
                attributes.add(e.getVariableDefinition().name);
            }
        }
        return attributes;
    }
    
    public ArrayList<VariableDefinition> getEntities() 
    {
        ArrayList<VariableDefinition> attributes = new ArrayList<>();
        
        for (int i = 0; i < elements.size(); i++) 
        {
            Element e = elements.get(i);
            //System.out.println("Checking: "+e.getClassname()+" with return type: "+e.getReturnType());
            if(e.getClassname().equals("AttributeDefinition") &&  
                    (
                        Library.getInstance().getProject().getEntityNames().contains(e.getReturnType())
                        ||
                        Library.getInstance().getProject().getWorldNames().contains(e.getReturnType())
                    ))
            {
                attributes.add(e.getVariableDefinition());
            }
        }
        return attributes;
    }
    
    public Element getMethod(String name) 
    {
        for (int i = 0; i < elements.size(); i++) 
        {
            Element e = elements.get(i);
            //System.out.println("Checking: "+e.getClassname()+" with return type: "+e.getReturnType());
            if(e.getClassname().equals("MethodDefinition") && e.getParameter(0).getTitle().equals(name)) // &&  Library.getInstance().getProject().getEntityNames().contains(e.getReturnType()))
            {
                return e;
            }
        }
        return null;
    }
    
    public ArrayList<VariableDefinition> getMethods() 
    {
        ArrayList<VariableDefinition> attributes = new ArrayList<>();
        
        for (int i = 0; i < elements.size(); i++) 
        {
            Element e = elements.get(i);
            //System.out.println("Checking: "+e.getClassname()+" with return type: "+e.getReturnType());
            if(e.getClassname().equals("MethodDefinition")) // &&  Library.getInstance().getProject().getEntityNames().contains(e.getReturnType()))
            {
                attributes.add(e.getMethodDefinition());
            }
        }
        return attributes;
    }

    public ArrayList<String> getMethodNames() 
    {
        ArrayList<String> attributes = new ArrayList<>();
        
        for (int i = 0; i < elements.size(); i++) 
        {
            Element e = elements.get(i);
            //System.out.println("Checking: "+e.getClassname()+" with return type: "+e.getReturnType());
            if(e.getClassname().equals("MethodDefinition")) // &&  Library.getInstance().getProject().getEntityNames().contains(e.getReturnType()))
            {
                attributes.add(e.getMethodDefinition().name);
            }
        }
        return attributes;
    }

    public ArrayList<String> getEntityNames() 
    {
        ArrayList<String> attributes = new ArrayList<>();
        
        for (int i = 0; i < elements.size(); i++) 
        {
            Element e = elements.get(i);
            System.out.println("Checking: "+e.getClassname()+" with return type: "+e.getReturnType());
            if(e.getClassname().equals("AttributeDefinition") && Library.getInstance().getProject().getEntityNames().contains(e.getReturnType()))
            {
                attributes.add(e.getVariableDefinition().name);
            }
        }
        System.out.println("attributes: "+attributes);
        return attributes;
    }

    public boolean hasAttributeWithName(String value)
    {
        ArrayList<VariableDefinition> vd = getAttributes();
        //System.out.println(vd.size());
        for (int j = 0; j < vd.size(); j++) {
            VariableDefinition get = vd.get(j);
            //System.out.println(get.name);
            if(get.name.equals(value))
                return true;
        }
        return false;
    }
    
    public void refresh(Change change) {
        Project p = Library.getInstance().getProject();
        if(p!=null)
        {
            p.refresh(change);
        }
        else
            refreshElements(change);
    }
    
    public void refreshElements(Change change) {
        for (int i = 0; i < elements.size(); i++) 
        {
            elements.get(i).refresh(change);
        }
    }
    
    public BloxsClass getBloxsClass() {
        return bloxsClass;
    }

    public ArrayList<Element> getDrawLast() {
        return drawLast;
    }

    public Element getList() {
        return list;
    }

    public void setList(Element list) {
        this.list = list;
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
