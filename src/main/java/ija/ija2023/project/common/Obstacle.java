/**
 * @author Anton Havlovskyi
 * Třída reprezentující překážku.
 */
package ija.ija2023.project.common;

import ija.ija2023.project.tool.common.Position;

public class Obstacle {
    public Environment env;
    public Position pos;

    public Obstacle(Environment env, Position pos) {
        this.env = env;
        this.pos = pos;
    }

    public Position getPosition() {
        return this.pos;
    }
}
