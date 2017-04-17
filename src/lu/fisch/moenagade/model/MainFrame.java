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
public class MainFrame extends BloxsClass {
    
    public MainFrame(String name) {
        super(name);
    }
    
    @Override
    public void saveSourceToFile(String dirname) throws FileNotFoundException, UnsupportedEncodingException, IOException
    {
        saveSourceToFile(dirname, false);
    }
    
    public void saveSourceToFile(String dirname, boolean closeOnExit) throws FileNotFoundException, UnsupportedEncodingException, IOException
    {
        String filename = dirname+System.getProperty("file.separator")+getName()+".java";
        FileOutputStream fos = new FileOutputStream(filename);
        Writer out = new OutputStreamWriter(fos, "UTF-8");
        out.write(getJavaCode(closeOnExit));
        out.close();       
    }

    @Override
    public String getJavaCode() {
        return getJavaCode(false);
    }
    
    public String getJavaCode(boolean closeOnExit) {
        String code = "";
        code+="package moenagade;\n";
        code+="\n";
        code+="import java.awt.event.KeyEvent;\n";
        code+="import moenagade.*;\n";
        code+="import moenagade.base.*;\n";
        code+="import moenagade.worlds.*;\n";
        code+="import moenagade.entities.*;\n";
        code+="\n";
        code+="public class "+getName()+" extends "+this.getClass().getSimpleName()+"\n";
        code+="{\n";
        code+=getEditor().getJavaCode(1)+"\n";
        code+="    \n";
        code+="    \n";
        code+="    public static void main(String args[])\n";
        code+="    {\n";
        code+="        java.awt.EventQueue.invokeLater(new Runnable() {\n";
        code+="            public void run()\n";
        code+="            {\n";
        code+="                Project project = new Project();\n";
        if(closeOnExit)
        code+="                project.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);\n";    
        code+="                project.setVisible(true);\n";
        code+="            }\n";
        code+="        });\n";
        code+="    }\n";
        code+="}";
        return code;
    }
}
