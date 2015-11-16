/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package catchthief;

import board.Board;
import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import static java.lang.System.arraycopy;
import static java.lang.System.err;
import static java.lang.System.exit;
import java.util.Scanner;
import pt.ua.gboard.CircleGelem;
import pt.ua.gboard.GBoard;
import pt.ua.gboard.Gelem;
import pt.ua.gboard.StringGelem;
import pt.ua.gboard.games.Labyrinth;
import static pt.ua.gboard.games.Labyrinth.validMapFile;
import pt.ua.gboard.games.LabyrinthGelem;
import threads.Passerby;

/**
 *
 * @author joelpinheiro
 */
public class CatchThief {

    static Labyrinth maze = null;
    public static final int pause = 100; // waiting time in each step [ms]
    static final char startSymbol = 'S';
    static final char markedStartSymbol = 's';
    static final char endSymbol = 'X';
    static final char markedPositionSymbol = '.';
    static final char actualPositionSymbol = 'o';
    static final String mapa = "mapa1.txt";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        LabyrinthGelem.setShowRoadBoundaries();

        char[] roadSymbols
                = {
                    startSymbol,
                    markedStartSymbol,
                    endSymbol,
                    markedPositionSymbol,
                    actualPositionSymbol
                };
        maze = new Labyrinth("/Users/joelpinheiro/Documents/GitHub/CatchThief/src/board/mapa1.txt", roadSymbols);

        String[] strMaze = loadMap("/Users/joelpinheiro/Documents/GitHub/CatchThief/src/board/mapa1.txt");

        int numberOfLines = strMaze.length;
        int numberOfColumns = strMaze[0].length();
        int gelemCellsSize = 1;
        int numberOfLayers = 2;

        GBoard gboard = new GBoard("Catch the Thief", numberOfLines * gelemCellsSize, numberOfColumns * gelemCellsSize, 25 / gelemCellsSize, 25 / gelemCellsSize, numberOfLayers);

        Labyrinth maze = new Labyrinth(strMaze, roadSymbols, 1, gboard);
        
        Gelem[] gelems = {
         new StringGelem("" + startSymbol, Color.red),
         new StringGelem("" + markedStartSymbol, Color.red),
         new StringGelem("" + endSymbol, Color.red),
         new CircleGelem(Color.green, 20),
         new CircleGelem(Color.green, 60)
        };
        for (int i = 0; i < roadSymbols.length; i++) {
            maze.attachGelemToRoadSymbol(roadSymbols[i], gelems[i]);
        }

        Point[] startPositions = maze.roadSymbolPositions(startSymbol);
        if (startPositions.length != 1) {
            err.println("ERROR: one, and only one, start point required!");
            exit(2);
        }
        Point[] endPositions = maze.roadSymbolPositions(endSymbol);
        if (endPositions.length != 1) {
            err.println("ERROR: one, and only one, end point required!");
            exit(3);
        }
        
        Passerby passerby = new Passerby(maze);
    }

    protected static String[] loadMap(String filename) {
        assert validMapFile(filename) : "Path \"" + filename + "\" is not valid";

        String[] result = null;
        try {
            File fin = new File(filename);
            Scanner scin = new Scanner(fin);
            String[] lines = new String[(int) Math.sqrt(fin.length())]; // heuristic (square map)
            int nLines = 0;
            while (scin.hasNextLine()) {
                if (nLines == lines.length) {
                    String[] copy = new String[lines.length + 10];
                    arraycopy(lines, 0, copy, 0, lines.length);
                    lines = copy;
                }
                lines[nLines] = scin.nextLine();
                nLines++;
            }
            scin.close();
            result = new String[nLines];
            for (int l = 0; l < nLines; l++) {
                result[l] = lines[l];
            }
        } catch (IOException e) {
            result = null;
        }

        return result;
    }

}
