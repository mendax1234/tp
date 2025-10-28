package modhero.exceptions;

import modhero.common.Constants.AcademicConstants;

public class InvalidYearOrSemException extends  ModHeroException {
    public InvalidYearOrSemException(int year, int semester) {
        super(String.format("Invalid academic period: Y%dS%d. Valid range is Y1–Y%d, S1–S%d.",
                year, semester,
                AcademicConstants.NUM_YEARS,
                AcademicConstants.NUM_TERMS));
    }
}
