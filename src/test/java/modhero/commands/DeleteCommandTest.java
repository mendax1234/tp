//package modhero.commands;
//
//import modhero.data.modules.Module;
//import modhero.data.timetable.Timetable;
//
//import org.junit.jupiter.api.BeforeEach;
//
//import java.util.List;
//
//public class DeleteCommandTest {
//    private Timetable timetable;
//    private Module cs1010 = new modhero.data.modules.Module("CS1010", "Programming Methodology", 4, "core", List.of());
//    private Module cs2040 = new modhero.data.modules.Module("CS2040", "Data Structures", 4, "core", List.of("CS1010"));
//    private Module cs2100 = new Module("CS2100", "Computer Organisation", 4, "core", List.of("CS1010"));
//
//    @BeforeEach
//     void setUpAll(){
//        timetable = new Timetable();
//
//        timetable.addModuleDirect(1,1, cs1010);
//        timetable.addModuleDirect(1,2, cs2040);
//        timetable.addModuleDirect(2,1, cs2100);
//    }
//}
