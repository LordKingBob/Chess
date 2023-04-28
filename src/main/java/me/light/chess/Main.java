package me.light.chess;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;

public class Main extends Application 
{ 
  @Override
  public void start(Stage primaryStage) {
    VBox vbox = new VBox(4);
    Button newGame = new Button("New Game");
    Button load = new Button("Load Game");

    newGame.setOnMousePressed(e -> primaryStage.setScene(new Scene(new GameBoard(primaryStage, "new_game"), 400, 400)));
    load.setOnMousePressed(e -> primaryStage.setScene(new Scene(new GameBoard(primaryStage, "load"), 400, 400)));
    
    vbox.getChildren().addAll(newGame, load);
    Scene scene = new Scene(vbox, 300, 200);
    primaryStage.setTitle("Chess");
    primaryStage.setScene(scene);
    primaryStage.show();
  }  
    
  public static void main(String[] args) {
    launch(args);
  }
} 
