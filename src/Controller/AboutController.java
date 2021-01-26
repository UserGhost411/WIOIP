package Controller;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class AboutController {
    private Stage dialogStage;
    public void setDialogStage(Stage dialogStage){ this.dialogStage = dialogStage; }
    // register semua component pada form About
    @FXML ImageView imgbg;
    //menginisialisasi image dan set rasio nya
    @FXML public void initialize() {
        imgbg.setImage(new Image("/res/img/bg/bg.png"));
        imgbg.setPreserveRatio(false);
    }
}
