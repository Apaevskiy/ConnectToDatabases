package DAO.mapper;

import DAO.entity.Department;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DepartmentMapper implements Mapper<Department>{
    @Override
    public Department productMapper(ResultSet resultSet) throws SQLException {
        return new Department(
                resultSet.getLong("dep_id"),
                resultSet.getString("dep_name")
        );
    }
}