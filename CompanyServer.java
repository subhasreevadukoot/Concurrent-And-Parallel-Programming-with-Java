import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CompanyServer {

	final static int portNumber = 1567;

	public static void main(String args[]) {
		int numThreads = Runtime.getRuntime().availableProcessors();
		// Executor servicepool to submit threads
		// semaphores to limit users to 50 by the server is added in
		// ServiceContoller.java
		ExecutorService pool = Executors.newFixedThreadPool(numThreads);

		CompanyDatabase dataServer = new CompanyDatabase();

		try {
			ServerSocket serverSocket = new ServerSocket(portNumber);
			while (true) {
				System.out.println("Server Running....");
				// server is listening
				Socket socket = serverSocket.accept();
				pool.submit(new ServiceController(socket, dataServer));

			}

		} catch (Exception e) {

		}

	}

}

class ServiceController extends Thread {

	Socket socket;
	CompanyDatabase data;
	// Semaphore to limit users to 50
	Semaphore userLimit = new Semaphore(50);

	ServiceController(Socket socket, CompanyDatabase data) {
		this.socket = socket;
		this.data = data;

	}

	public void run() {
		try {
			userLimit.acquire();
			DataInputStream dataIn = new DataInputStream(socket.getInputStream());
			DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
			int serviceMode = dataIn.readInt();
			System.out.println("serviceMode " + serviceMode);
			// different servicemodes for different functions
			if (serviceMode == 0) {
				Car newCar = new Car();
				newCar.setRegistration(dataIn.readUTF());
				newCar.setMake(dataIn.readUTF());
				newCar.setMileage(dataIn.readDouble());
				newCar.setPrice(dataIn.readDouble());
				newCar.setForSale(dataIn.readBoolean());

				if (data.addCar(newCar.getRegistration(), newCar)) {
					dataOut.writeBoolean(true);
					dataOut.writeUTF(newCar.getMake());

				} else {
					dataOut.writeBoolean(false);
				}
			} else if (serviceMode == 1) {
				if (data.sellCar(dataIn.readUTF())) {
					dataOut.writeBoolean(true);
				} else {

					dataOut.writeBoolean(false);
				}
			} else if (serviceMode == 2) {
				dataOut.writeUTF(data.searchByMake(dataIn.readUTF()));
			} else if (serviceMode == 3) {
				dataOut.writeUTF(data.carForSale());
			} else if (serviceMode == 4) {
				dataOut.writeDouble(data.totalValue());
			}
		} catch (IOException ex) {
			Logger.getLogger(ServiceController.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		userLimit.release();

	}
}

class CompanyDatabase {
	// Shared DataStructure where car data is stored
	HashMap<String, Car> carData = new HashMap<String, Car>();
	private final ReadWriteLock dataLock = new ReentrantReadWriteLock();
	// Locks are used to make the class ThreadSafe.
	// Cars added on server are thread safe as many clients have conflicting
	// requests
	private final Lock readLock = dataLock.readLock();

	private final Lock writeLock = dataLock.writeLock();

	ArrayList<Car> carDataArray = new ArrayList<Car>();

// Adding cars- 25 cars are added by the client.
	public boolean addCar(String reg, Car carDetails) {
		writeLock.lock();
		try {
			carData.put(reg, carDetails);
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			writeLock.unlock();

		}
	}

//Sell a car based on unique registration number
	public boolean sellCar(String reg) {
		writeLock.lock();
		try {

			Iterator ite = carData.keySet().iterator();
			for (int c = 0; c < carData.size(); c++) {
				String key = (String) ite.next();

				if (reg.equalsIgnoreCase(key)) {
					Car car = carData.get(key);
					if (car.isForSale()) {
						car.setForSale(false);
						return true;
					}

				}
			}
		} finally {
			writeLock.unlock();

		}
		return false;
	}

	public String searchByMake(String make) {
		readLock.lock();
		StringBuilder result = new StringBuilder();
		try {

			Iterator<String> ite = carData.keySet().iterator();
			for (int c = 0; c < carData.size(); c++) {
				String key = (String) ite.next();
				Car car = carData.get(key);
				if (car.getMake().equalsIgnoreCase(make)) {
					result.append(car.toString());
					result.append("\n");
				}
			}
		} finally {
			readLock.unlock();

		}
		return result.toString();
	}

// List of cars for sale
	public String carForSale() {
		readLock.lock();
		StringBuilder result = new StringBuilder();
		try {

			Iterator<String> ite = carData.keySet().iterator();
			for (int c = 0; c < carData.size(); c++) {
				String key = (String) ite.next();
				Car car = carData.get(key);
				if (car.isForSale()) {

					result.append(car.toString());
					result.append("\n");
				}
			}
		} finally {
			readLock.unlock();

		}
		return result.toString();

	}

// Total value of cars
	public double totalValue() {
		readLock.lock();
		double price = 0;
		try {
			Iterator<String> ite = carData.keySet().iterator();
			for (int c = 0; c < carData.size(); c++) {
				String key = (String) ite.next();
				Car car = carData.get(key);
				price = price + car.getPrice();
			}
		} finally {
			readLock.unlock();

		}
		return price;
	}

}

class Car {
	// private to ensure encapsulation. Thread safety is implemented in
	// CompanyDatabase.java where
	// conflicting client requests can be avoided while adding car details
	private String registration = "";
	private String make = "";
	private double mileage = 0;
	private boolean forSale = false;
	private double price = 0;

//Car properties are included in this class. 
	public Car(String registration, String make, double mileage, double price, boolean forSale) {
		this.registration = registration;
		this.make = make;
		this.mileage = mileage;
		this.forSale = forSale;
		this.price = price;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Car() {

	}

	public String getRegistration() {
		return registration;
	}

	public void setRegistration(String registration) {
		this.registration = registration;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public double getMileage() {
		return mileage;
	}

	public void setMileage(double mileage) {
		this.mileage = mileage;
	}

	public boolean isForSale() {
		return forSale;
	}

	public void setForSale(boolean forSale) {
		this.forSale = forSale;
	}

	public String toString() {
		return "[Registration = " + registration + " ,Make = " + make + " ,Milage = " + mileage + " ,Price = " + price
				+ " ,ForSale = " + forSale + "]";
	}

}
