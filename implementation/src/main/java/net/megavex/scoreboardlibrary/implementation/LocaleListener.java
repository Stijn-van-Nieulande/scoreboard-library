package net.megavex.scoreboardlibrary.implementation;

import net.megavex.scoreboardlibrary.implementation.sidebar.PlayerDependantLocaleSidebar;
import net.megavex.scoreboardlibrary.implementation.sidebar.SidebarTask;
import net.megavex.scoreboardlibrary.implementation.team.TeamManagerTask;
import net.megavex.scoreboardlibrary.implementation.util.DispatchUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLocaleChangeEvent;

public record LocaleListener(ScoreboardLibraryImpl scoreboardLibrary) implements Listener {
  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onPlayerLocaleChanged(PlayerLocaleChangeEvent event) {
    var player = event.getPlayer();
    // Need to wait a tick because the locale didn't update yet
    DispatchUtil.runNextTick(scoreboardLibrary.plugin(), () -> {
      var slPlayer = scoreboardLibrary.getPlayer(player);
      if (slPlayer != null) {
        var teamManager = slPlayer.teamManager();
        if (teamManager != null) {
          teamManager.taskQueue().add(new TeamManagerTask.ReloadPlayer(player));
        }

        var sidebar = slPlayer.sidebar();
        if (sidebar instanceof PlayerDependantLocaleSidebar) {
          sidebar.taskQueue().add(new SidebarTask.ReloadPlayer(player));
        }
      }
    });
  }
}
