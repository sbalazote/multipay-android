package com.mercadopago.model;

import android.content.Context;
import android.text.TextUtils;

import com.mercadopago.R;
import com.mercadopago.core.MercadoPago;

import java.util.Calendar;
import java.util.Locale;

public class CardToken {

    private final static Calendar now = Calendar.getInstance();
    public static final int MIN_LENGTH_NUMBER = 10;
    public static final int MAX_LENGTH_NUMBER = 19;

    private Cardholder cardholder;
    private String cardNumber;
    private Device device;
    private Integer expirationMonth;
    private Integer expirationYear;
    private String securityCode;

    public CardToken(String cardNumber, Integer expirationMonth, Integer expirationYear,
                     String securityCode, String cardholderName, String identificationType, String identificationNumber) {
        this.cardNumber = normalizeCardNumber(cardNumber);
        this.expirationMonth = expirationMonth;
        this.expirationYear = normalizeYear(expirationYear);
        this.securityCode = securityCode;
        this.cardholder = new Cardholder();
        this.cardholder.setName(cardholderName);
        Identification identification = new Identification();
        identification.setNumber(identificationNumber);
        identification.setType(identificationType);
        this.cardholder.setIdentification(identification);
    }

    public Cardholder getCardholder() {
        return cardholder;
    }

    public void setCardholder(Cardholder cardholder) {
        this.cardholder = cardholder;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Context context) {
        this.device = new Device(context);
    }

    public Integer getExpirationMonth() {
        return expirationMonth;
    }

    public void setExpirationMonth(Integer expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    public Integer getExpirationYear() {
        return expirationYear;
    }

    public void setExpirationYear(Integer expirationYear) {
        this.expirationYear = expirationYear;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public boolean validate(boolean includeSecurityCode) {
        boolean result = validateCardNumber() && validateExpiryDate() && validateIdentification() && validateCardholderName();
        if (includeSecurityCode) {
            result = result && validateSecurityCode();
        }
        return result;
    }

    public boolean validateCardNumber() {
        return !TextUtils.isEmpty(cardNumber) && (cardNumber.length() > MIN_LENGTH_NUMBER) && (cardNumber.length() < MAX_LENGTH_NUMBER);
    }

    public void validateCardNumber(Context context, PaymentMethod paymentMethod) throws Exception {

        // Empty field
        if (TextUtils.isEmpty(cardNumber)) {
            throw new Exception(context.getString(R.string.mpsdk_invalid_empty_card));
        }

        Setting setting = Setting.getSettingByBin(paymentMethod.getSettings(), (cardNumber.length() >= MercadoPago.BIN_LENGTH ? cardNumber.substring(0, MercadoPago.BIN_LENGTH) : ""));

        if (setting == null) {

            // Invalid bin
            throw new Exception(context.getString(R.string.mpsdk_invalid_card_bin));

        } else {

            // Validate card length
            int cardLength = setting.getCardNumber().getLength();
            if (cardNumber.trim().length() != cardLength) {
                throw new Exception(context.getString(R.string.mpsdk_invalid_card_length, cardLength));
            }

            // Validate luhn
            String luhnAlgorithm = setting.getCardNumber().getValidation();
            if (("standard".equals(luhnAlgorithm)) && (!checkLuhn(cardNumber))) {
                throw new Exception(context.getString(R.string.mpsdk_invalid_card_luhn));
            }
        }
    }

    public boolean validateSecurityCode() {

        return validateSecurityCode(securityCode);
    }

    public static boolean validateSecurityCode(String securityCode) {

        return securityCode == null || (!TextUtils.isEmpty(securityCode) && securityCode.length() >= 3 && securityCode.length() <= 4);
    }

    public void validateSecurityCode(Context context, PaymentMethod paymentMethod) throws Exception {

        validateSecurityCode(context, securityCode, paymentMethod, (((cardNumber != null) ? cardNumber.length() : 0) >= MercadoPago.BIN_LENGTH ? cardNumber.substring(0, MercadoPago.BIN_LENGTH) : ""));
    }

    public static void validateSecurityCode(Context context, String securityCode, PaymentMethod paymentMethod, String bin) throws Exception {

        if (paymentMethod != null) {
            Setting setting = Setting.getSettingByBin(paymentMethod.getSettings(), bin);

            // Validate security code length
            if (setting != null) {
                int cvvLength = setting.getSecurityCode().getLength();
                if ((cvvLength != 0) && (securityCode.trim().length() != cvvLength)) {
                    throw new Exception(context.getString(R.string.mpsdk_invalid_cvv_length, cvvLength));
                }
            } else {
                throw new Exception(context.getString(R.string.mpsdk_invalid_field));
            }
        }
    }

    public boolean validateExpiryDate() {

        return validateExpiryDate(expirationMonth, expirationYear);
    }

    public static boolean validateExpiryDate(Integer month, Integer year) {

        if (!validateExpMonth(month)) {
            return false;
        }
        if (!validateExpYear(year)) {
            return false;
        }
        return !hasMonthPassed(month, year);
    }

    public static boolean validateExpMonth(Integer month) {

        return (month == null) ? false : (month >= 1 && month <= 12);
    }

    public static boolean validateExpYear(Integer year) {

        return (year == null) ? false : !hasYearPassed(year);
    }

    public boolean validateIdentification() {
        return validateIdentificationType() && validateIdentificationNumber();
    }

    public boolean validateIdentificationType() {
        return (cardholder == null) ? false :
                (cardholder.getIdentification() == null) ? false :
                        !TextUtils.isEmpty(cardholder.getIdentification().getType());
    }

    public boolean validateIdentificationNumber() {
        return (cardholder == null) ? false :
                (cardholder.getIdentification() == null) ? false :
                        (!validateIdentificationType()) ? false :
                                !TextUtils.isEmpty(cardholder.getIdentification().getNumber());
    }

    public boolean validateIdentificationNumber(IdentificationType identificationType){

        if (identificationType != null) {
            if ((cardholder != null) &&
                    (cardholder.getIdentification() != null) &&
                    (cardholder.getIdentification().getNumber() != null)) {
                int len = cardholder.getIdentification().getNumber().length();
                Integer min = identificationType.getMinLength();
                Integer max = identificationType.getMaxLength();
                if ((min != null) && (max != null)) {
                    return ((len <= max) && (len >= min));
                } else {
                    return validateIdentificationNumber();
                }
            } else {
                return false;
            }
        } else {
            return validateIdentificationNumber();
        }
    }

    public boolean validateCardholderName(){
        return (cardholder == null) ? false :
                !TextUtils.isEmpty(cardholder.getName());
    }

    public static boolean checkLuhn(String cardNumber) {

        int sum = 0;
        boolean alternate = false;
        if ((cardNumber == null) || (cardNumber.length() == 0)) {
            return false;
        }
        for (int i = cardNumber.length() - 1; i >= 0; i--)
        {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate)
            {
                n *= 2;
                if (n > 9)
                {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }

    private static boolean hasYearPassed(int year) {
        int normalized = normalizeYear(year);
        return normalized < now.get(Calendar.YEAR);
    }

    private static boolean hasMonthPassed(int month, int year) {
        return hasYearPassed(year) || normalizeYear(year) == now.get(Calendar.YEAR) && month < (now.get(Calendar.MONTH) + 1);
    }

    private static Integer normalizeYear(Integer year)  {
        if ((year != null) && (year < 100 && year >= 0)) {
            String currentYear = String.valueOf(now.get(Calendar.YEAR));
            String prefix = currentYear.substring(0, currentYear.length() - 2);
            year = Integer.parseInt(String.format(Locale.US, "%s%02d", prefix, year));
        }
        return year;
    }

    private String normalizeCardNumber(String number) {
        return number.trim().replaceAll("\\s+|-", "");
    }
}
