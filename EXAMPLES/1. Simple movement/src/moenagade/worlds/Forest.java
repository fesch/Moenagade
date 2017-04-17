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
        loadImage("besch.png");
        addEntity(new Raccoon(this));
    }

}