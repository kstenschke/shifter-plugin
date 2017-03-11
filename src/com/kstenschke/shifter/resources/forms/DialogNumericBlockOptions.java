package com.kstenschke.shifter.resources.forms;

import com.kstenschke.shifter.ShifterPreferences;

import javax.swing.*;
import java.awt.event.*;

public class DialogNumericBlockOptions extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JRadioButton radioInsertEnumeration;
    private JRadioButton radioInDecrementEach;
    private JSpinner spinnerFirstEnumerationNumber;

    private boolean wasCancelled = false;

    public DialogNumericBlockOptions(Integer firstNumber) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        boolean isEnumerationMode = ShifterPreferences.getShiftNumericalBlockMode() == ShifterPreferences.SORTING_MODE_NUMERICAL_BLOCK_ENUM;
        radioInsertEnumeration.setSelected(isEnumerationMode);
        radioInDecrementEach.setSelected(!isEnumerationMode);
        spinnerFirstEnumerationNumber.setValue(firstNumber);

        spinnerFirstEnumerationNumber.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                radioInsertEnumeration.setSelected(true);
            }

            @Override
            public void focusLost(FocusEvent focusEvent) {

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

    private void saveSelectedMode() {
        ShifterPreferences.saveShiftNumericalBlockMode(radioInsertEnumeration.isSelected()
                ? ShifterPreferences.SORTING_MODE_NUMERICAL_BLOCK_ENUM
                : ShifterPreferences.SORTING_MODE_NUMERICAL_BLOCK_INC_DEC
        );
    }

    private void onOK() {
        saveSelectedMode();
        dispose();
    }

    private void onCancel() {
        wasCancelled = true;
        dispose();
    }

    public boolean wasCancelled() {
        return wasCancelled;
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
        return spinnerFirstEnumerationNumber.getValue().toString();
    }
}
