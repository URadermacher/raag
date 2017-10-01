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
	
	private List<String> errors = new ArrayList<>();

	@SuppressWarnings("unchecked")
	public void fillDatabase(Statement s, Database database, Connection connection) throws SQLException, ParseException, IOException {
		String dirname = "null";	
		dirname = StartUp.getProperty(StartUp.DBCONTENTDIR);
		String filenamesString = StartUp.getProperty(StartUp.DBCONTENTFILES);
		String[] filenames = filenamesString.split(",");
		connection.setAutoCommit(false);
		
		for (String filename :  filenames) {
			Reader reader = null;
			try {
				reader = new BufferedReader(new FileReader(dirname+filename));
				LOG.info("reading file " + filename);
				JSONObject root = (JSONObject) JSONValue.parseWithException(reader);
				LOG.debug("total object is of type " + root.getClass().getName());
				String tablename = (String) root.get(DBConst.VALTABLE);
				LOG.info("filling table " + tablename);
				if ("RagaComment".equals(tablename)) {
					int a = 2;
				}
				Table table = database.getTableByName(tablename);
				List<Column> columns = table.getColumns();
				JSONArray rows = (JSONArray)root.get(DBConst.VALROWS);
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
						if (rowCnt % 500 == 0) {
							LOG.info("nr of records inserted: " + rowCnt);
							connection.commit();
						}
					} catch (SQLException sqle) {
						LOG.error("Error executing insert '" + insertString + "': " + sqle, sqle);
					}
				}
				connection.commit();
			} catch (Exception e) {
				LOG.error("Error parsing " + filename + ": " + e, e);
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		}
		if (errors.isEmpty()) {
			LOG.info("Ready..");
		} else {
			LOG.info("Ready with errors.. ");
			for (String err : errors) {
				LOG.info(err);
			}
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
			if (selectCols.length != selVals.length) {
				LOG.error("not enough values for columns");
				LOG.error("   selectCols.length " + selectCols.length);
				LOG.error("   selVals.length " + selVals.length);
				for (int selColIdx = 0; selColIdx < selectCols.length; selColIdx++){
					LOG.error("selcol " + selColIdx  + ": " + selectCols[selColIdx]);
				}
				for (int selValIdx = 0; selValIdx < selVals.length; selValIdx++){
					LOG.error("selcol " + selValIdx  + ": " + selVals[selValIdx]);
				}
			}
			boolean first = true;
			for (int x = 0; x < selectCols.length; x++) {
				if (first) {
					sb.append(" where ").append(selectCols[x]).append(" = ").append("'").append(selVals[x]).append("'");
					first = false;
				} else {
					//sb.append(" and ").append(selectCols[x]).append(" = ").append("'").append(selVals[x]).append("'");
					sb.append(" and ");
					sb.append(selectCols[x]);
					sb.append(" = ");
					sb.append("'");
					sb.append(selVals[x]).append("'");
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
			String errStr = "Error executing " + sb.toString() +": " + sqle;
			LOG.error(errStr, sqle);
			errors.add(errStr);
			throw sqle;
		}
	}
}
