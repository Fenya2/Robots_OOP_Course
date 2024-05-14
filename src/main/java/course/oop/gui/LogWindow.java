package course.oop.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.TextArea;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import course.oop.locale.LocaleManager;
import course.oop.log.LogChangeListener;
import course.oop.log.LogEntry;
import course.oop.log.LogJournal;
import course.oop.log.Logger;
import course.oop.saving.Saveable;

public class LogWindow extends JInternalFrame implements Saveable, LogChangeListener {
    /**
     * Журнал логов (модель)
     */
    private LogJournal logSource;
    /**
     * Текст с записями логов
     */
    private TextArea logContent;

    /**
     * Шаблон строки вывода записи лога
     */
    private MessageFormat logEntryFormatter;

    /**
     * Шаблон вывода даты лога (ISO-8601)
     */
    private SimpleDateFormat logDateFormat;

    public LogWindow(LogJournal logSource) {
        super(LocaleManager.getInstance().getString("log_window.title"), true, true, true, true);
        setLocation(0, 0);
        setSize(500, 500);
        Logger.debug(LocaleManager.getInstance().getString("log.protocols_works"));
        this.logSource = logSource;
        logSource.registerListener(this);
        logContent = new TextArea("");
        logContent.setSize(200, 500);
        logEntryFormatter = new MessageFormat("{0}:{1}:{2}");
        logDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        updateLogContent();
    }

    /**
     * Дописывает в окно логов новую (последнюю) запись лога
     */
    private void updateLogContent() {
        StringBuilder content = new StringBuilder();
        for (LogEntry entry : logSource.all()) {
            String[] args = new String[] {
                    logDateFormat.format(entry.getDateCreated()),
                    entry.getLevel().toString(),
                    entry.getMessage()
            };
            content.append(logEntryFormatter.format(args)).append("\n");
        }
        logContent.setText(content.toString());
        logContent.invalidate();
    }

    /**
     * Возвращает свой уникальный идентификатор
     */
    @Override
    public String getFrameId() {
        return "log";
    }

    @Override
    public void onLogChanged() {
        EventQueue.invokeLater(this::updateLogContent);
    }
}
