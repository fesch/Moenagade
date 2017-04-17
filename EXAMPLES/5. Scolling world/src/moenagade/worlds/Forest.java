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
        loadImage("6-vector-game-backgrounds-8003_imgs_8003_2.png");
        addEntity(new Raccoon(this)).setX(250).setY(500);
        new Timer(10, new ActionListener() {
            @Override public void actionPerformed(ActionEvent ae)
            {
                moveLeft(1);
                if (countEntities("Raccoon") == 0)
                {
                    ((Timer)ae.getSource()).stop();
                }
                if(getWorld()!=null) getWorld().repaint();
            }
        }).start();
        
    }

}