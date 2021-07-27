# Concurrent-And-Parallel-Programming-with-Java

Files:

CompanyServer.java - This is the SERVER. ExecutorServicePool is used create Threadpool and to to submit threads.
                   Semaphore limits users to 50.
SalesPerson.java - This is the CLIENT which does the functionalities of adding cars, selling etc.



There are 3 other classes
Car.java - This class has the car details like make,mileage etc
CompanyDatabase.java - This class stores the data of the added cars.Sold cars are removed from the database.
ServiceController.java- This has various service modes for different functions like add car, sell car etc. 
              A counted semaphore is used in this class to limit the users to 50.


A car sales company requires a server to store all its car data and share it between its sales personnel. 
For the purposes of this assignment each car has the following attributes: registration, make, price, mileage and 
forSale (a boolean- true for forSale and false for sold).

Clients can do the following:  
 
-A sales person can add a new car to the system.  
-Sell a car  
-Request information from the system. 
 Sample requests would be cars for sale, cars of a given make, total value of all sales. 
 
 All cars added to the system should be stored in a shared data structure on the server.  
1. No log on/off required for users.  
2. For this assignment 25 cars are added to the system.  
3. Server uses thread pool and semaphores to limit users to 50 
4. All requests to server must be objects
5. Cars added on server must be thread safe as many clients have conflicting requests 


Since HashMap is created with registration number as key for a shared data strucutre to store details,
when trying to run clients if the registration number is not changed for add method, it overwrites the existing ones on hashmap again.
So to see multiple clients adding different cars, we have to change the registration number. 

