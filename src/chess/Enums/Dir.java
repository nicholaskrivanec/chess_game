package chess.Enums;

public class Dir {
    public static final int N = -1;                                   
    public static final int E = 10;                                   
    public static final int S = -N;                                
    public static final int W = -E;                                 
    public static final int NE = N + E;                      
    public static final int NW = N + W;                      
    public static final int SE = S + E;                      
    public static final int SW = S + W;                      
    public static final int SSW = S + S + W;
    public static final int SSE = S + S + E;
    public static final int NNE = N + N + E;
    public static final int NNW = N + N + W;
    public static final int NWW = N + W + W;
    public static final int NEE = N + E + E;
    public static final int SWW = S + W + W;
    public static final int SEE = S + E + E;
}
