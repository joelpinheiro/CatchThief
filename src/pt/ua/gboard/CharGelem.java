package pt.ua.gboard;

import java.awt.*;

/**
 *  This gelem writes a character in a rectangle of cells.
 */
public class CharGelem extends StringGelem
{
  /**
   * Constructs a new CharGelem (1x1 cells using Monospaced font).
   *
   * <P><B>requires</B>: {@code color != null}
   * 
   * @param c  character to be drawed
   * @param color  gelem's color
   */
   //@ requires color != null;
   public CharGelem(char c, Color color)
   {
      super(""+c, color);
   }

  /**
   * Constructs a new CharGelem (1x1 cells).
   *
   * <P><B>requires</B>: {@code color != null && fontName != null}
   * 
   * @param c  character to be drawed
   * @param color  gelem's color
   * @param fontName  font name
   */
   //@ requires color != null;
   //@ requires fontName != null;
   public CharGelem(char c, Color color, String fontName)
   {
      super(""+c, color, fontName);
   }

  /**
   * Constructs a new CharGelem (using Monospaced font).
   *
   * <P><B>requires</B>: {@code color != null && numberOfLines >= 1 && numberOfColumns >= 1}
   * 
   * @param c  character to be drawed
   * @param color  gelem's color
   * @param numberOfLines  number of lines
   * @param numberOfColumns  number of columns
   */
   //@ requires color != null;
   //@ requires numberOfLines >= 1 && numberOfColumns >= 1;
   public CharGelem(char c, Color color, int numberOfLines, int numberOfColumns)
   {
      super(""+c, color, numberOfLines, numberOfColumns);
   }

  /**
   * Constructs a new CharGelem.
   *
   * <P><B>requires</B>: {@code color != null && numberOfLines >= 1 && numberOfColumns >= 1 && fontName != null}
   * 
   * @param c  character to be drawed
   * @param color  gelem's color
   * @param numberOfLines  number of lines
   * @param numberOfColumns  number of columns
   * @param fontName  font name
   */
   //@ requires color != null;
   //@ requires numberOfLines >= 1 && numberOfColumns >= 1;
   //@ requires fontName != null;
   public CharGelem(char c, Color color, int numberOfLines, int numberOfColumns, String fontName)
   {
      super(""+c, color, numberOfLines, numberOfColumns, fontName);
   }
}
