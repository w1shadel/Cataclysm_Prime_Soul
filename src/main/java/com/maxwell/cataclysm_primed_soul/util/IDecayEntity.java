package com.maxwell.cataclysm_primed_soul.util;

public interface IDecayEntity {
    float getDecayAmount();
    void setDecayAmount(float amount);
    void addDecayAmount(float amount);

    boolean isSuperInvincible();
    void setSuperInvincible(boolean val);

    boolean isRemoveBypass();
    void setRemoveBypass(boolean val);

    void subtractTrueHP(float amount);

    int getDecayHoldTicks();
    void setDecayHoldTicks(int ticks);
}