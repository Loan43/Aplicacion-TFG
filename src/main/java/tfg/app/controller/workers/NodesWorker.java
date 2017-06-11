package tfg.app.controller.workers;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.JLabel;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import tfg.app.model.entities.FundDesc;
import tfg.app.model.entities.FundPort;
import tfg.app.model.service.FundService;
import tfg.app.util.exceptions.InstanceNotFoundException;

public class NodesWorker extends SwingWorker<Void, Integer> {

	private FundService fundService;
	private javax.swing.JTree tree;
	private DefaultMutableTreeNode top;
	private JLabel label;

	public NodesWorker(FundService fundService, javax.swing.JTree tree, DefaultMutableTreeNode top, JLabel label) {
		super();
		this.fundService = fundService;
		this.tree = tree;
		this.top = top;
		this.label = label;
	}

	@Override
	protected Void doInBackground() throws Exception {

		DefaultMutableTreeNode found = null;
		DefaultMutableTreeNode porfolio = null;
		Double progress = null;

		List<FundPort> fundPorts = fundService.findAllFundPortfolios();

		top.removeAllChildren();

		for (int x = 0; x < fundPorts.size(); x++) {
			porfolio = new DefaultMutableTreeNode(fundPorts.get(x));
			top.add(porfolio);

			progress = ((double) (x + 1) / fundPorts.size()) * 100;
			setProgress(progress.intValue());

			try {
				List<FundDesc> fundDescs = fundService.findFundsOfPortfolio(fundPorts.get(x));

				for (int x1 = 0; x1 < fundDescs.size(); x1++) {
					found = new DefaultMutableTreeNode(fundDescs.get(x1));
					porfolio.add(found);
				}
			} catch (InstanceNotFoundException e) {
				// ¡
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	protected void done() {

		try {

			get();
			DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
			treeModel.reload(top);

			for (int i = 0; i < tree.getRowCount(); i++) {
				tree.expandRow(i);
			}
			label.setText("Modelo Actualizado");

		} catch (InterruptedException | CancellationException | java.lang.NullPointerException e) {

		} catch (ExecutionException e) {

		}

	}

}
