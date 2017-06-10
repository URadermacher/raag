package eu.vdmr.raga.db.logic;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sqlite.SQLiteConfig;

import eu.vdmr.raga.db.dto.Database;

public class StartUp {
	public static final String DBFILE = "databasefile";
	public static final String DBDEFFILE = "databasedefinitionfile";
	public static final String DBCONTENTFILE = "databasecontentfile";
	private static final Logger LOG = LogManager.getLogger(StartUp.class);
	
	private static Properties properties = null;

	public static void main(String[] args) throws Throwable {
		StartUp startUp = new StartUp();
		startUp.start();
	}
	
	private void start() {
		try {
			readProperties();
		} catch (IOException ioe) {
			LOG.error("error reading properties: " + ioe);
		}
		String databasefile = getProperty(DBFILE);
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException cnfe) {
			LOG.error("error loading JDBC " + cnfe);
			System.exit(-1);
		}
		Connection connection = null;
		try {
			// create a database connection
			SQLiteConfig config = new SQLiteConfig();
			// config.enforceForeignKeys(true);
			connection = DriverManager.getConnection("jdbc:sqlite:"+databasefile, config.toProperties());
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			ResultSet rs1 = statement.executeQuery("pragma foreign_keys");
			if (!rs1.getBoolean(1)) {
				LOG.error("oeps, no FK supported..");
			} else {
				LOG.debug("FK on??");
			}
			LOG.debug("here i am");
			DatabaseCreator creator = new DatabaseCreator();
			Database database = creator.createDatabase(statement, true);
			DatabaseFiller filler = new DatabaseFiller();
			filler.fillDatabase(statement, database, connection);

		} catch (Exception e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			LOG.error("error creating/loading Database " + e, e);
			System.exit(-1);
		} finally {
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				LOG.error("error closing... " + e);
			}
		}
	}

	private void readProperties() throws IOException {
		properties = new Properties();
		try (final InputStream stream =
		           this.getClass().getClassLoader().getResourceAsStream("raga.properties")) {
		    properties.load(stream);
		    
		}
	}
	
	public static String getProperty(String key) {
		return properties.getProperty(key);
	}
}
