package com.maxwell.cataclysm_primed_soul.api.entity;

public interface IDialogueEntity {
    boolean isDowned();
    int getDialogueIndex();
    String getNameKey();
    String getLineKey(int index);
    int getMaxLines();
}
