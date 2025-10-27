package modhero.data.modules;

import modhero.common.util.SerialisationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Prerequisites {
    public static final Logger logger = Logger.getLogger(Prerequisites.class.getName());

    private List<List<String>> prereq;

    public Prerequisites() {
        this.prereq = new ArrayList<>();
    }

    public Prerequisites(List<List<String>> prereq) {
        this.prereq = prereq;
    }

    public List<List<String>> getPrereq() {
        return prereq;
    }
    /**
     * Serializes prerequisites into a doubly-serialized string format.
     *
     * Structure:
     * 1. Each module code in a combination is serialized
     * 2. Each combination is serialized together
     *
     * This produces a doubly-serialized blob that will be wrapped one more time
     * by the caller (DataGenerator), resulting in triple serialization total.
     *
     * Example for [[CS1010, CS1101S], [CS1010E]]:
     * - Serialize CS1010 → wrap1
     * - Serialize CS1101S → wrap2
     * - Serialize (wrap1 + wrap2) → combination1
     * - Serialize CS1010E → wrap3
     * - Serialize (wrap3) → combination2
     * - Return combination1 + combination2 (doubly-serialized blob)
     *
     * @return doubly-serialized prerequisites string
     */
    public String toFormatedString() {
        logger.log(Level.FINEST, "Serialising prerequisites");

        if (prereq == null || prereq.isEmpty()) {
            logger.log(Level.FINEST, "No prerequisites to serialize");
            return ""; // Empty string for no prerequisites
        }

        SerialisationUtil serialisationUtil = new SerialisationUtil();
        StringBuilder outerBuilder = new StringBuilder();

        // For each combination (OR group of prerequisites)
        for (List<String> combination : prereq) {
            // Serialize each module code in the combination
            StringBuilder innerBuilder = new StringBuilder();
            for (String moduleCode : combination) {
                innerBuilder.append(serialisationUtil.serialiseMessage(moduleCode));
            }

            // Serialize the entire combination
            outerBuilder.append(serialisationUtil.serialiseMessage(innerBuilder.toString()));
        }

        String serialisedPrereqs = outerBuilder.toString();
        logger.log(Level.FINEST, "Successfully serialized prerequisites (doubly-serialized)");
        return serialisedPrereqs;
    }

    @Override
    public String toString() {
        if (prereq == null || prereq.isEmpty()) {
            return "No prerequisites";
        }

        StringBuilder sb = new StringBuilder("Prerequisites: [");
        for (int i = 0; i < prereq.size(); i++) {
            List<String> group = prereq.get(i);
            sb.append(group);
            if (i < prereq.size() - 1) {
                sb.append(" OR ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
