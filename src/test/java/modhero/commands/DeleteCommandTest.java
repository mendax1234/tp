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

    @Test
    @DisplayName("Delete without violating prerequisites")
    void sucessfulDeleteCase(){
        List<String> toDelete = List.of("Cs2040");

        DeleteCommand DUT = new DeleteCommand(toDelete);
        DUT.setData(timetable);
        DUT.execute();
        assertFalse(timetable.getAllModules().contains(cs2040));
    }

    @Test
    @DisplayName("Violate prerequisite")
    void UnsucessfulDeleteCase(){
        List<String> toDelete = List.of("CS1010");

        DeleteCommand DUT = new DeleteCommand(toDelete);
        DUT.setData(timetable);
        DUT.execute();
        assertTrue(timetable.getAllModules().contains(cs1010));
    }

    @DisplayName("use lower case letters")
    void lowerCaseMajor(){
        List<String> toDelete = List.of("cs2040");

        DeleteCommand DUT = new DeleteCommand(toDelete);
        DUT.setData(timetable);
        DUT.execute();
        assertFalse(timetable.getAllModules().contains(cs2040));
    }

    @DisplayName("Delete major not in table")
    void fakeMajor(){
        List<String> toDelete = List.of("cs2004");

        DeleteCommand DUT = new DeleteCommand(toDelete);
        DUT.setData(timetable);
        DUT.execute();
        assertTrue(timetable.getAllModules().contains(cs2040));
        assertTrue(timetable.getAllModules().contains(cs1010));
        assertTrue(timetable.getAllModules().contains(cs2100));
    }


}
