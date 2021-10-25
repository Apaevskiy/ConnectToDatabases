package Models;

import lombok.*;

import java.sql.Statement;

@Data
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

    public Database(String path, String user, String password, String comment, TypeOfDataBase type) {
        this.path = path;
        this.user = user;
        this.password = password;
        this.comment = comment;
        this.type = type;
    }
}
