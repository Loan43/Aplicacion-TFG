package tfg.app.util.comparator;

import java.util.Comparator;

import tfg.app.model.entities.FundVl;

public class compVl implements Comparator<FundVl> {

	public int compare(FundVl a, FundVl b) {
		if (a.getVl() > b.getVl())
			return 1; // highest value first
		if (a.getVl() == b.getVl())
			return 0;
		return -1;
	}
}