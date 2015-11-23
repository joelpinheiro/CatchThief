package pt.ua.gboard;

/**
 * This module implements input handling for GBoard.
 *
 * <P>An input handler class should inherit from this (abstract) class,
 * call the constructor with the desired mask, and implement the
 * run procedure.  To activate input handler a pushInputHandler
 * on the GBoard object is required.
 *
 *  <P><B>invariant</B>: {@code validInputMask(mask())}
 *
 * <P>This class follows DbC(tm) methodology.
 * Where possible, contracts are implement with JML and native's Java assert.
 *
 * @author Miguel Oliveira e Silva (mos@ua.pt)
 */
public abstract class GBoardInputHandler
{
   //@ public invariant validInputMask(mask());

   /**
    * Constructs a new GBoardInputHandler accepting the events defined by the mask.
    *
    *  <P><B>requires</B>: {@code validInputMask(mask)}
    *
    *  <P><B>ensures</B>: {@code mask() == mask}
    * 
    * @param mask  the desired mask
    */
   //@ requires validInputMask(mask);
   //@ ensures mask() == mask;
   public GBoardInputHandler(int mask)
   {
      assert validInputMask(mask);

      setMask(mask);
   }

   public static final int keyPressedMask     = 0x01;
   public static final int keyReleasedMask    = 0x02;
   public static final int keyTypedMask       = 0x04;
   public static final int mousePressedMask   = 0x08;
   public static final int mouseReleasedMask  = 0x10;
   public static final int mouseClickedMask   = 0x20;
   public static final int gboardClosingMask  = 0x40;

   public static final int maxMaskValue       = 0x7F;

   /**
    * Checks if input mask is valid.
    *
    * @param mask  the mask
    * @return {@code boolean} true if mask ok
    */
   public static /*@ pure @*/ boolean validInputMask(int mask)
   {
      return mask >= 0x00 && mask <= maxMaskValue;
   }

   public static final int keyEventType    = 1;
   public static final int mouseEventType  = 2;
   public static final int windowEventType = 3;

   public /*@ pure @*/ boolean validEventType(int type)
   {
      return type >= 1 && type <= 3;
   }

   /**
    * The current mask value.
    *
    * @return {@code int} mask
    */
   public /*@ pure @*/ int mask()
   {
      return mask;
   }

   /**
    * Defines a new input mask.
    *
    *  <P><B>requires</B>: {@code validInputMask(mask)}
    *
    *  <P><B>ensures</B>: {@code mask() == mask}
    * 
    * @param mask  the new mask
    */
   //@ requires validInputMask(mask);
   //@ ensures mask() == mask;
   public void setMask(int mask)
   {
      assert validInputMask(mask): "invalid mask";

      this.mask = mask;
   }

   /**
    * Is current input handler object activated by event?
    *
    * @param event  the event mask value
    * @return {@code boolean} true if activated, false otherwise
    */
   public /*@ pure @*/ boolean activated(int event)
   {
      return (mask & event) != 0;
   }

   /**
    * Procedure to be automatically executed by {@code GBoard} when
    * the input handler object is activated.
    *
    * @param board  GBoard object
    * @param line  the line number
    * @param column  the column number
    * @param layer  the layer number
    * @param type  the event type (keyEventType or mouseEventType or windowEventType)
    * @param code  the event code (KeyEvent.getKeyCode() or MouseEvent.getButton() or WindowEvent.WINDOW_CLOSED)
    * @param gelem  object
    */
   //@ requires board != null;
   //@ requires validEventType(type);
   public abstract void run(GBoard board, int line, int column, int layer, int type, int code, Gelem gelem);

   protected int mask = 0x0;
}
