package org.leodreamer.sftcore.api.pattern;

import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MultiBlockFileReader {

    private MultiBlockFileReader() {}

    public static FactoryBlockPattern start(MultiblockMachineDefinition definition) {
        return start(
            RelativeDirection.LEFT,
            RelativeDirection.UP,
            RelativeDirection.FRONT,
            definition.getName()
        );
    }

    public static FactoryBlockPattern start(String name) {
        return start(RelativeDirection.LEFT, RelativeDirection.UP, RelativeDirection.FRONT, name);
    }

    public static FactoryBlockPattern start(
        MultiblockMachineDefinition definition,
        RelativeDirection charDir,
        RelativeDirection stringDir,
        RelativeDirection aisleDir
    ) {
        return start(charDir, stringDir, aisleDir, definition.getName());
    }

    static Pattern regex = Pattern.compile("\"([^\"]*)\"");

    private static FactoryBlockPattern start(
        RelativeDirection charDir,
        RelativeDirection stringDir,
        RelativeDirection aisleDir,
        String name
    ) {
        var pattern = FactoryBlockPattern.start(charDir, stringDir, aisleDir);
        var stream = MultiBlockFileReader.class.getClassLoader().getResourceAsStream("pattern/" + name + ".mbp");
        if (stream == null) throw new RuntimeException("pattern not found: " + name);
        // read lines from stream
        try (stream) {
            var reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) continue;
                line = line.trim();

                List<String> aisle = new ArrayList<>();
                Matcher matcher = regex.matcher(line);
                while (matcher.find()) {
                    String extracted = matcher.group(1);
                    aisle.add(extracted);
                }
                pattern.aisle(aisle.toArray(new String[0]));
            }
        } catch (Exception e) {
            throw new RuntimeException("failed to read pattern: " + name, e);
        }
        return pattern;
    }
}
