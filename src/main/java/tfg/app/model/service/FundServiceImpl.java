package tfg.app.model.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import tfg.app.util.validator.PropertyValidator;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;

import tfg.app.model.entities.FundDesc;
import tfg.app.model.entities.FundPort;
import tfg.app.model.entities.FundVl;
import tfg.app.model.entities.FundVlPK;
import tfg.app.model.entities.PortDesc;
import tfg.app.model.entities.PortDescPK;
import tfg.app.model.entities.PortOp;
import tfg.app.model.entities.PortOpPK;
import tfg.app.util.exceptions.InputValidationException;
import tfg.app.util.exceptions.InstanceNotFoundException;

public class FundServiceImpl implements FundService {

	private SessionFactory sessionFactory;
	private Transaction tx;

	public FundServiceImpl() {
		sessionFactory = new Configuration().configure().buildSessionFactory();

	}

	private void validateFund(FundDesc fundDesc) throws InputValidationException {

		PropertyValidator.validateIsin(fundDesc.getfId());
		for (int x = 0; x < fundDesc.getFundVls().size(); x++) {
			validateFundVl(fundDesc.getFundVls().get(x));
		}
	}

	private void validateFundVl(FundVl fundVl) throws InputValidationException {

		PropertyValidator.validateNotNegativeDouble(fundVl.getVl());

	}

	private void validatePortOp(PortOp portOp) throws InputValidationException {

		PropertyValidator.validateNotZeroInt(portOp.getfPartOp());
		calculatePortOp(portOp);

	}

	private void validateFundPort(FundPort fundPortfolio) throws InputValidationException {

		PropertyValidator.validateMandatoryString("Nombre de la cartera de fondos", fundPortfolio.getpName());

	}

	public void addFund(FundDesc fundDesc) throws InputValidationException {

		validateFund(fundDesc);

		try {
			Session session = sessionFactory.openSession();
			try {
				tx = session.beginTransaction();
				session.save(fundDesc);
				fundDesc.getFundVls().forEach((temp) -> {
					session.save(temp);
				});
				tx.commit();
			} catch (ConstraintViolationException e) {
				tx.rollback();
				throw new InputValidationException(
						"Error, el Isin del fondo: " + fundDesc.getfId() + " ya existe en la base de datos.");
			} catch (HibernateException | Error e) {
				tx.rollback();
				throw e;
			} finally {
				session.close();
			}
		} catch (HibernateException e) {
			throw new RuntimeException(e);
		}
	}

	public void updateFund(FundDesc fundDesc) throws InputValidationException {

		validateFund(fundDesc);

		try {
			Session session = sessionFactory.openSession();
			try {
				tx = session.beginTransaction();
				session.update(fundDesc);
				tx.commit();
			} catch (javax.persistence.PersistenceException e) {
				tx.rollback();
				throw new InputValidationException("Error, se está intentando modificar un id a otro que ya existe.");
			} catch (RuntimeException | Error e) {
				tx.rollback();
				throw e;
			} finally {
				session.close();
			}
		} catch (HibernateException e) {
			throw new RuntimeException(e);
		}
	}

	public void removeFund(FundDesc fundDesc) throws InstanceNotFoundException {

		try {
			Session session = sessionFactory.openSession();
			try {
				tx = session.beginTransaction();
				session.delete((FundDesc) session.get(FundDesc.class, fundDesc.getId()));
				tx.commit();
			} catch (java.lang.NullPointerException | java.lang.IllegalArgumentException e) {
				tx.rollback();
				throw new InstanceNotFoundException(fundDesc.getfId(), "fundDesc");
			} catch (HibernateException | Error e) {
				tx.rollback();
				throw e;
			} finally {
				session.close();
			}
		} catch (HibernateException e) {
			throw new RuntimeException(e);
		}

	}

	public FundDesc findFund(String fundId) throws InstanceNotFoundException {

		try {
			Session session = sessionFactory.openSession();
			try {
				tx = session.beginTransaction();
				String hql = "from FundDesc where fId like ?1";
				Query<?> query = session.createQuery(hql);
				query.setParameter(1, new String(fundId));
				query.setMaxResults(1);
				FundDesc fundDesc = (FundDesc) query.uniqueResult();
				tx.commit();
				if (fundDesc == null)
					throw new InstanceNotFoundException(fundId, "FundDesc");
				return fundDesc;
			} catch (ConstraintViolationException e) {
				tx.rollback();
				throw new RuntimeException(e);
			} catch (HibernateException | Error e) {
				tx.rollback();
				throw e;
			} finally {
				session.close();
			}
		} catch (HibernateException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public FundVl findFundVl(FundDesc fundDesc, LocalDate day) throws InstanceNotFoundException {

		try {
			Session session = sessionFactory.openSession();
			try {
				tx = session.beginTransaction();
				FundVl fundVl = (FundVl) session.get(FundVl.class, new FundVlPK(fundDesc, day));
				tx.commit();
				if (fundVl == null)
					throw new InstanceNotFoundException(day, "FundVl");
				return fundVl;
			} catch (ConstraintViolationException e) {
				tx.rollback();
				throw new RuntimeException(e);
			} catch (HibernateException | Error e) {
				tx.rollback();
				throw e;
			} finally {
				session.close();
			}
		} catch (HibernateException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FundDesc> findAllFunds() {

		try {
			Session session = sessionFactory.openSession();
			try {
				tx = session.beginTransaction();
				String hql = "from FundDesc";
				Query<?> query = session.createQuery(hql);
				List<?> fundDescList = query.list();
				tx.commit();
				return (List<FundDesc>) fundDescList;
			} catch (ConstraintViolationException e) {
				tx.rollback();
				throw new RuntimeException(e);
			} catch (HibernateException | Error e) {
				tx.rollback();
				throw e;
			} finally {
				session.close();
			}
		} catch (HibernateException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FundDesc> findFundsByKeywords(String keywords) {

		try {
			Session session = sessionFactory.openSession();
			try {
				tx = session.beginTransaction();
				String hql = "from FundDesc where fId like ?1 or fGest like ?1 or fType like ?1"
						+ " or fCategory like ?1 or fCurrency like ?1";
				Query<?> query = session.createQuery(hql);
				query.setParameter(1, new String("%" + keywords + "%"));
				List<FundDesc> fundDescList = (List<FundDesc>) query.list();
				tx.commit();
				return (List<FundDesc>) fundDescList;
			} catch (ConstraintViolationException e) {
				tx.rollback();
				throw new RuntimeException(e);
			} catch (HibernateException | Error e) {
				tx.rollback();
				throw e;
			} finally {
				session.close();
			}
		} catch (HibernateException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FundVl> findFundVlbyRange(FundDesc fundDesc, LocalDate startDay, LocalDate endDay) {

		try {
			Session session = sessionFactory.openSession();
			try {
				// (fGest like ?1) or (fType like ?1) or
				tx = session.beginTransaction();
				String hql = "from FundVl as vl where vl.fundDesc.id = ?1 and day BETWEEN ?2 and ?3";
				Query<?> query = session.createQuery(hql);
				query.setParameter(1, fundDesc.getId());
				query.setParameter(2, startDay);
				query.setParameter(3, endDay);
				List<FundVl> fundVlList = (List<FundVl>) query.list();
				tx.commit();
				return (List<FundVl>) fundVlList;

			} catch (ConstraintViolationException e) {
				tx.rollback();
				throw new RuntimeException(e);
			} catch (HibernateException | Error e) {
				tx.rollback();
				throw e;
			} finally {
				session.close();
			}
		} catch (HibernateException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void removeFundVl(FundVl fundVl) throws InstanceNotFoundException {

		try {
			Session session = sessionFactory.openSession();
			try {
				tx = session.beginTransaction();
				session.delete((FundVl) session.get(FundVl.class, new FundVlPK(fundVl.getFundDesc(), fundVl.getDay())));
				tx.commit();
			} catch (java.lang.NullPointerException | java.lang.IllegalArgumentException e) {
				tx.rollback();
				throw new InstanceNotFoundException(fundVl.getFundDesc().getfId() + " dia " + fundVl.getDay(),
						"fundVl");
			} catch (HibernateException | Error e) {
				tx.rollback();
				throw e;
			} finally {
				session.close();
			}
		} catch (HibernateException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void updateFundVl(FundVl fundVl) throws InputValidationException, InstanceNotFoundException {
		validateFundVl(fundVl);

		try {
			Session session = sessionFactory.openSession();
			try {
				tx = session.beginTransaction();
				session.update(fundVl);
				tx.commit();
			} catch (javax.persistence.PersistenceException e) {
				tx.rollback();
				throw new InstanceNotFoundException(fundVl.getDay().toString(), "fundVl");
			} catch (Error e) {
				tx.rollback();
				throw e;
			} finally {
				session.close();
			}
		} catch (HibernateException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addFundVl(FundVl fundVl) throws InputValidationException {

		validateFundVl(fundVl);

		try {
			Session session = sessionFactory.openSession();
			try {
				tx = session.beginTransaction();
				session.save(fundVl);
				tx.commit();
			} catch (javax.persistence.PersistenceException e) {
				tx.rollback();
				throw new InputValidationException("Error, el dia: " + fundVl.getDay().toString() + " para el fondo "
						+ fundVl.getFundDesc().getfId() + " ya existe en la base de datos.");
			} catch (RuntimeException | Error e) {
				tx.rollback();
				throw e;
			} finally {
				session.close();
			}
		} catch (HibernateException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addFundPortfolio(FundPort fundPortfolio) throws InputValidationException {

		validateFundPort(fundPortfolio);

		try {
			Session session = sessionFactory.openSession();
			try {
				tx = session.beginTransaction();
				session.save(fundPortfolio);
				tx.commit();
			} catch (ConstraintViolationException e) {
				tx.rollback();
				throw new InputValidationException("Error, el Nombre de la cartera de fondos : "
						+ fundPortfolio.getpName() + " ya existe en la base de datos.");
			} catch (HibernateException | Error e) {
				tx.rollback();
				throw e;
			} finally {
				session.close();
			}
		} catch (HibernateException e) {
			throw new RuntimeException(e);
		}
	}

	public void removeFundPortfolio(FundPort fundPortfolio) throws InstanceNotFoundException {

		try {
			Session session = sessionFactory.openSession();
			try {
				tx = session.beginTransaction();
				session.delete((FundPort) session.get(FundPort.class, fundPortfolio.getpId()));
				tx.commit();
			} catch (java.lang.NullPointerException | java.lang.IllegalArgumentException e) {
				tx.rollback();
				throw new InstanceNotFoundException(fundPortfolio.getpId(), "fundPortfolio");
			} catch (HibernateException | Error e) {
				tx.rollback();
				throw e;
			} finally {
				session.close();
			}
		} catch (HibernateException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public FundPort findFundPortfolio(Long pId) throws InstanceNotFoundException {

		try {
			Session session = sessionFactory.openSession();
			try {
				tx = session.beginTransaction();
				FundPort fundPort = (FundPort) session.get(FundPort.class, pId);
				tx.commit();
				if (fundPort == null)
					throw new InstanceNotFoundException(pId, "FundPortfolio");
				return fundPort;
			} catch (ConstraintViolationException e) {
				tx.rollback();
				throw new RuntimeException(e);
			} catch (HibernateException | Error e) {
				tx.rollback();
				throw e;
			} finally {
				session.close();
			}
		} catch (HibernateException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void updateFundPortfolio(FundPort fundPortfolio) throws InstanceNotFoundException, InputValidationException {

		validateFundPort(fundPortfolio);

		try {
			Session session = sessionFactory.openSession();
			try {
				tx = session.beginTransaction();
				session.update(fundPortfolio);
				tx.commit();
			} catch (javax.persistence.PersistenceException e) {
				tx.rollback();
				throw new InputValidationException(
						"Error, se está intentando modificar un nombre de cartera a otro que ya existe.");
			} catch (RuntimeException | Error e) {
				tx.rollback();
				throw e;
			} finally {
				session.close();
			}
		} catch (HibernateException e) {
			throw new RuntimeException(e);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FundPort> findAllFundPortfolios() {

		try {
			Session session = sessionFactory.openSession();
			try {
				tx = session.beginTransaction();
				String hql = "from FundPort";
				Query<?> query = session.createQuery(hql);
				List<?> fundDescList = query.list();
				tx.commit();
				return (List<FundPort>) fundDescList;
			} catch (ConstraintViolationException e) {
				tx.rollback();
				throw new RuntimeException(e);
			} catch (HibernateException | Error e) {
				tx.rollback();
				throw e;
			} finally {
				session.close();
			}
		} catch (HibernateException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<FundDesc> findFundsOfPortfolio(FundPort fundPortfolio) throws InstanceNotFoundException {

		List<FundDesc> fundDescList = new ArrayList<FundDesc>();

		FundPort findFundPortfolio = findFundPortfolio(fundPortfolio.getpId());

		findFundPortfolio.getPortDescs().forEach((temp) -> {
			fundDescList.add(temp.getFundDesc());
		});

		return fundDescList;
	}

	@Override
	public void removePortDesc(FundPort fundPort, FundDesc fundDesc) throws InstanceNotFoundException {
		try {
			Session session = sessionFactory.openSession();
			try {
				tx = session.beginTransaction();
				session.delete((PortDesc) session.get(PortDesc.class, new PortDescPK(fundDesc, fundPort)));
				tx.commit();
			} catch (java.lang.NullPointerException | java.lang.IllegalArgumentException e) {
				tx.rollback();
				throw new InstanceNotFoundException(fundDesc.getfId(), "fundDesc");
			} catch (HibernateException | Error e) {
				tx.rollback();
				throw e;
			} finally {
				session.close();
			}
		} catch (HibernateException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void addPortDesc(FundPort fundPort, FundDesc fundDesc)
			throws InstanceNotFoundException, InputValidationException {
		try {
			Session session = sessionFactory.openSession();
			try {
				tx = session.beginTransaction();
				session.save(new PortDesc(fundPort, fundDesc));
				tx.commit();
			} catch (javax.persistence.PersistenceException e) {
				tx.rollback();
				throw new InputValidationException("Error, el fondo : " + fundDesc.getfId()
						+ " ya se encuentra asignado a la cartera: " + fundPort.getpName());
			} catch (Error e) {
				tx.rollback();
				throw e;
			} finally {
				session.close();
			}
		} catch (HibernateException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public FundVl findLatestFundVl(FundDesc fundDesc, LocalDate day) throws InstanceNotFoundException {

		try {
			Session session = sessionFactory.openSession();
			try {

				tx = session.beginTransaction();
				String hql = "from FundVl as vl where vl.fundDesc.id = ?1 and day <= ?2 order by day desc";
				Query<?> query = session.createQuery(hql);
				query.setParameter(1, fundDesc.getId());
				query.setParameter(2, day);
				query.setMaxResults(1);
				FundVl fundVl = (FundVl) query.uniqueResult();
				tx.commit();
				if (fundVl == null)
					throw new InstanceNotFoundException("Vl anterior a " + day.toString(), "FundVl");
				return fundVl;
			} catch (ConstraintViolationException e) {
				tx.rollback();
				throw new RuntimeException(e);
			} catch (HibernateException | Error e) {
				tx.rollback();
				throw e;
			} finally {
				session.close();
			}
		} catch (HibernateException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addPortOp(PortOp portOp) throws InputValidationException {

		validatePortOp(portOp);

		try {
			Session session = sessionFactory.openSession();
			try {
				tx = session.beginTransaction();
				session.save(portOp);
				tx.commit();
			} catch (javax.persistence.PersistenceException e) {
				tx.rollback();
				throw new InputValidationException("Error, ya existe una operación en la cartera: "
						+ portOp.getPortDesc().getFundPort().getpName() + " para el fondo: "
						+ portOp.getPortDesc().getFundDesc().getfId() + " el día: " + portOp.getDay());
			} catch (Error e) {
				tx.rollback();
				throw e;
			} finally {
				session.close();
			}
		} catch (HibernateException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void removePortOp(PortOp portOp) throws InstanceNotFoundException {
		try {
			Session session = sessionFactory.openSession();
			try {
				tx = session.beginTransaction();
				session.delete((PortOp) session.get(PortOp.class, new PortOpPK(portOp.getPortDesc(), portOp.getDay())));
				tx.commit();
			} catch (java.lang.NullPointerException | java.lang.IllegalArgumentException e) {
				tx.rollback();
				throw new InstanceNotFoundException("Cartera: " + portOp.getPortDesc().getFundPort().getpName()
						+ " fondo: " + portOp.getPortDesc().getFundDesc().getfId() + " día " + portOp.getDay(),
						"PortOp");
			} catch (HibernateException | Error e) {
				tx.rollback();
				throw e;
			} finally {
				session.close();
			}
		} catch (HibernateException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void UpdatePortOp(PortOp portOp) {

		try {
			Session session = sessionFactory.openSession();
			try {
				tx = session.beginTransaction();
				session.update(portOp);
				tx.commit();
			} catch (ConstraintViolationException e) {
				tx.rollback();
				throw new RuntimeException(e);
			} catch (RuntimeException | Error e) {
				tx.rollback();
				throw e;
			} finally {
				session.close();
			}
		} catch (HibernateException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public PortOp findPortOp(FundPort fundPort, FundDesc fundDesc, LocalDate day)
			throws InstanceNotFoundException, InputValidationException {

		try {
			Session session = sessionFactory.openSession();
			try {
				tx = session.beginTransaction();
				PortOp portOp = (PortOp) session.get(PortOp.class, new PortOpPK(new PortDesc(fundPort, fundDesc), day));
				tx.commit();
				if (portOp == null)
					throw new InstanceNotFoundException(day, "PortOp");

				return calculatePortOp(portOp);
			} catch (ConstraintViolationException e) {
				tx.rollback();
				throw new RuntimeException(e);
			} catch (HibernateException | Error e) {
				tx.rollback();
				throw e;
			} finally {
				session.close();
			}
		} catch (HibernateException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PortOp> findAllPortOp(FundPort fundPort, FundDesc fundDesc) throws InputValidationException {
		try {
			Session session = sessionFactory.openSession();
			try {
				tx = session.beginTransaction();
				String hql = "from PortOp as po where po.portDesc.fundPortId.pId = ?1 and po.portDesc.fundDescId.id = ?2";
				Query<?> query = session.createQuery(hql);
				query.setParameter(1, fundPort.getpId());
				query.setParameter(2, fundDesc.getId());
				List<PortOp> fundPortOpList = (List<PortOp>) query.list();
				tx.commit();

				for (int x = 0; x < fundPortOpList.size(); x++) {
					calculatePortOp(fundPortOpList.get(x));
				}

				return (List<PortOp>) fundPortOpList;
			} catch (ConstraintViolationException e) {
				tx.rollback();
				throw new RuntimeException(e);
			} catch (HibernateException | Error e) {
				tx.rollback();
				throw e;
			} finally {
				session.close();
			}
		} catch (HibernateException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PortOp> findAllPortOpbyRange(FundPort fundPort, FundDesc fundDesc, LocalDate startDay, LocalDate endDay)
			throws InputValidationException {

		try {
			Session session = sessionFactory.openSession();
			try {
				tx = session.beginTransaction();
				String hql = "from PortOp as po where po.portDesc.fundPortId.pId = ?1 and po.portDesc.fundDescId.id = ?2 and (day BETWEEN ?3 and ?4)";
				Query<?> query = session.createQuery(hql);
				query.setParameter(1, fundPort.getpId());
				query.setParameter(2, fundDesc.getId());
				query.setParameter(3, startDay);
				query.setParameter(4, endDay);
				List<PortOp> fundPortOpList = (List<PortOp>) query.list();
				tx.commit();

				// for (int x = 0; x < fundPortOpList.size(); x++) {
				// calculatePortOp(fundPortOpList.get(x));
				// }

				return (List<PortOp>) fundPortOpList;
			} catch (ConstraintViolationException e) {
				tx.rollback();
				throw new RuntimeException(e);
			} catch (HibernateException | Error e) {
				tx.rollback();
				throw e;
			} finally {
				session.close();
			}
		} catch (HibernateException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public PortOp findLatestPortOp(FundPort fundPort, FundDesc fundDesc, LocalDate day)
			throws InstanceNotFoundException, InputValidationException {

		try {
			Session session = sessionFactory.openSession();
			try {

				tx = session.beginTransaction();
				String hql = "from PortOp as po where po.portDesc.fundPortId.pId = ?1 "
						+ "and po.portDesc.fundDescId.id = ?2 and day <= ?3 order by day desc";
				Query<?> query = session.createQuery(hql);
				query.setParameter(1, fundPort.getpId());
				query.setParameter(2, fundDesc.getId());
				query.setParameter(3, day);
				query.setMaxResults(1);
				PortOp portOp = (PortOp) query.uniqueResult();
				tx.commit();
				if (portOp == null)
					throw new InstanceNotFoundException("Operación anterior a " + day.toString(), "FundOp");
				return calculatePortOp(portOp);
			} catch (ConstraintViolationException e) {
				tx.rollback();
				throw new RuntimeException(e);
			} catch (HibernateException | Error e) {
				tx.rollback();
				throw e;
			} finally {
				session.close();
			}
		} catch (HibernateException e) {
			throw new RuntimeException(e);
		}
	}

	private PortOp calculatePortOp(PortOp portOp) throws InputValidationException {
		
		//REVISAR BIEN ESTA MAL PLANTEADO
		
		FundVl fundVl;
		try {
			fundVl = findLatestFundVl(portOp.getPortDesc().getFundDesc(), portOp.getDay());
		} catch (InstanceNotFoundException e) {
			throw new InputValidationException(
					"Error: No existe un vl anterior a la operación, por favor inserte uno.");
		}

		List<PortOp> fundPortOpList = findAllPortOpbyRange(portOp.getPortDesc().getFundPort(),
				portOp.getPortDesc().getFundDesc(), LocalDate.parse("1950-01-01"), portOp.getDay());

		int i = 0;

		for (int x = 0; x < fundPortOpList.size(); x++) {
			i += fundPortOpList.get(x).getfPartOp();
		}
		if ((i + portOp.getfPartOp()) < 0) {
			throw new InputValidationException(
					"Error: el conjunto de operaciones es erroneo, el total de participaciones es negativo.");
		}

		portOp.setfPartfin(i);

		portOp.setfPartini(i - portOp.getfPartOp());

		if (portOp.getfPartOp() > 0) {

			portOp.setfPrice(
					(portOp.getfPartOp() * fundVl.getVl()) * (1 + portOp.getPortDesc().getFundDesc().getfSubComm()));

		} else {
			if (portOp.getfPartOp() < 0) {

				portOp.setfPrice((portOp.getfPartOp() * fundVl.getVl()) - ((portOp.getfPartOp() * fundVl.getVl())
						* portOp.getPortDesc().getFundDesc().getfCancelComm()));

			}
		}
		return portOp;

	}

}
