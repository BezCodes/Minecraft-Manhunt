package net.bezeram.manhuntmod.game.players;

import net.bezeram.manhuntmod.game.Game;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.Hashtable;
import java.util.UUID;

public class PlayerCoords {

    public void update(final UUID uuid, final Vec3 newPosition) {
        try {
            coords.put(uuid, newPosition);
        } catch (Exception ignored) {}
    }

    public void update(final UUID uuid) {
        try {
            if (!Game.inSession())
                return;

            ServerPlayer player = Game.get().getPlayer(uuid);
            PlayerCoords coords = playerData.getLastPosition(player.getLevel().dimension());

            if (coords != null)
                coords.update(uuid, player.getPosition(1));
        } catch (Exception ignored) {}
    }

    public Vec3 get(final UUID uuid) {
        if (!coords.containsKey(uuid) || uuid == null)
            return null;

        return coords.get(uuid);
    }

    public PlayerCoords(final PlayerData playerData) { this.playerData = playerData; }

    // Links the Manhunt ID (MAID) of the player to coords
    private final Hashtable<UUID, Vec3> coords = new Hashtable<>();
    // Pointer to its holder
    private final PlayerData playerData;
}
