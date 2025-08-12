package shared;

public interface Component {

    void tick(int cycles);

    void reset();

    String getComponentName();

}
