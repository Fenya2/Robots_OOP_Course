package course.oop.gui;

import course.oop.locale.UserLocale;
import course.oop.locale.UserLocaleManager;
import course.oop.log.Logger;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * Диалог выбора языка пользователем (по умолчанию - )
 */
public class SelectLanguageDialog extends JDialog {

    private UserLocale choosedUserLocale;

    public SelectLanguageDialog(Frame parent) {
        super(parent, "0", true);
        ResourceBundle bundle = UserLocaleManager.getCurrentBundle();
        setTitle(bundle.getString("select_lang_dialog"));
        setLayout(new BorderLayout());

        ButtonGroup group = new ButtonGroup();
        JRadioButton optionEn = new JRadioButton(UserLocale.EN.toString());
        JRadioButton optionRu = new JRadioButton(UserLocale.RU.toString());
        optionEn.setSelected(true);
        group.add(optionEn);
        group.add(optionRu);

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridLayout(2, 1));
        optionsPanel.add(optionEn);
        optionsPanel.add(optionRu);

        add(optionsPanel, BorderLayout.CENTER);

        JPanel execPanel = new JPanel();
        execPanel.setLayout(new GridLayout(1, 2));

        JButton submitButton = new JButton(bundle.getString("select_lang_dialog_submit"));
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (optionEn.isSelected()) {
                    choosedUserLocale = UserLocale.EN;
                } else if (optionRu.isSelected()) {
                    choosedUserLocale = UserLocale.RU;
                }
                dispose();
            }
        });

        JButton cancelButton = new JButton(bundle.getString("select_lang_dialog_cancel"));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        execPanel.add(submitButton);
        execPanel.add(cancelButton);

        add(execPanel, BorderLayout.SOUTH);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    public UserLocale getResult() {
        return choosedUserLocale;
    }

    public UserLocale getChoosedUserLocale() {
        return choosedUserLocale;
    }
}