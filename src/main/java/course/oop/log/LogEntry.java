package course.oop.log;

import java.util.Date;

/**
 * Класс, представляющий запись в логе
 */
public class LogEntry
{
    private LogLevel m_logLevel;
    private String m_strMessage;
    private Date dateCreated;
    
    public LogEntry(LogLevel logLevel, String strMessage)
    {
        m_strMessage = strMessage;
        m_logLevel = logLevel;
        dateCreated = new Date();
    }
    
    public String getMessage()
    {
        return m_strMessage;
    }
    
    public LogLevel getLevel()
    {
        return m_logLevel;
    }

    public Date getDateCreated() {
        return dateCreated;
    }
}

