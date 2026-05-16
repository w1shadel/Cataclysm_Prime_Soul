package com.maxwell.cataclysm_primed_soul.client.model.entity;



import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.client.animation.Ignis_PrimeAnimation;
import com.maxwell.cataclysm_primed_soul.entity.InternalAnimationMonster.IABossMonsters.Ignis_PrimeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class Ignis_PrimeModel extends HierarchicalModel<Ignis_PrimeEntity> {

	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Primed_Soul.MODID, "ignis_primemodel"), "main");
	private final ModelPart Root;
	private final ModelPart Boddies;
	private final ModelPart Body_Under;
	private final ModelPart Fire;
	private final ModelPart Body_Upper;
	private final ModelPart Arm_Left;
	private final ModelPart L_Under;
	private final ModelPart Arm_Left_Joint;
	private final ModelPart Arm_Right;
	private final ModelPart R_Under;
	private final ModelPart Arm_Right_Joint;
	private final ModelPart Head;
	private final ModelPart Core;
	private final ModelPart Grip_pos;

	public Ignis_PrimeModel(ModelPart root) {
		this.Root = root.getChild("Root");
		this.Boddies = this.Root.getChild("Boddies");
		this.Body_Under = this.Boddies.getChild("Body_Under");
		this.Fire = this.Body_Under.getChild("Fire");
		this.Body_Upper = this.Boddies.getChild("Body_Upper");
		this.Arm_Left = this.Body_Upper.getChild("Arm_Left");
		this.L_Under = this.Arm_Left.getChild("L_Under");
		this.Arm_Left_Joint = this.Arm_Left.getChild("Arm_Left_Joint");
		this.Arm_Right = this.Body_Upper.getChild("Arm_Right");
		this.R_Under = this.Arm_Right.getChild("R_Under");
		this.Arm_Right_Joint = this.Arm_Right.getChild("Arm_Right_Joint");
		this.Head = this.Body_Upper.getChild("Head");
		this.Core = this.Body_Upper.getChild("Core");
		this.Grip_pos = this.Body_Upper.getChild("Grip_pos");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Root = partdefinition.addOrReplaceChild("Root", CubeListBuilder.create(), PartPose.offset(0.0F, 15.3F, 8.3F));

		PartDefinition Boddies = Root.addOrReplaceChild("Boddies", CubeListBuilder.create(), PartPose.offset(0.0F, -8.3F, -5.7F));

		PartDefinition Body_Under = Boddies.addOrReplaceChild("Body_Under", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = Body_Under.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(50, 72).addBox(-2.0F, -4.087F, -0.5F, 5.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.3F, 1.887F, 0.1905F, 0.0F, 1.5708F, 0.3491F));

		PartDefinition cube_r2 = Body_Under.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(62, 74).addBox(-2.0F, -3.887F, -0.5F, 5.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.2F, 1.687F, 0.1905F, 0.0F, 1.5708F, -0.3491F));

		PartDefinition cube_r3 = Body_Under.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(44, 31).addBox(-6.0F, -3.887F, -0.5F, 12.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.687F, 2.5905F, 0.3491F, 0.0F, 0.0F));

		PartDefinition cube_r4 = Body_Under.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(72, 43).addBox(-6.0F, -4.1F, -2.0F, 12.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.4F, -1.8F, -0.3491F, 0.0F, 0.0F));

		PartDefinition Fire = Body_Under.addOrReplaceChild("Fire", CubeListBuilder.create().texOffs(96, 104).addBox(-6.0F, -11.0F, -8.0F, 12.0F, 20.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 8.3F, 5.7F));

		PartDefinition Body_Upper = Boddies.addOrReplaceChild("Body_Upper", CubeListBuilder.create().texOffs(0, 42).addBox(-6.5F, -5.4F, -4.7F, 13.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r5 = Body_Upper.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(40, 42).addBox(-3.0F, -3.5F, -5.0F, 6.0F, 7.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, -10.6077F, -3.1223F, 0.2618F, 0.0F, 0.0F));

		PartDefinition cube_r6 = Body_Upper.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(40, 42).addBox(-3.0F, -3.5F, -5.0F, 6.0F, 5.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -10.7077F, -4.1223F, 0.2657F, -0.1685F, -0.0456F));

		PartDefinition cube_r7 = Body_Upper.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(32, 59).addBox(-3.0F, -5.0F, -4.0F, 6.0F, 3.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(0, 87).addBox(-9.5F, -4.0F, -2.0F, 2.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 99).addBox(7.5F, -4.0F, -2.0F, 2.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 54).addBox(3.0F, -5.0F, -4.0F, 6.0F, 7.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-8.0F, 2.0F, -4.0F, 16.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.9F, -3.7F, 0.2618F, 0.0F, 0.0F));

		PartDefinition cube_r8 = Body_Upper.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(0, 54).addBox(-3.0F, -3.5F, -5.0F, 6.0F, 5.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -10.7077F, -4.1223F, 0.2657F, 0.1685F, 0.0456F));

		PartDefinition cube_r9 = Body_Upper.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(0, 31).addBox(-7.0F, -2.5F, -4.0F, 14.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.8462F, -1.3106F, 0.1745F, 0.0F, 0.0F));

		PartDefinition Arm_Left = Body_Upper.addOrReplaceChild("Arm_Left", CubeListBuilder.create().texOffs(64, 59).addBox(-3.5263F, 4.0F, -3.7F, 7.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(16.0F, -15.0F, -4.6F));

		PartDefinition L_Under = Arm_Left.addOrReplaceChild("L_Under", CubeListBuilder.create().texOffs(72, 12).addBox(-2.8029F, -0.4448F, -2.7F, 6.0F, 14.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 15.0F, 0.0F));

		PartDefinition cube_r10 = L_Under.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(78, 14).addBox(6.7197F, 7.9022F, -1.5F, 2.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.2381F, -6.4346F, -0.2F, 0.0F, 0.0F, 0.48F));

		PartDefinition cube_r11 = L_Under.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(74, 14).addBox(-0.3084F, 3.2703F, -1.5F, 4.0F, 11.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.2381F, -6.4346F, -0.2F, 0.0F, 0.0F, 0.3054F));

		PartDefinition Arm_Left_Joint = Arm_Left.addOrReplaceChild("Arm_Left_Joint", CubeListBuilder.create().texOffs(74, 74).addBox(-2.05F, -8.6F, -0.6F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 12).addBox(-5.0263F, -2.6F, -4.6F, 9.0F, 10.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(40, 109).addBox(-9.0263F, -2.6F, -4.6F, 4.0F, 10.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 0.6F, -0.1F));

		PartDefinition cube_r12 = Arm_Left_Joint.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(74, 74).addBox(-2.683F, -11.497F, -0.5F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.7381F, 8.9654F, -0.1F, 0.0F, 0.0F, 0.3491F));

		PartDefinition Arm_Right = Body_Upper.addOrReplaceChild("Arm_Right", CubeListBuilder.create().texOffs(0, 71).addBox(-2.6F, 4.0F, -3.7F, 7.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(-17.0F, -15.0F, -4.6F));

		PartDefinition R_Under = Arm_Right.addOrReplaceChild("R_Under", CubeListBuilder.create().texOffs(28, 72).addBox(-3.1599F, 0.2008F, -2.7F, 6.0F, 14.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 14.0F, 0.0F));

		PartDefinition cube_r13 = R_Under.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(30, 74).addBox(-9.7843F, 7.1616F, -1.5F, 2.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5071F, -5.5999F, -0.2F, 0.0F, 0.0F, -0.48F));

		PartDefinition cube_r14 = R_Under.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(31, 74).addBox(-4.8223F, 2.8004F, -1.5F, 4.0F, 11.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5071F, -5.5999F, -0.2F, 0.0F, 0.0F, -0.3054F));

		PartDefinition Arm_Right_Joint = Arm_Right.addOrReplaceChild("Arm_Right_Joint", CubeListBuilder.create().texOffs(36, 12).addBox(-3.6F, -2.0F, -4.7F, 9.0F, 10.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(66, 109).addBox(5.4F, -2.0F, -4.7F, 4.0F, 10.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(50, 81).addBox(-1.6F, -8.0F, -0.7F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r15 = Arm_Right_Joint.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(50, 81).addBox(-1.1796F, -11.4089F, -0.5F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5071F, 9.4001F, -0.2F, 0.0F, 0.0F, -0.3491F));

		PartDefinition Head = Body_Upper.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(96, 61).addBox(-3.4393F, -8.1888F, -5.083F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(72, 52).addBox(-2.4393F, -9.1888F, -5.083F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(84, 0).addBox(-2.4393F, -9.1888F, -6.083F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.6607F, -12.1112F, -3.917F));

		PartDefinition cube_r16 = Head.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(74, 81).addBox(-0.5F, -0.5F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.6206F, -14.1633F, -3.2874F, -1.2853F, 0.1109F, -0.0475F));

		PartDefinition cube_r17 = Head.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(68, 83).addBox(-1.0F, -2.0F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.1393F, -11.5888F, -4.283F, -0.7181F, 0.1109F, -0.0475F));

		PartDefinition cube_r18 = Head.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(32, 54).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0607F, -7.4888F, -4.383F, 0.3227F, -0.4418F, 0.06F));

		PartDefinition cube_r19 = Head.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(60, 83).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.4393F, -9.2888F, -4.683F, 0.4163F, 0.1109F, -0.0475F));

		PartDefinition cube_r20 = Head.addOrReplaceChild("cube_r20", CubeListBuilder.create().texOffs(84, 3).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.2607F, -7.4888F, -5.383F, 0.3491F, -0.5236F, 0.0F));

		PartDefinition Core = Body_Upper.addOrReplaceChild("Core", CubeListBuilder.create(), PartPose.offset(0.0F, -9.3558F, -3.5359F));

		PartDefinition cube_r21 = Core.addOrReplaceChild("cube_r21", CubeListBuilder.create().texOffs(44, 40).addBox(0.0F, 2.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -2.5442F, -0.1641F, 0.2618F, 0.0F, 0.0F));

		PartDefinition Grip_pos = Body_Upper.addOrReplaceChild("Grip_pos", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, -17.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

    public void setupAnim(Ignis_PrimeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.animateHeadLookTarget(netHeadYaw, headPitch);
        this.animate(entity.getAnimationState("idle"), Ignis_PrimeAnimation.IDLE, ageInTicks, 0.75F);
        this.animate(entity.getAnimationState("spawn"), Ignis_PrimeAnimation.SPAWN, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("charge_attack"), Ignis_PrimeAnimation.CHARGE_ATTACK, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("charge_attack_end"), Ignis_PrimeAnimation.CHARGE_ATTACK_END, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("charge_attack_loop"), Ignis_PrimeAnimation.CHARGE_ATTACK_LOOP, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("charge_shockwave_attack"), Ignis_PrimeAnimation.CHARGE_SHOCKWAVE_ATTACK, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("rock_excavation_attack"), Ignis_PrimeAnimation.ROCK_EXCAVATION_ATTACK_START, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("rock_excavation_attack_loop"), Ignis_PrimeAnimation.ROCK_EXCAVATION_ATTACK_LOOP, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("rock_excavation_attack_end"), Ignis_PrimeAnimation.ROCK_EXCAVATION_ATTACK_END, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("uppercut"), Ignis_PrimeAnimation.UPPERCUT, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("uppercut_horizontal_combo"), Ignis_PrimeAnimation.UPPERCUT_HORIZONTAL_COMBO, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("uppercut_vertical_combo"), Ignis_PrimeAnimation.UPPERCUT_VERTICAL_COMBO, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("jab_attack_one"), Ignis_PrimeAnimation.JAB_ATTACK_ONE, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("jab_attack_two"), Ignis_PrimeAnimation.JAB_ATTACK_TWO, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("jab_attack_three"), Ignis_PrimeAnimation.JAB_ATTACK_THREE, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("dead"), Ignis_PrimeAnimation.DEAD, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("mode_change"), Ignis_PrimeAnimation.MODE_CHANGE, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("walk"), Ignis_PrimeAnimation.WALK, ageInTicks, 1.0F);
    }

    private void animateHeadLookTarget(float yRot, float xRot) {
        ModelPart var10000 = this.Head;
        var10000.xRot += xRot * ((float)Math.PI / 180F);
        var10000 = this.Head;
        var10000.yRot += yRot * ((float)Math.PI / 180F);
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