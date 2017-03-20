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
			PropertyValidator.validateNotNegativeDouble(fundDesc.getFundVls().get(x).getVl());
		}
	}

	private void validateFundVl(FundVl fundVl) throws InputValidationException {

		PropertyValidator.validateNotNegativeDouble(fundVl.getVl());

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
	public void removeFundVl(FundDesc fundDesc, LocalDate day) throws InstanceNotFoundException {

		try {
			Session session = sessionFactory.openSession();
			try {
				tx = session.beginTransaction();
				session.delete((FundVl) session.get(FundVl.class, new FundVlPK(fundDesc, day)));
				tx.commit();
			} catch (java.lang.NullPointerException | java.lang.IllegalArgumentException e) {
				tx.rollback();
				throw new InstanceNotFoundException(fundDesc.getfId(), "fundVl");
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
	public void addPortDesc(FundDesc fundDesc, FundPort fundPort) throws InstanceNotFoundException, InputValidationException {
		try {
			Session session = sessionFactory.openSession();
			try {
				tx = session.beginTransaction();
				session.save(new PortDesc(fundPort,fundDesc));
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
	public List<FundDesc> findFundsOfPortfolio(FundPort fundPortfolio) {

		List<FundDesc> fundDescList = new ArrayList<FundDesc>();

		fundPortfolio.getFundDescs().forEach((temp) -> {
			fundDescList.add(temp.getFundDesc());
		});

		return fundDescList;
	}

	@Override
	public void removePortDesc( FundDesc fundDesc, FundPort fundPort) throws InstanceNotFoundException {
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

}
