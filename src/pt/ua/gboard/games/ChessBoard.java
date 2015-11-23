package pt.ua.gboard.games;

import java.awt.*;
import pt.ua.gboard.*;

/**
 * A GBoard based chess board.
 *
 *  <P>Board coordinates can use a pair of integers or a classical chess {@code String} position.
 * <BR>Top-left coordinate is {@code (0,0)} or {@code AN} ({@code N} is the board's dimension).
 *
 *  <P><B>invariant</B>: {@code N >= 1}
 * <BR><B>invariant</B>: {@code board != null}
 *
 * <P>This class follows DbC(tm) methodology.
 * Where possible, contracts are implement with JML and native's Java assert.
 *
 * @author Miguel Oliveira e Silva (mos@ua.pt)
 */
public class ChessBoard
{
   /**
    * Creates a {@code NxN} chess board.
    *
    *  <P><B>requires</B>: {@code N > 0}
    *
    * @param N  board dimensions
    */
   //@ requires N > 0;
   public ChessBoard(int N)
   {
      assert N > 0: "invalid chess board dimensions: "+N;

      this.N = N;
      board = new GBoard("Chess Board", N+2, N+2, 60, 60, 2);
      pieces = new ChessPieces(board);

      black = new FilledGelem(Color.blue, 100);
      white = new FilledGelem(Color.lightGray, 100);
      selected = new FilledGelem(Color.red, 90);
      selectedPositions = new String[0];

      Gelem[] numbers = new Gelem[N];
      Gelem[] letters = new Gelem[N];
      for(int i = 0; i < N; i++)
      {
         numbers[i] = new StringGelem(""+(N-i), Color.black);
         letters[i] = new StringGelem(""+(char)((int)'a'+i), Color.black);
      }

      for(int i = 1; i <= N; i++)
      {
         board.draw(numbers[i-1], i, 0, 0);
         board.draw(numbers[i-1], i, N+1, 0);
         board.draw(letters[i-1], 0, i, 0);
         board.draw(letters[i-1], N+1, i, 0);
      }

      for(int l = 1; l <= N; l++)
      {
         for(int c = 1; c <= N; c++)
         {
            if (((l+c) % 2) == 0)
               board.draw(white, l, c, 0);
            else
               board.draw(black, l, c, 0);
         }
      }

      state = new ChessPiece[N][N]; // initialized with null entries
   }

   /**
    * Initializes a standard {@code 8x8} chess board.
    *
    *  <P><B>requires</B>: {@code N == 8}
    */
   //@ requires N == 8;
   public void initializeChessGame()
   {
      assert N == 8: "classical chess board dimensions required: "+N;

      for(int i = 0; i < 8; i++)
      {
         put(ChessPieceType.BLACK_PAWN, 1, i);
         put(ChessPieceType.WHITE_PAWN, 6, i);
      }
      put(ChessPieceType.BLACK_KING,   0, 4);
      put(ChessPieceType.BLACK_QUEEN,  0, 3);
      put(ChessPieceType.BLACK_ROOK,   0, 0);
      put(ChessPieceType.BLACK_ROOK,   0, 7);
      put(ChessPieceType.BLACK_KNIGHT, 0, 1);
      put(ChessPieceType.BLACK_KNIGHT, 0, 6);
      put(ChessPieceType.BLACK_BISHOP, 0, 2);
      put(ChessPieceType.BLACK_BISHOP, 0, 5);
      put(ChessPieceType.WHITE_KING,   7, 4);
      put(ChessPieceType.WHITE_QUEEN,  7, 3);
      put(ChessPieceType.WHITE_ROOK,   7, 0);
      put(ChessPieceType.WHITE_ROOK,   7, 7);
      put(ChessPieceType.WHITE_KNIGHT, 7, 1);
      put(ChessPieceType.WHITE_KNIGHT, 7, 6);
      put(ChessPieceType.WHITE_BISHOP, 7, 2);
      put(ChessPieceType.WHITE_BISHOP, 7, 5);
   }

   /**
    * Empties the board.
    */
   public void clear()
   {
      for(int l = 0; l < N; l++)
         for(int c = 0; c < N; c++)
            if (!emptyPosition(l, c))
               remove(l, c);
   }

   /**
    * Is {@code (line,column)} a valid board position?
    *
    * @param line  line number
    * @param column  column number
    * @return {@code boolean} true if position is valid, otherwise it returns false
    */
   public /*@ pure @*/ boolean validPosition(int line, int column)
   {
      return line >= 0 && line < N && column >= 0 && column < N;
   }

   /**
    * Is {@code pos} a valid board position?
    *
    * @param pos position text
    * @return {@code boolean} true if position is valid, false otherwise
    */
   public /*@ pure @*/ boolean validPosition(String pos)
   {
      boolean result = (pos != null);

      if (result)
         result = (N < 10 && pos.length() == 2 ||
               pos.length() >= 2 && pos.length() <= 3);

      char maxCol = (char)((int)'A' + N-1);
      if (result)
         result = Character.toUpperCase(pos.charAt(0)) >= 'A' &&
            Character.toUpperCase(pos.charAt(0)) <= maxCol;

      if (result)
         result = Character.isDigit(pos.charAt(1)) &&
            (pos.length() == 2 || Character.isDigit(pos.charAt(2)));
      if (result)
      {
         int v = Integer.decode(pos.substring(1));
         result = v >= 1 && v <= N;
      }
      return result;
   }

   /**
    * Converts integer position coordinates to a {@code String} position.
    *
    *  <P><B>requires</B>: {@code validPosition(line, column)}
    *
    *  <P><B>ensures</B>: {@code validPosition(\result)}
    * 
    * @param line line number
    * @param column column number
    * @return {@code String} position text
    */
   //@ requires validPosition(line, column);
   //@ ensures validPosition(\result);
   public /*@ pure @*/ String toPosition(int line, int column)
   {
      assert validPosition(line, column): "invalid position: ("+line+","+column+")";

      return "" + (char)('A'+(char)column) + Integer.toString(N-line);
   }

   /**
    * Converts a {@code String} position coordinate to a line number;
    *
    * <P><B>requires</B>: {@code validPosition(pos)}
    *
    *  <P><B>ensures</B>: {@code \result >= 0 && \result < N}
    * 
    * @param pos position text
    * @return {@code int} line number
    */
   //@ requires validPosition(pos);
   //@ ensures \result >= 0 && \result < N;
   public /*@ pure @*/ int line(String pos)
   {
      assert validPosition(pos): "invalid position: "+pos;

      return N-Integer.decode(pos.substring(1));
   }

   /**
    * Converts a {@code String} position coordinate to a column number;
    *
    * <P><B>requires</B>: {@code validPosition(pos)}
    *
    *  <P><B>ensures</B>: {@code \result >= 0 && \result < N}
    * 
    * @param pos position text
    * @return {@code int} column number
    */
   //@ requires validPosition(pos);
   //@ ensures \result >= 0 && \result < N;
   public /*@ pure @*/ int column(String pos)
   {
      assert validPosition(pos): "invalid position: "+pos;

      return (int)(Character.toUpperCase(pos.charAt(0)) - 'A');
   }

   /**
    * Is position {@code (line,column)} empty?
    *
    * <P><B>requires</B>: {@code validPosition(line, column)}
    *
    * @param line  line number
    * @param column  column number
    * @return {@code boolean} true if position is empty, otherwise it returns false
    */
   public /*@ pure @*/ boolean emptyPosition(int line, int column)
   {
      assert validPosition(line, column): "invalid position: ("+line+","+column+")";

      return state[line][column] == null;
   }

   /**
    * Is position {@code pos} empty?
    *
    * <P><B>requires</B>: {@code validPosition(pos)}
    *
    * @param pos position text
    * @return {@code boolean} true if position is empty, otherwise it returns false
    */
   public /*@ pure @*/ boolean emptyPosition(String pos)
   {
      assert validPosition(pos): "invalid position: "+pos;

      return emptyPosition(line(pos), column(pos));
   }

   /**
    * Puts a chess piece in the board.
    *
    *  <P><B>requires</B>: {@code emptyPosition(line, column)}
    *
    *  <P><B>ensures</B>: {@code !emptyPosition(line, column)}
    *
    * @param piece chess piece type
    * @param line line number
    * @param column column number
    */
   //@ requires emptyPosition(line, column);
   //@ ensures !emptyPosition(line, column);
   public void put(ChessPieceType piece, int line, int column)
   {
      assert emptyPosition(line, column): "position not empty: ("+line+","+column+")";

      board.draw(pieces.pieceGelem(piece),  line+1, column+1, 1);
      state[line][column] = ChessPieces.piece(piece);
   }

   /**
    * Puts a chess piece in the board.
    *
    *  <P><B>requires</B>: {@code emptyPosition(pos)}
    *
    *  <P><B>ensures</B>: {@code !emptyPosition(pos)}
    *
    * @param piece chess piece type
    * @param pos position text
    */
   //@ requires emptyPosition(pos);
   //@ ensures !emptyPosition(pos);
   public void put(ChessPieceType piece, String pos)
   {
      assert emptyPosition(pos): "position not empty: "+pos;

      put(piece, line(pos), column(pos));
   }

   /**
    * Puts a chess piece in the board.
    *
    *  <P><B>requires</B>: {@code emptyPosition(line, column)}
    *
    *  <P><B>ensures</B>: {@code !emptyPosition(line, column)}
    *
    * @param piece chess piece type
    * @param line line number
    * @param column column number
    */
   //@ requires emptyPosition(line, column);
   //@ ensures !emptyPosition(line, column);
   public void put(ChessPiece piece, int line, int column)
   {
      assert emptyPosition(line, column): "position not empty: ("+line+","+column+")";

      board.draw(piece.getGelem(board),  line+1, column+1, 1);
      state[line][column] = piece;
   }

   /**
    * Puts a chess piece in the board.
    *
    *  <P><B>requires</B>: {@code emptyPosition(pos)}
    *
    *  <P><B>ensures</B>: {@code !emptyPosition(pos)}
    *
    * @param piece chess piece type
    * @param pos position text
    */
   //@ requires emptyPosition(pos);
   //@ ensures !emptyPosition(pos);
   public void put(ChessPiece piece, String pos)
   {
      assert emptyPosition(pos): "position not empty: "+pos;

      put(piece, line(pos), column(pos));
   }

   /**
    * Observe a chess piece in the board.
    *
    *  <P><B>requires</B>: {@code !emptyPosition(line, column)}
    *
    * @param line line number
    * @param column column number
    * @return {@code ChessPiece} chess piece
    */
   //@ requires !emptyPosition(line, column);
   public ChessPiece get(int line, int column)
   {
      assert !emptyPosition(line, column): "position empty: ("+line+","+column+")";

      return state[line][column];
   }

   /**
    * Observe a chess piece in the board.
    *
    *  <P><B>requires</B>: {@code !emptyPosition(pos)}
    *
    * @param pos position text
    * @return {@code ChessPiece} chess piece
    */
   //@ requires !emptyPosition(pos);
   public ChessPiece get(String pos)
   {
      assert !emptyPosition(pos): "position empty: "+pos;

      return get(line(pos), column(pos));
   }

   /**
    * Removes a chess piece from the board.
    *
    *  <P><B>requires</B>: {@code !emptyPosition(line, column)}
    *
    *  <P><B>ensures</B>: {@code emptyPosition(line, column)}
    *
    * @param line line number
    * @param column column number
    */
   //@ requires !emptyPosition(line, column);
   //@ ensures emptyPosition(line, column);
   public void remove(int line, int column)
   {
      assert !emptyPosition(line, column): "empty position: ("+line+","+column+")";

      board.erase(line+1, column+1, 1, 1);
      //board.erase(board.topGelem(line+1, column+1), line+1, column+1, 1);
      state[line][column] = null;
   }

   /**
    * Removes a chess piece from the board.
    *
    *  <P><B>requires</B>: {@code !emptyPosition(pos)}
    *
    *  <P><B>ensures</B>: {@code emptyPosition(pos)}
    *
    * @param pos position text
    */
   //@ requires !emptyPosition(pos);
   //@ ensures emptyPosition(pos);
   public void remove(String pos)
   {
      assert !emptyPosition(pos): "empty position: "+pos;

      remove(line(pos), column(pos));
   }

   /**
    * Returns an array with the positions of a piece.
    *
    * @param piece piece type
    * @return {@code String[]} the positions array
    */
   public String[] piecePositions(ChessPieceType piece)
   {
      String[] result;

      int count = 0;
      for(int l = 0; l < N; l++)
         for(int c = 0; c < N; c++)
            if (state[l][c] != null && state[l][c].type == piece)
               count++;
      result = new String[count];
      count = 0;
      for(int l = 0; l < N; l++)
         for(int c = 0; c < N; c++)
            if (state[l][c] != null && state[l][c].type == piece)
               result[count++] = toPosition(l, c);

      return result;
   }

   /**
    * Moves a chess piece inside the board.
    *
    *  <P><B>requires</B>: {@code !emptyPosition(fromLine, fromColumn)}
    * <BR><B>requires</B>: {@code emptyPosition(toLine, toColumn)}
    *
    *  <P><B>ensures</B>: {@code emptyPosition(fromLine, fromColumn)}
    * <BR><B>ensures</B>: {@code !emptyPosition(toLine, toColumn)}
    *
    * @param fromLine source line number
    * @param fromColumn source column number
    * @param toLine destination line number
    * @param toColumn destination column number
    */
   //@ requires !emptyPosition(fromLine, fromColumn);
   //@ requires emptyPosition(toLine, toColumn);
   //@ ensures emptyPosition(fromLine, fromColumn);
   //@ ensures !emptyPosition(toLine, toColumn);
   public void move(int fromLine, int fromColumn, int toLine, int toColumn)
   {
      assert !emptyPosition(fromLine, fromColumn): "empty position: ("+fromLine+","+fromColumn+")";
      assert emptyPosition(toLine, toColumn): "position not empty: ("+toLine+","+toColumn+")";

      put(state[fromLine][fromColumn].type, toLine, toColumn);
      remove(fromLine, fromColumn);
   }

   /**
    * Moves a chess piece inside the board.
    *
    *  <P><B>requires</B>: {@code !emptyPosition(fromPos)}
    * <BR><B>requires</B>: {@code emptyPosition(toPos)}
    *
    *  <P><B>ensures</B>: {@code emptyPosition(fromPos)}
    * <BR><B>ensures</B>: {@code !emptyPosition(toPos)}
    *
    * @param fromPos source position text
    * @param toPos destination position text
    */
   //@ requires !emptyPosition(fromPos);
   //@ requires emptyPosition(toPos);
   //@ ensures emptyPosition(fromPos);
   //@ ensures !emptyPosition(toPos);
   public void move(String fromPos, String toPos)
   {
      assert !emptyPosition(fromPos): "empty position: "+fromPos;
      assert emptyPosition(toPos): "position not empty: "+toPos;

      move(line(fromPos), column(fromPos), line(toPos), column(toPos));
   }

   /**
    * Is position {@code (line,column)} selected?
    *
    *  <P><B>requires</B>: {@code validPosition(line, column)}
    *
    * @param line  line number
    * @param column  column number
    * @return {@code boolean} true if position is selected, otherwise it returns false
    */
   //@ requires validPosition(line, column);
   public /*@ pure @*/ boolean positionSelected(int line, int column)
   {
      assert validPosition(line, column): "invalid position: ("+line+","+column+")";

      return positionSelected(toPosition(line, column));
   }

   /**
    * Is position {@code pos} selected?
    *
    *  <P><B>requires</B>: {@code validPosition(pos)}
    *
    * @param pos position text
    * @return {@code boolean} true if position is selected, otherwise it returns false
    */
   //@ requires validPosition(pos);
   public /*@ pure @*/ boolean positionSelected(String pos)
   {
      assert validPosition(pos): "invalid position: "+pos;

      boolean result = false;

      for(int i = 0; !result && i < selectedPositions.length; i++)
         result = pos.equalsIgnoreCase(selectedPositions[i]);

      return result;
   }

   /**
    * Select position {@code (line,column)}.
    *
    *  <P><B>requires</B>: {@code !positionSelected(line, column)}
    *
    *  <P><B>ensures</B>: {@code positionSelected(line, column)}
    *
    * @param line  line number
    * @param column  column number
    */
   //@ requires !positionSelected(line, column);
   //@ ensures positionSelected(line, column);
   public void selectPosition(int line, int column)
   {
      assert !positionSelected(line, column): "position ("+line+","+column+") already selected";

      selectPosition(toPosition(line, column));
   }

   /**
    * Select position {@code pos}.
    *
    *  <P><B>requires</B>: {@code !positionSelected(pos)}
    *
    *  <P><B>ensures</B>: {@code positionSelected(pos)}
    *
    * @param pos position text
    */
   //@ requires !positionSelected(pos);
   //@ ensures positionSelected(pos);
   public void selectPosition(String pos)
   {
      assert validPosition(pos): "invalid position: "+pos;
      assert !positionSelected(pos): "position "+pos+" already selected";

      String[] strArr = new String[selectedPositions.length+1];
      System.arraycopy(selectedPositions, 0, strArr, 0, selectedPositions.length);
      strArr[selectedPositions.length] = pos;
      selectedPositions = strArr;
      board.draw(selected, line(pos)+1, column(pos)+1, 0);
   }

   /**
    * Unselect position {@code (line,column)}.
    *
    *  <P><B>requires</B>: {@code positionSelected(line, column)}
    *
    *  <P><B>ensures</B>: {@code !positionSelected(line, column)}
    *
    * @param line  line number
    * @param column  column number
    */
   //@ requires positionSelected(line, column);
   //@ ensures !positionSelected(line, column);
   public void unselectPosition(int line, int column)
   {
      assert positionSelected(line, column): "position ("+line+","+column+") not selected";

      unselectPosition(toPosition(line, column));
   }

   /**
    * Unelect position {@code pos}.
    *
    *  <P><B>requires</B>: {@code positionSelected(pos)}
    *
    *  <P><B>ensures</B>: {@code !positionSelected(pos)}
    *
    * @param pos position text
    */
   //@ requires positionSelected(pos);
   //@ ensures !positionSelected(pos);
   public void unselectPosition(String pos)
   {
      assert positionSelected(pos): "position "+pos+" not selected";

      String[] strArr = new String[selectedPositions.length-1];
      int i;
      for(i = 0; !pos.equalsIgnoreCase(selectedPositions[i]); i++)
         strArr[i] = selectedPositions[i];
      for(i++; i < selectedPositions.length; i++)
         strArr[i-1] = selectedPositions[i];
      selectedPositions = strArr;
      board.erase(selected, line(pos)+1, column(pos)+1, 0);
   }

   /**
    * Board dimensions.
    */
   public final int N;

   /**
    * Maze's GBoard object.
    */
   public final GBoard board;

   /**
    * Chess pieces.
    */
   public final ChessPieces pieces;

   protected Gelem black;
   protected Gelem white;
   protected Gelem selected;
   protected String[] selectedPositions;
   protected ChessPiece[][] state;
}

