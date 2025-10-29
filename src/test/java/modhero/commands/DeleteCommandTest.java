package modhero.commands;

import static org.junit.jupiter.api.Assertions.*;
import modhero.data.modules.Module;
import modhero.data.timetable.Timetable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

public class DeleteCommandTest {
    private Timetable timetable;
    private Module cs1010 = new modhero.data.modules.Module("CS1010", "Programming Methodology", 4, "core", List.of());
    private Module cs2040 = new modhero.data.modules.Module("CS2040", "Data Structures", 4, "core", List.of("CS1010"));
    private Module cs2100 = new Module("CS2100", "Computer Organisation", 4, "core", List.of("CS1010"));

    @BeforeEach
     void setUpAll(){
        timetable = new Timetable();

        timetable.addModuleInternal(1,1, cs1010);
        timetable.addModuleInternal(1,2, cs2040);
        timetable.addModuleInternal(2,1, cs2100);
    }
}
