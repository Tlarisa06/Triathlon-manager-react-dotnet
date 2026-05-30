package ro.mpp.triathlon.utils;


import ro.mpp.triathlon.ClientRpcWorker;
import ro.mpp.triathlon.services.ITriathlonServices;

import java.net.Socket;

public class TriathlonRpcConcurrentServer extends AbstractConcurrentServer {
    private ITriathlonServices triathlonServer;

    public TriathlonRpcConcurrentServer(int port, ITriathlonServices triathlonServer) {
        super(port);
        this.triathlonServer = triathlonServer;
        System.out.println("TriathlonRpcConcurrentServer creat pe portul " + port);
    }

    @Override
    protected Thread createWorker(Socket client) {
        ClientRpcWorker worker = new ClientRpcWorker(triathlonServer, client);

        Thread tw = new Thread(worker);
        return tw;
    }

    @Override
    public void stop() throws ServerException {
        System.out.println("Se opreste serverul Triathlon...");
        super.stop();
    }
}