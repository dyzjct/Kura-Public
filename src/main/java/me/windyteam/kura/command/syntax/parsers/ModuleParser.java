package me.windyteam.kura.command.syntax.parsers;

import me.windyteam.kura.command.syntax.SyntaxChunk;
import me.windyteam.kura.module.IModule;
import me.windyteam.kura.module.ModuleManager;
import me.windyteam.kura.module.IModule;
import me.windyteam.kura.module.ModuleManager;

public class ModuleParser
extends AbstractParser {
    @Override
    public String getChunk(SyntaxChunk[] chunks, SyntaxChunk thisChunk, String[] values2, String chunkValue) {
        if (chunkValue == null) {
            return this.getDefaultChunk(thisChunk);
        }
        IModule chosen = ModuleManager.getModules().stream().filter(module -> module.getName().toLowerCase().startsWith(chunkValue.toLowerCase())).findFirst().orElse(null);
        if (chosen == null) {
            return null;
        }
        return chosen.getName();
    }
}

