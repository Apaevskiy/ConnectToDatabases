package DAO.mapper;

import DAO.entity.Position;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PositionMapper implements Mapper<Position>{
    @Override
    public Position productMapper(ResultSet resultSet) throws SQLException {
        return new Position(
                resultSet.getLong("pos_id"),
                resultSet.getString("pos_name")
        );
    }
}