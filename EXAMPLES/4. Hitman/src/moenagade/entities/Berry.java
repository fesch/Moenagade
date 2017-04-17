package moenagade.entities;

import java.awt.event.KeyEvent;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import moenagade.*;
import moenagade.base.*;
import moenagade.worlds.*;
import moenagade.entities.*;

public class Berry extends Entity
{
    public Berry(World world)
    {
        super(world);
    }

    @Override
    protected void onCreate()
    {
        loadImage("berry.png");
    }
    
    
    @Override
    public void onTouched(Entity other)
    {
        if(other.getClass().getSimpleName().equals("Raccoon"))
        {
            delete();
        }
    }
    
    

}