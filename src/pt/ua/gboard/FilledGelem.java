package pt.ua.gboard;

import java.awt.*;

/**
 *  This gelem fills a rectangle of cells.
 */
public class FilledGelem extends OneColorGelem
{
  /**
   * Constructs a new FilledGelem (1x1 cells).
   *
   * <P><B>requires</B>: {@code color != null && cellOccupation >= 0.0 && cellOccupation <= 100.0}
   * 
   * @param color  gelem's color
   * @param cellOccupation  total cell's occupation (%)
   */
   //@ requires color != null;
   //@ requires cellOccupation >= 0.0 && cellOccupation <= 100.0; 
   public FilledGelem(Color color, double cellOccupation)
   {
      this(color, cellOccupation, 1, 1);
   }

  /**
   * Constructs a new FilledGelem.
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
   public FilledGelem(Color color, double cellOccupation, int numberOfLines, int numberOfColumns)
   {
      super(color, numberOfLines, numberOfColumns);

      assert cellOccupation >= 0.0 && cellOccupation <= 100.0; 

      this.cellOccupation = cellOccupation/100.0;
   }

   protected void internal_draw(Graphics g, Color color, int line, int column, int cellWidth, int cellHeight, Color background)
   {
      assert g != null;

      int w = cellWidth*numberOfColumns;
      int h = cellHeight*numberOfLines;

      int freeWidth = (int)((double)w*(1.0-cellOccupation));
      int freeHeight = (int)((double)h*(1.0-cellOccupation));

      g.setColor(color);
      g.fillRect(column*cellWidth+freeWidth/2, line*cellHeight+freeHeight/2, w-freeWidth, h-freeHeight);
   }

   protected final double cellOccupation;
}
