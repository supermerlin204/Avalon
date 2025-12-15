package com.merlin204.avalon.entity.api.patch;

import yesman.epicfight.api.client.animation.property.TrailInfo;

public interface IAvalonPatch {



    default TrailInfo modifierTrailInfo(TrailInfo oldInfo){
        return null;
    }

}
