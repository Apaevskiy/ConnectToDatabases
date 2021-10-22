package Config.FX;

import javafx.scene.Node;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TimerForHidingNodes {
    private final Timer timer;

    TimerTaskHidden timerTask;

    public TimerForHidingNodes(List<InputControl> nodes) {
        this.timer = new Timer(true);
        this.timerTask = new TimerTaskHidden(nodes);
    }
    public TimerForHidingNodes(InputControl nodes) {
        this.timer = new Timer(true);
        this.timerTask = new TimerTaskHidden(Collections.singletonList(nodes));
    }

    public void schedule() {
        timer.purge();
        this.timerTask = timerTask.cancelSchedule();
        long delay = 5000;
        timer.schedule(timerTask, delay);
    }


    private static class TimerTaskHidden extends TimerTask {
        private final List<InputControl> nodes;

        private TimerTaskHidden(List<InputControl> nodes) {
            this.nodes = nodes;
        }

        @Override
        public void run() {
            System.out.println(nodes);
            if (nodes != null)
                waitHidden();
        }

        public void waitHidden() {
            for (InputControl node : nodes) {
                String styleError = "hidden";
                if (node.getError() != null && !node.getError().getStyleClass().contains(styleError)) {
                    node.getError().getStyleClass().add(styleError);
                }
                if (node.getField() != null) {
                    for (Node field : node.getField()) {
                        String styleField = "error";
                        if (field.getStyleClass().contains(styleField)) {
                            field.getStyleClass().removeAll(styleField);
                        }
                    }
                }
            }
        }

        public TimerTaskHidden cancelSchedule() {
            this.cancel();
            return new TimerTaskHidden(nodes);
        }
    }
}
