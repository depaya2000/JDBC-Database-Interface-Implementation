/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import rs.etf.sab.operations.OrderOperations;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mp190537d
 */
public class mp190537_OrderOperations implements OrderOperations{

@Override
public int addArticle(int orderId, int articleId, int count) {
    Connection connection = DB.getInstance().getConnection();
    int itemId = -1;

    // Provera da li porudžbina postoji
    if (!doesOrderExist(orderId)) {
        return itemId;  // Porudžbina ne postoji
    }

    if (!doesArticleExist(articleId)) {
        return itemId;  // Artikal ne postoji
    }

    // Provjera da li ima dovoljno artikala na stanju
    if (!isEnoughArticle(articleId, count)) {
        System.out.println("Nedovoljna količina artikala sa ID " + articleId + " na stanju.");
        return itemId;
    }

    try (PreparedStatement insertItemStatement = connection.prepareStatement("INSERT INTO Stavka (IDArt, IDPor, Kolicina) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
         PreparedStatement updateItemStatement = connection.prepareStatement("UPDATE Stavka SET Kolicina = Kolicina + ? WHERE IDArt = ? AND IDPor = ?");
         PreparedStatement updateOrderAmountStatement = connection.prepareStatement("UPDATE Porudzbina SET UkupnaCena = UkupnaCena + ? WHERE IDPor = ?")) {

        // Provera da li je artikal već prisutan u porudžbini
        PreparedStatement checkItemStatement = connection.prepareStatement("SELECT * FROM Stavka WHERE IDArt = ? AND IDPor = ?");
        checkItemStatement.setInt(1, articleId);
        checkItemStatement.setInt(2, orderId);
        ResultSet itemResult = checkItemStatement.executeQuery();

        if (isArticleAlreadyAdded(orderId, articleId)) {
            // Artikal je već prisutan u porudžbini, ažurira se broj artikala
            updateItemStatement.setInt(1, count);
            updateItemStatement.setInt(2, articleId);
            updateItemStatement.setInt(3, orderId);
            updateItemStatement.executeUpdate();

            if (itemResult.next()) {
                itemId = itemResult.getInt("IDSta");
            }
        } else {
            // Artikal nije prisutan u porudžbini, dodaje se nova stavka sa odgovarajućim brojem artikala
            insertItemStatement.setInt(1, articleId);
            insertItemStatement.setInt(2, orderId);
            insertItemStatement.setInt(3, count);
            insertItemStatement.executeUpdate();

            ResultSet generatedKeys = insertItemStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                itemId = generatedKeys.getInt(1);
            }
        }

        // Ažuriranje ukupne cene porudžbine
        BigDecimal articlePrice = getArticlePrice(articleId);
        BigDecimal totalAmount = articlePrice.multiply(new BigDecimal(count));

        updateOrderAmountStatement.setBigDecimal(1, totalAmount);
        updateOrderAmountStatement.setInt(2, orderId);
        updateOrderAmountStatement.executeUpdate();
    } catch (SQLException e) {
        Logger.getLogger(mp190537_OrderOperations.class.getName()).log(Level.SEVERE, null, e);
    }

    return itemId;
}
    
    // Provera da li porudžbina postoji
    private boolean doesOrderExist(int orderId) {
        
        Connection connection = DB.getInstance().getConnection();
        
    try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM Porudzbina WHERE IDPor = ?")) {

        statement.setInt(1, orderId);
        ResultSet resultSet = statement.executeQuery();
        return resultSet.next();
    } catch (SQLException e) {
        Logger.getLogger(mp190537_OrderOperations.class.getName()).log(Level.SEVERE, null, e);
    }

    return false;
}
    // Provera da li artikal postoji
    private boolean doesArticleExist(int articleId) {
        
        Connection connection = DB.getInstance().getConnection();
        
    try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM Artikal WHERE IDArt = ?")) {

        statement.setInt(1, articleId);
        ResultSet resultSet = statement.executeQuery();
        return resultSet.next();
    } catch (SQLException e) {
       Logger.getLogger(mp190537_OrderOperations.class.getName()).log(Level.SEVERE, null, e);
    }

    return false;
    }
    
    // Provera da li ima dovoljno artikala na stanju
    private boolean isEnoughArticle(int articleId, int count) {
        
        Connection connection = DB.getInstance().getConnection();
        
    try (PreparedStatement statement = connection.prepareStatement("SELECT KolicinaNaStanju FROM Artikal WHERE IDArt = ?")) {

        statement.setInt(1, articleId);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            int stockCount = resultSet.getInt("KolicinaNaStanju");
            return count <= stockCount;
        }
    } catch (SQLException e) {
        Logger.getLogger(mp190537_OrderOperations.class.getName()).log(Level.SEVERE, null, e);
    }

    return false;
    }
    
      // Provera da li je artikal već prisutan u porudžbini
    private boolean isArticleAlreadyAdded(int orderId, int articleId) {
        
        Connection connection = DB.getInstance().getConnection();
        
    try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM Stavka WHERE IDPor = ? AND IDArt = ?")) {

        statement.setInt(1, orderId);
        statement.setInt(2, articleId);
        ResultSet resultSet = statement.executeQuery();
        return resultSet.next();
    } catch (SQLException e) {
        Logger.getLogger(mp190537_OrderOperations.class.getName()).log(Level.SEVERE, null, e);
    }

    return false;
    }
    
    //Dohvatanje Cene artikla na osnovu IDArt
    private BigDecimal getArticlePrice(int articleId) {
    Connection connection = DB.getInstance().getConnection();
    BigDecimal articlePrice = BigDecimal.ZERO;

    try {
        PreparedStatement statement = connection.prepareStatement("SELECT Cena FROM Artikal WHERE IDArt = ?");
        statement.setInt(1, articleId);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            articlePrice = resultSet.getBigDecimal("Cena");
        }

        resultSet.close();
        statement.close();
    } catch (SQLException e) {
        Logger.getLogger(mp190537_OrderOperations.class.getName()).log(Level.SEVERE, null, e);
    }

    return articlePrice;
}

    @Override
   public int removeArticle(int orderId, int articleId) {
    Connection connection = DB.getInstance().getConnection();
    int result = -1;

    // Provera da li porudžbina postoji
    if (!doesOrderExist(orderId)) {
        return result;  
    }

    // Provera da li artikal postoji
    if (!doesArticleExist(articleId)) {
        return result;  
    }

    try (PreparedStatement deleteItemStatement = connection.prepareStatement("DELETE FROM Stavka WHERE IDPor = ? AND IDArt = ?");
         PreparedStatement updateTotalAmountStatement = connection.prepareStatement("UPDATE Porudzbina SET UkupnaCena = ? WHERE IDPor = ?")) {

        // Provera da li je artikal prisutan u porudžbini
        if (isArticleAlreadyAdded(orderId, articleId)) {
            // Artikal je prisutan u porudžbini, izvršava se brisanje
            deleteItemStatement.setInt(1, orderId);
            deleteItemStatement.setInt(2, articleId);
            int rowsAffected = deleteItemStatement.executeUpdate();
            if (rowsAffected > 0) {
                // Ažuriranje UkupnaCena u tabeli Porudzbina
                BigDecimal newTotalAmount = getFinalPrice(orderId);
                updateTotalAmountStatement.setBigDecimal(1, newTotalAmount);
                updateTotalAmountStatement.setInt(2, orderId);
                updateTotalAmountStatement.executeUpdate();

                result = 1;  // Uspješno brisanje
            }
        } else {
            // Artikal nije prisutan u porudžbini
            result = -1;  
        }
    } catch (SQLException e) {
        Logger.getLogger(mp190537_OrderOperations.class.getName()).log(Level.SEVERE, null, e);
    }

    return result;
}

    @Override
    public List<Integer> getItems(int orderId) {
        
        Connection connection = DB.getInstance().getConnection();
        
        List<Integer> itemIds = new ArrayList<>();

        try (
             PreparedStatement statement = connection.prepareStatement("SELECT IDArt FROM Stavka WHERE IDPor = ?")) {

            statement.setInt(1, orderId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int itemId = resultSet.getInt("IDArt");
                itemIds.add(itemId);
            }

        } catch (SQLException e) {
            Logger.getLogger(mp190537_OrderOperations.class.getName()).log(Level.SEVERE, null, e);
        }

        return itemIds;
    }


    @Override
    public int completeOrder(int orderId) {
        
         Connection connection = DB.getInstance().getConnection();
        
        int result = -1;

        // Provera da li porudžbina postoji
        if (!doesOrderExist(orderId)) {
            return result;  // Porudžbina ne postoji
        }

        try (PreparedStatement statement = connection.prepareStatement("UPDATE Porudzbina SET Status = 'sent' WHERE IDPor = ?")) {

            statement.setInt(1, orderId);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                result = 1;  
            }

        } catch (SQLException e) {
            Logger.getLogger(mp190537_OrderOperations.class.getName()).log(Level.SEVERE, null, e);
        }

        return result;
    }

    @Override
    public BigDecimal getFinalPrice(int orderId) {
        
        Connection connection = DB.getInstance().getConnection();
        BigDecimal finalPrice = BigDecimal.ZERO;

        try (CallableStatement statement = connection.prepareCall("{CALL SP_FINAL_PRICE(?, ?)}")) {
            statement.setInt(1, orderId);
            statement.registerOutParameter(2, java.sql.Types.DECIMAL);
            statement.execute();

            finalPrice = statement.getBigDecimal(2);
        } catch (SQLException e) {
             Logger.getLogger(mp190537_OrderOperations.class.getName()).log(Level.SEVERE, null, e);
        }

    return finalPrice;
}

    @Override
    public BigDecimal getDiscountSum(int orderId) {
    BigDecimal shopDiscount = getShopDiscount(orderId);
    BigDecimal buyerDiscount = getBuyerDiscount(orderId);

    // Dobijanje ukupne cene porudžbine
    BigDecimal totalAmount = getTotalAmount(orderId);

    // Izračunavanje popusta na osnovu popusta prodavnice i kupca
    BigDecimal shopDiscountAmount = totalAmount.multiply(shopDiscount);
    BigDecimal buyerDiscountAmount = totalAmount.multiply(buyerDiscount);
    BigDecimal orderDiscount = shopDiscountAmount.add(buyerDiscountAmount);

    return orderDiscount;
}
    //Metoda za dobijanje popusta od 2% ukoliko je zadovoljen uslov
    private BigDecimal getBuyerDiscount(int buyerId) {
        
        Connection connection = DB.getInstance().getConnection();
    BigDecimal buyerDiscount = BigDecimal.ZERO;

    try {
        // Izračunavanje datuma pre 30 dana od trenutnog datuma
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);

        // Izvršavanje upita sa dodatnim uslovom za datume
        PreparedStatement statement = connection.prepareStatement("SELECT SUM(UkupnaCena) AS TotalAmount FROM Porudzbina WHERE IDKup = ? AND SentTime >= ?");
        statement.setInt(1, buyerId);
        statement.setObject(2, LocalDateTime.of(thirtyDaysAgo, LocalTime.MIDNIGHT));
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            BigDecimal totalAmount = resultSet.getBigDecimal("TotalAmount");
            if (totalAmount != null && totalAmount.compareTo(new BigDecimal("10000")) > 0) {
                buyerDiscount = new BigDecimal("0.02");
            }
        }

        resultSet.close();
        statement.close();
    } catch (SQLException e) {
        Logger.getLogger(mp190537_OrderOperations.class.getName()).log(Level.SEVERE, null, e);
    }

    return buyerDiscount;
    }
    
    //Metoda za dobijanje popusta prodavnice
    private BigDecimal getShopDiscount(int orderId) {
        
        Connection connection = DB.getInstance().getConnection();
    BigDecimal shopDiscount = BigDecimal.ZERO;

    try {
        PreparedStatement statement = connection.prepareStatement("SELECT IDPro FROM Transakcija WHERE IDPor = ?");
        statement.setInt(1, orderId);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            int shopId = resultSet.getInt("IDPro");

            statement = connection.prepareStatement("SELECT Popust FROM Prodavnica WHERE IDPro = ?");
            statement.setInt(1, shopId);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                shopDiscount = resultSet.getBigDecimal("Popust");
            }
        }

        resultSet.close();
        statement.close();
    } catch (SQLException e) {
      Logger.getLogger(mp190537_OrderOperations.class.getName()).log(Level.SEVERE, null, e);

    }

    return shopDiscount;
}
    
    //Metoda za dobijanje ukupne cene porudzbine bez popusta
    private BigDecimal getTotalAmount(int orderId) {
    Connection connection = DB.getInstance().getConnection();
    BigDecimal totalAmount = BigDecimal.ZERO;

    try {
        PreparedStatement statement = connection.prepareStatement(
            "SELECT SUM(s.Kolicina * a.Cena) AS TotalAmount " +
            "FROM Stavka s " +
            "INNER JOIN Artikal a ON s.IDArt = a.IDArt " +
            "WHERE s.IDPor = ?"
        );
        statement.setInt(1, orderId);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            totalAmount = resultSet.getBigDecimal("TotalAmount");
        }

        resultSet.close();
        statement.close();
    } catch (SQLException e) {
        Logger.getLogger(mp190537_OrderOperations.class.getName()).log(Level.SEVERE, null, e);
    }

    return totalAmount;
}

    @Override
    public String getState(int orderId) {
        
        Connection connection = DB.getInstance().getConnection();
        
        String state = null;

        // Provera da li porudžbina postoji
        if (!doesOrderExist(orderId)) {
            return state;  // Porudžbina ne postoji
        }

        try (PreparedStatement statement = connection.prepareStatement("SELECT Status FROM Porudzbina WHERE IDPor = ?")) {

            statement.setInt(1, orderId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                state = resultSet.getString("Status");
            }

        } catch (SQLException e) {
           Logger.getLogger(mp190537_OrderOperations.class.getName()).log(Level.SEVERE, null, e);
        }

     return state;
}

    @Override
    public Calendar getSentTime(int orderId) {
        
        Connection connection = DB.getInstance().getConnection();
        
        Calendar sentTime = null;

        try (PreparedStatement statement = connection.prepareStatement("SELECT SentTime FROM Porudzbina WHERE IDPor = ?")) {

            statement.setInt(1, orderId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Date date = resultSet.getDate("SentTime");
                sentTime = Calendar.getInstance();
                sentTime.setTime(date);
            }
        } catch (SQLException e) {
             Logger.getLogger(mp190537_OrderOperations.class.getName()).log(Level.SEVERE, null, e);
        }
        System.out.println(sentTime== null);
        return sentTime;
    }

    @Override
    public Calendar getRecievedTime(int orderId) {
        
         Connection connection = DB.getInstance().getConnection();
        String query = "SELECT ReceivedTime FROM Porudzbina WHERE IDPor = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, orderId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Date receivedDate = resultSet.getDate("ReceivedTime");
                    Calendar receivedTime = Calendar.getInstance();
                    receivedTime.setTimeInMillis(receivedDate.getTime());
                    return receivedTime;
                }
            }
        } catch (SQLException e) {
             Logger.getLogger(mp190537_OrderOperations.class.getName()).log(Level.SEVERE, null, e);
        }

        return null;
    }
    

    @Override
    public int getBuyer(int orderId) {
        
        Connection connection = DB.getInstance().getConnection();
        
        int buyerId = -1;

        try (PreparedStatement statement = connection.prepareStatement("SELECT IDKup FROM Porudzbina WHERE IDPor = ?")) {

            statement.setInt(1, orderId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                buyerId = resultSet.getInt("IDKup");
            }
        } catch (SQLException e) {
             Logger.getLogger(mp190537_OrderOperations.class.getName()).log(Level.SEVERE, null, e);
        }

        return buyerId;
}

    @Override
    public int getLocation(int orderId) {

        try {
            // Provera da li porudžbina postoji
            if (!doesOrderExist(orderId)) {
                return -1;  // Porudžbina ne postoji
            }

            // Dobijanje statusa porudžbine
            String status = getOrderStatus(orderId);

            // Provera stanja porudžbine
            if (status.equals("created")) {
                return -1;  // Porudžbina je u stanju "created", lokacija je -1
            } else if (status.equals("sent")) {
                // Dobijanje grada prodavnice najbliže kupcu
                int shopCityId = getClosestShopCityToBuyer(orderId);
                return shopCityId;
            } else if (status.equals("arrived")) {
                // Dobijanje grada iz kojeg je porudžbina poslata
                int fromCityId = getFromCityForOrder(orderId);
                return fromCityId;
            }

        } catch (SQLException e) {
           Logger.getLogger(mp190537_OrderOperations.class.getName()).log(Level.SEVERE, null, e);
        }
        return -1;
    }


    private String getOrderStatus(int orderId) throws SQLException {
        Connection connection = DB.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT Status FROM Porudzbina WHERE IDPor = ?");
        statement.setInt(1, orderId);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        String status = resultSet.getString("Status");
        resultSet.close();
        statement.close();
        return status;
    }
    
    private int getClosestShopCityToBuyer(int orderId) throws SQLException {
        Connection connection = DB.getInstance().getConnection();
        int buyerCityId = getBuyerCityId(orderId);
        int closestShopCityId = -1;
        int minDistance = Integer.MAX_VALUE;

        // Dobijanje svih gradova
        PreparedStatement cityStatement = connection.prepareStatement("SELECT IDGra FROM Grad");
        ResultSet cityResultSet = cityStatement.executeQuery();

        while (cityResultSet.next()) {
            int shopCityId = cityResultSet.getInt("IDGra");

            // Provera da li grad ima prodavnicu
            if (hasShopInCity(shopCityId)) {
                int distance = getDistanceBetweenCities(shopCityId, buyerCityId);
                if (distance < minDistance) {
                    minDistance = distance;
                    closestShopCityId = shopCityId;
                }
            }
        }

        cityResultSet.close();
        cityStatement.close();

        return closestShopCityId;
}

private int getBuyerCityId(int orderId) throws SQLException {
    Connection connection = DB.getInstance().getConnection();
    PreparedStatement statement = connection.prepareStatement("SELECT IDGra FROM Porudzbina p JOIN Kupac k ON p.IDKup = k.IDKup WHERE IDPor = ?");
    statement.setInt(1, orderId);
    ResultSet resultSet = statement.executeQuery();
    resultSet.next();
    int buyerCityId = resultSet.getInt("IDGra");
    resultSet.close();
    statement.close();
    return buyerCityId;
}

private boolean hasShopInCity(int cityId) throws SQLException {
    Connection connection = DB.getInstance().getConnection();
    PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM Prodavnica WHERE IDGra = ?");
    statement.setInt(1, cityId);
    ResultSet resultSet = statement.executeQuery();
    resultSet.next();
    int count = resultSet.getInt(1);
    resultSet.close();
    statement.close();
    return count > 0;
}

private int getDistanceBetweenCities(int cityId1, int cityId2) throws SQLException {
    Connection connection = DB.getInstance().getConnection();
    PreparedStatement statement = connection.prepareStatement("SELECT VremeTransporta FROM Linija WHERE (IDGraOd = ? AND IDGraDO = ?) OR (IDGraOd = ? AND IDGraDO = ?)");
    statement.setInt(1, cityId1);
    statement.setInt(2, cityId2);
    statement.setInt(3, cityId2);
    statement.setInt(4, cityId1);
    ResultSet resultSet = statement.executeQuery();
    resultSet.next();
    int distance = resultSet.getInt("VremeTransporta");
    resultSet.close();
    statement.close();
    return distance;
}

private int getFromCityForOrder(int orderId) throws SQLException {
    Connection connection = DB.getInstance().getConnection();
    PreparedStatement statement = connection.prepareStatement("SELECT IDGraOd FROM Linija l JOIN Porudzbina p ON l.IDLin = p.IDLin WHERE IDPor = ?");
    statement.setInt(1, orderId);
    ResultSet resultSet = statement.executeQuery();
    resultSet.next();
    int fromCityId = resultSet.getInt("IDGraOd");
    resultSet.close();
    statement.close();
    return fromCityId;
}
    
}
