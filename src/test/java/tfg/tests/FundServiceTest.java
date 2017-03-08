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
	private final String VALID_FOUND_ID_1 = "DE0008490962";
	private final String VALID_FOUND_ID_2 = "ES0173394034";
	private final String INVALID_FOUND_ID = "ES0173394033";

	@BeforeClass
	public static void init() {

		fundService = new FundServiceImpl();

	}

	private FundVl getValidFundVl(String date, FundDesc fundDesc) throws ParseException {
		LocalDate c = LocalDate.parse(date);
		return new FundVl(c, 25.00, fundDesc);
	}

	private FundDesc getValidFundDesc() throws ParseException {

		FundDesc fund = new FundDesc(VALID_FOUND_ID_1, "Pinball Wizards", "Alto riesgo", "Monetario", "Euro");
		fund.getFundVls().add(getValidFundVl("2020-04-20", fund));
		fund.getFundVls().add(getValidFundVl("2020-04-21", fund));
		fund.getFundVls().add(getValidFundVl("2020-04-22", fund));

		return fund;
	}

	@Test
	public void testAddFundFindFund() throws InputValidationException, InstanceNotFoundException, ParseException {

		FundDesc addedFound = this.getValidFundDesc();

		fundService.addFund(addedFound);
		FundDesc findFound = fundService.findFund(addedFound.getfId());
		fundService.removeFund(findFound); // Se realiza esta función
											// antes para
		// que se complete la transacción
		// (LAZY)
		assertTrue(addedFound.equals(findFound));

	}

	@Test(expected = InstanceNotFoundException.class)
	public void testFindNoExistentFund() throws InputValidationException, InstanceNotFoundException {

		fundService.findFund(NON_EXISTENT_FOUND_ID);

	}

	@Test(expected = InputValidationException.class)
	public void testAddInvalidIsinFund() throws InputValidationException, InstanceNotFoundException, ParseException {

		FundDesc baseFound = this.getValidFundDesc();
		baseFound.setfId(INVALID_FOUND_ID);
		fundService.addFund(baseFound);

	}

	@Test(expected = InputValidationException.class)
	public void testAddNegativeVlFund() throws InputValidationException, InstanceNotFoundException, ParseException {

		FundDesc baseFound = this.getValidFundDesc();
		baseFound.getFundVls().get(0).setVl(-25.0);
		fundService.addFund(baseFound);

	}

	@Test(expected = InputValidationException.class)
	public void testAddVltoExistentDay() throws InputValidationException, InstanceNotFoundException, ParseException {

		FundDesc baseFound = this.getValidFundDesc();

		try {
			fundService.addFund(baseFound);

			baseFound.getFundVls().add(getValidFundVl("2020-04-22", baseFound));
			baseFound.getFundVls().get(0).setVl(100.0);

			fundService.updateFund(baseFound);
		} catch (InputValidationException e) {
			throw new InputValidationException(e.getMessage());
		} finally {
			fundService.removeFund(fundService.findFund(baseFound.getfId()));
		}
	}

	@Test(expected = InputValidationException.class)
	public void testAddDuplicateFoundId() throws InputValidationException, InstanceNotFoundException, ParseException {

		FundDesc addedFound = this.getValidFundDesc();
		try {
			fundService.addFund(addedFound);
			fundService.addFund(addedFound);
		} catch (InputValidationException e) {
			throw new InputValidationException(e.getMessage());
		} finally {
			fundService.removeFund(addedFound);
		}
	}

	@Test
	public void testUpdateFund() throws InputValidationException, ParseException, InstanceNotFoundException {

		FundDesc baseFound = this.getValidFundDesc();

		fundService.addFund(baseFound);

		baseFound.setfGest("November Rain");
		baseFound.setfCurrency("Libras");

		fundService.updateFund(baseFound);

		FundDesc updatedFound = fundService.findFund(baseFound.getfId());

		fundService.removeFund(updatedFound); // Se realiza esta
												// función antes
		// para que se complete la
		// transacción (LAZY)
		assertTrue(baseFound.equals(updatedFound));

	}

	@Test
	public void testSaveAndUpdateNewFundVl()
			throws InputValidationException, ParseException, InstanceNotFoundException {

		FundDesc baseFound = this.getValidFundDesc();

		fundService.addFund(baseFound);

		FundDesc findFound = fundService.findFund(baseFound.getfId());

		findFound.setfGest("November Rain");
		findFound.setfCurrency("Libras");
		findFound.setfId(VALID_FOUND_ID_2);
		findFound.getFundVls().add(getValidFundVl("2020-04-23", baseFound));
		findFound.getFundVls().add(getValidFundVl("2020-04-24", baseFound));
		findFound.getFundVls().get(0).setVl(100.0);

		fundService.updateFund(findFound);

		FundDesc updatedFound = fundService.findFund(VALID_FOUND_ID_2);
		fundService.removeFund(updatedFound); // Se realiza esta
												// función antes
		// para que se complete la
		// transacción (LAZY)
		assertTrue(findFound.equals(updatedFound));

	}

	@Test
	public void testSaveNewFundVl() throws InputValidationException, ParseException, InstanceNotFoundException {

		FundDesc baseFound = this.getValidFundDesc();

		fundService.addFund(baseFound);

		FundDesc findFound = fundService.findFund(baseFound.getfId());

		findFound.getFundVls().add(getValidFundVl("2020-04-23", baseFound));
		findFound.getFundVls().add(getValidFundVl("2020-04-24", baseFound));

		fundService.updateFund(findFound);

		FundDesc updatedFound = fundService.findFund(findFound.getfId());
		fundService.removeFund(updatedFound); // Se realiza esta
												// función antes
		// para que se complete la
		// transacción (LAZY)
		assertTrue(findFound.equals(updatedFound));

	}

	@Test
	public void testFindFundVl() throws ParseException, InputValidationException, InstanceNotFoundException {

		FundDesc addedFound = this.getValidFundDesc();

		fundService.addFund(addedFound);

		FundVl vl = fundService.findFundVl(addedFound, LocalDate.parse("2020-04-20"));
		fundService.removeFund(addedFound); // Se realiza esta función
											// antes
		// para que se complete la
		// transacción (LAZY)
		assertTrue(vl.getVl() == 25.00);

	}

	@Test(expected = InputValidationException.class)
	public void testUpdateFundToExistenceFoundId()
			throws ParseException, InputValidationException, InstanceNotFoundException {

		FundDesc addedFound = this.getValidFundDesc();
		FundDesc addedFound1 = this.getValidFundDesc();
		addedFound1.setfId(VALID_FOUND_ID_2);

		fundService.addFund(addedFound);
		fundService.addFund(addedFound1);

		addedFound.setfId(VALID_FOUND_ID_2);

		try {
			fundService.updateFund(addedFound);
		} catch (InputValidationException e) {
			throw new InputValidationException(e.getMessage());
		} finally {
			fundService.removeFund(fundService.findFund(VALID_FOUND_ID_1));
			fundService.removeFund(fundService.findFund(VALID_FOUND_ID_2));
		}

	}

	@Test
	public void testFindAllFunds() throws ParseException, InputValidationException, InstanceNotFoundException {

		FundDesc addedFound = this.getValidFundDesc();
		FundDesc addedFound1 = this.getValidFundDesc();

		addedFound1.setfId(VALID_FOUND_ID_2);

		List<FundDesc> fundDescs1 = new ArrayList<FundDesc>();

		fundDescs1.add(addedFound);
		fundDescs1.add(addedFound1);

		fundService.addFund(addedFound);
		fundService.addFund(addedFound1);

		List<FundDesc> fundDescs = fundService.findAllFunds();

		fundService.removeFund(addedFound);
		fundService.removeFund(addedFound1);

		if (fundDescs.size() != fundDescs1.size())
			assertTrue(false);

		for (int x = 0; x < fundDescs.size(); x++) {
			if (!fundDescs.get(x).equals(fundDescs1.get(x))) {
				assertTrue(false);
			}
		}
		assertTrue(true);
	}

	@Test
	public void testFindAllEmptyFunds() throws ParseException, InputValidationException, InstanceNotFoundException {

		if (fundService.findAllFunds().size() == 0) {
			assertTrue(true);
		} else {
			assertTrue(false);
		}
	}

	@Test
	public void testFindFundByKeywords() throws ParseException, InputValidationException, InstanceNotFoundException {

		FundDesc addedFound1 = this.getValidFundDesc();
		FundDesc addedFound2 = this.getValidFundDesc();
		Boolean bool = true;

		addedFound2.setfId(VALID_FOUND_ID_2);

		fundService.addFund(addedFound1);
		fundService.addFund(addedFound2);

		if (fundService.findFundsByKeywords("DE").size() != 1) {
			bool = false;
		}
		if (fundService.findFundsByKeywords("EU").size() != 2) {
			bool = false;
		}
		if (fundService.findFundsByKeywords("DE0008490").size() != 1) {
			bool = false;
		}
		if (fundService.findFundsByKeywords("ES0173394").size() != 1) {
			bool = false;
		}
		if (fundService.findFundsByKeywords("alto").size() != 2) {
			bool = false;
		}
		if (fundService.findFundsByKeywords("mone").size() != 2) {
			bool = false;
		}
		if (fundService.findFundsByKeywords("pepe").size() != 0) {
			bool = false;
		}
		if (fundService.findFundsByKeywords("").size() != 2) {
			bool = false;
		}

		fundService.removeFund(addedFound1);
		fundService.removeFund(addedFound2);

		assertTrue(bool);
	}

	@Test
	public void testFindFundVlbyRangs() throws ParseException, InputValidationException, InstanceNotFoundException {

		FundDesc addedFound = this.getValidFundDesc();

		fundService.addFund(addedFound);
		List<FundVl> vlList = fundService.findFundVlbyRange(addedFound, LocalDate.parse("2020-04-20"),
				LocalDate.parse("2020-05-01"));

		for (int x = 0; x < vlList.size(); x++) {
			if (!addedFound.getFundVls().get(x).equals(vlList.get(x))) {
				assertTrue(false);
			}
		}
		fundService.removeFund(addedFound);

		assertTrue(true);
	}

	@Test(expected = InstanceNotFoundException.class)
	public void testRemoveNoExistentFund() throws InputValidationException, InstanceNotFoundException, ParseException {

		FundDesc addedFound = this.getValidFundDesc();
		addedFound.setId((long) 1);
		fundService.removeFund(addedFound);

	}

	@Test
	public void testRemoveFundVl() throws ParseException, InputValidationException, InstanceNotFoundException {

		FundDesc addedFound = this.getValidFundDesc();

		fundService.addFund(addedFound);

		fundService.removeFundVl(addedFound, LocalDate.parse("2020-04-20"));

		FundDesc findFund = fundService.findFund(addedFound.getfId());

		fundService.removeFund(addedFound);

		assertTrue(findFund.getFundVls().size() == 2);
	}

	@Test(expected = InstanceNotFoundException.class)
	public void testRemoveNonExistentFundVl() throws ParseException, InputValidationException, InstanceNotFoundException {

		FundDesc addedFound = this.getValidFundDesc();

		fundService.addFund(addedFound);

		try {
			fundService.removeFundVl(addedFound, LocalDate.parse("2027-04-22"));
		} catch (InstanceNotFoundException e) {
			throw new InstanceNotFoundException(addedFound.getfId(),"foundVl");
		} finally {

			fundService.removeFund(addedFound);
		}

	}
}
