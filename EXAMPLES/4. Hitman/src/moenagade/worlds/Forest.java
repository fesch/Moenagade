package moenagade.worlds;

import java.awt.event.KeyEvent;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import moenagade.*;
import moenagade.base.*;
import moenagade.worlds.*;
import moenagade.entities.*;

public class Forest extends World
{
    @Override
    protected void onCreate()
    {
        loadImage("besch.png");
        addEntity(new Raccoon(this));
    }
    @Override
    protected void formMousePressed(java.awt.event.MouseEvent evt)
    {
        super.formMousePressed(evt);
        addEntity(new Berry(this)).setX(getMouseX()).setY(getMouseY());
    }
    
    

}