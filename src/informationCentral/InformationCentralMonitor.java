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

    /**
     * InformationCentralMonitor
     */
    public InformationCentralMonitor() {
        this.actualThiefPosition = new Point();
        this.lastThiefPosition = new Point();
    }
    
    /**
     * setActualThiefPosition
     * @param lin
     * @param col 
     */
    public synchronized void setActualThiefPosition(int lin, int col) {
        this.actualThiefPosition.x = lin;
        this.actualThiefPosition.y = col;
    }
    
    /**
     * getActualThiefPosition
     * @return 
     */
    public Point getActualThiefPosition() {
        return this.actualThiefPosition;
    }
    
    /**
     * getLastThiefPosition
     * @return 
     */
    public Point getLastThiefPosition() {
        return this.lastThiefPosition;
    }
    
    /**
     * passerByFoundThief
     * @param lin
     * @param col
     * @return 
     */
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
    
    /**
     * CopFoundThief
     * @param lin
     * @param col
     * @return 
     */
    public boolean CopFoundThief(int lin, int col) {
        if(actualThiefPosition == null)
            return false;
        
        if(lin == actualThiefPosition.x && col == actualThiefPosition.y) {
            this.copFoundThief = true;
            return true;
        }
        else return false;
    }
    
    /**
     * thereAreIncidents
     * @return 
     */
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
    
    /**
     * copFoundThief
     * @return 
     */
    public synchronized  boolean copFoundThief() {
        if(this.copFoundThief){
            return true;
        }
        else
            return false;
    }
    
    /**
     * setThiefInPrison
     * @param b 
     */
    public synchronized void setThiefInPrison(boolean b) {
        this.thiefInPrison = b;
    }
    
    /**
     * thiefInPrison
     * @return 
     */
    public synchronized boolean thiefInPrison() {
        return this.thiefInPrison;
    }
    
    
}
