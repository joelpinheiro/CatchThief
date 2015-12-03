package pt.ua.gboard.games;

import static java.lang.System.*;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.awt.Point;
import pt.ua.gboard.*;

/**
 * GBoard's based maze module.
 *
 * <P>Top-left coordinate is {@code (0,0)}.
 *
 * <P>Point objects are used with the following meaning:
 * <BR>  - y: line position;
 * <BR>  - x: column position;
 *
 *  <P><B>invariant</B>: {@code numberOfLines >= 1 && numberOfColumns >= 1}
 * <BR><B>invariant</B>: {@code board != null}
 *
 * <P>This class follows DbC(tm) methodology.
 * Where possible, contracts are implement with JML and native's Java assert.
 *
 * @author Miguel Oliveira e Silva (mos@ua.pt)
 */
public class Labyrinth
{
   // BUG JML! //@ invariant numberOfLines >= 1 && numberOfColumns >= 1;
   // BUG JML! //@ invariant board != null;

   /**
    * Number of layers used when creating the GBoard (default value is 2).
    *
    * @return number of layers
    */
   public synchronized static int numberOfLayers()
   {
      return numberOfLayers;
   }

   /**
    * Define the number of layers to be used when creating the GBoard.
    *
    *  <P><B>requires</B>: {@code numberOfLayers >= 1}
    *
    * @param numberOfLayers  the number of layers
    */
   //@ requires numberOfLayers >= 1;
   public synchronized static void setNumberOfLayers(int numberOfLayers)
   {
      assert numberOfLayers >= 1: "invalid number of layers: "+numberOfLayers;

      Labyrinth.numberOfLayers = numberOfLayers;
   }

   /**
    * Checks if a file path is valid
    *
    * @param filename  file path
    * @return {@code boolean} true if the path is valid, otherwise it returns false
    */
   public static boolean validMapFile(String filename)
   {
      boolean result = filename != null;

      if (result)
      {
         File fin = new File(filename);
         result = fin.exists() && !fin.isDirectory() && fin.canRead();
      }

      return result;
   }

   protected static int numberOfLayers = 2; // default value

   /**
    * Nome of window used when creating the GBoard (default value is Labyrinth).
    *
    * @return name
    */
   public synchronized static String windowName()
   {
      return windowName;
   }

   /**
    * Define the name used when creating the GBoard.
    *
    *  <P><B>requires</B>: {@code windowName != null}
    *
    * @param windowName  name
    */
   //@ requires windowName != null;
   public synchronized static void setWindowName(String windowName)
   {
      assert windowName != null: "invalid window name";

      Labyrinth.windowName = windowName;
   }

   protected static String windowName = "Catch The Thief Simulator"; // default value

   /**
    * Creates a labyrinth from a file.
    *
    *  <P><B>requires</B>: {@code validMapFile(filename)}
    *
    * @param filename  path to the file
    */
   //@ requires validMapFile(filename);
   public Labyrinth(String filename)
   {
      this(loadMap(filename), null, 1);
   }

   /**
    * Creates a labyrinth from a file, using external road symbols.
    *
    *  <P><B>requires</B>: {@code validMapFile(filename)}
    *
    * @param filename  path to the file
    * @param roadSymbols  array of extra character road symbols
    */
   //@ requires validMapFile(filename);
   public Labyrinth(String filename, char[] roadSymbols)
   {
      this(loadMap(filename), roadSymbols, 1);
   }

   /**
    * Creates a labyrinth from a file.
    *
    *  <P><B>requires</B>: {@code validMapFile(filename)}
    * <BR><B>requires</B>: {@code gelemCellsSize > 0}
    *
    * @param filename  path to the file
    * @param gelemCellsSize  width and height of each gelem in GBoard
    */
   //@ requires validMapFile(filename);
   //@ requires gelemCellsSize > 0;
   public Labyrinth(String filename, int gelemCellsSize)
   {
      this(loadMap(filename), null, gelemCellsSize);
   }

   /**
    * Creates a labyrinth from a file, using external road symbols.
    *
    *  <P><B>requires</B>: {@code validMapFile(filename)}
    * <BR><B>requires</B>: {@code gelemCellsSize > 0}
    *
    * @param filename  path to the file
    * @param roadSymbols  array of extra character road symbols
    * @param gelemCellsSize  width and height of each gelem in GBoard
    */
   //@ requires validMapFile(filename);
   //@ requires gelemCellsSize > 0;
   public Labyrinth(String filename, char[] roadSymbols, int gelemCellsSize)
   {
      this(loadMap(filename), roadSymbols, gelemCellsSize);
   }

   /**
    * Creates a labyrinth from an array of Strings.
    *
    *  <P><B>requires</B>: {@code maze != null && maze.length > 0}
    *
    * @param maze  array
    */
   //@ requires maze != null && maze.length > 0;
   public Labyrinth(String[] maze)
   {
      this(maze, null, 1);
   }

   /**
    * Creates a labyrinth from an array of Strings, using external road symbols.
    *
    *  <P><B>requires</B>: {@code maze != null && maze.length > 0}
    *
    * @param maze  array
    * @param roadSymbols  array of extra character road symbols
    */
   //@ requires maze != null && maze.length > 0;
   public Labyrinth(String[] maze, char[] roadSymbols)
   {
      this(maze, roadSymbols, 1);
   }

   /**
    * Creates a labyrinth from an array of Strings.
    *
    *  <P><B>requires</B>: {@code maze != null && maze.length > 0}
    * <BR><B>requires</B>: {@code gelemCellsSize > 0}
    *
    * @param maze  array
    * @param gelemCellsSize  width and height of each gelem in GBoard
    */
   //@ requires maze != null && maze.length > 0;
   //@ requires gelemCellsSize > 0;
   public Labyrinth(String[] maze, int gelemCellsSize)
   {
      this(maze, null, gelemCellsSize);
   }

   /**
    * Creates a labyrinth from an array of Strings, using external road symbols.
    *
    *  <P><B>requires</B>: {@code maze != null && maze.length > 0}
    * <BR><B>requires</B>: {@code gelemCellsSize > 0}
    *
    * @param maze  array
    * @param roadSymbols  array of extra character road symbols
    * @param gelemCellsSize  width and height of each gelem in GBoard
    */
   //@ requires maze != null && maze.length > 0;
   //@ requires gelemCellsSize > 0;
   public Labyrinth(String[] maze, char[] roadSymbols, int gelemCellsSize)
   {
      assert maze != null && maze.length > 0: "invalid maze (or file not read)";
      assert gelemCellsSize > 0: "invalid gelem cell size: "+gelemCellsSize;

      if (roadSymbols != null)
      {
         this.roadSymbols = new char[roadSymbols.length]; // to prevent aliasing problems!
         arraycopy(roadSymbols, 0, this.roadSymbols, 0, roadSymbols.length);
         roadSymbolGelems = new Gelem[roadSymbols.length+1]; // null entries, by default (last for ' ')!
      }
      else
      {
         this.roadSymbols = null;
         roadSymbolGelems = new Gelem[1];
      }
 
      // setting wall symbols array
      char[] wallSymbols = new char[0];
      for(int l = 0; l < maze.length; l++)
         for(int c = 0; c < maze[l].length(); c++)
         {
            char s = maze[l].charAt(c);
            if (!symbolExists(s, roadSymbols) && !symbolExists(s, wallSymbols))
            {
               char[] newWallSymbols = new char[wallSymbols.length+1];
               arraycopy(wallSymbols, 0, newWallSymbols, 0, wallSymbols.length);
               newWallSymbols[wallSymbols.length] = s;
               wallSymbols = newWallSymbols;
            }
         }
      this.wallSymbols = wallSymbols;
      wallSymbolGelems = new Gelem[wallSymbols.length]; // null entries, by default

      roadSymbolsPositions = new Point[0];
      this.gelemCellsSize = gelemCellsSize;

      numberOfLines = maze.length;
      int numColumns = maze[0].length();
      for(int i = 1; i < maze.length; i++)
         if (numColumns < maze[i].length())
            numColumns = maze[i].length();
      numberOfColumns = numColumns;
      map = new int[numberOfLines][numberOfColumns];
      for(int l = 0; l < numberOfLines; l++)
         for(int c = 0; c < numberOfColumns; c++)
            map[l][c] = UNDEFINED;
      for(int l = 0; l < numberOfLines; l++)
      {
         fillOutside(maze, l, 0);
         fillOutside(maze, l, numberOfColumns-1);
      }
      for(int c = 0; c < numberOfColumns; c++)
      {
         fillOutside(maze, 0, c);
         fillOutside(maze, numberOfLines-1, c);
      }

      for(int l = 0; l < numberOfLines; l++)
         for(int c = 0; c < maze[l].length(); c++)
            if (!isOutside(l, c))
            {
               char ch = maze[l].charAt(c);
               if (isRoadSymbol(ch))
               {
                  map[l][c] = (ROAD | (int)ch);
                  if (ch != ' ')
                     addRoadSymbolPosition(l, c);
               }
               else
                  map[l][c] = (WALL | (int)ch);
            }

      //@ assert mapDefined();
      assert mapDefined();

      board = new GBoard(windowName, numberOfLines*gelemCellsSize, numberOfColumns*gelemCellsSize, 25/gelemCellsSize, 25/gelemCellsSize, numberOfLayers);
      createGelems();
      for(int l = 0; l < numberOfLines; l++)
         for(int c = 0; c < numberOfColumns; c++)
            board.draw(fetchGelem(l, c), l*gelemCellsSize, c*gelemCellsSize, 0);
   }

   /**
    * Maze's number of lines.
    */
   public final int numberOfLines;

   /**
    * Maze's number of columns.
    */
   public final int numberOfColumns;

   /**
    * Maze's GBoard object.
    */
   public final GBoard board;

   /**
    * Is {@code (line,column)} a position inside the rectangle defined by the maze?
    *
    * @param line  line number
    * @param column  column number
    * @return {@code boolean} true if position is valid, otherwise it returns false
    */
   public /*@ pure @*/ boolean validPosition(int line, int column)
   {
      return line >= 0 && line < numberOfLines && column >= 0 && column < numberOfColumns;
   }

   /**
    * Is {@code (line,column)} a position outside the maze?
    *
    *  <P><B>requires</B>: {@code validPosition(line, column)}
    *
    * @param line  line number
    * @param column  column number
    * @return {@code boolean} true if it is an outside position, otherwise it returns false
    */
   //@ requires validPosition(line, column);
   public /*@ pure @*/ boolean isOutside(int line, int column)
   {
      assert validPosition(line, column): "invalid position: ("+line+","+column+")";

      return map[line][column] == OUTSIDE;
   }

   /**
    * Is character {@code c} a road symbol?
    *
    * @param c  character symbol
    * @return {@code boolean} true if is a road symbol, otherwise it returns false (meaning that it is a wall symbol)
    */
   public /*@ pure @*/ boolean isRoadSymbol(char c)
   {
      boolean result = (c == ' ');

      if (!result && roadSymbols != null)
         result = symbolExists(c, roadSymbols);

      return result;
   }

   /**
    * Is character {@code c} a wall symbol?
    *
    * @param c  character symbol
    * @return {@code boolean} true if is a wall symbol, otherwise it returns false (meaning that it is a road symbol)
    */
   public /*@ pure @*/ boolean isWallSymbol(char c)
   {
      return !isRoadSymbol(c);
   }

   /**
    * Is {@code (line,column)} a road position?
    *
    *  <P><B>requires</B>: {@code validPosition(line, column)}
    *
    * @param line  line number
    * @param column  column number
    * @return {@code boolean} true if it is a road position, otherwise it returns false
    */
   //@ requires validPosition(line, column);
   public /*@ pure @*/ boolean isRoad(int line, int column)
   {
      assert validPosition(line, column): "invalid position: ("+line+","+column+")";

      return (map[line][column] & 0xFF0000) == ROAD;
   }

   /**
    * Is {@code (line,column)} a wall position?
    *
    *  <P><B>requires</B>: {@code validPosition(line, column)}
    *
    * @param line  line number
    * @param column  column number
    * @return {@code boolean} true if it is a wall position, otherwise it returns false
    */
   //@ requires validPosition(line, column);
   public /*@ pure @*/ boolean isWall(int line, int column)
   {
      assert validPosition(line, column): "invalid position: ("+line+","+column+")";

      return (map[line][column] & 0xFF0000) == WALL;
   }

   /**
    * Get the road symbol at position {@code (line,column)}.
    *
    *  <P><B>requires</B>: {@code isRoad(line, column)}
    *
    * @param line  line number
    * @param column  column number
    * @return {@code char} road symbol
    */
   //@ requires isRoad(line, column);
   public /*@ pure @*/ char roadSymbol(int line, int column)
   {
      assert isRoad(line, column): "position ("+line+","+column+") is not a road";

      return (char)(map[line][column] & 0xFFFF);
   }

   /**
    * Get the wall symbol at position {@code (line,column)}.
    *
    *  <P><B>requires</B>: {@code isWall(line, column)}
    *
    * @param line  line number
    * @param column  column number
    * @return {@code char} wall symbol
    */
   //@ requires isWall(line, column);
   public /*@ pure @*/ char wallSymbol(int line, int column)
   {
      assert isWall(line, column): "position ("+line+","+column+") is not a wall";

      return (char)(map[line][column] & 0xFFFF);
   }

   /**
    * Get all the external road symbol's positions at the maze.
    *
    *  <P><B>requires</B>: {@code isRoadSymbol(roadSymbol) && roadSymbol != ' '}
    *
    *  <P><B>ensures</B>: {@code \result != null}
    *
    * @param roadSymbol  character road symbol
    * @return {@code Point[]} array of positions
    */
   //@ requires isRoadSymbol(roadSymbol) && roadSymbol != ' ';
   //@ ensures \result != null;
   public Point[] roadSymbolPositions(char roadSymbol)
   {
      assert isRoadSymbol(roadSymbol) && roadSymbol != ' ': "char \'"+roadSymbol+"\' is not an external road symbol";

      Point[] result;

      int n = 0;
      for(int i = 0; i < roadSymbolsPositions.length; i++)
         if (roadSymbol(roadSymbolsPositions[i].y, roadSymbolsPositions[i].x) == roadSymbol)
            n++;
      result = new Point[n];
      n = 0;
      for(int i = 0; i < roadSymbolsPositions.length; i++)
         if (roadSymbol(roadSymbolsPositions[i].y, roadSymbolsPositions[i].x) == roadSymbol)
            result[n++] = new Point(roadSymbolsPositions[i]);

      return result;
   }

   /**
    * Get all the symbol's positions at the maze (applies both to wall and road symbols including space).
    *
    *  <P><B>ensures</B>: {@code \result != null}
    *
    * @param symbol  character symbol
    * @return {@code Point[]} array of positions
    */
   //@ ensures \result != null;
   public Point[] symbolPositions(char symbol)
   {
      Point[] result;

      int n = 0;
      for(int l = 0; l < numberOfLines; l++)
         for(int c = 0; c < numberOfColumns; c++)
            if((char)(map[l][c] & 0xFFFF) == symbol)
               n++;
      result = new Point[n];
      int i = 0;
      for(int l = 0; i < n && l < numberOfLines; l++)
         for(int c = 0; i < n && c < numberOfColumns; c++)
            if((char)(map[l][c] & 0xFFFF) == symbol)
               result[i++] = new Point(c, l);

      return result;
   }
   
   /**
    * Defines the road symbol at position {@code (line,column)}.
    *
    *  <P><B>requires</B>: {@code isRoad(line, column)}
    * <BR><B>requires</B>: {@code isRoadSymbol(roadSymbol)}
    *
    *  <P><B>ensures</B>: {@code isRoad(line, column)}
    * <BR><B>ensures</B>: {@code roadSymbol(line, column) == roadSymbol}
    *
    * @param line  line number
    * @param column  column number
    * @param roadSymbol  character road symbol
    */
   //@ requires isRoad(line, column);
   //@ requires isRoadSymbol(roadSymbol);
   //@ ensures isRoad(line, column);
   //@ ensures roadSymbol(line, column) == roadSymbol;
   public /*@ pure @*/ void putRoadSymbol(int line, int column, char roadSymbol)
   {
      assert isRoad(line, column): "position ("+line+","+column+") is not a road";
      assert isRoadSymbol(roadSymbol): "char \'"+roadSymbol+"\' is not a road symbol";

      if (isRoadSymbolAttachToGelem(roadSymbol(line, column)))
         eraseGelem(gelemAttachedToRoadSymbol(roadSymbol(line, column)), line, column);
      if (existsRoadSymbolPosition(line, column))
         removeRoadSymbolPosition(line, column);
      map[line][column] = (map[line][column] & 0xFF0000) | (int)roadSymbol;
      if (isRoadSymbolAttachToGelem(roadSymbol))
         drawGelem(gelemAttachedToRoadSymbol(roadSymbol), line, column);
      if (roadSymbol != ' ')
         addRoadSymbolPosition(line, column);
   }
   
   /**
    * Defines the road symbol at position {@code (line,column)}.
    *
    *  <P><B>requires</B>: {@code isRoad(line, column)}
    * <BR><B>requires</B>: {@code isRoadSymbol(roadSymbol)}
    *
    *  <P><B>ensures</B>: {@code isRoad(line, column)}
    * <BR><B>ensures</B>: {@code roadSymbol(line, column) == roadSymbol}
    *
    * @param line  line number
    * @param column  column number
    * @param roadSymbol  character road symbol
    */
   //@ requires isRoad(line, column);
   //@ requires isRoadSymbol(roadSymbol);
   //@ ensures isRoad(line, column);
   //@ ensures roadSymbol(line, column) == roadSymbol;
   public /*@ pure @*/ void putRoadSymbol(int line, int column, Gelem gelem)
   {
      assert isRoad(line, column): "position ("+line+","+column+") is not a road";

      drawGelem(gelem, line, column);
   }
   
   /**
    * Defines the wall symbol at position {@code (line,column)}.
    *
    *  <P><B>requires</B>: {@code isWall(line, column)}
    * <BR><B>requires</B>: {@code isWallSymbol(wallSymbol)}
    *
    *  <P><B>ensures</B>: {@code isWall(line, column)}
    * <BR><B>ensures</B>: {@code wallSymbol(line, column) == wallSymbol}
    *
    * @param line  line number
    * @param column  column number
    * @param wallSymbol  character wall symbol
    */
   //@ requires isWall(line, column);
   //@ requires isWallSymbol(wallSymbol);
   //@ ensures isWall(line, column);
   //@ ensures wallSymbol(line, column) == wallSymbol;
   public /*@ pure @*/ void putWallSymbol(int line, int column, char wallSymbol)
   {
      assert isWall(line, column): "position ("+line+","+column+") is not a wall";
      assert isWallSymbol(wallSymbol): "char \'"+wallSymbol+"\' is not a wall symbol";

      map[line][column] = (map[line][column] & 0xFF0000) | (int)wallSymbol;
   }

   /**
    * Is road symbol attached to a gelem?.
    *
    *  <P><B>requires</B>: {@code isRoadSymbol(roadSymbol)}
    *
    * @param roadSymbol  character road symbol
    * @return {@code boolean} true if is attached, otherwise it returns false
    */
   //@ requires isRoadSymbol(roadSymbol);
   public /*@ pure @*/ boolean isRoadSymbolAttachToGelem(char roadSymbol)
   {
      assert isRoadSymbol(roadSymbol): "char \'"+roadSymbol+"\' is not a road symbol";

      int i = indexOfSymbol(roadSymbol, roadSymbols);

      return roadSymbolGelems[i] != null;
   }

   /**
    * Get Gelem attached to road symbol.
    *
    *  <P><B>requires</B>: {@code isRoadSymbol(roadSymbol)}
    * <BR><B>requires</B>: {@code isRoadSymbolAttachToGelem(roadSymbol)}
    *
    * @param roadSymbol  character road symbol
    * @return {@code Gelem} gelem's object
    */
   //@ requires isRoadSymbol(roadSymbol);
   //@ requires isRoadSymbolAttachToGelem(roadSymbol);
   public /*@ pure @*/ Gelem gelemAttachedToRoadSymbol(char roadSymbol)
   {
      assert isRoadSymbol(roadSymbol): "char \'"+roadSymbol+"\' is not a road symbol";
      assert isRoadSymbolAttachToGelem(roadSymbol): "char \'"+roadSymbol+"\' is not attached to a gelem";

      int i = indexOfSymbol(roadSymbol, roadSymbols);

      return roadSymbolGelems[i];
   }

   /**
    * Attach a gelem to a road symbol.
    *
    *  <P><B>requires</B>: {@code isRoadSymbol(roadSymbol)}
    * <BR><B>requires</B>: {@code gelem != null}
    * <BR><B>requires</B>: {@code !isRoadSymbolAttachToGelem(roadSymbol)}
    *
    *  <P><B>ensures</B>: {@code isRoadSymbolAttachToGelem(roadSymbol)}
    *
    * @param roadSymbol  character road symbol
    * @param gelem  gelem object
    */
   //@ requires isRoadSymbol(roadSymbol);
   //@ requires gelem != null;
   //@ requires !isRoadSymbolAttachToGelem(roadSymbol);
   //@ ensures isRoadSymbolAttachToGelem(roadSymbol);
   public void attachGelemToRoadSymbol(char roadSymbol, Gelem gelem)
   {
      assert isRoadSymbol(roadSymbol): "char \'"+roadSymbol+"\' is not a road symbol";
      assert gelem != null: "null gelem!";
      assert !isRoadSymbolAttachToGelem(roadSymbol): "char \'"+roadSymbol+"\' is already attached to a gelem";

      int i = indexOfSymbol(roadSymbol, roadSymbols);
      roadSymbolGelems[i] = gelem;
      drawAllRoadGelem(roadSymbol, gelem);
   }

   /**
    * Detach a gelem to a road symbol.
    *
    *  <P><B>requires</B>: {@code isRoadSymbol(roadSymbol)}
    * <BR><B>requires</B>: {@code isRoadSymbolAttachToGelem(roadSymbol)}
    *
    *  <P><B>ensures</B>: {@code !isRoadSymbolAttachToGelem(roadSymbol)}
    *
    * @param roadSymbol  character road symbol
    */
   //@ requires isRoadSymbol(roadSymbol);
   //@ requires isRoadSymbolAttachToGelem(roadSymbol);
   //@ ensures !isRoadSymbolAttachToGelem(roadSymbol);
   public void detachGelemToRoadSymbol(char roadSymbol, Gelem gelem)
   {
      assert isRoadSymbol(roadSymbol): "char \'"+roadSymbol+"\' is not a road symbol";
      assert isRoadSymbolAttachToGelem(roadSymbol): "char \'"+roadSymbol+"\' is not attached to a gelem";

      int i = indexOfSymbol(roadSymbol, roadSymbols);
      eraseAllRoadGelem(roadSymbol, roadSymbolGelems[i]);
      roadSymbolGelems[i] = null;
   }

   /**
    * Is wall symbol attached to a gelem?.
    *
    *  <P><B>requires</B>: {@code isWallSymbol(wallSymbol)}
    *
    * @param wallSymbol  character wall symbol
    * @return {@code boolean} true if is attached, otherwise it returns false
    */
   //@ requires isWallSymbol(wallSymbol);
   public /*@ pure @*/ boolean isWallSymbolAttachToGelem(char wallSymbol)
   {
      assert isWallSymbol(wallSymbol): "char \'"+wallSymbol+"\' is not a wall symbol";

      int i = indexOfSymbol(wallSymbol, wallSymbols);

      return wallSymbolGelems[i] != null;
   }

   /**
    * Get Gelem attached to wall symbol.
    *
    *  <P><B>requires</B>: {@code isWallSymbol(wallSymbol)}
    * <BR><B>requires</B>: {@code isWallSymbolAttachToGelem(wallSymbol)}
    *
    * @param wallSymbol  character wall symbol
    * @return {@code Gelem} gelem's object
    */
   //@ requires isWallSymbol(wallSymbol);
   //@ requires isWallSymbolAttachToGelem(wallSymbol);
   public /*@ pure @*/ Gelem gelemAttachedToWallSymbol(char wallSymbol)
   {
      assert isWallSymbol(wallSymbol): "char \'"+wallSymbol+"\' is not a wall symbol";
      assert isWallSymbolAttachToGelem(wallSymbol): "char \'"+wallSymbol+"\' is not attached to a gelem";

      int i = indexOfSymbol(wallSymbol, wallSymbols);

      return wallSymbolGelems[i];
   }

   /**
    * Attach a gelem to a wall symbol.
    *
    *  <P><B>requires</B>: {@code isWallSymbol(wallSymbol)}
    * <BR><B>requires</B>: {@code gelem != null}
    * <BR><B>requires</B>: {@code !isWallSymbolAttachToGelem(wallSymbol)}
    *
    *  <P><B>ensures</B>: {@code isWallSymbolAttachToGelem(wallSymbol)}
    *
    * @param wallSymbol  character wall symbol
    * @param gelem  gelem object
    */
   //@ requires isWallSymbol(wallSymbol);
   //@ requires gelem != null;
   //@ requires !isWallSymbolAttachToGelem(wallSymbol);
   //@ ensures isWallSymbolAttachToGelem(wallSymbol);
   public void attachGelemToWallSymbol(char wallSymbol, Gelem gelem)
   {
      assert isWallSymbol(wallSymbol): "char \'"+wallSymbol+"\' is not a wall symbol";
      assert gelem != null: "null gelem!";
      assert !isWallSymbolAttachToGelem(wallSymbol): "char \'"+wallSymbol+"\' is already attached to a gelem";

      int i = indexOfSymbol(wallSymbol, wallSymbols);
      wallSymbolGelems[i] = gelem;
      drawAllWallGelem(wallSymbol, gelem);
   }

   /**
    * Detach a gelem to a wall symbol.
    *
    *  <P><B>requires</B>: {@code isWallSymbol(wallSymbol)}
    * <BR><B>requires</B>: {@code isWallSymbolAttachToGelem(wallSymbol)}
    *
    *  <P><B>ensures</B>: {@code !isWallSymbolAttachToGelem(wallSymbol)}
    *
    * @param wallSymbol  character wall symbol
    */
   //@ requires isWallSymbol(wallSymbol);
   //@ requires isWallSymbolAttachToGelem(wallSymbol);
   //@ ensures !isWallSymbolAttachToGelem(wallSymbol);
   public void detachGelemToWallSymbol(char wallSymbol, Gelem gelem)
   {
      assert isWallSymbol(wallSymbol): "char \'"+wallSymbol+"\' is not a wall symbol";
      assert isWallSymbolAttachToGelem(wallSymbol): "char \'"+wallSymbol+"\' is not attached to a gelem";

      int i = indexOfSymbol(wallSymbol, wallSymbols);
      eraseAllWallGelem(wallSymbol, wallSymbolGelems[i]);
      wallSymbolGelems[i] = null;
   }

   protected static boolean symbolExists(char symbol, char[] array)
   {
      assert array != null;

      return indexOfSymbol(symbol, array) < array.length;
   }

   protected static int indexOfSymbol(char symbol, char[] array)
   {
      assert array != null;

      int result;
      for(result = 0; result < array.length && array[result] != symbol; result++)
         ;
      return result;
   }

   protected void drawGelem(Gelem gelem, int line, int column)
   {
      board.draw(gelem, line*gelemCellsSize, column*gelemCellsSize, 1); // layer 1
   }

   protected void eraseGelem(Gelem gelem, int line, int column)
   {
      board.erase(gelem, line*gelemCellsSize, column*gelemCellsSize, 1); // layer 1
   }

   protected void drawAllRoadGelem(char roadSymbol, Gelem gelem)
   {
      for(int l = 0; l < numberOfLines; l++)
         for(int c = 0; c < numberOfColumns; c++)
            if (isRoad(l, c) && roadSymbol(l, c) == roadSymbol)
               drawGelem(gelem, l, c);
   }

   protected void eraseAllRoadGelem(char roadSymbol, Gelem gelem)
   {
      for(int l = 0; l < numberOfLines; l++)
         for(int c = 0; c < numberOfColumns; c++)
            if (isRoad(l, c) && roadSymbol(l, c) == roadSymbol)
               eraseGelem(gelem, l, c);
   }

   protected void drawAllWallGelem(char wallSymbol, Gelem gelem)
   {
      for(int l = 0; l < numberOfLines; l++)
         for(int c = 0; c < numberOfColumns; c++)
            if (isWall(l, c) && wallSymbol(l, c) == wallSymbol)
               drawGelem(gelem, l, c);
   }

   protected void eraseAllWallGelem(char wallSymbol, Gelem gelem)
   {
      for(int l = 0; l < numberOfLines; l++)
         for(int c = 0; c < numberOfColumns; c++)
            if (isWall(l, c) && wallSymbol(l, c) == wallSymbol)
               eraseGelem(gelem, l, c);
   }

   protected boolean isRoadUnbounded(int line, int column)
   {
      boolean result = !validPosition(line, column);
      if (validPosition(line, column))
         result = isRoad(line, column) || isOutside(line, column);
      return result;
   }

   protected void createGelems()
   {
      if (gelems == null)
      {
         gelems = new LabyrinthGelem[0x200];
         for(int i = 0x000; i < 0x200; i++)
            gelems[i] =  new LabyrinthGelem(i, gelemCellsSize, gelemCellsSize);
      }
   }

   protected Gelem fetchGelem(int line, int column)
   {
      int result = 0x000;
      if (isRoadUnbounded(line, column))
         result |= LabyrinthGelem.ROAD;
      if (isRoadUnbounded(line-1, column-1))
         result |= LabyrinthGelem.ROAD_NW;
      if (isRoadUnbounded(line-1, column))
         result |= LabyrinthGelem.ROAD_N;
      if (isRoadUnbounded(line-1, column+1))
         result |= LabyrinthGelem.ROAD_NE;
      if (isRoadUnbounded(line, column-1))
         result |= LabyrinthGelem.ROAD_W;
      if (isRoadUnbounded(line, column+1))
         result |= LabyrinthGelem.ROAD_E;
      if (isRoadUnbounded(line+1, column-1))
         result |= LabyrinthGelem.ROAD_SW;
      if (isRoadUnbounded(line+1, column))
         result |= LabyrinthGelem.ROAD_S;
      if (isRoadUnbounded(line+1, column+1))
         result |= LabyrinthGelem.ROAD_SE;
      return gelems[result];
   }

   protected static String[] loadMap(String filename)
   {
      assert validMapFile(filename): "Path \""+filename+"\" is not valid";

      String[] result = null;
      try
      {
         File fin = new File(filename);
         Scanner scin = new Scanner(fin);
         String[] lines = new String[(int)Math.sqrt(fin.length())]; // heuristic (square map)
         int nLines = 0;
         while(scin.hasNextLine())
         {
            if (nLines == lines.length)
            {
               String[] copy = new String[lines.length + 10];
               arraycopy(lines, 0, copy, 0, lines.length);
               lines = copy;
            }
            lines[nLines] = scin.nextLine();
            nLines++;
         }
         scin.close();
         result = new String[nLines];
         for(int l = 0; l < nLines; l++)
            result[l] = lines[l];
      }
      catch(IOException e)
      {
         result = null;
      }

      return result;
   }

   protected void fillOutside(String[] maze, int line, int column)
   {
      if (validPosition(line, column) && map[line][column] == UNDEFINED)
      {
         if (column >= maze[line].length() || isRoadSymbol(maze[line].charAt(column)))
         {
            map[line][column] = OUTSIDE;
            fillOutside(maze, line+1, column+0);
            fillOutside(maze, line-1, column+0);
            fillOutside(maze, line+0, column+1);
            fillOutside(maze, line+0, column-1);
         }
      }
   }

   protected /*@ pure @*/ boolean mapDefined()
   {
      boolean result = true;
      for(int l = 0; result && l < numberOfLines; l++)
         for(int c = 0; result && c < numberOfColumns; c++)
            result = (map[l][c] != UNDEFINED);
      return result;
   }

   protected boolean existsRoadSymbolPosition(int line, int column)
   {
      assert isRoad(line, column);

      boolean result = false;
      for(int i = 0; !result && i < roadSymbolsPositions.length; i++)
         result = roadSymbolsPositions[i].y == line && roadSymbolsPositions[i].x == column;
      return result;
   }

   protected void addRoadSymbolPosition(int line, int column)
   {
      assert !existsRoadSymbolPosition(line, column);

      Point[] nrsp = new Point[roadSymbolsPositions.length+1];
      arraycopy(roadSymbolsPositions, 0, nrsp, 0, roadSymbolsPositions.length);
      nrsp[roadSymbolsPositions.length] = new Point(column, line);
      roadSymbolsPositions = nrsp;
   }

   protected void removeRoadSymbolPosition(int line, int column)
   {
      assert existsRoadSymbolPosition(line, column);

      Point[] nrsp = new Point[roadSymbolsPositions.length-1];
      int i;
      for(i = 0; i < roadSymbolsPositions.length &&
                 (roadSymbolsPositions[i].y != line || roadSymbolsPositions[i].x != column); i++)
         ;
      arraycopy(roadSymbolsPositions, 0, nrsp, 0, i);
      arraycopy(roadSymbolsPositions, i+1, nrsp, i, roadSymbolsPositions.length-i-1);
      roadSymbolsPositions = nrsp;
   }

   protected int gelemCellsSize;
   protected final char[] roadSymbols;
   protected final char[] wallSymbols;
   protected final Gelem[] roadSymbolGelems;
   protected final Gelem[] wallSymbolGelems;
   protected Point[] roadSymbolsPositions;
   protected int[][] map;
   protected static LabyrinthGelem[] gelems = null;

   protected static final int UNDEFINED = -1;
   protected static final int OUTSIDE = 0x000000;
   protected static final int ROAD = 0x010000; // mask with road char
   protected static final int WALL = 0x020000; // mask with wall char
}

