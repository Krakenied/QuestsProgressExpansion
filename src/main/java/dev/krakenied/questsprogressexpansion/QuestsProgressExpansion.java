package dev.krakenied.questsprogressexpansion;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Quest;
import me.clip.placeholderapi.expansion.Configurable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Taskable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.IllegalFormatException;
import java.util.Map;
import java.util.logging.Level;

@SuppressWarnings("unused")
public final class QuestsProgressExpansion extends PlaceholderExpansion implements Configurable, Taskable {

    private BukkitQuestsPlugin plugin = null;

    @Override
    public @NotNull String getIdentifier() {
        return "questsprogress";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Krakenied";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public @NotNull String getRequiredPlugin() {
        return "Quests";
    }

    @Override
    public @NotNull String onPlaceholderRequest(final @Nullable Player player, final @NotNull String params) {
        if (player == null) {
            return this.log(null, params, "player is null");
        }

        if (this.plugin == null) {
            return this.log(player, params, "plugin is null");
        }

        final String[] parts = params.split("_");
        if (parts.length != 3) {
            return this.log(player, params, "invalid syntax");
        }

        final QPlayer qPlayer = this.plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return this.log(player, params, "quests player is null");
        }

        final Quest quest = this.plugin.getQuestManager().getQuestById(parts[0]);
        if (quest == null) {
            return this.log(player, params, "quest is null");
        }

        final QuestProgress questProgress = qPlayer.getQuestProgressFile().getQuestProgress(quest);
        if (questProgress == null) {
            return this.log(player, params, "quest progress is null");
        }

        if (questProgress.isCompleted()) {
            return this.getString("completed", "-");
        }

        final TaskProgress taskProgress = questProgress.getTaskProgress(parts[1]);
        if (taskProgress == null) {
            return this.log(player, params, "task progress is null");
        }

        if (taskProgress.isCompleted()) {
            return this.getString("completed", "-");
        }

        Object progress = taskProgress.getProgress();
        if (progress == null) {
//            if (!questProgress.isStarted()) {
//                return this.log(player, params, "progress is null");
//            }
            progress = 0;
        }

        try {
            return String.format("%" + parts[2], progress);
        } catch (final IllegalFormatException e) {
            return this.log(player, params, e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getDefaults() {
        return Map.of("completed", "-");
    }

    @Override
    public void start() {
        this.plugin = (BukkitQuestsPlugin) Bukkit.getPluginManager().getPlugin("Quests");
    }

    @Override
    public void stop() {
        this.plugin = null;
    }

    @Contract("_, _, _ -> param3")
    private @NotNull String log(final @Nullable Player player, final @NotNull String params, final @NotNull String message) {
        this.log(Level.WARNING, message + " for " + params + " (" + (player != null ? player.getName() : null) + ")");
        return message;
    }
}
