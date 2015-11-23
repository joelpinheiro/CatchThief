/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threads;

import catchthief.CityMap;
import java.awt.Color;
import java.awt.Point;
import static java.lang.System.out;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import pt.ua.gboard.CircleGelem;
import pt.ua.gboard.GBoard;
import pt.ua.gboard.games.Labyrinth;

/**
 *
 * @author joelpinheiro
 */
public class Thief extends Thread {

    static public int pause = 100; // waiting time in each step [ms]
    private Point[] startPositions;
    private Map markedPositions;
    private Color color;
    private static Labyrinth maze;
    static char prisonSymbol;         // prisonSymbol
    static char prisonStartSymbol;
    static char hindingPlaceSymbol;
    static char passerbyHouseSymbol;
    static char objectToStealSymbol;
    static char actualPositionSymbol;

    public Thief(Labyrinth maze, Point[] startPositions, Map markedPositions, char[] extraSymbols, Color color) {
        this.startPositions = startPositions;
        this.markedPositions = markedPositions;
        this.color = color;
        this.maze = maze;

        prisonSymbol = extraSymbols[0];
        hindingPlaceSymbol = extraSymbols[1];
        passerbyHouseSymbol = extraSymbols[2];
        objectToStealSymbol = extraSymbols[3];
        actualPositionSymbol = extraSymbols[4];
    }

    @Override
    public void run() {
        if (!searchPath(0, startPositions[0].y, startPositions[0].x, markedPositions, color)) {
            out.println("No solution!");
        }
    }

    public static boolean randomWalking(int lin, int col, Map markedPositions, Color color) {

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
                markPosition(lin, col, color);
                markedPositions.put(String.valueOf(lin) + "_" + String.valueOf(col), markedPositions.size());
                unmarkPosition(lin, col, null);
            }

            GBoard.sleep(1);
        }
        return result;
    }

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
            int x = se.indexOf('_');
            // get line and col from positions
            moveToPosition(Integer.parseInt(se.substring(0, x)), Integer.parseInt(se.substring(x + 1, se.length())), color);
        }
        return true;
    }

    public static boolean moveToPosition(int lin, int col, Color color) {
        boolean result = false;

        markPosition(lin, col, color);

        GBoard.sleep(pause);

        if (!isStartPosition(lin, col)) {
            maze.board.draw(new CircleGelem(color, 60), lin, col, 1);
        }

        unmarkPosition(lin, col, null);

        return result;
    }

    /**
     * Backtracking path search algorithm
     */
    public static boolean searchPath(int distance, int lin, int col, Map markedPositions, Color color) {

        boolean result = false;

        if (maze.validPosition(lin, col) && maze.isRoad(lin, col)) {
            if (maze.roadSymbol(lin, col) == objectToStealSymbol) {

                unmarkPosition(lin, col, markedPositions);

                out.println("Destination found at " + distance + " steps from start position.");
                out.println();
                result = true;

                goToPosition(markedPositions, Color.black);

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

    static void markPosition(int lin, int col, Color color) {
        assert maze.isRoad(lin, col);

        if (!isStartPosition(lin, col)) //maze.putRoadSymbol(lin, col, markedStartSymbol);
        //      else
        {
            //cityMap
//            maze.putRoadSymbol(lin, col, new CircleGelem(color, 60));
            maze.board.draw(new CircleGelem(color, 60), lin, col, 1);
        }

        GBoard.sleep(pause);
    }

    static void clearPosition(int lin, int col, Map markedPositions) {
        assert maze.isRoad(lin, col);

        markedPositions.remove(String.valueOf(lin) + "_" + String.valueOf(col));

        if (isStartPosition(lin, col)) {
            maze.putRoadSymbol(lin, col, hindingPlaceSymbol);
        } else {
            //maze.putRoadSymbol(lin, col, ' ');
            maze.board.erase(lin, col, 1, 1);
        }
        GBoard.sleep(pause);
    }

    static void unmarkPosition(int lin, int col, Map markedPositions) {
        assert maze.isRoad(lin, col);

        //markedPositions.remove(String.valueOf(lin) + "_" + String.valueOf(col));
        if (!isStartPosition(lin, col)) {
            //maze.putRoadSymbol(lin, col, ' ');
            maze.board.erase(lin, col, 1, 1);
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
