
package DAO.mapper;

import DAO.entity.Person;
import DAO.service.KeyService;
import org.firebirdsql.gds.impl.GDSHelper;
import org.firebirdsql.jdbc.FBBlob;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonMapper implements Mapper<Person> {
    @Override
    public Person productMapper(ResultSet resultSet) throws SQLException {
        Blob blob = resultSet.getBlob("PICT");
        byte[] photo = blob == null ? null : blob.getBytes(1, (int) blob.length());
        Date dateOfReceiving = resultSet.getDate("D_D_VIDACHI");
        Date birthday = resultSet.getDate("D_ROJD");
        Date startWork = resultSet.getDate("D_PR_NA_R");
        return new Person(
                resultSet.getLong("KU"),
                resultSet.getString("TAB_NOM"),
                resultSet.getString("D_NOMER"),
                resultSet.getString("D_L_NOMER"),
                dateOfReceiving != null ? dateOfReceiving.toLocalDate() : null,
                resultSet.getString("D_KEM_VIDAN"),
                resultSet.getInt("RAZR"),
                new DepartmentMapper().productMapper(resultSet),
                new PositionMapper().productMapper(resultSet),
                resultSet.getString("FAM"),
                resultSet.getString("IM"),
                resultSet.getString("OTCH"),
                birthday != null ? birthday.toLocalDate() : null,
                startWork != null ? startWork.toLocalDate() : null,
                resultSet.getString("ADDR_PROP_TEXT"),
                resultSet.getString("M_ROJD_TEXT"),
                null,
                photo
        );
    }
}


