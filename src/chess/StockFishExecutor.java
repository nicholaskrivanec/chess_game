package chess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class StockFishExecutor{
    private static final String PATH = "src/StockFish9/stockfish_9_x64.exe";
    private Process stockfish_exe;
    private BufferedReader output;
    private OutputStreamWriter input;
    private boolean running;
    private final int depth;
    private final int difficulty;
    
    public StockFishExecutor(){
        running = false;
        depth = 5; 
        difficulty = 10;
    }
    
    public boolean isRunning(){
        return running;
    }

    public void startEngine() {
        try {
            stockfish_exe = Runtime.getRuntime().exec(PATH);
            running = true;
            System.out.println("Stockfish engine initialized.");
            output = new BufferedReader(new InputStreamReader(stockfish_exe.getInputStream()));
            input = new OutputStreamWriter(stockfish_exe.getOutputStream());
            
            sendUCICommand("uci \n position startpos");
        } catch (IOException e) {
            System.out.println("Failed to start engine");
            running = false;
        }
    }
    
    public void sendUCICommand(String command) {
        try {
            input.write(command + "\n");
            input.flush();
        } catch (IOException ex) {
            System.out.println("IOException in StockFishExecutor.sendUCICommand()");
        }  
    }
    
    public String getFishOutput(){
        String str = "";
        String line = "";
        try {
            StringBuilder buffer = new StringBuilder();
            Thread.sleep(50);
            sendUCICommand("isready");
            
            while (!(line.trim()).equals("readyok")) { 
                if(output.ready()){
                    line = output.readLine();
                    buffer.append(line).append("\n"); 
                    str = buffer.toString();
                }   
            }
        } catch (InterruptedException | IOException ex) {
            System.out.println("Exception caught in StockFishExecutor.getFishOutput()");
        }
        return str;
    }

    public String getBestMove(String fen) {
        String str = "";
        try{
            if(!fen.isEmpty())
                sendUCICommand("position fen " + fen);
            sendUCICommand("go movetime " + 10 + "depth " + depth );
            String out = getFishOutput();
            if(out.contains("bestmove")){
                str = out.split("bestmove ")[1].split("\\s")[0];
            }
        }catch(Exception ex){
            System.out.println("Exception caught in StockFishExecutor.getBestMove");
        }
        return str;
    }

    public void killFish() {
        try {
            System.out.println("Shutting Down Stockfish child process...");
            sendUCICommand("quit");
            output.close();
            input.close();
            running = false;
            System.out.println("Stockfish has been shutdown.");
        } catch (IOException e) {}
    }
}
