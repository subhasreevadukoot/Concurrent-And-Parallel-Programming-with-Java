
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SalesPerson {
	static String choiceC;
	final static int portNumber = 1567;

	public static void main(String args[]) {

		// Details of 30 cars

		// Registration id is unique and added to hashmap as a key, so trying to add
		// same registration number again overwrites it, to run multiple clients who
		// adds different
		// cars, change registration number.

		String registration[] = { "16L1234", "01LH1234", "02D1234", "03WW1234", "05KK1234", "10WM1234", "11M1234",
				"16L1235", "01LH1134", "02D1264", "03WH1234", "05KJ1234", "06BW1234", "07LS1234", " 08KE1234",
				"10WH1234", "11M1834", "16L1834", "01LO1234", "02D7234", "03WI1234", "05AK1234", "06CW1234", "07LS1834",
				"08KE1634", "03WJ1234", "05BK1234", "06CK1234", "07WS1834", "08KE1639" };
		String Make[] = { "Ferrari", "Ford Fiesta", "Ford Focus", "Ford Mustang", "Ford B-Max", "Ford C-Max",
				"Ford S-Max", "Toyota Starlet", "Toyota Avensis", "Ferrari", "Ford Fiesta", "Ford Focus", "Ford Mondeo",
				"Ford Mustang", "Ford S-Max", "Toyota Starlet", "Toyota Avensis", "Ferrari", "Ford Fiesta",
				"Ford Focus", "Ford Mondeo", "Ford Mustang", "Mitsubishi Lancer", "Ford Fiesta", "Ford Focus",
				"Toyota Starlet", "Toyota Avensis", "Ferrari", "Ford Fiesta", "Ford Focus" };
		double[] price = { 120000, 1000, 11000, 12000, 14000, 15000, 16000, 17000, 19000, 20000, 120000, 1000, 11000,
				12000, 14000, 15000, 16000, 17000, 19000, 20000, 120000, 1000, 11000, 12000, 14000, 16000, 17000, 19000,
				20000, 120000 };
		double[] mileage = { 1000, 1000, 2000, 3000, 5000, 6000, 7000, 8000, 10000, 11000, 1000, 1000, 2000, 3000, 5000,
				6000, 7000, 8000, 10000, 11000, 1000, 1000, 2000, 3000, 5000, 11000, 1000, 1000, 2000, 3000, 5000 };
		boolean[] forSale = { true, true, true, true, true, true, true, true, true, true, true, true, true, true, true,
				true, true, true, true, true, true, true, true, true, true, true, true, true, true, true };
		// Client adds,sells, displays total value and searches for car automatically
		System.out.println("Add car");

		// To see cars added by different clients, change the i value, then different
		// registration numbers will be added
		// So total cars will be more than 15 when multiple client runs, with unique
		// registration numbers.
		// 15 cars are added by each client
		for (int i = 0; i < 25; i++) {
			addCar(new Car(registration[i], Make[i], price[i], mileage[i], forSale[i]));
		}
		// Random number to sell and search for random car
		System.out.println();
		int n = (int) ((Math.random() * 20) + 1);
		System.out.println();
		searchForSale();

		System.out.println();
		totalCarValue();
		System.out.println();

		searchCar(Make[n]);
		System.out.println();

		System.out.println("Sell a car");
		sellCar(registration[n]);
		System.out.println();
		searchForSale();
		System.out.println();
	}

	// All requests to servers are objects
	// Client adds a car controlled by server
	public static void addCar(Car car) {
		DataInputStream dataIn = null;
		Socket soc = null;
		try {
			soc = new Socket(InetAddress.getLocalHost(), portNumber);
			dataIn = new DataInputStream(soc.getInputStream());
			DataOutputStream dataOut = new DataOutputStream(soc.getOutputStream());
			dataOut.writeInt(0);
			dataOut.writeUTF(car.getRegistration());
			dataOut.writeUTF(car.getMake());
			dataOut.writeDouble(car.getMileage());
			dataOut.writeDouble(car.getPrice());
			dataOut.writeBoolean(car.isForSale());
			if (dataIn.readBoolean()) {
				System.out.println(dataIn.readUTF() + " added successfully");
			} else {
				System.out.println(dataIn.readUTF() + " failed to add");
			}
		} catch (IOException ex) {
			Logger.getLogger(SalesPerson.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				try {
					dataIn.close();
				} catch (IOException ex) {
					Logger.getLogger(SalesPerson.class.getName()).log(Level.SEVERE, null, ex);
				}
				soc.close();
			} catch (IOException ex) {
				Logger.getLogger(SalesPerson.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

	}

// Client sells a car controlled by server
	public static void sellCar(String registration) {
		DataInputStream dataIn = null;
		Socket soc = null;
		try {
			soc = new Socket(InetAddress.getLocalHost(), portNumber);
			dataIn = new DataInputStream(soc.getInputStream());
			DataOutputStream dataOut = new DataOutputStream(soc.getOutputStream());
			dataOut.writeInt(1);
			dataOut.writeUTF(registration);
			if (dataIn.readBoolean()) {
				System.out.println(registration + " sold successfully");
			} else {
				System.out.println(registration + " failed to sell! no car found");
			}
		} catch (IOException ex) {
			Logger.getLogger(SalesPerson.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				try {
					dataIn.close();
				} catch (IOException ex) {
					Logger.getLogger(SalesPerson.class.getName()).log(Level.SEVERE, null, ex);
				}
				soc.close();
			} catch (IOException ex) {
				Logger.getLogger(SalesPerson.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

	}

// Client searches for a car by make
	public static void searchCar(String make) {
		DataInputStream dataIn = null;
		Socket soc = null;
		try {
			soc = new Socket(InetAddress.getLocalHost(), portNumber);
			dataIn = new DataInputStream(soc.getInputStream());
			DataOutputStream dataOut = new DataOutputStream(soc.getOutputStream());
			dataOut.writeInt(2);
			dataOut.writeUTF(make);
			String result = dataIn.readUTF();
			if (result != null && result.length() > 0) {
				System.out.println("Search Car By Make");
				System.out.println(result);
			} else {
				System.out.println("Search By Make");
				System.out.println("No Cars found");
			}
		} catch (IOException ex) {
			Logger.getLogger(SalesPerson.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				try {
					dataIn.close();
				} catch (IOException ex) {
					Logger.getLogger(SalesPerson.class.getName()).log(Level.SEVERE, null, ex);
				}
				soc.close();
			} catch (IOException ex) {
				Logger.getLogger(SalesPerson.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

	}

//Client searches the car for sale controlled by server
	public static void searchForSale() {
		DataInputStream dataIn = null;
		Socket soc = null;
		try {
			soc = new Socket(InetAddress.getLocalHost(), portNumber);
			dataIn = new DataInputStream(soc.getInputStream());
			DataOutputStream dataOut = new DataOutputStream(soc.getOutputStream());
			dataOut.writeInt(3);
			String result = dataIn.readUTF();
			if (result != null && result.length() > 0) {
				System.out.println("Cars For Sale");
				System.out.println(result);
			} else {
				System.out.println("Cars For Sale");
				System.out.println("No Cars found");
			}
		} catch (IOException ex) {
			Logger.getLogger(SalesPerson.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				try {
					dataIn.close();
				} catch (IOException ex) {
					Logger.getLogger(SalesPerson.class.getName()).log(Level.SEVERE, null, ex);
				}
				soc.close();
			} catch (IOException ex) {
				Logger.getLogger(SalesPerson.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

	}

// Client wants to display the total value of all the cars together controlled by server
	public static void totalCarValue() {
		DataInputStream dataIn = null;
		Socket soc = null;

		try {
			soc = new Socket(InetAddress.getLocalHost(), portNumber);
			dataIn = new DataInputStream(soc.getInputStream());
			DataOutputStream dataOut = new DataOutputStream(soc.getOutputStream());
			dataOut.writeInt(4);
			double totalValue = dataIn.readDouble();
			System.out.println("Search Total Value");
			System.out.println(totalValue);
		} catch (IOException ex) {
			Logger.getLogger(SalesPerson.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				try {
					dataIn.close();
				} catch (IOException ex) {
					Logger.getLogger(SalesPerson.class.getName()).log(Level.SEVERE, null, ex);
				}
				soc.close();
			} catch (IOException ex) {
				Logger.getLogger(SalesPerson.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

	}
}
