package pt.ua.gboard;

import java.awt.*;

/**
 *  Abstract graphical element with a unique color.
 */
public abstract class OneColorGelem extends Gelem
{
  /**
   * Constructs a new OneColorGelem (1x1 cells).
   *
   * <P><B>requires</B>: {@code color != null}
   * 
   * @param color  gelem's color
   */
   //@ requires color != null;
   public OneColorGelem(Color color)
   {
      this(color, 1, 1);
   }

  /**
   * Constructs a new OneColorGelem.
   *
   * <P><B>requires</B>: {@code color != null}
   * 
   * @param color  gelem's color
   * @param numberOfLines  number of lines
   * @param numberOfColumns  number of columns
   */
   //@ requires color != null;
   public OneColorGelem(Color color, int numberOfLines, int numberOfColumns)
   {
      super(numberOfLines, numberOfColumns);

      assert color != null; 

      this.color = color;
   }

   public void draw(Graphics g, int line, int column, int cellWidth, int cellHeight, Color background)
   {
      internal_draw(g, color, line, column, cellWidth, cellHeight, background);
   }

   public void erase(Graphics g, int line, int column, int cellWidth, int cellHeight, Color background)
   {
      internal_draw(g, background, line, column, cellWidth, cellHeight, background);
   }

   protected abstract void internal_draw(Graphics g, Color color, int line, int column, int cellWidth, int cellHeight, Color background);

   protected final Color color;
}

