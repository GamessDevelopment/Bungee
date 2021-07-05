package net.zerox.github;

public class Bootstrap {
	
	public static void main(String[] args) {
		if(Float.parseFloat(System.getProperty("java.class.version")) < 52.0) {
			System.err.print("*** ERROR *** Bungee requires Java 8 or above");
			System.out.println("You can check ur version in CMD java -version");
			return;
		}
		BungeeCordLauncher.main(args);
	}
	

}
