import gnu.io.SerialPortEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

import javax.swing.*;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class SerialAssistant {
    @FXML
    private TextArea receiveText;
    @FXML
    private TextArea sendText;
    @FXML
    private Button openBnt;
    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private ComboBox<Integer> BaudRateBox;
    @FXML
    private CheckBox checkBox;

    private SerialController serialController;
    private List<String> systemPorts;
    private boolean isOpen = false;

    public SerialAssistant() {

        serialController = new SerialController();
    }

    /**
     * 点击commbox，刷新里面的数据
     */
    public void onShowComboBox(Event event) {
        systemPorts = SerialController.getSystemPort();
        comboBox.getItems().clear();
        comboBox.getItems().addAll(systemPorts);
    }

    /**
     * 点击commbox，刷新里面的数据
     */
    public void onActionClickCheck(Event event) {

//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("温馨提示");
//        alert.setHeaderText("提示对话");
//        alert.setContentText("请先选择串口");
//        alert.showAndWait();
//        checkBox.setSelected(true);
        preClickCheck("请先选择串口");
        System.out.println("click");
    }

    private boolean preClickCheck(String content) {
        if (comboBox.getValue() == null || BaudRateBox.getValue() == null) {
            JOptionPane.showMessageDialog(null, content, "温馨提示", JOptionPane.QUESTION_MESSAGE);
            return true;
        }
        return false;
    }

    /**
     * 点击BaudRateBox,显示可供选择的波特率
     */
    public void onShowBaudRateBox(Event event) {
        BaudRateBox.getItems().addAll(2400, 4800, 9600, 19200, 38400, 57600, 115200);
        BaudRateBox.setVisibleRowCount(4);
    }

    /**
     * 打开串口
     */
    public void onActionOpenBtn(ActionEvent actionEvent) {
        isOpen = !isOpen;
        if (isOpen) {

            if (preClickCheck("请先选择串口和频率")) {
                return;
            }

            if (!serialController.open(comboBox.getValue(), BaudRateBox.getValue())) {
                return;
            }
            serialController.setListenerToSerialPort(ev -> {
                if (ev.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
                    String str = null;
                    try {
                        str = serialController.readData();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String finalStr = str;
                    Platform.runLater(() -> {
                        if (receiveText.getLength() < 4000) {
                            receiveText.appendText(finalStr);
                        } else {
                            receiveText.deleteText(0, finalStr.length());
                            receiveText.appendText(finalStr);
                        }
                    });

                }
            });
            openBnt.setText("关闭串口");
        } else {
            serialController.close();
            openBnt.setText("打开串口");
        }
    }

    /**
     * 发送数据
     */
    public void onActionSendBtn(ActionEvent actionEvent) {
        if (preClickCheck("请先选择串口和频率")) {
            return;
        }
        if (sendText.getText() == null) {
            return;
        }
        serialController.sendData(sendText.getText());
    }

    /**
     * 清除接收框
     */
    public void clear(ActionEvent actionEvent) {
        if (preClickCheck("请先选择串口和频率")) {
            return;
        }
        receiveText.clear();
    }
}

