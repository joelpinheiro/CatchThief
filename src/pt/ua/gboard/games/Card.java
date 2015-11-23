package pt.ua.gboard.games;

import pt.ua.gboard.*;
import java.net.URL;

public class Card
{
   public Card(Suits suits, Rank rank)
   {
      this.suits = suits;
      this.rank = rank;
   }

   public final Suits suits;
   public final Rank rank;

   public boolean equalSuits(Card other)
   {
      return suits == other.suits;
   }

   public boolean equalRank(Card other)
   {
      return rank == other.rank;
   }

   public boolean equal(Card other)
   {
      return equalSuits(other) && equalRank(other);
   }

   public Gelem getGelem(GBoard gboard)
   {
      initialize(gboard);

      return gelems[suits.ordinal()][rank.ordinal()];
   }

   public static Gelem getBackBlueGelem(GBoard gboard)
   {
      initialize(gboard);

      return backBlue;
   }

   public static Gelem getBackRedGelem(GBoard gboard)
   {
      initialize(gboard);

      return backRed;
   }

   protected static String path = "pt/ua/gboard/games/resources/cards/";

   protected static String[] suitsFilename = {"s", "h", "d", "c"};

   protected static String[] rankFilenames = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "j", "q", "k"};

   protected static synchronized void initialize(GBoard gboard)
   {
      if (gelems == null)
      {
         URL imgURL;
         ClassLoader cl = gboard.getClass().getClassLoader();
         gelems = new Gelem[Suits.values().length][Rank.values().length];
         for(Suits s: Suits.values())
            for(Rank r: Rank.values())
            {
               imgURL = cl.getResource(path+suitsFilename[s.ordinal()]+rankFilenames[r.ordinal()]+".png");
               gelems[s.ordinal()][r.ordinal()] = new ImageGelem(imgURL, gboard, 90);
            }
         imgURL = cl.getResource(path+"backBlue.png");
         backBlue = new ImageGelem(imgURL, gboard, 90);
         imgURL = cl.getResource(path+"backRed.png");
         backRed = new ImageGelem(imgURL, gboard, 90);
      }
   }

   protected static Gelem[][] gelems = null;
   protected static Gelem backBlue;
   protected static Gelem backRed;
}

