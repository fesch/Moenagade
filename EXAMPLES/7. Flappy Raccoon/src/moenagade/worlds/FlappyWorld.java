package moenagade.worlds;

import java.awt.event.KeyEvent;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import moenagade.*;
import moenagade.base.*;
import moenagade.worlds.*;
import moenagade.entities.*;

public class FlappyWorld extends World
{
    public int count = 0;
    
    
    public int size = 100;
    
    
    public int wait = 0;
    
    
    @Override
    protected void onCreate()
    {
        loadImage("flappy_background.png");
        addEntity(new Raccoon(FlappyWorld.this)).setX(100).setY(250);
        new Timer(20, new ActionListener() {
            @Override public void actionPerformed(ActionEvent ae)
            {
                // count is used to determine when the space between the to tubes has to be reduced
                count+=1;
                if (((count % 1000) == 0))
                {
                    size-=5;
                }
                // move the world to the left
                moveLeft(1);
                // stop the movement is the raccoon has gone
                if (countEntities("Raccoon") == 0)
                {
                    ((Timer)ae.getSource()).stop();
                }
                // add a new tube
                if ((int)(Math.random() * (100-0+1)) + 0 == 50 && wait == 0)
                {
                    // total height = 405, margin = 100
                    int whole = (int)(Math.random() * ((305 - size)-100+1)) + 100;
                    // 720 = most right border outside the screen, 23 = height of the top & bottom tube images
                    addEntity(new PipeTop(FlappyWorld.this)).setX(720).setY((whole - 23));
                    addEntity(new PipeBottom(FlappyWorld.this)).setX(720).setY((whole + size));
                    // fill the tube until the top
                    int y = (whole - 23-23);
                    while (y > -23)
                    {
                        addEntity(new Pipe(FlappyWorld.this)).setX(720).setY(y);
                        y = (y - 23);
                    }
                    // fill the tube until the bottom
                    y = (whole + (size + 23));
                    while (y < 405+23)
                    {
                        addEntity(new Pipe(FlappyWorld.this)).setX(720).setY(y);
                        y = (y + 23);
                    }
                    wait = 100;
                }
                // wait is used to make shure tubes are not to close to each others
                if (wait > 0)
                {
                    wait-=1;
                }
                if(getWorld()!=null) getWorld().repaint();
            }
        }).start();
        
    }
    
    

}