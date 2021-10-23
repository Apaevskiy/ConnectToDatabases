
package DAO.mapper;

import DAO.entity.Person;
import DAO.service.KeyService;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AlfaBaseMapper implements Mapper<Person> {
    @Override
    public Person productMapper(ResultSet resultSet) throws SQLException {
        return new Person(
                resultSet.getLong("KU"),
                resultSet.getString("TAB_NOM"),
                resultSet.getString("D_NOMER"),
                resultSet.getString("D_L_NOMER"),
                resultSet.getDate("D_D_VIDACHI"),
                resultSet.getString("D_KEM_VIDAN"),
                resultSet.getInt("RAZR"),
                new DepartmentMapper().productMapper(resultSet),
                new PositionMapper().productMapper(resultSet),
                resultSet.getString("FAM"),
                resultSet.getString("IM"),
                resultSet.getString("OTCH"),
                resultSet.getDate("D_ROJD"),
                resultSet.getDate("D_PR_NA_R"),
                resultSet.getString("ADDR_PROP_TEXT"),
                resultSet.getString("M_ROJD_TEXT"),
                null,
                resultSet.getBlob("PICT")
        );
    }
}


