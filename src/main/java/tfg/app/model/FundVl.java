package tfg.app.model;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@IdClass(FundVlPK.class)
@Table(name = "fundvl")
public class FundVl implements Serializable {

	@Id
	@Column(name = "day")
	private LocalDate day;

	@Column(name = "vl")
	private Double vl;

	@Id
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id", nullable = false)
	private FundDesc fundDesc;

	public FundVl(LocalDate c1, Double vl) {
		this.day = c1;
		this.vl = vl;
	}

	public FundVl(LocalDate c1, Double vl, FundDesc fundDesc) {
		this.day = c1;
		this.vl = vl;
		this.fundDesc = fundDesc;
	}

	public FundVl() {
	}

	public LocalDate getDay() {
		return day;
	}

	public void setDay(LocalDate day) {
		this.day = day;
	}

	public Double getVl() {
		return vl;
	}

	public void setVl(Double vl) {
		this.vl = vl;
	}

	public FundDesc getFundDesc() {
		return fundDesc;
	}

	public void setFundDesc(FundDesc fundDesc) {
		this.fundDesc = fundDesc;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((day == null) ? 0 : day.hashCode());
		result = prime * result + ((fundDesc == null) ? 0 : fundDesc.hashCode());
		result = prime * result + ((vl == null) ? 0 : vl.hashCode());
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
		FundVl other = (FundVl) obj;
		if (day == null) {
			if (other.day != null)
				return false;
		} else if (!day.equals(other.day)) {
			return false;
		}
		if (fundDesc == null) {
			if (other.fundDesc != null)
				return false;
		} else if (fundDesc.getId() != other.fundDesc.getId())
			return false;
		if (vl == null) {
			if (other.vl != null)
				return false;
		} else if (!vl.equals(other.vl))
			return false;
		return true;
	}

}