package me.leopold.hubert.b.ihm.projet;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class MenuManager {

	MenuBar menu;
	Menu settings;
	
	/**
     * Init MenuBar
     */
	public MenuManager() {
		menu = new MenuBar();
		settings = new Menu(Main.instance.configManager.getTranslated("settings"));
		MenuItem exit = new MenuItem(Main.instance.configManager.getTranslated("exit"));
		exit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
	        public void handle(ActionEvent t) {
	            Main.instance.configManager.askExit(false);
	        }
	    });
		Menu file = new Menu(Main.instance.configManager.getTranslated("file"));
		file.getItems().add(exit);
		menu.getMenus().add(file);
		menu.getMenus().add(settings);
		reloadSettings();
	}
	
	public Node get() {
		return menu;
	}
	
	/**
     * reload settings menu with actual languages and themes
     */
	public void reloadSettings() {
		settings.getItems().clear();
		Menu langs = new Menu(Main.instance.configManager.getTranslated("langs"));
		for(String lng:Main.instance.configManager.languages) {
			MenuItem item = new MenuItem(lng);
			item.setOnAction(new EventHandler<ActionEvent>() {
				@Override
		        public void handle(ActionEvent t) {
		            Main.instance.configManager.setLang(lng);
		        }
		    });
			if(lng.equals(Main.instance.configManager.selectedLang)) {
				item.setDisable(true);
			}
			langs.getItems().add(item);
		}
		settings.getItems().add(langs);
		Menu themes = new Menu(Main.instance.configManager.getTranslated("themes"));
		for(String thm:Main.instance.configManager.themes) {
			MenuItem item = new MenuItem(thm);
			item.setOnAction(new EventHandler<ActionEvent>() {
				@Override
		        public void handle(ActionEvent t) {
		            Main.instance.configManager.setTheme(thm);
		        }
		    });
			if(thm.equals(Main.instance.configManager.selectedTheme)) {
				item.setDisable(true);
			}
			themes.getItems().add(item);
		}
		settings.getItems().add(themes);
	}
	
}
