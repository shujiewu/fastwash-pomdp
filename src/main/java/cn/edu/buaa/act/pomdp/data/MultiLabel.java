package cn.edu.buaa.act.pomdp.data;


public class MultiLabel {
    private int i;
    private int j;
    private int lij;

    public MultiLabel(int i, int j, int lij) {
        this.i = i;
        this.j = j;
        this.lij = lij;
    }

    public int delta(int k) {
        return k == lij ? 1 : 0;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public int getLij() {
        return lij;
    }

    public void setI(int i) {
        this.i = i;
    }

    public void setJ(int j) {
        this.j = j;
    }

    public void setLij(int lij) {
        this.lij = lij;
    }
}

