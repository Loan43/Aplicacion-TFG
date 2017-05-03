package tfg.app.model.entities;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *  Clase que modela una cartera de fondos de inversión usando hibernate.
 *
 * 
 */
@Entity
@Table(name = "fundport")
public class FundPort {

	@Id
	@Column(name = "pid")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long pId;

	@Column(name = "pname", unique = true, nullable = false, length = 40)
	private String pName;

	@Column(name = "pdesc", length = 200)
	private String pDesc;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fundPortId", cascade = CascadeType.REMOVE)
	private List<PortDesc> portDescs = new ArrayList<PortDesc>();

	public FundPort() {
	};

	public FundPort(String pName, String pDesc) {
		super();
		this.pName = pName;
		this.pDesc = pDesc;
	}

	public FundPort(String pName, String pDesc, List<PortDesc> fundDescs) {
		super();
		this.pName = pName;
		this.pDesc = pDesc;
		this.portDescs = fundDescs;
	}

	public Long getpId() {
		return pId;
	}

	public void setpId(Long pId) {
		this.pId = pId;
	}

	public String getpName() {
		return pName;
	}

	public void setpName(String pName) {
		this.pName = pName;
	}

	public String getpDesc() {
		return pDesc;
	}

	public void setpDesc(String pDesc) {
		this.pDesc = pDesc;
	}

	public List<PortDesc> getPortDescs() {
		return portDescs;
	}

	public void setPortDescs(List<PortDesc> fundDescs) {
		this.portDescs = fundDescs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pDesc == null) ? 0 : pDesc.hashCode());
		result = prime * result + ((pId == null) ? 0 : pId.hashCode());
		result = prime * result + ((pName == null) ? 0 : pName.hashCode());
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
		FundPort other = (FundPort) obj;
		if (pDesc == null) {
			if (other.pDesc != null)
				return false;
		} else if (!pDesc.equals(other.pDesc))
			return false;
		if (pId == null) {
			if (other.pId != null)
				return false;
		} else if (!pId.equals(other.pId))
			return false;
		if (pName == null) {
			if (other.pName != null)
				return false;
		} else if (!pName.equals(other.pName))
			return false;
		if (portDescs == null) {
			if (other.portDescs != null)
				return false;
		} else if (!(portDescs.size() == other.portDescs.size()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return pName;
	}

}
