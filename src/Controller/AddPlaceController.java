package Controller;

import Model.LocationModel;
import Model.PlaceModel;
import Module.Configuration;
import Module.Requester;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;


public class AddPlaceController {
    private Stage dialogStage;
    // register semua component pada form AddPlace
    @FXML private ProgressIndicator pb_loading;
    @FXML private TableView<LocationModel> tbl_location;
    @FXML private TableColumn<LocationModel,String> cityCol;
    @FXML private TableColumn<LocationModel,String> locCol;
    @FXML private TextField tx_loc;
    private ObservableList<LocationModel> locationdata = FXCollections.observableArrayList();

    //menginisialisasi TableColumn pada Tableview
    @FXML public void initialize() {
        cityCol.setCellValueFactory(cellData -> cellData.getValue().CityProperty());
        locCol.setCellValueFactory(cellData -> cellData.getValue().LocProperty());
        pb_loading.setVisible(false);
    }
    public void setDialogStage(Stage dialogStage){
        this.dialogStage = dialogStage;
    }
    public void handleSearch(){
        //request data dengan mencari data lokasi pada api
        Requester rq = new Requester();
        //loading
        pb_loading.setVisible(true);
        new Thread() {
            public void run() {
                //parse JSON kedalam bentuk object
                try {
                    JSONObject ob = new JSONObject(rq.requestget("http://wioip-api.herokuapp.com/region/" + tx_loc.getText()));
                    for (Object a : ob.getJSONArray("result")) {
                        JSONObject hasil = new JSONObject(a.toString());
                        //masukan data kedalam model
                        locationdata.add(new LocationModel(hasil.getString("city"), hasil.getDouble("lat") + "," + hasil.getDouble("long")));
                    }
                }catch (Exception e){e.printStackTrace();}
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        //muat data dalam model kedalam tableview
                        tbl_location.setItems(locationdata);
                        pb_loading.setVisible(false);
                    }
                });
            }
        }.start();

    }
    public void handleAdd() throws Exception {
        int selectID = tbl_location.getSelectionModel().getSelectedIndex();
        LocationModel selectedItems = tbl_location.getSelectionModel().getSelectedItem();
        if(selectID >= 0){
            Configuration cfg = new Configuration();
            JSONObject setting = cfg.getSetting();
            //check apakah terdapat nama kota yg sama?
            if(!cfg.getCitys().contains(selectedItems.getCity())) {
                //gk ada? maka input kota nya dalam localconfig
                JSONObject newdata = new JSONObject();
                newdata.put("city", selectedItems.getCity());
                newdata.put("default", false);
                newdata.put("added", new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
                setting.getJSONArray("locations").put(newdata);
                //simpan localconfig
                cfg.setSetting(setting);
                //tutup addPlace
                dialogStage.close();
            }
        }else{
            //belum pilih item di tableview
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Data Selected");
            alert.setContentText("Please select data from table");
            alert.showAndWait();
        }
    }
    public void handleCancel(){
        dialogStage.close();
    }
}
