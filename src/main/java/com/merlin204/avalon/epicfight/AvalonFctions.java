package com.merlin204.avalon.epicfight;

import com.merlin204.avalon.main.AvalonMOD;
import net.minecraft.resources.ResourceLocation;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.world.capabilities.entitypatch.Faction;

public enum AvalonFctions implements Faction {
    EMPTY(ResourceLocation.fromNamespaceAndPath(AvalonMOD.MOD_ID, "textures/empty.png"), MathUtils.packColor(0, 0, 0, 0), 0);

    final ResourceLocation healthBar;
    final int healthBarIndex;
    final int damageColor;
    final int id;

    private AvalonFctions(ResourceLocation healthBar, int damageColor, int healthBarIndex) {
        this.id = Faction.ENUM_MANAGER.assign(this);
        this.healthBar = healthBar;
        this.damageColor = damageColor;
        this.healthBarIndex = healthBarIndex;
    }

    public int universalOrdinal() {
        return this.id;
    }

    public ResourceLocation healthBarTexture() {
        return this.healthBar;
    }

    public int damageColor() {
        return this.damageColor;
    }

    public int healthBarIndex() {
        return this.healthBarIndex;
    }
}
