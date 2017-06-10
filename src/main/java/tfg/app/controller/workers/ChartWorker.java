package tfg.app.controller.workers;

import java.awt.BorderLayout;
import java.time.LocalDate;
import java.util.concurrent.ExecutionException;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import tfg.app.controller.Chart;
import tfg.app.model.entities.FundDesc;
import tfg.app.model.entities.FundPort;
import tfg.app.model.service.FundService;
import tfg.app.util.exceptions.InstanceNotFoundException;

public class ChartWorker extends SwingWorker<Chart, Integer> {

	ChartMaker chartMaker = new ChartMaker();

	private JOptionPane ventanaError = new javax.swing.JOptionPane();
	private FundService fundService;
	private JPanel panel;
	private JEditorPane description;
	private FundPort fundPort;
	private FundDesc fundDesc;
	private Double estimate;
	private LocalDate start;
	private int days;
	private LocalDate end;
	private int selectedChart = 0;

	public ChartWorker(FundService fundService, JPanel panel, JEditorPane description) {
		super();
		this.fundService = fundService;
		this.panel = panel;
		this.description = description;
	}

	public void setPortfolioDistributionChart(FundPort fundPort) {

		this.fundPort = fundPort;
		this.selectedChart = 0;

	}

	public void setProfitOfPortfolioLineChart(FundPort fundPort, LocalDate start, LocalDate end) {

		this.fundPort = fundPort;
		this.start = start;
		this.end = end;
		this.selectedChart = 1;

	}

	public void setFundVlLineChart(FundDesc fundDesc, LocalDate start, LocalDate end) {

		this.fundDesc = fundDesc;
		this.start = start;
		this.end = end;
		this.selectedChart = 2;

	}

	public void setFundDescsOfPortfolioNormalizedLineChart(FundPort fundPort) {

		this.fundPort = fundPort;
		this.selectedChart = 3;

	}

	public void setEstimateProfitOfFundDescLineChart(FundDesc fundDesc, Double estimate, LocalDate start,
			LocalDate end) {

		this.fundDesc = fundDesc;
		this.start = start;
		this.end = end;
		this.estimate = estimate;
		this.selectedChart = 4;

	}

	public void setFundDescMeanMobileLineChart(FundDesc fundDesc, int days) {

		this.fundDesc = fundDesc;
		this.days = days;
		this.selectedChart = 5;
	}

	public void setFundDescProfitBarChart(FundDesc fundDesc) {

		this.fundDesc = fundDesc;
		this.selectedChart = 6;
	}

	public void setPortfolioMostProfitableFundsBarChart(FundPort fundPort) {

		this.fundPort = fundPort;
		this.selectedChart = 7;

	}

	public void setPortfolioFundsValueBarChart(FundPort fundPort) {

		this.fundPort = fundPort;
		this.selectedChart = 8;

	}

	@Override
	protected Chart doInBackground() throws InstanceNotFoundException {

		Chart chart = null;

		switch (selectedChart) {

		case 0:
			chart = chartMaker.createPortfolioDistributionChart(fundService, fundPort);
			break;
		case 1:
			chart = chartMaker.createProfitOfPortfolioLineChart(fundService, fundPort, start, end);
			break;
		case 2:
			chart = chartMaker.createFundVlLineChart(fundService, fundDesc, start, end);
			break;
		case 3:
			chart = chartMaker.createFundDescsOfPortfolioNormalizedLineChart(fundService, fundPort);
			break;
		case 4:
			chart = chartMaker.createEstimateProfitOfFundDescLineChart(fundService, fundDesc, estimate, start, end);
			break;
		case 5:
			chart = chartMaker.createFundDescMeanMobileLineChart(fundService, fundDesc, days);
			break;
		case 6:
			chart = chartMaker.createFundDescProfitBarChart(fundService, fundDesc);
			break;
		case 7:
			chart = chartMaker.createPortfolioMostProfitableFundsBarChart(fundService, fundPort);
			break;
		case 8:
			chart = chartMaker.createPortfolioFundsValueBarChart(fundService, fundPort);
			break;
		}

		return chart;

	}

	@Override
	protected void done() {

		try {
			Chart chart = get();

			panel.removeAll();
			panel.updateUI();
			panel.setLayout(new java.awt.BorderLayout());
			panel.add(chart.getCP(), BorderLayout.CENTER);

			description.setText(chart.getDescription());

		} catch (InterruptedException e) {

		} catch (ExecutionException e) {
			JOptionPane.showMessageDialog(ventanaError, e.getMessage(), "Error de base de datos",
					JOptionPane.ERROR_MESSAGE);
		}

	}

}
