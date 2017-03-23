package tfg.app.model.service;

import java.time.LocalDate;
import java.util.List;

import tfg.app.model.entities.FundDesc;
import tfg.app.model.entities.FundPort;
import tfg.app.model.entities.FundVl;
import tfg.app.model.entities.PortOp;
import tfg.app.util.exceptions.InputValidationException;
import tfg.app.util.exceptions.InstanceNotFoundException;

public interface FundService {

	// ################################################################
	// # 															  #
	// # Métodos de objetos FundDesc 								  #
	// # 															  #
	// ################################################################

	// Añade un FundDesc y toda su lista de FundVLs a la base de datos
	public void addFund(FundDesc fundDesc) throws InputValidationException;

	// Devuelve el FundDesc y su lista de FundVls a partir de su ISIN
	public FundDesc findFund(String fundId) throws InstanceNotFoundException;

	// Actualiza los campos de un FundDesc
	public void updateFund(FundDesc fundDesc) throws InputValidationException;

	// Elimina un FundDesc y toda su lista de FundVls de la base de datos
	public void removeFund(FundDesc fundDesc) throws InstanceNotFoundException;

	// Obtiene todos los FundDesc de la base de datos.
	public List<FundDesc> findAllFunds();

	// Obtiene los fundDesc que cohinciden con una serie de cáracteres en algúno
	// de sus campos
	public List<FundDesc> findFundsByKeywords(String keywords);

	// ################################################################
	// # 															  #
	// # Métodos de objetos FundVl 									  #
	// # 															  #
	// ################################################################

	// Añade un unico FundVl a un fondo en un día concreto
	public void addFundVl(FundVl fundVl) throws InputValidationException;

	// Actualiza un unico FundVl de un fondo en un día concreto
	public void updateFundVl(FundVl fundVl) throws InputValidationException, InstanceNotFoundException;

	// Obtiene el FundVl de un fondo en un dia concreto
	public FundVl findFundVl(FundDesc fundDesc, LocalDate day) throws InstanceNotFoundException;

	// Obtiene los Vl de un fondo dado en el intervalo de tiempo deseado
	public List<FundVl> findFundVlbyRange(FundDesc fundDesc, LocalDate startDay, LocalDate endDay);

	// Elimina una fila de la tabla vl de un fondo en un dia concreto
	public void removeFundVl(FundDesc fundDesc, LocalDate day) throws InstanceNotFoundException;
	
	//Obtiene el vl del dia mas proximo a un dia dado (Se comporta exactamente igual a findFundVl si existe un valor
	//vl en ese dia)
	public FundVl findClosestFundVl(FundDesc fundDesc, LocalDate day) throws InstanceNotFoundException;
	

	// ################################################################
	// # 															  #
	// # Métodos de objetos FundPort    							  #
	// # 															  #
	// ################################################################

	//Añade una cartera de fondos
	public void addFundPortfolio(FundPort fundPortfolio) throws InputValidationException;

	//Elimina una cartera de fondos
	public void removeFundPortfolio(FundPort fundPortfolio) throws InstanceNotFoundException;
	
	//Obtiene una cartera de fondos a partir de su Id
	public FundPort findFundPortfolio(Long pId) throws InstanceNotFoundException;

	//Actualiza los campos de una cartera de fondos
	public void updateFundPortfolio(FundPort fundPortfolio) throws InstanceNotFoundException, InputValidationException;
	
	//Obtiene Todas las carteras de la base de datos
	public List<FundPort> findAllFundPortfolios();
	
	//Obtiene todos los fondos de una cartera 
	public List<FundDesc> findFundsOfPortfolio(FundPort fundPortfolio) throws InstanceNotFoundException;

	// ################################################################
	// # 															  #
	// # Métodos de objetos PortDesc    							  #
	// # 															  #
	// ################################################################	
	
	//Añade un fondo a una cartera 
	public void addPortDesc(FundDesc fundDesc, FundPort fundPort) throws InstanceNotFoundException, InputValidationException;
	
	//Elimina un fondo de una cartera
	public void removePortDesc( FundDesc fundDesc, FundPort fundPort) throws InstanceNotFoundException;
	
	// ################################################################
	// # 															  #
	// # Métodos de objetos PortOP	    							  #
	// # 															  #
	// ################################################################	
	
	//Al añadir una operacion sobre un fondo en un dia determinado, si ese dia no se encuentra en la tabla de vl
	//se selecciona para calcular el VL de cada participacion el día más próximo al dado.
	
	//Añade una operacion (Con participaciones como unidad) sobre un fondo en una cartera en un día determinado
	public void addPortOp(FundDesc fundDesc, FundPort fundPort, LocalDate day, Integer partOp);
	
	//Añade una operacion (Con dinero como unidad) sobre un fondo en una cartera en un día determinado
	public void addPortOp(FundDesc fundDesc, FundPort fundPort, LocalDate day, Double partOp);
	
	//Actualiza un PortOp(Con participaciones como unidad) sobre un fondo en una cartera en un día determinado
	public PortOp UpdatePortOp(FundDesc fundDesc, FundPort fundPort, LocalDate day, Integer partOp);
	
	//Actualiza un PortOp(Con dinero como unidad) sobre un fondo en una cartera en un día determinado
	public PortOp UpdatePortOp(FundDesc fundDesc, FundPort fundPort, LocalDate day, Double partOp);
	
	//Obtiene una operacion sobre un fondo en una cartera en un día determinado
	public PortOp findPortOp(FundDesc fundDesc, FundPort fundPort, LocalDate day);
	
	//Devuelve todas las operaciones realizadas sobre un fondo en una cartera
	public List<PortOp> findAllPortOp(FundDesc fundDesc, FundPort fundPort, LocalDate day);
	
	//Devuelve todas las operaciones realizadas sobre un fondo en una cartera entre dos fechas
	public List<PortOp> findAllPortOpbyRange(FundDesc fundDesc, FundPort fundPort, LocalDate start, LocalDate end);
	
	//Elimina una operacion realizada sobre un fondo en una cartera en una fecha
	public void removePortOp( FundDesc fundDesc, FundPort fundPort, LocalDate day) throws InstanceNotFoundException;

}