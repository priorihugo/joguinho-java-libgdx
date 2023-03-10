/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mygdx.game.sprites.mobs.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.constants.Direction;
import com.mygdx.game.constants.State;
import com.mygdx.game.screens.EntityHandler;
import com.mygdx.game.screens.MapHandler;
import com.mygdx.game.sprites.Projectile;
import com.mygdx.game.sprites.mobs.Mob;

/**
 *
 * @author Hugo
 */
public class SingleShot implements AttackType {

    private float timer = 0;
    private float cooldown = 0;
    private float delay = 0;
    private int speedModifier = 100;
    private boolean actionLock = false;
    private boolean didShoot = false;
    private Direction lockedDirecion;
    private State lockedState;

    private MapHandler mh;
    private EntityHandler eh;

    private Mob thisMob;

    public SingleShot(Mob thisMob) {
        this.thisMob = thisMob;
        this.mh = thisMob.getMapHandler();
        this.eh = thisMob.getEntityHandler();
    }

    @Override
    public void act(float dt) {

        if (actionLock) {
            //esse codigo ta horrivel mas foi uma forma que encontrei de adicioanr delay a a??o
            if (timer > delay && !didShoot) {
                didShoot = true;
                //System.out.println("Attacking");
                //System.out.println("Source Mob " + thisMob);
                new Projectile(mh, eh, this.thisMob, getProjectileDirection(mh, eh));
            }
            
            waitActionUnlock(dt);
            
        } else {
            //essa verifica??o vai dentro do moviment controller
            //ela esta aqui por teste
            if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                setActionLock(State.SHOTING, thisMob.getDirection());
                //thisMob.setActionLock(State.SHOTING, thisMob.getDirection());
            }
        }

    }

    @Override
    public void setCooldown(float cooldown) {
        this.cooldown = cooldown;
    }

    @Override
    public boolean isAttacking() {
        return thisMob.getAnimations().isCurrentAnimationFinished();
    }

    private Vector2 getProjectileDirection(MapHandler mh, EntityHandler eh) {

        Direction d = thisMob.getDirection();
        float x = 0;
        float y = 0;

        if (d == Direction.N) {
            y = speedModifier;
        } else if (d == Direction.NE) {
            x = y = speedModifier;
        } else if (d == Direction.E) {
            x = speedModifier;
        } else if (d == Direction.SE) {
            y = -speedModifier;
            x = speedModifier;
        } else if (d == Direction.S) {
            y = -speedModifier;
        } else if (d == Direction.SW) {
            x = y = -speedModifier;
        } else if (d == Direction.W) {
            x = -speedModifier;
        } else if (d == Direction.NW) {
            y = speedModifier;
            x = -speedModifier;
        }
        return new Vector2(x, y);

    }

    private void setActionLock(State state, Direction direction) {

        if (!actionLock && lockedDirecion == null && lockedState == null) {
            //System.out.println("Locked Single-Shot");
            this.lockedState = state;
            this.lockedDirecion = direction;
            thisMob.getAnimations().setAnimationLock(state, direction);
            this.actionLock = true;
            thisMob.setState(state);
        }

    }

    private void waitActionUnlock(float dt) {
        if (this.actionLock && thisMob.getAnimations().isCurrentAnimationFinished() && timer > cooldown) {
            //System.out.println("Unlocked Single-Shot");
            this.lockedState = null;
            this.lockedDirecion = null;
            this.actionLock = false;
            this.timer = 0;
            this.didShoot = false;

        }
        timer += dt;
    }

    @Override
    public void setDelay(float f) {
       this.delay = f;
    }

}
