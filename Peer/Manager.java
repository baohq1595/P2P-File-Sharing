//package p2p;
//
//import common.General;
//
//import java.io.*;
//import java.io.RandomAccessFile;
//import static java.lang.Thread.NORM_PRIORITY;
//import java.util.*;
//
///**
// *
// * @author Mrkeys
// */
//public class Manager {
//      
//     
//     private static final String filename = "E:\\9000M_PBr5.0.0_rel1748_PL5.2.0.95_A5.0.0.1079.exe";
//     public static void main(String[] args) throws InterruptedException, IOException {
//        ArrayList<Integer> list = new ArrayList<Integer>(); 
//        ArrayList<Integer> list2 = new ArrayList<Integer>(); 
//        ArrayList<Integer> list3 = new ArrayList<Integer>(); 
//        ArrayList<Integer> listsuper = new ArrayList<Integer>(); 
//        RandomAccessFile raf = null;
//        RandomAccessFile waf = null;
//        long leng = 0;
//        long lengFile = 0;
//        String hash;
//        try{
//            raf = new RandomAccessFile(filename,"rw");
//           // waf = new RandomAccessFile("Z:\\dealpool.exe","rw");
//            leng = raf.length();
//            lengFile = leng;
//        }
//        catch(IOException e){
//            System.out.println("Err while work with file" +e);
//        }
//        leng = leng/(16*1024);
//        for (int i =0;i<=leng;i++){
//            if (i<=15000) list.add(i);
//            else if (i<=30000) list2.add(i);
//            else list3.add(i);
//          //  listsuper.add(i);
//        }            
//         //System.out.println("list "+list);
//         //System.out.println("list "+list2);
//        // System.out.println("list "+list3);
//        // System.out.println("LengFile "+leng);
//        General ins = new General();
//        File f = new File (filename);
//        hash = ins.toSHAValue(f);
//        System.out.println("Hash Code "+hash);
//        Downloader personOne = new Downloader("012012","192.168.100.15",7000,"E:\\dealpool.pdf",hash,7000,list,lengFile);
//        Downloader personOne2 = new Downloader("012012","192.168.100.15",8000,"E:\\dealpool.pdf",hash,8000,list2,lengFile);
//        Downloader personOne3 = new Downloader("012012","192.168.100.15",9000,"E:\\dealpool.pdf",hash,9000,list3,lengFile);
//        personOne.start();
//        personOne2.start();
//        personOne3.start();
//        personOne.join();
//        personOne2.join();
//        personOne3.join();
//       /* Thread one = new Thread(personOne);
//        Thread two = new Thread(personOne2);
//        Thread three = new Thread(personOne3);
//            one.setPriority(NORM_PRIORITY );
//            two.setPriority(NORM_PRIORITY );
//            three.setPriority(NORM_PRIORITY );
//            one.start();
//            two.start();
//            three.start();
//          Sleep sleeper = new Sleep(1);
//                do{;}
//                while (one.isAlive() || two.isAlive() || three.isAlive()
//                        );
//            }
//        /*(personOne.start();
//        personOne2.start();
//        personOne3.start();
//        Sleep sleeper = new Sleep(1);
//        while (true){
//            if (personOne.isAlive() ||personOne.isAlive() || personOne.isAlive() ){
//                sleeper.init();
//            }
//            else break;
//        }*/
//       raf.close();
//        System.out.println("Exit Complete");
//        System.exit(0);
//     }
//    }
//
