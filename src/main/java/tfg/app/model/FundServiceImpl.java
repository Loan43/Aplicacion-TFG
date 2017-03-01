package tfg.app.model;

import java.time.LocalDate;
import java.util.List;

import tfg.app.util.validator.PropertyValidator;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
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

	}

	public void addFund(FundDesc fundDesc) throws InputValidationException {
		
		validateFund(fundDesc);
		Session session = sessionFactory.openSession();
		try {

			tx = session.beginTransaction();
			session.save(fundDesc);
			fundDesc.getFundVls().forEach((temp) -> {
				session.save(temp);
			});
			tx.commit();
		} catch (HibernateException e) {

			tx.rollback();
			e.printStackTrace();

		} finally {

			session.close();

		}
	}

	public void updateFund(FundDesc fundDesc) throws InputValidationException {
		
		validateFund(fundDesc);
		Session session = sessionFactory.openSession();
		try {

			tx = session.beginTransaction();
			session.saveOrUpdate(fundDesc);
			fundDesc.getFundVls().forEach((temp) -> {
				session.saveOrUpdate(temp);
			});
			tx.commit();

		} catch (HibernateException e) {

			tx.rollback();
			e.printStackTrace();

		} finally {

			session.close();

		}
	}

	public void removeFund(String fundId) throws InstanceNotFoundException {

		FundDesc fundDesc = findFund(fundId);
		Session session = sessionFactory.openSession();
		try {

			tx = session.beginTransaction();
			session.delete(fundDesc);
			tx.commit();

		} catch (HibernateException e) {

			tx.rollback();
			e.printStackTrace();

		} finally {

			session.close();

		}

	}

	public FundDesc findFund(String fundId) throws InstanceNotFoundException {

		Session session = sessionFactory.openSession();
		try {
			// System.out.println("ASDASDAS");
			tx = session.beginTransaction();
			String hql = "from FundDesc where fId like ?1";
			Query<?> query = session.createQuery(hql);
			query.setParameter(1, new String(fundId));
			query.setMaxResults(1);
			FundDesc fundDesc = (FundDesc) query.uniqueResult();
			tx.commit();
			// return this.findFundByFundId(fundDesc.getfId());
			if (fundDesc == null)
				throw new InstanceNotFoundException(fundId, "FundDesc");
			return fundDesc;

		} catch (HibernateException e) {

			tx.rollback();
			e.printStackTrace();

		} finally {

			session.close();
		}
		return null;
	}

	@Override
	public Double findFundVl(String fundId, LocalDate day) throws InstanceNotFoundException {

		FundDesc fundDesc = findFund(fundId);
		Session session = sessionFactory.openSession();
		try {
			tx = session.beginTransaction();
			FundVl fundVl = (FundVl) session.get(FundVl.class, new FundVlPK(fundDesc, day));
			tx.commit();
			if (fundVl == null)
				throw new InstanceNotFoundException(day, "FundVl");
			return fundVl.getVl();

		} catch (HibernateException e) {

			tx.rollback();
			e.printStackTrace();

		} finally {

			session.close();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FundDesc> findAllFunds() {
		Session session = sessionFactory.openSession();
		try {
			tx = session.beginTransaction();
			String hql = "from FundDesc";
			Query<?> query = session.createQuery(hql);
			List<?> fundDescList = query.list();
			tx.commit();
			return (List<FundDesc>) fundDescList;

		} catch (HibernateException e) {

			tx.rollback();
			e.printStackTrace();

		} finally {

			session.close();
		}
		return null;
	}

}
