/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.etf.sab.student;

import java.util.List;
import rs.etf.sab.operations.CityOperations;
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
public class mp190537_CityOperations implements CityOperations{
    
     Graph graph = new Graph();
    
    //Metoda koja kreira novi grad
    @Override
    public int createCity(String name) {
    int cityId = -1;
    
    Connection connection = DB.getInstance().getConnection();
     // Create the SQL query
     String query = "INSERT INTO Grad (Naziv) VALUES (?)";
     
    try (
        PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);){

        preparedStatement.setString(1, name);
        
        int rowsAffected = preparedStatement.executeUpdate();

        if (rowsAffected > 0) {
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                cityId = generatedKeys.getInt(1);
                
                // Kreiraj Ä?vor za grad u grafu
                graph.addCity(cityId);
            }
        }
    } catch (SQLException e) {
       Logger.getLogger(mp190537_CityOperations.class.getName()).log(Level.SEVERE, null, e);
    }
    return cityId;
}
   //Metoda koja dohvata sve ID-jeve gradova iz tabele Grad
   @Override
   public List<Integer> getCities() {
       
       Connection connection = DB.getInstance().getConnection();
        List<Integer> cityIds = new ArrayList<>();

        try (
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT IDGra FROM Grad")) {

            while (resultSet.next()) {
                int cityId = resultSet.getInt("IDGra");
                cityIds.add(cityId);
            }

        } catch (SQLException e) {
           Logger.getLogger(mp190537_CityOperations.class.getName()).log(Level.SEVERE, null, e);
           return null;
        }

        return cityIds;
    }

    //Metoda za unos nove linije izmeÄ‘u dva grada u tabelu Linija
    @Override
    public int connectCities(int cityId1, int cityId2, int distance) {
        int lineId = -1;
        
        Connection connection = DB.getInstance().getConnection();
        String query = "INSERT INTO Linija (IDGraOd, IDGraDo, VremeTransporta) VALUES (?, ?, ?)";

        try (
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, cityId1);
            statement.setInt(2, cityId2);
            statement.setInt(3, distance);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    lineId = generatedKeys.getInt(1);
                    
                    graph.addConnection(cityId1, cityId2, distance);
                }
            }

        } catch (SQLException e) {
            Logger.getLogger(mp190537_CityOperations.class.getName()).log(Level.SEVERE, null, e);
        }

        return lineId;
    }
    
    
    //Metoda koja dohvata povezane gradove za datu cityId vrednost
    @Override
    public List<Integer> getConnectedCities(int cityId) {
        Connection connection = DB.getInstance().getConnection();
        String query = "SELECT IDGraOd, IDGraDo FROM Linija WHERE IDGraOd = ? OR IDGraDo = ?";
        List<Integer> connectedCities = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, cityId);
            statement.setInt(2, cityId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int connectedCityId = resultSet.getInt("IDGraOd");
                    if (connectedCityId == cityId) {
                        connectedCityId = resultSet.getInt("IDGraDo");
                    }
                    connectedCities.add(connectedCityId);
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(mp190537_CityOperations.class.getName()).log(Level.SEVERE, null, e);
        }

        return connectedCities;
    }
    
    //Metoda koja dohvata prodavnice u datom gradu
    @Override
    public List<Integer> getShops(int cityId) {
        
        Connection connection = DB.getInstance().getConnection();
        
        String query = "SELECT IDPro FROM Prodavnica WHERE IDGra = ?";
        List<Integer> shopIds = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, cityId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int shopId = resultSet.getInt("IDPro");
                    shopIds.add(shopId);
                }
            }
        } catch (SQLException e) {
           Logger.getLogger(mp190537_CityOperations.class.getName()).log(Level.SEVERE, null, e);
        }

        return shopIds;
    }
    
}
