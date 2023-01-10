package me.windyteam.kura.command;

import me.windyteam.kura.Kura;
import me.windyteam.kura.command.syntax.SyntaxChunk;
import net.minecraft.client.Minecraft;

import java.util.Arrays;
import java.util.List;

public abstract class Command {
    protected String label;
    protected String syntax;
    protected String description;
    protected List<String> aliases;
    public final Minecraft mc = Minecraft.getMinecraft();
    protected SyntaxChunk[] syntaxChunks;
    public static final char SECTION_SIGN = '\u00a7';

    public Command(String label, SyntaxChunk[] syntaxChunks, String... aliases) {
        this.label = label;
        this.syntaxChunks = syntaxChunks;
        this.description = "Description Less";
        this.aliases = Arrays.asList(aliases);
    }

    public Command(String label) {
        this(label, SyntaxChunk.EMPTY);
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public static String getCommandPrefix() {
        return Kura.commandPrefix.getValue();
    }

    public String getLabel() {
        return this.label;
    }

    public abstract void call(String[] var1);

    public SyntaxChunk[] getSyntaxChunks() {
        return this.syntaxChunks;
    }

    protected SyntaxChunk getSyntaxChunk(String name) {
        for (SyntaxChunk c : this.syntaxChunks) {
            if (!c.getType().equals(name)) continue;
            return c;
        }
        return null;
    }


    public List<String> getAliases() {
        return this.aliases;
    }

    public static char SECTIONSIGN() {
        return '\u00a7';
    }
}

