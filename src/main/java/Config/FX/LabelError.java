package Config.FX;

import javafx.scene.Node;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class LabelError {
    private static Node node;
    private static String style;
}
