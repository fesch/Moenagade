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

package lu.fisch.moenagade.model;

import lu.fisch.moenagade.*;
import lu.fisch.moenagade.gui.dialogs.OpenProject;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import lu.fisch.moenagade.bloxs.Element;
import lu.fisch.moenagade.compilation.Runtime6;
import lu.fisch.moenagade.gui.About;
import lu.fisch.moenagade.gui.Change;
import lu.fisch.moenagade.gui.ImageFile;
import lu.fisch.moenagade.gui.SoundFile;
import lu.fisch.utils.StringList;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.xml.sax.SAXException;

/**
 *
 * @author robert.fisch
 */
public class Project {
    
    private boolean changed = false;
    private Frame frame;
    
    private String directoryName = null;
    private String containingDirectoryName = ".";
    
    private TreeMap<String,World> worlds = new TreeMap<>();
    private TreeMap<String,Entity> entities = new TreeMap<>();
    private MainFrame main = new MainFrame("Project");
    
    private BloxsClass selected = null;
    
    private String lastOpenedImage = "";
    private String lastOpenedSound = "";
    private String lastOpenedProject = "";

    public Project(Frame frame) {
        this.frame = frame;
    }
    
    public void clear()
    {
        worlds.clear();
        entities.clear();
        main = new MainFrame("Project");
        main.setProject(this);
        directoryName = null;
        Library.getInstance().setSelected(null);
    }
    
    public boolean newProject()
    {
       if(askToSave()==true)
       {
            clear();
            setChanged(false);
            return true;
       } 
       return false;
    }

    public BloxsClass getMain() {
        return main;
    }
    
    public Entity getEntity(String name)
    {
        return entities.get(name);
    }
    
    public boolean isChanged() {
        return changed;
    }
    
    public boolean isSaved()
    {
        return getDirectoryName()!=null;
    }

    
    public BloxsClass getSelected() {
        return selected;
    }

    /*
     * GETTER & SETTER
     */
    public void setSelected(BloxsClass selected) {    
        this.selected = selected;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }
    
    private boolean isEmpty()
    {
        return false;
    }

    public TreeMap<String, World> getWorlds() {
        return worlds;
    }

    public ArrayList<String> getWorldNames() {
        ArrayList<String> result = new ArrayList<String>();
        for(World world : getWorlds().values())
            result.add(world.getName());
        return result;
    }

    public TreeMap<String, Entity> getEntities() {
        return entities;
    }
    
    public ArrayList<String> getEntityNames() {
        ArrayList<String> result = new ArrayList<String>();
        for(Entity entity : getEntities().values())
            result.add(entity.getName());
        return result;
    }

    public String getDirectoryName()
    {
        return directoryName;
    }

    public void setDirectoryName(String directoryName)
    {
        this.directoryName = directoryName;

        String dir = new String(directoryName);
        if(dir.endsWith(System.getProperty("file.separator"))) dir=dir.substring(0,dir.length()-2);
        dir = dir.substring(0,dir.lastIndexOf(System.getProperty("file.separator")));
        setContainingDirectoryName(dir);
    }
    
    public String getContainingDirectoryName()
    {
        return containingDirectoryName;
    }

    public void setContainingDirectoryName(String containingDirectoryName) {
        this.containingDirectoryName = containingDirectoryName;
    }

    /*
     * ENTITIES
     */
    
    public BloxsClass renameEntity(Entity entity)
    {
        String name = entity.getName();
        boolean result;
        
        do {
            name = (String) JOptionPane.showInputDialog(frame, "Please enter the entitie's name.", "Rename entity", JOptionPane.PLAIN_MESSAGE, null, null, name);

            if(name==null) return null;

            result=true;

            // check if name is OK
            Matcher matcher = Pattern.compile("^[a-zA-Z_$][a-zA-Z_$0-9]*$").matcher(name);
            boolean found = matcher.find();
            if(!found) {
                result=false;
                JOptionPane.showMessageDialog(frame, "Please chose a valid name.", "Error", JOptionPane.ERROR_MESSAGE, Moenagade.IMG_ERROR);
            }
            // check if name is unique
            else if(entities.containsKey(name))
            {
                result=false;
                JOptionPane.showMessageDialog(frame, "This name is not unique.", "Error", JOptionPane.ERROR_MESSAGE, Moenagade.IMG_ERROR);
            }
            // check internal name
            else if(name.trim().equals("Entity"))
            {
                result=false;
                JOptionPane.showMessageDialog(frame, "The name \"Entity\" is already internaly\nused and thus not allowed!", "Error", JOptionPane.ERROR_MESSAGE, Moenagade.IMG_ERROR);
            }
            else if(name.charAt(0)!=name.toUpperCase().charAt(0))
            {
                result=false;
                JOptionPane.showMessageDialog(frame, "The name should start with a capital letter.", "Error", JOptionPane.ERROR_MESSAGE, Moenagade.IMG_ERROR);
            }
            else 
            {
                // send refresh
                refresh(new Change(null, -1, "rename.entity", entity.getName(), name));
                // switch 
                entities.remove(entity.getName());
                entities.put(name, entity);
                // rename file (if present)
                File fFrom = new File(directoryName+System.getProperty("file.separator")+"bloxs"+
                            System.getProperty("file.separator")+"entities"+
                            System.getProperty("file.separator")+entity.getName()+".bloxs");
                File fTo = new File(directoryName+System.getProperty("file.separator")+"bloxs"+
                            System.getProperty("file.separator")+"entities"+
                            System.getProperty("file.separator")+name+".bloxs");
                if(fFrom.exists())
                    fFrom.renameTo(fTo);
                // do the rename
                entity.setName(name);
                return entity;
            }
        }
        while(!result);
        
        return null;
    }
    
    public BloxsClass addEntity()
    {
        String name = "";
        boolean result;
        
        do {
            name = (String) JOptionPane.showInputDialog(frame, "Please enter the entitie's name.", "Add entity", JOptionPane.PLAIN_MESSAGE, null, null, name);

            if(name==null) return null;

            result=true;

            // check if name is OK
            Matcher matcher = Pattern.compile("^[a-zA-Z_$][a-zA-Z_$0-9]*$").matcher(name);
            boolean found = matcher.find();
            if(!found) {
                result=false;
                JOptionPane.showMessageDialog(frame, "Please chose a valid name.", "Error", JOptionPane.ERROR_MESSAGE, Moenagade.IMG_ERROR);
            }
            // check if name is unique
            else if(entities.containsKey(name))
            {
                result=false;
                JOptionPane.showMessageDialog(frame, "This name is not unique.", "Error", JOptionPane.ERROR_MESSAGE, Moenagade.IMG_ERROR);
            }
            // check internal name
            else if(name.trim().equals("Entity"))
            {
                result=false;
                JOptionPane.showMessageDialog(frame, "The name \"Entity\" is already internaly\nused and thus not allowed!", "Error", JOptionPane.ERROR_MESSAGE, Moenagade.IMG_ERROR);
            }
            else if(name.charAt(0)!=name.toUpperCase().charAt(0))
            {
                result=false;
                JOptionPane.showMessageDialog(frame, "The name should start with a capital letter.", "Error", JOptionPane.ERROR_MESSAGE, Moenagade.IMG_ERROR);
            }
            else 
            {
                Entity entity = new Entity(name);
                entity.setProject(this);
                selected = entity;
                entities.put(name, entity);
                
                return entity;
            }
        }
        while(!result);
        
        return null;
    }
    
    /*
     * WORLDS
     */
    
    public BloxsClass renameWorld(World world)
    {
        String name = world.getName();
        boolean result;
        
        do {
            name = (String) JOptionPane.showInputDialog(frame, "Please enter the world's new name.", "Rename world", JOptionPane.PLAIN_MESSAGE, null, null, name);

            if(name==null) return null;

            result=true;

            // check if name is OK
            Matcher matcher = Pattern.compile("^[a-zA-Z_$][a-zA-Z_$0-9]*$").matcher(name);
            boolean found = matcher.find();
            if(!found) {
                result=false;
                JOptionPane.showMessageDialog(frame, "Please chose a valid name.", "Error", JOptionPane.ERROR_MESSAGE, Moenagade.IMG_ERROR);
            }
            // check if name is unique
            else if(worlds.containsKey(name))
            {
                result=false;
                JOptionPane.showMessageDialog(frame, "This name is not unique.", "Error", JOptionPane.ERROR_MESSAGE, Moenagade.IMG_ERROR);
            }
            // check internal name
            else if(name.trim().equals("World"))
            {
                result=false;
                JOptionPane.showMessageDialog(frame, "The name \"World\" is already internaly\nused and thus not allowed!", "Error", JOptionPane.ERROR_MESSAGE, Moenagade.IMG_ERROR);
            }
            else if(name.charAt(0)!=name.toUpperCase().charAt(0))
            {
                result=false;
                JOptionPane.showMessageDialog(frame, "The name should start with a capital letter.", "Error", JOptionPane.ERROR_MESSAGE, Moenagade.IMG_ERROR);
            }
            else 
            {
                // send refresh
                refresh(new Change(null, -1, "rename.world", world.getName(), name));
                // switch 
                worlds.remove(world.getName());
                worlds.put(name, world);
                // rename file (if present)
                File fFrom = new File(directoryName+System.getProperty("file.separator")+"bloxs"+
                            System.getProperty("file.separator")+"worlds"+
                            System.getProperty("file.separator")+world.getName()+".bloxs");
                File fTo = new File(directoryName+System.getProperty("file.separator")+"bloxs"+
                            System.getProperty("file.separator")+"worlds"+
                            System.getProperty("file.separator")+name+".bloxs");
                if(fFrom.exists())
                    fFrom.renameTo(fTo);
                // do the rename
                world.setName(name);
                return world;
            }
        }
        while(!result);
        
        return null;        
    }
    
    public BloxsClass addWorld()
    {
        String name = "";
        boolean result;
        
        do {
            name = (String) JOptionPane.showInputDialog(frame, "Please enter the world's name.", "Add world", JOptionPane.PLAIN_MESSAGE, null, null, name);

            if(name==null) return null;

            result=true;

            // check if name is OK
            Matcher matcher = Pattern.compile("^[a-zA-Z_$][a-zA-Z_$0-9]*$").matcher(name);
            boolean found = matcher.find();
            if(!found) {
                result=false;
                JOptionPane.showMessageDialog(frame, "Please chose a valid name.", "Error", JOptionPane.ERROR_MESSAGE, Moenagade.IMG_ERROR);
            }
            // check if name is unique
            else if(worlds.containsKey(name))
            {
                result=false;
                JOptionPane.showMessageDialog(frame, "This name is not unique.", "Error", JOptionPane.ERROR_MESSAGE, Moenagade.IMG_ERROR);
            }
            // check internal name
            else if(name.trim().equals("World"))
            {
                result=false;
                JOptionPane.showMessageDialog(frame, "The name \"World\" is already internaly\nused and thus not allowed!", "Error", JOptionPane.ERROR_MESSAGE, Moenagade.IMG_ERROR);
            }
            else if(name.charAt(0)!=name.toUpperCase().charAt(0))
            {
                result=false;
                JOptionPane.showMessageDialog(frame, "The name should start with a capital letter.", "Error", JOptionPane.ERROR_MESSAGE, Moenagade.IMG_ERROR);
            }
            else 
            {
                World world = new World(name);
                world.setProject(this);
                selected = world;
                worlds.put(name, world);
                
                return world;
            }
        }
        while(!result);
        
        return null;
    }
    
    /*
     * IMAGES
     */
    
    public boolean renameImage(ImageFile imageFile)
    {
        String name = getFilename(imageFile);
        String ext  = getExtension(imageFile);
        boolean result;
        
        do {
            result=true;

            name = (String) JOptionPane.showInputDialog(frame, "Please enter the image's new name.", "Rename image", JOptionPane.PLAIN_MESSAGE, null, null, name);

            if(name==null) return false;
            
            // check if name is OK
            Matcher matcher = Pattern.compile("^[a-zA-Z_$][a-zA-Z_$0-9]*$").matcher(name);
            boolean found = matcher.find();
            if(!found) {
                result=false;
                JOptionPane.showMessageDialog(frame, "Please chose a valid name.", "Error", JOptionPane.ERROR_MESSAGE, Moenagade.IMG_ERROR);
            }
            // check if name is unique
            else if((new File(directoryName+System.getProperty("file.separator")+"bloxs"+
                            System.getProperty("file.separator")+"images"+
                            System.getProperty("file.separator")+name+"."+ext)).exists())
            {
                result=false;
                JOptionPane.showMessageDialog(frame, "There exists already an image with this name!", "Error", JOptionPane.ERROR_MESSAGE, Moenagade.IMG_ERROR);
            }
            else 
            {
                // send refresh
                refresh(new Change(null, -1, "rename.image", imageFile.toString(), name+"."+ext));
                // do the rename
                File fFrom = new File(directoryName+System.getProperty("file.separator")+"bloxs"+
                            System.getProperty("file.separator")+"images"+
                            System.getProperty("file.separator")+imageFile.toString());
                File fTo = new File(directoryName+System.getProperty("file.separator")+"bloxs"+
                            System.getProperty("file.separator")+"images"+
                            System.getProperty("file.separator")+name+"."+ext);
                if(fFrom.exists())
                    fFrom.renameTo(fTo);
                return true;
            }
        }
        while(!result);
        
        return false;   
    }
    
    /*
     * SOUND
     */
    
    public boolean renameSound(SoundFile soundFile)
    {
        String name = getFilename(soundFile);
        String ext  = getExtension(soundFile);
        boolean result;
        
        do {
            result=true;

            name = (String) JOptionPane.showInputDialog(frame, "Please enter the sound's new name.", "Rename sound", JOptionPane.PLAIN_MESSAGE, null, null, name);

            if(name==null) return false;
            
            // check if name is OK
            Matcher matcher = Pattern.compile("^[a-zA-Z_$][a-zA-Z_$0-9]*$").matcher(name);
            boolean found = matcher.find();
            if(!found) {
                result=false;
                JOptionPane.showMessageDialog(frame, "Please chose a valid name.", "Error", JOptionPane.ERROR_MESSAGE, Moenagade.IMG_ERROR);
            }
            // check if name is unique
            else if((new File(directoryName+System.getProperty("file.separator")+"bloxs"+
                            System.getProperty("file.separator")+"sounds"+
                            System.getProperty("file.separator")+name+"."+ext)).exists())
            {
                result=false;
                JOptionPane.showMessageDialog(frame, "There exists already a sound with this name!", "Error", JOptionPane.ERROR_MESSAGE, Moenagade.IMG_ERROR);
            }
            else 
            {
                // send refresh
                refresh(new Change(null, -1, "rename.sound", soundFile.toString(), name+"."+ext));
                // do the rename
                File fFrom = new File(directoryName+System.getProperty("file.separator")+"bloxs"+
                            System.getProperty("file.separator")+"sounds"+
                            System.getProperty("file.separator")+soundFile.toString());
                File fTo = new File(directoryName+System.getProperty("file.separator")+"bloxs"+
                            System.getProperty("file.separator")+"sounds"+
                            System.getProperty("file.separator")+name+"."+ext);
                if(fFrom.exists())
                    fFrom.renameTo(fTo);
                return true;
            }
        }
        while(!result);
        
        return false;   
    }
    
    /*
     * OPEN & SAVE
     */
    
    public boolean askToSave()
    {
        return askToSave(false);
    }
    
    public boolean askToSave(boolean forceSave)
    {
        try
        {
            if((!isEmpty() && isChanged()) || forceSave)
            {
                int answ = JOptionPane.showConfirmDialog(frame, "Do you want to save the current project?", "Save project?", JOptionPane.YES_NO_CANCEL_OPTION);
                if (answ == JOptionPane.YES_OPTION)
                {
                    if(directoryName==null) return saveWithAskingLocation();
                    else return save();
                }
                else if (answ == JOptionPane.NO_OPTION)
                {
                    return !forceSave;
                }
                else return false;
            }
            return true;
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(frame, "A terrible error occured!\n"+
                        e.getMessage()+"\n","Error", JOptionPane.ERROR_MESSAGE,Moenagade.IMG_ERROR);
            return false;
        }
    }
    
    
    public boolean openProject()
    {
        if(askToSave()==true)
        {
            OpenProject op = new OpenProject(new File(getContainingDirectoryName()),false);
            op.setSelectedFile(new File(lastOpenedProject));
            int result = op.showOpenDialog(frame);
            if(result==OpenProject.APPROVE_OPTION)
            {
                String dirName = op.getSelectedFile().getAbsolutePath().toString();
                lastOpenedProject=dirName;
                this.open(dirName);
                setChanged(false);
                return true;
            } else return false;
        } else return false;
    }
    
    private boolean saveWithAskingLocation()
    {
       OpenProject op = new OpenProject(new File(getContainingDirectoryName()),false);
       op.setSelectedFile(new File(lastOpenedProject));
       //op.setSelectedFile(new File(new File(getDirectoryName()).getName()));
       //op.set
       int result = op.showSaveDialog(frame,"Save");
       if(result==OpenProject.APPROVE_OPTION)
       {
            String dirName = op.getSelectedFile().getAbsolutePath().toString();
            lastOpenedProject=dirName;
            File myDir = new File(dirName);
            if(myDir.exists())
            {
                 JOptionPane.showMessageDialog(frame, "The selected project or directory already exists.\nPlease choose another one ..." , "New project", JOptionPane.WARNING_MESSAGE,Moenagade.IMG_WARNING);
                 return false;
            }
            else
            {
                boolean created = myDir.mkdir();
                if (created==false)
                {
                    JOptionPane.showMessageDialog(frame, "Error while creating the projet directory.\n"+
                        "The project name you specified is probably not valid!\n",
                        "Save project as ...", JOptionPane.ERROR_MESSAGE,Moenagade.IMG_ERROR);
                    return false;
                }
                else
                {
                    try
                    {
                        //System.out.println(dirName);
                        save(dirName);
                        setChanged(false);
                        return true;
                    }
                    catch (Exception e)
                    {
                        JOptionPane.showMessageDialog(frame, "An unknown error occured while saving your projet!\n", "Save project as ...", JOptionPane.ERROR_MESSAGE,Moenagade.IMG_ERROR);
                        return false;
                    }
                }
            }
       } return false;
    }
    
    public void saveMoenagade()
    {
        try
        {
            this.save();
            setChanged(false);
        } 
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(frame, "A terrible error occured!\n"+
                        ex.getMessage()+"\n","Error", JOptionPane.ERROR_MESSAGE,Moenagade.IMG_ERROR);
            setChanged(true);
        }
    }
    
    private void save(String dirName) throws FileNotFoundException, UnsupportedEncodingException, IOException
    {
        setDirectoryName(dirName);
        save();
    }
    
    public void saveNetBeansProject() throws FileNotFoundException, UnsupportedEncodingException, IOException
    {
        // adjust the dirname
        String dir = getDirectoryName();
        if (!dir.endsWith(System.getProperty("file.separator")))
        {
            dir += System.getProperty("file.separator");
        }

        // adjust the filename
        String name = getDirectoryName();
        if (name.endsWith(System.getProperty("file.separator")))
        {
            name = name.substring(0, name.length() - 1);
        }
        name = name.substring(name.lastIndexOf(System.getProperty("file.separator"))+1);

        // create the directory
        File fDir = new File(dir+"nbproject"+System.getProperty("file.separator"));
        fDir.mkdir();

        // variables we need
        String filename;
        StringList content;

        // save the the file "project.xml"
        filename = fDir.getAbsolutePath()+System.getProperty("file.separator")+"project.xml";
        content = new StringList();
        content.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        content.add("<project xmlns=\"http://www.netbeans.org/ns/project/1\">");
        content.add("   <type>org.netbeans.modules.java.j2seproject</type>");
        content.add("   <configuration>");
        content.add("       <data xmlns=\"http://www.netbeans.org/ns/j2se-project/3\">");
        content.add("           <name>"+name+"</name>");
        content.add("           <minimum-ant-version>1.6.5</minimum-ant-version>");
        content.add("           <source-roots>");
        content.add("               <root id=\"src.dir\"/>");
        content.add("           </source-roots>");
        content.add("           <test-roots>");
        content.add("           </test-roots>");
        content.add("       </data>");
        content.add("   </configuration>");
        content.add("</project>");
        content.saveToFile(filename); 

        // save the the file "project.properties"
        filename = fDir.getAbsolutePath()+System.getProperty("file.separator")+"project.properties";
        content = new StringList();
        content.add("build.classes.dir=${build.dir}/classes");
        content.add("build.classes.excludes=**/*.java,**/*.form");
        content.add("build.dir=build");
        content.add("build.generated.dir=${build.dir}/generated");
        content.add("build.generated.sources.dir=${build.dir}/generated-sources");
        content.add("build.sysclasspath=ignore");
        content.add("build.test.classes.dir=${build.dir}/test/classes");
        content.add("build.test.results.dir=${build.dir}/test/results");
        content.add("debug.classpath=\\");
        content.add("   ${run.classpath}");
        content.add("debug.test.classpath=\\");
        content.add("   ${run.test.classpath}");
        content.add("dist.dir=dist");
        content.add("dist.jar=${dist.dir}/"+name+".jar");
        content.add("dist.javadoc.dir=${dist.dir}/doc");
        content.add("excludes=bin");
        content.add("includes=**");
        content.add("jar.compress=true");
        content.add("javac.classpath=\\");
        content.add("   ${libs.swing-layout.classpath}");
        content.add("javac.compilerargs=");
        content.add("javac.deprecation=false");
        content.add("javac.source=1.7");
        content.add("javac.target=1.7");
        content.add("javadoc.additionalparam=");
        content.add("javadoc.author=true");
        content.add("javadoc.encoding=${source.encoding}");
        content.add("javadoc.noindex=false");
        content.add("javadoc.nonavbar=false");
        content.add("javadoc.notree=false");
        content.add("javadoc.private=true");
        content.add("javadoc.splitindex=true");
        content.add("javadoc.use=true");
        content.add("javadoc.version=true");
        content.add("javadoc.windowtitle=");
        //Vector<String> mains = getMains();
        //if(mains.size()>0) content.add("main.class="+mains.get(0));
        //else 
            content.add("main.class=");
        content.add("manifest.file=manifest.mf");
        content.add("meta.inf.dir=${src.dir}/META-INF");
        content.add("platform.active=default_platform");
        content.add("run.classpath=\\");
        content.add("    ${javac.classpath}:\\");
        content.add("   ${build.classes.dir}");
        content.add("run.jvmargs=");
        content.add("   run.test.classpath=\\");
        content.add("   ${javac.test.classpath}:\\");
        content.add("   ${build.test.classes.dir}");
        content.add("source.encoding=UTF-8");
        content.add("src.dir=src");
        content.add("test.src.dir=test");
        content.saveToFile(filename);
    }

    private void open(String dirname)
    {
        clear();
        
        setDirectoryName(dirname);
        // load package
        String filename = directoryName+System.getProperty("file.separator")+Moenagade.PACKAGE_NAME;

        File file = new File(filename);
        if(file.exists())
        {
            StringList content = new StringList();
            content.loadFromFile(filename);
            // load files
            for(int i = 0;i<content.count();i++)
            {
                // do something with the package file
            }
            
            // load project
            try {
                main.setProject(this);
                main.getEditor().loadFromBloxs(directoryName
                        +System.getProperty("file.separator")+"bloxs"
                        +System.getProperty("file.separator")+"Project.bloxs");
            } 
            catch (SAXException | IOException ex) 
            {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "A terrible error occured!\n"+
                    ex.getMessage()+"\n","Error", JOptionPane.ERROR_MESSAGE,Moenagade.IMG_ERROR);
            }/**/
            
            // load worlds
            File worldsDir = new File(directoryName
                    +System.getProperty("file.separator")+"bloxs"
                    +System.getProperty("file.separator")+"worlds");
            File[] files = worldsDir.listFiles();
            for (int i = 0; i < files.length; i++) {
                file = files[i];
                String extension = getExtension(file);
                if (extension != null) {
                    if (extension.equals("bloxs")) 
                    {
                        try {
                            World world = new World(getFilename(file));
                            world.setProject(this);
                            world.getEditor().loadFromBloxs(file.getAbsolutePath());
                            worlds.put(world.getName(), world);
                        } 
                        catch (SAXException | IOException ex) 
                        {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(frame, "A terrible error occured!\n"+
                                ex.getMessage()+"\n","Error", JOptionPane.ERROR_MESSAGE,Moenagade.IMG_ERROR);
                        }
                    }
                }
            }
            
            // load entities
            File entitiesDir = new File(directoryName
                    +System.getProperty("file.separator")+"bloxs"
                    +System.getProperty("file.separator")+"entities");
            files = entitiesDir.listFiles();
            for (int i = 0; i < files.length; i++) {
                file = files[i];
                String extension = getExtension(file);
                if (extension != null) {
                    if (extension.equals("bloxs")) 
                    {
                        try {
                            Entity entity = new Entity(getFilename(file));
                            entity.setProject(this);
                            entity.getEditor().loadFromBloxs(file.getAbsolutePath());
                            entities.put(entity.getName(), entity);
                        } 
                        catch (SAXException | IOException ex) 
                        {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(frame, "A terrible error occured!\n"+
                                ex.getMessage()+"\n","Error", JOptionPane.ERROR_MESSAGE,Moenagade.IMG_ERROR);
                        }
                    }
                }
            }
        }
        else
        {
            
        }
        setChanged(false);
    }
    
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) 
        {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
    
    public static String getFilename(File f) {
        String s = f.getName();
        return s.substring(0, s.length()-getExtension(f).length()-1);
    }
    
    public String getImageDirectory()
    {
        return directoryName
                    +System.getProperty("file.separator")+"bloxs"+
                    System.getProperty("file.separator")+"images"+
                    System.getProperty("file.separator");
    }
    
    public String getSoundDirectory()
    {
        return directoryName
                    +System.getProperty("file.separator")+"bloxs"+
                    System.getProperty("file.separator")+"sounds"+
                    System.getProperty("file.separator");
    }
    
    public void openImage(Frame frame)
    {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File(lastOpenedImage));
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) 
                {
                    return true;
                }

                String extension = getExtension(f);
                if (extension != null) {
                    if (extension.equals("png") ||
                        extension.equals("jpg") ||
                        extension.equals("jpeg")) 
                    {
                            return true;
                    } 
                    else 
                    {
                        return false;
                    }
                }

                return false;
            }

            @Override
            public String getDescription() {
                return "Supported image types.";
            }
        });
        int returnVal = fc.showDialog(frame, "Add image"); 
        if(returnVal == JFileChooser.APPROVE_OPTION)
        {
            loadImage(fc.getSelectedFile());
        }
        
    }
    
    public void loadImage(File source)
    {
        lastOpenedImage=source.getAbsolutePath();
        File dest = new File(getImageDirectory()+source.getName());
        //System.out.println(source);
        //System.out.println(dest);
        try 
        {
            FileUtils.copyFile(source, dest);
        } 
        catch (IOException ex) 
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "A terrible error occured!\n"+
                    ex.getMessage()+"\n","Error", JOptionPane.ERROR_MESSAGE,Moenagade.IMG_ERROR);
        }
    }
    
    public void openSound(Frame frame)
    {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File(lastOpenedSound));
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) 
                {
                    return true;
                }

                String extension = getExtension(f);
                if (extension != null) {
                    if (extension.equals("wav")) 
                    {
                            return true;
                    } 
                    else 
                    {
                        return false;
                    }
                }

                return false;
            }

            @Override
            public String getDescription() {
                return "Supported sound types.";
            }
        });
        int returnVal = fc.showDialog(frame, "Add sound"); 
        if(returnVal == JFileChooser.APPROVE_OPTION)
        {
            loadSound(fc.getSelectedFile());
        }
        
    }
    
    public void loadSound(File source)
    {
        lastOpenedSound=source.getAbsolutePath();
        File dest = new File(getSoundDirectory()+source.getName());
        //System.out.println(source);
        //System.out.println(dest);
        try 
        {
            FileUtils.copyFile(source, dest);
        } 
        catch (IOException ex) 
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "A terrible error occured!\n"+
                    ex.getMessage()+"\n","Error", JOptionPane.ERROR_MESSAGE,Moenagade.IMG_ERROR);
        }
    }
    
    // http://www.rgagnon.com/javadetails/java-0483.html
    public boolean deleteDirectory(File path)
    {
        try
        {
            FileUtils.deleteDirectory(path);
        }
        catch(Exception e)
        {
            return false;
        }
        return true;
    }
    
    public Element check()
    {
        Element result = null;
        
        Element mainCheck = main.check();
        if(mainCheck!=null) result=mainCheck;
        //result = main.check() && result;
        
        for(Entity entity : getEntities().values())
        {
            Element entityCheck = entity.check();
            if(entityCheck!=null) result=entityCheck;
            //result = entity.check() && result;
        }

        for(World world : getWorlds().values())
        {
            Element worldCheck = world.check();
            if(worldCheck!=null) result=worldCheck;
            //result = world.check() && result;
        }
        
        return result;
    }
    
    private void saveFiles()
    {
        if(directoryName!=null)
        {
            // delete the "src" directory
            //IMAGES ARE INSIDE !!!
            deleteDirectory(new File(directoryName+ System.getProperty("file.separator")+"src"+System.getProperty("file.separator")));
            
            File fDir = new File(directoryName + System.getProperty("file.separator")+"versions");
            if(!fDir.exists()) fDir.mkdir();
            
            
            fDir = new File(directoryName + System.getProperty("file.separator")+"src");
            if(!fDir.exists()) fDir.mkdir();
            
            fDir = new File(directoryName + System.getProperty("file.separator")+"src"+
                    System.getProperty("file.separator")+"moenagade");
            if(!fDir.exists()) fDir.mkdir();
            
            fDir = new File(directoryName + System.getProperty("file.separator")+"src"+
                    System.getProperty("file.separator")+"moenagade"+
                    System.getProperty("file.separator")+"base");
            if(!fDir.exists()) fDir.mkdir();
            
            fDir = new File(directoryName + System.getProperty("file.separator")+"src"+
                    System.getProperty("file.separator")+"moenagade"+
                    System.getProperty("file.separator")+"worlds");
            if(!fDir.exists()) fDir.mkdir();
            
            fDir = new File(directoryName + System.getProperty("file.separator")+"src"+
                    System.getProperty("file.separator")+"moenagade"+
                    System.getProperty("file.separator")+"entities");
            if(!fDir.exists()) fDir.mkdir();
            
            fDir = new File(directoryName + System.getProperty("file.separator")+"src"+
                    System.getProperty("file.separator")+"moenagade"+
                    System.getProperty("file.separator")+"images");
            if(!fDir.exists()) fDir.mkdir();
            
            fDir = new File(directoryName + System.getProperty("file.separator")+"src"+
                    System.getProperty("file.separator")+"moenagade"+
                    System.getProperty("file.separator")+"sounds");
            if(!fDir.exists()) fDir.mkdir();
            
            fDir = new File(directoryName + System.getProperty("file.separator")+"bloxs");
            if(!fDir.exists()) fDir.mkdir();

            fDir = new File(directoryName + System.getProperty("file.separator")+"bloxs"+
                    System.getProperty("file.separator")+"worlds");
            if(!fDir.exists()) fDir.mkdir();

            fDir = new File(directoryName + System.getProperty("file.separator")+"bloxs"+
                    System.getProperty("file.separator")+"entities");
            if(!fDir.exists()) fDir.mkdir();
            
            fDir = new File(directoryName + System.getProperty("file.separator")+"bloxs"+
                    System.getProperty("file.separator")+"images");
            if(!fDir.exists()) fDir.mkdir();

            fDir = new File(directoryName + System.getProperty("file.separator")+"bloxs"+
                    System.getProperty("file.separator")+"sounds");
            if(!fDir.exists()) fDir.mkdir();

            // save all bloxs files to the bloxs directory
            
            try 
            {
                main.saveToFile(directoryName+System.getProperty("file.separator")+"bloxs");
            } 
            catch (IOException ex) 
            {
                JOptionPane.showMessageDialog(frame, "Error while saving entitiy '"+main.getName()+"'!\n"+
                    ex.getMessage()+"\n","Error", JOptionPane.ERROR_MESSAGE,Moenagade.IMG_ERROR);
                ex.printStackTrace();
            }

            for(Entity entity : getEntities().values())
            {
                try 
                {
                    entity.saveToFile(directoryName+System.getProperty("file.separator")+"bloxs"+
                            System.getProperty("file.separator")+"entities");
                } 
                catch (IOException ex) 
                {
                    JOptionPane.showMessageDialog(frame, "Error while saving entitiy '"+entity.getName()+"'!\n"+
                        ex.getMessage()+"\n","Error", JOptionPane.ERROR_MESSAGE,Moenagade.IMG_ERROR);
                    ex.printStackTrace();
                }
            }
            
            for(World world : getWorlds().values())
            {
                try 
                {
                    world.saveToFile(directoryName+System.getProperty("file.separator")+"bloxs"+
                            System.getProperty("file.separator")+"worlds");
                } 
                catch (IOException ex) 
                {
                    JOptionPane.showMessageDialog(frame, "Error while saving world '"+world.getName()+"'!\n"+
                        ex.getMessage()+"\n","Error", JOptionPane.ERROR_MESSAGE,Moenagade.IMG_ERROR);
                    ex.printStackTrace();
                }
            }
            
            // copy images
            File imageDir = new File(getImageDirectory());
            File[] files = imageDir.listFiles();
            if(files!=null)
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    String extension = getExtension(file);
                    //System.out.println(file.getName());
                    if (extension != null) {
                        if (extension.equals("png") ||
                            extension.equals("jpg") ||
                            extension.equals("jpeg")) 
                        {
                            File dest = new File(directoryName + System.getProperty("file.separator")+"src"+
                                                    System.getProperty("file.separator")+"moenagade"+
                                                    System.getProperty("file.separator")+"images"+
                                                    System.getProperty("file.separator")+file.getName());
                            try 
                            {
                                Files.copy(file.toPath(), dest.toPath());
                            } 
                            catch (IOException ex) 
                            {
                                JOptionPane.showMessageDialog(frame, "Error while copying image '"+file.getName()+"'!\n"+
                                    ex.getMessage()+"\n","Error", JOptionPane.ERROR_MESSAGE,Moenagade.IMG_ERROR);
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            
            // copy sounds
            File soundDir = new File(getSoundDirectory());
            files = soundDir.listFiles();
            if(files!=null)
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    String extension = getExtension(file);
                    //System.out.println(file.getName());
                    if (extension != null) {
                        if (extension.equals("wav")) 
                        {
                            File dest = new File(directoryName + System.getProperty("file.separator")+"src"+
                                                    System.getProperty("file.separator")+"moenagade"+
                                                    System.getProperty("file.separator")+"sounds"+
                                                    System.getProperty("file.separator")+file.getName());
                            try 
                            {
                                Files.copy(file.toPath(), dest.toPath());
                            } 
                            catch (IOException ex) 
                            {
                                // windows may still block the original ressource, so this can be ignored!
                                // the media player seams not to always free well the ressources
                                
                                /*JOptionPane.showMessageDialog(frame, "Error while copying sound '"+file.getName()+"'!\n"+
                                    ex.getMessage()+"\n","Error", JOptionPane.ERROR_MESSAGE,Moenagade.IMG_ERROR);
                                /**/
                                ex.printStackTrace();
                                
                            }
                        }
                    }
                }
            
            // create backups
            createBackupOf("src");
            createBackupOf("bloxs");
            
            // generate the java code for each bloxs files 
            // and store it into the src folder
            // put game related classes there as well
            generateSource();
        }
    }
    
    private void createBackupOf(String folder)
    {
        // backup SRC directory
        File fDir = new File(directoryName + System.getProperty("file.separator")+folder);
        // check the versions directory
        String versionDir = directoryName+System.getProperty("file.separator")+"versions";
        File versionFile = new File(versionDir);
        if (!versionFile.exists()) versionFile.mkdir();
        // get the new filename
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        String now = dateFormater.format(new Date());
        String zipFilename = folder+"-"+now+".zip";
        zipFilename = directoryName+System.getProperty("file.separator")+"versions"+System.getProperty("file.separator")+zipFilename;
        try
        {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(new File(zipFilename)));
            addToZip(out, now+System.getProperty("file.separator"), fDir);
            out.close();

        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    private void addToZip(ZipOutputStream zo, String baseDir, File directory) throws FileNotFoundException, IOException
    {
        // get all files
        File[] files = directory.listFiles();
        if(files!=null)
            for(int f=0;f<files.length;f++)
            {
                if(files[f].isDirectory())
                {
                    String entry = files[f].getAbsolutePath();
                    entry = entry.substring(directory.getAbsolutePath().length()+1);
                    addToZip(zo,baseDir+entry+"/",files[f]);
                }
                else
                {
                    //System.out.println("File = "+files[f].getAbsolutePath());
                    //System.out.println("List = "+Arraysv.deepToString(excludeExtention));
                    //System.out.println("We got = "+getExtension(files[f]));
                    FileInputStream bi = new FileInputStream(files[f]);

                    String entry = files[f].getAbsolutePath();
                    entry = entry.substring(directory.getAbsolutePath().length()+1);
                    entry = baseDir+entry;
                    ZipEntry ze = new ZipEntry(entry);
                    zo.putNextEntry(ze);
                    byte[] buf = new byte[1024];
                    int anz;
                    while ((anz = bi.read(buf)) != -1)
                    {
                        zo.write(buf, 0, anz);
                    }
                    zo.closeEntry();
                    bi.close();
                }
            }
    }
    
    
    private StringList getSaveContent()
    {
        StringList content = new StringList();
        
        // generate the content of the package file here
        
        return content;
    }
    
    public boolean save() throws FileNotFoundException, UnsupportedEncodingException, IOException
    {
        if(directoryName!=null)
        {
            // save the "package"
            String filename = directoryName+System.getProperty("file.separator")+Moenagade.PACKAGE_NAME;
            StringList content = getSaveContent();
            content.saveToFile(filename);
            
            // save netbeans save file
            saveNetBeansProject();
            
            // save the java files
            saveFiles();
            
            return true;
        } 
        else return saveWithAskingLocation();
    }

    public void generateSource() {
        generateSource(false);
    }
    
    public void generateSource(boolean closeOnExit) {
        
        // save project
        try 
        {
            main.saveSourceToFile(directoryName+
                    System.getProperty("file.separator")+"src"+
                    System.getProperty("file.separator")+"moenagade"+
                    System.getProperty("file.separator"), closeOnExit);
        } 
        catch (IOException ex) 
        {
            JOptionPane.showMessageDialog(frame, "Error while saving project source '"+main.getName()+"'!\n"+
                ex.getMessage()+"\n","Error", JOptionPane.ERROR_MESSAGE,Moenagade.IMG_ERROR);
            ex.printStackTrace();
        }
        
        // save source for entities
        for(Entity entity : getEntities().values())
        {
            try 
            {
                entity.saveSourceToFile(directoryName+
                        System.getProperty("file.separator")+"src"+
                        System.getProperty("file.separator")+"moenagade"+
                        System.getProperty("file.separator")+"entities"+
                        System.getProperty("file.separator"));
            } 
            catch (IOException ex) 
            {
                JOptionPane.showMessageDialog(frame, "Error while saving entitiy source '"+entity.getName()+"'!\n"+
                    ex.getMessage()+"\n","Error", JOptionPane.ERROR_MESSAGE,Moenagade.IMG_ERROR);
                ex.printStackTrace();
            }
        }
        // save source for worlds
        for(World world : getWorlds().values())
        {
            try 
            {
                world.saveSourceToFile(directoryName+
                        System.getProperty("file.separator")+"src"+
                        System.getProperty("file.separator")+"moenagade"+
                        System.getProperty("file.separator")+"worlds"+
                        System.getProperty("file.separator"));
            } 
            catch (IOException ex) 
            {
                JOptionPane.showMessageDialog(frame, "Error while saving world source '"+world.getName()+"'!\n"+
                    ex.getMessage()+"\n","Error", JOptionPane.ERROR_MESSAGE,Moenagade.IMG_ERROR);
                ex.printStackTrace();
            }
        }    
        // copy files
        File file;
        
        InputStream in = getClass().getResourceAsStream("/lu/fisch/moenagade/base/MainFrame.txt"); 
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        try {
            String content = IOUtils.toString(reader);
            String filename = directoryName+
                    System.getProperty("file.separator")+"src"+
                    System.getProperty("file.separator")+"moenagade"+
                    System.getProperty("file.separator")+"base"+
                    System.getProperty("file.separator")+"MainFrame.java";
            FileOutputStream fos = new FileOutputStream(filename);
            Writer out = new OutputStreamWriter(fos, "UTF-8");
            out.write(content);
            out.close();   
        }
        catch (IOException ex) 
        {
            JOptionPane.showMessageDialog(frame, "Error while saving mainframe (base) source!\n"+
                ex.getMessage()+"\n","Error", JOptionPane.ERROR_MESSAGE,Moenagade.IMG_ERROR);
            ex.printStackTrace();
        }
        
        /*
        file = new File(this.getClass().getResource("/lu/fisch/moenagade/base/MainFrame.txt").getFile());
        try(FileInputStream inputStream = new FileInputStream(file)) {     
            String content = IOUtils.toString(inputStream);
            String filename = directoryName+
                    System.getProperty("file.separator")+"src"+
                    System.getProperty("file.separator")+"moenagade"+
                    System.getProperty("file.separator")+"base"+
                    System.getProperty("file.separator")+"MainFrame.java";
            FileOutputStream fos = new FileOutputStream(filename);
            Writer out = new OutputStreamWriter(fos, "UTF-8");
            out.write(content);
            out.close();   
        } 
        catch (IOException ex) 
        {
            JOptionPane.showMessageDialog(frame, "Error while saving mainframe (base) source!\n"+
                ex.getMessage()+"\n","Error", JOptionPane.ERROR_MESSAGE,Moenagade.IMG_ERROR);
            ex.printStackTrace();
        }
        */
        
        /*
        file = new File(this.getClass().getResource("/lu/fisch/moenagade/base/Entity.txt").getFile());
        try(FileInputStream inputStream = new FileInputStream(file)) {     
            String content = IOUtils.toString(inputStream);*/
        in = getClass().getResourceAsStream("/lu/fisch/moenagade/base/Entity.txt"); 
        reader = new BufferedReader(new InputStreamReader(in));
        try {
            String content = IOUtils.toString(reader);
            String filename = directoryName+
                    System.getProperty("file.separator")+"src"+
                    System.getProperty("file.separator")+"moenagade"+
                    System.getProperty("file.separator")+"base"+
                    System.getProperty("file.separator")+"Entity.java";
            FileOutputStream fos = new FileOutputStream(filename);
            Writer out = new OutputStreamWriter(fos, "UTF-8");
            out.write(content);
            out.close();   
        }
        catch (IOException ex) 
        {
            JOptionPane.showMessageDialog(frame, "Error while saving entity (base) source!\n"+
                ex.getMessage()+"\n","Error", JOptionPane.ERROR_MESSAGE,Moenagade.IMG_ERROR);
            ex.printStackTrace();
        }
        
        /*file = new File(this.getClass().getResource("/lu/fisch/moenagade/base/World.txt").getFile());
        try(FileInputStream inputStream = new FileInputStream(file)) {     
            String content = IOUtils.toString(inputStream);*/
        in = getClass().getResourceAsStream("/lu/fisch/moenagade/base/World.txt"); 
        reader = new BufferedReader(new InputStreamReader(in));
        try {
            String content = IOUtils.toString(reader);
            String filename = directoryName+
                    System.getProperty("file.separator")+"src"+
                    System.getProperty("file.separator")+"moenagade"+
                    System.getProperty("file.separator")+"base"+
                    System.getProperty("file.separator")+"World.java";
            FileOutputStream fos = new FileOutputStream(filename);
            Writer out = new OutputStreamWriter(fos, "UTF-8");
            out.write(content);
            out.close();   
        }
        catch (IOException ex) 
        {
            JOptionPane.showMessageDialog(frame, "Error while saving world (base) source!\n"+
                ex.getMessage()+"\n","Error", JOptionPane.ERROR_MESSAGE,Moenagade.IMG_ERROR);
            ex.printStackTrace();
        }
    }
    
    public ArrayList<String> getImageNames()
    {
        ArrayList<String> result = new ArrayList<String>();
        // images
        File imageDir = new File(getImageDirectory());
        File[] files = imageDir.listFiles();
        if(files!=null)
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                String extension = getExtension(file);
                if (extension != null) {
                    if (extension.equals("png") ||
                        extension.equals("jpg") ||
                        extension.equals("jpeg")) 
                    {
                        result.add(file.getName());
                    }
                }
            }
        return result;
    }
    
    public ArrayList<String> getSoundNames()
    {
        ArrayList<String> result = new ArrayList<String>();
        // images
        File imageDir = new File(getSoundDirectory());
        File[] files = imageDir.listFiles();
        if(files!=null)
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                String extension = getExtension(file);
                if (extension != null) {
                    if (extension.equals("wav")) 
                    {
                        result.add(file.getName());
                    }
                }
            }
        return result;
    }
    
    
    public void about()
    {
        About a = new About(frame);
        a.setLocationRelativeTo(frame);
        a.setVisible(true);
        //System.err.println("about");
    }
    
    private boolean readyToRunOrCompile()
    {
        if(worlds.size()==0)
        {
            JOptionPane.showMessageDialog(frame, "Sorry, but you need to define at least one world ...","Error", JOptionPane.ERROR_MESSAGE,Moenagade.IMG_ERROR);
            return false;
        }
        /*else if(entities.size()==0)
        {
            JOptionPane.showMessageDialog(frame, "Sorry, but you need to define at least one entity ...","Error", JOptionPane.ERROR_MESSAGE,Moenagade.IMG_ERROR);
            return false;
        }/**/
        
        Element check = check();
        if(check!=null)
        {
            JOptionPane.showMessageDialog(frame, 
                    "Sorry, but your project still contains errors!\nPlease correct them and try again ...\n\n"
                    + "Class: "+check.getTopMostElement().getEditor().getBloxsClass().getName()+"\n"
                    + "Element: "+check.getClassname()+"\n"
                    + "Parent: "+check.getParent().getClassname(),"Error", JOptionPane.ERROR_MESSAGE,Moenagade.IMG_ERROR);
            return false;
        }    
        
        return true;
    }
    
    private boolean compile()
    {
        // get all the files content
        Hashtable<String,String> codes = new Hashtable<>();
        String dir = directoryName+System.getProperty("file.separator")+"src";
        Collection files = FileUtils.listFiles(new File(dir), new String[] {"java"}, true);
        for (Iterator iterator = files.iterator(); iterator.hasNext();) 
        {
            File file = (File) iterator.next();   
            try 
            {
                codes.put(file.getPath().substring(dir.length()+1).replace(System.getProperty("file.separator"), ".").replace(".java", ""),IOUtils.toString(file.toURI(), "utf-8"));
            } 
            catch (IOException ex2) 
            {
                JOptionPane.showMessageDialog(frame, "Error while saving world (base) source!\n"+
                    ex2.getMessage()+"\n","Error", JOptionPane.ERROR_MESSAGE,Moenagade.IMG_ERROR);
                ex2.printStackTrace();
            }
        }
        
        // compile 
        try 
        {
            //Runtime6.getInstance().executeCommand("System.setProperty(\"user.dir\",\"" + directoryName + "\")");
            Runtime6.getInstance().setRootDirectory(directoryName+System.getProperty("file.separator")+"src");
            Runtime6.getInstance().compile(codes, "");
            //Runtime6.getInstance().compileToPath(sourceFiles, directoryName+System.getProperty("file.separator")+"bin", "7", "");
        } 
        catch (ClassNotFoundException ex) 
        {
            JOptionPane.showMessageDialog(frame, "Error while compiling!\n"+
                ex.getMessage()+"\n","Error", JOptionPane.ERROR_MESSAGE,Moenagade.IMG_ERROR);
            ex.printStackTrace();
            return false;
        } 
        
        return true;
    }

    public void run() {
        // do some garbage collection ...
        System.gc();
        
        if(!readyToRunOrCompile())
            return;
        
        saveFiles();
        
        compile();
        
        try
        {
            Class c = Runtime6.getInstance().load("moenagade.Project");
            //System.out.println("Loaded: "+c.getCanonicalName());
            
            Method m = c.getDeclaredMethod("main", String[].class);
            String[] params = null;
            m.invoke(null, (Object) params);
        } 
        catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) 
        {
            JOptionPane.showMessageDialog(frame, "Error while loading!\n"+
                ex.getMessage()+"\n","Error", JOptionPane.ERROR_MESSAGE,Moenagade.IMG_ERROR);
            ex.printStackTrace();
        }
    }
    
    public void refresh(Change change)
    {
        // main
        main.refresh(change);
        // entities
        for(Entity entity : getEntities().values())
        {
            entity.refresh(change);
        }
        // worlds
        for(World world : getWorlds().values())
        {
            world.refresh(change);
        }
    }

    public boolean removeEntity(Entity entity) {
        int answ = JOptionPane.showConfirmDialog(frame, "Are you sure to remove the entity \""+entity.getName()+"\"", "Remove an entity", JOptionPane.YES_NO_OPTION);
        if (answ == JOptionPane.YES_OPTION)
        {
            entities.remove(entity.getName());
            refresh(new Change(null, -1, "delete.entity", entity, null));
            return true;
        }
        return false;
    }

    public boolean removeWorld(World world) {
        int answ = JOptionPane.showConfirmDialog(frame, "Are you sure to remove the world \""+world.getName()+"\"", "Remove a world", JOptionPane.YES_NO_OPTION);
        if (answ == JOptionPane.YES_OPTION)
        {
            worlds.remove(world.getName());
            refresh(new Change(null, -1, "delete.world", world, null));
            return true;
        }
        return false;
    }

    public boolean removeImage(ImageFile imageFile) {
        int answ = JOptionPane.showConfirmDialog(frame, "Are you sure to remove the image \""+imageFile.getName()+"\"", "Remove an image", JOptionPane.YES_NO_OPTION);
        if (answ == JOptionPane.YES_OPTION)
        {
            try 
            {
                Files.delete(imageFile.toPath());
                refresh(new Change(null, -1, "delete.image", imageFile, null));
                return true;
            } 
            catch (IOException ex) 
            {
                ex.printStackTrace();
                return false;
            }

        }
        return false;
    }
    
    public boolean removeSound(SoundFile soundFile) {
        int answ = JOptionPane.showConfirmDialog(frame, "Are you sure to remove the sound \""+soundFile.getName()+"\"", "Remove a sound", JOptionPane.YES_NO_OPTION);
        if (answ == JOptionPane.YES_OPTION)
        {
            try 
            {
                Files.delete(soundFile.toPath());
                refresh(new Change(null, -1, "delete.sound", soundFile, null));
                return true;
            } 
            catch (IOException ex) 
            {
                ex.printStackTrace();
                return false;
            }

        }
        return false;
    }
    
    
    
    public void jar()
    {
        try
        {
            // compile all
            if(!save())
                return;
            
            generateSource(true);
            if(compile())
            {
                // adjust the dirname
                String bdir = getDirectoryName();
                if (!bdir.endsWith(System.getProperty("file.separator")))
                {
                    bdir += System.getProperty("file.separator");
                }

                // adjust the filename
                String bname = getDirectoryName();
                if (bname.endsWith(System.getProperty("file.separator")))
                {
                    bname = bname.substring(0, bname.length() - 1);
                }
                bname = bname.substring(bname.lastIndexOf(System.getProperty("file.separator"))+1);

                // default class to launch
                String mc = "moenagade.Project";
                
                // target JVM
                String target = "1.8";

                /*
                String[] targets = new String[]{"1.1","1.2","1.3","1.5","1.6"};
                if(System.getProperty("java.version").startsWith("1.7"))
                    targets = new String[]{"1.1","1.2","1.3","1.5","1.6","1.7"};
                if(System.getProperty("java.version").startsWith("1.8"))
                    targets = new String[]{"1.1","1.2","1.3","1.5","1.6","1.7","1.8"};

                target= (String) JOptionPane.showInputDialog(
                                   frame,
                                   "Please enter version of the JVM you want to target.",
                                   "Target JVM",
                                   JOptionPane.QUESTION_MESSAGE,
                                   Moenagade.IMG_QUESTION,
                                   targets,
                                   "1.6");*/
               
                File fDir = new File(directoryName + System.getProperty("file.separator")+"bin");
                if(!fDir.exists()) fDir.mkdir();
            
                
                // get all the files content
                Hashtable<String,String> codes = new Hashtable<>();
                String srcdir = directoryName+System.getProperty("file.separator")+"src";
                Collection files = FileUtils.listFiles(new File(srcdir), new String[] {"java"}, true);
                
                File[] javas = new File[files.size()];
                int i=0;
                for (Iterator iterator = files.iterator(); iterator.hasNext();) 
                {
                    File file = (File) iterator.next(); 
                    javas[i++]=file;
                }
                
                try 
                {
                    // make class files
                    Runtime6.getInstance().compileToPath(javas, fDir.getAbsolutePath(), target, "");

                    StringList manifest = new StringList();
                    manifest.add("Manifest-Version: 1.0");
                    manifest.add("Created-By: "+Moenagade.E_VERSION+" "+Moenagade.E_VERSION);
                    manifest.add("Name: "+bname);
                    if(mc!=null)
                    {
                        manifest.add("Main-Class: "+mc);
                    }

                    // compose the filename
                    fDir = new File(bdir+"dist"+System.getProperty("file.separator"));
                    fDir.mkdir();
                    bname = bdir + "dist"+System.getProperty("file.separator") + bname + ".jar";
                    String baseName = bdir;
                    String libFolderName = bdir + "lib";
                    String distLibFolderName = bdir + "dist" + System.getProperty("file.separator") + "lib";

                    File outFile = new File(bname);
                    FileOutputStream bo = new FileOutputStream(bname);
                    JarOutputStream jo = new JarOutputStream(bo);

                    String dirname = getDirectoryName();
                    if (!dirname.endsWith(System.getProperty("file.separator")))
                    {
                        dirname += System.getProperty("file.separator");
                    }
                    // add the files to the array
                    addToJar(jo,"",new File(dirname+"bin"+System.getProperty("file.separator")));
                    // add the files to the array
                    addToJar(jo,"",new File(dirname+"src"+System.getProperty("file.separator")),new String[]{"java"});
                    
                    //manifest.add("Class-Path: "+cp+" "+cpsw);

                    // adding the manifest file
                    manifest.add("");
                    JarEntry je = new JarEntry("META-INF/MANIFEST.MF");
                    jo.putNextEntry(je);
                    String mf = manifest.getText();
                    jo.write(mf.getBytes(), 0, mf.getBytes().length);

                    jo.close();
                    bo.close();

                    // delete bin directory
                    deleteDirectory(new File(getDirectoryName()+System.getProperty("file.separator")+
                            "bin"+System.getProperty("file.separator")));
                    // generate java code with dispose_on_exit
                    generateSource();
                    
                    JOptionPane.showMessageDialog(frame, "The JAR-archive has been generated and can\nbe found in the \"dist\" directory.", "Success", JOptionPane.INFORMATION_MESSAGE,Moenagade.IMG_INFO);
                } 
                catch (ClassNotFoundException ex) 
                {
                    ex.printStackTrace();
                }
            }
        }
        /*catch (ClassNotFoundException ex)
        {
            JOptionPane.showMessageDialog(frame, "There was an error while creating the JAR-archive ...", "Error :: ClassNotFoundException", JOptionPane.ERROR_MESSAGE,Unimozer.IMG_ERROR);
        }*/
        catch (IOException ex)
        {
            JOptionPane.showMessageDialog(frame, "There was an error while creating the JAR-archive ...", "Error :: IOException", JOptionPane.ERROR_MESSAGE,Moenagade.IMG_ERROR);
            ex.printStackTrace();
        }
    }
    
    private void addToJar(JarOutputStream jo, String baseDir, File directory) throws FileNotFoundException, IOException
    {
        addToJar( jo, baseDir, directory, new String[]{});
    }
    
    private void addToJar(JarOutputStream jo, String baseDir, File directory, String[] excludeExtention) throws FileNotFoundException, IOException
    {
        // get all files
        File[] files = directory.listFiles();
        for(int f=0;f<files.length;f++)
        {
            if(files[f].isDirectory())
            {
                String entry = files[f].getAbsolutePath();
                entry = entry.substring(directory.getAbsolutePath().length()+1);
                addToJar(jo,baseDir+entry+"/",files[f],excludeExtention);
            }
            else
            {
                //System.out.println("File = "+files[f].getAbsolutePath());
                //System.out.println("List = "+Arrays.deepToString(excludeExtention));
                //System.out.println("We got = "+getExtension(files[f]));
                if(!Arrays.asList(excludeExtention).contains(getExtension(files[f])))
                {

                    FileInputStream bi = new FileInputStream(files[f]);

                    String entry = files[f].getAbsolutePath();
                    entry = entry.substring(directory.getAbsolutePath().length()+1);
                    entry = baseDir+entry;
                    JarEntry je = new JarEntry(entry);
                    jo.putNextEntry(je);
                    byte[] buf = new byte[1024];
                    int anz;
                    while ((anz = bi.read(buf)) != -1)
                    {
                        jo.write(buf, 0, anz);
                    }
                    bi.close();
                }
            }
        }
    }
}
