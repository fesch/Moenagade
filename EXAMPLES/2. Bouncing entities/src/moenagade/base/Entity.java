/*
    Moenagade
    °°°°°°°°°

    Moenagade is a graphical programming tool for beginners. For this
    reason it is rather limited in its functionality. It aims to help
    students to gain knowledge about the programming structures. Due
    to the explicit conversion to real and executable Java code, it 
    should help to make an easy from block programming to coding.

    Copyright (C) 2009  Bob Fisch

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or any
    later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package moenagade.base;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import javax.imageio.ImageIO;


/**
 *
 * @author robert.fisch
 */
public abstract class Entity implements KeyListener {
    
    private String imageFile = "";
    private Image image = null;
    
    private Point location = new Point(0,0);

    private World world;
    
    private int width;
    private int height;
    
    public Entity(World world) {
        setWorld(world);
        onCreate();
    }
    
    protected void onCreate() 
    {
    }
    
    public void loadImage(String filename)
    {
        imageFile = "/moenagade/images/"+filename;
        // load image
        try 
        {
            image = ImageIO.read(this.getClass().getResource(imageFile));
            width= image.getWidth(null);
            height=image.getHeight(null);
        } 
        catch (IOException ex) 
        {
            ex.printStackTrace();
        }
    }

    public void draw(Graphics g)
    {
        if(image!=null)
        {
            g.drawImage(image, location.x, location.y, null);
        }
    }

    @Override
    public void keyTyped(KeyEvent ke) {
        
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        
    }
    
    public void moveLeft(int pixels)
    {
        location.x-=pixels;
        if(getWorld()!=null) getWorld().repaint();
        getWorld().checkCollisions();
    }
    
    public void moveRight(int pixels)
    {
        location.x+=pixels;
        if(getWorld()!=null) getWorld().repaint();
        getWorld().checkCollisions();
    }

    public void moveUp(int pixels)
    {
        location.y-=pixels;
        if(getWorld()!=null) getWorld().repaint();
        getWorld().checkCollisions();
    }
    
    public void moveDown(int pixels)
    {
        location.y+=pixels;
        if(getWorld()!=null) getWorld().repaint();
        getWorld().checkCollisions();
    }

    public Entity setX(int x)
    {
        location.x=x;
        if(getWorld()!=null) getWorld().repaint();
        getWorld().checkCollisions();
        return this;
    }
    
    public Entity setY(int y)
    {
        location.y=y;
        if(getWorld()!=null) getWorld().repaint();
        getWorld().checkCollisions();
        return this;
    }

    public int getX()
    {
        return location.x;
    }
    
    public int getY()
    {
        return location.y;
    }

    public void setWorld(World world) {
        this.world = world;
    }
    
    public World getWorld() {
        return world;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    
    public int getMouseX()
    {
        return world.getMouseX();
    }

    public int getMouseY()
    {
        return world.getMouseY();
    }

    protected void mousePressed(java.awt.event.MouseEvent evt) {                                  
        
    }

    public boolean mouseInside()
    {
        return (getX()<=getMouseX() && getMouseX()<=getX()+getWidth() &&
                getY()<=getMouseY() && getMouseY()<=getY()+getHeight());
    }

    public void delete()
    {
        if(world!=null)
            world.removeEntity(this);
    }
    
    public Rectangle getRectangle()
    {
        return new Rectangle(location, new Dimension(getWidth(), getHeight()));
    }
    
    public boolean isTouching(Entity other)
    {
        return this.getRectangle().intersects(other.getRectangle());
    }

    public void onTouched(Entity other) 
    {        
    }
    
    public void onOutOfWorld()
    {        
    }
}
