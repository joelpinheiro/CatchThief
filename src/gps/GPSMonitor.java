/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gps;

import informationCentral.InformationCentralMonitor;
import java.awt.Point;
import static java.lang.System.out;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.awt.Color;
import java.util.HashMap;
import pt.ua.gboard.GBoard;
import pt.ua.gboard.ImageGelem;
import pt.ua.gboard.games.Labyrinth;

/**
 *
 * @author joelpinheiro
 */
public class GPSMonitor {
    static public int pause = 0;
    private static Point endPosition;
    private static Map markedPositions;
    private static Map finalMarkedPositions;
    private static Labyrinth maze;
    
    static char prisonSymbol;
    static char hindingPlaceSymbol; 
    static char passerbyHouseSymbol;
    static char objectToStealSymbol;
    static char actualPositionSymbol;


    public GPSMonitor(Labyrinth maze, char[] extraSymbols) {
        GPSMonitor.markedPositions = new TreeMap<>();
        GPSMonitor.maze = maze;

        prisonSymbol = extraSymbols[0];
        hindingPlaceSymbol = extraSymbols[1];
        passerbyHouseSymbol = extraSymbols[2];
        objectToStealSymbol = extraSymbols[3];
        actualPositionSymbol = extraSymbols[4];
    }
    
    public static Map getGPSPositions(Point endPoint, Point startPoint){
        endPosition = endPoint;
        
        if (!searchPath(0, startPoint.x, startPoint.y, markedPositions, java.awt.Color.BLACK)) {
            out.println("Cannot get GPS Positions");
        }
        
        return finalMarkedPositions;
    }
   

   /**
     * Backtracking path search algorithm
     */
    public static boolean searchPath(int distance, int lin, int col, Map markedPositions, Color color) {

        boolean result = false;

        if (maze.validPosition(lin, col) && maze.isRoad(lin, col)) {
            if (lin == endPosition.y && col == endPosition.x) {

                unmarkPosition(lin, col, markedPositions);

//                out.println("Destination found at " + distance + " steps from start position.");
//                out.println();
                result = true;

//                System.out.println(entriesSortedByValues(markedPositions));
                //GPSMonitor.finalMarkedPositions = markedPositions;
                GPSMonitor.finalMarkedPositions = new HashMap<>(markedPositions);

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
    
    static boolean isSymbolPosition(int lin, int col) {
        assert maze.isRoad(lin, col);

        return maze.roadSymbol(lin, col) == objectToStealSymbol ||
               maze.roadSymbol(lin, col) == hindingPlaceSymbol || 
               maze.roadSymbol(lin, col) == prisonSymbol ||
               maze.roadSymbol(lin, col) == passerbyHouseSymbol;
    }

    static boolean freePosition(int lin, int col, Map markedPositions) {
        assert maze.isRoad(lin, col);

        if (markedPositions.containsKey(String.valueOf(lin) + "_" + String.valueOf(col))) {
            return false;
        }

        return maze.roadSymbol(lin, col) == ' '
                || isSymbolPosition(lin, col);
    }

    static void markPosition(int lin, int col, Color color) {
        assert maze.isRoad(lin, col);

        if (!isSymbolPosition(lin, col)) //maze.putRoadSymbol(lin, col, markedStartSymbol);
        {
            //maze.board.draw(new ImageGelem("/Users/joelpinheiro/Documents/GitHub/CatchThief/src/threads/thief.png", maze.board, 100), lin, col, 1);       
        }

        GBoard.sleep(pause);
    }

    static void clearPosition(int lin, int col, Map markedPositions) {
        assert maze.isRoad(lin, col);

        markedPositions.remove(String.valueOf(lin) + "_" + String.valueOf(col));

        if (isSymbolPosition(lin, col)) {
            //maze.putRoadSymbol(lin, col, hindingPlaceSymbol);
        } else {
            //maze.putRoadSymbol(lin, col, ' ');
           // maze.board.erase(lin, col, 1, 1);
        }
        GBoard.sleep(pause);
    }

    static void unmarkPosition(int lin, int col, Map markedPositions) {
        assert maze.isRoad(lin, col);

        //markedPositions.remove(String.valueOf(lin) + "_" + String.valueOf(col));
        if (!isSymbolPosition(lin, col)) {
            //maze.putRoadSymbol(lin, col, ' ');
            //maze.board.erase(lin, col, 1, 1);
        }
        GBoard.sleep(pause);
    }

    static <K, V extends Comparable<? super V>>
            SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {
        SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(
                new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
                int res = e1.getValue().compareTo(e2.getValue());
                return res != 0 ? res : 1;
            }
        }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }
}
