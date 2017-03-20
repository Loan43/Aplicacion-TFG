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

@Entity
@Table(name = "fundesc")
public class FundDesc {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "fId", unique = true, nullable = false)
	private String fId;

	@Column(name = "fgest")
	private String fGest;

	@Column(name = "ftype")
	private String fType;

	@Column(name = "fcategory")
	private String fCategory;

	@Column(name = "fcurrency")
	private String fCurrency;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "fundDesc", cascade = CascadeType.REMOVE)
	private List<FundVl> fundVls = new ArrayList<FundVl>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fundDescId", cascade = CascadeType.REMOVE)
	private List<PortDesc> portDescs = new ArrayList<PortDesc>();

	public FundDesc() {
	};

	public FundDesc(String fId, String fGest, String fType, String fCategory, String fCurrency) {
		super();
		this.fId = fId;
		this.fGest = fGest;
		this.fType = fType;
		this.fCategory = fCategory;
		this.fCurrency = fCurrency;
	}

	public FundDesc(String fId, String fGest, String fType, String fCategory, String fCurrency, List<FundVl> fundVls) {
		super();
		this.fId = fId;
		this.fGest = fGest;
		this.fType = fType;
		this.fCategory = fCategory;
		this.fCurrency = fCurrency;
		this.fundVls = fundVls;
	}

	public FundDesc(List<PortDesc> portfolios, String fId, String fGest, String fType, String fCategory,
			String fCurrency, List<FundVl> fundVls) {
		super();
		this.fId = fId;
		this.fGest = fGest;
		this.fType = fType;
		this.fCategory = fCategory;
		this.fCurrency = fCurrency;
		this.fundVls = fundVls;
		this.portDescs = portfolios;
	}

	public FundDesc(List<PortDesc> portfolios, String fId, String fGest, String fType, String fCategory,
			String fCurrency) {
		super();
		this.fId = fId;
		this.fGest = fGest;
		this.fType = fType;
		this.fCategory = fCategory;
		this.fCurrency = fCurrency;
		this.portDescs = portfolios;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getfId() {
		return fId;
	}

	public void setfId(String fId) {
		this.fId = fId;
	}

	public String getfGest() {
		return fGest;
	}

	public void setfGest(String fGest) {
		this.fGest = fGest;
	}

	public String getfType() {
		return fType;
	}

	public void setfType(String fType) {
		this.fType = fType;
	}

	public String getfCategory() {
		return fCategory;
	}

	public void setfCategory(String fCategory) {
		this.fCategory = fCategory;
	}

	public String getfCurrency() {
		return fCurrency;
	}

	public void setfCurrency(String fCurrency) {
		this.fCurrency = fCurrency;
	}

	public List<FundVl> getFundVls() {
		return fundVls;
	}

	public void setFundvls(List<FundVl> fundVls) {
		this.fundVls = fundVls;
	}

	public List<PortDesc> getPortfolios() {
		return portDescs;
	}

	public void setPortfolios(List<PortDesc> portfolios) {
		this.portDescs = portfolios;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fCategory == null) ? 0 : fCategory.hashCode());
		result = prime * result + ((fCurrency == null) ? 0 : fCurrency.hashCode());
		result = prime * result + ((fGest == null) ? 0 : fGest.hashCode());
		result = prime * result + ((fId == null) ? 0 : fId.hashCode());
		result = prime * result + ((fType == null) ? 0 : fType.hashCode());
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
		FundDesc other = (FundDesc) obj;
		if (fCategory == null) {
			if (other.fCategory != null)
				return false;
		} else if (!fCategory.equals(other.fCategory))
			return false;
		if (fCurrency == null) {
			if (other.fCurrency != null)
				return false;
		} else if (!fCurrency.equals(other.fCurrency))
			return false;
		if (fGest == null) {
			if (other.fGest != null)
				return false;
		} else if (!fGest.equals(other.fGest))
			return false;
		if (fId == null) {
			if (other.fId != null)
				return false;
		} else if (!fId.equals(other.fId))
			return false;
		if (fType == null) {
			if (other.fType != null)
				return false;
		} else if (!fType.equals(other.fType))
			return false;
		if (fundVls == null) {
			if (other.fundVls != null)
				return false;
		} else if (fundVls.size() != other.fundVls.size())
			return false;
		for (int x = 0; x < fundVls.size(); x++) {
			if (!fundVls.get(x).equals(other.fundVls.get(x))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "FundDesc [id=" + id + ", fId=" + fId + ", fGest=" + fGest + ", fType=" + fType + ", fCategory="
				+ fCategory + ", fCurrency=" + fCurrency + "]";
	}

}