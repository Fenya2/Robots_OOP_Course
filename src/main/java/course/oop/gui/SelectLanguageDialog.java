package course.oop.gui;

import course.oop.locale.UserLocale;
import course.oop.locale.LocaleManager;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * Диалог выбора локали (языка) пользователем
 */
public class SelectLanguageDialog extends JDialog {

    /**
     * Хранит выбранную пользователем локаль.
     */
    private UserLocale choosedUserLocale;

    /**
     * Конструктор диалога. Создает окошко с радио-кнопками выбора языка,
     * кнопкой подтверждения и отмены. Заполняет (или оставляет
     * непроинициализированным) choosedUserLocale в зависимости от действий
     * пользователя: заполняет соответствующим языком, если пользователь нажал
     * кнопку подтверждения. В любом случае по нажатии на любую из кнопок,
     * вызывает dispose окна диалога.
     */
    public SelectLanguageDialog(Frame parent) {
        super(parent, "0", true);
        setTitle(LocaleManager.getString("select_language_dialog.title"));
        setLayout(new BorderLayout());

        ButtonGroup group = new ButtonGroup();
        JRadioButton optionEn = new JRadioButton(UserLocale.EN.toString());
        JRadioButton optionRu = new JRadioButton(UserLocale.RU.toString());

        switch (LocaleManager.getInstance().getCurrentLocale()) {
            case EN:
                optionEn.setSelected(true);
                break;
            case RU:
                optionRu.setSelected(true);
                break;
        }

        group.add(optionEn);
        group.add(optionRu);

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridLayout(2, 1));
        optionsPanel.add(optionEn);
        optionsPanel.add(optionRu);

        add(optionsPanel, BorderLayout.CENTER);

        JPanel execPanel = new JPanel();
        execPanel.setLayout(new GridLayout(1, 2));

        JButton submitButton = new JButton(LocaleManager.getString("select_language_dialog.submit"));
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

        JButton cancelButton = new JButton(LocaleManager.getString("select_language_dialog.cancel"));
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

    /**
     * Возвращает выбранную пользователем локаль, или null, если не выбрал.
     */
    public UserLocale getChoosedUserLocale() {
        return choosedUserLocale;
    }
}
