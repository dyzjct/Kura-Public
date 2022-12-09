package me.dyzjct.kura.command.commands;

public abstract class AbstractCommand {
    private final String name;

    public AbstractCommand(String name) {
        this.name = name;
    }

    public abstract void execute(String[] var1);

    public String getName() {
        return this.name;
    }
}

