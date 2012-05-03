package askeroids;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.*;
import java.net.*;
import java.io.*;
import java.lang.System;

/**
 * Title:        askeroid
 * Description:  Askeroid Applet class. A game of asteroids with gravity and
 *               optional images
 * Copyright:    Copyright (c) 2000
 * Company:      Miami International Ltd
 * @author       Glynn Bird
 * @version 1.0
 */


public class askeroid extends Applet implements Runnable,ActionListener{
  final  int     MAX_ROCKS=30;               // MAX_ROCKS per game
  final  int     START_ROCKS=1;              // initial number of rocks
  final  int     MAX_MISSILES=25;            // max number of missiles in flight at any time
  final  int     MAX_EXPLOSIONS=100;         // max number of explosion bits in flight
  final  int     MAX_FRIENDS=10;
         boolean isStandalone = false;       // a borland thing
         boolean StopThread;                 // flag to kill the run thread
         Thread  runner;                     // the run thread
         Projectile Ship;                    // the user's ship
         Rock    R[];                        // the array of rocks
         Image    OffScreenBitmap;           // bitmap to reduce flicker
         Graphics OffG;                      // graphics context for off-screen drawing
         Missile  M[];                       // missiles in flight
         AudioClip Launch;                   // sound to indicate launched missile
         AudioClip Bang;                     // sound to indicate rock hit
         AudioClip BigBang;                  // sound to indicate ship hit
         Explosion E[];                      // pieces of the explosion
         boolean   ShipDead;                 // is the ship hit
         int       NumLives;                 // number of lives
         int       NumRocks;                 // number of rocks on screen
         alien     AL;                       // the alien that appears at random
         boolean   DeathIsImminent;          // the user's ship has been hit
         int       Score;                    // the user's score
         boolean   ShipGravity,MissileGravity,
                   AlienGravity,AllowAliens,
                   UseSounds,ShowImages;     // boolean values of on-screen checkboxes
        Image      TitleImage;
        MediaTracker MT;
        double     eSpin;
        double     eAcceleration;
        int        eFiring;
        private    String Hiscore;
	boolean    Recoil;
	boolean    Shield;
	long       Start;
	int        DebugKey;
	long       LastHyperspace;
	boolean    CaravanMode;
        Projectile Friend[];                    // the user's ship
	int        NumFriends;
	String     EmailAddress;
	
//        Image      Background;

  /**Construct the applet*/
  public askeroid()
  {
    R=new Rock[MAX_ROCKS];
    M=new Missile[MAX_MISSILES];
    E=new Explosion[MAX_EXPLOSIONS];
    Friend=new Projectile[MAX_FRIENDS];
    ShipDead=false;
    AL=null;
    DeathIsImminent=false;
    ShipGravity=MissileGravity=AlienGravity=AllowAliens=true;
    eSpin=0.0;
    eAcceleration=0.0;
    eFiring=0;
    UseSounds=false;
    Hiscore=new String("");
    LastHyperspace=0;
  }

  /**Initialize the applet*/
  public void init()
  {
    if(UseSounds==true)
    {
      // load the sounds
      Launch=getAudioClip(getCodeBase(),"askeroids/launch.wav");
      Bang=getAudioClip(getCodeBase(),"askeroids/bang.wav");
      BigBang=getAudioClip(getCodeBase(),"askeroids/bigbang.wav");
    }

    // create checkboxes
    this.setBackground(Color.black);
    this.setForeground(Color.white);

    // create three new lives
    NewLife();

    // show six s initially
    NumRocks=2;

    // start a new game
    restart_game();

    // kill the ship until mouse click
    ShipDead=true;
    NumLives=0;
    ShowDataEntryControls();
    EmailAddress=getParameter("EmailAddress","");
    Hiscore+=getParameter("Hiscore0","")+"\n";
    Hiscore+=getParameter("Hiscore1","")+"\n";
    Hiscore+=getParameter("Hiscore2","")+"\n";
    Hiscore+=getParameter("Hiscore3","")+"\n";
    Hiscore+=getParameter("Hiscore4","")+"\n";
    Hiscore+=getParameter("Hiscore5","")+"\n";
    Hiscore+=getParameter("Hiscore6","")+"\n";
    Hiscore+=getParameter("Hiscore7","")+"\n";
    Hiscore+=getParameter("Hiscore8","")+"\n";
    Hiscore+=getParameter("Hiscore9","")+"\n";
  }

  /** Display the form asking for the user's  email address **/
  public void ShowDataEntryControls()
  {
  }

  /** Remove the form asking for the user's email address **/
  public void RemoveDataEntryControls()
  {
  }

  /**Start the applet. Create a runner thread.**/
  public void start()
  {
      // create thread
      runner=new Thread(this);

      // start the running thread
      StopThread=false;
      runner.start();
  }

  /** Main program thread. The main program loop.**/
  public void run()
  {
    int     i,j;
    boolean ShipHit;
    Date    Timer=new Date();

    // load the off-screen background image
    if(OffScreenBitmap==null)
    {
      OffScreenBitmap=createImage(this.getSize().width,this.getSize().height);
      OffG=OffScreenBitmap.getGraphics();
    }

    // load Title Bitmap
    MT=new MediaTracker(this);
    TitleImage=getImage(getCodeBase(),"askeroids/askeroids.gif");
    MT.addImage(TitleImage,0);
    RegisterHighScore(0);

    // until the applet dies
    while(!StopThread )
    {
      // check shield
      if(Shield)
      {
        if((new Date().getTime()-Start)>5000)
	 Shield=false;
      }

      // calculate ships speed and position
      Ship.AddAngle(eSpin);
      Ship.AddSpeed(eAcceleration);
      if(CaravanMode)
      {
        int LastX=Ship.GetX();
	int LastY=Ship.GetY();
	Projectile LastP=Ship;
	for(i=0;i<NumFriends;i++)
	{
	  if(Friend[i]!=null)
	  {
            double Ang=Math.atan2(LastY-Friend[i].GetY(),LastX-Friend[i].GetX());
	    double D=Friend[i].DistanceFrom(LastP);
	    LastP=Friend[i];
	    double A=eAcceleration+(D-50.0)/100.0;
	    if(A>2.0)
	      A=2.0;
	    if(A<-2.0)
	      A=-2.0;
            Friend[i].AddVelocity(A,Ang);
	    Friend[i].AddAngle(eSpin);
	    LastX=Friend[i].GetX();
	    LastY=Friend[i].GetY();
	  }
	}
      }
      if(eFiring==1 && !Shield)
      {
         CreateMissile(Ship.GetX(),Ship.GetY(),Ship.GetAngle(),Ship,Color.green);
	 if(CaravanMode)
	 {
           for(j=0;j<NumFriends;j++)
	   {
	     if(Friend[j]!=null)
	      CreateMissile(Friend[j].GetX(),Friend[j].GetY(),Ship.GetAngle(),Ship,Color.yellow);
	   }
         }
	 if(Recoil)
	 {
	   Ship.AddSpeed(-3.0);
/*	   if(CaravanMode && Friend!=null)
	     Friend.AddSpeed(-3.0);*/
	  }
         eFiring=2;
      }

      // calculate new position and introduce natural slow down
      Ship.CalculateNewPosition(this);
      Ship.SlowDown(0.2);
      if(CaravanMode)
      {  
        for(i=0;i<NumFriends;i++)
	{
	  if(Friend[i]!=null)
	  {
            Friend[i].CalculateNewPosition(this);
  	    Friend[i].SlowDown(0.2);
	  }
	}
      }

      // redraw the scene
      repaint();

      // wait a while
      try { Thread.sleep(50);}
      catch (InterruptedException e) { }

      // check for ship colliding with rocks
      for(i=0;i<MAX_ROCKS;i++)
      {
        // if ship has hit a rock
        if(DeathIsImminent ||(!Shield && R[i]!=null && !ShipDead &&
            R[i].Hit(Ship.GetX(),Ship.GetY(),Ship.GetShape().getBounds()) ) )
        {
          // remove a life and make a big bang
	  eSpin=eFiring=0;
          eAcceleration=0.0;
          NumLives--;
          CreateExplosion(Ship.GetX(),Ship.GetY());
          if(UseSounds==true)
            BigBang.play();
          ShipDead=true;
          DeathIsImminent=false;

          for(j=0;j<NumFriends;j++)
	    Friend[j]=null; 

          // if there are some lives left
          if(NumLives>0)
          {
            // start a 3 second timer to trigger new life
            Timer=new Date();
          }
          else
          {
            RegisterHighScore(Score);
            ShowDataEntryControls();
          }
        }

        // check for alien hitting rocks
        if(R[i]!=null && AL!=null && AL.GetType()!=5 &&
           R[i].Hit(AL.GetX(),AL.GetY(),AL.GetShape().getBounds()) )
        {
          CreateExplosion(AL.GetX(),AL.GetY());
          if(UseSounds==true)
            BigBang.play();
          AL=null;
        }
	
	for(j=0;j<=2;j++)
	{
	  if(R[i]!=null && Friend[j]!=null && !Shield &&
             R[i].Hit(Friend[j].GetX(),Friend[j].GetY(),Friend[j].GetShape().getBounds()))
	  {
            CreateExplosion(Friend[j].GetX(),Friend[j].GetY());
            if(UseSounds==true)
              BigBang.play();
            Friend[j]=null;
	  } 
	}
      }

      // new life after death+3 seconds
      if(ShipDead && NumLives>0 && ((new Date().getTime()-Timer.getTime())/1000)>3)
      {
        SafeHyperspace();
        ShipDead=false;
        Shield=true;
        Start=new Date().getTime();
      }

      // create alien man
      if(AllowAliens && AL==null && Math.random()>0.99)
      {
        AL=new alien((int)(getSize().width*Math.random()),(int)(getSize().height*Math.random()),
                      4,Math.random()*2*Math.PI,this);
      }
    }
   }

  /**Stop the applet*/
  public void stop() {
      StopThread=true;
  }

  public void actionPerformed(ActionEvent evt)
  {
  }

  /**Get a parameter value*/
  public String getParameter(String key, String def) {
    return isStandalone ? System.getProperty(key, def) :
      (getParameter(key) != null ? getParameter(key) : def);
  }

  /**Called when a mouse event occurs
  * @param evt  the event that has happened
  * @param x    the x coordinate of the mouse
  * @param y    the y coordinate of the mouse */
  public boolean mouseDown(Event evt,int x, int y)
  {
    // if waiting for new game
    if(ShipDead && NumLives==0)
    {
      // restart game
      NewLife();
      restart_game();
    }
    return true;
  }

  /**Called when a key is released
  * @param evt  the event that has happened
  * @param key  the key released */
  public boolean keyUp(Event evt, int key)
  {
    switch(key)
    {
      case Event.LEFT:
      case Event.RIGHT:
       eSpin=0.0;
       break;
      case Event.UP:
      case Event.DOWN:
        eAcceleration=0.0;
        break;
      case Event.ENTER:
      case 32:
        eFiring=0;
        break;
    }
    return true;
  }

  /**Called when a key is pressed
  * @param evt  the event that has happened
  * @param key  the key pressed */
  public boolean keyDown(Event evt, int key)
  {
    // do nothing if the ship is invisible
    if(ShipDead==true)
      return true;

    switch(key)
    {

      case 'H':
      case 'h':
        if((new Date().getTime()-LastHyperspace)>10000)
	{
          SafeHyperspace();
	  Shield=true;
          Start=new Date().getTime()-4000;
	  LastHyperspace=new Date().getTime();
        }	
	break;
	
      // spin to the left
      case Event.LEFT:
        eSpin=-0.2;
        break;
      // spin to the right
      case Event.RIGHT:
        eSpin=0.2;
        break;
      // thrust forward
      case Event.UP:
        eAcceleration=1;
        break;
      // thrust backwards
      case Event.DOWN:
        eAcceleration=-1;
        break;
      // fire
      case Event.ENTER:
      case 32:
         if(eFiring==0)
           eFiring=1;
        break;
      default : 
        DebugKey=key;
    }
    return true;
  }

  /**Create a new missile starting at X,Y moving in direction Angle.
  *  The projectile's owner is stored so that you can't be killed your own missiles.
  * @param X  The X position of the new missile
  * @param Y  The Y position of the new missile
  * @param Angle The direction of the missile
  * @param Owner The missile's creator
  * @param C  The missile's colour */
  public void CreateMissile(int X,int Y,double Angle,Projectile Owner,Color C)
  {
      Velocity V=new Velocity();
      V.AddVelocity(Owner.V.Speed,Owner.V.Direction);
      V.AddVelocity(10,Owner.Angle);

      int Index=GetNewMissileIndex();
      if(Index!=-1)
      {
        M[Index]=new Missile(X,Y,V.Speed,V.Direction,Owner,C);
        if(UseSounds==true  && Launch!=null)
          Launch.play();
      }
  }

  /**Get an index into the missile array which is not currently occupied
  * @return Free index into missile array, or -1 if array is full */
  public int GetNewMissileIndex()
  {
    int i;
    for(i=0;i<MAX_MISSILES;i++)
    {
      if(M[i]==null)
        return i;
    }
    return -1;
  }

  /**Get an index into the rock array which is not currently occupied
  * @return Free index into rock array, or -1 if array is full */
  public int GetNewRockIndex()
  {
    for(int i=0;i<MAX_ROCKS;i++)
    {
      if(R[i]==null)
        return i;
    }
    return -1;
  }

  /**Create explosion at X,Y
  * @param  X   X position of explosion.
  * @param  Y   Y position of explosion */
  public void CreateExplosion(int x, int y)
  {
    int i;
    int BitCount=0;

    for(i=0;i<MAX_EXPLOSIONS;i++)
    {
      if(E[i]==null)
      {
        E[i]=new Explosion(x,y,BitCount*2*Math.PI/10);
        BitCount++;
        if(BitCount>=10)
          break;
      }
    }
  }


  /**Create four new rocks where a big rock used to be
  * @param  X   X position of old rock.
  * @param  Y   Y position of old rock
  * @param  Scale the size to make the new rocks */
  public void SpawnRocks(int X,int Y,double Scale)
  {
    int i,Index;

    // don't allow rocks less than 1/4 scale
    if(Scale<=0.25)
      return;

    // create four new rocks going at random speeds
    for(i=0;i<4;i++)
    {
      Index=GetNewRockIndex();
      if(Index!=-1)
        R[Index]=new Rock(X,Y,(int)(Math.random()*10),Scale,this);
    }
  }

  /**Record the user's high score with the "score" CGI script, using an HTTP request.
  *  The request is encoded using an MD5 algorithm to deter cheating.
  * @param  Score The score to register  X position of old rock.*/
  public void RegisterHighScore(int Score)
  {
  /*  String GetStr;
    String  userInput;
    if(EmailAddress.length()>0 && Score>0)
    {
      // make querystring
      GetStr="1?"+EmailAddress+"?"+Score;

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
       UrlStr=CB+"cgi-bin/askeroids?"+GetStr;
       U=new URL(UrlStr);
      } 
      catch(MalformedURLException e) 
      { 
        return;
      };
      AC.showDocument(U);
*/



  }

  /**Start a new game*/
  public void NewLife()
  {

    NumRocks=START_ROCKS;
    NumLives=3;
    ShipGravity=false;
    MissileGravity=true;
    Recoil=false;
    CaravanMode=true;

    // reset score
    Score=0;

    RemoveDataEntryControls();
  }

  /**Restart the game after being killed*/
  public void restart_game()
  {
    int i;
    int xPoints[]={ 10,-10,0,-10,10};
    int yPoints[]={ 0,-10,0,10,0};

    // clear out old crap
    Ship=null;
    for(i=0;i<MAX_ROCKS;i++)
      R[i]=null;
    for(i=0;i<MAX_MISSILES;i++)
      M[i]=null;
    for(i=0;i<MAX_EXPLOSIONS;i++)
      E[i]=null;

   // create initial rocks
    for(i=0;i<NumRocks;i++)
      R[i]=new Rock(200+(int)(Math.random()*200),200+(int)(Math.random()*200),
                   (int)(Math.random()*5),2,this);

    // create ship
    Ship=new Projectile();
    Ship.SetShape(xPoints,yPoints,xPoints.length);
    Ship.SetScale(2.0);
    ShipDead=false;
    DeathIsImminent=false;

    // get values from checkboxes;
    AlienGravity=false;
    AllowAliens=true;
    ShowImages=false;
    UseSounds=false;
    Shield=true;
    Start=new Date().getTime()-2000;
    NumFriends=NumRocks-1+1;
    if(NumFriends>MAX_FRIENDS)
      NumFriends=MAX_FRIENDS;
    // caravan
    if(CaravanMode)
    {
      for(i=0;i<NumFriends;i++)
      {
        Friend[i]=new Projectile();
        Friend[i].SetShape(xPoints,yPoints,xPoints.length);
        Friend[i].SetScale(1.0);
        Friend[i].SetColor(new Color((int)(128+128*Math.random()),(int)(128+128*Math.random()),(int)(128+128*Math.random())));
        Friend[i].SetNewPosition(Ship.GetX(),Ship.GetY()+50*(i+1));
      }
    }
 }

 /** Move ship to a point where it won't hit any rocks straight away */
 public void SafeHyperspace()
 {
    boolean ShipHit;
    int     i;

    // make sure you're not on a rock to start with
    do
    {
      Ship.HyperSpace(700,600);
      ShipHit=false;
      for(i=0;i<MAX_ROCKS;i++)
      {
        if(R[i]!=null && R[i].Hit(Ship.GetX(),Ship.GetY(),Ship.GetShape().getBounds()))
          ShipHit=true;
      }
    } while(ShipHit);
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
     int i,j;
     double D,Angle;
     String str;
     long End;

    // if the offscreen bitmap is ready
    if(OffG!=null)
    {
      // draw background
      OffG.setColor(Color.black);
      OffG.fillRect(0,0,getSize().width,getSize().height);
/*      if(MT.statusID(0,true)==MT.COMPLETE)
        OffG.drawImage(Background,0,0,getSize().width,getSize().height,this);
*/

      // draw rocks
      boolean AllRocksDead=true;
      for(i=0;i<MAX_ROCKS;i++)
      {
        if(R[i]!=null)
        {
          R[i].CalculateNewPosition(this);
          R[i].Draw(OffG,ShowImages);
          AllRocksDead=false;

          // if gravity is on
          if(ShipGravity==true)
          {
            // add gravity effect to ship
            if(R[i]!=null)
            {
              D=Ship.DistanceFrom(R[i]);
              Angle=Math.atan2(R[i].GetY()-Ship.GetY(),R[i].GetX()-Ship.GetX());
// anti gravity              Angle=Math.atan2(Ship.GetY()-R[i].GetY(),Ship.GetX()-R[i].GetX());
              Ship.AddVelocity(2500*R[i].GetScale()/(D*D),Angle);
            }
          }

          if(AlienGravity)
          {
            // add gravity effect to alien
            if(R[i]!=null && AL!=null)
            {
              D=AL.DistanceFrom(R[i]);
              Angle=Math.atan2(R[i].GetY()-AL.GetY(),R[i].GetX()-AL.GetX());
              AL.AddVelocity(5000*R[i].GetScale()/(D*D),Angle);
            }
          }
	  

          // black hole!
	  if(AL!=null && AL.GetType()==5)
	  {
            // add gravity effect to alien
            if(R[i]!=null)
            {
              D=AL.DistanceFrom(R[i]);
              Angle=Math.atan2(AL.GetY()-R[i].GetY(),AL.GetX()-R[i].GetX());
              R[i].AddVelocity(20000*R[i].GetScale()/(D*D),Angle);
            }
	  
	  }

          if(MissileGravity)
          {
            // add gravity effect to missiles
            for(j=0;j<MAX_MISSILES;j++)
            {
              if(M[j]!=null && R[i]!=null)
              {
                D=M[j].DistanceFrom(R[i]);
                Angle=Math.atan2(R[i].GetY()-M[j].GetY(),R[i].GetX()-M[j].GetX());
// anti-gravity Angle=Math.atan2(M[j].GetY()-R[i].GetY(),M[j].GetX()-R[i].GetX());
                M[j].AddVelocity(5000*R[i].GetScale()/(D*D),Angle);
              }
            }

            // add gravity effect to explosions
/*            for(j=0;j<MAX_EXPLOSIONS;j++)
            {
                if(E[j]!=null && R[i]!=null)
                {
                  D=E[j].DistanceFrom(R[i]);
                  Angle=Math.atan2(R[i].GetY()-E[j].GetY(),R[i].GetX()-E[j].GetX());
  // anti-gravity Angle=Math.atan2(M[j].GetY()-R[i].GetY(),M[j].GetX()-R[i].GetX());
                  E[j].AddVelocity(2500*R[i].GetScale()/(D*D),Angle);
                }
            }*/
          }
        }
      }

      /* stop game if all rocks are dead */
      if(AllRocksDead && !ShipDead)
      {
        // increase number of rocks
        NumRocks++;
        Score+=500;
	if(CaravanMode)
	{
	  boolean AllIntact=true;
	  for(i=0;i<NumFriends;i++)
	  {
	     if(Friend[i]==null)
	       AllIntact=false;
	  }
	  if(AllIntact)
  	    Score+=2500;
	}
//        CaravanMode=false;	  
        if(NumRocks>1)
          MissileGravity=false;
        CaravanMode=true;
        if(NumRocks>5)
	  Recoil=true; 
        if(NumRocks>8)
          ShipGravity=true;
        restart_game();
        return;
      }

      // draw missiles and check for collisions with rocks
      for(i=0;i<MAX_MISSILES;i++)
      {
        if(M[i]!=null)
        {
          // draw missile in new position
          M[i].CalculateNewPosition(this);
          M[i].Draw(OffG);

          // for each rock
          for(j=0;j<MAX_ROCKS;j++)
          {
            // if missile hit rock
            if(R[j]!=null && M[i]!=null && R[j].Hit(M[i].GetX(),M[i].GetY(),M[i].GetShape().getBounds()))
            {
              // create more rocks and an explosion
              SpawnRocks(R[j].GetX(),R[j].GetY(),R[j].GetScale()/2);
              CreateExplosion(R[j].GetX(),R[j].GetY());

              // add to score
	      if(M[i].BelongsTo!=AL)
                Score+=(int)(10/R[j].GetScale());

              // kill the big rock and the hitting missile
              R[j]=null;
              M[i]=null;

              // play sound
              if(Bang!=null && UseSounds==true)
                Bang.play();
              break;
            }
          }
        }

        // check for our  missiles hitting the alien
        if(AL!=null && AL.GetType()!=5 && M[i]!=null && M[i].BelongsTo!=AL &&
            AL.Hit(M[i].GetX(),M[i].GetY(),M[i].GetShape().getBounds()))
        {
          CreateExplosion(AL.GetX(),AL.GetY());
          AL=null;
          M[i]=null;
        }

        // check for alien's missiles hitting us
        if(!Shield && !ShipDead && M[i]!=null && M[i].BelongsTo==AL &&
            Ship.Hit(M[i].GetX(),M[i].GetY(),M[i].GetShape().getBounds()))
        {
           DeathIsImminent=true;
           M[i]=null;
        }

        // if a missile has timeout out, kill it
        if(M[i]!=null && M[i].KillMe)
          M[i]=null;

      }



      // show explosions
      for(i=0;i<MAX_EXPLOSIONS;i++)
      {
        if(E[i]!=null)
        {
          E[i].CalculateNewPosition(this);
          E[i].Draw(OffG);
          if(E[i].KillMe==true)
            E[i]=null;
        }
      }

      // display alien if alive
      if(AL!=null)
      {
        AL.CalculateNewPosition(this);
        AL.Draw(OffG,true);
        if(AL.GetType()!=5 && Math.random()>0.98)
          CreateMissile(AL.GetX(),AL.GetY(),Math.random()*2*Math.PI,AL,Color.red);
        if(AL.KillMe)
          AL=null;
      }

      // draw ship
      if(!ShipDead)
      {
        Ship.Draw(OffG,true);
  	if(Shield)
	{
	  // flash if nearing the end
          long T=new Date().getTime()-Start;
	  if(T>4000)
          {
	    if(Math.random()>0.5)
   	      OffG.setColor(Color.gray);
	    else
 	      OffG.setColor(Color.red);
	  }
	  else
	    OffG.setColor(Color.red);
	  OffG.drawOval(Ship.GetX()-50,Ship.GetY()-50,100,100);
  	}	
	if(CaravanMode)
	{
	  int LastX=Ship.GetX();
	  int LastY=Ship.GetY();

	  for(i=0;i<NumFriends;i++)
	  {
	    if(Friend[i]!=null)
	    {
              OffG.setColor(Color.gray);
	      OffG.drawLine(LastX,LastY,Friend[i].GetX(),Friend[i].GetY());
	      LastX=Friend[i].GetX();
	      LastY=Friend[i].GetY();
   	      Friend[i].Draw(OffG,false);
  	     if(Shield)
	     {
	       OffG.setColor(Color.red);
	       OffG.drawOval(Friend[i].GetX()-25,Friend[i].GetY()-25,50,50);
  	     }
	    }
	  }
        }
      }
      else
      {
        // or draw new game banner
        if(NumLives==0)
        {

          // draw title
          if(MT.statusID(0,true)==MT.COMPLETE)
          {
            OffG.drawImage(TitleImage,0,0,this);
            Ship.SetNewPosition(355,250);
            Ship.AddAngle(0.1);
            Ship.SetScale(3);
            Ship.Draw(OffG,true);
            int OldCR=-1,CR;
            String Line;
          }
        }
 /*           if(Hiscore.indexOf("That's bollocks!")==-1 && Hiscore.length()>0)
            {
              OffG.drawString("HISCORES",20,200);
              for(j=0;j<10;j++)
              {
                CR=Hiscore.indexOf(10,OldCR+1);
                if(CR==-1)
                  break;
                Line=Hiscore.substring(OldCR+1,CR);
                OffG.drawString((j+1)+". "+Line,20,220+16*j);
                OldCR=CR;
              }
            }
            else
            {
              if(Hiscore.length()>0)
                OffG.drawString("Your score has not been recorded",20,230);
            }*/
	/*  if(EmailAddress.length()>0)
	    OffG.drawString("Welcome!!!! "+EmailAddress,250,20);
        }
      }*/
      }

      // display number of lives
      OffG.setColor(Color.white);
      if(NumLives>0)
        str="Lives "+NumLives+"    Score "+Score+"   Level "+NumRocks;
      else
        str="Score "+Score;
      if(Recoil)
        str+="   RECOIL";
      if(ShipGravity)
        str+="   SHIP GRAVITY";	
      OffG.drawString(str,20,20);


      // transfer off-screen bitmap to screen
      g.drawImage(OffScreenBitmap,0,0,this);
    }
  }

  public void update(Graphics g)
  {
     paint(g);
  }
}
