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


package lu.fisch.moenagade.gui.dialogs;

import javax.swing.*;
import javax.swing.filechooser.*;
import java.io.File;
import lu.fisch.moenagade.Moenagade;
import lu.fisch.moenagade.Moenagade;

public class PackageFileView extends FileView
{
   @Override
    public String getName(File f)
    {
        return null;
    }

    @Override
    public String getDescription(File f)
    {
        return null;
    }

    @Override
    public String getTypeDescription(File f)
    {
        return null;
    }

    @Override
    public Icon getIcon(File f)
    {
        if(PackageFile.exists(f))
            return Moenagade.PACKAGE_ICON;
        else
            return null;
    }
}
