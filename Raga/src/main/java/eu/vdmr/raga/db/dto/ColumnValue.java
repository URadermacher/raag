package eu.vdmr.raga.db.dto;

public class ColumnValue extends Column {
	
	private String value;

	public ColumnValue(Column column) {
		this.setAutoInc(column.isAutoInc());
		this.setForeignKey(column.getForeignKey());
		this.setName(column.getName());
		this.setPk(column.isPk());
		this.setType(column.getType());
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
