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
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import threads.Passerby;
import pt.ua.gboard.CircleGelem;
import pt.ua.gboard.Gelem;
import pt.ua.gboard.StringGelem;
import threads.Thief;

/**
 *
 * @author joelpinheiro
 */
public class Main {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        int pause = 100;                // waiting time in each step [ms]
        char prisonSymbol = 'P';         // prisonSymbol
        char prisonStartSymbol = 'p';
        char hindingPlaceSymbol = 'H'; 
        char passerbyHouseSymbol = 'T';
        char objectToStealSymbol = '*';
        char actualPositionSymbol = '•';
        String mapa = "/Users/joelpinheiro/Documents/GitHub/CatchThief/src/board/mapa7.txt";
        
        char[] extraSymbols
                = {
                    prisonSymbol,
                    prisonStartSymbol,
                    hindingPlaceSymbol,
                    passerbyHouseSymbol,
                    objectToStealSymbol,
                    actualPositionSymbol
                };
        
        Gelem[] gelems = {
            new StringGelem("" + prisonSymbol, Color.blue),
            new StringGelem("" + prisonStartSymbol, Color.blue),
            new StringGelem("" + hindingPlaceSymbol, Color.red),
            new StringGelem("" + passerbyHouseSymbol, Color.gray),
            new StringGelem("" + objectToStealSymbol, Color.red),
            new StringGelem("" + actualPositionSymbol, Color.green),
            new CircleGelem(Color.green, 20),
            new CircleGelem(Color.green, 60)
        };
        
        Maze maze = new Maze(pause, mapa, extraSymbols, gelems);
        
        Point[] thiefHidingPlacePositions = maze.getMaze().roadSymbolPositions(hindingPlaceSymbol);
        if (thiefHidingPlacePositions.length != 1) {
            err.println("ERROR: one, and only one, start point required!");
            exit(2);
        }
        
        Map markedPositionsThief = new TreeMap<>();
        Color thiefColor = Color.red;
        Thief thief = new Thief(thiefHidingPlacePositions, markedPositionsThief, thiefColor);
        
        thief.start();
        
//        Point[] PasserbyHousePositions = maze.getMaze().roadSymbolPositions(passerbyHouseSymbol);
//        if (PasserbyHousePositions.length != 1) {
//            err.println("ERROR: one, and only one, start point required!");
//            exit(2);
//        }
//
//        Color passerbyColor = Color.blue;
//        for(int i = 0 ; i < 2 ; i++)
//        {
//            Map markedPositionsPasserBy = new TreeMap<>();
//            Passerby passerby = new Passerby(PasserbyHousePositions, markedPositionsPasserBy, passerbyColor);
//            passerby.start();
//
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
    }
 
}
