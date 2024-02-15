package com.kliminskyi.ffregions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class CommandFFRg implements TabCompleter, CommandExecutor {
    public CommandFFRg() {
        commands.put("claim", new CommandClaim());
        commands.put("show", new CommandShow());
        commands.put("create", new CommandCreate());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length <= 1) {
            String arg = args.length == 0 ? "" : args[0];
            return commands.keySet().stream().filter(s -> s.startsWith(arg)).toList();
        }

        Optional<Player> player = sender instanceof Player ? Optional.of((Player)sender) : Optional.empty();
        Optional<String> baseCommandOptional = completeBaseCommand(args[0]);
        if (baseCommandOptional.isEmpty()) {
            return List.of();
        }
        String baseCommand = baseCommandOptional.get();

        String[] argsForBase = Arrays.copyOfRange(args, 1, args.length);
        if (!commands.containsKey(baseCommand)) {
            return List.of();
        }

        return commands.get(baseCommand).getCompletes(player, argsForBase);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.getServer().getLogger().warning("Only players can send the ffrg command.");
            return false;
        }

        Player player = (Player)sender;

        if (args.length == 0) {
            player.sendMessage(
                String.format("%sPlease specify a subcommand, for example \"create\".%s", ChatColor.RED, ChatColor.RESET)
            );
            return false;
        }

        Optional<String> baseCommandOptional = completeBaseCommand(args[0]);
        if (baseCommandOptional.isEmpty() || !commands.containsKey(baseCommandOptional.get())) {
            player.sendMessage(
                String.format("%sUnknown subcommand \"%s\".%s", ChatColor.RED, args[0], ChatColor.RESET)
            );
            return false;
        }

        String baseCommand = baseCommandOptional.get();
        String[] argsForBase = Arrays.copyOfRange(args, 1, args.length);

        return commands.get(baseCommand).onCommand(player, command, label, argsForBase);
    }

    public FFRegionsCommand getCommand(String name) {
        return commands.get(name);
    }

    private Optional<String> completeBaseCommand(String unfinishedBaseArg) {
        List<String> list = commands.keySet().stream().filter(s -> s.startsWith(unfinishedBaseArg)).toList();
        if (list.size() == 1) {
            return Optional.of(list.get(0));
        }
        else {
            return Optional.empty();
        }
    }

    private HashMap<String, FFRegionsCommand> commands = new HashMap<String, FFRegionsCommand>();
}
