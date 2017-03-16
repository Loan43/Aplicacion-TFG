package tfg.app.model.service;

import java.time.LocalDate;
import java.util.List;

import tfg.app.model.entities.FundDesc;
import tfg.app.model.entities.FundVl;
import tfg.app.util.exceptions.InputValidationException;
import tfg.app.util.exceptions.InstanceNotFoundException;

public interface FundService {
	
	//################################################################
	//#																 #
	//# 				Métodos de objetos FundDesc					 #
	//#																 #
	//################################################################
	
	// Añade un FundDesc y toda su lista de FundVLs a la base de datos
	public void addFund(FundDesc fundDesc) throws InputValidationException;

	// Devuelve el FundDesc y su lista de FundVls a partir de su ID
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

	//################################################################
	//#																 #
	//# 				Métodos de objetos FundVl					 #
	//#																 #
	//################################################################

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
	
	
}