package tfg.tests;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.time.LocalDate;
import org.junit.BeforeClass;

import tfg.app.model.FundDesc;
import tfg.app.model.FundService;
import tfg.app.model.FundServiceImpl;
import tfg.app.model.FundVl;
import tfg.exceptions.InputValidationException;

import org.junit.Test;

public class FundServiceTest {

	private static FundService fundService = null;

	@BeforeClass
	public static void init() {

		fundService = new FundServiceImpl();

	}

	private FundVl getValidFundVl(String date, FundDesc fundDesc) throws ParseException {
		LocalDate c = LocalDate.parse(date);
		return new FundVl(c, 25.00, fundDesc);
	}

	private FundDesc getValidFundDesc(Integer id) throws ParseException {

		FundDesc fund = new FundDesc(id, "Pinball Wizards", "Alto riesgo", "Monetario", "Euro");
		fund.getFundVls().add(getValidFundVl("2020-04-20", fund));
		fund.getFundVls().add(getValidFundVl("2020-04-21", fund));

		return fund;
	}

	@Test
	public void testAddFundFindFund() throws ParseException, InputValidationException {

		FundDesc addedFound = this.getValidFundDesc(1);

		fundService.addFund(addedFound);
		FundDesc findFound = fundService.findFund(addedFound.getfId());
		fundService.removeFund(findFound); // Se realiza esta función antes para
											// que se complete la transacción
											// (LAZY)
		assertTrue(addedFound.equals(findFound));

	}

	@Test
	public void testUpdateFund() throws InputValidationException, ParseException {

		FundDesc baseFound = this.getValidFundDesc(1);

		fundService.addFund(baseFound);

		baseFound.setfGest("November Rain");
		baseFound.setfCurrency("Libras");

		fundService.updateFund(baseFound);

		FundDesc updatedFound = fundService.findFund(baseFound.getfId());

		fundService.removeFund(updatedFound); // Se realiza esta función antes
												// para que se complete la
												// transacción (LAZY)
		assertTrue(baseFound.equals(updatedFound));

	}

	@Test
	public void testSaveAndUpdateNewFundVl() throws InputValidationException, ParseException {

		FundDesc baseFound = this.getValidFundDesc(1);

		fundService.addFund(baseFound);

		baseFound.setfGest("November Rain");
		baseFound.setfCurrency("Libras");
		baseFound.getFundVls().add(getValidFundVl("2020-04-22", baseFound));
		baseFound.getFundVls().get(0).setVl(100.0);

		fundService.updateFund(baseFound);

		FundDesc updatedFound = fundService.findFund(baseFound.getfId());
		fundService.removeFund(updatedFound); // Se realiza esta función antes
												// para que se complete la
												// transacción (LAZY)
		assertTrue(baseFound.equals(updatedFound));

	}

	@Test
	public void testFindFundVl() throws ParseException, InputValidationException {

		FundDesc addedFound = this.getValidFundDesc(1);

		fundService.addFund(addedFound);

		Double vl = fundService.findFundVl(addedFound.getfId(), LocalDate.parse("2020-04-20"));
		fundService.removeFund(addedFound); // Se realiza esta función antes
											// para que se complete la
											// transacción (LAZY)
		assertTrue(vl == 25.00);

	}

}
