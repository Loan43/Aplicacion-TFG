package tfg.app.model;

import java.time.LocalDate;
import java.util.List;

import tfg.app.util.exceptions.InputValidationException;
import tfg.app.util.exceptions.InstanceNotFoundException;

public interface FundService {

	// Añade un FundDesc y toda su lista de FundVLs a la base de datos
	public void addFund(FundDesc fundDesc) throws InputValidationException;

	// Actualiza los campos de un FundDesc y actualiza o inserta los valores de
	// su lista de FundVls
	public void updateFund(FundDesc fundDesc) throws InputValidationException, InstanceNotFoundException;

	// Elimina un FundDesc y toda su lista de FundVls de la base de datos
	public void removeFund(String fundId) throws InstanceNotFoundException;

	// Devuelve el FundDesc y su lista de FundVls a partir de su ID
	public FundDesc findFund(String fundId) throws InstanceNotFoundException;

	// Obtiene todos los FundDesc de la base de datos.
	public List<FundDesc> findAllFunds();

	// Obtiene el vl de un fondo en un dia concreto
	public Double findFundVl(String fundId, LocalDate day) throws InstanceNotFoundException;

}