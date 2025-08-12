package shared;

public interface Addressable extends Component {

    int read(int address);

    void write(int address, int value);

    boolean accepts(int address);

}
