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
    private ComboBox<Integer> baudRateBox;
    @FXML
    private CheckBox showCheckBox;
    @FXML
    private CheckBox sendCheckBox;

    private SerialController serialController;
    private List<String> systemPorts;
    private boolean isOpen = true;

    public SerialAssistant() {
        serialController = new SerialController();
    }

    /**
     * 点击commbox，刷新里面的数据
     */
    public void onShowComboBox(Event event) {
//        systemPorts = SerialController.getSystemPort();
        systemPorts = SerialPortUtil.getSerialPortList();
        comboBox.getItems().clear();
        comboBox.getItems().addAll(systemPorts);
    }

    /**
     * 点击commbox，刷新里面的数据
     */
    public void onActionClickCheckShow(Event event) {
    }

    /**
     * 点击commbox，刷新里面的数据
     */
    public void onActionClickCheckSend(Event event) {
    }

    private boolean preClickCheck(String content) {
        if (comboBox.getValue() == null || baudRateBox.getValue() == null) {
            JOptionPane.showMessageDialog(null, content, "温馨提示", JOptionPane.QUESTION_MESSAGE);
            return true;
        }
        return false;
    }

    /**
     * 点击BaudRateBox,显示可供选择的波特率
     */
    public void onShowBaudRateBox(Event event) {
        baudRateBox.getItems().addAll(2400, 4800, 9600, 19200, 38400, 57600, 115200);
        baudRateBox.setVisibleRowCount(4);
    }

    /**
     * 打开串口
     */
    public void onActionOpenBtn(ActionEvent actionEvent) {

        if (isOpen) {

            if (preClickCheck("请先选择串口和频率")) {
                return;
            }

            if (!serialController.open(comboBox.getValue(), baudRateBox.getValue())) {
                return;
            }
//            serialController.sendData("CC 12 34 56 78 CC");
            serialController.setListenerToSerialPort(ev -> {
                if (ev.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
                    String str = null;
                    try {
                        str = serialController.readData();
                        System.out.println("接收数据: " + str);
                        if ("CC 1A 2B 3C 4D CC".equals(str)) {
                            JOptionPane.showMessageDialog(null, "握手成功!", "温馨提示", JOptionPane.QUESTION_MESSAGE);
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if (StringUtils.isNotEmpty(str)) {
                        String finalStr = str;
                        Platform.runLater(() -> {
                            if (receiveText.getLength() < 4000) {
                                receiveText.appendText(finalStr);
                                receiveText.appendText("\n");
                            } else {
                                receiveText.deleteText(0, finalStr.length());
                                receiveText.appendText(finalStr);
                            }
                        });
                    }
                }
            });
            comboBox.setDisable(true);
            baudRateBox.setDisable(true);
            isOpen = !isOpen;
            openBnt.setText("关闭串口");
//            Platform.runLater(()->{
//                while (true) {
//                    serialController.sendData("CC 1A 2B 3C 4D CC");
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });


        } else {
            comboBox.setDisable(false);
            baudRateBox.setDisable(false);
            serialController.close();
            isOpen = !isOpen;
            openBnt.setText("打开串口");
        }
    }

    /**
     * 发送数据
     */
    public void onActionSendBtn(ActionEvent actionEvent) {
        if (preClickCheck("请先选择串口和波特率") ) {
            return;
        }
        serialController.sendData(sendText.getText());
    }

    /**
     * 清除接收框
     */
    public void clear(ActionEvent actionEvent) {
        receiveText.clear();
    }
}

