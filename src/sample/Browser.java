package sample;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicBoolean;


public class Browser extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(final Stage stage) {
        VBox browserContainer = new VBox();
        HBox controlsContainer = new HBox();

        Button forward = new Button("Вперёд >>");
        Button back = new Button("<< Назад");
        Button historyBtn = new Button("История");
        TextField searchBar = new TextField("Поиск");
        Label statusLabel = new Label();
        ListView<WebHistory.Entry> historyList = new ListView<>();
        searchBar.minWidth(500);
        controlsContainer.getChildren().addAll(back, forward, searchBar, historyBtn);
        HBox.setHgrow(searchBar, Priority.ALWAYS);

        WebView browser = new WebView();
        WebEngine webEngine = browser.getEngine();
        WebHistory history = webEngine.getHistory();
        webEngine.load("https://google.com");

        searchBar.setOnAction(event ->
                webEngine.load(searchBar.getText()));

        webEngine.locationProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> ov, final String oldvalue, final String newvalue) {
                searchBar.setText(newvalue);
            }
        });
        back.setOnAction(event ->
                history.go(-1));
        forward.setOnAction(event ->
                history.go(1));
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observableValue, Worker.State state, Worker.State t1) {
                if (t1 == Worker.State.RUNNING) {
                    statusLabel.setText("Статус: загрузка");
                }
                if (t1 == Worker.State.FAILED) {
                    statusLabel.setText("Статус: ошибка");
                }
                if (t1 == Worker.State.SUCCEEDED) {
                    statusLabel.setText("Статус: Загрузка завершена");
                }
            }
        });

        AtomicBoolean hisVis = new AtomicBoolean(false);
        historyBtn.setOnMouseClicked(event -> {
            historyList.setItems(history.getEntries());
            if (hisVis.get()) {
                browserContainer.getChildren().remove(historyBtn);
                browserContainer.getChildren().remove(controlsContainer);
                browserContainer.getChildren().remove(browser);
                browserContainer.getChildren().remove(statusLabel);
                browserContainer.getChildren().remove(historyList);
                browserContainer.getChildren().addAll(controlsContainer, browser, statusLabel);
                historyList.setVisible(false);
                hisVis.set(false);
            }
            else {
                System.out.println("History");
                browserContainer.getChildren().remove(browser);
                browserContainer.getChildren().add(historyList);
                historyList.setVisible(true);
                hisVis.set(true);
            }

        });
        browserContainer.getChildren().add(controlsContainer);
        browserContainer.getChildren().addAll(browser, statusLabel);
        VBox.setVgrow(browser, Priority.ALWAYS);

        stage.setScene(new Scene(browserContainer));
        stage.setMaximized(true);
        stage.setTitle("JavaFX Browser Sample");
        stage.show();
    }
}