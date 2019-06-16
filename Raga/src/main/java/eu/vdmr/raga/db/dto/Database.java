package eu.vdmr.raga.db.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Database {
	private static final Logger LOG = LogManager.getLogger(Database.class);
	List<Table> tables = new ArrayList<>();

	public List<Table> getTables() {
		return tables;
	}

	public void add(Table table) {
		tables.add(table);
	}
	
	public int getTableCount() {
		return tables.size();
	}

	public Table getTableByIndex(int x) {
		return tables.get(x);
	}

	public Table getTableByName(String tablename) {
		for (Table table : tables){
			if (tablename.equals(table.getName())) {
				return table;
			}
		}
		LOG.error("table not found by name: " + tablename);
		return null;
	}
}
