package Jambient;
/**
 *
 * @author Benjamin D. Horne
 * 
 * This is the Ambient class for the Java library Jambient.
 */
import java.util.ArrayList;
import java.lang.Runnable;
import java.lang.Thread;
import com.googlecode.mobilityrpc.quickstart.QuickTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Ambient implements Runnable {
    
    // traits of Ambient objects
    ArrayList<Ambient> childList; 
    ArrayList<Ambient> siblingList;
    Ambient parent;
    String name;
    ArrayList<Process> processChildren;
    boolean allowEnter, allowExit; // used for approximating objective moves
    
    // private vars used for threading and utilities
    private String inorout;
    private final Object in;
    private final Object out;
    private Ambient tempAmbientToMoveInto;
    private Ambient tempAmbientThatsMoving;
    private Ambient tempParent;
    private Ambient tempAmbientMovingOut;
    private Thread t_out;
    private Thread t_in;
    
    
    /**
     * This Constructor should be paired with make    
     * @param n is a String for the Ambients name, it is recommended that this name is the same as your Object name.
     */
    public Ambient(String n){
       this.out = new Object();
       this.in = new Object();
       name = n;
       childList = new ArrayList<>();
       siblingList = new ArrayList<>();
       processChildren = new ArrayList<>();
       allowEnter = true; allowExit = true; // by default we allow enter and exit of obejctive moves
    }
    
    /**
     * Make must be ran on all Ambients before use.
     * This will set up parents, children, and siblings.
     * @param initialParent Ambient to start in, or null to start at the root
     */
    public void make(Ambient initialParent){
        if(initialParent == null){
            Ambient root = new Ambient("root");
            this.parent = root;
            root.childList.add(this);
            for (Ambient i : root.childList) {
                if (!(i.name.equals(this.name))){
                    this.setSibling(i);
                    i.setSibling(this);
                }
            }
        }
        else{
            this.parent = initialParent;
            initialParent.childList.add(this);
            for (Ambient i : initialParent.childList) {
                if (!(i.name.equals(this.name))){
                    this.setSibling(i);
                    i.setSibling(this);
                }
            }
        }
    }
    
    @Override
    public void run(){ //this run method is for when threads are fully implemented, it will be used for blocking waiting and notifying
        if(null != inorout)switch (inorout) {
            case "in":
                synchronized(in){
                    if(!(tempAmbientThatsMoving.siblingList.contains(tempAmbientToMoveInto))){
                        System.out.printf("Ambient %s blocked from moving in %s, they are not a siblings.\n", tempAmbientThatsMoving.name, tempAmbientToMoveInto.name);
                        //while(!(tempAmbientThatsMoving.siblingList.contains(tempAmbientToMoveInto))){}
                    }else {System.out.printf("Notified %s to move in\n", tempAmbientThatsMoving.name);  in.notify();}
                    break;
            }
            case "out":
                synchronized(out){
                    if(!(tempParent == tempAmbientMovingOut.parent)){
                        System.out.printf("Ambient %s blocked from moving out %s, %s is not the parent.\n", tempAmbientMovingOut.name, tempParent.name, tempParent.name);
                        //while(!(tempParent == tempAmbientMovingOut.parent)){}
                    }else {System.out.printf("Notified %s to move out\n", tempAmbientMovingOut.name); out.notify();}
                    break;
                }
            default:
                System.err.println("Something went wrong... sorry");
                break;
        } 
    }
    
    public void migrate_runall(String address){
        System.out.printf("Ambient %s is moving to machine at address %s\n", this.name, address);
        final Ambient temp = this;
        QuickTask.execute(address, new Runnable() {public void run() {
            for(Process p : temp.processChildren ){
                p.runProcess();
            }
            for(Ambient i : temp.childList){
                 for(Process q : i.processChildren ){
                    q.runProcess();
                }
            }
        }});
    }
    
    public void migrate_runone(String address, String pName){
        System.out.printf("Ambient %s is moving to machine at address %s\n", this.name, address);
        final Ambient temp = this;
        Process tempP = null;
        for(Process p : temp.processChildren){
            if(p.name.equals(pName)){
               tempP = p;
            }
        }
        if(tempP == null){
            for(Ambient i : temp.childList){
                for(Process q : i.processChildren ){
                    if(q.name.equals(pName)){
                        tempP = q;
                    }
                }
            }
        }
        final Process finalP = tempP;
        if(finalP != null){
        QuickTask.execute(address, new Runnable() {public void run() {
                finalP.runProcess();
        }});}else{System.out.printf("Prcess %s not found in Ambient %s", pName, this.name);}
    }
    
    public Ambient getParent(){
        return parent;
    }
    
    public ArrayList<Ambient> getChildren(){
        return this.childList;
    }
    
    public ArrayList<Ambient> getSiblings(){
        return this.siblingList;
    }
    
    /**
     * This method is based on the in keyword in the Ambient Calculus.
     * If I have an Ambient n and Ambient m, the method will operate as so:
     * n.in(m) means n will go into m, thus becoming a child of m and get
     * new siblings (the other children of m)
     * @param x is the Ambient to enter
     */
    
    public void in(Ambient x){ // will wait come back to the try?
        synchronized(in){
            try{
                tempAmbientToMoveInto = x;
                tempAmbientThatsMoving = this;
                // this is simple sibling blocking without threads, comment out when full threads are implemented
                if(!(tempAmbientThatsMoving.siblingList.contains(tempAmbientToMoveInto))){
                    System.out.printf("Ambient %s blocked from moving in %s, they are not a siblings.\n", tempAmbientThatsMoving.name, tempAmbientToMoveInto.name);
                }else{
                    // this is for sibling blocking and waiting, currently this feature does not give back to user main or work with mobility
                    /*inorout = "in";
                    t_in = new Thread(this);
                    t_in.start();
                    System.out.printf("Ambient %s is Waiting to move into Ambient %s\n", this.name, x.name );
                    in.wait();*/
                    x.setChild(tempAmbientThatsMoving);
                    System.out.printf("Ambient %s moved into Ambient %s.\n", tempAmbientThatsMoving.name, tempAmbientToMoveInto.name);
                }
            } catch(Exception e){/*catch(InterruptedException e){
                System.err.printf("Ambient %s was interrupted while waiting", this.name);*/ // this is also for sibling blocking and waiting with threads
                System.out.println("Something has went horribly wrong!");
            }
        }
    }
    
    /**
     * This method is based on the out keyword in the Ambient Calculus.
     * If I have an Ambient n and Ambient m, the method will operate as so:
     * n.out(m) means n will go outside of m, thus becoming a sibling of m
     * @param x is the Ambient to exit
     */
    public void out(Ambient x){ // will wait come back to the try?
        synchronized(out){
            try{  
                tempParent = x;
                tempAmbientMovingOut = this;
                // this is simple sibling blocking without threads, comment out when full threads are implemented
                if(!(tempParent == tempAmbientMovingOut.parent)){
                    System.out.printf("Ambient %s blocked from moving out %s, %s is not the parent.\n", tempAmbientMovingOut.name, tempParent.name, tempParent.name);
                }else{
                    // this is for sibling blocking and waiting with threads, currently this feature does not give back to user main or work with mobility                
                    /*inorout = "out";
                    t_out = new Thread(this);
                    t_out.start();
                    System.out.printf("Ambient %s is Waiting to move out of Ambient %s\n", this.name, x.name );
                    out.wait();*/
                    x.childList.remove(tempAmbientMovingOut);
                    tempAmbientMovingOut.parent = tempParent.parent;
                    for(Ambient i : tempParent.parent.childList){
                        tempAmbientMovingOut.setSibling(i);
                    }
                    tempParent.parent.childList.add(tempAmbientMovingOut);
                    System.out.printf("Ambient %s moved out of Ambient %s.\n", tempAmbientMovingOut.name, tempParent.name, tempParent.name);
                }
            }catch(Exception e){ /*catch(InterruptedException e){
                System.err.printf("Ambient %s was interrupted while waiting", this.name);*/ // this is also for sibling blocking and waiting with threads
                 System.out.println("Something has went horribly wrong!");
            }
        }
    }

    /**
     * This method is based on the open keyword in the Ambient Calculus.
     * If I have an Ambient n, the method will operate as so:
     * n.open() opens up n, meaning all contents inside n go to the parent 
     * and n is destroyed.
     */
    public void open(){
        // move its children up one level
        this.parent.childList.addAll(this.childList);
        // delete all references (childlist, sibling list)
        for(int i = 0; i < this.parent.childList.size(); i++){
            if (this.parent.childList.get(i).name.equals(this.name)){
                this.parent.childList.remove(i);
            }
        }
        for(int k = 0; k < this.siblingList.size(); k++){
            for(int j = 0; j < this.siblingList.get(k).siblingList.size(); j++){
                if (this.siblingList.get(k).siblingList.get(j).name.equals(this.name)){
                    this.siblingList.get(k).siblingList.remove(j);
                }
            }
        }
        this.parent.processChildren.addAll(this.processChildren);
        System.out.printf("Ambient %s has been openned \n", this.name);
        this.name = null; //is there a better way to make itself null?
        destroy(this);
        
    }
    
    private void destroy(Ambient x){
        x = null;
    }
    
    /**
     * This Method checks if calling Ambient is a child of x. 
     * Example: n.isachild(x) this method will return true if n is a child of x
     * false if n is not a child of x.
     * @param x an Ambient that could be a parent
     * @return true if Ambient is a child of x
     */
    public boolean isachild(Ambient x){
        for(Ambient i : x.childList){
            if(i.name.equals(this.name)){
                return true;
            }
        }
        return false;  
    }
    
    /**
     * This Method checks if calling Ambient is a sibling of x. 
     * Example: n.isasibling(x) this method will return true if n is a sibling of x
     * false if n is not a sibling of x.
     * @param x an Ambient that could be a sibling
     * @return true if Ambient is a sibling of x
     */
    public boolean isasibling(Ambient x){
        for(Ambient i : x.siblingList){
            if(i.name.equals(this.name)){
                return true;
            }
        }
        return false;  
    }
    
    public void printSiblings(){
        for(Ambient i : this.siblingList){
            System.out.printf("Siblings: %s\n", i.name);
        }
    }
        
    /**
     * This Method checks if calling Ambient is a parent of x. 
     * Example: n.isaparent(x) this method will return true if n is a parent of x
     * false if n is not a parent of x.
     * @param x an Ambient that could be a child
     * @return true if Ambient is a parent of x
     */
    public boolean isparent(Ambient x){
        for(Ambient i : this.childList){
            if(x.name.equals(i.name)){
                return true;
            }
        }
        return false;
    }
    
    public void setObjectiveMoveSecurity(boolean enter, boolean exit){
        allowEnter = enter;
        allowExit = exit;
    }
    
    /**
     * acquire is an implementation of the locks example in Cardelli and Gordons original paper
     * 
     */
    public void acquire(){
        this.open();
    }
    
    /**
     * release is an implementation of the locks example in Cardelli and Gordons original paper
     */
    public void release(){
        // move everything out
        if(!(this.childList.isEmpty())){
            for(int i = 0; i < this.childList.size(); i++){
                this.childList.get(i).out(this);
            }
        }
        if(!(this.processChildren.isEmpty())){
            for(int i = 0; i < this.processChildren.size(); i++){
                this.processChildren.get(i).mvout(this);
            }
        }
    }
    
    // Primary Public Methods Above (The methods the user can access)
    // Private and Protected Utility Methods Below (The methods the user cannot access)
    
    protected void addProcessAsChild(Process p){
        this.processChildren.add(p);
        System.out.printf("Process %s has moved into Ambient %s\n", p.name, this.name);
    }
    
    protected void removeProcessAsChild(Process p){
        boolean remove = this.processChildren.remove(p);
        if(!remove){
            System.err.print("Process not Child of Ambient");
        }else{System.out.printf("Process %s has moved out of Ambient %s\n", p.name, this.name);}
    }
    
    private void setChild(Ambient x){
        this.childList.add(x); // adds x to childlist
        x.parent.childList.remove(x); // removes x from previous parents child list
        if(!x.siblingList.isEmpty()){
            x.clearSiblings();// clear siblings list
        }
        x.parent = this; // set this ambeint as new parent of child
        // fill siblings list with other children of new parent
        for(Ambient i : this.childList){
            if (!(i.name.equals(x.name))){
                x.setSibling(i);
                i.setSibling(x);
            }
        }
    }
    
    private void setSibling(Ambient x){
        this.siblingList.add(x);
    }
    
    private void clearSiblings(){
        this.siblingList.clear();
    }
    
} // end of class
