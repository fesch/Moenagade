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

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import lu.fisch.moenagade.bloxs.Element;
import lu.fisch.moenagade.bloxs.List;

/**
 *
 * @author robert.fisch
 */
public class BloxsDefinition {
    private String categorie;
    private String classname;
    private String title;
    private Color color;
    private Element.Type type;
    private String returnType;
    private String destinations;
    private String transformation;
    private String needsParent = "";
    
    private boolean hasTop;
    private boolean hasBody;
    private boolean hasBottom;
    
    private ArrayList<String> allowDockAfter = new ArrayList<>();
    private ArrayList<String> allowDockInside = new ArrayList<>();
    private HashMap<Integer,String> transformations = new HashMap<>();
    private HashMap<Integer,List> lists = new HashMap<>();
    private ArrayList<String> paramTypes = new ArrayList<>();
    
    private ArrayList<ArrayList<BloxsDefinition>> parameterSubs = new ArrayList<>();
    
    private String code;

    public BloxsDefinition(String categorie, String classname, String title, String paramTypes, Color color, Element.Type type, String returnType, boolean hasTop, boolean hasBody, boolean hasBottom, String code, String destinations) {
        this.categorie=categorie;
        this.classname = classname;
        this.title = title;
        this.color = color;
        this.type = type;
        this.returnType = returnType;
        this.hasTop = hasTop;
        this.hasBody = hasBody;
        this.hasBottom = hasBottom;
        this.code = code;
        this.destinations=destinations;
        
        String[] paramT = paramTypes.split(",");
        for (int i = 0; i < paramT.length; i++) {
            this.paramTypes.add(paramT[i]);
        }
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }
    
    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Element.Type getType() {
        return type;
    }

    public void setType(Element.Type type) {
        this.type = type;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public boolean hasTop() {
        return hasTop;
    }

    public void setHasTop(boolean hasTop) {
        this.hasTop = hasTop;
    }

    public boolean hasBody() {
        return hasBody;
    }

    public void setHasBody(boolean hasBody) {
        this.hasBody = hasBody;
    }

    public boolean hasBottom() {
        return hasBottom;
    }

    public void setHasBottom(boolean hayNext) {
        this.hasBottom = hayNext;
    }

    public ArrayList<String> getAllowDockAfter() {
        return allowDockAfter;
    }
    public void setAllowDockAfter(ArrayList<String> allowDockAfter) {
        this.allowDockAfter = allowDockAfter;
    }
    
    public void nullAllowDockAfter() {
        this.allowDockAfter = null;
    }

    public void setAllowDockAfter(String[] allowDockAfter) {
        this.allowDockAfter = new ArrayList<>();
        for (int i = 0; i < allowDockAfter.length; i++) {
            if(!allowDockAfter[i].trim().isEmpty())
                this.allowDockAfter.add(allowDockAfter[i].trim());
        }
    }

    public ArrayList<String> getAllowDockInside() {
        return allowDockInside;
    }

    public void setAllowDockInside(ArrayList<String> allowDockInside) {
        this.allowDockInside = allowDockInside;
    }
        
    public void nullAllowDockInside() {
        this.allowDockInside = null;
    }
    
    public void setAllowDockInside(String[] allowDockInside) {
        this.allowDockInside = new ArrayList<>();
        for (int i = 0; i < allowDockInside.length; i++) {
            if(!allowDockInside[i].trim().isEmpty())
                this.allowDockInside.add(allowDockInside[i].trim());
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setTransformation(Integer key, String value)
    {
        transformations.put(key, value);
    }

    public HashMap<Integer, String> getTransformations() {
        return transformations;
    }

    public void setTransformations(HashMap<Integer, String> transformations) {
        this.transformations = transformations;
    }
    
    public void setList(Integer position, List list)
    {
        lists.put(position, list);
    }

    public HashMap<Integer, List> getLists() {
        return lists;
    }

    public ArrayList<String> getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(ArrayList<String> paramTypes) {
        this.paramTypes = paramTypes;
    }

    public String getDestinations() {
        return destinations;
    }

    public void setDestinations(String destinations) {
        this.destinations = destinations;
    }

    public String getTransformation() {
        return transformation;
    }

    public void setTransformation(String transformation) {
        this.transformation = transformation;
    }

    public String getNeedsParent() {
        return needsParent;
    }

    public void setNeedsParent(String needsParent) {
        this.needsParent = needsParent;
    }
    
    public boolean addSub(int paramPos, BloxsDefinition sub)
    {
        while(parameterSubs.size()<=paramPos)
            parameterSubs.add(new ArrayList<BloxsDefinition>());
        return parameterSubs.get(paramPos).add(sub);
    }

    public ArrayList<BloxsDefinition> getSubs(int paramPos) {
        while(parameterSubs.size()<=paramPos)
            parameterSubs.add(new ArrayList<BloxsDefinition>());
        return parameterSubs.get(paramPos);
    }
    
    
}
