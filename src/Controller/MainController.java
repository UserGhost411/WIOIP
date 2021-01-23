package Controller;

import Module.Requester;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.json.*;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainController {
    @FXML Label lbl_place,lbl_summary,lbl_temp,lbl_humidity,lbl_wind,lbl_press,lbl_upd;
    @FXML LineChart<String,Number> charthourly;
    @FXML LineChart<String,Number> chartdaily;
    @FXML ImageView imgweather;
    @FXML public void initialize() {
        refreshdata();
    }
    @FXML public void handleClose(){
        Platform.exit();
    }
    @FXML public void handleRefresh(){
        refreshdata();
    }
    @FXML
    public void handleAbout() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/About.fxml"));
        AnchorPane page = (AnchorPane) loader.load();
        Stage dialogStage = new Stage();
        dialogStage.setTitle("WIOIP About");
        dialogStage.initStyle(StageStyle.UTILITY);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);
        AboutController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        dialogStage.showAndWait();
    }
    public void refreshdata(){
        XYChart.Series<String, Number> D_tempsH = new XYChart.Series<>();
        XYChart.Series<String, Number> D_tempsL = new XYChart.Series<>();
        XYChart.Series<String, Number> D_windSpeed = new XYChart.Series<>();
        XYChart.Series<String, Number> D_rainChance = new XYChart.Series<>();
        XYChart.Series<String, Number> H_temps = new XYChart.Series<>();
        XYChart.Series<String, Number> H_windSpeed = new XYChart.Series<>();
        XYChart.Series<String, Number> H_rainChance = new XYChart.Series<>();
        D_tempsH.setName("Highest Temperature");
        D_tempsL.setName("Lowest Temperature");
        D_windSpeed.setName("wind Speed");
        D_rainChance.setName("Rain Chance");
        H_temps.setName("Temperature");
        H_windSpeed.setName("wind Speed");
        H_rainChance.setName("Rain Chance");
        chartdaily.setAnimated(true);
        charthourly.setAnimated(true);
        String d = null;///http://ip-api.com/json/");
        try { d = new Requester().requestget("https://weatherextension.com/api/v2/weather/location"); } catch (IOException e) { e.printStackTrace(); }

        JSONObject jsonObject = new JSONObject(d.replace("\\u00b0", ""));

        String icon = jsonObject.getJSONObject("weather").getJSONObject("currently").getString("iconv2").replace("wi ","").trim();
        lbl_place.setText(jsonObject.getJSONObject("currentLocation").getString("location_name"));
        JSONObject current = jsonObject.getJSONObject("weather").getJSONObject("currently");
        lbl_summary.setText(current.getString("summary"));
        lbl_temp.setText(""+(Integer.parseInt(current.getString("temperature"))- 32) * 5/9);
        lbl_humidity.setText("Humidity : "+current.getInt("humidity")+"%");
        lbl_wind.setText("Wind Speed : "+current.getString("windSpeed"));
        lbl_press.setText("Pressure : "+current.getInt("pressure")+" mb");
        lbl_upd.setText("Last Update : "+new SimpleDateFormat("HH:mm").format(new Date()));
        for (Object a : jsonObject.getJSONObject("weather").getJSONObject("daily").getJSONArray("data")) {
            JSONObject hasil = new JSONObject(a.toString());
            D_windSpeed.getData().add(new XYChart.Data(hasil.getString("time"),hasil.getInt("windSpeed")));
            D_tempsH.getData().add(new XYChart.Data(hasil.getString("time"), (hasil.getInt("temperatureHigh")- 32) * 5/9));
            D_tempsL.getData().add(new XYChart.Data(hasil.getString("time"), (hasil.getInt("temperatureLow")- 32) * 5/9));
            D_rainChance.getData().add(new XYChart.Data(hasil.getString("time"), Integer.parseInt(hasil.getString("precipProbability").replace("%",""))));
        }
        for (Object a : jsonObject.getJSONObject("weather").getJSONObject("hourly").getJSONArray("data")) {
            JSONObject hasil = new JSONObject(a.toString());
            H_windSpeed.getData().add(new XYChart.Data(hasil.getString("time"),hasil.getInt("windSpeed")));
            H_temps.getData().add(new XYChart.Data(hasil.getString("time"), (hasil.getInt("temperature")- 32) * 5/9));
            H_rainChance.getData().add(new XYChart.Data(hasil.getString("time"), Integer.parseInt(hasil.getString("precipProbability").replace("%",""))));
        }
        chartdaily.getData().clear();
        charthourly.getData().clear();
        chartdaily.getData().add(D_windSpeed);
        chartdaily.getData().add(D_tempsH);
        chartdaily.getData().add(D_tempsL);
        chartdaily.getData().add(D_rainChance);
        charthourly.getData().add(H_windSpeed);
        charthourly.getData().add(H_temps);
        charthourly.getData().add(H_rainChance);
        BufferedImageTranscoder transcoder = new BufferedImageTranscoder();
        try (InputStream file = getClass().getResourceAsStream("/img/"+icon+".svg")) {
            TranscoderInput transIn = new TranscoderInput(file);
            try {
                transcoder.transcode(transIn, null);
                Image img = SwingFXUtils.toFXImage(transcoder.getBufferedImage(), null);
                imgweather.setImage(img);
            } catch (TranscoderException ex) { ex.printStackTrace(); }
        }
        catch (IOException io) { io.printStackTrace(); }
    }
    public class BufferedImageTranscoder extends ImageTranscoder {
        private BufferedImage img = null;
        @Override
        public BufferedImage createImage(int width, int height) {
            return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
        @Override
        public void writeImage(BufferedImage img, TranscoderOutput to) throws TranscoderException { this.img = img; }
        public BufferedImage getBufferedImage() {
            return img;
        }
    }
}
