/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package catchthief;

import java.awt.Color;
import java.awt.Point;
import static java.lang.System.err;
import static java.lang.System.exit;
import static java.lang.System.out;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.ua.gboard.CircleGelem;
import pt.ua.gboard.GBoard;
import pt.ua.gboard.Gelem;
import pt.ua.gboard.StringGelem;
import pt.ua.gboard.games.Labyrinth;
import pt.ua.gboard.games.LabyrinthGelem;
import threads.Passerby;

/**
 *
 * @author joelpinheiro
 */
public class Maze {

    static public int pause = 100; // waiting time in each step [ms]
    public String mapa;

    static Labyrinth maze = null;

    static final char startSymbol = 'P';
    static final char markedStartSymbol = 'p';
    static final char endSymbol = 'E';
    static final char markedPositionSymbol = '.';
    static final char actualPositionSymbol = 'o';
    static private String[] gpsMap;

    public Maze(int pause, String mapa, char[] extraSymbols, Gelem[] gelems) {
        this.pause = pause;
        this.mapa = mapa;

        LabyrinthGelem.setShowRoadBoundaries();

        maze = new Labyrinth(mapa, extraSymbols);

        for (int i = 0; i < extraSymbols.length; i++) {
            maze.attachGelemToRoadSymbol(extraSymbols[i], gelems[i]);
        }

        Point[] endPositions = maze.roadSymbolPositions(endSymbol);
        if (endPositions.length != 1) {
            err.println("ERROR: one, and only one, end point required!");
            exit(3);
        }
    }

    public static Labyrinth getMaze() {
        return maze;
    }

    public static boolean randomWalking(int lin, int col) {
        boolean result = false;

        if (maze.validPosition(lin, col) && maze.isRoad(lin, col)) {
            markPosition(lin, col);
            unmarkPosition(lin, col, null);

            if (randomWalking(lin - 1, col)) // North
                {
                    result = true;
                } else if (randomWalking(lin, col + 1)) // East
                {
                    result = true;
                } else if (randomWalking(lin, col - 1)) // West
                {
                    result = true;
                } else if (randomWalking(lin + 1, col)) // South
                {
                    result = true;
                } else {
                    markPosition(lin, col);
                    unmarkPosition(lin, col, null);
                    randomWalking(lin, col + 1);
                }

            GBoard.sleep(1);
        }
        return result;
    }

    /**
     * Backtracking path search algorithm
     */
    public static boolean searchPath(int distance, int lin, int col, Map markedPositions) {
        boolean result = false;

        if (maze.validPosition(lin, col) && maze.isRoad(lin, col)) {
            if (maze.roadSymbol(lin, col) == endSymbol) {
                out.println("Destination found at " + distance + " steps from start position.");
                out.println();
                result = true;

//            gpsMap = new String[entriesSortedByValues(markedPositions).size()];
//            gpsMap = (String[]) entriesSortedByValues(markedPositions).toArray(gpsMap);
                System.out.println(entriesSortedByValues(markedPositions));

            } else if (freePosition(lin, col, markedPositions)) {
                markPosition(lin, col);

                markedPositions.put(String.valueOf(lin) + "_" + String.valueOf(col), markedPositions.size());
                unmarkPosition(lin, col, markedPositions);

                if (searchPath(distance + 1, lin - 1, col, markedPositions)) // North
                {
                    result = true;
                } else if (searchPath(distance + 1, lin, col + 1, markedPositions)) // East
                {
                    result = true;
                } else if (searchPath(distance + 1, lin, col - 1, markedPositions)) // West
                {
                    result = true;
                } else if (searchPath(distance + 1, lin + 1, col, markedPositions)) // South
                {
                    result = true;
                } else {
                    markPosition(lin, col);
                    markedPositions.put(String.valueOf(lin) + "_" + String.valueOf(col), markedPositions.size());
                    unmarkPosition(lin, col, markedPositions);
                }

                GBoard.sleep(1);
                clearPosition(lin, col, markedPositions);

            }
        }
        return result;
    }

    static boolean isStartPosition(int lin, int col) {
        assert maze.isRoad(lin, col);

        return maze.roadSymbol(lin, col) == startSymbol
                || maze.roadSymbol(lin, col) == markedStartSymbol;
    }

    static boolean freePosition(int lin, int col, Map markedPositions) {
        assert maze.isRoad(lin, col);

        if (markedPositions.containsKey(String.valueOf(lin) + "_" + String.valueOf(col))) {
            return false;
        }

        return maze.roadSymbol(lin, col) == ' '
                || maze.roadSymbol(lin, col) == startSymbol;
    }

    static void markPosition(int lin, int col) {
        assert maze.isRoad(lin, col);

        if (!isStartPosition(lin, col)) //maze.putRoadSymbol(lin, col, markedStartSymbol);
        //      else
        {
            maze.putRoadSymbol(lin, col, actualPositionSymbol);
        }
        GBoard.sleep(pause);
    }

    static void clearPosition(int lin, int col, Map markedPositions) {
        assert maze.isRoad(lin, col);

        markedPositions.remove(String.valueOf(lin) + "_" + String.valueOf(col));

        if (isStartPosition(lin, col)) {
            maze.putRoadSymbol(lin, col, startSymbol);
        } else {
            maze.putRoadSymbol(lin, col, ' ');
        }
        GBoard.sleep(pause);
    }

    static void unmarkPosition(int lin, int col, Map markedPositions) {
        assert maze.isRoad(lin, col);

        //markedPositions.remove(String.valueOf(lin) + "_" + String.valueOf(col));
        if (!isStartPosition(lin, col)) {
            maze.putRoadSymbol(lin, col, ' ');
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
