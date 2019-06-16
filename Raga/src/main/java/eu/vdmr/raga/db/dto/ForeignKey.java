package eu.vdmr.raga.db.dto;

public class ForeignKey {

	private String sourceColumn;
	private String targetTable;
	private String targetColumn;
	private String targetValue; // column which will be used 
	
	public ForeignKey(String targetTable, String targetCol, String targetValue, String sourceCol) {
		this.targetTable = targetTable;
		this.targetColumn = targetCol;
		this.targetValue = targetValue;
		this.sourceColumn = sourceCol;
	}
	
	public String getTargetTable() {
		return targetTable;
	}
	public String getSourceColumn() {
		return sourceColumn;
	}
	public String getTargetColumn() {
		return targetColumn;
	}
	public String getTargetValue() {
		return targetValue;
	}

	@Override
	public String toString() {
		return ", FK = " + targetTable + "," + targetColumn + "," + sourceColumn + "," + targetValue;
	}
}
