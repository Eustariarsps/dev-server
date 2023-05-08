package io.xeros.model.entity.action;

import io.xeros.model.entity.Entity;

public abstract class Action {

    public Entity entity;

    public Action(Entity entity) {
        this.entity = entity;
    }

    public Action() {

    }

    public abstract void tick();

    public void onStop() {

    }

    public void onStart() {

    }

    public boolean interruptable() {
        return true;
    }

}