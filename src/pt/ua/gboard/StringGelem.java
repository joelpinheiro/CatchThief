package pt.ua.gboard;

import java.awt.*;

/**
 *  This gelem writes a string in a rectangle of cells.
 */
public class StringGelem extends OneColorGelem
{
  /**
   * Constructs a new StringGelem (1x1 cells using Monospaced font).
   *
   *  <P><B>requires</B>: {@code text != null}
   * <BR><B>requires</B>: {@code color != null}
   * 
   * @param text  string to be drawed
   * @param color  gelem's color
   */
   //@ requires text != null;
   //@ requires color != null;
   public StringGelem(String text, Color color)
   {
      this(text, color, 1, 1, "Monospaced"); // default font
   }

  /**
   * Constructs a new StringGelem (1x1 cells).
   *
   *  <P><B>requires</B>: {@code text != null}
   * <BR><B>requires</B>: {@code color != null}
   * <BR><B>requires</B>: {@code fontName != null}
   * 
   * @param text  string to be drawed
   * @param color  gelem's color
   * @param fontName  font name
   */
   //@ requires text != null;
   //@ requires color != null;
   //@ requires fontName != null;
   public StringGelem(String text, Color color, String fontName)
   {
      this(text, color, 1, 1, fontName); // default font
   }

  /**
   * Constructs a new StringGelem (using Monospaced font).
   *
   *  <P><B>requires</B>: {@code text != null}
   * <BR><B>requires</B>: {@code color != null}
   * <BR><B>requires</B>: {@code numberOfLines >= 1 && numberOfColumns >= 1}
   * 
   * @param text  string to be drawed
   * @param color  gelem's color
   * @param numberOfLines  number of lines
   * @param numberOfColumns  number of columns
   */
   //@ requires text != null;
   //@ requires color != null;
   //@ requires numberOfLines >= 1 && numberOfColumns >= 1;
   public StringGelem(String text, Color color, int numberOfLines, int numberOfColumns)
   {
      this(text, color, numberOfLines, numberOfColumns, "Monospaced"); // default font
   }

  /**
   * Constructs a new StringGelem.
   *
   *  <P><B>requires</B>: {@code text != null}
   * <BR><B>requires</B>: {@code color != null}
   * <BR><B>requires</B>: {@code numberOfLines >= 1 && numberOfColumns >= 1}
   * <BR><B>requires</B>: {@code fontName != null}
   * 
   * @param text  string to be drawed
   * @param color  gelem's color
   * @param numberOfLines  number of lines
   * @param numberOfColumns  number of columns
   * @param fontName  font name
   */
   //@ requires text != null;
   //@ requires color != null;
   //@ requires numberOfLines >= 1 && numberOfColumns >= 1;
   //@ requires fontName != null;
   public StringGelem(String text, Color color, int numberOfLines, int numberOfColumns, String fontName)
   {
      super(color, numberOfLines, numberOfColumns);

      assert fontName != null;

      this.text = text;
      this.fontName = fontName;
      fonts = new Font[0]; // empty!
   }

   /**
    * String to be drawed.
    */
   public final String text;

   protected void internal_draw(Graphics g, Color color, int line, int column, int cellWidth, int cellHeight, Color background)
   {
      assert g != null;

      int w = width(cellWidth);
      int h = height(cellHeight);

      int size = (w/text.length() < h ? w/text.length() : h)*8/10;
      if (size >= fonts.length)
      {
         Font[] newFonts = new Font[size+1];
         System.arraycopy(fonts, 0, newFonts, 0, fonts.length);
         fonts = newFonts;
      }
      if (fonts[size] == null)
         fonts[size] = new Font(fontName, Font.PLAIN, size);
      g.setFont(fonts[size]);
      FontMetrics metrics = g.getFontMetrics();

      int x =column*cellWidth+(w - metrics.stringWidth(text))/2;
      int y =line*cellHeight+(h - metrics.getHeight())/2+metrics.getAscent();
      g.setColor(color);
      g.drawString(text, x, y);
   }

   protected final String fontName;
   protected Font[] fonts;
}
