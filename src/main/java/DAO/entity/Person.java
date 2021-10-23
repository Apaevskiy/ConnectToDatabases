package DAO.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Blob;
import java.sql.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Person {
    private long id;    // KU
    private String number;    // TAB_NOM
    private String passportNumber; // D_NOMER
    private String serialNumber; // D_L_NOMER
    private Date dateOfReceiving; // D_D_VIDACHI
    private String receivingBy; // D_KEM_VIDAN
    private int rank; // RAZR
    private Department department; // K_PODR
    private Position position; // K_DOLJN
    private String surname; // FAM
    private String name; // IM
    private String patronymic; // OTCH
    private Date birthday; // D_ROJD
    private Date startWork; // D_PR_NA_R
    private String placeOfResident; // ADDR_PROP_TEXT
    private String placeOfBirth; // M_ROJD_TEXT
    private List<Key> keys; // SP_OL_DOPUSK NAIM
    private Blob photo; // PICT
}
