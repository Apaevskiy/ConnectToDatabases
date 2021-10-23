package XML;

import DAO.entity.Department;
import DAO.entity.Key;
import DAO.entity.Person;
import DAO.entity.Position;
import javafx.concurrent.Task;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.sql.Blob;
import java.util.List;

public class CreateXML extends Task<Void> {
    private final List<Department> departments;
    private final List<Position> positions;
    private final List<Person> people;
    private final String path;

    public CreateXML(List<Person> people,List<Department> departments, List<Position> positions,  String path) {
        this.departments = departments;
        this.positions = positions;
        this.people = people;
        this.path = path;
    }

    @Override
    protected Void call() {
        int count = departments.size() + positions.size() + people.size();
        int i = 0;
        try {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element rootElement =
                    doc.createElementNS("https://github.com/Apaevskiy/", "Data");
            doc.appendChild(rootElement);
            Element peopleElement = doc.createElement("people");
            for (Person person : people) {
                Element personElement = doc.createElement("person");
                personElement.setAttribute("id", String.valueOf(person.getId()));
                personElement.setAttribute("number", person.getNumber());
                personElement.setAttribute("passportNumber", person.getPassportNumber());
                personElement.setAttribute("serialNumber", person.getSerialNumber());
                personElement.setAttribute("dateOfReceiving", String.valueOf(person.getDateOfReceiving()));
                personElement.setAttribute("receivingBy", person.getReceivingBy());
                personElement.setAttribute("rank", String.valueOf(person.getRank()));
                personElement.setAttribute("department", String.valueOf(person.getDepartment().getId()));
                personElement.setAttribute("department", String.valueOf(person.getPosition().getId()));
                personElement.setAttribute("surname", person.getSurname());
                personElement.setAttribute("name", person.getName());
                personElement.setAttribute("patronymic", person.getPatronymic());
                personElement.setAttribute("birthday", String.valueOf(person.getBirthday()));
                personElement.setAttribute("startWork", String.valueOf(person.getStartWork()));
                personElement.setAttribute("placeOfResident", person.getPlaceOfResident());
                personElement.setAttribute("placeOfBirth", person.getPlaceOfBirth());
                Element keysElements = doc.createElement("keys");
                for (Key key: person.getKeys()) {
                    Element keyElement = doc.createElement("department");
                    keyElement.setAttribute("id", String.valueOf(key.getId()));
                    keyElement.setAttribute("number", String.valueOf(key.getNumber()));
                    keyElement.setAttribute("start", String.valueOf(key.getStart()));
                    keyElement.setAttribute("finish", String.valueOf(key.getFinish()));
                    keysElements.appendChild(keyElement);
                }
                personElement.appendChild(keysElements);
                Blob photo = person.getPhoto();
                String data;
                if(photo!=null) {
                    byte[] bdata = photo.getBytes(1, (int) photo.length());
                    data = new String(bdata);
                } else data="";
                personElement.setAttribute("photo", data);
                        peopleElement.appendChild(personElement);
                this.updateProgress(i++, count);
            }
            Element departmentElements = doc.createElement("departments");
            for (Department department: departments) {
                Element element = doc.createElement("department");
                element.setAttribute("id", String.valueOf(department.getId()));
                element.setAttribute("name", String.valueOf(department.getName()));
                departmentElements.appendChild(element);
                this.updateProgress(i++, count);
            }
            Element positionElements = doc.createElement("positions");
            for (Position position: positions) {
                Element element = doc.createElement("position");
                element.setAttribute("id", String.valueOf(position.getId()));
                element.setAttribute("name", String.valueOf(position.getName()));
                positionElements.appendChild(element);
                this.updateProgress(i++, count);
            }

            rootElement.appendChild(peopleElement);
            rootElement.appendChild(departmentElements);
            rootElement.appendChild(positionElements);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult file = new StreamResult(new File(path+"/data.xml"));
            transformer.transform(source, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
