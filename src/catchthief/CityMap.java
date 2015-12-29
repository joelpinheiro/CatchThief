/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package catchthief;

import pt.ua.gboard.Gelem;
import pt.ua.gboard.games.Labyrinth;
import pt.ua.gboard.games.LabyrinthGelem;

/**
 *
 * @author joelpinheiro
 */
public class CityMap {

    static public int pause = 100; // waiting time in each step [ms]
    public String mapa;
    public static Labyrinth maze = null;
    static char prisonSymbol;
    static char hindingPlaceSymbol; 
    static char passerbyHouseSymbol;
    static char objectToStealSymbol;
    static char actualPositionSymbol;
    static char[] extraSymbols;
   
    /**
     * Constructor of CityMap
     * @param pause
     * @param mapa
     * @param extraSymbols
     * @param gelems 
     */
    public CityMap(int pause, String mapa, char[] extraSymbols, Gelem[] gelems) {
        this.mapa = mapa;
        CityMap.extraSymbols = extraSymbols;

        LabyrinthGelem.setShowRoadBoundaries();

        maze = new Labyrinth(mapa, extraSymbols);
        
        prisonSymbol = extraSymbols[0];
        hindingPlaceSymbol = extraSymbols[1];
        passerbyHouseSymbol = extraSymbols[2];
        objectToStealSymbol = extraSymbols[3];
        actualPositionSymbol = extraSymbols[4];

        for (int i = 0; i < extraSymbols.length; i++) {
            maze.attachGelemToRoadSymbol(extraSymbols[i], gelems[i]);
        }
    }

    /**
     * Get the instance of the maze
     * @return maze
     */
    public static Labyrinth getMaze() {
        assert maze != null;
        
        return maze;
    }

    /**
     * get the symbols
     * @return extraSymbols
     */
    public static char[] getExtraSymbols(){
        assert extraSymbols != null;
        
        return extraSymbols;
    }
}
