package me.leopold.hubert.b.ihm.projet;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import me.leopold.hubert.b.ihm.projet.configs.ConfigManager;


public class Main extends Application {
     
    public static void main(String[] args) 
    {
    	if(!System.getProperty("file.encoding").equalsIgnoreCase("UTF-8")) {
    		File jar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			try {
				Runtime.getRuntime().exec("java -Dfile.encoding=UTF-8 -jar "+jar.getName());
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.exit(7);
    	}
        Application.launch(args);
    }
     
    public static Main instance;
    
    public TaskRender taskRender;
    
    public TreeView<Task> treeView;

    public ConfigManager configManager;
    
    public MenuManager menuManager;
    
    public Stage mainStage;
    
    @Override
    public void start(Stage stage) 
    {
    	instance = this;
    	mainStage = stage;
    	
    	configManager = new ConfigManager();
    	
        BorderPane root = new BorderPane();
        
        treeView = new TreeView<Task>(configManager.loadTasksConfig());
        if(treeView.getRoot() == null || treeView.getRoot().getValue() == null) {
        	treeView.setRoot(new Task(configManager.getTranslated("defmaintask")).getTree());
        }
        
        taskRender = new TaskRender(treeView.getRoot().getValue());
        
        menuManager = new MenuManager();
        
        root.setTop(menuManager.get());
        root.setLeft(treeView);
        root.setCenter(taskRender.get());
        root.setPrefSize(800, 350);  
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.setTitle("Task's Manager");
        stage.show();

        treeView.setStyle(treeView.getStyle()+"-fx-height: 100%;");
        
        taskRender.saveTask();
    }
    
    public boolean isRunningTimer() {
    	return testTimer(treeView.getRoot());
    }
    
    private boolean testTimer(TreeItem<Task> item) {
    	boolean res = false;
    	for(TreeItem<Task> itm:item.getValue().getTree().getChildren()) {
    		res = testTimer(itm)?true:res;
    	}
    	res = item.getValue().isRunningTimer()?true:res;
    	return res;
    }
}
