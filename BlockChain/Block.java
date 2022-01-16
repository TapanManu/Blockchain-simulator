import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

class NodeInfo {
    static int node_num=0;
    public synchronized static int nodeUpdate() {
        node_num++;
        return node_num-1;
    }
}

class Node extends Thread {
    public void run() {
        Thread.currentThread().setName(Integer.toString(NodeInfo.nodeUpdate()));
        System.out.println("Thread num: "+Thread.currentThread().getName());
        Block.addBlocks(Block.flag,Block.blockchain, Block.prefix, Block.prefixString,0,2,25.3f);
        //addBlocks(blockchain, prefix, prefixString);
        //addBlocks(blockchain, prefix, prefixString);
        //addBlocks(blockchain, prefix, prefixString);
        Block.validate(Block.blockchain, Block.prefix, Block.prefixString);
    }
}

public class Block{
    private static final String UTF_8 = "utf-8";
    private String hash;
    private String previousHash;
    private String data;
    private long timeStamp;
    private int nonce;
    private int senderId;
    private int recieverId;
    private float amount;
    public static List<Block> blockchain;
    public static String prefixString;
    public static int prefix;
    public static int flag;
    
 
    public Block(String data, String previousHash, long timeStamp, int senderId, int recieverId, float amount) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = timeStamp;
        this.senderId = senderId;
        this.recieverId = recieverId;
        this.hash = calculateBlockHash();
        this.amount = amount;

    }
    public String getHash(){
        return this.hash;
    }
    public String getData(){
        return this.data;
    }
    public long getTime(){
        return this.timeStamp;
    }
    public String getPreviousHash(){
        return this.previousHash;
    }
    public String getSenderId() {
        return Integer.toString(this.senderId);
    }
    public String getRecieverId() {
        return Integer.toString(this.recieverId);
    }
    public String getAmount() {
        return Float.toString(this.amount);
    }
    // standard getters and setters
    public String calculateBlockHash() {
        String dataToHash = previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + data;
        MessageDigest digest = null;
        byte[] bytes = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            bytes = digest.digest(dataToHash.getBytes(UTF_8));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            System.out.println(ex.getMessage());
        }
        StringBuffer buffer = new StringBuffer();
        for (byte b : bytes) {
            buffer.append(String.format("%02x", b));
        }
        return buffer.toString();
    }
    public String mineBlock(int prefix) {
        String prefixString = new String(new char[prefix]).replace('\0', '0');
        while (!hash.substring(0, prefix).equals(prefixString)) {
            nonce++;
            hash = calculateBlockHash();
        }
        return hash;
    }
    
    public static void addBlocks(Integer flag, List<Block> blockchain,int prefix, String prefixString, int senderId, int recieverId, float amount) {
        String prev = "0";
        if(blockchain.size() != 0){
            prev = blockchain.get(blockchain.size() - 1).getHash();
        }
        Block newBlock = new Block("The is a New Block.", prev, new Date().getTime(), senderId, recieverId, amount);
        newBlock.mineBlock(prefix);
        synchronized(flag) {
            if(flag==0) {
                blockchain.add(newBlock);
                flag=1;
            }
            
        }
        
    }
   
    public static void validate(List<Block> blockchain,int prefix, String prefixString) {
        boolean flag = true;
        for (int i = 0; i < blockchain.size(); i++) {
            String previousHash = i==0 ? "0" : blockchain.get(i - 1).getHash();
            flag = blockchain.get(i).getHash().equals(blockchain.get(i).calculateBlockHash())
            && previousHash.equals(blockchain.get(i).getPreviousHash())
            && blockchain.get(i).getHash().substring(0, prefix).equals(prefixString);
                if (!flag) {
                    System.out.println("error");
                    break;
                }
        }
    }

    public static void display(List<Block> blockchain) {
        for (int i = 0; i < blockchain.size(); i++) {
            System.out.println("Block " + i);
            System.out.println("Hash: "+ blockchain.get(i).getHash());
            System.out.println("Data: "+ blockchain.get(i).getData());
            System.out.println("Time: "+ blockchain.get(i).getTime());
            System.out.println("Sender: "+ blockchain.get(i).getSenderId());
            System.out.println("Reciever: "+ blockchain.get(i).getRecieverId());
            System.out.println("Amount: "+ blockchain.get(i).getAmount());
            System.out.println();
        }

    }
    public static void main(String[] args) {
        blockchain = new ArrayList<>();
        prefix = 4;
        prefixString = new String(new char[prefix]).replace('\0', '0');

        //TEST
        flag=0;
        addBlocks(flag,blockchain, prefix, prefixString,-1,0,200);
        flag=0;
        Node[] obj = new Node[50];
        for(int i=0;i<50;i++) {
            obj[i]=new Node();
            obj[i].start();
            
        }
        for(int i=0;i<50;i++) {
            try {
                obj[i].join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        //addBlocks(flag,blockchain, prefix, prefixString,0,2,25.3f);
        //addBlocks(blockchain, prefix, prefixString);
        //addBlocks(blockchain, prefix, prefixString);
        //addBlocks(blockchain, prefix, prefixString);
        //validate(blockchain, prefix, prefixString);
        display(blockchain);
    }
}