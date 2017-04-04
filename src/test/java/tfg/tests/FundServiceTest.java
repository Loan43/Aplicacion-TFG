package tfg.tests;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;

import tfg.app.model.entities.FundDesc;
import tfg.app.model.entities.FundPort;
import tfg.app.model.entities.FundVl;
import tfg.app.model.entities.PortOp;
import tfg.app.model.service.FundService;
import tfg.app.model.service.FundServiceImpl;
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

	private PortOp getValidPortOp(String date, FundPort fundPort, FundDesc fundDesc) throws ParseException {
		LocalDate c = LocalDate.parse(date);
		return new PortOp(c, fundPort, fundDesc, 100);
	}

	private FundPort getValidFundPort() {

		return new FundPort("Cartera Test 1", "Esto es una cartera de prueba");
	}

	private FundDesc getValidFundDesc() throws ParseException {

		FundDesc fund = new FundDesc(VALID_FOUND_ID_1, "Pinball Wizards", "Alto riesgo", "Monetario", "Euro", 0.01,
				0.02);
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
		fundService.removeFund(findFound);
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
	public void testAddNegativeVlFundAtFundDescInsert()
			throws InputValidationException, InstanceNotFoundException, ParseException {

		FundDesc baseFound = this.getValidFundDesc();
		baseFound.getFundVls().get(0).setVl(-25.0);
		fundService.addFund(baseFound);

	}

	@Test(expected = InputValidationException.class)
	public void testAddVltoExistentDay() throws InputValidationException, InstanceNotFoundException, ParseException,
			InstantiationException, IllegalAccessException {

		FundDesc baseFound = this.getValidFundDesc();

		try {
			fundService.addFund(baseFound);

			FundVl fundVl = getValidFundVl("2020-04-20", baseFound);

			fundService.addFundVl(fundVl);

		} catch (InputValidationException e) {
			throw new InputValidationException(e.getMessage());
		} finally {
			fundService.removeFund(baseFound);
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

		fundService.removeFund(updatedFound);

		assertTrue(baseFound.equals(updatedFound));

	}

	@Test
	public void testAddNewFundVl() throws InputValidationException, ParseException, InstanceNotFoundException {

		FundDesc baseFound = this.getValidFundDesc();

		fundService.addFund(baseFound);

		fundService.addFundVl((getValidFundVl("2020-04-27", baseFound)));
		fundService.addFundVl((getValidFundVl("2020-04-28", baseFound)));

		FundDesc findFound = fundService.findFund(baseFound.getfId());

		fundService.removeFund(findFound);

		assertTrue(findFound.getFundVls().size() == 5);

	}

	@Test
	public void testFindFundVl() throws ParseException, InputValidationException, InstanceNotFoundException {

		FundDesc addedFound = this.getValidFundDesc();

		fundService.addFund(addedFound);

		FundVl vl = fundService.findFundVl(addedFound, LocalDate.parse("2020-04-20"));
		fundService.removeFund(addedFound);
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

	@Test(expected = InputValidationException.class)
	public void testUpdateFundToInvalidIsin()
			throws ParseException, InputValidationException, InstanceNotFoundException {

		FundDesc addedFound = this.getValidFundDesc();

		fundService.addFund(addedFound);

		addedFound.setfId(INVALID_FOUND_ID);

		try {
			fundService.updateFund(addedFound);
		} catch (InputValidationException e) {
			throw new InputValidationException(e.getMessage());
		} finally {
			fundService.removeFund(addedFound);
		}

	}

	@Test
	public void testFindAllFunds() throws ParseException, InputValidationException, InstanceNotFoundException {

		FundDesc addedFound = this.getValidFundDesc();
		FundDesc addedFound1 = this.getValidFundDesc();

		Boolean bool = true;

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
			bool = false;

		for (int x = 0; x < fundDescs.size(); x++) {
			if (!fundDescs.get(x).equals(fundDescs1.get(x))) {
				bool = false;
			}
		}
		assertTrue(bool);
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
	public void testFindFundVlbyRange() throws ParseException, InputValidationException, InstanceNotFoundException {

		FundDesc addedFound = this.getValidFundDesc();

		fundService.addFund(addedFound);
		List<FundVl> vlList = fundService.findFundVlbyRange(addedFound, LocalDate.parse("2020-04-20"),
				LocalDate.parse("2020-05-01"));

		Boolean bool = true;

		for (int x = 0; x < vlList.size(); x++) {
			if (!addedFound.getFundVls().get(x).equals(vlList.get(x))) {
				bool = false;
			}
		}
		vlList = fundService.findFundVlbyRange(addedFound, LocalDate.parse("2021-04-20"),
				LocalDate.parse("2021-05-01"));

		if (vlList.size() != 0)
			bool = false;

		vlList = fundService.findFundVlbyRange(addedFound, LocalDate.parse("2020-04-20"),
				LocalDate.parse("2020-04-21"));

		if (vlList.size() != 2)
			bool = false;

		fundService.removeFund(addedFound);

		assertTrue(bool);
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

		fundService.removeFundVl(new FundVl(addedFound, LocalDate.parse("2020-04-20")));

		FundDesc findFund = fundService.findFund(addedFound.getfId());

		fundService.removeFund(addedFound);

		assertTrue(findFund.getFundVls().size() == 2);
	}

	@Test(expected = InstanceNotFoundException.class)
	public void testRemoveNonExistentFundVl()
			throws ParseException, InputValidationException, InstanceNotFoundException {

		FundDesc addedFound = this.getValidFundDesc();

		fundService.addFund(addedFound);

		try {
			fundService.removeFundVl(new FundVl(addedFound, LocalDate.parse("2027-04-22")));
		} catch (InstanceNotFoundException e) {
			throw new InstanceNotFoundException(addedFound.getfId(), "foundVl");
		} finally {

			fundService.removeFund(addedFound);
		}

	}

	@Test
	public void testUpdateFundVl() throws InputValidationException, ParseException, InstanceNotFoundException {

		FundDesc baseFound = this.getValidFundDesc();

		fundService.addFund(baseFound);

		FundVl fundVl = getValidFundVl("2020-04-22", baseFound);

		fundVl.setVl(23.23);

		fundService.updateFundVl(fundVl);

		FundVl updatedFundVl = fundService.findFundVl(baseFound, fundVl.getDay());

		fundService.removeFund(baseFound);

		assertTrue(updatedFundVl.equals(fundVl));

	}

	@Test(expected = InstanceNotFoundException.class)
	public void testUpdateUnexistentDayFundVl()
			throws InputValidationException, ParseException, InstanceNotFoundException {

		FundDesc baseFound = this.getValidFundDesc();

		fundService.addFund(baseFound);

		FundVl fundVl = getValidFundVl("2020-04-30", baseFound);

		fundVl.setVl(23.23);

		try {
			fundService.updateFundVl(fundVl);
		} catch (InstanceNotFoundException e) {
			throw new InstanceNotFoundException(fundVl.getDay().toString(), "foundVl");
		} finally {

			fundService.removeFund(baseFound);
		}

	}

	@Test(expected = InputValidationException.class)
	public void testUpdateNegativeVlFund() throws InputValidationException, InstanceNotFoundException, ParseException {

		FundDesc baseFound = this.getValidFundDesc();

		fundService.addFund(baseFound);

		FundVl fundVl = getValidFundVl("2020-04-30", baseFound);

		fundVl.setVl(-23.2);

		try {
			fundService.updateFundVl(fundVl);
		} catch (InputValidationException e) {
			throw new InputValidationException(e.getMessage());
		} finally {

			fundService.removeFund(baseFound);
		}

	}

	@Test(expected = InputValidationException.class)
	public void testAddNegativeVlFund() throws InputValidationException, InstanceNotFoundException, ParseException {

		FundDesc baseFound = this.getValidFundDesc();

		fundService.addFund(baseFound);

		FundVl fundVl = getValidFundVl("2020-04-30", baseFound);

		fundVl.setVl(-23.2);

		try {
			fundService.addFundVl(fundVl);
		} catch (InputValidationException e) {
			throw new InputValidationException(e.getMessage());
		} finally {

			fundService.removeFund(baseFound);
		}

	}

	@Test
	public void testAddPortfolioFindPortfolio() throws InputValidationException, InstanceNotFoundException {

		FundPort fundPortfolio = getValidFundPort();
		fundService.addFundPortfolio(fundPortfolio);

		FundPort findFundPortfolio = fundService.findFundPortfolio(fundPortfolio.getpId());

		fundService.removeFundPortfolio(findFundPortfolio);

		assertTrue(fundPortfolio.equals(findFundPortfolio));

	}

	@Test(expected = InputValidationException.class)
	public void testAddDuplicatePortfolio() throws InputValidationException, InstanceNotFoundException, ParseException {

		FundPort fundPortfolio = getValidFundPort();
		fundService.addFundPortfolio(fundPortfolio);

		try {
			fundService.addFundPortfolio(fundPortfolio);
		} catch (InputValidationException e) {
			throw new InputValidationException(e.getMessage());
		} finally {
			fundService.removeFundPortfolio(fundPortfolio);
		}
	}

	@Test(expected = InputValidationException.class)
	public void testAddInvalidPortfolio() throws InputValidationException, InstanceNotFoundException, ParseException {

		FundPort fundPortfolio = getValidFundPort();
		fundPortfolio.setpName("");
		fundService.addFundPortfolio(fundPortfolio);

	}

	@Test
	public void testUpdatePortfolio() throws InputValidationException, InstanceNotFoundException {

		FundPort fundPortfolio = getValidFundPort();
		fundService.addFundPortfolio(fundPortfolio);

		FundPort findFundPortfolio = fundService.findFundPortfolio(fundPortfolio.getpId());

		findFundPortfolio.setpDesc("Cartera modificada");

		fundService.updateFundPortfolio(findFundPortfolio);

		FundPort updatedFundPortfolio = fundService.findFundPortfolio(findFundPortfolio.getpId());

		assertTrue(findFundPortfolio.equals(updatedFundPortfolio));

		fundService.removeFundPortfolio(updatedFundPortfolio);
	}

	@Test(expected = InputValidationException.class)
	public void testUpdateInvalidNamePortfolio()
			throws InputValidationException, InstanceNotFoundException, ParseException {

		FundPort fundPortfolio = getValidFundPort();
		fundService.addFundPortfolio(fundPortfolio);
		FundPort findFundPortfolio = fundService.findFundPortfolio(fundPortfolio.getpId());
		findFundPortfolio.setpName("");

		try {
			fundService.updateFundPortfolio(findFundPortfolio);
		} catch (InputValidationException e) {
			throw new InputValidationException(e.getMessage());
		} finally {
			fundService.removeFundPortfolio(fundPortfolio);
		}

	}

	@Test(expected = InstanceNotFoundException.class)
	public void testFindNoExistentPortfolio()
			throws InputValidationException, InstanceNotFoundException, ParseException {

		fundService.findFundPortfolio((long) -1);

	}

	@Test
	public void testFindAllFundPortfolios() throws InstanceNotFoundException, InputValidationException {

		FundPort fundPortfolio1 = getValidFundPort();
		FundPort fundPortfolio2 = getValidFundPort();
		FundPort fundPortfolio3 = getValidFundPort();

		fundPortfolio1.setpName("Test 1");
		fundPortfolio2.setpName("Test 2");
		fundPortfolio3.setpName("Test 3");

		fundService.addFundPortfolio(fundPortfolio1);
		fundService.addFundPortfolio(fundPortfolio2);
		fundService.addFundPortfolio(fundPortfolio3);

		Boolean bool = true;

		List<FundPort> fundPorts = new ArrayList<FundPort>();

		fundPorts.add(fundPortfolio1);
		fundPorts.add(fundPortfolio2);
		fundPorts.add(fundPortfolio3);

		List<FundPort> findfundPorts = fundService.findAllFundPortfolios();

		fundService.removeFundPortfolio(fundPortfolio1);
		fundService.removeFundPortfolio(fundPortfolio2);
		fundService.removeFundPortfolio(fundPortfolio3);

		if (fundPorts.size() != findfundPorts.size())
			bool = false;

		for (int x = 0; x < fundPorts.size(); x++) {
			if (!fundPorts.get(x).equals(findfundPorts.get(x))) {
				bool = false;
			}
		}
		assertTrue(bool);
	}

	@Test
	public void testFindAllEmptyFundPortfolios() {

		List<FundPort> fundPorts = fundService.findAllFundPortfolios();

		if (fundPorts.size() != 0)
			assertTrue(false);

		assertTrue(true);
	}

	@Test
	public void testAddPortDescFindFundsOfPortfolio()
			throws InputValidationException, ParseException, InstanceNotFoundException {

		FundPort fundPortfolio1 = getValidFundPort();
		FundPort fundPortfolio2 = getValidFundPort();
		FundPort fundPortfolio3 = getValidFundPort();

		fundPortfolio1.setpName("Test 1");
		fundPortfolio2.setpName("Test 2");
		fundPortfolio3.setpName("Test 3");

		fundService.addFundPortfolio(fundPortfolio1);
		fundService.addFundPortfolio(fundPortfolio2);
		fundService.addFundPortfolio(fundPortfolio3);

		FundDesc addedFound1 = this.getValidFundDesc();
		FundDesc addedFound2 = this.getValidFundDesc();
		Boolean bool = true;

		addedFound2.setfId(VALID_FOUND_ID_2);

		fundService.addFund(addedFound1);
		fundService.addFund(addedFound2);

		fundService.addPortDesc(fundPortfolio1, addedFound1);
		fundService.addPortDesc(fundPortfolio1, addedFound2);

		fundService.addPortDesc(fundPortfolio2, addedFound2);

		if (fundService.findFundsOfPortfolio(fundPortfolio1).size() != 2) {
			bool = false;
		}
		if (fundService.findFundsOfPortfolio(fundPortfolio2).size() != 1) {
			bool = false;
		}
		if (fundService.findFundsOfPortfolio(fundPortfolio3).size() != 0) {
			bool = false;
		}

		fundService.removeFundPortfolio(fundPortfolio1);
		fundService.removeFundPortfolio(fundPortfolio2);
		fundService.removeFundPortfolio(fundPortfolio3);

		fundService.removeFund(addedFound1);
		fundService.removeFund(addedFound2);

		assertTrue(bool);

	}

	@Test
	public void testRemovePortDesc() throws InstanceNotFoundException, InputValidationException, ParseException {

		FundPort fundPortfolio1 = getValidFundPort();
		FundPort fundPortfolio2 = getValidFundPort();

		fundPortfolio1.setpName("Test 1");
		fundPortfolio2.setpName("Test 2");

		fundService.addFundPortfolio(fundPortfolio1);
		fundService.addFundPortfolio(fundPortfolio2);

		FundDesc addedFound1 = this.getValidFundDesc();
		FundDesc addedFound2 = this.getValidFundDesc();
		Boolean bool = true;

		addedFound2.setfId(VALID_FOUND_ID_2);

		fundService.addFund(addedFound1);
		fundService.addFund(addedFound2);

		fundService.addPortDesc(fundPortfolio1, addedFound1);
		fundService.addPortDesc(fundPortfolio1, addedFound2);

		fundService.addPortDesc(fundPortfolio2, addedFound2);

		if (fundService.findFundsOfPortfolio(fundPortfolio1).size() != 2) {
			bool = false;
		}
		if (fundService.findFundsOfPortfolio(fundPortfolio2).size() != 1) {
			bool = false;
		}

		fundService.removePortDesc(fundPortfolio1, addedFound1);
		fundService.removePortDesc(fundPortfolio1, addedFound2);

		fundService.removePortDesc(fundPortfolio2, addedFound2);

		if (fundService.findFundsOfPortfolio(fundPortfolio1).size() != 0) {
			bool = false;
		}
		if (fundService.findFundsOfPortfolio(fundPortfolio2).size() != 0) {
			bool = false;
		}

		fundService.removeFundPortfolio(fundPortfolio1);
		fundService.removeFundPortfolio(fundPortfolio2);

		fundService.removeFund(addedFound1);
		fundService.removeFund(addedFound2);

		assertTrue(bool);

	}

	@Test(expected = InstanceNotFoundException.class)
	public void testFindFundsOfUnexistentPortfolio() throws InstanceNotFoundException, InputValidationException {

		FundPort fundPortfolio1 = getValidFundPort();
		fundService.addFundPortfolio(fundPortfolio1);
		fundService.removeFundPortfolio(fundPortfolio1);

		fundService.findFundsOfPortfolio(fundPortfolio1);

	}

	@Test(expected = InstanceNotFoundException.class)
	public void testRemoveUnexistentPortDesc()
			throws InstanceNotFoundException, InputValidationException, ParseException {

		FundPort fundPortfolio1 = getValidFundPort();
		fundService.addFundPortfolio(fundPortfolio1);

		FundDesc addedFound1 = this.getValidFundDesc();
		fundService.addFund(addedFound1);

		fundService.addPortDesc(fundPortfolio1, addedFound1);
		fundService.removePortDesc(fundPortfolio1, addedFound1);

		try {
			fundService.removePortDesc(fundPortfolio1, addedFound1);
		} catch (InstanceNotFoundException e) {
			throw new InstanceNotFoundException(
					"Fondo: " + addedFound1.getfId() + " " + "Cartera: " + fundPortfolio1.getpName(), "PortDesc");
		} finally {
			fundService.removeFundPortfolio(fundPortfolio1);
			fundService.removeFund(addedFound1);
		}

	}

	@Test
	public void testFindLatestFundVl() throws ParseException, InputValidationException, InstanceNotFoundException {

		FundDesc addedFound = this.getValidFundDesc();
		;

		fundService.addFund(addedFound);

		boolean bool = true;

		if (!fundService.findLatestFundVl(addedFound, LocalDate.parse("2020-04-20")).getDay().toString()
				.equals("2020-04-20")) {
			bool = false;
		}
		if (!fundService.findLatestFundVl(addedFound, LocalDate.parse("2020-04-21")).getDay().toString()
				.equals("2020-04-21")) {
			bool = false;
		}
		if (!fundService.findLatestFundVl(addedFound, LocalDate.parse("2020-04-22")).getDay().toString()
				.equals("2020-04-22")) {
			bool = false;
		}
		if (!fundService.findLatestFundVl(addedFound, LocalDate.parse("2020-04-29")).getDay().toString()
				.equals("2020-04-22")) {
			bool = false;
		}

		fundService.removeFund(addedFound);

		assertTrue(bool);

	}

	@Test(expected = InstanceNotFoundException.class)
	public void testInvalidFindClosestFundVl()
			throws ParseException, InputValidationException, InstanceNotFoundException {

		FundDesc addedFound = this.getValidFundDesc();

		fundService.addFund(addedFound);

		try {
			fundService.findLatestFundVl(addedFound, LocalDate.parse("2020-04-17"));
		} catch (InstanceNotFoundException e) {
			throw e;
		} finally {
			fundService.removeFund(addedFound);
		}

	}

	@Test(expected = InputValidationException.class)
	public void testAddDuplicatePortDesc() throws InputValidationException, ParseException, InstanceNotFoundException {

		FundPort fundPortfolio1 = getValidFundPort();

		fundPortfolio1.setpName("Test 1");

		fundService.addFundPortfolio(fundPortfolio1);

		FundDesc addedFound1 = this.getValidFundDesc();

		fundService.addFund(addedFound1);

		fundService.addPortDesc(fundPortfolio1, addedFound1);

		try {
			fundService.addPortDesc(fundPortfolio1, addedFound1);
		} catch (InstanceNotFoundException e) {
			throw e;
		} finally {
			fundService.removeFund(addedFound1);
			fundService.removeFundPortfolio(fundPortfolio1);
		}

	}

	@Test
	public void testAddPortOpFindPortOp() throws InputValidationException, ParseException, InstanceNotFoundException {

		FundPort fundPortfolio1 = getValidFundPort();

		fundPortfolio1.setpName("Test 1");

		fundService.addFundPortfolio(fundPortfolio1);

		FundDesc addedFound1 = this.getValidFundDesc();
		FundDesc addedFound2 = this.getValidFundDesc();
		addedFound2.setfId(VALID_FOUND_ID_2);

		fundService.addFund(addedFound1);
		fundService.addFund(addedFound2);

		fundService.addPortDesc(fundPortfolio1, addedFound1);
		fundService.addPortDesc(fundPortfolio1, addedFound2);

		PortOp portOp1 = getValidPortOp("2020-04-30", fundPortfolio1, addedFound2);

		fundService.addPortOp(portOp1);

		PortOp findPortOp = fundService.findPortOp(fundPortfolio1, addedFound2, LocalDate.parse("2020-04-30"));

		fundService.removeFundPortfolio(fundPortfolio1);
		fundService.removeFund(addedFound1);
		fundService.removeFund(addedFound2);

		assertTrue(findPortOp.equals(portOp1));

	}

	@Test
	public void testUpdatePortOp() throws InputValidationException, InstanceNotFoundException, ParseException {

		FundPort fundPortfolio1 = getValidFundPort();

		fundPortfolio1.setpName("Test 1");

		fundService.addFundPortfolio(fundPortfolio1);

		FundDesc addedFound1 = this.getValidFundDesc();
		FundDesc addedFound2 = this.getValidFundDesc();
		addedFound2.setfId(VALID_FOUND_ID_2);

		fundService.addFund(addedFound1);
		fundService.addFund(addedFound2);

		FundVl fundVl = getValidFundVl("2020-01-01", addedFound1);

		fundService.addFundVl(fundVl);

		FundVl fundVl2 = getValidFundVl("2020-01-01", addedFound2);

		fundService.addFundVl(fundVl2);

		fundService.addPortDesc(fundPortfolio1, addedFound1);
		fundService.addPortDesc(fundPortfolio1, addedFound2);

		PortOp portOp1 = getValidPortOp("2020-04-17", fundPortfolio1, addedFound2);

		fundService.addPortOp(portOp1);

		portOp1.setfPartOp(1000);

		fundService.UpdatePortOp(portOp1);

		PortOp findPortOp = fundService.findPortOp(fundPortfolio1, addedFound2, LocalDate.parse("2020-04-17"));

		fundService.removePortOp(findPortOp);
		fundService.removeFundPortfolio(fundPortfolio1);
		fundService.removeFund(addedFound1);
		fundService.removeFund(addedFound2);

		assertTrue(findPortOp.equals(portOp1));
	}

	@Test
	public void testFindAllPortOp() throws InputValidationException, InstanceNotFoundException, ParseException {

		FundPort fundPortfolio1 = getValidFundPort();

		fundPortfolio1.setpName("Test 1");

		fundService.addFundPortfolio(fundPortfolio1);

		FundDesc addedFound1 = this.getValidFundDesc();

		fundService.addFund(addedFound1);

		FundVl fundVl = getValidFundVl("2020-01-01", addedFound1);

		fundService.addFundVl(fundVl);

		fundService.addPortDesc(fundPortfolio1, addedFound1);

		PortOp portOp1 = getValidPortOp("2020-04-17", fundPortfolio1, addedFound1);
		PortOp portOp2 = getValidPortOp("2020-04-18", fundPortfolio1, addedFound1);
		PortOp portOp3 = getValidPortOp("2020-04-19", fundPortfolio1, addedFound1);
		PortOp portOp4 = getValidPortOp("2020-04-20", fundPortfolio1, addedFound1);

		fundService.addPortOp(portOp1);
		fundService.addPortOp(portOp2);
		fundService.addPortOp(portOp3);
		fundService.addPortOp(portOp4);

		boolean bool = true;

		if (!fundService.findAllPortOp(fundPortfolio1, addedFound1).get(0).equals(portOp1)) {
			bool = false;
		}

		if (fundService.findAllPortOp(fundPortfolio1, addedFound1).size() != 4) {
			bool = false;
		}

		fundService.removePortOp(portOp1);

		if (!fundService.findAllPortOp(fundPortfolio1, addedFound1).get(0).equals(portOp2)) {
			bool = false;
		}

		if (fundService.findAllPortOp(fundPortfolio1, addedFound1).size() != 3) {
			bool = false;
		}

		fundService.removePortOp(portOp2);

		if (!fundService.findAllPortOp(fundPortfolio1, addedFound1).get(0).equals(portOp3)) {
			bool = false;
		}

		if (fundService.findAllPortOp(fundPortfolio1, addedFound1).size() != 2) {
			bool = false;
		}

		fundService.removePortOp(portOp3);

		if (!fundService.findAllPortOp(fundPortfolio1, addedFound1).get(0).equals(portOp4)) {
			bool = false;
		}

		if (fundService.findAllPortOp(fundPortfolio1, addedFound1).size() != 1) {
			bool = false;
		}

		fundService.removePortOp(portOp4);

		if (fundService.findAllPortOp(fundPortfolio1, addedFound1).size() != 0) {
			bool = false;
		}

		fundService.removeFundPortfolio(fundPortfolio1);
		fundService.removeFund(addedFound1);

		assertTrue(bool);

	}

	@Test
	public void testFindAllPortOpByRange() throws InputValidationException, InstanceNotFoundException, ParseException {

		FundPort fundPortfolio1 = getValidFundPort();

		fundPortfolio1.setpName("Test 1");

		fundService.addFundPortfolio(fundPortfolio1);

		FundDesc addedFound1 = this.getValidFundDesc();

		fundService.addFund(addedFound1);

		FundVl fundVl = getValidFundVl("2020-01-01", addedFound1);

		fundService.addFundVl(fundVl);

		fundService.addPortDesc(fundPortfolio1, addedFound1);

		PortOp portOp1 = getValidPortOp("2020-04-17", fundPortfolio1, addedFound1);
		PortOp portOp2 = getValidPortOp("2020-04-18", fundPortfolio1, addedFound1);
		PortOp portOp3 = getValidPortOp("2020-04-19", fundPortfolio1, addedFound1);
		PortOp portOp4 = getValidPortOp("2020-04-20", fundPortfolio1, addedFound1);

		fundService.addPortOp(portOp1);
		fundService.addPortOp(portOp2);
		fundService.addPortOp(portOp3);
		fundService.addPortOp(portOp4);

		boolean bool = true;

		if (!fundService.findAllPortOpbyRange(fundPortfolio1, addedFound1, LocalDate.parse("2020-04-17"),
				LocalDate.parse("2020-04-17")).get(0).equals(portOp1)) {
			bool = false;
		}

		if (fundService.findAllPortOpbyRange(fundPortfolio1, addedFound1, LocalDate.parse("2020-04-17"),
				LocalDate.parse("2020-04-20")).size() != 4) {
			bool = false;
		}

		if (!fundService.findAllPortOpbyRange(fundPortfolio1, addedFound1, LocalDate.parse("2020-04-18"),
				LocalDate.parse("2020-04-18")).get(0).equals(portOp2)) {
			bool = false;
		}

		if (fundService.findAllPortOpbyRange(fundPortfolio1, addedFound1, LocalDate.parse("2020-04-18"),
				LocalDate.parse("2020-04-20")).size() != 3) {
			bool = false;
		}

		if (fundService.findAllPortOpbyRange(fundPortfolio1, addedFound1, LocalDate.parse("2020-04-23"),
				LocalDate.parse("2020-04-27")).size() != 0) {
			bool = false;
		}

		fundService.removeFundPortfolio(fundPortfolio1);
		fundService.removeFund(addedFound1);

		assertTrue(bool);

	}

	@Test
	public void testFindLatestPortOp() throws InputValidationException, ParseException, InstanceNotFoundException {

		FundPort fundPortfolio1 = getValidFundPort();

		fundPortfolio1.setpName("Test 1");

		fundService.addFundPortfolio(fundPortfolio1);

		FundDesc addedFound1 = this.getValidFundDesc();

		fundService.addFund(addedFound1);

		FundVl fundVl = getValidFundVl("2020-01-01", addedFound1);

		fundService.addFundVl(fundVl);

		fundService.addPortDesc(fundPortfolio1, addedFound1);

		PortOp portOp1 = getValidPortOp("2020-04-17", fundPortfolio1, addedFound1);
		PortOp portOp2 = getValidPortOp("2020-04-20", fundPortfolio1, addedFound1);
		PortOp portOp3 = getValidPortOp("2020-04-23", fundPortfolio1, addedFound1);

		fundService.addPortOp(portOp1);
		fundService.addPortOp(portOp2);
		fundService.addPortOp(portOp3);

		boolean bool = true;

		if (!fundService.findLatestPortOp(fundPortfolio1, addedFound1, LocalDate.parse("2020-04-19")).equals(portOp1)) {
			bool = false;
		}

		if (!fundService.findLatestPortOp(fundPortfolio1, addedFound1, LocalDate.parse("2020-04-22")).equals(portOp2)) {
			bool = false;
		}

		if (!fundService.findLatestPortOp(fundPortfolio1, addedFound1, LocalDate.parse("2020-04-27")).equals(portOp3)) {
			bool = false;
		}

		fundService.removeFundPortfolio(fundPortfolio1);
		fundService.removeFund(addedFound1);

		assertTrue(bool);

	}

	@Test(expected = InputValidationException.class)
	public void testAddInvalidPortOp() throws InputValidationException, ParseException, InstanceNotFoundException {

		FundPort fundPortfolio1 = getValidFundPort();

		fundPortfolio1.setpName("Test 1");

		fundService.addFundPortfolio(fundPortfolio1);

		FundDesc addedFound1 = this.getValidFundDesc();

		fundService.addFund(addedFound1);

		fundService.addPortDesc(fundPortfolio1, addedFound1);

		PortOp portOp1 = getValidPortOp("2020-04-30", fundPortfolio1, addedFound1);

		try {
			fundService.addPortOp(portOp1);
			fundService.addPortOp(portOp1);
		} catch (InputValidationException e) {
			throw e;
		} finally {

			fundService.removeFundPortfolio(fundPortfolio1);
			fundService.removeFund(addedFound1);

		}

	}

	@Test(expected = InputValidationException.class)
	public void testAddPortOpOfUnexistentFund()
			throws InputValidationException, ParseException, InstanceNotFoundException {

		FundPort fundPortfolio1 = getValidFundPort();

		fundService.addFundPortfolio(fundPortfolio1);

		FundDesc addedFound1 = this.getValidFundDesc();

		fundService.addFund(addedFound1);

		fundService.addPortDesc(fundPortfolio1, addedFound1);

		fundService.removeFund(addedFound1);

		PortOp portOp1 = getValidPortOp("2020-04-30", fundPortfolio1, addedFound1);

		try {
			fundService.addPortOp(portOp1);
		} catch (InputValidationException e) {
			throw e;
		} finally {

			fundService.removeFundPortfolio(fundPortfolio1);

		}

	}

	@Test(expected = InstanceNotFoundException.class)
	public void testFindUnexistentPortOp() throws InputValidationException, ParseException, InstanceNotFoundException {

		FundPort fundPortfolio1 = getValidFundPort();

		fundService.addFundPortfolio(fundPortfolio1);

		FundDesc addedFound1 = this.getValidFundDesc();

		fundService.addFund(addedFound1);

		fundService.addPortDesc(fundPortfolio1, addedFound1);

		PortOp portOp1 = getValidPortOp("2020-04-30", fundPortfolio1, addedFound1);

		fundService.addPortOp(portOp1);

		fundService.removePortOp(portOp1);

		try {
			fundService.findPortOp(fundPortfolio1, addedFound1, LocalDate.parse("2020-04-30"));
		} catch (InstanceNotFoundException e) {
			throw e;
		} finally {

			fundService.removeFundPortfolio(fundPortfolio1);
			fundService.removeFund(addedFound1);

		}
	}

	@Test
	public void testCalculatePortOp() throws InputValidationException, ParseException, InstanceNotFoundException {

		//REVISAR BIEN ESTE TEST ESTA MAL
		
		FundPort fundPortfolio1 = getValidFundPort();

		fundService.addFundPortfolio(fundPortfolio1);

		FundDesc addedFound1 = this.getValidFundDesc();

		fundService.addFund(addedFound1);

		fundService.addPortDesc(fundPortfolio1, addedFound1);

		PortOp portOp1 = getValidPortOp("2020-04-30", fundPortfolio1, addedFound1);

		fundService.addPortOp(portOp1);

		PortOp findPortOp = fundService.findPortOp(fundPortfolio1, addedFound1, LocalDate.parse("2020-04-30"));

		// System.out.println(findPortOp.getDay());
		// System.out.println(findPortOp.getfPartini());
		// System.out.println(findPortOp.getfPartfin());
		// System.out.println(findPortOp.getfPartOp());
		// System.out.println(findPortOp.getfPrice());

		boolean bool = true;

		if (findPortOp.getfPartini() != 0) {
			bool = false;
		}

		if (findPortOp.getfPartfin() != 100) {
			bool = false;
		}

		if (findPortOp.getfPartOp() != 100) {
			bool = false;
		}

		if (findPortOp.getfPrice() != 2525.0) {
			bool = false;
		}

		PortOp portOp2 = getValidPortOp("2020-07-22", fundPortfolio1, addedFound1);

		portOp2.setfPartOp(-100);

		fundService.addPortOp(portOp2);

		PortOp findPortOp2 = fundService.findPortOp(fundPortfolio1, addedFound1, LocalDate.parse("2020-07-22"));

		if (findPortOp2.getfPartini() != 100) {
			bool = false;
		}

		if (findPortOp2.getfPartfin() != 0) {
			bool = false;
		}

		if (findPortOp2.getfPartOp() != -100) {
			bool = false;
		}

		if (findPortOp2.getfPrice() != -2450.0) {
			bool = false;
		}
		
		fundService.removeFundPortfolio(fundPortfolio1);
		fundService.removeFund(addedFound1);
		assertTrue(bool);
	}

}
