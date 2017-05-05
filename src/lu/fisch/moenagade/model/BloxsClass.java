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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import lu.fisch.moenagade.bloxs.Element;
import lu.fisch.moenagade.gui.Change;

/**
 *
 * @author robert.fisch
 */
public abstract class BloxsClass {
    // list of elements
    private ArrayList<Element> elements = new ArrayList<>();
    
    // editor
    private BloxsEditor editor = new BloxsEditor(this);
    
    private String name;
    
    private Project project = null;
    
    @Override
    public String toString()
    {
        return name;
    }

    public BloxsClass(String name) {
        this.name = name;
    }

    public ArrayList<Element> getElements() {
        return elements;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BloxsEditor getEditor() {
        return editor;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
    
    public void saveToFile(String dirName) throws UnsupportedEncodingException, IOException
    {
        editor.saveAsBloxs(dirName+System.getProperty("file.separator")+name+".bloxs");
    }
    
    public void modified()
    {
        if (project!=null)
            project.setChanged(true);
    }
    
    public Element check()
    {
        return editor.check();
    }
    
    /**
     *
     * @param change
     */
    public void refresh(Change change)
    {
        editor.refreshElements(change);
    }
    
    public abstract void saveSourceToFile(String dirname) throws FileNotFoundException, UnsupportedEncodingException, IOException;
    
    public abstract String getJavaCode();
}
