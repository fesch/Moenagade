package moenagade.entities;

import java.awt.event.KeyEvent;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import moenagade.*;
import moenagade.base.*;
import moenagade.worlds.*;
import moenagade.entities.*;

public class Raccoon extends Entity
{
    public Raccoon(World world)
    {
        super(world);
    }

    @Override
    protected void onCreate()
    {
        loadImage("entity.png");
    }
    
    
    @Override
    public void keyPressed(KeyEvent keyEvent)
    {
        if (keyEvent.getKeyCode() == 37)
        {
            moveLeft(5);
        }
        if (keyEvent.getKeyCode() == 39)
        {
            moveRight(5);
        }
    }
    
    
    @Override
    public void onOutOfWorld()
    {
        delete();
    }
    
    

}