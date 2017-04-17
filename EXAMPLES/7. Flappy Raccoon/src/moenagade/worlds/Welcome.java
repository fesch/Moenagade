package moenagade.worlds;

import java.awt.event.KeyEvent;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import moenagade.*;
import moenagade.base.*;
import moenagade.worlds.*;
import moenagade.entities.*;

public class Welcome extends World
{
    @Override
    protected void onCreate()
    {
        loadImage("flappy_welcome.png");
    }
    
    
    @Override
    protected void formMousePressed(java.awt.event.MouseEvent evt)
    {
        super.formMousePressed(evt);
        getMain().setWorld(new FlappyWorld());
    }
    
    

}