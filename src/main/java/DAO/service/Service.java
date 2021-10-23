package DAO.service;

import DAO.mapper.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public abstract class Service {
    private final Statement statement;

    public Service(Statement statement) {
        this.statement = statement;
    }

    protected <T> List<T> query(String sql, Mapper<T> mapper) {
        List<T> list = new ArrayList<>();
        try {
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                list.add(mapper.productMapper(resultSet));
            }
            return list;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    protected <T> T queryOneElement(String sql, Mapper<T> mapper) {
        try {
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                return mapper.productMapper(resultSet);
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    protected boolean execute(String sql) {
        try {
            statement.execute(sql);
            return true;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
    }
}