package ru.mauveferret;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.InputStream;


public class GUI extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("fxml/Root.fxml"));
        primaryStage.setTitle("ISInCa - Ion Surface Interaction Calculation "+Main.getVersion());
        primaryStage.getIcons().add(new Image( Main.class.getResourceAsStream( "pics/CrocoLogo.png" )));
        primaryStage.setScene(new Scene(root, 800, 520));
        primaryStage.show();
    }

    public static void main(String[] args) {

        launch(args);
    }


    public void showGraph(double spectra[], double E0, double dE, String name)
    {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("ENERGY SPECTRUM");
        dialogStage.getIcons().add(new Image( Main.class.getResourceAsStream( "pics/CrocoLogo.png" )));
        dialogStage.initModality(Modality.NONE);
        NumberAxis x = new NumberAxis();
        NumberAxis y = new NumberAxis();
        LineChart<Number, Number> spectra1 = new LineChart<Number, Number>(x,y);
        XYChart.Series series1 = new XYChart.Series();
        series1.setName(name);
        ObservableList<XYChart.Data> datas = FXCollections.observableArrayList();
        for(int i=0; i<=(int) Math.round(E0 / dE); i++){
            datas.add(new XYChart.Data(i*dE,spectra[i]));
        }

        series1.setData(datas);

        Scene scene = new Scene(spectra1, 600,600);
        spectra1.getData().add(series1);
        dialogStage.setScene(scene);
        dialogStage.show();

    }
    public void showHelpPage(String pictureName)
    {
        Class<?> clazz = this.getClass();
        Stage HelpStage = new Stage();
        HelpStage.getIcons().add(new Image( Main.class.getResourceAsStream( "pics/CrocoLogo.png" )));
        HelpStage.setTitle("Help");
        HelpStage.initModality(Modality.WINDOW_MODAL);
        InputStream input2 = clazz.getResourceAsStream(pictureName);
        Image image2 = new Image(input2, 900, 600, false, true);
        ImageView imageView2 = new ImageView(image2);
        FlowPane root = new FlowPane();
        //root.setPadding(new Insets(20));
        root.getChildren().add(imageView2);
        Scene scene2 = new Scene(root,900,600);
        HelpStage.setScene(scene2);
        HelpStage.setResizable(false);
        HelpStage.show();
    }
    public void showNotification(String notificationText )
    {
        Stage NotificationStage = new Stage();
        NotificationStage.getIcons().add(new Image( Main.class.getResourceAsStream( "pics/CrocoLogo.png" )));
        NotificationStage.setTitle("Notification");
        NotificationStage.initModality(Modality.NONE);
        Label notification = new Label();
        notification.setText(""+notificationText);
        StackPane root = new StackPane();
        root.getChildren().add(notification);
        Scene scene2 = new Scene(root,300,70);
        NotificationStage.setScene(scene2);
        NotificationStage.show();
    }

    public void showNotificationAboutFile()
    {
        Stage NotificationStage = new Stage();
        NotificationStage.getIcons().add(new Image( Main.class.getResourceAsStream( "pics/CrocoLogo.png" )));
        NotificationStage.setTitle("Notification");
        NotificationStage.initModality(Modality.NONE);
        Label notification = new Label();
        notification.setText("Choose file!");
        StackPane root = new StackPane();
        root.getChildren().add(notification);
        Scene scene2 = new Scene(root,300,70);
        NotificationStage.setScene(scene2);
        NotificationStage.show();
    }
    public void showNotificationAboutFileType()
    {
        Stage NotificationStage = new Stage();
        NotificationStage.getIcons().add(new Image( Main.class.getResourceAsStream( "pics/CrocoLogo.png" )));
        NotificationStage.setTitle("Notification");
        NotificationStage.initModality(Modality.NONE);
        Label notification = new Label();
        notification.setText("This is not the file you are looking for!");
        StackPane root = new StackPane();
        root.getChildren().add(notification);
        Scene scene2 = new Scene(root,300,70);
        NotificationStage.setScene(scene2);
        NotificationStage.show();
    }
}
