package com.kstenschke.shifter.resources.forms;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;

public class DialogNumericBlockOptions extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JRadioButton radioInsertEnumeration;
    private JRadioButton radionInDecrementEach;
    private JSpinner spinnerFirstNumber;

    public DialogNumericBlockOptions(Integer firstNumber) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        spinnerFirstNumber.setValue(firstNumber);
        spinnerFirstNumber.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                radioInsertEnumeration.setSelected(true);
            }
        });

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
        DialogNumericBlockOptions dialog = new DialogNumericBlockOptions(0);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    public boolean isShiftModeEnumerate() {
        return radioInsertEnumeration.isSelected();
    }

    public String getFirstNumber() {
        return spinnerFirstNumber.getValue().toString();
    }
}
