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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 *
 * @author robert.fisch
 */
public class World extends BloxsClass {
    
    public World(String name) {
        super(name);
    }
    
    public void saveSourceToFile(String dirname) throws FileNotFoundException, UnsupportedEncodingException, IOException
    {
        String filename = dirname+System.getProperty("file.separator")+getName()+".java";
        FileOutputStream fos = new FileOutputStream(filename);
        Writer out = new OutputStreamWriter(fos, "UTF-8");
        out.write(getJavaCode());
        out.close();       
    }

    @Override
    public String getJavaCode() {
        String code = "";
        code+="package moenagade.worlds;\n";
        code+="\n";
        code+="import java.awt.*;\n";
        code+="import java.awt.event.KeyEvent;\n";
        code+="import javax.swing.Timer;\n";
        code+="import java.awt.event.ActionListener;\n";
        code+="import java.awt.event.ActionEvent;\n";
        code+="import moenagade.*;\n";
        code+="import moenagade.base.*;\n";
        code+="import moenagade.worlds.*;\n";
        if(getProject().getEntities().size()>0)
            code+="import moenagade.entities.*;\n";
        code+="\n";
        code+="public class "+getName()+" extends "+this.getClass().getSimpleName()+"\n";
        code+="{\n";
        code+="    public "+getName()+"()\n";
        code+="    {\n";
        code+="        super();\n";
        code+="        onCreate();\n";
        code+="    }\n";
        code+="\n";
        code+=getEditor().getJavaCode(1)+"\n";
        code+="}";
        return code;
    }
}
