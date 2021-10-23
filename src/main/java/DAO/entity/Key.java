package DAO.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;

@Data
@AllArgsConstructor
public class Key {
    private long id;
    private int number;
    private Date start;
    private Date finish;
}
