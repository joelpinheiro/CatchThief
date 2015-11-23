package pt.ua.gboard.games;

import pt.ua.gboard.*;
import java.net.URL;

public class ChessPiece
{
   public ChessPiece(ChessPieceType type)
   {
      this.type = type;
   }

   public final ChessPieceType type;

   public boolean isWhite()
   {
      return type.ordinal() >= ChessPieceType.WHITE_KING.ordinal() && type.ordinal() <= ChessPieceType.WHITE_PAWN.ordinal();
   }

   public boolean isBlack()
   {
      return type.ordinal() >= ChessPieceType.BLACK_KING.ordinal() && type.ordinal() <= ChessPieceType.BLACK_PAWN.ordinal();
   }

   public boolean isKing()
   {
      return type == ChessPieceType.WHITE_KING || type == ChessPieceType.BLACK_KING;
   }

   public boolean isQueen()
   {
      return type == ChessPieceType.WHITE_QUEEN || type == ChessPieceType.BLACK_QUEEN;
   }

   public boolean isRook()
   {
      return type == ChessPieceType.WHITE_ROOK || type == ChessPieceType.BLACK_ROOK;
   }

   public boolean isBishop()
   {
      return type == ChessPieceType.WHITE_BISHOP || type == ChessPieceType.BLACK_BISHOP;
   }

   public boolean isKnight()
   {
      return type == ChessPieceType.WHITE_KNIGHT || type == ChessPieceType.BLACK_KNIGHT;
   }

   public boolean isPawn()
   {
      return type == ChessPieceType.WHITE_PAWN || type == ChessPieceType.BLACK_PAWN;
   }

   public boolean equalColor(ChessPiece other)
   {
      return isWhite() == other.isWhite();
   }

   public boolean equalType(ChessPiece other)
   {
      return isKing() == other.isKing() ||
             isQueen() == other.isQueen() ||
             isRook() == other.isRook() ||
             isBishop() == other.isBishop() ||
             isKnight() == other.isKnight() ||
             isPawn() == other.isPawn();
   }

   public boolean equal(ChessPiece other)
   {
      return type == other.type;
   }

   public Gelem getGelem(GBoard gboard)
   {
      initialize(gboard);

      return gelems[type.ordinal()];
   }

   protected static String path = "pt/ua/gboard/games/resources/chess/";

   protected static String[] typesFilenames = {
      "black-king", "black-queen", "black-rook", "black-bishop", "black-knight", "black-pawn",
      "white-king", "white-queen", "white-rook", "white-bishop", "white-knight", "white-pawn"
   };

   protected static synchronized void initialize(GBoard gboard)
   {
      if (gelems == null)
      {
         URL imgURL;
         ClassLoader cl = gboard.getClass().getClassLoader();
         gelems = new Gelem[ChessPieceType.values().length];
         for(ChessPieceType t: ChessPieceType.values())
         {
            imgURL = cl.getResource(path+typesFilenames[t.ordinal()]+".png");
            gelems[t.ordinal()] = new ImageGelem(imgURL, gboard, 90);
         }
      }
   }

   protected static Gelem[] gelems = null;
}

