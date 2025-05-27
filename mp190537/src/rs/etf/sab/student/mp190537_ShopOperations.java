/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.etf.sab.student;

import java.util.List;
import rs.etf.sab.operations.ShopOperations;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mp190537d
 */
public class mp190537_ShopOperations implements ShopOperations{
    
    List shopCityIds = new ArrayList();
    
     // Metoda za kreiranje nove prodavnice
    @Override
    public int createShop(String name, String cityName) {
        
        Connection connection = DB.getInstance().getConnection();
       
        int shopId = -1;

            // Provera da li postoji prodavnica sa istim imenom
            if (isShopNameExists(name)) {
                System.out.println("Shop with name " + name + " already exists.");
                return shopId;
            }

            // Provera da li postoji grad sa datim imenom
            int cityId = getCityId(cityName);
            if (cityId == -1) {
                System.out.println("City with name " + cityName + " does not exist.");
                return shopId;
            }

            // Kreiranje upita za ubacivanje nove prodavnice
            String query = "INSERT INTO Prodavnica (Naziv, Popust, StanjeRacunaP, IDGra) VALUES (?, 0, 0, ?)";

            try (PreparedStatement statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, name);
                statement.setInt(2, cityId);

                // Izvršavanje upita
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    // Dobijanje ID-a nove prodavnice
                    try(ResultSet resultSet = statement.getGeneratedKeys()){

                    if (resultSet.next()) {
                        shopId = resultSet.getInt(1);
                        shopCityIds.add(shopId);
                    }
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(mp190537_ShopOperations.class.getName()).log(Level.SEVERE, null, e);
        }

        return shopId;
    }

    // Metoda za proveru da li postoji prodavnica sa datim imenom
    private boolean isShopNameExists(String name) {
        
        Connection connection = DB.getInstance().getConnection();
        String query = "SELECT COUNT(*) FROM Prodavnica WHERE Naziv = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        
        } catch (SQLException e) {
            Logger.getLogger(mp190537_ShopOperations.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    // Metoda za dobijanje ID-a grada na osnovu imena
    private int getCityId(String cityName){
        
        Connection connection = DB.getInstance().getConnection();
        String query = "SELECT IDGra FROM Grad WHERE Naziv = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, cityName);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("IDGra");
            }
        }catch (SQLException e) {
            Logger.getLogger(mp190537_ShopOperations.class.getName()).log(Level.SEVERE, null, e);
        }

        return -1;
    }
    
    // Metoda za promenu grada za prodavnicu
    @Override
    public int setCity(int shopId, String cityName) {
        
        Connection connection = DB.getInstance().getConnection();
         int result = -1;
         
            // Provera da li postoji prodavnica sa datim ID-om
            if (!isShopExists(shopId)) {
                System.out.println("Shop with ID " + shopId + " does not exist.");
                return result;
            }

            // Provera da li postoji grad sa datim imenom
            int cityId = getCityId(cityName);
            if (cityId == -1) {
                System.out.println("City with name " + cityName + " does not exist.");
                return result;
            }

            // Kreiranje upita za ažuriranje grada prodavnice
            String query = "UPDATE Prodavnica SET IDGra = ? WHERE IDPro = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, cityId);
                statement.setInt(2, shopId);

                // Izvršavanje upita
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    result = 1; // Uspesno promenjen grad za prodavnicu
                }
            
        } catch (SQLException e) {
            Logger.getLogger(mp190537_ShopOperations.class.getName()).log(Level.SEVERE, null, e);
        }

        return result;
    }
    
    // Metoda za proveru da li postoji prodavnica sa datim ID-om
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
            Logger.getLogger(mp190537_ShopOperations.class.getName()).log(Level.SEVERE, null, e);
        }

        return false;
    }

    // Metoda za dobijanje ID-a grada za prodavnicu
    @Override
    public int getCity(int shopId) {
        
        Connection connection = DB.getInstance().getConnection();
        int cityId = -1;

            // Provera da li postoji prodavnica sa datim ID-om
            if (!isShopExists(shopId)) {
                System.out.println("Shop with ID " + shopId + " does not exist.");
                return cityId;
            }

            // Kreiranje upita za dobijanje ID-a grada
            String query = "SELECT IDGra FROM Prodavnica WHERE IDPro = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, shopId);

                // Izvršavanje upita
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    cityId = resultSet.getInt("IDGra");
                }
            
        } catch (SQLException e) {
             Logger.getLogger(mp190537_ShopOperations.class.getName()).log(Level.SEVERE, null, e);
        }

        return cityId;
    }
    
    // Metoda za podešavanje popusta za prodavnicu
    @Override
    public int setDiscount(int shopId, int discountPercentage) {
        
        Connection connection = DB.getInstance().getConnection();
        int result = -1;

            // Provera da li postoji prodavnica sa datim ID-om
            if (!isShopExists(shopId)) {
                System.out.println("Shop with ID " + shopId + " does not exist.");
                return result;
            }

            // Kreiranje upita za podešavanje popusta
            String query = "UPDATE Prodavnica SET Popust = ? WHERE IDPro = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, discountPercentage);
                statement.setInt(2, shopId);

                // Izvršavanje upita
                result = statement.executeUpdate();
            
        } catch (SQLException e) {
             Logger.getLogger(mp190537_ShopOperations.class.getName()).log(Level.SEVERE, null, e);
        }

        return result;
    }

    // Metoda za povećanje broja artikala u prodavnici
    @Override
    public int increaseArticleCount(int articleId, int increment) {
        
        Connection connection = DB.getInstance().getConnection();
        int result = -1;

            // Provera da li postoji artikal sa datim ID-om
            if (!isArticleExists(articleId)) {
                System.out.println("Article with ID " + articleId + " does not exist.");
                return result;
            }

            // Dobijanje trenutnog broja artikala u prodavnici
            int currentCount = getArticleCount(articleId);

            // Ažuriranje broja artikala
            int newCount = currentCount + increment;

            // Kreiranje upita za ažuriranje broja artikala
            String query = "UPDATE Artikal SET KolicinaNaStanju = ? WHERE IDArt = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, newCount);
                statement.setInt(2, articleId);

                // Izvršavanje upita
                result = statement.executeUpdate();
            
        } catch (SQLException e) {
             Logger.getLogger(mp190537_ShopOperations.class.getName()).log(Level.SEVERE, null, e);
        }

        return result;
    }

    // Metoda za proveru da li postoji artikal sa datim ID-om
    private boolean isArticleExists(int articleId){
        
        Connection connection = DB.getInstance().getConnection();
        String query = "SELECT COUNT(*) FROM Artikal WHERE IDArt = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, articleId);

            // Izvršavanje upita
            if (statement.executeQuery().next()) {
                return true;
            }
        }catch (SQLException e) {
             Logger.getLogger(mp190537_ShopOperations.class.getName()).log(Level.SEVERE, null, e);
        }

        return false;
    }

    // Metoda za dobijanje trenutnog broja artikala u prodavnici
    @Override
    public int getArticleCount(int articleId){
        int count = -1;
        
         Connection connection = DB.getInstance().getConnection();

        String query = "SELECT KolicinaNaStanju FROM Artikal WHERE IDArt = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, articleId);

            // Izvršavanje upita
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt("KolicinaNaStanju");
            }
        } catch (SQLException ex) {
            Logger.getLogger(mp190537_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return count;
    }

      // Metoda za dobijanje svih artikala u prodavnici
    @Override
    public List<Integer> getArticles(int shopId) {
        
        Connection connection = DB.getInstance().getConnection();
        List<Integer> articleIds = new ArrayList<>();

            // Provera da li postoji prodavnica sa datim ID-om
            if (!isShopExists( shopId)) {
                System.out.println("Shop with ID " + shopId + " does not exist.");
                return articleIds;
            }

            // Kreiranje upita za dobijanje svih artikala u prodavnici
            String query = "SELECT IDArt FROM Artikal WHERE IDPro = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, shopId);

                // Izvršavanje upita
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    int articleId = resultSet.getInt("IDArt");
                    articleIds.add(articleId);
                }
            
        } catch (SQLException e) {
             Logger.getLogger(mp190537_ShopOperations.class.getName()).log(Level.SEVERE, null, e);
        }

        return articleIds;
    }
    
     // Metoda za dobijanje popusta za prodavnicu
    @Override
    public int getDiscount(int shopId) {
        
        Connection connection = DB.getInstance().getConnection();
        int discount = -1;

            // Provera da li postoji prodavnica sa datim ID-om
            if (!isShopExists(shopId)) {
                System.out.println("Shop with ID " + shopId + " does not exist.");
                return discount;
            }

            // Kreiranje upita za dobijanje popusta za prodavnicu
            String query = "SELECT Popust FROM Prodavnica WHERE IDPro = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, shopId);

                // Izvršavanje upita
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    discount = resultSet.getInt("Popust");
                }
            
        } catch (SQLException e) {
           Logger.getLogger(mp190537_ShopOperations.class.getName()).log(Level.SEVERE, null, e);
        }

        return discount;
    }
    
}
