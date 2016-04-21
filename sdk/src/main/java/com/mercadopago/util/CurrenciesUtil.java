package com.mercadopago.util;

import com.mercadopago.model.Currency;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Map;

public class CurrenciesUtil {

    private static Map<String, Currency> currenciesList = new HashMap<String, Currency>(){{
        put("ARS", new Currency("ARS", "Peso argentino", "$", 2, ",".charAt(0), ".".charAt(0)));
        put("BRL", new Currency("BRL", "Real", "R$", 2, ",".charAt(0), ".".charAt(0)));
        put("CLP", new Currency("CLP", "Peso chileno", "$", 0, ",".charAt(0), ".".charAt(0)));
        put("COP", new Currency("COP", "Peso colombiano", "$", 0, ",".charAt(0), ".".charAt(0)));
        put("MXN", new Currency("MXN", "Peso mexicano", "$", 2, ".".charAt(0), ",".charAt(0)));
        put("VEF", new Currency("VEF", "Bolivar fuerte", "BsF", 2, ",".charAt(0), ".".charAt(0)));

    }};

    public static String formatNumber(BigDecimal amount, String currencyId) {

        // Get currency configuration
        Currency currency = currenciesList.get(currencyId);

        if (currency != null) {

            // Set formatters
            DecimalFormatSymbols dfs = new DecimalFormatSymbols();
            dfs.setDecimalSeparator(currency.getDecimalSeparator());
            dfs.setGroupingSeparator(currency.getThousandsSeparator());
            DecimalFormat df = new DecimalFormat();
            df.setDecimalFormatSymbols(dfs);
            df.setMinimumFractionDigits(currency.getDecimalPlaces());
            df.setMaximumFractionDigits(currency.getDecimalPlaces());

            // return formatted string
            return currency.getSymbol() + " " + df.format(amount);

        } else {
            return null;
        }
    }
}
