package me.leopold.hubert.b.ihm.projet;

import java.util.ArrayList;
import java.util.Collections;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.TreeItem;
import javafx.util.Duration;
import me.leopold.hubert.b.ihm.projet.utils.TimeUtils;

public class Task {

	private TreeItem<Task> tree = new TreeItem<Task>();
	private String name;
	private String desc = Main.instance.configManager.getTranslated("defdesc");
	private long time = 0;
	private boolean runningTimer = false;
	
	private Timeline timeline;
	
	public Task(String name) {
		this.tree.setValue(this);
		this.tree.setExpanded(true);
		setName(name);
	}

	@Override
	public String toString() {
		String showTime = time <= 0?"-":""+TimeUtils.msToString(getTime());
		return name+" : "+showTime;
	}

	/**
     * @return task duration
     */
	public long getTime() {
		return time;
	}

	/**
     * set task duration
     */
	public void setTime(long time) {
		this.time = time;
	}

	/**
     * Check if task is editable
     */
	public boolean isEditable() {
		return getTree().getChildren().size() == 0;
	}
	
	/**
     * Check if timer is running
     */
	public boolean isRunningTimer() {
		return runningTimer;
	}

	/**
     * Set running timer
     */
	public void setRunningTimer(boolean runningTimer) {
		this.runningTimer = runningTimer;
	}

	/**
     * Set task name
     */
	public void setName(String name) {
		this.name = name;
		if(haveSameName()) {
			setName(name+"(1)");
		}
	}

	/**
     * @return task name
     */
	public String getName() {
		return name;
	}
	
	/**
     * @return task description
     */
	public String getDesc() {
		return desc;
	}

	/**
     * Set task description
     */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
     * @return task tree
     */
	public TreeItem<Task> getTree() {
		return tree;
	}
	
	/**
     * @return task path in tree
     */
	public ArrayList<String> getPath() {
		ArrayList<String> path = new ArrayList<String>();
		path.add(getName());
		TreeItem<Task> pTree = getTree();
		while(pTree.getParent() != null) {
			path.add(pTree.getParent().getValue().getName());
			pTree = pTree.getParent();
		}
		Collections.reverse(path);
		return path;
	}
	
	/**
     * @return task index in elements at same level
     */
	public int getTaskIndex() {
		int index = 0;
		if(!haveParent())return index;
		return getTree().getParent().getChildren().indexOf(getTree());
	}
	
	/**
     * Check if task have parent in tree
     */
	public boolean haveParent() {
		return getTree().getParent() != null;
	}

	/**
     * Check if task have same name as other tasks at same level
     */
	public boolean haveSameName() {
		if(!haveParent())return false;
		for(TreeItem<Task> tsk:getTree().getParent().getChildren()) {
			if(tsk.getValue() != this && tsk.getValue().getName().equals(getName()))return true;
		}
		return false;
	}
	
	/**
     * add task as child
     */
	public void addChild(Task tsk) {
		addChild(tsk,getTree().getChildren().size());
	}
	
	public void addChild(Task tsk, int index) {
		stopTimer();
		
		int idx = index<0?0:index;
		
		if(idx >= getTree().getChildren().size()) {
			getTree().getChildren().add(tsk.getTree());
		}else {
			getTree().getChildren().add(idx,tsk.getTree());
		}
	}
	
	/**
     * add task to similar level
     */
	public void addSim(Task tsk) {
		if(!haveParent())return;
		addSim(tsk, getTree().getParent().getChildren().size());
	}
	
	public void addSim(Task tsk, int index) {
		int idx = index<0?0:index;
		
		if(idx >= getTree().getParent().getChildren().size()) {
			getTree().getParent().getChildren().add(tsk.getTree());
		}else {
			getTree().getParent().getChildren().add(idx,tsk.getTree());
		}
	}
	
	/**
     * delete task
     */
	public void deleteTask() {
		if(!haveParent())return;
		getTree().getParent().getChildren().remove(getTree());
	}
	
	/**
     * start timer with actual duration
     */
	public boolean resumeTimer() {
		return startTimer(time);
	}
	
	/**
     * start timer from 0
     */
	public boolean startTimer() {
		return startTimer(0);
	}
	
	private boolean startTimer(long start) {
		if(Main.instance.isRunningTimer() || this.runningTimer)return false;
		setRunningTimer(true);
		time = start;
		Main.instance.taskRender.reload();
		timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
			 	time = time+1000;
			    Main.instance.taskRender.reload();
		    }));
	    timeline.setCycleCount(Animation.INDEFINITE);
	    timeline.play();
		return true;
	}
	
	/**
     * stop timer
     */
	public boolean stopTimer() {
		if(!this.runningTimer)return false;
		setRunningTimer(false);
		timeline.stop();
		return true;
	}
	
}
