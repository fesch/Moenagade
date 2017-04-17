package moenagade.worlds;

import java.awt.event.KeyEvent;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import moenagade.*;
import moenagade.base.*;
import moenagade.worlds.*;
import moenagade.entities.*;

public class MyWorld extends World
{
    @Override
    protected void onCreate()
    {
        loadImage("landscape.png");
        addEntity(new Raccoon(this)).setX(200).setY(525);
        new Timer(10, new ActionListener() {
            @Override public void actionPerformed(ActionEvent ae)
            {
                moveLeft(1);
                if (wait == 0 && (int)(Math.random() * (100-0+1)) + 0 == 50)
                {
                    wait = 100;
                    addEntity(new Ennemy(MyWorld.this)).setX(1500).setY(542);
                }
                if (wait > 0)
                {
                    wait = (wait - 1);
                }
                if (countEntities("Raccoon") == 0)
                {
                    ((Timer)ae.getSource()).stop();
                }
                if(getWorld()!=null) getWorld().repaint();
            }
        }).start();
        
    }
    
    
    public int wait = 0;
    
    

}