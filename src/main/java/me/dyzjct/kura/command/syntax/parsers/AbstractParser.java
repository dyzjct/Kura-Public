package me.dyzjct.kura.command.syntax.parsers;

import me.dyzjct.kura.command.syntax.SyntaxChunk;
import me.dyzjct.kura.command.syntax.SyntaxParser;

public abstract class AbstractParser
implements SyntaxParser {
    @Override
    public abstract String getChunk(SyntaxChunk[] var1, SyntaxChunk var2, String[] var3, String var4);

    protected String getDefaultChunk(SyntaxChunk chunk) {
        return (chunk.isHeadless() ? "" : chunk.getHead()) + (chunk.isNecessary() ? "<" : "[") + chunk.getType() + (chunk.isNecessary() ? ">" : "]");
    }
}

