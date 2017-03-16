package tfg.app.model.entities;

import java.io.Serializable;
import java.time.LocalDate;

@SuppressWarnings("serial")
public class FundVlPK implements Serializable {

	private FundDesc fundDesc;
	protected LocalDate day;

	public FundVlPK() {
	}

	public FundVlPK(FundDesc fundDesc, LocalDate c1) {
		this.fundDesc = fundDesc;
		this.day = c1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((day == null) ? 0 : day.hashCode());
		result = prime * result + ((fundDesc == null) ? 0 : fundDesc.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FundVlPK other = (FundVlPK) obj;
		if (day == null) {
			if (other.day != null)
				return false;
		} else if (!day.equals(other.day))
			return false;
		if (fundDesc == null) {
			if (other.fundDesc != null)
				return false;
		} else if (!fundDesc.equals(other.fundDesc))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FundVlPK [fundDesc=" + fundDesc + ", day=" + day + "]";
	}

}
