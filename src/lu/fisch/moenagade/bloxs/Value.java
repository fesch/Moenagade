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
import java.awt.Rectangle;
import lu.fisch.graphics.ColorUtils;
import lu.fisch.moenagade.bloxs.Element;
import static lu.fisch.moenagade.bloxs.Element.fontsize;
import lu.fisch.moenagade.model.BloxsDefinition;

/**
 *
 * @author robert.fisch
 */
public class Value extends Element {

    public Value() {
        super("");
        init();
    }
    
    public Value(String title) {
        super(title);
        init();
    }

    public Value(BloxsDefinition bd) {
        super(bd);
        init();
    }
    
    private void init()
    {
        color = ColorUtils.getColor("FFFFFF");
        setType(Type.VALUE);
        setClassname("Value");
    }
    
    @Override
    public Rectangle draw(Graphics2D g, Point topline)
    {
        Rectangle result = new Rectangle();
        this.offset=topline;
        
        // set font
        g.setFont(new Font("Monospaced", Font.BOLD, fontsize));
        //headDim.width=2*PADDING_LR+g.getFontMetrics().stringWidth(title);
        headDim=getHeadDimension(g);
        result = new Rectangle(offset, new Dimension(headDim.width, getTotalHeight(g)));
        
        g.setColor(color);
        g.fillRoundRect(topline.x, topline.y+2, 
                        headDim.width, headDim.height, 
                        0,0);
        if(hasError)
            g.setColor(Element.ERROR_COLOR);
        else if(dockBody)
            g.setColor(ColorUtils.getHighlight(color));
        else
            g.setColor(ColorUtils.getShadow(color));
        g.drawRoundRect(topline.x, topline.y+2, 
                        headDim.width, headDim.height, 
                        0,0);
        
        // set font
        g.setFont(new Font("Monospaced", Font.BOLD, fontsize));
        g.setColor(Color.BLACK);
        int sw = g.getFontMetrics().stringWidth(title);
        int sh = g.getFontMetrics().getAscent();
        g.drawString(title, topline.x+(headDim.width-sw)/2, topline.y+(headDim.height+sh)/2);
        
        return result;
    }

    
    
   
}
