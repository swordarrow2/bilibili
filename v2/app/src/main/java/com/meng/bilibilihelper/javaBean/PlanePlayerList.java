package com.meng.bilibilihelper.javaBean;

import java.util.*;

public class PlanePlayerList {
    public ArrayList<PlanePlayer> planePlayers = new ArrayList<>();

    public class PlanePlayer {
        public String name = "";
        public long qq = 0;
        public int bid = 0;
        public int bliveRoom = 0;
        public boolean autoTip = false;
    }

}
