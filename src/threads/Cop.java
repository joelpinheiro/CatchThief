/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threads;

import catchthief.CityMap;
import gps.GPSMonitor;
import informationCentral.InformationCentralMonitor;
import java.awt.Color;
import java.awt.Point;
import static java.lang.System.out;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import pt.ua.gboard.GBoard;
import pt.ua.gboard.ImageGelem;
import pt.ua.gboard.games.Labyrinth;

/**
 *
 * @author joelpinheiro
 */
public class Cop extends Thread {

    static public int pause = 100; // waiting time in each step [ms]
    private final Point[] startPositions;
    private final Map markedPositionsCop;
    private final Color copColor;
    private static Labyrinth maze;
    static char prisonSymbol;         // prisonSymbol
    static char hindingPlaceSymbol; 
    static char passerbyHouseSymbol;
    static char objectToStealSymbol;
    static char actualPositionSymbol;
    static InformationCentralMonitor informationCentralMonitor;

    /**
     * 
     * @param informationCentralMonitor
     * @param startPositions
     * @param markedPositionsCop
     * @param extraSymbols
     * @param copColor 
     */
    public Cop(InformationCentralMonitor informationCentralMonitor, Point[] startPositions, Map markedPositionsCop, char[] extraSymbols, Color copColor) {
        this.startPositions = startPositions;
        this.markedPositionsCop = markedPositionsCop;
        this.copColor = copColor;
        Cop.maze = CityMap.getMaze();

        prisonSymbol = extraSymbols[0];
        hindingPlaceSymbol = extraSymbols[1];
        passerbyHouseSymbol = extraSymbols[2];
        objectToStealSymbol = extraSymbols[3];
        actualPositionSymbol = extraSymbols[4];
        Cop.informationCentralMonitor = informationCentralMonitor;
    }

    @Override
    public void run() {
        while(true)
        {
            if(informationCentralMonitor.thereAreIncidents())
            {
                Point[] begin = maze.roadSymbolPositions(prisonSymbol);

                Point endPosition = new Point();
                endPosition.x = informationCentralMonitor.getLastThiefPosition().x;
                endPosition.y = informationCentralMonitor.getLastThiefPosition().y;
     
                Map gpsPositions = new TreeMap<>();
                GPSMonitor gpsMonitor = new GPSMonitor(maze, CityMap.getExtraSymbols());        
                gpsPositions = GPSMonitor.getGPSPositions(begin[0], endPosition);

                goToPosition(gpsPositions, Color.BLACK);
                
                if(informationCentralMonitor.thiefInPrison())
                    break;


                if (!randomWalking(endPosition.x, endPosition.y, markedPositionsCop, copColor)) {
                    //markedPositionsCop.clear();
                }
                
                if(informationCentralMonitor.thiefInPrison())
                    break;

            }
        }
    }

    /**
     * randomWalking
     * @param lin
     * @param col
     * @param markedPositions
     * @param color
     * @return 
     */
    public static boolean randomWalking(int lin, int col, Map markedPositions, Color color) {

        if(informationCentralMonitor.thiefInPrison())
            return false;
        
        if(informationCentralMonitor.copFoundThief()){
                
            Point begin = new Point(lin, col);

            Point[] endPosition = maze.roadSymbolPositions(prisonSymbol);

            Map gpsPositions = new TreeMap<>();
            GPSMonitor gpsMonitor = new GPSMonitor(maze, CityMap.getExtraSymbols());        
            gpsPositions = GPSMonitor.getGPSPositions(endPosition[0], begin);

            goToPrison(gpsPositions, Color.BLACK);   
            
            out.println("Cop arrived prison");

            
            return false;
        }
        
        boolean result = false;

        if (maze.validPosition(lin, col) && maze.isRoad(lin, col) && !markedPositions.containsKey(String.valueOf(lin) + "_" + String.valueOf(col))) {

            markPosition(lin, col, color);
            markedPositions.put(String.valueOf(lin) + "_" + String.valueOf(col), markedPositions.size());
            unmarkPosition(lin, col, null);

            if (randomWalking(lin - 1, col, markedPositions, color)) // North
            {
                result = true;
            } else if (randomWalking(lin, col + 1, markedPositions, color)) // East
            {
                result = true;
            } else if (randomWalking(lin, col - 1, markedPositions, color)) // West
            {
                result = true;
            } else if (randomWalking(lin + 1, col, markedPositions, color)) // South
            {
                result = true;
            } else {
                if(informationCentralMonitor.thiefInPrison())
                    return false;
                markPosition(lin, col, color);
                markedPositions.put(String.valueOf(lin) + "_" + String.valueOf(col), markedPositions.size());
                unmarkPosition(lin, col, null);
            }

            GBoard.sleep(1);
        }
        return result;
    }

    /**
     * goToPosition
     * @param positions
     * @param color
     * @return 
     */
    public static boolean goToPosition(Map positions, Color color) {
        Collection c = positions.keySet();
        Iterator itr = c.iterator();

        String[] ses = new String[positions.size()];
        int cont = positions.size() - 1;

        String[] tmp = new String[positions.size()];

        while (itr.hasNext()) {
            String g = (String) itr.next();
            int id = (int) positions.get(g);
            tmp[id] = g;
        }

        for (int i = tmp.length - 1; i >= 0; i--) {
            String se = tmp[i];
            int x;
            if(se != null){
                x = se.indexOf('_');
            // get line and col from positions
                moveToPosition(Integer.parseInt(se.substring(0, x)), Integer.parseInt(se.substring(x + 1, se.length())), color);
            }
        }
        return true;
    }
    
    /**
     * goToPrison
     * @param positions
     * @param color
     * @return 
     */
    public static boolean goToPrison(Map positions, Color color) {
        Collection c = positions.keySet();
        Iterator itr = c.iterator();

        String[] ses = new String[positions.size()];
        int cont = positions.size() - 1;

        String[] tmp = new String[positions.size()];

        while (itr.hasNext()) {
            String g = (String) itr.next();
            int id = (int) positions.get(g);
            tmp[id] = g;
        }

//        for (int i = tmp.length - 1; i >= 0; i--) {
        for (String se : tmp) {
            int x = se.indexOf('_'); 
            // get line and col from positions
            moveToPosition(Integer.parseInt(se.substring(0, x)), Integer.parseInt(se.substring(x + 1, se.length())), color);
        }
        return true;
    }

    /**
     * moveToPosition
     * @param lin
     * @param col
     * @param color
     * @return 
     */
    public static boolean moveToPosition(int lin, int col, Color color) {
        boolean result = false;

        markPosition(lin, col, color);

        GBoard.sleep(pause);

        if (!isSymbolPosition(lin, col)) {
            maze.board.draw(new ImageGelem("./src/threads/cop.png", maze.board, 100), lin, col, 1);
 
            if(informationCentralMonitor.CopFoundThief(lin, col))
                System.err.println("Cop found Thief!");
        }

        unmarkPosition(lin, col, null);

        return result;
    }

    /**
     * Backtracking path search algorithm
     * @param distance
     * @param lin
     * @param col
     * @param markedPositions
     * @param color
     * @return 
     */
    public static boolean searchPath(int distance, int lin, int col, Map markedPositions, Color color) {

        boolean result = false;

        if (maze.validPosition(lin, col) && maze.isRoad(lin, col)) {
            if (maze.roadSymbol(lin, col) == objectToStealSymbol) {

                unmarkPosition(lin, col, markedPositions);

                out.println("Destination found at " + distance + " steps from start position.");
                out.println();
                result = true;

                goToPosition(markedPositions, Color.gray);

                System.out.println(entriesSortedByValues(markedPositions));

            } else if (freePosition(lin, col, markedPositions)) {
                markPosition(lin, col, color);

                markedPositions.put(String.valueOf(lin) + "_" + String.valueOf(col), markedPositions.size());
                unmarkPosition(lin, col, markedPositions);

                if (searchPath(distance + 1, lin - 1, col, markedPositions, color)) // North
                {
                    result = true;
                } else if (searchPath(distance + 1, lin, col + 1, markedPositions, color)) // East
                {
                    result = true;
                } else if (searchPath(distance + 1, lin, col - 1, markedPositions, color)) // West
                {
                    result = true;
                } else if (searchPath(distance + 1, lin + 1, col, markedPositions, color)) // South
                {
                    result = true;
                } else {
                    markPosition(lin, col, color);
                    markedPositions.put(String.valueOf(lin) + "_" + String.valueOf(col), markedPositions.size());
                    unmarkPosition(lin, col, markedPositions);
                }

                GBoard.sleep(1);
                clearPosition(lin, col, markedPositions);

            }
        }

        return result;
    }
    
    /**
     * isSymbolPosition
     * @param lin
     * @param col
     * @return 
     */
    static boolean isSymbolPosition(int lin, int col) {
        assert maze.isRoad(lin, col);

        return maze.roadSymbol(lin, col) == objectToStealSymbol ||
               maze.roadSymbol(lin, col) == hindingPlaceSymbol || 
               maze.roadSymbol(lin, col) == prisonSymbol ||
               maze.roadSymbol(lin, col) == passerbyHouseSymbol;
    }
    
    /**
     * isObjectPosition
     * @param lin
     * @param col
     * @return 
     */
    static boolean isObjectPosition(int lin, int col) {
        assert maze.isRoad(lin, col);

        return maze.roadSymbol(lin, col) == objectToStealSymbol
                || maze.roadSymbol(lin, col) == objectToStealSymbol;
    }

    /**
     * freePosition
     * @param lin
     * @param col
     * @param markedPositions
     * @return 
     */
    static boolean freePosition(int lin, int col, Map markedPositions) {
        assert maze.isRoad(lin, col);

        if (markedPositions.containsKey(String.valueOf(lin) + "_" + String.valueOf(col))) {
            return false;
        }

        return maze.roadSymbol(lin, col) == ' '
                || isSymbolPosition(lin, col);
    }

    /**
     * markPosition
     * @param lin
     * @param col
     * @param color 
     */
    static void markPosition(int lin, int col, Color color) {
        assert maze.isRoad(lin, col);

        if (!isSymbolPosition(lin, col))
        {
            maze.board.draw(new ImageGelem("./src/threads/cop.png", maze.board, 100), lin, col, 1);
            
            if(informationCentralMonitor.CopFoundThief(lin, col)) {
                System.err.println("Cop found Thief!");
                
            }
        }

        GBoard.sleep(pause);
    }

    /**
     * clearPosition
     * @param lin
     * @param col
     * @param markedPositions 
     */
    static void clearPosition(int lin, int col, Map markedPositions) {
        assert maze.isRoad(lin, col);

        markedPositions.remove(String.valueOf(lin) + "_" + String.valueOf(col));

        if (isSymbolPosition(lin, col)) {
            // maze.putRoadSymbol(lin, col, hindingPlaceSymbol);
        } else {
            maze.board.erase(lin, col, 1, 1);
        }
        //GBoard.sleep(pause);
    }

    /**
     * unmarkPosition
     * @param lin
     * @param col
     * @param markedPositions 
     */
    static void unmarkPosition(int lin, int col, Map markedPositions) {
        assert maze.isRoad(lin, col);

        if (!isSymbolPosition(lin, col)) {
            maze.board.erase(lin, col, 1, 1);
        }
        //GBoard.sleep(pause);
    }

    /**
     * 
     * @param <K>
     * @param <V>
     * @param map
     * @return 
     */
    static <K, V extends Comparable<? super V>>
            SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {
        SortedSet<Map.Entry<K, V>> sortedEntries;
        sortedEntries = new TreeSet<>(
                (Map.Entry<K, V> e1, Map.Entry<K, V> e2) -> {
                    int res = e1.getValue().compareTo(e2.getValue());
                    return res != 0 ? res : 1;
        });
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }

}
