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

    public int vDir = 1;
    public int hDir = 1;
    public int count = 0;
    
    
    @Override
    public void onTouched(Entity other)
    {
        if(other.getClass().getSimpleName().equals("Berry"))
        {
            count = (count + 1);
            System.out.println(count);
            if (count > 10)
            {
                count = (count - 10);
                getWorld().addEntity(new Raccoon(getWorld()));
            }
        }
    }
    
    
    @Override
    protected void onCreate()
    {
        loadImage("entity.png");
        setX((int)(Math.random() * ((getWorld().getWidth() - getWidth())-0+1)) + 0);
        setY((int)(Math.random() * ((getWorld().getHeight() - getHeight())-0+1)) + 0);
        new Timer(5, new ActionListener() {
            @Override public void actionPerformed(ActionEvent ae)
            {
                    moveDown(hDir);
                moveRight(vDir);
                if ((getX() + getWidth()) > getWorld().getWidth())
                {
                    vDir = -1;
                }
                else if (getX() < 0)
                {
                    vDir = 1;
                }
                if ((getY() + getHeight()) > getWorld().getHeight())
                {
                    hDir = -1;
                }
                else if (getY() < 0)
                {
                    hDir = 1;
                }
                if(getWorld()!=null) getWorld().repaint();
                if(!Raccoon.this.getWorld().isDisplayable())
                {
                    ((Timer)ae.getSource()).stop();
                }
            }
        }).start();				
        
    }

}