package tfg.app.controller.workers;

import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import tfg.app.model.entities.FundDesc;
import tfg.app.model.entities.FundPort;
import tfg.app.model.service.FundService;
import tfg.app.util.exceptions.InstanceNotFoundException;

public class NodesWorker extends SwingWorker<Void, Integer> {

	private FundService fundService;
	private javax.swing.JTree tree;
	private DefaultMutableTreeNode top;
	private javax.swing.JTree tree2;
	private DefaultMutableTreeNode top2;
	private JLabel label;

	public NodesWorker(FundService fundService, javax.swing.JTree tree, JTree tree2, DefaultMutableTreeNode top,
			DefaultMutableTreeNode top2, JLabel label) {
		super();
		this.fundService = fundService;
		this.tree = tree;
		this.tree2 = tree2;
		this.top2 = top2;
		this.top = top;
		this.label = label;
	}

	private TreePath find(DefaultMutableTreeNode root, String s) {

		@SuppressWarnings("unchecked")
		Enumeration<DefaultMutableTreeNode> e = root.depthFirstEnumeration();

		while (e.hasMoreElements()) {
			DefaultMutableTreeNode node = e.nextElement();
			if (node.toString().equalsIgnoreCase(s)) {
				return new TreePath(node.getPath());
			}
		}
		return null;
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
			setProgress(progress.intValue() / 2);

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

		DefaultMutableTreeNode fund = null;
		top2.removeAllChildren();

		List<FundDesc> funds = fundService.findFundsByKeywords("");

		for (int x = 0; x < funds.size(); x++) {
			fund = new DefaultMutableTreeNode(funds.get(x));
			top2.add(fund);

			progress = ((double) (x + 1) / funds.size()) * 100;
			setProgress((progress.intValue() / 2) + 50);
		}

		setProgress(100);
		return null;
	}

	@Override
	protected void done() {

		try {

			get();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
			DefaultTreeModel treeModel2 = (DefaultTreeModel) tree2.getModel();

			treeModel.reload(top);
			treeModel2.reload(top2);

			label.setText("Modelo Actualizado");

			TreePath treePath = find((DefaultMutableTreeNode) treeModel.getRoot(), node.toString());

			if (treePath != null) {

				tree.setSelectionPath(treePath);
				tree.scrollPathToVisible(treePath);

			} else {

				node = (DefaultMutableTreeNode) node.getParent();

				if (node != null) {

					treePath = find((DefaultMutableTreeNode) treeModel.getRoot(), node.toString());
					if (treePath != null) {
						tree.setSelectionPath(treePath);
						tree.scrollPathToVisible(treePath);
					}

				} else {

					node = (DefaultMutableTreeNode) treeModel.getRoot();
					treePath = new TreePath(node.getPath());
					tree.setSelectionPath(treePath);
					tree.scrollPathToVisible(treePath);

				}

			}

			label.setText("Modelo Actualizado");

		} catch (InterruptedException | CancellationException | java.lang.NullPointerException e) {

		} catch (ExecutionException e) {

		}

	}

}
