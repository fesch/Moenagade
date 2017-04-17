package moenagade;

import java.awt.event.KeyEvent;
import moenagade.*;
import moenagade.base.*;
import moenagade.worlds.*;
import moenagade.entities.*;

public class Project extends MainFrame
{
    @Override
    protected void onCreate()
    {
        setWorld(new Forest());
        setTitle("Raccoon, please follow the mouse!");
    }

    
    public static void main(String args[])
    {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run()
            {
                new Project().setVisible(true);
            }
        });
    }    
    
}