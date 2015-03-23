package PJ3;

import java.util.*;

//--------------------------------------------------------------------------
//
// Define simulation queues in a service area. Queues hold references to Customer 
// and Teller objects
//
// Customer (FIFO) queue is used to hold waiting customers. If the queue is too long
// (i.e. >  customerQLimnit), customer goes away without entering customer queue
//
// There are several tellers in a service area. Use PriorityQueue to 
// hold BUSY tellers and FIFO queue to hold FREE tellers, 
// i.e. a teller that is FREE for the longest time should start be used first.
//
// To handle teller in PriorityQueue, we need to define comparator 
// for comparing 2 teller objects. Here is a constructor from Java API:
//
// 	PriorityQueue(int initialCapacity, Comparator<? super E> comparator) 
//
// For priority queue, the default compare function is "natural ordering"
// i.e. for numbers, minimum value is returned first
//
// User can define own comparator class for PriorityQueue.
// For teller objects, we like to have smallest end busy interval time first.
//
// The following class define compare() for two tellers :

class CompareTeller implements Comparator<Teller>{
	// overide compare() method
 	public int compare(Teller o1, Teller o2) {
		return o1.getEndBusyIntervalTime() - o2.getEndBusyIntervalTime(); 
	}
}

class ServiceArea {

  
  // Private data fields:
  
  // define one priority queue 
  private PriorityQueue <Teller> busyTellerQ;

  // define two FIFO queues
  private Queue<Customer> customerQ;
  private Queue<Teller> freeTellerQ;

  // define customer queue limit
  private int customerQLimit;


  // Constructor 
  public ServiceArea() 
  {
	// add statements
      customerQ = new ArrayDeque<Customer>();
      freeTellerQ = new ArrayDeque<Teller>();
      customerQLimit = 0;
  }

  // Constructor 
  public ServiceArea(int numTellers, int customerQlimit, int startTellerID)
  {
	// use ArrayDeque to construct FIFO queue objects

	// construct PriorityQueue object
 	// overide compare() in Comparator to compare Teller objects
	busyTellerQ= new PriorityQueue<Teller>( numTellers, 
						new CompareTeller()); 

	// initialize customerQlimit
        // Construct Teller objects and insert into FreeTellerQ

	// add statements
        customerQ = new ArrayDeque<Customer>();
        freeTellerQ = new ArrayDeque<Teller>(numTellers);
        
        for(int i = 0; i < numTellers; i++){
            Teller nextTeller = new Teller(startTellerID + i);
            insertFreeTellerQ(nextTeller);
        }
        customerQLimit = customerQlimit;
  }

  public Teller removeFreeTellerQ()
  {
	// remove and return a free teller
	// Add statetments
	return freeTellerQ.poll();
  }

  public Teller removeBusyTellerQ() 
  {
	// remove and return a busy teller
	// Add statetments
	return busyTellerQ.poll();
  }

  public Customer removeCustomerQ()
  {
	// remove and return a customer 
	// Add statetments
	return customerQ.poll();
  }

  public void insertFreeTellerQ(Teller teller)
  {
	// insert a free teller
	// Add statetments
      freeTellerQ.offer(teller);
  }

  public void insertBusyTellerQ(Teller teller)
  {
	// insert a busy teller
	// Add statetments
      busyTellerQ.offer(teller);
  }

  public void insertCustomerQ(Customer customer)
  {
	// insert a customer 
	// Add statetments
      customerQ.offer(customer);
  }

  public boolean emptyFreeTellerQ()
  {
	// is freeTellerQ empty?
	// Add statetments
      if(freeTellerQ.isEmpty()){
          return true;
      } else {
            return false;
        }
  }

  public boolean emptyBusyTellerQ()
  {
	// is busyTellerQ empty?
	// Add statetments
      if(busyTellerQ.isEmpty()) {
          return true;
     } else {
          return false;
     }
  }
  public boolean emptyCustomerQ()
  {
	// is customerQ empty?
	// Add statetments
      if(customerQ.isEmpty()) {
          return true;
      } else {
            return false;
      }
  }

  public int numFreeTellers()
  {
	// get number of free tellers
	// Add statetments
	return freeTellerQ.size();
  }

  public int numBusyTellers()
  {
	// get number of busy tellers
	// Add statetments
	return busyTellerQ.size();
  }

  public int numWaitingCustomers()
  {
	// get number of customers 
	// Add statetments
	return customerQ.size();
  }

  public Teller getFrontBusyTellerQ() 
  {
	// get front of busy tellers
	// "retrieve" but not "remove"
	// Add statetments
	return busyTellerQ.peek();
  }

  public boolean isCustomerQTooLong()
  {
	// is customerQ too long?
	// Add statetments
	return customerQ.size() >= customerQLimit;
  }

  public void printStatistics()
  {
  	System.out.println("\t# waiting customers : "+numWaitingCustomers());
  	System.out.println("\t# busy tellers      : "+numBusyTellers());
  	System.out.println("\t# free tellers      : "+numFreeTellers());
  }

  public static void main(String[] args) {

        // quick check

        ServiceArea sc = new ServiceArea(4, 5, 1001);
        Customer c1 = new Customer(1,18,10);
        Customer c2 = new Customer(2,33,10);
        Customer c3 = new Customer(3,21,10);
        Customer c4 = new Customer(3,37,10);
  	sc.insertCustomerQ(c1);
  	sc.insertCustomerQ(c2);
  	sc.insertCustomerQ(c3);
	System.out.println(""+sc.customerQ);
	System.out.println("Remove customer:"+sc.removeCustomerQ());
	System.out.println("Remove customer:"+sc.removeCustomerQ());
	System.out.println("Remove customer:"+sc.removeCustomerQ());

	System.out.println(""+sc.freeTellerQ);
	Teller p1=sc.removeFreeTellerQ();
	Teller p2=sc.removeFreeTellerQ();
	Teller p3=sc.removeFreeTellerQ();
	Teller p4=sc.removeFreeTellerQ();
	System.out.println("Remove free teller:"+p1);
	System.out.println("Remove free teller:"+p2);
	System.out.println("Remove free teller:"+p3);
	System.out.println("Remove free teller:"+p4);

        p1.freeToBusy (c1, 13);
        p2.freeToBusy (c2, 13);
        p3.freeToBusy (c3, 13);
        p4.freeToBusy (c4, 13);
	sc.insertBusyTellerQ(p1);
	sc.insertBusyTellerQ(p2);
	sc.insertBusyTellerQ(p3);
	sc.insertBusyTellerQ(p4);
	System.out.println(""+sc.busyTellerQ);
	p1=sc.removeBusyTellerQ();
	p2=sc.removeBusyTellerQ();
	p3=sc.removeBusyTellerQ();
	p4=sc.removeBusyTellerQ();
	System.out.println("Remove busy teller:"+p1);
	System.out.println("Remove busy teller:"+p2);
	System.out.println("Remove busy teller:"+p3);
	System.out.println("Remove busy teller:"+p4);

   }


};

