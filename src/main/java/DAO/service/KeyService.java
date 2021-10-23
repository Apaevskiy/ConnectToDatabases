package DAO.service;

import DAO.entity.Key;
import DAO.mapper.KeyMapper;

import java.sql.Statement;
import java.util.List;

public class KeyService extends Service {
    public KeyService(Statement statement) {
        super(statement);
    }
    public List<Key> getKeysByUserId(long id){
        return query("select * from sp_ol_dopusk where kod_il="+id, new KeyMapper());
    }
}
