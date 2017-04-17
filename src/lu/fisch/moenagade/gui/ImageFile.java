/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lu.fisch.moenagade.gui;

import java.io.File;

/**
 *
 * @author robert.fisch
 */
public class ImageFile extends File {
    
    public ImageFile(String string) {
        super(string);
    }
    
    @Override
    public String toString() {
        return getName();
    }
    
    
    
}
