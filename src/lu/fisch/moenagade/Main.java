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

package lu.fisch.moenagade;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.TreeSet;
import java.util.jar.JarFile;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author robertfisch
 */
public class Main
{

    public static String classpath = null;
    
    /**
     * @param args the command line arguments
     */
    public static void addJarFile(File file) throws Exception
    {
      URL url = file.toURI().toURL();
      URLClassLoader classLoader
             = (URLClassLoader) ClassLoader.getSystemClassLoader();
      Class clazz= URLClassLoader.class;

      // Use reflection
      Method method= clazz.getDeclaredMethod("addURL", new Class[] { URL.class });
      method.setAccessible(true);
      method.invoke(classLoader, new Object[] { url });
    }

    private static boolean isRunningJavaWebStart() {
        return System.getProperty("javawebstart.version", null) != null;
    }


    public static void main(String[] args) 
    {
        // start Unibloxs
        Moenagade.messages.add("Starting Moenagade ...");
        
        Moenagade.messages.add("Removing the security manager ...");
        try
        {
            System.setSecurityManager(null);
        }
        catch (Exception e)
        {
            Moenagade.messages.add(e.getMessage());
        }
        
        // apply look and feeld
        Moenagade.messages.add("Apply look and feel ...");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            /*if(System.getProperty("os.name").toLowerCase().contains("os x"))
                UIManager.setLookAndFeel(new javax.swing.plaf.nimbus.NimbusLookAndFeel());
            else
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());/**/
            //SwingUtilities.updateComponentTreeUI(mainform);
            //updateComponentTreeUI(mainform);
        } 
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) 
        //catch (UnsupportedLookAndFeelException ex)
        {
            ex.printStackTrace();
            Moenagade.messages.add("Applying look and feel failed!");
        }
        
        Moenagade.messages.add("--- OS");
        Moenagade.messages.add("Name  = "+System.getProperty("os.name"));
        Moenagade.messages.add("Version  = "+System.getProperty("os.version"));
        Moenagade.messages.add("--- Java");
        Moenagade.messages.add("Version  = "+System.getProperty("java.version"));
        Moenagade.messages.add("Vendor = "+System.getProperty("java.vendor"));
        Moenagade.messages.add("Home = "+System.getProperty("java.home"));
        Moenagade.messages.add("User = "+System.getProperty("user.name"));

        Moenagade.messages.add("--- JWS");
        // we need to find the file "swing-layout...jar"
        if(isRunningJavaWebStart())
        {
            Moenagade.messages.add("We are running JWS ...");
            try 
            {
                /*
                 * Task #0 >> Find <tools.jar>
                 */
                Moenagade.messages.add("---");
                Moenagade.messages.add("Searching <swing-layout-???.jar> ...");
                // All jars have an manifest
                Enumeration<URL> e2 = Thread.currentThread().getContextClassLoader().getResources("META-INF/MANIFEST.MF");
                while(e2.hasMoreElements()) 
                {
                    URL u = e2.nextElement();
                    String urlString = u.toExternalForm(); 

                    // skip unused libs
                    if (!(urlString.indexOf("swing-layout")>0)) 
                    {
                        continue;
                    }  
                    // index of .jar because the resource is behind it; “foo.jar!META-INF/MANIFEST.MF”
                    int jarIndex = urlString.lastIndexOf(".jar");
                    // skip non jar code
                    if (jarIndex<1) 
                    {
                        continue;
                    }                     
                    
                    JarFile cachedFile = ((JarURLConnection)u.openConnection()).getJarFile();
                    File tempFile = File.createTempFile("cached-",".jar");
                    tempFile.deleteOnExit();
                    copyFile(new File(cachedFile.getName()),tempFile);
                    //String jarLocation = "file:"+tempFile.getAbsolutePath();
                    classpath = tempFile.getAbsolutePath();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else    // we can use the local file
        {
            Moenagade.messages.add("No JWS detected ...");

            /*
             * Task #0 >> Find <tools.jar>
             */
            Moenagade.messages.add("---");
            Moenagade.messages.add("Searching <swing-layout-???.jar> ...");

            Moenagade.messages.add("Asking the class loder ...");
            // Let's search for the path of the swing-layout JAR file in the files loaded by the classloader
            ClassLoader cl = (new Main()).getClass().getClassLoader();
            int i = 0;
            while(cl!=null)
            {
                if(cl instanceof URLClassLoader)
                {
                    URL[] urls = ((URLClassLoader) cl).getURLs();
                    for(int c=0;c<urls.length;c++)
                    {
                        if(urls[c].getPath().contains("swing-layout-"))
                        {
                            try
                            {
                                File f = new File(urls[c].toURI());
                                if(!f.exists())
                                {
                                    String swingName = urls[c].getPath().toString().replace("swing-layout-","lib/swing-layout-");
                                    f = new File(swingName);
                                }
                                Moenagade.messages.add("Found it using the classloader in "+f.getAbsolutePath());
                                classpath=f.getAbsolutePath();
                            } 
                            catch (URISyntaxException ex)
                            {
                                // ignore
                            }
                        }
                    }
                }

                cl=cl.getParent();
                i++;
            }
            // Next, search in the boot-folder "lib/"
            if(classpath==null)
            {
                Moenagade.messages.add("Searching in ./lib/ ...");
                // get all files from the boot folder
                File here = new File("./lib/");
                File[] files = here.listFiles();
                if(files!=null)
                {
                    for(int f=0;f<files.length;f++)
                    {
                        if(files[f].getName().contains("swing-layout-"))
                        {
                            classpath=files[f].getAbsolutePath();
                            Moenagade.messages.add("Found it in the ./lib/ folder ...");
                        }
                    }
                }
            }
        }
        Moenagade.messages.add("classpath = "+classpath);
        
        /*
         * Task #1 >> Find <tools.jar>
         */
        Moenagade.messages.add("---");
        Moenagade.messages.add("Searching <tools.jar> ...");

        // is it in the path?
        Moenagade.messages.add("Searching in the actual path ...");
        boolean inPath = true;
        try
        {
            Class.forName("com.sun.tools.javadoc.Main");
            Moenagade.messages.add("<tools.jar> has been found somewhere in the path.");
        }
        catch (java.lang.UnsupportedClassVersionError ex)
        {
            Moenagade.messages.add("<tools.jar> has been found but is compiled with a wrong JDK version.");
            inPath=false;
        }
        catch (Exception ex)
        {
            inPath=false;
        }
        catch (Error ex)
        {
            inPath=false;
        }
        
        // if we are running Java 1.7, try to locate an installed JDK anyway ...
        
        if(System.getProperty("java.version").startsWith("1.7"))
        {
            Moenagade.messages.add("Trying to locate a locally installed JDK anyway ...");
            inPath=false;
        }
        
        // default lib folder
        String lib = System.getProperty("file.separator")+"lib";
        // on Mac, the bootfolder is the JDK_HOME folder, so clean it
        if((System.getProperty("os.name").toLowerCase().startsWith("mac os x")))
        {
            lib="";
        }

        if (inPath==false)
        {
            // get boot folder
            String bootFolder = System.getProperty("sun.boot.library.path");
            // go back two directories
            bootFolder=bootFolder.substring(0,bootFolder.lastIndexOf(System.getProperty("file.separator")));
            bootFolder=bootFolder.substring(0,bootFolder.lastIndexOf(System.getProperty("file.separator")));

            Moenagade.messages.add("Searching in the bootfolder: "+bootFolder);

            // get all files from the boot folder
            File bootFolderfile = new File(bootFolder);
            File[] files = bootFolderfile.listFiles();
            TreeSet<String> directories = new TreeSet<String>();
            for(int i=0;i<files.length;i++)
            {
                if(files[i].isDirectory()) directories.add(files[i].getAbsolutePath());
            }
            boolean found=false;
            String JDK_directory = "";

            while(directories.size()>0 && found==false)
            {
                JDK_directory = directories.last();
                directories.remove(JDK_directory);
                File tools = new File(JDK_directory+lib+System.getProperty("file.separator")+"tools.jar");
                //Unibloxs.messages.add(tools.getAbsolutePath());
                if(tools.exists())
                {
                    // we got it!
                    found=true;
                    Moenagade.messages.add("<tools.jar> found here: "+tools.getAbsolutePath());
                    // now fix the JDK_home
                    Moenagade.JDK_home=JDK_directory;
                    // include the <tools.jar>
                    try
                    {
                        addJarFile(tools);
                    } catch (Exception ex) { ex.printStackTrace(); }
                }
            }

            // not found?
            if (found==false)
            {
                // get boot folder
                bootFolder = System.getProperty("sun.boot.library.path");
                // go back two directories
                bootFolder=bootFolder.substring(0,bootFolder.lastIndexOf(System.getProperty("file.separator")));
                bootFolder=bootFolder.substring(0,bootFolder.lastIndexOf(System.getProperty("file.separator")));
                bootFolder=bootFolder.replace(" (x86)","");

                Moenagade.messages.add("Searching in the bootfolder: "+bootFolder);

                // get all files from the boot folder
                bootFolderfile = new File(bootFolder);
                files = bootFolderfile.listFiles();
                directories = new TreeSet<String>();
                if(files!=null)
                {
                    for(int i=0;i<files.length;i++)
                    {
                        if(files[i].isDirectory()) directories.add(files[i].getAbsolutePath());
                    }
                    JDK_directory = "";

                    while(directories.size()>0 && found==false)
                    {
                        JDK_directory = directories.last();
                        directories.remove(JDK_directory);
                        File tools = new File(JDK_directory+lib+System.getProperty("file.separator")+"tools.jar");
                        //Unibloxs.messages.add(tools.getAbsolutePath());
                        if(tools.exists())
                        {
                            // we got it!
                            found=true;
                            Moenagade.messages.add("<tools.jar> found here: "+tools.getAbsolutePath());
                            // now fix the JDK_home
                            Moenagade.JDK_home=JDK_directory;
                            // include the <tools.jar>
                            try
                            {
                                addJarFile(tools);
                            } catch (Exception ex) { ex.printStackTrace(); }
                        }
                    }
                } else Moenagade.messages.add("Bootfolder file list is empty ...");
            }
            
            
            // still not found?
            if (found==false)
            {
                // try to fetch the JDK_HOME env variable
                Moenagade.messages.add("Searching in JDK_HOME (if set)");
                String JDK_HOME = System.getenv("JDK_HOME");
                if (JDK_HOME!=null)
                {
                    if((new File(JDK_HOME)).exists())
                    {
                        Moenagade.messages.add("JDK_HOME has been set: "+JDK_HOME);
                        String TOOLS_filename = JDK_HOME+System.getProperty("file.separator")+"lib"+System.getProperty("file.separator")+"tools.jar";
                        File tools = new File(TOOLS_filename);
                        //  load it?
                        if (tools.exists())
                        {
                            Moenagade.messages.add("<tools.jar> found here: "+tools.getAbsolutePath());
                            // now fix the JDK_home
                            Moenagade.JDK_home=JDK_HOME;
                            // include the <tools.jar>
                            try
                            {
                                addJarFile(tools);
                            } catch (Exception ex) { ex.printStackTrace(); }
                        }
                    }
                    else
                    {
                        Moenagade.messages.add("JDK_HOME has *not* been set!");
                    }
                }
            }

            // only for the mac
            if ( (found==false) && (System.getProperty("os.name").toLowerCase().startsWith("mac os x")))
            {
                Moenagade.messages.add("Searching in the Mac JDK folder: /Library/Java/JavaVirtualMachines");

                // get all files from it
                bootFolderfile = new File("/Library/Java/JavaVirtualMachines");
                files = bootFolderfile.listFiles();
                if(files!=null)
                {
                    directories = new TreeSet<String>();
                    for(int i=0;i<files.length;i++)
                    {
                        if(files[i].isDirectory())
                            directories.add(files[i].getAbsolutePath());
                    }

                    while(directories.size()>0 && found==false)
                    {
                        JDK_directory = directories.last();
                        directories.remove(JDK_directory);
                        // JAR file
                        File tools = new File(JDK_directory+System.getProperty("file.separator")+"Contents"+System.getProperty("file.separator")+"Home"+System.getProperty("file.separator")+"lib"+System.getProperty("file.separator")+"tools.jar");
                        if(tools.exists())
                        {
                            // we got it!
                            found=true;
                            Moenagade.messages.add("<tools.jar> found here: "+tools.getAbsolutePath());
                            // now fix the JDK_home
                            Moenagade.JDK_home=JDK_directory;
                            try
                            {
                                addJarFile(tools);
                            } catch (Exception ex) { ex.printStackTrace(); }
                        }
                        // ZIP file
                    }
                } else Moenagade.messages.add("Bootfolder file list is empty ...");
            }
            // Linux
            else if ( (found==false) && (System.getProperty("os.name").toLowerCase().startsWith("linux")))
            {
                Moenagade.messages.add("Searching in the Linux JDK folder: /usr/lib/jvm");

                // get all files from it
                bootFolderfile = new File("/usr/lib/jvm");
                files = bootFolderfile.listFiles();
                if(files!=null)
                {
                    directories = new TreeSet<String>();
                    for(int i=0;i<files.length;i++)
                    {
                        if(files[i].isDirectory())
                            directories.add(files[i].getAbsolutePath());
                    }

                    while(directories.size()>0 && found==false)
                    {
                        JDK_directory = directories.last();
                        directories.remove(JDK_directory);
                        // JAR file
                        File tools = new File(JDK_directory+System.getProperty("file.separator")+"lib"+System.getProperty("file.separator")+"tools.jar");
                        if(tools.exists())
                        {
                            // we got it!
                            found=true;
                            Moenagade.messages.add("<tools.jar> found here: "+tools.getAbsolutePath());
                            // now fix the JDK_home
                            Moenagade.JDK_home=JDK_directory;
                            try
                            {
                                addJarFile(tools);
                            } catch (Exception ex) { ex.printStackTrace(); }
                        }
                        // ZIP file
                    }
                } else Moenagade.messages.add("Bootfolder file list is empty ...");
            }
            else if(found==false) // Windows
            {
                Moenagade.messages.add("Searching in the Windows Java folder: C:\\Program Files\\Java");

                // get all files from it
                bootFolderfile = new File("C:\\Program Files\\Java");
                files = bootFolderfile.listFiles();
                if(files!=null)
                {
                    directories = new TreeSet<String>();
                    for(int i=0;i<files.length;i++)
                    {
                        if(files[i].isDirectory())
                            directories.add(files[i].getAbsolutePath());
                    }

                    while(directories.size()>0 && found==false)
                    {
                        JDK_directory = directories.last();
                        directories.remove(JDK_directory);
                        // JAR file
                        File tools = new File(JDK_directory+System.getProperty("file.separator")+"lib"+System.getProperty("file.separator")+"tools.jar");
                        if(tools.exists())
                        {
                            // we got it!
                            found=true;
                            Moenagade.messages.add("<tools.jar> found here: "+tools.getAbsolutePath());
                            // now fix the JDK_home
                            Moenagade.JDK_home=JDK_directory;
                            try
                            {
                                addJarFile(tools);
                            } catch (Exception ex) { ex.printStackTrace(); }
                        }
                        // ZIP file
                    }
                } else Moenagade.messages.add("Bootfolder file list is empty ...");
                
                if(found==false) // Windows
                {
                    Moenagade.messages.add("Searching in the Windows Java folder: C:\\Program Files (x86)\\Java");

                    // get all files from it
                    bootFolderfile = new File("C:\\Program Files (x86)\\Java");
                    files = bootFolderfile.listFiles();
                    if(files!=null)
                    {
                        directories = new TreeSet<String>();
                        for(int i=0;i<files.length;i++)
                        {
                            if(files[i].isDirectory())
                                directories.add(files[i].getAbsolutePath());
                        }

                        while(directories.size()>0 && found==false)
                        {
                            JDK_directory = directories.last();
                            directories.remove(JDK_directory);
                            // JAR file
                            File tools = new File(JDK_directory+System.getProperty("file.separator")+"lib"+System.getProperty("file.separator")+"tools.jar");
                            if(tools.exists())
                            {
                                // we got it!
                                found=true;
                                Moenagade.messages.add("<tools.jar> found here: "+tools.getAbsolutePath());
                                // now fix the JDK_home
                                Moenagade.JDK_home=JDK_directory;
                                try
                                {
                                    addJarFile(tools);
                                } catch (Exception ex) { ex.printStackTrace(); }
                            }
                            // ZIP file
                        }
                    } else Moenagade.messages.add("Bootfolder file list is empty ...");
                }
                
                
            }

        }

        /*
         * Task #2 >> find <src.zip>
         */
        Moenagade.messages.add("---");
        Moenagade.messages.add("Search for <src.zip> / <src.jar> ...");
        boolean foundSrc = false;
        if (Moenagade.JDK_home!=null)
        {
            File JDK_home = new File(Moenagade.JDK_home);
            if (JDK_home.exists())
            {
                String SRC_filename = Moenagade.JDK_home+System.getProperty("file.separator")+"src.zip";
                File SRC_file = new File(SRC_filename);
                if (SRC_file.exists())
                {
                    foundSrc=true;
                    Moenagade.JDK_source = SRC_filename;
                    Moenagade.messages.add("<src.zip> found: "+SRC_filename);
                }
            }
        }
        if (foundSrc==false)
        {
            // get boot folder
            String bootFolder = System.getProperty("sun.boot.library.path");

            // go back two directories
            bootFolder=bootFolder.substring(0,bootFolder.lastIndexOf(System.getProperty("file.separator")));
            bootFolder=bootFolder.substring(0,bootFolder.lastIndexOf(System.getProperty("file.separator")));
            if(System.getProperty("os.name").toLowerCase().startsWith("mac os x"))
            {
                String bootFolder1=bootFolder+System.getProperty("file.separator")+"Contents"+System.getProperty("file.separator")+"Home";
                String bootFolder2=bootFolder;
                if((new File(bootFolder1)).exists())
                    bootFolder=bootFolder1;
                else
                    bootFolder=bootFolder2;
            }

            Moenagade.messages.add("Searching in the bootfolder: "+bootFolder);

            String JDK_HOME = bootFolder;
            if((new File(JDK_HOME)).exists())
            {
                Moenagade.messages.add("JDK_HOME has been set: "+JDK_HOME);
                String TOOLS_filename = JDK_HOME+System.getProperty("file.separator")+"src.zip";
                File src = new File(TOOLS_filename);
                //  load it?
                if (src.exists())
                {
                    Moenagade.messages.add("<src.zip> found here: "+src.getAbsolutePath());
                    // now fix the JDK_home
                    Moenagade.JDK_home=JDK_HOME;
                    Moenagade.JDK_source = src.getAbsolutePath();
                    foundSrc=true;
                }
            }
            else
            {
                Moenagade.messages.add("JDK_HOME has *not* been set!");
            }

        }
        if (foundSrc==false)
        {
            // get boot folder
            String bootFolder = System.getProperty("sun.boot.library.path");

            // go back two directories
            bootFolder=bootFolder.substring(0,bootFolder.lastIndexOf(System.getProperty("file.separator")));
            bootFolder=bootFolder.substring(0,bootFolder.lastIndexOf(System.getProperty("file.separator")));

            Moenagade.messages.add("Searching again in the bootfolder: "+bootFolder);

            // get all files from the boot folder
            File bootFolderfile = new File(bootFolder);
            File[] files = bootFolderfile.listFiles();
            if(files!=null)
            {
                TreeSet<String> directories = new TreeSet<String>();
                for(int i=0;i<files.length;i++)
                {
                    if(files[i].isDirectory()) directories.add(files[i].getAbsolutePath());
                }
                boolean found=false;
                String JDK_directory = "";

                while(directories.size()>0 && found==false)
                {
                    JDK_directory = directories.last();
                    directories.remove(JDK_directory);
                    File src = new File(JDK_directory+System.getProperty("file.separator")+"src.zip");
                    if(src.exists())
                    {
                        // we got it!
                        found=true;
                        Moenagade.messages.add("<src.zip> found here: "+src.getAbsolutePath());
                        // now fix the JDK_home
                        Moenagade.JDK_home=JDK_directory;
                        Moenagade.JDK_source = src.getAbsolutePath();
                    }
                }
            } else Moenagade.messages.add("Bootfolder file list is empty ...");

        }
        if (foundSrc==false)
        {
            // get boot folder
            String bootFolder = System.getProperty("sun.boot.library.path");

            // go back two directories
            bootFolder=bootFolder.substring(0,bootFolder.lastIndexOf(System.getProperty("file.separator")));
            bootFolder=bootFolder.substring(0,bootFolder.lastIndexOf(System.getProperty("file.separator")));
            bootFolder=bootFolder.replace(" (x86)","");

            Moenagade.messages.add("Searching in the bootfolder: "+bootFolder);

            // get all files from the boot folder
            File bootFolderfile = new File(bootFolder);
            if(bootFolderfile.exists())
            {
                if(bootFolderfile!=null)
                {
                    File[] files = bootFolderfile.listFiles();
                    if (files!=null)
                    {
                        TreeSet<String> directories = new TreeSet<String>();
                        for(int i=0;i<files.length;i++)
                        {
                            if(files[i].isDirectory()) directories.add(files[i].getAbsolutePath());
                        }
                        boolean found=false;
                        String JDK_directory = "";

                        while(directories.size()>0 && found==false)
                        {
                            JDK_directory = directories.last();
                            directories.remove(JDK_directory);
                            File src = new File(JDK_directory+System.getProperty("file.separator")+"src.zip");
                            if(src.exists())
                            {
                                // we got it!
                                found=true;
                                Moenagade.messages.add("<src.zip> found here: "+src.getAbsolutePath());
                                // now fix the JDK_home
                                Moenagade.JDK_home=JDK_directory;
                                Moenagade.JDK_source = src.getAbsolutePath();
                            }
                        }
                    }
                    else Moenagade.messages.add("Doens not contains directories!");
                }
                else Moenagade.messages.add("Bootfolder file list is empty ...");
             }
             else Moenagade.messages.add("Folder does not exist!");
        }
        // only for the mac
        if ( (foundSrc==false) && (System.getProperty("os.name").toLowerCase().startsWith("mac os x")))
        {
            Moenagade.messages.add("Searching in the Mac JDK folder: /Library/Java/JavaVirtualMachines");

            // get all files from it
            File bootFolderfile = new File("/Library/Java/JavaVirtualMachines");
            File[] files = bootFolderfile.listFiles();
            if(files!=null)
            {
                TreeSet<String> directories = new TreeSet<String>();
                for(int i=0;i<files.length;i++)
                {
                    if(files[i].isDirectory())
                        directories.add(files[i].getAbsolutePath());
                }
                boolean found=false;
                String JDK_directory = "";

                while(directories.size()>0 && found==false)
                {
                    JDK_directory = directories.last();
                    directories.remove(JDK_directory);
                    // JAR file
                    File src = new File(JDK_directory+System.getProperty("file.separator")+"Contents"+System.getProperty("file.separator")+"Home"+System.getProperty("file.separator")+"src.jar");
                    if(src.exists())
                    {
                        // we got it!
                        found=true;
                        Moenagade.messages.add("<src.jar> found here: "+src.getAbsolutePath());
                        // now fix the JDK_home
                        Moenagade.JDK_home=JDK_directory;
                        Moenagade.JDK_source = src.getAbsolutePath();
                    }
                    // ZIP file
                    src = new File(JDK_directory+System.getProperty("file.separator")+"Contents"+System.getProperty("file.separator")+"Home"+System.getProperty("file.separator")+"src.zip");
                    if(src.exists())
                    {
                        // we got it!
                        found=true;
                        Moenagade.messages.add("<src.jar> found here: "+src.getAbsolutePath());
                        // now fix the JDK_home
                        Moenagade.JDK_home=JDK_directory;
                        Moenagade.JDK_source = src.getAbsolutePath();
                    }
                }
            } else Moenagade.messages.add("Bootfolder file list is empty ...");
        }
        // Linux
        else if ( (foundSrc==false) && (System.getProperty("os.name").toLowerCase().startsWith("linux")))
        {
            Moenagade.messages.add("Searching in the Linux JDK folder: /usr/lib/jvm");

            // get all files from it
            File bootFolderfile = new File("/usr/lib/jvm");
            File[] files = bootFolderfile.listFiles();
            if(files!=null)
            {
                TreeSet<String> directories = new TreeSet<String>();
                for(int i=0;i<files.length;i++)
                {
                    if(files[i].isDirectory())
                        directories.add(files[i].getAbsolutePath());
                }
                boolean found=false;
                String JDK_directory = "";

                while(directories.size()>0 && found==false)
                {
                    JDK_directory = directories.last();
                    directories.remove(JDK_directory);
                    // JAR file
                    File src = new File(JDK_directory+System.getProperty("file.separator")+"src.zip");
                    if(src.exists())
                    {
                        // we got it!
                        found=true;
                        Moenagade.messages.add("<src.zip> found here: "+src.getAbsolutePath());
                        // now fix the JDK_home
                        Moenagade.JDK_home=JDK_directory;
                        Moenagade.JDK_source = src.getAbsolutePath();
                    }
                }
            } else Moenagade.messages.add("Bootfolder file list is empty ...");
        }
        // Widnows
        else if (foundSrc==false)
        {
            Moenagade.messages.add("Searching in the Windows JDK folder: C:\\Program Files\\Java");

            // get all files from it
            File bootFolderfile = new File("C:\\Program Files\\Java");
            File[] files = bootFolderfile.listFiles();
            if(files!=null)
            {
                TreeSet<String> directories = new TreeSet<String>();
                for(int i=0;i<files.length;i++)
                {
                    if(files[i].isDirectory())
                        directories.add(files[i].getAbsolutePath());
                }
                boolean found=false;
                String JDK_directory = "";

                while(directories.size()>0 && found==false)
                {
                    JDK_directory = directories.last();
                    directories.remove(JDK_directory);
                    // JAR file
                    File src = new File(JDK_directory+System.getProperty("file.separator")+"src.zip");
                    if(src.exists())
                    {
                        // we got it!
                        found=true;
                        Moenagade.messages.add("<src.zip> found here: "+src.getAbsolutePath());
                        // now fix the JDK_home
                        Moenagade.JDK_home=JDK_directory;
                        Moenagade.JDK_source = src.getAbsolutePath();
                    }
                }
            } else Moenagade.messages.add("Bootfolder file list is empty ...");
        }
        // still not found?
        if (foundSrc==false)
        {
            // try to fetch the JDK_HOME env variable
            Moenagade.messages.add("Searching in JDK_HOME (if set)");
            String JDK_HOME = System.getenv("JDK_HOME");
            if (JDK_HOME!=null)
            {
                if((new File(JDK_HOME)).exists())
                {
                    Moenagade.messages.add("JDK_HOME has been set: "+JDK_HOME);
                    String TOOLS_filename = JDK_HOME+System.getProperty("file.separator")+"src.zip";
                    File src = new File(TOOLS_filename);
                    //  load it?
                    if (src.exists())
                    {
                        Moenagade.messages.add("<src.zip> found here: "+src.getAbsolutePath());
                        // now fix the JDK_home
                        Moenagade.JDK_home=JDK_HOME;
                        Moenagade.JDK_source = src.getAbsolutePath();
                    }
                }
                else
                {
                    Moenagade.messages.add("JDK_HOME has *not* been set!");
                }
            }

        }

        Moenagade.messages.add("---");
        Moenagade.messages.add("JDK_home = "+Moenagade.JDK_home);

        
        //System.out.println(Moenagade.messages.getText());
        // Why does it hang here if a wrong tools.jar has been found
        // and we now try to use the correct one??

        try
        {
            Class.forName("com.sun.tools.javac.api.JavacTool");
            Moenagade.javaCompilerDetected=true;
        }
        catch (java.lang.UnsupportedClassVersionError ex)
        {
            Moenagade.messages.add("The Java compiler has been compiled with a wrong JDK version!");
        }
        catch (ClassNotFoundException ex)
        {
            Moenagade.messages.add("The Java compiler cannot be loaded ...");
            Moenagade.javaCompilerDetected=false;
        }
        catch (Error ex)
        {
            Moenagade.messages.add("The Java compiler cannot be loaded ...");
            Moenagade.javaCompilerDetected=false;
        }

        final MainFrame mainform = new MainFrame();
        mainform.setIconImage(new javax.swing.ImageIcon(mainform.getClass().getResource("/lu/fisch/moenagade/images/moenagade32.png")).getImage());

        try
        {
                String s = new String();
                int start = 0;
                if(args.length>0)
                    if (args[0].equals("-open"))
                        start=1;
                for(int i=start;i<args.length;i++)
                {
                        s+=args[i];
                }
                mainform.setVisible(true);
                Moenagade.messages.add("Opening from shell: "+s);
                throw new Exception("Not yet implements!!");
        }
        catch (Exception e)
        {
            // ignore
        }
        
        Moenagade.messages.add("---");
        
        Logger.getInstance().log("---------------%<------------------------------");
        for(int m=0; m<Moenagade.messages.count(); m++)
        {
            Logger.getInstance().log(Moenagade.messages.get(m));
        }
        
        /*
        try
        {
            // Construct data
            String data = URLEncoder.encode("messages", "UTF-8") + "=" + URLEncoder.encode(Unibloxs.messages.getText(), "UTF-8");
            // Send data
            URL url = new URL("http://unimozer.fisch.lu/boot.php");
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                // Process line... --> do nothing
            }
            wr.close();
            rd.close();
        } 
        catch (Exception ex)
        {
            // ignore
        }/**/
        
        //System.out.println(Unibloxs.messages.getText());

        //System.out.println(System.getProperty("os.name").toLowerCase());
        /*
         * These are MAC specific things
         */
        if(System.getProperty("os.name").toLowerCase().startsWith("mac os x"))
        {
         
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("apple.awt.graphics.UseQuartz", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Unibloxs");

            Application application = Application.getApplication();
            //application.setDockIconImage(mainform.getIconImage());

            try
            {
                application.setEnabledPreferencesMenu(true);
                application.addApplicationListener(new com.apple.eawt.ApplicationAdapter()
                {
                    @Override
                    public void handleAbout(ApplicationEvent e)
                    {
                        //throw new Exception("Not yet implements!!");
                        //mainform.getDiagram().about();
                        //e.setHandled(true);
                    }

                    @Override
                    public void handleOpenApplication(ApplicationEvent e)
                    {
                    }

                    @Override
                    public void handleOpenFile(ApplicationEvent e)
                    {
                        if(e.getFilename()!=null)
                        {
                            //System.out.println("Opening file: "+e.getFilename());
                            //System.out.println("Opening package: "+(new File(e.getFilename()).getParent()));
                            //mainform.diagram.openUnibloxs(e.getFilename());
                            //mainform.diagram.openNSD(e.getFilename());
                        }
                    }

                    @Override
                    public void handlePreferences(ApplicationEvent e)
                    {
                        //mainform.diagram.preferencesNSD();
                    }

                    @Override
                    public void handlePrintFile(ApplicationEvent e)
                    {
                       // mainform.getDiagram().printDiagram();
                    }

                    @Override
                    public void handleQuit(ApplicationEvent e)
                    {
                        //mainform.saveToINI();
                        //mainform.closeWindow();
                    }
               });
            }
            catch (Exception e)
            {
                //e.printStackTrace();
            }
        }
        /**/
        

    }


    private static void copyFile(File in, File out) throws Exception 
    {
       FileChannel sourceChannel = new FileInputStream(in).getChannel();
       FileChannel destinationChannel = new FileOutputStream(out).getChannel();
       sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
       sourceChannel.close();
       destinationChannel.close();
    } 

}
