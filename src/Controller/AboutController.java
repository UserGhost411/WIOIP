package Controller;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class AboutController {
    private Stage dialogStage;
    public void setDialogStage(Stage dialogStage){
        this.dialogStage = dialogStage;
    }
    @FXML
    ImageView imgbg;
    @FXML
    public void initialize() {
        imgbg.setPreserveRatio(false);
    }
}
