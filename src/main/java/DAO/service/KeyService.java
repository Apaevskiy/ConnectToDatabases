package DAO.service;

import DAO.entity.Key;
import DAO.mapper.KeyMapper;

import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

public class KeyService extends Service {
    public KeyService(Statement statement) {
        super(statement);
    }

    public List<Key> getKeysByUserId(long id) {
        return query("select * from sp_ol_dopusk where D_O is null and kod_il=" + id, new KeyMapper());
    }

    public boolean updateKey(Key k) {
        if (!closeKey(k)) return false;
        return execute(
                String.format("update SP_OL_DOPUSK set D_N='%s', D_O=%s, NOM=%s, NAIM='%s' where KOD=%d",
                        k.getStart() == null ? LocalDate.now() : k.getStart(),
                        k.getFinish() == null ? null : "'" + k.getFinish() + "'",
                        k.getNumber(), k.getNumber(), k.getId()));
    }

    public boolean addKey(Key k, long personId) {
        if (!closeKey(k)) return false;
        return execute(String.format(
                "insert into SP_OL_DOPUSK " +
                        "(KOD_IL, D_N, D_O, NOM, NAIM) " +
                        "values (%d,'%s',%s,'%s',%s)",
                personId,
                k.getStart() == null ? LocalDate.now() : k.getStart(),
                k.getFinish() == null ? null : "'" + k.getFinish() + "'", k.getNumber(), k.getNumber()));
    }

    public boolean closeKey(Key k) {
        return execute("update sp_ol_dopusk set D_O='" + LocalDate.now() + "' where D_O is null and kod!=" + k.getId() + " and nom=" + k.getNumber());
    }
}
