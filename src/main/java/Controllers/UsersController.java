package Controllers;

import Config.FX.MyAnchorPane;

public class UsersController extends MyAnchorPane {
    public UsersController() {
        super("/views/UsersPage.fxml", PriorityType.NON_PRIORITY_PAGE);
    }

    @Override
    public void timer() {

    }
}
