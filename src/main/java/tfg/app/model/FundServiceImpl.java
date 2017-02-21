package tfg.app.model;

import java.time.LocalDate;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import tfg.exceptions.InputValidationException;

public class FundServiceImpl implements FundService {

	private SessionFactory sessionFactory;
	private Transaction tx;

	public FundServiceImpl() {
		sessionFactory = new Configuration().configure().buildSessionFactory();

	}

	public void addFund(FundDesc fundDesc) throws InputValidationException {

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

	public void removeFund(FundDesc fundDesc) {

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

	public FundDesc findFund(Integer fundId) {

		Session session = sessionFactory.openSession();
		try {
			tx = session.beginTransaction();
			FundDesc fundDesc = (FundDesc) session.get(FundDesc.class, fundId);
			tx.commit();
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
	public Double findFundVl(Integer fundId, LocalDate day) {

		FundDesc fundDesc = this.findFund(fundId);
		Session session = sessionFactory.openSession();
		try {
			tx = session.beginTransaction();
			FundVl fundVl = (FundVl) session.get(FundVl.class, new FundVlPK(fundDesc, day));
			tx.commit();
			return fundVl.getVl();

		} catch (HibernateException e) {

			tx.rollback();
			e.printStackTrace();

		} finally {

			session.close();
		}
		return null;
	}

}
