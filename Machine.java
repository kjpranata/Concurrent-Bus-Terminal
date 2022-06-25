package mainterminal;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.*;

//declaring and inheriting variable
public class Machine {
    Semaphore tMachine = new Semaphore(1,true);
    Semaphore mQueue = new Semaphore(5,true);
    Ticket ticket;
    tNumber tCounting;
    boolean mBroken,mBusy;
    
    public Machine(tNumber tCounting){
        mBroken = false;
        this.tCounting = tCounting;
    }
    
   // repair function
    public void repair(){
        System.err.println("["+new Date()+"]"+"[Mechanic] : Machine has been repaired.");
        mBroken = false;
    }
    
    // enter queue function
    public void EnterQueue(Customer cust){
        //check whether queue has slot or not
        if(mQueue.availablePermits()==0){
            // for testing purpose to check queue slot 
//            System.err.println("Ticket Counter Queue is Full. (Queue = "+(5-mQueue.availablePermits()+")"));
        }else{
            //if queue has slot
            try {
                mQueue.acquire(); // enter queue
                System.out.println("["+new Date()+"]"+"[Customer "+cust.custID+"] : Queued for Ticket Machine. (Queue = "+(5-mQueue.availablePermits()+")"));
                Thread.sleep(100);
            } catch (InterruptedException ex) {}
        }
    }
    
    //buy ticket function
    public Ticket buyTicket(Customer cust, String wArea){
        while(true){
            try {
                //randomize for broken machine, 2.5% chance
                int randomize = new Random().nextInt(40);
                if(randomize == 0){
                    mBroken = true;
                    System.err.println("["+new Date()+"]"+"[Mechanic] : Machine is Broken.");
                    mQueue.release(); // exit queue if staff go to toilet
                    System.out.println("["+new Date()+"]"+"[Customer "+cust.custID+"] : has left the Ticket Machine Queue due to Broken Machine. "
                            + "(Queue = "+(5-mQueue.availablePermits())+")");
                    return null;
                }else{
                    //if machine is not broken
                    tMachine.acquire(); //enter ticket machine
                    mQueue.release(); // exit queue
                    
                    //double check whether the machine is broken or not
                    if(mBroken == true){
                        tMachine.release(); // leave machine if broken
                        System.out.println("["+new Date()+"]"+"[Customer "+cust.custID+"] : has left the Ticket Machine Queue due to Broken Machine. "
                                + "(Queue = "+(5-mQueue.availablePermits())+")");
                        break;
                    }
                    
                    //customer use ticket machine for 4 seconds
                    System.out.println("["+new Date()+"]"+"[Customer "+cust.custID+"]"
                            + " : is using the Ticket Machine. (Queue = "+(5-mQueue.availablePermits())+")");
                    try{
                        Thread.sleep(4000);
                    }catch(InterruptedException ex){}
                    
                    int tNo=tCounting.incrementID(); // ticket generator
                    ticket = new Ticket(tNo, wArea); // assign ticket 
                    System.out.println("["+new Date()+"]"+"[Customer "+cust.custID+"] : has bought Ticket "+ticket.tNo+" & Waiting Area "+ticket.wArea+".");
                    System.out.println("["+new Date()+"]"+"[Customer "+cust.custID+"] : has left the Ticket Machine.");
                    tMachine.release(); // leave ticket machine
                    Thread.sleep(100);
                    return ticket; // pass ticket value
                }
            } catch (InterruptedException ex) {}}
        return null;
    }
}