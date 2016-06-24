package baram.view;

public class Config {
	
	static {
		/*
		Logger log = Logger.getLogger("myApp");
		log.setLevel(Level.ALL);
		log.info("initializing - trying to load configuration file ...");

		Properties preferences = new Properties();
		try {
		    FileInputStream configFile = new FileInputStream("/path/to/app.properties");
		    preferences.load(configFile);
		    LogManager.getLogManager().readConfiguration(configFile);
		} catch (IOException ex)
		{
		    System.out.println("WARNING: Could not open configuration file");
		    System.out.println("WARNING: Logging not configured (console output only)");
		}
		log.info("starting myApp");
		...
		*/
	}
	
	public static boolean isInteralServer;
	public static boolean showLogin;
	public static boolean showMonitoring = true;
	public static boolean showAnalysis;
	public static boolean showNetwork;
	public static boolean showRowList;
	
	public static String jdbcDriver;
	public static String jdbcUrl;
	public static String jdbcUser;
	public static String jdbcPassword;
	
	public static String monitorTable;
	public static String analysisTable;
	public static String networkNodeTable;
	public static String networkEdgeTable;
	public static String UnstructuredDatTable;
	public static String LocDangerSourceTable;
	public static String dbmsOutputTable;
}
