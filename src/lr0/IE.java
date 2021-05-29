package lr0;

public class IE {
    //一个项目集到另一个项目集
    int orgin;
    int aim;
    String value;

    public IE(int orgin, String value, int aim) {
        this.orgin = orgin;
        this.aim = aim;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IE IE = (IE) o;

        if (orgin != IE.orgin) return false;
        if (aim != IE.aim) return false;
        return value != null ? value.equals(IE.value) : IE.value == null;
    }

    @Override
    public int hashCode() {
        int result = orgin;
        result = 31 * result + aim;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
    public String toString(){
            return "\nI"+orgin+"--"+value+"-->"+"I"+aim;
    }
    public int getOrgin() {
        return orgin;
    }

    public void setOrgin(int orgin) {
        this.orgin = orgin;
    }

    public int getAim() {
        return aim;
    }

    public void setAim(int aim) {
        this.aim = aim;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
