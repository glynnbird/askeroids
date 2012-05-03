package askeroids;

/**
 * Title:        Explosion
 * Description:  Class represents the shrapnel of an explosion by overriding
 *               the Projectile class
 * Copyright:    Copyright (c) 2000
 * Company:      Miami International Ltd
 * @author       Glynn Bird
 * @version 1.0
 */

import java.applet.*;
import java.awt.*;
import java.util.*;

public class Explosion extends Projectile
{
  double         RandomRotator;
  long           CreationTime;
  public boolean KillMe;

  /** creates an explosion piece at X,Y travelling in Direction. The class
      will set its KillMe flag after 1.5 seconds, so that the applet can
      stop the explosion.
      
  * @param Xpos The X position of the explostion
  * @param Ypos The Y position of the explosion
  * @param Direction The direction (radians) of the explosion*/
  public Explosion(int Xpos,int Ypos, double Direction)
  {
    int xPoints[]={ 0,2,-2};
    int yPoints[]={ 2,-2,-2};

    // set parameters
    X=Xpos;
    Y=Ypos;
    V.AddVelocity(10.0,Direction);

    // get time of creation
    CreationTime=new Date().getTime();
    KillMe=false;

    // generate rotation angle
    RandomRotator=(Math.random()-0.5)/10;

    // create rock shape
    SetShape(xPoints,yPoints,xPoints.length);

    // set colour
    SetColor(Color.white);
  }
  
 /** Paints the explosion on graphics device g.
  * @param g The graphics device to draw on*/
  public void Draw(Graphics g)
  {
    long Elapsedms=new Date().getTime()-CreationTime;

    if((Elapsedms/1000.0)<2.5)
    {
       // diminish brightness with time
       int Brightness=255-(255*(int)Elapsedms)/2500;
       C=new Color(Brightness,Brightness,Brightness);
       AddAngle(RandomRotator);
       super.Draw(g,false);
    }
    else
      KillMe=true;
  }
}
