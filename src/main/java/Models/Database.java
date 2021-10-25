package Models;

import lombok.*;

import java.sql.Statement;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Database {
    private String path;
    private String user;
    private String password;
    private String comment;
    private TypeOfDataBase type;
    private Boolean checkConnect;
    private Statement statement;
}