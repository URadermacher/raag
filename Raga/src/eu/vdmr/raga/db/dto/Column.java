package eu.vdmr.raga.db.dto;

public class Column {

	private String name;
	private DataType type;
	private boolean pk = false;
	private boolean autoInc = false;
	private ForeignKey foreignKey;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public DataType getType() {
		return type;
	}
	public void setType(DataType type) {
		this.type = type;
	}
	public boolean isPk() {
		return pk;
	}
	public void setPk(boolean pk) {
		this.pk = pk;
	}
	public boolean isAutoInc() {
		return autoInc;
	}
	public void setAutoInc(boolean autoInc) {
		this.autoInc = autoInc;
	}
	public ForeignKey getForeignKey() {
		return foreignKey;
	}
	public void setForeignKey(ForeignKey foreignKey) {
		this.foreignKey = foreignKey;
	}
	
	@Override
	public String toString() {
		return name + "-" + type + (pk?",PK":"") + (autoInc?",AI":"") + (foreignKey!=null?foreignKey.toString():"");
	}
}
