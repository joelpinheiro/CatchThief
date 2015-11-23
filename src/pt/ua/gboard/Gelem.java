package pt.ua.gboard;

import java.awt.*;

/**
 *  Abstract graphical element occupying a rectangular group of cells in a GBoard.
 *
 *  <P><B>invariant</B>: {@code numberOfLines() >= 1 && numberOfColumns() >= 1}
 *
 * <P>This class follows DbC(tm) methodology.
 * Where possible, contracts are implement with JML and native's Java assert.
 *
 * @author Miguel Oliveira e Silva (mos@ua.pt)
 */
public abstract class Gelem
{
   //@ public invariant numberOfLines() >= 1 && numberOfColumns() >= 1;

  /**
   * Constructs a new Gelem (1x1 cells).
   */
   public Gelem()
   {
      this(1, 1);
   }

  /**
   * Constructs a new Gelem (numberOfLines x numberOfColumns cells).
   *
   *  <P><B>requires</B>: {@code numberOfLines >= 1 && numberOfColumns >= 1}
   * 
   * @param numberOfLines  number of lines
   * @param numberOfColumns  number of columns
   */
   //@ requires numberOfLines >= 1 && numberOfColumns >= 1;
   public Gelem(int numberOfLines, int numberOfColumns)
   {
      assert numberOfLines >= 1 && numberOfColumns >= 1;

      this.numberOfLines = numberOfLines;
      this.numberOfColumns = numberOfColumns;
   }

   /**
    * Line dimensions of Gelem measured in number of cells.
    *
    * @return {@code int} number of lines
    */
   public int numberOfLines()
   {
      return numberOfLines;
   }

   /**
    * Column dimensions of Gelem measured in number of cells.
    *
    * @return {@code int} number of columns
    */
   public int numberOfColumns()
   {
      return numberOfColumns;
   }

   /**
    * Draw Gelem in GBoard (it should be exported only to GBoard).
    *
    * <P><B>requires</B>: {@code g != null && background != null} (incomplete!)
    *
    * @param g  Java's Graphics object to use in drawing
    * @param line  line in GBoard to draw Gelem.
    * @param column  column in GBoard to draw Gelem.
    * @param cellWidth  number of horizontal pixels per cell
    * @param cellHeight  number of vertical pixels per cell
    * @param background  background color
    */
   //@ requires g != null && background != null;
   public abstract void draw(Graphics g, int line, int column, int cellWidth, int cellHeight, Color background);

   /**
    * Erase Gelem from GBoard (it should be exported only to GBoard).
    *
    * <P><B>requires</B>: {@code g != null && background != null} (incomplete!)
    *
    * @param g  Java's Graphics object to use in drawing
    * @param line  line in GBoard to draw Gelem.
    * @param column  column in GBoard to draw Gelem.
    * @param cellWidth  number of horizontal pixels per cell
    * @param cellHeight  number of vertical pixels per cell
    */
   //@ requires g != null && background != null;
   public abstract void erase(Graphics g, int line, int column, int cellWidth, int cellHeight, Color background);

   /**
    * Does current gelem at position (line, column) intersects gelem other at position
    * (otherLine, otherColumn)?
    *
    * <P><B>requires</B>: {@code other != null}
    * 
    * @param line  line of current Gelem.
    * @param column  column of current Gelem.
    * @param other  the other gelem
    * @param otherLine  line of other Gelem.
    * @param otherColumn  column of other Gelem.
    *
    * @return {@code boolean} true if they intersect
    */
   //@ requires other != null;
   public boolean intersects(int line, int column, Gelem other, int otherLine, int otherColumn)
   {
      assert other != null;

      return (line <= otherLine && line+numberOfLines-1 >= otherLine ||
              otherLine < line && otherLine+other.numberOfLines-1 >= line) &&
             (column <= otherColumn && column+numberOfColumns-1 >= otherColumn ||
              otherColumn < column && otherColumn+other.numberOfColumns-1 >= column);
   }

   /**
    * Pixel x position of gelem.
    *
    * @param column  column of gelem.
    * @param cellWidth  number of horizontal pixels per cell
    *
    * @return {@code int} position
    */
   public int x(int column, int cellWidth)
   {
      return column*cellWidth;
   }

   /**
    * Pixel y position of gelem.
    *
    * @param line  line of gelem.
    * @param cellHeight  number of vertical pixels per cell
    *
    * @return {@code int} position
    */
   public int y(int line, int cellHeight)
   {
      return line*cellHeight;
   }

   /**
    * Width (in pixels) of current gelem.
    *
    * @param cellWidth  number of horizontal pixels per cell
    *
    * @return {@code int} number of pixels
    */
   public int width(int cellWidth)
   {
      return numberOfColumns*cellWidth;
   }

   /**
    * Height (in pixels) of current gelem.
    *
    * @param cellHeight  number of vertical pixels per cell
    *
    * @return {@code int} number of pixels
    */
   public int height(int cellHeight)
   {
      return numberOfLines*cellHeight;
   }

   /**
    * Is gelem mutable?
    * A mutable gelem automatically updates GBoard when updated.
    *
    * @return {@code boolean} true if mutable, otherwise retuns false
    */
   public boolean isMutable()
   {
      return mutable() != null;
   }

   /**
    * If mutable, returns a mutable version of current gelem, otherwise it returns null.
    *
    * @return {@code boolean} a reference to an object if mutable, otherwise retuns null
    */
   public MutableGelem mutable()
   {
      MutableGelem result = null;
      try
      {
         result = (MutableGelem)this;
      }
      catch(ClassCastException e)
      {
      }
      return result;
   }

   protected final int numberOfLines;
   protected final int numberOfColumns;
}
