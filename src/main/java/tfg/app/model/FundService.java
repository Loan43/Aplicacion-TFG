package tfg.app.model;

import java.time.LocalDate;

import tfg.exceptions.InputValidationException;

public interface FundService {

	// Añade un FundDesc y toda su lista de FundVLs a la base de datos
	public void addFund(FundDesc fundDesc) throws InputValidationException;

	// Actualiza los campos de un FundDesc y actualiza o inserta los valores de
	// su lista de FundVls
	public void updateFund(FundDesc fundDesc) throws InputValidationException;

	// Elimina un FundDesc y toda su lista de FundVls de la base de datos
	public void removeFund(FundDesc fundDesc);

	// Devuelve el FundDesc y su lista de FundVls a partir de su ID
	public FundDesc findFund(Integer fundId);

	// Obtiene el vl de un fondo en un dia concreto
	public Double findFundVl(Integer fundId, LocalDate day);

}