package eu.vdmr.raga.db.logic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import eu.vdmr.raga.db.dto.Column;
import eu.vdmr.raga.db.dto.ColumnValue;
import eu.vdmr.raga.db.dto.DBConst;
import eu.vdmr.raga.db.dto.DataType;
import eu.vdmr.raga.db.dto.Database;
import eu.vdmr.raga.db.dto.ForeignKey;
import eu.vdmr.raga.db.dto.Table;

public class DatabaseFiller {
	private static final Logger LOG = LogManager.getLogger(DatabaseFiller.class);
	
	private Map<String, Integer> fkMap = new HashMap<>();

	@SuppressWarnings("unchecked")
	public void fillDatabase(Statement s, Database database, Connection connection) throws SQLException, ParseException, IOException {
		String filename = "null";	
		try {
			filename = StartUp.getProperty(StartUp.DBCONTENTFILE);
			Reader reader = new BufferedReader(new FileReader(filename));
			JSONObject root = (JSONObject) JSONValue.parseWithException(reader);
			LOG.debug("total object is of type " + root.getClass().getName());
			connection.setAutoCommit(false);
			
			JSONArray tables = (JSONArray)root.get(DBConst.VALCONTENT);
			Iterator<JSONObject> tableIter = tables.iterator();
			while (tableIter.hasNext()) {
				JSONObject tableObj = tableIter.next();
				String tablename = (String)tableObj.get(DBConst.VALTABLE);
				LOG.info("filling table " + tablename);
				Table table = database.getTableByName(tablename);
				List<Column> columns = table.getColumns();
				JSONArray rows = (JSONArray)tableObj.get(DBConst.VALROWS);
				Iterator<JSONObject> rowIter = rows.iterator();
				int rowCnt = 1;
				while (rowIter.hasNext()) {
					List<ColumnValue> columnValues = new ArrayList<>(); 
					JSONObject row = (JSONObject)rowIter.next();
					for (Column column : columns) {
						String value = (String)row.get(column.getName());
						if (value != null) {
							ColumnValue colVal = new ColumnValue(column);
							if (column.getForeignKey() != null) {
								int fk = getFK(column.getForeignKey(), value, connection);
								colVal.setValue(""+fk);
							} else {
								colVal.setValue(value);
							}
							columnValues.add(colVal);
//						} else { // if FK column we must enter NULL explicitly
//							if (column.getForeignKey() != null) {
//								columnValues.add(new ColumnValue(column));
//							}
						}
					}
					String insertString = makeInsertStatement(tablename, columnValues);
					LOG.debug("Inserting: " + insertString);
					try {
						s.executeUpdate(insertString);
						rowCnt++;
						if (rowCnt % 100 == 0) {
							LOG.info("nr of records inserted: " + rowCnt);
							connection.commit();
						}
					} catch (SQLException sqle) {
						LOG.error("Error executing insert '" + insertString + "': " + sqle, sqle);
					}
				}
				connection.commit();
			}
		} catch (Exception e) {
			LOG.error("Error parsing " + filename + ": " + e, e);
		}
	}
	

	private String makeInsertStatement(String tablename, List<ColumnValue> columnValues){
		StringBuilder sb = new StringBuilder();
		sb.append("insert into ").append(tablename).append(" (id ");
		boolean first = false;
		for (ColumnValue val : columnValues) {
			if (first) {
				sb.append(" " + val.getName());
				first = false;
			} else {
				sb.append(" ," + val.getName());
			}
		}
		sb.append(") values ( $next_id");
		first = false;
		for (ColumnValue val : columnValues) {
			if (first) {
				sb.append(" ");
				first = false;
			} else {
				sb.append(", ");
			}
			if (val.getValue() == null) {
				sb.append("NULL");
			} else {
				if (DataType.TEXT.equals(val.getType())) {
					sb.append("'");
				}
				if (DataType.DATE.equals(val.getType())) {
					sb.append("date('");
				}
				sb.append(val.getValue());
				if (DataType.TEXT.equals(val.getType())) {
					sb.append("'");
				}
				if (DataType.DATE.equals(val.getType())) {
					sb.append("')");
				}
			}
		}
		sb.append(")");
		return sb.toString();
	}

	private int getFK(ForeignKey fk, String content, Connection connection) throws SQLException {
		//LOG.debug("searching for FK: " + fk.toString() + ", value = " + content);
		StringBuilder sb = new StringBuilder();
		String table = fk.getTargetTable();
		String col = fk.getTargetColumn();
		sb.append("select "  ).append(col).append(" from ").append(table);
		String valcol = fk.getTargetValue();
		if (valcol.indexOf(';') > -1) {
			String[] selectCols = valcol.split(";");
			String[] selVals = content.split(";");
			boolean first = true;
			for (int x = 0; x < selectCols.length; x++) {
				if (first) {
					sb.append(" where ").append(selectCols[x]).append(" = ").append("'").append(selVals[x]).append("'");
					first = false;
				} else {
					sb.append(" and ").append(selectCols[x]).append(" = ").append("'").append(selVals[x]).append("'");
				}
			}
		} else {
			sb.append(" where ").append(valcol).append(" = ").append("'").append(content).append("'");
		}
		String FKSearchString = sb.toString();
		LOG.debug("Searching foreign key with: " + FKSearchString);
		Integer result = fkMap.get(FKSearchString);
		if (result != null) { 
			LOG.debug("Found in map");
			return result.intValue();
		}
		try {
			ResultSet rs = connection.createStatement().executeQuery(sb.toString());
		
			rs.next();
			int res =  rs.getInt(col);
			LOG.debug("found key (not in Map)" + res);
			fkMap.put(FKSearchString, Integer.valueOf(res));
			return res;
		} catch (SQLException sqle) {
			LOG.error("Error executing " + sb.toString() +": " + sqle,sqle);
			throw sqle;
		}
		//return -1;
	}
/*
 * INSERT INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY)
VALUES (1, 'Paul', 32, 'California', 20000.00 );

 */

}
