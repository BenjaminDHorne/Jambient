package Jambient;

/**
 *
 * @author Benjamin D. Horne
 * 
 * This is the Process Class for the library Jambient
 */

import com.googlecode.mobilityrpc.quickstart.QuickTask;
import java.util.ArrayList;

public abstract class Process {
    
    Ambient parentAmbient;
    String name;
    
    /**
     * A process must take a string for its unique name and an ambient to start in
     * It is recommended that your string name is the same as the object name
     * Take Note: Starting a Process in an Ambient is slightly different than the
     * Ambient Calculus as you can start processes outside of ambients. 
     * Starting Processes in an Ambient was a design choice of the library.
     * @param n is a string for a unique name
     * @param x is an ambient to start in.
     */
    public Process(String n, Ambient x){
        name = n;
        parentAmbient = x;
    }
    
    public void make(){
        this.parentAmbient.processChildren.add(this);
    }
    
    // public objective methods, should not be overwritten
    public void mvin(Ambient x){
        if(x.allowEnter){
            if(x.parent.processChildren.contains(this)){
                parentAmbient = x;
                x.addProcessAsChild(this);
            }else{System.out.printf("Process %s is not a Sibling of Ambient %s\n", this.name, x.name);}
        }else{System.out.printf("Process %s is not allowed to enter Ambient %s\n", this.name, x.name);}
    }
    
    public void mvout(Ambient x){
        if(x.allowExit){
            if(x.processChildren.contains(this)){
                parentAmbient = x.parent;
                x.removeProcessAsChild(this);
            }else{System.out.printf("Process %s is not a Child of Ambient %s\n", this.name, x.name);}
        }else{System.out.printf("Process %s is not allowed to exit Ambient %s\n", this.name, x.name);}
    }
    
    // acid has some issues, alternatives are being explored
    /*public void acid(){
        this.parentAmbient.processChildren.remove(this);
        this.parentAmbient = null;
        System.out.printf("Process %s has been dissolved\n", this.name);
        destroy(this);
    }
    
    private void destroy(Process x){
        x = null;
    }*/
    

    /** This method is meant to be overwritten by the user
     * I will finish this doc later
     *
     */
        public abstract void runProcess();
    
}
