package askeroids;

/**
 * Title:        Missile
 * Description:  Class to represent a missile. Extends the projectile class
 * Copyright:    Copyright (c) 2000
 * Company:      Miami International Ltd
 * @author       Glynn Bird
 * @version 1.0
 */

import java.applet.*;
import java.awt.*;
import java.util.*;

public class Missile extends Projectile
{
            double     RandomRotator;       // rate of rotation
            long       CreationTime;        // time of creation
  public    boolean    KillMe;              // instruction to applet to kill this object
  public    Projectile BelongsTo;           // the owner of this missile

  /** class constructor; creates a missile at Xpos,Ypos travelling at Speed in Direction.
  * @param Xpos The X positon of the missile
  * @param Ypos The Y position of the missile
  * @param Speed The speed of the missile
  * @param Direction The direction of the missile
  * @param Owner The owner projectile of the missile
  * @param C The colour of the missile*/
  public Missile(int Xpos,int Ypos, double Speed, double Direction,Projectile Owner,Color C)
  {
    int xPoints[]={ 2,2,-2,-2};
    int yPoints[]={ 2,-2,-2,2};

    // set parameters
    X=Xpos;
    Y=Ypos;
    V.AddVelocity(Speed,Direction);

    // get time of creation
    CreationTime=new Date().getTime();
    KillMe=false;

    // keep owner of missile to stop people blowing themselves up
    BelongsTo=Owner;

    // generate rotation angle
    RandomRotator=(Math.random()-0.5)/10;

    // create rock shape
    SetShape(xPoints,yPoints,4);

    // set colour
    SetColor(C);
  }

  /** Draw the missile on graphics device g
  * @param g The grapgics device to draw on*/
  public void Draw(Graphics g)
  {
    Date Now=new Date();

    if(((Now.getTime()-CreationTime)/1000)<3)
    {
       AddAngle(RandomRotator);
       super.Draw(g,true);
    }
    else
      KillMe=true;
  }
}
