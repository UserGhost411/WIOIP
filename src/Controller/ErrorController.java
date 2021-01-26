package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import java.util.Random;

public class ErrorController {
    private Stage dialogStage;
    public void setDialogStage(Stage dialogStage){ this.dialogStage = dialogStage; }
    // register semua component pada form About
    @FXML private Button btn_a1,btn_a2,btn_a3,btn_b1,btn_b2,btn_b3,btn_c1,btn_c2,btn_c3;
    @FXML private ListView<String> lv_chat;
    private int dox = 0;
    private boolean playing = false;
    @FXML public void initialize() {
        addlog("Hai Player");
        addlog("Selamat Datang di Tic Tac Toe");
        addlog("Kamu tau kenapa kamu ada disini?");
        addlog("Ya benar ,karena kami mendeteksi");
        addlog("koneksi internet mu terputus");
        addlog("soo , lets play");
        btnclear();
        btnstate(false);
        btn_a1.setOnAction(e -> taruh(true,"a1"));
        btn_a2.setOnAction(e -> taruh(true,"a2"));
        btn_a3.setOnAction(e -> taruh(true,"a3"));
        btn_b1.setOnAction(e -> taruh(true,"b1"));
        btn_b2.setOnAction(e -> taruh(true,"b2"));
        btn_b3.setOnAction(e -> taruh(true,"b3"));
        btn_c1.setOnAction(e -> taruh(true,"c1"));
        btn_c2.setOnAction(e -> taruh(true,"c2"));
        btn_c3.setOnAction(e -> taruh(true,"c3"));
    }
    private void taruh(boolean player,String where){
        if(!playing){
            playing=true;
            lv_chat.getItems().clear();
            btnclear();
            dox = 0;
        };
        dox+=1;
        if(player) {
            taruhbidak(player,where);
        }else{
            String[] a = {"a","b","c"};
            while (true){

                String whe = (botthing().isEmpty())?a[randomize(2)]+""+(randomize(2)+1):botthing();
                System.out.println("what:"+whe);
                if(taruhbidak(player,whe)) break;
            }
        }
    }
    private boolean taruhbidak(Boolean player,String where){
        boolean d = bidak(player,where);
        String sapa = player ? "Player" : "Bot";
        if(d){
            addlog(sapa+" use "+where);
        }

        if (iswinner()) {
            playing = false;
            addlog(sapa + "winning");
            btnstate(false);
        }else{
            if(dox>8){
                playing = false;
                addlog("DRAW");
                btnstate(false);
            }
            if(dox<9 && player) taruh(false,"idk");
        }
        return d;
    }
    private int randomize(int max){
        Random rand = new Random();
        return rand.nextInt(max+1);
    }
    private String botthing(){
        if (checkrule1(btn_a3.getText() , btn_a2.getText()) && btn_a1.getText().isEmpty()) return "a1";
        if (checkrule1(btn_a3.getText() , btn_a1.getText()) && btn_a2.getText().isEmpty()) return "a2";
        if (checkrule1(btn_a1.getText() , btn_a2.getText()) && btn_a3.getText().isEmpty()) return "a3";

        if (checkrule1(btn_b3.getText() , btn_b2.getText()) && btn_b1.getText().isEmpty()) return "b1";
        if (checkrule1(btn_b1.getText() , btn_b3.getText()) && btn_b2.getText().isEmpty()) return "b2";
        if (checkrule1(btn_b1.getText() , btn_b2.getText()) && btn_b3.getText().isEmpty()) return "b3";

        if (checkrule1(btn_c3.getText() , btn_c2.getText()) && btn_c1.getText().isEmpty()) return "c1";
        if (checkrule1(btn_c1.getText() , btn_c3.getText()) && btn_c2.getText().isEmpty()) return "c2";
        if (checkrule1(btn_c1.getText() , btn_c2.getText()) && btn_c3.getText().isEmpty()) return "c3";

        if (checkrule1(btn_a1.getText() , btn_c3.getText()) && btn_b2.getText().isEmpty()) return "b2";
        if (checkrule1(btn_b2.getText() , btn_c3.getText()) && btn_a1.getText().isEmpty()) return "a1";
        if (checkrule1(btn_a1.getText() , btn_b2.getText()) && btn_c3.getText().isEmpty()) return "c3";

        if (checkrule1(btn_c1.getText() , btn_a3.getText()) && btn_b2.getText().isEmpty()) return "b2";
        if (checkrule1(btn_b2.getText() , btn_a3.getText()) && btn_c1.getText().isEmpty()) return "c1";
        if (checkrule1(btn_c1.getText() , btn_b2.getText()) && btn_a3.getText().isEmpty()) return "a3";

        return "";
    }
    private Boolean iswinner() {
        return (checkrule(btn_a1.getText() , btn_a2.getText() , btn_a3.getText())) ||
                (checkrule(btn_a1.getText() , btn_b2.getText() , btn_c3.getText())) ||
                (checkrule(btn_a3.getText() , btn_b2.getText() , btn_c1.getText())) ||
                (checkrule(btn_a1.getText() , btn_b1.getText() , btn_c1.getText())) ||
                (checkrule(btn_c1.getText() , btn_c2.getText() , btn_c3.getText())) ||
                (checkrule(btn_a3.getText() , btn_b3.getText(), btn_c3.getText())) ||
                (checkrule(btn_a2.getText() , btn_b2.getText() , btn_c2.getText())) ||
                (checkrule(btn_b1.getText() , btn_b2.getText() , btn_b3.getText()));
    }
    private boolean checkrule(String a,String b,String c){
        if(a=="" || b=="" || c=="") return false;
        if(a==b && b==c && a == c) return true;
        return false;
    }
    private boolean checkrule1(String a,String b){
        if(a=="" || b=="") return false;
        if(a==b ) return true;
        return false;
    }
    private Boolean bidak(Boolean player,String where){
        String sym = (player)?"O":"X";
        switch (where){
            case "a1":
                if(btn_a1.isDisabled()) return false;
                btn_a1.setText(sym);
                btn_a1.setDisable(true);
                return true;
            case "a2":
                if(btn_a2.isDisabled()) return false;
                btn_a2.setText(sym);
                btn_a2.setDisable(true);
                return true;
            case "a3":
                if(btn_a3.isDisabled()) return false;
                btn_a3.setText(sym);
                btn_a3.setDisable(true);
                return true;
            case "b1":
                if(btn_b1.isDisabled()) return false;
                btn_b1.setText(sym);
                btn_b1.setDisable(true);
                return true;
            case "b2":
                if(btn_b2.isDisabled()) return false;
                btn_b2.setText(sym);
                btn_b2.setDisable(true);
                return true;
            case "b3":
                if(btn_b3.isDisabled()) return false;
                btn_b3.setText(sym);
                btn_b3.setDisable(true);
                return true;
            case "c1":
                if(btn_c1.isDisabled()) return false;
                btn_c1.setText(sym);
                btn_c1.setDisable(true);
                return true;
            case "c2":
                if(btn_c2.isDisabled()) return false;
                btn_c2.setText(sym);
                btn_c2.setDisable(true);
                return true;
            case "c3":
                if(btn_c3.isDisabled()) return false;
                btn_c3.setText(sym);
                btn_c3.setDisable(true);
                return true;
            default:
                return false;
        }
    }
    private void addlog(String l){
        lv_chat.getItems().add(l);
        lv_chat.requestFocus();
        lv_chat.getSelectionModel().select(lv_chat.getItems().size());
    }
    private void btnstate(Boolean x){
        btn_a1.setDisable(x);
        btn_a2.setDisable(x);
        btn_a3.setDisable(x);
        btn_b1.setDisable(x);
        btn_b2.setDisable(x);
        btn_b3.setDisable(x);
        btn_c1.setDisable(x);
        btn_c2.setDisable(x);
        btn_c3.setDisable(x);
    }
    private void btnclear(){
        btn_a1.setText("");
        btn_a2.setText("");
        btn_a3.setText("");
        btn_b1.setText("");
        btn_b2.setText("");
        btn_b3.setText("");
        btn_c1.setText("");
        btn_c2.setText("");
        btn_c3.setText("");
    }
}
