/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lu.fisch.moenagade;

import java.util.Calendar;

/**
 *
 * @author robert.fisch
 */
public class Top {
    
    private long start = 0;
    
    public Top()
    {
        start = Calendar.getInstance().getTimeInMillis();
    }
    
    public void hit(String text)
    {
        System.out.println("Tick "+text+" > "+(Calendar.getInstance().getTimeInMillis()-start));
        start = Calendar.getInstance().getTimeInMillis();
    }
}
