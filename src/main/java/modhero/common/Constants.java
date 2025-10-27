package modhero.common;

public final class Constants {

    public static final class MessageConstants {
        public static final String INVALID_COMMAND_FORMAT = "Invalid command format! \n%1$s";
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
