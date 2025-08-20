package ca.demo.Controllers;

import ca.demo.InventoryApplication;
import ca.demo.Models.Login;
import ca.demo.Utility.SceneName;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginController {

    @FXML private Button loginBtn;
    @FXML private Label loginStatus;
    @FXML private PasswordField passwordPF;
    @FXML private TextField usernameTF;


    public void initialize(){

        // Disable the button initially
        loginBtn.setDisable(true);

        usernameTF.textProperty().addListener((obs, oldVal, newVal) -> updateLoginButton());
        passwordPF.textProperty().addListener((obs, oldVal, newVal) -> updateLoginButton());
    }

    private void updateLoginButton() {
        loginBtn.setDisable(usernameTF.getText().trim().isEmpty() || passwordPF.getText().trim().isEmpty());
    }

    @FXML
    void onLoginClick(ActionEvent event) {
        handleLogin();
    }

    public void handleLogin(){
        Login loginAttempt = new Login(usernameTF.getText(), passwordPF.getText());

        if (loginAttempt.validate()){
            loginStatus.setText("Login successful!");

            // Create a PauseTransition of 1 second (1000 milliseconds)
            PauseTransition pause = new PauseTransition(Duration.seconds(0.5));

            // Set an action to execute after the pause
            pause.setOnFinished(event -> {
                loadLoanApplicationView(); // This will be called after the 1-second pause
            });

            // Start the pause
            pause.play();

        }  else {
            loginStatus.setText("Wrong username or password!");
            usernameTF.requestFocus();
            usernameTF.selectAll();
        }
    }

    private void loadLoanApplicationView() {
        resetFields();
        Stage currentStage = (Stage) loginBtn.getScene().getWindow();
        currentStage.setTitle("Main View");
        currentStage.setScene(InventoryApplication.getScenes().get(SceneName.MAIN));
    }

    private void resetFields(){
        usernameTF.clear();
        passwordPF.clear();
        loginStatus.setText("");
    }
}
