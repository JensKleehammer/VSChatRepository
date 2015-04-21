package Main;


public class LogIn {
	public static void main(String[] args){
		String host = "127.0.0."+(int)(Math.random()*1);
		
		ClientGUI clientGui = new ClientGUI();
		
		Client client = new Client("localhost", 1500, "jens", clientGui);
	}
}
