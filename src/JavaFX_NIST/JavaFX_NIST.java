/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JavaFX_NIST;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author jim Adopted from Oracle's Login Tutorial Application
 * https://docs.oracle.com/javafx/2/get_started/form.htm
 */
public final class JavaFX_NIST extends Application {
    // declare and initialize logger
    static final Logger logger = Logger.getLogger("JavaFX_NIST Log");
    
    // declare fail counts and time for account lockout
    int failedAttempt;
    LocalDateTime ldt;
    LocalDateTime lockTime;
    
    //begin GUI
    @Override
    public void start(Stage primaryStage) {
        // set main title
        primaryStage.setTitle("JavaFX_NIST Login");
        // Grid Pane divides your window into grids
        GridPane grid = new GridPane();
        // Align to Center
        // Note Position is geometric object for alignment
        grid.setAlignment(Pos.CENTER);
        // Set gap between the components
        // Larger numbers mean bigger spaces
        grid.setHgap(10);
        grid.setVgap(10);

        // Display welcome screen
        Text scenetitle = new Text("Welcome.  Login to continue.");
        // Add text to grid 0,0 span 2 columns, 1 row
        grid.add(scenetitle, 0, 0, 2, 1);

        // Create username Label
        Label userName = new Label("User Name:");
        // Add label to grid 0,1
        grid.add(userName, 0, 1);

        // Create username Textfield
        TextField userTextField = new TextField();
        // Add textfield to grid 1,1
        grid.add(userTextField, 1, 1);

        // Create password Label
        Label pw = new Label("Password:");
        // Add label to grid 0,2
        grid.add(pw, 0, 2);

        // Create Passwordfield
        PasswordField pwBox = new PasswordField();
        // Add Password field to grid 1,2
        grid.add(pwBox, 1, 2);

        // Create Login Button
        Button btn = new Button("Login");
        // Add button to grid 1,4
        grid.add(btn, 1, 4);

        // check number of failed attempts.  If no attempts exist, do not display.
        // if failed attempts are present, display notice with number of failed
        // attempts and time account will be unlocked for further attempts
        if (failedAttempt > 0){
            Text actiontarget = new Text();
            grid.add(actiontarget, 1, 6);
            actiontarget.setFill(Color.FIREBRICK);
            actiontarget.setText("Authentication Failed."
                                + "\n Attempt: " + failedAttempt +
                                "\n Account locked until: " + lockTime);
        }else{
            // no failed attempts yet, show nothing
        }
        
        // Set the Action when button is clicked.
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override // listen for login button click
            public void handle(ActionEvent e) {
                // Try-Catch is to catch null pointer exceptions and ignore
                // while we wait for account timeout to expire
                try{
                    // while failed attempts are less than 3
                    if (failedAttempt < 3){

                        // Initial authentication of the user, set username for log use
                        String user = userTextField.getText();

                        boolean isValid = authenticate(user, pwBox.getText());

                        // If first factor is valid, send 2nd factor token via email
                        // and show system use acceptance banner screen with hyperlink
                        if (isValid) {
                            // step 1 authenticated, send token and create log
                            SendEmail email = new SendEmail(user);
                            sendLog(user, "Success1");

                            // Start new view
                            grid.setVisible(false);
                            GridPane grid1a = new GridPane();
                            // Align to Center
                            // Note Position is geometric object for alignment
                            grid1a.setAlignment(Pos.CENTER);
                            // Set gap between the components
                            // Larger numbers mean bigger spaces
                            grid1a.setHgap(10);
                            grid1a.setVgap(10);

                            // display banner alert to warn user of terms and conditions
                            Text scenetitle1a = new Text("By entering your token below and authenticating,"
                                    + "\n you agree to the terms of using this system.");
                            // Add text to grid 0,0 span 2 columns, 1 row
                            grid1a.add(scenetitle1a, 0, 0, 2, 1);

                            // Create hyperlink label
                            Label hyperlinkLabel = new Label("Terms and Services:");
                            // Add label to grid 0,1
                            grid1a.add(hyperlinkLabel, 0, 1);

                            // create Hyperlink for Terms and Conditions
                            File file = new File("Terms and Services.txt");
                            Hyperlink terms = new Hyperlink();
                            terms.setText("Terms and Services");
                            terms.setOnAction((ActionEvent click) ->{
                                // try to open file with default program when
                                // hyperlink is clicked
                                if (Desktop.isDesktopSupported()) {
                                    try{
                                        Desktop.getDesktop().open(file);
                                    }catch(IOException | IllegalArgumentException f){
                                        System.out.print("File not found.");
                                    }
                                } else {
                                    System.out.print("Unable to open file.");
                                }
                            });
                            // Add hyperlink to grid 1,1
                            grid1a.add(terms, 1, 1);

                            // Create token Label
                            Label factorTwo = new Label("Token:");
                            // Add label to grid 0,1
                            grid1a.add(factorTwo, 0, 2);

                            // Create token Textfield
                            TextField userTokenField = new TextField();
                            // Add textfield to grid 1,1
                            grid1a.add(userTokenField, 1, 2);

                            // Create Authenticate Button
                            Button btn1a = new Button("Agree and Authenticate");
                            // Add button to grid 1,4
                            grid1a.add(btn1a, 1, 4);

                            Scene scene = new Scene(grid1a, 500, 400);
                            primaryStage.setScene(scene);
                            primaryStage.show();


                            // Set the Action when button is clicked
                            btn1a.setOnAction(new EventHandler<ActionEvent>() {

                                @Override
                                public void handle(ActionEvent e) {

                                    // if authentication is validated, send to log
                                    // and display welcome screen
                                    boolean isValid2 = authenticate2(userTokenField.getText(), email.getToken(), email.getExpire());   

                                    if (isValid2) {
                                        grid1a.setVisible(false);
                                        GridPane grid2 = new GridPane();
                                        // Align to Center
                                        // Note Position is geometric object for alignment
                                        grid2.setAlignment(Pos.CENTER);
                                        // Set gap between the components
                                        // Larger numbers mean bigger spaces
                                        grid2.setHgap(10);
                                        grid2.setVgap(10);
                                        Text scenetitle2 = new Text("Welcome " + user + "!");
                                        // Add text to grid 0,0 span 2 columns, 1 row
                                        grid2.add(scenetitle2, 0, 0, 2, 1);
                                        Scene scene = new Scene(grid2, 500, 400);
                                        primaryStage.setScene(scene);
                                        primaryStage.show();
                                        // send to log
                                        sendLog(user, "Success2");
                                    }else{
                                        // 2nd authentication failed.
                                        // check if first attempt
                                        if(failedAttempt == 0){
                                            // first attempt failed; increase count,
                                            // set account time lock, send log,
                                            // return to main screen
                                            failedAttempt = ++failedAttempt;
                                            lockTime = LocalDateTime.now().plusMinutes(3);
                                            sendLog(user, "Fail2");
                                            
                                            // do not pass go, do not collect $200
                                            start(primaryStage);
                                        // else not first attempt, check time lock    
                                        }else{
                                            // if account is not currently timelocked; send log, display message
                                            if(LocalDateTime.now().isAfter(lockTime) || ldt.equals(lockTime)){
                                                failedAttempt = ++failedAttempt;
                                                lockTime = LocalDateTime.now().plusMinutes(failedAttempt * 3);
                                                
                                                // three strikes and you're out
                                                if(failedAttempt == 3){
                                                    sendLog(user, "Lock");
                                                // otherwise, log
                                                }else{
                                                    sendLog(user, "Fail2");
                                                }
                                                // do not pass go, do not collect $200
                                                start(primaryStage);
                                            }else{
                                                // account time lock is still active,
                                                // null pointers activated and ignored
                                                // until appropriate time has passed
                                            }
                                        }
                                    }
                                }
                            });
                        }else{
                            // 1st authentication failed
                            // check if first attempt
                            if(failedAttempt == 0){
                                // first attempt failed; increase count,
                                // set account time lock, send log,
                                // return to main screen 
                                failedAttempt = ++failedAttempt;
                                lockTime = LocalDateTime.now().plusMinutes(3);
                                sendLog(user, "Fail1");
                                
                                // do not pass go, do not collect $200
                                start(primaryStage);
                            // else not first attempt, check time lock    
                            }else{
                                // if account is not currently timelocked; send log, display message
                                if(LocalDateTime.now().isAfter(lockTime) || ldt.equals(lockTime)){
                                    failedAttempt = ++failedAttempt;
                                    lockTime = LocalDateTime.now().plusMinutes(failedAttempt * 3);
                                    
                                    // three strikes and you're out
                                    if(failedAttempt == 3){
                                        sendLog(user, "Lock");
                                    // otherwise, log
                                    }else{
                                        sendLog(user, "Fail1");
                                    }
                                    // do not pass go, do not collect $200
                                    start(primaryStage);
                                }else{
                                    // account time lock is still active,
                                    // null pointers activated and ignored
                                    // until appropriate time has passed
                                }
                            }               
                        }
                    // too many failed attempts, lock account
                    }else{
                        grid.setVisible(false);
                        GridPane gridFinal = new GridPane();
                        // Align to Center
                        // Note Position is geometric object for alignment
                        gridFinal.setAlignment(Pos.CENTER);
                        // Set gap between the components
                        // Larger numbers mean bigger spaces
                        gridFinal.setHgap(10);
                        gridFinal.setVgap(10);
                        // display alert to user on new screen
                        Text scenetitleFinal = new Text("Max number of attempts exceeded."
                                + "\n\n Your account has been locked."
                                + "\n\n Please contact the Administrator to reset password.");
                        scenetitleFinal.setFill(Color.RED);
                        // Add text to grid 0,0 span 2 columns, 1 row
                        gridFinal.add(scenetitleFinal, 0, 0, 2, 1);
                        Scene scene = new Scene(gridFinal, 500, 400);
                        primaryStage.setScene(scene);
                        primaryStage.show();
                    }
                // catch all null pointer exceptions and ignore while account
                // timeout is still active
                }catch(NullPointerException np){
                    //wait until account is unlocked and pointer reactivates
                }
            }
        });
        // show main
        Scene scene = new Scene(grid, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * @param user the username entered
     * @param pword the password entered
     * @return isValid true for authenticated
     */
    public boolean authenticate(String user, String pword) {
        boolean isValid = false;
        if (user.equalsIgnoreCase("testadmin")
                && pword.equals("adm!np@$$")) {
            isValid = true;
        }

        return isValid;
    }
    
    // check 2nd factor authentication
    public boolean authenticate2(String usrToken, String token, LocalDateTime expire){
        boolean isValid2 = false;
        
        LocalDateTime now = LocalDateTime.now();
        if (usrToken.equals(token) && now.isBefore(expire)){
            isValid2 = true;
        }
        
        return isValid2;
    }
    
    // method for log file
    public void sendLog(String user, String event){
        // declare log info string
        String log = null;
    
        // Set log info based on event
        try{
            switch(event){
                case "Success1":
                    log = user + " factor one success.";
                    break;
                case "Success2":
                    log = user + " factor two success.";
                    break;
                case "Fail1":
                    log = user + " factor one failed.";
                    break;
                case "Fail2":
                    log = user + " factor two failed.";
                    break;
                case "Lock":
                    log = user + " account locked.";
                default:
                    break;
            }
        // this shouldn't happen, but catch anyway out of safety
        }catch(Exception logEr){
            System.out.println("File IO exception " + logEr.getMessage());
        }
        
        // send event to log
        FileHandler fh;
        try{
            // configure logger with formatter and handler
            //try to get file, alert admin of errors
            try{
                fh = new FileHandler("JavaFX_NIST Log.log", true);
                logger.addHandler(fh);
                SimpleFormatter formatter = new SimpleFormatter();
                fh.setFormatter(formatter);
                logger.setUseParentHandlers(false);
                // set log message
                logger.info(log);
                fh.close();
            }catch(FileNotFoundException fnf){
                // send alert to admin and shut down program to prevent
                // futher action while logging is inactive
                AuditAlert alert = new AuditAlert(user);
                System.exit(0);
            }
        }catch (SecurityException | IOException se){
            AuditAlert alert = new AuditAlert(user);
            System.exit(0);
        }
    }
}
