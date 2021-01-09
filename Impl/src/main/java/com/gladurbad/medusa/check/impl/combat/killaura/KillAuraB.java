package com.gladurbad.medusa.check.impl.combat.killaura;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.exempt.type.ExemptType;
import com.gladurbad.medusa.packet.Packet;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

/**
 * Created on 10/24/2020 Package com.gladurbad.medusa.check.impl.combat.killaura by GladUrBad
 */

@CheckInfo(name = "KillAura (B)", experimental = true, description = "Checks for KeepSprint modules.")
public class KillAuraB extends Check {

    public KillAuraB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isPosLook()) {
            if (data.getExemptProcessor().isExempt(ExemptType.COMBAT)) {
                final boolean sprinting = actionInfo().isSprinting();
                final double deltaXZ = positionInfo().getDeltaXZ();
                final double lastDeltaXZ = positionInfo().getLastDeltaXZ();

                final double acceleration = Math.abs(deltaXZ - lastDeltaXZ);
                final long clickDelay = clickInfo().getDelay();
                final boolean onGround = positionInfo().isMathematicallyOnGround();
                final Entity target = combatInfo().getTarget();

                final boolean invalid = acceleration < 0.0025 &&
                        deltaXZ > 0.22 &&
                        onGround &&
                        sprinting &&
                        clickDelay < 250 &&
                        target.getType() == EntityType.PLAYER;

                if (invalid) {
                    if (++buffer > 5) {
                        fail(String.format("acceleration=%.6f", acceleration));
                        buffer = Math.min(10, buffer);
                    }
                } else {
                    buffer = Math.max(buffer - 0.25, 0);
                }
            }
        }
    }
}