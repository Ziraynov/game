package Entities;

import com.badlogic.gdx.graphics.g2d.Batch;

public class Enemy1 extends Entity{
    private float speed; // Скорость движения врага
    private float damage; // Урон, который враг наносит

    public Enemy1(float x, float y, float width, float height, float maxHP, float speed, float damage) {
        super(x, y, width, height, maxHP);
        this.speed = speed;
        this.damage = damage;
    }



    @Override
    public void update(float deltaTime) {

    }

    // Отрисовка врага
    public void render(Batch batch) {
        // Отрисовка спрайта врага
        // batch.draw(...);
    }

}
