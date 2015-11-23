/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gps;

import java.awt.Point;
import static java.lang.System.out;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import pt.ua.gboard.games.Labyrinth;

/**
 *
 * @author joelpinheiro
 */
public class GPSMonitor {
    private static Point endPosition;
    private static Map markedPositions;
    private static Labyrinth maze;
    
    static char prisonSymbol;         // prisonSymbol
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
    
    public static Map getGPSPositions(Point startPoint, Point endPoint){
        endPosition = endPoint;
        
        search(0, startPoint.x, startPoint.y);
        
        return GPSMonitor.markedPositions;
    }
    
    /**
     * Backtracking path search algorithm
     * @param distance
     * @param lin
     * @param col
     * @return 
     */
    public static boolean search(int distance, int lin, int col) {

        boolean result = false;

        if (maze.validPosition(lin, col) && maze.isRoad(lin, col)) {
            if (lin == endPosition.x && col == endPosition.y) {

                out.println();
                result = true;

                System.out.println(entriesSortedByValues(markedPositions));

            } else if (freePosition(lin, col, markedPositions)) {

                markedPositions.put(String.valueOf(lin) + "_" + String.valueOf(col), markedPositions.size());

                if (search(distance + 1, lin - 1, col)) // North
                {
                    result = true;
                } else if (search(distance + 1, lin, col + 1)) // East
                {
                    result = true;
                } else if (search(distance + 1, lin, col - 1)) // West
                {
                    result = true;
                } else if (search(distance + 1, lin + 1, col)) // South
                {
                    result = true;
                } else {
                    markedPositions.put(String.valueOf(lin) + "_" + String.valueOf(col), markedPositions.size());
                }

                //clearPosition(lin, col, markedPositions);

            }
        }

        return result;
    }

    static boolean isStartPosition(int lin, int col) {
        assert maze.isRoad(lin, col);

        return maze.roadSymbol(lin, col) == hindingPlaceSymbol
                || maze.roadSymbol(lin, col) == hindingPlaceSymbol;
    }

    static boolean freePosition(int lin, int col, Map markedPositions) {
        assert maze.isRoad(lin, col);

        if (markedPositions.containsKey(String.valueOf(lin) + "_" + String.valueOf(col))) {
            return false;
        }

        return maze.roadSymbol(lin, col) == ' '
                || maze.roadSymbol(lin, col) == hindingPlaceSymbol;
    }

    static void clearPosition(int lin, int col, Map markedPositions) {
        assert maze.isRoad(lin, col);

        markedPositions.remove(String.valueOf(lin) + "_" + String.valueOf(col));
    }

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
