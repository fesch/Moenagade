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

package lu.fisch.moenagade.bloxs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import lu.fisch.graphics.ColorUtils;
import static lu.fisch.moenagade.bloxs.Element.fontsize;
import lu.fisch.moenagade.bloxs.interfaces.Refresher;
import lu.fisch.moenagade.gui.Change;
import lu.fisch.moenagade.model.BloxsDefinition;
import lu.fisch.moenagade.model.Entity;
import lu.fisch.moenagade.model.Library;
import lu.fisch.moenagade.model.Project;

/**
 *
 * @author robert.fisch
 */
public class List extends Element {

    protected boolean open = false;
    
    public List() {
        init();
    }
    
    
    public List(String returnType) {
        init();
        setReturnType(returnType);
    }

    public List(BloxsDefinition bd) {
        super(bd);
        init();
    }
    
    public List(String title, String[] items) {
        super(title);
        init();

        for (int i = 0; i < items.length; i++) {
            String text = items[i];
            Item item = new Item(text);
            item.setParent(this);
            addToBody(item);
        }
    }

    private void init()
    {
        color = ColorUtils.getColor("#D77040");
        isHolder=true;
        setType(Type.LIST);
        setClassname("List");
    }

    @Override
    public Rectangle draw(Graphics2D g, Point offset)
    {
        drawLast.clear();
        if(isOpen())
        {
            drawLast.add(this);
        }
        //System.out.println("List: "+drawLast.size());
        
        //System.out.println("DRawing LIST");
        Rectangle result = new Rectangle();
        this.offset=offset;
        
        // set font
        g.setFont(new Font("Monospaced", Font.BOLD, fontsize));
        headDim = getHeadDimension(g);
        //System.out.println("Width of "+title+" = "+headDim.width);
        result = new Rectangle(offset, new Dimension(headDim.width, getTotalHeight(g)));
        
        // draw box
        g.setColor(color);
        g.fillRoundRect(offset.x, offset.y+2, 
                        headDim.width, headDim.height, 
                        0,0);
        if(hasError)
            g.setColor(Element.ERROR_COLOR);
        else if(dockBody)
            g.setColor(ColorUtils.getHighlight(color));
        else
            g.setColor(ColorUtils.getShadow(color));
        g.drawRoundRect(offset.x, offset.y+2, 
                        headDim.width, headDim.height, 
                        0,0);
        
        if(getBody()!=null && getBody() instanceof List)
        {
            result=getBody().draw(g,offset).union(result);
        }
        else  
        {
            // draw text
            //if(body!=null)
            {
                g.setFont(new Font("Monospaced", Font.BOLD, fontsize));
                g.setColor(Color.BLACK);
                int sw = g.getFontMetrics().stringWidth(title);
                int sh = g.getFontMetrics().getAscent();
                g.drawString(title, offset.x+PADDING_LR, offset.y+(headDim.height+sh)/2);
            }

            if(open)
            {
                // draw arrow up
                g.setColor(Color.BLACK);
                Polygon poly = new Polygon();
                int pad = 8;
                poly.addPoint(offset.x+headDim.width-pad+1,              3+offset.y+headDim.height-pad);
                poly.addPoint(offset.x+headDim.width-headDim.height/2,   3+offset.y+pad);
                poly.addPoint(offset.x+headDim.width-headDim.height+pad, 3+offset.y+headDim.height-pad);
                g.fillPolygon(poly);
            }
            else
            {
                // draw arrow down
                g.setColor(Color.BLACK);
                Polygon poly = new Polygon();
                int pad = 8;
                poly.addPoint(offset.x+headDim.width-pad+1,              3+offset.y+pad);
                poly.addPoint(offset.x+headDim.width-headDim.height/2,   3+offset.y+headDim.height-pad);
                poly.addPoint(offset.x+headDim.width-headDim.height+pad, 3+offset.y+pad);
                g.fillPolygon(poly);
            }

            if(open)
            {
                refresh(null);
                //System.out.println("I am: "+this.getClass().getSimpleName());
                
                // determine biggest item
                Element item = getBody();
                int width = 0;
                while(item!=null)
                {
                    width=Math.max(width, item.getHeadDimension(g).width);
                    item=item.getNext();
                }
                
                int y = offset.y+headDim.height;
                item = getBody();
                while(item!=null)
                {
                    //System.out.println("Now drawing ... "+item.getClass().getSimpleName()+" > "+item);
                
                    result=(((Item)item).draw(g, new Point(offset.x,y), Math.max(headDim.width,width),ColorUtils.getHighlight(color))).union(result);
                    y+=item.getHeadDimension(g).height;
                    item=item.getNext();
                }
            }
        }
        return result;
    }    

    public boolean isOpen() {
        return open;
    }

    /*public void setOpen(boolean open) {
        this.open = open;
    }*/
    
    ArrayList<Refresher> refreshers = new ArrayList<Refresher>();
    
    public void addRefresher(Refresher refresher)
    {
        //System.out.println("Adding refresher "+refresher.getClass().getSimpleName()+" to "+this.getClass().getSimpleName());
        
        if(!refreshers.contains(refresher))
            refreshers.add(refresher);
        /*
        if(body!=null && body instanceof List)
            ((List)body).addRefresher(refresher);
        if(parent!=null && parent instanceof List)
            ((List)parent).addRefresher(refresher);
        */
    }
    
    public void removeRefresher(Refresher refresher)
    {
        refreshers.remove(refresher);
    }
    
    @Override
    public void refresh(Change change)
    {
        //System.out.println("Refresh "+this.getClass().getSimpleName()+" Listeners: "+refreshers.size());
        for (int i = 0; i < refreshers.size(); i++) 
            refreshers.get(i).refresh(change);

        // also call parent
        if(getParent() instanceof List)
            ((List)getParent()).refresh(change);
    }
    
    public void toggle() {
        open=!open;
        
        /*
        System.out.println("Toggle IN for: "+getClassname());
        System.out.println("Parent       : "+getParent());
        /**/
        //System.out.println("Toggle IN for: "+getClassname()+" with return type: "+getReturnType());
        
        /*if(open && getParent()!=null && (
                getParent().getClassname().equals("VariableDefinition") ||
                getParent().getClassname().equals("AttributeDefinition")
                ))*/
        if(open && getReturnType()!=null && getReturnType().equals("Type"))
        {
            ArrayList<String> entries = new ArrayList<>();
            entries.add("int");
            entries.add("double");
            entries.add("boolean");
            entries.add("String");
            entries.add("long");
            entries.add("float");
            entries.addAll(Library.getInstance().getProject().getEntityNames());
            update(entries);
        }
        if(open && getReturnType()!=null && getReturnType().equals("Types"))
        {
            ArrayList<String> entries = new ArrayList<>();
            entries.add("void");
            entries.add("int");
            entries.add("double");
            entries.add("boolean");
            entries.add("String");
            entries.add("long");
            entries.add("float");
            entries.addAll(Library.getInstance().getProject().getEntityNames());
            update(entries);
        }
        else if(open && getReturnType()!=null && getReturnType().equals("Variable"))
                /*(
                getParent().getClassname().equals("Variable") ||
                getParent().getClassname().equals("SetVariable") ||
                getParent().getClassname().equals("VariableIncrement") ||
                getParent().getClassname().equals("VariableDecrement")
                ))*/
        {
            update(getVariables());
        }
        else if(open && getReturnType()!=null && getReturnType().equals("Image"))
                //getParent().getClassname().equals("LoadImage"))
        {
            Project project = Library.getInstance().getProject();
            if(project!=null)
            {
                update(project.getImageNames());
            }
        }
        else if(open && getReturnType()!=null && getReturnType().equals("Sound"))
            //getParent().getClassname().equals("PlaySound"))
        {
            Project project = Library.getInstance().getProject();
            if(project!=null)
            {
                update(project.getSoundNames());
            }
        }
        else if(open && getReturnType()!=null && getReturnType().equals("World"))
            // getParent().getClassname().equals("SetWorld"))
        {
            Project project = Library.getInstance().getProject();
            if(project!=null)
            {
                update(project.getWorldNames());
            }
        }
        else if(open && getReturnType()!=null && getReturnType().equals("Entity"))
            /*(getParent().getClassname().equals("AddEntity") ||
                                              getParent().getClassname().equals("OnTouchedEntity") ||
                                              getParent().getClassname().equals("CountEntitiesClass") ||
                                              getParent().getClassname().equals("TouchEntity")))*/
        {
            Project project = Library.getInstance().getProject();
            if(project!=null)
            {
                update(project.getEntityNames());
            }
        }
        else if(open && getReturnType()!=null && getReturnType().equals("Attribute"))
                /*(
                getParent().getClassname().equals("Attribute") ||
                getParent().getClassname().equals("SetAttribute")||
                getParent().getClassname().equals("AttributeIncrement")||
                getParent().getClassname().equals("AttributeDecrement")
                ))*/
        {
            update(getAttributes());
        }
        else if(open && getReturnType()!=null && getReturnType().equals("EntityList"))
        {
            update(getEntities());
        }
        else if(open && getReturnType()!=null && getReturnType().equals("MethodList"))
        {
            update(getMethods());
        }
        else if(open && getReturnType()!=null && getReturnType().equals("ObjectMethods"))
        {
            ArrayList<VariableDefinition> entities = getEntities();
            for (int i = 0; i < entities.size(); i++) {
                VariableDefinition vd = entities.get(i);
                if(vd.name.equals(getParent().parameters.get(0).getTitle()))
                {
                    // get reference to the loaded project
                    Project project = Library.getInstance().getProject();
                    // stop if null or not set
                    if(project==null) return;
                    // get the selected entity
                    //if(vd.classname==null) return;
                    Entity entity = project.getEntity(vd.classname);
                    // stop if not found
                    if(entity==null) return;  
                    // stop if class has no editor
                    if(entity.getEditor()==null) return;
                    // retrieve list of variables
                    ArrayList<VariableDefinition> methodNames = entity.getEditor().getMethods();
                    update(methodNames);
                }
            }
        }
        
        //System.out.println("Toggle OUT for: "+getClassname()+" with return type: "+getReturnType());
        
        //System.out.println("Toggle: "+open+" who: "+this.getClass().getSimpleName()+" body: "+getBody().getClass().getSimpleName());
    }
    
    public void update(ArrayList items)
    {
        // clean body
        setBody(null);

        // fill it up
        for (int i = 0; i < items.size(); i++) {
            String text = items.get(i).toString();
            //System.out.println(text);
            Item item = new Item(text);
            item.setParent(this);
            item.setReturnType(getReturnType());
            addToBody(item);
        } 
    }

    @Override
    public String getJavaCode(int indent) {
        return title;
    }

    @Override
    public Element check() {
        hasError = getTitle().isEmpty();
        if (getTitle().isEmpty())
            return this;
        return null;
    }
    
    
}
