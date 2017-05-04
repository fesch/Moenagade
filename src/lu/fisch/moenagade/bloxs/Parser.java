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

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import lu.fisch.graphics.ColorUtils;
import lu.fisch.moenagade.bloxs.Element.Type;
import lu.fisch.moenagade.model.BloxsDefinition;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class Parser extends DefaultHandler {

    class State {
        public boolean inParam = false;
        public boolean inBody  = false;
        public Element element = null;

        public State(Element element) { this.element=element; }
    }
    
    private ArrayList<Element> elements = new ArrayList<Element>();
    
    private Stack<State> stack = new Stack<>();
    private State actual = null;
    
    @Override
    public void startElement(String namespaceUri, String localName, String qualifiedName, Attributes attributes) throws SAXException 
    {
        if(qualifiedName.equals("blocx"))
        {
            elements = new ArrayList<Element>();
        }
        else if(qualifiedName.equals("element"))
        {
            /*try 
            {*/
                // create the object
                /*
                Class c   = Class.forName(attributes.getValue("class"));
                Object o  = c.newInstance();
                Element e = (Element) o;
                */
                
                // get attributes
                
                
                Type type = null;
                String readtype = attributes.getValue("type");
                if(readtype.equals("INSTRUCTION")) type=(Element.Type.INSTRUCTION);
                else if(readtype.equals("EXPRESSION")) type=(Element.Type.EXPRESSION);
                else if(readtype.equals("CONDITION")) type=(Element.Type.CONDITION);
                else if(readtype.equals("PARAMETERS")) type=(Element.Type.PARAMETERS);
                else if(readtype.equals("VALUE")) type=(Element.Type.VALUE);
                else if(readtype.equals("LIST")) type=(Element.Type.LIST);
                else if(readtype.equals("ITEM")) type=(Element.Type.ITEM);
                
                BloxsDefinition bd = new BloxsDefinition(
                        "?", 
                        attributes.getValue("class"), 
                        attributes.getValue("title"), 
                        attributes.getValue("paramTypes"), 
                        ColorUtils.getColor(attributes.getValue("color")), 
                        type, 
                        attributes.getValue("return"), 
                        attributes.getValue("top").equals("1"), 
                        attributes.getValue("body").equals("1"), 
                        attributes.getValue("bottom").equals("1"), 
                        attributes.getValue("code"),
                        attributes.getValue("destinations")
                );
                
                //System.out.println("Title = "+attributes.getValue("title"));
                
                bd.setNeedsParent(attributes.getValue("needsParent"));
                if(attributes.getValue("needsParent").equals("null"))
                    bd.setNeedsParent("");
                
                String inside = attributes.getValue("inside");
                if(inside.equals("NULL")) bd.nullAllowDockInside();
                else bd.setAllowDockInside(inside.split(","));
                
                String after = attributes.getValue("after");
                if(after.equals("NULL")) bd.nullAllowDockAfter();
                else bd.setAllowDockAfter(after.split(","));
                
                bd.setTransformation(attributes.getValue("transformation"));
                if(attributes.getValue("transformation").toLowerCase().equals("null"))
                    bd.setTransformation("");
  
                Element e;
                if(type==Type.VALUE) e  = new Value(bd);
                else if(type==Type.LIST) e  = new List(bd);
                else if(type==Type.ITEM) e  = new Item(bd);
                else if(type==Type.PARAMETERS) e  = new Parameters(bd);
                else e  = new Element(bd);
                
                String transform = attributes.getValue("paramTransformations");
                String[] transformations = transform.split(",");
                for (int i = 0; i < transformations.length; i++) {
                    String transformation = transformations[i];
                    //System.out.println(transformation);
                    e.setTransformation(i, transformation);
                }
               
                e.setOffset(new Point(Integer.valueOf(attributes.getValue("x")),Integer.valueOf(attributes.getValue("y"))));
                
                
                /*
                e.setTitle(attributes.getValue("title"));
                e.setReturnType(attributes.getValue("return"));
                String type = attributes.getValue("type");
                if(type.equals("INSTRUCTION")) e.setType(Element.Type.INSTRUCTION);
                else if(type.equals("EXPRESSION")) e.setType(Element.Type.EXPRESSION);
                else if(type.equals("CONDITION")) e.setType(Element.Type.CONDITION);
                e.setOffset(new Point(Integer.valueOf(attributes.getValue("x")),Integer.valueOf(attributes.getValue("y"))));
                e.setColor(ColorUtils.getColor(attributes.getValue("color")));
                e.setPosition(Integer.valueOf(attributes.getValue("position")));
                */
                
                State state = new State(e);
                
                if(stack.empty())
                {
                    elements.add(e);
                }
                else
                {
                    State last = stack.peek();
                    
                    if(last.inParam)
                    {
                        // inside parameter section
                        last.element.addParameter(e);
                    }
                    else if(last.inBody)
                    {
                        // inside body section
                        last.element.addToBody(e);
                    }
                    else
                    {
                        // after the element
                        last.element.append(e);
                    }
                }
                stack.push(state);
            /*} 
            catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) 
            {
                ex.printStackTrace();
            }*/
        }
        else if(qualifiedName.equals("parameters"))
        {
            stack.peek().inParam=true;
            stack.peek().element.clearParameters();
        }
        else if(qualifiedName.equals("body"))
        {
            stack.peek().inBody=true;
        }
        // name of element: qualifiedName.equals("root")
        // index of attribute: attributes.getIndex("version")
        // value of attribute: attributes.getValue("version")
    }
	
    @Override
    public void endElement(String namespaceUri, String localName, String qualifiedName) throws SAXException 
    {
        if(qualifiedName.equals("element"))
        {
            stack.pop();
        }
        else if(qualifiedName.equals("parameters"))
        {
            stack.peek().inParam=false;
        }
        else if(qualifiedName.equals("body"))
        {
            stack.peek().inBody=false;
        }
    }
	
    @Override
    public void characters(char[] chars, int startIndex, int endIndex) 
    {
        //
    }
	
    public ArrayList<Element> parse(String _filename) throws SAXException, IOException
    {
        elements.clear();
        
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try		
        {
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(_filename,this);
        } 
        catch(Exception e) 
        {
            String errorMessage = "Error parsing " + _filename + ": " + e;
            System.err.println(errorMessage);
            e.printStackTrace();
            if (e instanceof SAXException)
            {
                throw (SAXException)e;
            }
            else if (e instanceof IOException)
            {
                throw (IOException)e;
            }
        }
		
        return elements;
    }
}
