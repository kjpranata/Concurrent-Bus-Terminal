package mainterminal;

//declaring and inheriting variable
public class Security implements Runnable{
    MainTerminal Terminal;
    
    public Security(MainTerminal Terminal){
        this.Terminal=Terminal;
    }
    @Override
    public void run() {
        while(Terminal.custID < 150 || (50-Terminal.mTerminal.availablePermits())>0){
            if(Terminal.mTerminal.availablePermits()==0){
                Terminal.closeEntrance();
            }
        }
    }
}

