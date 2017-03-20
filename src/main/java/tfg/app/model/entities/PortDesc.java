package tfg.app.model.entities;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@IdClass(PortDescPK.class)
@Table(name = "portdesc")
public class PortDesc {

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pid", nullable = false)
	private FundPort fundPortId;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fid", nullable = false)
	private FundDesc fundDescId;

	public PortDesc() {
	};

	public PortDesc(FundPort fundPortId, FundDesc fundDescId) {
		super();
		this.fundDescId = fundDescId;
		this.fundPortId = fundPortId;
	}

	public FundDesc getFundDesc() {
		return fundDescId;
	}

	public void setFundDesc(FundDesc fundDesc) {
		this.fundDescId = fundDesc;
	}

	public FundPort getFundPort() {
		return fundPortId;
	}

	public void setFundPort(FundPort fundPort) {
		this.fundPortId = fundPort;
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
		PortDesc other = (PortDesc) obj;
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
