package tfg.app.util.comparator;

import java.util.Comparator;

import tfg.app.model.entities.FundDesc;

public class compFundProfit implements Comparator<FundDesc> {

	public int compare(FundDesc a, FundDesc b) {
		if (a.getProfit() > b.getProfit())
			return -1; // highest value first
		if (a.getProfit() == b.getProfit())
			return 0;
		return 1;
	}
}