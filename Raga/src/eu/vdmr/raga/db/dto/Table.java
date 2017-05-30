package eu.vdmr.raga.db.dto;

import java.util.ArrayList;
import java.util.List;

public class Table {
	
	private String name;
	private List<Column> columns = new ArrayList<>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Column> getColumns() {
		return columns;
	}
	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}
	public void addColumn(Column column) {
		this.columns.add(column);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name).append(":");
		if (columns.isEmpty()) {
			sb.append("no Columns yet");
		} else {
			boolean first = true;
			for (Column column : columns) {
				if (!first){
					sb.append(",");
					first = false;
				}
				sb.append(column.toString());
			}
		}
		return sb.toString();
	}

}
