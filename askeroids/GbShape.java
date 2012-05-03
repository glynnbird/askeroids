package askeroids;

/**
 * Title:        GbShape
 * Description:  Polygon class which allows the shape to be rotated, offset and scaled
 *               for drawing. Originally used the the GeneralShape class but this
 *               is only in Java 1.2 and above which means that it will only work
 *               with Netscape 6 or with Sun's Java Plug-in. Using GbShape it's
 *               distributable for nothing. And it's faster than the GeneralShape
 *               class.
 * Copyright:    Copyright (c) 2000
 * Company:      Miami International Ltd
 * @author       Glynn Bird
 * @version 1.0
 */
import java.awt.*;

public class GbShape {
  private Polygon P;  // the raw shape
  private Polygon P2; // the current shape with offset, rotate and scale applied

   /** Creates a new shape based on arrays of x and y points. Once created, the shape
       can be rotated, translated and scaled.
  * @param xpoints The array of x points
  * @param ypoints The array of y points
  * @param npoints The number of points in the arrays xpoints and ypoints.
  * @param Scale The size of the shape*/
  public GbShape(int xpoints[],int ypoints[], int npoints,double Scale)
  {
    // create polygon
    P=new Polygon(xpoints,ypoints,npoints);

    // do initial scale, rotate and translate
    RotateTranslateScale(0.0,0,0,Scale);
  }

  /** Apply a rotation, translation and scaling to the original polygon P and put
      the answer in P2
  * @param Angle The angle to rotate by
  * @param Xoffset The X offset to apply
  * @param YOffset The Y offset to apply
  * @param Scale The scaling to apply*/
  public Polygon RotateTranslateScale(double Angle,int XOffset,int YOffset,double Scale)
  {
    int X[]=new int[P.npoints];
    int Y[]=new int[P.npoints];
    double L,Theta;
    int i;

    // make a copy of the polygon
    for(i=0;i<P.npoints;i++)
    {
      // copy the original points
      X[i]=P.xpoints[i];
      Y[i]=P.ypoints[i];

      // calculate the length from 0,0 of the point and multiply it Scale
      L=Math.sqrt((double)(X[i]*X[i]+Y[i]*Y[i]))*Scale;

      // calculate the angle by converting from cartesian coordinates
      // to polar coordinates
      Theta=Math.atan2((double)Y[i],(double)X[i]);

      // add rotation angle
      Theta+=Angle;

      // convert back from polar coordinates back to
      // cartesian X,Y coordinates
      Y[i]=(int)(L*Math.sin(Theta))+YOffset;
      X[i]=(int)(L*Math.cos(Theta))+XOffset;
    }

    // create new polygon
    P2=new Polygon(X,Y,P.npoints);
    return P2;
  }

  /** Calculate the shape's width.
  * @return The shape's width.*/
  public int GetWidth()
  {
    // return width of the bounding rectangle
    return P2.getBounds().width;
  }

  /** Calculate the shape's height.
  * @return The shape's height.*/
  public int GetHeight()
  {
    // return the height of the bounding rectangle
    return P2.getBounds().height;
  }

  /** Calculate the shape's bounding rectangle.
  * @return The shape's bounding rectangle.*/
  public Rectangle getBounds()
  {
    // return the bounding rectangle
    return P2.getBounds();
  }
}
