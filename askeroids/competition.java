package askeroids;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.*;
import java.net.*;
import java.io.*;

/**
 * Title:        competition
 * Description:  Competition Applet class. A stupid competition
 * Copyright:    Copyright (c) 2000
 * Company:      Miami International Ltd
 * @author       Glynn Bird
 * @version 1.0
 */


public class competition extends Applet implements ActionListener{
        private    TextField TF;
        private    Label Lab;
	private    Button  BU;
        private    String Hiscore;
         boolean isStandalone = false;       // a borland thing

  /**Construct the applet*/
  public competition()
  {
    TF=new TextField("",20);
    Lab=new Label("Email address");
    BU=new Button("Enter");
  }

  /**Initialize the applet*/
  public void init()
  {
    this.setBackground(Color.white);
    ShowDataEntryControls();
  }

  /** Display the form asking for the user's  email address **/
  public void ShowDataEntryControls()
  {
    if(TF.getText().length()==0)
    {
      add(Lab);
      add(TF);
      add(BU);
      TF.addActionListener(this);
      BU.addActionListener(this);
    }
  }

  /** Remove the form asking for the user's email address **/
  public void RemoveDataEntryControls()
  {
    TF.removeActionListener(this);
    remove(Lab);
    remove(TF);
  }

  /**Start the applet. Create a runner thread.**/
  public void start()
  {
  }

  /** Main program thread. The main program loop.**/
  public void run()
  {
   }

  /**Stop the applet*/
  public void stop() {
  }

  public void actionPerformed(ActionEvent evt)
  {
     RegisterHighScore(0);
     AppletContext AC=getAppletContext();
     URL U;
     String UrlStr;
     try
     {
       String CB=getCodeBase().toString();
       UrlStr=CB+"comp2.html";
       U=new URL(UrlStr);
      } 
      catch(MalformedURLException e) 
      { 
        return;
      };
      AC.showDocument(U);
  }


  /**Get a parameter value*/
  public String getParameter(String key, String def) {
    return isStandalone ? System.getProperty(key, def) :
      (getParameter(key) != null ? getParameter(key) : def);
  }

  /**Record the user's high score with the "score" CGI script, using an HTTP request.
  *  The request is encoded using an MD5 algorithm to deter cheating.
  * @param  Score The score to register  X position of old rock.*/
  public void RegisterHighScore(int Score)
  {
    InetAddress Addr;
    Socket Sock;
    PrintWriter out;
    BufferedReader in;
    String GetStr;
    String  userInput;
    if(TF.getText().length()>0)
    {
      // make querystring
      GetStr="2?"+TF.getText()+"?"+Score;

      // calculate checksum
      MD5 M=new MD5();
      M.Init();
      M.Update(GetStr+"?monkey");

      // add checksum to querystring
      GetStr=GetStr+"?"+M.asHex();
    }
    else
    {
      return;
    }

     AppletContext AC=getAppletContext();
     URL U;
     String UrlStr;
     try
     {
       String CB=getCodeBase().toString();
       UrlStr=CB+"cgi-bin/camera?"+GetStr;
       U=new URL(UrlStr);
      } 
      catch(MalformedURLException e) 
      { 
        return;
      };
      AC.showDocument(U);
  }

  /**Get Applet information*/
  public String getAppletInfo() {
    return "Applet Information";
  }
  /**Get parameter info*/
  public String[][] getParameterInfo() {
    return null;
  }

  /** draw the screen
  * @param g  The graphics object to paint on*/
  public void paint(Graphics g)
  {

  }

  public void update(Graphics g)
  {
     paint(g);
  }
}
