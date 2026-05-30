package ro.mpp.triathlon;

import ro.mpp.triathlon.dto.EventDTO;
import ro.mpp.triathlon.dto.ParticipantDTO;
import ro.mpp.triathlon.dto.UserDTO;
import ro.mpp.triathlon.model.Referee;
import ro.mpp.triathlon.rpcprotocol.Request;
import ro.mpp.triathlon.rpcprotocol.Response;
import ro.mpp.triathlon.rpcprotocol.ResponseType;
import ro.mpp.triathlon.services.ITriathlonObserver;
import ro.mpp.triathlon.services.ITriathlonServices;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ClientRpcWorker implements Runnable, ITriathlonObserver {
    private ITriathlonServices server;
    private Socket connection;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    public ClientRpcWorker(ITriathlonServices server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try {
            // Ordinea contează: Output creat și flush-uit primul pentru a evita deadlock-ul pe client
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            connected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (connected) {
            try {
                Object request = input.readObject();
                Response response = handleRequest((Request) request);
                if (response != null) {
                    sendResponse(response);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Worker: Connection closed or error reading: " + e.getMessage());
                break;
            }
        }
        closeResources();
    }

    private Response handleRequest(Request request) {
        try {
            switch (request.getType()) {
                case LOGIN:
                    UserDTO udto = (UserDTO) request.getData();
                    Referee loggedReferee = server.login(udto.getUsername(), udto.getPassword(), this);
                    return new Response.Builder().type(ResponseType.OK).data(loggedReferee).build();

                case LOGOUT:
                    Referee rLog = (Referee) request.getData();
                    server.logout(rLog, this);
                    connected = false;
                    return new Response.Builder().type(ResponseType.OK).build();

                case GET_PARTICIPANTS_BY_EVENT:
                    int idEvent = (int) request.getData();
                    List<ParticipantDTO> participants = server.getParticipantsByEvent(idEvent);
                    return new Response.Builder().type(ResponseType.OK).data(participants).build();

                case ADD_RESULT:
                    EventDTO resultDto = (EventDTO) request.getData();
                    server.addResult(resultDto.getIdReferee(), resultDto.getIdParticipant(), resultDto.getPoints());
                    return new Response.Builder().type(ResponseType.OK).build();

                default:
                    return new Response.Builder().type(ResponseType.ERROR).data("Unknown request").build();
            }
        } catch (Exception e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private void sendResponse(Response response) throws IOException {
        synchronized (output) {
            output.writeObject(response);
            output.flush();
            output.reset();
        }
    }

    @Override
    public void updateReceived() {
        Response resp = new Response.Builder().type(ResponseType.UPDATE).build();
        try {
            sendResponse(resp);
        } catch (IOException e) {
            System.err.println("Worker: Error sending update notification: " + e.getMessage());
        }
    }

    private void closeResources() {
        try {
            connected = false;
            if (input != null) input.close();
            if (output != null) output.close();
            if (connection != null) connection.close();
        } catch (IOException e) {
            System.out.println("Worker: Error closing: " + e);
        }
    }
}