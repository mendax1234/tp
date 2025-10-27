package modhero.common.config;

import modhero.ModHero;
import modhero.commands.*;
import modhero.data.major.Major;
import modhero.data.modules.Module;
import modhero.data.modules.ModuleList;
import modhero.parser.Parser;
import modhero.data.timetable.Timetable;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Centralized logger configuration for the application.
 */
public class LoggerConfig {

    /**
     * Configures logging level for all application loggers.
     *
     * @param level The logging level to set.
     */
    public static void configureLoggers(Level level) {
        setLoggerLevel(ModHero.class, level);
        setLoggerLevel(Parser.class, level);
        setLoggerLevel(Command.class, level);
        setLoggerLevel(MajorCommand.class, level);
        setLoggerLevel(DeleteCommand.class, level);
        setLoggerLevel(ScheduleCommand.class, level);
        setLoggerLevel(ClearCommand.class, level);
        setLoggerLevel(HelpCommand.class, level);
        setLoggerLevel(ExitCommand.class, level);
        setLoggerLevel(Major.class, level);
        setLoggerLevel(Module.class, level);
        setLoggerLevel(ModuleList.class, level);
        setLoggerLevel(Timetable.class, level);
    }

    private static void setLoggerLevel(Class<?> clazz, Level level) {
        Logger.getLogger(clazz.getName()).setLevel(level);
    }
}
