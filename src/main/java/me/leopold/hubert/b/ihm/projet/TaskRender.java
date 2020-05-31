package me.leopold.hubert.b.ihm.projet;

import java.awt.AWTException;
import java.awt.Menu;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import me.leopold.hubert.b.ihm.projet.utils.TimeUtils;

public class TaskRender {

	private AnchorPane group;
	
	private TextField taskName;
	private Label taskPath;
	private TextArea desc;
	private TextField time;
	
	private Button deleteButton;
	private Button updateButton;
	
	private Button runButton;
	private Button resumeButton;
	private Button stopButton;
	
	private Task selectedTask;
	
	private ContextMenu rClicMenu;
	private MenuItem addBeforeItem;
	private MenuItem addAfterItem;
	private MenuItem addSubItem;
	private MenuItem deleteItem;
	private MenuItem cancelItem;
	
    private Image redIcon;
	private Image greenIcon;
	private Image runIcon;
	private Image resumeIcon;
	private Image stopIcon;
	private Image deleteIcon;
	
	private java.awt.Image awtRedIcon;
	private java.awt.Image awtGreenIcon;
	
	private TrayIcon trayIcon;
	
    private java.awt.Menu displayTray;
    
    private java.awt.PopupMenu popupTray;
    
    private java.awt.MenuItem actualTaskTray;
    
    private java.awt.MenuItem actualTimeTray;
    
    private java.awt.MenuItem runItemTray;
    
    private java.awt.MenuItem resumeItemTray;
    
    private java.awt.MenuItem stopItemTray;
	
	public String textAreaStyle;
    
	public TaskRender(Task task) {
		Main.instance.treeView.getSelectionModel().selectedItemProperty().addListener( new ChangeListener<TreeItem<Task>>() {
			@Override
			public void changed(ObservableValue<? extends TreeItem<Task>> observable, TreeItem<Task> oldItem, TreeItem<Task> newItem) {
				updateTask(newItem.getValue());
			}
        });
		
		redIcon = new Image(getClass().getResourceAsStream("/imgs/red.png"));
		greenIcon = new Image(getClass().getResourceAsStream("/imgs/green.png"));
		runIcon = new Image(getClass().getResourceAsStream("/imgs/run.png"));
		resumeIcon = new Image(getClass().getResourceAsStream("/imgs/resume.png"));
		stopIcon = new Image(getClass().getResourceAsStream("/imgs/stop.png"));
		deleteIcon = new Image(getClass().getResourceAsStream("/imgs/delete.png"));
		
		awtRedIcon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/imgs/red.png"));
		awtGreenIcon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/imgs/green.png"));
		
		registerPane();
		
		registerContextMenu();
		
		registerSystemTray();
		
		updateTask(task);
	}
	
	public Node get() {
		return group;
	}
	
	/**
     * register pane elements
     */
	public void registerPane() {
		group = new AnchorPane();
		taskName = new TextField();
		taskName.setLayoutX(10);
		taskName.setLayoutY(10);
		taskName.setFont(Font.font(50));
		taskName.setMinWidth(400);
		taskName.setMaxWidth(400);
		taskName.setMinHeight(80);
		taskName.setMaxHeight(80);
		taskPath = new Label();
		taskPath.setLayoutX(10);
		taskPath.setLayoutY(100);
		taskPath.setMaxWidth(taskName.getMaxWidth());
		taskPath.setFont(Font.font(15));
		desc = new TextArea();
		desc.setLayoutX(10);
		desc.setLayoutY(130);
		desc.setFont(Font.font(20));
		desc.setMinWidth(taskName.getMinWidth());
		desc.setMaxWidth(taskName.getMaxWidth());
		desc.setMinHeight(90);
		desc.setMaxHeight(90);
		time = new TextField();
		time.setLayoutX(10);
		time.setLayoutY(240);
		time.setFont(Font.font(20));
		time.setMinWidth(taskName.getMinWidth());
		time.setMaxWidth(taskName.getMaxWidth());
		time.setMinHeight(40);
		time.setMaxHeight(40);
		deleteButton = new Button(Main.instance.configManager.getTranslated("delete"));
		deleteButton.setLayoutX(430);
		deleteButton.setLayoutY(25);
		deleteButton.setMinWidth(120);
		deleteButton.setMinHeight(50);
		deleteButton.setStyle("-fx-color: #ad322a;-fx-text-fill: #ffffff;");
		deleteButton.setGraphic(new ImageView(deleteIcon));
		deleteButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				delete();
			}
		});
		updateButton = new Button(Main.instance.configManager.getTranslated("update"));
		updateButton.setLayoutX(430);
		updateButton.setLayoutY(245);
		updateButton.setMinWidth(120);
		updateButton.setStyle("-fx-color: #28993e;-fx-text-fill: #ffffff;");
		updateButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				saveTask();
			}
		});
		runButton = new Button(Main.instance.configManager.getTranslated("run"));
		runButton.setLayoutX(30);
		runButton.setLayoutY(300);
		runButton.setMinWidth(100);
		runButton.setMinHeight(40);
		runButton.setGraphic(new ImageView(runIcon));
		runButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				startTimer();
			}
		});
		resumeButton = new Button(Main.instance.configManager.getTranslated("resume"));
		resumeButton.setLayoutX(160);
		resumeButton.setLayoutY(300);
		resumeButton.setMinWidth(100);
		resumeButton.setMinHeight(40);
		resumeButton.setGraphic(new ImageView(resumeIcon));
		resumeButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				resumeTimer();
			}
		});
		stopButton = new Button(Main.instance.configManager.getTranslated("stop"));
		stopButton.setLayoutX(290);
		stopButton.setLayoutY(300);
		stopButton.setMinWidth(100);
		stopButton.setMinHeight(40);
		stopButton.setGraphic(new ImageView(stopIcon));
		stopButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				stopTimer();
			}
		});
		
		String buttonStyle = "";
		
		if(Main.instance.configManager.haveThemeColor("buttons")) {
			buttonStyle += "-fx-color:"+Main.instance.configManager.getThemeColor("buttons")+";";
		}

		textAreaStyle = "";
		
		if(Main.instance.configManager.haveThemeColor("text")) {
			String css = "-fx-text-fill:"+Main.instance.configManager.getThemeColor("text")+";";
			buttonStyle += css;
			textAreaStyle += css;
			taskPath.setStyle(taskPath.getStyle()+css);
		}
		
		if(Main.instance.configManager.haveThemeColor("textarea")) {
			textAreaStyle += "-fx-background-color:"+Main.instance.configManager.getThemeColor("textarea")+";";
			textAreaStyle += "-fx-control-inner-background:"+Main.instance.configManager.getThemeColor("textarea")+";";
		}
		
		if(Main.instance.configManager.haveThemeColor("background")) {
			group.setStyle("-fx-background-color:"+Main.instance.configManager.getThemeColor("background")+";");
		}
		
		taskName.setStyle(taskName.getStyle()+textAreaStyle);
		desc.setStyle(desc.getStyle()+textAreaStyle);
		time.setStyle(time.getStyle()+textAreaStyle);
		Main.instance.treeView.setStyle(textAreaStyle);
		
		runButton.setStyle(runButton.getStyle()+buttonStyle);
		resumeButton.setStyle(resumeButton.getStyle()+buttonStyle);
		stopButton.setStyle(stopButton.getStyle()+buttonStyle);
		
		group.getChildren().add(taskName);
		group.getChildren().add(taskPath);
		group.getChildren().add(desc);
		group.getChildren().add(time);
		group.getChildren().add(deleteButton);
		group.getChildren().add(updateButton);
		group.getChildren().add(runButton);
		group.getChildren().add(resumeButton);
		group.getChildren().add(stopButton);
	}
	
	/**
     * register context menu elements
     */
	public void registerContextMenu() {
		rClicMenu = new ContextMenu();
		addBeforeItem = new MenuItem(Main.instance.configManager.getTranslated("addbefore"));
		addBeforeItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				addBefore();
			}
		});
		addAfterItem = new MenuItem(Main.instance.configManager.getTranslated("addafter"));
		addAfterItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				addAfter();
			}
		});
		addSubItem = new MenuItem(Main.instance.configManager.getTranslated("addsub"));
		addSubItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				addSub();
			}
		});
		deleteItem = new MenuItem(Main.instance.configManager.getTranslated("delete"));
		deleteItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				delete();
			}
		});
		cancelItem = new MenuItem(Main.instance.configManager.getTranslated("cancel"));
		cancelItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				rClicMenu.hide();
			}
		});
		rClicMenu.setAutoHide(true);
		rClicMenu.getItems().addAll(addBeforeItem, addAfterItem, addSubItem, separator(), deleteItem, separator(), cancelItem);
		Main.instance.treeView.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent event) {
                rClicMenu.show(Main.instance.treeView, event.getScreenX(), event.getScreenY());
            }
        });
	}
	
	/**
     * register system tray elements
     */
	public void registerSystemTray() {
		if (SystemTray.isSupported()) {
        	
        	Platform.setImplicitExit(false);
            
        	awtRedIcon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/imgs/red.png"));

            trayIcon = new TrayIcon(awtRedIcon, "Task's Manager");

            SystemTray tray = SystemTray.getSystemTray();
            
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    if (event.getButton() == MouseEvent.BUTTON1) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                            	if(!Main.instance.mainStage.isShowing()) {
                            		Main.instance.mainStage.show();
                            	}else {
                            		if(Main.instance.mainStage.isIconified()) {
                            			Main.instance.mainStage.setIconified(false);
                                    }else {
                                    	Main.instance.mainStage.setIconified(true);
                                    }
                            	}
                                
                            }
                        });
                    }

                }
            });
            
            popupTray = new PopupMenu();
            displayTray = new java.awt.Menu(Main.instance.configManager.getTranslated("selecttask"));
            actualTaskTray = new java.awt.MenuItem();
            actualTimeTray = new java.awt.MenuItem();
            runItemTray = new java.awt.MenuItem(Main.instance.configManager.getTranslated("run"));
            runItemTray.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                        	startTimer();
                        }
                    });
				}
			});
            resumeItemTray = new java.awt.MenuItem(Main.instance.configManager.getTranslated("resume"));
            resumeItemTray.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                        	resumeTimer();
                        }
                    });
				}
			});
            
            stopItemTray = new java.awt.MenuItem(Main.instance.configManager.getTranslated("stop"));
            stopItemTray.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                        	stopTimer();
                        }
                    });
				}
			});
            java.awt.MenuItem exitItem = new java.awt.MenuItem(Main.instance.configManager.getTranslated("exit"));
            exitItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                        	saveTask();
                        	Main.instance.configManager.askExit(false);
                        }
                    });
				}
            });

            popupTray.add(new java.awt.MenuItem(Main.instance.configManager.getTranslated("selectedtask")));
            popupTray.add(actualTaskTray);
            popupTray.add(actualTimeTray);
            popupTray.addSeparator();
            popupTray.add(runItemTray);
            popupTray.add(resumeItemTray);
            popupTray.add(stopItemTray);
            popupTray.addSeparator();
            popupTray.add(displayTray);
            popupTray.add(exitItem);

            trayIcon.setPopupMenu(popupTray);
            
            try {
    			tray.add(trayIcon);
    		} catch (AWTException e) {
    			e.printStackTrace();
    		}
        }
	}
	
	/**
     * save changes of actual task
     */
	public void saveTask() {
		selectedTask.setName(taskName.getText());
		selectedTask.setDesc(desc.getText());
		selectedTask.setTime(TimeUtils.stringToMs(time.getText()));
		Main.instance.configManager.saveTaskConfig();
		reload();
	}
	
	/**
     * reload selected task
     */
	public void reload() {
		if(selectedTask == null) {
			selectedTask = Main.instance.treeView.getRoot().getValue();
		}
		updateTask(selectedTask);
		reloadTreeView();
		setTrayTasks();
	}
	
	/**
     * set selected task
     */
	public void updateTask(Task task) {
		taskName.setText(task.getName());
		StringBuilder sb = new StringBuilder();
		for(String nm:task.getPath()) {
			sb.append("/"+nm);
		}
		taskPath.setText(sb.toString());
		desc.setText(task.getDesc());
		
		if(!task.haveParent()) {
			deleteButton.setDisable(true);
			deleteItem.setDisable(true);
			addAfterItem.setDisable(true);
			addBeforeItem.setDisable(true);
		}else {
			deleteButton.setDisable(false);
			deleteItem.setDisable(false);
			addAfterItem.setDisable(false);
			addBeforeItem.setDisable(false);
		}
		
		if(task.getTime() <= 0) {
			if(!task.isEditable()) {
				time.setText(Main.instance.configManager.getTranslated("subnotcomplete"));
			}else {
				time.setText(Main.instance.configManager.getTranslated("notimereg"));
			}
		}else {
			time.setText(TimeUtils.msToString(task.getTime()));
		}
		
		if(task.isEditable()) {
			time.setEditable(true);
			if(!Main.instance.isRunningTimer()) {
				runButton.setDisable(false);
				runItemTray.setEnabled(true);
				resumeButton.setDisable(!(task.getTime() > 0));
				resumeItemTray.setEnabled(task.getTime() > 0);
				stopButton.setDisable(true);
				stopItemTray.setEnabled(false);
			}else {
				runButton.setDisable(true);
				runItemTray.setEnabled(false);
				resumeButton.setDisable(true);
				resumeItemTray.setEnabled(false);
				stopButton.setDisable(false);
				stopItemTray.setEnabled(true);
			}
		}else {
			time.setEditable(false);
			runButton.setDisable(true);
			runItemTray.setEnabled(false);
			resumeButton.setDisable(true);
			resumeItemTray.setEnabled(false);
			stopButton.setDisable(true);
			stopItemTray.setEnabled(false);
		}
		
		selectedTask = task;
	}
	
	/**
     * delete selected task
     */
	public void delete() {
		if(selectedTask.haveParent()) {
			if(question(Main.instance.configManager.getTranslated("confirmdelete"), "confirmation", JOptionPane.YES_NO_OPTION) == 0) {
				int index = selectedTask.getTaskIndex()<0?0:selectedTask.getTaskIndex();
				index = index >= selectedTask.getTree().getParent().getChildren().size()-2?0:index;
				Task parent = selectedTask.getTree().getParent().getValue();
				selectedTask.deleteTask();
				if(parent.getTree().getChildren().size()>index) {
					updateTask(parent.getTree().getChildren().get(index).getValue());
				}else {
					updateTask(parent);
				}
				saveTask();
			}
		}
	}
	
	/**
     * @return ContextMenu separator
     */
	public MenuItem separator() {
		return new MenuItem("----------");
	}
	
	/**
     * reload tasks in SystemTray
     */
	public void setTrayTasks() {
		displayTray.removeAll();
    	displayTray.add(trayTask(Main.instance.treeView.getRoot().getValue(), displayTray));
    	setTrayTaskRender();
    }
    
    private Menu trayTask(Task task, Menu menu) {
    	
    	Menu mn = null;
    	
    	if(task.getTree().getChildren().size() == 0) {
    		setTrayTaskRender();
    		mn = menu;
    		java.awt.MenuItem itm = new java.awt.MenuItem(task.getName());
    		itm.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                        	selectedTask = task;
                        	reload();
                        }
                    });
				}
            });
    		mn.add(itm);
    	}else {
    		mn = new Menu(task.getName());
    		menu.add(mn);
    	}
    	
    	for(TreeItem<Task> tsk:task.getTree().getChildren()) {
    		trayTask(tsk.getValue(), mn);
    	}
    	return mn;
    }
    
    /**
     * reload SystemTray selected task name
     */
    public void setTrayTaskRender() {
    	actualTaskTray.setLabel(selectedTask.getName());
    	actualTaskTray.setName(selectedTask.getName());
    	actualTaskTray.setEnabled(false);
		actualTaskTray.setEnabled(true);
		setTrayTimerRender();
    }
    
    /**
     * reload SystemTray selected task duration
     */
    public void setTrayTimerRender() {
    	actualTimeTray.setLabel(TimeUtils.msToString(selectedTask.getTime()));
    	actualTimeTray.setName(TimeUtils.msToString(selectedTask.getTime()));
    	actualTimeTray.setEnabled(false);
    	actualTimeTray.setEnabled(true);
    }
    
    /**
     * reload TreeView items
     */
    public void reloadTreeView() {
    	Main.instance.treeView.setRoot(Main.instance.treeView.getRoot());
    	recalculateTimes();
    	recalculateIcons();
    }
    
    /**
     * calculate tasks durations
     */
    public void recalculateTimes() {
    	calculate(Main.instance.treeView.getRoot().getValue());
	}
    
    private long calculate(Task tsk) {
    	if(tsk.getTree().getChildren().size() > 0) {
    		long total = 0;
    		boolean valid = true;
    		for(TreeItem<Task> itms:tsk.getTree().getChildren()) {
    			long res = calculate(itms.getValue());
    			if(res == 0) {
    				valid = false;
    			}
    			total = total+calculate(itms.getValue());
    		}
    		if(valid) {
    			tsk.setTime(total);
    			return total;
    		}else {
    			tsk.setTime(0);
    			return 0;
    		}
    	}else {
    		return tsk.getTime();
    	}
    }

    /**
     * reload TreeView icons
     */
    public void recalculateIcons() {
    	calculateIcon(Main.instance.treeView.getRoot());
    	if(Main.instance.isRunningTimer()) {
    		trayIcon.setImage(awtGreenIcon);
    	}else {
    		trayIcon.setImage(awtRedIcon);
    	}
    }
    
    private void calculateIcon(TreeItem<Task> item) {
    	if(item.getValue().getTime() > 0) {
    		item.setGraphic(new ImageView(greenIcon));
    	}else {
    		item.setGraphic(new ImageView(redIcon));
    	}
    	
    	for(TreeItem<Task> items:item.getChildren()) {
    		calculateIcon(items);
    	}
    	
    }
    
    /**
     * start selected task timer
     */
    public void startTimer() {
    	selectedTask.startTimer();
		saveTask();
		setTrayTaskRender();
    }
    
    /**
     * resume selected task timer
     */
    public void resumeTimer() {
    	selectedTask.resumeTimer();
		saveTask();
		setTrayTaskRender();
    }
    
    /**
     * stop selected task timer
     */
    public void stopTimer() {
    	selectedTask.stopTimer();
		saveTask();
		setTrayTaskRender();
    }
    
    /**
     * add new task before selected task
     */
    public void addBefore() {
    	if(selectedTask.haveParent()) {
			Task newTask = new Task("tsk");
			selectedTask.addSim(newTask, selectedTask.getTaskIndex());
			newTask.setName(String.format(Main.instance.configManager.getTranslated("defnewtask"),selectedTask.getTree().getParent().getChildren().size()));
			saveTask();
		}
    }
    
    /**
     * add new task after selected task
     */
    public void addAfter() {
    	if(selectedTask.haveParent()) {
    		Task newTask = new Task("tsk");
			selectedTask.addSim(newTask, selectedTask.getTaskIndex()+1);
			newTask.setName(String.format(Main.instance.configManager.getTranslated("defnewtask"),selectedTask.getTree().getParent().getChildren().size()));
			saveTask();
		}
    }
	
    /**
     * add new task as child of selected task
     */
    public void addSub() {
    	Task newTask = new Task("tsk");
		selectedTask.addChild(newTask);
		newTask.setName(String.format(Main.instance.configManager.getTranslated("defnewtask"),""));
		saveTask();
    }
    
    public int question(String message, String name, int type) {
    	JFrame q = new JFrame();
    	q.setLocationRelativeTo(null);
    	q.setVisible(true);
    	q.setAlwaysOnTop(true);
    	int res = JOptionPane.showConfirmDialog(q,message,name,type);
    	q.dispose();
    	return res;
    }
    
}
