/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package catchthief;

import gps.GPSMonitor;
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
import threads.Cop;
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
        char hindingPlaceSymbol = 'H'; 
        char passerbyHouseSymbol = 'T';
        char objectToStealSymbol = '*';
        char actualPositionSymbol = '•';
        String mapa = "/Users/joelpinheiro/Documents/GitHub/CatchThief/src/board/mapa7.txt";
        
        char[] extraSymbols
                = {
                    prisonSymbol,
                    hindingPlaceSymbol,
                    passerbyHouseSymbol,
                    objectToStealSymbol,
                    actualPositionSymbol
                };
        
        Gelem[] gelems = {
            new StringGelem("" + prisonSymbol, Color.blue),
            new StringGelem("" + hindingPlaceSymbol, Color.red),
            new StringGelem("" + passerbyHouseSymbol, Color.gray),
            new StringGelem("" + objectToStealSymbol, Color.red),
            new StringGelem("" + actualPositionSymbol, Color.green),
            new CircleGelem(Color.green, 20),
            new CircleGelem(Color.green, 60)
        };
        
        CityMap cityMap = new CityMap(pause, mapa, extraSymbols, gelems);
        
        Point[] thiefHidingPlacePositions = cityMap.getMaze().roadSymbolPositions(hindingPlaceSymbol);
        if (thiefHidingPlacePositions.length != 1) {
            err.println("ERROR: one, and only one, start point required!");
            exit(2);
        }
        
        Point[] end = cityMap.getMaze().roadSymbolPositions(hindingPlaceSymbol);
        if (thiefHidingPlacePositions.length != 1) {
            err.println("ERROR: one, and only one, start point required!");
            exit(2);
        }
        
        Point[] begin = cityMap.getMaze().roadSymbolPositions(prisonSymbol);
        if (thiefHidingPlacePositions.length != 1) {
            err.println("ERROR: one, and only one, start point required!");
            exit(2);
        }

        Map gpsPositions = new TreeMap<>();
        GPSMonitor gpsMonitor = new GPSMonitor(cityMap.getMaze(), extraSymbols);        
        gpsPositions = gpsMonitor.getGPSPositions(begin[0], end[0]);
        
        System.out.println(gpsPositions.toString());
        
        Map markedPositionsThief = new TreeMap<>();
        Color thiefColor = Color.red;
        Thief thief = new Thief(cityMap.getMaze(), thiefHidingPlacePositions, markedPositionsThief, extraSymbols, thiefColor);
        
        thief.start();
        
//        Start PasserBy
        Point[] PasserbyHousePositions = cityMap.getMaze().roadSymbolPositions(passerbyHouseSymbol);
        if (PasserbyHousePositions.length != 1) {
            err.println("ERROR: one, and only one, start point required!");
            exit(2);
        }

        Color passerbyColor = Color.green;
        Map markedPositionsPasserBy = new TreeMap<>();
        Passerby passerby = new Passerby(cityMap.getMaze(), PasserbyHousePositions, markedPositionsPasserBy, extraSymbols, passerbyColor);
        passerby.start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
//        Cop
        Point[] PrisonPositions = cityMap.getMaze().roadSymbolPositions(prisonSymbol);
        if (PrisonPositions.length != 1) {
            err.println("ERROR: one, and only one, start point required!");
            exit(2);
        }

        Color copColor = Color.blue;
        Map markedPositionsCop = new TreeMap<>();
        Cop cop = new Cop(cityMap.getMaze(), PrisonPositions, markedPositionsCop, extraSymbols, copColor);
        cop.start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
//        
//        Color passerbyColor2 = Color.yellow;
//        Map markedPositionsPasserBy2 = new TreeMap<>();
//        Passerby passerby2 = new Passerby(PasserbyHousePositions, markedPositionsPasserBy2, passerbyColor2);
//        passerby2.start();
//
//        try {
//            Thread.sleep(9000);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        Color passerbyColor3 = Color.magenta;
//        Map markedPositionsPasserBy3 = new TreeMap<>();
//        Passerby passerby3 = new Passerby(PasserbyHousePositions, markedPositionsPasserBy3, passerbyColor3);
//        passerby3.start();
//
//        try {
//            Thread.sleep(7000);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        
//        Color passerbyColor = Color.blue;
//        for(int i = 0 ; i < 3 ; i++)
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
