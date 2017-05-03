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

	/**
     *  Añade un FundDesc y toda su lista de FundVLs a la base de datos
     *
     * @param
     */
	public void addFund(FundDesc fundDesc) throws InputValidationException;

	/**
     *  Devuelve el FundDesc y su lista de FundVls a partir de su ISIN
     *
     * @param
     */
	public FundDesc findFund(String fundId) throws InstanceNotFoundException;

	/**
     *  Actualiza los campos de un FundDesc
     *
     * @param
     */
	public void updateFund(FundDesc fundDesc) throws InputValidationException;

	/**
     *  Elimina un FundDesc y toda su lista de FundVls de la base de datos
     *
     * @param
     */
	public void removeFund(FundDesc fundDesc) throws InstanceNotFoundException;

	/**
     *  Obtiene todos los FundDesc de la base de datos.
     *
     * @param
     */
	public List<FundDesc> findAllFunds();

	/**
     *  Obtiene los fundDesc que cohinciden con una serie de cáracteres en algúno de sus campos
     *
     * @param
     */
	public List<FundDesc> findFundsByKeywords(String keywords);

	// ################################################################
	// # 															  #
	// # Métodos de objetos FundVl 									  #
	// # 															  #
	// ################################################################

	/**
     *  Añade un unico FundVl a un fondo en un día concreto
     *
     * @param
     */
	public void addFundVl(FundVl fundVl) throws InputValidationException;

	/**
     *  Actualiza un unico FundVl de un fondo en un día concreto
     *
     * @param
     */
	public void updateFundVl(FundVl fundVl) throws InputValidationException, InstanceNotFoundException;

	/**
     *  Obtiene el FundVl de un fondo en un dia concreto
     *
     * @param
     */
	public FundVl findFundVl(FundDesc fundDesc, LocalDate day) throws InstanceNotFoundException;

	/**
     *  Obtiene los Vl de un fondo dado en el intervalo de tiempo deseado
     *
     * @param
     */
	public List<FundVl> findFundVlbyRange(FundDesc fundDesc, LocalDate startDay, LocalDate endDay);

	/**
     *  Elimina una fila de la tabla vl de un fondo en un dia concreto
     *
     * @param
     */
	public void removeFundVl(FundVl fundVl) throws InstanceNotFoundException;
	
	/**
     *  Obtiene el vl del dia mas proximo a un dia dado (Se comporta exactamente igual a findFundVl si existe un valor vl en ese dia)
     *
     * @param
     */
	public FundVl findLatestFundVl(FundDesc fundDesc, LocalDate day);
	

	// ################################################################
	// # 															  #
	// # Métodos de objetos FundPort    							  #
	// # 															  #
	// ################################################################

	/**
     * Añade una cartera de fondos
     *
     * @param
     */
	public void addFundPortfolio(FundPort fundPortfolio) throws InputValidationException;

	/**
     *  Elimina una cartera de fondos
     *
     * @param
     */
	public void removeFundPortfolio(FundPort fundPortfolio) throws InstanceNotFoundException;
	
	/**
     *  Obtiene una cartera de fondos a partir de su Id
     *
     * @param
     */
	public FundPort findFundPortfolio(Long pId) throws InstanceNotFoundException;

	/**
     *  Actualiza los campos de una cartera de fondos
     *
     * @param
     */
	public void updateFundPortfolio(FundPort fundPortfolio) throws InstanceNotFoundException, InputValidationException;
	
	/**
     * Obtiene Todas las carteras de la base de datos
     *
     * @param
     */
	public List<FundPort> findAllFundPortfolios();
	
	/**
     *  Obtiene todos los fondos de una cartera 
     *
     * @param
     */
	public List<FundDesc> findFundsOfPortfolio(FundPort fundPortfolio) throws InstanceNotFoundException;

	// ################################################################
	// # 															  #
	// # Métodos de objetos PortDesc    							  #
	// # 															  #
	// ################################################################	
	
	/**
     *  Añade un fondo a una cartera
     *
     * @param
     */
	public void addPortDesc(FundPort fundPort, FundDesc fundDesc) throws InstanceNotFoundException, InputValidationException;
	
	/**
     *  Elimina un fondo de una cartera
     *
     * @param
     */
	public void removePortDesc( FundPort fundPort, FundDesc fundDesc) throws InstanceNotFoundException;
	
	// ################################################################
	// # 															  #
	// # Métodos de objetos PortOP	    							  #
	// # 															  #
	// ################################################################	
	
	//Al añadir una operacion sobre un fondo en un dia determinado, si ese dia no se encuentra en la tabla de vl
	//se selecciona para calcular el VL de cada participacion el día más próximo al dado.
	
	/**
     *  Añade una operacion (Con participaciones como unidad) sobre un fondo en una cartera en un día determinado
     *
     * @param
     */
	public void addPortOp(PortOp portOp) throws InputValidationException;
	
	/**
     *  Elimina una operacion realizada sobre un fondo en una cartera en una fecha
     *
     * @param
     */
	public void removePortOp(PortOp portOp) throws InstanceNotFoundException, InputValidationException;
	
	/**
     *  Actualiza un PortOp(Con participaciones como unidad) sobre un fondo en una cartera en un día determinado
     *
     * @param
     */
	public void UpdatePortOp(PortOp portOp) throws InputValidationException;
	
	/**
     *  Obtiene una operacion sobre un fondo en una cartera en un día determinado
     *
     * @param
     */
	public PortOp findPortOp(FundPort fundPort, FundDesc fundDesc, LocalDate day) throws InstanceNotFoundException;
	
	/**
     *  Devuelve todas las operaciones realizadas sobre un fondo en una cartera
     *
     * @param
     */
	public List<PortOp> findAllPortOp(FundPort fundPort, FundDesc fundDesc) throws InstanceNotFoundException;
	
	/**
     *  Devuelve todas las operaciones realizadas sobre un fondo en una cartera entre dos fechas
     *
     * @param
     */
	public List<PortOp> findAllPortOpbyRange(FundPort fundPort, FundDesc fundDesc, LocalDate startDay, LocalDate endDay, int flag) throws InstanceNotFoundException;
	
	/**
     *  Obtiene el la operacion sobre un fondo en una cartera del dia mas proximo a un dia dado (Se comporta exactamente igual a findPortOp si existe un PortOp en ese dia)
     *
     * @param
     */
	public PortOp findLatestPortOp(FundPort fundPort, FundDesc fundDesc, LocalDate day) throws InstanceNotFoundException;
	

}