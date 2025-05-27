/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.etf.sab.student;

import java.util.Calendar;
import rs.etf.sab.operations.GeneralOperations;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mp190537d
 */
public class mp190537_GeneralOperations implements GeneralOperations{
    
    private Calendar initialTime;

    public Calendar getInitialTime() {
        return initialTime;
    }
  
    @Override
    public void setInitialTime(Calendar time) {
        this.initialTime = time;
    }

    @Override
    public Calendar time(int days) {
        Calendar currentTime = Calendar.getInstance();
        if (initialTime != null) {
            initialTime.add(Calendar.DAY_OF_MONTH, days);
        }
        currentTime.add(Calendar.DAY_OF_MONTH, days);
        return currentTime;
    }

    @Override
    public Calendar getCurrentTime() {
        if (initialTime != null) {
            return (Calendar) initialTime.clone();
        }
        return Calendar.getInstance();
    }
    
    //Metoda za brisanje svih podataka iz tabele
    @Override
public void eraseAll() {
    Connection connection = DB.getInstance().getConnection();

    try (Statement statement = connection.createStatement()) {

        // Isključivanje trigerova
        statement.executeUpdate("DISABLE TRIGGER ALL ON Stavka");
        statement.executeUpdate("DISABLE TRIGGER ALL ON Porudzbina");
        statement.executeUpdate("DISABLE TRIGGER ALL ON Transakcija");
        statement.executeUpdate("DISABLE TRIGGER ALL ON Kupac");
        statement.executeUpdate("DISABLE TRIGGER ALL ON Prodavnica");
        statement.executeUpdate("DISABLE TRIGGER ALL ON Artikal");
        statement.executeUpdate("DISABLE TRIGGER ALL ON Linija");
        statement.executeUpdate("DISABLE TRIGGER ALL ON Grad");

        // Isključivanje stranih ključeva
        statement.executeUpdate("ALTER TABLE Stavka NOCHECK CONSTRAINT ALL");
        statement.executeUpdate("ALTER TABLE Prodavnica NOCHECK CONSTRAINT ALL");
        statement.executeUpdate("ALTER TABLE Artikal NOCHECK CONSTRAINT ALL");
        statement.executeUpdate("ALTER TABLE Porudzbina NOCHECK CONSTRAINT ALL");
        statement.executeUpdate("ALTER TABLE Kupac NOCHECK CONSTRAINT ALL");
        statement.executeUpdate("ALTER TABLE Transakcija NOCHECK CONSTRAINT ALL");
        statement.executeUpdate("ALTER TABLE Linija NOCHECK CONSTRAINT ALL");

        // Izvršavanje SQL upita za brisanje svih podataka iz tabela
        statement.executeUpdate("DELETE FROM Stavka");
        statement.executeUpdate("DELETE FROM Porudzbina");
        statement.executeUpdate("DELETE FROM Transakcija");
        statement.executeUpdate("DELETE FROM Artikal");
        statement.executeUpdate("DELETE FROM Prodavnica");
        statement.executeUpdate("DELETE FROM Kupac");
        statement.executeUpdate("DELETE FROM Linija");
        statement.executeUpdate("DELETE FROM Grad");

        // Uključivanje trigerova
        statement.executeUpdate("ENABLE TRIGGER ALL ON Stavka");
        statement.executeUpdate("ENABLE TRIGGER ALL ON Porudzbina");
        statement.executeUpdate("ENABLE TRIGGER ALL ON Transakcija");
        statement.executeUpdate("ENABLE TRIGGER ALL ON Kupac");
        statement.executeUpdate("ENABLE TRIGGER ALL ON Prodavnica");
        statement.executeUpdate("ENABLE TRIGGER ALL ON Artikal");
        statement.executeUpdate("ENABLE TRIGGER ALL ON Linija");
        statement.executeUpdate("ENABLE TRIGGER ALL ON Grad");

        // Uključivanje stranih ključeva
        statement.executeUpdate("ALTER TABLE Stavka WITH CHECK CHECK CONSTRAINT ALL");
        statement.executeUpdate("ALTER TABLE Prodavnica WITH CHECK CHECK CONSTRAINT ALL");
        statement.executeUpdate("ALTER TABLE Artikal WITH CHECK CHECK CONSTRAINT ALL");
        statement.executeUpdate("ALTER TABLE Porudzbina WITH CHECK CHECK CONSTRAINT ALL");
        statement.executeUpdate("ALTER TABLE Kupac WITH CHECK CHECK CONSTRAINT ALL");
        statement.executeUpdate("ALTER TABLE Transakcija WITH CHECK CHECK CONSTRAINT ALL");
        statement.executeUpdate("ALTER TABLE Linija WITH CHECK CHECK CONSTRAINT ALL");
        
      
    } catch (SQLException e) {
        Logger.getLogger(mp190537_GeneralOperations.class.getName()).log(Level.SEVERE, null, e);
    }
}
    
}
