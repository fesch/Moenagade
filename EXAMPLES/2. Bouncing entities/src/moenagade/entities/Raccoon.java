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
    @Override
    protected void onCreate()
    {
        loadImage("entity.png");
        setX((int)(Math.random() * ((getWorld().getWidth() - getWidth())-0+1)) + 0);
        setY((int)(Math.random() * ((getWorld().getHeight() - getHeight())-0+1)) + 0);
        new Timer(5, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                    moveRight(hDir);
            moveDown(vDir);
            if (getX() < 0 || getX() > getWorld().getWidth() - getHeight())
            {
                hDir = 0 - hDir;
                if (hDir > 0)
                {
                    loadImage("entity.png");
                }
                else
                {
                    loadImage("entityRet.png");
                }
            }
            if (getY() < 0 || getY() > getWorld().getHeight() - getHeight())
            {
                vDir = 0 - vDir;
            }
                if(getWorld()!=null) getWorld().repaint();
            }
        }).start();
        
    }
    @Override
    protected void mousePressed(java.awt.event.MouseEvent evt)
    {
        super.mousePressed(evt);
        delete();
    }
    
    

}