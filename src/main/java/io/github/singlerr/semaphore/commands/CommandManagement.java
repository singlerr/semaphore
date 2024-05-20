/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.commands;

import io.github.singlerr.semaphore.network.packets.AddPlayerStatePacket;
import io.github.singlerr.semaphore.network.packets.RemovePlayerStatePacket;
import io.github.singlerr.semaphore.registries.CommonRegistries;
import io.github.singlerr.semaphore.registries.ServerRegistries;
import io.github.singlerr.semaphore.state.State;
import io.github.singlerr.semaphore.state.player.PlayerContext;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

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
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args[0].equalsIgnoreCase("list")) {
            for (Map.Entry<UUID, State> entry : ServerRegistries.getStatePool().getStates()) {
                if (!(entry.getValue() instanceof PlayerContext)) continue;
                PlayerContext ctx = (PlayerContext) entry.getValue();
                info(
                        sender,
                        "Player: " + ctx.getName() + ", Opponent: " + ctx.getOpponent() + ", State: "
                                + ctx.getCallState());
            }
            return;
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (args.length < 2) {
                error(sender, "Specify player name");
                return;
            }

            String playerName = args[1];
            UUID id = null;
            // Fetch player uuid
            if (args.length < 3) {
                EntityPlayerMP target = server.getPlayerList().getPlayerByUsername(playerName);
                if (target == null) {
                    error(sender, "Expected " + playerName + " to be online but not found");
                    return;
                }

                id = target.getUniqueID();
            }

            if (args.length == 3) {
                id = parseUUID(args[2]);
                if (id == null) {
                    error(sender, "Invalid UUID");
                    return;
                }
            }

            if (id == null) {
                error(sender, "Invalid UUID");
                return;
            }

            PlayerContext context =
                    PlayerContext.builder().owner(id).name(playerName).build();

            ServerRegistries.getStatePool().submit(id, context);
            CommonRegistries.NETWORK.sendToAll(
                    AddPlayerStatePacket.builder().playerState(context).build());

            info(sender, "Added or updated player : " + playerName);
            return;
        }

        if (args[0].equalsIgnoreCase("remove")) {
            if (args.length < 2) {
                error(sender, "/vcm remove (<uuid>|<name>)");
            }
            String id = args[1];
            UUID uuid;
            if ((uuid = parseUUID(id)) == null) {
                EntityPlayerMP target = server.getPlayerList().getPlayerByUsername(id);
                if (target != null) {
                    uuid = target.getUniqueID();
                }
            }

            if (uuid == null) {
                error(sender, "Expected UUID or playerName but neither not");
                return;
            }

            PlayerContext context = ServerRegistries.getStatePool()
                    .get(uuid, PlayerContext.class)
                    .orElse(null);
            if (context == null) {
                error(sender, "Player not found");
                return;
            }
            ServerRegistries.getStatePool().remove(uuid);
            CommonRegistries.NETWORK.sendToAll(
                    RemovePlayerStatePacket.builder().playerState(context).build());
            info(sender, "Removed player : " + id);
            return;
        }
        if (args[0].equalsIgnoreCase("set")) {
            if (args.length < 2) {
                error(sender, "/vcm set (<uuid>|<name>) <state>");
            }
            String id = args[1];
            UUID uuid;
            if ((uuid = parseUUID(id)) == null) {
                EntityPlayerMP target = server.getPlayerList().getPlayerByUsername(id);
                if (target != null) {
                    uuid = target.getUniqueID();
                }
            }

            if (uuid == null) {
                error(sender, "Expected UUID or playerName but neither not");
                return;
            }

            String state = args[2];
            PlayerContext.CallState newState;

            try {
                newState = PlayerContext.CallState.valueOf(state.toUpperCase());
            } catch (IllegalArgumentException e) {
                error(sender, "Invalid call state, available: " + Arrays.toString(PlayerContext.CallState.values()));
                return;
            }

            Optional<PlayerContext> context = ServerRegistries.getStatePool().get(uuid, PlayerContext.class);
            context.ifPresent(c -> {
                c.setCallState(newState);
                info(sender, "Set player : " + id + " to: " + newState);
            });
        }
    }

    private UUID parseUUID(String input) {
        try {
            return UUID.fromString(input);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private void info(ICommandSender sender, String message) {
        sender.sendMessage(new TextComponentString(message).setStyle(new Style().setColor(TextFormatting.GREEN)));
    }

    private void error(ICommandSender sender, String message) {
        sender.sendMessage(new TextComponentString(message).setStyle(new Style().setColor(TextFormatting.RED)));
    }
}
