package Controller;

import Model.weatherDailyModel;
import Module.Configuration;
import Module.Requester;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import javafx.scene.image.ImageView;
import org.json.JSONObject;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainController {
    // register semua component pada form main
    @FXML Label lbl_date,lbl_temp,lbl_unit,lbl_place,lbl_feels,lbl_update,lbl_weather,lbl_air,lbl_uv,lbl_humidity,lbl_wind;
    @FXML ImageView iv_weather,iv_bg,iv_about,iv_setting,iv_wind,iv_humidity,iv_refresh;
    @FXML ListView<String> lv_city;
    @FXML ProgressIndicator pb_loading;
    @FXML ProgressBar prog_uv,prog_air;
    @FXML Pane pane_weekly ,pane_daily,pane_air,pane_uv;
    @FXML private TableView<weatherDailyModel> tbl_daily;
    //@FXML private TableColumn<weatherDailyModel,String> DimgCol;
    @FXML private TableColumn<weatherDailyModel,String> DdateCol;
    @FXML private TableColumn<weatherDailyModel,String> DtempCol;
    @FXML private TableColumn<weatherDailyModel,String> DwindCol;
    @FXML private TableColumn<weatherDailyModel,String> DhumCol;
    @FXML private TableView<weatherDailyModel> tbl_weekly;
    //@FXML private TableColumn<weatherDailyModel,String> WimgCol;
    @FXML private TableColumn<weatherDailyModel,String> WdateCol;
    @FXML private TableColumn<weatherDailyModel,String> WtempCol;
    @FXML private TableColumn<weatherDailyModel,String> WwindCol;
    @FXML private TableColumn<weatherDailyModel,String> WhumCol;
    //===================================================================
    private ObservableList<weatherDailyModel> weatherweeklydata  = FXCollections.observableArrayList();
    private ObservableList<weatherDailyModel> weatherdailydata  = FXCollections.observableArrayList();
    private boolean refreshed = false;
    private RotateTransition refrotate;
    //menginisialisasi TableColumn pada Tableview dan memuat data
    @FXML private void initialize() {
        DdateCol.setCellValueFactory(cellData -> cellData.getValue().DateProperty());
        DtempCol.setCellValueFactory(cellData -> cellData.getValue().TempProperty());
        DwindCol.setCellValueFactory(cellData -> cellData.getValue().WindProperty());
        DhumCol.setCellValueFactory(cellData -> cellData.getValue().HumProperty());
        WdateCol.setCellValueFactory(cellData -> cellData.getValue().DateProperty());
        WtempCol.setCellValueFactory(cellData -> cellData.getValue().TempProperty());
        WwindCol.setCellValueFactory(cellData -> cellData.getValue().WindProperty());
        WhumCol.setCellValueFactory(cellData -> cellData.getValue().HumProperty());
        iv_refresh.setImage(svgset("/res/img/icon/refresh.svg"));
        iv_humidity.setImage(svgset("/res/img/icon/place.svg"));
        iv_wind.setImage(svgset("/res/img/icon/wind.svg"));
        iv_setting.setImage(svgset("/res/img/icon/gear.svg"));
        iv_about.setImage(svgset("/res/img/icon/info.svg"));
        double y_about = iv_about.getX();
        iv_setting.setSmooth(true);
        TranslateTransition translation = new TranslateTransition(Duration.seconds(0.1), iv_about);
        translation.interpolatorProperty().set(Interpolator.SPLINE(.1, .1, .3, .3));
        translation.setByY(-10);
        translation.setAutoReverse(true);
        translation.setCycleCount(2);
        RotateTransition rotation = new RotateTransition(Duration.seconds(0.5), iv_setting);
        rotation.setCycleCount(Animation.INDEFINITE);
        rotation.setByAngle(180);
        refrotate = new RotateTransition(Duration.seconds(0.5), iv_refresh);
        refrotate.setCycleCount(Animation.INDEFINITE);
        refrotate.setByAngle(180);
        iv_setting.setOnMouseEntered(e -> rotation.play());
        iv_setting.setOnMouseExited(e -> rotation.pause());
        iv_refresh.setOnMouseEntered(e -> refrotate.play());
        iv_refresh.setOnMouseExited(e ->{ if(!refreshed) refrotate.pause(); });
        iv_about.setOnMouseEntered(e -> translation.play());
        handleDaily();
        refreshdata();
    }
    @FXML private void handleDaily(){
        pane_weekly.setVisible(false);
        pane_daily.setVisible(true);
        pane_air.setVisible(false);
        pane_uv.setVisible(false);
    }
    @FXML private void handleWeekly(){
        pane_weekly.setVisible(true);
        pane_daily.setVisible(false);
        pane_air.setVisible(false);
        pane_uv.setVisible(false);
    }
    @FXML private void handleAir(){
        pane_weekly.setVisible(false);
        pane_daily.setVisible(false);
        pane_air.setVisible(true);
        pane_uv.setVisible(false);
    }
    @FXML private void handleUV(){
        pane_weekly.setVisible(false);
        pane_daily.setVisible(false);
        pane_air.setVisible(false);
        pane_uv.setVisible(true);
    }
    @FXML private void handleClose(){
        Platform.exit();
    }
    @FXML private void handleRefresh(){ refreshdata(); }
    //Handle click pada about
    @FXML private void handleAbout() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/About.fxml"));
        AnchorPane page = (AnchorPane) loader.load();
        Stage dialogStage = new Stage();
        dialogStage.setTitle("WIOIP About");
        dialogStage.initStyle(StageStyle.UTILITY);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setResizable(false);
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);
        AboutController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        dialogStage.showAndWait();
    }
    //Handle click pada setting
    @FXML private void handleSetting() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Settings.fxml"));
        AnchorPane page = (AnchorPane) loader.load();
        Stage dialogStage = new Stage();
        dialogStage.setTitle("WIOIP Place Settings");
        dialogStage.initStyle(StageStyle.UTILITY);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setResizable(false);
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);
        SettingsController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.openTab(0);
        dialogStage.showAndWait();
        refreshdata();
    }
    //Handle click pada add city
    @FXML private void handleLocations() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Settings.fxml"));
        AnchorPane page = (AnchorPane) loader.load();
        Stage dialogStage = new Stage();
        dialogStage.setTitle("WIOIP Place Settings");
        dialogStage.initStyle(StageStyle.UTILITY);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setResizable(false);
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);
        SettingsController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.openTab(1);
        dialogStage.showAndWait();
        refreshdata();
    }
    //Handle click pada listview kota
    @FXML private void handleMouseClick(MouseEvent arg0) throws IOException {
        //double click yak
        if(arg0.getClickCount()==2){
            //dapatkan kota yg di click
            String cityselected = lv_city.getSelectionModel().getSelectedItem();
            Configuration cfg = new Configuration();
            JSONObject d = cfg.getSetting();
            //load kota yg di click kedalam default
            for (int i = 0; i < d.getJSONArray("locations").length(); i++) {
                boolean samecity = d.getJSONArray("locations").getJSONObject(i).getString("city").equalsIgnoreCase(cityselected);
                d.getJSONArray("locations").getJSONObject(i).put("default",samecity );
            }
            //simpan localconfig
            cfg.setSetting(d);
            //muat ulang data
            refreshdata();
        }
    }
    private void refreshdata() {
        //bersihkan semua TableView
        tbl_daily.getItems().clear();
        tbl_weekly.getItems().clear();
        //Set BG (biar gk keliatan kosong saat Run awal / loading)
        if (iv_bg.getImage()==null) setbg("13n");
        Configuration cfg = new Configuration();
        //buat list dari kota yg terdapat di localconfig
        ObservableList<String> items = FXCollections.observableList(cfg.getCitys(3));
        //set item dari list kota2
        lv_city.setItems(items);
        //saat nya main main dengan listview
        lv_city.setCellFactory(param -> new ListCell<String>() {
            @Override
            public void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty) {
                    //kalo kosong , kosongin aja , jangan lupa di transparent yak
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: rgba(255, 255, 255, 0);");
                } else {
                    //buat container VBOX
                    VBox hBox = new VBox(5);
                    hBox.setAlignment(Pos.BOTTOM_CENTER);
                    //tambahkan imageview dan label
                    //https://picsum.photos/1200/800
                    Label lbl = new Label(name);
                    lbl.setFont(new Font("Arial", 16));
                    lbl.setStyle("-fx-font-weight: bold;-fx-text-fill: White;");
                    hBox.getChildren().addAll(new ImageView(new Image("https://picsum.photos/160/260", true)), lbl);
                    //set containter kedalam cell listview
                    setGraphic(hBox);
                    //jangan lupa TRANSPARENT GAN
                    setStyle("-fx-background-color: rgba(255, 255, 255, 0);");
                    }
            }
        });
        //anggap aja loading yekan
        refrotate.play();
        refreshed=true;
        pb_loading.setVisible(true);
        new Thread() {
            public void run() {
                //Thread Run - > BG Process
                try {
                    int timeout = 5000;
                    InetAddress[] addresses = InetAddress.getAllByName("www.google.com");
                    for (InetAddress address : addresses) {
                        if (!address.isReachable(timeout)) {
                            panggilerror();
                            return;
                        }
                    }
                }catch (Exception e){ e.printStackTrace(); }
                Requester rq = new Requester();
                Configuration cfg = new Configuration();
                try {
                    String kota = cfg.getdefaultLocation().getString("city").contains(" ")?cfg.getdefaultLocation().getString("city").substring(0,cfg.getdefaultLocation().getString("city").indexOf(" ")):cfg.getdefaultLocation().getString("city");
                    //ambil data pada api dan parse
                    JSONObject firstApi = new JSONObject(rq.requestget("https://wioip-api.herokuapp.com/weather/all/" + kota,20,30));
                    //ambil data current dan forecast
                    final JSONObject current = firstApi.getJSONObject("current");
                    final JSONObject forecast = firstApi.getJSONObject("forecast");
                    //ambil api dari waqi (untuk UV dan Air Polution)
                    final JSONObject secondApi = new JSONObject(rq.requestget("https://api.waqi.info/feed/geo:" + current.getJSONObject("coord").getInt("lat") + ";" + current.getJSONObject("coord").getInt("lon") + "/?token=9311857ccf3e488b86121268b016304e3c5ca3b4",20,30));
                    Platform.runLater(new Runnable() {
                        //Thread Run -> to javafx form
                        public void run() {
                            // check uv dan air polution dari secondAPI
                            double air_level = secondApi.getJSONObject("data").getJSONObject("iaqi").getJSONObject("pm10").getInt("v");
                            //udah nilai dalam progressbar (0 sampai 1) - index polusi terburuk pada 200 jadi pakai rumus persentase {air_level}/200*1
                            prog_air.setProgress((air_level / 200) * 1);
                            //tampilkan nilai ke label
                            lbl_air.setText("Air Polution : "+air_level+" AQI");
                            //ambil data UV dari array forecast dalam 2nd api
                            for (Object a :  secondApi.getJSONObject("data").getJSONObject("forecast").getJSONObject("daily").getJSONArray("uvi")) {
                                JSONObject hasil = new JSONObject(a.toString());
                                //temukan data sesuai tanggal sekarang dong
                                if(hasil.getString("day").equalsIgnoreCase(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))){
                                    //found it? , cari rata2 (udah tersedia dalam object)
                                    double uv_level = hasil.getDouble("avg");
                                    //tampilkan
                                    lbl_uv.setText("Index UV : "+uv_level);
                                    //UV index terparah 11 = udah auto kanker bruh , sama kyk diatas , pake rumus persentase {air_level}/11*1
                                    prog_uv.setProgress((uv_level / 11) * 1);
                                }
                            }
                            // isi label
                            String checkloc = firstApi.getString("country").equals(firstApi.getString("region")) ? firstApi.getString("region")+", "+current.getJSONObject("sys").getString("country"):firstApi.getString("region")+", "+firstApi.getString("country");
                            lbl_place.setText(current.getString("name") + ", " + checkloc);
                            lbl_temp.setText(String.valueOf(termocheck(current.getJSONObject("main").getInt("temp"))));
                            lbl_feels.setText("Feels Like "+termocheck(current.getJSONObject("main").getInt("feels_like"))+" °"+cfg.getUnits());
                            lbl_update.setText("Update "+new SimpleDateFormat("HH:mm").format(new Date()));
                            lbl_unit.setText("°"+cfg.getUnits());
                            lbl_date.setText(new SimpleDateFormat("EEEE, d MMMM yyyy").format(new Date()));
                            lbl_weather.setText(current.getJSONArray("weather").getJSONObject(0).getString("description"));
                            lbl_humidity.setText(current.getJSONObject("main").get("humidity").toString()+"%");
                            lbl_wind.setText(current.getJSONObject("wind").get("speed").toString()+" km/h");
                            iv_wind.setRotate((current.getJSONObject("wind").getInt("deg")-50)-180);
                            //load data ke tableview
                            List<String> df = new ArrayList<>();
                            //ambil data list forecast dari first api
                            for (Object a : forecast.getJSONArray("list")) {
                                JSONObject hasil = new JSONObject(a.toString());
                                JSONObject main = hasil.getJSONObject("main");
                                //checking yak , buat array simple yg nanti akan seleksi tgl berapa aja yg udah masuk (kalo udah masuk , gk perlu masuk lagi)
                                if(!df.contains(hasil.getString("dt_txt").substring(0,10))){
                                    //parse data kedalam model
                                    weatherweeklydata.add(new weatherDailyModel("",hasil.getString("dt_txt").substring(0,10),termocheck(main.getInt("temp"))+" "+cfg.getUnits(),hasil.getJSONObject("wind").getDouble("speed")+" km/h",main.getInt("humidity")+"%"));
                                    //ok tgl ini telah masuk , ayo kita siapkan kedalam selector (biar ak masuk lagi - duplicate day)
                                    df.add(hasil.getString("dt_txt").substring(0,10));
                                }
                                //cari tanggal yg sama untuk data harian dong (beda nya terdapat data di tiap jam)
                                if(hasil.getString("dt_txt").startsWith(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) {
                                    //parse data kedalam model
                                    weatherdailydata.add(new weatherDailyModel("",hasil.getString("dt_txt"),termocheck(main.getInt("temp"))+" "+cfg.getUnits(),hasil.getJSONObject("wind").getDouble("speed")+" km/h",main.getInt("humidity")+"%"));
                                }
                            }
                            //data udah diparsekan , saat nya memuat data dari model kedalam tableview
                            tbl_weekly.setItems(weatherweeklydata);
                            tbl_daily.setItems(weatherdailydata);
                            //ubah icon dan background berdasarkan api
                            iv_weather.setImage(svgset("/res/img/icon/" + geticon(current.getJSONArray("weather").getJSONObject(0).getString("description"))));
                            setbg(current.getJSONArray("weather").getJSONObject(0).getString("icon"));
                            //dah loading selesai
                            refrotate.pause();
                            refreshed=false;
                            pb_loading.setVisible(false);
                        }
                    });
                }catch (Exception e){e.printStackTrace();}
            }
        }.start();
    }
    private void panggilerror(){
        Platform.runLater(new Runnable() {
            //Thread Run -> to javafx form
            public void run() {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Error.fxml"));
                AnchorPane page = null;
                try {
                    page = (AnchorPane) loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Stage dialogStage = new Stage();
                dialogStage.setTitle("WIOIP TIC TAC TOE");
                dialogStage.initStyle(StageStyle.UTILITY);
                dialogStage.initModality(Modality.WINDOW_MODAL);
                Scene scene = new Scene(page);
                dialogStage.setScene(scene);
                ErrorController controller = loader.getController();
                controller.setDialogStage(dialogStage);
                dialogStage.showAndWait();
            }});
    }
    //Memuat Background pada aplikasi
    private void setbg(String wa){
        iv_bg.setImage(new Image("https://picsum.photos/1400/800",true));
        iv_bg.setPreserveRatio(true);
        //color adjust = biar bg nya agak gelap dikit (tulisan yg putih soalnya hehe)
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(-0.5);
        iv_bg.setEffect(colorAdjust);
    }
    //merender file svg menjadi Image dengan bantuan class Transcoder
    private Image svgset(String path){
        BufferedImageTranscoder transcoder = new BufferedImageTranscoder();
        try (InputStream file = getClass().getResourceAsStream(path)) {
            TranscoderInput transIn = new TranscoderInput(file);
            try {
                transcoder.transcode(transIn, null);
                Image img = SwingFXUtils.toFXImage(transcoder.getBufferedImage(), null);
                return img;
            } catch (TranscoderException ex) { ex.printStackTrace(); }
        } catch (IOException io) { io.printStackTrace(); }
        return null;
    }
    //mengubah suhu C to F dan sebalikanya
    private int termocheck(int h){
        Configuration cfg = new Configuration();
        //dev awal mles check suhu dari api apakah c or f (dengan offset 50c itu panas banget dan 50f itu dingin tapi gk beku)
        if(h>50){
            return (cfg.getUnits().equals("C"))?((h- 32) * 5/9):h;
        }else{
            return (cfg.getUnits().equals("C"))?h:((h*9/5) + 32);
        }
    }

    //mendapatkan icon berdasarkan deksripsi
    public String geticon(String w){
        if(w.contains("clear sky")){
            return "clear-sky-o.svg";
        }else if(w.contains("few clouds")){
            return "few-clouds-o.svg";
        }else if(w.contains("scattered clouds")){
            return "Shape_11.svg";
        }else if(w.contains("broken clouds")){
            return "Shape_11.svg";
        }else if(w.contains("shower rain")){
            return "shower-rain-o.svg";
        }else if(w.contains("rain")){
            return "rain-o.svg";
        }else if(w.contains("thunderstorm")){
            return "thunderstorm-o.svg";
        }else if(w.contains("snow")){
            return "snowflake-o.svg";
        }else if(w.contains("mist")){
            return "mist-o.svg";
        }else if(w.contains("overcast clouds")){
            return "Shape_11.svg";
        }else{
            return "clear-sky-o.svg";
        }
    }
    //class khusus untuk memuat svg ke Image
    public class BufferedImageTranscoder extends ImageTranscoder {
        private BufferedImage img = null;
        @Override public BufferedImage createImage(int width, int height) { return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB); }
        @Override public void writeImage(BufferedImage img, TranscoderOutput to) throws TranscoderException { this.img = img; }
        public BufferedImage getBufferedImage() {
            return img;
        }
    }
}
