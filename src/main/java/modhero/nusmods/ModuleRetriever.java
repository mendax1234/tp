package modhero.nusmods;

import modhero.parser.ModuleParser;
import modhero.modules.Module;

/**
 * Retrieve NUS module information parse into module.
 */
public class ModuleRetriever {
    private final NusmodsAPIClient client = new NusmodsAPIClient();
    private final ModuleParser parser = new ModuleParser();

    public Module getModule(String acadYear, String code) {
        String json = client.fetchModuleDataSafely(acadYear, code);
        if (json == null) {
            return null;
        }
        return parser.parseModule(json);
    }
}
