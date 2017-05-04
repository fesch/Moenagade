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

package lu.fisch.moenagade;

import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import lu.fisch.utils.StringList;

public class Moenagade
{
    public final static String E_NAME = "Moenagade";
    public final static String E_VERSION = "0.15" +
            "" +
            "";

    public static StringList messages = new StringList();

    public static String E_THANKS =
    "Developed and maintained by\n"+
    " - Robert Fisch <robert.fisch@education.lu>\n"+
    "\n"+
    "Take a look at the “license“ tab for details about third party components and files."
    ;

    public static String PACKAGE_NAME = "moenagade.pro";
    public static Icon PACKAGE_ICON = new javax.swing.ImageIcon(Moenagade.class.getResource("/lu/fisch/moenagade/images/moenagade16.png"));

    public static final ImageIcon IMG_INFO = new ImageIcon(Moenagade.class.getResource("/lu/fisch/icons/iconfinder_info_lgpl_matrc_martin.png"));
    public static final ImageIcon IMG_ERROR = new ImageIcon(Moenagade.class.getResource("/lu/fisch/icons/iconfinder_error_lgpl_david_vignoni.png"));
    public static final ImageIcon IMG_WARNING = new ImageIcon(Moenagade.class.getResource("/lu/fisch/icons/iconfinder_warning_gpl_pavel_infernodemon.png"));
    public static final ImageIcon IMG_QUESTION = new ImageIcon(Moenagade.class.getResource("/lu/fisch/icons/iconfinder_question_lgpl_david_vignoni.png"));
    
    
    public static boolean javaDocDetected = true;
    public static boolean javaCompilerDetected = true;
    public static boolean javaCompileOnTheFly = false;

    public static String JDK_home = null;
    public static String JDK_source = null;

    public static void switchButtons(JButton a, JButton b)
    {
        if(!System.getProperty("os.name").toLowerCase().startsWith("mac os x"))
	{
            JButton btnTmp = new JButton();
            copyButton(a,btnTmp);
            copyButton(b,a);
            copyButton(btnTmp,b);
            b.requestFocusInWindow();
        }
        else a.requestFocusInWindow();
    }
    
    private static void copyButton(JButton from, JButton to)
    {
        to.setText(from.getText());
        for(ActionListener a : to.getActionListeners())
        {
            to.removeActionListener(a);
        }
        for(ActionListener a : from.getActionListeners())
        {
            to.addActionListener(a);
        }
        if(from.hasFocus())
        {
            to.requestFocus();
            to.requestFocusInWindow();
        }
    }

}
