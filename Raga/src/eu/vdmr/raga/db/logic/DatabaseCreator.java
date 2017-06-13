package eu.vdmr.raga.db.logic;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.vdmr.raga.db.dto.Column;
import eu.vdmr.raga.db.dto.Database;
import eu.vdmr.raga.db.dto.ForeignKey;
import eu.vdmr.raga.db.dto.Table;

public class DatabaseCreator {
	private static final Logger LOG = LogManager.getLogger(DatabaseCreator.class);	
	
	//private static final String FILENAME = "D:/workspaces/eclipse_repeat/repeat/src/musicDBdef.json";
	
	public Database createDatabase(Statement s, boolean cleanup) throws FileNotFoundException, SQLException {
		String filename = StartUp.getProperty(StartUp.DBDEFFILE);
		DbJsonReader reader = new DbJsonReader();
		Database database = reader.readJson(filename);
		if (cleanup) {
			for (int x = database.getTableCount() - 1; x >= 0; x--) {
				Table table = database.getTableByIndex(x);
				s.executeUpdate("drop table if exists " + table.getName());
			}
		}
		for (Table table : database.getTables()) {
			LOG.debug("Table found: " + table);
			String createTableCmd = makeCreateTableCmd(table);
			LOG.debug("Executing: " + createTableCmd );
			s.executeUpdate(createTableCmd);
		}
		return database;
	}

	private String makeCreateTableCmd(Table table) {
		StringBuilder sb  = new StringBuilder();
		sb.append("create table " + table.getName() + " (" );
		boolean first = true;
		List<ForeignKey> fk_list = new ArrayList<>();
		for (Column column : table.getColumns()) {
			LOG.info(column.toString());
			appendColumnDefinition(column, sb, first, fk_list);
			first = false;
		}
		if (! fk_list.isEmpty()) {
			for (ForeignKey foreignKey : fk_list){
				sb.append(",FOREIGN KEY("+foreignKey.getSourceColumn()+") REFERENCES "
			               + foreignKey.getTargetTable()+ "("+ foreignKey.getTargetColumn()+ ")");
			}
		}
		sb.append(")");
		return sb.toString();
	}
	
	private void appendColumnDefinition(Column column, StringBuilder sb, boolean first, List<ForeignKey> fk_list) {
		if (!first){
			sb.append(", ");
		}
		//
		LOG.info(" = " + column.getName());
		if ("carnatic".equals(column.getName())){
			int a = 6;
			int b = a;
		}
		//
		sb.append(column.getName()).append(" ").append(column.getType().getSqliteString());
		if (column.getDefaultValue() != null) {
			sb.append(" DEFAULT " + column.getDefaultValue());
		}
		if (column.isPk()) {
			sb.append(" PRIMARY KEY ");
		}
		if (column.isAutoInc()) {
			sb.append(" AUTOINCREMENT ");
		}
		if (column.getForeignKey() != null) {
			fk_list.add(column.getForeignKey());
		}
	}
}
