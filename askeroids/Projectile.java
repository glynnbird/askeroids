package askeroids;

/**
 * Title:        Projectile
 * Description:  A class to represent any moving shape. Used to represent the
 *               sprites on the screen.
 * Copyright:    Copyright (c) 2000
 * Company:      Miami International Ltd
 * @author       Glynn Bird
 * @version 1.0
 */
import java.applet.*;
import java.awt.*;

public class Projectile {
  protected int           X;              // x position
  protected int           Y;              // y position
  protected Velocity      V;              // speed and direction of object
  protected double        Angle;          // angle of rotation
  protected GbShape       Picture;        // polygon to describe shape
  public    boolean       Hot;            // true=needs redrawing
  protected Color         C;              // fill colour
  protected double        Scale;          // scale factor of shape
  public    double        MaxSpeed;

  /** Class constructor; creates a new projectile which has position, velocity
      size and colour*/
  public Projectile()
  {
    X=100;
    Y=100;
    Hot=false;
    V=new Velocity();
    C=new Color(255,0,0);
    Scale=1.0;
    MaxSpeed=10.0;
  }

  /** Moves the projectile to new position
  * @param x The projectile's new X coordinate
  * @param y The projectile's new y coordinate */
  public void SetNewPosition(int x,int y)
  {
    X=x;
    Y=y;
    Hot=true;
  }

  /** set shape of this projectile by defining a polygon
  * @param xpoints The array of x coordinates
  * @param ypoints The array of y coordinates
  * @param NumPoints The number of points in the xpoints and ypoints arrays */
  public void SetShape(int xpoints[],int ypoints[],int NumPoints)
  {
    Picture=new GbShape(xpoints,ypoints,NumPoints,Scale);
  }

  /** Get the projectile's x coordinate
  * @return  Returns the X coordinate of the projectile */
  public int GetX()  {    return X;  }

  /** Get the projectile's y coordinate
  * @return  Returns the Y coordinate of the projectile */
  public int GetY()  {    return Y;  }
  
  /** Get the projectile's Angle
  * @return  Returns the Angle of the projectile */
  public double GetAngle()  {    return Angle;  }
  
  /** Get the projectile's Shape
  * @return  Returns the Shape of the projectile */
  public GbShape GetShape() { return Picture; }
  
  /** Sets the projectile's scale
  * @param  The scale of the projectile */
  public void SetScale(double S) { Scale=S; }
  
  
  /** Gets the projectile's scale
  * @return  Returns the Scale of the projectile */
  public double GetScale() { return Scale; }

  /** Adds A to the current angle of rotation of the object
  * @param A The angle to add (radians) */
  public void AddAngle(double A)
  {
    Angle+=A;
    Hot=true;
  }

  /** Set the colour of the projectile
  * @param Col The colour */
  public void SetColor(Color Col)
  {
    C=Col;
  }

  /** Adds speed in the current direction of pointing
  * @param S Speed to add */
  public void AddSpeed(double S)
  {
    double Multiplier=1.0;
    V.AddVelocity(S,Angle);
    if(V.Speed<1.0)
      Multiplier=-1;
    if(Math.abs(V.Speed)>MaxSpeed)
      V.Speed=MaxSpeed*Multiplier;
  }

  /** Adds velocity (speed & direction) to the projectile
  * @param S Speed to add 
  * @param A Angle to add*/
  public void AddVelocity(double S,double A)
  {
    double Multiplier=1.0;
    V.AddVelocity(S,A);
    if(V.Speed<0.0)
      Multiplier=-1;
    if(Math.abs(V.Speed)>MaxSpeed)
      V.Speed=MaxSpeed*Multiplier;
  }

/*  // set objects velocity
  public void SetVelocity(double Speed, double Dir)

  {
    double Multiplier=1.0;
    V.AddVelocity(Speed,Dir);
    if(V.Speed<1.0)
      Multiplier=-1;
    if(Math.abs(V.Speed)>MaxSpeed)
      V.Speed=MaxSpeed*Multiplier;
  }*/

  /** calculate object's new position based on it's velocity. The
      position of the object wraps around the screen.
  * @param A The applet that host's the projectile */
  public void CalculateNewPosition(Applet A)
  {
      int OldX=X;
      int OldY=Y;

      // move object to new position
      X+=(int)(V.GetSpeed()*Math.cos(V.GetDirection()));
      Y+=(int)(V.GetSpeed()*Math.sin(V.GetDirection()));

      // wrap around screen
      if(X<0)
        X+=A.getSize().width;
      if(Y<0)
        Y+=A.getSize().height;
      X%=A.getSize().width;
      Y%=A.getSize().height;

      // check to see if this projectile needs redrawing
      if(X!=OldX || Y!=OldY)
        Hot=true;
  }

  /** Slow down the projectile by speed S.
  * @param S The speed to slow down by */
  public void SlowDown(double S)
  {
    V.SlowDown(S);
  }

  /** Draw the projectile on graphics device g
  * @param g The graphics device to draw on
  * @param border True=draw border, False=don't */
  public void Draw(Graphics g, boolean border)
  {
    // calculate new polygon for shape
    Polygon P=Picture.RotateTranslateScale(Angle,X,Y,Scale);
    g.setColor(C);
    g.fillPolygon(P);

    // draw border around shape
    if(border)
    {
      g.setColor(Color.white);
      g.drawPolygon(P);
    }
  }

  /** Calculate the distance between this projectile and P using Pythagoras.
  * @param P The other projectile 
  * @return The distance to P*/
  public double DistanceFrom(Projectile P)
  {
    // a bit of pythagoras' theorum
    double dX=X-P.GetX();
    double dY=Y-P.GetY();
    return Math.sqrt(dX*dX+dY*dY);
  }

  /** See if this projectile overlaps rectangle R at (xpos,ypos)
  * @param xpos The X position of the rectangle
  * @param ypos the Y position of the rectangle
  * @param R The rectangle 
  * @return Whether the projectile overlaps or not*/
  public boolean Hit(int xpos, int ypos,Rectangle R)
  {
    Rectangle ThisProjectile=Picture.getBounds();
    ThisProjectile.x=X-ThisProjectile.width/2;
    ThisProjectile.y=Y-ThisProjectile.height/2;
    R.x=xpos-R.width/2;
    R.y=ypos-R.height/2;
   return ThisProjectile.intersects(R);
 }

  /** Move this projectile to a random position on the screen
  * @param X The maximum X coordinate
  * @param Y The maximum Y coordinate*/
  public void HyperSpace(int xmax,int ymax)
  {
    X=(int)(Math.random()*xmax);
    Y=(int)(Math.random()*ymax);
    V.Speed=0.0;
    Angle=0;
    V.Direction=0.0;
  }
}
