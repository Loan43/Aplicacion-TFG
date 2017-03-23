package tfg.app.model.entities;

import java.io.Serializable;
import java.time.LocalDate;

@SuppressWarnings("serial")
public class PortOpPK implements Serializable {

	private PortDesc portDesc;
	protected LocalDate day;

	public PortOpPK() {
	}

	public PortOpPK(PortDesc portDesc, LocalDate day) {
		this.portDesc = portDesc;
		this.day = day;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((day == null) ? 0 : day.hashCode());
		result = prime * result + ((portDesc == null) ? 0 : portDesc.hashCode());
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
		PortOpPK other = (PortOpPK) obj;
		if (day == null) {
			if (other.day != null)
				return false;
		} else if (!day.equals(other.day))
			return false;
		if (portDesc == null) {
			if (other.portDesc != null)
				return false;
		} else if (!portDesc.equals(other.portDesc))
			return false;
		return true;
	}

}
