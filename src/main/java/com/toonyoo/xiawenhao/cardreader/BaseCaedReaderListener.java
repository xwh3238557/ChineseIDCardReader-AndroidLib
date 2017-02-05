//package com.toonyoo.xiawenhao.cardreader;
//
//import java.util.concurrent.locks.ReentrantReadWriteLock;
//
///**
// * Created by xiawenhao on 16/7/29.
// */
//public abstract class BaseCaedReaderListener {
//    private boolean mShouldListen = false;
//    private ReentrantReadWriteLock shouldListenLock = new ReentrantReadWriteLock();
//
//    private void setShouldListen(boolean shouldListen){
//        try{
//            shouldListenLock.writeLock().lock();
//            mShouldListen = shouldListen;
//        }finally {
//            shouldListenLock.writeLock().unlock();
//        }
//
//    }
//
//    private boolean getShouldListen(){
//        try{
//            shouldListenLock.readLock().lock();
//            return mShouldListen;
//        }finally {
//            shouldListenLock.readLock().unlock();
//        }
//
//    }
//
//    public interface AutoSearchingListener{
//        void onResult(byte[] bytes);
//        void onTimeout();
//        void onError(CardReaderError error);
//    }
//
//    public int listeningFreqence    = 500;
//    public int listeningTimeout     = 2000;
//    private void regestAutoSearchingListener(final AutoSearchingListener listener){
//        setShouldListen(true);
//        Runnable listenerRunnable = new Runnable() {
//            @Override
//            public void run() {
//                int retryTime    = 0;
//                while(getShouldListen()){
//                    byte[] bytes = readCMDs();
//
//                    if(bytes != null) {
//                        listener.onResult(bytes);
//                    }
//
//                    retryTime++;
//
//                    try {
//                        Thread.sleep(listeningFreqence);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                    if(listeningTimeout != 0 && retryTime * listeningFreqence > listeningTimeout) {
//                        listener.onTimeout();
//                        return;
//                    }
//                }
//            }
//        };
//        new Thread(listenerRunnable).start();
//    }
//
//    private void unregestAutoSearchingListener(){
//        setShouldListen(false);
//    }
//}
