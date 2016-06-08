import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Jola
 */
public class tyran {

    /**
     *
     */
    public static List<String> IPs;
	public static String coordinator;
	public static tyran be;

    /**
     *
     */
    public static ReceiveElectionOne reo; // otrzymanie election messages

    /**
     *
     */
    public static ReceiveElectionTwo ret;

    /**
     *
     */
    public static ReceiveElectionThree reth;

    /**
     *
     */
    public static ReceiveElectionFour ref;
	public static ReceiveIsAliveOne riao; // odebranie isAlive Messages
	public static ReceiveIsAliveTwo riat;

    /**
     *
     */
    public static ReceiveIsAliveThree riath;
	public static ReceiveIsAliveFour riaf;

    /**
     *
     */
    public static ReceiveCoordinator rc; // to receive coordinator message
	public static ReceiveReplyElectionOne rrco; // to receive election reply
												// message

    /**
     *
     */
	public static ReceiveReplyElectionTwo rrct;

    /**
     *
     */
    public static ReceiveReplyElectionThree rrcth;

    /**
     *
     */
    public static ReceiveReplyElectionFour rrcf;

    /**
     *
     */
    public static CoordinatorTimeout ct; // Timeout thread

    /**
     *
     */
    public static Client client; // client

	public static int replyCount;

    /**
     *
     */
    public static long currentTime;

    /**
     *
     */
    public static int replyFlag;

	/**
	 * Constructor
	 */
	public tyran() {
		replyFlag = 0;

		coordinator = new String();
		replyCount = 0;

		IPs = new ArrayList<String>();
		IPs.add("192.168.1.180");
		IPs.add("192.168.154.1");
		IPs.add("");
		IPs.add("");
		IPs.add("");
	}

	/**
	 * To increment the reply count
	 */
	public void updateReplyCount() {

		replyCount++;
		if (replyCount == 1) {
			ct.start();
		}

	}

	/**
	 * Main method
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		// create threads
		be = new tyran();
		reo = be.new ReceiveElectionOne();
		ret = be.new ReceiveElectionTwo();
		reth = be.new ReceiveElectionThree();
		ref = be.new ReceiveElectionFour();
		client = be.new Client();
		riao = be.new ReceiveIsAliveOne();
		riat = be.new ReceiveIsAliveTwo();
		riath = be.new ReceiveIsAliveThree();
		riaf = be.new ReceiveIsAliveFour();
		rc = be.new ReceiveCoordinator();
		rrco = be.new ReceiveReplyElectionOne();
		rrct = be.new ReceiveReplyElectionTwo();
		rrcth = be.new ReceiveReplyElectionThree();
		rrcf = be.new ReceiveReplyElectionFour();
		ct = be.new CoordinatorTimeout();

		// thread start calls
		reo.start();
		ret.start();
		reth.start();
		ref.start();
		riao.start();
		riat.start();
		riath.start();
		riaf.start();
		rrco.start();
		rrct.start();
		rrcth.start();
		rrcf.start();
		// rt.start();
		rc.start();

		/**
		 * Take user input
		 */
		while (true) {
			System.out.println("Press 0 to send election message");
			Scanner sc = new Scanner(System.in);
			String input = sc.nextLine();
			if (input.equals("0")) {
				initiateElection();
			} else {
				System.out.println("Invalid Input");
			}
		}
	}

	/**
	 * To initiate election messages and send to other processes
	 */
	private static void initiateElection() {
		// be.getSetElectionFlag(1);
		coordinator = "";
		replyFlag = 0;
		int flag = 0;
		int count = 0;

		try { // send election message to all other processes
			for (int i = 0; i < IPs.size(); ++i) {
				if (flag == 1) {
					client.sendMessage(IPs.get(i), Integer.parseInt("3" + (count) + "000"),
							"Election: " + InetAddress.getLocalHost().getHostAddress());
					continue;
				}
				if (InetAddress.getLocalHost().getHostAddress().equals(IPs.get(i))) {
					flag = 1;
					count = i;
				}
			}
		} catch (UnknownHostException e) {
			// e.printStackTrace();
		} catch (IllegalStateException e) {
			// e.printStackTrace();
		}
		try {
			// electionFlag = 1;
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			// e1.printStackTrace();
		}
		// if no reply declare yourself as leader
		if (replyCount == 0) {
			for (int i = IPs.size() - 1; i >= 0; --i) {
				try {
					if (InetAddress.getLocalHost().getHostAddress().equals(IPs.get(i))) {
						coordinator = IPs.get(i);
					} else {
						client.sendMessage(IPs.get(i), 50000,
								"Coordinator: " + InetAddress.getLocalHost().getHostAddress());
					}
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}

			}
			// if no coordinator message after reply
		} else if (replyCount > 0 && replyFlag == 1) {
			replyFlag = 0;
			replyCount = 0;
			initiateElection();
		}
	}

	/**
	 * To send reply message reply message
	 * 
	 * @param IP
	 */
	public void replyTo(String IP) {
		try {
			if (IP.equals("129.21.22.196")) { // if process 1
				if (InetAddress.getLocalHost().getHostAddress().equals("129.21.37.18")) {
					client.sendMessage(IP, 35000, "Reply: 1");
				} else if (InetAddress.getLocalHost().getHostAddress().equals("129.21.37.16")) {
					client.sendMessage(IP, 36000, "Reply: 2");
				} else if (InetAddress.getLocalHost().getHostAddress().equals("129.21.37.15")) {
					client.sendMessage(IP, 37000, "Reply: 3");
				} else if (InetAddress.getLocalHost().getHostAddress().equals("129.21.37.23")) {
					client.sendMessage(IP, 38000, "Reply: 4");
				}
			} else if (IP.equals("129.21.37.18")) { // if process 2
				if (InetAddress.getLocalHost().getHostAddress().equals("129.21.37.16")) {
					client.sendMessage(IP, 36000, "Reply: 2");
				} else if (InetAddress.getLocalHost().getHostAddress().equals("129.21.37.15")) {
					client.sendMessage(IP, 37000, "Reply: 3");
				} else if (InetAddress.getLocalHost().getHostAddress().equals("129.21.37.23")) {
					client.sendMessage(IP, 38000, "Reply: 4");
				}
			} else if (IP.equals("129.21.37.16")) { // if process 3
				if (InetAddress.getLocalHost().getHostAddress().equals("129.21.37.15")) {
					client.sendMessage(IP, 37000, "Reply: 3");
				} else if (InetAddress.getLocalHost().getHostAddress().equals("129.21.37.23")) {
					client.sendMessage(IP, 38000, "Reply: 4");
				}
			} else if (IP.equals("129.21.37.15")) { // if process 4
				if (InetAddress.getLocalHost().getHostAddress().equals("129.21.37.23")) {
					client.sendMessage(IP, 38000, "Reply: 4");
				}
			}
		} catch (UnknownHostException e) {
			// e.printStackTrace();
		}
	}

	/**
	 * To perform coordinator message timeout
	 * 
	 *
	 *
	 */
	class CoordinatorTimeout extends Thread {
		public void run() {
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // reset parameters and restart election
			if (coordinator.equals("")) {

				replyCount = 0;
				replyFlag = 0;
				initiateElection();
			}
		}
	}

	/**
	 * Client to send messages
	 * 
	 *
	 */
	class Client {

		/**
		 * To ping coordinator
		 * 
		 * @param IP
		 * @param port
		 * @param message
		 */
		public void sendPing(String IP, int port, String message) {
			Socket client;
			while (true) {
				try {
					client = new Socket(IP, port);
					OutputStream outToServer = client.getOutputStream();
					DataOutputStream out = new DataOutputStream(outToServer);
					System.out.println(message);
					out.writeUTF(message);
					client.close();
					Thread.sleep(2000);
				} catch (IOException e) {
					initiateElection(); // if cannot connect to coordinator
										// restart election
					return;
				} catch (InterruptedException e) {
					// e.printStackTrace();
				}
			}
		}

		/**
		 * To send messages to other processes
		 * 
		 * @param IP
		 * @param port
		 * @param message
		 */
		public void sendMessage(String IP, int port, String message) {
			Socket client;
			try {
				System.out.println(IP + "***" + port);
				client = new Socket(IP, port);
				OutputStream outToServer = client.getOutputStream();
				DataOutputStream out = new DataOutputStream(outToServer);
				System.out.println(message);
				System.out.println("Sending " + message + " to: " + IP);
				out.writeUTF(message);
				client.close();
			} catch (IOException e) {
				// e.printStackTrace();
				return;
			}
		}
	}

	/**
	 * To receive isAlive message from process 1.
	 * 
	 * 
	 *
	 */
	class ReceiveIsAliveOne extends Thread {
		ServerSocket serverSocket;

		public void run() {
			while (true) {
				try {
					serverSocket = new ServerSocket(60000);
					Socket server = serverSocket.accept();

					DataInputStream in = new DataInputStream(server.getInputStream());
					String message = in.readUTF();

					server.close();
					serverSocket.close();
					// display message
					System.out.println(message);

				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}
		}
	}

	/**
	 * To receive isAlive message from process 2
	 * 
	 * 
	 *
	 */
	class ReceiveIsAliveTwo extends Thread {
		ServerSocket serverSocket;

		public void run() {
			while (true) {
				try {
					serverSocket = new ServerSocket(61000);
					Socket server = serverSocket.accept();

					DataInputStream in = new DataInputStream(server.getInputStream());
					String message = in.readUTF();

					server.close();
					serverSocket.close();
					// display message
					System.out.println(message);

				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}
		}
	}

	/**
	 * To receive isAlive message from process 3
	 * 
	 * 
	 *
	 */
	class ReceiveIsAliveThree extends Thread {
		ServerSocket serverSocket;

		public void run() {
			while (true) {
				try {
					serverSocket = new ServerSocket(62000);
					Socket server = serverSocket.accept();

					DataInputStream in = new DataInputStream(server.getInputStream());
					String message = in.readUTF();

					server.close();
					serverSocket.close();
					// display message
					System.out.println(message);

				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}
		}
	}

	/**
	 * To receive isAlive message from process 4
	 * 
	 * 
	 *
	 */
	class ReceiveIsAliveFour extends Thread {
		ServerSocket serverSocket;

		public void run() {
			while (true) {
				try {
					serverSocket = new ServerSocket(63000);
					Socket server = serverSocket.accept();

					DataInputStream in = new DataInputStream(server.getInputStream());
					String message = in.readUTF();

					server.close();
					serverSocket.close();
					// display message
					System.out.println(message);

				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}
		}
	}

	/**
	 * To receive coordinator message
	 * 
	 * 
	 *
	 */
	class ReceiveCoordinator extends Thread {
		ServerSocket serverSocket;

		public void run() {
			while (true) {
				try {
					serverSocket = new ServerSocket(50000);
					// serverSocket.setSoTimeout(15000);
					Socket server = serverSocket.accept();

					DataInputStream in = new DataInputStream(server.getInputStream());
					String message = in.readUTF();
					String subString = message.substring(message.indexOf(":") + 2);
					server.close();
					serverSocket.close();
					// display message
					System.out.println(message);
					if (message.contains("Coordinator")) {
						coordinator = subString;

						Thread.sleep(5000);
						// IPs.remove(coordinator);
						// int temp = be.getSetElectionFlag(0);
						replyFlag = 1;
						replyCount = 0;

						// start pinging coordinator at regular intervals
						client.sendPing(coordinator, getPort(), "Ping: " + InetAddress.getLocalHost().getHostAddress());

					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		/**
		 * To decide the port at which messages are to be sent
		 * 
		 * @return
		 */
		private int getPort() {
			try {
				if (InetAddress.getLocalHost().getHostAddress().equals(IPs.get(0))) {
					return 60000;
				} else if (InetAddress.getLocalHost().getHostAddress().equals(IPs.get(1))) {
					return 61000;
				} else if (InetAddress.getLocalHost().getHostAddress().equals(IPs.get(2))) {
					return 62000;
				} else if (InetAddress.getLocalHost().getHostAddress().equals(IPs.get(3))) {
					return 63000;
				}

			} catch (IOException e) {

			}
			return 0;
		}
	}

	/**
	 * To receive reply message from process 1
	 * 
	 * 
	 *
	 */
	class ReceiveReplyElectionOne extends Thread {
		ServerSocket serverSocket;

		public void run() {
			while (true) {
				try {
					serverSocket = new ServerSocket(35000);
					Socket server = serverSocket.accept();

					DataInputStream in = new DataInputStream(server.getInputStream());
					String message = in.readUTF();
					System.out.println("---" + message);
					String subString = message.substring(message.indexOf(":") + 2);

					// display message

					if (message.contains("Reply")) {
						be.updateReplyCount(); // increment reply count
					}
					server.close();
					serverSocket.close();
				} catch (IOException e) {

					break;
				}
			}
		}
	}

	/**
	 * To receive reply message from process 2
	 * 
	 * @author Akshai Prabhu
	 *
	 */
	class ReceiveReplyElectionTwo extends Thread {
		ServerSocket serverSocket;

		public void run() {
			while (true) {
				try {
					serverSocket = new ServerSocket(36000);
					Socket server = serverSocket.accept();

					DataInputStream in = new DataInputStream(server.getInputStream());
					String message = in.readUTF();
					System.out.println("---" + message);
					String subString = message.substring(message.indexOf(":") + 2);

					// display message

					if (message.contains("Reply")) {
						be.updateReplyCount(); // increment reply count
					}
					server.close();
					serverSocket.close();
				} catch (IOException e) {

					break;
				}
			}
		}
	}

	/**
	 * To receive reply message from process 3
	 * 
	 * @author Akshai Prabhu
	 *
	 */
	class ReceiveReplyElectionThree extends Thread {
		ServerSocket serverSocket;

		public void run() {
			while (true) {
				try {
					serverSocket = new ServerSocket(37000);
					Socket server = serverSocket.accept();

					DataInputStream in = new DataInputStream(server.getInputStream());
					String message = in.readUTF();
					System.out.println("---" + message);
					String subString = message.substring(message.indexOf(":") + 2);

					// display message

					if (message.contains("Reply")) {
						be.updateReplyCount(); // increment reply count
					}
					server.close();
					serverSocket.close();
				} catch (IOException e) {

					break;
				}
			}
		}
	}

	/**
	 * To receive reply message from process 4
	 * 
	 * @author Akshai Prabhu
	 *
	 */
	class ReceiveReplyElectionFour extends Thread {
		ServerSocket serverSocket;

		public void run() {
			while (true) {
				try {
					serverSocket = new ServerSocket(38000);
					Socket server = serverSocket.accept();

					DataInputStream in = new DataInputStream(server.getInputStream());
					String message = in.readUTF();
					System.out.println("---" + message);
					String subString = message.substring(message.indexOf(":") + 2);

					// display message

					if (message.contains("Reply")) {
						be.updateReplyCount(); // increment reply count
					}
					server.close();
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}
		}
	}

	/**
	 * To receive election message from process 1
	 * 
	 * @author Akshai Prabhu
	 *
	 */
	class ReceiveElectionOne extends Thread {
		ServerSocket serverSocket;

		public void run() {
			while (true) {
				try {
					serverSocket = new ServerSocket(30000);
					Socket server = serverSocket.accept();

					DataInputStream in = new DataInputStream(server.getInputStream());
					String message = in.readUTF();
					System.out.println("---" + message);
					String subString = message.substring(message.indexOf(":") + 2);

					// display message

					if (message.contains("Election")) {
						replyTo(subString); // reply to others
						initiateElection(); // propagate election
						// be.getSetElectionFlag(1);

						// Thread.sleep(3000);

					}
					server.close();
					serverSocket.close();
				} catch (IOException e) {

					break;
				}
			}
		}
	}

	/**
	 * To receive election message from process 2
	 * 
	 * 
	 *
	 */
	class ReceiveElectionTwo extends Thread {
		ServerSocket serverSocket;

		public void run() {
			while (true) {
				try {
					serverSocket = new ServerSocket(31000);
					Socket server = serverSocket.accept();

					DataInputStream in = new DataInputStream(server.getInputStream());
					String message = in.readUTF();
					System.out.println("---" + message);
					String subString = message.substring(message.indexOf(":") + 2);

					// display message
					// System.out.println(message);
					if (message.contains("Election")) {
						replyTo(subString); // reply to others
						initiateElection(); // propagate election

						// be.getSetElectionFlag(1);
						// Thread.sleep(3000);

					}
					server.close();
					serverSocket.close();

				} catch (IOException e) {

					break;
				}
			}
		}
	}

	/**
	 * To receive election message from process 3
	 * 
	 * 
	 *
	 */
	class ReceiveElectionThree extends Thread {
		ServerSocket serverSocket;

		public void run() {
			while (true) {
				try {
					serverSocket = new ServerSocket(32000);
					Socket server = serverSocket.accept();

					DataInputStream in = new DataInputStream(server.getInputStream());
					String message = in.readUTF();
					String subString = message.substring(message.indexOf(":") + 2);
					System.out.println("---" + message);

					// display message
					// System.out.println(message);
					if (message.contains("Election")) {
						replyTo(subString); // reply to others
						initiateElection(); // propagate election

						// be.getSetElectionFlag(1);
						// Thread.sleep(3000);

					}
					server.close();
					serverSocket.close();

				} catch (IOException e) {

					break;
				}
			}
		}
	}

	/**
	 * To receive election message from process 4
	 * 
	 * 
	 *
	 */
	class ReceiveElectionFour extends Thread {
		ServerSocket serverSocket;

		public void run() {
			while (true) {
				try {
					serverSocket = new ServerSocket(33000);
					Socket server = serverSocket.accept();

					DataInputStream in = new DataInputStream(server.getInputStream());
					String message = in.readUTF();
					String subString = message.substring(message.indexOf(":") + 2);
					System.out.println("---" + message);

					// display message
					// System.out.println(message);
					if (message.contains("Election")) {
						replyTo(subString); // reply to others
						initiateElection(); // propagate election
						// be.getSetElectionFlag(1);
						// Thread.sleep(3000);

					}
					server.close();
					serverSocket.close();

				} catch (IOException e) {
					break;
				}
			}
		}
	}

}
