package Module;
import org.json.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Configuration {
    public JSONObject getSetting() throws FileNotFoundException {
        checkfile("wioip.json");
        String temp = "";
        File myObj = new File("wioip.json");
        Scanner myReader = new Scanner(myObj);
        while (myReader.hasNextLine()) {
            temp += myReader.nextLine();
        }
        myReader.close();
        return new JSONObject(temp);
    }
    public void setSetting(JSONObject settings) throws IOException {
        checkfile("wioip.json");
        FileWriter myWriter = new FileWriter("wioip.json");
        myWriter.write(settings.toString());
        myWriter.close();
    }
    public JSONObject getdefaultLocation() throws FileNotFoundException {
        for (Object a : getSetting().getJSONArray("locations")) {
            JSONObject haa = new JSONObject(a.toString());
            if(haa.getBoolean("default")) return haa;
        }
        return new JSONObject("{\"city\":\"surabaya\",\"default\":true,\"added\":\"19/01/2021\"}");
    }
    public String getUnits() {
        try {
            return getSetting().getJSONObject("setting").getString("unit");
        } catch (Exception e) { e.printStackTrace();}
        return "C";
    }
    public List<String> getCitys(int max) {
        return this.getcitys(max);
    }
    public List<String> getCitys() {
        return this.getcitys(999);
    }
    private List<String> getcitys(int max){
        List<String> tmp = new ArrayList<>();
        try {
            for (Object a : getSetting().getJSONArray("locations")) {
                JSONObject haa = new JSONObject(a.toString());
                if(tmp.size()<max) tmp.add(haa.getString("city"));
            }
            return tmp;
        } catch (FileNotFoundException e) { e.printStackTrace(); }
        return null;
    }
    public void checkfile(String fn){
        try {
            File myObj = new File(fn);
            if (!myObj.exists()) {
                myObj.createNewFile();
                FileWriter myWriter = new FileWriter(fn);
                myWriter.write("{\"setting\"{\"unit\":\"C\"},\"locations\":[{\"city\":\"surabaya\",\"default\":true,\"added\":\"19/01/2021\"}]}");
                myWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
