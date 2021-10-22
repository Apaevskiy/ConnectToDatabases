package Models;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class DataBase {
    private String path;
    private String user;
    private String password;
    private String comment;
    private TypeOfDataBase type;
    private Boolean checkConnect;
//    private Connection statement;
}
