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
        new Timer(5, new ActionListener() {
            @Override public void actionPerformed(ActionEvent ae)
            {
                moveRight(dirX);
                if(getWorld()!=null) getWorld().repaint();
            }
        }).start();
        
    }
    
    
    @Override
    public void keyReleased(KeyEvent keyEvent)
    {
        if (keyEvent.getKeyCode() == 39 || keyEvent.getKeyCode() == 37)
        {
            dirX = 0;
        }
    }
    
    
    @Override
    public void keyPressed(KeyEvent keyEvent)
    {
        if (keyEvent.getKeyCode() == 39)
        {
            loadImage("entity.png");
            dirX = 1;
        }
        else if (keyEvent.getKeyCode() == 37)
        {
            loadImage("entityRet.png");
            dirX = -1;
        }
        else if (keyEvent.getKeyCode() == 38 && dirY == 0)
        {
            origY = getY();
            dirY = -1;
            new Timer(5, new ActionListener() {
                @Override public void actionPerformed(ActionEvent ae)
                {
                    moveDown(dirY);
                    if (getY() < (origY - 100))
                    {
                        dirY = 1;
                    }
                    else if (getY() == origY)
                    {
                        dirY = 0;
                        ((Timer)ae.getSource()).stop();
                    }
                    if(getWorld()!=null) getWorld().repaint();
                }
            }).start();
            
        }
    }
    
    
    public int origY = getY();
    
    
    public int dirY = 0;
    
    
    public int dirX = 0;
    
    
    @Override
    public void onTouched(Entity other)
    {
        if(other.getClass().getSimpleName().equals("Ennemy"))
        {
            delete();
        }
    }
    
    

}