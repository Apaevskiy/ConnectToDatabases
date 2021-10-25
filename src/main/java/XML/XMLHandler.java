package XML;

import Models.Database;
import Models.TypeOfDataBase;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class XMLHandler extends DefaultHandler {
    List<Database> list = new ArrayList<>();

    public List<Database> getList() {
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
            list.add(new Database(path,user, password, comment, type,null));
        }
    }
}
