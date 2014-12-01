package jambienttester;

/**
 *
 * @author Benjamin D Horne
 */
import Jambient.*;

public class JambientTester {

    public static void main(String[] args) {
        // Initial creation of Ambients
        Ambient a = new Ambient("a");
        a.make(null);
        Ambient o = new Ambient("o");
        o.make(a);
        Ambient n = new Ambient("n");
        n.make(a);
        Ambient m = new Ambient("m");
        m.make(a);
        Ambient b = new Ambient("b");
        b.make(a);
        // Creation of Processses
        LocalProcess p = new LocalProcess("P", a);
        p.make();
        LocalProcess q = new LocalProcess("Q", m);
        q.make();
        LocalProcess r = new LocalProcess("R", m);
        r.make();
        
        //Simple Movement and Disolving
        n.in(m);
        n.out(m);
        m.in(n);
        m.open();
        
        //Objective Moves and Security
        p.mvin(o);
        p.mvout(o);
        o.setObjectiveMoveSecurity(false, false);
        p.mvin(o);
        
        //Simple Blocking
        o.in(n);
        b.in(o); // b will be blocked since it is not a sibling of o
        
        //Migration and Mobility
        n.migrate_runall("127.0.0.1");
        n.migrate_runone("127.0.0.1", "Q");
        
        //Lock Example
        //b.release();
        //b.acquire();
        
    }
    
}

class LocalProcess extends Jambient.Process{
    LocalProcess(String n, Ambient x){
        super(n, x);
    }
    @Override
    public void runProcess() {
        System.out.print("A Process Ran!\n");
    }
    
}




