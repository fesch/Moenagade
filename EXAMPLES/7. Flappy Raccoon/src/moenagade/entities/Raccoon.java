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

    public int hDir = 0;
    
    
    public int vDir = 0;
    
    
    @Override
    public void keyPressed(KeyEvent keyEvent)
    {
        if (keyEvent.getKeyCode() == 37)
        {
            loadImage("flappy_raccoon_left.png");
            hDir = -1;
        }
        if (keyEvent.getKeyCode() == 39)
        {
            loadImage("flappy_raccoon_right.png");
            hDir = 1;
        }
        if (keyEvent.getKeyCode() == 32)
        {
            vDir = 10;
            getWorld().playSound("tick.wav");
        }
    }
    
    
    @Override
    public void onOutOfWorld()
    {
        getWorld().playSound("pups.wav");
        delete();
    }
    
    
    @Override
    public void keyReleased(KeyEvent keyEvent)
    {
        if (keyEvent.getKeyCode() == 37)
        {
            hDir = 0;
        }
        if (keyEvent.getKeyCode() == 39)
        {
            hDir = 0;
        }
    }
    
    
    @Override
    protected void onCreate()
    {
        loadImage("flappy_raccoon_right.png");
        new Timer(20, new ActionListener() {
            @Override public void actionPerformed(ActionEvent ae)
            {
                moveRight((hDir * 5));
                moveDown(1);
                // move up
                if ((vDir > 0))
                {
                    setY((getY() - 5));
                    vDir-=1;
                }
                if(getWorld()!=null) getWorld().repaint();
            }
        }).start();
        
    }
    
    
    @Override
    public void onTouched(Entity other)
    {
        if ((other.getClass().getSimpleName().equals("Pipe") || (other.getClass().getSimpleName().equals("PipeBottom") || other.getClass().getSimpleName().equals("PipeTop"))))
        {
            getWorld().playSound("pups.wav");
            delete();
        }
    }
    
    

}