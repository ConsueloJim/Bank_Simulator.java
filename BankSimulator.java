package PJ3;

import java.util.*;
import java.io.*;

// You may add new functions or data in this class 
// You may modify any functions or data members here
// You must use Customer, Teller and ServiceArea
// to implement your simulator

class BankSimulator {

  // input parameters
  private int numTellers, customerQLimit;
  private int simulationTime, dataSource;
  private int chancesOfArrival, maxTransactionTime;
  String filename;

  // statistical data
  private int numGoaway, numServed, totalWaitingTime, totalCustomer;

  // internal data
  private int customerIDCounter;
  private ServiceArea servicearea; // service area object
  private Scanner dataFile;	   // get customer data from file
  private Random dataRandom;	   // get customer data using random function
  private int customerID;
  
  // most recent customer arrival info, see getCustomerData()
  private boolean anyNewArrival;  
  private int transactionTime;

  private Queue<Teller> tempBusy;
  private Queue<Teller> tempFree;
 
  // initialize data fields
  private BankSimulator()
  {
	// add statements
      numGoaway = 0;
      numServed = 0;
      totalWaitingTime = 0;
      customerIDCounter = 1;
  }

  private void setupParameters()
  {
	// read input parameters
	// setup dataFile or dataRandom
	// add statements
      Scanner input = new Scanner(System.in);
      System.out.print("****** Get Simulation Parameters ******\n");
      
      System.out.print("Enter simulation time in the form of a positive number: ");
      simulationTime = input.nextInt();
      
      System.out.print("Enter maximum transaction time of customers: ");
        maxTransactionTime = input.nextInt();
       
      System.out.print("Enter chances from 0 to 100 of new customer: ");
        chancesOfArrival = input.nextInt();
        
      System.out.print("Enter the number of tellers: ");
        numTellers = input.nextInt();
        
      System.out.print("Enter customer queue limit: ");
        customerQLimit = input.nextInt();
        
      System.out.print("Enter 1 to get data from file, 0 to generate random: ");
        dataSource = input.nextInt();
        
        if (dataSource == 1) {
            System.out.print("Enter filename: ");
            filename = input.next();
            try {
                dataFile = new Scanner(new File(filename));
            } catch (FileNotFoundException e) {
                System.out.println("\nError opening the file " + filename);
                System.out.println("The program was terminated, please run again");
                System.exit(0);
            }
        }
       System.out.println();
       System.out.println("-----------------------------------------------------------------");


  }

  private void getCustomerData()
  {
	// get next customer data : from file or random number generator
	// set anyNewArrival and transactionTime
	// add statements
      if (dataSource == 1) {
            int data1 = dataFile.nextInt();
            int data2 = dataFile.nextInt();
            anyNewArrival = (((data1 % 100) + 1) <= chancesOfArrival);
            transactionTime = (data2 % maxTransactionTime) + 1;
        } else if (dataSource == 0) {
            dataRandom = new Random();
            anyNewArrival = ((dataRandom.nextInt(100) + 1) <= chancesOfArrival);
            transactionTime = dataRandom.nextInt(maxTransactionTime) + 1;
        }


  }

  private void doSimulation()
  {
	// add statements
      servicearea = new ServiceArea(numTellers, customerQLimit, 1);
      tempBusy = new ArrayDeque<Teller>(numTellers);
      tempFree = new ArrayDeque<Teller>(numTellers);
	// Initialize ServiceArea

	// Time driver simulation loop
  	for (int currentTime = 0; currentTime < simulationTime; currentTime++) {
                System.out.println("Time: " + currentTime);
    		// Step 1: any new customer enters the bank?
    		getCustomerData();

    		if (anyNewArrival) {

      		    // Step 1.1: setup customer data
                    totalCustomer++;
                Customer customer = new Customer(customerIDCounter++,
                        transactionTime, currentTime);
                System.out.println("\tCustomer #" + customer.getCustomerID()
                        + " arrives with checkout time "
                        + customer.getTransactionTime() + " units");
                if (servicearea.isCustomerQTooLong() == true) {
                    System.out.println("\tCustomer #"
                            + customer.getCustomerID() + " skipped the line.");
                    numGoaway++;
                } else {
                    servicearea.insertCustomerQ(customer);
                    System.out.println("\tCustomer #"
                            + customer.getCustomerID()
                            + " waits in the customer queue");
                }

      		    // Step 1.2: check customer waiting queue too long?
    		} else {
      		    System.out.println("\tNo new customer!");
    		}

    		// Step 2: free busy tellers, add to free tellerQ
                while (servicearea.emptyBusyTellerQ() == false) {
                Teller teller = servicearea.getFrontBusyTellerQ();
                
                if (teller.getEndBusyIntervalTime() == currentTime) {
                    servicearea.removeBusyTellerQ();
                    
                    Customer customer = teller.busyToFree();
                    
                    servicearea.insertFreeTellerQ(teller);
                    System.out.println("\tCustomer #"
                            + customer.getCustomerID() + " is done");
                    System.out.println("\tTeller  #" + teller.getTellerID()
                            + " is free");
                } else {
                    break;
                }
            }
            while (servicearea.numWaitingCustomers() > 0
                    && servicearea.numFreeTellers() > 0) {
                Teller teller = servicearea.removeFreeTellerQ();
                Customer customer = servicearea.removeCustomerQ();
               
                //customerID = customer.getCustomerID();
                teller.freeToBusy(customer, currentTime);
                servicearea.insertBusyTellerQ(teller);
                System.out.println("\tTeller  #" + teller.getTellerID()
                        + " starts serving Customer #"
                        + customer.getCustomerID() + " for "
                        + customer.getTransactionTime() + " units");
                numServed++;
            }
            if (servicearea.numWaitingCustomers() > 0) {
                totalWaitingTime++;
            }
            System.out.println("-----------------------------------------------------------------");
        }
         while (servicearea.emptyBusyTellerQ() == false) {
            Teller teller = servicearea.removeBusyTellerQ();
            teller.setEndIntervalTime(simulationTime, 1);
            tempBusy.offer(teller);
        }
    		// Step 3: get free tellers to serve waiting customers 
         while (servicearea.emptyFreeTellerQ() == false) {
            Teller teller = servicearea.removeFreeTellerQ();
            teller.setEndIntervalTime(simulationTime, 0);
            tempFree.offer(teller);
 
  	} // end simulation loop

  	// clean-up
  }

  private void printStatistics()
  {
	// add statements into this method!
	// print out simulation results
	// see the given example in project statement
        // you need to display all free and busy tellers
      
        System.out.println("\nEnd Simulatiuon Report \n");
        System.out.println("\t# total arrival customers  : " + totalCustomer);
        System.out.println("\t# customers gone-away      : " + numGoaway);
        System.out.println("\t# customers served         : " + numServed);
        System.out.println("\n\t*** Current Tellers Info. ***\n");
        System.out.println("\t# waiting customers  : "
                + servicearea.numWaitingCustomers());
        System.out.println("\t# busy tellers      : " + tempBusy.size());
        System.out
                .println("\t# free tellers      : " + tempFree.size() + "\n");
        System.out
                .println("\tTotal waiting time         : " + totalWaitingTime);
        System.out.println("\tAverage waiting            : "
                + ((double) totalWaitingTime / (double) totalCustomer) + "\n");

        // Busy Teller Info
        System.out.println("\tBusy Teller Info. : \n");
        if (tempBusy.isEmpty()) {
            System.out
                    .println("\t\tThere were no Busy Tellers at the end of the simulation");
        } else {
            while (!tempBusy.isEmpty()) {
                Teller teller = tempBusy.poll();
                teller.printStatistics();
            }
        }

        // Free Teller Info
        System.out.println("\tFree Teller Info. : \n");
        if (tempFree.isEmpty()) {
            System.out
                    .println("\t\tThere were no Free Tellers at the end of the simulation");
        } else {
            while (!tempFree.isEmpty()) {
                Teller teller = tempFree.poll();
                teller.printStatistics();
            }
        }

  }

  // *** main method to run simulation ****

  public static void main(String[] args) {
   	BankSimulator runBankSimulator=new BankSimulator();
   	runBankSimulator.setupParameters();
   	runBankSimulator.doSimulation();
   	runBankSimulator.printStatistics();
  }

}
