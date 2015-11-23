package pt.ua.gboard.games;

import pt.ua.gboard.*;

public class DeckOfCards
{
   public DeckOfCards(GBoard gboard)
   {
      this.gboard = gboard;
   }

   public final GBoard gboard;

   public static Card card(Suits suits, Rank rank)
   {
      return deck[suits.ordinal()][rank.ordinal()];
   }

   public Gelem cardGelem(Suits suits, Rank rank)
   {
      return card(suits, rank).getGelem(gboard);
   }

   public Gelem getBackBlueGelem()
   {
      return Card.getBackBlueGelem(gboard);
   }

   public Gelem getBackRedGelem()
   {
      return Card.getBackRedGelem(gboard);
   }

   public static Card random()
   {
      return deck[(int)(Math.random()*Suits.values().length)][(int)(Math.random()*Rank.values().length)];
   }

   protected static Card[][] deck = null;

   static
   {
      deck = new Card[Suits.values().length][Rank.values().length];

      for(Suits s: Suits.values())
         for(Rank r: Rank.values())
            deck[s.ordinal()][r.ordinal()] = new Card(s, r);
   }
}

