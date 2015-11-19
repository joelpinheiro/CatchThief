/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threads;

import static catchthief.Maze.searchPath;
import java.awt.Point;
import static java.lang.System.out;
import java.util.Map;

/**
 *
 * @author joelpinheiro
 */
public class Thief extends Thread {

    private Point[] startPositions;
    private Map markedPositions;

    public Thief(Point[] startPositions, Map markedPositions) {
        this.startPositions = startPositions;
        this.markedPositions = markedPositions;
    }

    @Override
    public void run() {
        if (!searchPath(0, startPositions[0].y, startPositions[0].x, markedPositions)) {
            out.println("No solution!");
        }
    }
}
