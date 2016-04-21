package com.mercadopago.model;

import com.mercadopago.VaultActivity;
import com.mercadopago.test.BaseTest;
import com.mercadopago.test.StaticMock;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AllModelTest extends BaseTest<VaultActivity> {

    public AllModelTest() {
        super(VaultActivity.class);
    }

    public void testAddress() {

        Address address = new Address();
        address.setStreetName("abcd");
        address.setStreetNumber(Long.parseLong("100"));
        address.setZipCode("1000");
        assertTrue(address.getStreetName().equals("abcd"));
        assertTrue(Long.toString(address.getStreetNumber()).equals("100"));
        assertTrue(address.getZipCode().equals("1000"));
    }

    public void testPayment() {

        Payment payment = new Payment();
        payment.setBinaryMode(true);
        payment.setCallForAuthorizeId("123");
        payment.setCaptured(false);
        payment.setCard(StaticMock.getCard(getApplicationContext()));
        payment.setCollectorId("1234567");
        payment.setCouponAmount(new BigDecimal("19"));
        payment.setCurrencyId("ARS");
        payment.setDateApproved(getDummyDate("2015-01-01"));
        payment.setDateCreated(getDummyDate("2015-01-02"));
        payment.setDateLastUpdated(getDummyDate("2015-01-03"));
        payment.setDescription("some desc");
        payment.setDifferentialPricingId(Long.parseLong("789"));
        payment.setExternalReference("some ext ref");
        payment.setFeeDetails(StaticMock.getPayment(getApplicationContext()).getFeeDetails());
        payment.setId(Long.parseLong("123456"));
        payment.setInstallments(3);
        payment.setIssuerId(3);
        payment.setLiveMode(true);
        payment.setMetadata(null);
        payment.setMoneyReleaseDate(getDummyDate("2015-01-04"));
        payment.setNotificationUrl("http://some_url.com");
        payment.setOperationType(StaticMock.getPayment(getApplicationContext()).getOperationType());
        payment.setOrder(StaticMock.getPayment(getApplicationContext()).getOrder());
        payment.setPayer(StaticMock.getPayment(getApplicationContext()).getPayer());
        payment.setPaymentMethodId("visa");
        payment.setPaymentTypeId("credit_card");
        payment.setRefunds(null);
        payment.setStatementDescriptor("statement");
        payment.setStatus("approved");
        payment.setStatusDetail("accredited");
        payment.setTransactionAmount(new BigDecimal("10.50"));
        payment.setTransactionAmountRefunded(new BigDecimal("20.50"));
        payment.setTransactionDetails(StaticMock.getPayment(getApplicationContext()).getTransactionDetails());
        assertTrue(payment.getBinaryMode());
        assertTrue(payment.getCallForAuthorizeId().equals("123"));
        assertTrue(!payment.getCaptured());
        assertTrue(payment.getCard().getId().equals("149024476"));
        assertTrue(payment.getCollectorId().equals("1234567"));
        assertTrue(payment.getCouponAmount().toString().equals("19"));
        assertTrue(payment.getCurrencyId().equals("ARS"));
        assertTrue(validateDate(payment.getDateApproved(), "2015-01-01"));
        assertTrue(validateDate(payment.getDateCreated(), "2015-01-02"));
        assertTrue(validateDate(payment.getDateLastUpdated(), "2015-01-03"));
        assertTrue(payment.getDescription().equals("some desc"));
        assertTrue(Long.toString(payment.getDifferentialPricingId()).equals("789"));
        assertTrue(payment.getExternalReference().equals("some ext ref"));
        assertTrue(payment.getFeeDetails().get(0).getAmount().toString().equals("5.99"));
        assertTrue(Long.toString(payment.getId()).equals("123456"));
        assertTrue(Integer.toString(payment.getInstallments()).equals("3"));
        assertTrue(Long.toString(payment.getIssuerId()).toString().equals("3"));
        assertTrue(payment.getLiveMode());
        assertTrue(payment.getMetadata() == null);
        assertTrue(validateDate(payment.getMoneyReleaseDate(), "2015-01-04"));
        assertTrue(payment.getNotificationUrl().equals("http://some_url.com"));
        assertTrue(payment.getOperationType().equals("regular_payment"));
        assertTrue(payment.getOrder().getId() == null);
        assertTrue(payment.getPayer().getId().equals("178101336"));
        assertTrue(payment.getPaymentMethodId().equals("visa"));
        assertTrue(payment.getPaymentTypeId().equals("credit_card"));
        assertTrue(payment.getRefunds() == null);
        assertTrue(payment.getStatementDescriptor().equals("statement"));
        assertTrue(payment.getStatus().equals("approved"));
        assertTrue(payment.getStatusDetail().equals("accredited"));
        assertTrue(payment.getTransactionAmount().toString().equals("10.50"));
        assertTrue(payment.getTransactionAmountRefunded().toString().equals("20.50"));
        assertTrue(payment.getTransactionDetails().getTotalPaidAmount().toString().equals("100"));
    }

    private Date getDummyDate(String date) {

        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (Exception ex) {
            return null;
        }
    }

    private Boolean validateDate(Date date, String value) {

        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(value).equals(date);
        } catch (Exception ex) {
            return null;
        }
    }
}
