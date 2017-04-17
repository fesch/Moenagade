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
        setTitle("Bouncing Raccoon's");
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