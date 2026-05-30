package ro.mpp.triathlon;

import ro.mpp.triathlon.dto.EventDTO;
import ro.mpp.triathlon.dto.ParticipantDTO;
import ro.mpp.triathlon.dto.UserDTO;
import ro.mpp.triathlon.model.Referee;
import ro.mpp.triathlon.rpcprotocol.Request;
import ro.mpp.triathlon.rpcprotocol.RequestType;
import ro.mpp.triathlon.rpcprotocol.Response;
import ro.mpp.triathlon.rpcprotocol.ResponseType;
import ro.mpp.triathlon.services.ITriathlonObserver;
import ro.mpp.triathlon.services.ITriathlonServices;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TriathlonServicesRpcProxy implements ITriathlonServices {
    private String host;
    private int port;
    private ITriathlonObserver client;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;
    private BlockingQueue<Response> responses;
    private volatile boolean finished;

    public TriathlonServicesRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        this.responses = new LinkedBlockingQueue<>();
    }

    @Override
    public Referee login(String username, String password, ITriathlonObserver client) throws Exception {
        initializeConnection();
        UserDTO userDto = new UserDTO(username, password);
        sendRequest(new Request.Builder().type(RequestType.LOGIN).data(userDto).build());

        Response response = readResponse();
        if (response.getType() == ResponseType.OK) {
            this.client = client;
            return (Referee) response.getData();
        }
        closeConnection();
        throw new Exception(response.getData().toString());
    }

    @Override
    public void logout(Referee referee, ITriathlonObserver client) throws Exception {
        sendRequest(new Request.Builder().type(RequestType.LOGOUT).data(referee).build());
        readResponse();
        closeConnection();
    }

    @Override
    public List<EventDTO> getAllEventsDTO() throws Exception {
        Request req = new Request.Builder()
                .type(RequestType.GET_EVENTS)
                .build();

        sendRequest(req);

        Response response = readResponse();

        if (response.getType() == ResponseType.OK) {
            return (List<EventDTO>) response.getData();
        }

        if (response.getType() == ResponseType.ERROR) {
            String err = response.getData().toString();
            throw new Exception(err);
        }

        return null;
    }

    @Override
    public void addResult(int idReferee, int idParticipant, int points) throws Exception {
        EventDTO dto = new EventDTO(0, "", idReferee, idParticipant, points);
        sendRequest(new Request.Builder().type(RequestType.ADD_RESULT).data(dto).build());
        Response response = readResponse();
        if (response.getType() == ResponseType.ERROR) throw new Exception(response.getData().toString());
    }

    @Override
    public List<ParticipantDTO> getParticipantsByEvent(int idEvent) {
        try {
            sendRequest(new Request.Builder().type(RequestType.GET_PARTICIPANTS_BY_EVENT).data(idEvent).build());
            Response response = readResponse();
            return (List<ParticipantDTO>) response.getData();
        } catch (Exception e) { return null; }
    }

    private void initializeConnection() throws Exception {
        try {
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            new Thread(new ReaderWorker()).start();
        } catch (IOException e) { throw new Exception("Proxy initialize error: " + e.getMessage()); }
    }

    private void sendRequest(Request request) throws Exception {
        try {
            output.writeObject(request);
            output.flush();
        } catch (IOException e) { throw new Exception("Error sending: " + e); }
    }

    private Response readResponse() throws Exception {
        return responses.take(); // Așteaptă până când ReaderWorker pune un răspuns în coadă
    }

    private void closeConnection() {
        finished = true;
        try { if(connection != null) connection.close(); } catch (IOException e) { e.printStackTrace(); }
    }

    private class ReaderWorker implements Runnable {
        public void run() {
            while (!finished) {
                try {
                    Object obj = input.readObject();
                    Response response = (Response) obj;
                    if (response.getType() == ResponseType.UPDATE) {
                        if (client != null) client.updateReceived();
                    } else {
                        responses.put(response);
                    }
                } catch (Exception e) {
                    if (!finished) break;
                }
            }
        }
    }
}