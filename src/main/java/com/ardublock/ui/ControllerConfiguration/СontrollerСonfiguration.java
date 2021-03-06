package com.ardublock.ui.ControllerConfiguration;

import java.awt.*;
import com.mit.blocks.codeblockutil.RMenuButton;
import java.util.ArrayList;
import javax.swing.JPanel;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import org.jfree.ui.tabbedui.VerticalLayout;

public class СontrollerСonfiguration extends JPanel {

    //верхняя панель контроллера
    public ControllerImage controllerImage;

    //лист всевозможных компонентов на подключение
    private List<Device> components;
    //нижняя панель
    private JPanel componentsPane;

    public enum Pin {

        dir04pwm05, dir07pwm06, d2, d3, d8, d10, d9, d11, a3, a2, a1, a0, i2c
    };
    public Pin controllerPin;

    public СontrollerСonfiguration() {
        super();
        this.setLayout(new BorderLayout());
        this.components = new ArrayList<>();
        this.controllerImage = new ControllerImage(this);
        this.componentsPane = new JPanel(new VerticalLayout());
        componentsPane.setBackground(Color.white);
        //buttonPane.setBorder(BorderFactory.createMatteBorder(3, 0, 0, 0, new Color(55, 150, 240)));
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
                controllerImage, componentsPane);
        splitPane.setDividerLocation(300/*750 / 2*/);
        this.add(splitPane);
        //this.controllerImage.add(justButt);
//        this.addButtons();
    }

//    public void addButtons() {
//        this.controllerImage.add(callButtons());
//    }
//
//    public Container callButtons() {
//        Container allButtons = new Container();
//        JButton testVisibleButton = new JButton();
//        testVisibleButton.setLayout(null);
//        testVisibleButton.setBounds(10, 10, 100, 100);
//        allButtons.add(testVisibleButton);
//        return allButtons;
//    }

    public void addComponent(String pin, String name) {
        this.components.add(new Device(pin, name));
    }

    public void changeConnectorComponentsPane(String buttonPin) {
        componentsPane.removeAll();
        for (int i = 0; i < components.size(); i++) {
            if (components.get(i).pin.equals(buttonPin)) {
                componentsPane.add(new ControllerMenuButton(
                        this, components.get(i).deviceName, buttonPin));
            }
        }
        componentsPane.validate();
        componentsPane.repaint();
    }
    
    public void changeModuleComponentsPane(String buttonPin) {
        componentsPane.removeAll();
        JLabel info = new JLabel();
        for (int i = 0; i < components.size(); i++) {
            if (components.get(i).pin.equals(buttonPin)) {
                info.setText(components.get(i).deviceInfo);
                info.setBackground(Color.black);
                componentsPane.add(new ControllerMenuButton(
                        this, components.get(i).deviceName, buttonPin));
                componentsPane.add(info);
            }
        }
        componentsPane.validate();
        componentsPane.repaint();
    }

    /**
     * Класс отвечающий за информацию о подключаемом устроистве
     */
    private static class Device {

        final String pin;
        final String deviceName;
        final String deviceInfo = "info";

        public Device(String pin, String deviceName) {
            this.pin = pin;
            this.deviceName = deviceName;
        }
    }
}
