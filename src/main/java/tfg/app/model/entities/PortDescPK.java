package tfg.app.model.entities;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PortDescPK implements Serializable {

	private FundDesc fundDescId;
	protected FundPort fundPortId;

	public PortDescPK() {
	}

	public PortDescPK(FundDesc fundDescId, FundPort fundPortId) {
		this.fundDescId = fundDescId;
		this.fundPortId = fundPortId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fundDescId == null) ? 0 : fundDescId.hashCode());
		result = prime * result + ((fundPortId == null) ? 0 : fundPortId.hashCode());
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
		PortDescPK other = (PortDescPK) obj;
		if (fundDescId == null) {
			if (other.fundDescId != null)
				return false;
		} else if (!fundDescId.equals(other.fundDescId))
			return false;
		if (fundPortId == null) {
			if (other.fundPortId != null)
				return false;
		} else if (!fundPortId.equals(other.fundPortId))
			return false;
		return true;
	}

}
