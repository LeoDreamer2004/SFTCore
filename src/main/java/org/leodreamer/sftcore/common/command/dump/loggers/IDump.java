package org.leodreamer.sftcore.common.command.dump.loggers;

import java.util.List;
import java.util.Map;

public interface IDump {

    String getTypeName();

    Map<String, List<String>> getIdentifierMap();

    Map<String, List<String>> getTagMap();
}
