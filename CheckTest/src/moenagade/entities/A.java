package moenagade.entities;

import java.awt.event.KeyEvent;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import moenagade.*;
import moenagade.base.*;
import moenagade.worlds.*;
import moenagade.entities.*;

public class A extends Entity
{
    public A(World world)
    {
        super(world);
    }

    @Override
    protected void onCreate()
    {
        
    }
    
    
    @Override
    public void keyPressed(KeyEvent keyEvent)
    {
        getWorld().playSound("");
    }
    
    

}