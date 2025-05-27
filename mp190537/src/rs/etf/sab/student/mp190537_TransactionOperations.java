/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import rs.etf.sab.operations.TransactionOperations;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author mp190537d
 */
public class mp190537_TransactionOperations implements TransactionOperations{

    @Override
    public BigDecimal getBuyerTransactionsAmmount(int buyerId) {
        BigDecimal totalAmount = BigDecimal.ZERO;

        Connection connection = DB.getInstance().getConnection();
        String query = "SELECT SUM(Iznos) AS TotalAmount FROM Transakcija WHERE IDKup = ? AND Vrsta='uplata'";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, buyerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    BigDecimal amount = resultSet.getBigDecimal("TotalAmount");
                    if (amount != null) {
                        totalAmount = amount;
                    }
                }
            }

        } catch (SQLException e) {
            Logger.getLogger(mp190537_TransactionOperations.class.getName()).log(Level.SEVERE, null, e);
            return BigDecimal.valueOf(-1);
        }

        return totalAmount;
    }

    @Override
    public BigDecimal getShopTransactionsAmmount(int shopId) {
        BigDecimal totalAmount = BigDecimal.ZERO;

        Connection connection = DB.getInstance().getConnection();
        String query = "SELECT SUM(Iznos) AS TotalAmount " +
                       "FROM Transakcija " +
                       "WHERE IDPro = ? AND Vrsta = 'isplata'";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, shopId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    BigDecimal amount = resultSet.getBigDecimal("TotalAmount");
                    if (amount != null) {
                        totalAmount = amount;
                    }
                }
            }

        } catch (SQLException e) {
            Logger.getLogger(mp190537_TransactionOperations.class.getName()).log(Level.SEVERE, null, e);
            return BigDecimal.valueOf(-1);
        }
        return totalAmount;
    }

    @Override
   public List<Integer> getTransationsForBuyer(int buyerId) {
    List<Integer> transactionIds = new ArrayList<>();

    Connection connection = DB.getInstance().getConnection();
    String query = "SELECT IDTra FROM Transakcija WHERE IDKup = ? AND Vrsta = ?";

    try (PreparedStatement statement = connection.prepareStatement(query)) {
        statement.setInt(1, buyerId);
        statement.setString(2, "uplata");
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int transactionId = resultSet.getInt("IDTra");
                transactionIds.add(transactionId);
            }
        }
    } catch (SQLException e) {
        Logger.getLogger(mp190537_TransactionOperations.class.getName()).log(Level.SEVERE, null, e);
        return null;
    }

    return transactionIds;
}


    @Override
    public int getTransactionForBuyersOrder(int orderId) {
        int transactionId = -1;

        Connection connection = DB.getInstance().getConnection();
        String query = "SELECT IDTra " +
                       "FROM Transakcija " +
                       "WHERE IDPor = ? and Vrsta = 'uplata";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, orderId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    transactionId = resultSet.getInt("IDTra");
                }
            }

        } catch (SQLException e) {
            Logger.getLogger(mp190537_TransactionOperations.class.getName()).log(Level.SEVERE, null, e);
            return -1;
        }

        return transactionId;
    }

    @Override
    public int getTransactionForShopAndOrder(int orderId, int shopId) {
        int transactionId = -1;

        Connection connection = DB.getInstance().getConnection();
        String query = "SELECT IDTra " +
                       "FROM Transakcija " +
                       "WHERE IDPor = ? AND IDPro = ? AND Vrsta='isplata'";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, orderId);
            statement.setInt(2, shopId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    transactionId = resultSet.getInt("IDTra");
                }
            }

        } catch (SQLException e) {
            Logger.getLogger(mp190537_TransactionOperations.class.getName()).log(Level.SEVERE, null, e);
            return -1;
        }

        return transactionId;
    }

   
    @Override
    public Calendar getTimeOfExecution(int transactionId) {
        Connection connection = DB.getInstance().getConnection();
        String query = "SELECT t.DatumT, o.ReceivedTime " +
                       "FROM Transakcija t " +
                       "JOIN Porudzbina o ON t.IDPor = o.IDPor " +
                       "WHERE t.IDTra = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, transactionId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Calendar timeOfExecution = Calendar.getInstance();
                    timeOfExecution.setTime(resultSet.getTimestamp("DatumT"));
                    Calendar receiveTime = Calendar.getInstance();
                    receiveTime.setTime(resultSet.getTimestamp("ReceivedTime"));
                    if (timeOfExecution.equals(receiveTime)) {
                        return timeOfExecution;
                    }
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(mp190537_TransactionOperations.class.getName()).log(Level.SEVERE, null, e);
        }

        return null;
    }

   @Override
   public BigDecimal getAmmountThatBuyerPayedForOrder(int orderId) {
       
    Connection connection = DB.getInstance().getConnection();
    BigDecimal ammount = BigDecimal.ZERO;

    try (
         PreparedStatement statement = connection.prepareStatement("SELECT SUM(Iznos) AS TotalAmount FROM Transakcija WHERE IDPor = ? AND Vrsta = 'uplata'")) {
        statement.setInt(1, orderId);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            ammount = resultSet.getBigDecimal("TotalAmount");
        }
    } catch (SQLException e) {
         Logger.getLogger(mp190537_TransactionOperations.class.getName()).log(Level.SEVERE, null, e);
    }

    return ammount;
}

    @Override
    public BigDecimal getAmmountThatShopRecievedForOrder(int shopId, int orderId) {
        
        Connection connection = DB.getInstance().getConnection();
        BigDecimal ammount = BigDecimal.ZERO;

        try (
             PreparedStatement statement = connection.prepareStatement("SELECT SUM(Iznos) AS TotalAmount FROM Transakcija WHERE IDPro = ? AND IDPor = ? AND Vrsta = 'isplata'")) {
            statement.setInt(1, shopId);
            statement.setInt(2, orderId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                ammount = resultSet.getBigDecimal("TotalAmount");
            }
        } catch (SQLException e) {
            Logger.getLogger(mp190537_TransactionOperations.class.getName()).log(Level.SEVERE, null, e);
        }

    return ammount;
}

    @Override
    public BigDecimal getTransactionAmount(int transactionId) {
        BigDecimal amount = BigDecimal.ZERO;

        try (Connection connection = DB.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT Iznos FROM Transakcija WHERE IDTrans = ?")) {
            statement.setInt(1, transactionId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                amount = resultSet.getBigDecimal("Iznos");
            }
        } catch (SQLException e) {
             Logger.getLogger(mp190537_TransactionOperations.class.getName()).log(Level.SEVERE, null, e);
        }

        return amount;
    }

    @Override
    public BigDecimal getSystemProfit() {
        Connection connection = DB.getInstance().getConnection();
    BigDecimal systemProfit = BigDecimal.ZERO;
    
    try (
         Statement statement = connection.createStatement();
         ResultSet resultSet = statement.executeQuery("SELECT SUM(Iznos) AS UkupanProfit FROM Transakcija WHERE Vrsta = 'isplata'")) {
        
        if (resultSet.next()) {
            BigDecimal ukupanProfit = resultSet.getBigDecimal("UkupanProfit");
            if (ukupanProfit != null) {
                if (isKupacUspunjavaUslov()) {
                    systemProfit = ukupanProfit.multiply(new BigDecimal("0.03"));
                } else {
                    systemProfit = ukupanProfit.multiply(new BigDecimal("0.05"));
                }
            }
        }
    } catch (SQLException e) {
         Logger.getLogger(mp190537_TransactionOperations.class.getName()).log(Level.SEVERE, null, e);
    }
    
    return systemProfit;
}
   
   private boolean isKupacUspunjavaUslov() {
       Connection connection = DB.getInstance().getConnection();
        try (
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS BrojPorudzbina FROM Porudzbina WHERE ReceivedTime >= DATE_SUB(CURRENT_DATE(), INTERVAL 30 DAY) AND UkupnaCena > 10000")) {

            if (resultSet.next()) {
                int brojPorudzbina = resultSet.getInt("BrojPorudzbina");
                return brojPorudzbina > 0;
            }
        } catch (SQLException e) {
             Logger.getLogger(mp190537_TransactionOperations.class.getName()).log(Level.SEVERE, null, e);
        }

          return false;
        }

    @Override
    public List<Integer> getTransationsForShop(int shopId) {
        Connection connection = DB.getInstance().getConnection();
        
        String query = "SELECT IDTra FROM Transakcija WHERE IDPro = ? AND Vrsta = ?";
        List<Integer> transactions = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, shopId);
            statement.setString(2, "isplata");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int transactionId = resultSet.getInt("IDTra");
                    transactions.add(transactionId);
                }
            }
        } catch (SQLException e) {
             Logger.getLogger(mp190537_TransactionOperations.class.getName()).log(Level.SEVERE, null, e);
        }

        return transactions.isEmpty() ? null : transactions;
    }
    
}
