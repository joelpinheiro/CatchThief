/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package informationCentral;

import java.awt.Point;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joelpinheiro
 */
public class InformationCentralMonitor {
    
    static Point actualThiefPosition;
    static Point lastThiefPosition;
    static boolean thiefFound = false;
    static boolean copFoundThief = false;
    static boolean thiefInPrison = false;

    public InformationCentralMonitor() {
        this.actualThiefPosition = new Point();
        this.lastThiefPosition = new Point();
    }
    
    public synchronized void setActualThiefPosition(int lin, int col) {
        this.actualThiefPosition.x = lin;
        this.actualThiefPosition.y = col;
    }
    
    public Point getActualThiefPosition() {
        return this.actualThiefPosition;
    }
    
    public Point getLastThiefPosition() {
        return this.lastThiefPosition;
    }
    
    public synchronized boolean passerByFoundThief(int lin, int col) {
        if(actualThiefPosition == null)
            return false;
        
        if(lin == actualThiefPosition.x && col == actualThiefPosition.y) {
            this.thiefFound = true;
            lastThiefPosition.x = lin;
            lastThiefPosition.y = col;
            notifyAll();
            return true;
        }
        else return false;
        
       
    }
    
    public boolean CopFoundThief(int lin, int col) {
        if(actualThiefPosition == null)
            return false;
        
        if(lin == actualThiefPosition.x && col == actualThiefPosition.y) {
            this.copFoundThief = true;
            return true;
        }
        else return false;
    }
    
    public synchronized boolean thereAreIncidents() {
        
        while(true){
            if(!this.thiefFound)
            { 
                try {
                    wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(InformationCentralMonitor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else
                break;
        }
        
        return true;
    }
    
    public synchronized  boolean copFoundThief() {
        if(this.copFoundThief){
            return true;
        }
        else
            return false;
    }
    
    public synchronized void setThiefInPrison(boolean b) {
        this.thiefInPrison = b;
    }
    
    public synchronized boolean thiefInPrison() {
        return this.thiefInPrison;
    }
    
    
}
