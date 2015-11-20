/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threads;

import static catchthief.Maze.randomWalking;
import static catchthief.Maze.searchPath;
import java.awt.Color;
import java.awt.Point;
import static java.lang.System.out;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author joelpinheiro
 */
public class Passerby extends Thread {

    private Point[] startPositions;
    private Map markedPositionsPasserBy;
    private Color passerbyColor;
    
    public Passerby(Point[] startPositions, Map markedPositionsPasserBy, Color passerbyColor) {
        this.startPositions = startPositions;
        this.markedPositionsPasserBy = markedPositionsPasserBy;
        this.passerbyColor = passerbyColor;
    }
    
    @Override
    public void run() {
        if (!randomWalking(startPositions[0].y, startPositions[0].x, markedPositionsPasserBy)) {
            out.println("No solution!");
        }
    }
    
}
