package DAO.mapper;

import DAO.entity.Key;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class KeyMapper implements Mapper<Key> {
    @Override
    public Key productMapper(ResultSet resultSet) throws SQLException {
        Date start = resultSet.getDate("D_N");
        Date finish = resultSet.getDate("D_O");
        return new Key(
                resultSet.getLong("KOD"),
                resultSet.getString("NOM"),
                start!=null?start.toLocalDate():null,
                finish!=null?finish.toLocalDate():null
        );
    }
}