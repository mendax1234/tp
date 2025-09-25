package modhero.storage;

import modhero.data.modules.Module;
import modhero.data.modules.ModuleList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Loading from and saving to a text file.
 */
public class Storage {

    private ModuleList allModules;

    private final String filePath;

    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Ensures that the directory for the file path exists.
     * Creates directories if not present.
     */
    public void ensureFileDirectoryExist() {
        new File(filePath).getParentFile().mkdirs();
    }

    /**
     * Loads the file contents into a list of strings, each line a list element.
     *
     * @return list of lines from file, or null if file not found
     */
    public List<String> load() {
        try {
            ensureFileDirectoryExist();
            return readFromFile();
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    /**
     * Reads all lines from the file at filePath.
     *
     * @return list of strings read line-by-line from the file
     * @throws FileNotFoundException when the file does not exist
     */
    private List<String> readFromFile() throws FileNotFoundException {
        File file = new File(filePath);
        Scanner s = new Scanner(file);
        List<String> rawTaskList = new ArrayList<>();
        while (s.hasNext()) {
            rawTaskList.add(s.nextLine());
        }
        return rawTaskList;
    }

    /**
     * Saves the given text string to the file, overwriting any existing content.
     *
     * @param textToAdd the text content to save
     */
    public void save(String textToAdd) {
        try {
            ensureFileDirectoryExist();
            writeToFile(textToAdd);
        } catch (IOException e) {
            System.out.println("Failed save file");
        }
    }

    /**
     * Writes the provided text content to the file at filePath.
     *
     * @param textToAdd text to write to the file
     * @throws IOException if an I/O error occurs during writing
     */
    private void writeToFile(String textToAdd) throws IOException {
        FileWriter fileWriter = new FileWriter(filePath);
        fileWriter.write(textToAdd);
        fileWriter.close();
    }

    /**
     * Loads all modules from a data file, assuming that the file at filePath contains list of all NUS modules.
     *
     */
    public void loadAllModules() {
        List<String> allModulesList = load();
        for (String code : allModulesList) {
            String electiveName = "placeholder";
            int numberOfMC = 0;
            String type = "placeholder";
            List<String> prerequisites = new ArrayList<>();
            modhero.data.modules.Module module = new Module(code, electiveName, numberOfMC, type, prerequisites);
            allModules.add(module);
        }
    }

    /**
     * Returns the required module.
     *
     * @return a Module with the given module code
     */
    public Module findModuleByCode(String code) {
        for (Module m : allModules.getModuleList()) {
            if (m.getCode().equals(code)) {
                return m;
            }
        }
        return null;
    }

}
