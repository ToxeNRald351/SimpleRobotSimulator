/**
 * @author Anton Havlovskyi
 * Třída reprezentující pozici v prostředí.
 */
package ija.ija2023.project.tool.common;

public class Position {
    public int row;
    public int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Position)) {
            return false;
        }

        Position pos = (Position) o;

        //System.out.println("Positions equal: " + pos.col + " == " + this.col + " AND " + pos.row + " == " + this.row);
        return pos.col == this.col && pos.row == this.row;
    }

    public int getCol() {
        return this.col;
    }

    public int getRow() {
        return this.row;
    }

    @Override
    public int hashCode() {
        return this.row + this.col * 10;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
