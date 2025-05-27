/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.etf.sab.student;

import rs.etf.sab.operations.ArticleOperations;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mp190537d
 */
public class mp190537_ArticleOperations implements ArticleOperations{

    @Override
    public int createArticle(int shopId, String articleName, int articlePrice) {
        
        Connection connection = DB.getInstance().getConnection();
        
         int articleId = -1;

            // Provera da li prodavnica postoji
            if (!isShopExists(shopId)) {
                System.out.println("Shop with ID " + shopId + " does not exist.");
                return articleId;
            }

            // Kreiranje upita za ubacivanje novog artikla
            String query = "INSERT INTO Artikal (Naziv, Cena, KolicinaNaStanju, IDPro) VALUES (?, ?, 0, ?)";

            try (PreparedStatement statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, articleName);
                statement.setInt(2, articlePrice);
                statement.setInt(3, shopId);

                // Izvršavanje upita
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    // Dobijanje ID-a novog artikla
                    ResultSet resultSet = statement.getGeneratedKeys();

                    if (resultSet.next()) {
                        articleId = resultSet.getInt(1);
                    }
                }
            
        } catch (SQLException e) {
           Logger.getLogger(mp190537_ArticleOperations.class.getName()).log(Level.SEVERE, null, e);
        }

        return articleId;
    }

    // Metoda za proveru da li prodavnica postoji
    private boolean isShopExists(int shopId){
        
        Connection connection = DB.getInstance().getConnection();
        String query = "SELECT COUNT(*) FROM Prodavnica WHERE IDPro = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, shopId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        }catch (SQLException e) {
           Logger.getLogger(mp190537_ArticleOperations.class.getName()).log(Level.SEVERE, null, e);
        }

        return false;
    }
    
    
}
