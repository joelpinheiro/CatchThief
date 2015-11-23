package pt.ua.gboard;

import static java.lang.System.*;
import java.awt.*;

public abstract class MutableGelem extends Gelem
{
  /**
   * Constructs a new MutableGelem (1x1 cells).
   */
   public MutableGelem()
   {
      super();
   }

  /**
   * Constructs a new MutableGelem (numberOfLines x numberOfColumns cells).
   *
   *  <P><B>requires</B>: {@code numberOfLines >= 1 && numberOfColumns >= 1}
   * 
   * @param numberOfLines  number of lines
   * @param numberOfColumns  number of columns
   */
   //@ requires numberOfLines >= 1 && numberOfColumns >= 1;
   public MutableGelem(int numberOfLines, int numberOfColumns)
   {
      super(numberOfLines, numberOfColumns);
   }

   /**
    * Update (erase & draw) mutable gelem's state in GBoard.
    */

   protected void erase()
   {
      for(int i = 0; i < gboardList.length ; i++)
         gboardList[i].gboard.erase(this, gboardList[i].list);
   }

   protected void draw()
   {
      for(int i = 0; i < gboardList.length ; i++)
         gboardList[i].gboard.draw(this, gboardList[i].list);
   }

   // services exported to GBoard (approached by package visibility)!
   void registerDraw(GBoard gboard, int line, int column, int layer)
   {
      assert gboard != null: "Gboard object required!";
      assert !existsDraw(gboard, line, column, layer);

      int i;
      for(i = 0; i < gboardList.length && gboardList[i].gboard != gboard; i++)
         ;
      if (i == gboardList.length)
      {
         GBoardList[] aux = new GBoardList[gboardList.length+1];
         arraycopy(gboardList, 0, aux, 0, gboardList.length);
         aux[gboardList.length] = new GBoardList(gboard);
         gboardList = aux;
      }
      gboardList[i].add(line, column, layer);
   }

   void unregisterDraw(GBoard gboard, int line, int column, int layer)
   {
      assert gboard != null: "Gboard object required!";
      assert existsDraw(gboard, line, column, layer);

      int i;
      for(i = 0; gboardList[i].gboard != gboard; i++)
         ;
      gboardList[i].remove(line, column, layer);
      if (gboardList[i].list == null)
      {
         GBoardList[] aux = new GBoardList[gboardList.length-1];
         arraycopy(gboardList, 0, aux, 0, i);
         for(i++; i < gboardList.length ; i++)
            aux[i-1] = gboardList[i];
         gboardList = aux;
      }
   }

   boolean existsDraw(GBoard gboard, int line, int column, int layer)
   {
      assert gboard != null: "Gboard object required!";

      boolean result = false;

      int i;
      for(i = 0; !result && i < gboardList.length; i++)
         result = (gboardList[i].gboard == gboard);
      if (result)
         result = gboardList[i].list.exists(line, column, layer);
      return result;
   }

   class GBoardReg
   {
      private int line, column, layer;
      private GBoardReg next = null;

      GBoardReg(int line, int column, int layer)
      {
         this.line = line;
         this.column = column;
         this.layer = layer;
      }

      int line() { return line; }
      int column() { return column; }
      int layer() { return layer; }

      GBoardReg next()
      {
         return next;
      }

      boolean isEqual(int line, int column, int layer)
      {
         return line == this.line && column == this.column && layer == this.layer;
      }

      GBoardReg find(int line, int column, int layer)
      {
         GBoardReg result = null;

         if (isEqual(line, column, layer))
            result = this;
         else if (next != null)
            result = next.find(line, column, layer);

         return result;
      }

      boolean exists(int line, int column, int layer)
      {
         return find(line, column, layer) != null;
      }
   } // class GBoardReg


   private GBoardList[] gboardList = new GBoardList[0];


   private class GBoardList
   {
      GBoard gboard;
      GBoardReg list = null;
      GBoardList(GBoard gboard)
      {
         this.gboard = gboard;
      }

      void add(int line, int column, int layer)
      {
         GBoardReg gbreg = new GBoardReg(line, column, layer);
         gbreg.next = list;
         list = gbreg;
      }

      void remove(int line, int column, int layer)
      {
         if (list.isEqual(line, column, layer))
            list = list.next;
         else
         {
            GBoardReg gbreg = list;
            while(!gbreg.next.isEqual(line, column, layer))
               gbreg = gbreg.next;
            gbreg.next = gbreg.next.next;
         }
      }
   } // class GBoardList
}

