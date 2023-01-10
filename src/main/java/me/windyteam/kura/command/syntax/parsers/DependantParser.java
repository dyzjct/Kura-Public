package me.windyteam.kura.command.syntax.parsers;

import me.windyteam.kura.command.syntax.SyntaxChunk;

public class DependantParser
extends AbstractParser {
    int dependantIndex;
    private Dependency dependancy;

    public DependantParser(int dependantIndex, Dependency dependancy) {
        this.dependantIndex = dependantIndex;
        this.dependancy = dependancy;
    }

    @Override
    protected String getDefaultChunk(SyntaxChunk chunk) {
        return this.dependancy.getEscape();
    }

    @Override
    public String getChunk(SyntaxChunk[] chunks, SyntaxChunk thisChunk, String[] values2, String chunkValue) {
        if (chunkValue != null && !chunkValue.equals("")) {
            return "";
        }
        if (values2.length <= this.dependantIndex) {
            return this.getDefaultChunk(thisChunk);
        }
        if (values2[this.dependantIndex] == null || values2[this.dependantIndex].equals("")) {
            return "";
        }
        return this.dependancy.feed(values2[this.dependantIndex]);
    }

    public static class Dependency {
        String[][] map = new String[0][];
        String escape;

        public Dependency(String[][] map, String escape) {
            this.map = map;
            this.escape = escape;
        }

        private String[] containsKey(String[][] map, String key) {
            for (String[] s : map) {
                if (!s[0].equals(key)) continue;
                return s;
            }
            return null;
        }

        public String feed(String food) {
            String[] entry = this.containsKey(this.map, food);
            if (entry != null) {
                return entry[1];
            }
            return this.getEscape();
        }

        public String[][] getMap() {
            return this.map;
        }

        public String getEscape() {
            return this.escape;
        }
    }
}

