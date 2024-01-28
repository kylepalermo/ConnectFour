package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class SaveData implements Serializable{
	private static final long serialVersionUID = 1L;
	private ArrayList<Integer> moves;
	private String saveName;
	private int difficulty;
	
	// Constructor for new save
    public SaveData(String filename, int difficulty) {
        this.saveName = filename;
        this.difficulty = difficulty;
        this.moves = new ArrayList<>();
    }

    // Constructor for loading save
    public SaveData(String filename) {
        this.saveName = filename;
        load();
    }
    
    public void setSaveName(String saveName) {
    	this.saveName = saveName;
    }
    
    public void setDifficulty(int difficulty) {
    	this.difficulty = difficulty;
    }

    public void addMove(int column) {
        moves.add(column);
    }

    public ArrayList<Integer> getMoves() {
        return moves;
    }
    
    public int getDifficulty() {
    	return difficulty;
    }
    
    public void save() {
    	try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("saves" + File.separator + saveName))) {
            out.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void load() {
    	try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("saves" + File.separator + saveName))) {
            SaveData data = (SaveData) in.readObject();
            this.difficulty = data.difficulty;
            this.moves = data.moves;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public static ArrayList<String> getSaveNames(){
    	ArrayList<String> saveNames = new ArrayList<>();
    	File directory = new File("saves");
    	File[] files = directory.listFiles();
    	for(File file : files) {
    		saveNames.add(file.getName());
    	}
    	return saveNames;
    }
}
