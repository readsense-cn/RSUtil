package cn.readsense.module.video;

import java.util.ArrayList;


public class FrameBuffer {


    private long ridx = 0;
    private long widx = 0;


    private ArrayList<byte[]> frameList = new ArrayList<>();

    public FrameBuffer(int bufsize, int bufcnt) {
        for (int i = 0; i < bufsize; i++) {
            frameList.add(new byte[bufcnt]);
        }
    }


    public byte[] get() {
        if ((widx - ridx) > 0) {
            return frameList.get((int) (ridx % frameList.size()));
        } else {
            return null;
        }
    }

    public void free() {
        if (ridx < widx) ridx++;
    }

    public boolean isfull() {
        if ((widx - ridx) >= frameList.size() * 8 / 10) {
            return true;
        }
        return false;
    }


    public boolean in(byte[] inbuf) {

        if ((widx - ridx) >= frameList.size()) {
            return false;
        }

        byte[] frame = frameList.get((int) (widx % frameList.size()));

        if (frame.length < inbuf.length) {
            frame = new byte[inbuf.length];
        }

        System.arraycopy(inbuf, 0, frame, 0, inbuf.length);

        widx++;

        return true;
    }
}
