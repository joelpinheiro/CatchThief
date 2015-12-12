/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package catchthief;

import gps.GPSMonitor;
import informationCentral.InformationCentralMonitor;
import java.awt.Color;
import java.awt.Point;
import static java.lang.System.err;
import static java.lang.System.exit;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import threads.Passerby;
import pt.ua.gboard.Gelem;
import pt.ua.gboard.StringGelem;
import pt.ua.gboard.games.PacmanGelem;
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
    public static final int NUMBER_OF_COPS = 1;
    public static final int NUMBER_OF_PASSERBIES = 2;
    public static final int NUMBER_OF_THIEFS = 1;
    
    public static void main(String[] args) {
        
        int pause = 100;                // waiting time in each step [ms]
        char prisonSymbol = 'P';         // prisonSymbol
        char hindingPlaceSymbol = 'H'; 
        char passerbyHouseSymbol = 'T';
        char objectToStealSymbol = '$';
        char actualPositionSymbol = '•';
        String mapa = "/Users/joelpinheiro/Documents/GitHub/CatchThief/src/board/city_map_1.txt";
        
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
            new StringGelem("" + objectToStealSymbol, Color.RED),
            new StringGelem("" + actualPositionSymbol, Color.green),
            new PacmanGelem(Color.green, 40),
            new PacmanGelem(Color.green, 60)
        };
        
        CityMap cityMap = new CityMap(pause, mapa, extraSymbols, gelems);
        InformationCentralMonitor informationCentralMonitor = new InformationCentralMonitor();

        Point[] PasserbyHousePositions = CityMap.getMaze().roadSymbolPositions(passerbyHouseSymbol);
        if (PasserbyHousePositions.length != 1) {
            err.println("ERROR: one, and only one, start point required!");
            exit(2);
        }
        
        Point[] PrisonPositions = cityMap.getMaze().roadSymbolPositions(prisonSymbol);
        if (PrisonPositions.length != 1) {
            err.println("ERROR: one, and only one, start point required!");
            exit(2);
        }
        
        Point[] thiefHidingPlacePositions = cityMap.getMaze().roadSymbolPositions(hindingPlaceSymbol);
        if (thiefHidingPlacePositions.length != 1) {
            err.println("ERROR: one hiding place, and only one, start point required!");
            exit(2);
        }

        Color passerbyColor = Color.green;
        for(int i = 0 ; i < NUMBER_OF_PASSERBIES ; i++) {
            Map markedPositionsPasserBy = new TreeMap<>();
            Passerby passerby = new Passerby(informationCentralMonitor, PasserbyHousePositions, markedPositionsPasserBy, extraSymbols, passerbyColor);
            passerby.start();
            
//            pt.ua.concurrent.
            //java.util.concurrent.Executors.scheduleAtFixedRate(Runnable n, long initialDelay, long period, TimeUnit unit);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        for(int i = 0 ; i < NUMBER_OF_THIEFS ; i++) {
            Map markedPositionsThief = new TreeMap<>();
            Color thiefColor = Color.red;
            Thief thief = new Thief(informationCentralMonitor, thiefHidingPlacePositions, markedPositionsThief, extraSymbols, thiefColor);

            thief.start();
            
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        for(int i = 0 ; i < NUMBER_OF_COPS ; i++) {
            Color copColor = Color.blue;
            Map markedPositionsCop = new TreeMap<>();
            Cop cop = new Cop(informationCentralMonitor, PrisonPositions, markedPositionsCop, extraSymbols, copColor);
            cop.start();
            
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }
    }
 
}
