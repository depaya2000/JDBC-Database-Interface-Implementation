/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import rs.etf.sab.operations.BuyerOperations;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
//import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mp190537d
 */
public class mp190537_BuyerOperations implements BuyerOperations{

    @Override
    public int createBuyer(String name, int cityId) {
        
        Connection connection = DB.getInstance().getConnection();
        String query = "INSERT INTO Kupac (Ime, IDGra, StanjeRacunaK) VALUES (?, ?, 0)";
        
        int buyerId = -1;

        try (
             PreparedStatement statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, name);
            statement.setInt(2, cityId);
            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        buyerId = generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(mp190537_BuyerOperations.class.getName()).log(Level.SEVERE, null, e);
        }

        return buyerId;
    }

    @Override
    public int setCity(int buyerId, int cityId) {
        
        Connection connection = DB.getInstance().getConnection();
    
        // Kreiranje SQL upita
        String query = "UPDATE Kupac SET IDGra = ? WHERE IDKup = ?";

        // Priprema upita
        try(PreparedStatement statement = connection.prepareStatement(query);){
        statement.setInt(1, cityId);
        statement.setInt(2, buyerId);

        // Izvršavanje upita
        int rowsAffected = statement.executeUpdate();

        // Provera rezultata izvršavanja upita
        if (rowsAffected > 0) {
            // Promena uspešna
            return 1;
        } else {
            // Promena neuspešna
            return -1;
        }
    } catch (SQLException e) {
        Logger.getLogger(mp190537_BuyerOperations.class.getName()).log(Level.SEVERE, null, e);
        return -1;
    }
}

    @Override
    public int getCity(int buyerId) {
        
        Connection connection = DB.getInstance().getConnection();
    
        String query = "SELECT IDGra FROM Kupac WHERE IDKup = ?";

        try(PreparedStatement statement = connection.prepareStatement(query);){
        statement.setInt(1, buyerId);

        ResultSet resultSet = statement.executeQuery();

        // Provera rezultata izvršavanja upita
        if (resultSet.next()) {
            // Dobijanje vrednosti iz rezultata
            int cityId = resultSet.getInt("IDGra");
            return cityId;
        } else {
            // Kupac nije pronađen
            return -1;
        }
    } catch (SQLException e) {
        Logger.getLogger(mp190537_BuyerOperations.class.getName()).log(Level.SEVERE, null, e);
        return -1;
    }
}

   @Override
   public BigDecimal increaseCredit(int buyerId, BigDecimal credit) {
    
       Connection connection = DB.getInstance().getConnection();
       
        String query = "UPDATE Kupac SET StanjeRacunaK = StanjeRacunaK + ? WHERE IDKup = ?";

        
        try(PreparedStatement statement = connection.prepareStatement(query);){
            statement.setBigDecimal(1, credit.setScale(3, RoundingMode.HALF_UP));
            statement.setInt(2, buyerId);

        int rowsAffected = statement.executeUpdate();


        if (rowsAffected > 0) {
            BigDecimal newCredit = getCurrentCredit(buyerId);
            return newCredit;
        } else {
            // Kupac nije pronađen ili ažuriranje nije uspelo
            return null;
        }
    } catch (SQLException e) {
        Logger.getLogger(mp190537_BuyerOperations.class.getName()).log(Level.SEVERE, null, e);
        return null;
    }
}

// Pomoćna metoda za dobijanje trenutnog stanja računa
private BigDecimal getCurrentCredit(int buyerId) {
    
        Connection connection = DB.getInstance().getConnection();
        String query = "SELECT StanjeRacunaK FROM Kupac WHERE IDKup = ?";

        try(PreparedStatement statement = connection.prepareStatement(query);){
        statement.setInt(1, buyerId);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            BigDecimal currentCredit = new BigDecimal(resultSet.getString("StanjeRacunaK"));
            return currentCredit;
        } else {
            return null;
        }
    } catch (SQLException e) {
       Logger.getLogger(mp190537_BuyerOperations.class.getName()).log(Level.SEVERE, null, e);
       return null;
    }
}

    @Override
    public int createOrder(int buyerId) {
        
    Connection connection = DB.getInstance().getConnection();
    String query = "INSERT INTO Porudzbina (Status, IDKup) VALUES (?, ?)";
    int orderId = -1;

    try (
         PreparedStatement statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
        
        statement.setString(1, "created");
        //statement.setDate(2, new java.sql.Date(System.currentTimeMillis()));
        statement.setInt(2, buyerId);

        int rowsAffected = statement.executeUpdate();

        if (rowsAffected > 0) {
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                orderId = generatedKeys.getInt(1);
            }
        }
    } catch (SQLException e) {
       Logger.getLogger(mp190537_BuyerOperations.class.getName()).log(Level.SEVERE, null, e);
    }

    return orderId;
}

    @Override
    public List<Integer> getOrders(int buyerId) {
        
        Connection connection = DB.getInstance().getConnection();
        String query = "SELECT IDPor FROM Porudzbina WHERE IDKup = ?";
        List<Integer> orders = new ArrayList<>();

        try (
            PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, buyerId);
            
            try(ResultSet resultSet = statement.executeQuery();){

            while (resultSet.next()) {
                int orderId = resultSet.getInt("IDPor");
                orders.add(orderId);
            }
          }
        } catch (SQLException e) {
            Logger.getLogger(mp190537_BuyerOperations.class.getName()).log(Level.SEVERE, null, e);
        }

        return orders;
}

    @Override
    public BigDecimal getCredit(int buyerId) {
        Connection connection = DB.getInstance().getConnection();
        String query = "SELECT StanjeRacunaK FROM Kupac WHERE IDKup = ?";
        BigDecimal credit = null;

        try (
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, buyerId);

            try(ResultSet resultSet = statement.executeQuery();){

            if (resultSet.next()) {
                credit = new BigDecimal(resultSet.getString("StanjeRacunaK"));
            }
           }
        } catch (SQLException e) {
           Logger.getLogger(mp190537_BuyerOperations.class.getName()).log(Level.SEVERE, null, e);
        }

        return credit;
    }
    
}
