package eu.vdmr.raga.db.dto;

public enum DataType {

	INT("INTEGER"),
	FLOAT("FLOAT"),
	TEXT("TEXT"),
	DATE("DATE");
	
	private final String sqliteString;
	
	DataType(String sqliteString) {
		this.sqliteString = sqliteString;
	}
	
	public String getSqliteString() {
		return sqliteString;
	}
	
	public static DataType toType(String name) {
		switch (name) {
		case "INTEGER" :
			return INT;
		case "TEXT" :
			return TEXT;
		case "DATE" :
			return DATE;
		default :
			throw new IllegalArgumentException("unknown dataype name: " + name);
		}
	}
}
