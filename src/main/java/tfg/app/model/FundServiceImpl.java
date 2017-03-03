package tfg.app.model;

import java.time.LocalDate;
import java.util.List;

import tfg.app.util.validator.PropertyValidator;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;
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
				throw new InputValidationException("Error, el Isin del fondo: "+fundDesc.getfId()+" ya existe en la base de datos.");
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
				session.saveOrUpdate(fundDesc);
				fundDesc.getFundVls().forEach((temp) -> {
					session.saveOrUpdate(temp);
				});
				tx.commit();
			} catch (javax.persistence.PersistenceException  e) {
				tx.rollback();
				throw new InputValidationException("Error, se está intentando modificar un id a otro que ya existe.");
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

	public void removeFund(FundDesc fundDesc) throws InstanceNotFoundException {

		try {
			Session session = sessionFactory.openSession();
			try {
				tx = session.beginTransaction();
				session.delete(fundDesc);
				tx.commit();
			} catch (java.lang.NullPointerException e) {
				tx.rollback();
				throw new InstanceNotFoundException(fundDesc.getfId(),"fundDesc");
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
	public Double findFundVl(String fundId, LocalDate day) throws InstanceNotFoundException {

		FundDesc fundDesc = findFund(fundId);
		try {
			Session session = sessionFactory.openSession();
			try {
				tx = session.beginTransaction();
				FundVl fundVl = (FundVl) session.get(FundVl.class, new FundVlPK(fundDesc, day));
				tx.commit();
				if (fundVl == null)
					throw new InstanceNotFoundException(day, "FundVl");
				return fundVl.getVl();
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

	@Override
	public List<FundDesc> findFundsByKeywords(String keywords) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double findFundVlbyRange(String fundId, LocalDate day) throws InstanceNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double removeFundVl(String fundId, LocalDate day) throws InstanceNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}
}
