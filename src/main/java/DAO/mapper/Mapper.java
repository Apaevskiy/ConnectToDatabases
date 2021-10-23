package DAO.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface Mapper<T> {
    T productMapper(ResultSet resultSet) throws SQLException;
}
