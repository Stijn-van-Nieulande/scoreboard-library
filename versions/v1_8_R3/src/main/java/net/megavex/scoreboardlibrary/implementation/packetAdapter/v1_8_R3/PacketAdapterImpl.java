package net.megavex.scoreboardlibrary.implementation.packetAdapter.v1_8_R3;

import java.lang.reflect.Field;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ScoreboardLibraryPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.SidebarPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.TeamsPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.UnsafeUtilities;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketListenerPlayOut;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardObjective;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PacketAdapterImpl extends ScoreboardLibraryPacketAdapter<Packet<PacketListenerPlayOut>> {
  static final Field objectiveModeField;

  static {
    objectiveModeField = UnsafeUtilities.getField(PacketPlayOutScoreboardObjective.class, "d");
  }

  private final PacketPlayOutScoreboardDisplayObjective displayPacket = new PacketPlayOutScoreboardDisplayObjective();
  private final PacketPlayOutScoreboardObjective removePacket = new PacketPlayOutScoreboardObjective();

  public PacketAdapterImpl() {
    // Setup static packets
    UnsafeUtilities.UNSAFE.putInt(
      displayPacket,
      UnsafeUtilities.UNSAFE.objectFieldOffset(UnsafeUtilities.getField(PacketPlayOutScoreboardDisplayObjective.class, "a")),
      POSITION_SIDEBAR
    );
    UnsafeUtilities.setField(UnsafeUtilities.getField(PacketPlayOutScoreboardDisplayObjective.class, "b"), displayPacket, objectiveName);

    UnsafeUtilities.setField(UnsafeUtilities.getField(PacketPlayOutScoreboardObjective.class, "a"), removePacket, objectiveName);
    UnsafeUtilities.UNSAFE.putInt(
      removePacket,
      UnsafeUtilities.UNSAFE.objectFieldOffset(objectiveModeField),
      OBJECTIVE_MODE_REMOVE
    );
  }

  @Override
  public @NotNull SidebarPacketAdapter<Packet<PacketListenerPlayOut>, ?> createSidebarPacketAdapter(@NotNull Sidebar sidebar) {
    return new SidebarPacketAdapterImpl(this, sidebar);
  }

  @Override
  public void displaySidebar(@NotNull Iterable<Player> players) {
    sendPacket(players, displayPacket);
  }

  @Override
  public void removeSidebar(@NotNull Iterable<Player> players) {
    sendPacket(players, removePacket);
  }

  @Override
  public @NotNull TeamsPacketAdapter<?, ?> createTeamPacketAdapter(@NotNull String teamName) {
    return new TeamsPacketAdapterImpl(this, teamName);
  }

  @Override
  public boolean isLegacy(@NotNull Player player) {
    return true;
  }

  @Override
  public void sendPacket(@NotNull Player player, @NotNull Packet<PacketListenerPlayOut> packet) {
    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
  }
}
