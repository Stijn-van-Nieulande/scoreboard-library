package net.megavex.scoreboardlibrary.implementation.packetAdapter.v1_19_R3.util;

import io.papermc.paper.adventure.AdventureComponent;
import net.kyori.adventure.text.Component;

public final class NativeAdventureUtil {
  private NativeAdventureUtil() {
  }

  public static net.minecraft.network.chat.Component fromAdventureComponent(Component component) {
    return new AdventureComponent(component);
  }
}
