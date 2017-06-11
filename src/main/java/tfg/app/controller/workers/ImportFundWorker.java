package tfg.app.controller.workers;

import java.io.File;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import tfg.app.model.entities.FundDesc;
import tfg.app.model.entities.FundVl;
import tfg.app.model.service.FundService;
import tfg.app.util.exceptions.InputValidationException;
import tfg.app.util.exceptions.InstanceNotFoundException;

public class ImportFundWorker extends SwingWorker<Void, Void> {

	private FundService fundService;
	private JLabel label;
	private JProgressBar progress;
	private NodesWorker createNodes;
	private File file;
	private FundDesc fundDesc;
	private int selectImportVl = 0;
	private String parser;

	public ImportFundWorker(FundService fundService, JLabel label, JProgressBar progress, NodesWorker createNodes,
			File file) {
		super();
		this.fundService = fundService;
		this.label = label;
		this.progress = progress;
		this.createNodes = createNodes;
		this.file = file;
	}

	public void setImportFund() {

		selectImportVl = 0;
	}

	public void setImportVls(FundDesc fundDesc, String parser) {

		selectImportVl = 1;
		this.fundDesc = fundDesc;
		this.parser = parser;
	}

	@Override
	protected Void doInBackground() throws InputValidationException, InstanceNotFoundException {

		switch (selectImportVl) {
		case 0:
			FundDesc fundDesc = fundService.importFundDescFromExcel(file);
			fundService.addFund(fundDesc);
			break;
		case 1:

			Double progress;
			List<FundVl> fundVls = fundService.importVlsFromExcel(file, this.fundDesc, 0, this.parser);

			for (int x = 0; x < fundVls.size(); x++) {

				try {

					fundService.addFundVl(fundVls.get(x));

				} catch (InputValidationException e) {

					fundService.updateFundVl(fundVls.get(x));

				}

				progress = ((double) (x + 1) / fundVls.size()) * 100;
				setProgress(progress.intValue());

			}

			break;
		}

		return null;

	}

	@Override
	protected void done() {

		try {

			get();
			createNodes.execute();

		} catch (InterruptedException | CancellationException e) {

		} catch (ExecutionException e) {

			if (e.getCause().getClass() == InputValidationException.class) {

				JOptionPane ventanaError = new javax.swing.JOptionPane();
				JOptionPane.showMessageDialog(ventanaError, e.getCause().getMessage(), "Error al importar",
						JOptionPane.ERROR_MESSAGE);

			}

			label.setText("Modelo Actualizado");
			progress.setIndeterminate(false);
			progress.setValue(100);

		}

	}

}
