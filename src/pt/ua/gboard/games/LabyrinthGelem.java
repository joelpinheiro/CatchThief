package pt.ua.gboard.games;

import java.awt.*;
import pt.ua.gboard.*;

/**
 * Labyrinth graphical element.
 *
 * <P>This gelem draws an appropriate labyrinth cell taking into consideration
 * the contents of the eight adjacent cells.
 * 
 *  <P> +++
 * <BR> +X+
 * <BR> +++
 *
 * <P>This class follows DbC(tm) methodology.
 * Where possible, contracts are implement with JML and native's Java assert.
 *
 * @author Miguel Oliveira e Silva (mos@ua.pt)
 */
public class LabyrinthGelem extends Gelem
{
   /*
    * N: North ; E: East ; W: West ; S: South
    * Order: ROAD_NW ROAD_N ROAD_NE ROAD_W ROAD_E ROAD_SW ROAD_S ROAD_SE ROAD
    *    0: W W W  1: R W W  2: W R W  3: R R W  ...
    *       W W W     W W W     W W W     W W W
    *       W W W     W W W     W W W     W W W
   */
   public static final int ROAD_NW =  0x001;
   public static final int ROAD_N  =  0x002;
   public static final int ROAD_NE =  0x004;
   public static final int ROAD_W  =  0x008;
   public static final int ROAD_E  =  0x010;
   public static final int ROAD_SW =  0x020;
   public static final int ROAD_S  =  0x040;
   public static final int ROAD_SE =  0x080;
   public static final int ROAD    =  0x100;

   /**
    * Singleton method to define the separation gap between a wall and a road.
    *
    *  <P><B>requires</B>: {@code perc >= 0.0 && perc <= 100.0}
    *
    * @param perc  percentage value
    */
   //@ requires perc >= 0.0 && perc <= 100.0;
   public static void setGapPercentageSize(double perc)
   {
      assert perc >= 0.0 && perc <= 100.0: "invalid gap percentage";

      gapPercSize = perc;
   }

   /**
    * Singleton method to set the visualization of road boundaries (as a thin yellow square).
    */
   public static void setShowRoadBoundaries()
   {
      showRoadBoundaries = true;
   }

   /**
    * Singleton method to reset the visualization of road boundaries (this is the default).
    */
   public static void resetShowRoadBoundaries()
   {
      showRoadBoundaries = false;
   }

   protected static double gapPercSize = 20.0; // default %
   protected static boolean rounded = true;
   protected static boolean showRoadBoundaries = false; // default
   protected static Color roadColor = Color.white;
   protected static Color wallColor = Color.BLACK;
   protected static Color roadBoundariesColor = Color.WHITE;

   public static boolean validForm(int form)
   {
      return form >= 0x00 && form <= 0x1FF;
   }

   public LabyrinthGelem(int form)
   {
      this(form, 1, 1);
   }

   public LabyrinthGelem(int form, int numberOfLines, int numberOfColumns)
   {
      super(numberOfLines, numberOfColumns);

      assert validForm(form);

      this.form = form;
   }

   public boolean isWall()
   {
      return (form&ROAD) == 0;
   }

   public boolean isRoad()
   {
      return !isWall();
   }

   public void draw(Graphics g, int line, int column, int cellWidth, int cellHeight, Color background)
   {
      assert g != null;

      int w = cellWidth*numberOfColumns;
      int h = cellHeight*numberOfLines;
      int wGap = (int)(w*gapPercSize)/100;
      int hGap = (int)(h*gapPercSize)/100;
      int wWall = w - 2*wGap;
      int hWall = h - 2*hGap;

      g.setColor(roadColor);
      g.fillRect(column*cellWidth, line*cellHeight, w, h);

      if (isWall())
      {
         g.setColor(wallColor);
         if (rounded)
         {
            g.fillRoundRect(column*cellWidth+wGap, line*cellHeight+hGap, wWall, hWall, wWall, hWall);
            if ((form&ROAD_N) == 0)
               g.fillRect(column*cellWidth+wGap, line*cellHeight+hGap, wWall, hWall/2+hWall%2);
            if ((form&ROAD_E) == 0)
               g.fillRect(column*cellWidth+wGap+wWall/2, line*cellHeight+hGap, wWall/2+wWall%2, hWall);
            if ((form&ROAD_S) == 0)
               g.fillRect(column*cellWidth+wGap, line*cellHeight+hGap+hWall/2, wWall, hWall/2+hWall%2);
            if ((form&ROAD_W) == 0)
               g.fillRect(column*cellWidth+wGap, line*cellHeight+hGap, wWall/2+wWall%2, hWall);
            if ((form&ROAD_NW) != 0 && (form&ROAD_N) == 0 && (form&ROAD_W) == 0)
            {
               g.fillRect(column*cellWidth, line*cellHeight, wGap, hGap);
               g.setColor(roadColor);
               g.fillRoundRect(column*cellWidth-wGap, line*cellHeight-hGap, wGap*2, hGap*2, wGap*2, hGap*2);
               g.setColor(wallColor);
            }
            if ((form&ROAD_NE) != 0 && (form&ROAD_N) == 0 && (form&ROAD_E) == 0)
            {
               g.fillRect(column*cellWidth+wGap+wWall, line*cellHeight, wGap, hGap);
               g.setColor(roadColor);
               g.fillRoundRect(column*cellWidth+wGap+wWall, line*cellHeight-hGap, wGap*2, hGap*2, wGap*2, hGap*2);
               g.setColor(wallColor);
            }
            if ((form&ROAD_SW) != 0 && (form&ROAD_S) == 0 && (form&ROAD_W) == 0)
            {
               g.fillRect(column*cellWidth, line*cellHeight+hGap+hWall, wGap, hGap);
               g.setColor(roadColor);
               g.fillRoundRect(column*cellWidth-wGap, line*cellHeight+hGap+hWall, wGap*2, hGap*2, wGap*2, hGap*2);
               g.setColor(wallColor);
            }
            if ((form&ROAD_SE) != 0 && (form&ROAD_S) == 0 && (form&ROAD_E) == 0)
            {
               g.fillRect(column*cellWidth+wGap+wWall, line*cellHeight+hGap+hWall, wGap, hGap);
               g.setColor(roadColor);
               g.fillRoundRect(column*cellWidth+wGap+wWall, line*cellHeight+hGap+hWall, wGap*2, hGap*2, wGap*2, hGap*2);
               g.setColor(wallColor);
            }
         }
         else
            g.fillRect(column*cellWidth+wGap, line*cellHeight+hGap, wWall, hWall);
         if ((form&ROAD_N) == 0)
            g.fillRect(column*cellWidth+wGap, line*cellHeight, wWall, hGap);
         if ((form&ROAD_E) == 0)
            g.fillRect(column*cellWidth+wGap+wWall, line*cellHeight+hGap, wGap, hWall);
         if ((form&ROAD_S) == 0)
            g.fillRect(column*cellWidth+wGap, line*cellHeight+hGap+hWall, wWall, hGap);
         if ((form&ROAD_W) == 0)
            g.fillRect(column*cellWidth, line*cellHeight+hGap, wGap, hWall);
         if ((form&ROAD_N) == 0 && (form&ROAD_W) == 0 && (form&ROAD_NW) == 0)
            g.fillRect(column*cellWidth, line*cellHeight, wGap, hGap);
         if ((form&ROAD_N) == 0 && (form&ROAD_E) == 0 && (form&ROAD_NE) == 0)
            g.fillRect(column*cellWidth+wGap+wWall, line*cellHeight, wGap, hGap);
         if ((form&ROAD_S) == 0 && (form&ROAD_W) == 0 && (form&ROAD_SW) == 0)
            g.fillRect(column*cellWidth, line*cellHeight+hGap+hWall, wGap, hGap);
         if ((form&ROAD_S) == 0 && (form&ROAD_E) == 0 && (form&ROAD_SE) == 0)
            g.fillRect(column*cellWidth+wGap+wWall, line*cellHeight+hGap+hWall, wGap, hGap);
      }
      else if (showRoadBoundaries)
      {
         g.setColor(roadBoundariesColor);
         g.drawRect(column*cellWidth, line*cellHeight, w-1, h-1);
      }
   }

   public void erase(Graphics g, int line, int column, int cellWidth, int cellHeight, Color background)
   {
      g.setColor(background);
      g.fillRect(column*cellWidth, line*cellHeight, cellWidth*numberOfColumns(), cellHeight*numberOfLines());
   }

   protected final int form;
}
