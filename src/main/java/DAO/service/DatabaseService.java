package DAO.service;

import DAO.entity.Department;
import DAO.entity.Key;
import DAO.entity.Person;
import DAO.entity.Position;
import Models.Database;
import Models.TypeOfDataBase;
import XML.XMLHandler;
import javafx.concurrent.Task;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.List;

public class DatabaseService extends Task<Void> {
    private List<Database> databaseList;
    private final List<Person> people;
    private final List<Department> departments;
    private final List<Position> positions;

    public DatabaseService(List<Person> people, List<Department> departments, List<Position> positions) {
        this.people = people;
        this.departments = departments;
        this.positions = positions;
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLHandler handler = new XMLHandler();
            parser.parse(new File("src/main/resources/databases.xml"), handler);
            databaseList = handler.getList();
            for (Database database : databaseList) {
                database.setStatement(getStatement(database));
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }
    protected boolean exist(String table, String key, Long id, Statement statement) {
        try {
            ResultSet resultSet = statement.executeQuery(
                    String.format("select count(*) from %s where %s=%d", table, key, id)
            );
            if (resultSet.next()) {
                int count = resultSet.getInt(0);
                return count > 0;
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return false;
    }

    protected boolean execute(String sql, Statement statement) {
        try {
            statement.execute(sql);
            return true;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
    }

    @Override
    protected Void call() throws Exception {
        int count = people.size()* databaseList.size();
        int i=0;
        for(Database database: databaseList){
            if(database.getType().equals(TypeOfDataBase.Checkpoint_CBASE)){
                Statement statement = database.getStatement();
                StringBuilder builder = new StringBuilder();
                for(Department department : departments){
                    builder = new StringBuilder();
                    if(exist("FB_POD","ID",department.getId(), statement)){
                        builder.append("update FB_POD set ")
                                .append("PARENT='").append(1001).append("', ")
                                .append("NAME=").append(department.getName()).append(" ")
                                .append("where ID=").append(department.getId());
                    } else {
                        builder.append("insert into FB_POD ")
                                .append("(ID, PARENT, NAME) values (")
                                .append(department.getId()).append(", ")
                                .append(1001).append(", ")
                                .append("'").append(department.getName()).append(") ");
                    }
                    execute(builder.toString(), statement);
                }
                for(Position position : positions){
                    builder = new StringBuilder();
                    if(exist("FB_DOL","ID",position.getId(), statement)){
                        builder.append("update FB_POD set ")
                                .append("PARENT='").append(1001).append("', ")
                                .append("NAME=").append(position.getName()).append(" ")
                                .append("where ID=").append(position.getId());
                    } else {
                        builder.append("insert into FB_DOL ")
                                .append("(ID, PARENT, NAME) values (")
                                .append(position.getId()).append(", ")
                                .append(1001).append(", ")
                                .append("'").append(position.getName()).append(") ");
                    }
                    execute(builder.toString(), statement);
                }
            }
        }

        for (Person person : people) {
            List<Key> keys = person.getKeys();
            for (Database database : databaseList) {
                Statement statement = database.getStatement();
                switch (database.getType()) {
                    case Buffet: {
                        String key = keys!=null && keys.size()>0? keys.get(0).getNumber():null;
                        StringBuilder builder = new StringBuilder();
                        if (exist("S_STAFF", "ID", person.getId(), statement)) {
                            builder.append("update S_STAFF set ")
                                    .append("LAST_NAME='").append(person.getLastName()).append("', ")
                                    .append("FIRST_NAME='").append(person.getLastName()).append("', ")
                                    .append("MIDDLE_NAME='").append(person.getLastName()).append("', ")
                                    .append("POS='").append(person.getLastName()).append("', ")
                                    .append("DEPARTMENT='").append(person.getLastName()).append("', ")
                                    .append("CARD_CODE=").append(key).append(" ")
                                    .append("where ID=").append(person.getLastName());
                        } else {
                            builder.append("insert into S_STAFF ")
                                    .append("(ID, LAST_NAME, FIRST_NAME, MIDDLE_NAME, POS, DEPARTMENT, CARD_CODE) values (")
                                            .append(person.getId()).append(", ")
                                            .append("'").append(person.getLastName()).append("', ")
                                            .append("'").append(person.getFirstName()).append("', ")
                                            .append("'").append(person.getMiddleName()).append("', ")
                                            .append("'").append(person.getPosition().getName()).append("', ")
                                            .append("'").append(person.getDepartment().getName()).append("', ")
                                            .append(key).append(") ");
                        }
                        execute(builder.toString(), statement);
                        builder = new StringBuilder();
                        if (exist("S_PHOTO", "STAFF_ID", person.getId(), statement)) {
                            builder.append("update S_PHOTO set ")
                                    .append("STAFF_ID=").append(person.getId()).append(", ")
                                    .append("PHOTO='").append(new javax.sql.rowset.serial.SerialBlob(person.getPhoto())).append("', ")
                                    .append("where STAFF_ID=").append(person.getId());
                        } else {
                            builder.append("insert into S_PHOTO ")
                                    .append("(STAFF_ID, PHOTO) values (")
                                    .append(person.getId()).append(", ")
                                    .append("'").append(new javax.sql.rowset.serial.SerialBlob(person.getPhoto())).append("') ");
                        }
                        execute(builder.toString(), statement);
                    }
                    break;
                    case Checkpoint_CBASE: {
                        StringBuilder builder = new StringBuilder();
                        if (exist("FB_USR", "ID", person.getId(), statement)) {
                            builder.append("update FB_USR set ")
                                    .append("TABNUM='").append(person.getNumber()).append("', ")
                                    .append("FNAME='").append(person.getFirstName()).append("', ")
                                    .append("LNAME='").append(person.getLastName()).append("', ")
                                    .append("SNAME='").append(person.getMiddleName()).append("', ")
                                    .append("DOLZ=").append(person.getPosition().getId()).append(" ")
                                    .append("PODR=").append(person.getDepartment().getId()).append(" ")
                                    .append("DOP4='").append("test").append("', ")
                                    .append("FIO='")
                                    .append(person.getLastName()).append(" ")
                                    .append(person.getFirstName()).append(" ")
                                    .append(person.getMiddleName()).append("', ")
                                    .append("where ID=").append(person.getId());
                        } else {
                            builder.append("insert into FB_USR ")
                                    .append("(ID, TABNUM, FNAME, LNAME, SNAME, DOLZ, PODR, DOP4, FIO) values (")
                                    .append(person.getId()).append(", ")
                                    .append("'").append(person.getNumber()).append("', ")
                                    .append("'").append(person.getFirstName()).append("', ")
                                    .append("'").append(person.getLastName()).append("', ")
                                    .append("'").append(person.getMiddleName()).append("', ")
                                    .append(person.getPosition().getId()).append(", ")
                                    .append(person.getDepartment().getId()).append(", ")
                                    .append("'").append("test").append("', '")
                                    .append(person.getFirstName()).append(" ")
                                    .append(person.getLastName()).append(" ")
                                    .append(person.getMiddleName()).append("')");
                        }
                        execute(builder.toString(), statement);
                    }
                    break;
                    case Checkpoint_GBASE: {
                        StringBuilder builder = new StringBuilder();
                        if (exist("UPH", "USERID", person.getId(), statement)) {
                            builder.append("update UPH set ")
                                    .append("PHOTO='").append(new javax.sql.rowset.serial.SerialBlob(person.getPhoto())).append("', ")
                                    .append("where USERID=").append(person.getId());
                        } else {
                            builder.append("insert into UPH ")
                                    .append("(USERID, PHOTO) values (")
                                    .append(person.getId()).append(", ")
                                    .append("'").append(new javax.sql.rowset.serial.SerialBlob(person.getPhoto())).append("') ");
                        }
                        execute(builder.toString(), statement);
                    }
                    break;
                }
                this.updateProgress(i++, count);
            }
        }
        return null;
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
