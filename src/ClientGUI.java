import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.apache.commons.io.FileUtils;
import org.controlsfx.control.Notifications;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.prefs.Preferences;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ClientGUI extends Application {
	
	private String server, username;
	private int port;
	private boolean connected;
	
	public TextArea chatArea = new TextArea();
	public ListView<String> listView = new ListView<String>();
	
	private Client client;
	
	//  app prefs - https://stackoverflow.com/questions/4017137/how-do-i-save-preference-user-settings-in-java
	public Preferences prefs = Preferences.userNodeForPackage(ClientGUI.class);
	
	// detech OS
	private static String OS = System.getProperty("os.name").toLowerCase();
	
    void cleanup() {
        // stop, reset  -  cleans up window
    }

    //  show server select dialog
    @Override
    public void start(Stage serverStage) throws IOException { // the stage name for each view is here serverStage 
	    	// there is a diffrent stage name for each part of the app
	    	// basicly javafx is simpler that java
	    	    	
		connected = false;
		
    	serverStage.setTitle("Chat Setup");
    	serverStage.show();
    	serverStage.getIcons().add(new Image(this.getClass().getResourceAsStream("res/icon64.png")));
		
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		
		Scene scene = new Scene(grid, 600, 300);
		serverStage.setScene(scene);
		
		Text scenetitle = new Text("Enter Server Address");
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid.add(scenetitle, 0, 0, 2, 1);

		Label serverLabel = new Label("Server:");
		grid.add(serverLabel, 0, 1);

		TextField serverTextField = new TextField(prefs.get("server_address", ""));
		grid.add(serverTextField, 1, 1);
		
		Label portLabel = new Label("port:");
		grid.add(portLabel, 0, 2);

		TextField portTextField = new TextField(prefs.get("server_port", ""));
		grid.add(portTextField, 1, 2);
		
		Button ConnectToServerbtn = new Button("Connect");
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(ConnectToServerbtn);
		grid.add(hbBtn, 1, 4);
		
		final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);

        
        ConnectToServerbtn.setOnAction(new EventHandler<ActionEvent>() {
        @Override
            public void handle(ActionEvent e) {
        		server = serverTextField.getText().trim();
    			port = Integer.parseInt(portTextField.getText().trim());
    			// set prefs
    			prefs.put("server_address", server);
    			prefs.put("server_port", Integer.toString(port));
        		loginStage(serverStage);
            }
        });
    }
    
    
    //show login dialog
	void loginStage(Stage loginStage) {
	    	loginStage.setTitle("Chat Login");
	    	loginStage.show();
		
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Scene scene = new Scene(grid, 600, 300);
		loginStage.setScene(scene);
		
		Text scenetitle = new Text("Login To: "+server);
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid.add(scenetitle, 0, 0, 2, 1);

		Label userName = new Label("User Name:");
		grid.add(userName, 0, 1);

		TextField userTextField = new TextField(prefs.get("username", ""));
		grid.add(userTextField, 1, 1);

		Label pw = new Label("Password:");
		grid.add(pw, 0, 2);

		PasswordField passField = new PasswordField();
		passField.setText(prefs.get("password", ""));
		grid.add(passField, 1, 2);
		
		
		CheckBox rememberMe = new CheckBox("Remember Me");
		rememberMe.setSelected(true);
		Button SignInbtn = new Button("Sign in");
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(SignInbtn);
		hbBtn.getChildren().add(rememberMe);
		grid.add(hbBtn, 1, 4);
		
		final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);
        
        SignInbtn.setOnAction(new EventHandler<ActionEvent>() {
        @Override
            public void handle(ActionEvent e) {

	        		try{
	        			Class.forName("com.mysql.jdbc.Driver");

	        			Connection con=DriverManager.getConnection("jdbc:mysql://"+server+":3306/JavaChat?autoReconnect=true&useSSL=false","chat","7xsVuPeF1rCQOeo2");
	        			//here sonoo is the database name, root is the username and root is the password
	        			Statement stmt=con.createStatement();
	        			
	        	        final String encodedPassword = Decrypt(passField.getText().trim());
	        			
	        			ResultSet result = stmt.executeQuery("select * from users where user='"+userTextField.getText().trim()+"' AND pass='"+encodedPassword+"'");
	        			if(result.next()){
	        				//System.out.println(result.getInt(1)+"  "+result.getString(2)+"  '"+result.getString(3)+"' = '"+encoded+"'");
	        				username = userTextField.getText().trim();
	        				actiontarget.setFill(Color.BLUE);
	        				actiontarget.setText("Login Successful!");
	        				// put remeber stuff here this is the successful login spot
	        				
	        				if(rememberMe.isSelected()) {
		            			prefs.put("username", userTextField.getText().trim());
		            			prefs.put("password", passField.getText().trim());
	        				}
	        				
	        				
	        				
	        			    cleanup();
	        			    initChat(loginStage);
        			    } else {
        					actiontarget.setFill(Color.FIREBRICK);
	        				actiontarget.setText("Invalid login.");
        				}
	        			con.close();

        			}catch(Exception e1){ 
        				System.out.println(e1.getMessage());
        				if(e1.getMessage().contains("empty result set")) {
            				actiontarget.setFill(Color.FIREBRICK);
            				actiontarget.setText("Invalid login.");
        				} else if(e1.getMessage().contains("Could not create connection to database server")) {
            				actiontarget.setFill(Color.FIREBRICK);
            				actiontarget.setText("Could not connect to server.");
        				} else {
            				actiontarget.setFill(Color.FIREBRICK);
            				actiontarget.setText("Error while processing request.");
        				}
    				}
            }
        });
    }
    
    void initChat(Stage chatStage) {
    	
    	chatStage.setTitle("Chat: "+server+":"+port);
    	chatStage.show();
		
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
	
		Scene scene = new Scene(grid, 600, 300);
		chatStage.setScene(scene);
		
		Text scenetitle = new Text("Chat: "+server+":"+port);
		Button clearButton = new Button("Clear");
		clearButton.setAlignment(Pos.CENTER_RIGHT);
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		HBox titleSection = new HBox(10);
		titleSection.setAlignment(Pos.CENTER_LEFT);
		titleSection.getChildren().add(scenetitle);
		titleSection.getChildren().add(clearButton);
		grid.add(titleSection, 0, 0);
    	
    		try {
    			client = new Client(server, port, username, this);
    			if(client.start()) {
    				connected = true;
    				
        	    	client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));

        	    	Button Disconnectbtn = new Button("Disconnect");
        	    	Button Settingsbtn = new Button("Settings");
    				HBox DisSetSection = new HBox(10);
    				DisSetSection.setAlignment(Pos.CENTER_LEFT);
    				DisSetSection.getChildren().add(Disconnectbtn);
    				DisSetSection.getChildren().add(Settingsbtn);
    				grid.add(DisSetSection, 1, 0);
    				
    				Disconnectbtn.setOnAction(new EventHandler<ActionEvent>() {
    			        @Override
    			            public void handle(ActionEvent e) {
    			        		if(connected) {
    			        			client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
    			        			Disconnectbtn.setText("Connect");
    			        			listView.getItems().clear();
    			        			connected = false;
    			        		} else if (!connected) {
    			        			reconnectToServer();
    			        			Disconnectbtn.setText("Disconnect");
    			        			connected = true;
    			        	    	client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));
    			        		}
    			        }
    				});
    				
    				Settingsbtn.setOnAction(new EventHandler<ActionEvent>() {
    			        @Override
			            public void handle(ActionEvent e) {
    			        	new Settings().start(new Stage());
    			        }
    				});
    				
    				chatArea.setEditable(false);
    				chatArea.setWrapText(true);
    				grid.add(chatArea, 0, 1);
    				
    				listView.setEditable(false);
    				listView.setSelectionModel(null);
    				grid.add(listView, 1, 1);
    				
    				TextField msgTextField = new TextField();
    				grid.add(msgTextField, 0, 2);
    				msgTextField.requestFocus();
    				
    				msgTextField.setOnAction(new EventHandler<ActionEvent>() {
    			        @Override
			            public void handle(ActionEvent e) {
    			        	if(!msgTextField.getText().equals("")) {
    			        		client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msgTextField.getText()));
    			        		msgTextField.setText("");
    			        	}
    			        }
    				});

    				Button sendMsgbtn = new Button("Send");
    				Button attachFilebtn = new Button("File");
    				HBox btns = new HBox(10);
    				btns.setAlignment(Pos.CENTER_LEFT);
    				btns.getChildren().add(sendMsgbtn);
    				btns.getChildren().add(attachFilebtn);
    				grid.add(btns, 1, 2);
    				
    				sendMsgbtn.setOnAction(new EventHandler<ActionEvent>() {
    			        @Override
			            public void handle(ActionEvent e) {
    			        	if(!msgTextField.getText().equals("")) {
    			        		client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msgTextField.getText()));
    			        		msgTextField.setText("");
    			        	}
    			        }
    				});
    				
    				attachFilebtn.setOnAction(new EventHandler<ActionEvent>() {
    			        @Override
			            public void handle(ActionEvent e) {
    			        	
    			        	FileChooser fileChooser = new FileChooser();
	    					fileChooser.setTitle("Select File");
    			        	File selectedFile = fileChooser.showOpenDialog(null);
    			        	
    			        	if (selectedFile != null) {
	    					    ImageView icon = new ImageView(new Image(this.getClass().getResourceAsStream("res/icon64.png")));
	    				        Notifications.create().title("Chat: "+server+":"+port).text("File selected: " + selectedFile.getName()).graphic(icon).show();
	    				        try {
									SendFile(selectedFile);
								} catch (IOException e1) {
									Notifications.create().title("Chat: "+server+":"+port).text("Error Sending File: " + selectedFile.getName()).showError();
								}
    			        	}
    			        }
    				});


    				
    				clearButton.setOnAction(new EventHandler<ActionEvent>() {
    			        @Override
			            public void handle(ActionEvent e) {
			        		chatArea.clear();
			        		chatArea.appendText("Cleared at: " + new Timestamp(System.currentTimeMillis())+ "\n");
    			        }
    				});
    			} else {
    				scenetitle.setText("Unable to connect to server.");
    				
    				Button Reconnectbtn = new Button("Reconnect");
    				HBox rcBtn = new HBox(10);
    				rcBtn.setAlignment(Pos.CENTER_LEFT);
    				rcBtn.getChildren().add(Reconnectbtn);
    				grid.add(rcBtn, 1, 0);
    				
    				Reconnectbtn.setOnAction(new EventHandler<ActionEvent>() {
    			        @Override
			            public void handle(ActionEvent e) {
		        			initChat(chatStage);
    			        }
    				});
    			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			scenetitle.setText("Unable to connect to server.");
			
			Button Reconnectbtn = new Button("Reconnect");
			HBox rcBtn = new HBox(10);
			rcBtn.setAlignment(Pos.CENTER_LEFT);
			rcBtn.getChildren().add(Reconnectbtn);
			grid.add(rcBtn, 1, 0);
			
			Reconnectbtn.setOnAction(new EventHandler<ActionEvent>() {
		        @Override
	            public void handle(ActionEvent e) {
        			initChat(chatStage);
		        }
			});
		}
    }
    
    public void append(String msg) {
		try {
			if(msg.contains("ServerResetUserList:")) {
				Platform.runLater(new Runnable() {
    			    @Override
    			    public void run() {
    			    	listView.getItems().clear();
    			    }
    			});
			} else {
				if(msg.contains("ServerAddToUserList:")) {
					Platform.runLater(new Runnable() {
	    			    @Override
	    			    public void run() {
	    					listView.getItems().add(msg.replace("ServerAddToUserList:", ""));
	    			    }
	    			});
				} else if(msg.contains("|ClintSendFile|")) {
					//System.out.println(msg);
					
					String s = new String(msg);
					
					String[] separatedMsg = s.split("\\|");

					String userAndTime = separatedMsg[0];
					String fileName = separatedMsg[2];
					String fileEncoded = msg.replace(userAndTime + "|ClintSendFile|" + fileName + "|", "");
					 
					System.out.println("Saving: "+ fileName);
					
					byte[] encoded = fileEncoded.getBytes("ISO-8859-1");

					if(!msg.contains(username)) {
						//  save file as
						Platform.runLater(new Runnable() {
			    			    @Override
			    			    public void run() {
			    					FileChooser fileChooser = new FileChooser();
			    					fileChooser.setTitle("Save File");
			    					if(isWindows()) {
				    					File userDirectory = new File(System.getProperty("user.home")+"\\Downloads");
				    					if(!userDirectory.canRead())
				    					    userDirectory = new File("c:/");
				    					fileChooser.setInitialDirectory(userDirectory);
			    					}
			    					
			    					fileChooser.setInitialFileName(separatedMsg[2]);
			    					File savedFile = fileChooser.showSaveDialog(null);
		
			    					if (savedFile != null) {
		
			    					    try {
			    					    	FileUtils.writeByteArrayToFile(savedFile, encoded);
			    					    }
			    					    catch(IOException e) {
			    						
			    					        e.printStackTrace();
			    					        Notifications.create().title("Chat: "+server+":"+port).text("An ERROR occurred while saving the file").showError();
			    					        return;
			    					    }
			    					    ImageView icon = new ImageView(new Image(this.getClass().getResourceAsStream("res/icon64.png")));
			    				        Notifications.create().title("Chat: "+server+":"+port).text("File saved: " + savedFile.toString()).graphic(icon).show();
			    					}
		    					}
			    			});
					}
					
					userAndTime=null;
					fileName=null;
					fileEncoded=null;
				} else {
					chatArea.appendText(msg);
					if(!msg.contains(username)) {
	        			Platform.runLater(new Runnable() {
	        			    @Override
	        			    public void run() {
	        			    	ImageView icon = new ImageView(new Image(this.getClass().getResourceAsStream("res/icon64.png")));
	        			    
	        			    	Notifications.create().title("Chat: "+server+":"+port).text(msg).graphic(icon).show();
	        			    }
	        			});
	        		}
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
    
    private void reconnectToServer() {
    	client = new Client(server, port, username, this);
    	client.start();
	}
    
    private void SendFile(File file) throws IOException {
    	// convert file to byte array then to string to send to server
		byte[] str = FileUtils.readFileToByteArray(file);
		String decoded = new String(str, "ISO-8859-1");
		client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, "|ClintSendFile|" + file.getName() + "|" + decoded));
	}
    
    public void connectionFailed() {
		chatArea.appendText("Connection to chat server failed");
	}
    
	public static boolean isWindows() {

		return (OS.indexOf("win") >= 0);

	}
    
    // decrypts the password for database
    public String Decrypt(String text) throws NoSuchAlgorithmException {
    	
		String salt = "nb9af3uobu80ag87bpfu4iwef";
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update((text+salt).getBytes());
        byte byteData[] = md.digest();

        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }

        //convert the byte to hex format method 2
        StringBuffer hexString = new StringBuffer();
	    	for (int i=0;i<byteData.length;i++) {
	    		String hex=Integer.toHexString(0xff & byteData[i]);
	   	     	if(hex.length()==1) hexString.append('0');
	   	     	hexString.append(hex);
	    	}
	    	return hexString.toString();
    }
    
    @Override
    public void stop(){
    	try {
    		client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
		} catch (Exception e) {}
    }
    
	public static void main(String[] args) {
		launch(args);
	}
}
