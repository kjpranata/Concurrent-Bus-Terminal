package mainterminal;

//declaring staff as thread and inheriting necessary variable
public class Staff implements Runnable {
    Counter NewCounter;
    MainTerminal Terminal;
    
    public Staff(Counter NewCounter, MainTerminal Terminal){
        this.NewCounter = NewCounter;
        this.Terminal = Terminal;
    }
    
    @Override
    public void run() {
        //thread runs while the condition is met
        while(Terminal.custID < 150 || (50-Terminal.mTerminal.availablePermits())>0){
            //do nothing if staff do not want to go to toilet
            if(NewCounter.cToilet == false){
                try{
                    Thread.sleep(1000);
                }catch(Exception e){}
            }else{ //staff wants to go to toilet
                try{
                    Thread.sleep(5000); // having toilet break for 5 seconds
                }catch(InterruptedException e){}
                NewCounter.FinishBreak(); // finish break function
            }
        }
    }
}

