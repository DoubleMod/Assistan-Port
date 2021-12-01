import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class SetController {
    @FXML
    private AnchorPane anchorage;

    private TextArea sendText;

    @FXML
    private TextArea setText;

    public TextArea getSendText() {
        return sendText;
    }

    public void setSendText(TextArea sendText) {
        this.sendText = sendText;
    }

    public SetController() {
    }

    @FXML
    void close(DragEvent event) {

        Stage stage = (Stage) anchorage.getScene().getWindow();
        stage.close();
    }

    @FXML
    void onTextAreaChanged(KeyEvent event) {
        String text = setText.getText();
        sendText.setText(text);

        System.out.println(text);
    }
}