package net.atrophygames.obscrts.events;

import net.atrophygames.obscrts.TTT;
import net.atrophygames.obscrts.countdown.LobbyCountdown;
import net.atrophygames.obscrts.gamestate.states.LobbyState;
import net.atrophygames.obscrts.stats.StatsManager;
import net.atrophygames.obscrts.util.ConfigLocationUtil;
import net.atrophygames.obscrts.util.ItemBuilder;
import net.atrophygames.obscrts.util.RuleBook;
import net.atrophygames.obscrts.voting.Voting;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlayerConnectLobbyListener implements Listener {

    public static final String VOTING_ITEM_NAME = "§6Stimmzettel §7(Rechtsklick)";

    private final TTT plugin;
    private StatsManager statsManager;
    private final ItemStack voteItem;

    public PlayerConnectLobbyListener(TTT plugin) {
        this.plugin = plugin;
        this.statsManager = plugin.getStatsManager();
        voteItem = new ItemBuilder(Material.PAPER)
                .setDisplayName(VOTING_ITEM_NAME)
                .build();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof LobbyState)) return;
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if(!statsManager.isUserExistentForStats(playerUUID))
            statsManager.initStatsForPlayer(playerUUID);

        plugin.getPlayers().add(player);
        event.setJoinMessage("§7[§a+§7] " + getUserRankColor(player) + player.getDisplayName());

        player.getInventory().clear();
        player.getInventory().setChestplate(null);
        player.getInventory().setItem(4, voteItem);
        player.getInventory().setItem(8, getRuleBook());

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                new TextComponent("§cDas zufällige Töten von Spielern ist verboten!"));

        for(Player currentPlayer : Bukkit.getOnlinePlayers()) {
            currentPlayer.showPlayer(player);
            player.showPlayer(currentPlayer);
        }

        ConfigLocationUtil configLocationUtil = new ConfigLocationUtil(plugin, "lobby");
        if(configLocationUtil.loadLocation() != null) player.teleport(configLocationUtil.loadLocation());

        LobbyState lobbyState = (LobbyState) plugin.getGameStateManager().getCurrentGameState();
        lobbyState.getLobbyScoreboard().setScoreboard(player);

        LobbyCountdown lobbyCountdown = lobbyState.getLobbyCountdown();
        if(plugin.getPlayers().size() >= LobbyState.MIN_PLAYERS) {
            if(!lobbyCountdown.isRunning()) {
                lobbyCountdown.stopIdle();
                lobbyCountdown.start();
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(!(plugin.getGameStateManager().getCurrentGameState() instanceof LobbyState)) return;
        Player player = event.getPlayer();

        plugin.getPlayers().remove(player);
        event.setQuitMessage("§7[§c-§7] " + getUserRankColor(player) + player.getDisplayName());

        LobbyState lobbyState = (LobbyState) plugin.getGameStateManager().getCurrentGameState();
        LobbyCountdown lobbyCountdown = lobbyState.getLobbyCountdown();
        if(plugin.getPlayers().size() < LobbyState.MIN_PLAYERS) {
            if(lobbyCountdown.isRunning()) {
                lobbyCountdown.stop();
                lobbyCountdown.startIdle();
            }
        }

        Voting voting = plugin.getVoting();
        if(voting.getPlayerVotes().containsKey(player.getName())) {
            voting.getVotingMaps()[voting.getPlayerVotes().get(player.getName())].removeVote();
            voting.getPlayerVotes().remove(player.getName());
            voting.initVotingInventory();
        }
    }

    private ItemStack getRuleBook() {
        return new RuleBook()
                .setTitle("§bRegelwerk §7(Rechtsklick)")
                .setAuthor("AtrophyGames.net")
                .addPage("\n\n\n\n\n        §cTrouble in\n     TerroristTown\n\n            §7von" +
                        "\n    §6AtrophyGames.net")
                .addPage("Trouble in Terrorist-\nTown basiert auf dem gleichnamigen Spiel-\nmodus " +
                        "aus Garry’s Mod und ist ein auf Strategie und Taktik ausgelegter Spiel-" +
                        "\nmodus. Eine Trouble in TerroristTown Runde umfasst 12 Spieler, welche " +
                        "zu Spielbeginn nach 30 Sekunden eine von drei möglichen Rollen")
                .addPage("zugewiesen bekommen:\n\n" +
                        "§8Unschuldiger\n" +
                        "\n" +
                        "§rMit keinen Hilfsmitteln außer seinen Waffen, dem vertrauens-\nwürdigen " +
                        "Detective und einem Traitor-Tester, müssen die Innocents versuchen, möglichst " +
                        "gemeinsam, die unbekannten Traitor unter sich zu entlarven und zu eliminieren. " +
                        "Die Innocents können sich nur auf die Befehle des Detectives verlassen, ansonsten " +
                        "müssen sie selbst das Spiel in die Hand nehmen und taktisch ihre Mitspieler beobachten, " +
                        "um mögliche Mord oder andere verdächtige Aktionen zu bemerken.\n" +
                        "Das Ziel der Innocents ist somit, mit der Hilfe des Detectives, alle Traitor zu entlarven" +
                        "und zu eliminieren.\n" +
                        "§8Detektiv\n" +
                        "\n" +
                        "Mit einem blauen Lederharnisch ist der Detective wohl der vertrauenswürdigste Spieler im Spielgeschehen, aber somit leider auch das größte Ziel für die Traitor. Mit einem kleinen Detective Shop, welcher mit dem Befehl /shop oder /s aufgerufen werden kann, kann der Detective mit den dort vorhandenen Gadgets die Jagd auf die Traitor beginnen. Er hat die Möglichkeit, sich mit @d [Nachricht] im Detectivechat mit den anderen Detectives abzusprechen. Da er der vertrauenswürdigste Spieler der laufenden Runde ist, hat er eine große Verantwortung und darf sich keine Fehler erlauben. Mit jeder falschen Entscheidung unterstützt er die Traitor. Aus diesem Grund sollte er sich besser zweimal überlegen, wen er tötet. Es können mehrere Spieler pro Runde die Rolle des Detective erhalten. Der Detective erhält darüber hinaus beim Identifizieren einer Leiche, nach einer kurzen Ermittlungsdauer, einen Eintrag in seine Leichenakte, in welcher steht, durch wen die Leiche ermordet wurde.\n" +
                        "Das Ziel des Detectives ist es, mit der Hilfe der Innocents, alle Traitor zu entlarven und zu eliminieren und möglichst viele/alle Innocents am Leben zu erhalten.\n" +
                        "Traitor\n" +
                        "\n" +
                        "Getarnt als unschuldige Innocents ermorden die Traitor, meistens in einer kleinen Gruppe, alle Innocents und den Detective. Als Hilfsmittel haben sie ihren Traitor-Chat und ihren Traitor-Shop (/shop oder /s) mit vielen Gadgets, um das Morden und Tarnen zu erleichtern. Sie haben die Möglichkeit mit @t [Nachricht] im Traitorchat zu kommunizieren, da die Absprache in diesem Spielmodus von enormer Wichtigkeit ist. Töte niemals deine Traitor-Kameraden!\n" +
                        "Das Ziel des Traitors ist es, mit der Hilfe von seinen Traitor Kameraden, alle Innocents und den Detective zu eliminieren und möglichst unerkannt zu bleiben.\n" +
                        "Bewaffnung / Ausrüstung\n" +
                        "\n" +
                        "Jeder Spieler startet mit einer Lederjacke in die Runde, wobei sich diese je nach Rolle im Spiel farblich ändert (Traitor rot, Detectives blau). Darüber hinaus sind auf den Maps Truhen verteilt, welche dem Spieler per Rechtsklick Equipment ins Inventar geben. In diesen Truhen kann man Holzschwerter, Steinschwerter und Bögen mit Pfeilen finden. Des Weiteren ist auf jeder Karte eine Enderchest, welche dem Spieler per Rechtsklick ein Eisenschwert ins Inventar gibt. Je nach Rolle kann die Ausrüstung durch den Shop und das Erzielen von Punkten verbessert werden. Um das Inventar nicht jede Runde aufs Neue sortieren zu müssen, könnt ihr schon in der Lobby, am Villager direkt neben der Schilderwand, eure Inventarsortierung festlegen.\n" +
                        "Traitor-Tester\n" +
                        "\n" +
                        "Der Traitor-Tester dient als Hilfsmittel, um herauszufinden, wer ein Traitor ist und wer nicht. Sobald ein Spieler diesen Tester betritt und auf den Knopf drückt, schließt sich der Traitor-Tester für fünf Sekunden. Wenn der Spieler ein Innocent ist, verfärbt sich das Glas über dem Tester grün. Sollte der Spieler jedoch ein Traitor sein, dann verfärben sich die beiden Glasblöcke rot. Außerdem haben Traitor die Möglichkeit Traitorfallen auszulösen, welche meistens in Verbindung mit den Traitor-Testern auf den Maps sind")
                .build();
    }

    private String getUserRankColor(Player player) {
        CachedMetaData metaData = plugin.getLuckPerms().getPlayerAdapter(Player.class).getMetaData(player);
        return metaData.getMetaValue("rank-color");
    }
}
