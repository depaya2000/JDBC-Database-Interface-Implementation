package rs.etf.sab.student;


import rs.etf.sab.operations.*;
//import org.junit.Test;
import rs.etf.sab.student.*;
import rs.etf.sab.tests.TestHandler;
import rs.etf.sab.tests.TestRunner;

//import java.util.Calendar;

public class StudentMain {

    public static void main(String[] args) {

        ArticleOperations articleOperations = new mp190537_ArticleOperations(); // Change this for your implementation (points will be negative if interfaces are not implemented).
        BuyerOperations buyerOperations = new mp190537_BuyerOperations();
        CityOperations cityOperations = new mp190537_CityOperations();
        GeneralOperations generalOperations = new mp190537_GeneralOperations();
        OrderOperations orderOperations = new mp190537_OrderOperations();
        ShopOperations shopOperations = new mp190537_ShopOperations();
        TransactionOperations transactionOperations = new mp190537_TransactionOperations();

        TestHandler.createInstance(
                articleOperations,
                buyerOperations,
                cityOperations,
                generalOperations,
                orderOperations,
                shopOperations,
                transactionOperations
        );

        TestRunner.runTests();
    }
}
