package pt.ua.gboard;

import java.awt.*;
import java.io.File;
import java.net.URL;
//import java.net.URI;
//import java.net.URISyntaxException;

/**
 *  This gelem draws an image in a rectangle of cells.
 */
public class ImageGelem extends Gelem
{
   /**
    * Constructs a new ImageGelem (1x1 cells).
    *
    * <P><B>requires</B>: {@code image != null && board != null && cellOccupation >= 0.0 && cellOccupation <= 100.0}
    * 
    * @param image  image's object
    * @param board  GBoard where the image will be drawed
    * @param cellOccupation  total cell's occupation (%)
    */
   //@ requires image != null;
   //@ requires board != null;
   //@ requires cellOccupation >= 0.0 && cellOccupation <= 100.0; 
   public ImageGelem(Image image, GBoard board, double cellOccupation)
   {
      this(image, board, cellOccupation, 1, 1);
   }

   /**
    * Constructs a new ImageGelem (1x1 cells).
    *
    * <P><B>requires</B>: {@code imagePath != null && board != null && cellOccupation >= 0.0 && cellOccupation <= 100.0}
    * 
    * @param imagePath  image's file path
    * @param board  GBoard where the image will be drawed
    * @param cellOccupation  total cell's occupation (%)
    */
   //@ requires imagePath != null;
   //@ requires board != null;
   //@ requires cellOccupation >= 0.0 && cellOccupation <= 100.0; 
   public ImageGelem(String imagePath, GBoard board, double cellOccupation)
   {
      this(imagePath, board, cellOccupation, 1, 1);
   }

   /**
    * Constructs a new ImageGelem.
    *
    * <P><B>requires</B>: {@code imagePath != null && board != null && cellOccupation >= 0.0 && cellOccupation <= 100.0 && numberOfLines >= 1 && numberOfColumns >= 1}
    * 
    * @param imagePath  image's file path
    * @param board  GBoard where the image will be drawed
    * @param cellOccupation  total cell's occupation (%)
    * @param numberOfLines  number of lines
    * @param numberOfColumns  number of columns
    */
   //@ requires imagePath != null;
   //@ requires board != null;
   //@ requires cellOccupation >= 0.0 && cellOccupation <= 100.0; 
   //@ requires numberOfLines >= 1 && numberOfColumns >= 1;
   public ImageGelem(String imagePath, GBoard board, double cellOccupation, int numberOfLines, int numberOfColumns)
   {
      this(Toolkit.getDefaultToolkit().createImage(imagePath), board, cellOccupation, numberOfLines, numberOfColumns);

      File f = new File(imagePath);
      imageOk = f.exists() && f.canRead();
   }

   /**
    * Constructs a new ImageGelem (1x1 cells).
    *
    * <P><B>requires</B>: {@code imageURL != null && board != null && cellOccupation >= 0.0 && cellOccupation <= 100.0}
    * 
    * @param imageURL  image's file url
    * @param board  GBoard where the image will be drawed
    * @param cellOccupation  total cell's occupation (%)
    */
   //@ requires imageURL != null;
   //@ requires board != null;
   //@ requires cellOccupation >= 0.0 && cellOccupation <= 100.0; 
   public ImageGelem(URL imageURL, GBoard board, double cellOccupation)
   {
      this(imageURL, board, cellOccupation, 1, 1);
   }

   /**
    * Constructs a new ImageGelem.
    *
    * <P><B>requires</B>: {@code imageURL != null && board != null && cellOccupation >= 0.0 && cellOccupation <= 100.0 && numberOfLines >= 1 && numberOfColumns >= 1}
    * 
    * @param imageURL  image's file url
    * @param board  GBoard where the image will be drawed
    * @param cellOccupation  total cell's occupation (%)
    * @param numberOfLines  number of lines
    * @param numberOfColumns  number of columns
    */
   //@ requires imageURL != null;
   //@ requires board != null;
   //@ requires cellOccupation >= 0.0 && cellOccupation <= 100.0; 
   //@ requires numberOfLines >= 1 && numberOfColumns >= 1;
   public ImageGelem(URL imageURL, GBoard board, double cellOccupation, int numberOfLines, int numberOfColumns)
   {
      this(Toolkit.getDefaultToolkit().createImage(imageURL), board, cellOccupation, numberOfLines, numberOfColumns);

      //try
      //{
         String path = imageURL.getFile();
         imageOk = path != null && path.length() > 0;
         assert imageOk;
         //File f = new File(new URI(imageURL.toString()));
         //imageOk = f.exists() && f.canRead();
      //}
      //catch(URISyntaxException e)
      //{
         //imageOk = false;
      //}
   }

   /**
    * Constructs a new ImageGelem.
    *
    * <P><B>requires</B>: {@code image != null && board != null && cellOccupation >= 0.0 && cellOccupation <= 100.0 && numberOfLines >= 1 && numberOfColumns >= 1}
    * 
    * @param image  image's object
    * @param board  GBoard where the image will be drawed
    * @param cellOccupation  total cell's occupation (%)
    * @param numberOfLines  number of lines
    * @param numberOfColumns  number of columns
    */
   //@ requires image != null;
   //@ requires board != null;
   //@ requires cellOccupation >= 0.0 && cellOccupation <= 100.0; 
   //@ requires numberOfLines >= 1 && numberOfColumns >= 1;
   public ImageGelem(Image image, GBoard board, double cellOccupation, int numberOfLines, int numberOfColumns)
   {
      super(numberOfLines, numberOfColumns);

      assert image != null;
      assert board != null;
      assert cellOccupation >= 0.0 && cellOccupation <= 100.0; 

      this.image = image;
      this.board = board;
      this.cellOccupation = cellOccupation/100.0;
      Toolkit.getDefaultToolkit().prepareImage(image, -1, -1, board);

      imageOk = true;
   }

   /**
    * Draw ImageGelem in GBoard.
    *
    * <P><B>requires</B>: {@link Gelem#draw}
    * <BR><B>requires</B>: {@code imageOk()}
    * 
    * @param g  Java's Graphics object to use in drawing
    * @param line  line in GBoard to draw Gelem.
    * @param column  column in GBoard to draw Gelem.
    * @param cellWidth  number of horizontal pixels per cell
    * @param cellHeight  number of vertical pixels per cell
    * @param background  background color
    */
   //@ requires imageOk();
   public void draw(Graphics g, int line, int column, int cellWidth, int cellHeight, Color background)
   {
      assert g != null;
      assert imageOk(): "ERROR: image was not loaded";

      int w = cellWidth*numberOfColumns;
      int h = cellHeight*numberOfLines;

      int freeWidth = (int)((double)w*(1.0-cellOccupation));
      int freeHeight = (int)((double)h*(1.0-cellOccupation));

      g.drawImage(image, column*cellWidth+freeWidth/2, line*cellHeight+freeHeight/2, w-freeWidth, h-freeHeight, board);
   }

   /**
    * Erase ImageGelem from GBoard.
    *
    * <P><B>requires</B>: {@link Gelem#erase}
    * <BR><B>requires</B>: {@code imageOk()}
    * 
    * @param background  background color
    * @param g  Java's Graphics object to use in drawing
    * @param line  line in GBoard to draw Gelem.
    * @param column  column in GBoard to draw Gelem.
    * @param cellWidth  number of horizontal pixels per cell
    * @param cellHeight  number of vertical pixels per cell
    */
   //@ requires imageOk();
   public void erase(Graphics g, int line, int column, int cellWidth, int cellHeight, Color background)
   {
      assert g != null;
      assert imageOk(): "ERROR: image was not loaded";

      int w = cellWidth*numberOfColumns;
      int h = cellHeight*numberOfLines;

      int freeWidth = (int)((double)w*(1.0-cellOccupation));
      int freeHeight = (int)((double)h*(1.0-cellOccupation));

      g.setColor(background);
      g.fillRect(column*cellWidth+freeWidth/2, line*cellHeight+freeHeight/2, w-freeWidth, h-freeHeight);
   }

   protected final Image image;
   protected final GBoard board;
   protected final double cellOccupation;
   public boolean imageOk;

   /**
    * Indicates if the image was correctly loaded.
    *
    * @return {@code boolean} true if successfully loaded, otherwise it returns false
    */
   public /*@ pure @*/ boolean imageOk()
   {
      return imageOk;
   }
}
