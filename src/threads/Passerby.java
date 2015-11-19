/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threads;

import static catchthief.Maze.randomWalking;
import java.awt.Point;
import static java.lang.System.out;
import java.util.Map;

/**
 *
 * @author joelpinheiro
 */
public class Passerby extends Thread {

    private Point[] startPositions;
    
    public Passerby(Point[] startPositions) {
        this.startPositions = startPositions;
    }
    
    @Override
    public void run() {
        randomWalking(startPositions[0].y, startPositions[0].x);
    }
    
}
