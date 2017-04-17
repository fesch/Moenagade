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

import lu.fisch.moenagade.bloxs.Element;

/**
 *
 * @author robert.fisch
 */
public class Change {
    public Element sender;
    public String cmd;
    public Object from;
    public Object to;
    public int position;

    public Change(Element sender, int position, String cmd, Object from, Object to) {
        this.sender = sender;
        this.position=position;
        this.cmd = cmd;
        this.from = from;
        this.to = to;
    }
    
    public String toString()
    {
        String senderString = "null";
        if(sender!=null) senderString=sender.getClassname();
        return "S: "+senderString+" / P: "+position+" / C: "+cmd+" / F: "+from+" / T: "+to;
    }
    
    
}
