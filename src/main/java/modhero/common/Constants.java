package modhero.common;

public final class Constants {
    public static final class UiConstants {
        public static final String WELCOME = """
                ┌───────────────────────────────────────────────────────────────────────────────┐
                │                               Welcome to ModHero                              │
                ├───────────────────────────────────────────────────────────────────────────────┤
                │ Hello there!                                                                  │
                │                                                                               │
                │ ModHero helps you plan your degree efficiently through fast, structured       │
                │ commands and a clear, consolidated overview.                                  │
                │                                                                               │
                │ Why ModHero?                                                                  │
                │ • Because planning your modules should feel logical, not messy.               │
                │ • Because good decisions come from seeing the whole picture clearly.          │
                │                                                                               │
                │ Let's get you started:                                                        │
                │  1. Specify your major so ModHero can load core modules                       │
                │     (psst! Please only enter Computer Science or Computer Engineering)        │
                │     Example: major Computer Engineering                                       │
                │                                                                               │
                │  2. See your recommended schedule!                                            │
                │     Example: schedule                                                         │
                │                                                                               │
                │  3. Add electives you wish to include to the specific Year & Semester         │
                │     Example: add CS3240 to Y3S2                                               │
                │                                                                               │
                │ Tip: Type 'help' anytime to see all available commands.                       │
                │                                                                               │
                │ Ready? Let's begin!!                                                          │
                └───────────────────────────────────────────────────────────────────────────────┘
                """;
    }

    public static final class MessageConstants {
        public static final String INVALID_COMMAND_FORMAT = "Invalid command format! \n%1$s";
        public static final String ARRAY_INDEX_OUT_BOUND = "(Your index is out of bound)";
    }

    public static final class AcademicConstants {
        public static final int NUM_YEARS = 4;
        public static final int NUM_TERMS = 2;
        public static final String ACAD_YEAR = "2025-2026";
        public static final int MAX_MODULES_PER_SEM = 5;
    }

    public static final class FilePathConstants {
        public static final String MODULES_FILE_PATH = "src/main/resources/modules.txt";
        public static final String MAJOR_FILE_PATH = "src/main/resources/major.txt";
    }

    public static final class FormatConstants {
        public static final String START_DELIMITER = "#";
        public static final String END_DELIMITER = "|";
    }
}
