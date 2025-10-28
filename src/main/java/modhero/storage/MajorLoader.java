package modhero.storage;

import modhero.exceptions.CorruptedDataFileException;
import modhero.common.util.DeserialisationUtil;
import modhero.data.major.Major;
import modhero.data.major.MajorModule;
import modhero.data.modules.Module;
import modhero.data.modules.ModuleList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Loads major data from persistent storage into memory.
 */
public class MajorLoader extends Storage{
    private static final Logger logger = Logger.getLogger(MajorLoader.class.getName());
    private static final int EXPECTED_MAJOR_ARGS = 3;

    /**
     * Constructs a MajorLoader with the specified file path.
     * Calls the superclass constructor to initialize the file path used for loading module data.
     *
     * @param filePath the path to the module data file to be loaded
     */
    public MajorLoader(String filePath) {
        super(filePath);
    }

    /**
     * Loads all majors from storage and populates the provided map.
     *
     * @param allModulesData existing modules map for module lookup
     * @param allMajorsData map to populate; indexed by both abbreviation and name
     * @throws CorruptedDataFileException if critical data corruption detected
     */
    public void loadAllMajorsData(Map<String, Module> allModulesData, Map<String, Major> allMajorsData)
            throws CorruptedDataFileException {
        assert allModulesData != null : "loadAllMajorsData allModulesData must not be null";
        assert allMajorsData != null : "loadAllMajorsData allMajorsData must not be null";
        logger.log(Level.FINEST, "Loading all major data");


        List<String> rawMajorsList = load();

        for (String aa : rawMajorsList) {
            List<String> majorTop = DeserialisationUtil.deserialiseMessage(aa);
            String name       = majorTop.get(0);
            String abbrName  = majorTop.get(1);
            String modulesBlob = majorTop.get(2);

            List<String> moduleYTList = DeserialisationUtil.deserialiseMessage(modulesBlob);

            List<MajorModule> majorModules = new ArrayList<>();
            for (String moduleYT : moduleYTList) {
                List<String> triplet = DeserialisationUtil.deserialiseMessage(moduleYT);
                String code = triplet.get(0);
                int year    = Integer.parseInt(triplet.get(1));
                int sem     = Integer.parseInt(triplet.get(2));
                MajorModule mod = new MajorModule(code.toUpperCase(), year, sem);
                //TODO: add code here when allModulesData is implemented
                majorModules.add(mod);
            }

            Major major = new Major(name, abbrName.toUpperCase(), majorModules);
            allMajorsData.put(abbrName.toLowerCase(), major);

        }
    }


    /**
     * Return ModuleList for major object.
     *
     * @param allModulesData hashmap to get modules object from
     * @param moduleCodes modules code in a list of string
     * @return ModuleList
     */
    private ModuleList createModuleList(Map<String, Module> allModulesData, List<String> moduleCodes) {
        ModuleList moduleList = new ModuleList();
        for (String code : moduleCodes) {
            Module module = allModulesData.get(code);
            if (module != null) {
                moduleList.add(module);
            } else {
                logger.log(Level.WARNING, "Missing module for major: " + code);
            }
        }
        return moduleList;
    }
}
