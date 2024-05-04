/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandManagement extends CommandBase {
    @Override
    public String getName() {
        return "vcm";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/vcm";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {}
}
