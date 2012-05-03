package askeroids;

/**
 * Title:        alien
 * Description:  Represents the alien which occasionally appears
 * Copyright:    Copyright (c) 2000
 * Company:      Miami International Ltd
 * @author       Glynn Bird
 * @version 1.0
 */

import java.applet.*;
import java.awt.*;
import java.util.*;

public class alien extends Projectile
{
         long         CreationTime;    // time of creation
         int          PicType;         // which pic to use
  public boolean      KillMe;          // flag to instruct Applet to kill class
  final  int          MAX_PICS=6;      // maximum number of pictures to use
  static Image        AlPic[];         // array of pictures
  static MediaTracker MT;              // image loader
  static Applet       App;             // the host applet
         double       RandomRotator;

  /** class constructor. Creates a new alien at X,Y travelling at Speed in
      a certain Direction on Applet A. An alien can be one of 6 types (0-5), each
      having a different picture or shape. Type 5 aliens are black holes and
      they don't move. An alien will set its KillMe flag after 10 seconds.
      The applet can then kill the alien off.
  * @param Xpos X position of new alien
  * @param Ypos Y position of new alien
  * @param Speed The speed of the new alien
  * @param Direction The direction (radians) of new alien
  * @param A The applet on which the alien will live
  */
  public alien(int Xpos,int Ypos, double Speed, double Direction, Applet A)
  {
    // triangle 
    int xPoints1[]={ -10,0,10};
    int yPoints1[]={ -10,10,-10};
    
    // square
    int xPoints2[]={ -10,-10,10,10};
    int yPoints2[]={ -10,10,10,-10};
    
    // cross
    int xPoints3[]={ 5,5,10,10,5,5,   -5,-5,-10,-10,-5,-5 };
    int yPoints3[]={ 10,5,5,-5,-5,-10,-10,-5,-5,5,   5 , 10};

    // square
    int xPoints4[]={ -10,-10,10,10};
    int yPoints4[]={ -10,10,10,-10};

    // square
    int xPoints5[]={ -10,-10,10,10};
    int yPoints5[]={ -10,10,10,-10};

    // star/black hole
    int xPoints6[]={ 0, 1, 5, 2 , 10, 2, 5, 1, 0 ,-1, -5, -2, -10, -2, -5, -1 };
    int yPoints6[]={ 10,2, 5, 1 , 0 , -1, -5, -2, -10,-2, -5, -1, 0,1, 5, 2 };

    // create array
    AlPic=new Image[MAX_PICS];

    // choose rock type
    PicType=(int)(Math.random()*MAX_PICS);
    if(PicType>=MAX_PICS)
      PicType=MAX_PICS-1;

    // set parameters
    X=Xpos;
    Y=Ypos;
    if(PicType!=5)
      V.AddVelocity(Speed,Direction);
    // generate rotation angle
    RandomRotator=(Math.random()-0.5)/10;    

    // get time of creation
    CreationTime=new Date().getTime();
    KillMe=false;

    // create rock shape
    SetScale(5.0);
    
    switch(PicType)
    {
      case 0: SetShape(xPoints1,yPoints1,xPoints1.length);break;
      case 1: SetShape(xPoints2,yPoints2,xPoints2.length);break;
      case 2: SetShape(xPoints3,yPoints3,xPoints3.length);break;
      case 3: SetShape(xPoints4,yPoints4,xPoints4.length);break;
      case 4: SetShape(xPoints5,yPoints5,xPoints5.length);break;
      case 5: SetShape(xPoints6,yPoints6,xPoints6.length);break;
      default:
        SetShape(xPoints1,yPoints1,xPoints1.length);break;
    };

    // set colour
    SetColor(new Color((int)(Math.random()*127)+128,(int)(Math.random()*127)+128,(int)(Math.random()*127)+128));

    // load images
    MT=new MediaTracker(A);
    AlPic[0]=A.getImage(A.getCodeBase(),"askeroids/sat1.gif");
    AlPic[1]=A.getImage(A.getCodeBase(),"askeroids/sat2.gif");
    AlPic[2]=A.getImage(A.getCodeBase(),"askeroids/sat3.gif");
    AlPic[3]=A.getImage(A.getCodeBase(),"askeroids/sat4.gif");
    AlPic[4]=A.getImage(A.getCodeBase(),"askeroids/dell.gif");
    AlPic[5]=A.getImage(A.getCodeBase(),"askeroids/blackhole.gif");
    MT.addImage(AlPic[0],0);
    MT.addImage(AlPic[1],0);
    MT.addImage(AlPic[2],0);
    MT.addImage(AlPic[3],0);
    MT.addImage(AlPic[4],0);
    MT.addImage(AlPic[5],0);

    
    // keep a reference to the applet
    App=A;
  }

  /** Get the type (0-5) of the alien
  * @return the type of the alien */
  public int GetType()
  {
    return PicType;
  }

  /** Draw the alien. If the ShowImages is true, then the gif files are used
      otherwise the vectors are used.
  * @param g The graphics display on which the alien will be drawn
  * @param ShowImages True=show images, False=show vectors*/
  public void Draw(Graphics g,boolean ShowImages)
  {
    Date Now=new Date();

    if(((Now.getTime()-CreationTime)/1000)<10)
    {

     if(ShowImages && MT.statusID(0,true)==MT.COMPLETE)
     {
       int W=Picture.getBounds().width;
       int H=Picture.getBounds().height;
       g.drawImage(AlPic[PicType],X-W/2,Y-H/2,W,H,App);
     }
     else
     {
        // don't draw blobby if in Images mode
        if(!ShowImages)
        {
          AddAngle(RandomRotator);
          Picture.RotateTranslateScale(Angle,X,Y,Scale);
          super.Draw(g,true);
        }
        else
           KillMe=true;
     }
    }
    else
      KillMe=true;
  }
}
