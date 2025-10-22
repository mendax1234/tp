package modhero.parser;

import modhero.data.modules.Module;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModuleParserTest {
    private final ModuleParser parser = new ModuleParser();

    @Test
    void parseModuleReturnsNullForNullJson() {
        assertNull(parser.parseModule(null));
    }

    @Test
    void parseModuleReturnsNullWhenCodeMissing() {
        String json = "{\"title\":\"Programming\",\"moduleCredit\":\"4\"}";
        assertNull(parser.parseModule(json));
    }

    @Test
    void parseModuleReturnsNullWhenTitleMissing() {
        String json = "{\"moduleCode\":\"CS2113\",\"moduleCredit\":\"4\"}";
        assertNull(parser.parseModule(json));
    }

    @Test
    void parseModuleReturnsNullWhenMcMissing() {
        String json = "{\"moduleCode\":\"CS2113\",\"title\":\"Programming\"}";
        assertNull(parser.parseModule(json));
    }

    @Test
    void parseModuleReturnsNullForInvalidMc() {
        String json = "{\"moduleCode\":\"CS2113\",\"title\":\"Programming\",\"moduleCredit\":\"abc\"}";
        assertNull(parser.parseModule(json));
    }

    @Test
    void parseModuleReturnsNullForNegativeMc() {
        String json = "{\"moduleCode\":\"CS2113\",\"title\":\"Programming\",\"moduleCredit\":\"-4\"}";
        assertNull(parser.parseModule(json));
    }

    @Test
    void parseModuleReturnsNullForLargeMc() {
        String json = "{\"moduleCode\":\"CS2113\",\"title\":\"Programming\",\"moduleCredit\":\"25\"}";
        assertNull(parser.parseModule(json));
    }

    @Test
    void parseModuleCreatesValidModuleWithoutPrereq() {
        String moduleCode = "CS2113";
        String title = "Software Engineering & Object-Oriented Programming";
        int moduleCredit = 4;
        String json = String.format("{\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\"}"
                , parser.CODE, moduleCode, parser.NAME, title, parser.MC, moduleCredit);
        Module module = parser.parseModule(json);
        assertEquals(moduleCode, module.getCode());
        assertEquals(title, module.getName());
        assertEquals(moduleCredit, module.getMc());
        assertNotNull(module.getPrerequisites());
    }

    @Test
    void parseModuleHandlesSimplePrereq() {
        String moduleCode = "CS2040C";
        String title = "Data Structures and Algorithms";
        int moduleCredit = 4;
        String prereq = "\"or\":[\"CS1010:D\",\"CS1010E:D\",\"CS1010X:D\",\"CS1101S:D\",\"CS1010S:D\",\"CS1010J:D\",\"CS1010A:D\",\"UTC2851:D\"]";
        String json = String.format("{\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":{%s}}"
                , parser.CODE, moduleCode, parser.NAME, title, parser.MC, moduleCredit, parser.PREREQ, prereq);
        Module module = parser.parseModule(json);
        assertEquals(moduleCode, module.getCode());
        assertEquals(title, module.getName());
        assertEquals(moduleCredit, module.getMc());
        List<List<String>> expectedPrereq = List.of(
                List.of("CS1010"),
                List.of("CS1010E"),
                List.of("CS1010X"),
                List.of("CS1101S"),
                List.of("CS1010S"),
                List.of("CS1010J"),
                List.of("CS1010A"),
                List.of("UTC2851")
        );
        assertEquals(expectedPrereq, module.getPrerequisites().getPrereq());
    }

    @Test
    void parseModuleHandlesComplexPrereq() {
        String moduleCode = "CS2113";
        String title = "Software Engineering & Object-Oriented Programming";
        int moduleCredit = 4;
        String prereq = "\"or\":[\"CS2040C:D\",{\"and\":[{\"or\":[\"CS2030:D\",\"CS2030S:D\",\"CS2030DE:D\"]},{\"or\":[\"CS2040S:D\",\"CS2040:D\",\"CS2040DE:D\"]}]}]}";
        String json = String.format("{\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":{%s}}"
                , parser.CODE, moduleCode, parser.NAME, title, parser.MC, moduleCredit, parser.PREREQ, prereq);
        Module module = parser.parseModule(json);
        assertEquals(moduleCode, module.getCode());
        assertEquals(title, module.getName());
        assertEquals(moduleCredit, module.getMc());
        List<List<String>> expectedPrereq = List.of(
                List.of("CS2040C"),
                List.of("CS2030","CS2040S"),
                List.of("CS2030","CS2040"),
                List.of("CS2030","CS2040DE"),
                List.of("CS2030S","CS2040S"),
                List.of("CS2030S","CS2040"),
                List.of("CS2030S","CS2040DE"),
                List.of("CS2030DE","CS2040S"),
                List.of("CS2030DE","CS2040"),
                List.of("CS2030DE","CS2040DE")
        );
        assertEquals(expectedPrereq, module.getPrerequisites().getPrereq());
    }




}