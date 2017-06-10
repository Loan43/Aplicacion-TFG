package tfg.app.controller;

import org.jfree.chart.ChartPanel;

public class Chart {

	private ChartPanel cP = null;
	private String description = null;

	public Chart(ChartPanel cP, String description) {
		super();
		this.cP = cP;
		this.description = description;
	}

	public Chart(ChartPanel cP) {
		super();
		this.cP = cP;
	}

	public ChartPanel getCP() {
		return cP;
	}

	public void setCP(ChartPanel cP) {
		this.cP = cP;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
