package mainterminal;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.Semaphore;

//declaring and inheriting variable
public class Counter {
    Semaphore TicketCounter = new Semaphore(1,true);
    Semaphore cQueue = new Semaphore(5,true);
    Ticket ticket;
    String cName;
    tNumber tCounting;
    boolean cToilet,cBusy;
    
    public Counter(tNumber tCounting, String cName){
        this.tCounting = tCounting;
        this.cName = cName;
        cToilet = false;
    }
    
    //finisih toilet break function
    public void FinishBreak(){
        System.err.println("["+new Date()+"]"+"[Counter "+cName+"] : Staff has returned.");
        cToilet = false;
        System.err.println("["+new Date()+"]"+"[Counter "+cName+"] : is now available.");
        
    }
    
    //enter queue function
    public void EnterQueue(Customer cust){
        if(cQueue.availablePermits()==0){ //checking whether the queue has slot or not
            //for testing purpose to check queue slot
//            System.err.println("Counter "+cName+" Queue is Full. (Queue = "+(5-cQueue.availablePermits()+")"));
        }else{
            //if queue has available slot
            try {
                cQueue.acquire();//enter the queue
                System.out.println("["+new Date()+"]"+"[Customer "+cust.custID+"] : queued for Counter "+cName+". (Queue = "+(5-cQueue.availablePermits()+")"));
                Thread.sleep(100);
            } catch (InterruptedException ex) {}
        }
    }
    
    // buy ticket function
    public Ticket buyTicket(Customer cust, String wArea){
        while(true){
                try{
                    //randomize for toilet break, 2.5% chance to go to toilet
                    int randomize = new Random(). nextInt(40);
                    if (randomize == 0){
                        cToilet = true;
                        System.err.println("["+new Date()+"]"+"[Staff "+cName+"] : is having toilet break.");
                        cQueue.release();
                        System.out.println("["+new Date()+"]"+"[Customer "+cust.custID+"] : has left Ticket Counter "+cName+" queue due to Staff toilet break. "
                                + "(Queue = "+(5-cQueue.availablePermits())+")");
                        return null;
                    }else{ 
                        //if staff do not go to toilet
                        TicketCounter.acquire(); // enter the ticket counter
                        cQueue.release(); // leave the queue
                        
                        //double check whether the staff is on toilet or not
                        if(cToilet == true){
                            TicketCounter.release();
                            System.out.println("["+new Date()+"]"+"[Customer "+cust.custID+"] : has left Ticket Counter "+cName+" queue due to Staff toilet break. "
                                + "(Queue = "+(5-cQueue.availablePermits())+")");
                            break;
                        }
                        //staff serve customer for 8 seconds long
                        System.out.println("["+new Date()+"]"+"[Counter "+cName+"] : is serving Customer "+cust.custID+"."
                                + "(Queue = "+(5-cQueue.availablePermits())+")");
                        try{
                            Thread.sleep(8000); 
                        } catch(InterruptedException e){}
                        
                        int tNo=tCounting.incrementID(); //ticket generator
                        ticket = new Ticket(tNo,wArea); // assign the ticket value
                        System.out.println("["+new Date()+"]"+"[Customer "+cust.custID+"] : has bought Ticket "+ticket.tNo+" Waiting Area "+ticket.wArea);
                        TicketCounter.release(); // leave the ticket counter
                        System.out.println("["+new Date()+"]"+"[Customer "+cust.custID+"] : has left Ticket Counter "+cName+".");
                        return ticket;   // pass ticket value
                    }
                }catch(InterruptedException e){}
        }
        return null;
    }
}
