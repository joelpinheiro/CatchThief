package pt.ua.gboard;

import static java.lang.System.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.List;
import java.util.LinkedList;

/**
 * This module implements a simplified (and yet quite powerful)
 * graphical board. This board is build as a 2D matrix of cells
 * accepting {@code Gelem} objects to be drawed. All drawings
 * are persistent within the board, so once a {@code Gelem} is
 * drawed the {@code GBoard} object will take care of its correct
 * drawing (for example when the dimensions of the board change).
 * Normally a {@code Gelem} uses only one cell ({@code 1x1}),
 * but it may occupy a rectangle of cells ({@code NxM}), as
 * long as it fits inside the board.
 *
 * <P>Top-left coordinate is {@code (0,0)}.
 *
 * <P>Multiple independent {@code GBoard} can be created, used, and
 * destroyed (terminate).  The last closing board will also terminate
 * the application.
 *
 * <P>To use GBoard the normal procedure is:
 *
 *  <P><B>1</B>. create a new {@code GBoard} object;
 * <BR><B>2</B>. draw/erase/move gelems in any position;
 * <BR><B>3</B>. terminate.
 *
 *  <P><B>invariant</B>: {@code numberOfLines() > 0 && numberOfColumns() > 0}
 * <BR><B>invariant</B>: {@code numberOfLayers() > 0}
 *
 * <P>This class follows DbC(tm) methodology
 * (@see <a href="http://en.wikipedia.org/wiki/Design_by_contract">Wikipedia</a>).
 * Where possible, contracts are implement with {@code JML}
 * (@see <a href="http://en.wikipedia.org/wiki/Java_Modeling_Language">Wikipedia</a>)
 * and native's {@code Java} assert.
 *
 * @author Miguel Oliveira e Silva (mos@ua.pt)
 */
public class GBoard extends JComponent
{
   //@ public invariant numberOfLines() > 0 && numberOfColumns() > 0;
   //@ public invariant numberOfLayers() > 0;

   /**
    * Constructs a new GBoard with ({@code numberOfLines x numberOfColumns}) cells and {@code numberOfLayers} layers.  The initial dimensions of cells will be: {@code 60x60}.
    *
    *  <P><B>requires</B>: {@code name != null}
    * <BR><B>requires</B>: {@code numberOfLines >= 1 && numberOfColumns >= 1}
    * <BR><B>requires</B>: {@code numberOfLayers >= 1}
    *
    * @param name  board name
    * @param numberOfLines  number of lines
    * @param numberOfColumns  number of columns
    * @param numberOfLayers  number of layers
    */
   //@ requires name != null;
   //@ requires numberOfLines >= 1 && numberOfColumns >= 1;
   //@ requires numberOfLayers >= 1;
   public GBoard(String name, int numberOfLines, int numberOfColumns, int numberOfLayers)
   {
      this(name, numberOfLines, numberOfColumns, 60, 60, numberOfLayers);
   }

   /**
    * Constructs a new GBoard with (numberOfLines x numberOfColumns) cells and numberOfLayers layers.
    *
    *  <P><B>requires</B>: {@code name != null}
    * <BR><B>requires</B>: {@code numberOfLines >= 1 && numberOfColumns >= 1}
    * <BR><B>requires</B>: {@code defaultCellWidth >= 1 && defaultCellHeight >= 1}
    * <BR><B>requires</B>: {@code numberOfLayers >= 1}
    *
    * @param name  board name
    * @param numberOfLines  number of lines
    * @param numberOfColumns  number of columns
    * @param defaultCellWidth  width in pixels of each cell
    * @param defaultCellHeight  height in pixels of each cell
    * @param numberOfLayers  number of layers
    */
   //@ requires name != null;
   //@ requires numberOfLines >= 1 && numberOfColumns >= 1;
   //@ requires defaultCellWidth >= 1 && defaultCellHeight >= 1;
   //@ requires numberOfLayers >= 1;
   @SuppressWarnings(value = "unchecked")
   public GBoard(String name, int numberOfLines, int numberOfColumns, int defaultCellWidth, int defaultCellHeight, int numberOfLayers)
   {
      assert name != null: "null name";
      assert numberOfLines >= 1 && numberOfColumns >= 1: "invalid board dimensions: "+numberOfLines+"x"+numberOfColumns;
      assert defaultCellWidth >= 1 && defaultCellHeight >= 1: "invalid cell dimensions: "+defaultCellWidth+"x"+defaultCellHeight;
      assert numberOfLayers >= 1: "invalid number of layers: "+numberOfLayers;

      this.numberOfLines = numberOfLines;
      this.numberOfColumns = numberOfColumns;
      this.defaultCellHeight = defaultCellHeight;
      this.defaultCellWidth = defaultCellWidth;
      this.numberOfLayers = numberOfLayers;
      maxGelemsDimensions = new Dimension[numberOfLayers];
      for(int ly = 0; ly < numberOfLayers; ly++)
         maxGelemsDimensions[ly] = new Dimension(1, 1); // minimum dimensions
      maxGelemNumberOfLines = 1;
      maxGelemNumberOfColumns = 1;
      boardMatrix = (LinkedList<Gelem>[][][])new LinkedList[numberOfLayers][numberOfLines][numberOfColumns];
      for(int ly = 0; ly < numberOfLayers; ly++)
         for(int ln = 0; ln < numberOfLines; ln++)
            for(int cl = 0; cl < numberOfColumns; cl++)
               boardMatrix[ly][ln][cl] = new LinkedList<Gelem>();
      awtListener = new AWTListener(this);

      final String windowName = name;
      final GBoard board = this;

      Runnable initGB = new Runnable()
      {
         public void run()
         {
            JFrame.setDefaultLookAndFeelDecorated(true);
            board.frame = new JFrame(windowName);
            board.frame.setBackground(Color.white);
            board.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            board.pane = frame.getContentPane();
            board.pane.add(board);
            board.frame.pack();
            board.frame.setVisible(true);
            board.setFocusable(true);
            board.requestFocusInWindow();
            board.addMouseListener(board.awtListener);
            board.addKeyListener(board.awtListener);
            board.frame.addWindowListener(board.awtListener);
            GBoard.increaseActiveBoardCount();
            //board.setFocusable(true);

            board.setDoubleBuffered(true);
            board.setOpaque(false);
            board.setBackground(Color.white);
         }
      };

      if(javax.swing.SwingUtilities.isEventDispatchThread())
      {
         initGB.run();
      }
      else
      {
         try
         {
            javax.swing.SwingUtilities.invokeAndWait(initGB);
         }
         catch(Exception e)
         {
            err.println("ERROR: unable to launch graphics server!");
            exit(1);
         }
      }
   }

   /**
    * Terminates the GBoard (closing also the attached window).
    *
    *  <P><B>requires</B>: {@code active()}
    *
    *  <P><B>ensures</B>: {@code !active()}
    */
   //@ requires active();
   //@ ensures !active();
   public synchronized void terminate()
   {
      assert active(): "board not active";

      Runnable terminateGB = new Runnable()
      {
         public void run()
         {
            frame.dispose();
            awtListener.processWindowClosingEvent();
         }
      };

      active = false;
      for(int ly = 0; ly < numberOfLayers; ly++)
         for(int ln = 0; ln < numberOfLines; ln++)
            for(int cl = 0; cl < numberOfColumns; cl++)
               boardMatrix[ly][ln][cl] = null;
      decreaseActiveBoardCount(this);
      if (javax.swing.SwingUtilities.isEventDispatchThread())
      {
         terminateGB.run();
      }
      else
      {
         try
         {
            javax.swing.SwingUtilities.invokeAndWait(terminateGB);
         }
         catch(Exception e)
         {
            err.println("ERROR: error while terminating board!");
            exit(1);
         }
      }
   }

   /**
    * Is object GBoard still active?
    *
    * @return {@code boolean} true if active, otherwise it returns false
    */
   public synchronized /*@ pure @*/ boolean active()
   {
      return active;
   }

   protected boolean active = true;

   /**
    * Board's number of lines.
    *
    * @return {@code int} number of lines
    */
   public synchronized /*@ pure @*/ int numberOfLines()
   {
      return numberOfLines;
   }

   protected final int numberOfLines;

   /**
    * Board's number of columns.
    *
    * @return {@code int} number of column
    */
   public synchronized /*@ pure @*/ int numberOfColumns()
   {
      return numberOfColumns;
   }

   protected final int numberOfColumns;

   /**
    * Board's number of layers.
    *
    * @return {@code int} number of layers
    */
   public synchronized /*@ pure @*/ int numberOfLayers()
   {
      return numberOfLayers;
   }

   protected final int numberOfLayers;

   /**
    * Checks if line is valid.
    *
    * @param line  the line number
    * @return {@code boolean} true if line is valid, otherwise it returns false
    */
   public synchronized /*@ pure @*/ boolean validLine(int line)
   {
      return line >= 0 && line < numberOfLines;
   }

   /**
    * Checks if column is valid.
    *
    * @param column  the column number
    * @return {@code boolean} true if column is valid, otherwise it returns false
    */
   public synchronized /*@ pure @*/ boolean validColumn(int column)
   {
      return column >= 0 && column < numberOfColumns;
   }

   /**
    * Checks if position is valid.
    *
    * @param line  the line number
    * @param column  the column number
    * @return {@code boolean} true if position is valid, otherwise it returns false
    */
   public synchronized /*@ pure @*/ boolean validPosition(int line, int column)
   {
      return validLine(line) && validColumn(column);
   }

   /**
    * Checks if layer is valid.
    *
    * @param layer  the layer number
    * @return {@code boolean} true if layer is valid, otherwise it returns false
    */
   public synchronized /*@ pure @*/ boolean validLayer(int layer)
   {
      return layer >= 0 && layer < numberOfLayers;
   }

   /**
    * Checks if a gelem fits inside current GBoard
    *
    *  <P><B>requires</B>: {@code gelem != null}
    * <BR><B>requires</B>: {@code validPosition(line, column)}
    * 
    * @param gelem  object
    * @param line  the line number
    * @param column  the column number
    * @return {@code boolean} true if fits inside
    */
   //@ requires gelem != null;
   //@ requires validPosition(line, column);
   public synchronized /*@ pure @*/ boolean gelemFitsInside(Gelem gelem, int line, int column)
   {
      assert gelem != null: "null gelem";
      assert validPosition(line, column): "invalid position: ("+line+","+column+")";

      return validLine(line+gelem.numberOfLines()-1) &&
             validColumn(column+gelem.numberOfColumns()-1);
   }

   /**
    * Checks if a gelem exists in a position at a layer.
    *
    *  <P><B>requires</B>: {@code gelem != null}
    * <BR><B>requires</B>: {@code validPosition(line, column)}
    * <BR><B>requires</B>: {@code validLayer(layer)}
    * 
    * @param gelem  object
    * @param line  the line number
    * @param column  the column number
    * @param layer  the layer number
    * @return {@code boolean} true if exists
    */
   //@ requires gelem != null;
   //@ requires validPosition(line, column);
   //@ requires validLayer(layer);
   public synchronized /*@ pure @*/ boolean exists(Gelem gelem, int line, int column, int layer)
   {
      assert gelem != null: "null gelem";
      assert validPosition(line, column): "invalid position: ("+line+","+column+")";
      assert validLayer(layer): "invalid layer";

      return boardMatrix[layer][line][column].contains(gelem);
   }

   /**
    * Checks if a gelem exists in a position in the layer interval {@code [minLayer, maxLayer]}.
    *
    *  <P><B>requires</B>: {@code gelem != null}
    * <BR><B>requires</B>: {@code validPosition(line, column)}
    * <BR><B>requires</B>: {@code validLayer(minLayer)}
    * <BR><B>requires</B>: {@code validLayer(maxLayer)}
    * <BR><B>requires</B>: {@code maxLayer >= minLayer;}
    * 
    * @param gelem  object
    * @param line  the line number
    * @param column  the column number
    * @param minLayer  the lower layer number
    * @param maxLayer  the higher (topest) layer number
    * @return {@code boolean} true if exists
    */
   //@ requires gelem != null;
   //@ requires validPosition(line, column);
   //@ requires validLayer(minLayer);
   //@ requires validLayer(maxLayer);
   //@ requires maxLayer >= minLayer;
   public synchronized /*@ pure @*/ boolean exists(Gelem gelem, int line, int column, int minLayer, int maxLayer)
   {
      assert gelem != null: "null gelem";
      assert validPosition(line, column): "invalid position: ("+line+","+column+")";
      assert validLayer(minLayer): "invalid layer";
      assert validLayer(maxLayer): "invalid layer";
      assert maxLayer >= minLayer: "empty layer interval";

      boolean result = false;

      for(int l = minLayer; !result && l <= maxLayer; l++)
         result = exists(gelem, line, column, l);

      return result;
   }

   /**
    * Checks if a gelem exists in a position in any layer.
    *
    *  <P><B>requires</B>: {@code gelem != null}
    * <BR><B>requires</B>: {@code validPosition(line, column)}
    * 
    * @param gelem  object
    * @param line  the line number
    * @param column  the column number
    * @return {@code boolean} true if exists
    */
   //@ requires gelem != null;
   //@ requires validPosition(line, column);
   public synchronized /*@ pure @*/ boolean exists(Gelem gelem, int line, int column)
   {
      assert gelem != null: "null gelem";
      assert validPosition(line, column): "invalid position: ("+line+","+column+")";

      return exists(gelem, line, column, 0, numberOfLayers-1);
   }

   /**
    * Checks if any gelem exists in a position at a layer.
    *
    *  <P><B>requires</B>: {@code validPosition(line, column)}
    * <BR><B>requires</B>: {@code validLayer(layer)}
    * 
    * @param line  the line number
    * @param column  the column number
    * @param layer  the layer number
    * @return {@code boolean} true if exists
    */
   //@ requires validPosition(line, column);
   //@ requires validLayer(layer);
   public synchronized /*@ pure @*/ boolean exists(int line, int column, int layer)
   {
      assert validPosition(line, column): "invalid position: ("+line+","+column+")";
      assert validLayer(layer): "invalid layer";

      return !boardMatrix[layer][line][column].isEmpty();
   }

   /**
    * Checks if any gelem exists in a position in the layer interval {@code [minLayer, maxLayer]}.
    *
    *  <P><B>requires</B>: {@code validPosition(line, column)}
    * <BR><B>requires</B>: {@code validLayer(minLayer)}
    * <BR><B>requires</B>: {@code validLayer(maxLayer)}
    * <BR><B>requires</B>: {@code maxLayer >= minLayer;}
    * 
    * @param line  the line number
    * @param column  the column number
    * @param minLayer  the lower layer number
    * @param maxLayer  the higher (topest) layer number
    * @return {@code boolean} true if exists
    */
   //@ requires validPosition(line, column);
   //@ requires validLayer(minLayer);
   //@ requires validLayer(maxLayer);
   //@ requires maxLayer >= minLayer;
   public synchronized /*@ pure @*/ boolean exists(int line, int column, int minLayer, int maxLayer)
   {
      assert validPosition(line, column): "invalid position: ("+line+","+column+")";
      assert validLayer(minLayer): "invalid layer";
      assert validLayer(maxLayer): "invalid layer";
      assert maxLayer >= minLayer: "empty layer interval";

      boolean result = false;

      for(int l = minLayer; !result && l <= maxLayer; l++)
         result = exists(line, column, l);

      return result;
   }

   /**
    * Checks if any gelem exists in a position at any layer.
    *
    *  <P><B>requires</B>: {@code validPosition(line, column)}
    * 
    * @param line  the line number
    * @param column  the column number
    * @return {@code boolean} true if exists
    */
   //@ requires validPosition(line, column);
   public synchronized /*@ pure @*/ boolean exists(int line, int column)
   {
      assert validPosition(line, column): "invalid position: ("+line+","+column+")";

      return exists(line, column, 0, numberOfLayers-1);
   }

   /**
    * Returns the topmost layer containing gelem in that position.
    *
    *  <P><B>requires</B>: {@code exists(gelem, line, column)}
    * 
    *  <P><B>ensures</B>: {@code validLayer(\result)}
    * 
    * @param gelem  object
    * @param line  the line number
    * @param column  the column number
    * @return {@code boolean} true if exists
    */
   //@ requires exists(gelem, line, column);
   //@ ensures validLayer(\result);
   public synchronized /*@ pure @*/ int gelemLayer(Gelem gelem, int line, int column)
   {
      assert exists(gelem, line, column): "gelem does not exist in position ("+line+","+column+")";

      int result;

      for(result = numberOfLayers-1; !exists(gelem, line, column, result); result--)
         ;

      return result;
   }

   /**
    * Returns the topest gelem at position {@code (line,column)}, or {@code null} if none exists.
    *
    *  <P><B>requires</B>: {@code validPosition(line, column)}
    *
    * @param line  the line number
    * @param column  the column number
    * @return {@code Gelem} the gelem's reference, or null
    */
   //@ requires validPosition(line, column);
   public synchronized Gelem topGelem(int line, int column)
   {
      assert validPosition(line, column): "invalid position: ("+line+","+column+")";

      return topGelem(line, column, 0, numberOfLayers-1);
   }

   /**
    * Returns the topest gelem at position {@code (line,column)} in layers
    * belonging to interval {@code [minLayer, maxLayer]}, or {@code null} if none exists.
    *
    *  <P><B>requires</B>: {@code validPosition(line, column)}
    * <BR><B>requires</B>: {@code validLayer(minLayer)}
    * <BR><B>requires</B>: {@code validLayer(maxLayer)}
    * <BR><B>requires</B>: {@code maxLayer >= minLayer;}
    *
    * @param line  the line number
    * @param column  the column number
    * @param minLayer  the lower layer number
    * @param maxLayer  the higher (topest) layer number
    * @return {@code Gelem} the gelem's reference, or null
    */
   //@ requires validPosition(line, column);
   //@ requires validLayer(minLayer);
   //@ requires validLayer(maxLayer);
   //@ requires maxLayer >= minLayer;
   public synchronized Gelem topGelem(int line, int column, int minLayer, int maxLayer)
   {
      assert validPosition(line, column): "invalid position: ("+line+","+column+")";
      assert validLayer(minLayer): "invalid layer";
      assert validLayer(maxLayer): "invalid layer";
      assert maxLayer >= minLayer: "empty layer interval";

      return topGelemAndLayer(line, column, minLayer, maxLayer).gelem;
   }

   /**
    * Move a gelem.
    *
    *  <P><B>requires</B>: {@code active()}
    * <BR><B>requires</B>: {@code gelem != null}
    * <BR><B>requires</B>: {@code validPosition(fromLine, fromColumn)}
    * <BR><B>requires</B>: {@code validLayer(fromLayer)}
    * <BR><B>requires</B>: {@code validPosition(toLine, toColumn)}
    * <BR><B>requires</B>: {@code validLayer(toLayer)}
    * <BR><B>requires</B>: {@code exists(gelem, fromLine, fromColumn, fromLayer)}
    * <BR><B>requires</B>: {@code (fromLine == toLine && fromColumn == toColumn && fromLayer == toLayer) || !exists(gelem, toLine, toColumn, toLayer)}
    *
    * @param gelem  object
    * @param fromLine  the line number origin
    * @param fromColumn  the column number origin
    * @param fromLayer  the origin layer
    * @param toLine  the line number destination
    * @param toColumn  the column number destination
    * @param toLayer  the destination layer
    */
   //@ requires active();
   //@ requires gelem != null;
   //@ requires validPosition(fromLine, fromColumn);
   //@ requires validLayer(fromLayer);
   //@ requires validPosition(toLine, toColumn);
   //@ requires validLayer(toLayer);
   //@ requires exists(gelem, fromLine, fromColumn, fromLayer);
   //@ requires !exists(gelem, toLine, toColumn, toLayer);
   //@ requires (fromLine == toLine && fromColumn == toColumn && fromLayer == toLayer) || !exists(gelem, toLine, toColumn, toLayer);
   public synchronized void move(Gelem gelem, int fromLine, int fromColumn, int fromLayer, int toLine, int toColumn, int toLayer)
   {
      assert active(): "board terminated";
      assert gelem != null: "null gelem";
      assert validPosition(fromLine, fromColumn): "invalid origin position: ("+fromLine+","+fromColumn+")";
      assert validLayer(fromLayer): "invalid origin layer";
      assert validPosition(toLine, toColumn): "invalid destination position: ("+toLine+","+toColumn+")";
      assert validLayer(toLayer): "invalid destination layer";
      assert exists(gelem, fromLine, fromColumn, fromLayer): "gelem does not exist in origin";
      assert (fromLine == toLine && fromColumn == toColumn && fromLayer == toLayer) ||
             !exists(gelem, toLine, toColumn, toLayer): "gelem already exists in destination";

      if (fromLine != toLine || fromColumn != toColumn || fromLayer != toLayer)
      {
         erase(gelem, fromLine, fromColumn, fromLayer);
         draw(gelem, toLine, toColumn, toLayer);
      }
   }

   /**
    * Move gelems within the same layer.
    *
    *  <P><B>requires</B>: {@code active()}
    * <BR><B>requires</B>: {@code gelem != null}
    * <BR><B>requires</B>: {@code validPosition(fromLine, fromColumn)}
    * <BR><B>requires</B>: {@code validPosition(toLine, toColumn)}
    * <BR><B>requires</B>: {@code exists(gelem, fromLine, fromColumn)}
    * <BR><B>requires</B>: {@code (fromLine == toLine && fromColumn == toColumn) || !exists(gelem, toLine, toColumn)}
    *
    * @param gelem  object
    * @param fromLine  the line number origin
    * @param fromColumn  the column number origin
    * @param toLine  the line number destination
    * @param toColumn  the column number destination
    */
   //@ requires active();
   //@ requires gelem != null;
   //@ requires validPosition(fromLine, fromColumn);
   //@ requires validPosition(toLine, toColumn);
   //@ requires exists(gelem, fromLine, fromColumn);
   //@ requires (fromLine == toLine && fromColumn == toColumn) || !exists(gelem, toLine, toColumn);
   public synchronized void move(Gelem gelem, int fromLine, int fromColumn, int toLine, int toColumn)
   {
      assert active(): "board terminated";
      assert gelem != null: "null gelem";
      assert validPosition(fromLine, fromColumn): "invalid origin position: ("+fromLine+","+fromColumn+")";
      assert validPosition(toLine, toColumn): "invalid destination position: ("+toLine+","+toColumn+")";
      assert exists(gelem, fromLine, fromColumn): "gelem does not exist in origin";
      assert (fromLine == toLine && fromColumn == toColumn) || !exists(gelem, toLine, toColumn): "gelem already exists in destination";

      for(int l = 0; l < numberOfLayers; l++)
         if (exists(gelem, fromLine, fromColumn, l))
            move(gelem, fromLine, fromColumn, l, toLine, toColumn, l);
   }

   /**
    * Add and draw a gelem.
    *
    *  <P><B>requires</B>: {@code active()}
    * <BR><B>requires</B>: {@code gelem != null}
    * <BR><B>requires</B>: {@code validPosition(line, column)}
    * <BR><B>requires</B>: {@code validLayer(layer)}
    * <BR><B>requires</B>: {@code !exists(gelem, line, column, layer)}
    * <BR><B>requires</B>: {@code gelemFitsInside(gelem, line, column)}
    *
    *  <P><B>ensures</B>: {@code exists(gelem, line, column, layer)}
    *
    * @param gelem  object
    * @param line  the line number
    * @param column  the column number
    * @param layer  the layer number
    */
   //@ requires active();
   //@ requires gelem != null;
   //@ requires validPosition(line, column);
   //@ requires validLayer(layer);
   //@ requires !exists(gelem, line, column, layer);
   //@ requires gelemFitsInside(gelem, line, column);
   //@ ensures exists(gelem, line, column, layer);
   public synchronized void draw(Gelem gelem, int line, int column, int layer)
   {
      assert active(): "board terminated";
      assert gelem != null: "null gelem";
      assert validPosition(line, column): "invalid position: ("+line+","+column+")";
      assert validLayer(layer): "invalid layer";
      assert !exists(gelem, line, column, layer): "gelm already exists in the layer at position";
      assert gelemFitsInside(gelem, line, column): "gelem cannot be drawed inside GBoard";


      if (gelem.numberOfLines() > maxGelemsDimensions[layer].height)
         maxGelemsDimensions[layer].height = gelem.numberOfLines(); // yes, it is a public attribute, but is not my fault!!!
      if (gelem.numberOfColumns() > maxGelemsDimensions[layer].width)
         maxGelemsDimensions[layer].width = gelem.numberOfColumns(); // yes, it is a public attribute, but is not my fault!!!

      // Needs to be optimized (this is a result of gelem registering elimination)!
      if (gelem.numberOfLines() > maxGelemNumberOfLines)
         maxGelemNumberOfLines = gelem.numberOfLines();
      if (gelem.numberOfColumns() > maxGelemNumberOfColumns)
         maxGelemNumberOfColumns = gelem.numberOfColumns();

      boardMatrix[layer][line][column].add(gelem);
      updatesIn(allocStartGEventTuple(column*cellWidth(), line*cellHeight(),
                                      gelem.numberOfColumns()*cellWidth(),
                                      gelem.numberOfLines()*cellHeight()));
      updatesIn(allocDrawGEventTuple(gelem, line, column));
      updateBoard(line, column, line+gelem.numberOfLines()-1, column+gelem.numberOfColumns()-1, layer+1, null);

      if (gelem.isMutable())
      {
         MutableGelem mgelem = gelem.mutable();
         assert mgelem != null;
         mgelem.registerDraw(this, line, column, layer);
      }

      assert exists(gelem, line, column, layer);
   }

   /**
    * Erase a gelem.
    *
    *  <P><B>requires</B>: {@code active()}
    * <BR><B>requires</B>: {@code gelem != null}
    * <BR><B>requires</B>: {@code validPosition(line, column)}
    * <BR><B>requires</B>: {@code validLayer(layer)}
    * <BR><B>requires</B>: {@code exists(gelem, line, column, layer)}
    *
    *  <P><B>ensures</B>: {@code !exists(gelem, line, column, layer)}
    *
    * @param gelem  object
    * @param line  the line number
    * @param column  the column number
    * @param layer  the layer number
    */
   //@ requires active();
   //@ requires gelem != null;
   //@ requires validPosition(line, column);
   //@ requires validLayer(layer);
   //@ requires exists(gelem, line, column, layer);
   //@ ensures !exists(gelem, line, column, layer);
   public synchronized void erase(Gelem gelem, int line, int column, int layer)
   {
      assert active(): "board terminated";
      assert gelem != null: "null gelem";
      assert validPosition(line, column): "invalid position: ("+line+","+column+")";
      assert validLayer(layer): "invalid layer";
      assert exists(gelem, line, column, layer): "gelm does not exist in layer at position";

      boardMatrix[layer][line][column].remove(gelem);
      updatesIn(allocStartGEventTuple(column*cellWidth(), line*cellHeight(),
                                      gelem.numberOfColumns()*cellWidth(),
                                      gelem.numberOfLines()*cellHeight()));
      updatesIn(allocEraseGEventTuple(gelem, line, column));
      updateBoard(line, column, line+gelem.numberOfLines()-1, column+gelem.numberOfColumns()-1, 0, null);

      if (gelem.isMutable())
      {
         MutableGelem mgelem = gelem.mutable();
         assert mgelem != null;
         mgelem.unregisterDraw(this, line, column, layer);
      }

      assert !exists(gelem, line, column, layer);
   }

   /**
    * Erase a gelem from position (in any layer where it exists).
    *
    *  <P><B>requires</B>: {@code active()}
    * <BR><B>requires</B>: {@code gelem != null}
    * <BR><B>requires</B>: {@code validPosition(line, column)}
    * <BR><B>requires</B>: {@code exists(gelem, line, column)}
    *
    *  <P><B>ensures</B>: {@code !exists(gelem, line, column)}
    *
    * @param gelem  object
    * @param line  the line number
    * @param column  the column number
    */
   //@ requires active();
   //@ requires gelem != null;
   //@ requires validPosition(line, column);
   //@ requires exists(gelem, line, column);
   //@ ensures !exists(gelem, line, column);
   public synchronized void erase(Gelem gelem, int line, int column)
   {
      assert active(): "board terminated";
      assert gelem != null: "null gelem";
      assert validPosition(line, column): "invalid position: ("+line+","+column+")";
      assert exists(gelem, line, column): "gelm does not exist in position";

      for(int l = 0; l < numberOfLayers; l++)
         if (exists(gelem, line, column, l))
            erase(gelem, line, column, l);

      assert !exists(gelem, line, column);
   }

   /**
    * Erases all gelems from a position in layers belonging to interval {@code [minLayer, maxLayer]}.
    *
    *  <P><B>requires</B>: {@code active()}
    * <BR><B>requires</B>: {@code validPosition(line, column)}
    * <BR><B>requires</B>: {@code validLayer(minLayer)}
    * <BR><B>requires</B>: {@code validLayer(maxLayer)}
    * <BR><B>requires</B>: {@code maxLayer >= minLayer;}
    *
    * @param line  the line number
    * @param column  the column number
    * @param minLayer  the lower layer number
    * @param maxLayer  the higher (topest) layer number
    */
   //@ requires active();
   //@ requires validPosition(line, column);
   //@ requires validLayer(minLayer);
   //@ requires validLayer(maxLayer);
   //@ requires maxLayer >= minLayer;
   public synchronized void erase(int line, int column, int minLayer, int maxLayer)
   {
      assert active(): "board terminated";
      assert validPosition(line, column): "invalid position: ("+line+","+column+")";
      assert validLayer(minLayer): "invalid layer";
      assert validLayer(maxLayer): "invalid layer";
      assert maxLayer >= minLayer: "empty layer interval";

      int nLine = 1;
      int nColumn = 1;
      for(int l = minLayer; l <= maxLayer; l++)
      {
         for(int i = 0; i < boardMatrix[l][line][column].size(); i++)
         {
            Gelem gelem = boardMatrix[l][line][column].get(i);
            if (nLine < gelem.numberOfLines())
               nLine = gelem.numberOfLines();
            if (nColumn < gelem.numberOfColumns())
               nColumn = gelem.numberOfColumns();

            if (gelem.isMutable())
            {
               MutableGelem mgelem = gelem.mutable();
               assert mgelem != null;
               mgelem.unregisterDraw(this, line, column, l);
            }

         }
      }
      updatesIn(allocStartGEventTuple(column*cellWidth(), line*cellHeight(),
                                      nColumn*cellWidth(), nLine*cellHeight()));
      for(int l = minLayer; l <= maxLayer; l++)
      {
         for(int i = 0; i < boardMatrix[l][line][column].size(); i++)
         {
            assert boardMatrix[l][line][column].get(i) != null;

            updatesIn(allocEraseGEventTuple(boardMatrix[l][line][column].get(i), line, column));
         }
         boardMatrix[l][line][column].clear();
      }
      updateBoard(line, column, line+nLine-1, column+nColumn-1, 0, null);
   }

   /**
    * Erase all gelems from a position (in all layers).
    *
    *  <P><B>requires</B>: {@code active()}
    * <BR><B>requires</B>: {@code validPosition(line, column)}
    *
    * @param line  the line number
    * @param column  the column number
    */
   //@ requires active();
   //@ requires validPosition(line, column);
   public synchronized void erase(int line, int column)
   {
      assert active(): "board terminated";
      assert validPosition(line, column): "invalid position: ("+line+","+column+")";

      erase(line, column, 0, numberOfLayers-1);
   }

   /**
    * Erases all drawed gelems.
    *
    *  <P><B>requires</B>: {@code active()}
    */
   //@ requires active();
   public synchronized void eraseAll()
   {
      eraseAll(0, numberOfLayers-1);
   }

   /**
    * Erases all drawed gelems in layers belonging to interval {@code [minLayer, maxLayer]}.
    *
    *  <P><B>requires</B>: {@code active()}
    * <BR><B>requires</B>: {@code validLayer(minLayer)}
    * <BR><B>requires</B>: {@code validLayer(maxLayer)}
    * <BR><B>requires</B>: {@code maxLayer >= minLayer;}
    *
    * @param minLayer  the lower layer number
    * @param maxLayer  the higher (topest) layer number
    */
   //@ requires active();
   //@ requires validLayer(minLayer);
   //@ requires validLayer(maxLayer);
   //@ requires maxLayer >= minLayer;
   public synchronized void eraseAll(int minLayer, int maxLayer)
   {
      assert active(): "board terminated";
      assert validLayer(minLayer): "invalid layer";
      assert validLayer(maxLayer): "invalid layer";
      assert maxLayer >= minLayer: "empty layer interval";

      for(int ly = minLayer; ly <= maxLayer; ly++)
         for(int ln = 0; ln < numberOfLines; ln++)
            for(int cl = 0; cl < numberOfColumns; cl++)
               boardMatrix[ly][ln][cl].clear();

      final GBoard board = this;

      Runnable eraseGB = new Runnable()
      {
         public void run()
         {
            synchronized(board)
            {
               Graphics g = getGraphics();
               Color c = getForeground();
               g.setColor(getBackground());
               g.setClip(0, 0, getWidth(), getHeight());
               g.fillRect(0, 0, getWidth(), getHeight());
               g.setColor(c);
               paintComponent(g);
            }
         }
      };

      if (javax.swing.SwingUtilities.isEventDispatchThread())
      {
         eraseGB.run();
      }
      else
      {
         javax.swing.SwingUtilities.invokeLater(eraseGB);
      }
   }

   /**
    * Pushes a new board input handler.
    *
    *  <P><B>requires</B>: {@code active()}
    * <BR><B>requires</B>: {@code handler != null}
    *
    * @param handler  the input handler obejct's
    */
   //@ requires active();
   //@ requires handler != null;
   public synchronized void pushInputHandler(GBoardInputHandler handler)
   {
      assert active(): "board terminated";
      assert handler != null: "null handler";

      awtListener.pushHandler(handler);
   }

   /**
    * Pops the board's top input handler.
    *
    *  <P><B>requires</B>: {@code inputListenerExists();}
    */
   //@ requires inputListenerExists();
   public synchronized void popInputHandler()
   {
      assert inputListenerExists(): "input listener stack is empty";

      awtListener.popHandler();
   }

   /**
    * Checks if exists an input handler.
    *
    * @return {@code boolean} true if exists
    */
   public synchronized /*@ pure @*/ boolean inputListenerExists()
   {
      return awtListener.top != null;
   }

/*
   public synchronized void printBoard()
   {
      for(int ly = 0; ly < numberOfLayers; ly++)
      {
         out.println("Layer "+ly);
         for(int ln = 0; ln < numberOfLines; ln++)
         {
            for(int cl = 0; cl < numberOfColumns; cl++)
            {
               int i;
               for(i = 0; i < boardMatrix[ly][ln][cl].size(); i++)
                  out.printf(" %s%5d", i > 0 ? "," : "", boardMatrix[ly][ln][cl].get(i));
               if (i == 0)
                  out.printf(" %5s", "-----");
            }
            out.println();
         }
      }
   }
*/

   protected class GelemAndLayer
   {
      Gelem gelem;
      int layer;
   }

   protected GelemAndLayer topGelemAndLayer(int line, int column, int minLayer, int maxLayer)
   {
      GelemAndLayer result = new GelemAndLayer();

      int lastLine = (line-maxGelemNumberOfLines+1 < 0 ?
            0 : line-maxGelemNumberOfLines+1);
      int lastColumn = (column-maxGelemNumberOfColumns+1 < 0 ?
            0 : column-maxGelemNumberOfColumns+1);

      int ly;
      result.gelem = null;
      for(ly = maxLayer; result.gelem == null && ly >= minLayer; ly--)
         for(int ln = line; result.gelem == null && ln >= lastLine; ln--)
            for(int cl = column; result.gelem == null && cl >= lastColumn; cl--)
               for(int i = boardMatrix[ly][ln][cl].size()-1; result.gelem == null && i >= 0; i--)
               {
                  Gelem gelem = boardMatrix[ly][ln][cl].get(i);
                  if (ln+gelem.numberOfLines()-1 >= line &&
                      cl+gelem.numberOfColumns()-1 >= column)
                     result.gelem = gelem;
               }
      result.layer = ly+1;

      return result;
   }

   protected void updateBoard(int startLine, int startColumn, int endLine, int endColumn, int startLayer, Gelem ignoreGelem)
   {
      int backwardLine = (startLine-maxGelemNumberOfLines+1 < 0 ?
                          0 : startLine-maxGelemNumberOfLines+1);
      int backwardColumn = (startColumn-maxGelemNumberOfColumns+1 < 0 ?
                            0 : startColumn-maxGelemNumberOfColumns+1);

      /* DEBUG
         out.println("startLine = "+startLine);
         out.println("startColumn = "+startColumn);
         out.println("startLayer = "+startLayer);
         out.println("maxGelemNumberOfLines = "+maxGelemNumberOfLines);
         out.println("maxGelemNumberOfColumns = "+maxGelemNumberOfColumns);
         out.println("backwardLine = "+backwardLine);
         out.println("backwardColumn = "+backwardColumn);
         out.println();
         */

      for(int ly = startLayer; ly < numberOfLayers; ly++)
      {
         for(int ln = backwardLine; ln <= endLine; ln++)
            for(int cl = backwardColumn; cl <= endColumn; cl++)
            {
               if (ln >= startLine && ln <= endLine && cl >= startColumn && cl <= endColumn)
               {
                  for(int i = 0; i < boardMatrix[ly][ln][cl].size(); i++)
                  {
                     assert boardMatrix[ly][ln][cl].get(i) != null;

                     Gelem other = boardMatrix[ly][ln][cl].get(i);
                     if (other != ignoreGelem)
                        updatesIn(allocDrawGEventTuple(other, ln, cl));
                  }
               }
               else
               {
                  for(int i = 0; i < boardMatrix[ly][ln][cl].size(); i++)
                  {
                     assert boardMatrix[ly][ln][cl].get(i) != null;

                     Gelem other = boardMatrix[ly][ln][cl].get(i);
                     if (other != ignoreGelem &&
                         ln+other.numberOfLines()-1 >= startLine &&
                         cl+other.numberOfColumns()-1 >= startColumn)
                        updatesIn(allocDrawGEventTuple(other, ln, cl));
                  }
               }
            }
      }
      updatesIn(allocEndGEventTuple());

      final GBoard board = this;

      Runnable updateGB = new Runnable()
      {
         public void run()
         {
            synchronized(board)
            {
               Graphics g = getGraphics();
               while(!updatesEmpty())
               {
                  GEventTuple elem = updatesOut();
                  switch(elem.type)
                  {
                     case START:
                        //out.println("[START]");
                        g.setClip(elem.clipRect);
                        break;
                     case DRAW:
                        //out.println("   [DRAW] ("+elem.line+","+elem.column+")");
                        gelemDraw(elem.gelem, elem.line, elem.column, g);
                        break;
                     case ERASE:
                        //out.println("   [ERASE] ("+elem.line+","+elem.column+")");
                        gelemErase(elem.gelem, elem.line, elem.column, g);
                        break;
                     case END:
                        //out.println("[END]");
                        g.setClip(0, 0, getWidth(), getHeight());
                        break;
                  }
                  freeGEventTuple(elem);
               }
            }
         }
      };

      if (javax.swing.SwingUtilities.isEventDispatchThread())
      {
         updateGB.run();
      }
      else
      {
         javax.swing.SwingUtilities.invokeLater(updateGB);
      }
   }

   protected synchronized void paintComponent(Graphics g)
   {
      assert javax.swing.SwingUtilities.isEventDispatchThread(): "[INTERNAL]: paintComponent invoked by a thread other than the event dispatch thread!";

      //synchronized(GBoard.class)
      {
         g.setClip(0, 0, getWidth(), getHeight());
         Color c = getForeground();
         g.setColor(getBackground());
         g.fillRect(0, 0, getWidth(), getHeight());
         g.setColor(c);
         super.paintComponent(g);
         for(int ly = 0; ly < numberOfLayers; ly++)
            for(int ln = 0; ln < numberOfLines; ln++)
               for(int cl = 0; cl < numberOfColumns; cl++)
                  for(int i = 0; i < boardMatrix[ly][ln][cl].size(); i++)
                     gelemDraw(boardMatrix[ly][ln][cl].get(i), ln, cl, g);
      }
   }

   protected void gelemDraw(Gelem gelem, int line, int column, Graphics g)
   {
      assert javax.swing.SwingUtilities.isEventDispatchThread(): "[INTERNAL]: gelemDraw invoked by a thread other than the event dispatch thread!";
      assert gelem != null: "null gelem!";
      assert validPosition(line, column): "invalid position: ("+line+","+column+")";
      assert g != null: "null Graphics!";

      /*
      int cellWidth = cellWidth();
      int cellHeight = cellHeight();
      if (!externalClip)
      {
         // ensure nothing is drawed outside cell bounding box!
         g.setClip(gelem.x(column, cellWidth), gelem.y(line, cellHeight),
                   gelem.width(cellWidth), gelem.height(cellHeight));
      }
      */
      gelem.draw(g, line, column, cellWidth(), cellHeight(), getBackground());
   }

   protected void gelemErase(Gelem gelem, int line, int column, Graphics g)
   {
      assert javax.swing.SwingUtilities.isEventDispatchThread(): "[INTERNAL]: gelemErase invoked by a thread other than the event dispatch thread!";
      assert gelem != null: "null gelem!";
      assert validPosition(line, column): "invalid position: ("+line+","+column+")";
      assert g != null: "null Graphics!";

      /*
      int cellWidth = cellWidth();
      int cellHeight = cellHeight();
      if (!externalClip)
      {
         // ensure nothing is drawed outside cell bounding box!
         g.setClip(gelem.x(column, cellWidth), gelem.y(line, cellHeight),
                   gelem.width(cellWidth), gelem.height(cellHeight));
      }
      */
      gelem.erase(g, line, column, cellWidth(), cellHeight(), getBackground());
   }

   /**
    * Returns the current width of each cell (in pixels).
    *
    * @return {@code int} number of pixels
    */
   public synchronized /*@ pure @*/ int cellWidth()
   {
      return getWidth() / numberOfColumns;
   }

   /**
    * Returns the current height of each cell (in pixels).
    *
    * @return {@code int} number of pixels
    */
   public synchronized /*@ pure @*/ int cellHeight()
   {
      return getHeight() / numberOfLines;
   }

   /**
    * Stops thread execution for {@code msec} milliseconds.
    *
    *  <P><B>requires</B>: {@code msec >= 0}
    *
    *  <P><B>ensures</B>: not interrupted!
    *
    * @param msec  duration
    */
   //@ requires msec >= 0;
   public static void sleep(int msec)
   {
      assert msec >= 0: "invalid duration: "+msec;

      try
      {
         Thread.sleep(msec);
      }
      catch(InterruptedException e)
      {
         //assert false: "Thread.sleep interrupted!";
         throw new RuntimeException("GBoard.sleep interrupted!");
      }
   }

   public synchronized Dimension getPreferredSize()
   {
      return new Dimension(numberOfColumns*defaultCellWidth, numberOfLines*defaultCellHeight);
   }

   public synchronized JFrame frame()
   {
      return frame;
   }

   public synchronized Container contentPane()
   {
      return pane;
   }

   // count of active boards to ensure a proper termination of application
   protected static int activeBoardCount = 0;

   protected static void increaseActiveBoardCount()
   { // GBoard class already synchronized in init
      GBoard.activeBoardCount++;
   }

   protected synchronized static void decreaseActiveBoardCount(GBoard board)
   {
      GBoard.activeBoardCount--;
      //out.println("GBoard: closed - "+GBoard.activeBoardCount+" remaining");
      if (GBoard.activeBoardCount == 0)
      {
         exit(0);
         //board.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      }
   }

   protected JFrame frame;
   protected Container pane;

   protected Dimension[] maxGelemsDimensions; 
   protected int maxGelemNumberOfLines = 1; // Used to optimize updateBoard! // to be replaced!
   protected int maxGelemNumberOfColumns = 1; // to be replaced!
   protected final int defaultCellWidth;
   protected final int defaultCellHeight;
   protected final List<Gelem>[][][] boardMatrix;
   protected AWTListener awtListener;

   protected GEventTuple updatesHead = null;
   protected GEventTuple updatesTail = null;

   protected void updatesIn(GEventTuple elem)
   {
      assert elem != null;

      if (updatesTail != null)
         updatesTail.next = elem;
      else
         updatesHead = elem;
      updatesTail = elem;
   }

   protected GEventTuple updatesOut()
   {
      GEventTuple result = updatesHead;
      updatesHead = updatesHead.next;
      if (updatesHead == null)
         updatesTail = null;
      return result;
   }

   boolean updatesEmpty()
   {
      return updatesHead == null;
   }

   private static final long serialVersionUID = 72635L;

   // AWTListener section:

   protected class AWTListener implements MouseListener, KeyListener, WindowListener
   {
      AWTListener(GBoard board)
      {
         this.board = board;
         top = null;
      }

      synchronized void pushHandler(GBoardInputHandler handler)
      {
         HandlerNode n = new HandlerNode();
         n.handler = handler;
         n.next = top;
         top = n;
      }

      synchronized void popHandler()
      {
         assert top != null;

         top = top.next;
      }

      synchronized void processMouseEvent(MouseEvent e, int mask)
      {
         assert javax.swing.SwingUtilities.isEventDispatchThread(): "[INTERNAL]: processMouseEvent invoked by a thread other than the event dispatch thread!";

         synchronized(board)
         {
            HandlerNode n = top;
            while(n != null && !n.handler.activated(mask))
               n = n.next;
            if (n != null)
            {
               int line = e.getY()/board.cellHeight();
               int column = e.getX()/board.cellWidth();
               int code = e.getButton();
               Gelem gelem = null;
               int layer = 0;
               if (board.validPosition(line, column))
               {
                  GelemAndLayer r = board.topGelemAndLayer(line, column, 0, numberOfLayers-1);
                  gelem = r.gelem;
                  layer = r.layer;
               }
               n.handler.run(board, line, column, layer, GBoardInputHandler.mouseEventType, code, gelem);
            }
         }
      }

      synchronized void processKeyEvent(KeyEvent e, int mask)
      {
         assert javax.swing.SwingUtilities.isEventDispatchThread(): "[INTERNAL]: processKeyEvent invoked by a thread other than the event dispatch thread!";

         synchronized(board)
         {
            HandlerNode n = top;
            while(n != null && !n.handler.activated(mask))
               n = n.next;
            if (n != null)
            {
               int line = -1;
               int column = -1;
               Gelem gelem = null;
               int layer = 0;
               Point p = board.getMousePosition();
               if (p != null)
               {
                  line = p.y/board.cellHeight();
                  column = p.x/board.cellWidth();
                  if (board.validPosition(line, column))
                  {
                     GelemAndLayer r = board.topGelemAndLayer(line, column, 0, numberOfLayers-1);
                     gelem = r.gelem;
                     layer = r.layer;
                  }
               }
               int code = e.getKeyCode();
               n.handler.run(board, line, column, layer, GBoardInputHandler.keyEventType, code, gelem);
            }
         }
      }

      synchronized void processWindowClosingEvent()
      {
         assert javax.swing.SwingUtilities.isEventDispatchThread(): "[INTERNAL]: processWindowClosingEvent invoked by a thread other than the event dispatch thread!";

         synchronized(board)
         {
            HandlerNode n = top;
            while(n != null && !n.handler.activated(GBoardInputHandler.gboardClosingMask))
               n = n.next;
            if (n != null)
            {
               int line = -1;
               int column = -1;
               Gelem gelem = null;
               int layer = 0;
               int code = WindowEvent.WINDOW_CLOSED;
               n.handler.run(board, line, column, layer, GBoardInputHandler.windowEventType, code, gelem);
            }
         }
      }

      synchronized public void mousePressed(MouseEvent e)
      {
         //out.println("mousePressed");
         processMouseEvent(e, GBoardInputHandler.mousePressedMask);
      }

      synchronized public void mouseReleased(MouseEvent e)
      {
         //out.println("mouseReleased");
         processMouseEvent(e, GBoardInputHandler.mouseReleasedMask);
      }

      synchronized public void mouseClicked(MouseEvent e)
      {
         //out.println("mouseClicked");
         board.requestFocusInWindow();
         processMouseEvent(e, GBoardInputHandler.mouseClickedMask);
      }

      synchronized public void mouseEntered(MouseEvent e)
      {
         //out.println("mouseEntered");
      }

      synchronized public void mouseExited(MouseEvent e)
      {
         //out.println("mouseExited");
      }

      synchronized public void keyPressed(KeyEvent e)
      {
         //out.println("keyPressed");
         processKeyEvent(e, GBoardInputHandler.keyPressedMask);
      }

      synchronized public void keyReleased(KeyEvent e)
      {
         //out.println("keyReleased");
         processKeyEvent(e, GBoardInputHandler.keyReleasedMask);
      }

      synchronized public void keyTyped(KeyEvent e)
      {
         //out.println("keyTyped");
         processKeyEvent(e, GBoardInputHandler.keyTypedMask);
      }

      // WindowListener

      synchronized public void windowActivated(WindowEvent e)
      {
      }

      synchronized public void windowClosed(WindowEvent e)
      {
      }

      synchronized public void windowClosing(WindowEvent e)
      {
         processWindowClosingEvent();
         decreaseActiveBoardCount(board);
      }

      synchronized public void windowDeactivated(WindowEvent e)
      {
      }

      synchronized public void windowDeiconified(WindowEvent e)
      {
      }

      synchronized public void windowIconified(WindowEvent e)
      {
      }

      synchronized public void windowOpened(WindowEvent e)
      {
      }

      // internal attributes

      protected GBoard board;
      protected HandlerNode top;

      protected class HandlerNode
      {
         GBoardInputHandler handler;
         HandlerNode next;
      }
   }

   // GEventTuple section:

   protected enum GEventType {START, DRAW, ERASE, END};
   protected class GEventTuple
   {
      GEventTuple set(int x, int y, int width, int height)
      {
         //out.println("GEventType.START: ("+x+","+y+","+width+","+height+")");
         type = GEventType.START;
         clipRect.setLocation(x, y);
         clipRect.setSize(width, height);
         return this;
      }

      GEventTuple set(Gelem gelem, int line, int column, boolean toDraw)
      {
         assert gelem != null;

         //out.println("GEventType."+(toDraw ? "DRAW":"ERASE")+": ("+line+","+column+")");
         type = (toDraw ? GEventType.DRAW : GEventType.ERASE);
         this.gelem = gelem;
         this.line = line;
         this.column = column;
         this.toDraw = toDraw;
         next = null;
         return this;
      }

      GEventTuple set()
      {
         //out.println("GEventType.END");
         type = GEventType.END;
         return this;
      }

      void reset()
      {
         gelem = null;
         line = 0;
         column = 0;
         toDraw = false;
         clipRect.setLocation(0,0);
         clipRect.setSize(0, 0);
         next = null;
      }

      GEventType type;
      Gelem gelem;
      int line;
      int column;
      boolean toDraw;
      Rectangle clipRect = new Rectangle();
      GEventTuple next;
   }

   protected synchronized GEventTuple allocGEventTuple()
   {
      GEventTuple result;

      if (freeElems == null)
         result = new GEventTuple();
      else
      {
         result = freeElems;
         freeElems = freeElems.next;
      }
      result.reset();

      return result;
   }

   protected synchronized void freeGEventTuple(GEventTuple elem)
   {
      elem.gelem = null;
      elem.next = freeElems;
      freeElems = elem;
   }

   protected boolean started = false;

   protected GEventTuple allocStartGEventTuple(int x, int y, int width, int height)
   {
      assert !started;

      started = true;
      return allocGEventTuple().set(x, y, width, height);
   }

   protected GEventTuple allocDrawGEventTuple(Gelem gelem, int line, int column)
   {
      assert started;

      return allocGEventTuple().set(gelem, line, column, true);
   }

   protected GEventTuple allocEraseGEventTuple(Gelem gelem, int line, int column)
   {
      assert started;

      return allocGEventTuple().set(gelem, line, column, false);
   }

   protected GEventTuple allocEndGEventTuple()
   {
      assert started;

      started = false;
      return allocGEventTuple().set();
   }

   protected GEventTuple freeElems = null;

   // MutableGelems: (exported to MutableGelem (approached by package visibility)

   void erase(Gelem gelem, MutableGelem.GBoardReg list)
   {
      assert gelem != null;
      assert list != null;

      MutableGelem.GBoardReg gbreg;
      // erase all:
      gbreg = list;
      while(gbreg != null)
      {
         updatesIn(allocStartGEventTuple(gbreg.column()*cellWidth(), gbreg.line()*cellHeight(),
                                         gelem.numberOfColumns()*cellWidth(),
                                         gelem.numberOfLines()*cellHeight()));
         updatesIn(allocEraseGEventTuple(gelem, gbreg.line(), gbreg.column()));
         updateBoard(gbreg.line(), gbreg.column(), gbreg.line()+gelem.numberOfLines()-1, gbreg.column()+gelem.numberOfColumns()-1, 0, gelem);
         gbreg = gbreg.next();
      }
   }

   void draw(Gelem gelem, MutableGelem.GBoardReg list)
   {
      assert gelem != null;
      assert list != null;

      MutableGelem.GBoardReg gbreg;
      // draw all:
      gbreg = list;
      while(gbreg != null)
      {
         updatesIn(allocStartGEventTuple(gbreg.column()*cellWidth(), gbreg.line()*cellHeight(),
                                         gelem.numberOfColumns()*cellWidth(),
                                         gelem.numberOfLines()*cellHeight()));
         updatesIn(allocDrawGEventTuple(gelem, gbreg.line(), gbreg.column()));
         updateBoard(gbreg.line(), gbreg.column(), gbreg.line()+gelem.numberOfLines()-1, gbreg.column()+gelem.numberOfColumns()-1, gbreg.layer()+1, gelem);
         gbreg = gbreg.next();
      }
   }
}

