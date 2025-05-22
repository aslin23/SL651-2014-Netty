package com.lin.demo_im.netty.client.tool;

import java.nio.channels.Selector;

public class ClientThread implements Runnable   {

    private final Selector  selector;

    public ClientThread(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {

    }
}
