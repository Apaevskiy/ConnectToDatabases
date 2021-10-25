package XML;

import DAO.entity.Department;
import DAO.entity.Key;
import DAO.entity.Person;
import DAO.entity.Position;
import javafx.concurrent.Task;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadXML extends Task<Void> {
    private final String path;
    private final List<Person> people = new ArrayList<>();
    private final Map<Long, String> departments = new HashMap<>();
    private final Map<Long, String> positions = new HashMap<>();
    private int count = 0;

    public List<Person> getPeople() {
        return people;
    }

    public Map<Long, String> getDepartments() {
        return departments;
    }

    public Map<Long, String> getPositions() {
        return positions;
    }

    public LoadXML(String path) {
        this.path = path;
    }

    private void progress(long l1, long l2) {
        this.updateProgress(l1, l2);
    }

    @Override
    protected Void call() {
        progress(0, 1);
        try {
            File file = new File(path + "\\data.xml");
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);

            count = doc.getElementsByTagName("person").getLength();
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            parser.parse(file, defaultHandler);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return null;
    }

    DefaultHandler defaultHandler = new DefaultHandler() {
        Person person;
        int i = 0;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            switch (qName) {
                case "person": {
                    if (person != null) {
                        people.add(person);
                        progress(i++, count);
                    }
                    String dateOfReceiving = attributes.getValue("dateOfReceiving");
                    String birthday = attributes.getValue("birthday");
                    String startWork = attributes.getValue("startWork");
                    person = new Person(
                            Long.parseLong(attributes.getValue("id")),
                            attributes.getValue("number"),
                            attributes.getValue("passportNumber"),
                            attributes.getValue("serialNumber"),
                            dateOfReceiving!=null?LocalDate.parse(dateOfReceiving):null,
                            attributes.getValue("receivingBy"),
                            Integer.parseInt(attributes.getValue("rank")),
                            new Department(Long.parseLong(attributes.getValue("department")), null),
                            new Position(Long.parseLong(attributes.getValue("position")), null),
                            attributes.getValue("surname"),
                            attributes.getValue("name"),
                            attributes.getValue("patronymic"),
                            birthday!=null?LocalDate.parse(birthday):null,
                            startWork!=null?LocalDate.parse(startWork):null,
                            attributes.getValue("placeOfResident"),
                            attributes.getValue("placeOfBirth"),
                            null,
                            null
                    );
                    try {
                        BufferedImage bImage;
                        bImage = ImageIO.read(new File(path + "\\images\\" + person.getId() + ".png"));
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        ImageIO.write(bImage, "jpg", bos);
                        byte[] data = bos.toByteArray();
                        person.setPhoto(data);
                    } catch (IOException ignored) {
                    }
                }
                break;
                case "key": {
                    if (person != null) {
                        if (person.getKeys() == null) {
                            person.setKeys(new ArrayList<>());
                        }
                        long id = Long.parseLong(attributes.getValue("key_id"));
                        String date = attributes.getValue("key_start");
                        LocalDate start = date.equals("") ? null : LocalDate.parse(date);
                        Key key = new Key(id, attributes.getValue("key_number"), start, null);
                        person.addKey(key);
                    }
                }
                break;
                case "department": {
                    departments.put(
                            Long.parseLong(attributes.getValue("id")),
                            attributes.getValue("name")
                    );
                }
                break;
                case "position": {
                    positions.put(
                            Long.parseLong(attributes.getValue("id")),
                            attributes.getValue("name")
                    );
                }
                break;
            }
        }
    };

}
