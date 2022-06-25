package mainterminal;

import java.util.Date;

//declaring and inheriting variable
public class Mechanic implements Runnable {
    Machine tMachine;
    MainTerminal Terminal;

    public Mechanic(Machine tMachine, MainTerminal Terminal){
        this.tMachine = tMachine;
        this.Terminal = Terminal;
    }
    @Override
    public void run() {
        //run while condition met
        while(Terminal.custID < 150 || (50-Terminal.mTerminal.availablePermits())>0){
            //do nothing if machine is not broken
            if(tMachine.mBroken == false){
                try{
                    Thread.sleep(1000);
                }catch(InterruptedException e){}
            }else{
                //if machine broken mechanic go fix
                try{
                    System.err.println("["+new Date()+"]"+"[Mechanic] : is coming to fix the Ticket Machine.");
                    Thread.sleep(3000); // move to the ticket machine take 3 seconds
                    System.err.println("["+new Date()+"]"+"[Mechanic] : is fixing the Ticket Machine.");
                    Thread.sleep(3000); // repairing the machine take 3 seconds
                }catch(InterruptedException e){}
                tMachine.repair(); // machine repaired function
            }
        }
    }
}
