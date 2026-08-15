package com.maxwell.cataclysm_primed_soul.client.model.entity;

import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.client.animation.entity.Maledictus_PrimeAnimation;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.MaledictusPhantomEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("removal")
public class MaledictusPhantomModel extends HierarchicalModel<MaledictusPhantomEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(Primed_Soul.MODID, "maledictus_phantommodel"), "main"
    );
    private final ModelPart Root;
    private final ModelPart Broken_Halbard;
    private final ModelPart Broken_MaceRight;
    private final ModelPart Broken_Mace_Left;
    private final ModelPart Broken_Bow;
    private final ModelPart Sword;

    public MaledictusPhantomModel(ModelPart root) {
        this.Root = root.getChild("Root");
        ModelPart body = this.Root.getChild("Body");
        ModelPart upperBody = body.getChild("UpperBody");
        ModelPart rightShoulder = upperBody.getChild("Right_Shoulder");
        ModelPart rightArm = rightShoulder.getChild("RightArm");
        ModelPart rightArmUnder = rightArm.getChild("Right_ArmUnder");
        ModelPart leftShoulder = upperBody.getChild("Left_Shoulder");
        ModelPart leftArm = leftShoulder.getChild("LeftArm");
        ModelPart leftArmUnder = leftArm.getChild("LeftArm_Under");
        this.Broken_Halbard = rightArmUnder.getChild("Broken_Halbard");
        this.Broken_MaceRight = rightArmUnder.getChild("Broken_MaceRight");
        this.Broken_Bow = rightArmUnder.getChild("Broken_Bow");
        this.Sword = rightArmUnder.getChild("Sword");
        this.Broken_Mace_Left = leftArmUnder.getChild("Broken_Mace_Left");
    }

    @Override
    public void setupAnim(MaledictusPhantomEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.setupWeaponVisibility(entity);
        int type = entity.getPhantomType();
        this.animate(entity.phantomSpearChargeAnimationState,
                Maledictus_PrimeAnimation.ATTACK_PHANTOM_SPEAR_CHARGE, ageInTicks, 1.0F);
        this.animate(entity.phantomMaceCrushAnimationState,
                Maledictus_PrimeAnimation.ATTACK_PHANTOM_MACE_CRUSH, ageInTicks, 1.0F);
        this.animate(entity.phantomBowSnipeAnimationState,
                Maledictus_PrimeAnimation.ATTACK_PHANTOM_BOW_SNIPE, ageInTicks, 1.0F);
    }

    private void setupWeaponVisibility(MaledictusPhantomEntity entity) {
        int type = entity.getPhantomType();
        boolean spear = type == MaledictusPhantomEntity.TYPE_SPEAR;
        boolean mace = type == MaledictusPhantomEntity.TYPE_MACE;
        boolean bow = type == MaledictusPhantomEntity.TYPE_BOW;
        this.Broken_Halbard.visible = spear;
        this.Broken_MaceRight.visible = mace;
        this.Broken_Mace_Left.visible = mace;
        this.Broken_Bow.visible = bow;
        this.Sword.visible = type == MaledictusPhantomEntity.TYPE_NEXT_STATE;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        Root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart root() {
        return Root;
    }
}
