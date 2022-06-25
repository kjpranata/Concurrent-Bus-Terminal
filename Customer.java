package mainterminal;

import java.util.Date;
import java.util.Random;

// declaring Customer as threads along with necessary variable
public class Customer implements Runnable{
    int custID;
    int randomize;
    MainTerminal Terminal;
    Machine tMachine;
    Counter A,B;
    
    Ticket ticket = null;
    String WaitingArea;
    
    // inheriting variable that is taken from the main terminal.
    public Customer(int custID, MainTerminal Terminal, Counter A, Counter B, Machine tMachine){
        this.custID = custID;
        this.Terminal = Terminal;
        this.A = A;
        this.B = B;
        this.tMachine = tMachine;
    }
    
    // function to random number
    public int Random1(){
        randomize = new Random().nextInt(3);
        return randomize;
    }
    
    // function to random number that will be used for customer to decide counters
    public int Random2(){
        while(true){
            randomize = new Random().nextInt(3);
            switch(randomize){
                case 0:
                    if(tMachine.mBroken == true || tMachine.mQueue.availablePermits() == 0){
                        break;
                    }else{
                        return randomize;
                    }
                case 1:
                    if(A.cToilet == true || A.cQueue.availablePermits() == 0){
                        break;
                    }else{
                        return randomize;
                    }
                case 2:
                    if(B.cToilet == true || B.cQueue.availablePermits() == 0){
                        break;
                    }else{
                        return randomize;
                    }
            }
        }
    }
    
    @Override
    public void run() {
       System.out.println("["+new Date()+"]"+"[Customer "+custID+"] : is outside the terminal");
       Terminal.Entry(this); // calling entry function so that the customer can enter the terminal
       
       // this is to decide which waiting area that the customer want to buy and enter
        Random1();
        switch (randomize) {
            case 0:
                WaitingArea = "1";
                break;
            case 1:
                WaitingArea = "2";
                break;
            default:
                WaitingArea = "3";
                break;
        }
       
       try{
           Thread.sleep(1000);
       }catch (InterruptedException e){}
       
       // this is to buy ticket and decide which counter that the customer will use
       while(ticket == null){
           Random2();
           switch (randomize) {
               //if the randomize get 0, customer will enter ticket machine queue
               case 0:
                   if(tMachine.mQueue.availablePermits()> 0 && tMachine.mQueue.availablePermits()<=5){ // check for available slot in the queue
                       tMachine.EnterQueue(this); // enter the queue
                       while(true){
                           // check the machine is broken or not 
                           if(tMachine.mBroken == true){
                               tMachine.mQueue.release(); // customer leave the queue if the machine is broken
                               System.out.println("["+new Date()+"]"+"[Customer "+custID+"] : has left the Ticket Machine Queue due to Broken Machine. "
                                       + "(Queue = "+(5-tMachine.mQueue.availablePermits())+")");
                               break;
                           }
                           //check the ticket machine is busy or not
                           if(tMachine.tMachine.availablePermits()==1 && tMachine.mBroken == false){
                                ticket = tMachine.buyTicket(this, WaitingArea); // buy the ticket if the machine is not busy
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException ex) {}
                                break;
                           }
                       }
                   }else{
                       // this is just for testing purpose to make sure whether the queue can fit 5 people or not
//                       System.err.println("Machine queue is full. Customer "+custID+" Unable to queue.");
                       break;
                   }
                   break;
               case 1:
                   //if the randomize get 1, customer will enter counter a queue
                   if(A.cQueue.availablePermits()> 0 && A.cQueue.availablePermits()<=5){ // check for available slot in the queue
                       A.EnterQueue(this); // enter queue
                       while(true){
                           //check whether the staff is in the toilet or not 
                           if(A.cToilet == true){
                               A.cQueue.release(); //if the staff is having toilet break, customer will leave the queue
                               System.out.println("["+new Date()+"]"+"[Customer "+custID+"] : has left the Counter "+A.cName+" queue due to Staff Toilet Break. "
                                       + "(Queue = "+(5-A.cQueue.availablePermits())+")");
                               break;
                           }
                           //check whether whether Counter A is busy or not
                           if(A.TicketCounter.availablePermits()==1 && A.cToilet == false){
                               ticket = A.buyTicket(this, WaitingArea);// buy ticket
                               try {
                                   Thread.sleep(100);
                               } catch (InterruptedException ex) {}
                               break;
                           }
                       }
                   }else{
                       //testing purpose to check max queue slot is 5
//                       System.err.println("Counter A queue is full. Customer "+custID+" unable to queue.");
                       break;
                   }
                   break;
               case 2:
                    //if the randomize get 2, customer will enter counter b queue
                   if(B.cQueue.availablePermits()> 0 && B.cQueue.availablePermits()<=5){ // check for available slot in the queue
                       B.EnterQueue(this); // enter queue
                       while(true){
                           //check whether the staff is in the toilet or not
                           if(B.cToilet == true){
                               B.cQueue.release();// if the staff is having toilet break, customer will leave the queue
                                System.out.println("["+new Date()+"]"+"[Customer "+custID+"] : has left Ticket Counter "+B.cName+" queue due to Staff toilet break "
                                       + "(Queue = "+(5-B.cQueue.availablePermits())+")");
                               break;
                           }
                           //check whether counter B is busy or not
                           if(B.TicketCounter.availablePermits()==1 && B.cToilet == false){
                               ticket = B.buyTicket(this, WaitingArea); // buy ticket
                               try {
                                   Thread.sleep(100);
                               } catch (InterruptedException ex) {}
                               break;
                           }
                       }
                   }else{
                       //testing purpose to check queue slot is 5 or not
//                       System.err.println("Counter B queue is full. Customer "+custID+" unable to queue.");
                       break;
                   }
                   break;
           }
       }
//        }
      
       try{
           Thread.sleep(2500);
       }catch(InterruptedException e){}
       
       try{
           //ASSUME ENTERING WAITING AREA MEANS LEAVING THE TERMINAL FOR PART 1 PURPOSES.
            Terminal.mTerminal.release(); // enter waiting area and leave the terminal 
            System.out.println("["+new Date()+"]"+"[Customer "+custID+"] : has entered Waiting Area "+ticket.wArea+". ("
                    + "Current Population = "+(50-Terminal.mTerminal.availablePermits())+")");
            Thread.sleep(100); 
        }catch(InterruptedException e){}
    }
}