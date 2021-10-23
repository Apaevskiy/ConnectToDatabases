package DAO.service;

import DAO.entity.Department;
import DAO.entity.Position;
import DAO.mapper.AlfaBaseMapper;
import DAO.entity.Person;
import DAO.mapper.DepartmentMapper;
import DAO.mapper.PositionMapper;

import java.sql.Statement;
import java.util.List;

public class UserService extends Service {
    private final KeyService keyService;

    public UserService(Statement statement) {
        super(statement);
        this.keyService = new KeyService(statement);
    }

    public List<Person> getAllUsers() {
        List<Person> list = query("select u.KU, u.TAB_NOM, u.D_NOMER, u.D_L_NOMER, u.D_D_VIDACHI, u.D_KEM_VIDAN, u.RAZR, u.FAM, u.IM, u.OTCH, u.D_ROJD, u.D_PR_NA_R, u.ADDR_PROP_TEXT, u.M_ROJD_TEXT, " +
                "i.PICT as PICT, dep.KOD as dep_id, dep.NAIM as dep_name, pos.KOD as pos_id, pos.NAIM as pos_name " +
                "from sp_ol u " +
                "left outer join SP_OL_BLOBS i on i.KOD_IL=u.KU " +
                "left outer join SP_DOLJN pos on pos.KOD=u.K_DOLJN " +
                "left outer join SP_PODR dep on dep.KOD=u.K_PODR " +
                "where u.D_UV is null and u.TAB_NOM!=''", new AlfaBaseMapper());
        for (Person base: list) {
            base.setKeys(keyService.getKeysByUserId(base.getId()));
        }
        return list;
    }
    public List<Department> getAllDepartments() {
        return query("select KOD as dep_id, NAIM as dep_name from SP_PODR", new DepartmentMapper());
    }
    public List<Position> getAllPositions() {
        return query("select KOD as pos_id, NAIM as pos_name from SP_DOLJN", new PositionMapper());
    }
}
