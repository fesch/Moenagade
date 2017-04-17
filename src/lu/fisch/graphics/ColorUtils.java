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

package lu.fisch.graphics;

import java.awt.Color;

/**
 *
 * @author robert.fisch
 */
public class ColorUtils {
    
    public static Color getHighlight(Color color)
    {
        float hsbVals[] = Color.RGBtoHSB(color.getRed(),color.getGreen(),color.getBlue(), null);
        return Color.getHSBColor( hsbVals[0], hsbVals[1], 0.5f * ( 1f + hsbVals[2] ));
    }   
    
    public static Color getShadow(Color color)
    {
        float hsbVals[] = Color.RGBtoHSB(color.getRed(),color.getGreen(),color.getBlue(), null);
        return Color.getHSBColor( hsbVals[0], hsbVals[1], 0.5f * hsbVals[2] );
    }   
    
    public static Color getDarker(Color color)
    {
        float hsbVals[] = Color.RGBtoHSB(color.getRed(),color.getGreen(),color.getBlue(), null);
        return Color.getHSBColor( hsbVals[0], hsbVals[1], 0.8f * hsbVals[2] );
    }   
    
    public static Color getColor(String hex)
    {
        hex=hex.replace("#", "");
        return Color.decode("0x"+hex);
    }
    
    public static String getHexColor(Color _color)
    {
        String rgb = Integer.toHexString(_color.getRGB());
        return rgb.substring(2, rgb.length());
    }
}
