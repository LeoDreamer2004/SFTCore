package org.leodreamer.sftcore.common.item.terminal.builder;

import org.leodreamer.sftcore.common.item.terminal.api.*;

import net.minecraft.nbt.CompoundTag;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface IPatternShapedBuilder extends IMekMultiblockBuilder {

    Pattern pattern(CompoundTag terminalTag);

    @Override
    default BuildDimensions dimensions(CompoundTag terminalTag) {
        return pattern(terminalTag).dimensions();
    }

    @Override
    default Iterable<RelativeBuildPos> positions(
        CompoundTag terminalTag,
        BuildDimensions dimensions
    ) {
        return pattern(terminalTag).positions();
    }

    @Override
    default PlacementCandidate candidateFor(
        CompoundTag terminalTag,
        BuildDimensions dimensions,
        RelativeBuildPos pos,
        InventorySnapshot inventory
    ) {
        return pattern(terminalTag).candidateFor(inventory, pos);
    }

    @FunctionalInterface
    interface CandidateSupplier {

        PlacementCandidate create(InventorySnapshot inventory, RelativeBuildPos pos);
    }

    /**
     * Pattern definition for the multiblock, just like
     * {@link com.gregtechceu.gtceu.api.multiblock.pattern.BlockPattern}
     */
    @Accessors(fluent = true)
    final class Pattern {

        public static final char SKIP = ' ';

        private final List<List<String>> layers;
        private final Map<Character, CandidateSupplier> candidates;
        @Getter
        private final BuildDimensions dimensions;
        @Getter
        private final List<RelativeBuildPos> positions;

        private Pattern(
            List<List<String>> layers,
            Map<Character, CandidateSupplier> candidates,
            BuildDimensions dimensions,
            List<RelativeBuildPos> positions
        ) {
            this.layers = layers;
            this.candidates = candidates;
            this.dimensions = dimensions;
            this.positions = positions;
        }

        public static Builder start() {
            return new Builder();
        }

        public char symbolAt(RelativeBuildPos pos) {
            if (
                pos.x() < 0 || pos.x() >= dimensions.width() ||
                    pos.y() < 0 || pos.y() >= dimensions.height() ||
                    pos.z() < 0 || pos.z() >= dimensions.depth()
            ) {
                return SKIP;
            }

            return layers.get(pos.y()).get(pos.z()).charAt(pos.x());
        }

        public PlacementCandidate candidateFor(InventorySnapshot inventory, RelativeBuildPos pos) {
            char symbol = symbolAt(pos);

            if (symbol == SKIP) {
                return PlacementCandidate.air();
            }

            var candidate = candidates.get(symbol);
            if (candidate == null) {
                throw new IllegalStateException(
                    "No placement candidate registered for symbol '" + symbol + "'"
                );
            }

            return candidate.create(inventory, pos);
        }

        public static final class Builder {

            private final List<String[]> layers = new ArrayList<>();
            private final Map<Character, CandidateSupplier> candidates = new LinkedHashMap<>();

            private Builder() {}

            /**
             * The coordinate convention is as follows:
             * - The order of aisle calls: Y, from bottom to top;
             * - The order of strings within an aisle: Z, from north to south;
             * - The order of characters within each string: X, from west to east.
             */
            public Builder layer(String... rows) {
                if (rows.length == 0) {
                    throw new IllegalArgumentException(
                        "Pattern aisle must have at least one row"
                    );
                }

                layers.add(rows);
                return this;
            }

            public Builder where(char symbol, PlacementCandidate candidate) {
                return where(symbol, (inventory, pos) -> candidate);
            }

            public Builder where(char symbol, CandidateSupplier candidate) {
                if (symbol == SKIP) {
                    throw new IllegalArgumentException(
                        "Space is reserved as the implicit skip symbol"
                    );
                }

                if (candidates.putIfAbsent(symbol, candidate) != null) {
                    throw new IllegalArgumentException(
                        "Duplicate placement candidate for symbol '" + symbol + "'"
                    );
                }

                return this;
            }

            public Pattern build() {
                if (layers.isEmpty()) {
                    throw new IllegalStateException(
                        "Pattern must have at least one aisle"
                    );
                }

                int height = layers.size();
                int depth = 0;
                int width = 0;

                for (var aisle : layers) {
                    depth = Math.max(depth, aisle.length);

                    for (var row : aisle) {
                        width = Math.max(width, row.length());
                    }
                }

                var normalizedLayers = new ArrayList<List<String>>(height);
                var positions = new ArrayList<RelativeBuildPos>();

                for (int y = 0; y < height; y++) {
                    var aisle = layers.get(y);
                    var rows = new ArrayList<String>(depth);

                    for (int z = 0; z < depth; z++) {
                        String row = z < aisle.length ? aisle[z] : "";
                        String normalized = padRight(row, width);

                        rows.add(normalized);

                        for (int x = 0; x < width; x++) {
                            char symbol = normalized.charAt(x);

                            if (symbol == SKIP) {
                                continue;
                            }

                            if (!candidates.containsKey(symbol)) {
                                throw new IllegalStateException(
                                    "No placement candidate registered for symbol '" +
                                        symbol + "' at (" + x + ", " + y + ", " + z + ")"
                                );
                            }

                            positions.add(new RelativeBuildPos(x, y, z));
                        }
                    }

                    normalizedLayers.add(List.copyOf(rows));
                }

                return new Pattern(
                    normalizedLayers,
                    candidates,
                    new BuildDimensions(width, height, depth),
                    positions
                );
            }

            private static String padRight(String value, int width) {
                if (value.length() >= width) {
                    return value;
                }

                return value + " ".repeat(width - value.length());
            }
        }
    }
}
