package tfg.app.model.entities;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@IdClass(PortOpPK.class)
@Table(name = "portop")
public class PortOp {

	@Id
	@Column(name = "day")
	private LocalDate day;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	private PortDesc portDesc;

	@Column(name = "fpartini")
	private Integer fPartini;

	@Column(name = "fpartfin")
	private Integer fPartfin;

	@Transient
	private Integer fPartOp;

	@Transient
	private Double fPrice;

	public PortOp() {
	}

	public PortOp(LocalDate day, PortDesc portDesc, Integer fPartfin) {
		super();
		this.day = day;
		this.portDesc = portDesc;
		this.fPartfin = fPartfin;
	}

	public LocalDate getDay() {
		return day;
	}

	public void setDay(LocalDate day) {
		this.day = day;
	}

	public PortDesc getPortDesc() {
		return portDesc;
	}

	public void setPortDesc(PortDesc portDesc) {
		this.portDesc = portDesc;
	}

	public Integer getfPartini() {
		return fPartini;
	}

	public void setfPartini(Integer fPartini) {
		this.fPartini = fPartini;
	}

	public Integer getfPartfin() {
		return fPartfin;
	}

	public void setfPartfin(Integer fPartfin) {
		this.fPartfin = fPartfin;
	}

	public Integer getfPartOp() {
		return fPartOp;
	}

	public void setfPartOp(Integer fPartOp) {
		this.fPartOp = fPartOp;
	}

	public Double getfPrice() {
		return fPrice;
	}

	public void setfPrice(Double fPrice) {
		this.fPrice = fPrice;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((day == null) ? 0 : day.hashCode());
		result = prime * result + ((fPartOp == null) ? 0 : fPartOp.hashCode());
		result = prime * result + ((fPartfin == null) ? 0 : fPartfin.hashCode());
		result = prime * result + ((fPartini == null) ? 0 : fPartini.hashCode());
		result = prime * result + ((fPrice == null) ? 0 : fPrice.hashCode());
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
		PortOp other = (PortOp) obj;
		if (day == null) {
			if (other.day != null)
				return false;
		} else if (!day.equals(other.day))
			return false;
		if (fPartOp == null) {
			if (other.fPartOp != null)
				return false;
		} else if (!fPartOp.equals(other.fPartOp))
			return false;
		if (fPartfin == null) {
			if (other.fPartfin != null)
				return false;
		} else if (!fPartfin.equals(other.fPartfin))
			return false;
		if (fPartini == null) {
			if (other.fPartini != null)
				return false;
		} else if (!fPartini.equals(other.fPartini))
			return false;
		if (fPrice == null) {
			if (other.fPrice != null)
				return false;
		} else if (!fPrice.equals(other.fPrice))
			return false;
		return true;
	}

}
