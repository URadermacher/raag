package eu.vdmr.raga.db.logic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sqlite.SQLiteConfig;

import eu.vdmr.raga.db.dto.Database;

public class StartUp {
	 private static final Logger LOG = LogManager.getLogger(StartUp.class);	
	 
	 public static void main(String[] args) throws Throwable {
		 try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			LOG.error("error loading JDBC " + e);
			System.exit(-1);
		}
		 Connection connection = null;
		 try
		    {
		      // create a database connection
			  SQLiteConfig config = new SQLiteConfig();
		      //config.enforceForeignKeys(true);
		      connection = DriverManager.getConnection("jdbc:sqlite:D:/data/ragadatabase.db", config.toProperties());
		      Statement statement = connection.createStatement();
		      statement.setQueryTimeout(30);  // set timeout to 30 sec.

		      ResultSet rs1 = statement.executeQuery("pragma foreign_keys");
		      if (!rs1.getBoolean(1)) {
		    	  LOG.error("oeps");
		      } else {
		    	  LOG.debug("FK on??");
		      }
		      LOG.debug("here i am");
		      DatabaseCreator creator = new DatabaseCreator();
		      Database database = creator.createDatabase(statement, true);
		      DatabaseFiller filler = new DatabaseFiller();
		      filler.fillDatabase(statement, database, connection);
		      
		    }
		    catch(SQLException e)
		    {
		      // if the error message is "out of memory", 
		      // it probably means no database file is found
		    	LOG.error("error creating/loading Database " + e);
				System.exit(-1);
		    }
		    finally
		    {
		      try
		      {
		        if(connection != null)
		          connection.close();
		      }
		      catch(SQLException e)
		      {
		    	  LOG.error("error closing... " + e);
		      }
		    }
		  }

	}

