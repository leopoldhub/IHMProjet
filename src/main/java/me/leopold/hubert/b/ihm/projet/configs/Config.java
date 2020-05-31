package me.leopold.hubert.b.ihm.projet.configs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import me.leopold.hubert.b.ihm.projet.Main;

public class Config {

	File file;
	InputStream def;
	JSONObject parsed;
	
	public Config(String name, String path) {
		this(name, path, null);
	}
	
	public Config(String name, String path, String defpath) {
		def = null;
		try {
			def = this.getClass().getClassLoader().getResourceAsStream(defpath);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("no def file found for: "+defpath);
		}
		file = new File(path);
		boolean work = true;
		if(!file.exists() || !file.isDirectory()) {
			if(!file.mkdirs()) {
				work = false;
			}
		}
		if(!file.canRead() || !file.canWrite()) {
			work = false;
		}
		if(!work) {
			JOptionPane.showMessageDialog(null, String.format(Main.instance.configManager.getTranslated("accesdenied"), file.getPath().toString()), "configuration error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		file = new File(file, name);
		boolean nw = false;
		if(!file.exists() || !file.isFile()) {
			nw = true;
			try {
				if(!file.createNewFile()) {
					work = false;
				}
			} catch (IOException e) {
				work = false;
				e.printStackTrace();
			}
		}
		if(!file.canRead() || !file.canWrite()) {
			work = false;
		}
		if(!work) {
			JOptionPane.showMessageDialog(null, String.format(Main.instance.configManager.getTranslated("accesdenied"), file.getPath().toString()), "configuration error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		if(nw) {
			if(def != null) {
				InputStream is = def;
			    OutputStream os = null;
			    try {
			        os = new FileOutputStream(file);
			        byte[] buffer = new byte[1024];
			        int length;
			        while ((length = is.read(buffer)) > 0) {
			            os.write(buffer, 0, length);
			        }
			        is.close();
			        os.close();
			    } catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if(file.length() == 0){
			try {
		      FileWriter myWriter = new FileWriter(file, true);
		      myWriter.write("{}");
		      myWriter.close();
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
		}
		parse();
		listAllKeys();
	}
	
	/**
     * parse json config
     */
	private void parse() {
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(
					new FileReader(file)
					);
			parsed = (JSONObject) obj;
		} catch (Exception e) {
			e.printStackTrace();
			if(JOptionPane.showConfirmDialog(null, String.format(Main.instance.configManager.getTranslated("cfgloaderr"), file.getName()), "Config loading error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, UIManager.getIcon("OptionPane.errorIcon")) == 0) {
				parsed = new JSONObject();
			}else {
				System.exit(1);
			}
			
		}
	}
	
	/**
     * list all keys in config
     */
	public ArrayList<ArrayList<String>> listAllKeys() {
		ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
		if(parsed == null)return list;
		Iterator<String> keys = parsed.keySet().iterator();
		while(keys.hasNext()) {
			ArrayList<String> aKey = new ArrayList<String>();
			aKey.add(keys.next());
			list.add(aKey);
			listKeys(parsed.get(aKey.get(0)),aKey,list);
		}
		return list;
	}
	
	private void listKeys(Object key, ArrayList<String> skey, ArrayList<ArrayList<String>> lst){
		ArrayList<ArrayList<String>> list = lst == null?new ArrayList<ArrayList<String>>():lst;
		if(key instanceof JSONObject) {
			Iterator<String> keys = ((JSONObject)key).keySet().iterator();
			while(keys.hasNext()) {
				String aKey = keys.next();
				ArrayList<String> sk = new ArrayList<String>();
				sk.addAll(skey);
				sk.add(aKey);
				list.add(sk);
				listKeys(((JSONObject)key).get(aKey),sk, list);
			}
		}
	}
	
	/**
     * Check if config have key
     */
	public boolean haveKey(String... key) {
		JSONObject obj = parsed;
		for(int i = 0; i < key.length; i++) {
			if(obj.containsKey(key[i])) {
				if(i == key.length-1) {
					return true;
				}
				obj = (JSONObject) obj.get(key[i]);
			}else {
				return false;
			}
		}
		return true;
	}
	
	/**
     * Get config key
     */
	public Object getKey(String... key) {
		if(!haveKey(key))return null;
		JSONObject obj = parsed;
		for(int i = 0; i < key.length; i++) {
			if(obj.containsKey(key[i])) {
				if(i == key.length-1) {
					return obj.get(key[i]);
				}
				obj = (JSONObject) obj.get(key[i]);
			}else {
				return null;
			}
		}
		return null;
	}
	
	/**
     * Set config key
     */
	public void setKey(Object value, String... key) {
		JSONObject obj = parsed;
		for(int i = 0; i < key.length; i++) {

			if(i == key.length-1) {
				if(obj.containsKey(key[i])) {
					obj.remove(key[i]);
				}
				obj.put(key[i], value);
			}else {
				if(!obj.containsKey(key[i])) {
					obj.put(key[i], new JSONObject());
				}
			}
			if(i != key.length-1) {
				obj = (JSONObject) obj.get(key[i]);
			}
		}
	}
	
	/**
     * Clear config
     */
	public void clear() {
		parsed.clear();
	}
	
	/**
     * Save config
     */
	public void save() {
		save(file);
	}
	
	@Deprecated
	public void save(File fle) {
		try {
	      FileWriter myWriter = new FileWriter(fle, false);
	      Gson gson = new GsonBuilder().setPrettyPrinting().create();
	      myWriter.write(gson.toJson((JsonElement) new JsonParser().parse(JSONObject.toJSONString(parsed))));
	      myWriter.close();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	}
	
}
