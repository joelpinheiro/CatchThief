package pt.ua.gboard.games;

import pt.ua.gboard.*;

public class ChessPieces
{
   public ChessPieces(GBoard gboard)
   {
      this.gboard = gboard;
   }

   public final GBoard gboard;

   public static ChessPiece piece(ChessPieceType type)
   {
      return pieces[type.ordinal()];
   }

   public Gelem pieceGelem(ChessPieceType type)
   {
      return piece(type).getGelem(gboard);
   }

   public static ChessPiece random()
   {
      return pieces[(int)(Math.random()*ChessPieceType.values().length)];
   }

   protected static ChessPiece[] pieces = null;

   static
   {
      pieces = new ChessPiece[ChessPieceType.values().length];

      for(ChessPieceType t: ChessPieceType.values())
         pieces[t.ordinal()] = new ChessPiece(t);
   }
}

