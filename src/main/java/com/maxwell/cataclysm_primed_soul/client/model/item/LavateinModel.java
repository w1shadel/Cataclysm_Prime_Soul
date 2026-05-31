package com.maxwell.cataclysm_primed_soul.client.model.item;

import com.github.L_Ender.lionfishapi.client.model.tools.AdvancedEntityModel;
import com.github.L_Ender.lionfishapi.client.model.tools.AdvancedModelBox;
import com.github.L_Ender.lionfishapi.client.model.tools.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.Entity;

public class LavateinModel extends AdvancedEntityModel<Entity> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox sword_root2;
    private final AdvancedModelBox sword_phase1;
    private final AdvancedModelBox chaine;
    private final AdvancedModelBox chaine2;
    private final AdvancedModelBox sword_phase2;
    private final AdvancedModelBox sword_phase3;

    public LavateinModel() {
        this.texWidth = 128;
        this.texHeight = 128;

        this.root = new AdvancedModelBox(this);
        this.root.setRotationPoint(0.0F, 24.0F, 0.0F);

        this.sword_root2 = new AdvancedModelBox(this);
        this.sword_root2.setRotationPoint(0.0F, 11.4645F, 0.8586F);
        this.root.addChild(this.sword_root2);

        this.sword_phase1 = new AdvancedModelBox(this);
        this.sword_phase1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.sword_root2.addChild(this.sword_phase1);
        this.sword_phase1.setTextureOffset(64, 33).addBox(-1.5F, 7.5355F, -2.8586F, 3, 9, 3, 0.0F, false);
        this.sword_phase1.setTextureOffset(54, 54).addBox(-2.0F, 7.5355F, -4.8586F, 4, 2, 7, 0.0F, false);

        AdvancedModelBox cube_r1 = new AdvancedModelBox(this);
        cube_r1.setRotationPoint(0.0F, 6.2355F, -4.6586F);
        this.sword_phase1.addChild(cube_r1);
        this.setRotationAngle(cube_r1, 0.3927F, 0.0F, 0.0F);
        cube_r1.setTextureOffset(0, 74).addBox(-1.0F, 2.5F, -1.0F, 2, 5, 2, 0.0F, false);

        AdvancedModelBox cube_r2 = new AdvancedModelBox(this);
        cube_r2.setRotationPoint(0.0F, 7.2355F, 2.3414F);
        this.sword_phase1.addChild(cube_r2);
        this.setRotationAngle(cube_r2, -0.4363F, 0.0F, 0.0F);
        cube_r2.setTextureOffset(28, 52).addBox(-1.0F, -1.5F, -1.0F, 2, 8, 1, 0.0F, false);

        AdvancedModelBox cube_r3 = new AdvancedModelBox(this);
        cube_r3.setRotationPoint(-0.5F, 6.2855F, -0.8586F);
        this.sword_phase1.addChild(cube_r3);
        this.setRotationAngle(cube_r3, 0.0F, 3.1416F, 0.0F);
        cube_r3.setTextureOffset(64, 45).addBox(-2.0F, -27.75F, -3.0F, 3, 2, 5, 0.0F, false);

        AdvancedModelBox cube_r4 = new AdvancedModelBox(this);
        cube_r4.setRotationPoint(1.0F, 8.2855F, 8.1414F);
        this.sword_phase1.addChild(cube_r4);
        this.setRotationAngle(cube_r4, 0.0F, 3.1416F, 0.0F);
        cube_r4.setTextureOffset(74, 0).addBox(0.0F, -12.75F, 3.0F, 2, 3, 2, 0.0F, false);

        AdvancedModelBox cube_r5 = new AdvancedModelBox(this);
        cube_r5.setRotationPoint(0.5F, 8.2855F, 8.1414F);
        this.sword_phase1.addChild(cube_r5);
        this.setRotationAngle(cube_r5, 0.0F, 3.1416F, 0.0F);
        cube_r5.setTextureOffset(52, 72).addBox(-1.0F, -9.75F, 3.0F, 3, 7, 2, 0.0F, false);

        AdvancedModelBox cube_r6 = new AdvancedModelBox(this);
        cube_r6.setRotationPoint(0.0F, 8.2855F, -0.8586F);
        this.sword_phase1.addChild(cube_r6);
        this.setRotationAngle(cube_r6, 0.0F, 3.1416F, 0.0F);
        cube_r6.setTextureOffset(54, 28).addBox(-2.0F, -25.75F, 4.0F, 4, 25, 1, 0.0F, false);
        cube_r6.setTextureOffset(16, 41).addBox(-2.0F, -27.75F, 2.0F, 4, 27, 2, 0.0F, false);
        cube_r6.setTextureOffset(36, 33).addBox(-2.0F, -27.75F, -3.0F, 4, 27, 5, 0.0F, false);
        cube_r6.setTextureOffset(54, 0).addBox(-2.0F, -27.75F, -4.0F, 4, 27, 1, 0.0F, false);

        this.chaine = new AdvancedModelBox(this);
        this.chaine.setRotationPoint(-8.0F, -5.9645F, 8.1414F);
        this.sword_phase1.addChild(this.chaine);
        this.chaine.setTextureOffset(14, 97).addBox(9.0F, -0.5F, -14.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(8.0F, -0.5F, -15.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(5.9F, -2.5F, -15.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(5.0F, -3.5F, -13.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(5.0F, -5.5F, -11.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(5.0F, -3.5F, -9.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(5.0F, -4.5F, -6.9F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(5.0F, -1.5F, -11.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(5.0F, 0.5F, -13.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(5.0F, 2.5F, -15.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(5.0F, -7.5F, -6.9F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(5.9F, -5.5F, -5.9F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(5.0F, -11.5F, -9.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(5.0F, -13.4F, -6.9F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(6.0F, -14.5F, -6.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(8.0F, -14.5F, -6.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(9.0F, -13.6F, -6.9F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(9.0F, -12.6F, -9.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(9.0F, -11.6F, -11.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(5.0F, -7.5F, -13.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(5.0F, -5.5F, -15.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(7.0F, -8.5F, -6.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(8.1F, -5.5F, -6.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(9.0F, -6.5F, -6.9F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(9.0F, -9.5F, -6.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(9.0F, -8.5F, -8.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(9.0F, -7.5F, -10.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(9.0F, -7.5F, -12.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(9.0F, -9.5F, -11.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(9.0F, -9.5F, -14.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(8.0F, -9.5F, -15.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(7.0F, -10.5F, -15.2F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(5.7F, -11.1F, -14.5F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(9.0F, 1.5F, -12.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(9.0F, 2.5F, -10.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(9.0F, 3.5F, -8.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(9.0F, 5.5F, -6.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(9.0F, 7.5F, -4.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(7.0F, 7.5F, -4.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(5.0F, 7.5F, -4.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(5.0F, 8.5F, -6.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(5.0F, 9.5F, -8.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(5.0F, 11.5F, -10.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(9.0F, -5.5F, -9.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(9.0F, -4.5F, -11.0F, 2, 2, 2, 0.0F, false);
        this.chaine.setTextureOffset(14, 97).addBox(9.0F, -2.5F, -13.0F, 2, 2, 2, 0.0F, false);

        this.chaine2 = new AdvancedModelBox(this);
        this.chaine2.setRotationPoint(8.1918F, -3.0327F, -8.7163F);
        this.chaine.addChild(this.chaine2);
        this.setRotationAngle(this.chaine2, 0.0F, 3.1416F, 0.0F);
        this.chaine2.setTextureOffset(14, 97).addBox(1.0082F, 2.8327F, -5.2837F, 2, 2, 2, 0.0F, false);
        this.chaine2.setTextureOffset(14, 97).addBox(0.0082F, 2.8327F, -6.2837F, 2, 2, 2, 0.0F, false);
        this.chaine2.setTextureOffset(14, 97).addBox(-2.9918F, -0.1673F, -3.9837F, 2, 2, 2, 0.0F, false);
        this.chaine2.setTextureOffset(14, 97).addBox(-2.9918F, -2.1673F, -2.2837F, 2, 2, 2, 0.0F, false);
        this.chaine2.setTextureOffset(14, 97).addBox(-2.9918F, -3.1673F, -0.2837F, 2, 2, 2, 0.0F, false);
        this.chaine2.setTextureOffset(14, 97).addBox(-2.9918F, -1.1673F, 1.8163F, 2, 2, 2, 0.0F, false);
        this.chaine2.setTextureOffset(14, 97).addBox(-2.9918F, 1.8327F, -2.2837F, 2, 2, 2, 0.0F, false);
        this.chaine2.setTextureOffset(14, 97).addBox(-2.9918F, 3.8327F, -4.2837F, 2, 2, 2, 0.0F, false);
        this.chaine2.setTextureOffset(14, 97).addBox(-2.9918F, 5.8327F, -6.2837F, 2, 2, 2, 0.0F, false);
        this.chaine2.setTextureOffset(14, 97).addBox(-2.9918F, -10.0673F, 1.8163F, 2, 2, 2, 0.0F, false);
        this.chaine2.setTextureOffset(14, 97).addBox(-1.9918F, -11.1673F, 2.7163F, 2, 2, 2, 0.0F, false);
        this.chaine2.setTextureOffset(14, 97).addBox(0.0082F, -11.1673F, 2.7163F, 2, 2, 2, 0.0F, false);
        this.chaine2.setTextureOffset(14, 97).addBox(1.0082F, -10.2673F, 1.8163F, 2, 2, 2, 0.0F, false);
        this.chaine2.setTextureOffset(14, 97).addBox(1.0082F, -9.2673F, -0.2837F, 2, 2, 2, 0.0F, false);
        this.chaine2.setTextureOffset(14, 97).addBox(1.0082F, -8.2673F, -2.2837F, 2, 2, 2, 0.0F, false);
        this.chaine2.setTextureOffset(14, 97).addBox(-2.9918F, -4.1673F, -4.2837F, 2, 2, 2, 0.0F, false);
        this.chaine2.setTextureOffset(14, 97).addBox(-0.9918F, -5.1673F, 2.7163F, 2, 2, 2, 0.0F, false);
        this.chaine2.setTextureOffset(14, 97).addBox(0.1082F, -2.1673F, 2.7163F, 2, 2, 2, 0.0F, false);
        this.chaine2.setTextureOffset(14, 97).addBox(1.0082F, -6.1673F, 2.7163F, 2, 2, 2, 0.0F, false);
        this.chaine2.setTextureOffset(14, 97).addBox(1.0082F, -5.1673F, 0.7163F, 2, 2, 2, 0.0F, false);
        this.chaine2.setTextureOffset(14, 97).addBox(1.0082F, -4.1673F, -3.2837F, 2, 2, 2, 0.0F, false);
        this.chaine2.setTextureOffset(14, 97).addBox(1.0082F, -6.1673F, -2.2837F, 2, 2, 2, 0.0F, false);
        this.chaine2.setTextureOffset(14, 97).addBox(1.0082F, -6.1673F, -5.2837F, 2, 2, 2, 0.0F, false);
        this.chaine2.setTextureOffset(14, 97).addBox(1.0082F, 4.8327F, -3.2837F, 2, 2, 2, 0.0F, false);
        this.chaine2.setTextureOffset(14, 97).addBox(1.0082F, 5.8327F, -1.2837F, 2, 2, 2, 0.0F, false);
        this.chaine2.setTextureOffset(14, 97).addBox(1.0082F, 6.8327F, 0.7163F, 2, 2, 2, 0.0F, false);
        this.chaine2.setTextureOffset(14, 97).addBox(1.0082F, 8.8327F, 2.7163F, 2, 2, 2, 0.0F, false);
        this.chaine2.setTextureOffset(14, 97).addBox(1.0082F, 10.8327F, 4.7163F, 2, 2, 2, 0.0F, false);
        this.chaine2.setTextureOffset(14, 97).addBox(-0.9918F, 10.8327F, 4.7163F, 2, 2, 2, 0.0F, false);
        this.chaine2.setTextureOffset(14, 97).addBox(-2.9918F, 10.8327F, 4.7163F, 2, 2, 2, 0.0F, false);
        this.chaine2.setTextureOffset(14, 97).addBox(-2.9918F, 11.8327F, 2.7163F, 2, 2, 2, 0.0F, false);
        this.chaine2.setTextureOffset(14, 97).addBox(-2.9918F, 12.3327F, 0.7163F, 2, 2, 2, 0.0F, false);
        this.chaine2.setTextureOffset(14, 97).addBox(-2.9918F, 12.8327F, -1.2837F, 2, 2, 2, 0.0F, false);
        this.chaine2.setTextureOffset(14, 97).addBox(1.0082F, -2.1673F, -0.2837F, 2, 2, 2, 0.0F, false);
        this.chaine2.setTextureOffset(14, 97).addBox(1.0082F, 0.8327F, -4.2837F, 2, 2, 2, 0.0F, false);

        this.sword_phase2 = new AdvancedModelBox(this);
        this.sword_phase2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.sword_root2.addChild(this.sword_phase2);
        this.sword_phase2.setTextureOffset(64, 0).addBox(-2.0F, -9.4645F, -4.6586F, 4, 17, 1, 0.0F, false);
        this.sword_phase2.setTextureOffset(28, 41).addBox(-1.5F, -19.4645F, -4.8586F, 3, 10, 1, 0.0F, false);
        this.sword_phase2.setTextureOffset(64, 52).addBox(-2.0F, -20.4645F, -4.8586F, 4, 1, 1, 0.0F, false);
        this.sword_phase2.setTextureOffset(36, 0).addBox(-2.0F, -20.4645F, -3.8586F, 4, 28, 5, 0.0F, false);
        this.sword_phase2.setTextureOffset(64, 33).addBox(-1.5F, 7.5355F, -2.8586F, 3, 9, 3, 0.0F, false);
        this.sword_phase2.setTextureOffset(54, 54).addBox(-2.0F, 7.5355F, -4.8586F, 4, 2, 7, 0.0F, false);

        AdvancedModelBox cube_r7 = new AdvancedModelBox(this);
        cube_r7.setRotationPoint(0.0F, 6.2355F, -4.6586F);
        this.sword_phase2.addChild(cube_r7);
        this.setRotationAngle(cube_r7, 0.3927F, 0.0F, 0.0F);
        cube_r7.setTextureOffset(0, 74).addBox(-1.0F, 2.5F, -1.0F, 2, 5, 2, 0.0F, false);

        AdvancedModelBox cube_r8 = new AdvancedModelBox(this);
        cube_r8.setRotationPoint(0.0F, 7.2355F, 2.3414F);
        this.sword_phase2.addChild(cube_r8);
        this.setRotationAngle(cube_r8, -0.4363F, 0.0F, 0.0F);
        cube_r8.setTextureOffset(28, 52).addBox(-1.0F, -1.5F, -1.0F, 2, 8, 1, 0.0F, false);

        AdvancedModelBox cube_r9 = new AdvancedModelBox(this);
        cube_r9.setRotationPoint(0.0F, -0.1591F, 2.0024F);
        this.sword_phase2.addChild(cube_r9);
        this.setRotationAngle(cube_r9, 0.0175F, 0.0F, 0.0F);
        cube_r9.setTextureOffset(16, 70).addBox(-1.5F, -4.5F, -1.0F, 3, 13, 2, 0.0F, false);

        AdvancedModelBox cube_r10 = new AdvancedModelBox(this);
        cube_r10.setRotationPoint(-0.5F, -10.4645F, 1.5414F);
        this.sword_phase2.addChild(cube_r10);
        this.setRotationAngle(cube_r10, 0.1571F, 0.0F, 0.0F);
        cube_r10.setTextureOffset(64, 18).addBox(-1.0F, -6.0F, -2.5F, 3, 12, 3, 0.0F, false);

        this.sword_phase3 = new AdvancedModelBox(this);
        this.sword_phase3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.sword_root2.addChild(this.sword_phase3);
        this.sword_phase3.setTextureOffset(0, 41).addBox(-1.5F, -20.5645F, -3.8586F, 3, 28, 5, 0.0F, false);
        this.sword_phase3.setTextureOffset(64, 33).addBox(-1.5F, 7.5355F, -2.8586F, 3, 9, 3, 0.0F, false);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(this.root);
    }

    public void setupStage(int stage) {
        this.sword_phase1.showModel = (stage == 0);
        this.sword_phase2.showModel = (stage == 1);
        this.sword_phase3.showModel = (stage >= 2);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(
                root,
                sword_root2,
                sword_phase1,
                chaine,
                chaine2,
                sword_phase2,
                sword_phase3
        );
    }
}