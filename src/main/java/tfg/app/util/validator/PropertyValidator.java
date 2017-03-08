package tfg.app.util.validator;

import tfg.app.util.exceptions.InputValidationException;

public final class PropertyValidator {

	private PropertyValidator() {
	}

	public static void validateNotNegativeDouble(Double doubleValue) throws InputValidationException {

		if (doubleValue < 0) {
			throw new InputValidationException("Vl inválido (Debe ser mayor que 0): " + doubleValue);
		}

	}

	public static void validateMandatoryString(String propertyName, String stringValue)
			throws InputValidationException {

		if ((stringValue == null) || (stringValue.length() == 0)) {
			throw new InputValidationException(
					propertyName + " inválido (no puede ser null ni una cadena vacía): " + stringValue);
		}

	}

	public static void validateIsin(String isin) throws InputValidationException {

		isin = isin.trim().toUpperCase();

		if (!isin.matches("^[A-Z]{2}[A-Z0-9]{9}\\d$"))
			throw new InputValidationException(" El formato del ISIN proporcionado: " + isin + " no es válido.");

		StringBuilder sb = new StringBuilder();
		for (char c : isin.substring(0, 12).toCharArray())
			sb.append(Character.digit(c, 36));

		if (!isinTest(sb.toString())) {
			throw new InputValidationException(" El valor del ISIN proporcionado: " + isin + " no es válido.");
		}
	}

	private static boolean isinTest(String number) {
		int s1 = 0, s2 = 0;
		String reverse = new StringBuffer(number).reverse().toString();
		for (int i = 0; i < reverse.length(); i++) {
			int digit = Character.digit(reverse.charAt(i), 10);
			if (i % 2 == 0) {
				s1 += digit;
			} else {
				s2 += 2 * digit;
				if (digit >= 5) {
					s2 -= 9;
				}
			}
		}
		return (s1 + s2) % 10 == 0;
	}
}
