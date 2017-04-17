package moenagade.worlds;

import java.awt.event.KeyEvent;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import moenagade.*;
import moenagade.base.*;
import moenagade.worlds.*;
import moenagade.entities.*;

public class Nico extends World
{
    @Override
    protected void onCreate()
    {
        loadImage("nico.png");
        addEntity(new A(Nico.this));
        getWorld().playSound("pups.wav");
    }
    
    

}