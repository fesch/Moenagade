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
        new Timer(5, new ActionListener() { @Override public void actionPerformed(ActionEvent ae) {
            moveDown(hDir);
            moveRight(vDir);
            if ((getX() + (getWidth() / 2)) > getMouseX())
            {
                vDir = -1;
            }
            else
            {
                vDir = 1;
            }
            if ((getY() + (getHeight() / 2)) > getMouseY())
            {
                hDir = -1;
            }
            else
            {
                hDir = 1;
            }
            if(getWorld()!=null) getWorld().repaint();
        }}).start();
        
    }

}