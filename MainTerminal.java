package mainterminal;
import java.util.Date;
import java.util.concurrent.*;
import java.util.Random;

public class MainTerminal extends Thread {
    Semaphore mTerminal = new Semaphore (50,true); //max 50 people in the terminal
    // semaphore for gate so that customers will enter the main terminal through the gate 1 by 1
    Semaphore mGate = new Semaphore(1,true); 
    Counter A,B;
    Machine tMachine;
    tNumber tCounting;
    boolean open;
    public int custID;
    
    
    public static void main(String[] args) {
        MainTerminal Terminal = new MainTerminal(); // declaring the whole main terminal
        Terminal.start();
    }
    
    public MainTerminal(){
        //declaring variables that is going to be used in the terminal
        open = true;
        tCounting = new tNumber();
        A = new Counter(tCounting,"A");
        B = new Counter(tCounting,"B");
        tMachine = new Machine(tCounting);
        custID = 1;
    }
    
    @Override
    public void run(){
        //executor service built in function 
        ExecutorService terminal = Executors.newCachedThreadPool();
        //submiting and passing value to all necessary thread to the thread pool so that the executor service will run the threads
        terminal.submit(new Security(this));
        terminal.submit(new Mechanic(tMachine, this));
        terminal.submit(new Staff(A,this));
        terminal.submit(new Staff(B,this));
        
        //customer generator, will be generated every 1-4 seconds
        while(custID <= 150){
            terminal.submit(new Customer(custID, this, A, B, tMachine));
            custID++;
            try{
                    Thread.sleep(1000*(1+new Random().nextInt(3)));
            }catch(Exception e){}
        }
        if(custID > 150){
            //to stop the threads so that the system can build successfully
            terminal.shutdown();
            try{
                if(!terminal.awaitTermination(20, TimeUnit.SECONDS)){
                    terminal.shutdownNow(); 
                }
            }catch(Exception e){
                terminal.shutdownNow();
            }
        } 
    }
    
    //function for customer to enter the terminal
    public void Entry(Customer cust)
    {
        if(open==false)
        {
            System.err.println("["+new Date()+"]"+"[Security] : is blocking the entrance. "
                    + "(Current Terminal Population : "+(50-mTerminal.availablePermits())+")");
            System.out.println("["+new Date()+"]"+"[Customer "+cust.custID+"] : Waiting for the gate to open.");
        }
        try
        {
            mGate.acquire(); //this is to let a customer pass the gate
            mTerminal.acquire(); //this is to let the customer who passed the gate to enter the main terminal
            Thread.sleep(20);
            
            //this is to decide whether the customer enter from east or west entrance
            int rand=new Random().nextInt(2);
            if(rand==0){
                System.out.println("["+new Date()+"]"+"[Customer "+cust.custID+"] : has entered the Bus Terminal from East Entrance. "
                        + "(Current Population = "+(50-mTerminal.availablePermits())+")");
                Thread.sleep(100);
                mGate.release();
            }else {
                System.out.println("["+new Date()+"]"+"[Customer "+cust.custID+"] : has entered the Bus Terminal from West Entrance. "
                        + "(Current Population = "+(50-mTerminal.availablePermits())+")");
                Thread.sleep(100);
                mGate.release();
            }
        }
        catch(Exception e){}  
    }
    
    //function to close the gate when the terminal is full
    public void closeEntrance(){
        open = false;
        System.err.println("["+new Date()+"]"+"[Security] : Terminal is Full. Terminal entrance is temporary closed.");
        try{
            mTerminal.acquire(15); // this is to let a number of customers pass
            System.err.println("["+new Date()+"]"+"[Security] : Terminal entrance is now open.");
            mTerminal.release(15); // after a number of customers has left the terminal, the gate will be open again
        }catch(Exception e){}
        open=true;
    }
}
