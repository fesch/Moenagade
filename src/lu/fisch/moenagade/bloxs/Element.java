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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import lu.fisch.graphics.ColorUtils;
import static lu.fisch.moenagade.bloxs.Element.Type.INSTRUCTION;
import lu.fisch.moenagade.model.BloxsEditor;
import lu.fisch.moenagade.gui.Change;
import lu.fisch.moenagade.model.BloxsDefinition;
import lu.fisch.moenagade.model.Entity;
import lu.fisch.moenagade.model.Library;
import lu.fisch.moenagade.model.Project;
import sun.swing.SwingUtilities2;


/**
 *
 * @author robert.fisch
 */
public class Element {
    
    final public static int PADDING_LR = 6;
    final public static int PADDING_BT = 5;
    final public static int BAR_SIZE = 16;
    final public static int CONNECTOR_WIDTH = 16;
    final public static int CONNECTOR_HEIGHT = 4;
    final public static int CONNECTOR_OFFSET = 16;
    final public static int EMPTY_HEIGHT = 32;
    
    final public static int PARAMTER_HEIGHT = 20;
    final public static int PARAMTER_WIDTH = 50;
    
    final public static Color TEXT_COLOR = Color.WHITE;
    final public static Color DOCK_COLOR = Color.WHITE;
    final public static Color ERROR_COLOR = Color.RED;
    
    final public static int MIN_ELEMENT_WIDTH = 2*CONNECTOR_OFFSET+CONNECTOR_WIDTH+BAR_SIZE;
    final public static int MIN_PARAMETER_WIDTH = 2*PARAMTER_HEIGHT;

    // define two positions (use to test if an element is allowed to dock somewhere)
    public enum Pos {BODY, BOTTOM}
    
    // define tree important types
    public enum Type {INSTRUCTION, EXPRESSION, CONDITION, VALUE, LIST, ITEM, PARAMETERS}
    
    public static int fontsize = 12;
    
    public Point offset = new Point(0,0);
    public Dimension headDim = new Dimension();
    protected Dimension bodyDim = new Dimension();
    
    private Element parent = null;
    protected Element top = null;
    protected Element bottom = null;
    protected ArrayList<Element> parameters = new ArrayList<>();
    private Element body = null;
    private Element next = null;
    private Element prev = null;
    private String returnType = null;
    private String classname;
    private ArrayList<String> allowDockAfter = new ArrayList<>();
    private ArrayList<String> allowDockInside = new ArrayList<>();
    private ArrayList<String> paramTypes = new ArrayList<>();
    private Type type = INSTRUCTION;
    private String code = "";
    private String destinations = "";
    private String transformation = "";
    private String needsParent = "";
    private HashMap<Integer,String> transformations = new HashMap<>();
    private int position = -1;
    
    private BloxsEditor editor = null;
    protected ArrayList<Element> drawLast = new ArrayList<>();
    
    //protected ArrayList<Element> body = new ArrayList<>();
    
    protected boolean hasTop    = true;
    protected boolean hasBottom = true;
    protected boolean hasBody   = false;
    //protected boolean isParameter  = false;
    protected boolean isHolder = false;
    protected boolean hasError = false;
    
    protected boolean dockBody   = false;
    protected boolean dockBottom = false;   
    
    protected String title;
    protected String label;
    protected ArrayList<String> titlePieces = new ArrayList<>();
    
    protected Color color = ColorUtils.getColor("C88C37");
    
    public Element()
    {
       title="";
    }
    
    public Element(String title)
    {
        setTitle(title);
    }
    
    public Element(Type type, String classname, String returnType)
    {
        setType(type);
        setClassname(classname);
        setReturnType(returnType);
        
        if(type==Type.EXPRESSION)
            color=ColorUtils.getColor("#0480C8");
        else if(type==Type.CONDITION)
            color=ColorUtils.getColor("#748C51");
    }
    
    public Element(BloxsDefinition bd)
    {
        this();
        this.classname = bd.getClassname();
        this.color = bd.getColor();
        this.type = bd.getType();
        this.returnType = bd.getReturnType();
        this.hasTop = bd.hasTop();
        this.hasBody = bd.hasBody();
        this.hasBottom = bd.hasBottom();
        this.code = bd.getCode();
        this.allowDockAfter = bd.getAllowDockAfter();
        this.allowDockInside = bd.getAllowDockInside();
        this.transformations=bd.getTransformations();
        this.paramTypes=bd.getParamTypes();
        this.destinations=bd.getDestinations();
        this.transformation=bd.getTransformation();
        this.needsParent=bd.getNeedsParent();
        setTitle(bd.getTitle());
       
        for (Map.Entry<Integer, List> entry : bd.getLists().entrySet()) {
            Integer key = entry.getKey();
            List value = entry.getValue();
            setParameter(key, value.clone());
        }

        // add sub elements
        for(int i=0; i<parameterCount(); i++)
        {
            for (int j = 0; j < bd.getSubs(i).size(); j++) {
                BloxsDefinition sbd = bd.getSubs(i).get(j);
                Element expressionHolder = new Element(Type.EXPRESSION, "ExpressionHolder", "");
                expressionHolder.addToBody(new Element(sbd));
                getParameter(i).addParameter(expressionHolder);
            }
        }
        
        //System.out.println(getClassname()+" I: "+allowDockInside+" A: "+allowDockAfter);
    }
    
    @Override
    public Element clone()
    {
        //System.out.println("Cloning: "+this.getClass().getSimpleName());
        Element element;
        
        if(getType()==Type.VALUE) element = new Value();
        else if(getType()==Type.LIST) element = new List();
        else if(getType()==Type.ITEM) element = new Item();
        else if(getType()==Type.PARAMETERS) element = new Parameters();
        else element = new Element();
        
        //System.out.println("Type: "+getType()+" Element: "+element.getClass().getSimpleName());
        
        element.classname=this.classname;
        element.setTitle(this.title);
        element.color=this.color;
        element.type=this.type;
        element.returnType=this.returnType;
        element.hasTop=this.hasTop;
        element.hasBody=this.hasBody;
        element.hasBottom=this.hasBottom;
        element.setParent(this.getParent());
        element.code=this.code;
        element.destinations=this.destinations;
        element.transformation=this.transformation;
        element.needsParent=this.needsParent;
        if(this.allowDockAfter!=null)
            element.allowDockAfter=(ArrayList<String>) this.allowDockAfter.clone();
        else element.allowDockAfter=null;
        if(this.allowDockInside!=null)
            element.allowDockInside=(ArrayList<String>) this.allowDockInside.clone();
        else element.allowDockInside=null;
        if(this.paramTypes!=null)
            element.paramTypes=(ArrayList<String>) this.paramTypes.clone();
        else element.paramTypes=null;
        
        element.offset=(Point) this.offset.clone();
        
        
        element.parameters.clear();
        //System.out.println(">>>>>");
        for (int i = 0; i < parameters.size(); i++) {
            Element param = parameters.get(i);
            //System.out.println(i+" Class: "+param.getClassname()+" / "+param.getClass().getSimpleName());
            element.addParameter(param.clone());
        }
        
        for (Map.Entry<Integer, String> entry : transformations.entrySet()) 
        {
            int key = entry.getKey();
            String value = entry.getValue();
            element.transformations.put(key, value);
        }
        
        if(this.getBody()!=null)
        {
            element.setBody(this.body.clone());
            getBody().setParent(this);
        }
        
        if(this.getNext()!=null)
        {
            element.setNext(this.next.clone());
            if(element.getNext()!=null)
                element.getNext().setParent(getParent());
            //System.out.println("Setting parent for "+this+" to "+getParent());
        }
        
        return element;
    }
    
    private static int countOccurrences(String haystack, char needle)
    {
        int count = 0;
        for (int i=0; i < haystack.length(); i++)
        {
            if (haystack.charAt(i) == needle)
            {
                 count++;
            }
        }
        return count;
    }

    /*
    public Element(String title, int paramCount)
    {
        //System.out.println("Called: "+title);
        parseTitle(title,paramCount);
    }
    */

    public void parseTitle(String title) {
        parseTitle(title,true);
    }
    
    public void parseTitle(String title, boolean setParameters) {
        this.title = title;
        
        if(title==null) return;
        
        titlePieces = new ArrayList<>();
        if(setParameters)
            parameters = new ArrayList<>();
        
        if(title.trim().isEmpty())
        {
            titlePieces.add("");
        }
        else
        {
            int paramCount = 0;
            // expressions
            paramCount+=countOccurrences(title, '$');
            // conditions
            paramCount+=countOccurrences(title, '£');
            // values
            paramCount+=countOccurrences(title, '§');
            // lists
            paramCount+=countOccurrences(title, '€');
            // parameters
            paramCount+=countOccurrences(title, '^');
            paramCount+=countOccurrences(title, '`');
            
            String buffer = "";
            for(int i=0; i<title.length(); i++)
            {
                char sym = title.charAt(i);
                if(sym=='$' || sym=='£' || sym=='§' || sym=='€' || sym=='^' || sym=='`' )
                {
                    if(buffer.isEmpty()) buffer=" ";
                    titlePieces.add(buffer);
                    buffer="";
                    
                    // only set parameters if requested
                    if(setParameters)
                    {
                        String returnType = "";
                        if(parameters.size()<paramTypes.size() && paramTypes.size()>0) 
                            returnType=paramTypes.get(parameters.size());
                        //System.out.println("Parameter: "+(parameters.size())+" is a: "+returnType);

                        if(sym=='$') addParameter(new Element(Type.EXPRESSION,"ExpressionHolder",returnType));
                        else if(sym=='£') addParameter(new Element(Type.CONDITION,"ConditionHolder","Boolean"));
                        else if(sym=='§') addParameter(new Value());
                        else if(sym=='^') addParameter(new Parameters());
                        else if(sym=='€') addParameter(new List(returnType));
                        else if(sym=='`') 
                        {
                            Parameters parameters = new Parameters();
                            parameters.setAllowConfig(false);
                            addParameter(parameters);
                        }
                    }
                }
                else
                {
                    buffer=buffer+sym;
                }
            }
            if(buffer.isEmpty()) buffer=" ";
            titlePieces.add(buffer);
            
            /*
            // split it up & generate parameters
            titlePieces = new ArrayList<>();
            parameters = new ArrayList<>();
            title=" "+title+" ";
            String[] pieces = title.split("\\$");
            for (int i = 0; i < pieces.length; i++) {
                titlePieces.add(pieces[i].trim());
                if(parameters.size()<paramCount)
                    parameters.add(new Empty());
            }
            //System.out.println("Found "+pieces.length+" for element: "+title);
            */
        }
    }
    
    public Element setParameter(int index, Element parameter)
    {
        parameters.set(index, parameter);
        parameter.isHolder=true;
        parameter.setPosition(index);
        parameter.setParent(this);
        parameter.setPrev(this);
        return this;
    }
    
    public Element addParameter(Element parameterHolder)
    {
        parameters.add(parameterHolder);
        parameterHolder.isHolder=true;
        parameterHolder.setPosition(parameters.size()-1);
        parameterHolder.setParent(this);
        parameterHolder.setPrev(this);
        return this;
    }
    
    public int parameterCount()
    {
        return parameters.size();
    }
    
    public Element getParameter(int index)
    {
        return parameters.get(index);
    }
    
    public Element addToBody(Element element)
    {
        //body.add(element);
        
        // add as first body element
        if(getBody()==null) 
        {
            setBody(element);
            //if(element!=null)
            //    element.setPrev(getBody());
            /*if(element!=null) ---> done by setBody()
            {
                element.setParent(this);
                element.setPrev(this);
            }*/
        }
        else 
        {
            // add at the end of the list
            Element tmp = getBody();
            while(tmp.getNext()!=null) tmp=tmp.getNext();
            tmp.setNext(element);
            //if(element!=null)
            //    element.setPrev(tmp);
            
        }
        if(element!=null)
            element.setParent(this);
        return this;
    }
    
    public Element append(Element element)
    {
        element.parent=this.parent;
        if(getNext()==null) 
        {
            setNext(element);
        }
        else 
        {
            // add at the end of the list
            Element tmp = getNext();
            while(tmp.getNext()!=null) tmp=tmp.getNext();
            tmp.setNext(element);
            
        }
        return this;
    }
    
    public Rectangle draw(Graphics2D g)
    {
        return draw(g, offset);
    }
    
    public Rectangle draw(Graphics2D g, Point offset)
    {
        drawLast.clear();

        // update some elements
        /*
        if(getClassname().equals("Variable"))
        {
            String varname = parameters.get(0).getTitle();
            //System.out.println("Yes: "+varname);
            ArrayList<VariableDefinition> variable = getVariables();
            boolean found = false;
            for (int i = 0; i < variable.size(); i++) {
                VariableDefinition get = variable.get(i);
                if(get.name.equals(varname)) 
                    found = true;
            }
            if(!found) parameters.get(0).setTitle("");
        }
        */
        
        // update variables and attributes
        for (int i = 0; i < parameters.size(); i++) {
            Element parameter = parameters.get(i);
            if(parameter.getReturnType()!=null && parameter.getReturnType().equals("Variable"))
            {
                ArrayList<VariableDefinition> variable = getVariables();
                boolean found = false;
                for (int j = 0; j < variable.size(); j++) {
                    VariableDefinition get = variable.get(j);
                    if(get.name.equals(parameter.getTitle())) 
                        found = true;
                }
                if(!found) 
                {
                    parameter.setTitle("");
                    setReturnType("");
                }
            }
            else if(parameter.getReturnType()!=null && parameter.getReturnType().equals("Attributes"))
            {
                ArrayList<VariableDefinition> variable = getAttributes();
                boolean found = false;
                for (int j = 0; j < variable.size(); j++) {
                    VariableDefinition get = variable.get(j);
                    if(get.name.equals(parameter.getTitle())) 
                        found = true;
                }
                if(!found) 
                {
                    parameter.setTitle("");
                    setReturnType("");
                }
            }
            else if(parameter.getReturnType()!=null && parameter.getReturnType().equals("Sound"))
            {
                Project project = Library.getInstance().getProject();
                if(project!=null)
                {
                    ArrayList<String> variable = project.getSoundNames();
                    boolean found = false;
                    for (int j = 0; j < variable.size(); j++) {
                        String get = variable.get(j);
                        if(get.equals(parameter.getTitle())) 
                            found = true;
                    }
                    if(!found) parameter.setTitle("");
                }
            }
            else if(parameter.getReturnType()!=null && parameter.getReturnType().equals("Image"))
            {
                Project project = Library.getInstance().getProject();
                if(project!=null)
                {
                    ArrayList<String> variable = project.getImageNames();
                    boolean found = false;
                    for (int j = 0; j < variable.size(); j++) {
                        String get = variable.get(j);
                        if(get.equals(parameter.getTitle())) 
                            found = true;
                    }
                    if(!found) parameter.setTitle("");
                }
            }
            else if(parameter.getReturnType()!=null && parameter.getReturnType().equals("Entity"))
            {
                Project project = Library.getInstance().getProject();
                if(project!=null)
                {
                    ArrayList<String> variable = project.getEntityNames();
                    boolean found = false;
                    for (int j = 0; j < variable.size(); j++) {
                        String get = variable.get(j);
                        if(get.equals(parameter.getTitle())) 
                            found = true;
                    }
                    if(!found) parameter.setTitle("");
                }
            }
            else if(parameter.getReturnType()!=null && parameter.getReturnType().equals("World"))
            {
                Project project = Library.getInstance().getProject();
                if(project!=null)
                {
                    ArrayList<String> variable = project.getWorldNames();
                    boolean found = false;
                    for (int j = 0; j < variable.size(); j++) {
                        String get = variable.get(j);
                        if(get.equals(parameter.getTitle())) 
                            found = true;
                    }
                    if(!found) parameter.setTitle("");
                }
            }
        }
        
        
        
        Rectangle result = new Rectangle();
        this.offset=offset;
            
        // set font
        g.setFont(new Font("Monospaced", Font.BOLD, fontsize));
        
        if(getType()==INSTRUCTION)
        {
            headDim = getHeadDimension(g);
            bodyDim = getBodyDimension(g);
            
            result = new Rectangle(offset, new Dimension(Math.max(headDim.width,bodyDim.width), getTotalHeight(g)));

            // draw base shape
            g.setColor(color);
            if(hasTop) 
            {
                g.fillRect(offset.x, offset.y, 
                           CONNECTOR_OFFSET,CONNECTOR_HEIGHT);
                g.fillRect(offset.x+CONNECTOR_OFFSET+CONNECTOR_WIDTH, offset.y, 
                           headDim.width-CONNECTOR_OFFSET-CONNECTOR_WIDTH,CONNECTOR_HEIGHT);
            }
            else
            {
                g.fillRect(offset.x, offset.y, 
                           headDim.width,CONNECTOR_HEIGHT);
            }
            g.fillRect(offset.x,offset.y+CONNECTOR_HEIGHT,
                       headDim.width,headDim.height);

            if(hasBody)
            {
                // bar
                g.fillRect(offset.x, offset.y+CONNECTOR_HEIGHT+headDim.height, 
                           BAR_SIZE,bodyDim.height);
                // bottom
                g.fillRect(offset.x, offset.y+CONNECTOR_HEIGHT+headDim.height+bodyDim.height,
                           headDim.width,BAR_SIZE);
                // inner connector
                g.fillRect(offset.x+BAR_SIZE+CONNECTOR_OFFSET,offset.y+CONNECTOR_HEIGHT+headDim.height,
                           CONNECTOR_WIDTH,CONNECTOR_HEIGHT);

                if(hasBottom)
                {
                    // bottom connector
                    g.fillRect(offset.x+CONNECTOR_OFFSET,offset.y+CONNECTOR_HEIGHT+headDim.height+bodyDim.height+BAR_SIZE,
                               CONNECTOR_WIDTH,CONNECTOR_HEIGHT);
                }
            }
            else
            {
                if(hasBottom)
                {
                    // bottom connector
                    g.fillRect(offset.x+CONNECTOR_OFFSET,offset.y+CONNECTOR_HEIGHT+headDim.height,
                               CONNECTOR_WIDTH,CONNECTOR_HEIGHT);
                }
            }

            // draw highlight
            g.setColor(ColorUtils.getHighlight(color));
            if(hasTop) 
            {
                g.drawLine(offset.x,offset.y,
                           offset.x+CONNECTOR_OFFSET-1,offset.y);
                g.drawLine(offset.x+CONNECTOR_OFFSET-1,offset.y,
                           offset.x+CONNECTOR_OFFSET-1,offset.y+CONNECTOR_HEIGHT);
                g.drawLine(offset.x+CONNECTOR_OFFSET-1,offset.y+CONNECTOR_HEIGHT,
                           offset.x+CONNECTOR_OFFSET+CONNECTOR_WIDTH,offset.y+CONNECTOR_HEIGHT);
                g.drawLine(offset.x+CONNECTOR_OFFSET+CONNECTOR_WIDTH,offset.y+CONNECTOR_HEIGHT,
                           offset.x+CONNECTOR_OFFSET+CONNECTOR_WIDTH,offset.y);
                g.drawLine(offset.x+CONNECTOR_OFFSET+CONNECTOR_WIDTH,offset.y,
                           offset.x+headDim.width-1,offset.y);  
            }
            else
            {
                g.drawLine(offset.x,offset.y,
                           offset.x+headDim.width-1,offset.y);  
            }
            if(hasBody)
            {
                g.drawLine(offset.x,offset.y,
                           offset.x,offset.y+CONNECTOR_HEIGHT+headDim.height+bodyDim.height+BAR_SIZE-1);
            }
            else
            {
                g.drawLine(offset.x,offset.y,
                           offset.x,offset.y+CONNECTOR_HEIGHT+headDim.height-1);
            }

            // draw shadow
            g.setColor(ColorUtils.getShadow(color));
            // bottom
            if(hasBody)
            {
                if(hasBottom)
                {
                    g.drawLine(offset.x,offset.y+CONNECTOR_HEIGHT+headDim.height+bodyDim.height+BAR_SIZE-1, 
                               offset.x+CONNECTOR_OFFSET,offset.y+CONNECTOR_HEIGHT+headDim.height+bodyDim.height+BAR_SIZE-1);
                    g.drawLine(offset.x+CONNECTOR_OFFSET,offset.y+CONNECTOR_HEIGHT+headDim.height+bodyDim.height+BAR_SIZE+CONNECTOR_HEIGHT-1, 
                               offset.x+CONNECTOR_OFFSET+CONNECTOR_WIDTH-1,offset.y+CONNECTOR_HEIGHT+headDim.height+bodyDim.height+BAR_SIZE+CONNECTOR_HEIGHT-1);
                    g.drawLine(offset.x+CONNECTOR_OFFSET+CONNECTOR_WIDTH,offset.y+CONNECTOR_HEIGHT+headDim.height+bodyDim.height+BAR_SIZE-1, 
                               offset.x+headDim.width-1,offset.y+CONNECTOR_HEIGHT+headDim.height+bodyDim.height+BAR_SIZE-1);
                    g.drawLine(offset.x+CONNECTOR_OFFSET,offset.y+CONNECTOR_HEIGHT+headDim.height+bodyDim.height+BAR_SIZE-1,
                               offset.x+CONNECTOR_OFFSET,offset.y+CONNECTOR_HEIGHT+headDim.height+bodyDim.height+BAR_SIZE+CONNECTOR_HEIGHT-1);
                    g.drawLine(offset.x+CONNECTOR_OFFSET+CONNECTOR_WIDTH-1,offset.y+CONNECTOR_HEIGHT+headDim.height+bodyDim.height+BAR_SIZE-1,
                               offset.x+CONNECTOR_OFFSET+CONNECTOR_WIDTH-1,offset.y+CONNECTOR_HEIGHT+headDim.height+bodyDim.height+BAR_SIZE+CONNECTOR_HEIGHT-1);
                }
                else
                {
                    g.drawLine(offset.x,offset.y+CONNECTOR_HEIGHT+headDim.height+bodyDim.height+BAR_SIZE-1, 
                               offset.x+headDim.width,offset.y+CONNECTOR_HEIGHT+headDim.height+bodyDim.height+BAR_SIZE-1);
                }

                // dropping down
                g.drawLine(offset.x+headDim.width,offset.y,
                           offset.x+headDim.width,offset.y+CONNECTOR_HEIGHT+headDim.height-1);
                g.drawLine(offset.x+BAR_SIZE-1,offset.y+CONNECTOR_HEIGHT+headDim.height-1, 
                           offset.x+BAR_SIZE-1,offset.y+CONNECTOR_HEIGHT+headDim.height+bodyDim.height-1);
                g.drawLine(offset.x+BAR_SIZE,offset.y+CONNECTOR_HEIGHT+headDim.height+bodyDim.height-1, 
                           offset.x+BAR_SIZE+headDim.width-BAR_SIZE,offset.y+CONNECTOR_HEIGHT+headDim.height+bodyDim.height-1);
                g.drawLine(offset.x+BAR_SIZE+headDim.width-BAR_SIZE,offset.y+CONNECTOR_HEIGHT+headDim.height+bodyDim.height,
                           offset.x+BAR_SIZE+headDim.width-BAR_SIZE,offset.y+CONNECTOR_HEIGHT+headDim.height+bodyDim.height+BAR_SIZE-1);

                // innert
                g.drawLine(offset.x+BAR_SIZE,offset.y+CONNECTOR_HEIGHT+headDim.height-1, 
                        offset.x+BAR_SIZE+CONNECTOR_OFFSET,offset.y+CONNECTOR_HEIGHT+headDim.height-1);
                g.drawLine(offset.x+BAR_SIZE+CONNECTOR_OFFSET,offset.y+CONNECTOR_HEIGHT+headDim.height+CONNECTOR_HEIGHT-1, 
                        offset.x+BAR_SIZE+CONNECTOR_OFFSET+CONNECTOR_WIDTH-1,offset.y+CONNECTOR_HEIGHT+headDim.height+CONNECTOR_HEIGHT-1);
                g.drawLine(offset.x+BAR_SIZE+CONNECTOR_OFFSET+CONNECTOR_WIDTH,offset.y+CONNECTOR_HEIGHT+headDim.height-1, 
                        offset.x+headDim.width-1,offset.y+CONNECTOR_HEIGHT+headDim.height-1);
                g.drawLine(offset.x+BAR_SIZE+CONNECTOR_OFFSET,offset.y+CONNECTOR_HEIGHT+headDim.height-1,
                        offset.x+BAR_SIZE+CONNECTOR_OFFSET,offset.y+CONNECTOR_HEIGHT+headDim.height+CONNECTOR_HEIGHT-1);
                g.drawLine(offset.x+BAR_SIZE+CONNECTOR_OFFSET+CONNECTOR_WIDTH-1,offset.y+CONNECTOR_HEIGHT+headDim.height-1,
                        offset.x+BAR_SIZE+CONNECTOR_OFFSET+CONNECTOR_WIDTH-1,offset.y+CONNECTOR_HEIGHT+headDim.height+CONNECTOR_HEIGHT-1);
            }
            else
            {
                if(hasBottom)
                {
                    g.drawLine(offset.x,offset.y+CONNECTOR_HEIGHT+headDim.height-1, 
                               offset.x+CONNECTOR_OFFSET,offset.y+CONNECTOR_HEIGHT+headDim.height-1);
                    g.drawLine(offset.x+CONNECTOR_OFFSET,offset.y+CONNECTOR_HEIGHT+headDim.height+CONNECTOR_HEIGHT-1, 
                               offset.x+CONNECTOR_OFFSET+CONNECTOR_WIDTH-1,offset.y+CONNECTOR_HEIGHT+headDim.height+CONNECTOR_HEIGHT-1);
                    g.drawLine(offset.x+CONNECTOR_OFFSET+CONNECTOR_WIDTH,offset.y+CONNECTOR_HEIGHT+headDim.height-1, 
                               offset.x+headDim.width-1,offset.y+CONNECTOR_HEIGHT+headDim.height-1);
                    g.drawLine(offset.x+CONNECTOR_OFFSET,offset.y+CONNECTOR_HEIGHT+headDim.height-1,
                               offset.x+CONNECTOR_OFFSET,offset.y+CONNECTOR_HEIGHT+headDim.height+CONNECTOR_HEIGHT-1);
                    g.drawLine(offset.x+CONNECTOR_OFFSET+CONNECTOR_WIDTH-1,offset.y+CONNECTOR_HEIGHT+headDim.height-1,
                               offset.x+CONNECTOR_OFFSET+CONNECTOR_WIDTH-1,offset.y+CONNECTOR_HEIGHT+headDim.height+CONNECTOR_HEIGHT-1);
                    g.drawLine(offset.x+headDim.width,offset.y+CONNECTOR_HEIGHT+headDim.height-1,
                               offset.x+headDim.width,offset.y);
                }
                else
                {
                    g.drawLine(offset.x,offset.y+CONNECTOR_HEIGHT+headDim.height-1,
                               offset.x+headDim.width,offset.y+CONNECTOR_HEIGHT+headDim.height-1);
                    g.drawLine(offset.x+headDim.width,offset.y+CONNECTOR_HEIGHT+headDim.height-1,
                               offset.x+headDim.width,offset.y);
                }
            }

            // draw blocs inside body
            int y = offset.y+CONNECTOR_HEIGHT+headDim.height;
            if(getBody()!=null) result=getBody().draw(g, new Point(offset.x+BAR_SIZE,y)).union(result);
            if(hasBody)
            {
                if(getNext()!=null) result=getNext().draw(g, new Point(offset.x,offset.y+CONNECTOR_HEIGHT+headDim.height+bodyDim.height+BAR_SIZE)).union(result);
            }
            else
            {
                if(getNext()!=null) result=getNext().draw(g, new Point(offset.x,offset.y+CONNECTOR_HEIGHT+headDim.height)).union(result);
            }
            /*
            for (int i = 0; i < body.size(); i++) {
                Element element = body.get(i);
                element.draw(g, new Point(offset.x+BAR_SIZE,y));
                y+=element.getTotalHeight(g);
            }*/

            // docking things

            if(dockBody)
            {
                g.setColor(DOCK_COLOR);
                g.fillRect(offset.x+BAR_SIZE, offset.y+CONNECTOR_HEIGHT+headDim.height, headDim.width-BAR_SIZE, CONNECTOR_HEIGHT);
            }
            if(dockBottom)
            {
                if(hasBody)
                {
                    g.setColor(DOCK_COLOR);
                    g.fillRect(offset.x,offset.y+CONNECTOR_HEIGHT+headDim.height+bodyDim.height+BAR_SIZE,headDim.width,CONNECTOR_HEIGHT);
                }
                else
                {
                    g.setColor(DOCK_COLOR);
                    g.fillRect(offset.x,offset.y+CONNECTOR_HEIGHT+headDim.height,headDim.width,CONNECTOR_HEIGHT);
                }
            }

            // text
            int x = offset.x+PADDING_LR;
            if(titlePieces.size()>0)
            {
                for (int i = 0; i < titlePieces.size(); i++) {
                    String piece = titlePieces.get(i);
                    // draw piece
                    if(!piece.trim().isEmpty())
                    {
                        g.setColor(TEXT_COLOR);
                        g.setFont(new Font("Monospaced", Font.BOLD, fontsize));
                        int th = g.getFontMetrics().getAscent();
                        g.drawString(piece.trim(), x, offset.y+th+(headDim.height+CONNECTOR_HEIGHT-th)/2);
                        x+=g.getFontMetrics().stringWidth(piece.trim()+" ");
                    }
                    // draw parameter
                    if(i<parameters.size())
                    {
                        Element param = parameters.get(i);
                        //System.out.println("DRawing param: "+i+" )) "+param.getClass().getSimpleName()+" : "+param.getClassname()+" > "+param);
                        Dimension paramHead = param.getHeadDimension(g);
                        result=param.draw(g, new Point(x, offset.y+(headDim.height+CONNECTOR_HEIGHT-paramHead.height)/2)).union(result);
                        drawLast.addAll(param.getDrawLast());
                        g.setFont(new Font("Monospaced", Font.BOLD, fontsize));
                        x+=paramHead.width+g.getFontMetrics().stringWidth(" ");
                    }
                }
            }
            else
            {
                g.setColor(TEXT_COLOR);
                g.setFont(new Font("Monospaced", Font.BOLD, fontsize));
                int th = g.getFontMetrics().getAscent();
                g.drawString(title, x, offset.y+th+(headDim.height+CONNECTOR_HEIGHT-th)/2);
            }
            
        }
        else if(getType()==Type.EXPRESSION)
        {
            if(getBody()!=null)
                getBody().offset=offset;

            // set font
            g.setFont(new Font("Monospaced", Font.BOLD, fontsize));

            // draw corpus
            headDim = getHeadDimension(g);
            result = new Rectangle(offset, new Dimension(headDim.width, getTotalHeight(g)));

            if(isHolder)
                g.setColor(ColorUtils.getDarker(color));
            else
                g.setColor(color);
            g.fillRoundRect(offset.x, offset.y+2, 
                            headDim.width, headDim.height, 
                            8,8);
            if (dockBody)
                g.setColor(ColorUtils.getHighlight(color));
            else if(hasError)
                g.setColor(Element.ERROR_COLOR);
            else
                g.setColor(ColorUtils.getShadow(color));
            g.drawRoundRect(offset.x, offset.y+2, 
                            headDim.width, headDim.height, 
                            8,8);

            // text
            int x = offset.x+PADDING_LR;
            if(titlePieces.size()>0)
            {
                if(parameters.isEmpty())
                {
                    int th = g.getFontMetrics().getAscent();
                    int tw = g.getFontMetrics().stringWidth(title);
                    g.setColor(TEXT_COLOR);
                    g.setFont(new Font("Monospaced", Font.BOLD, fontsize));    
                    g.drawString(title, offset.x+(headDim.width-tw)/2, offset.y+th+(headDim.height-th)/2); 
                }
                else
                {
                    for (int i = 0; i < titlePieces.size(); i++) {
                        String piece = titlePieces.get(i);
                        // draw piece
                        if(!piece.trim().isEmpty())
                        {
                            int th = g.getFontMetrics().getAscent();
                            g.setColor(TEXT_COLOR);
                            g.setFont(new Font("Monospaced", Font.BOLD, fontsize));    
                            g.drawString(piece.trim(), x, offset.y+th+(headDim.height-th)/2);
                            x+=g.getFontMetrics().stringWidth(piece.trim()+" ");
                        }
                        // draw parameter
                        if(i<parameters.size())
                        {
                            Element param = parameters.get(i);
                            Dimension paramHead = param.getHeadDimension(g);
                            result=param.draw(g, new Point(x, offset.y+(headDim.height-paramHead.height)/2)).union(result);
                            drawLast.addAll(param.getDrawLast());
                            g.setFont(new Font("Monospaced", Font.BOLD, fontsize));
                            x+=paramHead.width+g.getFontMetrics().stringWidth(" ");
                        }
                    }
                }
            }
            else
            {
                g.setColor(TEXT_COLOR);
                g.setFont(new Font("Monospaced", Font.BOLD, fontsize));
                int th = g.getFontMetrics().getAscent();
                if(title!=null)
                    g.drawString(title, x, offset.y+th+(headDim.height-th)/2);
            }

            if(getBody()!=null)
            {
                result=getBody().draw(g,offset).union(result);
            }
        }
        else if(getType()==Type.CONDITION)
        {
            // set font
            g.setFont(new Font("Monospaced", Font.BOLD, fontsize));
            headDim=getHeadDimension(g);
            result = new Rectangle(offset, new Dimension(headDim.width, getTotalHeight(g)));

            if(getBody()==null)
            {
                offset.y+=2;
                Polygon poly = new Polygon();
                poly.addPoint(offset.x, offset.y+headDim.height/2);
                poly.addPoint(offset.x+headDim.height/2, offset.y);
                poly.addPoint(offset.x+headDim.width-headDim.height/2, offset.y);
                poly.addPoint(offset.x+headDim.width, offset.y+headDim.height/2);
                poly.addPoint(offset.x+headDim.width-headDim.height/2, offset.y+headDim.height);
                poly.addPoint(offset.x+headDim.height/2, offset.y+headDim.height);
                if(isHolder)
                    g.setColor(ColorUtils.getDarker(color));
                else
                    g.setColor(color);
                g.fillPolygon(poly);

                if (dockBody)
                     g.setColor(ColorUtils.getHighlight(color));
                else if(hasError)
                    g.setColor(Element.ERROR_COLOR);
                else
                    g.setColor(ColorUtils.getShadow(color));
                g.drawPolygon(poly);
                offset.y-=2;
            }
            else
            {
                result=getBody().draw(g,offset).union(result);
            }

            // text
            int x = offset.x+headDim.height/2;
            //if(titlePieces.size()>0 && titlePieces.get(0).trim().isEmpty())
            // text
            if(titlePieces.size()>0)
            {
                if(parameters.isEmpty())
                {
                    int th = g.getFontMetrics().getAscent();
                    int tw = g.getFontMetrics().stringWidth(title);
                    g.setColor(TEXT_COLOR);
                    g.setFont(new Font("Monospaced", Font.BOLD, fontsize)); 
                    g.drawString(title, offset.x+(headDim.width-tw)/2, offset.y+th+(headDim.height-th)/2); 
                }
                else
                {
                    for (int i = 0; i < titlePieces.size(); i++) {
                        String piece = titlePieces.get(i);
                        // draw piece
                        if(!piece.trim().isEmpty())
                        {
                            int th = g.getFontMetrics().getAscent();
                            g.setColor(TEXT_COLOR);
                            g.setFont(new Font("Monospaced", Font.BOLD, fontsize)); 
                            g.drawString(piece.trim(), x, offset.y+th+(headDim.height-th)/2);
                            x+=g.getFontMetrics().stringWidth(piece.trim()+" ");
                        }
                        // draw parameter
                        if(i<parameters.size())
                        {
                            Element param = parameters.get(i);
                            Dimension paramHead = param.getHeadDimension(g);
                            result=param.draw(g, new Point(x, offset.y+(headDim.height-paramHead.height)/2)).union(result);
                            //System.out.println("Param: "+param.getDrawLast());
                            //if(param.getDrawLast().size()!=0) System.out.println("****");
                            drawLast.addAll(param.getDrawLast());
                            g.setFont(new Font("Monospaced", Font.BOLD, fontsize));
                            x+=paramHead.width+g.getFontMetrics().stringWidth(" ");
                        }
                    }
                }
            }
            else
            {
                g.setColor(TEXT_COLOR);
                g.setFont(new Font("Monospaced", Font.BOLD, fontsize));
                int th = g.getFontMetrics().getAscent();
                if(title!=null)
                    g.drawString(title, x, offset.y+th+(headDim.height-th)/2);
            }

            if(getBody()!=null)
            {
                result=getBody().draw(g).union(result);
            }
        } 
        
        if(getEditor()!=null)
            getEditor().getDrawLast().addAll(drawLast);
        else if(getParent()!=null)
            getParent().getDrawLast().addAll(drawLast);
        
        return result;
   }
    
    public int getTotalHeight(Graphics2D g)
    {
        int height = 0;
        height+=CONNECTOR_HEIGHT;
        height+=getHeadDimension(g).height;
        if(hasBody)
        {
            height+=getBodyDimension(g).height;
            height+=BAR_SIZE;
        }
        return height;
    }
    
    public int getTotalHeight()
    {
        int height = 0;
        height+=CONNECTOR_HEIGHT;
        height+=headDim.height;
        if(hasBody)
        {
            height+=bodyDim.height;
            height+=BAR_SIZE;
        }
        return height;
    }
    
    public Dimension getHeadDimension(Graphics2D g) {
        // %TODO% : implement caching mecanisme for this method
        
        // get width of title
        // %TODO% : filter out placeholders in title string
        
        if(isHolder & getBody()!=null && getType()!=Type.LIST) return getBody().getHeadDimension(g);
        
        // get the height
        int height = g.getFontMetrics().getHeight();
        // get height of head
        int head = 0;
        for (int i = 0; i < parameters.size(); i++) {
            Element get = parameters.get(i);
            Dimension dim = get.getHeadDimension(g);
            if(dim.height>head) head=dim.height;
            
        }
        height=Math.max(height, head);
        // add padding
        if(type!=Type.CONDITION && type!=Type.EXPRESSION && type!=Type.VALUE && type!=Type.LIST && type!=Type.ITEM)
            height+=2*Element.PADDING_BT;
        else
            height+=1*Element.PADDING_BT;
        
        // add padding twice (left & right)
        int width = 2*PADDING_LR;
        
        // change this for Condition
        if(getType()==Type.CONDITION)
        {
            width = height;
        }
        
        // add extra space for List
        //System.out.println(this.getClass().getSimpleName());
        if(getType()==Type.LIST)
        {
            width+=height;
        }
        
        //System.out.println(title+" > "+titlePieces.size());
        if(titlePieces.size()>0)
        {
            /*
            for (int i = 0; i < titlePieces.size(); i++) {
                String piece = titlePieces.get(i);
                //System.out.println("Piece "+i+" : "+piece);
                // draw piece
                g.setColor(TEXT_COLOR);
                g.setFont(new Font("Monospaced", Font.BOLD, fontsize));
                int th = g.getFontMetrics().getAscent();
                width+=g.getFontMetrics().stringWidth(piece.trim());
                // draw parameter
                if(i<parameters.size())
                {
                    Element param = parameters.get(i);
                    Dimension paramHead = param.getHeadDimension(g);
                    width+=paramHead.width+2*g.getFontMetrics().stringWidth(" ");
                }
            }*/
            
            g.setColor(TEXT_COLOR);
            g.setFont(new Font("Monospaced", Font.BOLD, fontsize));
            for (int i = 0; i < titlePieces.size(); i++) {
                String piece = titlePieces.get(i);
                // draw piece
                if(!piece.trim().isEmpty())
                {
                    int th = g.getFontMetrics().getAscent();
                    //g.drawString(piece, x, offset.y+th+(headDim.height-th)/2);
                    piece=piece.trim();
                    piece+=" ";
                    width+=g.getFontMetrics().stringWidth(piece);
                }
                //else if (i != titlePieces.size()-1)
                //    width+=g.getFontMetrics().stringWidth(" ");
                // draw parameter
                if(i<parameters.size())
                {
                    Element param = parameters.get(i);
                    Dimension paramHead = param.getHeadDimension(g);
                    width+=paramHead.width+g.getFontMetrics().stringWidth("");
                    if(i<parameters.size()-1)
                    {
                        width+=g.getFontMetrics().stringWidth(" ");
                    }
                }
            }
            
            /*if(getType()==Type.CONDITION)
            {
                width+=parameters.size()*g.getFontMetrics().stringWidth(" ");
            }*/
        }
        /*
        if(titlePieces.size()>0)
        {
            for (int i = 0; i < titlePieces.size(); i++) {
                String piece = titlePieces.get(i);
                g.setFont(new Font("Monospaced", Font.BOLD, fontsize));
                if(!piece.isEmpty())
                {
                    // add width of string
                    width+=g.getFontMetrics().stringWidth(piece.trim());
                    // if not last string in list, add a space
                    if(i<=titlePieces.size()-1)
                        width+=+g.getFontMetrics().stringWidth(" ");
                }
                // draw parameter
                if(i<parameters.size())
                {
                    Element param = parameters.get(i);
                    // add width of param
                    width+=param.getHeadDimension(g).width;
                    // if not last string in list, add a space
                    if(i < titlePieces.size()-2)
                        width+=g.getFontMetrics().stringWidth(" ");
                    else if(i == titlePieces.size()-1 && !titlePieces.get(titlePieces.size()-1).isEmpty())
                        width+=g.getFontMetrics().stringWidth(" ");
                }
            }
        }*/
        else
        {
            if(title!=null && !title.trim().isEmpty())
            {
                width+=g.getFontMetrics().stringWidth(title);
            }
        }
        
        if(type==Type.CONDITION || type==Type.EXPRESSION || type==Type.VALUE || type==Type.LIST || type==Type.ITEM)
            return new Dimension(Math.max(width,MIN_PARAMETER_WIDTH),height);
        else
            return new Dimension(Math.max(width,MIN_ELEMENT_WIDTH),height);
    }
    
    public Dimension getBodyDimension(Graphics2D g) {
        // %TODO% : implement caching mecanisme for this method
        
        // get width of title
        // %TODO% : filter out placeholders in title string
        int width = BAR_SIZE;
        // get width of each input
        for (int i = 0; i < parameters.size(); i++) {
            Element get = parameters.get(i);
            width+=get.getBodyDimension(g).width;
        }
        // add padding
        width+=2*Element.PADDING_LR;
        
        
        // get the height
        int height = 0; //BAR_SIZE;
        // get height of body
        Element tmp = getBody();
        while(tmp!=null)
        {
            height+=tmp.getTotalHeight(g);
            tmp=tmp.getNext();
        }
        /*
        for (int i = 0; i < body.size(); i++) {
            Element get = body.get(i);
            height+=get.getTotalHeight(g);
            
        }*/
        
        // add empty
        // if(body.isEmpty())
        if(getBody()==null)
            height+=EMPTY_HEIGHT;
        
        // add padding
        //height+=2*Element.PADDING_BT;
        
        return new Dimension(Math.max(width,MIN_ELEMENT_WIDTH),height);
    }
    
    public Element getSelected(Point point)
    {
        /***
         * ANALYSING
         */
        /*
        System.out.println("This is a: "+this.getClassname());
        System.out.println("My parent is: "+(parent==null?"NULL":getParent().getClassname()));
        System.out.println("My next is: "+(next==null?"NULL":getNext().getClassname()));
        System.out.println("My prev is: "+(prev==null?"NULL":getPrev().getClassname()));
        /**/
        
        
        Element selected = null;
                
        //System.out.println("Checking: "+this.getClassname());
        
        // if the used clicked inside an element, he may have clicked
        // the element itself or one of it's parameters.
        if(isInside(point))
        {
            // check each parameter
            for (int i = 0; i < parameters.size(); i++) {
                Element param = parameters.get(i);
                //System.out.println("Checking parameter: "+param.getClassname());
                Element result = param.getSelected(point);
                if(result!=null)
                {
                    //System.out.println("Selecting parameter: "+result.getClassname());
                    return result;
                }
            }
                
            // remember that we have been selected
            selected=this;
        }
        else
        {
            // we may have clicked on an item of a list that is open, se all
            // parameters with the type "List" have to be checked anyway (if open)
            for (int i = 0; i < parameters.size(); i++) {
                Element param = parameters.get(i);
                boolean go = true;
                // don't continue if this is a CLOSED list
                if(param.getType()==Type.LIST && !((List)param).isOpen())
                    go=false;
                if(go)
                {
                    //System.out.println("Checking parameter: "+param.getClassname());
                    Element result = param.getSelected(point);
                    if(result!=null)
                    {
                        //System.out.println("Selecting parameter: "+result.getClassname());
                        return result;
                    }
                }
            }
        }
        
        // let's dive into the body 
        if(getBody()!=null)
        {
            //System.out.println("Checking body of "+getClassname());
            Element result = getBody().getSelected(point);
            if(result!=null)
            {
                // detach if first element
                if(result==body && result.getType()!=Type.ITEM)
                {
                    Element last = result.getLastElementForStructureByPosition(Pos.BODY);
                    setBody(last.next);
                    if(last!=null)
                    {
                        last.setNext(null);
                        last.setPrev(null);
                    }
                }
                return result;
            }
        }/**/
        
        // don't forget to check the next
        if(getNext()!=null)
        {
            Element result = getNext().getSelected(point);
            if(result!=null)
                return result;
        }/**/
        
        // if any above has been seleced, we will never get here
        // so if we did not find something until this point, go ahead
        if(selected!=null)
        {
            //System.out.println("Being inside: "+this.getClassname());
            // special case to VALUE
            if(getType()==Type.VALUE || 
               getType()==Type.LIST  ||
               getType()==Type.PARAMETERS  ||
               getType()==Type.ITEM) 
                return this;
            
            // we don't want to select any holder
            if(isHolder) return null;
        
            // do we have a previous element
            if(prev!=null)
            {
                // are we the top most element of some other body?
                if(prev!=parent)
                {
                    Element last = getLastElementForStructureByPosition(Pos.BOTTOM);
                    // make my previous' element point to my next element
                    prev.setNext(last.next);
                    // unlink from the next element
                    if(last!=null)
                    {
                        last.setNext(null);
                        last.setPrev(null);
                    }
                }
            }
            
        }
        
        return selected;
    }
    
    public Element getSelected_old(Point point)
    {
        /*if(isHolder && body!=null)
        {
            Element sub = body.getSelected(point);
            if(sub!=null) return sub;
        }*/
        
        // this applies only for Lists, as wee need to check if one of the subitems has been clicked on
        System.out.println("Checking: "+this.getClassname());
        /*if(getType()==Type.LIST)
        {
            //System.out.println("Check the list: "+getClassname());
            if(getBody()!=null && ((List) this).isOpen())
            {
                Element sub = getBody();
                while(sub!=null)
                {
                    //System.out.println("Sub: "+sub.getClass().getSimpleName());
                    if(sub.isInside(point))
                    {
                        //System.out.println("--Sub: "+sub.getClass().getSimpleName());
                        if(sub!=null)
                            sub.onDetach(sub.getPrev());
                        return sub;
                    }
                    sub=sub.getNext();
                }
            }
        }*/
        
        /*
        System.out.println("I am: "+this.getClass().getSimpleName());
        if(getType()==Type.LIST)
            System.out.println("-- and I am: "+((List)this).isOpen());
        */
        
        // try parameters first
        for (int i = 0; i < parameters.size(); i++) {
            Element param = parameters.get(i);
            System.out.println("Param: "+param.getClassname());
            
            if(param.getType()==Type.LIST)
            {
                System.out.println("Type: List, Class: "+getClass().getSimpleName());
                if(isInside(point)) {
                    //((List)this).toggle();
                    return this;
                }
            }
            else
            // check if we are inside the parameter
            /*boolean go = param.isInside(point);
            if(param.getType()==Type.LIST && param.getBody()!=null)
            {
                System.out.println("Got LIST and I am: "+((List)param.body).isOpen());
                go = go || ((List)param.getBody()).isOpen();
            }*/
            //if(go)
            //if(param.isInside(point) || (param instanceof List && ((List)param.body).isOpen()))
            {
                // check if this parameter has a body and is not a value
                if(param.getBody()!=null)
                {
                    // the body may contain parameters too, so try to get them
                    Element sub = param.getBody().getSelected(point);
                    // if we found something
                    // the previous getSelected will also catch the item iteself, 
                    // so pay attention to discard that one!!!
                    if(sub!=null && sub!=param.getBody())
                    {
                        //System.out.println("Found sub: "+sub+" / "+sub.getClass().getSimpleName()+" inside: "+param.body+" / "+param.body.getClass().getSimpleName());
                        // sub will contain the reference to the sub-parameters body
                        // so return it.
                        //System.out.println("Return sub: "+sub.getClass().getSimpleName());
                        if(sub.getType()==Type.LIST)
                        {
                            if(((List)sub.getParent()).isOpen())
                            {
                                if(sub!=null)
                                    sub.onDetach(sub.getPrev());
                                return sub;
                            }
                        }
                        else
                        {
                            if(sub!=null)
                                sub.onDetach(sub.getPrev());
                            return sub;
                        }
                    }
                    
                    if(param.isInside(point))
                    {
                        // store a reference to the body
                        Element paramBody = param.getBody();
                        // disconnect the body if not a value
                        //System.out.println("BODY PARAM: "+param.body.getClass().getSimpleName());
                        //if(!(param.body instanceof Text) && !(param.body instanceof List) && !(param.body instanceof Item))
                        if(getType()!=Type.VALUE && 
                           getType()!=Type.LIST && 
                           getType()!=Type.ITEM) 
                        {
                            //System.out.println("Disco"); 
                            param.setBody(null);
                        }
                        // return the body element
                        //System.out.println("Return paramBody: "+paramBody.getClass().getSimpleName());
                        if(paramBody!=null)
                            paramBody.onDetach(paramBody.getPrev());
                        return paramBody;
                    }
                }
                // we are in the case of a parameter that cannot be dragged away with the mouse.
                else
                {
                    //System.out.println("Value: "+param.isInside(point));
                    if(param.isInside(point) && 
                            (param.getType()==Type.VALUE ||
                            param.getType()==Type.LIST ||
                            param.getType()==Type.ITEM))
                    {
                        return param;
                    }
                }
            }
        }
      
        // base element
        if(isInside(point)) 
        {
            //System.out.println("Here we are! "+this.getClassname());
            this.onDetach(this.getPrev());
            return this;
        }
        
        /*
        // any of the inputs
        for (int i = 0; i < parameters.size(); i++) {
            Element get = parameters.get(i);
            if(get.isInside(point)) 
            {
                parameters.remove(get);
                //get.parent=null;
                return get;
            }
        }
        */
        
        // any of the body elements
        Element tmp = getBody();
        Element prev = null;
        while(tmp!=null)
        {
            //System.out.println("tmp: "+tmp);
            //System.out.println("next: "+tmp.next);
            
            // try sub element first (disconnect by recursive called method)
            Element selected = tmp.getSelected(point);
            if(selected!=tmp && selected!=null) 
            {
                //System.out.println("Found in "+this+" sub element: "+selected);
                if(selected!=null)
                    selected.onDetach(selected.getPrev());
                return selected;
            }
            
            // if not, try the element itself
            if(tmp.isInside(point))  
            {
                if(tmp!=null && tmp.getType()!=Type.ITEM)
                {
                    // connect node that is after tmp
                    if(prev==null)
                        setBody(tmp.getNext());
                    else
                        prev.setNext(tmp.getNext());
                    // diconnect tmp from the next nore
                    tmp.setNext(null);
                }
                
                //System.out.println("Found in "+this+" body element: "+tmp);
                if(tmp!=null)
                    tmp.onDetach(tmp.getPrev());
                return tmp;
            }
            
            prev=tmp;
            tmp=tmp.getNext();
        }
        
        // one of the connected elements
        if(getNext()!=null)
        {
            Element testNext = getNext().getSelected(point);
            
            // diconnect is done by previous recursive call
            if(testNext==getNext())
            {
                setNext(getNext().getNext());
                testNext.setNext(null);
            }
            //System.out.println("Found in "+this+" next element: "+testNext);
            if(testNext!=null)
                testNext.onDetach(testNext.getPrev());
            return testNext;
        }
        
        // none
        return null;
    }
    
    public boolean isInside(Point point)
    {
        if(getType()==Type.INSTRUCTION)
        {
            //System.out.println("Offset for "+this+" is "+offset+" Head : "+headDim);
            Rectangle top = new Rectangle(offset.x, offset.y, headDim.width, headDim.height+CONNECTOR_HEIGHT);

            if(hasBody)
            {
                Rectangle bar    = new Rectangle(offset.x, offset.y+headDim.height, BAR_SIZE, bodyDim.height);
                Rectangle bottom = new Rectangle(offset.x, offset.y+CONNECTOR_HEIGHT+headDim.height+bodyDim.height, headDim.width, BAR_SIZE);
                return top.contains(point) || bar.contains(point) || bottom.contains(point);
            }
            else
                return top.contains(point);
        }
        else // all the others
        {
            Rectangle top = new Rectangle(offset.x, offset.y, headDim.width, headDim.height+CONNECTOR_HEIGHT);
            return top.contains(point);
        }
    }
    
    public Element canDock(Element other)
    {
        dockBody=false;
        dockBottom=false;
        
        if(other.type==Type.EXPRESSION || other.type==Type.CONDITION)
        {
            // check parameters
            if(parameters.size()>0)
            {
                for (int i = 0; i < parameters.size(); i++) {
                    Element param = parameters.get(i);
                    
                    //System.out.println("testing parameter "+i+" ("+param.getReturnType()+") of: "+this.getTitle());
            
                    Element sub = param.canDock(other);
                    if(sub!=null)
                    {
                        //System.out.println("Returning");
                        return sub;
                    }
                    
                    boolean typeCheck = true;
                    //System.out.println("Checking type ...");
                    //System.out.println("ParamType: "+param.getType()+" / "+param.getTitle());
                    //System.out.println("OtherType: "+other.getType()+" / "+other.getTitle());
                    /*
                    System.out.println("---");
                    System.out.println("ParamType: "+(param).getReturnType()); //+" / "+param.getTitle());
                    System.out.println("OtherType: "+(other).getReturnType()); //+" / "+other.getTitle());
                    System.out.println("Param: "+param.getType());
                    System.out.println("Other: "+other.getType());
                    /**/
                    if((param.getType()==Type.EXPRESSION && other.getType()==Type.EXPRESSION))
                    {
                        //System.out.println("INSIDE");
                        typeCheck = typeCanAcceptType(param.getReturnType(),other.getReturnType());
                        //System.out.println("ParamType: "+(param).getReturnType()+" / "+param.getTitle());
                        //System.out.println("OtherType: "+(other).getReturnType()+" / "+other.getTitle());
                    }
                    /*
                    System.out.println("TypeCheck: "+typeCheck);
                    System.out.println("ParamClass: "+param.getClass().getSimpleName());
                    System.out.println("OtherClass: "+other.getClass().getSimpleName());
                    System.out.println("ClassCheck: "+(param.getClass().isInstance(other)));
                    System.out.println("BodyCheck : "+(param.getBody()==null));
                    System.out.println("InsideChk : "+param.isInside(other.offset));
                    /**/
                    
                    String otherNeeds = other.getSubNeedsParent();
                    if(otherNeeds!=null && !otherNeeds.trim().isEmpty())
                        typeCheck=typeCheck && param.getParent().checkParentConstraint(other);
                    
                    if(param.isInside(other.offset) && 
                            param.getType()==other.getType() && 
                            (param.getClass().isInstance(other)) && 
                            param.getBody()==null && 
                            typeCheck)
                    {
                        //System.out.println("OK");
                        param.dockBody=true;
                        return param;
                    }
                }
            }
            
            // check body
            if(getBody()!=null)
            {
                Element test = getBody().canDock(other);
                if(test!=null)
                    return test;
            }
            
            // check next
            if(getNext()!=null)
            {
                Element test = getNext().canDock(other);
                if(test!=null)
                    return test;
            }
        }
        else
        {
            if(this.hasBottom && other.hasTop)
            {
                // zone "bottom" with body present
                if(this.hasBody)
                {
                    // define zone where offset has to be to be dockable
                    Rectangle zone = new Rectangle(offset.x, offset.y+CONNECTOR_HEIGHT+headDim.height+bodyDim.height+BAR_SIZE, 
                                                   CONNECTOR_OFFSET, BAR_SIZE);
                    // check if we can inter-connect
                    boolean testBottom = true;
                    if(!other.hasBottom && getNext()!=null) testBottom=false;

                    boolean ok = zone.contains(other.offset) && testBottom && other.allowDockTo(this, Pos.BOTTOM);
                    // check if we need to test if the inserted element is allowed to be docked to from the bottom element
                    if(testBottom && getNext()!=null) 
                        ok=ok && getNext().allowDockTo(other, Pos.BOTTOM);
                    
                    if(other.getClassname().equals("VariableDefinition") && hasVariableWithName(other.getVariableDefinition().name)) 
                        ok=false;
                    
                    String otherNeeds = other.getSubNeedsParent();
                    if(otherNeeds!=null && !otherNeeds.trim().isEmpty() && getParent()!=null)
                        ok=ok&&getParent().checkParentConstraint(other);
                    
                    if (ok)
                    {
                        dockBottom=true;
                        return this;
                    }
                }
                // zone "bottom" with no body there!!
                else
                {
                     // define zone where offset has to be to be dockable
                    Rectangle zone = new Rectangle(offset.x, offset.y+CONNECTOR_HEIGHT+headDim.height, 
                                                   CONNECTOR_OFFSET, BAR_SIZE);
                    // check if we can inter-connect
                    boolean testBottom = true;
                    if(!other.hasBottom && getNext()!=null) testBottom=false;

                    boolean ok = zone.contains(other.offset) && testBottom && other.allowDockTo(this, Pos.BOTTOM);
                    // check if we need to test if the inserted element is allowed to be docked to from the bottom element
                    if(testBottom && getNext()!=null) 
                        ok=ok&&getNext().allowDockTo(other, Pos.BOTTOM);
                    
                    if(other.getClassname().equals("VariableDefinition") && hasVariableWithName(other.getVariableDefinition().name)) 
                        ok=false;
                    
                    String otherNeeds = other.getSubNeedsParent();
                    if(otherNeeds!=null && !otherNeeds.trim().isEmpty() && getParent()!=null)
                        ok=ok&&getParent().checkParentConstraint(other);
                    
                    if (ok)
                    {
                        dockBottom=true;
                        return this;
                    }
                }
            }
            // zone "body"
            if(this.hasBody && other.hasTop)
            {
                // define zone where offset has to be to be dockable
                Rectangle zone = new Rectangle(offset.x+CONNECTOR_OFFSET, offset.y+CONNECTOR_HEIGHT+headDim.height, 
                                               CONNECTOR_OFFSET, BAR_SIZE);
                // check if we can inter-connect
                boolean testBottom = true;
                if(!other.hasBottom && getBody()!=null) testBottom=false;
                
                if(other.getClassname().equals("VariableDefinition") && 
                            (hasVariableWithNameInBlock(other.getVariableDefinition().name) || hasVariableWithName(other.getVariableDefinition().name))) 
                    testBottom=false;
                    
                String otherNeeds = other.getSubNeedsParent();
                if(otherNeeds!=null && !otherNeeds.trim().isEmpty())
                    testBottom=testBottom&&checkParentConstraint(other);
                
                if (zone.contains(other.offset) && testBottom && other.allowDockTo(this, Pos.BODY))
                {
                    dockBody=true;
                    return this;
                }
                
                // loop through body elements
                Element tmp = getBody();
                while(tmp!=null)
                {
                    Element dock = tmp.canDock(other);
                    if(dock!=null)
                        return dock;
                    tmp=tmp.getNext();
                }
                
                // check next element
                if(getNext()!=null)
                {
                    Element dock = getNext().canDock(other);
                    if(dock!=null)
                        return dock;
                }
            }
        }
        return null;
    }
    
    public ArrayList<Element> getSubs()
    {
        ArrayList<Element> subs = new ArrayList<>();
        
        // add this
        subs.add(this);
        
        // add parameters
        for (int i = 0; i < parameters.size(); i++) {
            subs.addAll(parameters.get(i).getSubs());
        }
        
        // add body
        if(getBody()!=null)
            subs.addAll(getBody().getSubs());
        
        // add next
        if(getNext()!=null)
            subs.addAll(getNext().getSubs());
        
        return subs;
    }
    
    public boolean checkParentConstraint(Element other)
    {
        //System.out.println("Testing if: "+this.getClassname()+" passes parent check for: "+other.getClassname()+" > "+other.getSubNeedsParent());
        
        /*
        ArrayList<Element> subs = other.getSubs();
        
        boolean result = false;
        
        //System.out.println(subs.size());
        Element tmp = this;
        while(tmp!=null)
        {
            System.out.println("- Checking: "+tmp.getClassname());
            for (int i = 0; i < subs.size(); i++) {
                Element sub = subs.get(i);
                System.out.println("--"+tmp.getClassname()+ " > "+sub.getClassname());
                if(sub.getNeedsParent()!=null && 
                   !sub.getNeedsParent().trim().isEmpty())
                {
                    System.out.println("Found need for: "+sub.getClassname()+" ("+sub.getNeedsParent()+")");
                    result = sub.getNeedsParent().contains(tmp.getClassname()) || result;
                }
            }
            tmp=tmp.getParent();
        }
            
        System.out.println("=> "+result);
        return result;
        */
        
        
        // check direkt needs of other
        
        String otherNeeds = other.getSubNeedsParent();
        
        if(otherNeeds==null) return true;
        
        if(otherNeeds.trim().isEmpty()) return true;
        
        Element tmp = this;
        while(tmp!=null)
        {
            if(otherNeeds.contains(tmp.getClassname()))
                return true;
            tmp=tmp.getParent();
        }
        
        return false;
    }
    
    public Element dock(Element other)
    {
        if(other.type==Type.EXPRESSION || other.type==Type.CONDITION)
        {
            //System.out.println("I am    : "+this.getClassname());
            //System.out.println("Other is: "+other.getClassname());
            // dock immediately
            if(this.isHolder)
            {
                //System.out.println("this is holder");
                this.setBody(other);
                other.onConnect(this);
            }
            
            // check parameters
            if(parameters.size()>0)
            {
                for (int i = 0; i < parameters.size(); i++) {
                    Element param = parameters.get(i);
                    
                    if(param.canDock(other)!=null)
                        param.dock(other);
                    
                    //System.out.println("trying "+i+" for "+this);
                    if(param.isInside(other.offset))
                    {
                        //System.out.println("Dockd");
                        param.setBody(other);
                        other.onConnect(param);
                        return param;
                    }
                }
            }
            
            // check body
            if(getBody()!=null)
            {
                Element test = getBody().canDock(other);
                if(test!=null)
                {
                    test.dock(other);
                    return test;
                }
            }
            
            // check next
            if(getNext()!=null)
            {
                Element test = getNext().canDock(other);
                if(test!=null)
                {
                    test.dock(other);
                    return test;
                }
            }
        }
        else
        {
            if(this.hasBottom && other.hasTop)
            {
                if(this.hasBody)
                {
                    // define zone where offset has to be to be dockable
                    Rectangle zone = new Rectangle(offset.x, offset.y+CONNECTOR_HEIGHT+headDim.height+bodyDim.height+BAR_SIZE, 
                                                   CONNECTOR_OFFSET, BAR_SIZE);
                    if (zone.contains(other.offset))
                    {
                        other.getLastNext().setNext(this.getNext());
                        this.setNext(other);
                        other.onConnect(this);
                        //other.parent=this.parent;
                        // notify blockmost element
                        /*
                        if(other.getClassname().equals("VariableDefinition"))
                            getBlockMostElement().refresh(new Change(other, -1, "dock", this, other));
                        else if(other.getClassname().equals("SetVariable"))
                            getBlockMostElement().refresh(new Change(other, -1, "dock", this, other));
                        */
                        // notify other elements
                        getBlockMostElement().refresh(new Change(other, -1, "dock", this, other));
                        return this;
                    }
                }
                else
                {
                    // define zone where offset has to be to be dockable
                    Rectangle zone = new Rectangle(offset.x, offset.y+CONNECTOR_HEIGHT+headDim.height, 
                                                   CONNECTOR_OFFSET, BAR_SIZE);
                    if (zone.contains(other.offset))
                    {
                        other.getLastNext().setNext(this.getNext());
                        this.setNext(other);
                        other.onConnect(this);
                        //other.parent=this.parent;
                        // notify blockmost element
                        /*
                        if(other.getClassname().equals("VariableDefinition"))
                            getBlockMostElement().refresh(new Change(other, -1, "dock", this, other));
                        else if(other.getClassname().equals("SetVariable"))
                            getBlockMostElement().refresh(new Change(other, -1, "dock", this, other));
                        */
                        // notify other elements
                        getBlockMostElement().refresh(new Change(other, -1, "dock", this, other));
                        return this;
                    }
                }
            }
            if(this.hasBody && other.hasTop)
            {
                // define zone where offset has to be to be dockable
                Rectangle zone = new Rectangle(offset.x+CONNECTOR_OFFSET, offset.y+CONNECTOR_HEIGHT+headDim.height, 
                                               CONNECTOR_OFFSET, BAR_SIZE);
                if (zone.contains(other.offset))
                {
                    //other.next=body;
                    Element tmp = other;
                    while(tmp.getNext()!=null) tmp=tmp.getNext();
                    tmp.setNext(getBody());
                    
                    setBody(other);
                    other.onConnect(this);
                    //other.parent=this;
                    // notify blockmost element
                    /*
                    if(other.getClassname().equals("VariableDefinition"))
                        getBlockMostElement().refresh(new Change(other, -1, "dock", this, other));
                    else if(other.getClassname().equals("SetVariable"))
                        getBlockMostElement().refresh(new Change(other, -1, "dock", this, other));
                    */
                    // notify other elements
                    getBlockMostElement().refresh(new Change(other, -1, "dock", this, other));
                    return this;
                }
                
                // loop through body elements
                Element tmp = getBody();
                while(tmp!=null)
                {
                    Element dock = tmp.canDock(other);
                    if(dock!=null)
                    {
                        return dock.dock(other);
                    }
                    tmp=tmp.getNext();
                }
                
                if(getNext()!=null)
                {
                    Element dock = getNext().canDock(other);
                    if(dock!=null)
                        return dock.dock(other);
                }
            }
        }
        return null;
    }
    
    
    public void cleanDockStatus()
    {
        // self
        dockBody=false;
        dockBottom=false;
        
        // parameter
        for (int i = 0; i < parameters.size(); i++) {
            Element param = parameters.get(i);
            param.cleanDockStatus();
        }
        
        // body
        if(getBody()!=null)
            getBody().cleanDockStatus();
        
        // next
        if(getNext()!=null)
            getNext().cleanDockStatus();
        
    }
    
    public Element getLastNext()
    {
        Element tmp = getNext();
        if(tmp==null) return this;
        while(tmp.getNext()!=null)
            tmp=tmp.getNext();
        return tmp;
    }
    
    public Element check()
    {
        //stem.out.println("Checking: "+this.getClassname()+" - Type? "+getType()+" - Holder? "+isHolder()+" - Body: "+getBody()+" - Next: "+getNext());
        Element result = null;
        
        if(getType()==Type.VALUE && getTitle().trim().isEmpty()) 
        {
            setError(true);
            result = this;
        }
        else if(getType()!=Type.VALUE && getType()!=Type.PARAMETERS && isHolder() && getBody()==null) 
        {
            setError(true);
            result = this;
        }
        
        // check each parameter
        for (int i = 0; i < parameters.size(); i++) {
            Element param = parameters.get(i);
            Element paramCheck = param.check();
            if (paramCheck!=null) result=paramCheck;
            //result = param.check() && result;
        }
        
        // check body
        if(getBody()!=null)
        {
            Element bodyCheck = getBody().check();
            if (bodyCheck!=null) result=bodyCheck;
            //result =  getBody().check() && result;
        }
        
        // check next
        if(getNext()!=null)
        {
            Element nextCheck = getNext().check();
            if (nextCheck!=null) result=nextCheck;
            //result =  getNext().check() && result;
        }
        
        return result;
    }
    
    public String getJavaCode()
    {
        return getJavaCode(0);
    }
    
    public String getJavaCode(int indent)
    {
        if(getType()==Type.VALUE) return title;
        
        
        // get a copy of the output code
        String result = code; 
        
        // replace parameters
        for (int i = 0; i < parameters.size(); i++) {
            Element param = parameters.get(i);
            String paramCode = param.getJavaCode().trim();
            /*System.out.println("ME: "+getClassname()+": "+i);
            System.out.println("PA: "+param.getClassname());
            System.out.println("JA: "+paramCode);/**/
            //System.out.println(transformations);
            if(transformations.containsKey(i))
            {
                String name = transformations.get(i);
                if(!name.isEmpty())
                    try {
                        Method method = getClass().getMethod(name,String.class);
                        paramCode = (String) method.invoke(param,paramCode);
                    } 
                    catch (NoSuchMethodException | SecurityException ex) {
                        ex.printStackTrace();
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                        ex.printStackTrace();
                    }
            }
            result=result.replace("$"+i, paramCode);
            
        }
        
        // add indents
        //result=getIndent(indent)+result;
        result=result.replace("\n", "\n"+getIndent(indent));
        
        // apply transformations
        if(transformation!=null && !transformation.isEmpty())
        {
            //System.out.println("Doing transformation: "+transformation+" on: "+result.trim()+".");
            try {
                Method method = getClass().getMethod(transformation,String.class);
                result = (String) method.invoke(this,result);
            } 
            catch (NoSuchMethodException | SecurityException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                ex.printStackTrace();
            }
        }
        
        // replace the body
        if(getBody()!=null)
        {
            if(getType()==Type.INSTRUCTION)
            {
                if(result.contains("$$$"))
                {
                    String body=getBody().getJavaCode(indent+2);
                    result=result.replace("$$$",getIndent(2)+body);
                }
                else
                {
                    String body=getBody().getJavaCode(indent+1);
                    result=result.replace("$$",getIndent(1)+body);
                }
            }
            else
                result+=getBody().getJavaCode(indent+1);
        }
        else
        {
            if(result.contains("$$$"))
            {
                result=result.replace("$$$",getIndent(2));
            }
            else
                result=result.replace("$$",getIndent(1));
        }
        
        // for instructions, append the code of the next node
        if(getType()==Type.INSTRUCTION)
        {
             // get the code of the next element
            if(getNext()!=null)
                result +="\n"+getIndent(indent)+getNext().getJavaCode(indent);
        }
        
        //System.out.println(result);
        //result+="|";
        
        // replace "$this"
        result = result.replace("$this",getTopMostElement().getEditor().getBloxsClass().getName()+".this");
        
        return result;
    }
    
    public String getIndent(int indent)
    {
        String result = "";
        for(int i=0; i<indent*4; i++)
            result+=" ";
        return result;
    }
    
    public void cleanErrors()
    {
        // self
        hasError=false;
        
        // parameter
        for (int i = 0; i < parameters.size(); i++)
            parameters.get(i).cleanErrors();
        
        // body
        if(getBody()!=null)
            getBody().cleanErrors();
        
        // next
        if(getNext()!=null)
            getNext().cleanErrors();
        
    }

    public Point getOffset() {
        return offset;
    }

    public void setOffset(Point offset) {
        this.offset = offset;
    }

    @Override
    public String toString()
    {
        return title+" @ "+Integer.toHexString(this.hashCode());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
       parseTitle(title);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean hasError() {
        return hasError;
    }

    public void setError(boolean hasError) {
        this.hasError = hasError;
    }

    public Element getBody() {
        return body;
    }

    public void setBody(Element body) {
        //System.out.println("Setting body for: "+this.getClassname()+" width: "+body.getClassname());
        this.body = body;
        if(body!=null)
        {
            body.setParent(this);
            body.setPrev(this);
            //System.out.println("Setting PREV for: "+body.getClassname()+" to: "+this.getClassname());
        }
    }
    
    
    
    public static Object cloneObject(Object obj){
        try{
            Object clone = obj.getClass().newInstance();
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if(field.get(obj) == null || Modifier.isFinal(field.getModifiers())){
                    continue;
                }
                if(field.getType().isPrimitive() || field.getType().equals(String.class)
                        || field.getType().getSuperclass().equals(Number.class)
                        || field.getType().equals(Boolean.class)){
                    field.set(clone, field.get(obj));
                }else{
                    Object childObj = field.get(obj);
                    if(childObj == obj){
                        field.set(clone, clone);
                    }else{
                        field.set(clone, cloneObject(field.get(obj)));
                    }
                }
            }
            return clone;
        }catch(Exception e){
            return null;
        }
    }
   
    public VariableDefinition getVariable(String name)
    {
        ArrayList<VariableDefinition> variables = getVariables();

        for (int i = 0; i < variables.size(); i++) {
            VariableDefinition get = variables.get(i);

            if(get.name.equals(name))
                return get;
        }
        
        return null;
    }
    
    // Get all variables defined for the current position
    // = got up the tree until there is no "prev" anymore
    public ArrayList<VariableDefinition> getVariables()
    {
        //System.out.println("Look vor var inside: "+this.getClassname());
        
        ArrayList<VariableDefinition> variables = new ArrayList<VariableDefinition>();
        
        Element tmp = this;
        while(tmp!=null)
        {
            if(tmp.getClassname().equals("StartTimer")) break;
            //System.out.println("Look vor var inside: "+this.getClass().getSimpleName());
            if(tmp.getClassname().equals("VariableDefinition") ||
               tmp.getClassname().equals("For"))
            {
                variables.add(tmp.getVariableDefinition());
            }
            if(tmp.getPrev()!=null &&
               tmp.getPrev().getClassname().equals("For") &&
               tmp.getPrev()!=tmp.getParent())
                tmp=tmp.getPrev().getPrev();
            else
                tmp=tmp.getPrev();
        }
        
        return variables;
    }
    
    public boolean hasVariableWithName(String value)
    {
        ArrayList<VariableDefinition> vd = getVariables();
        //System.out.println(vd.size());
        for (int j = 0; j < vd.size(); j++) {
            VariableDefinition get = vd.get(j);
            //System.out.println(get.name);
            if(get.name.equals(value))
                return true;
        }
        return false;
    }
    
    public ArrayList<VariableDefinition> getVariablesInBlock()
    {
        //System.out.println("Look for var inside: "+this.getClassname());
        
        ArrayList<VariableDefinition> variables = new ArrayList<VariableDefinition>();
        
        if(getClassname().equals("VariableDefinition"))
        {
            variables.add(getVariableDefinition());
        }
        
        if(getBody()!=null)
            variables.addAll(getBody().getVariablesInBlock());
        
        if(getNext()!=null)
            variables.addAll(getNext().getVariablesInBlock());
        
        return variables;
    }
    
    public boolean hasVariableWithNameInBlock(String value)
    {
        ArrayList<VariableDefinition> vd = getVariablesInBlock();
        //System.out.println(vd.size());
        for (int j = 0; j < vd.size(); j++) {
            VariableDefinition get = vd.get(j);
            //System.out.println(get.name+ " vs: "+value);
            if(get.name.equals(value))
                return true;
        }
        return false;
    }
    
    public VariableDefinition getAttribute(String name)
    {
        ArrayList<VariableDefinition> attributes = getAttributes();

        for (int i = 0; i < attributes.size(); i++) {
            VariableDefinition get = attributes.get(i);

            if(get.name.equals(name))
                return get;
        }
        
        return null;
    }
    
    
    public ArrayList<VariableDefinition> getAttributes()
    {
        Element tme = getTopMostElement();
        if(tme!=null)
            return tme.getEditor().getAttributes();
        
        return new ArrayList<VariableDefinition>();
    }
    
    public ArrayList<VariableDefinition> getEntities()
    {
        ArrayList<VariableDefinition> result = new ArrayList<>();
        
        Element tme = getTopMostElement();
        if(tme!=null)
            result=tme.getEditor().getEntities();
        
        Element tmp = this;
        while(tmp!=null)
        {
            if(tmp.getClassname().equals("StartTimer")) break;
            //System.out.println("Look vor var inside: "+this.getClass().getSimpleName());
            if((tmp.getClassname().equals("VariableDefinition") ||
               tmp.getClassname().equals("For")) && Library.getInstance().getProject().getEntityNames().contains(tmp.getReturnType()))
            {
                result.add(tmp.getVariableDefinition());
            }
            if(tmp.getPrev()!=null &&
               tmp.getPrev().getClassname().equals("For") &&
               tmp.getPrev()!=tmp.getParent())
                tmp=tmp.getPrev().getPrev();
            else
                tmp=tmp.getPrev();
        }
        
        return result;
    }

    public ArrayList<VariableDefinition> getMethods()
    {
        ArrayList<VariableDefinition> result = new ArrayList<>();
        
        Element tme = getTopMostElement();
        if(tme!=null)
            result=tme.getEditor().getMethods();
        
        return result;
    }

    public boolean allowDockTo(Element destination, Pos pos)
    {
        if(pos==Pos.BODY) 
        {
            return (allowDockInside != null);
        }
        else
        {
            //System.out.println("allowDockAfter: "+allowDockAfter+" > "+allowDockAfter.size());
            if(allowDockAfter==null) return false;
            if(allowDockAfter.isEmpty()) return true;
            else return allowDockAfter.contains(destination.getClassname());
        }
    }

    // This method refreshed parameters based upon their type.
    // 
    //    ATTENTION
    //
    // Only use this for things that are always the same for
    // andy kind of element.
    private void refreshParameters(Change change)
    {
        // loop through all paramters of the current element
        for (int i = 0; i < parameters.size(); i++) 
        {
            Element parameter = parameters.get(i);
            
            // Type: VARIABLE
            if(parameter.getReturnType()!=null && parameter.getReturnType().equals("Variable"))
            {
                // variable has been renamed
                if(change.sender!=null &&
                   (change.sender.getClassname().equals("VariableDefinition") || change.sender.getClassname().equals("For")) &&
                   change.position==0 &&
                   //change.cmd.equals("rename.variable") &&
                   parameter.getTitle().equals(change.from.toString()))
                {
                    parameter.setTitle(change.to.toString());
                }
                // variable type has changed
                if(change.sender!=null &&
                   change.sender.getClassname().equals("VariableDefinition") &&
                   change.position==1 &&
                   //change.cmd.equals("rename.variable") &&
                   parameter.getTitle().equals(change.sender.getVariableDefinition().name))
                {
                    //parameter.setReturnType(change.to.toString());
                    setReturnType(change.to.toString());
                }
                // variable moved out of block
                if(change.sender!=null &&
                   (change.sender.getClassname().equals("VariableDefinition") ||
                    change.sender.getClassname().equals("SetVariable"))  &&
                   change.position==-1 &&
                   change.cmd.equals("dock") &&
                   !hasVariableWithName(parameter.getTitle()))
                {
                    parameter.setTitle("");
                }
            }
            // Type: ENTITY ATTRIBUTES
            /*
            else if(parameter.getReturnType()!=null && (
                    parameter.getReturnType().equals("EntityAttributes")
                    ))
            {
                if(change.sender!=null &&
                   change.sender.getClassname().equals("AttributeDefinition") &&
                   change.position==0 &&
                   change.cmd.equals("rename.attribute") &&
                   parameter.getTitle().equals(change.from.toString()))
                {
                    System.out.println("BloxName "+change.sender.getTopMostElement().getEditor().getBloxsClass().getName());
                    System.out.println("NameOfAtt "+parameter.getParent().getParameter(0).getTitle());
                    System.out.println("TypeOfAtt "+parameter.getParent().getParameter(2).getReturnType());
                }
            }
            */
            // Type: ATTRIBUTE & ENTITY LIST
            else if(parameter.getReturnType()!=null && (
                    parameter.getReturnType().equals("Attribute") ||
                    parameter.getReturnType().equals("EntityList")
                    ))
            {
                // attribute has been renamed
                if(change.sender!=null &&
                   change.sender.getClassname().equals("AttributeDefinition") &&
                   change.position==0 &&
                   change.cmd.equals("rename.attribute") &&
                   parameter.getTitle().equals(change.from.toString()))
                {
                    parameter.setTitle(change.to.toString());
                }
                // attribute type has changed
                if(change.sender!=null &&
                   change.sender.getClassname().equals("AttributeDefinition") &&
                   change.position==1 &&
                   //change.cmd.equals("rename.variable") &&
                   parameter.getTitle().equals(change.sender.getVariableDefinition().name))
                {
                    // erase the title
                    if(parameter.getReturnType().equals("EntityList"))
                    {
                        String old=parameter.getTitle();
                        parameter.setTitle("");
                        refresh(new Change(this, 0, "list", old, ""));
                    }
                    else
                    {
                        //parameter.setReturnType(change.to.toString());
                        setReturnType(change.to.toString());
                    }
                }
                // attribute type has deleted
                /*
                if(change.sender!=null &&
                   change.cmd.equals("delete.AttributeDefinition") &&
                   change.position==-1 &&
                   //change.cmd.equals("rename.variable") &&
                   parameter.getTitle().equals(change.sender.getVariableDefinition().name))
                {
                    if(parameter.getReturnType().equals("EntityList"))
                    {
                        String old = parameter.getTitle();
                        parameter.setTitle("");
                        refresh(new Change(this, parameter.getPosition(), "list", old, ""));
                    }
                    else
                    {
                        parameter.setTitle("");
                    }
                }/**/
            }
            // Type: WORLD
            else if(parameter.getReturnType()!=null && parameter.getReturnType().equals("World"))
            {
                if( change.sender==null &&
                    change.position==-1 &&
                    change.cmd.equals("delete.world") &&
                    parameter.getTitle().equals(change.from.toString()))
                {
                    parameter.setTitle("");
                }
                else if( change.sender==null &&
                    change.position==-1 &&
                    change.cmd.equals("rename.world") &&
                    parameter.getTitle().equals(change.from.toString()))
                {
                    parameter.setTitle(change.to.toString());
                }
            }
            // Type: ENTITY
            else if(parameter.getReturnType()!=null && parameter.getReturnType().equals("Entity"))
            {
                if( change.sender==null &&
                    change.position==-1 &&
                    change.cmd.equals("delete.entity") &&
                    parameter.getTitle().equals(change.from.toString()))
                {
                    parameter.setTitle("");
                }
                else if( change.sender==null &&
                    change.position==-1 &&
                    change.cmd.equals("rename.entity") &&
                    parameter.getTitle().equals(change.from.toString()))
                {
                    parameter.setTitle(change.to.toString());
                }
            }
            // Type: IMAGE
            else if(parameter.getReturnType()!=null && parameter.getReturnType().equals("Image"))
            {
                if( change.sender==null &&
                    change.position==-1 &&
                    change.cmd.equals("delete.image") &&
                    parameter.getTitle().equals(change.from.toString()))
                {
                    parameter.setTitle("");
                }
                else if( change.sender==null &&
                    change.position==-1 &&
                    change.cmd.equals("rename.image") &&
                    parameter.getTitle().equals(change.from.toString()))
                {
                    parameter.setTitle(change.to.toString());
                }
            }
            // Type: SOUND
            else if(parameter.getReturnType()!=null && parameter.getReturnType().equals("Sound"))
            {
                if( change.sender==null &&
                    change.position==-1 &&
                    change.cmd.equals("delete.sound") &&
                    parameter.getTitle().equals(change.from.toString()))
                {
                    parameter.setTitle("");
                }
                else if( change.sender==null &&
                    change.position==-1 &&
                    change.cmd.equals("rename.sound") &&
                    parameter.getTitle().equals(change.from.toString()))
                {
                    parameter.setTitle(change.to.toString());
                }
            }
            /*
            // Type: METHODLIST
            else if(parameter.getReturnType()!=null && parameter.getReturnType().equals("MethodList"))
            {
                if( change.sender!=null &&
                    change.position==-1 &&
                    change.cmd.equals("delete.MethodDefinition") &&
                    parameter.getTitle().equals(change.sender.getParameter(0).getTitle()))
                {
                    parameter.setTitle("");
                }
                else if( change.sender!=null &&
                    change.position==0 &&
                    change.cmd.equals("rename.method") &&
                    parameter.getTitle().equals(change.from.toString()))
                {
                    parameter.setTitle(change.to.toString());
                }
            }
            */
        }
    }
    
    private void refreshElements(Change change)
    {
        if(this.getClassname().startsWith("Method") || this.getClassname().startsWith("ObjectMethod") || this.getClassname().endsWith("Method"))
            refreshHandleMethod(change);
        else if(this.getClassname().startsWith("Object"))
            refreshHandleObject(change);
        else if(this.getClassname().startsWith("Variable")  || this.getClassname().endsWith("Variable"))
            refreshHandleVariable(change);
        else if(this.getClassname().startsWith("Attribute") || this.getClassname().endsWith("Attribute"))
            refreshHandleAttribute(change);
        else if(this.getClassname().endsWith("Entity"))
            refreshHandleEntity(change);
    }
    
    private void refreshHandleEntity(Change change)
    {
        // elements to be updated
        // - AddEntity
        //      if the selected entity type ($0) has changed
        //      the return type has to be updated accordingly
        if((this.getClassname().equals("AddEntity")) &&
                change.sender!=null &&
                change.position==0 &&
                change.sender==this
           )
        {
            // set the type
            setReturnType(change.to.toString());
            // empty selection if type mismatch
            if(getParent()!=null &&
               !typeCanAcceptType(getParent().getReturnType(), change.to.toString()))
            {
                getParameter(0).setTitle("");
                setReturnType("");
            }
        }    
    }
    
    private void refreshHandleObject(Change change)
    {
        // elements to be updated
        // 1- ObjectSetAttribute
        //      if the name of the selected object ($0) has been changed
        //      the names of the attributes ($1) have to be reloaded
        //      and the type of the holder ($2) has to be reset
        if(
                this.getClassname().equals("ObjectSetAttribute") &&
                !change.cmd.equals("dock") &&
                change.position == 0 &&
                change.sender==this
           )
        {
            // if the title selection is empty
            if(getParameter(0).getTitle().isEmpty())
            {
                String old = getParameter(1).getTitle();
                // empty the name of the selected attribute too
                getParameter(1).setTitle("");
                // pass on the change
                refresh(new Change(this, 1, change.cmd, old, ""));
            }
            else
            {
                // get list of all entity
                ArrayList<VariableDefinition> entities = getEntities();
                for (int i = 0; i < entities.size(); i++) {
                    VariableDefinition vd = entities.get(i);
                    // find the right one
                    if(vd.name.equals(change.to.toString()))
                    {
                        // get reference to the loaded project
                        Project project = Library.getInstance().getProject();
                        // stop if null or not set
                        if(project==null) return;
                        // get the selected entity
                        Entity entity = project.getEntity(vd.classname);
                        // stop if not found
                        if(entity==null) return;  
                        // stop if class has no editor
                        if(entity.getEditor()==null) return;
                        // retrieve list of variables
                        ArrayList<String> attributeNames = entity.getEditor().getAttributeNames();
                        // update the second parameter
                        ((List)parameters.get(1)).update(attributeNames);
                        // reset the attribute name
                        String old = getParameter(1).getTitle();
                        parameters.get(1).setTitle("");
                        // pass on the change
                        refresh(new Change(this, 1, change.cmd, old, ""));
                        // reset the parameter
                        parameters.get(2).setReturnType("");
                    }
                }
            }
        }
        // 2- ObjectSetAttribute
        //      if the attribute of an object ($1) has been changed
        //      the type of the holder ($2) has to be updated
        else if(
                this.getClassname().equals("ObjectSetAttribute") &&
                !change.cmd.equals("dock") &&
                change.position == 1 &&
                change.sender==this
           )
        {
            // if the selected object it empty, the second one should be empied too
            if(getParameter(0).getTitle().isEmpty())
            {
                String old = getParameter(1).getTitle();
                // empty the attribute name
                getParameter(1).setTitle("");
                // reset parameter type
                getParameter(2).setReturnType("");
            }
            else
            {
                ArrayList<VariableDefinition> entities = getEntities();
                boolean found = false;
                for (int i = 0; i < entities.size(); i++) 
                {
                    VariableDefinition vd = entities.get(i);
                    if(vd.name.equals(parameters.get(0).getTitle()))
                    {
                        found=true;
                        // get reference to the loaded project
                        Project project = Library.getInstance().getProject();
                        // stop if null or not set
                        if(project==null) return;
                        // get the selected entity
                        Entity entity = project.getEntity(vd.classname);
                        // stop if not found
                        if(entity==null) return;  
                        // stop if class has no editor
                        if(entity.getEditor()==null) return;
                        // retrieve list of variables
                        ArrayList<VariableDefinition> attributeNames = entity.getEditor().getAttributes();
                        for (int j = 0; j < attributeNames.size(); j++) {
                            // find the one where the name matches
                            VariableDefinition vdi = attributeNames.get(j);
                            if(vdi.name.equals(change.to.toString()))
                            {
                                if(!typeCanAcceptType(vdi.type, parameters.get(2).getReturnType()))
                                {
                                    setParameter(2, new Element(Type.EXPRESSION,"ExpressionHolder",vdi.type));
                                }
                                if(vdi.type.equals("boolean"))
                                {
                                    // update title
                                    title = title.substring(0,title.length()-1);
                                    title+="£";
                                    // set new parameter
                                    if(!parameters.get(2).getClassname().equals("ConditionHolder"))
                                        setParameter(2, new Element(Type.CONDITION,"ConditionHolder","boolean"));
                                    parameters.get(2).setReturnType(vdi.type);
                                }
                                else
                                {
                                    // update title
                                    title = title.substring(0,title.length()-1);
                                    title+="$";
                                    // set new parameter
                                    if(!parameters.get(2).getClassname().equals("ExpressionHolder"))
                                        setParameter(2, new Element(Type.EXPRESSION,"ExpressionHolder",vdi.type));
                                    parameters.get(2).setReturnType(vdi.type);
                                }
                                // modify parameter type
                                while(paramTypes.size()<3) paramTypes.add("");
                                // JOS
                                paramTypes.set(2, vdi.type);
                            }
                        }
                    }
                }
                
                // entity is not within reach
                if(!found)
                {
                    String old = getParameter(0).getTitle();
                    getParameter(0).setTitle("");
                    refresh(new Change(this, 1, change.cmd, old, ""));
                }
            }
        }
        // 3- ObjectGetAttribute
        //      if the name of the selected object ($0) has been changed
        //      the names of the attributes ($1) have to be reloaded
        //      and the *return type* has to be reset
        else if(
                this.getClassname().equals("ObjectGetAttribute") &&
                !change.cmd.equals("dock") &&
                change.position == 0 &&
                change.sender==this
           )
        {
            // if the title selection is empty
            if(getParameter(0).getTitle().isEmpty())
            {
                String old = getParameter(1).getTitle();
                // empty the name of the selected attribute too
                getParameter(1).setTitle("");
                // pass on the change
                refresh(new Change(this, 1, change.cmd, old, ""));
            }
            else
            {
                // get list of all entity
                ArrayList<VariableDefinition> entities = getEntities();
                for (int i = 0; i < entities.size(); i++) {
                    VariableDefinition vd = entities.get(i);
                    // find the right one
                    if(vd.name.equals(change.to.toString()))
                    {
                        // get reference to the loaded project
                        Project project = Library.getInstance().getProject();
                        // stop if null or not set
                        if(project==null) return;
                        // get the selected entity
                        Entity entity = project.getEntity(vd.classname);
                        // stop if not found
                        if(entity==null) return;  
                        // stop if class has no editor
                        if(entity.getEditor()==null) return;
                        // retrieve list of variables
                        ArrayList<String> attributeNames = entity.getEditor().getAttributeNames();
                        // update the second parameter
                        ((List)parameters.get(1)).update(attributeNames);
                        // reset the attribute name
                        String old = getParameter(1).getTitle();
                        parameters.get(1).setTitle("");
                        // pass on the change
                        refresh(new Change(this, 1, change.cmd, old, ""));
                        // set return type
                        setReturnType("");
                    }
                }
            }
        }
        // 4- ObjectGetAttribute
        //      if the attribute of an object ($1) has been changed
        //      the *return type* has to be updated
        else if(
                this.getClassname().equals("ObjectGetAttribute") &&
                !change.cmd.equals("dock") &&
                change.position == 1 &&
                change.sender==this
           )
        {
            // if the selected object it empty, the second one should be empied too
            if(getParameter(0).getTitle().isEmpty())
            {
                String old = getParameter(1).getTitle();
                // empty the attribute name
                getParameter(1).setTitle("");
                // reset the return type
                setReturnType("");
            }
            else
            {
                ArrayList<VariableDefinition> entities = getEntities();
                boolean found = false;
                for (int i = 0; i < entities.size(); i++) 
                {
                    VariableDefinition vd = entities.get(i);
                    if(vd.name.equals(parameters.get(0).getTitle()))
                    {
                        found = true;
                        // get reference to the loaded project
                        Project project = Library.getInstance().getProject();
                        // stop if null or not set
                        if(project==null) return;
                        // get the selected entity
                        Entity entity = project.getEntity(vd.classname);
                        // stop if not found
                        if(entity==null) return;  
                        // stop if class has no editor
                        if(entity.getEditor()==null) return;
                        // retrieve list of variables
                        ArrayList<VariableDefinition> attributeNames = entity.getEditor().getAttributes();
                        for (int j = 0; j < attributeNames.size(); j++) {
                            // find the one where the name matches
                            VariableDefinition vdi = attributeNames.get(j);
                            if(vdi.name.equals(change.to.toString()))
                            {
                                // set the return type
                                setReturnType(vdi.type);
                                // reset if type is not allowed
                                if(getParent()!=null &&
                                   !typeCanAcceptType(getParent().getReturnType(), vdi.type))
                                {
                                    // reset title
                                    getParameter(1).setTitle("");
                                    // reset return type
                                    setReturnType("");
                                }
                            }
                        }
                    }
                }
                // entity is not within reach
                if(!found)
                {
                    String old = getParameter(0).getTitle();
                    getParameter(0).setTitle("");
                    refresh(new Change(this, 1, change.cmd, old, ""));
                }
            }
        }
        // 5- ObjectSetAttribute || ObjectGetAttribute
        //      if the element has been undocked,
        //      check if entity is still reachable
        else if(
                (this.getClassname().equals("ObjectSetAttribute") || this.getClassname().equals("ObjectGetAttribute")) &&
                change.sender == this &&
                change.cmd.equals("undock")
           )
        {
            ArrayList<VariableDefinition> entities = getEntities();
            boolean found = false;
            for (int i = 0; i < entities.size(); i++) 
            {
                VariableDefinition vd = entities.get(i);
                if(vd.name.equals(parameters.get(0).getTitle()))
                {
                    found = true;
                }
            }
            // entity is not within reach
            if(!found)
            {
                String old = getParameter(0).getTitle();
                getParameter(0).setTitle("");
                refresh(new Change(this, 0, "list", old, ""));
            }
        }
        // 6- ObjectSetAttribute || ObjectGetAttribute
        //      if the corresponding AttributeDefinition has been deleted
        //      the selected object ($0) has to be emptied
        //      the selected attribute ($1) has to be emptied
        //      and the holder ($2) has to be reset
        else if(
                (this.getClassname().equals("ObjectSetAttribute") || this.getClassname().equals("ObjectGetAttribute")) &&
                (change.cmd.equals("delete.AttributeDefinition") || change.cmd.equals("delete.VariableDefinition")) &&
                change.sender.getTopMostElement().getEditor().equals(getTopMostElement().getEditor()) // same class
           )
        {
            // case  #1: the definition of an attribute or variable of this class has been deleted
            //           --> the name of the deleted element == the selected name ($0)
            
            String old = getParameter(0).getTitle();
            // same class & same name
            if(old.equals(change.sender.getParameter(0).getTitle()))
            {
                // empty the name in the list
                getParameter(0).setTitle("");
                // pass on the refresh
                refresh(new Change(this, 0, "list", old, ""));
            }
        }
        else if(
                (this.getClassname().equals("ObjectSetAttribute") || this.getClassname().equals("ObjectGetAttribute")) &&
                (change.cmd.equals("delete.AttributeDefinition") || change.cmd.equals("delete.VariableDefinition")) &&
                !change.sender.getTopMostElement().getEditor().equals(getTopMostElement().getEditor()) // different class
           )
        {
            // case  #2: the definition of an attribute or variable of another class has been deleted
            //           --> the name of the deleted element == the selected attribute name ($1)
            
            // get the old title
            String old = getParameter(1).getTitle();

            // check that the first name ($0) matches an entity of this class
            ArrayList<VariableDefinition> attributes = getEntities();
            for (int i = 0; i < attributes.size(); i++) 
            {
                VariableDefinition vd = attributes.get(i);
                if(vd.name.equals(getParameter(0).getTitle()) &&
                   getParameter(1).getTitle().equals(change.sender.getParameter(0).getTitle()))
                {
                    // empty the title
                    getParameter(1).setTitle("");
                    // pass the change on (update the attribute)
                    refresh(new Change(this, 1, "list", old, ""));
                    // update list as well (this updated the list of attribute names)
                    refresh(new Change(this, 0, "list", getParameter(0).getTitle(), getParameter(0).getTitle()));
                }

                // if the type fits, we need to update the second parameter (list - $1)
                if(getParameter(0).getTitle().equals(vd.name) &&
                   vd.type.equals(change.sender.getTopMostElement().getEditor().getBloxsClass().getName()))
                {
                    System.out.println("hello");
                    // get reference to the loaded project
                    Project project = Library.getInstance().getProject();
                    // stop if null or not set
                    if(project==null) return;
                    // get the selected entity
                    Entity entity = project.getEntity(vd.classname);
                    // stop if not found
                    if(entity==null) return;  
                    // stop if class has no editor
                    if(entity.getEditor()==null) return;
                    // retrieve list of variables
                    ArrayList<String> attributeNames = entity.getEditor().getAttributeNames();
                    // update the second parameter
                    ((List)parameters.get(1)).update(attributeNames);                    
                }
            }
        }
        // 7- ObjectSetAttribute || ObjectGetAttribute
        //      if the corresponding *entity* has been renamed
        //      the name of the object($1) has to be changed accordingly
        else if(
                (this.getClassname().equals("ObjectSetAttribute") || this.getClassname().equals("ObjectGetAttribute")) &&
                change.sender !=null &&
                change.sender.getClassname().equals("AttributeDefinition") &&
                change.cmd.equals("rename.attribute") &&
                !change.sender.getTopMostElement().getEditor().equals(getTopMostElement().getEditor()) // different class
           )
        {
            // get all entities
            ArrayList<VariableDefinition> vds = getEntities();
            // check if this one fits
            for (int i = 0; i < vds.size(); i++) 
            {
                // get the actual definition
                VariableDefinition vd = vds.get(i);
                // does it fit?
                if(getParameter(1).getTitle().equals(change.from.toString()) &&
                   getParameter(0).getTitle().equals(vd.name) &&
                   vd.type.equals(change.sender.getTopMostElement().getEditor().getBloxsClass().getName()))
                {
                    // pass on the refresh to update the list
                    refresh(new Change(this, 0, "list", getParameter(0).getTitle(), getParameter(0).getTitle()));
                    // update the parameter
                    getParameter(1).setTitle(change.to.toString());
                    // pass on the refresh to update the type
                    refresh(new Change(this, 1, "list", change.from, change.to));
                }
                
                // if the type fits, we need to update the second parameter (list - $1)
                if(getParameter(0).getTitle().equals(vd.name) &&
                   vd.type.equals(change.sender.getTopMostElement().getEditor().getBloxsClass().getName()))
                {
                    // get reference to the loaded project
                    Project project = Library.getInstance().getProject();
                    // stop if null or not set
                    if(project==null) return;
                    // get the selected entity
                    Entity entity = project.getEntity(vd.classname);
                    // stop if not found
                    if(entity==null) return;  
                    // stop if class has no editor
                    if(entity.getEditor()==null) return;
                    // retrieve list of variables
                    ArrayList<String> attributeNames = entity.getEditor().getAttributeNames();
                    // update the second parameter
                    ((List)parameters.get(1)).update(attributeNames);                    
                }
            }
            
        }
        // 8- ObjectSetAttribute || ObjectGetAttribute
        //      if the corresponding AttributeDefinition has been renamed
        //      the name of the selected attribute ($1) has to be changed accordingly
        else if(
                (this.getClassname().equals("ObjectSetAttribute") || this.getClassname().equals("ObjectGetAttribute")) &&
                change.sender !=null &&
                change.sender.getClassname().equals("AttributeDefinition") &&
                change.cmd.equals("rename.attribute") &&
                change.sender.getTopMostElement().getEditor().equals(getTopMostElement().getEditor()) // same class
           )
        {
            if(getParameter(0).getTitle().equals(change.from.toString()))
            {
                getParameter(0).setTitle(change.to.toString());
            }
        }
        // 9- ObjectSetAttribute 
        //      if the corresponding AttributeDefinition changed it's type
        //      the type of the holder ($2) has to be updated
        else if(
                this.getClassname().equals("ObjectSetAttribute") &&
                change.position == 1 &&
                change.sender.getClassname().equals("AttributeDefinition")
           )
        {
            // get all entities
            ArrayList<VariableDefinition> vds = getEntities();
            // check if this one fits
            for (int i = 0; i < vds.size(); i++) 
            {
                // get the actual definition
                VariableDefinition vd = vds.get(i);
                // does it fit?
                if(getParameter(1).getTitle().equals(change.sender.getParameter(0).getTitle()) &&
                   getParameter(0).getTitle().equals(vd.name) &&
                   vd.type.equals(change.sender.getTopMostElement().getEditor().getBloxsClass().getName()))
                {
                    // discard content if not type compatible
                    if(parameters.get(2).getBody()!=null &&
                       !typeCanAcceptType(change.to.toString(),parameters.get(2).getBody().getReturnType()))
                    {
                        parameters.get(2).setBody(null);
                    }
                    // case of a "boolean"
                    if(change.to.toString().equals("boolean"))
                    {
                        // update title
                        title = title.substring(0,title.length()-1);
                        title+="£";
                        // set new parameter
                        if(!parameters.get(2).getClassname().equals("ConditionHolder"))
                            setParameter(2, new Element(Type.CONDITION,"ConditionHolder","boolean"));
                    }
                    else
                    {
                        // update title
                        title = title.substring(0,title.length()-1);
                        title+="$";
                        // set new parameter
                        if(!parameters.get(2).getClassname().equals("ExpressionHolder"))
                            setParameter(2, new Element(Type.EXPRESSION,"ExpressionHolder",change.to.toString()));
                    }
                    // change the return type accordingly
                    parameters.get(2).setReturnType(change.to.toString());
                }
            }
        }
        // 10- ObjectGetAttribute 
        //      if the corresponding AttributeDefinition changed it's type
        //      the *return type* has to be updated
        else if(
                this.getClassname().equals("ObjectGetAttribute") &&
                change.position == 1 &&
                change.sender.getClassname().equals("AttributeDefinition")
           )
        {
            // get all entities
            ArrayList<VariableDefinition> vds = getEntities();
            // check if this one fits
            for (int i = 0; i < vds.size(); i++) 
            {
                // get the actual definition
                VariableDefinition vd = vds.get(i);
                // does it fit?
                if(getParameter(1).getTitle().equals(change.sender.getParameter(0).getTitle()) &&
                   getParameter(0).getTitle().equals(vd.name) &&
                   vd.type.equals(change.sender.getTopMostElement().getEditor().getBloxsClass().getName()))
                {
                    // change type
                    setReturnType(change.to.toString());
                    // test if the holder accepts this type
                    if(getParent()!=null &&
                       !typeCanAcceptType(getParent().getReturnType(), getReturnType()))
                    {
                        // clean title of attibute
                        String old = getParameter(1).getTitle();
                        getParameter(1).setTitle("");
                        // reset the return type
                        setReturnType(""); 
                        // pass the change
                        refresh(new Change(this, 1, "list", old, ""));
                    }
                }
            }
        }
    }   
    
    private void refreshHandleVariable(Change change)
    {
        // Elements to be updated
        // 1- VariableDefinition
        //      if an entity is being renamed, the type ($1) has to be changed (if selected)
        if(
                this.getClassname().equals("VariableDefinition") && 
                change.cmd.equals("rename.entity")
           )
        {
            // test if the type ($1) is the same than the old value of the entity
            if(getParameter(1).getTitle().equals(change.from.toString()))
            {
                // set it ($1) to the new one
                getParameter(1).setTitle(change.to.toString());
            }
        }
        // 2- VariableDefinition
        //      change the type of the holder ($2) if the type ($1) has been changed
        else if(
                this.getClassname().equals("VariableDefinition") &&
                change.sender == this &&
                !change.cmd.equals("dock")
           )
        {
            // if the actual content if the holder ($2) is not comptaible with the newly
            // selcted type, a new holder has to be created, discarding the old content
            if(parameters.get(2).getBody()!=null &&
               !typeCanAcceptType(parameters.get(1).getTitle(), parameters.get(2).getBody().getReturnType()))
            {
                setParameter(2, new Element(Type.EXPRESSION,"ExpressionHolder",change.to.toString()));
            }
            // switch between "boolean" type and others
            if(change.to.equals("boolean"))
            {
                // update title
                title = title.substring(0,title.length()-1);
                title+="£";
                // set new parameter
                if(!parameters.get(2).getClassname().equals("ConditionHolder"))
                    setParameter(2, new Element(Type.CONDITION,"ConditionHolder","boolean"));
                parameters.get(2).setReturnType(change.to.toString());
            }
            else
            {
                // update title
                title = title.substring(0,title.length()-1);
                title+="$";
                // set new parameter
                if(!parameters.get(2).getClassname().equals("ExpressionHolder"))
                    setParameter(2, new Element(Type.EXPRESSION,"ExpressionHolder",change.to.toString()));
                parameters.get(2).setReturnType(change.to.toString());
            }
            // make shure enough types have been defined
            while(paramTypes.size()<3) paramTypes.add("");
            // modify parameter type
            paramTypes.set(2, change.to.toString());
            // modify type of parameter
            parameters.get(2).setReturnType(change.to.toString());
            // update my own return type
            setReturnType(change.to.toString());
        }
        // 3- Variable
        //      change the return type if another variable has been selected
        else if( 
                this.getClassname().equals("Variable") &&
                change.sender==this
           )
        {
            // get the names of all variables at this place
            ArrayList<VariableDefinition> variables = getVariables();
            // find the one with the same name
            for (int i = 0; i < variables.size(); i++) {
                VariableDefinition vd = variables.get(i);
                // if found
                if(vd.name.equals(change.to.toString()))
                {
                    // change the return type accordingly
                    setReturnType(vd.type);
                    // test if the holder accepts this type
                    if(getParent()!=null &&
                       !typeCanAcceptType(getParent().getReturnType(), getReturnType()))
                    {
                        // clean title of parameter
                        getParameter(0).setTitle("");
                        // reset the return type
                        setReturnType(""); 
                    }
                }
            }
        }
        // 4- Variable
        //      change the *return type* if the corresponding definition has changed it's type ($1)
        else if( 
                this.getClassname().equals("Variable") &&
                change.sender!=null &&
                change.position==1 &&   // $1 = type
                change.sender.getClassname().equals("VariableDefinition")
           )
        {
            // only apply change if the name of the variable is the same
            if(parameters.get(0).getTitle().equals(change.sender.getVariableDefinition().name))
            {
                // change the type
                if(change.to.equals("boolean"))
                    setType(Type.CONDITION);
                else
                    setType(Type.EXPRESSION);
                // set the return type
                setReturnType(change.to.toString());    
                // clean (deselect) if type mismatches
                if(getParent()!=null &&
                   !typeCanAcceptType(getParent().getReturnType(), getReturnType()))
                {
                    // clean title of parameter
                    getParameter(0).setTitle("");
                    // reset the return type
                    setReturnType("");
                }
            }
        }
        // 5- Variable
        //      change the name ($0) if the corresponding definition has changed it's name ($0)
        else if( 
                this.getClassname().equals("Variable") &&
                change.sender!=null &&
                change.position==0 &&   // $0 = name
                change.sender.getClassname().equals("VariableDefinition")
           )
        {
            if(parameters.get(0).getTitle().equals(change.from.toString()))
            {
                parameters.get(0).setTitle(change.to.toString());
            }
        }
        // 6- SetVariable || VariableIncrement || VariableDecrement
        //      change return type of holder ($1) when variable selection ($0) changes
        else if(
                (this.getClassname().equals("SetVariable") || this.getClassname().equals("VariableIncrement") || this.getClassname().equals("VariableDecrement"))&&
                change.sender == this &&
                !change.cmd.equals("dock") &&
                change.position==0 
           )
        {
            // get the names of all variables at this place
            ArrayList<VariableDefinition> variables = getVariables();
            // find the one with the same name
            for (int i = 0; i < variables.size(); i++) {
                VariableDefinition vd = variables.get(i);
                // if found
                if(vd.name.equals(change.to.toString()))
                {
                    // disacrd content if not type compatible
                    if(parameters.get(1).getBody()!=null &&
                       !typeCanAcceptType(vd.type,parameters.get(1).getBody().getReturnType()))
                    {
                        parameters.get(1).setBody(null);
                    }
                    // case of a "boolean"
                    if(vd.type.equals("boolean"))
                    {
                        // update title
                        title = title.substring(0,title.length()-1);
                        title+="£";
                        // set new parameter
                        if(!parameters.get(1).getClassname().equals("ConditionHolder"))
                            setParameter(1, new Element(Type.CONDITION,"ConditionHolder","boolean"));
                    }
                    else
                    {
                        // update title
                        title = title.substring(0,title.length()-1);
                        title+="$";
                        // set new parameter
                        if(!parameters.get(1).getClassname().equals("ExpressionHolder"))
                            setParameter(1, new Element(Type.EXPRESSION,"ExpressionHolder",change.to.toString()));
                    }
                    // change the return type accordingly
                    parameters.get(1).setReturnType(vd.type);
                }
            }
        }
        // 7- SetVariable || VariableIncrement || VariableDecrement
        //      change the type of the holder ($2) if the corresponding definition has changed it's type ($1)
        else if(
                (this.getClassname().equals("SetVariable") || this.getClassname().equals("VariableIncrement") || this.getClassname().equals("VariableDecrement"))&&
                change.sender!=null &&
                change.position==1 &&   // $1 = type
                change.sender.getClassname().equals("VariableDefinition")
           )
        {
            // only change the usage with the same variable name
            if(getParameter(0).getTitle().equals(change.sender.getParameter(0).getTitle()))
            {
                // if the actual content if the holder ($2) is not comptaible with the newly
                // selcted type, a new holder has to be created, discarding the old content
                if(parameters.get(1).getBody()!=null &&
                   !typeCanAcceptType(change.to.toString(), parameters.get(1).getBody().getReturnType()))
                {
                    setParameter(1, new Element(Type.EXPRESSION,"ExpressionHolder",change.to.toString()));
                }
                // case of a "boolean"
                if(change.to.equals("boolean"))
                {
                    // update title
                    title = title.substring(0,title.length()-1);
                    title+="£";
                    // set new parameter
                    if(!parameters.get(1).getClassname().equals("ConditionHolder"))
                        setParameter(1, new Element(Type.CONDITION,"ConditionHolder","boolean"));
                    parameters.get(1).setReturnType(change.to.toString());
                }
                else
                {
                    // update title
                    title = title.substring(0,title.length()-1);
                    title+="$";
                    // set new parameter
                    if(!parameters.get(1).getClassname().equals("ExpressionHolder"))
                        setParameter(1, new Element(Type.EXPRESSION,"ExpressionHolder",change.to.toString()));
                    parameters.get(1).setReturnType(change.to.toString());
                }

                // make shure enough types have been defined
                while(paramTypes.size()<2) paramTypes.add("");
                // modify parameter type
                paramTypes.set(1, change.to.toString());
                // modify type of parameter
                parameters.get(1).setReturnType(change.to.toString());
            }
        }
        // 8- SetVariable || VariableIncrement || VariableDecrement
        //      change the name ($0) if the corresponding definition has changed it's name ($0)
        else if(
                (this.getClassname().equals("SetVariable") || this.getClassname().equals("VariableIncrement") || this.getClassname().equals("VariableDecrement"))&&
                change.sender!=null &&
                change.position==0 &&   // $0 = name
                change.sender.getClassname().equals("VariableDefinition")
           )
        {
            // only change the usage with the same variable name
            if(getParameter(0).getTitle().equals(change.from.toString()))
            {
                getParameter(0).setTitle(change.to.toString());
            }
        }
    }
    
    private void refreshHandleAttribute(Change change)
    {
        // Elements to be updates
        // 1- AttributeDefinition
        //      if an entity is being renamed, the type ($1) has to be changed (if selected)
        if(
                this.getClassname().equals("AttributeDefinition") && 
                change.cmd.equals("rename.entity") && 
                change.sender.getTopMostElement().getEditor().equals(getTopMostElement().getEditor()) // same class
           )
        {
            // test if the type ($1) is the same than the old value of the entity
            if(getParameter(1).getTitle().equals(change.from.toString()))
            {
                // set it ($1) to the new one
                getParameter(1).setTitle(change.to.toString());
            }
        }
        // 2- AttributeDefinition
        //      change the type of the holder ($2) if the type ($1) has been changed
	else if(
                this.getClassname().equals("AttributeDefinition") &&
                change.sender == this &&
                change.position == 1 && // type
                !change.cmd.equals("dock")
           )
        {
            // if the actual content if the holder ($2) is not comptaible with the newly
            // selcted type, a new holder has to be created, discarding the old content
            if(parameters.get(2).getBody()!=null &&
               !typeCanAcceptType(parameters.get(1).getTitle(), parameters.get(2).getBody().getReturnType()))
            {
                setParameter(2, new Element(Type.EXPRESSION,"ExpressionHolder",change.to.toString()));
            }
            // switch between "boolean" type and others
            if(change.to.equals("boolean"))
            {
                // update title
                title = title.substring(0,title.length()-1);
                title+="£";
                // set new parameter
                if(!parameters.get(2).getClassname().equals("ConditionHolder"))
                    setParameter(2, new Element(Type.CONDITION,"ConditionHolder","boolean"));
                parameters.get(2).setReturnType(change.to.toString());
            }
            else
            {
                // update title
                title = title.substring(0,title.length()-1);
                title+="$";
                // set new parameter
                if(!parameters.get(2).getClassname().equals("ExpressionHolder"))
                    setParameter(2, new Element(Type.EXPRESSION,"ExpressionHolder",change.to.toString()));
                parameters.get(2).setReturnType(change.to.toString());
            }
            // make shure enough types have been defined
            while(paramTypes.size()<3) paramTypes.add("");
            // modify parameter type
            paramTypes.set(2, change.to.toString());
            // modify type of parameter
            parameters.get(2).setReturnType(change.to.toString());
            // update my own return type
            setReturnType(change.to.toString());
        }
        // 3- Attribute
        //      change the return type if another attribute has been selected
        else if( 
                this.getClassname().equals("Attribute") &&
                change.sender==this
           )
        {
            // get the names of all attributes at this place
            ArrayList<VariableDefinition> attributes = getAttributes();
            // find the one with the same name
            for (int i = 0; i < attributes.size(); i++) {
                VariableDefinition vd = attributes.get(i);
                // if found
                if(vd.name.equals(change.to.toString()))
                {
                    // change the return type accordingly
                    setReturnType(vd.type);
                    // test if the holder accepts this type
                    if(getParent()!=null &&
                       !typeCanAcceptType(getParent().getReturnType(), getReturnType()))
                    {
                        // clean title of parameter
                        getParameter(0).setTitle("");
                        // reset the return type
                        setReturnType(""); 
                    }
                }
            }
        }
        // 4- Attribute
        //      change the *return type* if the corresponding definition has changed it's type ($1)
        else if( 
                this.getClassname().equals("Attribute") &&
                change.sender!=null &&
                change.position==1 &&   // $1 = type
                change.sender.getClassname().equals("AttributeDefinition") && 
                change.sender.getTopMostElement().getEditor().equals(getTopMostElement().getEditor()) // same class
           )
        {
            // only apply change if the name of the attribute is the same
            if(parameters.get(0).getTitle().equals(change.sender.getVariableDefinition().name))
            {
                // change the type
                if(change.to.equals("boolean"))
                    setType(Type.CONDITION);
                else
                    setType(Type.EXPRESSION);
                // set the return type
                setReturnType(change.to.toString());    
                // clean (deselect) if type mismatches
                if(getParent()!=null &&
                   !typeCanAcceptType(getParent().getReturnType(), getReturnType()))
                {
                    // clean title of parameter
                    getParameter(0).setTitle("");
                    // reset the return type
                    setReturnType("");
                }
            }
        }
        // 5- Attribute
        //      change the name ($0) if the corresponding definition has changed it's name ($0)
        else if( 
                this.getClassname().equals("Attribute") &&
                change.sender!=null &&
                change.position==0 &&   // $0 = name
                change.sender.getClassname().equals("AttributeDefinition")
           )
        {
            if(parameters.get(0).getTitle().equals(change.from.toString()))
            {
                parameters.get(0).setTitle(change.to.toString());
            }
        }
        // 6- SetAttribute || AttributeIncrement || AttributeDecrement
        //      change return type of holder ($1) when variable selection ($0) changes
        else if(
                (this.getClassname().equals("SetAttribute") || this.getClassname().equals("AttributeIncrement") || this.getClassname().equals("AttributeDecrement"))&&
                change.sender == this &&
                !change.cmd.equals("dock") &&
                change.position==0 
           )
        {
            // get the names of all attribute at this place
            ArrayList<VariableDefinition> attributes = getAttributes();
            // find the one with the same name
            for (int i = 0; i < attributes.size(); i++) {
                VariableDefinition vd = attributes.get(i);
                // if found
                if(vd.name.equals(change.to.toString()))
                {
                    // disacrd content if not type compatible
                    if(parameters.get(1).getBody()!=null &&
                       !typeCanAcceptType(vd.type,parameters.get(1).getBody().getReturnType()))
                    {
                        parameters.get(1).setBody(null);
                    }
                    // case of a "boolean"
                    if(vd.type.equals("boolean"))
                    {
                        // update title
                        title = title.substring(0,title.length()-1);
                        title+="£";
                        // set new parameter
                        if(!parameters.get(1).getClassname().equals("ConditionHolder"))
                            setParameter(1, new Element(Type.CONDITION,"ConditionHolder","boolean"));
                    }
                    else
                    {
                        // update title
                        title = title.substring(0,title.length()-1);
                        title+="$";
                        // set new parameter
                        if(!parameters.get(1).getClassname().equals("ExpressionHolder"))
                            setParameter(1, new Element(Type.EXPRESSION,"ExpressionHolder",change.to.toString()));
                    }
                    // change the return type accordingly
                    parameters.get(1).setReturnType(vd.type);
                }
            }
        }
        // 7- SetAttribute || AttributeIncrement || AttributeDecrement
        //      change the type of the holder ($2) if the corresponding definition has changed it's type ($1)
        else if(
                (this.getClassname().equals("SetAttribute") || this.getClassname().equals("AttributeIncrement") || this.getClassname().equals("AttributeDecrement"))&&
                change.sender!=null &&
                change.position==1 &&   // $1 = type
                change.sender.getClassname().equals("AttributeDefinition")
           )
        {
            // only change the usage with the same attribute name
            if(getParameter(0).getTitle().equals(change.sender.getParameter(0).getTitle()))
            {
                // if the actual content if the holder ($2) is not comptaible with the newly
                // selcted type, a new holder has to be created, discarding the old content
                if(parameters.get(1).getBody()!=null &&
                   !typeCanAcceptType(change.to.toString(), parameters.get(1).getBody().getReturnType()))
                {
                    setParameter(1, new Element(Type.EXPRESSION,"ExpressionHolder",change.to.toString()));
                }
                // case of a "boolean"
                if(change.to.equals("boolean"))
                {
                    // update title
                    title = title.substring(0,title.length()-1);
                    title+="£";
                    // set new parameter
                    if(!parameters.get(1).getClassname().equals("ConditionHolder"))
                        setParameter(1, new Element(Type.CONDITION,"ConditionHolder","boolean"));
                    parameters.get(1).setReturnType(change.to.toString());
                }
                else
                {
                    // update title
                    title = title.substring(0,title.length()-1);
                    title+="$";
                    // set new parameter
                    if(!parameters.get(1).getClassname().equals("ExpressionHolder"))
                        setParameter(1, new Element(Type.EXPRESSION,"ExpressionHolder",change.to.toString()));
                    parameters.get(1).setReturnType(change.to.toString());
                }

                // make shure enough types have been defined
                while(paramTypes.size()<2) paramTypes.add("");
                // modify parameter type
                paramTypes.set(1, change.to.toString());
                // modify type of parameter
                parameters.get(1).setReturnType(change.to.toString());
            }
        }
        // 8- SetAttribute || AttributeIncrement || AttributeDecrement
        //      change the name ($0) if the corresponding definition has changed it's name ($0)
        else if(
                (this.getClassname().equals("SetAttribute") || this.getClassname().equals("AttributeIncrement") || this.getClassname().equals("AttributeDecrement"))&&
                change.sender!=null &&
                change.position==0 &&   // $0 = name
                change.sender.getClassname().equals("AttributeDefinition")
           )
        {
            // only change the usage with the same variable name
            if(getParameter(0).getTitle().equals(change.from.toString()))
            {
                getParameter(0).setTitle(change.to.toString());
            }
        }
        // 9- Attribute || SetAttribute || GetAttribute || AttributeIncrement || AttributeDecrement
        //      empty if the attribute definition has been deleted
        else if( 
                (
                    this.getClassname().equals("Attribute") ||
                    this.getClassname().equals("SetAttribute") ||
                    this.getClassname().equals("GetAttribute") ||
                    this.getClassname().equals("AttributeIncrement") ||
                    this.getClassname().equals("AttributeDecrement")
                ) &&
                change.sender!=null &&
                change.position==-1 &&   // $1 = type
                change.sender.getClassname().equals("AttributeDefinition") && 
                change.cmd.equals("delete.AttributeDefinition") &&
                change.sender.getTopMostElement().getEditor().equals(getTopMostElement().getEditor()) // same class
           )
        {
            // only apply change if the name of the attribute is the same
            if(parameters.get(0).getTitle().equals(change.sender.getVariableDefinition().name))
            {
                // clean title of parameter
                getParameter(0).setTitle("");
                // reset the return type
                setReturnType("");
            }
        }
    }
    
    private void refreshHandleMethod(Change change)
    {
        // elements to be updated
        // 1- ObjectMethodCall
        //      if the name of the selected object ($0) has been changed
        //      the names of the method ($1) have to be reloaded
        if(
                this.getClassname().equals("ObjectMethodCall") &&
                change.cmd.equals("list") &&
                change.sender!=null &&
                change.position==0 &&   // object name
                change.sender==this
           )
        {
            if(parameters.get(0).getTitle().isEmpty())
            {
                String old = parameters.get(1).getTitle();
                parameters.get(1).setTitle("");
                refresh(new Change(this, 1, "list", old, ""));
            }
            else
            {
                // get all entities
                ArrayList<VariableDefinition> entities = getEntities();
                for (int i = 0; i < entities.size(); i++) {
                    VariableDefinition vd = entities.get(i);
                    // find the one with the same name
                    if(vd.name.equals(parameters.get(0).getTitle()))
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
                        // retrieve list oall methods of that entity
                        ArrayList<VariableDefinition> methodNames = entity.getEditor().getMethods();
                        ((List)parameters.get(1)).update(methodNames);
                    }
                }
            }
        }
        // 2- ObjectMethodCall
        //      if the name of the selected method ($1) has been changed
        //      the *return type* has to be changed too
        //      and the parameters have to updated
        else if(
                this.getClassname().equals("ObjectMethodCall") &&
                change.cmd.equals("list") &&
                change.sender!=null &&
                change.position==1 &&   // method name
                change.sender==this
           )
        {
            // modify the title
            if(change.to.toString().isEmpty())
            {
                // remove unwanted text
                if(title.contains("with parameters"))
                {
                    title=title.substring(0, title.indexOf("with parameters")).trim();
                    parseTitle(title,false);
                }
                // remove unwanted parameters
                while(parameters.size()>2)
                    parameters.remove(parameters.size()-1); 
                // reset code
                if(getType()==Type.INSTRUCTION)
                    setCode("$0.$1();");
                else
                     setCode("$0.$1()");
            }
            else
            {
                ArrayList<VariableDefinition> entities = getEntities();
                for (int i = 0; i < entities.size(); i++) {
                    VariableDefinition vd = entities.get(i);
                    if(vd.name.equals(parameters.get(0).getTitle()))
                    {
                        // get reference to the loaded project
                        Project project = Library.getInstance().getProject();
                        // stop if null or not set
                        if(project==null) return;
                        // get the selected entity
                        Entity entity = project.getEntity(vd.classname);
                        // stop if not found
                        if(entity==null) return;  
                        // stop if class has no editor
                        if(entity.getEditor()==null) return;
                        // retrieve element
                        Element method = entity.getEditor().getMethod(change.to.toString());
                        // remove unwanted text
                        if(title.contains("with parameters"))
                        {
                            title=title.substring(0, title.indexOf("with parameters")).trim();
                            parseTitle(title,false);
                        }
                        // remove unwanted parameters
                        while(parameters.size()>2)
                            parameters.remove(parameters.size()-1); 
                        // reset code
                        if(getType()==Type.INSTRUCTION)
                            setCode("$0.$1();");
                        else
                             setCode("$0.$1()");
                        if(method==null) return;
                        
                        // set the new type
                        setReturnType("void");
                        if(method.parameterCount()>1) setReturnType(method.getParameter(1).getTitle());
                        // check to see if it fits into the parent
                        if(getType()==Type.EXPRESSION &&
                           getParent()!=null &&
                            !typeCanAcceptType(getParent().getReturnType(), getReturnType()))
                        {
                            // clean title of selected method
                            getParameter(1).setTitle("");
                            // reset the return type
                            setReturnType("");
                            // end here
                            return;
                        }

                        // check if parameters @ the end
                        if(method.getTitle().endsWith("^"))
                        {
                            // modify code
                            String code = "$0.$1(";
                            // modify title
                            String title = getTitle()+" with parameters ";
                            // get the second parameter (= element PARAMETERS)
                            Parameters p = ((Parameters)method.getParameter(2));
                            // loop
                            for (int j = 0; j < p.parameterCount(); j++) {
                                Element param = p.getParameter(j);
                                // add placeholder
                                title+=" $";
                                // add a expression holder
                                addParameter(new Element(Type.EXPRESSION,"ExpressionHolder",param.getBody().getReturnType()));
                                code+="$"+(2+j);
                                if(j<p.parameterCount()-1) code+=",";
                            }
                            parseTitle(title,false);
                            code+=")";
                            if(getType()==Type.INSTRUCTION)
                                code+=";";
                            setCode(code);
                        }
                    }
                }
            }
        }
        // 3- ObjectMethodCall
        //      if the method has been renamed inside it's class
        //      the name has to be changed
        //      the list has to be updated if the object fit's the classname
        else if((this.getClassname().equals("ObjectMethodCall")) &&
                change.cmd.equals("rename.method") &&
                change.sender!=null &&
                change.position==0
           )
        {
            // get all entities
            ArrayList<VariableDefinition> vds = getEntities();
            // check if this one fits
            for (int i = 0; i < vds.size(); i++) 
            {
                // get the actual definition
                VariableDefinition vd = vds.get(i);
                // does it fit?
                if(getParameter(1).getTitle().equals(change.from.toString()) &&
                   getParameter(0).getTitle().equals(vd.name) &&
                   vd.type.equals(change.sender.getTopMostElement().getEditor().getBloxsClass().getName()))
                {
                    getParameter(1).setTitle(change.to.toString());
                }
                
                // if the type fits, we need to update the second parameter (list - $1)
                if(getParameter(0).getTitle().equals(vd.name) &&
                   vd.type.equals(change.sender.getTopMostElement().getEditor().getBloxsClass().getName()))
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
                    // retrieve list oall methods of that entity
                    ArrayList<VariableDefinition> methodNames = entity.getEditor().getMethods();
                    ((List)parameters.get(1)).update(methodNames);                   
                }
            }
        }
        // 4- ObjectMethodCall
        //      if the method has been deleted
        else if((this.getClassname().equals("ObjectMethodCall")) &&
                change.cmd.equals("delete.MethodDefinition") &&
                change.sender!=null &&
                change.position==-1
           )
        {
            // get all entities
            ArrayList<VariableDefinition> vds = getEntities();
            // check if this one fits
            for (int i = 0; i < vds.size(); i++) 
            {
                // get the actual definition
                VariableDefinition vd = vds.get(i);
                // does it fit?
                if(getParameter(1).getTitle().equals(change.sender.getParameter(0).getTitle()) &&
                   getParameter(0).getTitle().equals(vd.name) &&
                   vd.type.equals(change.sender.getTopMostElement().getEditor().getBloxsClass().getName()))
                {
                    String old = getParameter(1).getTitle();
                    getParameter(1).setTitle("");
                    // pass the change
                    refresh(new Change(this, 1, "list", old, ""));
                }
                
                // if the type fits, we need to update the second parameter (list - $1)
                if(getParameter(0).getTitle().equals(vd.name) &&
                   vd.type.equals(change.sender.getTopMostElement().getEditor().getBloxsClass().getName()))
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
                    // retrieve list oall methods of that entity
                    ArrayList<VariableDefinition> methodNames = entity.getEditor().getMethods();
                    ((List)parameters.get(1)).update(methodNames);                   
                }
            }
        }
        // 5- ObjectMethodCall
        //      if the parameters of the method changed
        else if(
                this.getClassname().equals("ObjectMethodCall") &&
                change.cmd.equals("parameters.configured") &&
                change.sender!=null &&
                change.position==2  // parameters
           )
        {
            // change title to itself and pass the refresh
            refresh(new Change(this, 1, "list", getParameter(1).getTitle(), getParameter(1).getTitle()));
        }
        // 6- ObjectMethodCall
        //      if the MethodDefinition has changed it's name
        else if(
                this.getClassname().equals("ObjectMethodCall") &&
                change.sender !=null &&
                change.sender.getClassname().equals("MethodDefinition") &&
                change.cmd.equals("list") &&
                change.position == 0    // name
           )
        {
            // get all entities
            ArrayList<VariableDefinition> vds = getEntities();
            // check if this one fits
            for (int i = 0; i < vds.size(); i++) 
            {
                // get the actual definition
                VariableDefinition vd = vds.get(i);
                // does it fit?
                if(getParameter(1).getTitle().equals(change.from.toString()) &&
                   getParameter(0).getTitle().equals(vd.name) &&
                   vd.type.equals(change.sender.getTopMostElement().getEditor().getBloxsClass().getName()))
                {
                    String old = getParameter(1).getTitle();
                    getParameter(1).setTitle(change.to.toString());
                    // pass the change
                    refresh(new Change(this, 1, "list", old, ""));
                }
                
                // if the type fits, we need to update the second parameter (list - $1)
                if(getParameter(0).getTitle().equals(vd.name) &&
                   vd.type.equals(change.sender.getTopMostElement().getEditor().getBloxsClass().getName()))
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
                    // retrieve list oall methods of that entity
                    ArrayList<VariableDefinition> methodNames = entity.getEditor().getMethods();
                    ((List)parameters.get(1)).update(methodNames);                   
                }
            }
        }
        // 7- ObjectMethodCall
        //      if the MethodDefinition has changed it's type
        else if(
                this.getClassname().equals("ObjectMethodCall") &&
                change.sender !=null &&
                change.sender.getClassname().equals("MethodDefinition") &&
                change.cmd.equals("list") &&
                change.position == 1    // type
           )
        {
            // get all entities
            ArrayList<VariableDefinition> vds = getEntities();
            // check if this one fits
            for (int i = 0; i < vds.size(); i++) 
            {
                // get the actual definition
                VariableDefinition vd = vds.get(i);
                // does it fit?
                if(getParameter(1).getTitle().equals(change.sender.getParameter(0).getTitle()) &&
                   getParameter(0).getTitle().equals(vd.name) &&
                   vd.type.equals(change.sender.getTopMostElement().getEditor().getBloxsClass().getName()))
                {
                    // change return type
                    setReturnType(change.to.toString());
                    // check if it fits
                    if(getType()==Type.EXPRESSION &&
                        getParent()!=null &&
                        !typeCanAcceptType(getParent().getReturnType(), getReturnType()))
                    {
                        String old = getParameter(1).getTitle();
                        // clean title of selected method
                        getParameter(1).setTitle("");
                        // reset the return type
                        setReturnType("");
                        // pass change
                        refresh(new Change(this, 1, "list", old, ""));
                    }
                }
            }
        }
        // 8- MethodCall
        //      if the parameters where changed
        else if(
                this.getClassname().equals("MethodCall") &&
                change.cmd.equals("parameters.configured") &&
                change.sender!=null &&
                change.position==2 &&
                change.sender.getTopMostElement().getEditor().equals(getTopMostElement().getEditor()) // same class
           )
        {
            // change the title ti itself and pass on the refresh
            refresh(new Change(this, 0, "list", getParameter(0).getTitle(), getParameter(0).getTitle()));
        }
        // 9- MethodCall
        //      change the parameters & type if the selected name method changed
        else if((this.getClassname().equals("MethodCall")) &&
                change.sender!=null &&
                change.position==0 &&   // name
                change.sender==this &&
                change.sender.getTopMostElement().getEditor().equals(getTopMostElement().getEditor()) // same class
           )
        {
            Element tm = getTopMostElement();
            if(tm==null) return;
            Element method = tm.getEditor().getMethod(change.to.toString());

            // remove unwanted text
            if(title.contains("with parameters"))
            {
                title=title.substring(0, title.indexOf("with parameters")).trim();
                parseTitle(title,false);
            }
            // remove unwanted parameters
            while(parameters.size()>1)
                parameters.remove(parameters.size()-1); 
            // reset code
            if(getType()==Type.INSTRUCTION)
                setCode("$0();");
            else
                setCode("$0()");
            
            // set the new type
            setReturnType("void");
            if(method.parameterCount()>1) setReturnType(method.getParameter(1).getTitle());
            // check to see if it fits into the parent
            if(getType()==Type.EXPRESSION &&
               getParent()!=null &&
                !typeCanAcceptType(getParent().getReturnType(), getReturnType()))
            {
                // clean title of selected method
                getParameter(0).setTitle("");
                // reset the return type
                setReturnType("");
                // end here
                return;
            }
            
            // check if parameters @ the end
            if(method.getTitle().endsWith("^"))
            {
                // modify code
                String code = "$0(";
                // modify title
                String title = getTitle()+" with parameters ";
                // get the second parameter (= element PARAMETERS)
                Parameters p = ((Parameters)method.getParameter(2));
                // loop
                for (int j = 0; j < p.parameterCount(); j++) {
                    Element param = p.getParameter(j);
                    // add placeholder
                    title+=" $";
                    // add a expression holder
                    addParameter(new Element(Type.EXPRESSION,"ExpressionHolder",param.getBody().getReturnType()));
                    code+="$"+(1+j);
                    if(j<p.parameterCount()-1) code+=",";
                }
                parseTitle(title,false);
                code+=")";
                if(getType()==Type.INSTRUCTION)
                    code+=";";
                setCode(code);
            }
        }
        // 11- MethodCall
        //      if the method has been renamed 
        //      the name has to be changed
        else if((this.getClassname().equals("MethodCall")) &&
                change.cmd.equals("rename.method") &&
                change.sender!=null &&
                change.position==0 &&
                change.sender.getTopMostElement().getEditor().equals(getTopMostElement().getEditor()) // same class
           )
        {
            // change title
            if(getParameter(0).getTitle().equals(change.from.toString()))
                getParameter(0).setTitle(change.to.toString());
        }
        // 12- MethodCall
        //      if the method has been deleted
        else if((this.getClassname().equals("MethodCall")) &&
                change.cmd.equals("delete.MethodDefinition") &&
                change.sender!=null &&
                change.position==-1 &&
                change.sender.getTopMostElement().getEditor().equals(getTopMostElement().getEditor()) // same class
           )
        {
            // change title
            if(getParameter(0).getTitle().equals(change.sender.getParameter(0).getTitle()))
                getParameter(0).setTitle("");
        }
        // 13- MethodCall
        //      if the method has changed it's type
        //      the *return type* must be updated
        else if((this.getClassname().equals("MethodCall")) &&
                change.cmd.equals("list") &&
                change.sender!=null &&
                change.sender.getClassname().equals("MethodDefinition") &&
                change.position==1 &&
                change.sender.getTopMostElement().getEditor().equals(getTopMostElement().getEditor()) // same class
           )
        {
            // set the return type
            if(getParameter(0).getTitle().equals(change.sender.getParameter(0).getTitle()))
                setReturnType(change.to.toString());
            
            // reset if type is not allowed
            if(getParent()!=null &&
               !typeCanAcceptType(getParent().getReturnType(), getReturnType()))
            {
                // reset title
                getParameter(0).setTitle("");
                // reset return type
                setReturnType("");
            }
        }
    }
    
    public void refresh(Change change)
    {
        /*
        
            !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        
            Don't forget, that some elements (Variable, Attribute, Entity, World, Sound, Image)
            are being checked before being drawn!
        
        */
        
        //System.out.println("I ("+this.getClassname()+") got a refresh: "+change);
        
        // refresh parameters
        refreshParameters(change);
        
        // refresh elements
        refreshElements(change);
        
        // forward the changes to each parameter
        for (int i = 0; i < parameters.size(); i++) {
            parameters.get(i).refresh(change);
        }
        
        // forward the changes to the body
        if(getBody()!=null)
            getBody().refresh(change);
        
        // forward the changes to the next element
        if(getNext()!=null)
            getNext().refresh(change);
    }
    
    
    
    // get the top most element of the tree
    public Element getTopMostElement()
    {
        Element topmost = this;
        while(topmost.getParent()!=null)
            topmost=topmost.getParent();
        return topmost;
    }
    
    // get the top most element of the current bloc
    public Element getBlockMostElement()
    {
        Element blockmost = this;
        while(blockmost!=null && (blockmost.getParent()!=blockmost.getPrev() || blockmost.isHolder() || blockmost.getType()==Type.ITEM))
            blockmost=blockmost.getParent();
        //System.out.println("Blockmost element of: "+this+" is: "+blockmost);
        return blockmost; 
    }
    
    public boolean typeCanAcceptType(String destination, String source)
    {
        if(destination==null || source==null) return false;
        
        if(source.toLowerCase().equals("int")) source="integer";
        if(destination.toLowerCase().equals("int")) destination="integer";
        
        //System.out.println("Destination: "+destination+" << Source: "+source+" / I am: "+this.getTitle());
        
        if(source.trim().isEmpty() || destination.trim().isEmpty()) return true;
        if(destination.toLowerCase().equals(source.toLowerCase())) return true;
        
        if(destination.toLowerCase().equals("float") && source.toLowerCase().equals("integer")) return true;
        if(destination.toLowerCase().equals("long") && source.toLowerCase().equals("integer")) return true;
        if(destination.toLowerCase().equals("double") && source.toLowerCase().equals("float")) return true;
        if(destination.toLowerCase().equals("double") && source.toLowerCase().equals("integer")) return true;
        if(destination.toLowerCase().equals("double") && source.toLowerCase().equals("long")) return true;
        
        if(destination.toLowerCase().contains(source.toLowerCase())) return true;

        return false;
    }

    public boolean typeIsComplex(String type)
    {
        if(type.trim().isEmpty()) return false;
        return type.charAt(0)==type.toUpperCase().charAt(0);
    }
    
    public boolean isElementary()
    {
        return !hasTop && !hasBottom && getType()==Type.INSTRUCTION;
    }
    
    public boolean isConnected()
    {
        return (parent!=null);
    }
     
    
    // called each time the element is being drop onto some other
    public void onConnect(Element onto)
    {
        //System.out.println("Connection "+getClassname()+" onto "+onto.getClassname());
        
        
    }
    
     // called each time the element is being drop onto some other
    public void onDetach(Element from)
    {
        //
    }
// source: http://stackoverflow.com/questions/439298/best-way-to-encode-text-data-for-xml-in-java
    private String xmlEscapeText(String t) 
    {
        if(t==null) return "";
        
       StringBuilder sb = new StringBuilder();
       for(int i = 0; i < t.length(); i++){
          char c = t.charAt(i);
          switch(c){
          case '<': sb.append("&lt;"); break;
          case '>': sb.append("&gt;"); break;
          case '\"': sb.append("&quot;"); break;
          case '&': sb.append("&amp;"); break;
          case '\'': sb.append("&apos;"); break;
          case '\n': sb.append("&#10;"); break;
          default:
             if(c>0x7e) {
                sb.append("&#"+((int)c)+";");
             }else
                sb.append(c);
          }
       }
       return sb.toString();
    }

    public ArrayList<String> getAllNeeds()
    {
        ArrayList<String> subs = new ArrayList<>();
        
        // add this
        if(this.getNeedsParent()!=null && !this.getNeedsParent().trim().isEmpty())
            subs.add(this.getNeedsParent());
        
        // add parameters
        for (int i = 0; i < parameters.size(); i++) {
            ArrayList<String> ss = parameters.get(i).getAllNeeds();
            // sub needs may be satisfied, so ...
            for (int j = 0; j < ss.size(); j++) {
                String get = ss.get(j);
                // ... test an elimintate if satisfied
                if(get.contains(this.getClassname()))
                    get=get.replace(this.getClassname(), "");
                subs.add(get);
            }
        }
        
        // add body
        if(getBody()!=null)
        {
            ArrayList<String> ss = getBody().getAllNeeds();
            for (int j = 0; j < ss.size(); j++) {
                String get = ss.get(j);
                if(get.contains(this.getClassname()))
                    get=get.replace(this.getClassname(), "");
                subs.add(get);
            }
        }
        
        // add next
        if(getNext()!=null)
        {
            ArrayList<String> ss = getNext().getAllNeeds();
            for (int j = 0; j < ss.size(); j++) {
                String get = ss.get(j);
                if(get.contains(this.getClassname()))
                    get=get.replace(this.getClassname(), "");
                subs.add(get);
            }
        }
        
        return subs;
    }
    
    public String getSubNeedsParent() {
        return getAllNeeds().toString().replace(", ", ",").replace("[", "").replace("]", "");
        //return needsParent;
    }
    
    public String getNeedsParent() {
        return needsParent;
    }

    public void setNeedsParent(String needsParent) {
        this.needsParent = needsParent;
    }

    public String getXml(int indent)
    {
        String result = "";
        
/*
       
        private ArrayList<String> acceptDockTo = new ArrayList<>();

        private String code;
 */       
        ArrayList<String> transforms = new ArrayList<>();
        for (int i = 0; i < parameters.size(); i++) {
            if(transformations.containsKey(i))
                transforms.add(transformations.get(i));
            else
                transforms.add("");
        }

        result+= getIndent(indent)  +"<element class=\""+this.getClassname()
                +"\" return=\""+this.getReturnType()
                +"\" title=\""+xmlEscapeText(this.getTitle())
                +"\" paramTypes=\""+(paramTypes==null?"NULL":paramTypes.toString().replace(", ", ",").replace("[", "").replace("]", ""))
                +"\" type=\""+this.getType()
                +"\" needsParent=\""+this.getNeedsParent()
                +"\" position=\""+this.getPosition()
                +"\" x=\""+this.getOffset().x
                +"\" y=\""+this.getOffset().y
                +"\" top=\""+(this.hasTop?1:0)
                +"\" body=\""+(this.hasBody?1:0)
                +"\" bottom=\""+(this.hasBottom?1:0)
                +"\" transformation=\""+transformation
                +"\" paramTransformations=\""+(transforms==null?"NULL":transforms.toString().replace(", ", ",").replace("[", "").replace("]", ""))
                +"\" after=\""+(allowDockAfter==null?"NULL":allowDockAfter.toString().replace(", ", ",").replace("[", "").replace("]", ""))
                +"\" inside=\""+(allowDockInside==null?"NULL":allowDockInside.toString().replace(", ", ",").replace("[", "").replace("]", "")) 
                +"\" code=\""+xmlEscapeText(this.code)
                +"\" color=\""+ColorUtils.getHexColor(this.color)+"\">\n";
       
        if(parameters.size()>0)
            result+= getIndent(indent+1)+"<parameters>\n";
        for (int i = 0; i < parameters.size(); i++) {
            Element param = parameters.get(i);
            /*if(param.isHolder && param.getBody()!=null)
                result+= param.getBody().getXml(indent+2);
            else*/
                result+= param.getXml(indent+2);
        }
        if(parameters.size()>0)
            result+= getIndent(indent+1)+"</parameters>\n";

        if(getBody()!=null)
        {
            result+= getIndent(indent+1)+"<body>\n";
            result+= getBody().getXml(indent+2);
            result+= getIndent(indent+1)+"</body>\n";
        }

        result+= getIndent(indent)  +"</element>\n";
        
        if(getNext()!=null)
            result+= next.getXml(indent);
        
        return result;
    }

    
    public void clearParameters()
    {
        parameters.clear();
    }

    
    /*
     * SETTERS & GETTERS
     */
    
    
    public Element getNext() {
        return next;
    }

    public Element getLastElementForStructureByPosition(Pos pos)
    {
        //if(prev==null) return next;
        
        /*
        System.out.println("This is a: "+this.getClassname());
        System.out.println("My parent is: "+(parent==null?"NULL":getParent().getClassname()));
        System.out.println("My next is: "+(next==null?"NULL":getNext().getClassname()));
        System.out.println("My prev is: "+(prev==null?"NULL":getPrev().getClassname()));
        */
        
        Element result = this;
        
        while(result.next!=null)
        {
            //System.out.println("May "+result.next.getClassname()+" dock to "+prev.getClassname()+" as position "+pos+" ?");
            if(!result.next.allowDockTo(prev,pos))
            {
                //System.out.println("No");
                result=result.next;
            }
            else 
            {
                //ystem.out.println("Yes");
                return result;
            }
        }
        
        return result;
    }
    
    public void setNext(Element next) {
        this.next = next;
        // link back
        if(next!=null)
        {
            //next.setParent(getParent());
            next.setPrev(this);
        }
    }

    public Element getPrev() {
        return prev;
    }

    public void setPrev(Element prev) {
        this.prev = prev;
    }
    
    public String getReturnType() {
        //if(isHolder() && getBody()!=null)
        //    return body.getReturnType();
        return returnType;
    }

    public void setReturnType(String type) {
        this.returnType = type;
    }
    
    public Element getParent() {
        return parent;
    }

    public void setParent(Element parent) {
        this.parent = parent;
        
        if(getNext()!=null)
            getNext().setParent(parent);
    }
    
    public String getXml()
    {
        return getXml(0);
    }
    
    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
    
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
        
        for (int i = 0; i < parameters.size(); i++) {
            parameters.get(i).setPosition(position);
        }
    }
    
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
    
    public boolean isHolder()
    {
        return isHolder;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }
    
    public void setTransformation(Integer key, String value)
    {
        transformations.put(key, value);
    }

    public ArrayList<String> getParamTypes() {
        return paramTypes;
    }
    
    public String getEntityClassname()
    {
        if(getClassname().equals("AddEntity"))
        {
            return parameters.get(0).getTitle();
        }
        return null;
    }
    
    public VariableDefinition getVariableDefinition() {
        if(getClassname().equals("VariableDefinition") ||
           getClassname().equals("AttributeDefinition")||
           getClassname().equals("For"))
        {
            VariableDefinition v = new VariableDefinition();
            v.name = parameters.get(0).getTitle();
            v.type = parameters.get(1).getTitle();
            //if(parameters.get(2).getBody()!=null)
            //    v.classname = parameters.get(2).getBody().getEntityClassname();
            v.classname = parameters.get(1).getTitle();
            if(getClassname().equals("For"))
                v.type="double";
            else
                v.type = parameters.get(1).getTitle();
            //System.out.println(v);
            return v;
        }
        return null;
    }
    
    public VariableDefinition getMethodDefinition() {
        if(getClassname().equals("MethodDefinition"))
        {
            VariableDefinition v = new VariableDefinition();
            v.name = parameters.get(0).getTitle();
            v.type = "void";
            if(parameters.size()>1)
                v.type = parameters.get(1).getTitle();
            return v;
        }
        return null;
    }
    
    /**************************/
    
    public String stringify(String value)
    {
        if(value==null || value.trim().isEmpty()) return value;
        return value.replaceAll("\"", "\\\\\"");
    }
    
    public String variable(String value)
    {
        if(parameters.size()==3)
        {
            Element param0 = parameters.get(0);
            Element param1 = parameters.get(1);
            Element param2 = parameters.get(2);
            
            if(param1.getTitle().trim().equals("String"))
            {
                return param1.getTitle()+" "+param0.getTitle()+" = \""+stringify(param2.getTitle())+"\";";
            }
            return value;
        }
        return value;
    }
    
    public String attribute(String value)
    {
        if(parameters.size()==3)
        {
            Element param0 = parameters.get(0);
            Element param1 = parameters.get(1);
            Element param2 = parameters.get(2);
            
            String result = value;
            if(param1.getTitle().trim().equals("String"))
            {
                result = param1.getTitle()+" "+param0.getTitle()+" = \""+stringify(param2.getTitle())+"\";";
            }
            value = value.replace("=;", ";");
            return value;
        }
        return value;
    }
    
    
    public String compare(String value)
    {
        /*System.out.println("-----");
        System.out.println("Calling from: "+this.getClassname());
        System.out.println(value);/**/
        if(parameters.size()==3)
        {
            Element param0 = parameters.get(0);
            Element param2 = parameters.get(2);
            
            //System.out.println("P0 T: "+param0.getReturnType());
            //System.out.println("P2 T: "+param2.getReturnType());

            if(typeIsComplex(param0.getReturnType()) || typeIsComplex(param2.getReturnType()))
            {
                if(value.contains(" == "))
                {
                    value=value.replace(" == ", ".equals(");
                    value+=")";
                }
                if(value.contains(" != "))
                {
                    value="!"+value.replace(" != ", ".equals(");
                    value+=")";
                }
                else if(value.contains(" < "))
                {
                    value=value.replace(" < ", ".compareTo(");
                    value+=") < 0";
                }
                else if(value.contains(" <= "))
                {
                    value=value.replace(" <= ", ".compareTo(");
                    value+=") <= 0";
                }
                else if(value.contains(" > "))
                {
                    value=value.replace(" > ", ".compareTo(");
                    value+=") > 0";
                }
                else if(value.contains(" >= "))
                {
                    value=value.replace(" >= ", ".compareTo(");
                    value+=") >= 0";
                }
            }
        }
        return value;
    }

    public BloxsEditor getEditor() {
        return editor;
    }

    public Element setEditor(BloxsEditor editor) {
        this.editor = editor;
        return this;
    }

    public ArrayList<Element> getDrawLast() {
        return drawLast;
    }
    
    
    
    
}
