package Controller;

import Model.PlaceModel;
import Module.Configuration;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.JSONObject;
import java.io.FileNotFoundException;
public class SettingsController {
    private Stage dialogStage;
    public void setDialogStage(Stage dialogStage){ this.dialogStage = dialogStage; }
    // register semua component pada form Settings
    @FXML private TableView<PlaceModel> TableLocation;
    @FXML private TableColumn<PlaceModel,String> cityCol;
    @FXML private TableColumn<PlaceModel,String> defCol;
    @FXML private TableColumn<PlaceModel,String> addedCol;
    @FXML ChoiceBox opt_temp;
    @FXML TabPane tab_panel;
    private ObservableList<PlaceModel> PlaceData = FXCollections.observableArrayList();

    //menginisialisasi TableColumn pada Tableview dan memuat data
    @FXML public void initialize() {
        cityCol.setCellValueFactory(cellData -> cellData.getValue().CityProperty());
        defCol.setCellValueFactory(cellData -> cellData.getValue().DefProperty());
        addedCol.setCellValueFactory(cellData -> cellData.getValue().AddedProperty());
        opt_temp.setItems(FXCollections.observableArrayList("Celcius", "Fahrenheit"));
        opt_temp.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Configuration cfg = new Configuration();
                try {
                    JSONObject stg = cfg.getSetting();
                    stg.getJSONObject("setting").put("unit", (newValue.intValue()==1) ? "F" : "C");
                    cfg.setSetting(stg);
                }catch (Exception e){ e.printStackTrace(); }
            }
        });
        readdata();
    }
    @FXML
    private void handleAdd() throws Exception{
        //buka addPlace
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/AddLocation.fxml"));
        AnchorPane page = (AnchorPane) loader.load();
        Stage dialogStage = new Stage();
        //window
        dialogStage.setTitle("Add Location");
        //
        dialogStage.initStyle(StageStyle.UTILITY);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setResizable(false);
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);
        AddPlaceController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        dialogStage.showAndWait();
        readdata();
    }
    @FXML
    private void handleUp() throws Exception {
        int selectID = TableLocation.getSelectionModel().getSelectedIndex();
        if(selectID >= 0){
            if(selectID==0) return;
            Configuration cfg = new Configuration();
            JSONObject d = cfg.getSetting();
            //tukar nilai dari {index} dan {index - 1}
            JSONObject simpan = d.getJSONArray("locations").getJSONObject(selectID);
            JSONObject simpan2 = d.getJSONArray("locations").getJSONObject(selectID-1);
            d.getJSONArray("locations").put(selectID-1,simpan);
            d.getJSONArray("locations").put(selectID,simpan2);
            //simpan config
            cfg.setSetting(d);
            //reload dan tampilkan ulang data yg telah di ubah
            readdata();
            //fokuskan ke item yg telah diubah
            TableLocation.requestFocus();
            TableLocation.getSelectionModel().select(selectID-1);
            TableLocation.getFocusModel().focus(selectID-1);
        }else {
            //belum pilih item di tableview
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Data Selected");
            alert.setContentText("Please select data from table");
            alert.showAndWait();
        }
    }
    @FXML
    private void handleDefault() throws Exception {
        int selectID = TableLocation.getSelectionModel().getSelectedIndex();
        if(selectID >= 0){
            Configuration cfg = new Configuration();
            JSONObject d = cfg.getSetting();
            //jadikan semua item default = false
            for (int i = 0; i < d.getJSONArray("locations").length(); i++) {
                d.getJSONArray("locations").getJSONObject(i).put("default",false);
            }
            //jadikan item yg dipilih sebagai default = true
            d.getJSONArray("locations").getJSONObject(selectID).put("default",true);
            //simpan config
            cfg.setSetting(d);
            readdata();
        }else {
            //belum pilih item di tableview
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Data Selected");
            alert.setContentText("Please select data from table");
            alert.showAndWait();
        }
    }
    @FXML
    private void handleDelete() throws Exception {
        int selectID = TableLocation.getSelectionModel().getSelectedIndex();
        if(selectID >= 0){
            //remove item dari tableview
            TableLocation.getItems().remove(selectID);
            Configuration cfg = new Configuration();
            //dapatkan data dari locaconfig
            JSONObject d = cfg.getSetting();
            //remove item dari localconfig
            d.getJSONArray("locations").remove(selectID);
            //simpan config
            cfg.setSetting(d);

        }else {
            //belum pilih item di tableview
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Data Selected");
            alert.setContentText("Please select data from table");
            alert.showAndWait();
        }
    }
    public void openTab(int i){
        tab_panel.getSelectionModel().select(i);
    }
    private void readdata(){
        //bersihkan tableview location
        TableLocation.getItems().clear();
        Configuration cfg = new Configuration();
        try {
            for (Object a : cfg.getSetting().getJSONArray("locations")) {
                JSONObject haa = new JSONObject(a.toString());
                //masukan data dari localconfig ke model
                PlaceData.add(new PlaceModel(haa.getString("city"),haa.getString("added"),String.valueOf(haa.getBoolean("default"))));
            }
            opt_temp.getSelectionModel().select(cfg.getUnits().equals("C")?0:1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //masukan semua item dalam model kedalam tableview
        TableLocation.setItems(PlaceData);
    }
}
