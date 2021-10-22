package Config.FX;

import javafx.scene.Node;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
public class InputControl {
    private List<Node> field;
    private Node error;

    public InputControl(Node field, Node error) {
        this.field = Collections.singletonList(field);
        this.error = error;
    }

    public InputControl(Node error) {
        this.error = error;
    }
}
