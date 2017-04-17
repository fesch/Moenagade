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
        setWorld(new Welcome());
        setTitle("Flappy Raccoon");
    }
    
    

    
    
    public static void main(String args[])
    {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run()
            {
                Project project = new Project();
                project.setVisible(true);
            }
        });
    }
}