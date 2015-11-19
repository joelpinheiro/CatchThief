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
import java.util.logging.Level;
import java.util.logging.Logger;
import threads.Passerby;
import pt.ua.gboard.CircleGelem;
import pt.ua.gboard.Gelem;
import pt.ua.gboard.StringGelem;

/**
 *
 * @author joelpinheiro
 */
public class Main {



    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        int pause = 100; // waiting time in each step [ms]
        char startSymbol = 'P';
        char markedStartSymbol = 'p';
        char endSymbol = 'E';
        char passerbyHouseSymbol = 'T';
        char markedPositionSymbol = '.';
        char actualPositionSymbol = 'o';
        String mapa = "/Users/joelpinheiro/Documents/GitHub/CatchThief/src/board/mapa7.txt";
        
        char[] extraSymbols
                = {
                    startSymbol,
                    markedStartSymbol,
                    endSymbol,
                    passerbyHouseSymbol,
                    markedPositionSymbol,
                    actualPositionSymbol
                };
        
        Gelem[] gelems = {
            new StringGelem("" + startSymbol, Color.red),
            new StringGelem("" + markedStartSymbol, Color.red),
            new StringGelem("" + endSymbol, Color.red),
            new StringGelem("" + passerbyHouseSymbol, Color.red),
            new CircleGelem(Color.green, 20),
            new CircleGelem(Color.green, 60)
        };
        
        Maze maze = new Maze(pause, mapa, extraSymbols, gelems);

        Point[] startPositions = maze.getMaze().roadSymbolPositions(startSymbol);
        if (startPositions.length != 1) {
            err.println("ERROR: one, and only one, start point required!");
            exit(2);
        }
        
        Point[] PasserbyHousePositions = maze.getMaze().roadSymbolPositions(passerbyHouseSymbol);
        if (PasserbyHousePositions.length != 1) {
            err.println("ERROR: one, and only one, start point required!");
            exit(2);
        }
        
//        Map markedPositions = new TreeMap<>();
//        
//        Thief thief1 = new Thief(startPositions, markedPositions);
//        thief1.start();
//        
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//        }
//       
//        
//        Map markedPositions2 = new TreeMap<>();
//        Thief thief2 = new Thief(startPositions, markedPositions2);
//        thief2.start();
//        
//        
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        Passerby passerby = new Passerby(PasserbyHousePositions);
        passerby.start();
        
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
 
}
