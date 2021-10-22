package Config;

import Models.DataBase;
import Models.TypeOfDataBase;
import lombok.Getter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class XMLHandler extends DefaultHandler {
    List<DataBase> list = new ArrayList<>();

    public List<DataBase> getList() {
        return list;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equals("database")) {
            String path = attributes.getValue("path");
            String user = attributes.getValue("user");
            String password = attributes.getValue("password");
            TypeOfDataBase type = TypeOfDataBase.valueOf(attributes.getValue("type"));
            String comment = attributes.getValue("comment");
            list.add(new DataBase(path,user, password, comment, type,null));
        }
    }
}
