package modhero.data.major;

/**
 * Represents a module that is required for a specific major.
 * Each {@code MajorModule} includes the module code,
 * the year, and the term in which it should be taken.
 */
public class MajorModule {
    private final String code;
    private final int year;
    private final int term;

    public MajorModule(String code, int year, int term) {
        this.code = code;
        this.year = year;
        this.term = term;
    }

    public String getCode() {
        return code;
    }

    public int getYear() {
        return year;
    }
    public int getTerm() {
        return term;
    }
}