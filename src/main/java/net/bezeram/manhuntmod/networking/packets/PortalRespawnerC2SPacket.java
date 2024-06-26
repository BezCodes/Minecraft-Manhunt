package net.bezeram.manhuntmod.networking.packets;

import net.bezeram.manhuntmod.game.Game;
import net.bezeram.manhuntmod.networking.ModMessages;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PortalRespawnerC2SPacket {

    public PortalRespawnerC2SPacket(FriendlyByteBuf ignoredBuff) {}
    public PortalRespawnerC2SPacket() {}

    public void toBytes(FriendlyByteBuf ignoredBuff) {}

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // SERVER SIDE

            try {
                ServerPlayer player = context.getSender();
                if (player == null || !Game.inSession())
                    return;

                GlobalPos prevRespawnPos;
                if (player.getRespawnPosition() == null)
                    prevRespawnPos = GlobalPos.of(player.getRespawnDimension(), new BlockPos(0, 90, 0));
                else
                    prevRespawnPos = GlobalPos.of(player.getRespawnDimension(), player.getRespawnPosition());

                BlockPos nextRespawnBlockPosition = Game.get().getPlayerData().getPortalRespawnCoords(player.getUUID());
                Game.get().getPlayerData().setRespawnBuffer(player.getUUID(), prevRespawnPos);
                this.setRespawnPosition(player, nextRespawnBlockPosition);

                Game.get().getPlayerData().setUsedPortalRespawn(true);
                Game.LOG("New Portal Respawn position set");
                ModMessages.sendToPlayer(new PortalRespawnSetAcknowledgeS2CPacket(), player);
            } catch (Exception ignored) {}
         });

        context.setPacketHandled(true);
    }

    private void setRespawnPosition(final ServerPlayer player, final BlockPos newPosition) {
        player.setRespawnPosition(player.getLevel().dimension(), newPosition, player.getRespawnAngle(), true, false);
    }
}
