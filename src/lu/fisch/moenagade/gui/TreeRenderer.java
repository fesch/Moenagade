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

package lu.fisch.moenagade.gui;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import lu.fisch.moenagade.Moenagade;

/**
 *
 * @author robert.fisch
 */
public class TreeRenderer extends DefaultTreeCellRenderer {

    private static final Icon project = new javax.swing.ImageIcon(Moenagade.class.getResource("/lu/fisch/moenagade/images/moenagade16.png"));
    private static final Icon world   = new javax.swing.ImageIcon(Moenagade.class.getResource("/lu/fisch/moenagade/images/world16.png"));
    private static final Icon entitiy = new javax.swing.ImageIcon(Moenagade.class.getResource("/lu/fisch/moenagade/images/entity16.png"));
    private static final Icon images = new javax.swing.ImageIcon(Moenagade.class.getResource("/lu/fisch/moenagade/images/images16.png"));
    private static final Icon sounds = new javax.swing.ImageIcon(Moenagade.class.getResource("/lu/fisch/moenagade/images/sounds16.png"));

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
        boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        String s = node.getUserObject().toString();
        //System.out.println(s);
        if ("Project".equals(s)) {
            setOpenIcon(project);
            setClosedIcon(project);
            setLeafIcon(project);
        }
        else if ("Worlds".equals(s)) {
            setOpenIcon(world);
            setClosedIcon(world);
            setLeafIcon(world);
        } else if ("Entities".equals(s)) {
            setOpenIcon(entitiy);
            setClosedIcon(entitiy);
            setLeafIcon(entitiy);
        } else if ("Images".equals(s)) {
            setOpenIcon(images);
            setClosedIcon(images);
            setLeafIcon(images);
        } else if ("Sounds".equals(s)) {
            setOpenIcon(sounds);
            setClosedIcon(sounds);
            setLeafIcon(sounds);
        } else {
            setOpenIcon(getDefaultOpenIcon());
            setClosedIcon(getDefaultClosedIcon());
            setLeafIcon(getDefaultLeafIcon());
        }
        super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);
        return this;
    }
}