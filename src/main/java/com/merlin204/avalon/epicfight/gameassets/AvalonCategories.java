package com.merlin204.avalon.epicfight.gameassets;

import yesman.epicfight.world.capabilities.item.WeaponCategory;

public enum AvalonCategories implements WeaponCategory {
    ANIMATION_ITEM;
    AvalonCategories(){
        this.id = WeaponCategory.ENUM_MANAGER.assign(this);
    }
    final int id;
    @Override
    public int universalOrdinal() {
        return this.id;
    }

}
