package tfg.app.controller.workers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.time.temporal.ChronoUnit;
import javax.swing.SwingWorker;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import tfg.app.controller.Chart;
import tfg.app.model.entities.FundDesc;
import tfg.app.model.entities.FundPort;
import tfg.app.model.entities.FundVl;
import tfg.app.model.entities.PortOp;
import tfg.app.model.service.FundService;
import tfg.app.util.comparator.compVl;
import tfg.app.util.exceptions.InstanceNotFoundException;

/**
 * Clase que se encarga de crear la gráfica solicitada, insertarla en un JPanel
 * y añadir una descripcción acorde en un JEditorPane.
 */

public class ChartMaker extends SwingWorker<Chart, Integer> {

	/**
	 * Crea una gráfica en forma de tarta con la distribución del capital en una
	 * cartera. 0
	 * 
	 * @param
	 * @throws InstanceNotFoundException
	 */

	public Chart createPortfolioDistributionChart(FundService fundService, FundPort fundPort)
			throws InstanceNotFoundException {

		List<FundDesc> fundDescs = null;
		DefaultPieDataset dataset = new DefaultPieDataset();
		String desc = "";

		fundDescs = fundService.findFundsOfPortfolio(fundPort);

		for (int x = 0; x < fundDescs.size(); x++) {

			try {

				double vl = 0;
				FundVl fundVl = fundService.findLatestFundVl(fundDescs.get(x), LocalDate.now());

				if (fundVl != null) {
					vl = fundVl.getVl();
				}

				double value = fundService.findLatestPortOp(fundPort, fundDescs.get(x), LocalDate.now()).getfPartfin()
						* vl;
				dataset.setValue(fundDescs.get(x).toString(), value);
			} catch (InstanceNotFoundException e) {
				continue;
			}

		}

		JFreeChart chart = ChartFactory.createPieChart("Capital invertido de la cartera " + fundPort.getpName(),
				dataset, true, true, false);

		PiePlot plot = (PiePlot) chart.getPlot();

		plot.setOutlinePaint(Color.BLUE);
		plot.setOutlineStroke(new BasicStroke(2.0f));

		plot.setBackgroundPaint(Color.DARK_GRAY);

		ChartPanel cP = new ChartPanel(chart);

		if (fundDescs.size() == 0) {
			desc = ("La cartera seleccionada: " + fundPort.getpName() + " no tiene ningún fondo asignado.");
		} else {
			desc = ("Gráfica de la distrubución del capital de la cartera " + fundPort.getpName());
		}

		return new Chart(cP, desc);
	}

	/**
	 * Crea una gráfica de lineas con la rentabilidad diaria en conjunto de la
	 * cartera. 1
	 * 
	 * @param
	 * @throws InstanceNotFoundException
	 */

	public Chart createProfitOfPortfolioLineChart(FundService fundService, FundPort fundPort, LocalDate start,
			LocalDate end) throws InstanceNotFoundException {

		List<FundDesc> fundDescs = null;
		double total = 0;
		String desc = "";

		TimeSeries series = new TimeSeries("Rentabilidad total");

		for (int x = 0; start.plusDays(x).compareTo(end) < 0; x++) {

			fundDescs = fundService.getProfitOfFundsOfPortfolio(fundPort, start.plusDays(x));
			total = 0;

			for (int i = 0; i < fundDescs.size(); i++) {

				total = total + fundDescs.get(i).getProfit();

			}
			LocalDate date = start.plusDays(x);
			Day day = new Day(date.getDayOfMonth(), date.getMonthValue(), date.getYear());
			series.add(day, total);

		}

		final TimeSeriesCollection data = new TimeSeriesCollection(series);

		final JFreeChart chart = ChartFactory.createTimeSeriesChart("Rentabilidad total de la cartera", "Fecha",
				"Rentabilidad (%)", data, true, true, false);

		XYPlot plot = (XYPlot) chart.getPlot();

		plot.getRenderer().setSeriesPaint(0, Color.BLUE);

		plot.setOutlinePaint(Color.BLUE);
		plot.setOutlineStroke(new BasicStroke(2.0f));

		plot.setBackgroundPaint(Color.DARK_GRAY);

		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.BLACK);

		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.BLACK);

		ChartPanel cP = new ChartPanel(chart);

		// panel.removeAll();
		// panel.updateUI();
		// panel.setLayout(new java.awt.BorderLayout());
		// panel.add(CP, BorderLayout.CENTER);
		//
		// description.setText("Gráfica de la rentabilidad total del capital de
		// la cartera " + fundPort.getpName()
		// + " en los últimos " + start.until(end, ChronoUnit.DAYS) + " días.");

		desc = ("Gráfica de la rentabilidad total del capital de la cartera " + fundPort.getpName() + " en los últimos "
				+ start.until(end, ChronoUnit.DAYS) + " días.");

		return new Chart(cP, desc);
	}

	/**
	 * Crea una gráfica de lineas con los valores liquidativos de un fondo.
	 * <p>
	 * Si las fechas son nulas se toman todos los vls asociados al fondo, en
	 * caso contrario se toman los vls acotados entre ambas fechas. 2
	 * 
	 * @param
	 */

	public Chart createFundVlLineChart(FundService fundService, FundDesc fundDesc, LocalDate start, LocalDate end) {

		List<FundVl> fundVlList = null;
		String desc = "";

		TimeSeries series = new TimeSeries("Valor Liquidativo");

		if (start == null || end == null) {

			fundVlList = fundDesc.getFundVls();

			if (fundVlList.size() == 0) {
				desc = ("El fondo seleccionado: " + fundDesc.getfName() + " con ISIN: " + fundDesc.getfId()
						+ " no tiene ningún Vl.");
			} else {

				LocalDate date1 = fundVlList.get(0).getDay();
				LocalDate date2 = fundVlList.get(fundDesc.getFundVls().size() - 1).getDay();

				desc = ("Gáfica con el historial de los Vl del fondo: " + fundDesc.getfName() + " con ISIN: "
						+ fundDesc.getfId() + " entre los días: " + date1 + " y " + date2 + ".");

			}

		} else {

			fundVlList = fundService.findFundVlbyRange(fundDesc, start, end);

			if (fundVlList.size() == 0) {
				desc = ("El fondo seleccionado: " + fundDesc.getfName() + " con ISIN: " + fundDesc.getfId()
						+ " no tiene ningún Vl entre los días: " + start + " y " + end + ".");
			} else {

				desc = ("Gáfica con el historial de los Vl del fondo: " + fundDesc.getfName() + " con ISIN: "
						+ fundDesc.getfId() + " entre los días: " + start + " y " + end + ".");

			}

		}

		for (int x = 0; x < fundVlList.size(); x++) {

			LocalDate date = fundVlList.get(x).getDay();
			Day day = new Day(date.getDayOfMonth(), date.getMonthValue(), date.getYear());
			series.add(day, fundVlList.get(x).getVl());
		}

		final TimeSeriesCollection data = new TimeSeriesCollection(series);

		final JFreeChart chart = ChartFactory.createTimeSeriesChart("Historial del valor liquidativo", "Fecha", "Valor",
				data, true, true, false);

		XYPlot plot = (XYPlot) chart.getPlot();

		plot.getRenderer().setSeriesPaint(0, Color.BLUE);

		plot.setOutlinePaint(Color.BLUE);
		plot.setOutlineStroke(new BasicStroke(2.0f));

		plot.setBackgroundPaint(Color.DARK_GRAY);

		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.BLACK);

		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.BLACK);

		ChartPanel cP = new ChartPanel(chart);

		return new Chart(cP, desc);

	}

	/**
	 * Crea una gráfica de lineas con los valores liquidativos normalizados de
	 * todos los fondos de una cartera. 3
	 * 
	 * @param
	 */
	public Chart createFundDescsOfPortfolioNormalizedLineChart(FundService fundService, FundPort fundPort)
			throws InstanceNotFoundException {

		List<FundDesc> fundDescs = null;
		String desc = "";

		fundDescs = fundService.findFundsOfPortfolio(fundPort);

		final TimeSeriesCollection data = new TimeSeriesCollection();

		for (int x = 0; x < fundDescs.size(); x++) {

			TimeSeries series = new TimeSeries(fundDescs.get(x).getfName());

			for (int y = 0; y < fundDescs.get(x).getFundVls().size(); y++) {

				Double max = Collections.max(fundDescs.get(x).getFundVls(), new compVl()).getVl();
				Double min = Collections.min(fundDescs.get(x).getFundVls(), new compVl()).getVl();
				Double z = fundDescs.get(x).getFundVls().get(y).getVl();

				LocalDate date = fundDescs.get(x).getFundVls().get(y).getDay();
				Day day = new Day(date.getDayOfMonth(), date.getMonthValue(), date.getYear());
				series.add(day, (z - min) / (max - min));

			}
			data.addSeries(series);
		}

		final JFreeChart chart = ChartFactory.createTimeSeriesChart("Valor liquidativo normalizado", "Fecha", "Valor",
				data, true, true, false);

		XYPlot plot = (XYPlot) chart.getPlot();

		plot.setOutlinePaint(Color.BLUE);
		plot.setOutlineStroke(new BasicStroke(2.0f));

		plot.setBackgroundPaint(Color.DARK_GRAY);

		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.BLACK);

		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.BLACK);

		ChartPanel cP = new ChartPanel(chart);

		if (fundDescs.size() == 0) {
			desc = ("La cartera seleccionada: " + fundPort.getpName() + " no tiene ningún fondo asignado.");
		} else {
			desc = ("Gráfica de los valores de la cartera: " + fundPort.getpName() + " normalizados.");
		}

		return new Chart(cP, desc);

	}

	/**
	 * Crea una gráfica de lineas que muestra la rentabilidad por dia y compara
	 * los Vls reales con los esperados en función de la rentabilidad por día. 4
	 * 
	 * @param
	 */
	public Chart createEstimateProfitOfFundDescLineChart(FundService fundService, FundDesc fundDesc, Double estimate,
			LocalDate start, LocalDate end) {

		final TimeSeriesCollection data = new TimeSeriesCollection();
		String desc = "";

		TimeSeries real = new TimeSeries("Vl real del fondo");

		TimeSeries esperado = new TimeSeries("Vl Esperado");

		if (fundDesc.getFundVls().size() != 0) {

			List<FundVl> fundVls = fundService.findFundVlbyRange(fundDesc, start, end);

			if (fundVls.size() != 0) {

				FundVl vlInicial = fundDesc.getFundVls().get(0);

				FundVl vlFinal = fundDesc.getFundVls().get(fundDesc.getFundVls().size() - 1);

				double d = vlInicial.getDay().until(vlFinal.getDay(), ChronoUnit.DAYS);

				double rentDiaria = 0;

				if (estimate == null) {

					rentDiaria = (Math.pow(vlFinal.getVl() / vlInicial.getVl(), (1 / d))) - 1;

				} else {

					rentDiaria = (Math.pow(1 + (estimate / 100), (1 / d))) - 1;

				}

				double primero = (fundVls.get(0).getVl());

				double siguiente = primero / (1 + rentDiaria);

				for (int y = 0; y < fundVls.size(); y++) {

					siguiente = siguiente * (1 + rentDiaria);

					LocalDate date = fundVls.get(y).getDay();
					Day day = new Day(date.getDayOfMonth(), date.getMonthValue(), date.getYear());

					esperado.add(day, siguiente);

					real.add(day, fundVls.get(y).getVl());

				}

			}

		}

		data.addSeries(esperado);
		data.addSeries(real);

		final JFreeChart chart = ChartFactory.createTimeSeriesChart("Rentabilidad Esperada", "Fecha", "Valor", data,
				true, true, false);

		XYPlot plot = (XYPlot) chart.getPlot();

		plot.setOutlinePaint(Color.BLUE);
		plot.setOutlineStroke(new BasicStroke(2.0f));

		plot.setBackgroundPaint(Color.DARK_GRAY);

		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.BLACK);

		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.BLACK);

		ChartPanel cP = new ChartPanel(chart);

		desc = ("Gráfica que calcula la rentabilidad por dia del fondo " + fundDesc.getfName()
				+ ", a partir de su rentabilidad y comparala los Vls reales con los esperados en función de la misma."
				+ "\nSi se introduce 0 en el campo de texto se calculará sobre la rentabilidad real.");

		return new Chart(cP, desc);

	}

	/**
	 * Crea una gráfica de líneas que muestra los vls y sus medias móviles a los
	 * días indicados. 5
	 * 
	 * @param
	 */

	public Chart createFundDescMeanMobileLineChart(FundService fundService, FundDesc fundDesc, int days) {

		final TimeSeriesCollection data = new TimeSeriesCollection();

		String desc = "";

		TimeSeries vls = new TimeSeries("Valor Liquidativo");
		TimeSeries mediaMovil = new TimeSeries("Media Móvil");

		if (fundDesc.getFundVls().size() <= days) {

			desc = "El fondo " + fundDesc.getfName()
					+ " no tiene suficientes valores para poder calcular su media móvil a " + days + " días.";

		} else {

			desc = (" Gráfica que muestra la media móvil del fondo " + fundDesc.getfName()
					+ " para un período de tiempo de " + days + " días.");

			double sum = 0.0;
			int i = 0;

			for (i = 0; i < days; i++) {

				LocalDate date = fundDesc.getFundVls().get(i).getDay();
				Day day = new Day(date.getDayOfMonth(), date.getMonthValue(), date.getYear());

				vls.add(day, fundDesc.getFundVls().get(i).getVl());

				sum = sum + fundDesc.getFundVls().get(i).getVl();

			}

			double anterior = sum / days;

			double siguiente = 0;

			for (int x = i; x < fundDesc.getFundVls().size(); x++) {

				siguiente = ((days * anterior) + fundDesc.getFundVls().get(x).getVl()
						- fundDesc.getFundVls().get(x - days).getVl()) / days;

				LocalDate date = fundDesc.getFundVls().get(x).getDay();
				Day day = new Day(date.getDayOfMonth(), date.getMonthValue(), date.getYear());

				mediaMovil.add(day, siguiente);
				vls.add(day, fundDesc.getFundVls().get(x).getVl());

				anterior = siguiente;
			}
		}

		data.addSeries(vls);
		data.addSeries(mediaMovil);

		final JFreeChart chart = ChartFactory.createTimeSeriesChart("Media móvil a " + days + " días", "Fecha", "Valor",
				data, true, true, false);

		XYPlot plot = (XYPlot) chart.getPlot();

		plot.getRenderer().setSeriesPaint(0, Color.BLUE);

		plot.setOutlinePaint(Color.BLUE);
		plot.setOutlineStroke(new BasicStroke(2.0f));

		plot.setBackgroundPaint(Color.DARK_GRAY);

		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.BLACK);

		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.BLACK);

		ChartPanel cP = new ChartPanel(chart);

		return new Chart(cP, desc);

	}

	/**
	 * Crea una gráfica de barras que muestra las rentabilidades históricas de
	 * un fondo para los siguientes períodos de tiempo:
	 * <p>
	 * Último año fiscal, último semestre, último trimestre y último mes 6
	 * 
	 * @param
	 */

	public Chart createFundDescProfitBarChart(FundService fundService, FundDesc fundDesc) {

		DefaultCategoryDataset bar_chart_dataset = new DefaultCategoryDataset();
		Double profit = 0.0;
		String desc = "";

		desc = "Gráfica que muestra las rentabilidades históricas del fondo " + fundDesc.getfName()
				+ ", para los siguientes períodos de tiempo:\n"
				+ "Último año fiscal, último semestre, último trimestre y último mes.\n";

		if (fundDesc.getFundVls().size() == 0) {

			desc += "\nEl fondo no tiene ningún VL.";

		} else {

			LocalDate today = LocalDate.now();

			// Año

			List<FundVl> fundVls = fundService.findFundVlbyRange(fundDesc,
					LocalDate.parse(today.minusYears(1).getYear() + "-01-01"),
					LocalDate.parse(today.minusYears(1).getYear() + "-12-31"));

			if (fundVls.size() != 0) {

				double d = fundVls.get(0).getDay().until(fundVls.get(fundVls.size() - 1).getDay(), ChronoUnit.DAYS);

				if (d >= 175) {

					profit = (fundVls.get(fundVls.size() - 1).getVl() - fundVls.get(0).getVl())
							/ fundVls.get(0).getVl();
					bar_chart_dataset.addValue(profit, fundDesc.getfName(), "Último año fiscal");
				} else {
					desc += "\nNo existen suficentes datos para calcular la rentabilidad del último año fiscal ("
							+ today.minusYears(1).getYear() + "). ";
				}

			} else {
				desc += "\nNo existen datos del último año fiscal (" + today.minusYears(1).getYear() + "). ";

			}

			// Semestre

			fundVls = fundService.findFundVlbyRange(fundDesc, today.minusMonths(6), today);

			if (fundVls.size() != 0) {

				double d = fundVls.get(0).getDay().until(fundVls.get(fundVls.size() - 1).getDay(), ChronoUnit.DAYS);

				if (d >= 90) {
					profit = (fundVls.get(fundVls.size() - 1).getVl() - fundVls.get(0).getVl())
							/ fundVls.get(0).getVl();
					bar_chart_dataset.addValue(profit, fundDesc.getfName(), "Último semestre");
				} else {
					desc += "\nNo existen suficentes datos para calcular la rentabilidad del último semestre. ";
				}

			} else {
				desc += "\nNo existen datos del último semestre. ";

			}
			// Trimestre

			fundVls = fundService.findFundVlbyRange(fundDesc, today.minusMonths(3), today);

			if (fundVls.size() != 0) {

				double d = fundVls.get(0).getDay().until(fundVls.get(fundVls.size() - 1).getDay(), ChronoUnit.DAYS);

				if (d >= 45) {
					profit = (fundVls.get(fundVls.size() - 1).getVl() - fundVls.get(0).getVl())
							/ fundVls.get(0).getVl();
					bar_chart_dataset.addValue(profit, fundDesc.getfName(), "Último trimestre");
				} else {
					desc += "\nNo existen suficentes datos para calcular la rentabilidad del último trimestre. ";
				}
			} else {
				desc += "\nNo existen datos del último trimestre. ";

			}

			// Mes

			fundVls = fundService.findFundVlbyRange(fundDesc, today.minusMonths(1), today);

			if (fundVls.size() != 0) {

				double d = fundVls.get(0).getDay().until(fundVls.get(fundVls.size() - 1).getDay(), ChronoUnit.DAYS);

				if (d >= 15) {
					profit = (fundVls.get(fundVls.size() - 1).getVl() - fundVls.get(0).getVl())
							/ fundVls.get(0).getVl();
					bar_chart_dataset.addValue(profit, fundDesc.getfName(), "Último mes");
				} else {
					desc += "\nNo existen suficentes datos para calcular la rentabilidad del último mes. ";
				}
			} else {
				desc += "\nNo existen datos del último mes. ";

			}
		}

		JFreeChart barChart = ChartFactory.createBarChart("Rentabilidades", "Fondo", "Rentabilidad", bar_chart_dataset,
				PlotOrientation.VERTICAL, true, true, false);

		CategoryPlot cplot = (CategoryPlot) barChart.getPlot();
		cplot.setBackgroundPaint(Color.DARK_GRAY);
		cplot.setOutlinePaint(Color.BLUE);

		((BarRenderer) cplot.getRenderer()).setBarPainter(new StandardBarPainter());

		BarRenderer r = (BarRenderer) barChart.getCategoryPlot().getRenderer();
		r.setSeriesPaint(0, Color.BLUE);

		ChartPanel cP = new ChartPanel(barChart);

		return new Chart(cP, desc);

	}

	/**
	 * Crea una gráfica de barras que muestra los cinco fondos más y menos
	 * rentables de la cartera en el día de hoy. 7
	 * 
	 * @param
	 */
	public Chart createPortfolioMostProfitableFundsBarChart(FundService fundService, FundPort fundPort)
			throws InstanceNotFoundException {

		List<FundDesc> fundDescs = null;

		String desc = "";

		fundDescs = fundService.getProfitOfFundsOfPortfolio(fundPort, LocalDate.now());

		DefaultCategoryDataset bar_chart_dataset = new DefaultCategoryDataset();

		for (int x = 0; x < fundDescs.size() && x < 5; x++) {

			if (fundDescs.get(x).getProfit() > 0) {
				bar_chart_dataset.addValue(fundDescs.get(x).getProfit(), fundDescs.get(x).getfName(),
						Integer.toString(x));
			}

		}

		for (int x = fundDescs.size() - 1; x >= 0 && x >= fundDescs.size() - 5; x--) {
			if (fundDescs.get(x).getProfit() < 0) {

				bar_chart_dataset.addValue(fundDescs.get(x).getProfit(), fundDescs.get(x).getfName(),
						Integer.toString(x));
			}

		}

		JFreeChart barChart = ChartFactory.createStackedBarChart("Rentabilidades", "Fondos", "Rentabilidad",
				bar_chart_dataset, PlotOrientation.VERTICAL, true, true, false);

		CategoryPlot cplot = (CategoryPlot) barChart.getPlot();
		cplot.setBackgroundPaint(Color.DARK_GRAY);
		cplot.setOutlinePaint(Color.BLUE);
		cplot.getDomainAxis().setTickLabelsVisible(false);

		((BarRenderer) cplot.getRenderer()).setBarPainter(new StandardBarPainter());

		BarRenderer r = (BarRenderer) barChart.getCategoryPlot().getRenderer();
		r.setSeriesPaint(0, Color.CYAN);

		ChartPanel cP = new ChartPanel(barChart);

		if (fundDescs.size() == 0) {
			desc = ("La cartera seleccionada: " + fundPort.getpName() + " no tiene ningún fondo asignado.");
		} else {

			desc = ("Gráfica de los cinco fondos más y menos rentables de la cartera: " + fundPort.getpName());

		}

		return new Chart(cP, desc);

	}

	/**
	 * Crea una gráfica de barras que muestra una comparativa entre el valor de
	 * los diferentes fondos de la cartera el día de la última operación y el
	 * día actual. 8
	 * 
	 * @param
	 */
	public Chart createPortfolioFundsValueBarChart(FundService fundService, FundPort fundPort)
			throws InstanceNotFoundException {

		List<FundDesc> fundDescs = null;

		String desc = "";

		fundDescs = fundService.findFundsOfPortfolio(fundPort);

		DefaultCategoryDataset bar_chart_dataset = new DefaultCategoryDataset();

		for (int x = 0; x < fundDescs.size(); x++) {

			try {

				PortOp portOp = fundService.findLatestPortOp(fundPort, fundDescs.get(x), LocalDate.now());

				FundVl inicialFundVl = fundService.findLatestFundVl(fundDescs.get(x), portOp.getDay());

				FundVl actualFundVl = fundService.findLatestFundVl(fundDescs.get(x), LocalDate.now());

				if (actualFundVl != null && inicialFundVl != null) {

					double inicial = ((portOp.getfPartfin() * inicialFundVl.getVl()));

					bar_chart_dataset.addValue(inicial, fundDescs.get(x).getfName(), "Valor en la última inversión");

					double actual = ((portOp.getfPartfin() * actualFundVl.getVl()));

					bar_chart_dataset.addValue(actual, fundDescs.get(x).getfName(), "Valor Actual");

				}

			} catch (InstanceNotFoundException e) {
				continue;

			}

		}

		JFreeChart barChart = ChartFactory.createBarChart("Comparativa de inversión", "Fondo", "Valor",
				bar_chart_dataset, PlotOrientation.VERTICAL, true, true, false);

		CategoryPlot cplot = (CategoryPlot) barChart.getPlot();
		cplot.setBackgroundPaint(Color.DARK_GRAY);
		cplot.setOutlinePaint(Color.BLUE);

		((BarRenderer) cplot.getRenderer()).setBarPainter(new StandardBarPainter());

		ChartPanel cP = new ChartPanel(barChart);

		if (fundDescs.size() == 0) {
			desc = ("La cartera seleccionada: " + fundPort.getpName() + " no tiene ningún fondo asignado.");
		} else {

			desc = ("Gráfica que muestra una comparativa entre el valor de los diferentes fondos de la cartera: "
					+ fundPort.getpName() + " el día de la última operación y el día actual. ");

		}

		return new Chart(cP, desc);

	}

	@Override
	protected Chart doInBackground() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
