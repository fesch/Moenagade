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

    public int vDir = 0;
    public int hDir = 0;
    @Override
    protected void onCreate()
    {
        loadImage("entity.png");
        setY(320);
    }
    public void keyPressed(KeyEvent keyEvent)
    {
        if (keyEvent.getKeyCode() == 37)
        {
            loadImage("entityRet.png");
            moveLeft(10);
        }
        else if (keyEvent.getKeyCode() == 39)
        {
            loadImage("entity.png");
            moveRight(10);
        }
        else if (keyEvent.getKeyCode() == 38)
        {
            moveUp(10);
        }
        else if (keyEvent.getKeyCode() == 40)
        {
            moveDown(10);
        }
    }

}