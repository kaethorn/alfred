package de.wasenweg.alfred.scanner;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class MetaDataReaderUtil {

  public static String mapPosition(final String number) throws InvalidIssueNumberException {
    String convertableNumber = number;
    if ("½".equals(number) || "1/2".equals(number)) {
      convertableNumber = "0.5";
    }
    if (number.endsWith("a")) {
      convertableNumber = convertableNumber.replace("a", ".5");
    }
    BigDecimal position = new BigDecimal(0);
    try {
      position = new BigDecimal(convertableNumber);
    } catch (final Exception exception) {
      throw new InvalidIssueNumberException(number);
    }
    final String result = new DecimalFormat("0000.0").format(position);
    return result;
  }
}
