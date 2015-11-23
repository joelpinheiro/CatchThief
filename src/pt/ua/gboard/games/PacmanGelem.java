package pt.ua.gboard.games;

import java.awt.*;
import pt.ua.gboard.*;

/**
 *  Pacman gelem.
 *
 * <P>This class follows DbC(tm) methodology.
 * Where possible, contracts are implement with JML and native's Java assert.
 *
 * @author Miguel Oliveira e Silva (mos@ua.pt)
 */
public class PacmanGelem extends OneColorGelem
{
  /**
   * Constructs a new PacmanGelem (1x1 cells).
   *
   * <P><B>requires</B>: {@code color != null && cellOccupation >= 0.0 && cellOccupation <= 100.0}
   * 
   * @param color  gelem's color
   * @param cellOccupation  total cell's occupation (%)
   */
   //@ requires color != null;
   //@ requires cellOccupation >= 0.0 && cellOccupation <= 100.0; 
   public PacmanGelem(Color color, double cellOccupation)
   {
      this(color, cellOccupation, 1, 1);
   }

  /**
   * Constructs a new PacmanGelem.
   *
   * <P><B>requires</B>: {@code color != null && cellOccupation >= 0.0 && cellOccupation <= 100.0 && numberOfLines >= 1 && numberOfColumns >= 1}
   * 
   * @param color  gelem's color
   * @param cellOccupation  total cell's occupation (%)
   * @param numberOfLines  number of lines
   * @param numberOfColumns  number of columns
   */
   //@ requires color != null;
   //@ requires cellOccupation >= 0.0 && cellOccupation <= 100.0; 
   //@ requires numberOfLines >= 1 && numberOfColumns >= 1;
   public PacmanGelem(Color color, double cellOccupation, int numberOfLines, int numberOfColumns)
   {
      super(color, numberOfLines, numberOfColumns);

      assert cellOccupation >= 0.0 && cellOccupation <= 100.0; 

      this.cellOccupation = cellOccupation/100.0;
   }

   protected void internal_draw(Graphics g, Color color, int line, int column, int cellWidth, int cellHeight, Color background)
   {
      assert g != null;

      int angle = 0;
      if (lastLine >= 0 && lastColumn >= 0 && (lastLine != line || lastColumn != column))
      {
         if (column > lastColumn) // EAST
            angle = 0;
         else if (column < lastColumn) // WEST
            angle = 180;
         else if (line > lastLine) // SOUTH
            angle = 270;
         else // NORTH
            angle = 90;
      }

      int a = closedAngle;
      if (angle == 90 || angle == 270)
      {
         if (line % (2*numberOfLines) < numberOfLines) // shrinking
            a += (openedAngle-closedAngle)*(numberOfLines-(line%numberOfLines))/(numberOfLines);
         else // growing
            a += (openedAngle-closedAngle)*(line%numberOfLines)/(numberOfLines);
      }
      else
      {
         if (column % (2*numberOfColumns) < numberOfColumns) // shrinking
            a += (openedAngle-closedAngle)*(numberOfColumns-(column%numberOfColumns))/(numberOfColumns);
         else // growing
            a += (openedAngle-closedAngle)*(column%numberOfColumns)/(numberOfColumns);
      }

      int w = cellWidth*numberOfColumns;
      int h = cellHeight*numberOfLines;

      int freeWidth = (int)((double)w*(1.0-cellOccupation));
      int freeHeight = (int)((double)h*(1.0-cellOccupation));

      g.setColor(color);
      g.fillArc(column*cellWidth+freeWidth/2, line*cellHeight+freeHeight/2, w-freeWidth, h-freeHeight, angle+a, 360-2*a);
      lastLine = line;
      lastColumn = column;
   }

   protected static int openedAngle = 45;
   protected static int closedAngle =  2;

   protected int lastLine = -1;
   protected int lastColumn = -1;
   protected final double cellOccupation;
}
