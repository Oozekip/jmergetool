// Copyright (c) 2016 Matt Samudio (Zenteknix)  All Rights Reserved.
// Contact information for Zenteknix is available at http://www.zenteknix.com
//
// This file is part of JMergeTool.
//
// JMergeTool is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as
// published by the Free Software Foundation, either version 3 of
// the License, or (at your option) any later version.
//
// JMergeTool is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the
// GNU Lesser General Public License along with JMergeTool.
// If not, see <http://www.gnu.org/licenses/>.
package com.ztx.jmergetool;

import java.io.*;
import java.text.NumberFormat;

import java.util.Set;
import java.util.Iterator;
import java.util.Vector;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;

import java.awt.KeyboardFocusManager;
import java.awt.Color;
import java.awt.SystemColor;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.SwingUtilities;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.tree.*;
import javax.swing.JTextField;
import javax.swing.JFormattedTextField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JSplitPane;
import javax.swing.BoxLayout;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.border.Border;
import javax.swing.border.BevelBorder;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Date;

import java.security.Security;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import com.ztx.util.Trace;
import com.ztx.util.Misc;
import com.ztx.util.MagStripe;

//@SuppressWarnings("unchecked")

public class DemoPage01 extends JPanel implements ActionListener {

	public DemoPage01( Properties pOpts) {
		pTree = null;
		pFrom = null;
		pTo = null;
		init();
		initComponents();
		return;
	}

	protected void init() {
		setLayout( new BorderLayout());
		return;
	}

	protected void initComponents() {
		setBorder( new BevelBorder( BevelBorder.RAISED));
		add( generateFilterArea(), BorderLayout.NORTH);
		add( generateKeyboardArea(), BorderLayout.CENTER);
		return;
	}

	private JPanel generateFilterArea() {
		JPanel pRet = new JPanel( new GridBagLayout());
		GridBagConstraints pGBC = null;
		pGBC = new GridBagConstraints(); pGBC.gridx=0; pGBC.gridy=0;
		pRet.add( new JLabel( "So far, this is just a demo"), pGBC);
		return( pRet);
	}

	private JPanel generateKeyboardArea() {
		JPanel pRet = new JPanel();
		pRet.setLayout( new BoxLayout( pRet, BoxLayout.Y_AXIS));
		pRet.add( generateControlArea());
	//	Add a split-pane section
		JSplitPane pArea = new JSplitPane();
		pArea.setOrientation( SwingUtilities.VERTICAL);
		pArea.setContinuousLayout( true);
//		pArea.setResizeWeight( 1);
		pArea.setPreferredSize( new Dimension( 350, 500));
		pArea.setMinimumSize( new Dimension( 350, 500));
		pArea.setMaximumSize( new Dimension( 640, 1200));
	//	Put a tree control in here
		pTree = new JTree( new DefaultTreeModel( new DefaultMutableTreeNode( "Some stuff in the tree control")));
		pArea.setTopComponent( new JScrollPane( pTree));
	//	Add something here, too
		JPanel pBox = new JPanel();
		pBox.add( new JLabel( "something in the box"));
		pArea.setBottomComponent( pBox);
	// All done laying out this part of the GUI, return it
		pRet.add( pArea);
		return( pRet);
	}

	private JPanel generateControlArea() {
		JPanel pRet = new JPanel( new BorderLayout());
		JSplitPane pSplit = new JSplitPane();
		pSplit.setOrientation( SwingUtilities.VERTICAL);
		pSplit.setContinuousLayout( true);
		pSplit.setResizeWeight( 0.4);
		pSplit.setTopComponent( new JLabel( "some stuff in the left"));
		pSplit.setBottomComponent( new JLabel( "other stuff in the right"));
		pRet.add( pSplit, BorderLayout.CENTER);
		return( pRet);
	}

	public void actionPerformed( ActionEvent e) {
		boolean bNext = true;
		String sCmd = e.getActionCommand();
		if ( sCmd == "hi") Trace.info( "Hi");
		return;
	}

	private JTree pTree;
	private JTextField pFrom;
	private JTextField pTo;

	private boolean bTesting;
}
