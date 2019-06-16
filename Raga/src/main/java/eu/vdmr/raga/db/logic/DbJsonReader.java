package eu.vdmr.raga.db.logic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import eu.vdmr.raga.db.dto.Column;
import eu.vdmr.raga.db.dto.DBConst;
import eu.vdmr.raga.db.dto.DataType;
import eu.vdmr.raga.db.dto.Database;
import eu.vdmr.raga.db.dto.ForeignKey;
import eu.vdmr.raga.db.dto.Table;

public class DbJsonReader {
	private static final Logger LOG = LogManager.getLogger(DbJsonReader.class);	
	@SuppressWarnings("unchecked")
	
	
	public Database readJson(String filename) throws FileNotFoundException {
		Database database  = new Database();
		
	
		Reader reader = new BufferedReader(new FileReader(filename));
		try {
			JSONObject obj = (JSONObject)JSONValue.parseWithException(reader);
			LOG.debug("total object is of type " + obj.getClass().getName());
			if (obj != null && obj instanceof JSONObject) {
				JSONObject root = (JSONObject) obj;
				LOG.debug(root);
				JSONArray tables = (JSONArray)root.get(DBConst.TABLELIST);
				Iterator<JSONObject> tableIter = tables.iterator();
				while (tableIter.hasNext()) {
					Table table = new Table();
					JSONObject tableObj = tableIter.next();
					String tablename = (String)tableObj.get(DBConst.TABLENAME);
					table.setName(tablename);
					LOG.debug("tablename = " + tablename);
					JSONArray columns = (JSONArray) tableObj.get(DBConst.TABLECOLS);
					Iterator<JSONObject> columnIter = columns.iterator();
					while (columnIter.hasNext()) {
						Column column = new Column();
						JSONObject columnObject = columnIter.next();
						String columnName = (String) columnObject.get(DBConst.COLNAME);
						column.setName(columnName);
						String columnType = (String) columnObject.get(DBConst.COLTYPE);
						column.setType(DataType.toType(columnType));
						Boolean pk = (Boolean) columnObject.get(DBConst.COLPK);
						if (pk != null && pk.booleanValue() == true) {
							column.setPk(true);
						}
						Boolean ai = (Boolean) columnObject.get(DBConst.COLAUTOINCR);
						if (ai != null && ai.booleanValue() == true) {
							column.setAutoInc(true);
						}
						String ref = (String) columnObject.get(DBConst.COLFK);
						if (ref != null && ref.length() > 3) {
							String[] refs = ref.split(",");
							if (refs.length < 3){
								LOG.error("missing data in ref for " + tablename + "." + columnName);
							}
							ForeignKey fk = new ForeignKey(refs[0], refs[1], refs[2], columnName);
							column.setForeignKey(fk);
						}
						String defaultVal = (String) columnObject.get(DBConst.COLDEFVALUE);
						if (defaultVal != null) {
							column.setDefaultValue(defaultVal);
						}
						table.addColumn(column);
					}
					database.add(table);
				}
			}
		} catch (Exception e) {
			LOG.error("Error parsing " + filename + ": " + e, e);
		}
		return database;
	}
}
