package tfg.app.controller;

import java.awt.BorderLayout;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.time.temporal.ChronoUnit;
import javax.swing.JEditorPane;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import tfg.app.model.entities.FundDesc;
import tfg.app.model.entities.FundPort;
import tfg.app.model.entities.FundVl;
import tfg.app.model.service.FundService;
import tfg.app.util.comparator.compVl;
import tfg.app.util.exceptions.InstanceNotFoundException;

/**
 * Clase que se encarga de crear la gráfica solicitada, insertarla en un JPanel
 * y añadir una descripcción acorde en un JEditorPane.
 */

public class ChartMaker {

	/**
	 * Crea una gráfica en forma de tarta con la distribución del capital en una
	 * cartera.
	 * 
	 * @param
	 * @throws InstanceNotFoundException
	 */

	public void createPortfolioDistributionChart(FundService fundService, JPanel panel, JEditorPane description,
			FundPort fundPort) throws InstanceNotFoundException {

		List<FundDesc> fundDescs = null;
		DefaultPieDataset dataset = new DefaultPieDataset();

		fundDescs = fundService.findFundsOfPortfolio(fundPort);

		for (int x = 0; x < fundDescs.size(); x++) {

			try {
				dataset.setValue(fundDescs.get(x).toString(),
						fundService.findLatestPortOp(fundPort, fundDescs.get(x), LocalDate.now()).getfPartfin());
			} catch (InstanceNotFoundException e) {
				continue;
			}

		}

		JFreeChart chart = ChartFactory.createPieChart("Participaciones de la cartera " + fundPort.getpName(), dataset,
				true, true, false);

		ChartPanel CP = new ChartPanel(chart);

		panel.removeAll();
		panel.updateUI();
		panel.setLayout(new java.awt.BorderLayout());
		panel.add(CP, BorderLayout.CENTER);
		panel.validate();

		description.setText("Gráfica de la distrubución del capital de la cartera " + fundPort.getpName());
	}

	/**
	 * Crea una gráfica de lineas con los valores liquidativos de un fondo.
	 * <p>
	 * Si las fechas son nulas se toman todos los vls asociados al fondo, en
	 * caso contrario se toman los vls acotados entre ambas fechas.
	 * 
	 * @param
	 */

	public void createFundVlLineChart(FundService fundService, JPanel panel, JEditorPane description, FundDesc fundDesc,
			LocalDate start, LocalDate end) {

		DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();
		List<FundVl> fundVlList = null;
		String string = "";

		if (start == null || end == null) {

			fundVlList = fundDesc.getFundVls();

			if (fundVlList.size() == 0) {
				string = ("El fondo seleccionado: " + fundDesc.getfName() + " con ISIN: " + fundDesc.getfId()
						+ " no tiene ningún Vl.");
			} else {

				LocalDate date1 = fundVlList.get(0).getDay();
				LocalDate date2 = fundVlList.get(fundDesc.getFundVls().size() - 1).getDay();

				string = ("Gáfica con el historial de los Vl del fondo: " + fundDesc.getfName() + " con ISIN: "
						+ fundDesc.getfId() + " entre los días: " + date1 + " y " + date2 + ".");

			}

		} else {

			fundVlList = fundService.findFundVlbyRange(fundDesc, start, end);

			if (fundVlList.size() == 0) {
				string = ("El fondo seleccionado: " + fundDesc.getfName() + " con ISIN: " + fundDesc.getfId()
						+ " no tiene ningún Vl entre los días: " + start + " y " + end + ".");
			} else {

				string = ("Gáfica con el historial de los Vl del fondo: " + fundDesc.getfName() + " con ISIN: "
						+ fundDesc.getfId() + " entre los días: " + start + " y " + end + ".");

			}

		}

		for (int x = 0; x < fundVlList.size(); x++) {
			line_chart_dataset.addValue(fundVlList.get(x).getVl(), "Valor liquidativo",
					fundVlList.get(x).getDay().toString());
		}

		JFreeChart lineChartObject = ChartFactory.createLineChart("Historial del valor liquidativo", "Días", "Valor",
				line_chart_dataset, PlotOrientation.VERTICAL, true, true, false);

		ChartPanel CP = new ChartPanel(lineChartObject);
		panel.removeAll();
		panel.updateUI();
		panel.setLayout(new java.awt.BorderLayout());
		panel.add(CP, BorderLayout.CENTER);

		description.setText(string);

	}

	/**
	 * Crea una gráfica de lineas con los valores liquidativos normalizados de
	 * todos los fondos de una cartera.
	 * 
	 * @param
	 */
	public void createFundDescsOfPortfolioNormalizedLineChart(FundService fundService, JPanel panel,
			JEditorPane description, FundPort fundPort) throws InstanceNotFoundException {

		List<FundDesc> fundDescs = null;
		DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();

		fundDescs = fundService.findFundsOfPortfolio(fundPort);

		for (int x = 0; x < fundDescs.size(); x++) {

			for (int y = 0; y < fundDescs.get(x).getFundVls().size(); y++) {

				Double max = Collections.max(fundDescs.get(x).getFundVls(), new compVl()).getVl();
				Double min = Collections.min(fundDescs.get(x).getFundVls(), new compVl()).getVl();
				Double z = fundDescs.get(x).getFundVls().get(y).getVl();

				line_chart_dataset.addValue((z - min) / (max - min), fundDescs.get(x).getfName(),
						fundDescs.get(x).getFundVls().get(y).getDay().toString());
			}

		}

		JFreeChart lineChartObject = ChartFactory.createLineChart("Historial del valor liquidativo", "Días", "Valor",
				line_chart_dataset, PlotOrientation.VERTICAL, true, true, false);

		ChartPanel CP = new ChartPanel(lineChartObject);
		panel.removeAll();
		panel.updateUI();
		panel.setLayout(new java.awt.BorderLayout());
		panel.add(CP, BorderLayout.CENTER);

		if (fundDescs.size() == 0) {
			description.setText("La cartera seleccionada: " + fundPort.getpName() + " no tiene ningún fondo asignado.");
		} else {

			description.setText("Gráfica de los valores de la cartera: " + fundPort.getpName() + " normalizados.");

		}

	}

	/**
	 * Crea una gráfica de lineas que muestra la rentabilidad por dia y compara
	 * los Vls reales con los esperados en función de la rentabilidad por día.
	 * 
	 * @param
	 */
	public void createEstimateProfitOfFundDescLineChart(FundService fundService, JPanel panel, JEditorPane description,
			FundDesc fundDesc) {

		FundVl vlInicial = fundDesc.getFundVls().get(0);

		FundVl vlFinal = fundDesc.getFundVls().get(fundDesc.getFundVls().size() - 1);

		double d = vlInicial.getDay().until(vlFinal.getDay(), ChronoUnit.DAYS);

		double resultado = (Math.pow(vlFinal.getVl() / vlInicial.getVl(), (1 / d))) - 1;

		DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();

		List<FundVl> fundVls = fundService.findFundVlbyRange(fundDesc, LocalDate.parse("2017-01-02"), LocalDate.now());

		double primero = (fundVls.get(0).getVl());

		double siguiente = primero / (1 + resultado);

		for (int y = 0; y < fundVls.size(); y++) {

			siguiente = siguiente * (1 + resultado);

			line_chart_dataset.addValue(siguiente, "Vl Esperado", fundVls.get(y).getDay().toString());
			line_chart_dataset.addValue(fundVls.get(y).getVl(), "Vl real del fondo",
					fundVls.get(y).getDay().toString());

		}

		JFreeChart lineChartObject = ChartFactory.createLineChart("Rentabilidad Esperada", "Días", "Valor",
				line_chart_dataset, PlotOrientation.VERTICAL, true, true, false);

		ChartPanel CP = new ChartPanel(lineChartObject);
		panel.removeAll();
		panel.updateUI();
		panel.setLayout(new java.awt.BorderLayout());
		panel.add(CP, BorderLayout.CENTER);

		description.setText(" Gráfica que muestra la rentabilidad por dia del fondo " + fundDesc.getfName()
				+ " y comparala los " + "Vls reales con los esperados en función de la rentabilidad por día");

	}

	/**
	 * Crea una gráfica de barras que muestra las rentabilidades históricas de
	 * un fondo para los siguientes períodos de tiempo:
	 * <p>
	 * Último año fiscal, último semestre, último trimestre y último mes
	 * 
	 * @param
	 */

	public void createFundDescProfitBarChart(FundService fundService, JPanel panel, JEditorPane description,
			FundDesc fundDesc) {

		DefaultCategoryDataset bar_chart_dataset = new DefaultCategoryDataset();
		Double profit = 0.0;

		String string = "Gráfica que muestra las rentabilidades históricas del fondo " + fundDesc.getfName()
				+ ", para los siguientes períodos de tiempo:\n"
				+ "Último año fiscal, último semestre, último trimestre y último mes.\n";

		LocalDate today = LocalDate.now();

		// Año

		List<FundVl> fundVls = fundService.findFundVlbyRange(fundDesc,
				LocalDate.parse(today.minusYears(1).getYear() + "-01-01"),
				LocalDate.parse(today.minusYears(1).getYear() + "-12-31"));

		if (fundVls.size() >= 300) {

			profit = (fundVls.get(fundVls.size() - 1).getVl() - fundVls.get(0).getVl()) / fundVls.get(0).getVl();
			bar_chart_dataset.addValue(profit, "Año", "Último año fiscal");
		} else {
			string += "\nNo existen suficentes datos para calcular la rentabilidad del último año fiscal ("
					+ today.minusYears(1).getYear() + "). ";
		}

		// Semestre

		fundVls = fundService.findFundVlbyRange(fundDesc, today.minusMonths(6), today);

		if (fundVls.size() >= 180) {
			profit = (fundVls.get(fundVls.size() - 1).getVl() - fundVls.get(0).getVl()) / fundVls.get(0).getVl();
			bar_chart_dataset.addValue(profit, "Semestre", "Último semestre");
		} else {
			string += "\nNo existen suficentes datos para calcular la rentabilidad del último semestre. ";
		}

		// Trimestre

		fundVls = fundService.findFundVlbyRange(fundDesc, today.minusMonths(3), today);

		if (fundVls.size() >= 90) {
			profit = (fundVls.get(fundVls.size() - 1).getVl() - fundVls.get(0).getVl()) / fundVls.get(0).getVl();
			bar_chart_dataset.addValue(profit, "Trimestre", "Último trimestre");
		} else {
			string += "\nNo existen suficentes datos para calcular la rentabilidad del último trimestre. ";
		}

		// Mes

		fundVls = fundService.findFundVlbyRange(fundDesc, today.minusMonths(1), today);

		if (fundVls.size() >= 30) {
			profit = (fundVls.get(fundVls.size() - 1).getVl() - fundVls.get(0).getVl()) / fundVls.get(0).getVl();
			bar_chart_dataset.addValue(profit, "Mes", "Último mes");
		} else {
			string += "\nNo existen suficentes datos para calcular la rentabilidad del último mes. ";
		}

		JFreeChart barChart = ChartFactory.createBarChart("Rentabilidades", "Fondo", "Rentabilidad", bar_chart_dataset,
				PlotOrientation.VERTICAL, true, true, false);

		ChartPanel CP = new ChartPanel(barChart);
		panel.removeAll();
		panel.updateUI();
		panel.setLayout(new java.awt.BorderLayout());
		panel.add(CP, BorderLayout.CENTER);

		description.setText(string);

	}

	/**
	 * Crea una gráfica de barras que muestra los cinco fondos más y menos
	 * rentables de la cartera.
	 * 
	 * @param
	 */
	public void createPortfolioProfitBarChart(FundService fundService, JPanel panel, JEditorPane description,
			FundPort fundPort) throws InstanceNotFoundException {

		List<FundDesc> fundDescs = null;

		fundDescs = fundService.getProfitOfFundsOfPortfolio(fundPort);

		DefaultCategoryDataset bar_chart_dataset = new DefaultCategoryDataset();

		for (int x = 0; x < fundDescs.size() && x < 5; x++) {

			if (fundDescs.get(x).getProfit() > 0) {
				bar_chart_dataset.addValue(fundDescs.get(x).getProfit(), fundDescs.get(x).getfName(), "Más rentables");
			}

		}

		for (int x = fundDescs.size() - 1; x >= 0 && x >= fundDescs.size() - 5; x--) {
			if (fundDescs.get(x).getProfit() <= 0) {

				bar_chart_dataset.addValue(fundDescs.get(x).getProfit(), fundDescs.get(x).getfName(),
						"Menos rentables");
			}

		}

		JFreeChart barChart = ChartFactory.createBarChart("Rentabilidades", "Fondo", "Rentabilidad", bar_chart_dataset,
				PlotOrientation.VERTICAL, true, true, false);

		ChartPanel CP = new ChartPanel(barChart);
		panel.removeAll();
		panel.updateUI();
		panel.setLayout(new java.awt.BorderLayout());
		panel.add(CP, BorderLayout.CENTER);

		if (fundDescs.size() == 0) {
			description.setText("La cartera seleccionada: " + fundPort.getpName() + " no tiene ningún fondo asignado.");
		} else {

			description.setText("Gráfica de los fondos más y menos rentables de la cartera: " + fundPort.getpName());

		}

	}

}
