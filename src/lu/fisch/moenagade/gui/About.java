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

package lu.fisch.moenagade.gui;

/******************************************************************************************************
 *
 *      Author:         Bob Fisch
 *
 *      Description:    This is the "about" dialog window.
 *
 ******************************************************************************************************
 *
 *      Revision List
 *
 *      Author          Date			Description
 *      ------		----			-----------
 *      Bob Fisch       2007.12.29              First Issue
 *      Bob Fisch       2009.05.22              Adapted for Unimozer
 *      Bob Fisch       2017.04.10              Adapted for Moenagade
 *
 ******************************************************************************************************
 *
 *      Comment:		I used JFormDesigner to desin this window graphically.
 *
 ******************************************************************************************************///

/*
 * Created by JFormDesigner on Sat Dec 29 21:36:58 CET 2007
 */

import java.io.*; 

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import lu.fisch.moenagade.Moenagade;


public class About extends JDialog implements ActionListener, KeyListener
{
	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Robert Fisch
	protected JPanel dialogPane;
	protected JPanel contentPanel;
	protected JPanel pnlLeft;
	protected JPanel pnlTop;
	protected JPanel panel2;
	protected JLabel lblName;
	protected JLabel lblVersion;
	protected JLabel label2;
	protected JTabbedPane pnlTabbed;
	protected JScrollPane scrollPane1;
	protected JTextPane txtThanks;
	protected JScrollPane scrollPane2;
	protected JTextPane txtChangelog;
	protected JScrollPane scrollPane3;
	protected JTextPane txtLicence;
	protected JPanel buttonBar;
	protected JButton btnOK;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
	
	public void create()
	{
		// set window title
		setTitle("About");
		// set layout (OS default)
		setLayout(null);
		// set windows size
		setSize(600, 400);
		// show form
		setVisible(false);
		// set action to perfom if closed
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// set icon

		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Robert Fisch
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		pnlLeft = new JPanel();
		pnlTop = new JPanel();
		panel2 = new JPanel();
		lblName = new JLabel();
		lblVersion = new JLabel();
		label2 = new JLabel();
		pnlTabbed = new JTabbedPane();
		scrollPane1 = new JScrollPane();
		txtThanks = new JTextPane();
		scrollPane2 = new JScrollPane();
		txtChangelog = new JTextPane();
		scrollPane3 = new JScrollPane();
		txtLicence = new JTextPane();
		buttonBar = new JPanel();
		btnOK = new JButton();
		
		//======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		
		//======== dialogPane ========
		{
			dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
			
			// JFormDesigner evaluation mark
			/*
			dialogPane.setBorder(new javax.swing.border.CompoundBorder(
																	   new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
																										   "JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
																										   javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
																										   java.awt.Color.red), dialogPane.getBorder())); dialogPane.addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});
			*/
			dialogPane.setLayout(new BorderLayout());
			
			//======== contentPanel ========
			{
				contentPanel.setLayout(new BorderLayout(5, 5));
				
				//======== pnlLeft ========
				{
					pnlLeft.setLayout(new BorderLayout());
				}
				contentPanel.add(pnlLeft, BorderLayout.WEST);
				
				//======== pnlTop ========
				{
					pnlTop.setLayout(new BorderLayout());
					
					//======== panel2 ========
					{
						panel2.setLayout(new BorderLayout());
						
						//---- lblName ----
						lblName.setText("Moenagade");
						lblName.setFont(lblName.getFont().deriveFont(lblName.getFont().getStyle() | Font.BOLD, lblName.getFont().getSize() + 10f));
						panel2.add(lblName, BorderLayout.NORTH);
						
						//---- lblVersion ----
						lblVersion.setText(Moenagade.E_VERSION);
						panel2.add(lblVersion, BorderLayout.CENTER);
					}
					pnlTop.add(panel2, BorderLayout.CENTER);
					
					//---- label2 ----
					label2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lu/fisch/moenagade/images/moenagade32.png")));
					label2.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
					pnlTop.add(label2, BorderLayout.WEST);
				}
				contentPanel.add(pnlTop, BorderLayout.NORTH);
				
				//======== pnlTabbed ========
				{
					//pnlTabbed.setSelectedIndex(0);
					pnlTabbed.setTabPlacement(SwingConstants.BOTTOM);
					
					//======== scrollPane1 ========
					{
						scrollPane1.setViewportView(txtThanks);
					}
					pnlTabbed.addTab("About", scrollPane1);
					
					
					//======== scrollPane2 ========
					{
						scrollPane2.setViewportView(txtChangelog);
					}
					pnlTabbed.addTab("Changelog", scrollPane2);

					//======== scrollPane3 ========
					{
						scrollPane3.setViewportView(txtLicence);
					}
					pnlTabbed.addTab("License", scrollPane3);
				}
				contentPanel.add(pnlTabbed, BorderLayout.CENTER);
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);
			
			//======== buttonBar ========
			{
				buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
				buttonBar.setLayout(new GridBagLayout());
				((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 80};
				((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0};
				
				//---- okButton ----
				btnOK.setText("OK");
				btnOK.addActionListener(this);
				buttonBar.add(btnOK, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
															   GridBagConstraints.CENTER, GridBagConstraints.BOTH,
															   new Insets(0, 0, 0, 0), 0, 0));
			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		//pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents

		btnOK.addKeyListener(this);
		txtThanks.addKeyListener(this);
		txtChangelog.addKeyListener(this);
		pnlTabbed.addKeyListener(this);
		addKeyListener(this);
		
		txtThanks.setEditable(false);
		txtChangelog.setEditable(false);
		
		
		String input = new String();
		try
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("../changelog.txt")));
			String str;
			while ((str = in.readLine()) != null)
			{
				input+=str+"\n";
			}
			in.close();
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
		txtChangelog.setText(input);


		txtThanks.setText(Moenagade.E_THANKS);
		txtThanks.setCaretPosition(0);

		//txtChangelog.setText(Element.E_CHANGELOG);
		txtChangelog.setCaretPosition(0);

		input = new String();
		try
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("../license.txt")));
			String str;
			while ((str = in.readLine()) != null)
			{
				input+=str+"\n";
			}
			in.close();
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
		txtLicence.setText(input);
		txtLicence.setCaretPosition(0);

        txtChangelog.setFont(new Font("Courier",0,12));
        txtLicence.setFont(new Font("Courier",0,12));

		lblVersion.setText("Version "+Moenagade.E_VERSION);
	}
	
	// listen to actions
	public void actionPerformed(ActionEvent event)
	{
				setVisible(false);
	}
	
	public void keyTyped(KeyEvent kevt) 
	{
    }
	
	public void keyPressed(KeyEvent e) 
	{
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
                    setVisible(false);
		}
		else if(e.getKeyCode() == KeyEvent.VK_ENTER && (e.isShiftDown() || e.isControlDown()))
		{
			setVisible(false);
		}
    }
	
	public void keyReleased(KeyEvent ke)
	{
	} 
	

	public About(Frame owner)
	{
		super(owner);
		this.setModal(true);
		create();
	}

}
