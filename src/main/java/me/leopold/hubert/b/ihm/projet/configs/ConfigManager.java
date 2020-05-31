package me.leopold.hubert.b.ihm.projet.configs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.json.simple.JSONObject;

import javafx.scene.control.TreeItem;
import me.leopold.hubert.b.ihm.projet.Main;
import me.leopold.hubert.b.ihm.projet.Task;
import me.leopold.hubert.b.ihm.projet.utils.TimeUtils;

public class ConfigManager {

	public Config tasksConfig;
	public Config langConfig;
	public Config settingsConfig;
	public Config themesConfig;
	
	public ConfigManager() {
		try {
			File configDir = new File(new File("."),"TasksManagerConfigs");
			boolean firstTime = false;
			if(!configDir.exists() || !configDir.isDirectory()) {
				firstTime = true;
			}
			langConfig = new Config("langs.json",configDir.getCanonicalPath(), "langs.json");
			settingsConfig = new Config("settings.json",configDir.getCanonicalPath(), "settings.json");
			themesConfig = new Config("themes.json",configDir.getCanonicalPath(), "themes.json");
			tasksConfig = new Config("tasks.json",configDir.getCanonicalPath(), "tasks.json");
			loadLangsConfig();
			loadThemesConfig();
			loadSettings();
			if(firstTime) {
				firstTimeConnect();
			}
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, Main.instance.configManager.getTranslated("dirfinderr"), "Fatal Error", JOptionPane.ERROR_MESSAGE);
			System.exit(9);
		}
	}
	
	public void firstTimeConnect() {
		String[] lngs = new String[languages.size()];
		for(int i = 0; i < languages.size(); i++) {
			lngs[i] = languages.get(i);
		}
		setLang((String) JOptionPane.showInputDialog(null, "Its the first time you launch the app, please choose your language", "First Connection", JOptionPane.QUESTION_MESSAGE, null, lngs, lngs[0]),false);
		
		if(themes.size() > 0) {
			String[] thms = new String[themes.size()];
			for(int i = 0; i < themes.size(); i++) {
				thms[i] = themes.get(i);
			}
			selectedTheme = (String) JOptionPane.showInputDialog(null, getTranslated("themechoose"), "First Connection", JOptionPane.QUESTION_MESSAGE, null, thms, thms[0]);
		}
		
	}
	
	public TreeItem<Task> loadTasksConfig() {
    	ArrayList<ArrayList<String>> keys = tasksConfig.listAllKeys();
    	
    	ArrayList<ArrayList<String>> tasks = new ArrayList<ArrayList<String>>();
    	
		for(ArrayList<String> path:keys) {
			String[] p = new String[path.size()];
			for(int i = 0; i < p.length; i++) {
				p[i] = path.get(i);
			}
    		if(tasksConfig.getKey(p) instanceof JSONObject) {
    			tasks.add(path);
    		}
    	}
		
		TreeItem<Task> main = null;
		
		if(tasks.size() > 0) {
			main = new Task(tasks.get(0).get(0)).getTree();
	    	if(tasksConfig.haveKey(tasks.get(0).get(0), "desciption")) {
	    		main.getValue().setDesc((String) tasksConfig.getKey(tasks.get(0).get(0), "desciption"));
	    	}
	    	if(tasksConfig.haveKey(tasks.get(0).get(0), "time")) {
	    		main.getValue().setTime(TimeUtils.stringToMs((String) tasksConfig.getKey(tasks.get(0).get(0), "time")));
	    	}
	    	
	    	for(int i = 1; i < tasks.size(); i++) {
	    		
	    		TreeItem<Task> tmp = main.getValue().getTree();
	    		
	    		boolean add = true;
	    		
	    		for(int e = 1; e < tasks.get(i).size()-1; e++) {
	    			
	    			boolean valid = false;
	    			
	    			for(int j = 0; j < tmp.getValue().getTree().getChildren().size(); j++) {
	    				
	    				if(tmp.getValue().getTree().getChildren().get(j).getValue().getName().equals(tasks.get(i).get(e))) {
	    					valid = true;
	    					tmp = tmp.getValue().getTree().getChildren().get(j).getValue().getTree();
	    				}
	    				
	    			}
	    			
	    			if(!valid) {
	    				add = false;
	    			}
	    			
	    		}
	    		
	    		if(add) {
	    			
	    			String[] p = new String[tasks.get(i).size()+1];
	    			
	    			for(int o = 0; o < tasks.get(i).size(); o++) {
	    				p[o] = tasks.get(i).get(o);
	    			}
	    			
	    			Task t = new Task(tasks.get(i).get(tasks.get(i).size()-1));
	    			tmp.getValue().addChildren(t);
	    			
	    			p[p.length-1] = "description";
	    			if(tasksConfig.haveKey(p)) {
	    				t.setDesc((String) tasksConfig.getKey(p));
	    	    	}
	    			p[p.length-1] = "time";
	    	    	if(tasksConfig.haveKey(p)) {
	    	    		t.setTime(TimeUtils.stringToMs((String) tasksConfig.getKey(p)));
	    	    	}
	    		}
	    		
	    	}
		}
		
    	return main;
    	
    }
    
    public void saveTaskConfig() {
    	tasksConfig.clear();
    	svTskConfig(new ArrayList<String>(), Main.instance.treeView.getRoot());
    	tasksConfig.save();
    }
    
    private void svTskConfig(ArrayList<String> path, TreeItem<Task> itm) {
    	
    	Iterator<TreeItem<Task>> it = itm.getChildren().iterator();
    	
    	ArrayList<String> pth = new ArrayList<String>();
    	pth.addAll(path);
    	pth.add(itm.getValue().getName());
    	
    	String[] p = new String[pth.size()+1];
    	
    	for(int i = 0; i < pth.size(); i++) {
    		p[i] = pth.get(i);
    	}
    	
    	p[p.length-1] = "description";
    	
    	tasksConfig.setKey(itm.getValue().getDesc(), p);
    	
    	if(itm.getValue().isEditable()) {
    		p[p.length-1] = "time";
        	
        	tasksConfig.setKey(TimeUtils.msToString(itm.getValue().getTime()), p);
    	}
    	while(it.hasNext()) {
    		TreeItem<Task> act = it.next(); 
    		svTskConfig(pth, act);
    	}
    	
    }
	
    public ArrayList<String> languages = new ArrayList<String>();
    
    public String selectedLang;
    
    public void loadLangsConfig() {
    	ArrayList<ArrayList<String>> keys = langConfig.listAllKeys();
    	
    	languages.clear();
    	
		for(ArrayList<String> path:keys) {
			String[] p = new String[path.size()];
			for(int i = 0; i < p.length; i++) {
				p[i] = path.get(i);
			}
    		if(langConfig.getKey(p) instanceof JSONObject) {
    			languages.add(path.get(0));
    		}
    	}
		
		if(languages.size() == 0) {
			JOptionPane.showMessageDialog(null, "no languages found... please restart app...", "No Languages Found", JOptionPane.ERROR_MESSAGE);
			langConfig.file.delete();
			System.exit(3);
		}
		
		selectedLang = languages.get(0);
		
    }
    
    public String getTranslated(String name) {
    	if(!languages.contains(selectedLang)) {
    		JOptionPane.showMessageDialog(null, "selected language: "+selectedLang+" does not exist... changing to: "+languages.get(0), "No Languages Found", JOptionPane.ERROR_MESSAGE);
    		selectedLang = languages.get(0);
    	}
    	String res = "missing translation for: "+name+" in lang:"+selectedLang;
    	if(langConfig.haveKey(selectedLang,name)) {
    		if(langConfig.getKey(selectedLang,name) instanceof String) {
    			res = (String) langConfig.getKey(selectedLang,name);
    		}
    	}
    	return res;
    }
    
    public void setLang(String lang) {
    	setLang(lang, true);
    }
    
    public void setLang(String lang, boolean restart) {
    	if(languages.contains(lang)) {
    		selectedLang = lang;
    		settingsConfig.setKey(selectedLang, "lang");
    		settingsConfig.save();
    		if(Main.instance.menuManager != null) {
    			Main.instance.menuManager.reloadSettings();
    		}
    		if(restart)askExit(true);
    	}
    }
    
    public void loadSettings() {
    	if(settingsConfig.haveKey("lang") && settingsConfig.getKey("lang") instanceof String && languages.contains(settingsConfig.getKey("lang"))) {
    		selectedLang = (String) settingsConfig.getKey("lang");
    	}else {
    		System.out.println("actual lang is invalid... changing");
    		settingsConfig.setKey(selectedLang, "lang");
    	}
    	
    	if(settingsConfig.haveKey("theme") && settingsConfig.getKey("theme") instanceof String && themes.contains(settingsConfig.getKey("theme"))) {
    		selectedTheme = (String) settingsConfig.getKey("theme");
    	}else {
    		settingsConfig.setKey(selectedTheme, "theme");
    	}
    	
    	settingsConfig.save();
    }
    
    public ArrayList<String> themes = new ArrayList<String>();
    
    public String selectedTheme = "classic";
    
    public void loadThemesConfig() {
    	ArrayList<ArrayList<String>> keys = themesConfig.listAllKeys();
    	
    	themes.clear();
    	
		for(ArrayList<String> path:keys) {
			String[] p = new String[path.size()];
			for(int i = 0; i < p.length; i++) {
				p[i] = path.get(i);
			}
    		if(themesConfig.getKey(p) instanceof JSONObject) {
    			themes.add(path.get(0));
    		}
    	}
		
    }

    public void setTheme(String theme) {
    	setTheme(theme, true);
    }
    
    public void setTheme(String theme, boolean restart) {
    	if(themes.contains(theme)) {
    		selectedTheme = theme;
    		settingsConfig.setKey(selectedTheme, "theme");
    		settingsConfig.save();
    		if(Main.instance.menuManager != null) {
    			Main.instance.menuManager.reloadSettings();
    		}
    		if(restart)askExit(true);
    	}
    }
    
    Thread hook;
    
    public void askExit(boolean restart) {
    	if(restart) {
    		if(Main.instance.taskRender.question(Main.instance.configManager.getTranslated("restartconfirm"), "Restart", JOptionPane.YES_NO_OPTION) == 0) {
    			hook = new Thread(new Runnable() {
    				@Override
    				public void run() {
    					try {
    						File jar = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
    						if(!jar.isFile()) {
    							System.err.println("ERROR. Restart Not Supported (if you are using eclipse, please export app to unlock this feature)");
    						}else {
    							Runtime.getRuntime().exec("java -jar "+jar.getName());
    						}
    					} catch (IOException e) {
    						JOptionPane.showConfirmDialog(null, Main.instance.configManager.getTranslated("restarterr"), "Restart", JOptionPane.ERROR_MESSAGE);
    						e.printStackTrace();
    					}
    				}
    			});
        		Runtime.getRuntime().addShutdownHook(hook);
        		askExit(false);
        	}
    	}else {
    		if(Main.instance.taskRender.question(Main.instance.configManager.getTranslated("exitconfirm"), "Exit", JOptionPane.YES_NO_OPTION) == 0) {
        		System.exit(0);
        	}else {
        		if(hook != null) {
        			Runtime.getRuntime().removeShutdownHook(hook);
        		}
        	}
    	}
    	
    }
    
    public boolean haveThemeColor(String name) {
    	return haveThemeColor(selectedTheme, name);
    }
    
    public boolean haveThemeColor(String theme, String name) {
    	return themesConfig.haveKey(theme,name);
    }
    
    public String getThemeColor(String name) {
    	return getThemeColor(selectedTheme, name);
    }
    
    public String getThemeColor(String theme, String name) {
    	if(!haveThemeColor(theme, name))return null;
    	if(themesConfig.getKey(theme,name) instanceof String) {
    		return (String) themesConfig.getKey(theme,name);
    	}
    	return null;
    }
    
}
