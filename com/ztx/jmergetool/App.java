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

import java.util.Set;
import java.util.Iterator;
import java.util.Vector;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;

import java.text.SimpleDateFormat;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Cursor;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.BorderLayout;

import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.tree.*;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Date;

import com.ztx.util.Trace;

@SuppressWarnings("unchecked")

public class App extends JFrame implements	ActionListener,
															ListSelectionListener {

	public static App pApp = null;

	public static void main( String args[]) {
		try {
			Properties pOpts = null;
			String sPFile = System.getProperty( "cfg");
			if ( sPFile == null) sPFile = new String( ".jmtrc");
			InputStream pIn = new FileInputStream( sPFile);
			if ( pIn != null) {
				pOpts = new Properties();
				pOpts.load( pIn);
			}
		//	Abort if there are no options at all
			if ( pOpts != null) {
				pApp = new App( pOpts);
				pApp.setVisible( true);
			} else Trace.error( "ERROR: Properties not available from "+sPFile);
		} catch ( Exception pErr) { Trace.error( pErr); }
		return;
	}

	public static String version() {
		return( new String( "0.1.0"));
	}

	public App( Properties pOptions) {
		pOpts = pOptions;
		bTesting = false;
		bNew = false;
		pCxn = null;
		pRmv = null;
		CmdMap = new Hashtable();
		EFlds = new Hashtable();
		NFlds = new Hashtable();

		sHost = pOpts.getProperty( "jmergetool.jdbc.host");
		sDB = pOpts.getProperty( "jmergetool.jdbc.db");
		sUID = pOpts.getProperty( "jmergetool.jdbc.uid");
		sPwd = pOpts.getProperty( "jmergetool.jdbc.pass");

		String sLog = pOpts.getProperty( "jmergetool.log.file");
		String sLevel = pOpts.getProperty( "jmergetool.log.level");
		int iLevel = Trace.DEFAULT;
		if ( sLog != null) Trace.setOutputFile( sLog);
		if ( sLevel.equalsIgnoreCase( "none")) {
			iLevel = Trace.NONE;
		} else if ( sLevel.equalsIgnoreCase( "error")) {
			iLevel = Trace.ERROR;
		} else if ( sLevel.equalsIgnoreCase( "info")) {
			iLevel = Trace.INFO;
		} else if ( sLevel.equalsIgnoreCase( "summary")) {
			iLevel = Trace.SUMMARY;
		} else if ( sLevel.equalsIgnoreCase( "debug")) {
			iLevel = Trace.DEBUG;
		} else if ( sLevel.equalsIgnoreCase( "detail")) {
			iLevel = Trace.DETAIL;
		} else if ( sLevel.equalsIgnoreCase( "diagnostic")) {
			iLevel = Trace.DIAGNOSTIC;
		}
		Trace.setLevel( iLevel);
		init();
		initComponents();
		return;
	}

	protected void initComponents() {
		setTitle( "Merge Application (v"+version()+")");
		addWindowListener(	new java.awt.event.WindowAdapter() {
										public void windowClosing(
											java.awt.event.WindowEvent evt)
										{ exitForm( evt); }
									} );
		pTabs = new JTabbedPane();
		pTabs.addTab( "Demo", generateDemoPage01());
		pTabs.addTab( "Debug", generateDebugPage());
		getContentPane().add( pTabs, BorderLayout.CENTER);
		pack();
		return;
	}

	private Component generateDebugPage() {
		JPanel pRet = new JPanel( new BorderLayout());
		pDbgLog = new JTextArea();
		pDbgLog.setEditable( false);
		pRet.add( new JScrollPane( pDbgLog), BorderLayout.CENTER);
		return( pRet);
	}

//	This instantiates your class, found in DemoPage01.java, which
//	controls your GUI page, and is where all your code will go
	private Component generateDemoPage01() {
		JPanel pRet = new DemoPage01( pOpts);
		return( pRet);
	}

	protected boolean init() {
		boolean bRet = bTesting ? true : false;
		if ( !bRet) {
			String sPfx = "jdbc:mysql://";
			String sDrv = "com.mysql.jdbc.Driver";
	//		String sURL = sPfx+sHost+"/"+sDB+"?user="+sUID+"&password="+sPwd;
			String sURL = sPfx+sHost+"/"+sDB;
			try {
				Class.forName( sDrv);
	//			pCxn = DriverManager.getConnection( sURL);
				pCxn = DriverManager.getConnection( sURL, sUID, sPwd);
				if ( pCxn != null) bRet = true;
			} catch ( Exception pErr) {
				Trace.error( pErr);
				bRet = false;
			}
		}
		return( bRet);
	}

	protected void exitForm( java.awt.event.WindowEvent evt) {
		if ( pCxn != null) {
			try {
				pCxn.close();
			} catch ( Exception pErr) { pErr.printStackTrace(); }
			pCxn = null;
		}
		System.exit( 0);
	}

	public void actionPerformed( ActionEvent e) {
		boolean bNext = true;
		String sCmd = e.getActionCommand();
		if ( sCmd == "hi") Trace.info( "Hi");
		return;
	}

	public void valueChanged( ListSelectionEvent e) {
		if ( !e.getValueIsAdjusting()) {
			Object pSrc = e.getSource();
			if ( pSrc instanceof JList) {
				JList pLst = (JList)pSrc;
				int iTab = pTabs.getSelectedIndex();
				if ( (iTab == 0) || (iTab == 1)) {
				} else {
				}
			}
		}
		return;
	}

	private Properties pOpts;
	private Vector pRmv;
	private boolean bNew;
	private String sHost;
	private String sDB;
	private String sUID;
	private String sPwd;
	private Connection pCxn;
	private JTabbedPane pTabs;
	private JTextArea pDbgLog;
	private Hashtable CmdMap;
	private Hashtable NFlds;
	private Hashtable EFlds;
	private boolean bTesting;
}
