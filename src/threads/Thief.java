/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threads;

import static catchthief.Maze.searchPath;
import java.awt.Color;
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
    private Color color;

    public Thief(Point[] startPositions, Map markedPositions, Color color) {
        this.startPositions = startPositions;
        this.markedPositions = markedPositions;
        this.color = color;
    }

    @Override
    public void run() {
        if (!searchPath(0, startPositions[0].y, startPositions[0].x, markedPositions, color)) {
            out.println("No solution!");
        }
    }
}
