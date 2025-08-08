package net.villagerzock.commands;

import net.villagerzock.Server;

public class ExitCommand implements Command<ExitCommand.NoArguments>{
    @Override
    public boolean onExecute(NoArguments options) {
        Server.getInstance().shutdown();
        return false;
    }

    @Override
    public NoArguments getOptions() {
        return new NoArguments();
    }
    public static class NoArguments{

    }
}
