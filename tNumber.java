package mainterminal;

//ticket number generator
public class tNumber {
    private static int tID = 0;
        
    public synchronized int incrementID(){
        tID++;
        return tID;
    }
}
