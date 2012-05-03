package askeroids;

/**
 * Title:        Rock
 * Description:  Represents the rocks. Extends Projectile
 * Copyright:    Copyright (c) 2000
 * Company:      Miami International Ltd
 * @author       Glynn Bird
 * @version 1.0
 */

import java.applet.*;
import java.awt.*;

public class Rock extends Projectile
{
          double       RandomRotator;  // rate of rotation
  public  int          PicType;        // picture to use
  final   int          MAX_PICS=3;     // number of pictures
  static  Image        RockPic[];      // array of pictures
  static  MediaTracker MT;             // image loader
  static  Applet       App;            // host applet

  /** class constructor; creates a rock at Xpos,Ypos travelling at Speed 
  * @param Xpos The X positon of the missile
  * @param Ypos The Y position of the missile
  * @param Velocity The speed of the missile
  * @param Scale The size of the missile
  * @param A The applet host of the Rock*/
  public Rock(int Xpos,int Ypos, int Velocity,double Scale, Applet A)
  {
    // rock with a cut in it
    int xPoints1[]={ 0 ,10,20,30,20 ,10 ,0  ,-10,-13,-15,-20,-30,-20,-10};
    int yPoints1[]={ 30,28,20,0 ,-15,-20,-30,-20,16 ,14 ,-17,0  ,17 ,24};

    // rock with no cut in it
    int xPoints2[]={ 0 ,10,20,30,20 ,10 ,0  ,-10,-13,-15,-20,-30,-20,-10};
    int yPoints2[]={ 30,28,20,0 ,-15,-20,-30,-20,-16 ,-14 ,-17,0  ,17 ,24};

    // rock with some other feature
    int xPoints3[]={ 0 ,10,20,30,20 ,10 ,0  ,-10,-13,-15,-20,-30,-11,-10};
    int yPoints3[]={ 30,28,20,0 ,-15,-14,-30,-20,-16 ,-14 ,-17,0  ,12 ,24};

    // create array
    RockPic=new Image[MAX_PICS];

    MaxSpeed=7.5;

    // set parameters
    X=Xpos;
    Y=Ypos;
    V.AddVelocity(Velocity,Math.random()*Math.PI*2);

    // generate rotation angle
    RandomRotator=(Math.random()-0.5)/10;

    // record ScaleFactor
    SetScale(Scale);

    // choose rock type
    PicType=(int)(Math.random()*MAX_PICS);
    if(PicType>=MAX_PICS)
      PicType=MAX_PICS-1;

    // create rock shape
    switch(PicType)
    {
      case 0: SetShape(xPoints1,yPoints1,xPoints1.length); break;
      case 1: SetShape(xPoints2,yPoints2,xPoints2.length); break;
      case 2: SetShape(xPoints3,yPoints3,xPoints3.length); break;
      default: SetShape(xPoints1,yPoints1,xPoints1.length); break;
    }

    // set colour
    SetColor(Color.black);

    // load images
    MT=new MediaTracker(A);
    RockPic[0]=A.getImage(A.getCodeBase(),"askeroids/asteroid4.gif");
    RockPic[1]=A.getImage(A.getCodeBase(),"askeroids/asteroid5.gif");
    RockPic[2]=A.getImage(A.getCodeBase(),"askeroids/asteroid.gif");
    MT.addImage(RockPic[0],0);
    MT.addImage(RockPic[1],0);
    MT.addImage(RockPic[2],0);

    // keep a reference to the applet
    App=A;
  }

  /** Draw the rock on graphics device g
  * @param g The grapgics device to draw on
  * @param ShowImages true=show images,false=show vectors*/
  public void Draw(Graphics g, boolean ShowImages)
  {
     if(ShowImages && MT.statusID(0,true)==MT.COMPLETE)
     {
       int W=Picture.getBounds().width;
       int H=Picture.getBounds().height;
       g.drawImage(RockPic[PicType],X-W/2,Y-H/2,W,H,App);
     }
     else
     {
         AddAngle(RandomRotator);
         Picture.RotateTranslateScale(Angle,X,Y,Scale);
         super.Draw(g,true);
     }
     // keep it moving
     if(this.V.Speed<1.0)
       this.AddSpeed(1.5);
  }
}
