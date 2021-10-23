package DAO.mapper;

import DAO.entity.Key;

import java.sql.ResultSet;
import java.sql.SQLException;

public class KeyMapper implements Mapper<Key> {
    @Override
    public Key productMapper(ResultSet resultSet) throws SQLException {
        return new Key(
                resultSet.getLong("KOD"),
                resultSet.getInt("NOM"),
                resultSet.getDate("D_N"),
                resultSet.getDate("D_O")
        );
    }
}