package tfg.tests;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;

import tfg.app.model.FundDesc;
import tfg.app.model.FundService;
import tfg.app.model.FundServiceImpl;
import tfg.app.model.FundVl;
import tfg.app.util.exceptions.InputValidationException;
import tfg.app.util.exceptions.InstanceNotFoundException;

import org.junit.Test;

public class FundServiceTest {

	private static FundService fundService = null;
	private final String NON_EXISTENT_FOUND_ID = "123123123DE";

	@BeforeClass
	public static void init() {

		fundService = new FundServiceImpl();

	}

	private FundVl getValidFundVl(String date, FundDesc fundDesc) throws ParseException {
		LocalDate c = LocalDate.parse(date);
		return new FundVl(c, 25.00, fundDesc);
	}

	private FundDesc getValidFundDesc() throws ParseException {

		FundDesc fund = new FundDesc("DE0008490962", "Pinball Wizards", "Alto riesgo", "Monetario", "Euro");
		fund.getFundVls().add(getValidFundVl("2020-04-20", fund));
		fund.getFundVls().add(getValidFundVl("2020-04-21", fund));

		return fund;
	}

	@Test
	public void testAddFundFindFund() throws ParseException, InputValidationException, InstanceNotFoundException {

		FundDesc addedFound = this.getValidFundDesc();

		fundService.addFund(addedFound);
		FundDesc findFound = fundService.findFund(addedFound.getfId());
		fundService.removeFund(findFound.getfId()); // Se realiza esta función antes para
											// que se complete la transacción
											// (LAZY)
		assertTrue(addedFound.equals(findFound));

	}
	
	@Test(expected = InstanceNotFoundException.class)
	public void testFindNotExistentFund() throws ParseException, InputValidationException, InstanceNotFoundException {

		fundService.findFund(NON_EXISTENT_FOUND_ID);

	}

	@Test
	public void testUpdateFund() throws InputValidationException, ParseException, InstanceNotFoundException {

		FundDesc baseFound = this.getValidFundDesc();

		fundService.addFund(baseFound);

		baseFound.setfGest("November Rain");
		baseFound.setfCurrency("Libras");

		fundService.updateFund(baseFound);

		FundDesc updatedFound = fundService.findFund(baseFound.getfId());

		fundService.removeFund(updatedFound.getfId()); // Se realiza esta función antes
												// para que se complete la
												// transacción (LAZY)
		assertTrue(baseFound.equals(updatedFound));

	}

	@Test
	public void testSaveAndUpdateNewFundVl() throws InputValidationException, ParseException, InstanceNotFoundException {

		FundDesc baseFound = this.getValidFundDesc();

		fundService.addFund(baseFound);

		baseFound.setfGest("November Rain");
		baseFound.setfCurrency("Libras");
		baseFound.getFundVls().add(getValidFundVl("2020-04-22", baseFound));
		baseFound.getFundVls().get(0).setVl(100.0);

		fundService.updateFund(baseFound);

		FundDesc updatedFound = fundService.findFund(baseFound.getfId());
		fundService.removeFund(updatedFound.getfId()); // Se realiza esta función antes
												// para que se complete la
												// transacción (LAZY)
		assertTrue(baseFound.equals(updatedFound));

	}

	@Test
	public void testFindFundVl() throws ParseException, InputValidationException, InstanceNotFoundException {

		FundDesc addedFound = this.getValidFundDesc();

		fundService.addFund(addedFound);

		Double vl = fundService.findFundVl(addedFound.getfId(), LocalDate.parse("2020-04-20"));
		fundService.removeFund(addedFound.getfId()); // Se realiza esta función antes
											// para que se complete la
											// transacción (LAZY)
		assertTrue(vl == 25.00);

	}

	@Test
	public void testFindAllFunds() throws ParseException, InputValidationException, InstanceNotFoundException {

		FundDesc addedFound = this.getValidFundDesc();
		FundDesc addedFound1 = this.getValidFundDesc();

		addedFound1.setfId("ES0173394034");

		List<FundDesc> fundDescs1 = new ArrayList<FundDesc>();

		fundDescs1.add(addedFound);
		fundDescs1.add(addedFound1);

		fundService.addFund(addedFound);
		fundService.addFund(addedFound1);

		List<FundDesc> fundDescs = fundService.findAllFunds();

		fundService.removeFund(addedFound.getfId());
		fundService.removeFund(addedFound1.getfId());

		if (fundDescs.size() != fundDescs1.size())
			assertTrue(false);

		for (int x = 0; x < fundDescs.size(); x++) {
			if (!fundDescs.get(x).equals(fundDescs1.get(x))) {
				assertTrue(false);
			}
		}
		assertTrue(true);
	}
}
