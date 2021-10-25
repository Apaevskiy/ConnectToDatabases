package DAO.service;

import DAO.entity.Person;
import Models.Database;
import Models.TypeOfDataBase;
import XML.XMLHandler;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {
    private List<Database> databaseList;
    private final List<Person> people;

    public DatabaseService(List<Person> people) {
        this.people = people;
//        databaseList = new ArrayList<>();
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLHandler handler = new XMLHandler();
            parser.parse(new File("src/main/resources/databases.xml"), handler);
            databaseList=handler.getList();
            for (Database database : databaseList) {
                database.setStatement(getStatement(database));
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }
    public void export(){
        for (Database database: databaseList) {
            switch (database.getType()){
                case AlfaBase: {

                } break;
                case Buffet:{

                } break;
                case Checkpoint_CBASE:{

                } break;
                case Checkpoint_GBASE: {

                } break;
            }
        }
    }
    private void generateSql(){

    }
    private Statement getStatement(Database database) {
        try {
            Class.forName("org.firebirdsql.jdbc.FBDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        try {
            if (database.getType() != TypeOfDataBase.AlfaBase) {
                Connection connection = DriverManager.getConnection(
                        "jdbc:firebirdsql://" + database.getPath() + "?sql_dialect=3&lc_ctype=WIN1251",
                        database.getUser(),
                        database.getPassword());
                if (connection != null) {
                    return connection.createStatement();
                }
            }
        } catch (SQLException ignored) {
            database.setCheckConnect(false);
        }
        return null;
    }
}
