import gnu.io.SerialPortEvent;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.swing.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;

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

            if (preClickCheck("请先选择串口和波特率")) {
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

    private Stage settingsStage;

    /**
     * 打开新窗口
     * @param event
     * @throws IOException
     */
    @FXML
    public void startCapture(ActionEvent event) throws IOException {

        if (settingsStage != null) {
            return;
        }

//        AnchorPane settings = FXMLLoader.load(getClass().getResource("SerialWindow.fxml"));

        FXMLLoader loader =
                new FXMLLoader(Objects.requireNonNull(getClass().getResource("SerialWindow.fxml")));
        // 直接将单例放到工厂类中
        loader.setControllerFactory(param -> {
            SetController controller = null;
            try {
                controller = new SetController();
                // 这里赋值
                controller.setSendText(sendText);
                // 这里设置需要的属性
            } catch (Exception e) {
                e.printStackTrace();
            }
            return controller;
        });
        Parent settings = loader.load();
        settingsStage = new Stage();
        settingsStage.setTitle("配置");
        settingsStage.setScene(new Scene(settings));
        settingsStage.setResizable(false);
        //捕获窗口 关闭监听器
        settingsStage.setOnCloseRequest(ev -> {
            settingsStage = null;
        });
        settingsStage.show();
    }
}

