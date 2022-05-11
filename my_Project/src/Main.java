
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.swing.text.html.ImageView;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.io.IOException;
import java.util.Scanner;

public class Main extends Application {
    Scanner sc = new Scanner(System.in);
    @FXML
    ImageView img_view;
    @FXML
    ListView ListVF;
    @FXML
    ListView ListVT;
    @FXML
    Button translate;
    @FXML
    Button delete;
    @FXML
    TextArea inArea;
    @FXML
    TextArea outArea;
    @FXML
    Button dbOutButton;
    @FXML
    TextArea dbOutArea;
    @FXML
    CheckBox checkBox;
    @FXML
    Button firstDel;
    @FXML
    TextField idNumber;
    @FXML
    Button allDel;
    @FXML
    Button inform;
    Statement stmt;
    Connection conn;

    static String dbName = "LangArray.db";
    int id = 0;
    String otLang = "en";
    String toLang = "ru";
    String text = "";
    String res = "";
    String arr2[] = {"Русский", "Английский", "Немецкий", "Французкий","Китайский","Испанский"};
    String arr[] = {"[Русский]", "[Английский]", "[Немецкий]", "[Французкий]", "[Китайский]", "[Испанский]"};
    String arr1[] = {"ru", "en", "de", "fr", "zh","es"};

    public static void main(String[] args) {
        launch();
    }
    public void initialize() throws SQLException, FileNotFoundException {
        ObservableList<String> observableList = FXCollections.observableArrayList(arr2);
        ListVF.setItems(observableList);
        ListVT.setItems(observableList);
        crNewDataBase();
        crNewTable();


    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        Scene sc = new Scene(root);
        primaryStage.setTitle("Переводчик");
        primaryStage.setScene(sc);
        primaryStage.show();


    }

    public void crNewDataBase() throws SQLException {
        String url = "jdbc:sqlite:C:/Users/user/Desktop/SQLITE/" + dbName;
        Connection conn = DriverManager.getConnection(url);
        try {

            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();

            }
        } catch (Exception e) {
            System.out.println("Что то не так.");
        }
    }

    static String url = "jdbc:sqlite:C:/Users/user/Desktop/SQLITE/" + dbName;
    public  void crNewTable(){

        String sql = "CREATE TABLE IF NOT EXISTS langarray(\n"
                + " id integer PRIMARY KEY,\n"
                + " LangFrom text,\n"
                + " LangTo text,\n"
                + " TextIn text,\n"
                + " TextOut text \n"
                + ");";
        try {
            conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            stmt.execute(sql);

        } catch (Exception e) {
            System.out.println("Что то не так2.");
            System.out.println(e);
        }
    }

    public void trans(ActionEvent actionEvent) throws IOException {
        otLang = String.valueOf(ListVF.getSelectionModel().getSelectedItems());

        for (int i = 0; i < arr.length; i++) {
            if (otLang.equals(arr[i])) {
                otLang = arr1[i];

                break;
            }
        }
        toLang = String.valueOf(ListVT.getSelectionModel().getSelectedItems());
        for (int i = 0; i < arr.length; i++) {
            if (toLang.equals(arr[i])) {
                toLang = arr1[i];

                break;
            }
        }
        text = inArea.getText();
        res = Translator.translate(otLang, toLang, text);
        outArea.setText(res);
        insert(otLang, toLang, text, res);
    }

     public void insert(String langot, String langto, String textin, String textout) {
         if (checkBox.isSelected() == true) {
             String sql = "INSERT INTO langarray(LangFrom, LangTo, TextIn, TextOut) VALUES(?,?,?,?)";

             try {
                 PreparedStatement pstmt = conn.prepareStatement(sql);

                 pstmt.setString(1, langot);

                 pstmt.setString(2, langto);

                 pstmt.setString(3, textin);

                 pstmt.setString(4, textout);
                 pstmt.executeUpdate();
             } catch (Exception e) {
                 System.out.println(e);
             }

         }
     }

    public void del(ActionEvent actionEvent) {
        inArea.setText("");
        outArea.setText("");
    }

    public void dbVisible(ActionEvent actionEvent) {

        String sql = "SELECT * FROM langarray";
        String resdb ="";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            res = "";
            while (rs.next()){
            res = res + "№_"+rs.getInt("id")+"\t"+"  "+
                        rs.getString("LangFrom")+"\t"+
                        rs.getString("LangTo")+"\t"+
                        rs.getString("TextIn")+"\t"+
                        rs.getString("TextOut")+"\n";
            }

            dbOutArea.setText(res);
        }
        catch (Exception e){
            System.out.println("Не читается");
        }
    }

    public void firstDel(ActionEvent actionEvent) {
        String text1;
        // Получение номера строки для удаления.
        int id1 = Integer.parseInt(idNumber.getText());

        String sql = "DELETE FROM langarray WHERE id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,id1);
            pstmt.executeUpdate();
        } catch (SQLException e) {

        }

    }

    public void allDel(ActionEvent actionEvent) throws SQLException {
        conn = DriverManager.getConnection(url);
        stmt = conn.createStatement();
        conn.setAutoCommit(false);
        try {
            String sqlCommand = "DROP TABLE langarray";
            stmt.executeUpdate(sqlCommand);
            stmt.close();
            conn.commit();
            conn.close();
            crNewTable();
            createDialog("Системная информация","База данных успешно очищена","Нажмите ОК для подтверждения.");

        }
        catch (SQLException e){
            System.out.println(e);

        }
    }

    public void createDialog(String t, String h, String c){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(t);
        dialog.setHeaderText(h);
        dialog.setContentText(c);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.show();
    }
    
    public void inform(ActionEvent actionEvent) {
        createDialog("О программе!","Приложение на Java, интерфейс JavaFX, база данных SQLite, сервер script.google.com","Создатель: Малышев Илья Александрович"+"\n"+
                "Моя почта: mia74chel@mail.ru");
    }
}
