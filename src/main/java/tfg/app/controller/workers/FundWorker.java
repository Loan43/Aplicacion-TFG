package tfg.app.controller.workers;

import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.JLabel;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import tfg.app.model.entities.FundDesc;
import tfg.app.model.service.FundService;

public class FundWorker extends SwingWorker<Void, Integer> {

	private FundService fundService;
	private javax.swing.JTree tree;
	private DefaultMutableTreeNode top;
	private JLabel label;
	private String keywords;

	public FundWorker(FundService fundService, javax.swing.JTree tree, DefaultMutableTreeNode top, JLabel label,
			String keywords) {
		super();
		this.fundService = fundService;
		this.tree = tree;
		this.top = top;
		this.label = label;
		this.keywords = keywords;
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

		DefaultMutableTreeNode fund = null;
		Double progress = null;
		top.removeAllChildren();

		List<FundDesc> funds = fundService.findFundsByKeywords(keywords);

		for (int x = 0; x < funds.size(); x++) {
			fund = new DefaultMutableTreeNode(funds.get(x));
			top.add(fund);

			progress = ((double) (x + 1) / funds.size()) * 100;
			setProgress(progress.intValue());
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

			treeModel.reload(top);

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
