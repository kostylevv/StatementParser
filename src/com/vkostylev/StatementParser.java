package com.vkostylev;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class StatementParser {
    private boolean configOk = false;
    private String delim = null;
    private int accountIdIndex = -1;
    private int dateIndex = -1;
    private int amountIndex = -1;
    private int operationTypeIndex = -1;
    private int operationDescriptionIndex = -1;

    private static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }



    private void findTerm(String word, int index) {
        switch (word.strip().toLowerCase()) {
            case "rch":
            case "cчет выписки":
            case "\uFEFFномер счета":
                accountIdIndex = index;
                break;
            case "дата учета операции по счету":
            case "date_oper":
            case "дата авторизации":
                dateIndex = index;
                break;
            case "сумма док-та":
            case "sum_rur":
            case "сумма в валюте счета":
                amountIndex = index;
                break;
            case "тип операции (пополнение/списание)":
            case "d_c":
            case "признак дебет/кредит":
                operationTypeIndex = index;
                break;
            case "назначение платежа":
            case "text70":
                operationDescriptionIndex = index;
                break;
        }
    }

    private void configure(String configString) {
        if (configString.contains(";")) {
            delim = ";";
        } else {
            delim = "\\t";
        }

        String[] words = configString.split(delim);

        for (int i = 0; i < words.length; i++) {
            findTerm(words[i], i);
        }

        System.out.println(delim + ", " + accountIdIndex  + ", " + dateIndex  + ", " + amountIndex +
                ", " + operationTypeIndex  + ", " + operationDescriptionIndex );

        if (!configOk) {
            if (delim != null && accountIdIndex > -1 && dateIndex > -1 && amountIndex > -1 && operationTypeIndex > -1 && operationDescriptionIndex > -1) {
                configOk = true;

                System.out.println(configOk);
            }
        }

    }

    private BankTransaction parseTransaction(String statementString) {
        BankTransaction transaction = new BankTransaction();
        String[] statementWords = statementString.split(delim);

        transaction.setAccountId(statementWords[accountIdIndex]);
        transaction.setTransactionDescription(statementWords[operationDescriptionIndex]);
        System.out.println("amm="+statementWords[amountIndex]);

        if (statementWords[amountIndex].contains("\"")) {
            statementWords[amountIndex] = statementWords[amountIndex].substring(1,statementWords[amountIndex].length()-1);
        }
        Double rawAmount = Double.valueOf(statementWords[amountIndex].replace(",","."));


        transaction.setAmount(BigDecimal.valueOf(rawAmount));
        System.out.println("date="+statementWords[dateIndex]);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        transaction.setDate(LocalDate.parse(statementWords[dateIndex], formatter));
        String rawOperationType = statementWords[operationTypeIndex];
        BankTransaction.operationType operationType;
        if (rawOperationType.equalsIgnoreCase("c") || rawOperationType.equalsIgnoreCase("credit")){
            operationType = BankTransaction.operationType.CREDIT;
        } else {
            operationType = BankTransaction.operationType.DEBIT;
        }
        transaction.setOperationType(operationType);
        return transaction;
    }

    public List<BankTransaction> statementParser(String pathToStatement) {
        System.out.println("Parsing statement: " + pathToStatement);
        List<BankTransaction> result = new ArrayList<>();

        try {
            Path path = Paths.get(pathToStatement);
            List<String> statementStrings = Files.readAllLines(path);
            //@TODO chaset det

            for (String statementString : statementStrings) {

                /**
                delim = "\\t";

                String[] statementWords = statementString.split(delim);

                for (int i = 0; i < statementWords.length; i++) {
                    System.out.println(i + ": " + statementWords[i]+ ", ");
                }
                **/



                System.out.println("working with str " + statementString);
                //check if string is a header or contains transaction data
                if (configOk && isNumeric(statementString.strip().substring(0,1))) {
                    System.out.println("String is num, conf ok:");
                    result.add(parseTransaction(statementString));
                } else if (!configOk) {
                        configure(statementString);
                    System.out.println("configure with str ");
                } else {
                    System.out.println("Can't parse transaction with no config. Skip");
                }

            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return result;
    }


    public static void main(String[] args) {
        /**
        System.out.println("Hello statement parser!");
        StatementParser parser = new StatementParser();
        List<BankTransaction> list = parser.statementParser("src/com/vkostylev/state.csv");
        for (BankTransaction transaction : list) {
            System.out.println(transaction);
        }
        **/
        /**
        int[] arr = {1,2,3,0,0,0};
        insert(arr, 0, 2);
        System.out.println(Arrays.toString(arr));
        **/


    }



    private static void merge(int[] nums1, int m, int[] nums2, int n) {
        if (m == 0) {
            System.arraycopy(nums2,0,nums1,0,nums2.length);
        } else {
            for (int i = 0; i < n; i++) {
                boolean inserted = false;
                for (int j = 0; j < m; j++) {
                    if ((nums2[i] <= nums1[j])) {
                        insert(nums1, j, nums2[i]);
                        m++;
                        inserted=true;
                        break;
                    }
                }
                if (!inserted) {
                    insert(nums1, m, nums2[i]);
                    m++;
                }
            }
            System.out.println(m);
        }
    }



    private static void insert(int[] arr, int index, int value) {
        if (index >= 0 && index < arr.length) {
            for (int i = arr.length - 1; i > index; i--) {
                arr[i] = arr[i - 1];
            }
            arr[index] = value;
        }
    }

}
