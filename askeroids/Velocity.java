package askeroids;

/**
 * Title:        Velocity
 * Description:  Class to represent the velocity (Speed & Direction) of an
 *               object
 * Copyright:    Copyright (c) 2000
 * Company:      Miami International Ltd
 * @author       Glynn Bird
 * @version 1.0
 */

public class Velocity
{
  double   Speed;
  double   Direction;

  /** class constructor; clears speed and direction */
  public Velocity()
  {
    Speed=0.0;
    Direction=0.0;
  }

  /** Gets the speed component 
  * @return The speed*/
  public double GetSpeed() { return Speed; }
  
  /** Gets the direction component (radians)
  * @return The direction*/
  public double GetDirection() { return Direction; };


  /** diminish current speed by speed S
  * @param S The speed to slow down by */
  public void SlowDown(double S)
  {
       if(Speed>S)
       {
        Speed-=S;
       }
       if(Speed<=-S)
       {
         Speed+=S;
       }
  }

  /** Add velocity (S,D) to this velocity
  * @param S The speed to add
  * @param D The direction to add */
  public void AddVelocity(double S,double D)
  {
    // convert to points
    double x1=Speed*Math.cos(Direction);
    double y1=Speed*Math.sin(Direction);
    double x2=S*Math.cos(D);
    double y2=S*Math.sin(D);

    // add points
    double X=x1+x2;
    double Y=y1+y2;

    // convert back to vector
    Direction=Math.atan2(Y,X);
    Speed=Math.sqrt(X*X+Y*Y);
    if(Speed>20.0)
     Speed=20.0;
    if(Speed<-20.0)
      Speed=-20.0;
  }
}
