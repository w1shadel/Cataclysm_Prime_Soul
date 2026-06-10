package com.maxwell.cataclysm_primed_soul.client.model.entity;

import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.client.animation.entity.Maledictus_PrimeAnimation;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.Maledictus_PrimeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("removal")
public class Maledictus_PrimeModel extends HierarchicalModel<Maledictus_PrimeEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Primed_Soul.MODID, "maledictus_primemodel"), "main");
    private final ModelPart Root;
    private final ModelPart Body;
    private final ModelPart UpperBody;
    private final ModelPart Head;
    private final ModelPart Armor;
    private final ModelPart Horns;
    private final ModelPart LeftH;
    private final ModelPart RightH;
    private final ModelPart Left_eye;
    private final ModelPart Right_eye;
    private final ModelPart Wings;
    private final ModelPart Left_Wing;
    private final ModelPart Right_Wing;
    private final ModelPart Right_Shoulder;
    private final ModelPart RightArm;
    private final ModelPart Right_ArmUnder;
    private final ModelPart RightHand;
    private final ModelPart Broken_Bow;
    private final ModelPart String_Upper;
    private final ModelPart String_Lower;
    private final ModelPart Broken_Halbard;
    private final ModelPart Broken_MaceRight;
    private final ModelPart Sword;
    private final ModelPart Left_Shoulder;
    private final ModelPart LeftArm;
    private final ModelPart LeftArm_Under;
    private final ModelPart Broken_Mace_Left;
    private final ModelPart LeftHand;
    private final ModelPart UnderBody;
    private final ModelPart Legs;
    private final ModelPart mantle_leg;
    private final ModelPart dwon_mantle;
    private final ModelPart LeftLeg;
    private final ModelPart LeftLeg_Under;
    private final ModelPart RightLeg;
    private final ModelPart RightLeg_Under;

    public Maledictus_PrimeModel(ModelPart root) {
        this.Root = root.getChild("Root");
        this.Body = this.Root.getChild("Body");
        this.UpperBody = this.Body.getChild("UpperBody");
        this.Head = this.UpperBody.getChild("Head");
        this.Armor = this.Head.getChild("Armor");
        this.Horns = this.Armor.getChild("Horns");
        this.LeftH = this.Horns.getChild("LeftH");
        this.RightH = this.Horns.getChild("RightH");
        this.Left_eye = this.Head.getChild("Left_eye");
        this.Right_eye = this.Head.getChild("Right_eye");
        this.Wings = this.UpperBody.getChild("Wings");
        this.Left_Wing = this.Wings.getChild("Left_Wing");
        this.Right_Wing = this.Wings.getChild("Right_Wing");
        this.Right_Shoulder = this.UpperBody.getChild("Right_Shoulder");
        this.RightArm = this.Right_Shoulder.getChild("RightArm");
        this.Right_ArmUnder = this.RightArm.getChild("Right_ArmUnder");
        this.RightHand = this.Right_ArmUnder.getChild("RightHand");
        this.Broken_Bow = this.Right_ArmUnder.getChild("Broken_Bow");
        this.String_Upper = this.Broken_Bow.getChild("String_Upper");
        this.String_Lower = this.Broken_Bow.getChild("String_Lower");
        this.Broken_Halbard = this.Right_ArmUnder.getChild("Broken_Halbard");
        this.Broken_MaceRight = this.Right_ArmUnder.getChild("Broken_MaceRight");
        this.Sword = this.Right_ArmUnder.getChild("Sword");
        this.Left_Shoulder = this.UpperBody.getChild("Left_Shoulder");
        this.LeftArm = this.Left_Shoulder.getChild("LeftArm");
        this.LeftArm_Under = this.LeftArm.getChild("LeftArm_Under");
        this.Broken_Mace_Left = this.LeftArm_Under.getChild("Broken_Mace_Left");
        this.LeftHand = this.LeftArm_Under.getChild("LeftHand");
        this.UnderBody = this.Body.getChild("UnderBody");
        this.Legs = this.UnderBody.getChild("Legs");
        this.mantle_leg = this.Legs.getChild("mantle_leg");
        this.dwon_mantle = this.mantle_leg.getChild("dwon_mantle");
        this.LeftLeg = this.Legs.getChild("LeftLeg");
        this.LeftLeg_Under = this.LeftLeg.getChild("LeftLeg_Under");
        this.RightLeg = this.Legs.getChild("RightLeg");
        this.RightLeg_Under = this.RightLeg.getChild("RightLeg_Under");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition Root = partdefinition.addOrReplaceChild("Root", CubeListBuilder.create(), PartPose.offset(0.0F, -7.5F, 0.0F));
        PartDefinition Body = Root.addOrReplaceChild("Body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition UpperBody = Body.addOrReplaceChild("UpperBody", CubeListBuilder.create().texOffs(174, 127).addBox(-5.5F, -15.5F, -3.5F, 11.0F, 13.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(78, 189).addBox(-6.0F, -2.7F, -2.5F, 12.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 8.0F, 0.0F));
        PartDefinition Head = UpperBody.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(188, 169).addBox(-3.25F, -8.25F, -4.25F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.85F, -15.25F, 0.25F));
        PartDefinition Armor = Head.addOrReplaceChild("Armor", CubeListBuilder.create().texOffs(188, 185).addBox(-2.5F, -2.5F, -4.5F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.4F))
                .texOffs(100, 219).addBox(3.5F, 0.0F, -5.5F, 4.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(112, 219).addBox(-4.5F, -0.1F, -5.5F, 4.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.75F, -5.75F, 0.25F));
        PartDefinition Horns = Armor.addOrReplaceChild("Horns", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition LeftH = Horns.addOrReplaceChild("LeftH", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition cube_r1 = LeftH.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(216, 40).addBox(-2.6312F, 8.7316F, -1.0F, 11.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.9F, -0.9F, -0.5F, 0.0F, 0.0F, -2.0071F));
        PartDefinition cube_r2 = LeftH.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(122, 188).addBox(-1.1808F, -9.4264F, 0.0F, 17.0F, 16.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(192, 51).addBox(0.8192F, -1.4264F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.9F, -0.9F, -0.5F, 0.0F, 0.0F, -0.4363F));
        PartDefinition RightH = Horns.addOrReplaceChild("RightH", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition cube_r3 = RightH.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(134, 60).addBox(-2.6312F, -12.7316F, -1.0F, 11.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.8F, -0.9F, -0.5F, 0.0F, 0.0F, -1.1345F));
        PartDefinition cube_r4 = RightH.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(156, 188).addBox(-0.1808F, -7.5736F, 1.0F, 16.0F, 17.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(216, 32).addBox(0.8191F, -2.5736F, -1.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.8F, -0.9F, -1.5F, 0.0F, 0.0F, -2.7053F));
        PartDefinition Left_eye = Head.addOrReplaceChild("Left_eye", CubeListBuilder.create(), PartPose.offset(2.7F, -3.7F, -4.0F));
        PartDefinition Right_eye = Head.addOrReplaceChild("Right_eye", CubeListBuilder.create(), PartPose.offset(-1.2F, -3.7F, -4.0F));
        PartDefinition Wings = UpperBody.addOrReplaceChild("Wings", CubeListBuilder.create(), PartPose.offset(8.5F, -7.5F, 9.5F));
        PartDefinition Left_Wing = Wings.addOrReplaceChild("Left_Wing", CubeListBuilder.create(), PartPose.offset(-8.5F, 0.0F, -4.0F));
        PartDefinition cube_r5 = Left_Wing.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(187, 74).addBox(-12.0F, -7.0F, 0.0F, 24.0F, 14.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(11.5F, 0.0F, 0.0F, 0.0F, -0.1745F, 0.0F));
        PartDefinition Right_Wing = Wings.addOrReplaceChild("Right_Wing", CubeListBuilder.create(), PartPose.offset(-8.5F, 0.0F, -4.0F));
        PartDefinition cube_r6 = Right_Wing.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(163, 74).addBox(-12.0F, -7.0F, 0.0F, 24.0F, 14.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.5F, 0.0F, 0.0F, 0.0F, 0.1745F, 0.0F));
        PartDefinition Right_Shoulder = UpperBody.addOrReplaceChild("Right_Shoulder", CubeListBuilder.create(), PartPose.offset(-4.5F, -14.5F, 0.0F));
        PartDefinition cube_r7 = Right_Shoulder.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(212, 214).addBox(-5.7139F, 3.0763F, -2.5F, 6.0F, 8.0F, 5.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.3054F));
        PartDefinition cube_r8 = Right_Shoulder.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(144, 205).addBox(-4.6872F, -8.3566F, -3.0F, 4.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, 0.0F, -1.5708F));
        PartDefinition RightArm = Right_Shoulder.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(164, 205).addBox(-2.3695F, -0.0086F, -2.5F, 5.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -1.0F, 0.0F));
        PartDefinition Right_ArmUnder = RightArm.addOrReplaceChild("Right_ArmUnder", CubeListBuilder.create().texOffs(40, 207).addBox(-2.3695F, -1.0086F, -2.5F, 5.0F, 10.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(80, 215).addBox(-3.3695F, -1.0086F, -2.5F, 5.0F, 7.0F, 5.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, 11.0F, 0.0F));
        PartDefinition RightHand = Right_ArmUnder.addOrReplaceChild("RightHand", CubeListBuilder.create(), PartPose.offset(-0.3F, 9.0F, 0.0F));
        PartDefinition Broken_Bow = Right_ArmUnder.addOrReplaceChild("Broken_Bow", CubeListBuilder.create().texOffs(0, 188).addBox(-1.0F, -2.0F, -8.0F, 2.0F, 2.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.0F, 0.0F));
        PartDefinition cube_r9 = Broken_Bow.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(104, 147).addBox(-2.0F, -2.0F, -11.0F, 4.0F, 4.0F, 19.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.2474F, 12.1158F, 0.5672F, 0.0F, 0.0F));
        PartDefinition cube_r10 = Broken_Bow.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(58, 146).addBox(-2.0F, -2.0F, -8.0F, 4.0F, 4.0F, 19.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.6601F, -14.2828F, -0.5672F, 0.0F, 0.0F));
        PartDefinition String_Upper = Broken_Bow.addOrReplaceChild("String_Upper", CubeListBuilder.create().texOffs(168, 88).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 0.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, -17.0F));
        PartDefinition String_Lower = Broken_Bow.addOrReplaceChild("String_Lower", CubeListBuilder.create().texOffs(44, 169).addBox(-1.0F, 0.0F, -17.0F, 2.0F, 0.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, 16.0F));
        PartDefinition Broken_Halbard = Right_ArmUnder.addOrReplaceChild("Broken_Halbard", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.2222F, -53.0556F, 2.0F, 2.0F, 65.0F, new CubeDeformation(0.0F))
                .texOffs(216, 65).addBox(-1.5F, -1.7222F, -52.0556F, 3.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(60, 207).addBox(-1.5F, -1.7222F, -45.0556F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(34, 198).addBox(-2.0F, -2.2222F, -38.0556F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(134, 66).addBox(-4.0F, -0.7222F, -37.5556F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(138, 66).addBox(2.0F, -0.7222F, -37.5556F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(164, 64).addBox(-0.5F, -4.2222F, -37.5556F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(162, 64).addBox(-0.5F, 1.7778F, -37.5556F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(100, 198).addBox(-0.5F, -0.7222F, -58.0556F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(176, 106).addBox(0.0F, -1.7222F, -72.0556F, 0.0F, 3.0F, 17.0F, new CubeDeformation(0.0F))
                .texOffs(118, 67).addBox(0.0F, 1.2778F, -58.0556F, 0.0F, 15.0F, 25.0F, new CubeDeformation(0.0F))
                .texOffs(192, 32).addBox(0.0F, -8.7222F, -51.0556F, 0.0F, 7.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 8.2222F, 0.0556F));
        PartDefinition cube_r11 = Broken_Halbard.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(44, 187).addBox(0.0F, -1.5F, -9.0F, 0.0F, 3.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.2222F, -63.0556F, 0.0F, 0.0F, -1.5708F));
        PartDefinition cube_r12 = Broken_Halbard.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(146, 66).addBox(1.5F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(142, 66).addBox(-3.5F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(44, 166).addBox(-0.5F, 1.5F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(160, 64).addBox(-0.5F, -3.5F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(160, 60).addBox(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.2222F, -54.5556F, 0.0F, 0.0F, 0.7854F));
        PartDefinition Broken_MaceRight = Right_ArmUnder.addOrReplaceChild("Broken_MaceRight", CubeListBuilder.create().texOffs(150, 147).addBox(-1.0F, -1.0333F, -14.1667F, 2.0F, 2.0F, 20.0F, new CubeDeformation(0.0F))
                .texOffs(150, 169).addBox(-1.5F, -1.5333F, -27.1667F, 3.0F, 3.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(137, 107).addBox(0.5F, 0.0667F, -34.1667F, 9.0F, 0.0F, 20.0F, new CubeDeformation(0.0F))
                .texOffs(118, 107).addBox(-9.5F, 0.0667F, -34.1667F, 9.0F, 0.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 8.0333F, 0.1667F));
        PartDefinition cube_r13 = Broken_MaceRight.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(118, 107).addBox(-4.0F, 0.0F, -10.0F, 9.0F, 0.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.4667F, -24.1667F, 0.0F, 0.0F, -1.5708F));
        PartDefinition cube_r14 = Broken_MaceRight.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(118, 107).addBox(-5.0F, 0.0F, -10.0F, 9.0F, 0.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.5333F, -24.1667F, 0.0F, 0.0F, -1.5708F));
        PartDefinition Sword = Right_ArmUnder.addOrReplaceChild("Sword", CubeListBuilder.create().texOffs(216, 46).addBox(-1.0F, -1.0031F, -4.7939F, 2.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(34, 188).addBox(-1.5F, -4.0031F, -6.7939F, 3.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(220, 0).addBox(0.0F, 0.9969F, -18.7939F, 0.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(220, 24).addBox(0.0F, 0.9969F, -37.4939F, 0.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 67).addBox(-0.5F, -0.5017F, -61.0013F, 1.0F, 1.0F, 58.0F, new CubeDeformation(0.0F))
                .texOffs(220, 8).addBox(0.0F, -3.0031F, -18.7939F, 0.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(220, 16).addBox(0.0F, -3.0031F, -37.4939F, 0.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(198, 216).addBox(0.0F, -0.9549F, -50.6708F, 0.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 7.0031F, -0.2061F));
        PartDefinition cube_r15 = Sword.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(184, 216).addBox(0.0F, -1.0F, -5.0F, 0.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.4962F, -39.0402F, -0.3054F, 0.0F, 0.0F));
        PartDefinition cube_r16 = Sword.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(122, 170).addBox(0.0F, -1.0F, -12.0F, 0.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.4962F, -20.3402F, -0.3054F, 0.0F, 0.0F));
        PartDefinition cube_r17 = Sword.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(216, 153).addBox(0.0F, -1.0F, -4.0F, 0.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2031F, -39.9939F, 0.3054F, 0.0F, 0.0F));
        PartDefinition cube_r18 = Sword.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(192, 16).addBox(0.0F, -1.0F, -11.0F, 0.0F, 2.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(192, 0).addBox(0.0F, -1.0F, -11.0F, 0.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2031F, -21.2939F, 0.3054F, 0.0F, 0.0F));
        PartDefinition cube_r19 = Sword.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(216, 144).addBox(0.0F, -1.0F, -5.5F, 0.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.4031F, -7.8939F, -0.3054F, 0.0F, 0.0F));
        PartDefinition cube_r20 = Sword.addOrReplaceChild("cube_r20", CubeListBuilder.create().texOffs(216, 74).addBox(0.0F, -1.0F, -5.5F, 0.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.3969F, -7.8939F, 0.3054F, 0.0F, 0.0F));
        PartDefinition cube_r21 = Sword.addOrReplaceChild("cube_r21", CubeListBuilder.create().texOffs(212, 164).addBox(-0.5F, -1.0F, -1.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -0.0031F, -5.7939F, 0.7418F, 0.0F, 0.0F));
        PartDefinition Left_Shoulder = UpperBody.addOrReplaceChild("Left_Shoulder", CubeListBuilder.create(), PartPose.offset(3.5F, -14.5F, 0.0F));
        PartDefinition cube_r22 = Left_Shoulder.addOrReplaceChild("cube_r22", CubeListBuilder.create().texOffs(20, 205).addBox(-2.7043F, -0.3823F, -3.0F, 4.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.5708F));
        PartDefinition cube_r23 = Left_Shoulder.addOrReplaceChild("cube_r23", CubeListBuilder.create().texOffs(212, 201).addBox(1.4903F, 1.6486F, -2.5F, 6.0F, 8.0F, 5.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3054F));
        PartDefinition LeftArm = Left_Shoulder.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(208, 88).addBox(-2.3695F, 0.0086F, -2.5F, 5.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, -1.0F, 0.0F));
        PartDefinition LeftArm_Under = LeftArm.addOrReplaceChild("LeftArm_Under", CubeListBuilder.create().texOffs(210, 103).addBox(-3.3695F, 0.0086F, -2.5F, 5.0F, 10.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(60, 215).addBox(-2.3695F, 0.0086F, -2.5F, 5.0F, 7.0F, 5.0F, new CubeDeformation(0.1F)), PartPose.offset(1.0F, 10.0F, 0.0F));
        PartDefinition Broken_Mace_Left = LeftArm_Under.addOrReplaceChild("Broken_Mace_Left", CubeListBuilder.create().texOffs(118, 107).addBox(-9.5F, 0.0667F, -34.1667F, 9.0F, 0.0F, 20.0F, new CubeDeformation(0.0F))
                .texOffs(137, 107).addBox(0.5F, 0.0667F, -34.1667F, 9.0F, 0.0F, 20.0F, new CubeDeformation(0.0F))
                .texOffs(84, 170).addBox(-1.5F, -1.5333F, -27.1667F, 3.0F, 3.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(0, 166).addBox(-1.0F, -1.0333F, -14.1667F, 2.0F, 2.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 9.0333F, 0.1667F));
        PartDefinition cube_r24 = Broken_Mace_Left.addOrReplaceChild("cube_r24", CubeListBuilder.create().texOffs(137, 107).addBox(-5.0F, 0.0F, -10.0F, 9.0F, 0.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.5333F, -24.1667F, 0.0F, 0.0F, -1.5708F));
        PartDefinition cube_r25 = Broken_Mace_Left.addOrReplaceChild("cube_r25", CubeListBuilder.create().texOffs(118, 107).addBox(-4.0F, 0.0F, -10.0F, 9.0F, 0.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.4667F, -24.1667F, 0.0F, 0.0F, -1.5708F));
        PartDefinition LeftHand = LeftArm_Under.addOrReplaceChild("LeftHand", CubeListBuilder.create(), PartPose.offset(-0.3F, 10.0F, 0.0F));
        PartDefinition UnderBody = Body.addOrReplaceChild("UnderBody", CubeListBuilder.create(), PartPose.offset(0.0F, 8.0F, 0.0F));
        PartDefinition Legs = UnderBody.addOrReplaceChild("Legs", CubeListBuilder.create(), PartPose.offset(0.0F, 0.5F, 0.0F));
        PartDefinition mantle_leg = Legs.addOrReplaceChild("mantle_leg", CubeListBuilder.create(), PartPose.offset(0.0F, -0.5F, -3.5F));
        PartDefinition cube_r26 = mantle_leg.addOrReplaceChild("cube_r26", CubeListBuilder.create().texOffs(194, 164).addBox(-4.5F, -4.5F, 0.0F, 9.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, -0.0436F, 0.0F, 0.0F));
        PartDefinition dwon_mantle = mantle_leg.addOrReplaceChild("dwon_mantle", CubeListBuilder.create(), PartPose.offset(0.0F, 4.0F, 0.0F));
        PartDefinition cube_r27 = dwon_mantle.addOrReplaceChild("cube_r27", CubeListBuilder.create().texOffs(216, 56).addBox(-4.5F, -4.5F, 0.0F, 9.0F, 9.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.8F, 0.2F, 0.0436F, 0.0F, 0.0F));
        PartDefinition LeftLeg = Legs.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(78, 198).addBox(-3.0F, 0.0F, -2.0F, 6.0F, 12.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(3.5F, -1.0F, -0.5F));
        PartDefinition cube_r28 = LeftLeg.addOrReplaceChild("cube_r28", CubeListBuilder.create().texOffs(188, 201).addBox(-3.0F, -5.0F, -2.5F, 6.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.6F, 4.0F, 0.0F, 0.0873F, 0.0F, -0.1745F));
        PartDefinition LeftLeg_Under = LeftLeg.addOrReplaceChild("LeftLeg_Under", CubeListBuilder.create().texOffs(0, 205).addBox(-2.5F, 0.0F, -0.5F, 5.0F, 12.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 12.0F, -1.0F));
        PartDefinition cube_r29 = LeftLeg_Under.addOrReplaceChild("cube_r29", CubeListBuilder.create().texOffs(210, 118).addBox(-3.0F, -2.0F, -2.5F, 6.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4F, 0.5F, 0.5F, 0.48F, 0.0F, 0.0F));
        PartDefinition RightLeg = Legs.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(194, 147).addBox(-3.0F, 0.0F, -1.0F, 6.0F, 12.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.5F, -1.0F, -1.5F));
        PartDefinition cube_r30 = RightLeg.addOrReplaceChild("cube_r30", CubeListBuilder.create().texOffs(100, 204).addBox(-3.0F, -5.0F, -1.5F, 6.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.6F, 4.0F, 0.0F, 0.0873F, 0.0F, 0.1745F));
        PartDefinition RightLeg_Under = RightLeg.addOrReplaceChild("RightLeg_Under", CubeListBuilder.create().texOffs(124, 204).addBox(-2.5F, 0.0F, -0.5F, 5.0F, 12.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 12.0F, 0.0F));
        PartDefinition cube_r31 = RightLeg_Under.addOrReplaceChild("cube_r31", CubeListBuilder.create().texOffs(210, 131).addBox(-2.0F, -2.0F, -2.5F, 6.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.4F, 0.5F, 0.5F, 0.48F, 0.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    public ModelPart getBody() {
        return Body;
    }

    public ModelPart getUpperBody() {
        return UpperBody;
    }

    public ModelPart getRight_Shoulder() {
        return Right_Shoulder;
    }

    public ModelPart getRightArm() {
        return RightArm;
    }

    public ModelPart getRight_ArmUnder() {
        return Right_ArmUnder;
    }

    public ModelPart getLeft_eye() {
        return Left_eye;
    }

    public ModelPart getRight_eye() {
        return Right_eye;
    }

    public ModelPart getRightHand() {
        return RightHand;
    }

    public ModelPart getLeftHand() {
        return LeftHand;
    }

    @Override
    public void setupAnim(Maledictus_PrimeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.setupWeaponVisibility(entity);
        if (entity.getAttackState() == 0) {
            this.animateHeadLookTarget(netHeadYaw, headPitch);
        }
        boolean isPhase2 = entity.isPhase2();
        float comboSpeed = isPhase2 ? 1.4F : 1.15F;
        float heavyAttackSpeed = isPhase2 ? 1.25F : 1.05F;
        float godSpeed = isPhase2 ? 1.6F : 1.35F;
        this.animate(entity.getAnimationState("idle"), Maledictus_PrimeAnimation.IDLE, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("jab_1"), Maledictus_PrimeAnimation.ATTACK_JAB_1, ageInTicks, comboSpeed);
        this.animate(entity.getAnimationState("jab_2"), Maledictus_PrimeAnimation.ATTACK_JAB_2, ageInTicks, comboSpeed);
        this.animate(entity.getAnimationState("jab_3"), Maledictus_PrimeAnimation.ATTACK_JAB_3, ageInTicks, comboSpeed);
        this.animate(entity.getAnimationState("charge"), Maledictus_PrimeAnimation.ATTACK_CHARGE, ageInTicks, heavyAttackSpeed);
        this.animate(entity.getAnimationState("counter_start"), Maledictus_PrimeAnimation.ATTACK_COUNTER_START, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("counter_success"), Maledictus_PrimeAnimation.ATTACK_COUNTER_SUCCESS, ageInTicks, godSpeed);
        this.animate(entity.getAnimationState("counter_fail"), Maledictus_PrimeAnimation.ATTACK_COUNTER_FAIL, ageInTicks, 1.1F);
        this.animate(entity.getAnimationState("shockwave_start"), Maledictus_PrimeAnimation.ATTACK_SHOCKWAVE_START, ageInTicks, heavyAttackSpeed);
        this.animate(entity.getAnimationState("shockwave_end"), Maledictus_PrimeAnimation.ATTACK_SHOCKWAVE_END, ageInTicks, heavyAttackSpeed);
        this.animate(entity.getAnimationState("grab_start"), Maledictus_PrimeAnimation.ATTACK_GRAB_START, ageInTicks, comboSpeed);
        this.animate(entity.getAnimationState("grab_success"), Maledictus_PrimeAnimation.ATTACK_GRAB_SUCCESS, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("grab_fail"), Maledictus_PrimeAnimation.ATTACK_GRAB_FAIL, ageInTicks, 1.1F);
        this.animate(entity.getAnimationState("head_break"), Maledictus_PrimeAnimation.ATTACK_HEAD_BREAKER, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("ex_jab_1"), Maledictus_PrimeAnimation.ATTACK_EX_JAB_1, ageInTicks, comboSpeed);
        this.animate(entity.getAnimationState("ex_jab_2"), Maledictus_PrimeAnimation.ATTACK_EX_JAB_2, ageInTicks, comboSpeed);
        this.animate(entity.getAnimationState("ex_jab_3"), Maledictus_PrimeAnimation.ATTACK_EX_JAB_3, ageInTicks, godSpeed);
        this.animate(entity.getAnimationState("far_start"), Maledictus_PrimeAnimation.ATTACK_FAR_START, ageInTicks, comboSpeed);
        this.animate(entity.getAnimationState("backstep"), Maledictus_PrimeAnimation.BACKSTEP, ageInTicks, godSpeed);
        boolean isAttacking = entity.getAttackState() != 0;
        if (!isAttacking) {
            this.animate(entity.getAnimationState("walk"), Maledictus_PrimeAnimation.WALK, ageInTicks, 1.0F);
        }
    }

    private void setupWeaponVisibility(Maledictus_PrimeEntity entity) {
        int attackState = entity.getAttackState();
        boolean thrownSword = attackState == Maledictus_PrimeEntity.ATTACK_FAR_START && entity.attackTicks >= 25;
        this.Broken_Halbard.visible = false;
        this.Broken_MaceRight.visible = false;
        this.Broken_Mace_Left.visible = false;
        this.Broken_Bow.visible = false;
        this.Sword.visible = !thrownSword;
        this.Armor.visible = !entity.isPhase2();
    }

    private void animateHeadLookTarget(float yRot, float xRot) {
        this.Head.xRot += xRot * ((float) Math.PI / 180F);
        this.Head.yRot += yRot * ((float) Math.PI / 180F);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        Root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart root() {
        return Root;
    }
}
