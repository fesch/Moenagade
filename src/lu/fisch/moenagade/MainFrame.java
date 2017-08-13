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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import lu.fisch.moenagade.console.Console;
import lu.fisch.moenagade.gui.ImageFile;
import lu.fisch.moenagade.gui.LibraryPanel;
import lu.fisch.moenagade.gui.SoundFile;
import lu.fisch.moenagade.gui.TreeRenderer;
import lu.fisch.moenagade.model.BloxsClass;
import lu.fisch.moenagade.model.BloxsColors;
import lu.fisch.moenagade.model.BloxsDefinitions;
import lu.fisch.moenagade.model.BloxsEditor;
import lu.fisch.moenagade.model.Entity;
import lu.fisch.moenagade.model.Library;
import lu.fisch.moenagade.model.Project;
import lu.fisch.moenagade.model.World;
import net.iharder.dnd.FileDrop;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 *
 * @author robert.fisch
 */
public class MainFrame extends javax.swing.JFrame {

    private Project project;
    
    private DefaultMutableTreeNode mainNode;
    private DefaultMutableTreeNode worldNode;
    private DefaultMutableTreeNode entityNode;
    private DefaultMutableTreeNode imagesNode;
    private DefaultMutableTreeNode soundsNode;
    
    private RSyntaxTextArea textArea;
    private BloxsClass bloxsClass;
    private Object selectedNode;
    
    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
        
        // set editor
        textArea = new RSyntaxTextArea(20, 60);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        textArea.setCodeFoldingEnabled(true);
        textArea.setEditable(false);
        RTextScrollPane sp = new RTextScrollPane(textArea);
        splitter3.setRightComponent(sp);
        
        // apply toolbar layout
        this.setLayout(new AKDockLayout());
        this.getContentPane().removeAll();
        this.getContentPane().add(tbStandard,AKDockLayout.NORTH);
        this.getContentPane().add(tbProject,AKDockLayout.NORTH);
        this.getContentPane().add(tbEditor,AKDockLayout.NORTH);
        this.getContentPane().add(tbRun,AKDockLayout.NORTH);
        this.getContentPane().add(topPanel,AKDockLayout.CENTER);
        this.getContentPane().validate();
        
        // create an new and empty project
        project = new Project(this); 
        Library.getInstance().setProject(project);
        setupProjectTree();
        
        // load settings
        Ini ini = Ini.getInstance();
        try
        {
            ini.load();
        }
        catch (FileNotFoundException ex)
        {
            MyError.display(ex);
        }
        catch (IOException ex)
        {
            MyError.display(ex);;
        }   
        
        // window
        int top    = Integer.valueOf(ini.getProperty("top","0")).intValue();
        int left   = Integer.valueOf(ini.getProperty("left","0")).intValue();
        int width  = Integer.valueOf(ini.getProperty("width","750")).intValue();
        int height = Integer.valueOf(ini.getProperty("height","550")).intValue();
        
        // Get the size of the default screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        // make shure the window is inside the wisible area
        if ( (left>dim.getWidth()) || (top>dim.getHeight()) ||
             (left<0) || (top<0)   )
        {
            // revert to defaults
            top=0;
            left=0;
            width=750;
            height=550;
        }
        
        setPreferredSize(new Dimension(width,height));
        setSize(width,height);
        setLocation(new Point(top,left));
        validate();
        
        // sliders
        splitter1.setDividerLocation(Integer.valueOf(ini.getProperty("splitter1", "350")));
        splitter2.setDividerLocation(Integer.valueOf(ini.getProperty("splitter2", "150")));
        splitter3.setDividerLocation(Integer.valueOf(ini.getProperty("splitter3", "5000")));
        splitter4.setDividerLocation(Integer.valueOf(ini.getProperty("splitter4", "5000")));
        
        // menu shortcuts
        miNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        miSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        miOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        miAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1,0));
	miRun.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6,0));
	miQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        
        // connect the console
        Console.disconnectAll();

        // load the library
        loadLibrary("");
        
        // blank the project
        speNewActionPerformed(null);
    }

    public void loadLibrary(String destination)
    {
        // load the library
        Library library = Library.getInstance();
        library.load(destination);
        tabs.removeAll();

        // hide the tabs
        tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT); //WRAP_TAB_LAYOUT || SCROLL_TAB_LAYOUT);
        tabs.setUI(new BasicTabbedPaneUI() {  
            @Override  
            protected int calculateTabAreaHeight(int tab_placement, int run_count, int max_tab_height) {  
                /*if (tabs.getTabCount() > 1)
                    return super.calculateTabAreaHeight(tab_placement, run_count, max_tab_height);  
                else*/ 
                    return 0;  
            }  
        });
        
        // jlist with colored items
        catList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Color col = (new BloxsDefinitions()).getColor(value.toString());
                if (col==null) col=Color.white;
                
                setBackground(col);
                if (isSelected) {
                     setBackground(getBackground().darker());
                }
                
                JPanel p = new JPanel();
                p.setLayout(new BorderLayout());
                p.setBorder(new LineBorder(col, 5));
                p.add(c,BorderLayout.CENTER);
                return p;
            }
        });
        //catList.setFixedCellHeight(50);
        //catList.setFixedCellWidth(100);
        

        
        Vector<String> names = new Vector<>();
        for (int i = 0; i < library.getTabs().size(); i++) {
            LibraryPanel ep = library.getTabs().get(i);
            // register the library for the mouse events
            // it will pass them on to the actual opened editor
            ep.addMouseListener(library);
            ep.addMouseMotionListener(library);
            ep.revalidate();
            JScrollPane scroll = new JScrollPane(ep);
            scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            names.add(ep.getLabel());
            tabs.add(ep.getLabel(), scroll);
        }
        catList.setListData(names);
    }
    
    public void closeWindow()
    {
        /*
         * Save settings to the INI file
         */
        try
        {
           Ini ini = Ini.getInstance();
            ini.load();
            // window
            // position
            ini.setProperty("top",Integer.toString(getLocationOnScreen().x));
            ini.setProperty("left",Integer.toString(getLocationOnScreen().y));
            ini.setProperty("width",Integer.toString(getWidth()));
            ini.setProperty("height",Integer.toString(getHeight()));
            // sliders
            ini.setProperty("splitter1",Integer.toString(splitter1.getDividerLocation()));
            ini.setProperty("splitter2",Integer.toString(splitter2.getDividerLocation()));
            ini.setProperty("splitter3",Integer.toString(splitter3.getDividerLocation()));
            ini.setProperty("splitter4",Integer.toString(splitter4.getDividerLocation()));
            
            ini.save();
        }
        catch (FileNotFoundException ex)
        {
            MyError.display(ex);
        }
        catch (IOException ex)
        {
            MyError.display(ex);
        }

        /*
         * Save diagram?
         */
        if(project.isChanged())
        {
            if(project.askToSave()) System.exit(0);
        }
        else System.exit(0);
        
        
             
    }

    private void setupProjectTree()
    {
        // set tree selection model
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        // clean the tree
        ((DefaultMutableTreeNode)tree.getModel().getRoot()).removeAllChildren();
        ((DefaultTreeModel)tree.getModel()).reload();
        
        // apply renderer
        tree.setCellRenderer(new TreeRenderer());
        tree.setRowHeight(20);
        
        // 
        mainNode = new DefaultMutableTreeNode(project.getMain());
        worldNode  = new DefaultMutableTreeNode("Worlds");
        entityNode = new DefaultMutableTreeNode("Entities");
        imagesNode = new DefaultMutableTreeNode("Images");
        soundsNode = new DefaultMutableTreeNode("Sounds");
        ((DefaultTreeModel)tree.getModel()).setRoot(mainNode);
        mainNode.add(worldNode);
        mainNode.add(entityNode);
        mainNode.add(imagesNode);
        mainNode.add(soundsNode);
        tree.expandPath(new TreePath(mainNode.getPath()));
        //System.out.println(tree.getModel().getClass().getSimpleName());
        
        // set the filedropper for the tree
        FileDrop fileDrop = new FileDrop(tree, new FileDrop.Listener()
        {

            @Override
            public void filesDropped(java.io.File[] files)
            {
                if(!project.isSaved())
                {
                    JOptionPane.showMessageDialog(MainFrame.this, "The project needs to be saved before you drop any files.\n\nAction aborted!","Error", JOptionPane.ERROR_MESSAGE,Moenagade.IMG_ERROR);
                    return;
                }
            
                boolean found = false;
                for (int i = 0; i < files.length; i++)
                {
                    String filename = files[i].toString();
                    File f = new File(filename);
                    
                    if (Project.getExtension(f).toLowerCase().equals("jpg") ||
                        Project.getExtension(f).toLowerCase().equals("png") ||
                        Project.getExtension(f).toLowerCase().equals("jpeg"))
                    {
                        project.loadImage(f);
                        updateProjectTree();
                    }
                    else if (Project.getExtension(f).toLowerCase().equals("wav"))
                    {
                        project.loadSound(f);
                        updateProjectTree();
                    }
                }
            }

        });
    }
    
    private void updateProjectTree()
    {
        TreePath selected = null;
        
        // clear
        worldNode.removeAllChildren();
        entityNode.removeAllChildren();       
        imagesNode.removeAllChildren();
        soundsNode.removeAllChildren();
        
        mainNode.setUserObject(project.getMain());
        
        // worlds
        for(World world : project.getWorlds().values())
        {
            DefaultMutableTreeNode node  = new DefaultMutableTreeNode(world);
            worldNode.add(node);
        }
        
        // entities
        for(Entity entity : project.getEntities().values())
        {
            DefaultMutableTreeNode node  = new DefaultMutableTreeNode(entity);
            entityNode.add(node);
        }
        
        // images
        File imageDir = new File(project.getImageDirectory());
        File[] files = imageDir.listFiles();
        if(files!=null)
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                String extension = project.getExtension(file);
                if (extension != null) {
                    if (extension.equals("png") ||
                        extension.equals("jpg") ||
                        extension.equals("jpeg")) 
                    {
                        //System.out.println(file.getName());
                        DefaultMutableTreeNode node  = new DefaultMutableTreeNode(new ImageFile(file.getAbsolutePath()));
                        imagesNode.add(node);
                    }
                }
            }
        
        // sounds
        File soundDir = new File(project.getSoundDirectory());
        files = soundDir.listFiles();
        if(files!=null)
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                String extension = project.getExtension(file);
                if (extension != null) {
                    if (extension.equals("wav")) 
                    {
                        //System.out.println(file.getName());
                        DefaultMutableTreeNode node  = new DefaultMutableTreeNode(new SoundFile(file.getAbsolutePath()));
                        soundsNode.add(node);
                    }
                }
            }
        
        ((DefaultTreeModel)tree.getModel()).reload();
        
        tree.expandPath(new TreePath(worldNode.getPath()));
        tree.expandPath(new TreePath(entityNode.getPath()));
        tree.expandPath(new TreePath(imagesNode.getPath()));
        tree.expandPath(new TreePath(soundsNode.getPath()));
        
        if(project.getSelected()!=null)
            tree.setSelectionPath(selected);
    }
    
    public void setTitleNew()
    {
        if(project.getDirectoryName()!=null)
        {
            String name = project.getDirectoryName();
            name = name.substring(name.lastIndexOf('/')+1).trim(); 
            if(project.isChanged()) name+=" [changed]";

            this.setTitle(Moenagade.E_NAME+" - "+name);
        }
        else this.setTitle(Moenagade.E_NAME+" - [new]");
    }
    
    public void showCode()
    {
        if(bloxsClass!=null)
        {
            textArea.setText(bloxsClass.getJavaCode());
            //textArea.setText(Library.getInstance().getBloxsEditor().getXml());
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        treePopper = new javax.swing.JPopupMenu();
        popWorld = new javax.swing.JMenuItem();
        popEntity = new javax.swing.JMenuItem();
        popImage = new javax.swing.JMenuItem();
        popSound = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        popDelete = new javax.swing.JMenuItem();
        popRename = new javax.swing.JMenuItem();
        tbStandard = new javax.swing.JToolBar();
        speOpen = new javax.swing.JButton();
        speNew = new javax.swing.JButton();
        speSave = new javax.swing.JButton();
        tbProject = new javax.swing.JToolBar();
        speNewWorld = new javax.swing.JButton();
        speNewEntity = new javax.swing.JButton();
        speNewImage = new javax.swing.JButton();
        speNewSound = new javax.swing.JButton();
        speDelete = new javax.swing.JButton();
        speRename = new javax.swing.JButton();
        topPanel = new javax.swing.JPanel();
        splitter1 = new javax.swing.JSplitPane();
        rightPanel = new javax.swing.JPanel();
        splitter3 = new javax.swing.JSplitPane();
        splitter4 = new javax.swing.JSplitPane();
        editorScroller = new javax.swing.JScrollPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        console = new lu.fisch.moenagade.console.Console();
        splitter2 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();
        jPanel1 = new javax.swing.JPanel();
        tabs = new javax.swing.JTabbedPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        catList = new javax.swing.JList<>();
        tbRun = new javax.swing.JToolBar();
        speRun = new javax.swing.JButton();
        speJar = new javax.swing.JButton();
        tbEditor = new javax.swing.JToolBar();
        speUndo = new javax.swing.JButton();
        speRedo = new javax.swing.JButton();
        miMenu = new javax.swing.JMenuBar();
        miFile = new javax.swing.JMenu();
        miNew = new javax.swing.JMenuItem();
        miOpen = new javax.swing.JMenuItem();
        miSave = new javax.swing.JMenuItem();
        miSaveAs = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        miQuit = new javax.swing.JMenuItem();
        miProject = new javax.swing.JMenu();
        miWorld = new javax.swing.JMenuItem();
        miEntity = new javax.swing.JMenuItem();
        miImage = new javax.swing.JMenuItem();
        miSound = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        miDelete = new javax.swing.JMenuItem();
        miRename = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        miRun = new javax.swing.JMenuItem();
        miJar = new javax.swing.JMenuItem();
        miHelp = new javax.swing.JMenu();
        miAbout = new javax.swing.JMenuItem();

        popWorld.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/moenagade/images/world16.png"))); // NOI18N
        popWorld.setText("Add World ...");
        popWorld.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popWorldActionPerformed(evt);
            }
        });
        treePopper.add(popWorld);

        popEntity.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/moenagade/images/entity16.png"))); // NOI18N
        popEntity.setText("Add Entity ...");
        popEntity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popEntityActionPerformed(evt);
            }
        });
        treePopper.add(popEntity);

        popImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/moenagade/images/images16.png"))); // NOI18N
        popImage.setText("Add Image ...");
        popImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popImageActionPerformed(evt);
            }
        });
        treePopper.add(popImage);

        popSound.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/moenagade/images/sounds16.png"))); // NOI18N
        popSound.setText("Add Sound ...");
        popSound.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popSoundActionPerformed(evt);
            }
        });
        treePopper.add(popSound);
        treePopper.add(jSeparator4);

        popDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/icons/gen_del.png"))); // NOI18N
        popDelete.setText("Remove ...");
        popDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popDeleteActionPerformed(evt);
            }
        });
        treePopper.add(popDelete);

        popRename.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/icons/pencil.png"))); // NOI18N
        popRename.setText("Rename ...");
        popRename.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popRenameActionPerformed(evt);
            }
        });
        treePopper.add(popRename);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(null);

        tbStandard.setRollover(true);

        speOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/icons/gen_open_project.png"))); // NOI18N
        speOpen.setToolTipText("Open an existing project ...");
        speOpen.setFocusable(false);
        speOpen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        speOpen.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        speOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                speOpenActionPerformed(evt);
            }
        });
        tbStandard.add(speOpen);

        speNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/icons/gen_new_project.png"))); // NOI18N
        speNew.setToolTipText("Create a new and empty project ...");
        speNew.setFocusable(false);
        speNew.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        speNew.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        speNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                speNewActionPerformed(evt);
            }
        });
        tbStandard.add(speNew);

        speSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/icons/gen_save.png"))); // NOI18N
        speSave.setToolTipText("Save the current project");
        speSave.setFocusable(false);
        speSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        speSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        speSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                speSaveActionPerformed(evt);
            }
        });
        tbStandard.add(speSave);

        getContentPane().add(tbStandard);
        tbStandard.setBounds(0, 0, 200, 25);

        tbProject.setRollover(true);

        speNewWorld.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/moenagade/images/world16.png"))); // NOI18N
        speNewWorld.setToolTipText("Add a new world ...");
        speNewWorld.setFocusable(false);
        speNewWorld.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        speNewWorld.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        speNewWorld.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                speNewWorldActionPerformed(evt);
            }
        });
        tbProject.add(speNewWorld);

        speNewEntity.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/moenagade/images/entity16.png"))); // NOI18N
        speNewEntity.setToolTipText("Add a new entity ...");
        speNewEntity.setFocusable(false);
        speNewEntity.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        speNewEntity.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        speNewEntity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                speNewEntityActionPerformed(evt);
            }
        });
        tbProject.add(speNewEntity);

        speNewImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/moenagade/images/images16.png"))); // NOI18N
        speNewImage.setToolTipText("Add an image to the project ...");
        speNewImage.setFocusable(false);
        speNewImage.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        speNewImage.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        speNewImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                speNewImageActionPerformed(evt);
            }
        });
        tbProject.add(speNewImage);

        speNewSound.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/moenagade/images/sounds16.png"))); // NOI18N
        speNewSound.setToolTipText("Add a sound to the project ...");
        speNewSound.setFocusable(false);
        speNewSound.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        speNewSound.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        speNewSound.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                speNewSoundActionPerformed(evt);
            }
        });
        tbProject.add(speNewSound);

        speDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/icons/gen_del.png"))); // NOI18N
        speDelete.setToolTipText("Delete selected ...");
        speDelete.setFocusable(false);
        speDelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        speDelete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        speDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                speDeleteActionPerformed(evt);
            }
        });
        tbProject.add(speDelete);

        speRename.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/icons/pencil.png"))); // NOI18N
        speRename.setToolTipText("Rename selected ...");
        speRename.setFocusable(false);
        speRename.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        speRename.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        speRename.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                speRenameActionPerformed(evt);
            }
        });
        tbProject.add(speRename);

        getContentPane().add(tbProject);
        tbProject.setBounds(0, 30, 200, 33);

        topPanel.setLayout(new java.awt.BorderLayout());

        splitter1.setDividerLocation(200);
        splitter1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                splitter1PropertyChange(evt);
            }
        });

        rightPanel.setLayout(new java.awt.BorderLayout());

        splitter3.setDividerLocation(200);
        splitter3.setResizeWeight(1.0);

        splitter4.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitter4.setResizeWeight(1.0);
        splitter4.setTopComponent(editorScroller);

        console.setMinimumSize(new java.awt.Dimension(0, 0));
        console.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                consoleMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(console);

        splitter4.setRightComponent(jScrollPane2);

        splitter3.setLeftComponent(splitter4);

        rightPanel.add(splitter3, java.awt.BorderLayout.CENTER);

        splitter1.setRightComponent(rightPanel);

        splitter2.setDividerLocation(150);
        splitter2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitter2.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                splitter2ComponentResized(evt);
            }
        });

        tree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                treeMousePressed(evt);
            }
        });
        tree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                treeValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(tree);

        splitter2.setLeftComponent(jScrollPane1);

        jPanel1.setLayout(new java.awt.BorderLayout());
        jPanel1.add(tabs, java.awt.BorderLayout.CENTER);

        catList.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        catList.setForeground(new java.awt.Color(255, 255, 255));
        catList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 1", "Item 2", "Item 3", "Item 4", "Item 6" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        catList.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
        catList.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                catListComponentResized(evt);
            }
        });
        catList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                catListValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(catList);

        jPanel1.add(jScrollPane3, java.awt.BorderLayout.PAGE_START);

        splitter2.setRightComponent(jPanel1);

        splitter1.setLeftComponent(splitter2);

        topPanel.add(splitter1, java.awt.BorderLayout.CENTER);

        getContentPane().add(topPanel);
        topPanel.setBounds(0, 112, 585, 431);

        tbRun.setRollover(true);

        speRun.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/icons/netbeans_run.png"))); // NOI18N
        speRun.setToolTipText("Run the project ...");
        speRun.setFocusable(false);
        speRun.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        speRun.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        speRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                speRunActionPerformed(evt);
            }
        });
        tbRun.add(speRun);

        speJar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/icons/java_jar.png"))); // NOI18N
        speJar.setToolTipText("Run the project ...");
        speJar.setFocusable(false);
        speJar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        speJar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        speJar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                speJarActionPerformed(evt);
            }
        });
        tbRun.add(speJar);

        getContentPane().add(tbRun);
        tbRun.setBounds(0, 60, 200, 40);

        tbEditor.setRollover(true);

        speUndo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/icons/iconfinder_undo_gplaleksandra_wolska.png"))); // NOI18N
        speUndo.setToolTipText("Undo last action");
        speUndo.setFocusable(false);
        speUndo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        speUndo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        speUndo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                speUndoActionPerformed(evt);
            }
        });
        tbEditor.add(speUndo);

        speRedo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/icons/iconfinder_redo_gplaleksandra_wolska.png"))); // NOI18N
        speRedo.setToolTipText("Redo last action");
        speRedo.setFocusable(false);
        speRedo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        speRedo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        speRedo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                speRedoActionPerformed(evt);
            }
        });
        tbEditor.add(speRedo);

        getContentPane().add(tbEditor);
        tbEditor.setBounds(270, 10, 80, 40);

        miFile.setText("File");
        miFile.setToolTipText("");

        miNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/icons/gen_new_project.png"))); // NOI18N
        miNew.setText("New Project ...");
        miNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miNewActionPerformed(evt);
            }
        });
        miFile.add(miNew);

        miOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/icons/gen_open_project.png"))); // NOI18N
        miOpen.setText("Open Project ...");
        miOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miOpenActionPerformed(evt);
            }
        });
        miFile.add(miOpen);

        miSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/icons/gen_save.png"))); // NOI18N
        miSave.setText("Save Project");
        miSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miSaveActionPerformed(evt);
            }
        });
        miFile.add(miSave);

        miSaveAs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/icons/gen_save.png"))); // NOI18N
        miSaveAs.setText("Save Project As ...");
        miSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miSaveAsActionPerformed(evt);
            }
        });
        miFile.add(miSaveAs);
        miFile.add(jSeparator1);

        miQuit.setText("Quit");
        miQuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miQuitActionPerformed(evt);
            }
        });
        miFile.add(miQuit);

        miMenu.add(miFile);

        miProject.setText("Project");

        miWorld.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/moenagade/images/world16.png"))); // NOI18N
        miWorld.setText("Add World ...");
        miWorld.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miWorldActionPerformed(evt);
            }
        });
        miProject.add(miWorld);

        miEntity.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/moenagade/images/entity16.png"))); // NOI18N
        miEntity.setText("Add Entity ...");
        miEntity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miEntityActionPerformed(evt);
            }
        });
        miProject.add(miEntity);

        miImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/moenagade/images/images16.png"))); // NOI18N
        miImage.setText("Add Image ...");
        miImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miImageActionPerformed(evt);
            }
        });
        miProject.add(miImage);

        miSound.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/moenagade/images/sounds16.png"))); // NOI18N
        miSound.setText("Add Sound ...");
        miSound.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miSoundActionPerformed(evt);
            }
        });
        miProject.add(miSound);
        miProject.add(jSeparator2);

        miDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/icons/gen_del.png"))); // NOI18N
        miDelete.setText("Delete selected ...");
        miDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miDeleteActionPerformed(evt);
            }
        });
        miProject.add(miDelete);

        miRename.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/icons/pencil.png"))); // NOI18N
        miRename.setText("Rename selected ...");
        miRename.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miRenameActionPerformed(evt);
            }
        });
        miProject.add(miRename);
        miProject.add(jSeparator3);

        miRun.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/icons/netbeans_run.png"))); // NOI18N
        miRun.setText("Run");
        miRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miRunActionPerformed(evt);
            }
        });
        miProject.add(miRun);

        miJar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/icons/java_jar.png"))); // NOI18N
        miJar.setText("Build JAR");
        miJar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miJarActionPerformed(evt);
            }
        });
        miProject.add(miJar);

        miMenu.add(miProject);

        miHelp.setText("Help");

        miAbout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/moenagade/images/moenagade16.png"))); // NOI18N
        miAbout.setText("About ...");
        miAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miAboutActionPerformed(evt);
            }
        });
        miHelp.add(miAbout);

        miMenu.add(miHelp);

        setJMenuBar(miMenu);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        closeWindow();
    }//GEN-LAST:event_formWindowClosing

    private void speOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_speOpenActionPerformed
        project.openProject();
        setTitleNew();
        updateProjectTree();
        openBloxsEditor(project.getMain());
    }//GEN-LAST:event_speOpenActionPerformed

    private void speSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_speSaveActionPerformed
        project.saveMoenagade();
        setTitleNew();
    }//GEN-LAST:event_speSaveActionPerformed

    private void speNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_speNewActionPerformed
        project.newProject();
        setTitleNew();
        updateProjectTree();
        openBloxsEditor(project.getMain());
    }//GEN-LAST:event_speNewActionPerformed

    private void speNewWorldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_speNewWorldActionPerformed
        BloxsClass bc = project.addWorld();
        if(bc!=null)
        {
            bloxsClass=bc;
            updateProjectTree();
            openBloxsEditor(bloxsClass);
        }
    }//GEN-LAST:event_speNewWorldActionPerformed

    private void speNewEntityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_speNewEntityActionPerformed
        BloxsClass bc = project.addEntity();
        if(bc!=null)
        {
            bloxsClass=bc;
            updateProjectTree();
            openBloxsEditor(bloxsClass);
        }
    }//GEN-LAST:event_speNewEntityActionPerformed

    private void openImagfile(ImageFile imageFile)
    {
        // eanble scrolling
        editorScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        editorScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        JLabel label = new JLabel();
        label.setIcon(new ImageIcon(imageFile.getAbsolutePath()));
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);
        label.setBackground(BloxsColors.$BACKGROUND);
        label.setOpaque(true);
        
        editorScroller.getViewport().add(label);
        editorScroller.getViewport().validate();
        editorScroller.repaint();
    }
    
    private void openSoundFile(final SoundFile imageFile)
    {
        // eanble scrolling
        editorScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        editorScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        JLabel label = new JLabel();
        label.setText("Click to play ...");
        label.setFont(new Font("Monospaced", Font.BOLD, 20));
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);
        label.setBackground(BloxsColors.$BACKGROUND);
        label.setOpaque(true);
        
        label.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent me) {
                try {
                    String filename = project.getDirectoryName()+System.getProperty("file.separator")+
                            "bloxs"+System.getProperty("file.separator")+
                            "sounds"+System.getProperty("file.separator")+imageFile.getName();
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(filename));
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioIn);
                    clip.start();
                }
                catch(Exception ex)         
                {
                    ex.printStackTrace();
                }
            }

            @Override
            public void mousePressed(MouseEvent me) {
            }

            @Override
            public void mouseReleased(MouseEvent me) {
            }

            @Override
            public void mouseEntered(MouseEvent me) {
            }

            @Override
            public void mouseExited(MouseEvent me) {
            }
        });
        
      
        editorScroller.getViewport().add(label);
        editorScroller.getViewport().validate();
        editorScroller.repaint();
    }
    
    public void openNothing()
    {
        // eanble scrolling
        editorScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        editorScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        JLabel label = new JLabel();
        label.setText("");
        label.setFont(new Font("Monospaced", Font.BOLD, 20));
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);
        label.setBackground(BloxsColors.$BACKGROUND);
        label.setOpaque(true);
      
        editorScroller.getViewport().add(label);
        editorScroller.getViewport().validate();
        editorScroller.repaint();
    }
    
    private void openBloxsEditor(BloxsClass bloxsClass)
    {
        this.bloxsClass = bloxsClass;
        
        // eanble scrolling
        editorScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        editorScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        // clean the right panel
        //editorScroller.removeAll();
        // add the editor
        //editorScroller.add(bloxsClass.getEditor());
        editorScroller.getViewport().add(bloxsClass.getEditor());
        if(bloxsClass instanceof Entity) loadLibrary("entity");
        else if(bloxsClass instanceof World) loadLibrary("world");
        else loadLibrary("main");

        // validate the panel
        editorScroller.getViewport().validate();
        editorScroller.repaint();
        // register with the library
        Library.getInstance().setBloxsEditor(bloxsClass.getEditor());
        // link to mainframe
        bloxsClass.getEditor().setMainFrame(this);
        showCode();
    }
    
    private void treeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_treeValueChanged
        //Returns the last path element of the selection.
        //This method is useful only when the selection model allows a single selection.
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

        if (node == null)
            //Nothing is selected.  
            return;

        Object nodeInfo = node.getUserObject();
        selectedNode = nodeInfo;

        if (nodeInfo instanceof BloxsClass) 
        {
            // get the editor
            openBloxsEditor((BloxsClass) nodeInfo);
        }
        if (nodeInfo instanceof ImageFile) 
        {
            openImagfile((ImageFile) nodeInfo);
        }
        if (nodeInfo instanceof SoundFile) 
        {
            openSoundFile((SoundFile) nodeInfo);
        }
    }//GEN-LAST:event_treeValueChanged

    
    private void speNewImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_speNewImageActionPerformed
        if(!project.isSaved())
        {
            if(project.askToSave(true))
            {
                project.openImage(this);
                updateProjectTree();
            }
            else
            {
                JOptionPane.showMessageDialog(this, "The project needs to be saved before you add an image.\n\nAction aborted!","Error", JOptionPane.ERROR_MESSAGE,Moenagade.IMG_ERROR);
            }
        }
        else
        {
            project.openImage(this);
            updateProjectTree();
        }
    }//GEN-LAST:event_speNewImageActionPerformed

    private void speRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_speRunActionPerformed
        if(!project.isSaved())
        {
            if(project.askToSave(true))
            {
                project.generateSource();
                project.run();
            }
            else
            {
                JOptionPane.showMessageDialog(this, "The project needs to be saved before you can run it.\n\nRun aborted!","Error", JOptionPane.ERROR_MESSAGE,Moenagade.IMG_ERROR);
            }
        }
        else
        {
            project.generateSource();
            project.run();
        }
    }//GEN-LAST:event_speRunActionPerformed

    private void miNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miNewActionPerformed
        speNewActionPerformed(evt);
    }//GEN-LAST:event_miNewActionPerformed

    private void miOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miOpenActionPerformed
        speOpenActionPerformed(evt);
    }//GEN-LAST:event_miOpenActionPerformed

    private void miSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miSaveActionPerformed
        speSaveActionPerformed(evt);
    }//GEN-LAST:event_miSaveActionPerformed

    private void miSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miSaveAsActionPerformed
        System.out.println("To be done ...");
    }//GEN-LAST:event_miSaveAsActionPerformed

    private void miQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miQuitActionPerformed
        formWindowClosing(null);
    }//GEN-LAST:event_miQuitActionPerformed

    private void miWorldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miWorldActionPerformed
        speNewWorldActionPerformed(evt);
    }//GEN-LAST:event_miWorldActionPerformed

    private void miEntityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miEntityActionPerformed
        speNewEntityActionPerformed(evt);
    }//GEN-LAST:event_miEntityActionPerformed

    private void miImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miImageActionPerformed
        speNewImageActionPerformed(evt);
    }//GEN-LAST:event_miImageActionPerformed

    private void miRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miRunActionPerformed
        speRunActionPerformed(evt);
    }//GEN-LAST:event_miRunActionPerformed

    private void miAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miAboutActionPerformed
        project.about();
    }//GEN-LAST:event_miAboutActionPerformed

    private void speUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_speUndoActionPerformed
        bloxsClass.getEditor().undo();
    }//GEN-LAST:event_speUndoActionPerformed

    private void speRedoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_speRedoActionPerformed
        bloxsClass.getEditor().redo();
    }//GEN-LAST:event_speRedoActionPerformed

    private void consoleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_consoleMouseClicked
        if(evt.getClickCount()==2)
            splitter4.setDividerLocation(5000);
    }//GEN-LAST:event_consoleMouseClicked

    private void speDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_speDeleteActionPerformed
        if(selectedNode instanceof Entity)
        {
            if(project.removeEntity((Entity) selectedNode))
                updateProjectTree();
        }
        else if(selectedNode instanceof World)
        {
            if(project.removeWorld((World) selectedNode))
                updateProjectTree();
        }
        else if(selectedNode instanceof ImageFile)
        {
            if(project.removeImage((ImageFile) selectedNode))
                updateProjectTree();
        }
        else if(selectedNode instanceof SoundFile)
        {
            if(project.removeSound((SoundFile) selectedNode))
                updateProjectTree();
        }
    }//GEN-LAST:event_speDeleteActionPerformed

    private void miDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miDeleteActionPerformed
        speDeleteActionPerformed(evt);
    }//GEN-LAST:event_miDeleteActionPerformed

    private void speRenameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_speRenameActionPerformed
        if(selectedNode instanceof Entity)
        {
            if(project.renameEntity((Entity) selectedNode)!=null)
                updateProjectTree();
        }
        else if(selectedNode instanceof World)
        {
            if(project.renameWorld((World) selectedNode)!=null)
                updateProjectTree();
        }
        else if(selectedNode instanceof ImageFile)
        {
            if(project.renameImage((ImageFile) selectedNode))
                updateProjectTree();
        }
        else if(selectedNode instanceof SoundFile)
        {
            if(project.renameSound((SoundFile) selectedNode))
            {
                updateProjectTree();
                openNothing();
            }
        }
    }//GEN-LAST:event_speRenameActionPerformed

    private void miRenameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miRenameActionPerformed
        speRenameActionPerformed(evt);
    }//GEN-LAST:event_miRenameActionPerformed

    private void popWorldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popWorldActionPerformed
        speNewWorldActionPerformed(evt);
    }//GEN-LAST:event_popWorldActionPerformed

    private void popEntityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popEntityActionPerformed
        speNewEntityActionPerformed(evt);
    }//GEN-LAST:event_popEntityActionPerformed

    private void popImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popImageActionPerformed
        speNewImageActionPerformed(evt);
    }//GEN-LAST:event_popImageActionPerformed

    private void popDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popDeleteActionPerformed
        speDeleteActionPerformed(evt);
    }//GEN-LAST:event_popDeleteActionPerformed

    private void popRenameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popRenameActionPerformed
        speRenameActionPerformed(evt);
    }//GEN-LAST:event_popRenameActionPerformed

    private void treeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeMousePressed
        if(evt.getButton()==MouseEvent.BUTTON3) 
        {
            // select the node under the click
            int selRow = tree.getRowForLocation(evt.getX(), evt.getY());
            TreePath selPath = tree.getPathForLocation(evt.getX(), evt.getY());
            tree.setSelectionPath(selPath); 
            if (selRow>-1){
               tree.setSelectionRow(selRow); 
            }

            // display all items
            popWorld.setVisible(true);
            popEntity.setVisible(true);
            popImage.setVisible(true);
            popSound.setVisible(true);
            
            // adaptive popup menu
            if(selPath.toString().contains("Worlds"))
            {
                popEntity.setVisible(false);
                popImage.setVisible(false);
                popSound.setVisible(false);
            }
            else if(selPath.toString().contains("Entities"))
            {
                popWorld.setVisible(false);
                popImage.setVisible(false);
                popSound.setVisible(false);
            }
            else if(selPath.toString().contains("Images"))
            {
                popWorld.setVisible(false);
                popEntity.setVisible(false);
                popSound.setVisible(false);
            }
            else if(selPath.toString().contains("Sounds"))
            {
                popWorld.setVisible(false);
                popEntity.setVisible(false);
                popImage.setVisible(false);
            }
            
            treePopper.show(tree, evt.getX(), evt.getY());  
        }
    }//GEN-LAST:event_treeMousePressed

    private void speNewSoundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_speNewSoundActionPerformed
        if(!project.isSaved())
        {
            if(project.askToSave(true))
            {
                project.openSound(this);
                updateProjectTree();
            }
            else
            {
                JOptionPane.showMessageDialog(this, "The project needs to be saved before you add a sound file.\n\nAction aborted!","Error", JOptionPane.ERROR_MESSAGE,Moenagade.IMG_ERROR);
            }
        }
        else
        {
            project.openSound(this);
            updateProjectTree();
        }
    }//GEN-LAST:event_speNewSoundActionPerformed

    private void miSoundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miSoundActionPerformed
        speNewSoundActionPerformed(evt);
    }//GEN-LAST:event_miSoundActionPerformed

    private void popSoundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popSoundActionPerformed
        speNewSoundActionPerformed(evt);
    }//GEN-LAST:event_popSoundActionPerformed

    private void speJarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_speJarActionPerformed
        project.jar();
    }//GEN-LAST:event_speJarActionPerformed

    private void miJarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miJarActionPerformed
        speJarActionPerformed(evt);
    }//GEN-LAST:event_miJarActionPerformed

    private void catListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_catListValueChanged
        tabs.setSelectedIndex(catList.getSelectedIndex());
    }//GEN-LAST:event_catListValueChanged

    private void splitter1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_splitter1PropertyChange
        
    }//GEN-LAST:event_splitter1PropertyChange

    private void catListComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_catListComponentResized
        
    }//GEN-LAST:event_catListComponentResized

    private void splitter2ComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_splitter2ComponentResized
        catList.setFixedCellWidth((splitter2.getBottomComponent().getWidth()-2)/3);
    }//GEN-LAST:event_splitter2ComponentResized

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // re-align categories
        catList.setFixedCellWidth((splitter2.getBottomComponent().getWidth()-2)/3);
    }//GEN-LAST:event_formComponentShown

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<String> catList;
    private lu.fisch.moenagade.console.Console console;
    private javax.swing.JScrollPane editorScroller;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JMenuItem miAbout;
    private javax.swing.JMenuItem miDelete;
    private javax.swing.JMenuItem miEntity;
    private javax.swing.JMenu miFile;
    private javax.swing.JMenu miHelp;
    private javax.swing.JMenuItem miImage;
    private javax.swing.JMenuItem miJar;
    private javax.swing.JMenuBar miMenu;
    private javax.swing.JMenuItem miNew;
    private javax.swing.JMenuItem miOpen;
    private javax.swing.JMenu miProject;
    private javax.swing.JMenuItem miQuit;
    private javax.swing.JMenuItem miRename;
    private javax.swing.JMenuItem miRun;
    private javax.swing.JMenuItem miSave;
    private javax.swing.JMenuItem miSaveAs;
    private javax.swing.JMenuItem miSound;
    private javax.swing.JMenuItem miWorld;
    private javax.swing.JMenuItem popDelete;
    private javax.swing.JMenuItem popEntity;
    private javax.swing.JMenuItem popImage;
    private javax.swing.JMenuItem popRename;
    private javax.swing.JMenuItem popSound;
    private javax.swing.JMenuItem popWorld;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JButton speDelete;
    private javax.swing.JButton speJar;
    private javax.swing.JButton speNew;
    private javax.swing.JButton speNewEntity;
    private javax.swing.JButton speNewImage;
    private javax.swing.JButton speNewSound;
    private javax.swing.JButton speNewWorld;
    private javax.swing.JButton speOpen;
    private javax.swing.JButton speRedo;
    private javax.swing.JButton speRename;
    private javax.swing.JButton speRun;
    private javax.swing.JButton speSave;
    private javax.swing.JButton speUndo;
    private javax.swing.JSplitPane splitter1;
    private javax.swing.JSplitPane splitter2;
    private javax.swing.JSplitPane splitter3;
    private javax.swing.JSplitPane splitter4;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JToolBar tbEditor;
    private javax.swing.JToolBar tbProject;
    private javax.swing.JToolBar tbRun;
    private javax.swing.JToolBar tbStandard;
    private javax.swing.JPanel topPanel;
    private javax.swing.JTree tree;
    private javax.swing.JPopupMenu treePopper;
    // End of variables declaration//GEN-END:variables
}
