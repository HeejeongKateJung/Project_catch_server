import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.Map;

public class DataOutputStreamStorage{

    public Map<String, DataOutputStream> _dataOutputStreams;

    public DataOutputStreamStorage(){
        _dataOutputStreams = new HashMap<String, DataOutputStream>();
    }
    
}